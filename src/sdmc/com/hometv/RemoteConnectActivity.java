package sdmc.com.hometv;

import org.videolan.libvlc.VLCApplication;
import hometv.remote.bean.NetInterface;
import hometv.remote.socketUtil.ConnectUtil;
import hometv.remote.socketUtil.Constant;
import hometv.remote.socketUtil.ToastUtil;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * 远程连接界面
 * @author fee
 *
 */
public class RemoteConnectActivity extends Activity implements 
			OnClickListener,NetInterface{
	private static final String TAG="RemoteConnectActivity";
	private TextView tvHeadBack;
	private TextView tvHeadCurTab;
	private EditText edtServer;
	private EditText edtUserName;
	private EditText edtPassword;
	private CheckBox isShowPassword;                      
	private TextView tvBtnLogin;
	private ProgressBar progressBar;
	private ConnectUtil connectUtil;
	private VLCApplication app;
	private String btnText_connect = null, btnText_exit_connect = null;
	private LinearLayout loginLayout;
	private Animation enterAnimation = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   app = (VLCApplication) getApplication();
	   setContentView(R.layout.connect_remote_xml);
	   Resources res = getResources();
	   btnText_connect = res.getString(R.string.remote_to_connect);
	   btnText_exit_connect = res.getString(R.string.remote_to_disconnect);
	   connectUtil = ConnectUtil.getInstance();
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
	app.setCurActivity(this);
	if (ConnectUtil.login_success) {
		tvBtnLogin.setText(R.string.remote_to_disconnect);
		edtPassword.setText(app.curPW);
	} else {
		tvBtnLogin.setText(R.string.remote_to_connect);
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
		case R.id.tvbtn_login: //登陆连接、断开连接
			String curBtnText = tvBtnLogin.getText().toString();
			if(btnText_connect.equals(curBtnText)){ //连接
				if (!checkLoginInfo()) {// 验证输入状况不通过
					break;
				}
				if (ConnectUtil.login_success) {// 如果已经登陆成功了，要重新连接
//					Log.w("info"," has logined  serverIp=  "   +serverIP +" app.curServerIp= "+app.curServerIP);
//					Log.w("info"," has logined  strUserName= "+strUserName +" app.curUserName= "+app.curUserName);
//					Log.w("info"," has logined  strPassWord= "+strPassWord +" app.curPW= "+app.curPW);
					if (serverIP.equals(app.curServerIP)
							 && strUserName.equals(app.curUserName)
							  ) {
						// 已登陆上 修改信息时，同IP、同用户 这样无需重连
						ToastUtil.showToast(R.string.forbit_to_re_login,Gravity.BOTTOM);
						onResume();
						break;
					}
				}
				connectUtil.release(false);
				progressBar.setVisibility(View.VISIBLE);
				connectUtil.initControlServer(serverIP, Constant.PORT,strUserName,strPassWord);
			}
			else if(btnText_exit_connect.equals(curBtnText)){ //断开连接
				connectUtil.release(false);
				tvBtnLogin.setText(btnText_connect);
			}
			break;
		}
	}
	private void initViews(){
		tvHeadBack = (TextView) findViewById(R.id.head_back);
		tvHeadCurTab = (TextView) findViewById(R.id.head_current_tab);
		tvHeadCurTab.setText(R.string.remote_connect);
		loginLayout = (LinearLayout) findViewById(R.id.user_login_layout);
		enterAnimation = AnimationUtils.loadAnimation(this,R.anim.login_layout_enter);
		loginLayout.startAnimation(enterAnimation);
		edtServer = (EditText ) findViewById(R.id.serverName);
		edtUserName = (EditText) findViewById(R.id.username);
		edtPassword = (EditText) findViewById(R.id.userPassWord);
		isShowPassword = (CheckBox) findViewById(R.id.checkbox_showpw);
		tvBtnLogin = (TextView) findViewById(R.id.tvbtn_login);
		progressBar = (ProgressBar) findViewById(R.id.loading_progress);
		
		// set listeners
		tvHeadBack.setOnClickListener(this);
		tvBtnLogin.setOnClickListener(this);

		isShowPassword
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							edtPassword
									.setTransformationMethod(HideReturnsTransformationMethod
											.getInstance());
						} else {
							edtPassword
									.setTransformationMethod(PasswordTransformationMethod
											.getInstance());
						}
					}
				});
		String savedServer = app.curServerIP;
		String savedUserName = app.curUserName;
		String savedPW = app.curPW;
		if (savedServer != null && savedPW != null && savedUserName != null) {
			edtServer.setText(savedServer);
			edtPassword.setText(savedPW);
			edtUserName.setText(savedUserName);
		}
	}
	private String serverIP = null, strUserName = null, strPassWord = null;

	private boolean checkLoginInfo() {
		serverIP = edtServer.getText().toString().trim();
		if (serverIP == null || "".equals(serverIP)) {
			ToastUtil.showToast(R.string.serverip_cannot_null,Gravity.BOTTOM);
			return false;
		}
		strUserName = edtUserName.getText().toString().trim();
		if (strUserName == null || "".equals(strUserName)) {
			ToastUtil.showToast(R.string.username_cannot_null,Gravity.BOTTOM);
			return false;
		}
		byte[] nameByte = strUserName.getBytes(); // 一个中文在UTF-8下为3个字节
		int byteLen = nameByte.length;
		if (byteLen > 8) {
			ToastUtil.showToast(R.string.username_is_too_long,Gravity.BOTTOM);
			return false;
		}
		strPassWord = edtPassword.getText().toString().trim();
		if (strPassWord == null || "".equals(strPassWord)
				|| strPassWord.length() != 6) {
			ToastUtil.showToast(R.string.password_cannot_null,Gravity.BOTTOM);
			return false;
		}
		return true;
	}
	@Override
	public void handleMsg(Message msg) {
		int what = msg.what;
		Log.i("info", "RemoteConnectActivity" + " : handleMsg ---> " + what + " msg.arg1= " + msg.arg1);
	  switch (what) {
		case Constant.HOST_ERROR:// 服务器错误
			onResume();
			break;
		case Constant.CONNECT_ERROR: // 连接错误
			onResume();
			break;
		case Constant.CONNECT_SUCCESS:// 连接成功,connectUtil会自动去登陆
			break;
		case Constant.CONNECT_BREAK: //连接断开
			onResume();
			break;
		case Constant.SERVER_RESPONSE_LOAD: // 登陆后的状态
			int loginState = msg.arg1;
			switch (loginState) { // 0没有设备，1设备不在线,2密码错误，3登录成功,4在其他设备已经登陆
			case 0: // 即表示输入 用户名错误
				break;
			case 1:
//				ToastUtil.showToast(R.string.device_isnot_online,Gravity.BOTTOM);
				break;
			case 2:
				break;
			case 3: // 登陆成功,则要保存帐户 信息
				app.setUserInfo(serverIP, strUserName, strPassWord);
				onResume();
				break;
			case 4: // 在其他地方已经登陆
				ToastUtil.showToast(R.string.userlogin_in_other_device,Gravity.BOTTOM);
				break;
			}
			break;
		case Constant.REQUEST_TIME_OUT://请求超时
			ToastUtil.showToast(R.string.request_timeup,Gravity.BOTTOM);
			onResume();
			break;
		}// end switch
		if (what != Constant.CONNECT_SUCCESS) {
			progressBar.setVisibility(View.GONE);
		}
	}
	@Override
	public void handleLocalMsg(Message msg) {
		 
		
	}
}
