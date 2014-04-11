package hometv.remote.bean;

import hometv.remote.socketUtil.ConnectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

import com.sdmc.dtv.programsend.Program;
import com.sdmc.dtv.programsend.ShortEPG;

/**
 * 远程协议接口实现
 * @author fee
 *
 */
public class ProtecolRemoteImp implements NetProtecolInterface{
	
	private ConnectUtil connectUtil;
	public ProtecolRemoteImp( ConnectUtil curConnectUtil) {
		this.connectUtil = curConnectUtil;
	}
	@Override
	public boolean commonSendToServer(byte... cmdByte) {
		return connectUtil.commonSendToServer(cmdByte);
	}

	 
	/** 获取节目的字幕 **/
	@Override
	public void getCurProgramSubtitle() {
		
	}

	@Override
	public void getCurProgramAudioTrack() {
		connectUtil.getAudioTrackIndex();
	}

	@Override
	public void getCurProgramList() {
		connectUtil.getDvbProgramList();
	}
	@Override
	public void setAudioTrack(int audioThackId) {
		connectUtil.setAudioTrack((byte)audioThackId);
	}
	@Override
	public void getURL() {
		connectUtil.getProgramPlayUrl();
	}
	@Override
	public void stopPlay() {
		connectUtil.stop();
	}
	@Override
	public void programPlay(int programId, int programType, String password) {
		 connectUtil.programPlay(programId, (byte)programType, password);
	}
	
	//发送命令
	@Override
	public void getMoreEPG(int day) {
		byte weekDay = (byte) day;
		connectUtil.getCurDayDetailEpg(weekDay);
	}
	@Override
	public void getEPGTime() {
		connectUtil.getEpgTime();
	}
	
	@Override
	public HashMap<String, ArrayList<Program>> getProgramData() {
		return connectUtil.getProgramData();
	}
	@Override
	public ArrayList<String> getDetailEpgs() {
		return connectUtil.getDetailEpg();
	}
	@Override
	public int getCurPlayingProgramId() {
		return connectUtil == null ? 0: connectUtil.getCurPlayingPorgramId();
	}
	public void editProgram(byte option ,int programId){
		connectUtil.editProgram(option, programId);
	}
// *******************  获取 ShortEpg 相关 *****************	
	private Timer mTimer = new Timer() ;
	private static final int PERIOD = 2000;
	private static final int GET_COUNT = 5;
	private int mCount;
	private boolean mIsRun;
	private TimerTask getShortEpgTask = new TimerTask() {
		
		@Override
		public void run() {
//			Log.w("info"," 获取简单EPG 任务  run  start ** ");
			if (mIsRun) {
				if (mCount < GET_COUNT) {
					mCount ++;
					getCurSimpleEpg();
//					Log.w("info","  获取次数：mCount =  "+mCount);
				} else {
					stopGetShortEpgTask();
				}
			}
//			Log.w("info"," 获取简单EPG 任务  run  end mCount = "+mCount);
		}
	};
	@Override
	public void startGetShortEpgTask() {
		if(!mIsRun){
			try{
				 
//				Log.w("info"," 启动获取简单EPG任务...mTimer = "+mTimer);
//				mTimer.scheduleAtFixedRate(getShortEpgTask, PERIOD, PERIOD);
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
	@Override
	public void stopGetShortEpgTask() {
		mIsRun = false;
		mCount = 0;
	}
	@Override
	public ShortEPG getShortEPG() {
		return connectUtil.getShortEpg();
	}
	
	public void releaseGetShortEpgTask() {
//		Log.e("info"," releaseGetShortEpgTask()..");
//		getShortEpgTask.cancel();
		mTimer.cancel();
//		mTimer.purge(); // 从任务队列中移除所有任务
		mTimer = new Timer();
		mIsRun = false;
	}
	@Override
	public void getCurSimpleEpg() {
		connectUtil.sendGetShortEpgCmd();
	}
	
}
