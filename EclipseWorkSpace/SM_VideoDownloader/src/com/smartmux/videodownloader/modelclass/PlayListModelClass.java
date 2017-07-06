package com.smartmux.videodownloader.modelclass;

public class PlayListModelClass {

	Integer mPlayListId = 0;
	String mPlayListName = null;
	String mSong = null;
	boolean edit ;
	private boolean isChecked = false;
	
	public boolean isChecked() {
		return isChecked;
	}

	public void setIsChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
//	Integer mFolderId = 0;
//	String mVideoName = null;
//	String mVideoDuration = null;
//	String mVideoSize = null;
//	String mVideoDate = null;
//	String mVideoTime = null;
//	String mVideoUrl = null;
	
	
	public PlayListModelClass() {
	}

	public PlayListModelClass(int eId, String eName, String eSong) {
		this.mPlayListId = eId;
		this.mPlayListName = eName;
		this.mSong = eSong;
	}
	
	public PlayListModelClass(String eName, String eSong) {
		
		this.mPlayListName = eName;
		this.mSong = eSong;
	}
	
	public PlayListModelClass(int eId, String eName) {
		this.mPlayListId = eId;
		this.mPlayListName = eName;
	}
	
	
//	public PlayListModelClass(int eId,String eFolderName, String eVideoName, String eDurarion, String eSize, String eDate, String eTime, String eUrl) {
//		
//		this.mPlayListId = eId;
//		this.mPlayListName = eFolderName;
//		this.mVideoName = eVideoName;
//		this.mVideoDuration = eDurarion;
//		this.mVideoSize = eSize;
//		this.mVideoDate = eDate;
//		this.mVideoTime = eTime;
//		this.mVideoUrl = eUrl;
//		
//	}
//	
//	public PlayListModelClass(int eId, int eFolderid,String eFolderName, String eName, String eDuration, String eSize, String eDate, String eTime, String eUrl) {
//		this.mPlayListId = eId;
//		this.mFolderId = eFolderid;
//		this.mPlayListName = eFolderName;
//		this.mVideoName = eName;
//		this.mVideoDuration = eDuration;
//		this.mVideoSize = eSize;
//		this.mVideoDate = eDate;
//		this.mVideoTime = eTime;
//		this.mVideoUrl = eUrl;
//	}
	
	
	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public Integer getmPlayListId() {
		return mPlayListId;
	}

	public void setmPlayListId(Integer mPlayListId) {
		this.mPlayListId = mPlayListId;
	}

	public String getmPlayListName() {
		return mPlayListName;
	}

	public void setmPlayListName(String mPlayListName) {
		this.mPlayListName = mPlayListName;
	}

	public String getmSong() {
		return mSong;
	}

	public void setmSong(String mSong) {
		this.mSong = mSong;
	}

//	public String getmVideoName() {
//		return mVideoName;
//	}
//
//	public void setmVideoName(String mVideoName) {
//		this.mVideoName = mVideoName;
//	}
//
//	public String getmVideoDuration() {
//		return mVideoDuration;
//	}
//
//	public void setmVideoDuration(String mVideoDuration) {
//		this.mVideoDuration = mVideoDuration;
//	}
//
//	public String getmVideoSize() {
//		return mVideoSize;
//	}
//
//	public void setmVideoSize(String mVideoSize) {
//		this.mVideoSize = mVideoSize;
//	}
//
//	public String getmVideoDate() {
//		return mVideoDate;
//	}
//
//	public void setmVideoDate(String mVideoDate) {
//		this.mVideoDate = mVideoDate;
//	}
//
//	public String getmVideoTime() {
//		return mVideoTime;
//	}
//
//	public void setmVideoTime(String mVideoTime) {
//		this.mVideoTime = mVideoTime;
//	}
//
//	public String getmVideoUrl() {
//		return mVideoUrl;
//	}
//
//	public void setmVideoUrl(String mVideoUrl) {
//		this.mVideoUrl = mVideoUrl;
//	}
//
//	public Integer getmFolderId() {
//		return mFolderId;
//	}
//
//	public void setmFolderId(Integer mFolderId) {
//		this.mFolderId = mFolderId;
//	}


	
}

