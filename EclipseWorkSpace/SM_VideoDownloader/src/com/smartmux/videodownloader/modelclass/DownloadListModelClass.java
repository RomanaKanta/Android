package com.smartmux.videodownloader.modelclass;

public class DownloadListModelClass {

	Integer mRowID = 0;
	String mRowTitle = null;
	String mRowSize = null;
	String mRemainingTime = null;
	String mProgress = null;
	Integer mSerialID = 0;
	
	public DownloadListModelClass() {
	}

	public DownloadListModelClass(int eSerial, String eName,String eSize, String ePB) {
		this.mSerialID = eSerial;
		this.mRowTitle = eName;
		this.mRowSize = eSize;
		this.mProgress = ePB;
	}
	public DownloadListModelClass(int eId, int eSerial, String eName,String eSize, String ePB) {
		this.mRowID = eId;
		this.mSerialID  = eSerial;
		this.mRowTitle = eName;
		this.mRowSize = eSize;
		this.mProgress = ePB;
	}
	public DownloadListModelClass(String eName,String eSize, String ePB) {
		
		this.mRowTitle = eName;
		this.mRowSize = eSize;
		this.mProgress = ePB;
	}

	public Integer getmRowID() {
		return mRowID;
	}

	public void setmRowID(Integer mRowID) {
		this.mRowID = mRowID;
	}

	public String getmRowTitle() {
		return mRowTitle;
	}

	public void setmRowTitle(String mRowTitle) {
		this.mRowTitle = mRowTitle;
	}

	public String getmRowSize() {
		return mRowSize;
	}

	public void setmRowSize(String mRowSize) {
		this.mRowSize = mRowSize;
	}

	public String getmProgress() {
		return mProgress;
	}

	public void setmProgress(String mProgress) {
		this.mProgress = mProgress;
	}

	public Integer getmSerialID() {
		return mSerialID;
	}

	public void setmSerialID(Integer mSerialID) {
		this.mSerialID = mSerialID;
	}

	
}
