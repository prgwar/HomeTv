package sdmc.com.hometv;

import hometv.remote.bean.UdpScanCompleteListener;

import com.sdmc.dlna.service.ControlService;
import com.sdmc.phone.util.NetConnect;
import com.sdmc.phone.util.NetUtil;
import com.sdmc.phone.util.PreferencesVisiter;
import com.sdmc.phone.util.ScanLocalServerByUDP;
import com.sdmc.phone.util.ScanManager;
import com.sdmc.phone.util.VersionUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher.ViewFactory;

/**
 * 
 * @author fdk
 * 欢迎界面
 *
 */

public class WelcomeActivity extends Activity implements ViewFactory{

	private static final int WIFI_NOT_OPEN = 0;
	private static final int CANT_FIND_IP = 1;
	private static final int MSG_CONNECT = 0x301;
	private static final int MSG_SCAN = 0x302;
	private static final int MSG_CHANGE_IMGAE = 0x310;
	private static final int TATAL_SHOWTIME = 2000;
	private static final int SHOWTIME = 800;
	
	private static final int[] IMAGES = {R.drawable.bg_welcome1, R.drawable.bg_welcome2};
	
	private String mIp;
	//private boolean mRemember;
	private int mPort;
	private long mTime;
	
	private NetUtil mNetUtil;
	private PreferencesVisiter mSetting;
//	private ScanManager mScanManager;
	private ImageSwitcher mImageSwitcher;
	
	private Handler mChangeImageHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			int msgWhat=msg.what;
			switch (msgWhat) {
			case MSG_CHANGE_IMGAE:
				mImageSwitcher.setImageResource(IMAGES[1]);
				sendEmptyMessageDelayed(MSG_SCAN, 2000);
				break;
			case MSG_SCAN:
				Intent toGuide=new Intent();
				toGuide.setClass(WelcomeActivity.this, GuideActivity.class);
				startActivity(toGuide);
				WelcomeActivity.this.finish();
				break;
			}
		}
	};
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_welcome);
		mTime = System.currentTimeMillis();
		getInfo();
//		mScanManager = new ScanManager(NetUtil.IP, this, mOnScanCompleteListener);

//		 判断wifi及网络的状态
//		if (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED){
//			wrongInfoDialogMake(WIFI_NOT_OPEN).show();
//		} else if (!mNetUtil.isNetworkUseable()){
//			wrongInfoDialogMake(CANT_FIND_IP).show();
//		} else {
//			
//		}
		
//		runInBack();
//		ScanLocalServerByUDP.getUDPScanner().setOnScanCompleteListener(mScanCompleteListener);
//		ScanLocalServerByUDP.getUDPScanner().startToScanServer();
		showImage();
		ControlService.startControlService(this);
	}
	
	private void getInfo() {
		VersionUtil.init(this);
		mNetUtil = new NetUtil(this);
	}
	private void showImage(){
		mImageSwitcher = (ImageSwitcher) findViewById(R.id.switcher_welcome);
		mImageSwitcher.setFactory(this);
		Animation animIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
		animIn.setDuration(700);
		Animation animOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
		animOut.setDuration(700);
		mImageSwitcher.setInAnimation(animIn);
		mImageSwitcher.setOutAnimation(animOut);
		mImageSwitcher.setImageResource(IMAGES[0]);
		Message msg = new Message();
		msg.what = MSG_CHANGE_IMGAE;
		mChangeImageHandler.sendMessageDelayed(msg, SHOWTIME);
	}
	
	@Override
	public View makeView() {
		ImageView view = new ImageView(this);
		view.setScaleType(ScaleType.FIT_XY);
		view.setLayoutParams(new ImageSwitcher.LayoutParams(
		    LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return view;
	}
	
	
	//错误信息提示对话框
	private AlertDialog wrongInfoDialogMake(int msg){
		Builder builder = new AlertDialog.Builder(this);
		switch(msg){
			//wifi未开启
			case WIFI_NOT_OPEN:
//				builder.setTitle(R.string.wifinotOpen);
//				builder.setMessage(R.string.wifinotOpenMassage);
				break;
			//未连接到网络中
			case CANT_FIND_IP:
//				builder.setMessage(R.string.nonet);
				break;
		}
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				WelcomeActivity.this.finish();
			}
		});
		return builder.create();
	}
	
	private ScanManager.OnScanCompleteListener mOnScanCompleteListener = new ScanManager.OnScanCompleteListener() {
		
		@Override
		public void onScanComplete() {
			Log.e("info","onScanComplete().......");
			Message msg = new Message();
	    	msg.what = MSG_SCAN;
	    	//为了确保欢迎界面画面切换有2000毫秒
	    	long time = TATAL_SHOWTIME + mTime - System.currentTimeMillis();
			if (time > 0){
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			mChangeImageHandler.sendMessage(msg);
		}
	};
	private UdpScanCompleteListener mScanCompleteListener=new UdpScanCompleteListener() {
		
		@Override
		public void onScanComplete() {
			Log.e("info","udp --> onScanComplete().......");
			Message msg = new Message();
	    	msg.what = MSG_SCAN;
	    	//为了确保欢迎界面画面切换有2000毫秒
//	    	long time = TATAL_SHOWTIME + mTime - System.currentTimeMillis();
//			if (time > 0){
//				try {
//					Thread.sleep(time);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
			mChangeImageHandler.sendMessage(msg);
		}
	};
}
