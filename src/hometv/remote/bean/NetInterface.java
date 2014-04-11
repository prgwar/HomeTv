package hometv.remote.bean;

import android.os.Message;

/**
 * 网络接口，用来控制网络状态
 * @author fee
 *
 */
public interface NetInterface {
	public void handleMsg(Message msg);
	public void handleLocalMsg(Message msg);
}
