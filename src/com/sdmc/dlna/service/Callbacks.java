package com.sdmc.dlna.service;

import com.sdmc.dlna.filebrowser.FileUpdateUtil;
import com.sdmc.dlna.util.DevUpdateUitl;
import com.sdmc.dlna.util.VolumeManager;

import android.util.Log;

/*
 * 由底层触发的回调函数，尽量不要主动调用
 */
public class Callbacks {

	private static final String TAG = "Callbacks";
	private static final int HTTP_INIT_WAIT = 0;
	private static final int HTTP_INIT_SUCCESS = 1;
	
	private static DlnaControlInterface dlnaControlImper;
	// 更新设备列表列表
	public static void updateDevList(DevInfo[] devList) {
		DevUpdateUitl.getInstance().update(devList);
	}
	/** regest dlna play control object ,use this object can handle some other dlna device push media to play in hear **/
	public static void registerDlnaControler(DlnaControlInterface dlnaControlImp){
		dlnaControlImper = dlnaControlImp;
	}
	// 播放，与暂停相对应
	public static int play() {
		Log.i(TAG, "play");
		if(dlnaControlImper == null) return 0;
		dlnaControlImper.dlnaPLay();
		return 0;
	}

	public static String getFlieType(String path) {
		/*
		 * try { URL myFileUrl = new URL(path); HttpURLConnection conn =
		 * (HttpURLConnection) myFileUrl.openConnection();
		 * conn.setDoInput(true); conn.connect(); String type =
		 * URLConnection.guessContentTypeFromStream(conn.getInputStream());
		 * Log.i("getFlieType:", type + "  time:  " +
		 * (SystemClock.currentThreadTimeMillis() - mStartTime));
		 * conn.disconnect(); return type; } catch (IOException e) {
		 * e.printStackTrace(); }
		 */
		return null;
	}

	// C通知上层有文件播放请求，正在等待播放与底层解析中
	public static int waitPlay() {
		if(dlnaControlImper == null){
			PlayUtil.waitPlay();
		}else{
			dlnaControlImper.dlnaWaitPlay();
		}
		return 0;
	}

	// 播放指定地址的文件
	public static int playImage(int type, String path) {
		Log.i(TAG, "playImage" + " type: " + type + " path: " + path);
 
		if (dlnaControlImper != null) {
			Log.i(TAG, "playImage -->" + " DlnaPlay(Activity) start yet and to play...");
			 dlnaControlImper.playImage(type, path);
		} else { //PlayerHelper对象为空时
			Log.i(TAG, "playImage -> DlnaPlay(Activity) not start yet to play");
			PlayUtil.playInLocal(path, type, false,-1);
		}
		return 0;
	}

	// 播放时会先调 stop() 再调play()
	public static int stop() {
		Log.i(TAG, "stop");
		if(dlnaControlImper == null) return 0;
		dlnaControlImper.dlnaStop();
		return 0;
	}

	// 暂停
	public static int pause() {
		Log.i(TAG, "pause");
		if(dlnaControlImper == null) return 0;
		dlnaControlImper.dlnaPause();
		return 0;
	}

	// 设置播放进度
	public static int setSpeed(int speed) {
		if(dlnaControlImper != null){
			dlnaControlImper.setDlnaPlayProgress(speed);
		}
		return speed;
	}

	// 快进
	public static int speedUp() {
		if (dlnaControlImper != null) {
			dlnaControlImper.dlnaSpeedUp();
		}
		return 0;
	}

	// 快退
	public static int speedDown() {
		if(dlnaControlImper != null){
			dlnaControlImper.dlnaSpeedDown();
		}
			return 0;
	}

	public static int setVolume(int volume) {
		VolumeManager.setVolume(volume);
		return volume;
	}

	public static int volumeUp() {
		VolumeManager.volumeUp();
		return 0;
	}

	public static int volumeDown() {
		VolumeManager.volumeDown();
		return 0;
	}

	// 搜索文件
	public static int searchFileList(FileNode[] fileNodes, int count,
			int totalCount) {
		FileUpdateUtil.update(fileNodes, count, totalCount);
		return 0;
	}

	// 播放器端回调，得到当前播放情况
	public static PositionInfo searchPosition(int InstanceID) {
		return PlayUtil.sendPosition();
		 
	}

	// 控制端回调，根据preinfo显示当前播放情况
	public static int getPosition(PositionInfo preInfo) {
		if (preInfo != null) {
			PlayUtil.setPosition(preInfo);
		}
		return 0;
	}

	public static int httpStatusChange(int status) {
		if (status == HTTP_INIT_WAIT) {
//			NotificationUtil.setIsNotifiShow(false);
			// USBBroadcastReceiver.unRegisterReceiver();
		} else if (status == HTTP_INIT_SUCCESS) {
//			NotificationUtil.setIsNotifiShow(true);
			// USBBroadcastReceiver.registerReceiver();
		}
		return 0;
	}
	/** dlna 控制接口 **/
	public static interface DlnaControlInterface {
		/**  dlna 的播放与暂停  **/
		public void dlnaPLay();
		public void dlnaPause();
		public void dlnaStop();
		/**  dlna 等待播放  **/
		public void dlnaWaitPlay();
		/**  dlna 设置播放进度  **/
		public void setDlnaPlayProgress(int progress);
		/**  dlna 快进  **/
		public void dlnaSpeedUp();
		/**  dlna 快退  **/
		public void dlnaSpeedDown();
		/** 播放指定文件 **/
		public  int playImage(int type, String path);
	}
}
