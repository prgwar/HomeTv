package com.sdmc.phone.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class VersionUtil {
	
	private static int mVersionCode;
	private static String mVersionName;
	/**
	 * 获取代码版本号
	 * @param context
	 */
	public static void init(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			mVersionCode =  packInfo.versionCode;
			mVersionName = packInfo.versionName;
		} catch (NameNotFoundException e) {
			mVersionCode = 200;
			mVersionName = "1.2";
			e.printStackTrace();
		}
	}
	
	public static int getVersionCode() {
        return mVersionCode;
	}
	public static String getVersionName(){
		return mVersionName;
	}
}
