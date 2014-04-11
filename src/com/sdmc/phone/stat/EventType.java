package com.sdmc.phone.stat;

//命令类型
public class EventType {
	public static final byte EVENT_KEYDOWN = 10;
	public static final byte EVENT_MOUSE_MOVE = 20;
	public static final byte EVENT_MOUSE_LEFT = 21;
	public static final byte EVENT_MOUSE_RIGHT = 22;
	public static final byte EVENT_MOUSE_TOUCH = 23;
	public static final byte EVENT_KEYBOARD = 30;
	public static final byte EVENT_GRAVITY = 40;
	public static final byte EVENT_GRAVITY_TOUCH = 41;
	public static final byte EVENT_PROGRAM_PLAY = 50;
	public static final byte EVENT_GET_MORE_EPG = 52;
	public static final byte EVENT_PROGRAM_DELETE = 53;
	public static final byte EVENT_PROGRAM_FAV = 54;
	public static final byte EVENT_PROGRAM_LOCK = 55;
	public static final byte EVENT_PROGRAM_SKIP = 56;
	public static final byte EVENT_PROGRAM_RENAME = 57;
	public static final byte EVENT_PROGRAM_EDIT_PASS = 58;
	
	public static final byte EVENT_STOP_PLAY = 60;
	public static final byte EVENT_GET_AUDIO_TRACK = 61;
	public static final byte EVENT_SET_AUDIO_TRACK = 62;
	public static final byte EVENT_GET_EPGTIME = 63;
	public static final byte EVENT_GET_URL = 64;
	
	public static final byte EVENT_END = 0;
	public static final byte EVENT_HAND = 1;
	public static final byte EVENT_SCAN = 2;
	public static final byte EVENT_PASS = 3;
	public static final byte EVENT_PASS_VERSION = 4;
	public static final byte EVENT_PASS_WRONG = 5;
	public static final byte EVENT_PHONE_GET_PROGRAMLIST = 5;
	public static final byte EVENT_PROGRAM_START = 6;
	public static final byte EVENT_PROGRAM_END = 7;
	public static final byte EVENT_CURRENT_PLAY = 8;
	public static final byte EVENT_CURRENT_EPG = 9; //有改动
	public static final byte EVENT_BINDDTV_SUCCESS = 10;
	public static final byte EVENT_BINDDTV_FALSE = 11;
	public static final byte EVENT_MORE_EPG_START = 12;
	public static final byte EVENT_MORE_EPG_NULL = 13;
	public static final byte EVENT_EDIT_SUCCESS = 14;
	public static final byte EVENT_EDIT_FALSE = 15;
	public static final byte EVENT_EDIT_PASS_SUCCESS = 16;
	public static final byte EVENT_EDIT_PASS_FALSE = 17;
	public static final byte EVENT_VERSION_MISMATCH = 18;
	
}
