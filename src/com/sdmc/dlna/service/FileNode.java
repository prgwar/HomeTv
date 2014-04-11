package com.sdmc.dlna.service;

import android.os.Parcel;
import android.os.Parcelable;

public class FileNode implements Parcelable {
	public String name;
	public int id;
	public int restricted;
	public int parentID;
	public int childCount;
	public int searchable;
	public int type;
	public String path;

	public FileNode() {
		this.name = null;
		this.id = -1;
		this.restricted = -1;
		this.parentID = -1;
		this.childCount = -1;
		this.searchable = -1;
		this.type = -1;
		this.path = null;
	}

	public FileNode(String name, int id, int restricted, int parentId,
			int childCount, int searchable, int type, String path) {
		this.name = name;
		this.id = id;
		this.restricted = restricted;
		this.parentID = parentId;
		this.childCount = childCount;
		this.searchable = searchable;
		this.type = type;
		this.path = path;
	}

	public FileNode(FileNode fileNode) {
		this();
		if (fileNode != null) {
			this.name = fileNode.name;
			this.id = fileNode.id;
			this.restricted = fileNode.restricted;
			this.parentID = fileNode.parentID;
			this.childCount = fileNode.childCount;
			this.searchable = fileNode.searchable;
			this.type = fileNode.type;
			this.path = fileNode.path;
		}
	}

	public FileNode(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeInt(this.id);
		dest.writeInt(this.restricted);
		dest.writeInt(this.parentID);
		dest.writeInt(this.childCount);
		dest.writeInt(this.searchable);
		dest.writeInt(this.type);
		dest.writeString(this.path);

	}

	public void readFromParcel(Parcel in) {
		this.name = in.readString();
		this.id = in.readInt();
		this.restricted = in.readInt();
		this.parentID = in.readInt();
		this.childCount = in.readInt();
		this.searchable = in.readInt();
		this.type = in.readInt();
		this.path = in.readString();
	}

	public static final Parcelable.Creator<FileNode> CREATOR = new Parcelable.Creator<FileNode>() {
		public FileNode createFromParcel(Parcel in) {
			return new FileNode(in);
		}

		public FileNode[] newArray(int size) {
			return new FileNode[size];
		}
	};
	public String toString() {
		return "name:"+name +" id: "+id +" type: "+type+" parentID: "+parentID+" path: "+path;
	};
}
