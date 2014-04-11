package sdmc.com.views;

import java.lang.reflect.Method;
import java.util.Formatter;
import java.util.Locale;

import sdmc.com.hometv.DlnaPlayerActivity;
import sdmc.com.hometv.R;

import com.sdmc.dlna.filebrowser.Const;
import com.sdmc.dlna.service.NativeAccess;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * 自定义播放器控制层 extends FrameLayout
 * 
 * @COM SDMC
 */

public class CustomMediaController extends FrameLayout {

	private MediaPlayerControl mPlayer;
	private Context mContext;
	/**
	 * 本控制条依附的对象
	 */
	private View mAnchor;
	/**
	 * 播放控制条
	 */
	private View mRoot;
	private WindowManager mWindowManager;
	private Window mWindow;
	private View mDecor;
	private WindowManager.LayoutParams mDecorLayoutParams;
	private SeekBar mProgress;
	private TextView mEndTime, mCurrentTime;
	private boolean mShowing;
	private boolean mDragging;
	private static int sDefaultTimeout = 3000;
	private static final int FADE_OUT = 1;
	private static final int SHOW_PROGRESS = 2;
	private boolean mUseFastForward;
	private boolean mFromXml;
	private boolean mListenersSet;
	 
	StringBuilder mFormatBuilder;
	Formatter mFormatter;
	private ImageButton mPauseButton;
	private ImageButton mNextButton;
	private ImageButton mPrevButton;
	private ImageButton dlnaPushButton;
	// 以下按键值为兼容低版本
	/** Key code constant: Play media key. */
	public static final int KEYCODE_MEDIA_PLAY = 126;
	/** Key code constant: Pause media key. */
	public static final int KEYCODE_MEDIA_PAUSE = 127;
	/**
	 * Key code constant: Volume Mute key. Mutes the speaker, unlike
	 * {@link #KEYCODE_MUTE}. This key should normally be implemented as a
	 * toggle such that the first press mutes the speaker and the second press
	 * restores the original volume.
	 */
	public static final int KEYCODE_VOLUME_MUTE = 164;
	// 文件类型
	private int mFileType;
	/**
	 * 写XML文件中调用
	 * @param context
	 * @param attrs
	 */
	public CustomMediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRoot = this;
		mContext = context;
		mUseFastForward = true;
		mFromXml = true;
	}

	@Override
	public void onFinishInflate() {
		Log.e("info", "　CustomMediaController   xml解析成view最后的回调 onFinishInflate");
		// xml解析成view最后的回调
		if (mRoot != null)
			initControllerView(mRoot);
	}

	public CustomMediaController(Context context, boolean useFastForward) {
		super(context);
		mContext = context;
		mUseFastForward = useFastForward;
		initFloatingWindowLayout();
		initFloatingWindow();
	}

	public CustomMediaController(Context context) {
		this(context, true);
	}
	private OnClickListener activityClickListener = null;
	public void setPlayControllerOnclickListener(OnClickListener inActivityListener){
		activityClickListener = inActivityListener;
	}
	/**
	 * 设置文件类型（比如是音乐文件或视频文件）
	 */
	protected void setFileType(int fileType) {

		mFileType = fileType;
	}

	/**
	 * 设置控制层显示的时间，0代表控制层不隐藏
	 */

	protected void setTimeout(int time) {

		CustomMediaController.sDefaultTimeout = time;

	}
	/**
	 * 构造方法中调用
	 */
	private void initFloatingWindow() {
		mWindowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		try {
			// 通过反射获取PolicyManager
			Class clazz = Class
					.forName("com.android.internal.policy.PolicyManager");
			Method makeNewWindow = clazz.getMethod("makeNewWindow",
					Context.class);
			mWindow = (Window) makeNewWindow.invoke(clazz, mContext);
			// mWindow = PolicyManager.makeNewWindow(mContext);
		} catch (Exception e) {
			// 反射异常处理
			e.printStackTrace();
		}
		mWindow.setWindowManager(mWindowManager, null, null);
		mWindow.requestFeature(Window.FEATURE_NO_TITLE);
		mDecor = mWindow.getDecorView();
		mDecor.setOnTouchListener(mTouchListener);
		mWindow.setContentView(this);
		mWindow.setBackgroundDrawableResource(android.R.color.transparent);

		// While the media controller is up, the volume control keys should
		// affect the media stream type
		mWindow.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		setFocusable(true);
		setFocusableInTouchMode(true);
		setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		requestFocus();
	}
	// 构造方法中调用
	// Allocate and initialize the static parts of mDecorLayoutParams. Must
	// also call updateFloatingWindowLayout() to fill in the dynamic parts
	// (y and width) before mDecorLayoutParams can be used.
	private void initFloatingWindowLayout() {
		mDecorLayoutParams = new WindowManager.LayoutParams();
		WindowManager.LayoutParams p = mDecorLayoutParams;
		p.gravity = Gravity.TOP;
		// p.gravity=Gravity.FILL;
		p.height = LayoutParams.WRAP_CONTENT;
		p.x = 0;
		p.format = PixelFormat.TRANSLUCENT;
		p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
		p.flags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
				| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		// | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
		p.token = null;
		p.windowAnimations = 0; // android.R.style.DropDownAnimationDown;
	}
	 
	// Update the dynamic parts of mDecorLayoutParams
	// Must be called with mAnchor != NULL.
	// 自定义播放控制布局在方法  show()中调用
	private void updateFloatingWindowLayout() {
		int[] anchorPos = new int[2];  // x= 0  y=0
		mAnchor.getLocationOnScreen(anchorPos);
//		Log.e("info","  updateFloatingWindowLayout  >>>>  "+anchorPos[0]  +"  y "+ anchorPos[1]);
		
		WindowManager.LayoutParams p = mDecorLayoutParams;

		p.width = mAnchor.getWidth();
		if (mFileType == Const.FILETYPE_MUSIC) {
			// 确定控制层要放的位置
			// 播放音乐文件的话，位置暂时不变，控制层的高度改变
			p.height = LayoutParams.WRAP_CONTENT;//?
			Log.e("info","  updateFloatingWindowLayout  >>>>  p.height =	"+p.height  );
//			p.height = 200;
			p.y = anchorPos[1] + mAnchor.getHeight();
		} else {
			p.y = anchorPos[1] + mAnchor.getHeight();
		}
//		Log.e("info","  updateFloatingWindowLayout  >>>>  p.width =	"+p.width  +"  p.y = "+p.y);
		// p.width =  800  p.y=480  即为屏幕宽高
 	}

	// This is called whenever mAnchor's layout bound changes
	// private OnLayoutChangeListener mLayoutChangeListener =
	// new OnLayoutChangeListener() {
	// public void onLayoutChange(View v, int left, int top, int right,
	// int bottom, int oldLeft, int oldTop, int oldRight,
	// int oldBottom) {
	// updateFloatingWindowLayout();
	// if (mShowing) {
	// mWindowManager.updateViewLayout(mDecor, mDecorLayoutParams);
	// }
	// }
	// };

	private OnTouchListener mTouchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (mShowing) {
					hide();
				}
			}
			return false;
		}
	};

	public void setMediaPlayer(MediaPlayerControl player) {
		mPlayer = player;
		updatePausePlay();
	}

	/**
	 * Set the view that acts as the anchor for the control view. This can for
	 * example be a VideoView, or your Activity's main view.
	 * 
	 * @param view
	 *            The view to which to anchor the controller when it is visible.
	 *  设置依附对象
	 */
	public void setAnchorView(View view) {
		// if (mAnchor != null) {
		// mAnchor.removeOnLayoutChangeListener(mLayoutChangeListener);
		// }
		mAnchor = view;
		// if (mAnchor != null) {
		// mAnchor.addOnLayoutChangeListener(mLayoutChangeListener);
		// }

		RelativeLayout.LayoutParams frameParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		removeAllViews();
		View v = makeControllerView();
		addView(v, frameParams); //把控制条布局视图 添加到本FrameLayout对象
	}

	/**
	 * Create the view that holds the widgets that control playback. Derived
	 * classes can override this to create their own.
	 * 
	 * @return The controller view.
	 * @hide This doesn't work as advertised
	 * 初始化控制条
	 */
	protected View makeControllerView() {
		LayoutInflater inflate = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRoot = inflate.inflate(R.layout.custom_media_controller, null);
		initControllerView(mRoot);

		return mRoot;
	}
	/**
	 * 初始化 播放控件,播放、暂停、快进，快退等
	 * @param v
	 */
	private void initControllerView(View playControlLayout) {
		mPauseButton = (ImageButton) playControlLayout.findViewById(R.id.player_play_pause);
//		if (mPauseButton != null) {
			mPauseButton.requestFocus();
			mPauseButton.setFocusableInTouchMode(true);
			mPauseButton.setOnClickListener(playerControllerOnclick);
//		}

		mProgress = (SeekBar) playControlLayout.findViewById(R.id.mediacontroller_progress);
		if (mProgress != null) {
			 
			mProgress.setOnSeekBarChangeListener(mSeekListener);
		}

		mEndTime = (TextView) playControlLayout.findViewById(R.id.time);
		mCurrentTime = (TextView) playControlLayout.findViewById(R.id.time_current);
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
		mNextButton = (ImageButton) playControlLayout.findViewById(R.id.player_next);
		if (mNextButton != null) {
			mNextButton.setOnClickListener(activityClickListener != null ? activityClickListener :playerControllerOnclick);
//			mNextButton.setEnabled(playerControllerOnclick != null);
		}
		mPrevButton = (ImageButton) playControlLayout.findViewById(R.id.player_pre);
		if (mPrevButton != null) {
			mPrevButton.setOnClickListener(activityClickListener != null ? activityClickListener :playerControllerOnclick);
//			mPrevButton.setEnabled(playerControllerOnclick != null);
		}
		dlnaPushButton=(ImageButton) playControlLayout.findViewById(R.id.dlna_push);
		dlnaPushButton.setOnClickListener(activityClickListener != null ? activityClickListener :playerControllerOnclick);
	}
	
	/**
	 * Show the controller on screen. It will go away automatically after 3
	 * seconds of inactivity.
	 */
	public void show() {
		show(sDefaultTimeout);
	}

	/**
	 * Disable pause or seek buttons if the stream cannot be paused or seeked.
	 * This requires the control interface to be a MediaPlayerControlExt
	 */
	private void disableUnsupportedButtons() {
		try {
			if (mPauseButton != null && ! mPlayer.canPause()) {
//				mPauseButton.setEnabled(false); //???
			}
//			if (mRewButton != null && !mPlayer.canSeekBackward()) {
//				mRewButton.setEnabled(false);
//			}
//			if (mFfwdButton != null && !mPlayer.canSeekForward()) {
//				mFfwdButton.setEnabled(false);
//			}
		} catch (IncompatibleClassChangeError ex) {
			// We were given an old version of the interface, that doesn't have
			// the canPause/canSeekXYZ methods. This is OK, it just means we
			// assume the media can be paused and seeked, and so we don't
			// disable
			// the buttons.
		}
	}

	/**
	 * Show the controller on screen. It will go away automatically after
	 * 'timeout' milliseconds of inactivity.
	 * 
	 * @param timeout
	 *            The timeout in milliseconds. Use 0 to show the controller
	 *            until hide() is called.
	 */
	public void show(int timeout) {
		if (!mShowing && mAnchor != null) {
			setProgress();
			if (mPauseButton != null) {
				mPauseButton.requestFocus();
			}
			disableUnsupportedButtons();
			
			updateFloatingWindowLayout();
			mWindowManager.addView(mDecor, mDecorLayoutParams);
			mShowing = true;
		}
		updatePausePlay();

		// cause the progress bar to be updated even if mShowing
		// was already true. This happens, for example, if we're
		// paused with the progress bar showing the user hits play.
		mHandler.sendEmptyMessage(SHOW_PROGRESS);

		Message msg = mHandler.obtainMessage(FADE_OUT);
		if (timeout != 0) {
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendMessageDelayed(msg, timeout);
		}
	}

	public boolean isShowing() {
		return mShowing;
	}

	/**
	 * Remove the controller from the screen.
	 */
	public void hide() {
		if (mAnchor == null)
			return;

		if (mShowing) {
			try {
				mHandler.removeMessages(SHOW_PROGRESS);
				mWindowManager.removeView(mDecor);
			} catch (IllegalArgumentException ex) {
				Log.w("MediaController", "already removed");
			}
			mShowing = false;
		}
		//add
		if(mPlayer != null){
			if(curActivityUser != null){
				DlnaPlayerActivity playActivity = (DlnaPlayerActivity) curActivityUser;
				playActivity.showOrDismissResName(false);
			}
		}
	}
	private Activity curActivityUser;
	/** 当前DLNA推送的设备ID  **/
	private int curDlnaPushedDeviceId = -1;
	public void setActivityOwner(Activity curUseActivity,int dlnaPushDeviceId){
		curActivityUser = curUseActivity;
		curDlnaPushedDeviceId = dlnaPushDeviceId;
	}
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int pos;
			switch (msg.what) {
			case FADE_OUT:
				hide();
				break;
			case SHOW_PROGRESS:
				pos = setProgress();
				if (!mDragging && mShowing && mPlayer.isPlaying()) {
					msg = obtainMessage(SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
				}
				break;
			}
		}
	};

	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	private int setProgress() {
		if (mPlayer == null || mDragging) {
			return 0;
		}
		int position = mPlayer.getCurrentPosition();
		int duration = mPlayer.getDuration();
//		Log.e("info"," in CustomMeidaController setProgress() cur play position = "+position +" duration= "+duration);
		if (mProgress != null) {
			if (duration > 0) {
				// use long to avoid overflow
				long pos = 1000L * position / duration;
				mProgress.setProgress((int) pos);
			}
			int percent = mPlayer.getBufferPercentage();
			mProgress.setSecondaryProgress(percent * 10);
		}

		if (mEndTime != null)
			mEndTime.setText("/"+stringForTime(duration));
		if (mCurrentTime != null)
			mCurrentTime.setText(stringForTime(position));

		return position;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		show(sDefaultTimeout);
		return true;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		show(sDefaultTimeout);
		return false;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		final boolean uniqueDown = event.getRepeatCount() == 0
				&& event.getAction() == KeyEvent.ACTION_DOWN;
		if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
				|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
				|| keyCode == KeyEvent.KEYCODE_SPACE) {
			if (uniqueDown) {
				doPauseResume();
				show(sDefaultTimeout);
				if (mPauseButton != null) {
					mPauseButton.requestFocus();
				}
			}
			return true;
		} else if (keyCode == KEYCODE_MEDIA_PLAY) {
			if (uniqueDown && !mPlayer.isPlaying()) {
				mPlayer.start();
				updatePausePlay();
				show(sDefaultTimeout);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
				|| keyCode == KEYCODE_MEDIA_PAUSE) {
			if (uniqueDown && mPlayer.isPlaying()) {
				mPlayer.pause();
				updatePausePlay();
				show(sDefaultTimeout);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| keyCode == KeyEvent.KEYCODE_VOLUME_UP
				|| keyCode == KEYCODE_VOLUME_MUTE) {
			// don't show the controls for volume adjustment
			return super.dispatchKeyEvent(event);
		} else if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_MENU) {
			if (uniqueDown) {
				hide();
			}
			return true;
		}

		show(sDefaultTimeout);
		return super.dispatchKeyEvent(event);
	}
	 
	private View.OnClickListener playerControllerOnclick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mHandler.removeMessages(FADE_OUT);
			int btnViewId = v.getId();
			switch (btnViewId) {
			case R.id.dlna_push:
				// DLNA  推送
				break;
			case R.id.player_play_pause: //播放、暂停
				
				doPauseResume();
				if (mFileType != Const.FILETYPE_MUSIC) {
					show(sDefaultTimeout);
				}
				break;
			case R.id.player_next: // 下一曲
				break;
			case R.id.player_pre: //上一曲
				break;
			}
		}
	};
	 

	private void updatePausePlay() {
		if (mRoot == null || mPauseButton == null)
			return;

		if (mPlayer.isPlaying()) {
//			Log.e("info","--------------变成暂停 键-----------------");
//			mPauseButton.setBackgroundResource(R.drawable.selector_for_media_player_pause);
			mPauseButton.setBackgroundResource(R.drawable.media_player_pause_pressed);
		} else {
//			Log.e("info","--------------变成播放 键-----------------");
//			mPauseButton.setBackgroundResource(R.drawable.selector_for_media_player_play);
			mPauseButton.setBackgroundResource(R.drawable. media_player_play_pressed);
		}
	}

	private void doPauseResume() {
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
			//add for remote dlna play control 2014 - 03-27
			if(curDlnaPushedDeviceId != -1){
				NativeAccess.pause(curDlnaPushedDeviceId);
			}
			//add end
		} else {
			mPlayer.start();
			//add for remote dlna play control 2014 - 03-27
			if(curDlnaPushedDeviceId != -1){
				NativeAccess.play(curDlnaPushedDeviceId, "");
			}
			//add end
		}
		updatePausePlay();
	}

	// There are two scenarios that can trigger the seekbar listener to trigger:
	//
	// The first is the user using the touchpad to adjust the posititon of the
	// seekbar's thumb. In this case onStartTrackingTouch is called followed by
	// a number of onProgressChanged notifications, concluded by
	// onStopTrackingTouch.
	// We're setting the field "mDragging" to true for the duration of the
	// dragging
	// session to avoid jumps in the position in case of ongoing playback.
	//
	// The second scenario involves the user operating the scroll ball, in this
	// case there WON'T BE onStartTrackingTouch/onStopTrackingTouch
	// notifications,
	// we will simply apply the updated position without suspending regular
	// updates.
	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {
			show(3600000);

			mDragging = true;

			// By removing these pending progress messages we make sure
			// that a) we won't update the progress while the user adjusts
			// the seekbar and b) once the user is done dragging the thumb
			// we will post one of these messages to the queue again and
			// this ensures that there will be exactly one message queued up.
			mHandler.removeMessages(SHOW_PROGRESS);
		}

		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (!fromuser) {
				// We're not interested in programmatically generated changes to
				// the progress bar's position.
				return;
			}

			long duration = mPlayer.getDuration();
			long newposition = (duration * progress) / 1000L;
			mPlayer.seekTo((int) newposition);
			if (mCurrentTime != null)
				mCurrentTime.setText(stringForTime((int) newposition));
			
			// add for remote dlna play control 2014 - 03-27
			if(curDlnaPushedDeviceId != -1){
				NativeAccess.setSpeed(curDlnaPushedDeviceId, progress);
			}
			// add end 
		}

		public void onStopTrackingTouch(SeekBar bar) {
			mDragging = false;
			setProgress();
			updatePausePlay();
			show(sDefaultTimeout);

			// Ensure that progress is properly updated in the future,
			// the call to show() does not guarantee this because it is a
			// no-op if we are already showing.
			mHandler.sendEmptyMessage(SHOW_PROGRESS);
		}
	};

	@Override
	public void setEnabled(boolean enabled) {
		if (mPauseButton != null) {
			mPauseButton.setEnabled(enabled);
		}
		if (mNextButton != null) {
			mNextButton.setEnabled(enabled  );
		}
		if (mPrevButton != null) {
			mPrevButton.setEnabled(enabled  );
		}
		if (mProgress != null) {
			mProgress.setEnabled(enabled);
		}
		disableUnsupportedButtons();
		super.setEnabled(enabled);
	}

//	private View.OnClickListener mRewListener = new View.OnClickListener() {
//		public void onClick(View v) {
//			int pos = mPlayer.getCurrentPosition();
//			pos -= 5000; // milliseconds
//			mPlayer.seekTo(pos);
//			setProgress();
//
//			show(sDefaultTimeout);
//		}
//	};

//	private View.OnClickListener mFfwdListener = new View.OnClickListener() {
//		public void onClick(View v) {
//			int pos = mPlayer.getCurrentPosition();
//			pos += 15000; // milliseconds
//			mPlayer.seekTo(pos);
//			setProgress();
//			if (mFileType != Const.FILETYPE_MUSIC) {
//				show(sDefaultTimeout);
//			}
//
//		}
//	};
 

//	public void setPrevNextListeners(View.OnClickListener next,
//			View.OnClickListener prev) {
//		mNextListener = next;
//		mPrevListener = prev;
//		mListenersSet = true;
//
//		if (mRoot != null) {
//			installPrevNextListeners();
//
//			if (mNextButton != null && !mFromXml) {
//				mNextButton.setVisibility(View.VISIBLE);
//			}
//			if (mPrevButton != null && !mFromXml) {
//				mPrevButton.setVisibility(View.VISIBLE);
//			}
//		}
//	}

	public interface MediaPlayerControl {
		void start();

		void pause();

		int getDuration();

		int getCurrentPosition();

		void seekTo(int pos);

		boolean isPlaying();

		int getBufferPercentage();

		boolean canPause();

		boolean canSeekBackward();

		boolean canSeekForward();
	}

}
