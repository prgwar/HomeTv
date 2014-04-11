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
 * Զ�����ӽ���
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
		case R.id.tvbtn_login: //��½���ӡ��Ͽ�����
			String curBtnText = tvBtnLogin.getText().toString();
			if(btnText_connect.equals(curBtnText)){ //����
				if (!checkLoginInfo()) {// ��֤����״����ͨ��
					break;
				}
				if (ConnectUtil.login_success) {// ����Ѿ���½�ɹ��ˣ�Ҫ��������
//					Log.w("info"," has logined  serverIp=  "   +serverIP +" app.curServerIp= "+app.curServerIP);
//					Log.w("info"," has logined  strUserName= "+strUserName +" app.curUserName= "+app.curUserName);
//					Log.w("info"," has logined  strPassWord= "+strPassWord +" app.curPW= "+app.curPW);
					if (serverIP.equals(app.curServerIP)
							 && strUserName.equals(app.curUserName)
							  ) {
						// �ѵ�½�� �޸���Ϣʱ��ͬIP��ͬ�û� ������������
						ToastUtil.showToast(R.string.forbit_to_re_login,Gravity.BOTTOM);
						onResume();
						break;
					}
				}
				connectUtil.release(false);
				progressBar.setVisibility(View.VISIBLE);
				connectUtil.initControlServer(serverIP, Constant.PORT,strUserName,strPassWord);
			}
			else if(btnText_exit_connect.equals(curBtnText)){ //�Ͽ�����
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
		byte[] nameByte = strUserName.getBytes(); // һ��������UTF-8��Ϊ3���ֽ�
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
		case Constant.HOST_ERROR:// ����������
			onResume();
			break;
		case Constant.CONNECT_ERROR: // ���Ӵ���
			onResume();
			break;
		case Constant.CONNECT_SUCCESS:// ���ӳɹ�,connectUtil���Զ�ȥ��½
			break;
		case Constant.CONNECT_BREAK: //���ӶϿ�
			onResume();
			break;
		case Constant.SERVER_RESPONSE_LOAD: // ��½���״̬
			int loginState = msg.arg1;
			switch (loginState) { // 0û���豸��1�豸������,2�������3��¼�ɹ�,4�������豸�Ѿ���½
			case 0: // ����ʾ���� �û�������
				break;
			case 1:
//				ToastUtil.showToast(R.string.device_isnot_online,Gravity.BOTTOM);
				break;
			case 2:
				break;
			case 3: // ��½�ɹ�,��Ҫ�����ʻ� ��Ϣ
				app.setUserInfo(serverIP, strUserName, strPassWord);
				onResume();
				break;
			case 4: // �������ط��Ѿ���½
				ToastUtil.showToast(R.string.userlogin_in_other_device,Gravity.BOTTOM);
				break;
			}
			break;
		case Constant.REQUEST_TIME_OUT://����ʱ
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
