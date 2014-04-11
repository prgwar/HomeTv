package com.sdmc.dlna.service;

import sdmc.com.hometv.DlnaPlayerActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sdmc.dlna.service.NativeAccess;
import com.sdmc.dlna.service.PositionInfo;

/**
 * 播放工具类
 */
public class PlayUtil {

	private static PositionInfo mPreInfo = null;
 
     private static Context appContext;
     public static void setAppContext (Context mContext){
    	 appContext = mContext;
     }
	/*
	 * //本地播放 public static void play(int type, String path){ switch(type){ case
	 * Const.FILETYPE_VIDEO: case Const.FILETYPE_MUSIC: playInLocal(path, type);
	 * break; case Const.FILETYPE_IMAGE: playImageInLocal(path); break; default:
	 * break; } } //在其他设备上播放 public static void play(int type, String path, int
	 * deviceID){ switch(type){ case Const.FILETYPE_VIDEO: case
	 * Const.FILETYPE_MUSIC: playInOther(deviceID, path, type); break; case
	 * Const.FILETYPE_IMAGE: playImageInOther(deviceID, path); break; default:
	 * break; } }
	 */
	/**   本地播放  先启动 播放界面 **/
	public static void waitPlay() {
		intentTo(DlnaPlayerActivity.class);
	}

	// 本地播放
	public static void playInLocal(String path, int type, boolean isLocalFile,int curPlayPosition) {
		intentTo(DlnaPlayerActivity.class, path, type, isLocalFile,curPlayPosition);
	}

	/**
	 *  使用其他DLNA 设备进行播放,此时本地只负责控制
	 * @param dlnaDeviceId  DLNA 设备ID
	 * @param path    文件资源 路径
	 * @param fileType  文件资源类型
	 * @param  justUseControl
	 */
	public static void playInOther(int dlnaDeviceId, String path, int fileType ,int curPlayPosition,boolean justUseControl) {
		intentTo(DlnaPlayerActivity.class, path, dlnaDeviceId, fileType,curPlayPosition,justUseControl);
		NativeAccess.playPic(dlnaDeviceId, path);
	}

	/*
	 * public static void playImageInLocal(String path){
	 * IntentUtil.intentTo(MyPhotoPlayer.class, path); }
	 * 
	 * public static void playImageInOther(int devnum, String path){
	 * IntentUtil.intentTo(PhotoPlayerController.class, path, devnum,
	 * Const.FILETYPE_IMAGE); NativeAccess.playPic(devnum, path); }
	 */

	public static void setPosition(PositionInfo positionInfo) {
		mPreInfo = positionInfo;
	}

	public static PositionInfo getPosition() {
		return mPreInfo;
	}

	public static PositionInfo sendPosition() {
		if (mPreInfo == null) {
			return new PositionInfo();
		}
		return mPreInfo;
	}
	public static final String PATH = "path";
	public static final String DEVICEID = "deviceId";
	public static final String FILEINDEX = "fileIndex";
	public static final String FILETYPE = "fileType";
	public static final String DLNA_JUST_CONTROL = "justcontrol";
	public static final String TARGET_PLAY_POSITION = "playposition";

//	private void intentTo(Class<?> mClass, String path) {
//		Intent intent = new Intent(appContext, mClass);
//		intent.putExtra(PATH, path);
//		appContext.startActivity(intent);
//	}

	private static void intentTo(Class<?> mClass, String path, int deviceId,
			int fileType,int curPlayIndex,boolean justControl) {
		Intent intent = new Intent(appContext, mClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(PATH, path);
		intent.putExtra(DEVICEID, deviceId);
		intent.putExtra(FILETYPE, fileType);
		intent.putExtra(TARGET_PLAY_POSITION, curPlayIndex);
		intent.putExtra(DLNA_JUST_CONTROL, justControl);
		appContext.startActivity(intent);
	}

	private static void intentTo(Class<?> mClass, String path, int fileType, boolean isLocalFile,int curPlayPosition) {
		Intent intent = new Intent(appContext, mClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(PATH, path);
		intent.putExtra(FILETYPE, fileType);
		intent.putExtra(DLNA_JUST_CONTROL, isLocalFile);
		intent.putExtra(TARGET_PLAY_POSITION, curPlayPosition);
		appContext.startActivity(intent);
	}

	private static void intentTo(Class<?> mClass) {
		Intent intent = new Intent(appContext, mClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		appContext.startActivity(intent);
	}

//	private void intentTo(Context context, Class<?> mClass) {
//		Intent intent = new Intent(context, mClass);
//		context.startActivity(intent);
//	}
//
//	private void intentTo(Class<?> mClass, Bundle bundle) {
//		Context context = appContext;
//		Intent intent = new Intent(context, mClass);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.putExtras(bundle);
//		context.startActivity(intent);
//	}
	 
}
