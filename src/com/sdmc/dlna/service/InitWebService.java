package com.sdmc.dlna.service;

import hometv.remote.socketUtil.Constant;

import com.sdmc.phone.util.NetUtil;
import com.sdmc.phone.util.PreferencesVisiter;

/*
 * 初始化底层服务
 */
public class InitWebService {

	public void initJNI() {
	PreferencesVisiter visiter = PreferencesVisiter.getVisiter();	
	String localDlnaName = visiter.getPreferInfo(Constant.LOCAL_DLNA_NAME, "DLNA" );
		 
		// 改变用户自定义的名字
		NativeAccess.setUserName(localDlnaName+" : "+NetUtil.IP, NetUtil.MAC);
		NativeAccess.initUpnp();
		boolean[] mIsShares = visiter.getLocalShareMediaPrefers();
		// 设置允许共享的文件类型：
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
