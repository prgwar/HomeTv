package org.videolan.libvlc;

import sdmc.com.tvlive.jni.UDPModule.UDPCallback;
import sdmc.com.tvlive.jni.UDPModule.UDPEvent;
import hometv.remote.socketUtil.Constant;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UDPCallbackImp extends UDPCallback {
	private Handler localHandler=null;
	private final int UDPMSG=888;
	public UDPCallbackImp(Handler uiHandler	) {
		this.localHandler=uiHandler;
	}
	@Override
	public int onEvent(UDPEvent event) {
		Log.e("info","event.eEvent=  "+event.eEvent);
		Log.e("info","event.eMessage=  "+event.eMessage);
		Message udpMsg=Message.obtain(localHandler);
		udpMsg.what=UDPMSG;
		Bundle bd=new Bundle();
		bd.putInt(Constant.UDPCALLBACK_EVENT_TYPE, event.eEvent);
		bd.putString(Constant.UDPCALLBACK_EVENT_MESSAGE, event.eMessage);
		udpMsg.setData(bd);
		localHandler.sendMessage(udpMsg);
		return 0;
	}

}
