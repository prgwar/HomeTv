package hometv.remote.bean;

import java.util.ArrayList;
import java.util.HashMap;

import com.sdmc.dtv.programsend.Program;
import com.sdmc.dtv.programsend.ShortEPG;

/**
 * 网络协议接口
 * 将局域网与外网的协议中实现的具体方法列出
 * @author fee
 *
 */
public interface NetProtecolInterface {
	 
	public boolean commonSendToServer(byte... cmdByte);
	/** 设置音频声道 **/ 
	public void setAudioTrack(int audioThackId);
	/** 发命令获取节目的字幕 **/ 
	public void getCurProgramSubtitle();
	/** 发命令获取节目的音频声道 **/ 
	public void getCurProgramAudioTrack();
	/** 发命令获取DVB 节目列表 **/ 
	public void getCurProgramList();
	/** 发命令获取当前播放的节目的ID  **/ 
	public int getCurPlayingProgramId();
	/** 发命令获取播放节目的URL **/ 
	public void getURL();
	/** 发命令 停止播放节目 **/ 
	public void stopPlay();
	public void getCurSimpleEpg();
	/**
	 * 播放节目
	 * @param programId  当前节目的ID
	 * @param programType  当前节目类型：广播、电视
	 * @param password   父母锁密码
	 */
	public void programPlay(int programId, int programType, String password);
	/** 从已经得到简单EPG的对象中获取 简单EPG **/ 
	public ShortEPG getShortEPG();
	/** 编辑当前将节目 具体操作有、删除节目：53；
			设置喜爱：54；
			设置加锁：55；
			设置跳过：56喜爱 **/
	public void editProgram(byte option ,int programId);
	/** 发命令获取 某一天的详细EPG  day = 0 为当天**/ 
	public void getMoreEPG(int day);
	/** 发命令获取EPG时间 **/ 
	public void getEPGTime();
	/** 开启获取简单EPG 任务 **/ 
	public void startGetShortEpgTask();
	/** 简单EPG获取结束后，停止该任务 **/ 
	public void stopGetShortEpgTask();
	/** 简单EPG获取结束后，释放该任务 **/ 
	public void releaseGetShortEpgTask();
	/**  获取 得到的节目数据 包括TV、Radio、喜好 **/ 
	public HashMap<String, ArrayList<Program>> getProgramData();
	/**  获取节目当天的详细EPG **/ 
	public ArrayList<String> getDetailEpgs();
}
