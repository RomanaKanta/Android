package com.smartmux.textmemo.modelclass;

public class TextSizeModel {
	
	private String mTitle;
	private boolean mValue;
	Integer mID;
	
	public TextSizeModel( int id, String aTitle,boolean aValue) {
		mID = id;
		mTitle = aTitle;
		mValue=aValue;
	}
	
	public Integer getmID() {
		return mID;
	}

	public void setmID(Integer mID) {
		this.mID = mID;
	}

	public boolean ismValue() {
		return mValue;
	}
	public void setmValue(boolean mValue) {
		this.mValue = mValue;
	}
	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}
	
	public String getmTitle() {
		return mTitle;
	}

}
