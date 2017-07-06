package com.smartmux.videodownloader.modelclass;

public class VisitedSiteListModelClass {

	Integer mVsiteId = 0;
	String mVsiteTitle = null;
	String mVsiteUrl = null;
	String mBookmark = null;
	
	/* if mBookmark= "1" add in bookmark list*/

	public VisitedSiteListModelClass() {
	}

	public VisitedSiteListModelClass(String eTitle, String eVurl,
			String eBookmark) {

		this.mVsiteTitle = eTitle;
		this.mVsiteUrl = eVurl;
		this.mBookmark = eBookmark;
	}

	public VisitedSiteListModelClass(int eId, String eTitle, String eVurl,
			String eBookmark) {
		this.mVsiteId = eId;
		this.mVsiteTitle = eTitle;
		this.mVsiteUrl = eVurl;
		this.mBookmark = eBookmark;
	}

	public Integer getmVsiteId() {
		return mVsiteId;
	}

	public void setmVsiteId(Integer mVsiteId) {
		this.mVsiteId = mVsiteId;
	}

	public String getmVsiteTitle() {
		return mVsiteTitle;
	}

	public void setmVsiteTitle(String mVsiteTitle) {
		this.mVsiteTitle = mVsiteTitle;
	}

	public String getmVsiteUrl() {
		return mVsiteUrl;
	}

	public void setmVsiteUrl(String mVsiteUrl) {
		this.mVsiteUrl = mVsiteUrl;
	}

	public String getmBookmark() {
		return mBookmark;
	}

	public void setmBookmark(String mBookmark) {
		this.mBookmark = mBookmark;
	}

}
