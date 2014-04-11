package sdmc.com.views;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.vlc.Util;
import org.videolan.vlc.WeakHandler;

import sdmc.com.hometv.DlnaPlayerActivity;
import sdmc.com.views.CustomMediaController.MediaPlayerControl;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * 集成 VLC、MediaPlayer 播放
 * @author fee
 *
 */
public class CustomVlcVideoView extends SurfaceView implements MediaPlayerControl {
	private String TAG = "CustomVlcVideoView";
	// settable by the client
	private Uri mUri;
	private Map<String, String> mHeaders;
	private int mDuration;
	private int mWidth;
	private int mHeight;
	// all possible internal states
	private static final int STATE_ERROR = -1;
	private static final int STATE_IDLE = 0;
	private static final int STATE_PREPARING = 1;
	private static final int STATE_PREPARED = 2;
	private static final int STATE_PLAYING = 3;
	private static final int STATE_PAUSED = 4;
	private static final int STATE_PLAYBACK_COMPLETED = 5;

	// mCurrentState is a VideoView object's current state.
	// mTargetState is the state that a method caller intends to reach.
	// For instance, regardless the VideoView object's current state,
	// calling pause() intends to bring the object to a target state
	// of STATE_PAUSED.
	private int mCurrentState = STATE_IDLE;
	private int mTargetState = STATE_IDLE;

	// All the stuff we need for playing and showing a video
	private SurfaceHolder mSurfaceHolder = null;
	/** 如果VLC 获取不到，则用传统 **/
	private MediaPlayer mMediaPlayer = null;
	private   LibVLC libVLC=null;
	private String uriPath=null;
	private int mSurfaceAlign;
	private int mVideoWidth;
	private int mVideoHeight;
	private int mSurfaceWidth;
	private int mSurfaceHeight;
	/**  播放控制组件 **/
	private CustomMediaController mMediaController;
	private OnCompletionListener mOnCompletionListener;
	private MediaPlayer.OnPreparedListener mOnPreparedListener;
	private int mCurrentBufferPercentage;
	private OnErrorListener mOnErrorListener;
	private int mSeekWhenPrepared; // recording the seek position while
									// preparing
	private boolean mCanPause;
	private boolean mCanSeekBack;
	private boolean mCanSeekForward;
	private Context mContext;
	 
	public static final int KEYCODE_MUTE = 91;
	public static final int KEYCODE_VOLUME_MUTE = 164;
	public static final int KEYCODE_MEDIA_PLAY = 126;
	public static final int KEYCODE_MEDIA_PAUSE = 127;
	// 自定义缓冲监听器
	private OnBufferListener mOnBufferListener;
	private OnInfoListener mOnInfoListener;
	private MySizeChangeLinstener mMyChangeLinstener;
	private IVideoPlayer playActivity;
	public void setActivity(IVideoPlayer currentActivity){
		this.playActivity= currentActivity;
	}
	public CustomVlcVideoView(Context context) {
		super(context);
		mContext = context;
		initVideoView();
	}

	public CustomVlcVideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		initVideoView();
	}

	public CustomVlcVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initVideoView();
	}
	boolean isUseMediaPlayer = false;
	/** 切换传统播放器，与VLC播放器 **/
	public void switchDefaultMediaPlayer(boolean notUseVlcPlayer){
		isUseMediaPlayer = notUseVlcPlayer;
		initMediaPlayerMode(isUseMediaPlayer);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Log.i("@@@@", "onMeasure");
		mWidth = getDefaultSize(mVideoWidth, widthMeasureSpec);
		mHeight = getDefaultSize(mVideoHeight, heightMeasureSpec);
		// int width = getDefaultSize(widthMeasureSpec, widthMeasureSpec);
		// int height = getDefaultSize(heightMeasureSpec, heightMeasureSpec);
		// int width=1280;
		// int height=720;
		// if (mVideoWidth > 0 && mVideoHeight > 0) {
		// if (mVideoWidth * height > width * mVideoHeight) {
		// // Log.i("@@@", "image too tall, correcting");
		// height = width * mVideoHeight / mVideoWidth;
		// } else if (mVideoWidth * height < width * mVideoHeight) {
		// // Log.i("@@@", "image too wide, correcting");
		// width = height * mVideoWidth / mVideoHeight;
		// } else {
		// // Log.i("@@@", "aspect ratio is correct: " +
		// // width+"/"+height+"="+
		// // mVideoWidth+"/"+mVideoHeight);
		// }
		// }
		// Log.i("@@@@@@@@@@", "setting size: " + width + 'x' + height);
		setMeasuredDimension(mWidth, mHeight);
	}

	public int resolveAdjustedSize(int desiredSize, int measureSpec) {
		int result = desiredSize;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		switch (specMode) {
		case MeasureSpec.UNSPECIFIED:
			/*
			 * Parent says we can be as big as we want. Just don't be larger
			 * than max size imposed on ourselves.
			 */
			result = desiredSize;
			break;

		case MeasureSpec.AT_MOST:
			/*
			 * Parent says we can be as big as we want, up to specSize. Don't be
			 * larger than specSize, and don't be larger than the max size
			 * imposed on ourselves.
			 */
			result = Math.min(desiredSize, specSize);
			break;

		case MeasureSpec.EXACTLY:
			// No choice. Do what we are told.
			result = specSize;
			break;
		}
		return result;
	}
	/**
	 * 初始化,构造方法中调用
	 */
	public void initVideoView() {
//		mVideoWidth = 0;
//		mVideoHeight = 0;
		mSurfaceHolder=getHolder();// 20130923
//		int pitch=1;
		mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);// 20130923
//		PixelFormat info = new PixelFormat();
//        PixelFormat.getPixelFormatInfo(PixelFormat.RGBX_8888, info);
//        pitch = info.bytesPerPixel;
////        Log.i("info"," initVideoView  pitch= "+pitch); //== 4
//        mSurfaceAlign = 16 / pitch - 1;
		mSurfaceHolder.addCallback(mSHCallback);// 20130923
//		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 20130923
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
	}
	private void initMediaPlayerMode(boolean notUseVlc){
		if(!notUseVlc){ //使用VLC 播放
			try {
				 libVLC=Util.getLibVlcInstance();
				 eventHandler= new VideoPlayerEventHandler(mContext);
				 mChangeSizeHandler= new VideoPlayerHandler(mContext);
				 EventHandler em = EventHandler.getInstance();
			     em.addHandler(eventHandler);
			} catch (LibVlcException e) {
				  Log.e(TAG, "LibVLC initialisation failed");
				  libVLC = null;
			}
		}
		if(libVLC == null || notUseVlc){
			 mMediaPlayer = new MediaPlayer();
			 mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			 mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			 mMediaPlayer.setOnPreparedListener(mPreparedListener);
		}
	}
	 /** 播放之前先设置 播放路径 **/
	 public void setVideoPath(String path) {
		 Log.i("info"," setVideoPath()...path = "+path);
	        setVideoURI(Uri.parse(path));
	    }

	    public void setVideoURI(Uri uri) {
	        setVideoURI(uri, null);
	    }
	    
	/**
	 * @hide
	 */
	public void setVideoURI(Uri uri, Map<String, String> headers) {
		mUri = uri;
		mHeaders = headers;
		mSeekWhenPrepared = 0;
		uriPath=uri.toString();//20130923
		mDuration = 0;
		if(isUseMediaPlayer){
			openVideo();
		}
		else{
			if(libVLC != null){
				libVLC.playMRL(uriPath);
			}
		}
		requestLayout();
		invalidate();
	}
 
 
    private void openVideo() {
    	Log.w("info"," openVideo() --- > uriPath = "+uriPath);
        if (uriPath == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        // Tell the music playback service to pause
        // TODO: these constants need to be published somewhere in the framework.
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);

        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mDuration = -1;
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
//            mMediaPlayer.setDataSource(mContext, mUri, mHeaders);
            mMediaPlayer.setDataSource(uriPath);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING;
//            if( mOnSubFocusItems != null ) {
//                mOnSubFocusItems.subFocusItems();
//            }
//            attachMediaController();
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        }
    }
	public void setMediaController(CustomMediaController controller) {
		if (mMediaController != null) {
			mMediaController.hide();
		}
		mMediaController = controller;
		attachMediaController();
	}
	/**
	 * 依附 播放控制组件
	 */
	private void attachMediaController() {
//		if (mMediaPlayer != null && mMediaController != null) {
			mMediaController.setMediaPlayer(this);
			View anchorView = this.getParent() instanceof View ? (View) this
					.getParent() : this;
			mMediaController.setAnchorView(anchorView);
			mMediaController.setEnabled(isInPlaybackState());
//		}
	}

	MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
			if (mMyChangeLinstener != null) {
				mMyChangeLinstener.doMyThings();
			}
			if (mVideoWidth != 0 && mVideoHeight != 0) {
				getHolder().setFixedSize(mVideoWidth, mVideoHeight);
			}
		}
	};

	/*MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
		public void onPrepared(MediaPlayer mp) {
			mCurrentState = STATE_PREPARED;
			try {
				// 通过反射
				Class mpClazz = mp.getClass();
				// METADATA_ALL和BYPASS_METADATA_FILTER本来是MediaPlayer的常量，但因为设置了隐藏，所以通过反射来获取
				boolean METADATA_ALL = mpClazz.getField("METADATA_ALL")
						.getBoolean(null);
				boolean BYPASS_METADATA_FILTER = mpClazz.getField(
						"BYPASS_METADATA_FILTER").getBoolean(null);
				// Get the capabilities of the player for this stream
				Method method = mpClazz.getMethod("getMetadata", boolean.class,
						boolean.class);
				Object metaData = method.invoke(mp, METADATA_ALL,
						BYPASS_METADATA_FILTER);
				// Metadata data = mp.getMetadata(METADATA_ALL,
				// BYPASS_METADATA_FILTER);
				Class metaDataClazz = metaData.getClass();
				int PAUSE_AVAILABLE = metaDataClazz.getField("PAUSE_AVAILABLE")
						.getInt(null);
				int SEEK_BACKWARD_AVAILABLE = metaDataClazz.getField(
						"SEEK_BACKWARD_AVAILABLE").getInt(null);
				int SEEK_FORWARD_AVAILABLE = metaDataClazz.getField(
						"SEEK_FORWARD_AVAILABLE").getInt(null);
				Method has = metaDataClazz.getMethod("has", int.class);
				Method getBoolean = metaDataClazz.getMethod("getBoolean",
						int.class);
				if (metaData != null) {
					mCanPause = !(Boolean) has
							.invoke(metaData, PAUSE_AVAILABLE)
							|| (Boolean) getBoolean.invoke(metaData,
									PAUSE_AVAILABLE);
					mCanSeekBack = !(Boolean) has.invoke(metaData,
							SEEK_BACKWARD_AVAILABLE)
							|| (Boolean) getBoolean.invoke(metaData,
									SEEK_BACKWARD_AVAILABLE);
					mCanSeekForward = !(Boolean) has.invoke(metaData,
							SEEK_FORWARD_AVAILABLE)
							|| (Boolean) getBoolean.invoke(metaData,
									SEEK_FORWARD_AVAILABLE);

					// mCanSeekBack =
					// !data.has(Metadata.SEEK_BACKWARD_AVAILABLE)
					// || data.getBoolean(Metadata.SEEK_BACKWARD_AVAILABLE);
					// mCanSeekForward =
					// !data.has(Metadata.SEEK_FORWARD_AVAILABLE)
					// || data.getBoolean(Metadata.SEEK_FORWARD_AVAILABLE);
				} else {
					mCanPause = mCanSeekBack = mCanSeekForward = true;
				}
			} catch (Exception e) {

			}
//
//			if (mOnPreparedListener != null) {
//				mOnPreparedListener.onPrepared(mMediaPlayer);
//			}
			if (mMediaController != null) {
				mMediaController.setEnabled(true);
			}
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();

			int seekToPosition = mSeekWhenPrepared; // mSeekWhenPrepared may be
													// changed after seekTo()
													// call
			if (seekToPosition != 0) {
				seekTo(seekToPosition);
			}
			if (mVideoWidth != 0 && mVideoHeight != 0) {
				// Log.i("@@@@", "video size: " + mVideoWidth +"/"+
				// mVideoHeight);
				getHolder().setFixedSize(mVideoWidth, mVideoHeight);
				if (mSurfaceWidth == mVideoWidth
						&& mSurfaceHeight == mVideoHeight) {
					// We didn't actually change the size (it was already at the
					// size
					// we need), so we won't get a "surface changed" callback,
					// so
					// start the video here instead of in the callback.
					if (mTargetState == STATE_PLAYING) {
						start();

						if (mMediaController != null) {
							mMediaController.show();
						}
					} else if (!isPlaying()
							&& (seekToPosition != 0 || getCurrentPosition() > 0)) {
						if (mMediaController != null) {
							// Show the media controls when we're paused into a
							// video and make 'em stick.
							mMediaController.show(0);
						}
					}
				}
			} else {
				// We don't know the video size yet, but should start anyway.
				// The video size might be reported to us later.
				if (mTargetState == STATE_PLAYING) {
					start();
				}
			}
//			DlnaPlayerActivity playerActivity = (DlnaPlayerActivity) CustomVlcVideoView.this.playActivity;
////			CustomVlcVideoView.this.playActivity.setSurfaceSize(mSurfaceWidth, mSurfaceHeight, mSurfaceWidth, mSurfaceHolder, sar_num, sar_den);	
//			playerActivity.showPlayContent(true);
		}
		 
		
	};*/
	 MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
	        public void onPrepared(MediaPlayer mp) {
	            mCurrentState = STATE_PREPARED;
	            Log.i("info","  the media player is onPrepared...");
	            // Get the capabilities of the player for this stream
//	            Metadata data = mp.getMetadata(MediaPlayer.METADATA_ALL,
//	                                      MediaPlayer.BYPASS_METADATA_FILTER);
	//
//	            if (data != null) {
//	                mCanPause = !data.has(Metadata.PAUSE_AVAILABLE)
//	                        || data.getBoolean(Metadata.PAUSE_AVAILABLE);
//	                mCanSeekBack = !data.has(Metadata.SEEK_BACKWARD_AVAILABLE)
//	                        || data.getBoolean(Metadata.SEEK_BACKWARD_AVAILABLE);
//	                mCanSeekForward = !data.has(Metadata.SEEK_FORWARD_AVAILABLE)
//	                        || data.getBoolean(Metadata.SEEK_FORWARD_AVAILABLE);
//	            } else {
//	                mCanPause = mCanSeekBack = mCanSeekForward = true;
//	            }

	            if (mOnPreparedListener != null) {
	                mOnPreparedListener.onPrepared(mMediaPlayer);
	            }
	            if (mMediaController != null) {
	                mMediaController.setEnabled(true);
	            }
	            mVideoWidth = mp.getVideoWidth();
	            mVideoHeight = mp.getVideoHeight();

	            int seekToPosition = mSeekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call
	            if (seekToPosition != 0) {
	                seekTo(seekToPosition);
	            }
	            if (mVideoWidth > 0 && mVideoHeight > 0) {
	            	getHolder().setFixedSize(mVideoWidth, mVideoHeight);
	            	
	                if (mTargetState == STATE_PLAYING) {
	                	start();
	                	if (mMediaController != null) {
	                		//mMediaController.show();
	                	}
	                } else if (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0)) {
	                	if (mMediaController != null) {
	                		// Show the media controls when we're paused into a video and make 'em stick.
	                		mMediaController.show(0);
	                	}
	                }
	            } 
	            else {
	                // We don't know the video size yet, but should start anyway.
	                // The video size might be reported to us later.
	                if (mTargetState == STATE_PLAYING) {
	                    start();
	                }
	            }
	        }
	    };
	    
	public void setVideoScale(int w, int h) {
		android.widget.FrameLayout.LayoutParams lp = (android.widget.FrameLayout.LayoutParams) getLayoutParams();
		lp.width = w;
		lp.height = h;
		// lp.width=lp.MATCH_PARENT;
		// lp.height=lp.MATCH_PARENT;
		setLayoutParams(lp);

	}

	// 缓冲监听器接口
	public interface OnBufferListener {
		public void onBuffer(int percent);
	}

	public interface MySizeChangeLinstener {

		public void doMyThings();
	}

	private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			mCurrentState = STATE_PLAYBACK_COMPLETED;
			mTargetState = STATE_PLAYBACK_COMPLETED;
			if (mMediaController != null) {
				mMediaController.hide();
			}
			if (mOnCompletionListener != null) {
				mOnCompletionListener.onCompletion(mMediaPlayer);
			}
		}
	};

	private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
		public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
			Log.d(TAG, "Error: " + framework_err + "," + impl_err);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			if (mMediaController != null) {
				mMediaController.hide();
			}

			/* If an error handler has been supplied, use it and finish. */
			if (mOnErrorListener != null) {
				if (mOnErrorListener.onError(mMediaPlayer, framework_err,
						impl_err)) {
					return true;
				}
			}

			/*
			 * Otherwise, pop up an error dialog so the user knows that
			 * something bad has happened. Only try and pop up the dialog if
			 * we're attached to a window. When we're going away and no longer
			 * have a window, don't bother showing the user an error.
			 */
			if (getWindowToken() != null) {
				Resources r = mContext.getResources();
				String messageId;

				if (framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
					messageId = "不支持此视频流";
				} else {
					messageId = "未指明的媒体播放错误";
				}
				createDialog();
				showErrorDialog(messageId);
				
			}
			return true;
		}
	};
	private AlertDialog dialog=null;
	private void createDialog(){
		 if(dialog==null){
			 AlertDialog.Builder builder=new Builder(mContext);
			 builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
//					if (mOnCompletionListener != null) {
//						mOnCompletionListener
//								.onCompletion(mMediaPlayer);
//					}
				}
			});
			 dialog=builder.create();
			 dialog.setCancelable(false);
		 }
	}
	private void showErrorDialog(String message){
		 
		dialog.setTitle("VideoView_error");
		dialog.setMessage(message);
		dialog.show(); 
	}
	private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			mCurrentBufferPercentage = percent;
		 
			if (mOnBufferListener == null) {
				return;
			}
			mOnBufferListener.onBuffer(percent);
		}
	};

	/**
	 * Register a callback to be invoked when the media file is loaded and ready
	 * to go.
	 * 
	 * @param l
	 *            The callback that will be run
	 */
	public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
		mOnPreparedListener = l;
	}

	/**
	 * Register a callback to be invoked when the end of a media file has been
	 * reached during playback.
	 * 
	 * @param l
	 *            The callback that will be run
	 */
	public void setOnCompletionListener(OnCompletionListener l) {
		mOnCompletionListener = l;
	}

	// 设置缓冲监听器
	public void setOnBufferListener(OnBufferListener onBufferListener) {
		mOnBufferListener = onBufferListener;
	}

	public void setMySizeChangeLinstener(MySizeChangeLinstener l) {
		mMyChangeLinstener = l;
	}

	public void setOnInfoListener(OnInfoListener onInfoListener) {
		mOnInfoListener = onInfoListener;
	}

	/**
	 * Register a callback to be invoked when an error occurs during playback or
	 * setup. If no listener is specified, or if the listener returned false,
	 * VideoView will inform the user of any errors.
	 * 
	 * @param l
	 *            The callback that will be run
	 */
	public void setOnErrorListener(OnErrorListener l) {
		mOnErrorListener = l;
	}

	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		
		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
//			mSurfaceWidth = w;
//			mSurfaceHeight = h;
			boolean isValidState = (mTargetState == STATE_PLAYING);
			boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
			  if (mMediaPlayer != null && isUseMediaPlayer && isValidState && hasValidSize) {
	                if (mSeekWhenPrepared != 0) {
	                    seekTo(mSeekWhenPrepared);
	                }
	                start();
	                if (mMediaController != null) {
	                    if (mMediaController.isShowing()) {
	                        // ensure the controller will get repositioned later
	                        mMediaController.hide();
	                    }
	                    //mMediaController.show();
	                }
	            }
			
			
			// width= 960 height= 540 format= 2 or default= 4
			 Log.e("info"," surfaceChanged    width= "+w+" height= "+h+" format= "+format );
			 if(libVLC != null){
				 libVLC.attachSurface(mSurfaceHolder.getSurface(), playActivity);
				 }
			if(format == PixelFormat.RGBX_8888)
                Log.i("info", "Pixel format is RGBX_8888");
            else if(format == PixelFormat.RGB_565)
                Log.d(TAG, "Pixel format is RGB_565");
            else if(format == ImageFormat.YV12)
                Log.d(TAG, "Pixel format is YV12");
            else
                Log.d(TAG, "Pixel format is other/unknown");
		}

		public void surfaceCreated(SurfaceHolder holder) {
			Log.w("info"," surfaceCreated().....");
			if(mSurfaceHolder == null){
				mSurfaceHolder=holder;
			}
			if(isUseMediaPlayer){
				 openVideo();
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.w("info"," surfaceDestroyed().....");
			if(libVLC != null){
				libVLC.detachSurface();
			}
			// after we return from this we can't use the surface any more
			mSurfaceHolder = null;
			if (mMediaController != null)
				mMediaController.hide();
			release(true);
		}
	};

	/*  libVLC.stop();
	 * release the media player in any state
	 */
	private void release(boolean clearTargetState) {
		Log.e("info"," release() media player ");
		  if (libVLC != null && !isUseMediaPlayer) {
	            libVLC.stop();
	        }
	        EventHandler em = EventHandler.getInstance();
	        em.removeHandler(eventHandler);
			mCurrentState = STATE_IDLE;
			if(isUseMediaPlayer && mMediaPlayer != null){
					mMediaPlayer.reset();
		            mMediaPlayer.release();
		            mMediaPlayer = null;
		            mCurrentState = STATE_IDLE;
		           if(clearTargetState){
			            mTargetState = STATE_IDLE;
		           }
			}
//			System.exit(0);
	}
 
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isInPlaybackState() && mMediaController != null) {
			toggleMediaControlsVisiblity();
		}
		
		return false;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		if (isInPlaybackState() && mMediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK
				&& keyCode != KeyEvent.KEYCODE_VOLUME_UP
				&& keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
				&& keyCode != KEYCODE_VOLUME_MUTE
				&& keyCode != KeyEvent.KEYCODE_MENU
				&& keyCode != KeyEvent.KEYCODE_CALL
				&& keyCode != KeyEvent.KEYCODE_ENDCALL;
		if (isInPlaybackState() && isKeyCodeSupported
				&& mMediaController != null) {
			if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
					|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
//				if (mMediaPlayer.isPlaying()) {
//					pause();
//					mMediaController.show();
//				} else {
//					start();
//					mMediaController.hide();
//				}
				return true;
			} else if (keyCode == KEYCODE_MEDIA_PLAY) {
//				if (!mMediaPlayer.isPlaying()) {
//					start();
//					mMediaController.hide();
//				}
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
					|| keyCode == KEYCODE_MEDIA_PAUSE) {
//				if (mMediaPlayer.isPlaying()) {
//					pause();
//					mMediaController.show();
//				}
				return true;
			} else {
				toggleMediaControlsVisiblity();
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private void toggleMediaControlsVisiblity() {
		DlnaPlayerActivity playActivity = (DlnaPlayerActivity) this.playActivity;
		boolean isShowNameInActivity = false;
		if (mMediaController.isShowing()) {
			mMediaController.hide();
			
		} else {
			mMediaController.show();
			isShowNameInActivity = true;
		}
		if(playActivity != null){
			playActivity.showOrDismissResName(isShowNameInActivity);
		}
	}
	 
	public void vlcPlay(String mediaFilePath){
		setKeepScreenOn(true);
		uriPath = mediaFilePath;
		if(libVLC != null && !isUseMediaPlayer){
			libVLC.playMRL(uriPath);
		}
		else{
			try {
				mMediaPlayer.setDataSource(mediaFilePath);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			} catch (IllegalArgumentException e) {
				Log.e("info"," use mediaPlayer occur IllegalArgumentException");
			} catch (SecurityException e) {
				Log.e("info"," use mediaPlayer occur SecurityException");
			} catch (IllegalStateException e) {
				Log.e("info"," use mediaPlayer occur IllegalStateException");
			} catch (IOException e) {
				Log.e("info"," use mediaPlayer occur IOException");
			}
		}
		if (isInPlaybackState()) {			
			mCurrentState = STATE_PLAYING;
		}
		mTargetState = STATE_PLAYING;
	}
	/**
	 * 让 libVLC.pause();
	 */
	public void pause() {
		if(libVLC != null && !isUseMediaPlayer){
			libVLC.pause();
		}
		else{
			mMediaPlayer.pause();
		}
		setKeepScreenOn(false);
		if (isInPlaybackState()) {
				mCurrentState = STATE_PAUSED;
		}
		mTargetState = STATE_PAUSED;
	}
	/**
	 * Activity onDestroy 时调用
	 */
	public void vlcDestroy() {
		release(false);
	}

	public void resume() {
		openVideo();
	}
//	public void addOrRemoveDisPlaySurface(boolean needRemoveSurface){
//		if(isUseMediaPlayer && mMediaPlayer != null){
//			if(needRemoveSurface){ //移除显示
//				mMediaPlayer.setDisplay(null);
//			}
//			else{
//				mMediaPlayer.setDisplay(mSurfaceHolder);
//			}
//		}
//		else{
//			//VLC  do something
//		}
//	}
	public void removeDisplaySurface(){
		if(isUseMediaPlayer){
			if(mMediaPlayer == null) return;
			mMediaPlayer.setDisplay(null);
		}
	}
	// cache duration as mDuration for faster access
	public int getDuration() {
		 
		if (isInPlaybackState()) {
			if (mDuration > 0) {
				return mDuration;
			}
			if(libVLC !=null && !isUseMediaPlayer ){
				mDuration = (int) libVLC.getLength();
				return mDuration;
			}
			mDuration = mMediaPlayer.getDuration();
			Log.e("info"," use media player cur played file 's duration = "+mDuration);
			return mDuration;
		}
		mDuration = -1;
		return mDuration;
	}

	public int getCurrentPosition() {
		if (isInPlaybackState()) {
			if(isUseMediaPlayer){
				return mMediaPlayer.getCurrentPosition();
			}
			return (int) (libVLC !=null ? libVLC.getTime() : 0);
		}
		return 0;
	}

	public void seekTo(int msec) {
		if (isInPlaybackState()) {
			if(isUseMediaPlayer){
				mMediaPlayer.seekTo(msec);
			}
			else if(libVLC != null ){
				libVLC.setTime(msec);
			}
			mSeekWhenPrepared = 0;
		} else {
			mSeekWhenPrepared = msec;
		}
	}

	public boolean isPlaying() {
		if(isUseMediaPlayer)return mMediaPlayer.isPlaying();
		return libVLC.isPlaying();
	}

	public int getBufferPercentage() {
//		if (mMediaPlayer != null) {
			return mCurrentBufferPercentage;
//		}
//		return 0;
	}

	private boolean isInPlaybackState() {
		return true;
//				(mMediaPlayer != null && mCurrentState != STATE_ERROR
//				&& mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
	}

	public boolean canPause() {
		return mCanPause;
	}

	public boolean canSeekBackward() {
		if(isUseMediaPlayer) return mDuration > 0;
		return libVLC.isSeekable();
//		return mCanSeekBack;
	}

	public boolean canSeekForward() {
		if(isUseMediaPlayer) return mDuration > 0;
		return libVLC.isSeekable();
	}
	/**
     *  Handle libvlc asynchronous events
     */
    private Handler eventHandler;
    private  class VideoPlayerEventHandler extends WeakHandler<Context> {
        public VideoPlayerEventHandler(Context owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            DlnaPlayerActivity activity = (DlnaPlayerActivity) getOwner();
            if(activity == null) return;
            int vlcPlayEvent = msg.getData().getInt("event");
            activity.dealWithVlcPlayEvent(vlcPlayEvent);
            switch (vlcPlayEvent) {
                case EventHandler.MediaPlayerPlaying:
//                    Log.i("info", "MediaPlayerPlaying");
                    //activity.setESTracks();
                    break;
                case EventHandler.MediaPlayerPaused:
//                    Log.i("info", "MediaPlayerPaused");
                    break;
                case EventHandler.MediaPlayerStopped:
//                    Log.i("info", "MediaPlayerStopped");
                    break;
                case EventHandler.MediaPlayerEndReached:
//                    Log.i("info", "MediaPlayerEndReached " );
                    CustomVlcVideoView.this.libVLC.stop();
                    CustomVlcVideoView.this.libVLC.play();
                    break;
                case EventHandler.MediaPlayerVout:
//                	Log.i("info", "MediaPlayerVout");
//                    activity.handleVout(msg);
                    break;
                case EventHandler.MediaPlayerPositionChanged:
                    //don't spam the logs
                    break;
                default:
//                    Log.e("info", String.format("Event not handled (0x%x)", msg.getData().getInt("event")));
                    break;
            }
        }
        
        
    };
    /**
     * Handle resize of the surface and the overlay
     */
    public Handler mChangeSizeHandler;

    private static class VideoPlayerHandler extends WeakHandler<Context> {
        public VideoPlayerHandler(Context owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
        	DlnaPlayerActivity activity = (DlnaPlayerActivity) getOwner();
            if(activity == null) // WeakReference could be GC'ed early
                return;
            switch (msg.what) {
                case 888:
                    activity.changeSurfaceSize();
                    break;
            }
        }
    }
    /**  暂停后，又重新 开始播放  **/
	@Override
	public void start() {
		 if (isInPlaybackState()) {
			 if(isUseMediaPlayer){
					mMediaPlayer.start();
					mCurrentState = STATE_PLAYING;
				}
			 	else if(libVLC != null){
			 		libVLC.play();
			 	}
	        }
		 mTargetState = STATE_PLAYING;
	}
	public void stopPlay(){
		if(isUseMediaPlayer){
			if( mMediaPlayer != null && mMediaPlayer.isPlaying()){
				mMediaPlayer.stop();
				mMediaPlayer.reset();
			}
		}
		else{
			if(libVLC != null){
				libVLC.stop();
			}
		}
	}
}
