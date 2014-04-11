package com.sdmc.dtv.programsend;

import hometv.remote.socketUtil.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sdmc.phone.stat.EventType;
import com.sdmc.phone.util.BytesMaker;
import com.sdmc.phone.util.NetConnect;

public class ProgramRunnable implements Runnable{

	public static final int PROGRAM_REFRESH = 0x070;
	public static final int CURRENT_PROGRAM = 0x071;
	public static final int CURRENT_EPG = 0x072;
	public static final int INDEX_AUDIO_TRACK = 0x075;
	public static final int MSG_GET_EPG = 0x078;
	public static final int MSG_GET_EPG_NULL = 0x079;
	public static final int MSG_PLAY_PROGRAM_RESULT = 0x080;
	public static final int MSG_GET_EPGTIME = 0x081;
	public static final int MSG_GET_URL = 0x082;
	public static final int MSG_STOP_PLAY = 0x083;
	public static final int MSG_EDIT_Result = 0x084;
	
	private static final String TAG = "ProgramRunnable";
	public static final int TYPE_TV = 0;
	public static final int TYPE_RADIO = 1;
	public static final int TYPE_FAV = 2;
	public static final int TYPE_NOTE_SCRAMBLER = 10;
	public static final int TYPE_NOTE_FREE = 11;
	
	public static final int FLAG_NO_PREPARE = 0;
	public static final int FLAG_PREPARE = 1;
	public static final int FLAG_ERROR = 2;
	
	public static final int PLAY_RESULT_PLAY_FALSE = 0; 
	public static final int PLAY_RESULT_TV_RECORD = 1;
	public static final int PLAY_RESULT_TV_CHANGE = 2;
	public static final int PLAY_RESULT_PASSWRONG = 3;
	public static final int RESULT_SUCCEESS = 1;
	
	private boolean mIsRun = false;
//	private boolean mIsCurrentPlayRefreshing = false;
	
	private int mCurrentPlayID;
	private ShortEPG mShortEPG;
	private ArrayList<Program> mAllList;
	private ArrayList<Program> mTVList;
	private ArrayList<Program> mRadioList;
	private ArrayList<Program> mFavorList;
	private ArrayList<String> mEPGList;
	private Handler mHandler;
//	private Handler mEPGHandler;
//	private boolean mCanSend;
	
	private static ProgramRunnable mInstance;
	
	private ProgramRunnable() {
		mShortEPG = new ShortEPG();
		mAllList = new ArrayList<Program>();
		mTVList = new ArrayList<Program>();
		mRadioList = new ArrayList<Program>();
		mFavorList = new ArrayList<Program>();
		mEPGList = new ArrayList<String>();
	}
	
	public static ProgramRunnable instance() {
		if (mInstance == null){
			mInstance = new ProgramRunnable();
		}
		return mInstance;
	}
	
	public static void init() {
		Log.e(TAG, "ProgramRunnable ---> init");
		mInstance = ProgramRunnable.instance();
		if (!mInstance.isRun()) {
			Log.i(TAG, "Thread start");
			new Thread(mInstance).start();
		}
	}
	
	@Override
	public void run() {
		Log.i(TAG, "run");
		mIsRun = true;
		int response;
		try {
			while ((response = NetConnect.mIn.read()) != -1){
				switch (response) {
				case EventType.EVENT_PROGRAM_START: //接收到节目列表的发送
					mAllList.clear();
					mTVList.clear();
					mRadioList.clear();
					mFavorList.clear();
					byte[] bytes = new byte[4];
					NetConnect.mIn.read(bytes);
					int length = BytesMaker.bytes2int(bytes, 0);
					Log.i("info", TAG +"-----------> length:" + length);
					for(int i = 0; i < length; i ++){
						Program program = readProgram();
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
						}else{
							break;
						}
					}
					sendMessage(PROGRAM_REFRESH, 0);
					break;
				case EventType.EVENT_CURRENT_PLAY: //当前播放节目ID
					byte[] bytes1 = new byte[4];
					NetConnect.mIn.read(bytes1);
					if (mCurrentPlayID != BytesMaker.bytes2int(bytes1, 0)){
						mCurrentPlayID = BytesMaker.bytes2int(bytes1, 0);
						sendMessage(CURRENT_PROGRAM, mCurrentPlayID);
					}
					break;
				case EventType.EVENT_CURRENT_EPG: //当前的EPG
					Log.i("ProgramRunnable", "EVENT_CURRENT_EPG");
					byte[] shortEPGLengthBytes = new byte[4];
					NetConnect.mIn.read(shortEPGLengthBytes);
					int shortEPGLength = BytesMaker.bytes2int(shortEPGLengthBytes, 0);
					Log.i("ProgramRunnable", "shortEPGLength : " + shortEPGLength);
					if (shortEPGLength <= 0){
						mShortEPG = new ShortEPG();
					} else {
						byte[] shortEPGbytes = new byte[shortEPGLength];
						NetConnect.mIn.read(shortEPGbytes);
						mShortEPG = new ShortEPG(shortEPGbytes);
					}
					sendMessage(CURRENT_EPG, mShortEPG);
					break;
				case EventType.EVENT_MORE_EPG_START: //当前节目详细EPG 
					mEPGList.clear();
					int length3 = NetConnect.mIn.read();
					Log.i(TAG, "egp list length:" + length3);
					for (int i = 0; i < length3; i ++){
						String epg = readEpg();
						Log.i("ProgramRunnable epg : ", epg);
						if (epg != null) {
							mEPGList.add(epg);
						} else {
							break;
						}
					}
					sendMessage(MSG_GET_EPG, 0);
					break;
				case EventType.EVENT_MORE_EPG_NULL: //当前节目无EPG
					Log.i(TAG, "no epg info");
					sendMessage(MSG_GET_EPG_NULL, 0);
					break;
				case EventType.EVENT_EDIT_SUCCESS:
					Log.i(TAG, "EDIT_SUCCESS");
					refreshFavList();
					sendMessage(MSG_EDIT_Result, 1);
					break;
				case EventType.EVENT_EDIT_FALSE:
					Log.i(TAG, "EDIT_FALSE");
					sendMessage(MSG_EDIT_Result, 0);
					break;
				case EventType.EVENT_GET_AUDIO_TRACK:
					int audioTrackIndex = NetConnect.mIn.read();
					sendMessage(INDEX_AUDIO_TRACK, audioTrackIndex);
					break;
				case EventType.EVENT_PROGRAM_PLAY:
					int result = NetConnect.mIn.read();
					Log.i(TAG, "MSG_PLAY_PROGRAM_RESULT = " + result);
					sendMessage(MSG_PLAY_PROGRAM_RESULT, result);
					break;
				case EventType.EVENT_GET_EPGTIME: //获取EPG时间
					int epgTimelength = NetConnect.mIn.read();
					String epgTime = "";
					if (epgTimelength > 0) {
						byte[] epgTimeBytes = new byte[epgTimelength];
						NetConnect.mIn.read(epgTimeBytes);
						epgTime = new String(epgTimeBytes);
					}
					sendMessage(MSG_GET_EPGTIME, epgTime);
					break;
				case EventType.EVENT_GET_URL: //获取 播放地址URL
					int flag = NetConnect.mIn.read();
					Log.e("info"," 接收URL地址... flag = "+flag);
					switch (flag) {
					case FLAG_PREPARE:
						int urlLength = NetConnect.mIn.read();
						byte[] urlBytes = new byte[urlLength];
						NetConnect.mIn.read(urlBytes);
						String url = new String(urlBytes);
						sendMessage(MSG_GET_URL, flag, url);
						break;

					default:
						sendMessage(MSG_GET_URL, flag);
						break;
					}
					break;
				case EventType.EVENT_STOP_PLAY:
					sendMessage(MSG_STOP_PLAY, 0);
					break;
				
				default:
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		mIsRun = false;
	}
	
	public void setHandler(Handler handler) {
		mHandler = handler;
	}
	
	public void removeHandler(Handler handler) {
		if (mHandler == handler) {
			mHandler = null;
		}
	}
	
	private Program readProgram() throws IOException {
		int programSize = NetConnect.mIn.read();
		byte[] bytes = new byte[programSize];
		if (NetConnect.mIn.read(bytes) == programSize) {
			int id = BytesMaker.bytes2int(bytes, 0);
			int type = bytes[4];
			boolean isScrambler = bytes[5] == 1;
			boolean isFavor = bytes[6] == 1;
			boolean isLock = bytes[7] == 1;
			boolean isSkip = bytes[8] == 1;
			String name = new String(bytes, 9, programSize - 9);
			return new Program(id, name, type, isScrambler, isFavor, isLock, isSkip);
		}
		return null;
	}
	
	private String readEpg() throws IOException {
		int size = NetConnect.mIn.read();
		byte[] bytes = new byte[size];
		if (NetConnect.mIn.read(bytes) == size) {
			String epg = new String(bytes);
			return epg;
		}
		return null;
	}
	
	private void sendMessage(int what, int arg1){
		sendMessage(what, arg1, null);
	}
	
	private void sendMessage(int what, Object obj){
		sendMessage(what, 0, obj);
	}
	
	private void sendMessage(int what, int arg1, Object obj){
		if (mHandler != null){
			Message msg = new Message();
			msg.what = what;
			msg.arg1 = arg1;
			msg.obj = obj;
			mHandler.sendMessage(msg);
		}
	}
	
	private void refreshFavList() {
		mFavorList.clear();
		for (Program program : mTVList) {
			if (program.isFavor()) {
				mFavorList.add(program);
			}
		}
		for (Program program : mRadioList) {
			if (program.isFavor()) {
				mFavorList.add(program);
			}
		}
	}
	
//	public ArrayList<Program> getAllList(){
//		return (ArrayList<Program>) mAllList.clone();
//	}
//	
//	public ArrayList<Program> getTVList(){
//		return (ArrayList<Program>) mTVList.clone();
//	}
//
//	public ArrayList<Program> getRadioList(){
//		return (ArrayList<Program>) mRadioList.clone();
//	}
//	
//	public ArrayList<Program> getFavorList(){
//		return (ArrayList<Program>) mFavorList.clone();
//	}


	/** 获取当前播放的节目的当天详细EPG **/
	public ArrayList<String> getEPGList() {
		return (ArrayList<String>) mEPGList.clone();
	}
	
	public boolean isRun(){
		return mIsRun;
	}
	
	public int getCurrentPlay(){
		return mCurrentPlayID;
	}
	
	public ShortEPG getShortEPG(){
		return mShortEPG;
	}
	public HashMap<String, ArrayList<Program>> getProgramData(){
		HashMap<String, ArrayList<Program>> data = new HashMap<String, ArrayList<Program>>();
		data.put(Constant.PROGRAM_TV_KEY, mTVList);
		data.put(Constant.PROGRAM_RADIO_KEY, mRadioList);
		data.put(Constant.PROGRAM_FAVORATE_KEY, mFavorList);
		data.put(Constant.PROGRAM_ALL, mAllList);
		return data;
	}
}
