package sdmc.com.hometv;

import hometv.remote.socketUtil.ConnectUtil;
import hometv.remote.socketUtil.Constant;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sdmc.phone.util.NetConnect;
import com.sdmc.phone.util.PreferencesVisiter;
/**
 * 设置界面
 * @author fee
 *
 */
public class SettingsActivity extends Activity implements 
				OnClickListener,OnCheckedChangeListener{
	private static final String TAG="SettingsActivity";
	/**
	 * 返回
	 */
	private TextView tvHeadBack = null;
	private TextView tvHeadCurTab = null;
	/**
	 * 当前登陆的用户
	 */
	private TextView curLoginUser = null;
	/**
	 * 当前连接的局域网服务DVB
	 */
	private TextView curLocalConnectedServer = null;
	/**
	 * 选择去进行远程登陆
	 */
	private TextView tvArrowToRemote = null;
	/**
	 * 选择去进行本地连接
	 */
	private TextView tvArrowToLocal = null;
	/**
	 * 选择去查看本程序版本
	 */
	private TextView tvArrowToCurApp = null;
	private ToggleButton togBtnChooseRemote = null;
	private ToggleButton togBtnChooseLocal = null;
	private ToggleButton togBtnEnableSensorAccelerator = null;
	private ToggleButton togBtnEnableSensorDirection = null;
	private ToggleButton togBtnAllowPlayTv = null;
	private ToggleButton togBtnEnableHwDecoding = null;
	
	private PreferencesVisiter preFerVisiter;
	


	String remote_login_suc;
	String remote_login_fail;
	String local_connect_suc;
	String local_connect_fail;
	
	Spinner picQualitySpinner;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   setContentView(R.layout.settings_layout);
	   Resources res = getResources();
	   remote_login_suc = res.getString(R.string.remote_connect_login_state_suc);
	   remote_login_fail = res.getString(R.string.remote_connect_login_state_fail);
	   local_connect_fail = res.getString(R.string.local_connect_state_fail);
	   local_connect_suc = res.getString(R.string.local_connect_state_suc);
	   preFerVisiter=PreferencesVisiter.getVisiter();
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
		//get the settings
	 boolean isRemoteEnable=preFerVisiter.getEnableState(Constant.ENABLE_REMOTE);
	 togBtnChooseRemote.setChecked(isRemoteEnable);
	 tvArrowToRemote.setEnabled(isRemoteEnable);
	 boolean isLocalEnable=preFerVisiter.getEnableState(Constant.ENABLE_LOCAL);
	 togBtnChooseLocal.setChecked(isLocalEnable);
	 tvArrowToLocal.setEnabled(isLocalEnable);
	 togBtnEnableSensorAccelerator.setChecked(preFerVisiter.getEnableState(Constant.ENABLE_SENSOR_SPEED));
	 togBtnEnableSensorDirection.setChecked(preFerVisiter.getEnableState(Constant.ENABLE_SENSOR_DIRECTION));
	 togBtnAllowPlayTv.setChecked(preFerVisiter.getEnableState(Constant.REMOTE_PLAY_ALLOW_STATE));
	 togBtnEnableHwDecoding.setChecked(preFerVisiter.getEnableState(Constant.ENABLE_HW_DECODEING));
	
	 String user = preFerVisiter.getPreferInfo(Constant.USERNAME_KEY, "");
		if(ConnectUtil.login_success){
			curLoginUser.setText(user+" "+remote_login_suc);
		}
		else{
			curLoginUser.setText(user+" "+remote_login_fail);
		}
	 String connectedLocalIp = preFerVisiter.readLastIp();
	 	if(NetConnect.mIsLocalConnected){
	 		curLocalConnectedServer.setText(connectedLocalIp + " "+local_connect_suc);
	 	}
	 	else{
	 		curLocalConnectedServer.setText(connectedLocalIp + " "+local_connect_fail);
	 	}
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
	
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	 switch (requestCode) {
	case REQUEST_CODE_FOR_ABOUT_APP:
		
		break;
	case REQUEST_CODE_FOR_REMOTE_CONNECT:
//		String user = preFerVisiter.getPreferInfo(Constant.USERNAME_KEY, "");
//		if(ConnectUtil.login_success){
//			curLoginUser.setText(user+remote_login_suc);
//		}
//		else{
//			curLoginUser.setText(user+remote_login_fail);
//		}
		break;
	case REQUEST_CODE_FOR_LOCAL_CONNECT:
//		curLocalConnectedServer.setText("");?
		break;
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
		tvHeadBack = (TextView) findViewById(R.id.head_back);
		tvHeadCurTab = (TextView) findViewById(R.id.head_current_tab);
		tvHeadCurTab.setText(R.string.setting_module_name);
		curLoginUser =(TextView) findViewById(R.id.tv_cur_login_user);
		curLocalConnectedServer = (TextView) findViewById(R.id.tv_cur_local_connected_ip);
		tvArrowToRemote = (TextView) findViewById(R.id.tvbtn_arrow_to_remote);
		tvArrowToLocal = (TextView) findViewById(R.id.tvbtn_arrow_to_local);
		tvArrowToCurApp = (TextView) findViewById(R.id.tvbtn_arrow_to_about_app);
		
		togBtnChooseRemote = (ToggleButton) findViewById(R.id.togglebtn_turn_remote_net_on);
		togBtnChooseLocal = (ToggleButton) findViewById(R.id.togglebtn_turn_local_net_on);
		togBtnEnableSensorAccelerator = (ToggleButton) findViewById(R.id.togglebtn_turn_sensor_speed_on);
		togBtnEnableSensorDirection = (ToggleButton) findViewById(R.id.togglebtn_turn_sensor_orientation_on);
		togBtnAllowPlayTv = (ToggleButton) findViewById(R.id.togglebtn_turn_can_play_on);
		togBtnEnableHwDecoding = (ToggleButton) findViewById(R.id.togglebtn_turn_enable_hw_decoding_on);
		//set listener
		tvHeadBack.setOnClickListener(this);
		tvArrowToRemote.setOnClickListener(this);
		tvArrowToLocal.setOnClickListener(this);
		
		
		tvArrowToCurApp.setOnClickListener(this);
		togBtnChooseRemote.setOnCheckedChangeListener(this);
		togBtnChooseLocal.setOnCheckedChangeListener(this);
		togBtnEnableSensorAccelerator.setOnCheckedChangeListener(this);
		togBtnEnableSensorDirection.setOnCheckedChangeListener(this);
		togBtnAllowPlayTv.setOnCheckedChangeListener(this);
		togBtnEnableHwDecoding.setOnCheckedChangeListener(this);
		picQualitySpinner = (Spinner) findViewById(R.id.spinner_pic_quality);
		ArrayAdapter<CharSequence>  picQualityAdapter = ArrayAdapter.createFromResource(this, R.array.pic_selections, android.R.layout.simple_spinner_item);
		picQualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
		picQualitySpinner.setAdapter(picQualityAdapter);
		picQualitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				preFerVisiter.saveIntPrefer(Constant.CUR_SELECTED_PIC_QUAL_INDEX, position);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		}); 
		SharedPreferences sp = preFerVisiter.getSharedPreferences();
		int curSelectedPicIndex = sp.getInt(Constant.CUR_SELECTED_PIC_QUAL_INDEX, 0);
		picQualitySpinner.setSelection(curSelectedPicIndex);
	}
	private static final int REQUEST_CODE_FOR_REMOTE_CONNECT = 300;
	private static final int REQUEST_CODE_FOR_LOCAL_CONNECT = 200;
	private static final int REQUEST_CODE_FOR_ABOUT_APP = 400;
	
	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.head_back:
			finish();
			break;
		case R.id.tvbtn_arrow_to_remote: //远程连接
			Intent toRemote=new Intent(this, RemoteConnectActivity.class);
			startActivityForResult(toRemote, REQUEST_CODE_FOR_REMOTE_CONNECT);
			break;
		case R.id.tvbtn_arrow_to_local://本地连接
			Intent toLocal=new Intent(this, LocalConnectActivity.class);
			startActivityForResult(toLocal, REQUEST_CODE_FOR_LOCAL_CONNECT);
			break;
		case R.id.tvbtn_arrow_to_about_app://关于程序
			Intent toAbout=new Intent(this, AboutAppInfoActivity.class);
			startActivityForResult(toAbout, REQUEST_CODE_FOR_ABOUT_APP);
			break;
		}
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int toggleBtnId=buttonView.getId();
		switch (toggleBtnId) {
		case R.id.togglebtn_turn_remote_net_on: //远程连接选择
			tvArrowToRemote.setEnabled(isChecked);
			preFerVisiter.savePrefer(Constant.ENABLE_REMOTE, isChecked);
			if(isChecked){
				togBtnChooseLocal.setChecked(false);
			}
			break;
		case R.id.togglebtn_turn_local_net_on:
			tvArrowToLocal.setEnabled(isChecked);
			preFerVisiter.savePrefer(Constant.ENABLE_LOCAL, isChecked);
			if(isChecked){
				togBtnChooseRemote.setChecked(false);
			}
			break;
		case R.id.togglebtn_turn_sensor_speed_on:
			preFerVisiter.savePrefer(Constant.ENABLE_SENSOR_SPEED, isChecked);
			break;
		case R.id.togglebtn_turn_sensor_orientation_on:
			preFerVisiter.savePrefer(Constant.ENABLE_SENSOR_DIRECTION, isChecked);
			break;
		case R.id.togglebtn_turn_can_play_on:
			preFerVisiter.savePrefer(Constant.REMOTE_PLAY_ALLOW_STATE, isChecked);
			break;
		case R.id.togglebtn_turn_enable_hw_decoding_on:
			preFerVisiter.savePrefer(Constant.ENABLE_HW_DECODEING, isChecked);
			break;
		}
		
	}
}
