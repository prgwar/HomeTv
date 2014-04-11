package com.sdmc.phone.util;

public class BytesMaker {
	
	public static void int2bytes(int n, byte[] bytes, int start){
		for(int i = 0; i < 4; i ++){
			bytes[i + start] = (byte)(n >> (24 - i * 8)); 
		}
	}
	
	public static void int2bytes_sensor(int n, byte[] bytes, int end){
		for(int i = 0; i < 4; i ++){
			bytes[end - i] = (byte)(n >> (24 - i * 8)); 
		}
	}
	
	public static void float2bytes(float x, byte[] bytes, int start) {  
		int l = Float.floatToIntBits(x);  
		for (int i = 0; i < 4; i++) {  
			bytes[start + i] = Integer.valueOf(l).byteValue();
			l = l >> 8;  
		}  
	}  
	
	//byteתint
	public static int byte2int(byte b){
		return (int) (b & 0xff);
	}
	
	public static int bytes2int(byte[] b, int start){
		return (int)(((b[start] & 0xff) << 24) | ((b[start + 1] & 0xff) << 16)
				| ((b[start + 2] & 0xff) << 8) | ((b[start + 3] & 0xff) << 0));
	}
	
	public static void copyBytes(byte[] from, byte[] to, int start) {
		for (int i = 0; i < from.length; i ++) {
			to[start + i] = from[i];
		}
	}
}
