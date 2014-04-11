package sdmc.com.hometv;

import hometv.remote.socketUtil.Constant;
import hometv.remote.socketUtil.ToastUtil;

import java.util.ArrayList;

import org.videolan.libvlc.VLCApplication;

import com.sdmc.dlna.filebrowser.Const;
import com.sdmc.dlna.filebrowser.FileItem;
import com.sdmc.dlna.filebrowser.FileUpdateUtil;
import com.sdmc.dlna.service.DeviceItem;
import com.sdmc.dlna.service.NativeAccess;
import com.sdmc.dlna.service.PlayUtil;
import com.sdmc.dlna.util.DevUpdateUitl;
import com.sdmc.phone.util.NetUtil;
import com.sdmc.phone.util.PreferencesVisiter;

import sdmc.com.adapter.DlnaDevicesAdapter;
import sdmc.com.adapter.FileAdapter;
import sdmc.com.hometv.R;
import sdmc.com.views.OnViewChangeListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class DlnaActivity extends Activity implements 
					OnViewChangeListener,
					OnItemClickListener ,
					OnClickListener{
	private final static String TAG = "DlnaActivity";
	
//	private MyScrollLayout myScrollLayout;
	private LinearLayout headMenuLayout ;
	private LinearLayout loadDataLayout ;
	private TextView tvDlnaResDirs;
	// ****************** dlna devices views *********
	private ViewSwitcher switcherDlnaPlayDevice;
	private ViewSwitcher switcherDlnaResDevice;
	private ListView lvDlnaPlayDevice;
	private ListView lvDlnaResDevice;
	private DlnaDevicesAdapter adapterDlnaPlayDevice;
	private DlnaDevicesAdapter adapterDlnaResDevice;
	
	//<----右进，左出<---
	private Animation slideRightIn;
	private Animation slideLeftOut;
	// ---> 左进，右出 --->
	private Animation slideLeftIn;
	private Animation slideRightOut;
    ArrayList<DeviceItem> dlnaPlayDevices ;
	ArrayList<DeviceItem> dlnaResDevices ;  
	private ImageView headIndicator;
	private int bmpW;// 动画图片宽度
	private int offset = 0;// 动画图片偏移量
	private ViewPager viewPager;
	private View dlnaDeviceView,dlnaPicView,dlnaVideoView,dlnaMusicView;
	VLCApplication app ;
	private TextView tvBtnDlnaDeviceTab;
	private TextView tvBtnDlnaPicTab;
	private TextView tvBtnDlnaVideoTab;
	private TextView tvBtnDlnaMusicTab;
//	private DlnaSettingPopuWindow dlnaSetPopuWindow;
	private PreferencesVisiter visiter;
	boolean hasDeviceDataAreadyBeforeUpdated = false;
	private void initDlnaDeviceViews(View dlnaDeviceView){
		   switcherDlnaPlayDevice  =(ViewSwitcher)dlnaDeviceView. findViewById(R.id.switcher_about_dlna_play_device);
		   switcherDlnaResDevice = (ViewSwitcher)dlnaDeviceView. findViewById(R.id.switcher_about_dlna_res_device);
		   slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
		   slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
		   slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
		   slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
		   lvDlnaPlayDevice  = (ListView) dlnaDeviceView.findViewById(R.id.lv_dlna_play_devices);
		   lvDlnaResDevice = (ListView) dlnaDeviceView.findViewById(R.id.lv_dlna_res_devices);
		   switcherDlnaPlayDevice .setInAnimation(slideRightIn);
		   switcherDlnaPlayDevice.setOutAnimation(slideLeftOut);
		   switcherDlnaResDevice .setInAnimation(slideRightIn);
		   switcherDlnaResDevice.setOutAnimation(slideLeftOut);
		   
		   //about DLNA play devices 
		   dlnaPlayDevices  = DevUpdateUitl.getInstance().getPlayerDeviceList();
		   adapterDlnaPlayDevice = new DlnaDevicesAdapter(this, null);
		   lvDlnaPlayDevice .setAdapter(adapterDlnaPlayDevice);
		   
		   // about DLNA res devices
		   dlnaResDevices  = DevUpdateUitl.getInstance().getDBDeviceList();
		   adapterDlnaResDevice = new DlnaDevicesAdapter(this,null);
		   lvDlnaResDevice .setAdapter(adapterDlnaResDevice);
		   
		   
		   
		   //add 2014 - 04 - 09
		 String  savedPlayDeviceUdn= visiter.getPreferInfo(Constant.DLNA_SAVE_PLAY_DEVICE_UDN, null);
		 String	savedResDeviceUdn= visiter.getPreferInfo(Constant.DLNA_SAVE_RES_DEVICE_UDN, null);
		   adapterDlnaPlayDevice.setCurrentCheckedItemBaseUdn(savedPlayDeviceUdn);
		   adapterDlnaResDevice.setCurrentCheckedItemBaseUdn(savedResDeviceUdn);
		   
		   //add end
		   lvDlnaPlayDevice.setOnItemClickListener(this);
		   lvDlnaResDevice.setOnItemClickListener(this);
		   DevUpdateUitl.setUpdateHandler(uiHandler);
//		   boolean hasDataAlready = false;
		   if( dlnaPlayDevices != null && dlnaPlayDevices.size() > 0){ // DLNA 播放设备
			   switcherDlnaPlayDevice.showNext();
			   adapterDlnaPlayDevice.updateListData(dlnaPlayDevices);
//			   hasDataAlready  = true;
			   hasDeviceDataAreadyBeforeUpdated = true;
			   app.setCurDlnaPlayDevices(dlnaPlayDevices);
			   //add 
			   if(savedPlayDeviceUdn != null){
				   curSelectedDlnaPlayDeviceId = findTheSaveDeviceByUdn(dlnaPlayDevices, savedPlayDeviceUdn);
			   }
			   //add end
		   }
		   if( dlnaResDevices != null && dlnaResDevices.size() > 0){ // DLNA 资源设备
			   switcherDlnaResDevice.showNext();
			   adapterDlnaResDevice.updateListData(dlnaResDevices);
//			   hasDataAlready  = true;
			   hasDeviceDataAreadyBeforeUpdated = true;
			   if(savedResDeviceUdn == null){
				   Object[] result = findTheSaveDeviceInfoByUdn(dlnaResDevices, NetUtil.getSelfDBUdn());
				   curSelectedDlnaResDeviceId = (Integer) result[0];
				   curSelectedResDeviceName = (String) result[1];
			   }
			   else{
				   Object[] result = findTheSaveDeviceInfoByUdn(dlnaResDevices, savedResDeviceUdn);
				   curSelectedDlnaResDeviceId = (Integer) result[0];
				   curSelectedResDeviceName = (String) result[1];
			   }
		   }
		   
		   
		   
		   if(hasDeviceDataAreadyBeforeUpdated ){
//		   	   loadDataLayoutShow();
			   isToGetRootDirsFile = true;
			   letNativeGetFileList(ROOT_FILENOTE_ID);
			   showDataContent();
		   }else{
			   loadDataLayoutShow();
		   }
		   
	}
	// ****************** dlna devices views *********
	
	private static final int ROOT_FILENOTE_ID = 0;
	private static final int GRANDPARENT_FILENOTE_ID = -1;
	private static final String ROOT_PATH = " root/";
	private int curSelectedDlnaResDeviceId = -1;
	/** dlna player device id, default = -1, means local dlna player **/
	private int curSelectedDlnaPlayDeviceId = -1;
	
	private String curSelectedResDeviceName = "";
	// ****************** dlna res views *********
	private ListView lvDlnaPic;
	private ListView lvDlnaVideo;
	private ListView lvDlnaMusic;
	private FileAdapter adapterPic;
	/** 得到 三个平级根目录  Video ; Music;  Image **/
	private FileAdapter adapterVideo;
	private FileAdapter adapterMusic;
	private ArrayList<String> mPathList = new ArrayList<String>();
	
	private void initDlnaResLayoutViews(){
		lvDlnaPic = (ListView) dlnaPicView.findViewById(R.id.lv_dlna_pic);
		lvDlnaVideo = (ListView)dlnaVideoView. findViewById(R.id.lv_dlna_video);
		lvDlnaMusic = (ListView) dlnaMusicView.findViewById(R.id.lv_dlna_music);
		
		lvDlnaPic .setOnItemClickListener(this); 
		lvDlnaVideo .setOnItemClickListener(this);
		lvDlnaMusic.setOnItemClickListener(this);
		adapterVideo = new FileAdapter(this, null);
		adapterMusic = new FileAdapter(this, null);
		adapterPic = new FileAdapter(this, null);
		lvDlnaMusic.setAdapter(adapterMusic);
		lvDlnaPic.setAdapter(adapterPic);
		lvDlnaVideo.setAdapter(adapterVideo);
		allVideoFolder = new ArrayList<FileItem>();
		allMusicFolder = new ArrayList<FileItem>();
		allImagesFolder = new ArrayList<FileItem>();
	 
	}
	
  	/**底层根据 资源设备ID 与文件节点进行查找文件  =-1 获取失败 **/
  	private int letNativeGetFileList(int curFileNodeId){
  		Log.e("info","  letNativeGetFileList () -->　curSelectedDlnaResDeviceId = "+curSelectedDlnaResDeviceId);
  	 return	NativeAccess.searchAllFileList(curSelectedDlnaResDeviceId, curFileNodeId);
  	}
 
		ArrayList<FileItem> allVideoFolder ;
		ArrayList<FileItem> allMusicFolder ;
		ArrayList<FileItem> allImagesFolder ;
		
		private void getAllMedias( ArrayList<FileItem> rootLis){
			if( rootLis == null || rootLis.size() < 3) return;
			
			FileItem videos  = rootLis.get(0);
			allVideoFolder.add(videos);
			adapterVideo.updateFileData(allVideoFolder);
			
			FileItem musics = rootLis.get(1);
			 allMusicFolder.add(musics);
			adapterMusic.updateFileData(allMusicFolder);
			
			FileItem images = rootLis.get(2);
			 allImagesFolder.add(images);
			 adapterPic.updateFileData(allImagesFolder);
			if(curViewIndex == DLNA_PIC_VIEW){
				tvDlnaResDirs.setText(curSelectedResDeviceName +"/"+images.getName());
			}
		}
	// ****************** dlna  res views *********
		 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlna_layout);
		app = (VLCApplication) getApplication();
		Log.i("info",TAG+"---> onCreate()");
		visiter = PreferencesVisiter.getVisiter();
		
		
		createDlnaSetDialog();
		FileUpdateUtil.setUiHandler(uiHandler);
		InitImageView();
		initViews();
		initDlnaDeviceViews(dlnaDeviceView);
		initDlnaResLayoutViews();
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i("info",TAG+"---> onRestart()");
	}
	@Override
	protected void onStart() {
		Log.i("info",TAG+"---> onStart()");
		super.onStart();
	}
	@Override
	protected void onResume() {
		Log.i("info",TAG+"---> onResume()");
		super.onResume();
		 
	}
	@Override
	protected void onPause() {
		Log.i("info",TAG+"---> onPause()");
		super.onPause();
	}
	@Override
	protected void onStop() {
		Log.i("info",TAG+"---> onStop()");
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		Log.i("info",TAG+"---> onDestroy()");
		super.onDestroy();
	}
	@Override
	protected void onNewIntent(Intent intent) {
		Log.i("info",TAG+"---> onNewIntent()");
		super.onNewIntent(intent);
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.i("info",TAG+"---> onCreateContextMenu()");
		super.onCreateContextMenu(menu, v, menuInfo);
	}
 
	@Override
	public void onBackPressed() {
		Log.i("info",TAG+"---> onBackPressed()");
		super.onBackPressed();
		if (mPathList.size() <= 1) {
			finish();
		} else {
//			listRemoveLastOne();
//			updateListView(GRANDPARENT_FILENOTE_ID);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("info",TAG+"---> onActivityResult()");
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.dlnasetting, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int menuOrder = item.getOrder();
		if(menuOrder == 100){
			
//			View rootView = findViewById(R.id.dlna_layout_root);
//			dlnaSetPopuWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
			dlnaSetDialog.show();
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(curViewIndex == 0){
			return true;
		}
			return false;
	}
	private Dialog dlnaSetDialog = null;
	private void createDlnaSetDialog (){
		dlnaSetDialog = new Dialog(this, R.style.dialog);
		View dialogView = getLayoutInflater().inflate(R.layout.dlna_setting_dialog_layout, null);
		Button btnCommitSet = (Button) dialogView.findViewById(R.id.btn_dlna_setting_commit	);
		final EditText editDlnaName = (EditText) dialogView.findViewById(R.id.edit_dlna_name);
		String theOldDLnaName = visiter.getPreferInfo(Constant.LOCAL_DLNA_NAME, "");
		editDlnaName.setText(theOldDLnaName);
		btnCommitSet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String newDlnaName = editDlnaName.getText().toString();
				String theOldDLnaName = visiter.getPreferInfo(Constant.LOCAL_DLNA_NAME, "");
				if(null == newDlnaName || newDlnaName.length() == 0 || newDlnaName .equals(theOldDLnaName)){
					dlnaSetDialog.dismiss();
					return;
				}
					NativeAccess.setUserName(newDlnaName+" : "+NetUtil.IP, NetUtil.MAC);
					visiter.savePrefer(Constant.LOCAL_DLNA_NAME, newDlnaName);
					dlnaSetDialog.dismiss();
			}
		});
		dlnaSetDialog.setContentView(dialogView);
	}
	/** 记录当前 视图序号 **/
	private int curViewIndex = 0;
	@Override
	public void OnViewChange(int viewIndex) {
		curViewIndex = viewIndex;
		if(viewIndex > 0){
			tvDlnaResDirs.setVisibility(View.VISIBLE);
			
//			// 默认去取自己的资源
//			if(
//					 
////					viewIndex == 1 
////					&& 
////					curSelectedDlnaResDeviceId == -1
//					){
////				if(dlnaResDevices != null && dlnaResDevices.size() > 0){
////					DeviceItem defaultDevice = dlnaResDevices.get(0);
////					curSelectedResDeviceName = defaultDevice.getName();
////					curSelectedDlnaResDeviceId  =defaultDevice.getDeviceID();
////				}
//				isToGetRootDirsFile = true;
//				loadDataLayoutShow();
//				letNativeGetFileList(ROOT_FILENOTE_ID);
//			}
		}
		else{
			tvDlnaResDirs.setVisibility(View.INVISIBLE);
		}
		// 填充数据
		updateDlnaMediaViews(viewIndex);
	}
	private static final int DLNA_PIC_VIEW = 1;
	private static final int DLNA_VIDEO_VIEW = 2;
	private static final int DLNA_MUSIC_VIEW = 3;
	
	/** 如果 用户没有选择资源设备，默认 显示手机自身的资源,切换页面时调用 **/
	private void updateDlnaMediaViews(int curMenuView){
		if(curMenuView <=0 || curMenuView >3) return;
		switch (curMenuView) {
		case DLNA_PIC_VIEW: //移动到了图片界面
			adapterPic.updateFileData(allImagesFolder);
			if(allImagesFolder.size() == 0)break;
			FileItem dirFileItem = allImagesFolder.get(0);
			if(dirFileItem.getFileType() == Const.FILETYPE_FOLDER){
				tvDlnaResDirs.setText(curSelectedResDeviceName +"/"+ dirFileItem.getName());
			}
			else{
				tvDlnaResDirs.setText(curSelectedResDeviceName +"/" +"All Images");
			}
			break;
		case DLNA_VIDEO_VIEW: //移动到了视频界面
			adapterVideo.updateFileData(allVideoFolder);
			if(allVideoFolder.size() == 0)break;
			FileItem dirVideoFileItem = allVideoFolder.get(0);
			if(dirVideoFileItem.getFileType() == Const.FILETYPE_FOLDER){
				tvDlnaResDirs.setText(curSelectedResDeviceName +"/"+ dirVideoFileItem.getName());
			}
			else{
				tvDlnaResDirs.setText(curSelectedResDeviceName +"/" +"All Videos");
			}
			break; 
		case DLNA_MUSIC_VIEW:  // 移动到了音乐界面
			adapterMusic.updateFileData(allMusicFolder);
			if(allMusicFolder.size() == 0)break;
			FileItem dirMusicFileItem = allMusicFolder.get(0);
			if(dirMusicFileItem.getFileType() == Const.FILETYPE_FOLDER){
				tvDlnaResDirs.setText(curSelectedResDeviceName +"/"+ dirMusicFileItem.getName());
			}
			else{
				tvDlnaResDirs.setText(curSelectedResDeviceName +"/" +"All Musics");
			}
			break; 
		}
	}
	/** 根据当前页面 接收来自底层的查询文件结果来更新界面 **/
	private void updateMediaListByCurView(int curViewIndex){
		if(curViewIndex == 0)return;
		switch(curViewIndex){
		case DLNA_PIC_VIEW: //移动到了图片界面
			allImagesFolder = FileUpdateUtil.getFileList();
			adapterPic.updateFileData(allImagesFolder);
			tvDlnaResDirs.setText(curSelectedResDeviceName +"/All Image" );
			break;
		case DLNA_VIDEO_VIEW: //移动到了视频界面
			allVideoFolder = FileUpdateUtil.getFileList();
			adapterVideo.updateFileData(allVideoFolder);
			tvDlnaResDirs.setText(curSelectedResDeviceName +"/All Video" );
			break; 
		case DLNA_MUSIC_VIEW:  // 移动到了音乐界面
			allMusicFolder = FileUpdateUtil.getFileList();
			adapterMusic.updateFileData(allMusicFolder);
			tvDlnaResDirs.setText(curSelectedResDeviceName +"/All Music" );
			break; 
		}
	}
	private void loadDataLayoutShow(){
		loadDataLayout.setVisibility(View.VISIBLE);
		viewPager.setVisibility(View.INVISIBLE); 
	}
	private void showDataContent(){
		loadDataLayout.setVisibility(View.INVISIBLE);
		viewPager.setVisibility(View.VISIBLE); 
	}
	private void initViews(){
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		 
		ArrayList<View> chidViews = new ArrayList<View>();
		dlnaDeviceView = layoutInflater.inflate(R.layout.dlna_device_layout,null);
		chidViews.add(dlnaDeviceView);
		dlnaPicView = layoutInflater.inflate(R.layout.dlna_pic_layout, null);
		chidViews.add(dlnaPicView);
		dlnaVideoView = layoutInflater.inflate(R.layout.dlna_video_layout, null);
		chidViews.add(dlnaVideoView);
		dlnaMusicView = layoutInflater.inflate(R.layout.dlna_music_layout, null);
		chidViews.add(dlnaMusicView);
		
		MyPageViewAdapter  pageViewAdapter = new MyPageViewAdapter(chidViews);
		viewPager.setAdapter(pageViewAdapter);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		headMenuLayout = (LinearLayout) findViewById(R.id.top_menu_layout);
		loadDataLayout = (LinearLayout) findViewById(R.id.loading_data_layout);
		tvDlnaResDirs = (TextView) findViewById(R.id.tv_dlna_res_dirs);
		tvBtnDlnaDeviceTab = (TextView) findViewById(R.id.tvbtn_dlna_device_tab);
		tvBtnDlnaPicTab = (TextView) findViewById(R.id.tvbtn_dlna_pic_tab);
		tvBtnDlnaVideoTab = (TextView) findViewById(R.id.tvbtn_dlna_video_tab);
		tvBtnDlnaMusicTab = (TextView) findViewById(R.id.tvbtn_dlna_music_tab);
		
		tvBtnDlnaDeviceTab.setOnClickListener(this);
		tvBtnDlnaMusicTab.setOnClickListener(this);
		tvBtnDlnaPicTab.setOnClickListener(this);
		tvBtnDlnaVideoTab.setOnClickListener(this);
//		dlnaSetPopuWindow = new DlnaSettingPopuWindow(this);
	}
	int mCurSelectTabIndex = 1;
	private void InitImageView() {
		headIndicator= (ImageView) findViewById(R.id.tab_top_select_indicator);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.pageview_indicator).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 4 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		headIndicator.setImageMatrix(matrix);// 设置动画初始位置
	}
	int selfResDeviceId = -1;
    private Handler uiHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		int msgWhat = msg.what;
    		switch (msgWhat) {
			case DevUpdateUitl.MSG_REFRESH_DLNA_DEVICE: // DLNA devices are updated...
				showDataContent();
				dlnaPlayDevices = DevUpdateUitl.getInstance().getPlayerDeviceList();
				dlnaResDevices = DevUpdateUitl.getInstance().getDBDeviceList();
				String savedResUdn = visiter.getPreferInfo(Constant.DLNA_SAVE_RES_DEVICE_UDN, null);
				
				Object [] result = savedResUdn == null ? 
						findTheSaveDeviceInfoByUdn(dlnaResDevices, NetUtil.getSelfDBUdn()): //find self
						findTheSaveDeviceInfoByUdn(dlnaResDevices, savedResUdn);	        // find saved
				curSelectedDlnaResDeviceId = (Integer) result[0];
				curSelectedResDeviceName = (String) result[1];
			 
				
				String savedPlayUdn = visiter.getPreferInfo(Constant.DLNA_SAVE_PLAY_DEVICE_UDN, null);
				if(savedPlayUdn != null){ // 有保存
					curSelectedDlnaPlayDeviceId = findTheSaveDeviceByUdn(dlnaPlayDevices, savedPlayUdn);
				}
				
				selfResDeviceId = msg.arg1;
				Log.i("info",TAG+" handleMessage  the local dlna res device id = "+selfResDeviceId);
				int switchPlayDeviceChildIndex=	switcherDlnaPlayDevice.getDisplayedChild();
				setViewSwitcherAnimationMode(switcherDlnaPlayDevice, switchPlayDeviceChildIndex);
				if(dlnaPlayDevices != null && dlnaPlayDevices.size() > 0){
					app.setCurDlnaPlayDevices(dlnaPlayDevices);	
					if(! hasDeviceDataAreadyBeforeUpdated ){
						 isToGetRootDirsFile = true;
						 letNativeGetFileList(ROOT_FILENOTE_ID);
					}
					hasDeviceDataAreadyBeforeUpdated = true;
					if(switchPlayDeviceChildIndex==0){ //TextView
						switcherDlnaPlayDevice.showNext();
					}
				}else{
					if(switchPlayDeviceChildIndex == 1){ // ListView
						switcherDlnaPlayDevice.showPrevious();
					}
				}
				int switchResDeviceChildIndex=	switcherDlnaResDevice.getDisplayedChild();
				setViewSwitcherAnimationMode(switcherDlnaResDevice, switchResDeviceChildIndex);
				if(dlnaResDevices != null && dlnaResDevices.size() > 0){
					if(switchResDeviceChildIndex==0){ //TextView
						switcherDlnaResDevice.showNext();
					}
				}else{
					if(switchResDeviceChildIndex == 1){ // ListView
						switcherDlnaResDevice.showPrevious();
					}
				}
				adapterDlnaPlayDevice.updateListData(dlnaPlayDevices);
				adapterDlnaResDevice.updateListData(dlnaResDevices);
				
//				lvDlnaResDevice.setFocusableInTouchMode(true);
//				lvDlnaResDevice.requestFocusFromTouch();
				break;
			case FileUpdateUtil.MSG_FILE_UPDATE: // 各文件节点下的文件更新
				showDataContent();
				int msgArg = msg.arg1;
				Log.i("info",TAG +" handleMessage() get file result ：  msgArg = "+msgArg);
				switch (msgArg) {
				case FileUpdateUtil.MSG_HAS_NO_FILE:
					ToastUtil.showToast(R.string.dlna_get_file_empty, Gravity.BOTTOM);
					break;
				case FileUpdateUtil.MSG_UPDATE_ERROR:
					ToastUtil.showToast(R.string.dlna_get_file_error, Gravity.BOTTOM);
					break;
				case FileUpdateUtil.MSG_NEED_UPDATE: // 更新文件列表
					if(isToGetRootDirsFile){
						getAllMedias(FileUpdateUtil.getFileList());
						isToGetRootDirsFile = false;
					}
					else{
						// 根据当前页面来更新 文件列表
						updateMediaListByCurView(curViewIndex);
					}
					break;
				}
				break;
			}
    	};
    };
    boolean isToGetRootDirsFile = false;
   /** just find the saved DLNA play device's device ID **/
   private int findTheSaveDeviceByUdn(ArrayList<DeviceItem> devices,String savedUdn){
    	if(devices == null || devices.size() == 0)return - 1;
    	for(DeviceItem device : devices){
    		String curUdn = device.getUdn();
    		if(savedUdn .equals(curUdn)){
    			return device.getDeviceID();
    		}
    	}
    	return -1;
    } 
    
   /**
    * find the saved selected DLNA device's device ID and its name
    * @param devices
    * @param savedUdn
    * @return Object[0] is the device ID,if no result its default value is -1 ;Object[1] is the device name if no result its default name is null; 
    */
   private Object[] findTheSaveDeviceInfoByUdn(ArrayList<DeviceItem> devices,String savedUdn){
	   Object[] result = new Object[2];
	   result[0] = -1;
	   result[1] = null;
   	if(devices == null || devices.size() == 0){
   		return result;
   	}
   	for(DeviceItem device : devices){
   		String curUdn = device.getUdn();
   		if(savedUdn .equals(curUdn)){
   			result[0] = device.getDeviceID();
   			result[1] = device.getName();
   			return result;
   		}
   	}
   	return result;
   }
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int lvId = parent.getId();
		switch (lvId) {
		case R.id.lv_dlna_play_devices: // dlna 播放设备列表
			parent.requestFocus();
			parent.requestFocusFromTouch();
			curSelectedDlnaPlayDeviceId = (int) adapterDlnaPlayDevice.getItemId(position);
//			adapterDlnaPlayDevice.setCurrentCheckedItem(position);
			
			app.curDlnaPushedDeviceId = curSelectedDlnaPlayDeviceId; 
			DeviceItem curPlayDevice = dlnaPlayDevices.get(position);
			String udn = curPlayDevice.getUdn();
			adapterDlnaPlayDevice.setCurrentCheckedItemBaseUdn(udn);
			visiter.savePrefer(Constant.DLNA_SAVE_PLAY_DEVICE_UDN, udn);
			break;
		case R.id.lv_dlna_res_devices: // dlna 资源设备列表
			parent.requestFocus();
			parent.requestFocusFromTouch();
//			adapterDlnaResDevice.setCurrentCheckedItem(position);
			int clickedDlnaResDeviceId = (int) adapterDlnaResDevice.getItemId(position);
			if(curSelectedDlnaResDeviceId == clickedDlnaResDeviceId) break;
			curSelectedDlnaResDeviceId = clickedDlnaResDeviceId;
			//获取资源
			DeviceItem curDeviceItem = dlnaResDevices.get(position);
			String savedResDeviceUdn = curDeviceItem.getUdn();
			adapterDlnaResDevice.setCurrentCheckedItemBaseUdn(savedResDeviceUdn);
			visiter.savePrefer(Constant.DLNA_SAVE_RES_DEVICE_UDN, savedResDeviceUdn);
			curSelectedResDeviceName = curDeviceItem.getName();
			allImagesFolder.clear();
			allMusicFolder.clear();
			allVideoFolder.clear();
			// 一点击每个资源设备，则先去获取根文件目录
			int findResult =  letNativeGetFileList(ROOT_FILENOTE_ID);
			if(findResult == -1){
				//获取失败
				ToastUtil.showToast(R.string.dlna_get_file_error, Gravity.BOTTOM);
			}
			isToGetRootDirsFile = true;
			
			break;
		case R.id.lv_dlna_pic:
			isToGetRootDirsFile =false;
			Object picItem = parent.getItemAtPosition(position);
			if (picItem instanceof FileItem) {
				FileItem fi = (FileItem) picItem;
				if (fi.getFileType() == Const.FILETYPE_FOLDER) {
//					mPathList.add(fi.getName() + "/");
					int fileNode = fi.getId();
					letNativeGetFileList(fileNode);
					loadDataLayoutShow();
				} else {
					app.setCurDlnaPlayList(allImagesFolder);
					goToPlay(fi, position);
				}
			}
			break;
		case R.id.lv_dlna_video:
			isToGetRootDirsFile =false;
			Object videoItem = parent.getItemAtPosition(position);
			if (videoItem instanceof FileItem) {
				FileItem fi = (FileItem) videoItem;
				if (fi.getFileType() == Const.FILETYPE_FOLDER) {
//					mPathList.add(fi.getName() + "/");
					int fileNode = fi.getId();
					Log.i("info"," video list item click cur file node is  ："+fileNode);
					letNativeGetFileList(fileNode);
					loadDataLayoutShow();
				} else {
					app.setCurDlnaPlayList(allVideoFolder);
					goToPlay(fi, position);
				}
			}
			break;
		case R.id.lv_dlna_music:
			isToGetRootDirsFile =false;
			Object musicItem = parent.getItemAtPosition(position);
			if (musicItem instanceof FileItem) {
				FileItem fi = (FileItem) musicItem;
				if (fi.getFileType() == Const.FILETYPE_FOLDER) {
//					mPathList.add(fi.getName() + "/");
					letNativeGetFileList(fi.getId());
					loadDataLayoutShow();
				} else {
					app.setCurDlnaPlayList(allMusicFolder);
					goToPlay(fi, position);
				}
			}
			break;
		}
	}
	private void setViewSwitcherAnimationMode(ViewSwitcher curSwitcher,int curChildIndex){
		switch (curChildIndex) {
		case 0: //TextView 此时要左出 <-- 右进<-- 
			curSwitcher.setInAnimation(slideRightIn);
			curSwitcher.setOutAnimation(slideLeftOut);
			break;
		case 1: // has data child layout, 此时要 左进 --> 右出-->
			curSwitcher.setInAnimation(slideLeftIn);
			curSwitcher.setOutAnimation(slideRightOut);
			break;
		}
	}
	private class MyPageViewAdapter extends PagerAdapter{
		public ArrayList<View> childViews;
		 public MyPageViewAdapter(ArrayList<View> mListViews) {
	            this.childViews = mListViews;
	        }
		@Override
		public int getCount() {
			return childViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View theChild = childViews.get(position);
			 container.addView(theChild);
			return theChild;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(childViews.get(position));  
		}
		 
	}
	class MyOnPageChangeListener implements OnPageChangeListener{
        @Override
        public   void onPageScrollStateChanged (int state){
             
        }
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
       
        @Override
        public void onPageSelected(int position) {
//            Log.e("info",  "page view pos="+position);
            int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
//    		int two = one * 2;// 页卡1 -> 页卡3 偏移量
            Animation animation = new TranslateAnimation(one * mCurSelectTabIndex, one * position, 0, 0);
            mCurSelectTabIndex = position;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			headIndicator.startAnimation(animation);
			OnViewChange(position);
        }
    }

	private void goToPlay(FileItem curSelectedFile,int curListPosition){
		if(curSelectedDlnaPlayDeviceId == -1){
			PlayUtil.playInLocal(curSelectedFile.getAbsPath(), curSelectedFile.getFileType(), false,curListPosition);
		}
		else{
			app.curDlnaPushedDeviceId = curSelectedDlnaPlayDeviceId;
			PlayUtil.playInOther(curSelectedDlnaPlayDeviceId, curSelectedFile.getAbsPath(), curSelectedFile.getFileType(),curListPosition,true);
		}
	}

	@Override
	public void onClick(View v) {
		boolean isDlnaTabClick = false;
		int curTab = 0;
		int viewId  = v.getId();
		switch (viewId) {
		case R.id.tvbtn_dlna_device_tab:
			curTab = 0;
			isDlnaTabClick = true;
			break;
		case R.id.tvbtn_dlna_pic_tab:
			curTab = 1;
			isDlnaTabClick = true;
			break;
		case R.id.tvbtn_dlna_video_tab:
			curTab = 2;
			isDlnaTabClick = true;
			break;
		case R.id.tvbtn_dlna_music_tab:
			curTab = 3;
			isDlnaTabClick = true;
			break;
		}
		if(isDlnaTabClick)
			viewPager.setCurrentItem(curTab);
	}
}
// -------------------------------------  end -- save some no use methods ----------------------------
///** 
//* 移动tab选中标识图片 
//* @param selectIndex 
//* @param curIndex 
//*/  
//public void moveTopSelect(int selectIndex) { 
////	Log.w("info"," moveTopSelect*****  mCurSelectTabIndex= "+mCurSelectTabIndex +" ------> selectIndex "+selectIndex);
//	View ivHeadIndicator = (ImageView) findViewById(R.id.tab_top_select_indicator);
//	ivHeadIndicator.setVisibility(View.VISIBLE);
// // 起始位置中心点  
//	int startMid = ((View) headMenuLayout.getChildAt(mCurSelectTabIndex)).getLeft() + ((View) headMenuLayout.getChildAt(mCurSelectTabIndex)).getWidth() / 2;  
// // 起始位置左边位置坐标  
// int startLeft = startMid - headMenuLayout.getWidth() / 2;  
//
// // 目标位置中心点  
// int endMid = ((View) headMenuLayout.getChildAt(selectIndex)).getLeft() + ((View) headMenuLayout.getChildAt(selectIndex)).getWidth() / 2;  
// // 目标位置左边位置坐标  
// int endLeft = endMid - ivHeadIndicator.getWidth() / 2;  
//
// TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft - ivHeadIndicator.getLeft(), 0, 0);  
// animation.setDuration(200);  
// animation.setFillAfter(true);  
// ivHeadIndicator.bringToFront();  
// ivHeadIndicator.startAnimation(animation);  
// // 改变当前选中按钮索引  
// mCurSelectTabIndex = selectIndex;  
//// Log.e("fs", "endMid  " + endMid + "  startLeft  " + startLeft + "  endLeft" + (endLeft - topSelect.getLeft()));  
//}  