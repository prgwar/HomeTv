package com.sdmc.dlna.service;

public class PositionInfo {
	public int track;
	public String trackDuration;
	public String trackMetaData;
	public String trackURI;
	public String relTime;
	public String absTime;
	public int relCount;
	public int absCount;

	public PositionInfo() {
		this.track = 1;
		this.trackDuration = "00:00:00";
		this.trackMetaData = "";
		this.trackURI = "http://";
		this.relTime = "00:00:00";
		this.absTime = "NOT_IMPLEMENTED";
		this.relCount = -1;
		this.absCount = -1;
	}

	public PositionInfo(int track, String trackDuration, String trackMetaData,
			String trackURI, String relTime, String absTime, int relCount,
			int absCount) {
		this.track = track;
		this.trackDuration = trackDuration;
		this.trackMetaData = trackMetaData;
		this.trackURI = trackURI;
		this.relTime = relTime;
		this.absTime = absTime;
		this.relCount = relCount;
		this.absCount = absCount;
	}

	public PositionInfo(String trackDuration, String trackURI, String relTime) {
		this.track = 1;
		this.trackDuration = trackDuration;
		this.trackMetaData = "";
		this.trackURI = trackURI;
		this.relTime = relTime;
		this.absTime = "NOT_IMPLEMENTED";
		this.relCount = -1;
		this.absCount = -1;
	}
}
