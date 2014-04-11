package com.sdmc.dlna.service;

import com.sdmc.dlna.filebrowser.FileUpdateUtil;
import com.sdmc.dlna.util.DevUpdateUitl;
import com.sdmc.dlna.util.VolumeManager;

import android.util.Log;

/*
 * �ɵײ㴥���Ļص�������������Ҫ��������
 */
public class Callbacks {

	private static final String TAG = "Callbacks";
	private static final int HTTP_INIT_WAIT = 0;
	private static final int HTTP_INIT_SUCCESS = 1;
	
	private static DlnaControlInterface dlnaControlImper;
	// �����豸�б��б�
	public static void updateDevList(DevInfo[] devList) {
		DevUpdateUitl.getInstance().update(devList);
	}
	/** regest dlna play control object ,use this object can handle some other dlna device push media to play in hear **/
	public static void registerDlnaControler(DlnaControlInterface dlnaControlImp){
		dlnaControlImper = dlnaControlImp;
	}
	// ���ţ�����ͣ���Ӧ
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

	// C֪ͨ�ϲ����ļ������������ڵȴ�������ײ������
	public static int waitPlay() {
		if(dlnaControlImper == null){
			PlayUtil.waitPlay();
		}else{
			dlnaControlImper.dlnaWaitPlay();
		}
		return 0;
	}

	// ����ָ����ַ���ļ�
	public static int playImage(int type, String path) {
		Log.i(TAG, "playImage" + " type: " + type + " path: " + path);
 
		if (dlnaControlImper != null) {
			Log.i(TAG, "playImage -->" + " DlnaPlay(Activity) start yet and to play...");
			 dlnaControlImper.playImage(type, path);
		} else { //PlayerHelper����Ϊ��ʱ
			Log.i(TAG, "playImage -> DlnaPlay(Activity) not start yet to play");
			PlayUtil.playInLocal(path, type, false,-1);
		}
		return 0;
	}

	// ����ʱ���ȵ� stop() �ٵ�play()
	public static int stop() {
		Log.i(TAG, "stop");
		if(dlnaControlImper == null) return 0;
		dlnaControlImper.dlnaStop();
		return 0;
	}

	// ��ͣ
	public static int pause() {
		Log.i(TAG, "pause");
		if(dlnaControlImper == null) return 0;
		dlnaControlImper.dlnaPause();
		return 0;
	}

	// ���ò��Ž���
	public static int setSpeed(int speed) {
		if(dlnaControlImper != null){
			dlnaControlImper.setDlnaPlayProgress(speed);
		}
		return speed;
	}

	// ���
	public static int speedUp() {
		if (dlnaControlImper != null) {
			dlnaControlImper.dlnaSpeedUp();
		}
		return 0;
	}

	// ����
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

	// �����ļ�
	public static int searchFileList(FileNode[] fileNodes, int count,
			int totalCount) {
		FileUpdateUtil.update(fileNodes, count, totalCount);
		return 0;
	}

	// �������˻ص����õ���ǰ�������
	public static PositionInfo searchPosition(int InstanceID) {
		return PlayUtil.sendPosition();
		 
	}

	// ���ƶ˻ص�������preinfo��ʾ��ǰ�������
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
	/** dlna ���ƽӿ� **/
	public static interface DlnaControlInterface {
		/**  dlna �Ĳ�������ͣ  **/
		public void dlnaPLay();
		public void dlnaPause();
		public void dlnaStop();
		/**  dlna �ȴ�����  **/
		public void dlnaWaitPlay();
		/**  dlna ���ò��Ž���  **/
		public void setDlnaPlayProgress(int progress);
		/**  dlna ���  **/
		public void dlnaSpeedUp();
		/**  dlna ����  **/
		public void dlnaSpeedDown();
		/** ����ָ���ļ� **/
		public  int playImage(int type, String path);
	}
}
