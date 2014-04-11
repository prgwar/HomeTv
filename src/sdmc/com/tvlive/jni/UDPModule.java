package sdmc.com.tvlive.jni;

public class UDPModule{
	/**
	 * ½øÐÐUDP²¥·Å
	 * @param serviceIp
	 * @param port
	 * @param userName
	 * @param passWord
	 * @return
	 */
	public static native int init(String serviceIp, int port, String userName,
			String passWord);
			
	
	public static native int deInit();
	
	
	public static class UDPEvent{
		public  int eEvent;
		public String eMessage;
	}
	
	public static abstract class UDPCallback {
		public abstract int onEvent(UDPEvent event);
	}
	
	public static native int callBackInit();
	
	public static native int registerCallBack(UDPCallback callback);
	
}