package com.sdmc.dlna.service;

import com.sdmc.dlna.filebrowser.BaseItem;
import com.sdmc.dlna.filebrowser.Const;

import android.os.Bundle;

/**
 * DLNAÉè±¸Ïî
 * 
 * @author Ke Chow
 * @date 2012-08-24
 */
public class DeviceItem extends BaseItem {
	private int deviceType;
	private int deviceID;
	private String ipAddress;
	private String macAddress;
	private String mUdn;
	private Bundle mBundle;

	/*
	 * public DeviceItem(int type) throws Exception { this(type, "Anonymous"); }
	 */
	/*
	 * public DeviceItem(int type, String name) throws Exception { this(type,
	 * name, 0); }
	 */

	/*
	 * public DeviceItem(int type, String name, int witchIcon) throws Exception
	 * { this(type, -1, name, witchIcon, null); }
	 */

	public DeviceItem(int type, int deviceID, String name, int witchIcon,
			String udn) throws Exception {
		super(name, witchIcon);
		if (!checkValidityOfType(type)) {
			throw new Exception("Invalid Type");
		}
		this.deviceType = type;
		this.deviceID = deviceID;
		this.mUdn = udn;
		setBundle();
	}

	public void setType(int type) {
		if (checkValidityOfType(type)) {
			this.deviceType = type;
		}
	}

	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
	}

	public void setIPAddress(String ipAddress) {
		if (checkValidityOfIP(ipAddress)) {
			this.ipAddress = ipAddress;
		}
	}

	public void setMACAddress(String macAddress) {
		if (checkValidityOfMAC(macAddress)) {
			this.macAddress = macAddress;
		}
	}

	public int getType() {
		return this.deviceType;
	}

	public int getDeviceID() {
		return this.deviceID;
	}

	public String getIPAddress() {
		return this.ipAddress;
	}

	public String getMACAddress() {
		return this.macAddress;
	}

	private void setBundle() {
		mBundle = new Bundle();
		mBundle.putInt(Const.DEVID, deviceID);
		mBundle.putString(Const.DEVID_NAME, this.getName());
	}

	public Bundle getBundle() {
		return this.mBundle;
	}

	@Override
	public String toString() {
		return "[ type: " + deviceType + " ] [ name: " + getName()
				+ " ] [ icon: " + getIndexOfIcon() + " ] [ IP: " + ipAddress
				+ " ] [ MAC: " + macAddress + " ]";
	}
	/** dlna 's type valid in 0 -- 5 **/
	private boolean checkValidityOfType(int type) {
		return (type >= Const.TYPE_BASE && type < Const.TYPE_MAX);
	}

	private boolean checkValidityOfIP(String ipAddress) {
		return checkValidityOfString(ipAddress);
	}

	private boolean checkValidityOfMAC(String macAddress) {
		return checkValidityOfString(macAddress);
	}

	public String getUdn() {
		return mUdn;
	}
}
