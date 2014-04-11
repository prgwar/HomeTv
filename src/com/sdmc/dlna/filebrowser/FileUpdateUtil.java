package com.sdmc.dlna.filebrowser;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.sdmc.dlna.service.FileNode;

/**
 * 更新文件列表工具类
 */
public class FileUpdateUtil {

	private static ArrayList<FileItem> mFileItemList = new ArrayList<FileItem>();
	private static boolean isRefresh = false;
	private static final int REFRESH_TIME = 10;
	private static final int REFRESH_WAIT_TIME = 500;
	private static  Handler toUiHandler ;
	public static void setUiHandler(Handler uiHandler){
		toUiHandler = uiHandler;
	}
	public static final int MSG_FILE_UPDATE = 169;
	public static final byte MSG_HAS_NO_FILE = 0;
	public static final byte MSG_UPDATE_ERROR = 1;
	public static final byte MSG_NEED_UPDATE = 2;
	/** CallBacks  调用 **/
	public static void update(FileNode[] fileNodes, int count, int totalCount) {
		Log.w("info"," FileUpdateUtil -->  update () count "+count);
		mFileItemList.clear();
		Message toDlnaActivityMsg = new Message();
		toDlnaActivityMsg.what = MSG_FILE_UPDATE;
		int fileNodesLen = fileNodes.length;
		if (count == 0) { // 无内容
			isRefresh = true;
			toDlnaActivityMsg.arg1 =  MSG_HAS_NO_FILE;
//			return;
		}
		else if (fileNodesLen != count) { //更新失败
			isRefresh = true;
			toDlnaActivityMsg.arg1 = MSG_UPDATE_ERROR;
//			return;
		} 
		else {
			for (int i = 0; i < count; i++) {
				try {
					FileNode curFile = fileNodes[i];
//					Log.i("info","FileUpdateUtil -- > search file the file is : "+curFile);
					mFileItemList.add(new FileItem(curFile.name,
							curFile.path, curFile.type,
							curFile.id, curFile.parentID));
					isRefresh = true;
				} catch (Exception e) {
					Log.e("info"," FileUpdateUtil  add FileItem occur Exception ...");
				}
			}//end for
			toDlnaActivityMsg.arg1 = MSG_NEED_UPDATE;
		}
		if(toUiHandler != null){
			toUiHandler.sendMessage(toDlnaActivityMsg);
		}
	}

	public static ArrayList<FileItem> getFileList() {
//		for (int i = 0; i < REFRESH_TIME; i++) {
//			if (isRefresh) {
//				return mFileItemList;
//			} else {
//				SystemClock.sleep(REFRESH_WAIT_TIME);
//			}
//		}
//		mFileItemList.clear();
		
		ArrayList<FileItem > data = new ArrayList<FileItem>();
		data.addAll(mFileItemList);
		return data;
	}

	public static int getParentID(int lastID) {
		if (mFileItemList.isEmpty()) {
			return lastID;
		} else {
			return mFileItemList.get(0).getParentId();
		}
	}

	public static void setRefresh() {
		isRefresh = false;
	}
}
