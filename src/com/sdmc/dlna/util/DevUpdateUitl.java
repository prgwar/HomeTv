package com.sdmc.dlna.util;

import hometv.remote.socketUtil.Constant;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sdmc.dlna.service.DevInfo;
import com.sdmc.dlna.service.DeviceItem;
import com.sdmc.phone.util.NetUtil;
import com.sdmc.phone.util.PreferencesVisiter;

/*
 * �����豸�б�Ĺ�����
 */
public class DevUpdateUitl {
	private static DevUpdateUitl devUpdateUitlInstance = new DevUpdateUitl();
	private DevUpdateUitl (){}
	public static DevUpdateUitl getInstance(){
		return devUpdateUitlInstance;
	}
	private  ArrayList<DeviceItem> mDBDevList = new ArrayList<DeviceItem>();
	private   ArrayList<DeviceItem> mPlayerDevList = new ArrayList<DeviceItem>();
	private static DevInfo[] mDevInfoList;
	public static final int MSG_REFRESH_DLNA_DEVICE = 168;
	private static final int DEVICE_DB = 0;
	private static final int DEVICE_PLAYER = 1;
	private static DeviceItem mSelfDB = null;
	private static  Handler uiHandler ;
	public static void setUpdateHandler(Handler curUiHandler){
		uiHandler = curUiHandler;
	}
	// �����豸�б�
	public synchronized void update(DevInfo[] devList) {
		boolean isSame = isEquals(devList);
		if (!isSame) { // �и���
			Message msg = new Message();
			msg.what = MSG_REFRESH_DLNA_DEVICE;
			mDBDevList.clear();
			mPlayerDevList.clear();
			int newListSize = devList.length;
//			PreferencesVisiter visiter = PreferencesVisiter.getVisiter();
//			String savedResUdn = visiter.getPreferInfo(Constant.DLNA_SAVE_RES_DEVICE_UDN, null);
			String selfDbUdn = NetUtil.getSelfDBUdn();
			
			int selfDeviceId = -1;
//			boolean isFindResDevice = false;
			for (int i = 0; i < newListSize; i++) {
				try {
					DevInfo dev = devList [i];
					int devType = dev.tvType;
					switch (devType) {
					case DEVICE_DB: //��Դ�豸
					
//					if(savedResUdn != null && !isFindResDevice){ //��ʾ�����������һ����Ȼ����
//						if(savedResUdn .equals(dev.udn)){ //�ҵ���
//							isFindResDevice = true;
//							msg.arg1 = dev.devNum; //���� ��Դ�豸ID 
//						}
//						else{// δ�ҵ�
//							
//						}
//					}
					 
					if (dev.udn.equals(selfDbUdn)) { // ����DLNA�豸
							selfDeviceId = dev.devNum;
							Log.i("info","the local dlna device's res device id = "+selfDeviceId);
							msg.arg1 = selfDeviceId;
							mSelfDB = new DeviceItem(
									devType,
									dev.devNum, 
									dev.userName, 
									0,
									dev.udn);
//							if(savedResUdn == null){ // û�б������Դ�豸
//								msg.arg1 = dev.devNum;
//								visiter.savePrefer(Constant.DLNA_SAVE_RES_DEVICE_UDN, selfDbUdn);
//							}
						} 
					else { // ����DLNA�豸
							mDBDevList.add(new DeviceItem(
									devType,
									dev.devNum, 
									dev.userName,
									0,
									dev.udn));
						}
						break;
					case DEVICE_PLAYER: //�����豸
						
						if (!dev.udn.equals(NetUtil.getSelfPlayerUdn())) {
							mPlayerDevList.add(new DeviceItem(
									devType, 
									dev.devNum,
									dev.userName,
									0, 
									dev.udn));
						}
						break;
					}
					 
				} catch (Exception e) {
					Log.e("info"," add dlna device failed ");
				}
				if (mSelfDB != null) {
					mDBDevList.add(0, mSelfDB);
					mSelfDB = null;
				}
//				isRefresh = true;
			} // end for
			if(uiHandler != null)
				uiHandler.sendMessage(msg);
		}
	}

	public ArrayList<DeviceItem> getDBDeviceList() {
		ArrayList<DeviceItem > data = new ArrayList<DeviceItem>();
		for(DeviceItem a : mDBDevList){
			data.add(a);
		}
		return data;
	}

	public  ArrayList<DeviceItem> getPlayerDeviceList() {
		ArrayList<DeviceItem > playDevices = new ArrayList<DeviceItem>();
		for(DeviceItem b : mPlayerDevList){
			playDevices.add(b);
		}
		return playDevices;
	}

	private static boolean isEquals(DevInfo[] devList) {
		if (mDevInfoList == null || mDevInfoList.length != devList.length) {
			mDevInfoList = devList.clone();
			return false;
		}
		for (int i = 0; i < devList.length; i++) {
			if (!devList[i].equals(mDevInfoList[i])) {
				mDevInfoList = devList.clone();
				return false;
			}
		}
		return true;
	}
}
