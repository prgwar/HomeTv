package com.sdmc.dtv.programsend;

public class Program{

	private int mId;
	private String mName;
	private int mType;
	private boolean mIsScrambler;
	private boolean mIsFavor;
	private boolean mIsLock;
	private boolean mIsSkip;
	
	public Program(int id, String name, int type, 
			boolean isScrambler, boolean isFavor, boolean isLock, boolean isSkip){
		mId = id;
		mName = name;
		mType = type;
		mIsScrambler = isScrambler;
		mIsFavor = isFavor;
		mIsLock = isLock;
		mIsSkip = isSkip;
	}
	
	public Program(ProgramToPhone.Program progam){
		mId = progam.getId();
		mName = progam.getName();
		mType = progam.getType();
		mIsScrambler = progam.getIsScrambler();
		mIsFavor = progam.getIsFavor();
		mIsLock = progam.getIsLock();
	}
	
	public Program(int type){
		mId = 1;
		mName = "0";
		mType = type;
		mIsScrambler = false;
		mIsFavor = false;
		mIsLock = false;
		mIsSkip = false;
	}
	
	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}
	/** 1表示推送；0表示否 **/
	public int getType() {
		return mType;
	}

	public void setType(int type) {
		this.mType = type;
	}

	public boolean isScrambler() {
		return mIsScrambler;
	}

	public void setIsScrambler(boolean isScrambler) {
		this.mIsScrambler = isScrambler;
	}

	public boolean isFavor() {
		return mIsFavor;
	}

	public void setIsFavor(boolean isFavor) {
		this.mIsFavor = isFavor;
	}

	public boolean isLock() {
		return mIsLock;
	}

	public void setIsLock(boolean isLock) {
		this.mIsLock = isLock;
	}
	
	public boolean isSkip() {
		return mIsSkip;
	}

	public void setIsSkip(boolean isSkip) {
		this.mIsSkip = isSkip;
	}
	
}
