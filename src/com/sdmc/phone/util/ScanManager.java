package com.sdmc.phone.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.sdmc.phone.stat.EventType;
import com.sdmc.phone.stat.ScanData;
import com.sdmc.phone.stat.ScanData.ScanInfo;

/*
 * 扫描可连接的机顶盒
 *  
 */
 
public class ScanManager {
	
	private final static int END_IP = 254;
	
	private int mConnectCount;
	private ExecutorService mExecutorService;
	private PreferencesVisiter mSetting;
	private String mIp;
	private String mLeftIP;
	private int mPort;
	
	private ArrayList<ScanData.ScanInfo> mDeviceList;
	private ArrayList<ScanData.ScanInfo> mUnKnownDeviceList;
	private ScanData mScanData;
	private OnScanCompleteListener mOnScanCompleteListener;
	private Handler mHandler;
	
	private Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			for (ScanData.ScanInfo info : mDeviceList) {
				mScanData.addScanInfo(info);
			}
			for (ScanData.ScanInfo info : mUnKnownDeviceList) {
				mScanData.addScanInfo(info);
			}
			if(mOnScanCompleteListener !=null)
			 mOnScanCompleteListener.onScanComplete();
		}
	};
	
	public ScanManager(String ip,  OnScanCompleteListener onScanCompleteListener){
		mSetting = PreferencesVisiter.getVisiter();
		mIp = ip;
		mPort = mSetting.readPort();
		mScanData = ScanData.instance();
		mDeviceList = new ArrayList<ScanData.ScanInfo>();
		mUnKnownDeviceList = new ArrayList<ScanData.ScanInfo>();
		mOnScanCompleteListener = onScanCompleteListener;
		mHandler = new Handler();
	}
	
	public void scan(){
		mDeviceList.clear();
		mUnKnownDeviceList.clear();
		mScanData.getScanList().clear();
		int i = mIp.lastIndexOf('.');
		mLeftIP = mIp.substring(0, i + 1);
		mExecutorService = Executors.newCachedThreadPool();
		start();
	}
	
	public boolean stop(){
		mExecutorService.shutdownNow();
		return false;
	}
	
	private void start(){
		mConnectCount = 0;
		for(int i = 1; i <= END_IP; i ++){
			mExecutorService.execute(new ConnectRunnable(i));
		}
		mExecutorService.shutdown();
	}
	
	private class ConnectRunnable implements Runnable{
		
		private static final int TIMEOUT = 2000;
		
		private String mIP;
		
		ConnectRunnable(int right){
			this.mIP = mLeftIP + right;
		}
		
		@Override
		public void run() {
			Socket socket = new Socket();
			InetSocketAddress address = new InetSocketAddress(mIP, mPort);
			try {
				socket.connect(address, TIMEOUT);
				send(mIP, socket);
				socket.close();
			} catch (IOException e) {
//				Log.e("info"," tcp create socket occur IOExeption.. ");
			}
			addConnectCount();
		}
	}
	/**
	 * 给要扫描的Ip地址发送消息
	 * @param ip
	 * @param socket
	 */
	private void send(String ip, Socket socket){
		try {
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			byte[] scanBytes = new byte[6];
			scanBytes[0] = EventType.EVENT_SCAN;
			scanBytes[1] = 4;
			BytesMaker.int2bytes(VersionUtil.getVersionCode(), scanBytes, 2);
			out.write(scanBytes);
			int type = in.read();
			if (type == EventType.EVENT_SCAN) {
				int length = in.read();
				byte[] bytes = new byte[length];
				in.read(bytes);
				byte[] nameBytes = new byte[length - 4];
				for (int i = 0; i < nameBytes.length; i ++) {
					nameBytes[i] = bytes[i];
				}
				String name = new String(nameBytes);
				int version = BytesMaker.bytes2int(bytes, nameBytes.length);
				 //?
				String hasConnectedIp = NetConnect.getServerIP();
				if(hasConnectedIp!=null && hasConnectedIp.equals(ip)){
					NetConnect.setCurServerName(name);
				}
				else
					addDevice(new ScanData.ScanInfo(name, ip, version));
			} else {
				addUnKnownDevice(new ScanData.ScanInfo(ip));
			}
			out.write(0);
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
		}
	}
	
	private synchronized void addConnectCount() {
		mConnectCount ++;
		if (mConnectCount >= END_IP) {
			mHandler.postDelayed(mRunnable, 100);
		}
	}
	
	private synchronized void addDevice(ScanData.ScanInfo info) {
		mDeviceList.add(info);
	}
	
	private synchronized void addUnKnownDevice(ScanData.ScanInfo info) {
		mUnKnownDeviceList.add(info);
	}
	
	public static interface OnScanCompleteListener {
		public void onScanComplete();
	}
	
}

