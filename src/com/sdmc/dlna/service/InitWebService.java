package com.sdmc.dlna.service;

import hometv.remote.socketUtil.Constant;

import com.sdmc.phone.util.NetUtil;
import com.sdmc.phone.util.PreferencesVisiter;

/*
 * ��ʼ���ײ����
 */
public class InitWebService {

	public void initJNI() {
	PreferencesVisiter visiter = PreferencesVisiter.getVisiter();	
	String localDlnaName = visiter.getPreferInfo(Constant.LOCAL_DLNA_NAME, "DLNA" );
		 
		// �ı��û��Զ��������
		NativeAccess.setUserName(localDlnaName+" : "+NetUtil.IP, NetUtil.MAC);
		NativeAccess.initUpnp();
		boolean[] mIsShares = visiter.getLocalShareMediaPrefers();
		// ������������ļ����ͣ�
		NativeAccess.setShare(boolean2int(mIsShares[0]),
				boolean2int(mIsShares[1]), boolean2int(mIsShares[2]));
		
	}

	private int boolean2int(boolean isTrue) {
		if (isTrue) {
			return 1;
		} else {
			return 0;
		}
	}
}
