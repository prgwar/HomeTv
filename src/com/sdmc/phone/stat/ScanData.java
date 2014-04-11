package com.sdmc.phone.stat;

import java.util.ArrayList;

import com.sdmc.phone.util.VersionUtil;
/**
 * 
 * @author fdk
 * 记录网络连接的数据
 */
public class ScanData {
	
	private static final String UNKNOWN_NAME = "unknown";
	
	private ArrayList<ScanInfo> mScanInfoList;
	
	private static ScanData mNetData;
	
	private ScanData(){
		mScanInfoList = new ArrayList<ScanInfo>();
	}
	
	public static ScanData instance(){
		if (mNetData == null){
			mNetData = new ScanData();
		}
		return mNetData;
	}

	public synchronized void addScanInfo(ScanInfo scanInfo) {
		mScanInfoList.add(scanInfo);
	}
	
	public ArrayList<ScanInfo> getScanList() {
		return mScanInfoList;
	}
	
	public static class ScanInfo {
		private String mName;
		private String mIp;
		private int mVersion;
		private boolean mIsVersionMatch;
		private boolean isConnected;
		public ScanInfo(String name, String ip, int version) {
			mName = name;
			mIp = ip;
			mVersion = version;
			mIsVersionMatch = VersionUtil.getVersionCode() == mVersion;
		}
		
		public ScanInfo(String ip) {
			mName = UNKNOWN_NAME;
			mIp = ip;
			mVersion = 0;
			mIsVersionMatch = VersionUtil.getVersionCode() == mVersion;
		}
		
		public String getName() {
			return mName;
		}
		
		public String getIp() {
			return mIp;
		}

		public int getVersion() {
			return mVersion;
		}
		
		public boolean isVersionMatch() {
			return mIsVersionMatch;
		}

		public boolean isConnected() {
			return isConnected;
		}

		public void setConnected(boolean isConnected) {
			this.isConnected = isConnected;
		}
		public void setDeviceName(String deviceName){
			mName = deviceName;
		}
	}
}
