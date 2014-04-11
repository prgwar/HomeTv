package com.sdmc.dtv.programsend;

import java.io.IOException;

import android.util.Log;

import com.sdmc.phone.stat.EventType;
import com.sdmc.phone.util.BytesMaker;
import com.sdmc.phone.util.NetConnect;
import com.sdmc.phone.util.WriteUitl;

public class DTVMessageSender {
	
//	private WriteUitl mWriteUitl;
//	
//	public DTVMessageSender(WriteUitl writeUitl) {
//		mWriteUitl = writeUitl;
//	}
	
	public void getShortEPG() {
		sendSimpleMessage(EventType.EVENT_CURRENT_EPG, 0);
	}
	
	public void programPlay(int id, int type, String password){
		byte[] passBytes = password.getBytes();
		byte[] bytes = new byte[7 + passBytes.length];
		bytes[0] = EventType.EVENT_PROGRAM_PLAY;
		bytes[1] = (byte) (5 + passBytes.length);
		BytesMaker.int2bytes(id, bytes, 2);
		bytes[6] = (byte) type;
		BytesMaker.copyBytes(passBytes, bytes, 7);
		sendToServer(bytes);
	}
	
	public void getAudioTrack(){
		sendSimpleMessage(EventType.EVENT_GET_AUDIO_TRACK, 0);
	}
	
	public void setAudioTrack(int select){
		sendSimpleMessage(EventType.EVENT_SET_AUDIO_TRACK, select);
	}
	/**  **/
	public void getEPGTime(){
		sendSimpleMessage(EventType.EVENT_GET_EPGTIME, 0);
	}
	
	public void getMoreEPG(int day) {
		byte[] bytes = new byte[]{EventType.EVENT_GET_MORE_EPG, 1, (byte) day};
		sendToServer(bytes);
	}
	
	public void stopPlay() {
		sendSimpleMessage(EventType.EVENT_STOP_PLAY, 0);
	}
	
	public void programRename(int id, String name) {
		byte[] strBytes = name.getBytes();
		byte[] bytes = new byte[6 + strBytes.length];
		bytes[0] = EventType.EVENT_PROGRAM_RENAME;
		bytes[1] = (byte) (strBytes.length + 4);
		BytesMaker.int2bytes(id, bytes, 2);
		BytesMaker.copyBytes(strBytes, bytes, 6);
		sendToServer(bytes);
	}
	
	public void editProgram(int flag, int id) {
		byte[] bytes = new byte[6];
		bytes[0] = (byte) flag;
		bytes[1] = 4;
		BytesMaker.int2bytes(id, bytes, 2);
		sendToServer(bytes);
	}
	
	public void getURL() {
		sendSimpleMessage(EventType.EVENT_GET_URL, 0);
	}
	
	private void sendSimpleMessage(int type, int msg) {
		byte[] bytes = new byte[3];
		bytes[0] = (byte) type;
		bytes[1] = 1;
		bytes[2] = (byte) msg;
		Log.i("DTVMessageSender", "sendSimpleMessage msg: " + msg);
		sendToServer(bytes);
	}
	private void sendToServer(byte[] cmds){
		if(NetConnect.mOut !=null){
			try {
				NetConnect.mOut.write(cmds);
			} catch (IOException e) {
				Log.e("info"," local tcp write occur IOException...");
			}
		}
	}
}
