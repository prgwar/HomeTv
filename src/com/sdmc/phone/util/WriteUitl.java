package com.sdmc.phone.util;

import java.io.IOException;
import java.io.OutputStream;

import sdmc.com.hometv.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sdmc.phone.stat.EventType;

/**
 * 发送数据的工具类
 */

public class WriteUitl {
	
	private Activity mActivity = null;
	private AlertDialog mConnectloseDialog = null;
	private AlertDialog mReConnectDialog;
	private OutputStream mOut = null;
	private Handler mHandler;
	private boolean mIsConnectting = true; 
	private boolean mIsAcceptting = false;
	private boolean mIsThreadRun = true;

	private final int CONNECT_TIME = 400;
	private final int CONNECT_WAIT = 600;
	private final int CONNECT_COUNT = 10; //连接断开后自动重连的次数
	
	public WriteUitl(Activity activity){
		this.mActivity = activity;
		mOut = NetConnect.mOut;
		mHandler = new Handler();
	}
	
	public synchronized void write(byte[] bytes){
		if(mIsConnectting){
			try {
				if (mOut != null) {
					mOut.write(bytes);
					mOut.flush();
				}
			} catch (IOException e) {
				mIsConnectting = false;
				Log.e("info","WriteUitl  lost connectting");
				if (NetConnect.isConnected()) {
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
//							connectAgain();
						}
					});
				}
				 
			}
		}
	}
	
	/*public void flush(){
		if(mIsConnectting){
			try {
				if (mOut != null) {
					mOut.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/
	
//	private void connectAgain(){
//		Log.i("WriteUitl", "connectAgain");
////		showReconnectDialog();
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				while (mIsThreadRun) {
//					for(int i = 0; i < CONNECT_COUNT; i ++){
//						if(NetConnect.reConnet(CONNECT_TIME) == NetConnect.NO_EXCEPTION){
//							mOut = NetConnect.mOut;
//							mIsConnectting = true;
//							mHandler.removeCallbacks(mRunnable);
//							if (mReConnectDialog.isShowing()) {
//								mReConnectDialog.dismiss();
//							}
//							return;
//						}
//						try {
//							Thread.sleep(CONNECT_WAIT);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//		}).start();
//		mHandler.postDelayed(mRunnable, (CONNECT_COUNT + CONNECT_WAIT) * CONNECT_TIME);
//	}
	
/*	private void showReconnectDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		LayoutInflater inflater = mActivity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.connect_dialog, null);
		builder.setView(layout);
		TextView textView = (TextView) layout.findViewById(R.id.text_connect_notice);
		textView.setText(R.string.reconnect_notice);
		mReConnectDialog = builder.create();
		mReConnectDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				mIsThreadRun = false;
				mHandler.removeCallbacks(mRunnable);
				mIsConnectting = true;
				NetConnect.close();
				intentTo(ConnectActivity.class);
				mActivity.finish();
			}
		});
		mReConnectDialog.show();
	}  */                 
	
	private Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			if(!mIsConnectting){
				connetLost();
			}
		}
	};
	
	private void connetLost(){
		if (mReConnectDialog.isShowing()) {
			mReConnectDialog.dismiss();
		}
		if(mConnectloseDialog == null){
			mConnectloseDialog = connectLostDialogMake();
		}
		if(!mConnectloseDialog.isShowing()){
			mConnectloseDialog.show();
		}
	}
	//连接断开对话框
	private AlertDialog connectLostDialogMake(){
		Builder builder = new AlertDialog.Builder(mActivity);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.connect_lose);
		builder.setMessage(R.string.connect_again);
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				mIsConnectting = true;
//				intentTo(ConnectActivity.class);
				mActivity.finish();
			}
		});
		builder.setNegativeButton(R.string.close, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				mActivity.finish();
			}
		});
		return builder.create();
	}
	//跳转至其他操作界面
	public void intentTo(Class<?> target){
		if (mIsConnectting){
			Intent intent = new Intent(mActivity, target);
			mActivity.startActivity(intent);
			mActivity.finish();
		}
	}
	
	//接收并回应心跳包 
	public void startAccept(){
		if(!mIsAcceptting){
			Log.i("WriteUitl:", "StartAccept");
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					mIsAcceptting = true;
					try {
						while(mIsConnectting && NetConnect.mIn.read() != -1){
							write(new byte[]{EventType.EVENT_HAND});
							Log.i("WriteUitl:", "sendhand");
						}
						mIsAcceptting = false;
					} catch (IOException e) {
						mIsAcceptting = false;
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	/*public void acceptProgram(){
		ProgramRunnable receiveProgram = ProgramRunnable.instance();
		Log.i("WriteUitl", "mReceiveProgram.isRun()" + receiveProgram.isRun());
		if(!receiveProgram.isRun()){
			new Thread(receiveProgram).start();
		}
	}*/
}
