package sdmc.com.hometv;

import hometv.remote.socketUtil.ToastUtil;

import java.util.ArrayList;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.VLCApplication;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.sdmc.dlna.filebrowser.FileItem;
import com.sdmc.dlna.service.Callbacks;
import com.sdmc.dlna.service.DeviceItem;
import com.sdmc.dlna.service.NativeAccess;
import com.sdmc.dlna.service.Callbacks.DlnaControlInterface;
import com.sdmc.dlna.service.PlayUtil;
import com.sdmc.phone.util.BitmapUtil;

import sdmc.com.adapter.DlnaDevicesAdapter;
import sdmc.com.views.CustomMediaController;
import sdmc.com.views.CustomVlcVideoView;
import sdmc.com.views.DlnaPushDevicePopuWindow;
import android.app.Activity;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothClass.Device;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * DLNA 播放界面
 * @author fee
 *
 */
public class DlnaPlayerActivity extends Activity implements 
						IVideoPlayer,
						DlnaControlInterface,
						OnClickListener,
						OnItemClickListener{
	private final static String TAG = "DlnaPlayerActivity";
	private final static boolean DEBUG = true;
	

    // size of the video
    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;
    
    private static final int SURFACE_BEST_FIT = 4;
    private static final int SURFACE_FIT_HORIZONTAL = 3;
    private static final int SURFACE_FIT_VERTICAL = 5;
    private static final int SURFACE_FILL = 0;
    private static final int SURFACE_16_9 = 1;
    private static final int SURFACE_4_3 = 2;
    private static final int SURFACE_ORIGINAL = 6;
    private int mCurrentSize = SURFACE_FILL;
    
    private CustomVlcVideoView vlcVideoView;
    private CustomMediaController playController;
    private TextView tvDlnaResName;
    private LinearLayout loadIngLayout;
    private ImageView ivDlnaPlayMusicHint;
    private FrameLayout playContentLayout;
    private FrameLayout playImagesLayout;
    private VLCApplication app;
    // play file infos
    String mPath ;
    /**  1、视频类型   2、音乐类型    3、图片    -1、等待播放；
	   默认、类型无法识别
	 */
    int mFileType ;
    
    private static final int FILE_TYPE_VIDEO = 1;
    private static final int FILE_TYPE_MUSIC = 2;
    private static final int FILE_TYPE_PIC = 3;
    private static final int FILE_TYPE_UNKNOW = -1;
    boolean mDlnaIsInPushPlaying = false ;
    private static final int WAITPLAY = -1;
    private int curPlayPosition = -1;
    private int playListTotalCount = 0;
    /** 当前播放进度 **/
    private int curPlayProgress = 0; 
    private ArrayList<FileItem> curDlnaPlayFiles ;
    
    private DlnaPushDevicePopuWindow dlnaPushDevicePopuWindow;
    private DlnaDevicesAdapter adapterDlnaPushDevice ;
    
    // about images 
    ViewPager viewPagerShowPic;
     
    DisplayImageOptions options;
    ImageLoader imageLoader;
    private TextView tvDlnaPicName ;
    private ImageButton ivDlnaPicPush;
    private LinearLayout picPushLayout;
    private void initImageModle(){
    	tvDlnaPicName = (TextView) findViewById(R.id.dlna_pic_name);
    	ivDlnaPicPush = (ImageButton) findViewById(R.id.dlna_pic_push);
    	ivDlnaPicPush.setOnClickListener(this);
    	picPushLayout = (LinearLayout) findViewById(R.id.dlna_pic_push_layout);
    	
    	options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.pic_flag)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory()
		.cacheOnDisc()
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
        imageLoader = ImageLoader.getInstance();
        
        viewPagerShowPic = (ViewPager) findViewById(R.id.viewPage_show_img);
        viewPagerShowPic.setAdapter(new ImagePagerAdapter(curDlnaPlayFiles));
        viewPagerShowPic.setOnPageChangeListener(new PageChangeListener());
        viewPagerShowPic.setCurrentItem(curPlayPosition);
        
    }
    private String findDlnaDeviceById(ArrayList<DeviceItem> devices,int deviceId){
    	if(devices == null || devices.size() ==0) return null;
    	for(DeviceItem device : devices){
    		if(device.getDeviceID() == deviceId){
    			return device.getUdn();
    		}
    	}
    	return null;
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(DEBUG)
			Log.i("info",TAG+"---> onCreate()");
		setContentView(R.layout.dlna_play_layout);
//		generateBitmaps();
		if(savedInstanceState != null)
		 curPlayProgress = savedInstanceState.getInt(SAVE_PLAY_PROGRESS_KEY);
		dlnaPushDevicePopuWindow = new DlnaPushDevicePopuWindow(this);
		adapterDlnaPushDevice = new DlnaDevicesAdapter(this, null);
		dlnaPushDevicePopuWindow.setLvAdapter(adapterDlnaPushDevice);
		dlnaPushDevicePopuWindow.setLvOnitemClickListener(this);
		
		app = (VLCApplication) getApplication();
		curDlnaPlayFiles = app.getDlnaPlayList();
		playListTotalCount = curDlnaPlayFiles.size();
		curDlnaPushedDeviceId = app.curDlnaPushedDeviceId;
		
		String curPushDeviceUdn = findDlnaDeviceById(app.getDlnaPlayDevices(), curDlnaPushedDeviceId);
		adapterDlnaPushDevice.setCurrentCheckedItemBaseUdn(curPushDeviceUdn);
		
		playController = new CustomMediaController(this);
		playController.setPlayControllerOnclickListener(this);
		playController.setActivityOwner(this, curDlnaPushedDeviceId);
		
		vlcVideoView  = (CustomVlcVideoView) findViewById(R.id.vlc_videoview);
		vlcVideoView.setMediaController(playController);
		vlcVideoView.setActivity(this);
		vlcVideoView.switchDefaultMediaPlayer(false);
		 
		loadIngLayout = (LinearLayout) findViewById(R.id.dlna_play_loading_layout);
		ivDlnaPlayMusicHint = (ImageView) findViewById(R.id.dlna_play_music_hint);
		playContentLayout = (FrameLayout) findViewById(R.id.dlna_play_content_layout);
		tvDlnaResName = (TextView) findViewById(R.id.dlna_res_name);
		playImagesLayout = (FrameLayout) findViewById(R.id.play_pics_layout);
		
		Callbacks.registerDlnaControler(this);
		// get the play intent info
		Intent playIntent = getIntent();
		mPath = playIntent.getStringExtra(PlayUtil.PATH);
		mFileType = playIntent.getIntExtra(PlayUtil.FILETYPE, WAITPLAY);
		Log.i("info",TAG+" --> init()  mFileType= 1、video; 2、music;3、image; -1、wait; default unknow --> "+mFileType);
		mDlnaIsInPushPlaying = playIntent.getBooleanExtra(PlayUtil.DLNA_JUST_CONTROL, false);
		curPlayPosition = playIntent.getIntExtra(PlayUtil.TARGET_PLAY_POSITION, 0);
		mHandler.sendEmptyMessageDelayed(MSG_DISSMISS_RES_NAME, 3000);
		if(mFileType == FILE_TYPE_PIC){
			initImageModle();
		}
		startToPlayBaseFileType(mFileType);
	}
	private void startToPlayBaseFileType(int fileType){
		loadIngLayout.setVisibility(View.INVISIBLE);
		switch (fileType) {
		case FILE_TYPE_VIDEO: //视频
			if(mPath != null){
				 vlcVideoView.setVideoPath(mPath);
				 vlcVideoView.start();
			   }
			playContentLayout.setVisibility(View.VISIBLE);
			playImagesLayout.setVisibility(View.INVISIBLE);
			ivDlnaPlayMusicHint.setVisibility(View.INVISIBLE);
			FileItem curPlayVideoItem = curDlnaPlayFiles.get(curPlayPosition);
			tvDlnaResName.setText(curPlayVideoItem.getName());
			break;
		case FILE_TYPE_MUSIC:
			 if(mPath != null){
				 vlcVideoView.setVideoPath(mPath);
				 vlcVideoView.start();
			   }
			 playContentLayout.setVisibility(View.VISIBLE);
			 ivDlnaPlayMusicHint.setVisibility(View.VISIBLE);
			 playImagesLayout.setVisibility(View.INVISIBLE);
			 FileItem curPlayMusicItem = curDlnaPlayFiles.get(curPlayPosition);
				tvDlnaResName.setText(curPlayMusicItem.getName());
			break;
		case FILE_TYPE_PIC:
			loadIngLayout.setVisibility(View.VISIBLE);// 异步从网络上取图片
			mHandler.sendEmptyMessageDelayed(MSG_HAS_PIC_UPDATE, 5000);
			break;
		case FILE_TYPE_UNKNOW:
			break;
		}
	}
 
	@Override
	protected void onRestart() {
		super.onRestart();
		if(DEBUG)
		Log.i("info",TAG+"---> onRestart()");
	}
	@Override
	protected void onStart() {
		if(DEBUG)
		Log.i("info",TAG+"---> onStart()");
		super.onStart();
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(DEBUG)
		Log.i("info",TAG+"---> onResume() curPlayProgress= "+curPlayProgress);
		if(curPlayProgress > 0 && mPath != null && mFileType != FILE_TYPE_PIC){
//			vlcVideoView.setVideoPath(mPath);
			vlcVideoView.start();
			vlcVideoView.seekTo(curPlayProgress);
		}
		 /*
         * if the activity has been paused by pressing the power button,
         * pressing it again will show the lock screen.
         * But onResume will also be called, even if vlc-android is still in the background.
         * To workaround that, pause playback if the lockscreen is displayed
         */
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (vlcVideoView != null && vlcVideoView.isPlaying()) {
                    KeyguardManager km = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
                    if (km.inKeyguardRestrictedInputMode())
                    	vlcVideoView.pause();
                }
            }}, 500);
	}
	@Override
	protected void onPause() {
		super.onPause();
		if(DEBUG)
		Log.i("info",TAG+"---> onPause()");
		if(vlcVideoView != null){
			curPlayProgress = vlcVideoView.getCurrentPosition();
			vlcVideoView.pause();
		}
	}
	@Override
	protected void onStop() {
		super.onStop();
		if(DEBUG)
		Log.i("info",TAG+"---> onStop()");
		if(vlcVideoView != null){
//			curPlayProgress = vlcVideoView.getCurrentPosition();
			vlcVideoView.stopPlay();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(DEBUG)
		Log.i("info",TAG+"---> onDestroy()");
		vlcVideoView.vlcDestroy();
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
		if(DEBUG)
			Log.i("info",TAG+"---> onSaveInstanceState()");
		outState.putInt(SAVE_PLAY_PROGRESS_KEY, curPlayProgress);
	}
	private static final String SAVE_PLAY_PROGRESS_KEY = "save_progress";
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
		super.onConfigurationChanged(newConfig);
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(DEBUG)
		Log.i("info",TAG+"---> onNewIntent()");
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if(DEBUG)
		Log.i("info",TAG+"---> onCreateContextMenu()");
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(DEBUG)
		Log.i("info",TAG+"---> onTouchEvent()");
	 
		return super.onTouchEvent(event);
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(DEBUG)
		Log.i("info",TAG+"---> onBackPressed()");
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(DEBUG)
		Log.i("info",TAG+"---> onActivityResult()");
	}
	/** used in CustomVlcVideoView and CustomMediaControler **/
	public void showOrDismissResName(boolean needShow){
		if(needShow){
			tvDlnaResName.setVisibility(View.VISIBLE);
		}
		else{
			tvDlnaResName.setVisibility(View.INVISIBLE);
		}
	}
	
	 /**
     * 更改播放界面尺寸
     */
	public void changeSurfaceSize() {
		// get screen size
		int sw = getWindow().getDecorView().getWidth();
		int sh = getWindow().getDecorView().getHeight();

		double dw = sw, dh = sh;

		// getWindow().getDecorView() doesn't always take orientation into
		// account, we have to correct the values
		boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
		if (sw > sh && isPortrait || sw < sh && !isPortrait) {
			dw = sh;
			dh = sw;
		}

		// sanity check
		if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0) {
			Log.e("info", "Invalid surface size");
			return;
		}

		// compute the aspect ratio
		double ar, vw;
		double density = (double) mSarNum / (double) mSarDen;
		if (density == 1.0) {
			/* No indication about the density, assuming 1:1 */
			vw = mVideoVisibleWidth;
			ar = (double) mVideoVisibleWidth / (double) mVideoVisibleHeight;
		} else {
			/* Use the specified aspect ratio */
			vw = mVideoVisibleWidth * density;
			ar = vw / mVideoVisibleHeight;
		}

		// compute the display aspect ratio
		double dar = dw / dh;

		switch (mCurrentSize) {
		case SURFACE_BEST_FIT:
			if (dar < ar)
				dh = dw / ar;
			else
				dw = dh * ar;
			break;
		case SURFACE_FIT_HORIZONTAL:
			dh = dw / ar;
			break;
		case SURFACE_FIT_VERTICAL:
			dw = dh * ar;
			break;
		case SURFACE_FILL:
			break;
		case SURFACE_16_9:
			ar = 16.0 / 9.0;
			if (dar < ar)
				dh = dw / ar;
			else
				dw = dh * ar;
			break;
		case SURFACE_4_3:
			ar = 4.0 / 3.0;
			if (dar < ar)
				dh = dw / ar;
			else
				dw = dh * ar;
			break;
		case SURFACE_ORIGINAL:
			dh = mVideoVisibleHeight;
			dw = vw;
			break;
		}

		// force surface buffer size
		vlcVideoView.getHolder().setFixedSize(mVideoWidth, mVideoHeight);
		// set display size
		LayoutParams lp = vlcVideoView.getLayoutParams();
		lp.width = (int) Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
		lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
		vlcVideoView.setLayoutParams(lp);

		// set frame size (crop if necessary)
		// lp = mSurfaceFrame.getLayoutParams();
		lp.width = (int) Math.floor(dw);
		lp.height = (int) Math.floor(dh);
		// mSurfaceFrame.setLayoutParams(lp);

		vlcVideoView.invalidate();
	}
	@Override
	public void setSurfaceSize(int width, int height, int visible_width,
			int visible_height, int sar_num, int sar_den) {
		if (width * height == 0)
			return;
		// store video size
		mVideoHeight = height;
		mVideoWidth = width;
		mVideoVisibleHeight = visible_height;
		mVideoVisibleWidth = visible_width;
		mSarNum = sar_num;
		mSarDen = sar_den;
		Message msg = vlcVideoView.mChangeSizeHandler.obtainMessage(888);
		vlcVideoView.mChangeSizeHandler.sendMessage(msg);
		
	}
	
 
	/** 加载数据 **/
	private void loadIngData(){
		loadIngLayout.setVisibility(View.VISIBLE);
		playContentLayout.setVisibility(View.INVISIBLE);
		playImagesLayout.setVisibility(View.INVISIBLE);
	}
	
	public void dealWithVlcPlayEvent(int vlcPlayEvent){
		switch (vlcPlayEvent) {
		 case EventHandler.MediaPlayerPlaying:
             Log.i("info", TAG+" --- > MediaPlayerPlaying");
             
             //activity.setESTracks();
             break;
         case EventHandler.MediaPlayerPaused:
             Log.i("info", "MediaPlayerPaused");
             break;
         case EventHandler.MediaPlayerStopped:
             Log.i("info", "MediaPlayerStopped");
             break;
         case EventHandler.MediaPlayerEndReached:
             Log.i("info", "MediaPlayerEndReached " );
              
             break;
         case EventHandler.MediaPlayerVout:
         	Log.i("info", "MediaPlayerVout");
//             activity.handleVout(msg);
             break;
         case EventHandler.MediaPlayerPositionChanged:
             //don't spam the logs
             break;
         default:
             Log.e("info",  " event can not handler ");
             break;
		}
	}
	
	
	@Override
	public void onClick(View v) {
		boolean isClickedPlayControl = false;
		int viewId = v.getId();
		switch (viewId) {
		case R.id.image:
			if(picPushLayout != null){
				if(picPushLayout.getVisibility() != View.VISIBLE){
					picPushLayout.setVisibility(View.VISIBLE);
				}else{
					picPushLayout.setVisibility(View.INVISIBLE);
				}
			}
			break;
		case R.id.dlna_pic_push: //显示图片时的单击事件
			ArrayList<DeviceItem> dlnaCanPushDevices1 = app.getDlnaPlayDevices();
			 
			if(dlnaCanPushDevices1 == null || dlnaCanPushDevices1.size() == 0	){
				// 
				ToastUtil.showToast(R.string.dlna_no_device_can_push, Gravity.CENTER);
			}
			else{
				adapterDlnaPushDevice.updateListData(dlnaCanPushDevices1);
				dlnaPushDevicePopuWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
			}
			break;
		case R.id.dlna_push: //推送
			ArrayList<DeviceItem> dlnaCanPushDevices = app.getDlnaPlayDevices();
			if(dlnaCanPushDevices == null || dlnaCanPushDevices.size() == 0	){
				// 
				ToastUtil.showToast(R.string.dlna_no_device_can_push, Gravity.CENTER);
			}
			else{
				adapterDlnaPushDevice.updateListData(dlnaCanPushDevices);
				dlnaPushDevicePopuWindow.showAtLocation(playContentLayout, Gravity.CENTER, 0, 0);
			}
			break;
		case R.id.player_next: // 下一曲
			if(curPlayPosition < playListTotalCount-1 ){
				curPlayPosition ++;
			}else{
				curPlayPosition = 0;
			}
			FileItem curPlayNextItem = curDlnaPlayFiles.get(curPlayPosition);
			tvDlnaResName.setText(curPlayNextItem.getName());
			mPath = curPlayNextItem.getAbsPath();
//			vlcVideoView.stopPlay();
//			vlcVideoView.vlcPlay(mPath);
			 vlcVideoView.setVideoPath(mPath);
			 vlcVideoView.start();
			isClickedPlayControl = true;
		
			break;
		case R.id.player_pre: //上一曲
			if(curPlayPosition <= 0){
				curPlayPosition = playListTotalCount -1;
			}
			else{
				curPlayPosition --;
			}
			FileItem curPlayPreItem = curDlnaPlayFiles.get(curPlayPosition);
			tvDlnaResName.setText(curPlayPreItem.getName());
//			vlcVideoView.stopPlay();
			mPath = curPlayPreItem.getAbsPath();
//			vlcVideoView.vlcPlay(mPath);
			 vlcVideoView.setVideoPath(mPath);
			 vlcVideoView.start();
			isClickedPlayControl = true;
			
			break;
		}
		if(isClickedPlayControl && mDlnaIsInPushPlaying){
			NativeAccess.playPic(curDlnaPushedDeviceId, mPath);
		}
	}
	private int curSelectedPlayDeviceIndex = 0;
	private int curDlnaPushedDeviceId = 0;
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int listViewId = parent.getId();
		switch (listViewId) {
		case R.id.lv_dlna_push_devices:
			curDlnaPushedDeviceId = (int) adapterDlnaPushDevice.getItemId(position);
			DeviceItem curSelected = app.getDlnaPlayDevices().get(position);
			String curUdn = curSelected.getUdn();
			adapterDlnaPushDevice.setCurrentCheckedItemBaseUdn(curUdn);
//			adapterDlnaPushDevice.setCurrentCheckedItem(position);
			NativeAccess.playPic(curDlnaPushedDeviceId, mPath);
			playController.setActivityOwner(this, curDlnaPushedDeviceId);
			mDlnaIsInPushPlaying = true;
			dlnaPushDevicePopuWindow.dismiss();
			if(picPushLayout != null){
				picPushLayout.setVisibility(View.INVISIBLE);
			}
			break;
		}
		
	}
	private static final int MSG_DISSMISS_RES_NAME = 0;
	private static final int MSG_HAS_PIC_UPDATE = 1;
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			int msgWhat = msg.what;
			switch (msgWhat) {
			case MSG_DISSMISS_RES_NAME:
				tvDlnaResName.setVisibility(View.INVISIBLE);
				break;
			case MSG_HAS_PIC_UPDATE:
				loadIngLayout.setVisibility(View.INVISIBLE);
				playContentLayout.setVisibility(View.INVISIBLE);
				playImagesLayout.setVisibility(View.VISIBLE);
				break;
			}
		};
	};
	private class ImagePagerAdapter extends PagerAdapter {

		private ArrayList<FileItem> images;
		private LayoutInflater inflater;

		ImagePagerAdapter(ArrayList<FileItem> images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
				imageView.setOnClickListener(DlnaPlayerActivity.this);
				
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			FileItem curPicFileItem = images.get(position);
			tvDlnaPicName .setText(curPicFileItem.getName());
			mPath = curPicFileItem.getAbsPath();
			imageLoader.displayImage(curPicFileItem.getAbsPath(), imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					String message = null;
					switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
					}
					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});

			((ViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}
	private class PageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int arg0) {
			curPlayPosition = arg0;
		}
		
	}
	// dlna  播放 控制 接口 相关 主要是 外部推送到手机播放 down down down //
		@Override
		public void dlnaPLay() {
			
		}
		@Override
		public void dlnaPause() {
			
		}
		@Override
		public void dlnaStop() {
			
		}
		@Override
		public void dlnaWaitPlay() {
			
		}
		@Override
		public void setDlnaPlayProgress(int progress) {
			
		}
		@Override
		public void dlnaSpeedUp() {
			
		}
		@Override
		public void dlnaSpeedDown() {
			
		}
		@Override
		public int playImage(int type, String path) {
			return 0;
		}
		// dlna  播放 控制 接口 相关   up up up  //
		
// ----------------------------------- end  save some no use methods -------------------		
		  // add 
//	    private Bitmap createReflectedBitmapById(int resId)
//	    {
//	        Drawable drawable = getResources().getDrawable(resId);
//	        if (drawable instanceof BitmapDrawable)
//	        {
//	            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
//	            Bitmap reflectedBitmap = BitmapUtil.createReflectedBitmap(bitmap);
//	            
//	            return reflectedBitmap;
//	        }
//	        
//	        return null;
//	    }
	    
//	    private void generateBitmaps()
//	    {
//	        int[] ids =
//	        {
//	            R.drawable.bg_welcome1,
//	            R.drawable.bg_welcome2,
//	            R.drawable.all,
//	            R.drawable.volume_icon,
//	        };
//	        
//	        for (int id : ids)
//	        {
//	            Bitmap bitmap = createReflectedBitmapById(id);
//	            if (null != bitmap)
//	            {
//	                BitmapDrawable drawable = new BitmapDrawable(bitmap);
//	                drawable.setAntiAlias(true);
//	                mBitmaps.add(drawable);
//	            }
//	        }
//	    }
	    // add end	
}
