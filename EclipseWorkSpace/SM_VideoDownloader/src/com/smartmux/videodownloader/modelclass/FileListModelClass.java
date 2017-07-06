package com.smartmux.videodownloader.modelclass;

public class FileListModelClass {

	

	Integer mId = 0;
	String mFileName = null;
	String mFileDuration = null;
	String mFileSize = null;
	String mFileDate = null;
	String mFileTime = null;
	String mFileUrl = null;
	Integer mPlayListId = 0;
	Integer mFileId = 0;
	String mPlayListName = null;
	
	public FileListModelClass() {
	}

	public FileListModelClass(String eName, String eDurarion, String eSize, String eDate, String eTime, String eUrl) {
	
		this.mFileName = eName;
		this.mFileDuration = eDurarion;
		this.mFileSize = eSize;
		this.mFileDate = eDate;
		this.mFileTime = eTime;
		this.mFileUrl = eUrl;
		
	}
	
	public FileListModelClass(int eId, String eName, String eDuration, String eSize, String eDate, String eTime, String eUrl) {
		this.mId = eId;
		this.mFileName = eName;
		this.mFileDuration = eDuration;
		this.mFileSize = eSize;
		this.mFileDate = eDate;
		this.mFileTime = eTime;
		this.mFileUrl = eUrl;
	}

	
	public FileListModelClass(int eId,int eFolderid,String eFolderName, int eVdoID){
		this.mId = eId;
		this.mPlayListId = eFolderid;
		this.mPlayListName = eFolderName;
		this.mFileId = eVdoID;
	}
	
	public FileListModelClass(int eFolderid,String eFolderName, int eVdoID){
		this.mPlayListId = eFolderid;
		this.mPlayListName = eFolderName;
		this.mFileId = eVdoID;
	}
	
//	public FileListModelClass(int eFolderid,String eFolderName, String eVideoName, String eDuration, String eSize, String eDate, String eTime, String eUrl) {
//		
//		this.mPlayListId = eFolderid;
//		this.mPlayListName = eFolderName;
//		
//		
//		this.mFileName = eVideoName;
//		this.mFileDuration = eDuration;
//		this.mFileSize = eSize;
//		this.mFileDate = eDate;
//		this.mFileTime = eTime;
//		this.mFileUrl = eUrl;
//	
//		
//	}
	
//	public FileListModelClass(int eId, int eFolderid,String eFolderName, String eName, String eDuration, String eSize, String eDate, String eTime, String eUrl) {
//		this.mFileId = eId;
//		this.mPlayListId = eFolderid;
//		this.mPlayListName = eFolderName;
//		this.mFileName = eName;
//		this.mFileDuration = eDuration;
//		this.mFileSize = eSize;
//		this.mFileDate = eDate;
//		this.mFileTime = eTime;
//		this.mFileUrl = eUrl;
//	}
//	

	public Integer getmFileId() {
		return mFileId;
	}

	public void setmFileId(Integer mFileId) {
		this.mFileId = mFileId;
	}
	
	public Integer getmId() {
		return mId;
	}


	public void setmId(Integer mId) {
		this.mId = mId;
	}
	
	public Integer getmPlayListId() {
		return mPlayListId;
	}

	public void setmPlayListId(Integer mPlayListId) {
		this.mPlayListId = mPlayListId;
	}

	public String getmFileName() {
		return mFileName;
	}

	public void setmFileName(String mFileName) {
		this.mFileName = mFileName;
	}

	public String getmFileDuration() {
		return mFileDuration;
	}

	public void setmFileDuration(String mFileDuration) {
		this.mFileDuration = mFileDuration;
	}

	public String getmFileSize() {
		return mFileSize;
	}

	public void setmFileSize(String mFileSize) {
		this.mFileSize = mFileSize;
	}

	public String getmFileDate() {
		return mFileDate;
	}

	public void setmFileDate(String mFileDate) {
		this.mFileDate = mFileDate;
	}

	public String getmFileTime() {
		return mFileTime;
	}

	public void setmFileTime(String mFileTime) {
		this.mFileTime = mFileTime;
	}

	public String getmFileUrl() {
		return mFileUrl;
	}

	public void setmFileUrl(String mFileUrl) {
		this.mFileUrl = mFileUrl;
	}

	public String getmPlayListName() {
		return mPlayListName;
	}

	public void setmPlayListName(String mPlayListName) {
		this.mPlayListName = mPlayListName;
	}
	
	
}
