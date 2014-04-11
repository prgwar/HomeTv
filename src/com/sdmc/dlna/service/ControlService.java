package com.sdmc.dlna.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import com.sdmc.phone.util.NetUtil;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.os.Process;
import android.provider.MediaStore;
import android.util.Log;

/**
 * 开启后台服务
 */
public class ControlService extends Service {

	private static String TAG = "ControlService";
	/**
	 * 标志本ControlService是否在运行
	 */
	private static boolean mIsRun = false;
//	private NotificationUtil mNotificationUtil;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		 Log.e("info", TAG+ "  ---->  onCreate");
		mIsRun = true;
//		new VolumeManager(this);
		
//		mNotificationUtil = new NotificationUtil(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		deepCopy("dlna", "/data/tmp/");
		if (!NetUtil.MAC.equals("")) {// 获取到了MAC地址
			new InitWebService().initJNI();
		} else {
			Log.i(TAG, "can not find mac!");
			stopSelf();
		}
		 
		ContentResolver resolver = getContentResolver();
//   	Uri uri, 
//		String[] projection,  
//		String selection,
//		String[] selectionArgs,
//		String sortOrder)  
		
//		  new String[] { 
////          		MediaStore.Audio.Media._ID,
////                  MediaStore.Audio.Media.DISPLAY_NAME,
////                  MediaStore.Audio.Media.TITLE,
////                  MediaStore.Audio.Media.DURATION,
////                  MediaStore.Audio.Media.ARTIST,
////                  MediaStore.Audio.Media.ALBUM,
////                  MediaStore.Audio.Media.YEAR,
////                  MediaStore.Audio.Media.MIME_TYPE,
////                  MediaStore.Audio.Media.SIZE,
//                  MediaStore.Audio.Media.DATA }
		String [] audioMediaDataProjecion = {
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DURATION
		};
		 
	Cursor externalMusicData=	resolver.query(
	                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
	                audioMediaDataProjecion,
//	                MediaStore.Audio.Media.MIME_TYPE + "=? or "
	                        null,null,
//	                        + MediaStore.Audio.Media.MIME_TYPE + "=?",
//	                new String[] { "audio/mpeg", "audio/x-ms-wma" }
	                null);
	ArrayList<String> mediaFilePaths = new ArrayList<String>();
	ArrayList<Byte> musicFileType = new ArrayList<Byte>();
	// 外部 音乐文件
	while( externalMusicData != null && externalMusicData.moveToNext()){
		long audioDuration = externalMusicData.getLong(1);
		if(audioDuration <= 5000)continue;
		String musicData = externalMusicData.getString(0);
		
		mediaFilePaths.add(musicData);
		musicFileType.add((byte)2); // 2 音乐； 3 图片； 1 视频
	}
	if(externalMusicData != null)
		externalMusicData.close();
	
	// 内部音乐文件
	Cursor interMusicCursor = resolver.query( MediaStore.Audio.Media.INTERNAL_CONTENT_URI, audioMediaDataProjecion, null, null, null);
	while( interMusicCursor != null && interMusicCursor.moveToNext()){
		long audioDuration = interMusicCursor.getLong(1);
		if(audioDuration <= 5000)continue;
		String musicData = interMusicCursor.getString(0);
		mediaFilePaths.add(musicData);
		musicFileType.add((byte)2); // 2 音乐； 3 图片； 1 视频
	}
	if(interMusicCursor !=null)
		interMusicCursor.close();
	
	// 内部视频文件
	Cursor interVideoCursor = resolver.query( MediaStore.Video.Media.INTERNAL_CONTENT_URI, audioMediaDataProjecion, null, null, null);
	while( interVideoCursor != null && interVideoCursor.moveToNext()){
		String interVideoPath = interVideoCursor.getString(0);
		mediaFilePaths.add(interVideoPath);
		musicFileType.add((byte)1); // 2 音乐； 3 图片； 1 视频
	}
	if(interVideoCursor !=null)
		interVideoCursor.close();
	
	// 外部视频文件
	Cursor externalVideoCursor = resolver.query( MediaStore.Video.Media.EXTERNAL_CONTENT_URI, audioMediaDataProjecion, null, null, null);
	while( externalVideoCursor != null && externalVideoCursor.moveToNext()){
		String externalVideoPath = externalVideoCursor.getString(0);
		mediaFilePaths.add(externalVideoPath);
		musicFileType.add((byte)1); // 2 音乐； 3 图片； 1 视频
	}
	if(externalVideoCursor !=null)
		externalVideoCursor.close();
	
	// 内部图片文件
	String[] picProjection = {
			MediaStore.Audio.Media.DATA
	};
		Cursor interPicCursor = resolver.query( MediaStore.Images.Media.INTERNAL_CONTENT_URI, picProjection, null, null, null);
		while( interPicCursor != null && interPicCursor.moveToNext()){
			String interPicPath = interPicCursor.getString(0);
			mediaFilePaths.add(interPicPath);
			musicFileType.add((byte)3); // 2 音乐； 3 图片； 1 视频
		}
		if(interPicCursor !=null)
			interPicCursor.close();
		
		// 外部图片文件
		Cursor externalPicCursor = resolver.query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, picProjection, null, null, null);
		while( externalPicCursor != null && externalPicCursor.moveToNext()){
			String externalPicPath = externalPicCursor.getString(0);
			mediaFilePaths.add(externalPicPath);
			musicFileType.add((byte)3); // 2 音乐； 3 图片； 1 视频
		}
		if(externalPicCursor !=null)
		externalPicCursor.close();
	
	int dataLen = musicFileType.size();
	Log.e("info"," scanned  all media file total count is  :	"+dataLen	);
	byte[] toNativeByte = new byte[dataLen];
	 for(int i = 0; i<dataLen ; i++	){
		 toNativeByte [i] = musicFileType .get(i);
	 }
	 
	NativeAccess.createFileNodS(toNativeByte, mediaFilePaths);
	
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
//		mNotificationUtil.stop();
		mIsRun = false;
//		 NativeAccess.reinit();
		System.exit(0);
	}

	/**
	 * 如果本Service未运行，则启动，启动自身
	 * 
	 * @param context
	 */
	public static void startControlService(Context context) {
		if (!mIsRun) {
			Intent serverIntent = new Intent();
			serverIntent.setAction("com.sdmc.dlna.service.CONTROL_SERVICE");
			context.startService(serverIntent);
		}
	}

	public static void stopControlService(Context context) {
		if (mIsRun) {
			Intent serverIntent = new Intent();
			serverIntent.setAction("com.sdmc.dlna.service.CONTROL_SERVICE");
			context.stopService(serverIntent);
			// ---> onDestroy()
		}
	}

	/**
	 * 
	 * @param srcDir
	 *            源路径
	 * @param desDir
	 *            目标路径
	 */
	private void deepCopy(String srcDir, String desDir) {
		Log.i(TAG, "srcDir = " + srcDir + ",desDir = " + desDir);
		File file = new File(desDir + srcDir);
		if (file.exists() && file.isDirectory()) {
			Log.i(TAG, "dir exist now");
			return;
		}

		try {
			String[] str = getAssets().list(srcDir);
			if (str.length > 0) {
				Log.i(TAG, "Create Dir = " + desDir + srcDir);
				File nfile = new File(desDir + srcDir);
				nfile.mkdirs();
				String path = srcDir;
				for (String string : str) {
					path = path + "/" + string;
					deepCopy(path, desDir);
					path = path.substring(0, path.lastIndexOf('/'));
				}
			} else {
				Log.i(TAG, "Create File = " + desDir + srcDir);
				InputStream is = getAssets().open(srcDir);
				FileOutputStream fos = new FileOutputStream(desDir + srcDir);
				byte[] buffer = new byte[1024];
				int count = 0;
				while (true) {
					count++;
					int len = is.read(buffer);
					if (len == -1) {
						break;
					}
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			}

		} catch (IOException e) {
			Log.i(TAG, "IOException ");
			e.printStackTrace();
		}

	}
}
