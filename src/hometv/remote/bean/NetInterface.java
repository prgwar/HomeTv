package hometv.remote.bean;

import android.os.Message;

/**
 * ����ӿڣ�������������״̬
 * @author fee
 *
 */
public interface NetInterface {
	public void handleMsg(Message msg);
	public void handleLocalMsg(Message msg);
}
