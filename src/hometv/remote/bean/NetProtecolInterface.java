package hometv.remote.bean;

import java.util.ArrayList;
import java.util.HashMap;

import com.sdmc.dtv.programsend.Program;
import com.sdmc.dtv.programsend.ShortEPG;

/**
 * ����Э��ӿ�
 * ����������������Э����ʵ�ֵľ��巽���г�
 * @author fee
 *
 */
public interface NetProtecolInterface {
	 
	public boolean commonSendToServer(byte... cmdByte);
	/** ������Ƶ���� **/ 
	public void setAudioTrack(int audioThackId);
	/** �������ȡ��Ŀ����Ļ **/ 
	public void getCurProgramSubtitle();
	/** �������ȡ��Ŀ����Ƶ���� **/ 
	public void getCurProgramAudioTrack();
	/** �������ȡDVB ��Ŀ�б� **/ 
	public void getCurProgramList();
	/** �������ȡ��ǰ���ŵĽ�Ŀ��ID  **/ 
	public int getCurPlayingProgramId();
	/** �������ȡ���Ž�Ŀ��URL **/ 
	public void getURL();
	/** ������ ֹͣ���Ž�Ŀ **/ 
	public void stopPlay();
	public void getCurSimpleEpg();
	/**
	 * ���Ž�Ŀ
	 * @param programId  ��ǰ��Ŀ��ID
	 * @param programType  ��ǰ��Ŀ���ͣ��㲥������
	 * @param password   ��ĸ������
	 */
	public void programPlay(int programId, int programType, String password);
	/** ���Ѿ��õ���EPG�Ķ����л�ȡ ��EPG **/ 
	public ShortEPG getShortEPG();
	/** �༭��ǰ����Ŀ ��������С�ɾ����Ŀ��53��
			����ϲ����54��
			���ü�����55��
			����������56ϲ�� **/
	public void editProgram(byte option ,int programId);
	/** �������ȡ ĳһ�����ϸEPG  day = 0 Ϊ����**/ 
	public void getMoreEPG(int day);
	/** �������ȡEPGʱ�� **/ 
	public void getEPGTime();
	/** ������ȡ��EPG ���� **/ 
	public void startGetShortEpgTask();
	/** ��EPG��ȡ������ֹͣ������ **/ 
	public void stopGetShortEpgTask();
	/** ��EPG��ȡ�������ͷŸ����� **/ 
	public void releaseGetShortEpgTask();
	/**  ��ȡ �õ��Ľ�Ŀ���� ����TV��Radio��ϲ�� **/ 
	public HashMap<String, ArrayList<Program>> getProgramData();
	/**  ��ȡ��Ŀ�������ϸEPG **/ 
	public ArrayList<String> getDetailEpgs();
}
