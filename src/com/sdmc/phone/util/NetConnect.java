package com.sdmc.phone.util;

import hometv.remote.bean.NetModeManager;
import hometv.remote.bean.ProtecolLocalImp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sdmc.dtv.programsend.ProgramRunnable;
import com.sdmc.phone.stat.EventType;
import com.sdmc.phone.stat.ScanData;
import com.sdmc.phone.stat.ScanData.ScanInfo;
//建立TCP连接
public class NetConnect {
	private static Socket mSocket;
	public static OutputStream mOut;
	public static InputStream mIn;
	
	public final static int NO_EXCEPTION = 0;
	public final static int EXCEPTION_TIMEOUT = 1;
	public final static int EXCEPTION_SOCKET = 2;
	public final static int EXCEPTION_OTHER = 3;
	public final static int PASS_WRONG = 4;
	public final static int VERSION_MISMATCH = 5;
	
	private final static int DEFAUT_TIME = 5000;
	
	private static String mLastIP = null;
	private static int mLastPort;
	private static boolean mIsBindDtv;
	public  static boolean mIsLocalConnected = false;
	
	private Handler uiHandler;
	public NetConnect(Handler handler) {
		this.uiHandler = handler;
	}

	
	public static void close(){
		Log.i("PhoneClient", "close");
		mIsLocalConnected = false;
		try {
			if(mIn != null){
				Log.i("PhoneClient", "mIn");
				mIn.close();
			}
			if(mOut != null){
				Log.i("PhoneClient", "mOut");
				mOut.close();
			}
			if(mSocket != null){
				Log.i("PhoneClient", "mSocket");
				mSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void sendEnd(){
		try {
			if(mOut != null){
				mOut.write(EventType.EVENT_END);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static byte[] findBytes(String pass){
		byte[] temp = pass.getBytes();
		byte[] bytes = new byte[temp.length + 2];
		bytes[0] = EventType.EVENT_PASS;
		bytes[1] = (byte) temp.length;
		for(int i = 0; i < temp.length; i ++){
			bytes[i + 2] = temp[i];
		}
		return bytes;
	}
	
	public static final String getServerIP() {
		return mLastIP;
	}
	
	public static boolean isBindDtv() {
		return mIsBindDtv;
	}
	
	public static boolean isConnected() {
		return mIsLocalConnected;
	}
	private static String curServerName = null;
	public static void setCurServerName(String curServer){
		curServerName = curServer;
	}
	public static String getServerName(){
		return curServerName;
	}
	public static ScanData.ScanInfo getTheConnectedDevice(){
		if(mLastIP == null){
			return null;
		}
		if(curServerName == null) curServerName = "unknow";
		ScanInfo connectedDevice = new ScanInfo(curServerName, mLastIP, 200);
		return connectedDevice;
	}
	public  void inerToConnect(String curIp,int port){
		if(isConnecting) return;
		connectRunnable = new ConnectRunnable(curIp, port);
		new Thread(connectRunnable).start();
	}
	private static boolean isConnecting = false;
	private synchronized void setIsRunningFlag(boolean isRunning){
		isConnecting = isRunning;
	}
	private static ConnectRunnable connectRunnable =null;
	private class ConnectRunnable implements Runnable{
		String targetIp;
		int targetPort;
		int connectTimes = 0;
		InetSocketAddress address; 
		public ConnectRunnable(String ip,int port) {
			this.targetIp = ip;
			this.targetPort = port;
		}
		@Override
		public void run() {
			int result = -1;
			address= new InetSocketAddress(targetIp, targetPort);
			setIsRunningFlag(true);
			while(connectTimes <= 3){
				mSocket = new Socket();
				try {
					mSocket.connect(address, DEFAUT_TIME);
					mOut = mSocket.getOutputStream();
					mIn = mSocket.getInputStream();
					//验证版本
					byte[] passbyte = new byte[6];
					passbyte[0] = EventType.EVENT_PASS_VERSION;
					passbyte[1] = 4;
					BytesMaker.int2bytes(VersionUtil.getVersionCode(), passbyte, 2);
					
					mOut.write(passbyte);
					switch (mIn.read()) {
						case EventType.EVENT_BINDDTV_SUCCESS:
							mIsBindDtv = true;
							break;
						case EventType.EVENT_BINDDTV_FALSE:
							mIsBindDtv = false;
							break;
						case EventType.EVENT_VERSION_MISMATCH:
							close();
							result = VERSION_MISMATCH;
							break;
							
						default:
							close();
							result = EXCEPTION_OTHER;
							break;
					}
					if(result == -1){ //即可以表示 连接成功,未进入 上面两个return 情况
						mLastIP = targetIp;
						mLastPort = targetPort;
						mIsLocalConnected = true;
						PreferencesVisiter .getVisiter().writeLastIp(mLastIP);
						PreferencesVisiter .getVisiter().writePort(mLastPort);
						Log.e("info","local tcp connect suc..............");
						ProgramRunnable.init();
						result = NO_EXCEPTION;
						//连接成功即自动切换当前网络模式
						NetModeManager.getNetModeManager().switchCurNetMode(new ProtecolLocalImp());
						break; //退出while
					}
				} catch (UnknownHostException e) {
					mIsLocalConnected = false;
					result = EXCEPTION_OTHER;
					break; //退出while
				} catch(SocketTimeoutException e){
					mIsLocalConnected = false;
					result = EXCEPTION_TIMEOUT;
				} catch(SocketException e){
					mIsLocalConnected = false;
					result = EXCEPTION_SOCKET;
				} catch (IOException e) {
					mIsLocalConnected = false;
					result = EXCEPTION_OTHER;
				} 
				
				connectTimes++; 
			}//end while
			if(!mIsLocalConnected){
				setCurServerName(null);
			}
			if(uiHandler != null){
				Message toUiMsg = new Message();
				toUiMsg.what = 100;
				toUiMsg.arg1 = result;
				Log.e("info","local tcp connect result..====..."+result);
				uiHandler.sendMessage(toUiMsg);
			}
			setIsRunningFlag(false);
			//run end
		}
		
	}
}
//public static int connect(String ip ,int port, int time){
//int result = -1;
//mSocket = new Socket();
//InetSocketAddress address = new InetSocketAddress(ip, port);
//try {
//	mSocket.connect(address, time);
//	mOut = mSocket.getOutputStream();
//	mIn = mSocket.getInputStream();
//	//验证版本
//	byte[] passbyte = new byte[6];
//	passbyte[0] = EventType.EVENT_PASS_VERSION;
//	passbyte[1] = 4;
//	BytesMaker.int2bytes(VersionUtil.getVersionCode(), passbyte, 2);
//	
//	mOut.write(passbyte);
//	switch (mIn.read()) {
//		case EventType.EVENT_BINDDTV_SUCCESS:
//			mIsBindDtv = true;
//			break;
//		case EventType.EVENT_BINDDTV_FALSE:
//			mIsBindDtv = false;
//			break;
//		case EventType.EVENT_VERSION_MISMATCH:
//			close();
//			result = VERSION_MISMATCH;
////			return VERSION_MISMATCH;
//			break;
//			
//		default:
//			close();
//			result = EXCEPTION_OTHER;
////			return EXCEPTION_OTHER;
//			break;
//	}
//	if(result == -1){ //即可以表示 连接成功,未进入 上面两个return 情况
//		mLastIP = ip;
//		mLastPort = port;
//		mIsLocalConnected = true;
//		PreferencesVisiter .getVisiter().writeLastIp(mLastIP);
//		PreferencesVisiter .getVisiter().writePort(mLastPort);
//		Log.e("info","local tcp connect suc..............");
//		ProgramRunnable.init();
//		result = NO_EXCEPTION;
//	}
////	return NO_EXCEPTION;
//} catch (UnknownHostException e) {
//	mIsLocalConnected = false;
//	result = EXCEPTION_OTHER;
//} catch(SocketTimeoutException e){
//	mIsLocalConnected = false;
//	result = EXCEPTION_TIMEOUT;
//} catch(SocketException e){
//	mIsLocalConnected = false;
//	result = EXCEPTION_SOCKET;
//} catch (IOException e) {
//	mIsLocalConnected = false;
//	result = EXCEPTION_OTHER;
//} 
//if(!mIsLocalConnected){
//	setCurServerName(null);
//}
//return result;
//}
///**
//* 如果连接成功，则自动读取节目列表
//* @param ip
//* @param port
//* @return
//*/
//public static int connet(String ip ,int port){
//return connect(ip, port, DEFAUT_TIME);
//}

//public static int reConnet(int time){
//return connect(mLastIP, mLastPort, time);
//}
