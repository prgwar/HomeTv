package com.sdmc.phone.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * 获取MAC及Ip地址的工具类
 */
public class NetUtil {
	
	private WifiInfo mWifiInfo;
	public static String MAC = "";
	/**
	 * 记录本手机的IP地址
	 */
	public static String IP = "";
	private final static String DB_UDN_PREFIX = "uuid:upnp-sdmc-1200-";
	private final static String PLAYER_UDN_PREFIX = "uuid:upnp-sdmc-1201-";
	
	private ConnectivityManager connectivityManager=null;
	/**
	 * 构造方法中获取MAC及IP地址
	 * @param context
	 */
	public NetUtil(Context context){
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = wifi.getConnectionInfo();
		connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		IP = findIp();
		MAC = getLocalMacAddress();
		Log.i("info","  current phone 's ip = "+IP +" mac = "+MAC);
	}
	
	private String getLocalMacAddress(){
		if(mWifiInfo.getMacAddress() == null) return IP;
			
		StringBuffer buffer = new StringBuffer(mWifiInfo.getMacAddress());
		for(int i = 4; i >= 0; i --){
        	buffer.deleteCharAt(i * 3 + 2);
        }			
	    return buffer.toString();
    } 
	
	private String findIp(){
		String ip = new String();
		int wifiIp = mWifiInfo.getIpAddress();
		if(wifiIp == 0){
			ip = getLocalIpAddress();
		}
		else {
			ip = intToIp(wifiIp);
		}
        return ip;
    }
	
	//获取本地IP地址
	private String getLocalIpAddress() {   
        try {   
        	Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); 
        	if (en != null) {
        		while (en.hasMoreElements()) {
        			NetworkInterface intf = en.nextElement();   
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); 
                    		enumIpAddr.hasMoreElements();) {   
                        InetAddress inetAddress = enumIpAddr.nextElement();  
                        if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {   
                            return inetAddress.getHostAddress().toString();   
                        }
                    }   
        		}
        	}
        } catch (SocketException ex) {   
            Log.e("info", "NetUtil: can not get LocalIpAddress!");   
        }   
        return "";   
    }  
	
	private String intToIp(int ip){ 
        return (ip & 0xFF) + "." + ((ip >> 8 ) & 0xFF) + "." 
                + ((ip >> 16 ) & 0xFF) + "." + ((ip >> 24 ) & 0xFF ); 
    }
	/** uuid:upnp-sdmc-1200- +MAC **/
	public static String getSelfDBUdn(){
		return DB_UDN_PREFIX + MAC;
	}
	/** uuid:upnp-sdmc-1201- +MAC **/
	public static String getSelfPlayerUdn(){
		return PLAYER_UDN_PREFIX + MAC;
	}
	
	public boolean isNetworkUseable(){
		if(IP.equals("")){
			return false;
		} 
		return true;
	}
	//***********************add************************
	/**
	 * 判断是否有网络连接包括 wifi 、GPRS、Mobile
	 * @return
	 */
	public boolean isNetworkConnected(){
		NetworkInfo mNetworkInfo=connectivityManager.getActiveNetworkInfo();
		if(mNetworkInfo!=null){
			return mNetworkInfo.isAvailable();
		}
		return false;
	}
	/**
	 * 单独判断Wifi网络是否可用
	 * @return
	 */
	public boolean isWifiConnected(){
		NetworkInfo mWifiNetworkInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(mWifiNetworkInfo!=null){
			return mWifiNetworkInfo.isAvailable();
		}
		return false;
	}
	/**
	 * 单独判断MoBile网络是否可用
	 * @return
	 */
	public boolean isMobileConnected(){
		NetworkInfo mMobileNetworkInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if(mMobileNetworkInfo!=null){
			return mMobileNetworkInfo.isAvailable();
		}
		return false;
	}
	/**
	 * 获取当前网络连接类型
	 * @return  -1,无网络  0：Mobile; 1:Wifi;7：蓝牙；9:以太网；
	 */
	public  int getConnectedType(){
		NetworkInfo mNetworkInfo=connectivityManager.getActiveNetworkInfo();
		if(mNetworkInfo!=null && mNetworkInfo.isAvailable()){
			return mNetworkInfo.getType();
		}
		return -1;
	}
	
}
