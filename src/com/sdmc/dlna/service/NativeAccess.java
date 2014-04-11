package com.sdmc.dlna.service;

import java.util.ArrayList;

/**
 * ���ط���
 * 
 * @author Ke Chow
 * @date 2012-08-24
 */
public class NativeAccess {
	/**
	 * Set UserName username:�û�����udn��Ψһ��ʾ���� ���ط���
	 */
	public static native int setUserName(String username, String udn);

	/** init & open the dlna **/
	public static native int initUpnp();

	/**
	 * ���ţ�����ͣ���Ӧ��
	 * @param dlnaPlayDeviceId  DLNA�����豸���
	 * @param noUsedMediaPath   pathΪ���ò���
	 * @return
	 */
	public static native int play(int dlnaPlayDeviceId, String noUsedMediaPath);

	/**
	 *  ֹͣ���豸���
	 * @param devnum
	 * @return
	 */
	public static native int stop(int devnum);

	/**
	 *  ��ͣ���豸���
	 * @param devnum
	 * @return
	 */
	public static native int pause(int devnum);

	/**
	 *  ���ò��Ž��ȣ��豸���+������λ��
	 * @param devnum
	 * @param speed
	 * @return
	 */
	public static native int setSpeed(int devnum, int speed);

	// ���Ӳ����ٶȣ�ÿ��+1�������豸���
	public static native int speedUp(int devnum);

	// ���ٲ����ٶȣ�ÿ��-1�������豸���
	public static native int speedDown(int devnum);

	/**
	 *  �����������豸���+Ԥ��������
	 * @param devnum
	 * @param volume
	 * @return
	 */
	public static native int setVolume(int devnum, int volume);

	/**
	 *  �����������豸���
	 * @param devnum
	 * @return
	 */
	public static native int volumeUp(int devnum);

	/**
	 * �����������豸���
	 * @param devnum
	 * @return
	 */
	public static native int volumeDown(int devnum);

	/** ������Ƶ����Ƶ����ͼƬ �ļ��ӿڣ��豸���+�����ļ�·�� **/
	public static native int playPic(int dlnaDeviceId, String path);

	// �����ļ��б�
	/*
	 * devnum: �豸��. ObjectID: �ļ��ڵ�ID. BrowseFlag: ����Ϊ"BrowseDirectChildren".
	 * Filter: ����Ϊ"*". StartingIndex: ����Ϊ0. RequestedCount: ϣ��ȡ�õĽڵ������ȡ0Ϊȫ����ȡ.
	 * SortCriteria: ����Ϊ"*".
	 */
	public static native int search(int devnum, int objectID,
			String BrowseFlag, String Filter, int StartingIndex,
			int RequestedCount, String SortCriteria);

	public static int searchAllFileList(int devnum, int objectID) {
		return search(devnum, objectID, "BrowseDirectChildren", "*", 0, 0, "*");
	}

	/** ���ƶ˲�ѯ��������ǰ����״̬��**/
	public static native int getPosition(int devnum, int InstanceID);

	/**
	 * ���ع���,0��ʾ�رգ�1��ʾ��
	 */
	public static native int setShare(int videoShare, int musicShare, int picShare);

	/** ɱ������ **/
	public static native int reinit();

	/** ���������ļ��б� **/
	public static native int reinitFs();

	// ��ȡURL��������Ϣ
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
