package com.sdmc.dlna.filebrowser;

import sdmc.com.hometv.R;

/**
 * 用于存放各种常量的类
 * 
 * @author Ke Chow
 * @date 2012-08-24
 */
public class Const {
	// 设备类型
	public static final int TYPE_BASE = 0x0;
	public static final int TYPE_ANDROID_PHONE = TYPE_BASE + 1;
	public static final int TYPE_ANDROID_TABLET = TYPE_ANDROID_PHONE + 1;
	public static final int TYPE_IOS_PHONE = TYPE_ANDROID_TABLET + 1;
	public static final int TYPE_IOS_PAD = TYPE_IOS_PHONE + 1;
	public static final int TYPE_Windows = TYPE_IOS_PAD + 1;
	public static final int TYPE_MAX = TYPE_Windows + 1;

	// 文件类型
	public static final int FILETYPE_FOLDER = 0x0;
	public static final int FILETYPE_VIDEO = FILETYPE_FOLDER + 1;
	public static final int FILETYPE_MUSIC = FILETYPE_VIDEO + 1;
	public static final int FILETYPE_IMAGE = FILETYPE_MUSIC + 1;
	public static final int FILETYPE_UNKNOWN = FILETYPE_IMAGE + 1;

	// 操作类型
	public static final int ACTIONTYPE_INTENT = 0x0;
	public static final int ACTIONTYPE_SHAREON = ACTIONTYPE_INTENT + 1;
	public static final int ACTIONTYPE_SHAREOFF = ACTIONTYPE_SHAREON + 1;
	public static final int ACTIONTYPE_SETTINGS = ACTIONTYPE_SHAREOFF + 1;
	public static final int ACTIONTYPE_FILEBROWSER = ACTIONTYPE_SETTINGS + 1;
	public static final int ACTIONTYPE_UNKNOWN = ACTIONTYPE_FILEBROWSER + 1;

	public static final int[] DEFAULT_ICONs = { // 默认图标
			R.drawable.device, // 0x00 设备图标
			R.drawable.setting, // 0x01 设置图标
			R.drawable.item_type_dir, // 0x02 文件夹
			R.drawable.item_type_video, // 0x03 视频文件
			R.drawable.item_type_music, // 0x04 音乐文件
			R.drawable.item_type_photo, // 0x05 图片文件
			R.drawable.item_type_file, // 0x06 未知文件
	};
	public static final int DEFAULT_ICON_COUNT = 7;

	public static final String DEVID = "DEVID";
	public static final String DEVID_NAME = "DEVID_NAME";
	public static final int MYSELF = 0;
	public static final String MYROOTPATH = "/mnt/";
}
