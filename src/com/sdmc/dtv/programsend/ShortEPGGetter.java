package com.sdmc.dtv.programsend;

import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

public class ShortEPGGetter {
	
	private static final int PERIOD = 2000;
	private static final int GET_COUNT = 5;
	
	private int mCount;
	private boolean mIsRun;
	private DTVMessageSender mSender;
	
	private Timer mTimer = new Timer();
	private TimerTask getShortEpgTask = new TimerTask() {
		
		@Override
		public void run() {
			if (mIsRun) {
				Log.i("ShortEPGGetter", "mCount : " + mCount);
				if (mCount < GET_COUNT) {
					mCount ++;
					mSender.getShortEPG();
				} else {
					stop();
				}
			}
		}
	};
	
	public ShortEPGGetter(DTVMessageSender sender) {
		mSender = sender;
		mTimer.schedule(getShortEpgTask, PERIOD, PERIOD); 
	}
	
	public void start() {
		mIsRun = true;
		mCount = 0;
	}
	
	public void stop() {
		mIsRun = false;
		mCount = 0;
	}
	
	public void release() {
		mTimer.cancel();
		mTimer.purge();
		getShortEpgTask.cancel();
	}
	
}
