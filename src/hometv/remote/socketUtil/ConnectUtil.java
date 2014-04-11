package hometv.remote.socketUtil;

import hometv.remote.bean.NetModeManager;
import hometv.remote.bean.ProtecolRemoteImp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import sdmc.com.tvlive.jni.UDPModule;

import com.sdmc.dtv.programsend.Program;
import com.sdmc.dtv.programsend.ProgramRunnable;
import com.sdmc.dtv.programsend.ShortEPG;
import com.sdmc.phone.stat.EventType;
import com.sdmc.phone.util.BytesMaker;

import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

/**
 * ���ӹ���
 * @author fee
 */
public class ConnectUtil implements Runnable {
	private static final String TAG = "ConnectUtil";
	/**
	 * ���ӷ������߳�
	 */
	private Thread connectServerThread = null;
	private static ConnectUtil connectUtil = null;
	public static boolean connect_success = false;
	public static boolean login_success = false;
	public static boolean stb_in_pvr = false;
	private int serverPort;
	/**
	 * ��ʼ������Э��,����ǰ�˷�����
	 * @param serverIP
	 * @param port
	 * @return �ɹ�����0��ʧ���򷵻�-1 note ���psRectΪSDI_NULL�����ʾȫ����ʾ
	 */
//	public synchronized void initControlServer(String serverIP, int port) {
//		if(isAutoConnecting){
//			return ;
//		}
//		ip = serverIP;
//		serverPort = port;
//		if (connectServerThread == null) {
//			connectServerThread = new Thread(this);
//			connectServerThread.start();
//		}
//	}
	public synchronized void initControlServer(String serverIP, int port,String curUser,String curPassword) {
		if(isAutoConnecting){
			return ;
		}
		ip = serverIP;
		serverPort = port;
		curLoginUser=curUser;
		curLoginPW=curPassword;
		if (connectServerThread == null) {
			connectServerThread = new Thread(this);
			connectServerThread.start();
		}
	}
	int rec = -2;
	/**
	 * ��¼ǰ�˷����� (ʵ������ƥ���Ӧ��Զ��BOX��Ϣ�Ƿ���ȷ)
	 * @param userName
	 * @param passWord
	 * @return �ɹ�����0��ʧ���򷵻�-1
	 */
//	public int logonServer(String userName, String passWord) {
//		 tempUser=userName;
//		 tempPassword=passWord;
//		 byte commad[]=new byte[17];
//		 commad[0]=Constant.LOGIN_SERVER_COMMAND;
//		 byte[] userByte=autoFillByteBit(userName, 8);
//		 byte[] pwByte=autoFillByteBit(passWord, 8);
//		 int userLen=userByte.length;
//		 System.arraycopy(userByte, 0, commad, 1, userLen);
//	     System.arraycopy(pwByte, 0, commad, userLen+1, pwByte.length);
//		 sendToServer(commad);
//		return rec;
//	}
	private  int logonServer() {
		 byte commad[]=new byte[17];
		 commad[0]=Constant.LOGIN_SERVER_COMMAND;
		 byte[] userByte=autoFillByteBit(curLoginUser, 8);
		 byte[] pwByte=autoFillByteBit(curLoginPW, 8);
		 int userLen=userByte.length;
		 System.arraycopy(userByte, 0, commad, 1, userLen);
	     System.arraycopy(pwByte, 0, commad, userLen+1, pwByte.length);
		 sendToServer(commad);
		return rec;
	}
	/**
	 * ��ȡ���ŵ�URL��ַ
	 * @param url
	 *            url���Ƿ��������ؿ��Բ��ŵ�����URL
	 * @return �ɹ�����0��ʧ���򷵻�-1
	 */
	public int getPlayUrl() {
		byte[] getUrl = { Constant.GETURL_COMMAND };
		sendToServer(getUrl);
		return 0;
	}

	/**
	 * ȥ��������
	 * 
	 * @param startPVR
	 *            1��ʾ������0��ʾ��
	 * @return 0��ʾ�޷�����(����û��AV����)��1��ʾ�ɲ���
	 */
	public int play(byte startPVR) {
		byte[] playCom = { Constant.PLAY_COMMAND, startPVR };
		sendToServer(playCom);
		return 0;
	}

	/**
	 * ֹͣ����
	 */
	public void stop() {
		byte[] stop = { Constant.STOP_COMMAND };
		sendToServer(stop);
	}

	/**
	 * ��ȡƵ���б�����
	 */
	public void getChannelList() {
		byte[] getChannel = { Constant.GET_CHANNEL_LIST };
		sendToServer(getChannel);
	}

	/**
	 * �ϴ�Ƶ���б�
	 * 
	 * @param channelName
	 * @param channelNoyy
	 */
	public void upLoadChannel(String channelName, int channelNo) {
		byte[] cmd = new byte[43];
		cmd[0] = Constant.UPLOAD_CHANNEL_CMD;
		byte[] channelByte = autoFillByteBit(channelName, 40);
		byte[] no = int2byteArray(channelNo, 2);
		System.arraycopy(no, 0, cmd, 1, no.length);
		System.arraycopy(channelByte, 0, cmd, no.length + 1, channelByte.length);
		sendToServer(cmd);
	}

	/**
	 * ɾ��ָ��Ƶ��
	 * 
	 * @param deleteChannelNo
	 */
	public void deleteChannel(int deleteChannelNo) {
		byte[] cmd = new byte[3];
		cmd[0] = Constant.DELETE_CHANNEL_CMD;
		byte[] no = int2byteArray(deleteChannelNo, 2);
		System.arraycopy(no, 0, cmd, 1, no.length);
		sendToServer(cmd);
	}

	public void changeChannelName(int targetChannelNo, String newChannelName) {
		byte[] cmd = new byte[43];
		cmd[0] = Constant.CHANGE_CHANNEL_NAME_CMD;
		byte[] no = int2byteArray(targetChannelNo, 2);
		byte[] channelByte = autoFillByteBit(newChannelName, 40);
		System.arraycopy(no, 0, cmd, 1, no.length);
		System.arraycopy(channelByte, 0, cmd, no.length + 1, channelByte.length);
		sendToServer(cmd);
	}

	/**
	 * ң����Զ�̿���
	 * 
	 * @param irCode
	 *            ģ��ң�ع��ܴ���
	 */
	public void irControl(byte irIndex) {
		byte irs[] = { Constant.REMOTE_CONTROL_COMMAND, irIndex };
		sendToServer(irs);
	}

	private Socket client = null;
	private Handler uiHandler;
	private InputStream fromServerIn = null;
	private OutputStream toServerOut = null;
	private String ip = null;
	private String curLoginUser,curLoginPW;
	public void setHandler(Handler fromMainThread) {
		this.uiHandler = fromMainThread;
	}
	private ConnectUtil() {
	}
	@Override
	public void run() {
		try {
			curConnectThreadId=Thread.currentThread().getId();
			Log.i("info"," connect server thread begin to run  curConnectThreadId = "+curConnectThreadId);
			client = new Socket(ip, serverPort); // �ڹ����߳������ӷ�����
			if (client != null) { // ��ʾ�����������������
				client.setTcpNoDelay(true);
				Log.i("info"," new Socket client success ");
				fromServerIn = client.getInputStream();// �ӷ������˶�ȡ���ݵ�������
				toServerOut = client.getOutputStream();// ��������������ݵ������
				manualStopConnect=false;
				sendToUI(Constant.CONNECT_SUCCESS);//���Զ�ȥ��½
				// �������϶�ȡ��������Ϣ���߳�
				 if(receiServerThread==null){
					 receiServerThread=new ReceiServerThread();
					 Log.w("info"," create new receiServerThread which to read from server  ..");
					 readServerWorkingFlag=true;
					 receiServerThread.start();
				 }
			}
		} catch (UnknownHostException e) {
			long occurThreadId=Thread.currentThread().getId();
			if(curConnectThreadId==occurThreadId){
			connect_success = false;
			login_success = false;
			// ֪ͨUI ����������
			sendToUI(Constant.HOST_ERROR);
		  }
		} catch (IOException e) {
			long occurThreadId=Thread.currentThread().getId();
			if(curConnectThreadId==occurThreadId){
			connect_success = false;
			login_success = false;
			Log.e("info"," connect server thread --> create socket occur IOExeption..");
			sendToUI(Constant.CONNECT_ERROR);// ����ʧ��
			}
		}
	  Log.i("info"," connect server  thread --> run end..thread id = "+Thread.currentThread().getId());
	}
	private long curConnectThreadId=0;
	private void sendToUI(int msgWhat) {
		switch (msgWhat) {
		case Constant.HOST_ERROR: // ����������
			uiHandler.sendEmptyMessage(Constant.HOST_ERROR);
			connectServerThread = null;
			break;
		case Constant.CONNECT_ERROR:// ����ʧ��
			uiHandler.sendEmptyMessage(Constant.CONNECT_ERROR);
			connectServerThread = null;
			break;
		case Constant.CONNECT_SUCCESS: // ���ӳɹ�
			uiHandler.sendEmptyMessage(Constant.CONNECT_SUCCESS);
			connect_success = true;
			connectServerThread=null;
			logonServer();
//			if(isAutoConnecting){
//				//�Զ���½
//				if(lastUser==null || lastPassword==null)break;
//				logonServer(lastUser, lastPassword);
//			}
			break;
		}
	}
	public synchronized static ConnectUtil getInstance() {
		if (connectUtil == null) {
			connectUtil = new ConnectUtil();
		}
		return connectUtil;
	}

	private void onDataReceiver(ArrayList<Byte> buffer) {
		byte commadType = cache.get(0);
//		byte commadType = src[0];
		Message toUiMessage = new Message();
		toUiMessage.what = commadType;
		switch (commadType) {
		case Constant.SERVER_RESPONSE_LOAD: // 22
//			byte recLoginState = src[1]; // 0û���豸��1�豸������,2�������3��¼�ɹ� ,4�Ѿ���½
			byte recLoginState = cache.get(1); // 0û���豸��1�豸������,2�������3��¼�ɹ� ,4�Ѿ���½
			toUiMessage.arg1 = recLoginState;
			if (recLoginState == 3) {
				login_success = true;
				getDvbProgramList();
				//��������ģʽ
				NetModeManager.getNetModeManager().switchCurNetMode(new ProtecolRemoteImp(this));
//				getChannelList();
				//��ѯ��ǰPVR״̬
//				commonSendToServer(Constant.START_STOP_QUERY_PVR,(byte)2);
			}else{
				if(login_success && recLoginState==1){
					//�����ǰ�Ѿ���½�ɹ�������������BOX�˲����ߣ�
					//��ʱ��������û�йر��ֻ��˵�Socket,������Ȼ�������ֵ�½�ɹ���״̬
					break;
				}
				login_success=false;
			}
			needClearCacheData = true;
			break;
//		case Constant.PLAY_COMMAND: // ��������Ӧ������Ϣ
//			byte retPlayState = cache.get(1); // 0��ʾ�޷�����(����û��AV����)��1��ʾ�ɲ���
//			toUiMessage.arg1 = retPlayState;
//			break;
//		case Constant.GETURL_COMMAND:// �����������Ƿ��ȡURL�ɹ���Ϣ 32
//			break;
//		case Constant.GET_CHANNEL_LIST:// 41 ����������Ƶ���б�
//			break;
//		case Constant.UPLOAD_CHANNEL_CMD:            // 42 ��������Ӧ�ϴ�Ƶ����Ϣ
//		case Constant.DELETE_CHANNEL_CMD:         	 // 43 ��������Ӧɾ��Ƶ��
//		case Constant.CHANGE_CHANNEL_NAME_CMD:    	 // 44 ��������Ӧ����Ƶ������
//		case Constant.BOX_RESPONSE_CUR_AV_CHANNEL:	 // 48 BOX����Ӧ��ǰAV-In ��·
//		case Constant.BOX_RESPONSE_IR_CONTROL:    	 // 84 BOX����Ӧң��ѧϰ����ң��ת��״̬
//		case Constant.BOX_RESPONSE_CAN_NOT_PLAY_HINT: //86 BOX��Ӧ��Ԥ�����ܲ���
//			toUiMessage.arg1=cache.get(1);
//			break;
		case Constant.BOX_RESPONSE_CUR_PVR_STATE:// 81 BOX����Ӧ��ǰPVR״̬
			byte cur_pvr_state = cache.get(1);
			if(cur_pvr_state==2){
				stb_in_pvr=true;
			}else{
				stb_in_pvr=false;
			}
			toUiMessage.arg1 = cur_pvr_state;
			break;					   //	0       1           2              3			
		case Constant.CMD_HEAD: //90   // [90] [���ݳ��� ��λ] [���ݳ��� ��λ] [newProtecolCmdType]
			int byteSize = cache.size();
			if( byteSize < 4 ){ 
				needClearCacheData = false;
				return; 
				}
			byte newProtecolCmdType = cache.get(3);
			byte hBit = cache.get(1);
			byte lBite = cache.get(2);
			
			int hBitLen = hBit << 8;
			int lBitLen = lBite <= 0 ? lBite+256 : lBite;
			int totalDataLen =  hBitLen + lBitLen;
			int cmdLen = totalDataLen +3; //һ��������ܳ���
			if( byteSize < cmdLen) {
				needClearCacheData = false;
				return;
			}
			switch (newProtecolCmdType) {
			case Constant.TVCAST_CMD_TYPE: //����Զ����TV cast�Ľ���
 
				int tvcastLen = totalDataLen -1;
				byte [] tvcastData =  new byte[tvcastLen];
				for (int i = 0; i< tvcastLen ;i ++){
					tvcastData [i] = cache.get(i+4);
				}
				//���� tvcast ����
			    dealWithTvcastData(tvcastData);
				if(byteSize > cmdLen){
					int restBytesSize = byteSize -cmdLen;
					
					byte [] restBuffer = new byte[restBytesSize];
					 for (int i=0;  i < restBytesSize; i++	){
						 restBuffer [i] = cache.get(i+cmdLen);
					 }
					  cache.clear();
					  for (int i=0;i<restBytesSize;i++){
						  cache.add(restBuffer[i]);
					  }
					 onDataReceiver(cache);
				  }else {
					  needClearCacheData = true;
				  }
				break;
			case 51:
				break;
			}
			
			break;
		}// end/break switch
		if(commadType != 90)
			uiHandler.sendMessage(toUiMessage);
	   if(needClearCacheData)
		   cache.clear();
	}
	private boolean needClearCacheData = false;
	private ArrayList<Byte> cache = new ArrayList<Byte>();
	/**
	 * ���ϴӷ������϶�ȡ��Ϣ���߳� extends Thread
	 */
	private ReceiServerThread receiServerThread = null;
	boolean readServerWorkingFlag = true;
	private class ReceiServerThread extends Thread {
		byte[] receive = new byte[512]; // һ�� 512Byte������
		@Override
		public void run() {
			while (readServerWorkingFlag) {
//				System.out.println("read from server the thread id = "+this.getId());
				try {
					int len = fromServerIn.read(receive);
					for (int i = 0; i < len; i++) {
						cache.add(receive[i]);
					}
					if(len==-1){
						// =-1ʱ���������Ͽ�������
						Log.e("info"," =-1   disconnect to the server...");
						readServerWorkingFlag = false;
						connect_success = false;
						login_success = false;
						connectServerThread = null;
						NetModeManager.getNetModeManager().switchCurNetMode(null);
						if(!manualStopConnect)
							 uiHandler.sendEmptyMessage(Constant.CONNECT_BREAK);
						break;
					}
					onDataReceiver(cache);
						
				   } catch (IOException e) {
					Log.e("info", "ReceiServerThread--> fromServerIn.read  occur IOExeption.. is client connected = "+client.isConnected());
					NetModeManager.getNetModeManager().switchCurNetMode(null);
					uiHandler.sendEmptyMessage(Constant.CONNECT_BREAK);
					readServerWorkingFlag = false;
					connect_success = false;
					login_success = false;
					connectServerThread = null;
					break;
				}
			}// end while circle
			Log.w("info","ReceiServerThread--->  run to the end...");
		}
	}
	private boolean isAutoConnecting=false;
	public void autoToConnectServer(){
//		release(false);
		if(autoConnectThread==null){
			autoConnectThread=new AutoConnectThread();
			autoConnectThread.start();
		}
	}
	boolean manualStopConnect=false;
	public void release(boolean exit){
		manualStopConnect=true;
		readServerWorkingFlag = false;
		if (receiServerThread != null) {
			 Log.i("info","release() receiServerThread.isAlive() --> "+receiServerThread.isAlive());
			receiServerThread = null;
		}
		if (connectServerThread != null) {
			 Log.i("info","release() connectServerThread.isAlive() --> "+connectServerThread.isAlive());
			connectServerThread = null;
		}
		if (client != null) {
			try {
				client.shutdownInput();;
				client.shutdownOutput();
				client.close();
			} catch (IOException e) {
				client=null;
				Log.e("info"," release() client.close occur IOExeption...");
			}
		}
		connect_success = false;
		login_success = false;
		if(exit){
			Process.killProcess(Process.myPid());
		}
	}
 
	private boolean sendToServer(byte[] command) {
		boolean isSendSuc =false;
		try {
			if (toServerOut != null) {
//				Log.w("info","sendToServer  -- > command= "+command[0]);
//				triggerTimeOutMsg();
				toServerOut.write(command);
				toServerOut.flush();
				isSendSuc = true;
			}
		} catch (IOException e) {
			Log.e("info"," sendToServer  occur IOException ...");
			isSendSuc = false;
		}
		return isSendSuc;
	}

	public boolean commonSendToServer(byte... cmds) {
		return sendToServer(cmds);
	}
	private void triggerTimeOutMsg(){
		this.uiHandler.sendEmptyMessageDelayed(Constant.REQUEST_TIME_OUT, 15*1000);
	}
	public String byte2Str(byte[] src) {
		return new String(src).toString().trim();
	}

	private byte[] autoFillByteBit(String str, int bitCount) {
		byte[] strByte = str.getBytes();//Ĭ����UTF-8ת��
		int byteLen = strByte.length;// ԭ���ֽڳ���
		if (byteLen == bitCount) {
			return strByte;
		}
		return Arrays.copyOf(strByte, bitCount);
	}

	/**
	 * ���� ת����2���ֽڵ��ֽ�����
	 * @param src
	 * @param byteLen
	 * @return
	 */
	private byte[] int2byteArray(int src, int byteLen) {
		byte[] result = new byte[byteLen];
		result[0] = (byte) ((src >> 8) & 255);
		result[1] = (byte) (src & 255);
		return result;
	}
	/**
	 * @param lBit
	 * @param hBit
	 * @return
	 */
	private int t2BitByteToInt(byte lBit, byte hBit) {
//		System.err.println("lBit= "+lBit +"  hBit= "+hBit);
		if (hBit == 0 && lBit == 0)
			return 0;
		if (hBit == 0 && lBit != 0) //
			return lBit&0xff;
		
		return (hBit*256) + (lBit&0xff);
	}
	
	private int towBitByteToInt(byte hBit, byte lBit) {
		if (hBit == 0 && lBit == 0)
			return 0;
		if (hBit == 0 && lBit != 0)
			return lBit;
		return (hBit << 8) | lBit;
	}
	public void enableSomceInTime(int msgWhat,long delayTime){
		boolean hasMsgAlready=this.uiHandler.hasMessages(msgWhat);
		if(hasMsgAlready){
			return;
		}
		this.uiHandler.sendEmptyMessageDelayed(msgWhat, delayTime);
	}
	private AutoConnectThread autoConnectThread=null;
	private class AutoConnectThread extends Thread{
		@Override
		public void run() {
			super.run();
			isAutoConnecting=true;
			int i=0;
			while(i<3){
				Log.i("info","AutoConnectThread-- >  run()");
			try {
				client=new Socket(ip, serverPort);
				if (client != null) { // ��ʾ�����������������
					i=3;//�Զ�������һ�����ӳɹ���ֹͣ�Զ�����
					Log.i("info","AutoConnectThread --> new Socket client success  thread id = "+Thread.currentThread().getId());
					fromServerIn = client.getInputStream();// �ӷ������˶�ȡ���ݵ�������
					toServerOut = client.getOutputStream();// ��������������ݵ������
					manualStopConnect=false;
					sendToUI(Constant.CONNECT_SUCCESS);
					// �������϶�ȡ��������Ϣ���߳�
					 if(receiServerThread==null){
						 receiServerThread=new ReceiServerThread();
						 Log.w("info","AutoConnectThread --> create new receiServerThread read from server  ..");
						 readServerWorkingFlag=true;
						 receiServerThread.start();
					 }
					break; 
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				Log.e("info","auto to connect server thread occur IOException...");
			}
			i++;
		}//end while
			Log.i("info","AutoConnectThread --> run to end..connect_success= "+connect_success);
			isAutoConnecting=false;
		 //�Զ�����������Ȼû�����ӳɹ�����֪ͨUI
			if(!connect_success)
				uiHandler.sendEmptyMessage(Constant.CONNECT_REALY_DISCONNECT);
			autoConnectThread=null;
	  }
	}
	private ArrayList<Program> mAllList = new ArrayList<Program>();
	private ArrayList<Program> mTVList = new ArrayList<Program>();
	private ArrayList<Program> mRadioList = new ArrayList<Program>();
	private ArrayList<Program> mFavorList = new ArrayList<Program>();
	private ArrayList<String> mEPGList =new ArrayList<String>();
	private int mCurrentPlayID;
	private ShortEPG mShortEPG;
	private boolean isProgramListNeedUpdate = true;
	private boolean isDetailEpgNeedUpdate = true;
	/**
	 * 
	 * @param tvcastData  ȥ��4���ֽں��ȫTv cast��Э������
	 * @throws IOException
	 */
	private void dealWithTvcastData(byte[] tvcastData){
		int tvcastDataLen = tvcastData.length;
		byte tvcastType = tvcastData[0];
		switch (tvcastType) {
		case EventType.EVENT_PROGRAM_START: //���յ���Ŀ�б�ķ���
			if( isProgramListNeedUpdate){
				mAllList.clear();
				mTVList.clear();
				mRadioList.clear();
				mFavorList.clear();
				isProgramListNeedUpdate = false;
			}
			byte hasProgramList = tvcastData [1];
			if( hasProgramList == 1){
				//�޽�Ŀ 
				
			}else{
				if(programListCount == 0){
					programListCount = BytesMaker.bytes2int(tvcastData, 2);
					Log.w("info"," ��Ŀ�б��С   �� "+programListCount);
				}
				byte [] programDataByte = new byte[tvcastDataLen -6];
				System.arraycopy(tvcastData, 6, programDataByte, 0, tvcastDataLen -6);
				//���� ����
				Program program = readProgram(programDataByte);
				if (program != null) {
					mAllList.add(program);
					if (program.getType() == TYPE_TV){
						mTVList.add(program);
					} else {
						mRadioList.add(program);
					}
					if (program.isFavor()){
						mFavorList.add(program);
					}
					curProgramListSizeindex ++;
//					Log.w("info"," ��Ӵ����� "+curProgramListSizeindex);
				}
			}
			if(curProgramListSizeindex == programListCount){
				curProgramListSizeindex = 0;
				programListCount = 0 ;
				isProgramListNeedUpdate = true;
				sendToLocalHandler(ProgramRunnable.PROGRAM_REFRESH, 0); //���߽����н�Ŀ����
			}
			break;
		case EventType.EVENT_CURRENT_PLAY: //��ǰ������Ϣ
			int curPlayId = BytesMaker.bytes2int(tvcastData, 1);	
			Log.e("info","ConnectUtil ��ǰ���Ž�Ŀ��ID = "+curPlayId);
			if(curPlayId == -1)break;
			if (mCurrentPlayID != curPlayId){
				mCurrentPlayID = curPlayId;
				sendToLocalHandler(ProgramRunnable.CURRENT_PROGRAM, mCurrentPlayID); 
			}
			break;
		case EventType.EVENT_CURRENT_EPG: //��ǰ��EPG �ӵ����ʱ����ʾ�м�EPG��Ϣ
			byte [] curEpgInfo = new byte [ tvcastDataLen -1];
			System.arraycopy(tvcastData, 1, curEpgInfo, 0, tvcastDataLen -1);
			mShortEPG = new ShortEPG(curEpgInfo);
			sendMessage(ProgramRunnable.CURRENT_EPG, mShortEPG);
			break;
		case EventType.EVENT_MORE_EPG_START: //��ǰ��Ŀ��ϸEPG 
			if(isDetailEpgNeedUpdate){
				mEPGList.clear();
				isDetailEpgNeedUpdate = false; 
			}
			byte isHasDetailEpg = tvcastData[1];
			if(isHasDetailEpg == 2){
				if(detailEpgCount == 0){
					byte hBit = tvcastData[2];
					byte lBit = tvcastData[3];
					int hValue = hBit << 8;
					int lValue = lBit <= 0 ? lBit+256 :lBit;
					detailEpgCount = hValue +lValue;
				}
				byte [] detailEpgInfo = new byte[tvcastDataLen - 4];
				System.arraycopy(tvcastData, 4, detailEpgInfo, 0, tvcastDataLen - 4);
				String epgInfo = new String(detailEpgInfo);
				 
				mEPGList.add(epgInfo);
				curDetailEpgInfoIndex ++;
			}else{
				//==1 ����ϸEPG��Ϣ
			}
			 
			 if(curDetailEpgInfoIndex == detailEpgCount){ //��ʾ��������,ˢ��UI
				  
				 sendToLocalHandler(ProgramRunnable.MSG_GET_EPG, 0);
				 curDetailEpgInfoIndex = 0;
				 detailEpgCount = 0;
				 isDetailEpgNeedUpdate = true;
			 }
			break;
		case EventType.EVENT_MORE_EPG_NULL: //��ǰ��Ŀ��EPG
			sendToLocalHandler(ProgramRunnable.MSG_GET_EPG_NULL, 0);
			break;
		case EventType.EVENT_EDIT_SUCCESS:
			sendToLocalHandler(ProgramRunnable.MSG_EDIT_Result, 1);
			break;
		case EventType.EVENT_EDIT_FALSE:
			Log.i("info", TAG+" ---> EDIT_FALSE");
			sendToLocalHandler(ProgramRunnable.MSG_EDIT_Result, 0);
			break;
		case EventType.EVENT_GET_AUDIO_TRACK: // �õ� �� �������
		      byte audioTrackIndex = tvcastData[1];   //��ǰ�������
			sendToLocalHandler(ProgramRunnable.INDEX_AUDIO_TRACK, audioTrackIndex);
			break;
		case EventType.EVENT_PROGRAM_PLAY:  //�������Ӧ����������
			  
			  // [ 0��ʾ�޷����ţ�1��ʾ�ɲ��ţ�2��ʾ��̨��3��ʾ������� ]
			int result = tvcastData[1];
			Log.e("info", TAG+ " [ 0��ʾ�޷����ţ�1��ʾ�ɲ��ţ�2��ʾ��̨��3��ʾ������� ] > ��Ӧ���Ž�� = " + result);
			sendToLocalHandler(ProgramRunnable.MSG_PLAY_PROGRAM_RESULT, result);
			break;
		case EventType.EVENT_GET_EPGTIME: //��ȡEPGʱ��
			 byte [] egpTime = new byte[tvcastDataLen - 1];
			 System.arraycopy(tvcastData, 1, egpTime, 0, tvcastDataLen - 1);
			 
			String epgTime   = new String(egpTime);
			 
			sendMessage(ProgramRunnable.MSG_GET_EPGTIME, epgTime);
			break;
		case EventType.EVENT_GET_URL: //��ȡ ���ŵ�ַURL
			byte readUrlResult = tvcastData[1]; //[ 0��ʾδ׼���ã�1��ʾ��׼���ã�2��ʾ�������� ]
//			Log.i("info",TAG+" ����URL��ַ...readUrlResult = "+readUrlResult);
			switch (readUrlResult) {
			case 1: //�Ѿ�׼����
				byte[] urlByte = new byte [tvcastDataLen - 2];
				System.arraycopy(tvcastData, 2, urlByte, 0, tvcastDataLen - 2); 
				String url = new String(urlByte);
				sendMessage(ProgramRunnable.MSG_GET_URL, 1, url);
				break;

			default:
				sendToLocalHandler(ProgramRunnable.MSG_GET_URL, readUrlResult);
				break;
			}
			break;
		case EventType.EVENT_STOP_PLAY:
			sendToLocalHandler(ProgramRunnable.MSG_STOP_PLAY, 0);
			break;
		
		default:
			break;
		}
		
	}
	private int detailEpgCount =0;
	private int programListCount = 0;
	private int curDetailEpgInfoIndex = 0;
	private int curProgramListSizeindex = 0;
	public static final int TYPE_TV = 0;
	/** ��ȡ��Ŀ�б�����.. **/
	public void getDvbProgramList(){
		byte [] cmd = {
				EventType.EVENT_PHONE_GET_PROGRAMLIST,
				1,
				0
		};
		 
	createBytesAndSendToServer(cmd);
	}
	/** ��ȡ��Ƶ���� **/
	public void getAudioTrackIndex(){
		byte[] cmd ={
			EventType.EVENT_GET_AUDIO_TRACK,
			1,
			0
		};
		createBytesAndSendToServer(cmd);
	}
	/** ������Ƶ����**/
	public void setAudioTrack(byte targetTrackIndex){
		byte [] cmd ={
				EventType.EVENT_SET_AUDIO_TRACK,
				1,
				targetTrackIndex
		};
		createBytesAndSendToServer(cmd);
	}
	/**  ��ȡEPGʱ�� **/
	public void getEpgTime(){
		byte[] cmd ={
				EventType.EVENT_GET_EPGTIME,
				1,
				0 // ��������
			};
		createBytesAndSendToServer(cmd);
	}
	/** ��ȡDVB ����URL��ַ  **/
	public void getProgramPlayUrl(){
		byte[] cmd ={
				EventType.EVENT_GET_URL,
				1,
				0
			};
		createBytesAndSendToServer(cmd);
	}
	/**
	 * @param programId
	 * @param programType 1��ʾ���ͣ�0��ʾ��
	 * @param password
	 */
public void programPlay(int programId, byte programType, String password) {
		 byte [] parentLockPwByte = password.getBytes();
		 byte [] cmds = new byte [7+parentLockPwByte.length];
		 cmds [0] = EventType.EVENT_PROGRAM_PLAY;
		 cmds [1] = (byte) (5 + parentLockPwByte.length);
		 byte [] programIdByte = new byte[4];
		 BytesMaker.int2bytes(programId, programIdByte, 0);
		 System.arraycopy(programIdByte, 0, cmds, 2, 4);
		 cmds [6] = 0;
		 System.arraycopy(parentLockPwByte, 0, cmds, 7, parentLockPwByte.length);
		  
		 createBytesAndSendToServer(cmds);
		 //���������ڴˣ�����ֻ�Ǹ�TV cast��������ٷ�UDP��������
		 UDPModule.init(ip, serverPort, curLoginUser,curLoginPW);
		  
	}
	/** ��ȡ������ĳ�����ϸEPG **/
	public void getCurDayDetailEpg(byte day){
		byte cmds[] = {
				EventType.EVENT_GET_MORE_EPG,
				1,
				day
		};
		createBytesAndSendToServer(cmds);
	}
	/** Ŀǰֻ����ϲ�� **/
	public void editProgram(byte option,int programId){
		
		byte cmds [] = new byte [6];
		cmds[0] = option;
		cmds[1] = 4;
		BytesMaker.int2bytes(programId, cmds, 2);
		createBytesAndSendToServer(cmds);
	}
	/** ֹͣDVB�Ľ�Ŀ���ţ��˴�Զ��ҲҪ����ֹͣ���� **/
	public void stopProgramPlay(){
		byte cmds [] ={
				EventType.EVENT_STOP_PLAY,
				1,	
				0
		};
		createBytesAndSendToServer(cmds);
		
	}
	 
	public void sendGetShortEpgCmd(){
		byte cmds [] ={
				EventType.EVENT_CURRENT_EPG,
				1,	
				0
		};
		createBytesAndSendToServer(cmds);
	}
// ****************  ��ȡ�Ѿ��õ�����Դ��������������������������������������
	public HashMap<String, ArrayList<Program>> getProgramData(){
		HashMap<String, ArrayList<Program>> data = new HashMap<String, ArrayList<Program>>();
		data.put(Constant.PROGRAM_TV_KEY, mTVList);
		data.put(Constant.PROGRAM_RADIO_KEY, mRadioList);
		data.put(Constant.PROGRAM_FAVORATE_KEY, mFavorList);
		data.put(Constant.PROGRAM_ALL, mAllList);
		return data;
	}
	public ShortEPG getShortEpg(){
		return mShortEPG;
	}
	public int getCurPlayingPorgramId(){
		return mCurrentPlayID;
	}
	public ArrayList<String> getDetailEpg(){
		return mEPGList;
	}
	// ****************  ��ȡ�Ѿ��õ�����Դ��������������������������������������������������������
	
	private Program readProgram(byte[] programData){
		int programId = BytesMaker.bytes2int(programData, 0); //[0],[1],[2],[3[] 4���ֽڴ���ID
		int programIdType = programData[4];
		boolean isScrambler = programData[5] == 1; //�Ƿ����
		boolean isFavor = programData[6] == 1; //ϲ��
		boolean isLock = programData[7] == 1; // ��
		boolean isSkip = programData[8] == 1; //����
		String name = new String(programData, 9, programData.length - 9);
//		System.err.println(" ���ӽ�Ŀ name = "+name.trim());
		return new Program(programId, name, programIdType, isScrambler, isFavor, isLock, isSkip);
	}
 
	private void sendToLocalHandler(int msgWhat,int arg1){
		sendMessage(msgWhat, arg1, null);
	}
	
 
	private void sendMessage(int what, Object obj){
		sendMessage(what, 0, obj);
	}
	private Handler mLocalHandler;
	public void setLocalHandler(Handler localHandelr){
		this.mLocalHandler = localHandelr;
	}
	private void sendMessage(int what, int arg1, Object obj){
		if (mLocalHandler != null){
			Message msg = new Message();
			msg.what = what;
			msg.arg1 = arg1;
			msg.obj = obj;
			mLocalHandler.sendMessage(msg);
		}
	}
	/** 
	  * ��byte����ת��Ϊint���� 
	  * @param b �ֽ����� 
	  * @return ���ɵ�int���� 
	  */  
	  public static int byteToInt2(byte[] b){  
	      return (((int)b[0]) << 24) + (((int)b[1]) << 16) + (((int)b[2]) << 8) + b[3];  
	  }
	  private void printBuffer(String titel,byte [] buffer,int len){
			 StringBuffer sb = new StringBuffer();
			 for (int i =0; i<len;i++){
				 sb.append(" * "+buffer[i]);
			 }
			 Log.i("info",titel+ ": <<  "+sb.toString() +" >>");
		 }
	 
		/**
		 * ��TV Cast������ (����) �Ӱ� ת����������
		 * @param fromTvcast
		 */
	 private void createBytesAndSendToServer(byte [] toTvcastCmd){
		 int toTvcastByteLen = toTvcastCmd.length;
		 
		 byte [] cmds = new byte[toTvcastByteLen +4];
		 cmds[0] = Constant.CMD_HEAD; //90
		 //���ݳ��� 2 ���ֽ�
		 int dataLen = toTvcastByteLen +1; // tvcast ȫ�ֽڳ��� + һ���ֽڵ���������
		 cmds [1] = (byte) (dataLen >> 8); //�Ÿ�λ
		 cmds [2] =(byte) dataLen;
//		 Log.i("info",CLASS_TAG +" ���ݳ��ȵĸ�λ = "+cmds[1] +"���ݳ��� ��λ = "+cmds[2]);
		//��������
		 cmds [3] = Constant.TVCAST_CMD_TYPE; 
		// ����
		 System.arraycopy(toTvcastCmd, 0, cmds, 4, toTvcastByteLen);
//		 Log.e("info"," ****createBytesAndSendToRemote ����  �ֽ���   = "+cmds.length);
		 sendToServer(cmds);
	 }
		private byte[] receiver(InputStream in,int needLen){
			byte [] back = new byte [needLen];
			int pos = 0;
			while( needLen >0){
				byte [] temp = new byte [needLen];
				try {
					int len = in.read(temp);
					needLen = needLen -len;
					System.arraycopy(temp, 0, back, pos, len);
					pos += len;
				} catch (IOException e) {
					 //?
				}
				
			}
			return back;
		}
//	 private void createBytesAndSendToRemoteJustByte(byte... fromTvcast){
//		 int fromTvcastByteLen = fromTvcast.length;
//		 
//		 byte [] cmds = new byte[fromTvcastByteLen +4];
//		 cmds[0] = CMD_HEAD; //90
//		 //���ݳ��� 2 ���ֽ�
//		 int dataLen = fromTvcastByteLen +1; // tvcast ȫ�ֽڳ��� + һ���ֽڵ���������
//		 cmds [1] = (byte) (dataLen >> 8); //�Ÿ�λ
//		 cmds [2] =(byte) dataLen;
////		 Log.i("info",CLASS_TAG +" ���ݳ��ȵĸ�λ = "+cmds[1] +"���ݳ��� ��λ = "+cmds[2]);
//		//��������
//		 cmds [3] = 50; 
//		// ����
//		 System.arraycopy(fromTvcast, 0, cmds, 4, fromTvcastByteLen);
////		 Log.e("info"," ###createBytesAndSendToRemoteJustByte  �����ֽ���  = "+cmds.length);
//		 sendToServerUseC(cmds);
//	 }
}
// ******************************* end ************************************ 

