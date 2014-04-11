/*****************************************************************************
 * VLCApplication.java
 *****************************************************************************
 * Copyright @ 2010-2012 VLC authors and VideoLAN
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
package org.videolan.libvlc;

import java.util.ArrayList;

import hometv.remote.bean.NetInterface;
import hometv.remote.socketUtil.ConnectUtil;
import hometv.remote.socketUtil.Constant;
import hometv.remote.socketUtil.ToastUtil;
import sdmc.com.hometv.R;
import sdmc.com.tvlive.jni.UDPModule;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sdmc.dlna.filebrowser.FileItem;
import com.sdmc.dlna.service.DeviceItem;
import com.sdmc.dlna.service.PlayUtil;
import com.sdmc.dtv.programsend.ProgramRunnable;
import com.sdmc.phone.util.NetConnect;
import com.sdmc.phone.util.NetUtil;
import com.sdmc.phone.util.PreferencesVisiter;
import com.sdmc.phone.util.ScanManager;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;

public class VLCApplication extends Application {
	static{
		System.loadLibrary("phone_udp_jni");
		System.loadLibrary("iod");  // run in phone need this library,and must in front of the next 
		System.loadLibrary("sdmc_dlna_jni");
	}
	/**
	 * �ж���ǰ��������ѡ����Ǿ��������ǹ�������
	 * false:������
	 * true: ������
	 */
	public static boolean isInWildNet;
	public static String appCurVersion=null;
	private static VLCApplication instance;
	private ConnectUtil connectUtil;
	private SharedPreferences sp=null;
	private Editor editor=null;
	private String savedServerIp;
	private String savedUserName;
	private String savedPW;
	public  String curServerIP=null;
	public  String curUserName=null;
	public  String curPW=null;
	private NetInterface subClass=null;
	public  boolean isAllowRemotePlay;
	public boolean isAllowToPvr;
	public  boolean isAutoLogin=true;
	public static int screenWidth;
	public static int screenHeight;
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		//************** temp ***********
		ProgramRunnable.instance().setHandler(localHandler);
		UDPModule.callBackInit();
		UDPModule.registerCallBack(new UDPCallbackImp(mHandler));
		PreferencesVisiter.initVisiter(getApplicationContext());
		initImageLoader(getAppContext());
		PlayUtil.setAppContext(getApplicationContext());
		sp = getSharedPreferences(Constant.PREFERENCEINFO, MODE_PRIVATE);
		savedServerIp = sp.getString(Constant.SERVERIP_KEY, null);
        savedUserName = sp.getString(Constant.USERNAME_KEY, null);
        savedPW = sp.getString(Constant.PW_KEY, null);
        isAllowRemotePlay = sp.getBoolean(Constant.REMOTE_PLAY_ALLOW_STATE, true);
        isAllowToPvr = sp.getBoolean(Constant.PVR_ALLOW_STATE, false);
        curServerIP = savedServerIp;
        curUserName = savedUserName;
        curPW = savedPW;
        connectUtil = ConnectUtil.getInstance();
        connectUtil.setHandler(mHandler);
        connectUtil.setLocalHandler(localHandler);
        Log.e("info"," VLCApplication ---> oncreate()   savedServerIp= "+savedServerIp);
        boolean isLocalMode = sp.getBoolean(Constant.ENABLE_LOCAL, false);
        boolean isRemoteMode = sp.getBoolean(Constant.ENABLE_REMOTE, true);
        PreferencesVisiter preferencesVisiter = PreferencesVisiter.getVisiter();
        String lastConnectedLocalIp = preferencesVisiter.readLastIp();
        int lastServerPort = preferencesVisiter.readPort();
        NetUtil mNetUtil = new NetUtil(this);
        
        if(mNetUtil.isNetworkConnected()){ //�������������
        	if(isLocalMode){ //�û�ѡ���˾���������
        		if( lastConnectedLocalIp !=null){
        			//�Զ����Ӿ�����
        			NetConnect connect = new NetConnect(null);
        			connect.inerToConnect(lastConnectedLocalIp, lastServerPort);
        		}
        		else{
        			//�Զ�ɨ��
        			ScanManager manager = new ScanManager(NetUtil.IP,  null);
        			manager.scan();
        		}
        	}
        	//���������Զ���½
        	else if(isRemoteMode && savedServerIp!=null && savedUserName!=null && savedPW!=null){
        		connectUtil.initControlServer(savedServerIp, Constant.PORT,savedUserName,savedPW);
        		Log.e("info","VlcApplication--------  >  onCreate  auto to connect...");
        	}
        }
		ToastUtil.setContext(getApplicationContext());
		DisplayMetrics dm2 = getResources().getDisplayMetrics();  
		screenHeight = dm2.heightPixels;
		screenWidth = dm2.widthPixels;
	}

	public static Context getAppContext() {
		return instance;
	}

	public static Resources getAppResources() {
		return instance.getResources();
	}
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what=msg.what;
//			Log.w("info","  VlcApplication---->  handleMessage   what= "+what +" isAutoLogin = "+isAutoLogin);
			if(what!=Constant.REQUEST_TIME_OUT){
				this.removeMessages(Constant.REQUEST_TIME_OUT);
			}
			switch (what) {
			case Constant.HOST_ERROR://����������
				if(isAutoLogin)break;
				ToastUtil.showToast(R.string.serverip_not_found,Gravity.BOTTOM);
				break;
			case Constant.CONNECT_ERROR: //���Ӵ���
				if(isAutoLogin)break;
				ToastUtil.showToast(R.string.connect_server_fail,Gravity.BOTTOM);
				break;
			case Constant.CONNECT_SUCCESS://���ӳɹ�
				if(!isAutoLogin){
					ToastUtil.showToast(R.string.connect_success_and_logining,Gravity.BOTTOM);
				}
				break;
			case Constant.SERVER_RESPONSE_LOAD: //��½���״̬
				if(isAutoLogin)break;
				int loginState=msg.arg1;
				switch (loginState) {  //0û���豸��1�豸������,2�������3��¼�ɹ�,4�������豸�Ѿ���½
				case 0: //����ʾ���� �û�������
					ToastUtil.showToast(R.string.device_not_found,Gravity.BOTTOM);
					break;
				case 1:
					ToastUtil.showToast(R.string.device_isnot_online);
					break;
				case 2:
					ToastUtil.showToast(R.string.password_error,Gravity.BOTTOM);
					break;
				case 3: //��½�ɹ�,��Ҫ�����ʻ� ��Ϣ
					ToastUtil.showToast(R.string.login_success,Gravity.BOTTOM);
					break;
				case 4: //�������ط��Ѿ���½
					break;
				}
				break;
			}//end switch
			if(subClass!=null) 
			    subClass.handleMsg(msg);
		};
	};
	private Handler localHandler  =new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(subClass !=null){
				subClass.handleLocalMsg(msg);
			}
		};
	};
	public void setCurActivity(NetInterface curActivity){
		subClass=  curActivity;
		isAutoLogin = false;
	}
	public void setUserInfo(String inputServerIp,String inputUser,String inputPw){
		if(inputServerIp==null)return;
//		Log.i("info"," inputServerIp = "+inputServerIp +" curServerIP= "+curServerIP);
//		Log.i("info"," inputUser = "+inputUser +" curUserName= "+curUserName);
//		Log.i("info"," inputPw = "+inputPw +" curPW= "+curPW);
		if(inputServerIp.equals(curServerIP) && inputUser.equals(curUserName) && inputPw.equals(curPW))return;
		if(editor==null){
			editor=sp.edit();
		}
		editor.putString(Constant.SERVERIP_KEY, inputServerIp);
	    editor.putString(Constant.USERNAME_KEY, inputUser);
		editor.putString(Constant.PW_KEY, inputPw);
		if(editor.commit()){
			//ToastUtil.showToast(R.string.saveok,Gravity.BOTTOM);
		}
		this.curPW=inputPw;
		this.curServerIP=inputServerIp;
		this.curUserName=inputUser;
		
	}
	public void changeCurAllowRemoteState(boolean curState){
		if(editor==null){
			editor=sp.edit();
		}
		editor.putBoolean(Constant.REMOTE_PLAY_ALLOW_STATE, curState);
		editor.commit();
		isAllowRemotePlay=curState;
	}
	/**
	 * �ı䵱ǰ �����Ƿ����� Զ�̿���PVR״̬
	 * @param curAllowState
	 */
	public void changeCurAllowPvrState(boolean curAllowState){
		if(editor==null){
			editor=sp.edit();
		}
		editor.putBoolean(Constant.PVR_ALLOW_STATE, curAllowState);
		editor.commit();
		isAllowToPvr=curAllowState;
	}
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.e("info"," Application ---- >  onTerminate()");
	}
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them, 
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.enableLogging() // Not necessary in common
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	
	ArrayList<FileItem> curDlnaPlayList = null;
	public void setCurDlnaPlayList(ArrayList<FileItem> tagetPlayList){
		curDlnaPlayList = tagetPlayList;
	}
	public ArrayList<FileItem> getDlnaPlayList(){
		return curDlnaPlayList;
	}
	ArrayList<DeviceItem> dlnaCanPlayDevices = null;
	public void setCurDlnaPlayDevices(ArrayList<DeviceItem> tagetPlayDevices){
		dlnaCanPlayDevices = tagetPlayDevices;
	}
	public ArrayList<DeviceItem> getDlnaPlayDevices(){
		return dlnaCanPlayDevices;
	}
	/**  the default value = -1 **/
	public int curDlnaPushedDeviceId = -1;
}
