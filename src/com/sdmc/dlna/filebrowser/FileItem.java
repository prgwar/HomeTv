package com.sdmc.dlna.filebrowser;

import java.io.File;

/**
 * 文件节点
 * 
 * @author Ke Chow
 * @date 2012-08-25
 */
public class FileItem extends BaseItem {

	private int mFileType;
	private String mAbsPath;
	private int mId;
	private int mParentId;
	private boolean mIsPlay;

	private static final String[] PROLATENAME_VIDEO = new String[] { "avi",
			"mp4", "mkv", "3gp", "mpeg", "mpg", "ts", "flv", "wmv", "rm",
			"rmvb", "m2ts", "mov" };
	private static final String[] PROLATENAME_MUSIC = new String[] { "mp3",
			"mid", "ac3", "wav", "wma" };
	private static final String[] PROLATENAME_PHOTO = new String[] { "png",
			"bmp", "jpg", "jpeg", "gif" };

	public FileItem(String fileName, String absPath) throws Exception {
		super(fileName, 6);// 默认Icon是未知
		if (!checkValidityOfString(absPath)) {
			throw new Exception("Invalid File Path");
		}
		File f = new File(absPath);
		if (!f.exists()) {
			throw new Exception("File Not Found");
		}
		this.mAbsPath = absPath;
		if (f.isDirectory()) {
			setFileType(Const.FILETYPE_FOLDER);
		} else {
			setFileType(checkFileType(fileName));
		}
	}

	public FileItem(String fileName, String absPath, int nodeType, int id,
			int parentId) throws Exception {
		super(fileName, 6);// 默认Icon是未知
		setFileType(castFileType(nodeType, fileName));
		this.mId = id;
		this.mParentId = parentId;
		this.mAbsPath = absPath;
		this.mIsPlay = false;
	}

	private int castFileType(int nodeType, String name) {
		switch (nodeType) {
		case 1:
			return Const.FILETYPE_FOLDER;
		case 2:
			return checkFileType(name);
		case 3:
			return Const.FILETYPE_VIDEO;
		case 4:
			return Const.FILETYPE_MUSIC;
		case 5:
			return Const.FILETYPE_IMAGE;
		default:
			return Const.FILETYPE_UNKNOWN;
		}
	}

	private int checkFileType(String fileName) {
		int position = fileName.lastIndexOf('.');
		if (position >= 0) {
			String type = fileName.substring(position);
			for (int i = 0; i < PROLATENAME_VIDEO.length; i++) {
				if (type.endsWith(PROLATENAME_VIDEO[i])) {
					return Const.FILETYPE_VIDEO;
				}
			}
			for (int i = 0; i < PROLATENAME_MUSIC.length; i++) {
				if (type.endsWith(PROLATENAME_MUSIC[i])) {
					return Const.FILETYPE_MUSIC;
				}
			}
			for (int i = 0; i < PROLATENAME_PHOTO.length; i++) {
				if (type.endsWith(PROLATENAME_PHOTO[i])) {
					return Const.FILETYPE_IMAGE;
				}
			}
		}
		return Const.FILETYPE_UNKNOWN;
	}

	public FileItem(File file) throws Exception {
		this(file.getName(), file.getAbsolutePath());
	}

	public void setFileType(int type) {
		this.mFileType = type;
		switch (type) {
		case Const.FILETYPE_FOLDER:
		case Const.FILETYPE_VIDEO:
		case Const.FILETYPE_MUSIC:
		case Const.FILETYPE_IMAGE:
		case Const.FILETYPE_UNKNOWN:
			setIcon(type - Const.FILETYPE_FOLDER + 2);
			break;
		default:
			setIcon(6);
			break;
		}
	}

	public void setAbsPath(String absPath) {
		if (checkValidityOfString(absPath)) {
			this.mAbsPath = absPath;
		}
	}

	public int getFileType() {
		return this.mFileType;
	}

	public String getAbsPath() {
		return this.mAbsPath;
	}
	/** 得到文件节点 **/
	public int getId() {
		return mId;
	}

	public int getParentId() {
		return mParentId;
	}

	public boolean getIsPlay() {
		return mIsPlay;
	}

	public void setIsPlay(boolean isPlay) {
		this.mIsPlay = isPlay;
	}

	@Override
	public String toString() {
		return "[ type: " + mFileType + " ] [ icon: " + getIndexOfIcon()
				+ " ] [ name: " + getName() + " ] [ path: " + mAbsPath + " ]" +"[ fileNode "+mId +"]";
	}
}
