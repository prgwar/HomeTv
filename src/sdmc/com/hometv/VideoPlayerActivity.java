/*****************************************************************************
 * VideoPlayerActivity.java
 *****************************************************************************
 * Copyright © 2011-2013 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package sdmc.com.hometv;

import hometv.remote.bean.NetInterface;
import hometv.remote.bean.NetModeManager;
import hometv.remote.bean.NetProtecolInterface;
import hometv.remote.socketUtil.ConnectUtil;
import hometv.remote.socketUtil.Constant;
import hometv.remote.socketUtil.ToastUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.libvlc.VLCApplication;
import org.videolan.vlc.Util;
import org.videolan.vlc.WeakHandler;

import com.sdmc.dtv.programsend.Program;
import com.sdmc.dtv.programsend.ProgramRunnable;
import com.sdmc.dtv.programsend.ShortEPG;
import com.sdmc.phone.stat.EventType;

import sdmc.com.adapter.ProgramAdapter;
import sdmc.com.views.CommonEditDialog;
import sdmc.com.views.DetailsEpgPopuWindow;
import sdmc.com.views.ProgramPopuWindow;
import sdmc.com.views.SimpleEpgPopuWindow;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings.SettingNotFoundException;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VideoPlayerActivity extends Activity implements 
					IVideoPlayer,
					OnClickListener, 
					NetInterface,
					OnItemClickListener,
					OnItemLongClickListener{
    public final static String TAG = "VideoPlayerActivity";

    // Internal intent identifier to distinguish between internal launch and
    // external intent.
    private final static String PLAY_FROM_VIDEOGRID = "org.videolan.vlc.gui.video.PLAY_FROM_VIDEOGRID";

    private SurfaceView mSurface;
    private SurfaceHolder mSurfaceHolder;
    /** mSurface 的父控件  **/
    private FrameLayout mSurfaceFrame;
    private LibVLC mLibVLC;
    private String mLocation;
   
    private static final int SURFACE_BEST_FIT = 4;
    private static final int SURFACE_FIT_HORIZONTAL = 3;
    private static final int SURFACE_FIT_VERTICAL = 5;
    private static final int SURFACE_FILL = 0;
    private static final int SURFACE_16_9 = 1;
    private static final int SURFACE_4_3 = 2;
    private static final int SURFACE_ORIGINAL = 6;
    private int mCurrentSize = SURFACE_FILL;

    /** Overlay  头部信息，电量、系统时间 */
    private View mOverlayHeader;
    /**  锁屏、更多菜单操作     */
    private View mOverlayOption;
    
    private static final int OVERLAY_TIMEOUT = 4000;
    private static final int OVERLAY_INFINITE = 3600000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private static final int SURFACE_SIZE = 3;
    private static final int FADE_OUT_INFO = 4;
    private boolean mDragging;
    private boolean mShowing;
    private int mUiVisibility = -1;
    private TextView mTitle;
    private TextView mSysTime;
    private TextView mBattery;
    private ListView lvMenuContent;
    ArrayAdapter<String > menuContentAdapter;
    private TextView mInfo;
    private boolean mEnableJumpButtons;
    private boolean mEnableBrightnessGesture;
    private boolean mDisplayRemainingTime = false;
    private int mScreenOrientation;
    private ImageButton mLock;
    private ImageButton mMenu;
    Button  btnShowProgrameList;
    private Animation menuAnimUp;
    private Animation menuAnimDown;
    private boolean mIsLocked = false;
    private int mLastAudioTrack = -1;
    private int mLastSpuTrack = -2;

    /**
     * For uninterrupted switching between audio and video mode
     */
    private boolean mSwitchingView;
    private boolean mEndReached;
    private boolean mCanSeek;

    // Playlist
    private int savedIndexPosition = -1;

    // size of the video
    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;

    //Volume
    private AudioManager mAudioManager;
    private int mAudioMax;
    private OnAudioFocusChangeListener mAudioFocusListener;

    //Touch Events
    private static final int TOUCH_NONE = 0;
    private static final int TOUCH_VOLUME = 1;
    private static final int TOUCH_BRIGHTNESS = 2;
    private static final int TOUCH_SEEK = 3;
    private int mTouchAction;
    private int mSurfaceYDisplayRange;
    private float mTouchY, mTouchX, mVol;

    // Brightness
    private boolean mIsFirstBrightnessGesture = true;

    /** Tracks & Subtitles 声道、字幕 **/ 
    private Map<Integer,String> mAudioTracksList;
    private Map<Integer,String> mSubtitleTracksList;
    /**
     * Used to store a selected subtitle; see onActivityResult.
     * It is possible to have multiple custom subs in one session
     * (just like desktop VLC allows you as well.)
     */
    private ArrayList<String> mSubtitleSelectedFiles = new ArrayList<String>();

    private VLCApplication app;
    private ConnectUtil connectUtil=null;
    private String url = null;
    
    /**
	 * 0
	 */
	private final byte PVR_STOP_CMD = 0;
	/**
	 * 1
	 */
	private final byte PVR_START_CMD = 1;
	/**
	 * 2
	 */
	private final byte PVR_QUERY_CMD = 2;
	
	private boolean sendPvrCmdInResume = false;
	
	boolean firstCurAv = true;
	
	private boolean isInPvr = false;
	/**
	 * 判断当前是否正在播放
	 */
	boolean isPlaying;
	private RelativeLayout viewRoot = null;
	private RelativeLayout loadingLayout =null;
	private RelativeLayout functionsLayout = null;
	private ImageView ivAnim;
	private AnimationDrawable horseLoading;
	/** 获取局域网连接时的各种节目列表	 */
	private ArrayList<Program>  curProgramList;
	private ArrayList<Program> tvPrograms;
	private ArrayList<Program> favoritePrograms;
	private ArrayList<Program> radioPrograms;
	private ArrayList<Program> mFindPrograms = new ArrayList<Program>();
	private ArrayList<ArrayList<Program>> totalPrograms;
	private SparseArray<String[]> array_menu_item_content = new SparseArray<String[]>();
	private SimpleEpgPopuWindow simpleEpgPopuWindow;
	private DetailsEpgPopuWindow detailsEpgPopuWindow;
	ShortEPG mShortEPG;
	private NetProtecolInterface netImp;
	private NetModeManager netModeManager;
	private CommonEditDialog commonEditDialog;
	@Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("info"," VideoPlayerActivity------------ > onCreate()   " );
        setContentView(R.layout.player);
        Resources res =getResources();
        array_menu_item_content.put(0, res.getStringArray(R.array.pvr_selections));
        array_menu_item_content.put(1,res.getStringArray(R.array.screen_size));
        array_menu_item_content.put(5, res.getStringArray(R.array.pic_selections));
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
//        if(Util.isICSOrLater())
//        	            getWindow().getDecorView().findViewById(android.R.id.content)
//        	            .setOnSystemUiVisibilityChangeListener(
//                    new OnSystemUiVisibilityChangeListener() {
//                        @Override
//                        public void onSystemUiVisibilityChange(int visibility) {
//                            if (visibility == mUiVisibility)
//                                return;
//                            setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
//                            if (visibility == View.SYSTEM_UI_FLAG_VISIBLE && !mShowing && !isFinishing()) {
//                                showOverlay();
//                            }
//                            mUiVisibility = visibility;
//                        }
//                    }
//            );
        // init views      
        viewRoot=(RelativeLayout)findViewById(R.id.video_play_view_root);
        /** initialize Views and their Events */
        mOverlayHeader = findViewById(R.id.player_overlay_header);
        mOverlayOption = findViewById(R.id.option_overlay);
        
        loadingLayout =(RelativeLayout)findViewById(R.id.loading_layout);
        functionsLayout =(RelativeLayout)findViewById(R.id.functions_layout);
        ivAnim =(ImageView) findViewById(R.id.iv_anime);
        horseLoading = (AnimationDrawable) ivAnim.getDrawable();  
        horseLoading.start();  
        /* header */
        mTitle = (TextView) findViewById(R.id.player_overlay_title);
       
        mSysTime = (TextView) findViewById(R.id.player_overlay_systime);
        mBattery = (TextView) findViewById(R.id.player_overlay_battery);

        // Position and remaining time  7204 remove this
//        mTime = (TextView) findViewById(R.id.player_overlay_time);
//        mTime.setOnClickListener(mRemainingTimeListener);
//        mLength = (TextView) findViewById(R.id.player_overlay_length);
//        mLength.setOnClickListener(mRemainingTimeListener);

        // the info textView is not on the overlay
        mInfo = (TextView) findViewById(R.id.player_overlay_info);
        mSurface = (SurfaceView) findViewById(R.id.player_surface);
        mSurfaceHolder = mSurface.getHolder();
        mSurfaceFrame = (FrameLayout) findViewById(R.id.player_surface_frame);
        lvMenuContent = (ListView) findViewById(R.id.lv_menu_item_content);
        mLock = (ImageButton) findViewById(R.id.ivbtn_lock_screen);
        mLock.setOnClickListener(this);
        mMenu = (ImageButton) findViewById(R.id.ivbtn_more_menu);
        mMenu.setOnClickListener(this);
        btnShowProgrameList = (Button) findViewById(R.id.btn_list);
        btnShowProgrameList.setOnClickListener(this);
        // end init views
        
        commonEditDialog = new CommonEditDialog(this, 350, 300);
        commonEditDialog.setOnclickListener(this);
        //init the menu for more, show item menu's oprations
        menuContentAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice );
        menuContentAdapter.setNotifyOnChange(true);
        lvMenuContent.setAdapter(menuContentAdapter);
        lvMenuContent.setChoiceMode(ListView.CHOICE_MODE_SINGLE	);
        lvMenuContent.setOnItemClickListener(this);
        
        mEnableBrightnessGesture = pref.getBoolean("enable_brightness_gesture", true);
        mScreenOrientation = Integer.valueOf(
                pref.getString("screen_orientation_value", "4" /*SCREEN_ORIENTATION_SENSOR*/));

        mEnableJumpButtons = pref.getBoolean("enable_jump_buttons", false);

        
        menuAnimUp = AnimationUtils.loadAnimation(this, R.anim.btn_translate_up_anim);
        menuAnimDown = AnimationUtils.loadAnimation(this, R.anim.btn_translate_down_anim);
        menuAnimDown.setAnimationListener(animationListener);
        menuAnimUp.setAnimationListener(animationListener);
         
        
        // 创建菜单、节目列表布局
        moreMenuPopupWindow = createMoreMenuPopuWindow();
        programListWindow = new ProgramPopuWindow(this);
        simpleEpgPopuWindow = new SimpleEpgPopuWindow(this);
        
        app = (VLCApplication) getApplication();
        //获取当前网络类型
        netModeManager = NetModeManager.getNetModeManager();
        netImp = netModeManager.getCurNetMode();
        if( netImp ==null) return;
       
        mShortEPG = netImp.getShortEPG();
        if(mShortEPG !=null){
        	simpleEpgPopuWindow.setCurProgramShortEpg(mShortEPG);
        }
        detailsEpgPopuWindow  = new DetailsEpgPopuWindow(this);
        detailsEpgPopuWindow.updateEpgList(netImp.getDetailEpgs());
        int curPlayProgramId = netImp.getCurPlayingProgramId();
        curSelectedProgram = curPlayProgramId;
        
        HashMap<String, ArrayList<Program>> programData = netImp.getProgramData();
        
        tvPrograms = programData.get(Constant.PROGRAM_TV_KEY);
        favoritePrograms = programData.get(Constant.PROGRAM_FAVORATE_KEY);
        radioPrograms = programData.get(Constant.PROGRAM_RADIO_KEY);
        ArrayList<Program> allPrograms = programData.get(Constant.PROGRAM_ALL);
        if(curPlayProgramId < allPrograms.size()){
        	Program curPlayProgram = allPrograms.get(curPlayProgramId);
        
        if(!curPlayProgram.isLock()){
        	//没有锁定，直接播放
        	//播放当前播放的节目
            netImp.programPlay(curPlayProgramId, 1-curPlayProgram.getType(), "");
        }else{
        	//输入父母锁密码
        	commonEditDialog.show(CommonEditDialog.DIALOG_TYPE_ENTER_PARENT_UNLOCK);
        	viewLayout(false);
        }
        }
        totalPrograms = new ArrayList<ArrayList<Program>>();
        totalPrograms.add(tvPrograms);
        totalPrograms.add(radioPrograms);
        totalPrograms.add(favoritePrograms);
        curProgramList = totalPrograms.get(0);
        // 
        programAdapter = new ProgramAdapter(this,totalPrograms.get(0), curPlayProgramId, null);
        programListWindow.setLvAdapter(programAdapter);
        programListWindow.setOnclickListener(clickListenerForProgramWindow);
        programListWindow.setTextWatcherForEditText(mTextWatcher);
        programListWindow.setLvOnItemClickListener(this);
        programListWindow.setLvOnItemLongClickListener(this);
        
        String chroma = pref.getString("chroma_format", "");
        if(Util.isGingerbreadOrLater() && chroma.equals("YV12")) {
            mSurfaceHolder.setFormat(ImageFormat.YV12);
        } else if (chroma.equals("RV16")) {
            mSurfaceHolder.setFormat(PixelFormat.RGB_565);
        } else {
            mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
        }
        mSurfaceHolder.addCallback(mSurfaceCallback);


        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mSwitchingView = false;
        mEndReached = false;
        try {
            mLibVLC = Util.getLibVlcInstance();
            mLibVLC.setNetworkCaching(2000);
        } catch (LibVlcException e) {
            Log.e("info", TAG+"--> LibVLC initialisation failed");
            return;
        }
      
        
        // 注册 系统电量广播接收
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
//        filter.addAction(VLCApplication.SLEEP_INTENT);
        registerReceiver(mReceiver, filter);
        
       
        
		connectUtil = ConnectUtil.getInstance();
		 
        EventHandler em = EventHandler.getInstance();
        em.addHandler(eventHandler);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // 100 is the value for screen_orientation_start_lock
//        setRequestedOrientation(mScreenOrientation != 100
//                ? mScreenOrientation
//                : getScreenOrientation());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("info",TAG+" -----------> onPause()");
        if(mSwitchingView) {
            Log.d(TAG, "mLocation = \"" + mLocation + "\"");
//            AudioServiceController.getInstance().showWithoutParse(savedIndexPosition);
//            AudioServiceController.getInstance().unbindAudioService(this);
            return;
        }

      /*  long time = mLibVLC.getTime();
        long length = mLibVLC.getLength();
        //remove saved position if in the last 5 seconds
        if (length - time < 5000)
            time = 0;
        else
            time -= 5000; // go back 5 seconds, to compensate loading time
*/
        /*
         * Pausing here generates errors because the vout is constantly
         * trying to refresh itself every 80ms while the surface is not
         * accessible anymore.
         * To workaround that, we keep the last known position in the playlist
         * in savedIndexPosition to be able to restore it during onResume().
         */
        if(mLibVLC != null)
        	mLibVLC.stop();

        mSurface.setKeepScreenOn(false);

 
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("info",TAG+" -----------> onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("info",TAG+" -----------> onDestroy()");
        if (netImp != null)
			netImp.stopPlay();
        unregisterReceiver(mReceiver);

        EventHandler em = EventHandler.getInstance();
        em.removeHandler(eventHandler);
        mAudioManager = null;
        mHandler.removeCallbacks(mGetURLRunnable);
        if(programListWindow .isShowing()) programListWindow.dismiss();
        if(moreMenuPopupWindow.isShowing()) moreMenuPopupWindow.dismiss();
        detailsEpgPopuWindow.dismiss();
        detailsEpgPopuWindow.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("info",TAG+" -----------> onResume()");
        app.setCurActivity(this);
        mSwitchingView = false;
        if(!isPlaying && url != null){
        	mLibVLC.playMRL(url);
        }
//        AudioServiceController.getInstance().bindAudioService(this);
  
        /*
         * if the activity has been paused by pressing the power button,
         * pressing it again will show the lock screen.
         * But onResume will also be called, even if vlc-android is still in the background.
         * To workaround that, pause playback if the lockscreen is displayed
         */
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mLibVLC != null && mLibVLC.isPlaying()) {
                    KeyguardManager km = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
                    if (km.inKeyguardRestrictedInputMode()) 
                        mLibVLC.pause();
                }
            }}, 500);

        // Add any selected subtitle file from the file picker
        if(mSubtitleSelectedFiles.size() > 0) {
            for(String file : mSubtitleSelectedFiles) {
                Log.i("info", TAG+" Adding user-selected subtitle " + file);
                mLibVLC.addSubtitleTrack(file);
            }
        }
//		// 查询AV线路
//		connectUtil.commonSendToServer(Constant.QUERY_BOX_CUR_AV_CHANNEL);
//		// 查询当前PVR状态
//		connectUtil.commonSendToServer(Constant.START_STOP_QUERY_PVR,PVR_QUERY_CMD);
//		sendPvrCmdInResume = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null) return;

        if(data.getDataString() == null) {
            Log.d(TAG, "Subtitle selection dialog was cancelled");
        }
        if(data.getData() == null) return;

//        String uri = data.getData().getPath();
//        if(requestCode == CommonDialogs.INTENT_SPECIFIC) {
//            Log.d(TAG, "Specific subtitle file: " + uri);
//        } else if(requestCode == CommonDialogs.INTENT_GENERIC) {
//            Log.d(TAG, "Generic subtitle file: " + uri);
//        }
        mSubtitleSelectedFiles.add(data.getData().getPath());
    }

    public static void start(Context context, String location) {
        start(context, location, null, -1, false, false);
    }
    /**
     * VideoGridFragment 里的item单击时调�?    
     * @param context
     * @param location
     * @param fromStart
     */
    public static void start(Context context, String location, Boolean fromStart) {
        start(context, location, null, -1, false, fromStart);
    }

    public static void start(Context context, String location, String title, Boolean dontParse) {
        start(context, location, title, -1, dontParse, false);
    }

    public static void start(Context context, String location, String title, int position, Boolean dontParse) {
        start(context, location, title, position, dontParse, false);
    }
    /**
     * 从本地视频列表中去启�?     * AudioService 里检测到有视频输出也会启�? dontParse=true;
     * @param context
     * @param location
     * @param title
     * @param position
     * @param dontParse
     * @param fromStart  
     */
    public static void start(Context context, String location, String title, int position, Boolean dontParse, Boolean fromStart) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.setAction(VideoPlayerActivity.PLAY_FROM_VIDEOGRID);
        intent.putExtra("itemLocation", location);
        intent.putExtra("itemTitle", title);
        intent.putExtra("dontParse", dontParse); //从视频列表那启动时为false
        intent.putExtra("fromStart", fromStart);
        intent.putExtra("itemPosition", position);

        if (dontParse)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        else {
            // Stop the currently running audio
//            AudioServiceController asc = AudioServiceController.getInstance();
//            asc.stop();
        }

        context.startActivity(intent);
    }
    /** 监听电量 信息 **/
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_CHANGED)) {
                int batteryLevel = intent.getIntExtra("level", 0);
                int drawableLeftId = 0;
                if(batteryLevel == 100){
                	mBattery.setTextColor(Color.GREEN);
                	drawableLeftId = R.drawable.power_full;
                }
                else if (batteryLevel >= 50){
                	mBattery.setTextColor(Color.GREEN);
                	drawableLeftId = R.drawable.power_half;
                }
                else if (batteryLevel >= 30){
                    mBattery.setTextColor(Color.YELLOW);
                    drawableLeftId = R.drawable.power_third;
                }
                 
                else{
                	mBattery.setTextColor(Color.RED);
                	 drawableLeftId = R.drawable.power_below_tenth;
                }
              mBattery.setText(String.format("%d%%", batteryLevel));
              mBattery.setCompoundDrawablesWithIntrinsicBounds(drawableLeftId, 0, 0, 0);
            }
//            else if (action.equalsIgnoreCase(VLCApplication.SLEEP_INTENT)) {
//                finish();
//            }
        }
    };

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        showOverlay();
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
    		Log.e("info"," screen turn to portrait...");
    		return;
    	}
        setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
    }
 
  /**
   * 创建列表对话框
   */
  @Override
  protected Dialog onCreateDialog(int id) {
      Dialog dialog=null;
      switch (id) {
      case 1:
          Builder builder=new android.app.AlertDialog.Builder(this);
          //设置对话框的图标
//          builder.setIcon(R.drawable.header);
          //设置对话框的标题
          builder.setTitle(R.string.operate);
          
          builder.setItems(R.array.program_option, new DialogInterface.OnClickListener(){
              public void onClick(DialogInterface dialog, int which) {
            	  byte optionType =EventType.EVENT_PROGRAM_FAV;
            	  longPressedOption = which;
                  switch (which) {
					case SET_FAV:
						//设置喜爱
						optionType =EventType.EVENT_PROGRAM_FAV;
						break;
					case SET_LOCK:
						optionType =EventType.EVENT_PROGRAM_LOCK;
						break;
					case SET_SKIP:
						optionType =EventType.EVENT_PROGRAM_SKIP;
						break; 
				}//end switch
                  netImp.editProgram(optionType, longPressedProgramId);
              }
          });
          //创建一个列表对话框
          dialog=builder.create();
          break;
      }
      return dialog;
  }
  
    @Override
    public void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {
        if (width * height == 0)
            return;
        // store video size
        mVideoHeight = height;
        mVideoWidth = width;
        mVideoVisibleHeight = visible_height;
        mVideoVisibleWidth  = visible_width;
        mSarNum = sar_num;
        mSarDen = sar_den;
        Message msg = mHandler.obtainMessage(SURFACE_SIZE);
        mHandler.sendMessage(msg);
    }

    /**
     * Lock screen rotation
     */
    private void lockScreen() {
        if(mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                setRequestedOrientation(14 /* SCREEN_ORIENTATION_LOCKED */);
            else
                setRequestedOrientation(getScreenOrientation());
        }
        showInfo(R.string.locked, 1000);
        mLock.setBackgroundResource(R.drawable.screen_lock);
        mMenu.setEnabled(false);
        hideOverlay(true);
    }

    /**
     * Remove screen lock
     */
    private void unlockScreen() {
        if(mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        showInfo(R.string.unlocked, 1000);
        mLock.setBackgroundResource(R.drawable.screen_unlock);
        
        mMenu.setEnabled(true);
        mShowing = false;
        showOverlay();
    }

    /**
     * Show text in the info view for "duration" milliseconds
     * @param text
     * @param duration
     */
    private void showInfo(String text, int duration) {
        mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(text);
        mHandler.removeMessages(FADE_OUT_INFO);
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
    }

    private void showInfo(int textid, int duration) {
        mInfo.setVisibility(View.VISIBLE);
        mInfo.setText(textid);
        mHandler.removeMessages(FADE_OUT_INFO);
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
    }


    /**
     * hide the info view with "delay" milliseconds delay
     * @param delay
     */
    private void hideInfo(int delay) {
        mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, delay);
    }


    private void fadeOutInfo() {
        if (mInfo.getVisibility() == View.VISIBLE)
            mInfo.startAnimation(AnimationUtils.loadAnimation(
                    VideoPlayerActivity.this, android.R.anim.fade_out));
        mInfo.setVisibility(View.INVISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private void changeAudioFocus(boolean gain) {
        if(!Util.isFroyoOrLater()) // NOP if not supported
            return;

        if (mAudioFocusListener == null) {
            mAudioFocusListener = new OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    /*
                     * Pause playback during alerts and notifications
                     */
                    switch (focusChange)
                    {
                        case AudioManager.AUDIOFOCUS_LOSS:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            if (mLibVLC.isPlaying())
                                mLibVLC.pause();
                            break;
                        case AudioManager.AUDIOFOCUS_GAIN:
                        case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                        case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                            if (!mLibVLC.isPlaying())
                                mLibVLC.play();
                            break;
                    }
                }
            };
        }

        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        if(gain)
            am.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        else
            am.abandonAudioFocus(mAudioFocusListener);
    }

    /**
     *  Handle libvlc asynchronous events
     */
    private final Handler eventHandler = new VideoPlayerEventHandler(this);

    private static class VideoPlayerEventHandler extends WeakHandler<VideoPlayerActivity> {
        public VideoPlayerEventHandler(VideoPlayerActivity owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoPlayerActivity activity = getOwner();
            if(activity == null) return;

            switch (msg.getData().getInt("event")) {
                case EventHandler.MediaPlayerPlaying:
                    Log.i("info", TAG+" eventHandler-- > MediaPlayerPlaying");
                    activity.isPlaying = true;
                    activity. viewLayout(true);
                    activity.showOverlay();
                    /** FIXME: update the track list when it changes during the
                     *  playback. (#7540) */
                    activity.setESTrackLists(true);
                    activity.setESTracks();
                    activity.changeAudioFocus(true);
                    break;
                case EventHandler.MediaPlayerPaused:
                    Log.i("info", TAG+" eventHandler-- >MediaPlayerPaused");
                    break;
                case EventHandler.MediaPlayerStopped:
                    Log.i("info", TAG+" eventHandler-- >MediaPlayerStopped");
                    activity.isPlaying = false;
                    activity.changeAudioFocus(false);
                    break;
                case EventHandler.MediaPlayerEndReached:
                    Log.i("info", TAG+" eventHandler-- >MediaPlayerEndReached");
                    activity.isPlaying = false;
                    activity.changeAudioFocus(false);
//                    activity.endReached();
                    break;
                case EventHandler.MediaPlayerVout:
                    activity.handleVout(msg);
                    break;
                case EventHandler.MediaPlayerPositionChanged:
                    if (!activity.mCanSeek)
                        activity.mCanSeek = true;
                    //don't spam the logs
                    break;
                case EventHandler.MediaPlayerEncounteredError:
                    Log.i("info", TAG+" eventHandler-- >MediaPlayerEncounteredError");
                    activity.encounteredError();
                    break;
                default:
                    Log.e("info", String.format("Event not handled (0x%x)", msg.getData().getInt("event")));
                    break;
            }
//            activity.updateOverlayPausePlay();
        }
    };

    /**
     * Handle resize of the surface and the overlay
     */
    private final Handler mHandler = new VideoPlayerHandler(this);

    private static class VideoPlayerHandler extends WeakHandler<VideoPlayerActivity> {
        public VideoPlayerHandler(VideoPlayerActivity owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoPlayerActivity activity = getOwner();
            if(activity == null) // WeakReference could be GC'ed early
                return;

            switch (msg.what) {
                case FADE_OUT: //隐藏布局
                	if(activity.moreMenuPopupWindow.isShowing() ) break;
                    activity.hideOverlay(false);
                    break;
                case SHOW_PROGRESS: //显示进度条
//                    int pos = activity.setOverlayProgress();
//                    if (activity.canShowProgress()) {
//                        msg = obtainMessage(SHOW_PROGRESS);
//                        sendMessageDelayed(msg, 1000 - (pos % 1000));
//                    }
                    break;
                case SURFACE_SIZE:
                    activity.changeSurfaceSize();
                    break;
                case FADE_OUT_INFO:
                    activity.fadeOutInfo();
                    break;
            }
        }
    };

  

    private void endReached() {
        if(mLibVLC.getMediaList().expandMedia(savedIndexPosition) == 0) {
            Log.d(TAG, "Found a video playlist, expanding it");
            eventHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    load();
                }
            }, 1000);
        } else {
            /* Exit player when reaching the end */
            mEndReached = true;
            finish();
        }
    }

    private void encounteredError() {
        /* Encountered Error, exit player with a message */
        AlertDialog dialog = new AlertDialog.Builder(VideoPlayerActivity.this)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        })
        .setTitle(R.string.encountered_error_title)
        .setMessage(R.string.encountered_error_message)
        .create();
        dialog.show();
    }

    private void handleVout(Message msg) {
        if (msg.getData().getInt("data") == 0 && !mEndReached) {
            /* Video track lost, open in audio mode */
            Log.i(TAG, "Video track lost, switching to audio");
            mSwitchingView = true;
            finish();
        }
    }
    /**
     * 更改播放界面尺寸
     */
    private void changeSurfaceSize() {
        // get screen size
        int sw = getWindow().getDecorView().getWidth();
        int sh = getWindow().getDecorView().getHeight();
        double dw = sw, dh = sh;

        // getWindow().getDecorView() doesn't always take orientation into account,
        //we have to correct the values(意思为getWindow().getDecorView() 该方法并不是每次都考虑到当前屏幕方向)
        int curOrientation=getResources().getConfiguration().orientation;
        boolean isPortrait = curOrientation == Configuration.ORIENTATION_PORTRAIT;//判断当前是否为竖屏
        //sw>sh 屏幕宽 > 屏幕高 ==横屏值  sw<sh 屏幕宽 < 屏幕高  ==竖屏值
        if (sw > sh && isPortrait || sw < sh && !isPortrait) {
        	//如果当前是竖屏isPortrait=true，但是通过 getWindow().getDecorView()得到的仍然是宽>高(横屏值)
        	//或者当前是横屏isPortrait=false，但是通过getWindow().getDecorView()得到的仍然是宽 <高(竖屏值)
        	//则人为把宽高互换
            dw = sh;
            dh = sw;
        }

        // sanity check
        if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0) {
            Log.e(TAG, "Invalid surface size");
            return;
        }

        // compute the aspect ratio  计算高宽比
        double ar, vw;
        double density = (double)mSarNum / (double)mSarDen;
        if (density == 1.0) {
            /* No indication about the density, assuming 1:1 */
            vw = mVideoVisibleWidth;
            ar = (double)mVideoVisibleWidth / (double)mVideoVisibleHeight;
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
        mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        LayoutParams lp = mSurface.getLayoutParams();
        lp.width  = (int) Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
        lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
        mSurface.setLayoutParams(lp);

        // set frame size (crop if necessary)
        lp = mSurfaceFrame.getLayoutParams();
        lp.width = (int) Math.floor(dw);
        lp.height = (int) Math.floor(dh);
        mSurfaceFrame.setLayoutParams(lp);

        mSurface.invalidate();
    }

    /**
     * show/hide the overlay
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
		 if(lvMenuContent.isShown()){
			 lvMenuContent.setVisibility(View.GONE);
		 }
        if (mIsLocked) {
            // locked, only handle show/hide & ignore all actions
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!mShowing) {
                    showOverlay();
                } else {
                    hideOverlay(true);
                }
            }
            return false;
        }
        
        DisplayMetrics screen = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(screen);

        if (mSurfaceYDisplayRange == 0)
            mSurfaceYDisplayRange = Math.min(screen.widthPixels, screen.heightPixels);

        float y_changed = event.getRawY() - mTouchY;
        float x_changed = event.getRawX() - mTouchX;

        // coef(系数、折算率) is the gradient(梯度)'s move to determine(判定) a neutral（中间�?中�?�?zone
        float coef = Math.abs (y_changed / x_changed);
        float xgesturesize = ((x_changed / screen.xdpi) * 2.54f);

        switch (event.getAction()) {

        case MotionEvent.ACTION_DOWN:
            // Audio
            mTouchY = event.getRawY();
            mVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mTouchAction = TOUCH_NONE;
            // Seek
            mTouchX = event.getRawX();
            break;

        case MotionEvent.ACTION_MOVE:
            // No volume/brightness action if coef < 2
            if (coef > 2) {
                // Volume (Up or Down - Right side)
                if (!mEnableBrightnessGesture || mTouchX > (screen.widthPixels / 2)){
                    doVolumeTouch(y_changed);
                }
                // Brightness (Up or Down - Left side)
                if (mEnableBrightnessGesture && mTouchX < (screen.widthPixels / 2)){
                    doBrightnessTouch(y_changed);
                }
                // Extend the overlay for a little while, so that it doesn't
                // disappear on the user if more adjustment is needed. This
                // is because on devices with soft navigation (e.g. Galaxy
                // Nexus), gestures can't be made without activating the UI.
                if(Util.hasNavBar())
                    showOverlay();
            }
            // Seek (Right or Left move)
//            doSeekTouch(coef, xgesturesize, false);
            break;

        case MotionEvent.ACTION_UP:
            // Audio or Brightness
            if ( mTouchAction == TOUCH_NONE) {
                if (!mShowing) {
                    showOverlay();
                } else {
                    hideOverlay(true);
                }
            }
            // Seek
//            doSeekTouch(coef, xgesturesize, true);
            break;
        }
        return mTouchAction != TOUCH_NONE;
    }
   
    private void doVolumeTouch(float y_changed) {
        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_VOLUME)
            return;
        int delta = -(int) ((y_changed / mSurfaceYDisplayRange) * mAudioMax);
        int vol = (int) Math.min(Math.max(mVol + delta, 0), mAudioMax);
        if (delta != 0) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
            mTouchAction = TOUCH_VOLUME;
            showInfo(getString(R.string.volume) + '\u00A0' + Integer.toString(vol),1000);
        }
    }

    private void initBrightnessTouch() {
        float brightnesstemp = 0.01f;
        // Initialize the layoutParams screen brightness
        try {
            brightnesstemp = android.provider.Settings.System.getInt(getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS) / 255.0f;
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightnesstemp;
        getWindow().setAttributes(lp);
        mIsFirstBrightnessGesture = false;
    }

    private void doBrightnessTouch(float y_changed) {
        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_BRIGHTNESS)
            return;
        if (mIsFirstBrightnessGesture) initBrightnessTouch();
        mTouchAction = TOUCH_BRIGHTNESS;

        // Set delta : 0.07f is arbitrary for now, it possibly will change in the future
        float delta = - y_changed / mSurfaceYDisplayRange * 0.07f;

        // Estimate and adjust Brightness
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness =  Math.min(Math.max(lp.screenBrightness + delta, 0.01f), 1);

        // Set Brightness
        getWindow().setAttributes(lp);
        showInfo(getString(R.string.brightness) + '\u00A0' + Math.round(lp.screenBrightness*15),1000);
    }

   
    private int curSelectMenuItem = -1;
    private int lastSelectedMenuItem = -1;
    /*** pvr 、screenSize 、节目向导、声道、字幕、清晰度 **/
    private int [] menuItemSelectedIndex = new int [6];
    private final OnClickListener forMoreMenuItemOnclickListener =new OnClickListener() {
		@Override
		public void onClick(View v) {
			int viewId = v.getId();
			switch (viewId) {
			case R.id.tvbtn_menu_pvr :// 更多菜单中 PVR
				curSelectMenuItem=0;
				if(lastSelectedMenuItem == curSelectMenuItem){
					if(lvMenuContent.getVisibility() == View.VISIBLE){
						lvMenuContent.setVisibility(View.GONE);
						return;
					}
				}
				break;
			case R.id.tvbtn_menu_screen_size:
				curSelectMenuItem=1;
				if(lastSelectedMenuItem == curSelectMenuItem){
					if(lvMenuContent.getVisibility() == View.VISIBLE){
						lvMenuContent.setVisibility(View.GONE);
						return;
					}
				}
				break;
			case R.id.tvbtn_menu_program_epg:  //节目向导 
				lvMenuContent.setVisibility(View.GONE);
				curSelectMenuItem=2;
				array_menu_item_content.put(2, null);
				 if(isDetailEpgPopShowing){
					 detailsEpgPopuWindow.dismiss();
					 isDetailEpgPopShowing = false;
				 }
				 else{
					 isDetailEpgPopShowing = true;
					detailsEpgPopuWindow.showAtLocation(viewRoot, Gravity.CENTER, 0, 0);
				}
				break;
			case R.id.tvbtn_menu_sound_track: //声道
				curSelectMenuItem=3;
				if(lastSelectedMenuItem == curSelectMenuItem){
					if(lvMenuContent.getVisibility() == View.VISIBLE){
						lvMenuContent.setVisibility(View.GONE);
						return;
					}
				}
				String []  a= {
					"左声道","右声道"	
				};
				array_menu_item_content.put(3,a);
				break;
			case R.id.tvbtn_menu_subtitle: //字幕
				curSelectMenuItem=4;
				if(lastSelectedMenuItem == curSelectMenuItem){
					if(lvMenuContent.getVisibility() == View.VISIBLE){
						lvMenuContent.setVisibility(View.GONE);
						return;
					}
				}
				String subTitles[] ={
						"中文字幕","英文字幕" ,"法文字幕"	
					};
				array_menu_item_content.put(4,subTitles);
				break;
			case R.id.tvbtn_menu_pic_qually:  // 画面质量
				curSelectMenuItem=5;
				if(lastSelectedMenuItem == curSelectMenuItem){
					if(lvMenuContent.getVisibility() == View.VISIBLE){
						lvMenuContent.setVisibility(View.GONE);
						return;
					}
				}
				break;
			}// end switch
	     lastSelectedMenuItem = curSelectMenuItem;
		 updateMenuItemContent(curSelectMenuItem);
		}
	};
	boolean isDetailEpgPopShowing = false;
	/** 更新每个菜单项的内容  @param curClickMenuItem  **/ 
	private void updateMenuItemContent(int curClickMenuItem){
		if(curClickMenuItem == -1 || curClickMenuItem ==2)return;
		String[] targetData = array_menu_item_content.get(curClickMenuItem);
		if(detailsEpgPopuWindow.isShowing()){detailsEpgPopuWindow.dismiss(); isDetailEpgPopShowing = false;}
		if(targetData == null) return;
		lvMenuContent.setVisibility(View.VISIBLE);
		menuContentAdapter.clear();
		menuContentAdapter.addAll(targetData);
		lvMenuContent.setItemChecked(menuItemSelectedIndex[curClickMenuItem], true);
	}
    /**
     *  改变视频尺寸
     */
	private void changeScreenSize(int sizeIndex){
		if(sizeIndex < 0 || sizeIndex > 6) return;
		mCurrentSize = sizeIndex;
        changeSurfaceSize();
       switch (mCurrentSize) {
           case SURFACE_BEST_FIT:
               showInfo(R.string.surface_best_fit, 1000);
               break;
           case SURFACE_FIT_HORIZONTAL:
               showInfo(R.string.surface_fit_horizontal, 1000);
               break;
           case SURFACE_FIT_VERTICAL:
               showInfo(R.string.surface_fit_vertical, 1000);
               break;
           case SURFACE_FILL:
               showInfo(R.string.surface_fill, 1000);
               break;
           case SURFACE_16_9:
               showInfo("16:9", 1000);
               break;
           case SURFACE_4_3:
               showInfo("4:3", 1000);
               break;
           case SURFACE_ORIGINAL:
               showInfo(R.string.surface_original, 1000);
               break;
       }
       showOverlay();
	}

	

    /**
     * attach and disattach surface to the lib
     */
    private final SurfaceHolder.Callback mSurfaceCallback = new Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if(format == PixelFormat.RGBX_8888)
                Log.d(TAG, "Pixel format is RGBX_8888");
            else if(format == PixelFormat.RGB_565)
                Log.d(TAG, "Pixel format is RGB_565");
            else if(format == ImageFormat.YV12)
                Log.d(TAG, "Pixel format is YV12");
            else
                Log.d(TAG, "Pixel format is other/unknown");
            mLibVLC.attachSurface(holder.getSurface(), VideoPlayerActivity.this);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mLibVLC.detachSurface();
        }
    };

    /**
     * show overlay the the default timeout
     */
    private void showOverlay() {
        showOverlay(OVERLAY_TIMEOUT);
    }

    /**
     * 显示播放控制等视图?     * show overlay
     */
    private void showOverlay(int dismissTime) {
//        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        if (! mShowing) {
            mShowing = true;
            if (!mIsLocked) {//如果没有锁住
                mOverlayHeader.setVisibility(View.VISIBLE);
                mOverlayOption.setVisibility(View.VISIBLE);
                mMenu.setVisibility(View.VISIBLE);
                mSysTime.setText(DateFormat.getTimeFormat(this).format(new Date(System.currentTimeMillis())));
                btnShowProgrameList.setVisibility(View.VISIBLE);
//                dimStatusBar(false);
            }
            else { //锁定后显示
            	 mOverlayOption.setVisibility(View.VISIBLE);
            	 mOverlayHeader.setVisibility(View.INVISIBLE);
            	 mMenu.setVisibility(View.INVISIBLE);
            	 mLock.setVisibility(View.VISIBLE);
            	 btnShowProgrameList.setVisibility(View.INVISIBLE);
            }
             
        }
        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (dismissTime != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, dismissTime);
        }
    }


    /**
     * hider overlay
     */
    private void hideOverlay(boolean fromUser) {
        if (mShowing) {
            mHandler.removeMessages(SHOW_PROGRESS);
            Log.i(TAG, "remove View!");
            if (!fromUser && ! mIsLocked) { //如果不是用户强制隐藏或锁屏,则渐渐消隐
                mOverlayHeader.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                mOverlayOption.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
//                mOverlayHeader.setVisibility(View.INVISIBLE);
//            	mOverlayOption.setVisibility(View.INVISIBLE);
            }
//            else {
            	mOverlayHeader.setVisibility(View.INVISIBLE);
            	mOverlayOption.setVisibility(View.INVISIBLE);
            	
//            }
            btnShowProgrameList.setVisibility(View.INVISIBLE);
            mShowing = false;
//            dimStatusBar(true);
        }
    }
   

    private void setESTracks() {
        if (mLastAudioTrack >= 0) {
            mLibVLC.setAudioTrack(mLastAudioTrack);
            mLastAudioTrack = -1;
        }
        if (mLastSpuTrack >= -1) {
            mLibVLC.setSpuTrack(mLastSpuTrack);
            mLastSpuTrack = -2;
        }
    }

    private void setESTrackLists(boolean force) {
        if(mAudioTracksList == null || force) {
        	int audioTracksCount = mLibVLC.getAudioTracksCount();
        	  mAudioTracksList = mLibVLC.getAudioTrackDescription();
        	  Log.e("info","  声道 数： "+audioTracksCount );
            if (audioTracksCount > 2) {
                mAudioTracksList = mLibVLC.getAudioTrackDescription();
//                mAudioTrack.setOnClickListener(mAudioTrackListener);
//                mAudioTrack.setVisibility(View.VISIBLE);
              
            }
            else {
//                mAudioTrack.setVisibility(View.GONE);
//                mAudioTrack.setOnClickListener(null);
            }
        }
        if (mSubtitleTracksList == null || force) {
            if (mLibVLC.getSpuTracksCount() > 0) {
                mSubtitleTracksList = mLibVLC.getSpuTrackDescription();
//                mSubtitle.setOnClickListener(mSubtitlesListener);
//                mSubtitle.setVisibility(View.VISIBLE);
            }
            else {
//                mSubtitle.setVisibility(View.GONE);
//                mSubtitle.setOnClickListener(null);
            }
        }
    }
   
    /**
     *
     */
    private void play() {
        mLibVLC.play();
        mSurface.setKeepScreenOn(true);
    }

    /**
     *
     */
    private void pause() {
        mLibVLC.pause();
        mSurface.setKeepScreenOn(false);
    }
 
    /**
     * 获取屏幕角度
     * @return
     */
    @SuppressWarnings("deprecation")
    private int getScreenRotation(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO /* Android 2.2 has getRotation */) {
            try {
                Method m = display.getClass().getDeclaredMethod("getRotation");
                return (Integer) m.invoke(display);
            } catch (Exception e) {
                return Surface.ROTATION_0;
            }
        } else {
            return display.getOrientation();
        }
    }
    /**
     * 获取当前屏幕方向
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private int getScreenOrientation(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int rot = getScreenRotation();
        /*
         * Since getRotation() returns the screen's "natural" orientation,
         * which is not guaranteed to be SCREEN_ORIENTATION_PORTRAIT,
         * we have to invert the SCREEN_ORIENTATION value if it is "naturally"
         * landscape.
         */
        @SuppressWarnings("deprecation")
        boolean defaultWide = display.getWidth() > display.getHeight();
        if(rot == Surface.ROTATION_90 || rot == Surface.ROTATION_270)
            defaultWide = !defaultWide;
        if(defaultWide) {
            switch (rot) {
            case Surface.ROTATION_0:
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            case Surface.ROTATION_90:
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            case Surface.ROTATION_180:
                // SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
                // Level 9+
                return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            case Surface.ROTATION_270:
                // SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
                // Level 9+
                return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            default:
                return 0;
            }
        } else {
            switch (rot) {
            case Surface.ROTATION_0:
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            case Surface.ROTATION_90:
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            case Surface.ROTATION_180:
                // SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
                // Level 9+
                return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            case Surface.ROTATION_270:
                // SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
                // Level 9+
                return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            default:
                return 0;
            }
        }
    }
    @Override
	public void handleMsg(Message msg) {
		int what = msg.what;
		Log.w("info", "VideoPlayerActivity----handleMsg()-> " + what);
		switch (what) {
		case Constant.CONNECT_BREAK:
			// 此时不影响播放，让程序自动去重连三次
			connectUtil.autoToConnectServer();
			break;
		case Constant.CONNECT_REALY_DISCONNECT:
			// 自动重连三次后，仍然不能连接，则提示用户
			//????
//			net_disconnect_hint(true);
			break;
		case Constant.SERVER_RESPONSE_LOAD:
			byte loginState = (byte) msg.arg1;
			if (loginState == 1) {// BOX 端下线了，仍然能播放
			// tcp_play_Loading();
				System.err.println("BOX was  off-line...");
			}
			break;
//		case Constant.PLAY_COMMAND:// 服务器响应播放信息
//			// 0表示无法播放(可能没有AV数据)；1表示可播放player_surface_frame
//			byte canPLay = (byte) msg.arg1;
//			if (canPLay == 0) {
//				ToastUtil.showToast(R.string.no_av_to_play, Gravity.TOP);
//				onResume();
//			} else {
//				ToastUtil.showToast(R.string.can_to_playAndgeturi, Gravity.TOP);
//				if (connectUtil != null) {
//					connectUtil.getPlayUrl();
//				}
//			}
//			break;
//		case Constant.GETURL_COMMAND:
//			byte state = (byte) msg.arg1; // 0表示未准备好，1表示已准备好，2表示发生错误
//			switch (state) {
//			case 0:
//				ToastUtil.showToast(R.string.server_not_prepare, Gravity.TOP);
//				break;
//			case 1:
//				ToastUtil.showToast(R.string.server_has_prepared, Gravity.TOP);
//				Bundle urlInfo = msg.getData();
//				String url = urlInfo.getString(Constant.URL_KEY);
//				if (url == null) {
//					break;
//				}
//				break;
//			case 2:
//				ToastUtil.showToast(R.string.geturi_error, Gravity.TOP);
//				break;
//			}
//			onResume();
//			break;

//		case Constant.BOX_RESPONSE_CUR_AV_CHANNEL:// 当前AV线路
//			 
//			break;
//		case Constant.BOX_RESPONSE_IR_CONTROL:// 84 BOX端响应切台或者是红外转发
//			int ir_transform_state = msg.arg1;
//			switch (ir_transform_state) {
//			case 0:// 未学习遥控
//				ToastUtil.showToast(R.string.box_not_study_ir, Gravity.TOP);
//				break;
//			case 1:// 按键序号超出所学范围
//				break;
//			case 2:// 红外转发失败
//				break;
//			case 3:// 红外转发成功
//				break;
//			}
//			break;
		case Constant.BOX_RESPONSE_CUR_PVR_STATE:// 81 Box端响应当前PVR状态
			int pvr_state = msg.arg1;
			switch (pvr_state) {
			case 0:// BOX 端未插入外部存储设备不能录制
				isInPvr = false;
				if (sendPvrCmdInResume) {
					sendPvrCmdInResume = false;
					break;
				}
				ToastUtil.showToast(R.string.box_no_extranal_save_device, Gravity.TOP);
				break;
			case 1:// 空闲
				isInPvr = false;
				break;
			case 2:// 正在录制
				isInPvr = true;
				break;
			case 3:// BOX 不允许
				isInPvr = false;
				if (sendPvrCmdInResume) {
					sendPvrCmdInResume = false;
					break;
				}
				ToastUtil.showToast(R.string.box_forbit_pvr, Gravity.TOP);
				break;
			case 4:// 录制失败
				isInPvr = false;
				break;
			case 5:// 存储设备已满
				isInPvr = false;
				break;
			}// end switch
//			if (isInPvr) {
//				ir_pvr.setText("Stop");
//			} else {
//				ir_pvr.setText(R.string.pvr);
//			}
			break;
		case Constant.UDP_PLAY_MSG:// udp 底层发送数据过来
			Bundle bd = msg.getData();
			int eventType = bd.getInt(Constant.UDPCALLBACK_EVENT_TYPE);
			String eventContent = bd
					.getString(Constant.UDPCALLBACK_EVENT_MESSAGE);
			switch (eventType) {
			case 11:
				url = eventContent;
//				int curPlayIndex = netImp.getCurPlayingProgramId();
//				
//				mTitle.setText(curProgramList.get(curPlayIndex).getName());
//				loadingLayout.setVisibility(View.GONE);
//				functionsLayout.setVisibility(View.VISIBLE);
//				horseLoading.stop();
//				Log.e("info"," UDP 可以播放了》。。。。。。。。。。");
				viewLayout(true);
				onResume();
				break;
			case 12:// 停止（UDP,超时)
//				mLibVLC.stop();
//				finish();// 关闭播放界面
				break;
			case 13:  // UDP不通 走TCP 播放失败
				viewLayout(false);
				ToastUtil.showToast(R.string.play_fail, Gravity.BOTTOM);
				break;
			}
			break;
//		case Constant.TOAST_CAN_SHOW_AGAIN_MSG:
//			break;
//		case Constant.IR_CAN_CLICK_AGAIN_MSG:
//			break;
//		case Constant.REQUEST_TIME_OUT:// 请求超时
//			ToastUtil.showToast(R.string.request_timeup, Gravity.TOP);
//			onResume();
//			break;
		}// end switch
	}
    /**
     * 更多 菜单布局
     */
    PopupWindow moreMenuPopupWindow ;
    int moreMenuPopWinHeight=135;
    /**
	 * init More Menu layout
	 * @return
	 */
 	private PopupWindow createMoreMenuPopuWindow() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		PopupWindow pop = null;
	    View moreMenuLayou = (LinearLayout) layoutInflater.inflate(R.layout.more_menu_layout, null);
//	    moreMenuLayou.setFocusable(true);
//	    moreMenuLayou.setFocusableInTouchMode(true);
	    initMoreMenuViews(moreMenuLayou);                               //LayoutParams.WRAP_CONTENT
	    pop = new PopupWindow(moreMenuLayou, LayoutParams.MATCH_PARENT, moreMenuPopWinHeight);
//		pop.setFocusable(true);
		// 要在代码里设置背景，即使在布局文件里设置了背景也不行，不然，TouchInterceptor无法触发,
//		pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.more_menu_bg));
//		pop.setTouchable(true);
		pop.setAnimationStyle(R.style.more_menu_pop_animstyle);
//		pop.setOutsideTouchable(true);
		moreMenuLayou.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					moreMenuPopupWindow.dismiss();
					return true;
				}
				return false;
			}
		});
		return pop;
	} 
 	private TextView more_menu_pvr;
 	private TextView more_menu_screen_size;
 	private TextView more_menu_program_epg;
 	private TextView more_menu_sound_track;
 	private TextView more_menu_subtitle;
 	private TextView more_menu_pic_quallity;
 	private void initMoreMenuViews(View parentView){
 		more_menu_pvr = (TextView) parentView.findViewById(R.id.tvbtn_menu_pvr);
 		more_menu_screen_size = (TextView) parentView.findViewById(R.id.tvbtn_menu_screen_size);
 		more_menu_program_epg = (TextView) parentView.findViewById(R.id.tvbtn_menu_program_epg);
 		more_menu_sound_track = (TextView) parentView.findViewById(R.id.tvbtn_menu_sound_track);
 		more_menu_subtitle = (TextView) parentView.findViewById(R.id.tvbtn_menu_subtitle);
 		more_menu_pic_quallity = (TextView) parentView.findViewById(R.id.tvbtn_menu_pic_qually);
 		
 		// set onclick listeners
 		more_menu_pvr.setOnClickListener(forMoreMenuItemOnclickListener);
 		more_menu_screen_size.setOnClickListener(forMoreMenuItemOnclickListener);
 		more_menu_program_epg.setOnClickListener(forMoreMenuItemOnclickListener);
 		more_menu_sound_track.setOnClickListener(forMoreMenuItemOnclickListener);
 		more_menu_subtitle.setOnClickListener(forMoreMenuItemOnclickListener);
 		more_menu_pic_quallity.setOnClickListener(forMoreMenuItemOnclickListener);
 	}
 	/**
 	 * 节目列表布局
 	 */
 	ProgramPopuWindow programListWindow;
 	ProgramAdapter programAdapter ;
 	int  curTypeIndex = 0;
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (programListWindow.getMfindProgramEdit().isShown()) {
				String name = s.toString().toUpperCase();
				findTV(name);
			}
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
		}
	};
	//生成搜索节目列表
	private void findTV(String name){
		mFindPrograms.clear();
		for(int i = 0; i < curProgramList.size(); i ++){
			if(curProgramList.get(i).getName().toUpperCase().contains(name)){
				mFindPrograms.add(curProgramList.get(i));
			}
		}
		programAdapter.updateListData(mFindPrograms);
//		mProgramListView.setSelectionFromTop(getCurrentIndex(mFindPrograms), 10);
		mIsFind = true;
	}
 	final OnClickListener clickListenerForProgramWindow =new OnClickListener() {
		@Override
		public void onClick(View v) {
			int viewId = v.getId();
			switch (viewId) {
			case R.id.btn_left: //左
				if(curTypeIndex == 0){
					curTypeIndex = 2;
				}else{
					curTypeIndex --;
				}
				changeListType(curTypeIndex, mIsAll );
				break;
			case R.id.btn_right: //右
				if(curTypeIndex == 2){
					curTypeIndex = 0;
				}else{
					curTypeIndex ++;
				}
				changeListType(curTypeIndex, mIsAll);
				break;
			case R.id.btn_all:
				if (!mIsAll || programListWindow.getMfindProgramEdit().isShown()) {
					mIsAll = true;
					changeListType(curTypeIndex, mIsAll);
				}
				break;
			case R.id.btn_curtype_for_free: //当前节目下的免费节目
				if (mIsAll || programListWindow.getMfindProgramEdit().isShown()) {
					mIsAll = false;
					changeListType(curTypeIndex, mIsAll);
				}
				break;
			case R.id.btn_search:
				programListWindow.prepareToFind();
				break;
			case R.id.btn_clear: //清除输入的搜索字符
				programListWindow.getMfindProgramEdit().setText("");
				break;
			default:
				break;
			}
		}
	};
	private boolean mIsFind;
	private boolean mIsAll = true;
	private void changeListType(int type, boolean isAll) {
		Log.i("info"," changeListType = "+type +"　isAll = "+isAll);
		curProgramList = totalPrograms.get(type);
		if (!isAll) {
			curProgramList = getIsnotScablingList(curProgramList);
		}
		programListWindow.changeListType(type, isAll);
		updateListView();
		mIsFind= false;
	}
	private void updateListView(){
		// 放在PorgramPopWindow 里
		int curPlayId = netImp.getCurPlayingProgramId();
		programAdapter.setCurrentPlayingProgram(curPlayId);
		//更新adapter
		programAdapter.updateListData(curProgramList);
		programListWindow.setCurPlayingItemInList(curPlayId);
	}
	/** 获取非加扰 节目列表 **/
  private ArrayList<Program> getIsnotScablingList(ArrayList<Program> curProgramList){
	  ArrayList<Program> scablingList = new ArrayList<Program>();
	  for (Program isScablingProgram: curProgramList){
		  if(!isScablingProgram .isScrambler()){
			  scablingList.add(isScablingProgram);
		  }
	  }
	  return scablingList;
  }
	 
	private AnimationListener animationListener =new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			if(mMenu != null) mMenu.setEnabled(false);
		}
		@Override
		public void onAnimationRepeat(Animation animation) {
		}
		@Override
		public void onAnimationEnd(Animation animation) {
			RelativeLayout.LayoutParams forMenu =new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			forMenu.addRule(RelativeLayout.CENTER_HORIZONTAL);
			forMenu.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			mMenu.clearAnimation();
			if(animation == menuAnimUp){
				forMenu.bottomMargin = 130;
				mMenu.setBackgroundResource(R.drawable.more_menu_show_down);
			}else{
				mMenu.setBackgroundResource(R.drawable.more_menu_show_up);
			}
			mMenu.setLayoutParams(forMenu);
			mMenu.setEnabled(true);
		}
	};
	
	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.ivbtn_lock_screen: //锁屏
			   if (mIsLocked) {
	                mIsLocked = false;
	                unlockScreen();
	            } else {
	                mIsLocked = true;
	                lockScreen();
	            }
			break;
		case R.id.ivbtn_more_menu: //更多菜单
			if(moreMenuPopupWindow.isShowing()){
				v.startAnimation(menuAnimDown);
				moreMenuPopupWindow.dismiss();
				lvMenuContent.setVisibility(View.GONE);
			}else{ //向上
//				mMenu.setBackgroundResource(R.drawable.more_menu_show_up);
				v.startAnimation(menuAnimUp);
				simpleEpgPopuWindow.showAtLocationAndAutoDismiss(viewRoot, Gravity.TOP, 0, 0);
				moreMenuPopupWindow.showAtLocation(viewRoot, Gravity.BOTTOM, 0, 0);
			}
			 
			break;
		case R.id.btn_list:
			 v.setVisibility(View.INVISIBLE);
			 boolean isProgramListShowing = programListWindow.isShowing();
		        if( !isProgramListShowing ){
		        	programListWindow.showAtLocation(viewRoot, Gravity.LEFT, 0, 0);
		        }else {
		        	programListWindow.dismiss();
		        }
			break;
		case R.id.comm_dialog_tvbtn_sure:
			if(netImp == null) return;
			EditText edtView = commonEditDialog.getTheEditTextView();
			String programPw = edtView.getText().toString(); 
			if(programPw == null || programPw.length() == 0){
				ToastUtil.showToast(R.string.the_lock_password_can_not_null, Gravity.BOTTOM);
				break;
			}
		ArrayList<Program> allPrograms=	netImp.getProgramData().get(Constant.PROGRAM_ALL);
			Program curProgram = allPrograms.get(curSelectedProgram);
			int type = curProgram.getType();
			type = 1-type;
			int programId = curProgram.getId();
			netImp.programPlay(programId, type, programPw);
			loadingLayout();
			getUrlCount = 0;
			commonEditDialog.dismiss();
			if( programListWindow.isShowing()){
				programListWindow.dismiss();
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int listViewId = parent.getId();
		switch (listViewId) {
		case R.id.lv_menu_item_content:
			menuItemSelectedIndex [curSelectMenuItem] = position;
			switch (curSelectMenuItem) {
			case 0: // PVR中
				Log.w("info"," PVR  position = "+position	);
				break;
			case 1: //尺寸
				Log.w("info"," 尺寸  position = "+position	);
				changeScreenSize(position);
				break;
			case 2: //节目向导
				Log.w("info"," 节目向导  position = "+position	);
				break;
			case 3:  // 声道
				Log.w("info"," 声道  position = "+position	);
				break;
			case 4: //字幕
				Log.w("info"," 字幕  position = "+position	);
				break;
			case 5: // 清晰度
				Log.w("info"," 清晰度  position = "+position	);
				break;
			}
		 
			break;
		case R.id.list_programlist:
		//到时要分远程与局域网的播放方式
				Program program;
				if (mIsFind){
					program = mFindPrograms.get(position);
				} else {
					program = curProgramList.get(position);
				}
				if (program.getType() != ProgramRunnable.TYPE_NOTE_FREE 
					 &&  program.getType() != ProgramRunnable.TYPE_NOTE_SCRAMBLER){
					int type;
					if ( program.getType() == ProgramRunnable.TYPE_TV) {
						type = TYPE_SHOW;
					} else {
						type = TYPE_NO_SHOW;
					}
					int 	mPreparePlayID = program.getId();
					int  lastPlayingProgramId = netImp .getCurPlayingProgramId();
					if(lastPlayingProgramId == mPreparePlayID){
						break;
					}
					curSelectedProgram = position;
					if (program.isLock()) { //如果锁定了
						commonEditDialog.show(CommonEditDialog.DIALOG_TYPE_ENTER_PARENT_UNLOCK);
					} 
					else 
					{ //发送播放命令
						netImp.programPlay(mPreparePlayID, type, "");
						getUrlCount = 0;
						netImp.startGetShortEpgTask();
						loadingLayout();
					}
				}
				break;
		}
		
	}
	private int curSelectedProgram = 0;
	 private void loadingLayout(){
		 //其他布局考虑要不要清除
		 if(lvMenuContent !=null && lvMenuContent.isShown()){
			 lvMenuContent.setVisibility(View.GONE);
		 }
		 if(moreMenuPopupWindow !=null && moreMenuPopupWindow.isShowing()){
			 moreMenuPopupWindow.dismiss();
		 }
		 if(programListWindow !=null && programListWindow.isShowing()){
			 programListWindow.dismiss();
		 }
		 
		 if(mMenu !=null){
			 mMenu.startAnimation(menuAnimDown);
		 }
		 if(mLibVLC !=null && isPlaying){
			 mLibVLC.stop();
			 isPlaying =false;
		 }
	    	mSurfaceFrame.setVisibility(View.INVISIBLE);
	    	functionsLayout.setVisibility(View.INVISIBLE);
	    	loadingLayout .setVisibility(View.VISIBLE);
	    	horseLoading.start();  
	    }
	 private void viewLayout(boolean prepareToPlay){
		 	mSurfaceFrame.setVisibility(View.VISIBLE);
	    	functionsLayout.setVisibility(View.VISIBLE);
	    	loadingLayout .setVisibility(View.INVISIBLE);
	    	horseLoading.stop();  
	    	if(mShortEPG !=null && prepareToPlay){
				simpleEpgPopuWindow.setCurProgramShortEpg(mShortEPG);
				simpleEpgPopuWindow.showAtLocationAndAutoDismiss(viewRoot, Gravity.TOP, 0, 0);
			}
	 }
	public static final int TYPE_SHOW = 1;
	public static final int TYPE_NO_SHOW = 0;
 
	@Override
	public void handleLocalMsg(Message msg) {
		switch (msg.what) {
		case ProgramRunnable.PROGRAM_REFRESH: //节目有更新
			Log.e("info"," 节目有更新..."	);
			if (mLibVLC.isPlaying()) {
//				mLibVLC.stop();
			}
			HashMap<String, ArrayList<Program>> updatedData = netImp.getProgramData();
			switch (curTypeIndex) {
			case ProgramRunnable.TYPE_TV:
				curProgramList = updatedData.get(Constant.PROGRAM_TV_KEY);
				break;
			case ProgramRunnable.TYPE_RADIO:
				curProgramList = updatedData.get(Constant.PROGRAM_RADIO_KEY);
				break;
			case ProgramRunnable.TYPE_FAV:
				curProgramList = updatedData.get(Constant.PROGRAM_FAVORATE_KEY);
				break;
			}
			programAdapter.updateListData(curProgramList);
			mIsFind = false;
			break;
		case ProgramRunnable.CURRENT_PROGRAM:    // 当前播放节目的ID
			int	mCurrentProgramID = msg.arg1;
			programAdapter.setCurrentPlayingProgram(mCurrentProgramID);
//			mFindProgramEdit.setText("");
			mIsFind = false;
//			mProgramListView.requestFocusFromTouch();
//			mProgramListView.setSelectionFromTop(getCurrentIndex(mProgramList), 10);
//			mShortEPGGetter.start();
			break;
		case ProgramRunnable.CURRENT_EPG: // 简单EPG
			mShortEPG = (ShortEPG) msg.obj;
//			Log.e("info",TAG+" 获取到了简单EPG 并停止获取简单EPG的任务 ");
			if (mShortEPG == null || mShortEPG.isEmpty()) {
				netImp.releaseGetShortEpgTask(); 
			} 
//			programListWindow.getMfindProgramEdit().setText("");
			mIsFind = false;
			break;
		case ProgramRunnable.INDEX_AUDIO_TRACK: 
			Log.e("info","   当前音频 声道 :" +msg.arg1 );
 
			break;
		case ProgramRunnable.MSG_PLAY_PROGRAM_RESULT: //ProgramRunnable 发来的消息
			switch (msg.arg1) {
			case ProgramRunnable.PLAY_RESULT_TV_RECORD: 
				Log.e("info",TAG+"---->  可以播放,开始获取URL ");
				// 可以播放了
				netImp.getURL();
				netImp.getEPGTime();
				netImp.getMoreEPG(0);
				 
				break;
			case ProgramRunnable.PLAY_RESULT_PASSWRONG: //输入的解密的密码错误
				commonEditDialog.show(CommonEditDialog.DIALOG_TYPE_PARENT_UNLOCK_PW_ERROR);			
 
				break;
			case ProgramRunnable.PLAY_RESULT_PLAY_FALSE: //播放失败
				
				break;
			}
			break;
		case ProgramRunnable.MSG_GET_URL:      // 响应 获取URL 信息
			switch (msg.arg1) {
			case ProgramRunnable.FLAG_NO_PREPARE: //未准备好
				mHandler.postDelayed(mGetURLRunnable, 1000);
				Log.i("info",TAG+" --> url 未准备好...");
				if(getUrlCount == 20){
					mHandler.removeCallbacks(mGetURLRunnable);
					viewLayout(false);
					ToastUtil.showToast(R.string.play_fail, Gravity.BOTTOM);
					break;
				}
				getUrlCount ++;
				break;
			case ProgramRunnable.FLAG_ERROR:   // 获取URL 错误
//				mNoticeText.setVisibility(View.VISIBLE);
				Log.e("info",TAG+"--> url 错误...");
				break;
			case ProgramRunnable.FLAG_PREPARE:  //获取URL地址成功，开始播放
				programListWindow.dismiss();
				mHandler.removeCallbacks(mGetURLRunnable);
				String obtainUrl = (String) msg.obj;
				url = obtainUrl;
				Log.e("info",TAG+" dvb  url  =  "+url);
//				int curPlayIndex = netImp.getCurPlayingProgramId();
//				if(curPlayIndex < curProgramList.size())
//					mTitle.setText(curProgramList.get(curPlayIndex).getName());
				 
				mTitle.setText(curProgramList.get(curSelectedProgram).getName());
				programAdapter.setCurrentPlayingProgram(curSelectedProgram);
				getUrlCount = 0;
				if(netImp !=null){
					netImp.getCurProgramAudioTrack();
				}
				onResume();
				break;

			default:
				break;
			}
			break;
		case ProgramRunnable.MSG_STOP_PLAY: // 服务端响应停止了播放
			if(mLibVLC != null){
				mLibVLC.stop();
			}
			ToastUtil.showToast(R.string.stop_play, Gravity.BOTTOM);
			break;
		case ProgramRunnable.MSG_GET_EPG: 	// 获取到了详细EPG
			detailsEpgPopuWindow.setEPGtimeCount(0);
			detailsEpgPopuWindow.updateEpgList(netImp.getDetailEpgs());
			
			break;
		case ProgramRunnable.MSG_GET_EPG_NULL:  // 获取详细EPG 信息失败
			Log.i(TAG, " get epg is null.........");
			detailsEpgPopuWindow.setEPGtimeCount(30);
			detailsEpgPopuWindow.updateEpgList(null);
			break;
		case ProgramRunnable.MSG_GET_EPGTIME:
			Log.i(TAG, " get epg time........");
			String epgTime = (String) msg.obj;
			detailsEpgPopuWindow.refreshEPGTime(epgTime);
			break;
		case ProgramRunnable.MSG_EDIT_Result: //编辑节目
			int result = msg.arg1;
			if(result ==1){
				//编辑成功
				Log.w("info"," edit suc。。。");
				Program operatedProgram = netImp.getProgramData().get(Constant.PROGRAM_ALL).get(longPressedProgramId);
				
				switch (longPressedOption) {
				case SET_FAV:
					operatedProgram.setIsFavor(!operatedProgram.isFavor());
					break;
				case SET_LOCK:
					operatedProgram.setIsLock(!operatedProgram.isLock());
					break;
				case SET_SKIP:
					operatedProgram.setIsSkip(!operatedProgram.isSkip());
					break; 
				}
				 
				programAdapter.notifyDataSetChanged(); 
			}else{
				//编辑失败
				Log.w("info"," edit fail。。。");
			}
			break;
		} // end outside switch
		
	}
	/** 获取播放地址任务 **/
	private Runnable mGetURLRunnable = new Runnable() {
		@Override
		public void run() {
			netImp.getURL();
		}
	};
	
	int getUrlCount =0;
	int longPressedOption = 0; 
	int longPressedProgramId = 0;
	static	final byte SET_FAV = 0;
	static final byte SET_LOCK = 1;
	static final byte SET_SKIP = 2;
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		longPressedProgramId = (int) id;
		showDialog(1);
		return false;
	}
	
// ******************************** end --save no use functions ***********************************	
// ******************************** end --save no use functions ***********************************	
	 /* private boolean canShowProgress() {
	        return !mDragging && mShowing && mLibVLC.isPlaying();
	    }
	  *//**
	     * handle changes of the seekbar (slicer)
	     *//*
	    private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {

	        @Override
	        public void onStartTrackingTouch(SeekBar seekBar) {
	            mDragging = true;
	            showOverlay(OVERLAY_INFINITE);
	        }

	        @Override
	        public void onStopTrackingTouch(SeekBar seekBar) {
	            mDragging = false;
	            showOverlay();
	            hideInfo();
	        }

	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	            if (fromUser && mCanSeek) {
	                mLibVLC.setTime(progress);
	                setOverlayProgress();
//	                mTime.setText(Util.millisToString(progress));
	                showInfo(Util.millisToString(progress));
	            }

	        }
	    };

	    *//**
	    *
	    *//*
	    private final OnClickListener mAudioTrackListener = new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            final String[] arrList = new String[mAudioTracksList.size()];
	            int i = 0;
	            int listPosition = 0;
	            for(Map.Entry<Integer,String> entry : mAudioTracksList.entrySet()) {
	                arrList[i] = entry.getValue();
	                // map the track position to the list position
	                if(entry.getKey() == mLibVLC.getAudioTrack())
	                    listPosition = i;
	                i++;
	            }
	            AlertDialog dialog = new AlertDialog.Builder(VideoPlayerActivity.this)
	            .setTitle(R.string.track_audio)
	            .setSingleChoiceItems(arrList, listPosition, new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int listPosition) {
	                    int trackID = -1;
	                    // Reverse map search...
	                    for(Map.Entry<Integer, String> entry : mAudioTracksList.entrySet()) {
	                        if(arrList[listPosition].equals(entry.getValue())) {
	                            trackID = entry.getKey();
	                            break;
	                        }
	                    }
	                    if(trackID < 0) return;

//	                    MediaDatabase.getInstance(VideoPlayerActivity.this).updateMedia(
//	                            mLocation,
//	                            MediaDatabase.mediaColumn.MEDIA_AUDIOTRACK,
//	                            trackID);
	                    mLibVLC.setAudioTrack(trackID);
	                    dialog.dismiss();
	                }
	            })
	            .create();
	            dialog.setCanceledOnTouchOutside(true);
	            dialog.setOwnerActivity(VideoPlayerActivity.this);
	            dialog.show();
	        }
	    };

	    *//**
	    *
	    *//*
	    private final OnClickListener mSubtitlesListener = new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            final String[] arrList = new String[mSubtitleTracksList.size()];
	            int i = 0;
	            int listPosition = 0;
	            for(Map.Entry<Integer,String> entry : mSubtitleTracksList.entrySet()) {
	                arrList[i] = entry.getValue();
	                // map the track position to the list position
	                if(entry.getKey() == mLibVLC.getSpuTrack())
	                    listPosition = i;
	                i++;
	            }

	            AlertDialog dialog = new AlertDialog.Builder(VideoPlayerActivity.this)
	            .setTitle(R.string.track_text)
	            .setSingleChoiceItems(arrList, listPosition, new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int listPosition) {
	                    int trackID = -2;
	                    // Reverse map search...
	                    for(Map.Entry<Integer, String> entry : mSubtitleTracksList.entrySet()) {
	                        if(arrList[listPosition].equals(entry.getValue())) {
	                            trackID = entry.getKey();
	                            break;
	                        }
	                    }
	                    if(trackID < -1) return;

//	                    MediaDatabase.getInstance(VideoPlayerActivity.this).updateMedia(
//	                            mLocation,
//	                            MediaDatabase.mediaColumn.MEDIA_SPUTRACK,
//	                            trackID);
	                    mLibVLC.setSpuTrack(trackID);
	                    dialog.dismiss();
	                }
	            })
	            .create();
	            dialog.setCanceledOnTouchOutside(true);
	            dialog.setOwnerActivity(VideoPlayerActivity.this);
	            dialog.show();
	        }
	    };

	    *//**
	    *
	    *//*
	    private final OnClickListener mPlayPauseListener = new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            if (mLibVLC.isPlaying())
	                pause();
	            else
	                play();
	            showOverlay();
	        }
	    };

	    *//**
	    *
	    *//*
	    private final OnClickListener mBackwardListener = new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            seek(-10000);
	        }
	    };

	    *//**
	    *
	    *//*
	    private final OnClickListener mForwardListener = new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            seek(10000);
	        }
	    };
	    private final OnClickListener mRemainingTimeListener = new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            mDisplayRemainingTime = !mDisplayRemainingTime;
	            showOverlay();
	        }
	    };*/
	 /**
     * 处理快进
     * @param coef
     * @param gesturesize
     * @param seek
     */
  /* private void doSeekTouch(float coef, float gesturesize, boolean seek) {
        // No seek action if coef > 0.5 and gesturesize < 1cm
        if (coef > 0.5 || Math.abs(gesturesize) < 1 || !mCanSeek)
            return;

        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_SEEK)
            return;
        mTouchAction = TOUCH_SEEK;

        // Always show seekbar when searching
        if (!mShowing) showOverlay();

        long length = mLibVLC.getLength();
        long time = mLibVLC.getTime();

        // Size of the jump, 10 minutes max (600000), with a bi-cubic progression, for a 8cm gesture
        int jump = (int) (Math.signum(gesturesize) * ((600000 * Math.pow((gesturesize / 8), 4)) + 3000));

        // Adjust the jump
        if ((jump > 0) && ((time + jump) > length))
            jump = (int) (length - time);
        if ((jump < 0) && ((time + jump) < 0))
            jump = (int) -time;

        //Jump !
        if (seek && length > 0)
            mLibVLC.setTime(time + jump);

        if (length > 0)
            //Show the jump's size
            showInfo(String.format("%s%s (%s)",
                    jump >= 0 ? "+" : "",
                    Util.millisToString(jump),
                    Util.millisToString(time + jump)), 1000);
//        else
//            showInfo(R.string.unseekable_stream, 1000);
    }
    *//**
     * Show text in the info view
     * @param text
     *//*
    private void showInfo(String text) {
    	mInfo.setVisibility(View.VISIBLE);
    	mInfo.setText(text);
    	mHandler.removeMessages(FADE_OUT_INFO);
    }
    *//**
     * hide the info view
     *//*
    private void hideInfo() {
        hideInfo(0);
    }


    public void seek(int delta) {
        // unseekable stream
        if(mLibVLC.getLength() <= 0 || !mCanSeek) return;

        long position = mLibVLC.getTime() + delta;
        if (position < 0) position = 0;
        mLibVLC.setTime(position);
        showOverlay();
    }
    
    *//**
     * Dim the status bar and/or navigation icons when needed on Android 3.x.
     * Hide it on Android 4.0 and later
     *//*
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    private void dimStatusBar(boolean dim) {
//        if (!Util.isHoneycombOrLater() || !Util.hasNavBar())
//            return;
//        int layout = 0;
//        if (!Util.hasCombBar() && Util.isJellyBeanOrLater())
//            layout = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//        mSurface.setSystemUiVisibility(
//                (dim ? (Util.hasCombBar()
//                        ? View.SYSTEM_UI_FLAG_LOW_PROFILE
//                        : View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
//                    : View.SYSTEM_UI_FLAG_VISIBLE) | layout);
//    }

//    private void updateOverlayPausePlay() {
//        if (mLibVLC == null) {
//            return;
//        }
//
//        mPlayPause.setBackgroundResource(mLibVLC.isPlaying()
//                ? R.drawable.pause_circle : R.drawable.play_circle);
//    }

    *//**
     * update the overlay 获取进度
     *//*
    private int setOverlayProgress() {
        if (mLibVLC == null) {
            return 0;
        }
        int time = (int) mLibVLC.getTime();
        int length = (int) mLibVLC.getLength();
//        if (length == 0) {
//            Media media = MediaDatabase.getInstance(this).getMedia(mLocation);
//            if (media != null)
//                length = (int) media.getLength();
//        }

        // Update all view elements
        boolean isSeekable = mEnableJumpButtons && length > 0;
        return time;
    } */

}
