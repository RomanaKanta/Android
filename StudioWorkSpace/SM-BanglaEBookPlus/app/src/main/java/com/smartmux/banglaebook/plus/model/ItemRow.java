package com.smartmux.banglaebook.plus.model;

import com.smartmux.banglaebook.plus.util.ProgressWheel;


public class ItemRow {

	public enum DownloadState {
		NOT_STARTED, QUEUED, DOWNLOADING, COMPLETE
	}

	private volatile DownloadState mDownloadState = DownloadState.NOT_STARTED;
	private volatile ProgressWheel mProgressBar;
	private volatile float mProgress;

	String aurthor;
	String image;
	String title;
	String bengaliTitle;
	String page;
	String size;
	String pdfUrl;
	boolean isDownload;

	public ItemRow(String bengaliTitle, String title, String page,
			String image, String aurthor, String size, String pdfUrl,
			boolean isDownload) {
		this.bengaliTitle = bengaliTitle;
		this.image = image;
		this.title = title;
		this.page = page;
		this.aurthor = aurthor;
		this.size = size;
		this.pdfUrl = pdfUrl;
		this.isDownload = isDownload;
		mProgress = 0;
		mProgressBar = null;

	}

	public ProgressWheel getProgressBar() {
		return mProgressBar;
	}

	public void setProgressBar(ProgressWheel progressBar) {
		mProgressBar = progressBar;
	}

	public void setDownloadState(DownloadState state) {
		mDownloadState = state;
	}

	public DownloadState getDownloadState() {
		return mDownloadState;
	}

	public float getProgress() {
		return mProgress;
	}

	public void setProgress(float progress) {
		this.mProgress = progress;
	}

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}

	public String getPdfUrl() {
		return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}

	public String getAurthor() {
		return aurthor;
	}

	public void setAurthor(String aurthor) {
		this.aurthor = aurthor;
	}

	public String getBengaliTitle() {
		return bengaliTitle;
	}

	public void setBengaliTitle(String bengaliTitle) {
		this.bengaliTitle = bengaliTitle;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getsize() {
		return size;
	}

	public void setsize(String size) {
		this.size = size;
	}
}
