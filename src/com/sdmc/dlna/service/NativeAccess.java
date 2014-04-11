package com.sdmc.dlna.service;

import java.util.ArrayList;

/**
 * 本地方法
 * 
 * @author Ke Chow
 * @date 2012-08-24
 */
public class NativeAccess {
	/**
	 * Set UserName username:用户名，udn：唯一标示符。 本地方法
	 */
	public static native int setUserName(String username, String udn);

	/** init & open the dlna **/
	public static native int initUpnp();

	/**
	 * 播放，与暂停相对应：
	 * @param dlnaPlayDeviceId  DLNA播放设备编号
	 * @param noUsedMediaPath   path为无用参数
	 * @return
	 */
	public static native int play(int dlnaPlayDeviceId, String noUsedMediaPath);

	/**
	 *  停止：设备编号
	 * @param devnum
	 * @return
	 */
	public static native int stop(int devnum);

	/**
	 *  暂停：设备编号
	 * @param devnum
	 * @return
	 */
	public static native int pause(int devnum);

	/**
	 *  设置播放进度：设备编号+进度条位置
	 * @param devnum
	 * @param speed
	 * @return
	 */
	public static native int setSpeed(int devnum, int speed);

	// 增加播放速度，每次+1：传入设备编号
	public static native int speedUp(int devnum);

	// 减少播放速度，每次-1：传入设备编号
	public static native int speedDown(int devnum);

	/**
	 *  设置音量：设备编号+预设置音量
	 * @param devnum
	 * @param volume
	 * @return
	 */
	public static native int setVolume(int devnum, int volume);

	/**
	 *  增加音量：设备编号
	 * @param devnum
	 * @return
	 */
	public static native int volumeUp(int devnum);

	/**
	 * 减少音量：设备编号
	 * @param devnum
	 * @return
	 */
	public static native int volumeDown(int devnum);

	/** 播放视频、音频、及图片 文件接口：设备编号+播放文件路径 **/
	public static native int playPic(int dlnaDeviceId, String path);

	// 请求文件列表
	/*
	 * devnum: 设备号. ObjectID: 文件节点ID. BrowseFlag: 总设为"BrowseDirectChildren".
	 * Filter: 总设为"*". StartingIndex: 总设为0. RequestedCount: 希望取得的节点个数，取0为全部获取.
	 * SortCriteria: 总设为"*".
	 */
	public static native int search(int devnum, int objectID,
			String BrowseFlag, String Filter, int StartingIndex,
			int RequestedCount, String SortCriteria);

	public static int searchAllFileList(int devnum, int objectID) {
		return search(devnum, objectID, "BrowseDirectChildren", "*", 0, 0, "*");
	}

	/** 控制端查询播放器当前播放状态：**/
	public static native int getPosition(int devnum, int InstanceID);

	/**
	 * 开关共享,0表示关闭，1表示打开
	 */
	public static native int setShare(int videoShare, int musicShare, int picShare);

	/** 杀掉进程 **/
	public static native int reinit();

	/** 重新生成文件列表 **/
	public static native int reinitFs();

	// 获取URL包含的信息
	public static native String[] getMediaMsg(String filePath, String artist,
			String title);
	public static native int createFileNodS(byte[] fileType,ArrayList<String> filePath);
//	static {
//		/*
//		 * System.loadLibrary("avcodec"); System.loadLibrary("avformat");
//		 * System.loadLibrary("avutil"); System.loadLibrary("iod");
//		 */
//
//		System.loadLibrary("iod");  // run in phone need this library,and must in front of the next 
//		System.loadLibrary("sdmc_dlna_jni");
//	}
}
