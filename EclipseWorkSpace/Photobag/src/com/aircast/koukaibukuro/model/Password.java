package com.aircast.koukaibukuro.model;

public class Password {
	
	String nickName;
	String password;
	String createdDate;
	int photoCount;
	int numberOfDownload;
	int favorite;
	int honey;
	String thumbURL;
	String expiresAT;
	String expiredTime;
	String chargesTime;;
	int newItem;
	boolean isDownload;
	int recommend;
	
			
	public Password() {
		super();
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public int getPhotoCount() {
		return photoCount;
	}
	public void setPhotoCount(int photoCount) {
		this.photoCount = photoCount;
	}
	public int getNumberOfDownload() {
		return numberOfDownload;
	}
	public void setNumberOfDownload(int numberOfDownload) {
		this.numberOfDownload = numberOfDownload;
	}
	public int getFavorite() {
		return favorite;
	}
	public void setFavorite(int favorite) {
		this.favorite = favorite;
	}
	public int getHoney() {
		return honey;
	}
	public void setHoney(int honey) {
		this.honey = honey;
	}
	public String getThumbURL() {
		return thumbURL;
	}
	public void setThumbURL(String thumbURL) {
		this.thumbURL = thumbURL;
	}
	public String getExpiresAT() {
		return expiresAT;
	}
	public void setExpiresAT(String expiresAT) {
		this.expiresAT = expiresAT;
	}
	public String getExpiredTime() {
		return expiredTime;
	}
	public void setExpiredTime(String expiredTime) {
		this.expiredTime = expiredTime;
	}
	public int getNewItem() {
		return newItem;
	}
	public void setNewItem(int newItem) {
		this.newItem = newItem;
	}
	public boolean isDownload() {
		return isDownload;
	}
	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}
	public int getRecommend() {
		return recommend;
	}
	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}
	
	public String getChargesTime() {
		return chargesTime;
	}
	public void setChargesTime(String chargesTime) {
		this.chargesTime = chargesTime;
	}
	


}
