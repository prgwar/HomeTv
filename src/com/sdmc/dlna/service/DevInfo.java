package com.sdmc.dlna.service;

import android.os.Parcel;
import android.os.Parcelable;

public class DevInfo implements Parcelable {

	public int devNum;
	public int tvType;
	public String userName;
	public String udn;

	public DevInfo() {
		devNum = -1;
		tvType = -1;
		userName = null;
		udn = null;
	}

	public DevInfo(DevInfo devInfo) {
		this();
		if (devInfo != null) {
			this.devNum = devInfo.devNum;
			this.tvType = devInfo.tvType;
			this.userName = devInfo.userName;
			this.udn = devInfo.udn;
		}
	}

	public DevInfo(int devNum, int tvType, String userName, String udn) {
		this.devNum = devNum;
		this.tvType = tvType;
		this.userName = userName;
		this.udn = udn;
	}

	public DevInfo(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(devNum);
		dest.writeInt(tvType);
		dest.writeString(userName);
		dest.writeString(udn);
	}

	public void readFromParcel(Parcel in) {
		this.devNum = in.readInt();
		this.tvType = in.readInt();
		this.userName = in.readString();
		this.udn = in.readString();
	}

	public static final Parcelable.Creator<DevInfo> CREATOR = new Parcelable.Creator<DevInfo>() {
		public DevInfo createFromParcel(Parcel in) {
			return new DevInfo(in);
		}

		public DevInfo[] newArray(int size) {
			return new DevInfo[size];
		}
	};

	@Override
	public boolean equals(Object o) {
		DevInfo devInfo = (DevInfo) o;
		if (this.devNum == devInfo.devNum && this.tvType == devInfo.tvType
				&& this.userName.equals(devInfo.userName)
				&& this.udn.equals(devInfo.udn)) {
			return true;
		} else {
			return false;
		}
	}

}
