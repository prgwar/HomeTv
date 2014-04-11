package com.sdmc.dlna.filebrowser;

/**
 * »ù±¾Ïî
 * 
 * @author Ke Chow
 * @date 2012-08-24
 */
public class BaseItem implements Comparable<BaseItem> {
	private long id;
	private String name;
	private int indexOfIcon;
	private Object tag;

	public BaseItem() {

	}

	public BaseItem(String name, int witchIcon) throws Exception {
		if (!checkValidityOfString(name)) {
			throw new Exception("Invalid Name");
		}
		if (!checkValidityOfIconIndex(witchIcon)) {
			throw new Exception("Invalid Index of Icon");
		}
		this.name = name;
		this.indexOfIcon = witchIcon;
	}

	public void setID(long id) {
		this.id = id;
	}

	public void setName(String name) {
		if (checkValidityOfString(name)) {
			this.name = name;
		}
	}

	public void setIcon(int index) {
		if (checkValidityOfIconIndex(index)) {
			this.indexOfIcon = index;
		}
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public long getID() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getIndexOfIcon() {
		return this.indexOfIcon;
	}

	public Object getTag() {
		return this.tag;
	}

	@Override
	public String toString() {
		return "[ name: " + name + " ] [ icon: " + indexOfIcon + " ]";
	}
	/** the param "string"  can not be null and "" **/
	protected boolean checkValidityOfString(String string) {
		return (string != null && !string.trim().equals(""));
	}
	/** the device witchIcon is just in 0 --7  **/
	private boolean checkValidityOfIconIndex(int witchIcon) {
		return (witchIcon >= 0 && witchIcon < Const.DEFAULT_ICON_COUNT);
	}

	@Override
	public int compareTo(BaseItem another) {
		return this.getName().compareTo(another.getName());
	}

}
