package hometv.remote.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

import com.sdmc.dtv.programsend.DTVMessageSender;
import com.sdmc.dtv.programsend.Program;
import com.sdmc.dtv.programsend.ProgramRunnable;
import com.sdmc.dtv.programsend.ShortEPG;

/**
 * 网络协议接口的局域网实现
 * @author fee
 *
 */
public class ProtecolLocalImp implements NetProtecolInterface{
	private DTVMessageSender dtvMessageSender;
	private ProgramRunnable programRunnable;
	public ProtecolLocalImp() {
		dtvMessageSender = new DTVMessageSender();
		programRunnable = ProgramRunnable.instance();
	}
	
	@Override
	public boolean commonSendToServer(byte... cmdByte) {
		return false;
	}
 
	/** 获取节目的字幕 **/
	@Override
	public void getCurProgramSubtitle() {
		
	}

	@Override
	public void getCurProgramAudioTrack() {
		dtvMessageSender.getAudioTrack();
	}

	@Override
	public void getCurProgramList() {
		
	}

	@Override
	public void setAudioTrack(int audioThackId) {
		dtvMessageSender.setAudioTrack(audioThackId);
		
	}

	@Override
	public void getURL() {
		dtvMessageSender.getURL();
	}

	@Override
	public void stopPlay() {
		dtvMessageSender.stopPlay();
		
	}

	@Override
	public void programPlay(int programId, int programType, String password) {
		dtvMessageSender.programPlay(programId, programType, password);
		 
	}

 

	@Override
	public void getMoreEPG(int day) {
		dtvMessageSender.getMoreEPG(day);
	}

	@Override
	public void getEPGTime() {
		dtvMessageSender.getEPGTime();
	}
	
	@Override
	public HashMap<String, ArrayList<Program>> getProgramData() {
		
		return programRunnable.getProgramData();
	}
	@Override
	public ArrayList<String> getDetailEpgs() {
		 
		return programRunnable.getEPGList();
	}

	@Override
	public int getCurPlayingProgramId() {
		return programRunnable == null ? 0 : programRunnable.getCurrentPlay();
	}
	public void editProgram(byte option ,int programId){
		dtvMessageSender.editProgram(option, programId);
	}
// *******************  获取 ShortEpg 相关 *****************	
	private static final int PERIOD = 2000;
	private static final int GET_COUNT = 5;
	
	private int mCount;
	private boolean mIsRun;
	 
	
	private Timer mTimer = new Timer();
	private TimerTask getShortEpgTask = new TimerTask() {
		
		@Override
		public void run() {
			if (mIsRun) {
				 
				if (mCount < GET_COUNT) {
					mCount ++;
					getCurSimpleEpg();
				} else {
					stopGetShortEpgTask();
				}
			}
		}
	};
	
	 
 
	public void startGetShortEpgTask() {
		if(!mIsRun){
			try{
				mTimer.schedule(getShortEpgTask, PERIOD, PERIOD); 
			}catch(IllegalStateException e){
				/**  if the   Timer  has been canceled, 
				     or if the task has been
			            scheduled or canceled.
			             **/
				Log.w("info","The timer schedule the task occur IllegalStateException  ");
				mIsRun = true;
				mCount = 0;
				getShortEpgTask.run();
			}
		}
		mIsRun = true;
		mCount = 0;
	}
	
	public void stopGetShortEpgTask() {
		mIsRun = false;
		mCount = 0;
	}
	
	public void releaseGetShortEpgTask() {
		mTimer.cancel();
		mTimer.purge();
//		getShortEpgTask.cancel();
		mTimer = new Timer();
	}

	@Override
	public ShortEPG getShortEPG() {
		return programRunnable.getShortEPG();
	}

	@Override
	public void getCurSimpleEpg() {
	 dtvMessageSender.getShortEPG();
	}
}
