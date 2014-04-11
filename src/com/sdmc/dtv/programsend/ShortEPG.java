package com.sdmc.dtv.programsend;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class ShortEPG {
	
	private String mDate = "";
	private String mTime = "";
	private String mpEpgTime = "";
	private String mfEpgTime = "";
	private String mpEpgName = "";
	private String mfEpgName = "";
	private boolean mIsEmpty;
	
	public ShortEPG(byte[] bytes) {
		int timeLength = bytes[0];
		String coreTime = new String(bytes, 1, timeLength);
		int pEpgLength = bytes[1 + timeLength];
		String pEpg = new String(bytes, 2 + timeLength, pEpgLength);
		int fEpgLength = bytes[2 + timeLength + pEpgLength];
		String fEpg = new String(bytes, 3 + timeLength + pEpgLength, fEpgLength);
		
		Log.i("ShortEPG", "coreTime : " + coreTime);
		Log.i("ShortEPG", "pEpg : " + pEpg);
		Log.i("ShortEPG", "fEpg : " + fEpg);
		
		if (timeLength != 0) {
			int index = coreTime.indexOf(":");
			mDate = coreTime.substring(0, index);
			mTime = coreTime.substring(index + 1);
		} else {
			mDate = "";
			mTime = "";
		}
		
		int pIndex = pEpg.indexOf(";");
		mpEpgTime = pEpg.substring(0, pIndex);
		mpEpgName = pEpg.substring(pIndex + 1);
		
		int fIndex = fEpg.indexOf(";");
		mfEpgTime = fEpg.substring(0, fIndex);
		mfEpgName = fEpg.substring(fIndex + 1);
		Log.i("ShortEPG", "mpEpgTime : " + mpEpgTime);
		Log.i("ShortEPG", "mpEpgName : " + mpEpgName);
		Log.i("ShortEPG", "mfEpgTime : " + mfEpgTime);
		Log.i("ShortEPG", "mfEpgName : " + mfEpgName);
		mIsEmpty = true;
	}

	public ShortEPG() {
		mIsEmpty = false;
	}

	public String getDate() {
		if (mDate.equals("")) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date curDate = new Date(System.currentTimeMillis());
	        return formatter.format(curDate);
		}
		return mDate;
	}

	public String getTime() {
		if (mTime.equals("")) {
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
			Date curDate = new Date(System.currentTimeMillis());
			return formatter.format(curDate);
		}
		return mTime;
	}

	public String getPEpgTime() {
		return mpEpgTime;
	}

	public String getFEpgTime() {
		return mfEpgTime;
	}

	public String getPEpgName() {
		return mpEpgName;
	}

	public String getFEpgName() {
		return mfEpgName;
	}
	
	public String getPFEpgs() {
		if (mpEpgTime.equals("") && mfEpgTime.equals("")) {
			return "";
		}
		return mpEpgTime + " " + mpEpgName + " ; " + mfEpgTime + " " + mfEpgName;
	}
	
	public String getPEpgForTV() {
		if (mpEpgTime.equals("")) {
			return "";
		} else {
			return mpEpgTime + mpEpgName;
		}
	}
	
	public String getFEpgForTV() {
		if (mfEpgTime.equals("")) {
			return "";
		} else {
			return mfEpgTime + mfEpgName;
		}
	}
	
	public boolean isEmpty() {
		return mIsEmpty;
	}
	
}
