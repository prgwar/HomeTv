package sdmc.com.hometv;

import hometv.remote.socketUtil.ToastUtil;

import java.util.ArrayList;

import sdmc.com.adapter.ScannedDeviceAdapter;

import com.sdmc.phone.stat.DefaultData;
import com.sdmc.phone.stat.ScanData;
import com.sdmc.phone.util.NetConnect;
import com.sdmc.phone.util.NetUtil;
import com.sdmc.phone.util.PreferencesVisiter;
import com.sdmc.phone.util.ScanManager;
import com.sdmc.phone.util.ScanManager.OnScanCompleteListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.widget.CompoundButton.OnCheckedChangeListener;
/**
 * 局域网连接
 * @author fee
 *
 */
public class LocalConnectActivity extends Activity implements 
					OnClickListener,
//					UdpScanCompleteListener,
					OnScanCompleteListener,
					OnItemClickListener{
	private static final String TAG="LocalConnectActivity";
	private static boolean debug=true;
	private TextView tvHeadBack;
	private TextView tvHeadCurTab;
	private TextView curLocalServerName;
	private TextView curLocalServerIP;
	private TextView curConnectState;
	private TextView tvArrowProcessCurServer;
	private RelativeLayout layoutCurConnected;
	private ViewSwitcher switcherAboutConnDevice;
	private ViewSwitcher switcherAboutDevices;
	
	/**
	 * 所扫描到的本地设备
	 */
	private ListView lvDevices;
	private TextView tvBtnManuallyAdd;
	private TextView tvBtnScan;
//	private ScanLocalServerByUDP scanner;
	private ScanManager mScanManager;
	private ScannedDeviceAdapter deviceAdapter;
	private ArrayList<ScanData.ScanInfo> deviceList;
	private boolean isScanning = false;
	private boolean isInManullyConnect = false;
	private static final int MSG_SCAN_END = 0;
	private static final int MSG_CONNECT_END=100;
	private PreferencesVisiter preferencesVisiter;
	//<----右进，左出<---
	private Animation slideRightIn;
	private Animation slideLeftOut;
	// ---> 左进，右出 --->
	private Animation slideLeftIn;
	private Animation slideRightOut;
	private ScanData.ScanInfo curConnectDevice = null;
	private NetConnect mNetConnect;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   setContentView(R.layout.connect_local);
	   preferencesVisiter = PreferencesVisiter.getVisiter();
	   slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
	   slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
	   slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
	   slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
//	   scanner = ScanLocalServerByUDP.getUDPScanner();
//	   scanner.setOnScanCompleteListener(this);
//	   deviceList = scanner.getScannedResult();
	   deviceList = ScanData.instance().getScanList();
	   mScanManager = new ScanManager(NetUtil.IP, this);
	   deviceAdapter = new ScannedDeviceAdapter(this, null);
	   mNetConnect = new NetConnect(handler);
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
	
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	super.onConfigurationChanged(newConfig);
	
	}
	@Override
	public void onBackPressed() {
	super.onBackPressed();
	
	}
	@Override
	public void onClick(View v) {
		int viewId=v.getId();
		switch (viewId) {
		case R.id.head_back:
			finish();
			break;
		case R.id.layout_has_connected_in_local_set: //单击整个已连接布局
			break;
		case R.id.tvbtn_arrow_to_process_has_connected_server: //功能同上
			break;
		case R.id.tvbtn_manual_add:  
			if(isScanning){ 
//				ToastUtil.topShow("正在扫描，请稍候！");
				break;
				}
			manullyDialog.show();
			break;
		case R.id.tvbtn_scan: //扫描设备
			if(isScanning) break;
//				scanner.startToScanServer();
				isScanning = true;
			    mScanManager.scan();
				tvBtnScan.setText(R.string.scanning);
			break;
		case R.id.tvbtn_cancel_connect:
			connectDialog.dismiss();
			break;
		case R.id.tvbtn_connect:
			//连接局域网
			mNetConnect.inerToConnect(prepareIp, DefaultData.port);
			connectDialog.dismiss();
			break;
		case R.id.tvbtn_recover_port: //恢复默认端口
			editPort.setText(DefaultData.port+"");
			break;
		case R.id.tvbtn_commit_manully:
			if(!checkInputInfo()) break;
			//连接
			isInManullyConnect = true;
			mNetConnect.inerToConnect(prepareIp, DefaultData.port);
			
			String ip01 = null,ip02 = null,ip03 = null,ip04 = null;
			if(checkBoxSaveIp.isChecked()){
				 ip01 = editIP01.getText().toString();
				 ip02 = editIP02.getText().toString();
				 ip03 = editIP03.getText().toString();
				 ip04 = editIP04.getText().toString();
			}
			preferencesVisiter.writeInputIp(ip01, ip02, ip03, ip04);
			manullyDialog.dismiss();
			break;
		}
	}
	private void initViews(){
		tvHeadBack = (TextView) findViewById(R.id.head_back);
		tvHeadCurTab = (TextView) findViewById(R.id.head_current_tab);
		curLocalServerName = (TextView) findViewById(R.id.tv_cur_server_name);
		curLocalServerIP = (TextView) findViewById(R.id.tv_cur_server_ip);
		curConnectState = (TextView) findViewById(R.id.tv_cur_connect_state);
		tvArrowProcessCurServer = (TextView) findViewById(R.id.tvbtn_arrow_to_process_has_connected_server);
		layoutCurConnected = (RelativeLayout) findViewById(R.id.layout_has_connected_in_local_set);
		lvDevices = (ListView) findViewById(R.id.lv_scanned_devices);
		lvDevices.setOnItemClickListener(this);
		lvDevices.setAdapter(deviceAdapter);
		deviceAdapter.updateDevices(deviceList);
		tvBtnManuallyAdd = (TextView) findViewById(R.id.tvbtn_manual_add);
		tvBtnScan = (TextView) findViewById(R.id.tvbtn_scan);		
		tvHeadCurTab.setText(R.string.local_connect);
		switcherAboutConnDevice = (ViewSwitcher) findViewById(R.id.switcher_about_cur_connect);
		switcherAboutDevices =  (ViewSwitcher) findViewById(R.id.switcher_about_device);
		switcherAboutConnDevice.setInAnimation(slideRightIn);
		switcherAboutConnDevice.setOutAnimation(slideLeftOut);
		switcherAboutDevices.setInAnimation(slideRightIn);
		switcherAboutDevices.setOutAnimation(slideLeftOut);
		if(deviceList != null && deviceList.size() > 0){
			switcherAboutDevices.showNext();
		} 
		boolean isLocalConnected = NetConnect.isConnected();
		if(isLocalConnected){
			curLocalServerIP.setText(NetConnect.getServerIP());
			curConnectState.setText(R.string.has_connect_local_server);
			curLocalServerName.setText(NetConnect.getServerName());
			switcherAboutConnDevice.showNext();
			curConnectDevice = NetConnect.getTheConnectedDevice();
		}
		// set listeners
		tvHeadBack.setOnClickListener(this);
		layoutCurConnected.setOnClickListener(this);
		tvArrowProcessCurServer.setOnClickListener(this);
		tvBtnManuallyAdd.setOnClickListener(this);
		tvBtnScan.setOnClickListener(this);
		createConnectDialog();
		createManullyDialog();
	}
	private Handler handler =new Handler(){
		public void handleMessage(android.os.Message msg) {
			int msgWhat = msg.what;
			switch (msgWhat) {
			case MSG_SCAN_END:
				isScanning = false;
				tvBtnScan.setText(R.string.scan_device);
				curLocalServerName.setText(NetConnect.getServerName());
				deviceAdapter.updateDevices(deviceList);
				int childIndex=	switcherAboutDevices.getDisplayedChild();
				setViewSwitcherAnimationMode(switcherAboutDevices, childIndex);
				if(deviceList != null && deviceList.size() > 0){
					if(childIndex==0){ //TextView
						switcherAboutDevices.showNext();
					}
				}else{
					if(childIndex == 1){ // ListView
						switcherAboutDevices.showPrevious();
					}
				}
				break;
			case MSG_CONNECT_END: //连接结束
				int result = msg.arg1;
				dealWithConnectResult(result);
				break;
			}
		};
	};
	 
	@Override
	public void onScanComplete() {
//		deviceList = scanner.getScannedResult();
		deviceList = ScanData.instance().getScanList();
		handler.sendEmptyMessage(MSG_SCAN_END);
	}
	//--------connect dialog------------
	TextView tvSelectedServer;
	EditText editServerPw;
	CheckBox checkBoxShowPw;
	TextView tvBtnCancel;
	TextView tvBtnConnect;
	private Dialog connectDialog = null ;
	private void createConnectDialog(){
		if (connectDialog == null) {
			connectDialog = new Dialog(this, R.style.MyDialog);
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			View v = layoutInflater.inflate(R.layout.local_server_connect_dialog_layout, null);
			tvSelectedServer = (TextView) v.findViewById(R.id.selected_server_name);
			tvBtnCancel  = (TextView) v.findViewById(R.id.tvbtn_cancel_connect);
			tvBtnConnect = (TextView) v.findViewById(R.id.tvbtn_connect);
			editServerPw = (EditText) v.findViewById(R.id.edt_adapter_password);
			checkBoxShowPw = (CheckBox) v.findViewById(R.id.checkbox_showadapter_pw);
			checkBoxShowPw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						editServerPw
								.setTransformationMethod(HideReturnsTransformationMethod
										.getInstance());
					} else {
						editServerPw
								.setTransformationMethod(PasswordTransformationMethod
										.getInstance());
					}
				}
			});
			tvBtnCancel.setOnClickListener(this);
			tvBtnConnect.setOnClickListener(this);
			connectDialog.setContentView(v);
		}
	}
	//--------manully connect dialog------------
		TextView tvBtnRecover;
		EditText editIP01;
		EditText editIP02;
		EditText editIP03;
		EditText editIP04;
		EditText editPassWord;
		EditText editPort;
		CheckBox checkBoxSaveIp;
		TextView tvBtnCommit;
		private Dialog manullyDialog = null ;
		private void createManullyDialog(){
			if (manullyDialog == null) {
				manullyDialog = new Dialog(this, R.style.MyDialog);
				LayoutInflater layoutInflater = LayoutInflater.from(this);
				View v = layoutInflater.inflate(R.layout.manual_input_local_server, null);
				editIP01 = (EditText) v.findViewById(R.id.edt_01);
				editIP02 = (EditText) v.findViewById(R.id.edt_02);
				editIP03 = (EditText) v.findViewById(R.id.edt_03);
				editIP04 = (EditText) v.findViewById(R.id.edt_04);
				
				editIP01.addTextChangedListener(new MyTextWatcher(editIP01, editIP02));
				editIP02.addTextChangedListener(new MyTextWatcher(editIP02, editIP03));
				editIP03.addTextChangedListener(new MyTextWatcher(editIP03, editIP04));
				
				editPort = (EditText) v.findViewById(R.id.edt_port);
				tvBtnRecover = (TextView) v.findViewById(R.id.tvbtn_recover_port);
				editPassWord = (EditText) v.findViewById(R.id.edt_password);
				tvBtnCommit  = (TextView) v.findViewById(R.id.tvbtn_commit_manully);
				checkBoxSaveIp = (CheckBox) v.findViewById(R.id.checkbox_save_ip);
				checkBoxSaveIp.setChecked(preferencesVisiter.readRecord());
				checkBoxSaveIp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						 preferencesVisiter.writeRecord(isChecked);
					}
				});
				tvBtnRecover.setOnClickListener(this);
				tvBtnCommit.setOnClickListener(this);
				manullyDialog.setContentView(v);
				
				String[] inputIp = preferencesVisiter.readInputedIp();
				editIP01.setText(inputIp[0]);
				editIP02.setText(inputIp[1]);
				editIP03.setText(inputIp[2]);
				editIP04.setText(inputIp[3]);
			}
		}
	private String prepareIp =null;
	private String prepareServerName = null;
	private int curSelectPosition = 0;
	//单击扫描到的设备
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(isScanning) return;
		ScanData.ScanInfo selectedDevice = deviceList.get(position);
		if(!selectedDevice.isVersionMatch()){
			//版本不匹配，不能连接
			ToastUtil.topShow(R.string.version_can_not_matching );
			return;
		}
		curSelectPosition = position;
		prepareIp = selectedDevice.getIp();
		prepareServerName = selectedDevice.getName();
		tvSelectedServer .setText(prepareServerName +" ("+prepareIp +")");
		connectDialog.show();
	}
 
	private void setViewSwitcherAnimationMode(ViewSwitcher curSwitcher,int curChildIndex){
		switch (curChildIndex) {
		case 0: //TextView 此时要左出 <-- 右进<-- 
			curSwitcher.setInAnimation(slideRightIn);
			curSwitcher.setOutAnimation(slideLeftOut);
			break;
		case 1: // has data chikld layout, 此时要 左进 --> 右出-->
			curSwitcher.setInAnimation(slideLeftIn);
			curSwitcher.setOutAnimation(slideRightOut);
			break;
		}
	}
	//监听EditText的变化
	private class MyTextWatcher implements TextWatcher{
		
		private EditText edit_current;
		private EditText edit_target;

		public MyTextWatcher(EditText edit_current, EditText edit_after){
			this.edit_current = edit_current;
			this.edit_target = edit_after;
		}
		
		@Override
		public void afterTextChanged(Editable arg0) {
			if(edit_current.getText().toString().length() == 3 ){
				edit_target.requestFocus();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}
	}
	private String manullyPort =null;
	private String manullyPW = null;
	private boolean checkInputInfo(){
		String ip01=editIP01.getText().toString();
		if(null == ip01 || ip01.length() == 0){
			editIP01.requestFocus();
			ToastUtil.topShow(R.string.ip_wrong);
			return false;
		}
		String ip02=editIP02.getText().toString();
		if(null == ip02 || ip02.length() == 0) {
			editIP02.requestFocus();
			ToastUtil.topShow(R.string.ip_wrong );
			return false;
		}
		String ip03=editIP03.getText().toString();
		if(null == ip03 || ip03.length() == 0) {
			editIP03.requestFocus();
			ToastUtil.topShow(R.string.ip_wrong );
			return false;
		}
		String ip04=editIP04.getText().toString();
		if(null == ip04 || ip04.length() == 0) {
			editIP04.requestFocus();
			ToastUtil.topShow(R.string.ip_wrong );
			return false;
		}
//		manullyPW = editPassWord.getText().toString();
//		if(manullyPW == null || manullyPW.length() == 0) {
//			editPassWord.requestFocus();
//			ToastUtil.topShow(R.string.please_input_pw, Gravity.BOTTOM);
//			return false;
//		}
		manullyPort = editPort.getText().toString();
		prepareIp = ip01+"."+ip02+"."+ip03+"."+ip04;
		prepareServerName="unknow";
		return true;
	}
 
	private void dealWithConnectResult(int result){
		 switch(result){
		    case NetConnect.NO_EXCEPTION: //连接成功，
		    	if(!isInManullyConnect){
					ScanData.ScanInfo temp= deviceList.get(curSelectPosition);
					deviceList.remove(curSelectPosition);
					if(curConnectDevice != null){
						curConnectDevice.setDeviceName(NetConnect.getServerName());
						deviceList.add(curConnectDevice);
					}
					curConnectDevice=temp;
					deviceAdapter.updateDevices(deviceList);
				}else{
					isInManullyConnect = false;
				}
					int curChildIndex = switcherAboutConnDevice.getDisplayedChild();
					if(curChildIndex == 0){
						switcherAboutConnDevice.showNext();
					}
				curLocalServerName.setText(prepareServerName);
				curLocalServerIP.setText(prepareIp);
				curConnectState.setText(R.string.has_connect_local_server);
		    	NetConnect.setCurServerName(prepareServerName);
				break;
			//出现SocketTimeoutException	
		    case NetConnect.EXCEPTION_TIMEOUT:
//		    	isTimeout = true ;
//		    	connectDialog.dismiss();
				break;	
			//出现SocketException
		    case NetConnect.EXCEPTION_SOCKET:
//		    	isSocket = true;
//		    	connectDialog.dismiss();
		    	break;
		    case NetConnect.EXCEPTION_OTHER:
//		    	isTimeout = true;
//		    	connectDialog.dismiss();
		    	break;
		    //配对码错误
		    case NetConnect.PASS_WRONG:
//		    	passWrong = true;
//		    	connectDialog.dismiss();
		    	break;
		    //版本不匹配
		    case NetConnect.VERSION_MISMATCH:
//		    	mVersionMismatch = true;
//		    	connectDialog.dismiss();
		    	break;
			}// end switch
		 if(result !=0){
			 NetConnect.setCurServerName(null);
		 }
		 	manullyDialog.dismiss();
			connectDialog.dismiss();
	}
}
