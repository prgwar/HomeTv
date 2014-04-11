package com.sdmc.phone.util;

import hometv.remote.bean.UdpScanCompleteListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sdmc.phone.stat.EventType;
import com.sdmc.phone.stat.ScanData;

import android.util.Log;

public class ScanLocalServerByUDP {
	private static final String TAG = "ScanLocalServerByUDP";
	private  int LOCAL_PORT = 8888;
	private final String BROADCAST_ADD = "255.255.255.255";
	private DatagramSocket datagramSocket = null;
	InetAddress targetAdd = null;
	private byte [] scanCmdByte=new byte[6];
	private ExecutorService mExecutorService;
	private static ScanLocalServerByUDP scanLocalServerByUDP = new ScanLocalServerByUDP();

	private ScanLocalServerByUDP() {
		  scanCmdByte[0] = EventType.EVENT_SCAN;
		  scanCmdByte[1] = 4; 
		  BytesMaker.int2bytes(VersionUtil.getVersionCode(), scanCmdByte, 2);
		  mDeviceList = new ArrayList<ScanData.ScanInfo>();
		try {
			targetAdd = InetAddress.getByName(BROADCAST_ADD);
		}   catch (UnknownHostException e) {
			Log.e(TAG, "The  UDP broadCast Host address is error! ");
			targetAdd = null;
		}
	}

	public static ScanLocalServerByUDP getUDPScanner() {
		if (scanLocalServerByUDP == null) {
			scanLocalServerByUDP = new ScanLocalServerByUDP();
		}
		return scanLocalServerByUDP;
	}

	private long curSendTime = 0;
	
	private UDPReceiver receiverOjb = new UDPReceiver();
	
	private boolean isScanning = false;

	private class UDPReceiver implements Runnable {
		long limitReceiveTime = 5 * 1000; // 10秒钟之内接收服务端的返回信息
		byte[] buffers = new byte[252];
		DatagramPacket receivePacket = new DatagramPacket(buffers, 252);

		@Override
		public void run() {
			isScanning = true;
			try {
				datagramSocket = new DatagramSocket(LOCAL_PORT);
				datagramSocket.setSoTimeout(10*1000);
				int serverPort = PreferencesVisiter.getVisiter().readPort();
				DatagramPacket datagramPacket = new DatagramPacket(
						scanCmdByte, 
						scanCmdByte.length,						
						targetAdd,
						serverPort);
				datagramSocket.send(datagramPacket);
				curSendTime = System.currentTimeMillis();
				while ((System.currentTimeMillis() - curSendTime) <= limitReceiveTime) {
					try {
						datagramSocket.receive(receivePacket);
						byte[] realRecData = receivePacket.getData();
						if(realRecData != null){
							byte eventType=realRecData[0];
							if(eventType==2){//扫描消息类型
								int dataLen=realRecData[1];
								byte [] dataBytes = new byte[dataLen]; //这里面包含服务器名称及版本号字节(最后4个字节)
								System.arraycopy(realRecData, 2, dataBytes, 0, dataLen); //复制数据字节
								
								//获取远程服务设备的名字
								byte [] serverNameByte=new byte[dataLen-4];
								System.arraycopy(dataBytes, 0, serverNameByte, 0, dataLen-4);
								
								int serverVersion = BytesMaker.bytes2int(dataBytes, serverNameByte.length);
								String serverName=new String(serverNameByte);
								String serverIP = receivePacket.getAddress().getHostAddress();
								Log.e("info", "serverIP= " + serverIP + " serverName= "
										+ serverName +" serverVersion= "+serverVersion);
								addDevice(new ScanData.ScanInfo(serverName, serverIP, serverVersion));
							}
						}
					} catch (IOException e) {
						Log.e(TAG, "receive nothing  because Occur IOExeption ...");
						break;
					}
				}// end while
				
			} catch (SocketException e) {
				Log.e(TAG, "Create UDP Socket failed! ");
				LOCAL_PORT++;
				datagramSocket = null;
			} catch (IOException e) {
				Log.e(TAG, "The UDP send occur IOExceptinon! ");
				curSendTime = 0;
			}  
			if(datagramSocket != null)
				datagramSocket.close();
			isScanning = false;
			if(scanCompleteListener !=null)
				scanCompleteListener.onScanComplete();
			Log.e("info", "********* udp scan the servers over! *********Thread id= "+Thread.currentThread().getId());
		}
	}
	/**
	 * 开始进行UDP扫描
	 */
	public void startToScanServer(){
		mDeviceList.clear();
		if(!isScanning){
			new Thread(receiverOjb).start();
		}
//		if(mExecutorService==null){
//			mExecutorService = Executors.newCachedThreadPool();
//		}
		if(isScanning){
//			stopScan();
		}
//		mExecutorService.execute(receiverOjb);
	}
	/**
	 * 停止扫描
	 */
	public void stopScan(){
		mExecutorService.shutdownNow();
		isScanning=false;
	}
	private UdpScanCompleteListener scanCompleteListener;
	public void setOnScanCompleteListener(UdpScanCompleteListener completeListener){
		this.scanCompleteListener=completeListener;
	}
	private ArrayList<ScanData.ScanInfo> mDeviceList;
	private synchronized void addDevice(ScanData.ScanInfo info) {
		String hasConnectIp = NetConnect.getServerIP();
		if(NetConnect.isConnected() && null != hasConnectIp){
			if(hasConnectIp.equals(info.getIp())){
				// 不添加，已经连接了的设备到集合中
				NetConnect.setCurServerName(info.getName());
				return;
			}
		}
		mDeviceList.add(info);
	}
	public ArrayList<ScanData.ScanInfo> getScannedResult(){
			return mDeviceList;
	}
}
