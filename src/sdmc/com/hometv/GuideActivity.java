package sdmc.com.hometv;

import hometv.remote.bean.NetModeManager;
import hometv.remote.bean.NetProtecolInterface;
import hometv.remote.bean.ProtecolLocalImp;
import hometv.remote.bean.ProtecolRemoteImp;
import hometv.remote.socketUtil.ConnectUtil;
import hometv.remote.socketUtil.Constant;
import hometv.remote.socketUtil.ToastUtil;

import org.videolan.libvlc.VLCApplication;

import com.sdmc.phone.util.NetUtil;
import com.sdmc.phone.util.PreferencesVisiter;

import sdmc.com.adapter.AdImageAdapter;
import sdmc.com.views.AutoFlipGallery;
import sdmc.com.views.CommonEditDialog;
import sdmc.com.views.CustomLinearLayout;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GuideActivity extends Activity implements 
				OnClickListener,
				OnItemSelectedListener{
    private static final String TAG="GuideActivity";
	private AutoFlipGallery autoFlipGallery;
	private LinearLayout ll_focus_indicator_container = null;
	private AdImageAdapter adImageAdapter=null;
	private CustomLinearLayout tvCastModule;
	private CustomLinearLayout dlnaModule;
	private CustomLinearLayout settingsModule;
	private CustomLinearLayout remoteCtrlModule;
	private NetUtil netUtil;
	private CommonEditDialog netDialog;
	PreferencesVisiter preferencesVisiter;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);  
	   setContentView(R.layout.layout_guide);
	   TextView headBack = (TextView) findViewById(R.id.head_back);
	   headBack.setVisibility(View.INVISIBLE);
	   TextView headCurTab = (TextView) findViewById(R.id.head_current_tab);
	   headCurTab.setText(R.string.app_name);
	   
	   preferencesVisiter = PreferencesVisiter.getVisiter();
	   netUtil = new NetUtil(this);
	   boolean isNetEnable = netUtil.isNetworkConnected();
	   int width = VLCApplication.screenWidth *2/3;
	   int height = VLCApplication.screenHeight / 4;
	   Log.w("info"," dialog width = "+width +" dialog height = "+height);
	   netDialog = new CommonEditDialog(this,width,height);
	   netDialog.setOnclickListener(this);
		 if(!isNetEnable){
			 netDialog.show(CommonEditDialog.DIALOG_TYPE_NO_WIFI);
		 }
	   initViews();
	}
	@Override
	protected void onStart() {
	super.onStart();
	
	}
	@Override
	protected void onRestart() {
	super.onRestart();
	
	}
	@Override
	protected void onResume() {
		super.onResume();
	 
	}
	@Override
	protected void onPause() {
	super.onPause();
	
	}
	protected void onStop() {
		super.onStop();
	};
	@Override
	protected void onDestroy() {
	super.onDestroy();
		autoFlipGallery.destroy();
		if(ConnectUtil.getInstance()!=null){
			ConnectUtil.getInstance().release(false);
		}
//		System.exit(0);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("info"," requestCode= "+requestCode +" resultCode= "+resultCode);
		if(requestCode == 200){
			boolean netEnable = netUtil.isNetworkConnected();
			if(netEnable){
				netDialog.dismiss();
			}
			else if(!netDialog.isShowing()){
				netDialog.show(CommonEditDialog.DIALOG_TYPE_NO_WIFI);
			}
		}
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	super.onConfigurationChanged(newConfig);
	
	}
	@Override
	public void onBackPressed() {
	super.onBackPressed();
	
	}
	private void initViews(){
		autoFlipGallery=(AutoFlipGallery)findViewById(R.id.auto_flip_gallery);
		adImageAdapter=new AdImageAdapter(this);
		autoFlipGallery.setAdapter(adImageAdapter);
		autoFlipGallery.setOnItemSelectedListener(this);
		ll_focus_indicator_container =(LinearLayout)findViewById(R.id.ll_focus_indicator_container);
		initFocusIndicatorContainer();
		tvCastModule=(CustomLinearLayout)findViewById(R.id.tvcast_module);
		dlnaModule=(CustomLinearLayout)findViewById(R.id.dlan_module);
		settingsModule=(CustomLinearLayout)findViewById(R.id.setting_module);
		remoteCtrlModule=(CustomLinearLayout)findViewById(R.id.remote_module);
		tvCastModule.setOnClickListener(this);
		dlnaModule.setOnClickListener(this);
		settingsModule.setOnClickListener(this);
		remoteCtrlModule.setOnClickListener(this);
	}
	private int preSelImgIndex=0;
	
    private void initFocusIndicatorContainer() {
	for (int i = 0; i < 3; i++) {
	    ImageView localImageView = new ImageView(this);
	    localImageView.setId(i);
	    ImageView.ScaleType localScaleType = ImageView.ScaleType.FIT_XY;
	    localImageView.setScaleType(localScaleType);
	    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
		    24, 24);
	    localImageView.setLayoutParams(localLayoutParams);
	    localImageView.setPadding(5, 5, 5, 5);
	    localImageView.setImageResource(R.drawable.indicator_default);
	    this.ll_focus_indicator_container.addView(localImageView);
	}
    }
	@Override
	public void onClick(View v) {
		boolean hasNetWork = netUtil.isNetworkConnected();
		int viewId=v.getId();
		if(! hasNetWork && viewId != R.id.setting_module && viewId != R.id.comm_dialog_tvbtn_sure){
			netDialog.show(CommonEditDialog.DIALOG_TYPE_NO_WIFI);
			return;
		}
		Intent intent=new Intent();
		boolean canEnter = false;
		switch (viewId) {
		case R.id.tvcast_module:
			boolean allowToPlay = preferencesVisiter.getEnableState(Constant.REMOTE_PLAY_ALLOW_STATE);
			if( !allowToPlay){
				//不允许观看
				canEnter = false;
				showDialog(NOT_ALLOW_PLAY);
				break;
			}
			boolean enableLocal = preferencesVisiter.getEnableState(Constant.ENABLE_LOCAL);
			boolean enableRemote = preferencesVisiter.getEnableState(Constant.ENABLE_REMOTE);
			NetModeManager netModeManager = NetModeManager.getNetModeManager();
			NetProtecolInterface subClass= netModeManager.getCurNetMode();
			if((!enableLocal && !enableRemote) || subClass == null){
				showDialog(NO_SETTED_NET);
				canEnter = false;
				break;
			}
			
//			boolean isRemoteSubClass = subClass instanceof ProtecolRemoteImp;
//			boolean isLocalSubClass = subClass instanceof ProtecolLocalImp;
			canEnter = true;
			intent.setClass(this, VideoPlayerActivity.class);
			break;
		case R.id.dlan_module:
			canEnter = netUtil.isWifiConnected();
			if(!canEnter){
				ToastUtil.topShow(R.string.dlna_need_wifi_hint );
			}
			intent.setClass(this, DlnaActivity.class);
			break;
		case R.id.remote_module:
			NetProtecolInterface netImp= NetModeManager.getNetModeManager().getCurNetMode();
			if(netImp == null || !(netImp instanceof ProtecolLocalImp)){
				canEnter = false;
				showDialog(LOCAL_NO_CONNECT);
				break;
			}
			intent.setClass(this, RemoteControlerGuideActivity.class);
			canEnter = true;
			break;
		case R.id.setting_module:
			intent.setClass(this, SettingsActivity.class);
			canEnter = true;
			break;
		case R.id.comm_dialog_tvbtn_sure://去设置网络
			 
			Intent toWifiSet = new Intent(android.provider.Settings.ACTION_SETTINGS);
			startActivityForResult(toWifiSet, 200);
			break;
		}
		if(canEnter)
			startActivityForResult(intent, 100);
	}
	private final int LOCAL_NO_CONNECT = 0;
	private final int NO_SETTED_NET = 1;
	private final int NOT_ALLOW_PLAY = 2;
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog =null;
		Builder builder=new android.app.AlertDialog.Builder(this);
		builder.setTitle(R.string.hint);
		 switch (id) {
		case LOCAL_NO_CONNECT:
            //设置对话框的图标
//            builder.setIcon(R.drawable.header);
            //设置对话框的标题
            builder.setMessage(R.string.no_connect_stb_in_local);
            //添加按钮，android.content.DialogInterface.OnClickListener.OnClickListener
             
            //创建一个列表对话框
            
			break;
		case NO_SETTED_NET:
			 builder.setMessage(R.string.no_connect_please_enter_setting);
			break;
		case NOT_ALLOW_PLAY: //不允许观看
			builder.setMessage(R.string.forbit_to_play_in_setting);
			break;
		}
		 dialog=builder.create();
		return dialog;
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		position=position % 3;
		ImageView preImg=(ImageView) ll_focus_indicator_container.findViewById(preSelImgIndex);
		preImg.setImageResource(R.drawable.indicator_default);
		ImageView curIndicator=(ImageView) ll_focus_indicator_container.findViewById(position);
		curIndicator.setImageResource(R.drawable.curindicator_select);
		preSelImgIndex=position;
	}
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
}
