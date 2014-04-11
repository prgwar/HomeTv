package com.sdmc.phone.util;

import hometv.remote.socketUtil.Constant;

import com.sdmc.phone.stat.DefaultData;

import android.content.Context;
import android.content.SharedPreferences;
/**
 * 喜好访问对象
 * @author fee
 */
public class PreferencesVisiter {
	
	public static final int PRC_STYLE_HLS_SEGMENTER = 1;
	public static final int PRC_STYLE_UDP_SEND = 2;
	
	private final static String NAME = "hometv_setting";
	private final static String SUCCESS_IP = "last_ip";
	private final static String ACCELEROMETER = "accelerometer";
	private final static String ORIENTATION = "orientaion";
	private final static String TVSHOW = "tv_show";
	private final static String ENABLE_IOMX = "enable_iomx";
	private final static String CHROMA_FORMAT = "chroma_format";
	private static final String TV_CAST_PROTOCOL = "tv_cast_protocol";
	
	private static SharedPreferences mPreferences;
	private static SharedPreferences.Editor mEditor;
	private static PreferencesVisiter mPrefeVisiter=new PreferencesVisiter();
	 
	public static void initVisiter(Context mContext){
		mPreferences = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		mEditor = mPreferences.edit();
	}
	private PreferencesVisiter(){		
	}
	public static PreferencesVisiter getVisiter(){
		return mPrefeVisiter;
	}
	//记录输入的ip地址
	public void writeInputIp(String ip01,String ip02,String ip03,String ip04){
		
		mEditor.putString("ip01", ip01);
		mEditor.putString("ip02", ip02);
		mEditor.putString("ip03", ip03);
		mEditor.putString("ip04", ip04);
		mEditor.commit();
	}
	
	//读取ip记录
	public String[] readInputedIp(){
		String [] result = {
		  mPreferences.getString("ip01", null),
		  mPreferences.getString("ip02", null),
		  mPreferences.getString("ip03", null),
		  mPreferences.getString("ip04", null)
		};
		return  result;
	}
	//写入端口信息
	public void writePort(int port){
		mEditor.putInt("port", port);
	}
	//读取端口信息
	public int readPort(){
		return mPreferences.getInt("port", DefaultData.port);
	}
	//记录ip地址中的各个数值
	public void writeIpOfInt(int ip ,String name){
		mEditor.putInt(name, ip);
	}
	//读取ip地址中的各个数值
	public int readIpOfInt(String name){
		return mPreferences.getInt(name , 0);
	}
	//记录是否记录ip
	public void writeRecord(boolean bool){
		mEditor.putBoolean("isRecord", bool);
		mEditor.commit();
	}
	//读取设置记录
	public boolean readRecord(){
		return mPreferences.getBoolean("isRecord", false);
	}
	//记录是否自动连接
	public void writeAuto(boolean bool){
		mEditor.putBoolean("isAuto", bool);
	}
	//读取自动连接的设置记录
	public boolean readAuto(){
		return mPreferences.getBoolean("isAuto", false);
	}
	
	//记录最后登录成功的ip地址
	public void writeLastIp(String ip){
		mEditor.putString(SUCCESS_IP, ip);
	}
	//读取最后登录成功的ip地址
	public String readLastIp(){
		return mPreferences.getString(SUCCESS_IP, "");
	}
	
	public void writeAccelerometer(boolean accelerometer){
		mEditor.putBoolean(ACCELEROMETER, accelerometer);
	}
	
	public boolean readAccelerometer(){
		return mPreferences.getBoolean(ACCELEROMETER, false);
	}
	
	public void writeOrientaion(boolean orientaion){
		mEditor.putBoolean(ORIENTATION, orientaion);
	}
	/** return default value 'true' **/
	public boolean readOrientaion(){
		return true;
	}
	
	public void setShowTV(boolean isShowTV) {
		mEditor.putBoolean(TVSHOW, isShowTV).commit();
	}
	
	public boolean isShowTV() {
		return mPreferences.getBoolean(TVSHOW, true);
	}
	
	public void setEnableIomx(boolean enableIomx) {
		mEditor.putBoolean(ENABLE_IOMX, enableIomx).commit();
	}
	
	public boolean isEnableIomx() {
		return mPreferences.getBoolean(ENABLE_IOMX, false);
	}
	
	public void setChromaFormat(int format) {
		mEditor.putInt(CHROMA_FORMAT, format).commit();
	}
	
	public int getChromaFormat() {
		return mPreferences.getInt(CHROMA_FORMAT, 0);
	}
	
	public void setTVCastProtocolType(int type) {
		mEditor.putInt(TV_CAST_PROTOCOL, type).commit();
	}
	
	public int getTVCastProtocolType() {
		return mPreferences.getInt(TV_CAST_PROTOCOL, PRC_STYLE_HLS_SEGMENTER);
	}
	//提交记录
	public void commit(){
		mEditor.commit();
	}
	
	//----------------
	public boolean savePrefer(String tagetKey,boolean values){
		mEditor.putBoolean(tagetKey, values);
		return mEditor.commit();
	}
	public boolean savePrefer(String tagetKey,String values){
		mEditor.putString(tagetKey, values);
		return mEditor.commit();
	}
	public String getPreferInfo(String tagetKey,String defaultValue){
		return	mPreferences.getString(tagetKey, defaultValue);
	}
	public boolean getEnableState(String targetKeys){
		return mPreferences.getBoolean(targetKeys, false);
	}
	/** 获取本地 设置的共享 媒体种类 **/
	public boolean [] getLocalShareMediaPrefers(){
		boolean [] states = {
			mPreferences.getBoolean(Constant.DLNA_SHARE_VIDEO, true),	
			mPreferences.getBoolean(Constant.DLNA_SHARE_MUSIC, true),	
			mPreferences.getBoolean(Constant.DLNA_SHARE_IMAGE, true),	
		};
		return states;
	}
	public boolean saveIntPrefer(String key,int value){
		mEditor.putInt(key, value);
		return mEditor.commit();
	}
	public SharedPreferences getSharedPreferences(){
		return mPreferences;
	}
}
