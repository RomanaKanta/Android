package com.smartmux.trackfriends.modelclass;

public class ProfileModelClass {

	Integer mUserId = 0;
	String mUserName = "";
	String mAge = "";
	String mGender = "";
	String mPhone = "";
	String mEmail = "";
	String mImagePath = "";
	
	ProfileModelClass(){
		
	}
	public ProfileModelClass(String eUserName ,String eAge ,String eGender ,String ePhone ,String eEmail ){
		this.mUserName = eUserName;
		this.mAge = eAge;
		this.mGender = eGender;
		this.mPhone = ePhone;
		this.mEmail = eEmail;
	}	
	public ProfileModelClass(String eUserName ,String eAge ,String eGender ,String ePhone ,String eEmail ,String eImagePath ){
		this.mUserName = eUserName;
		this.mAge = eAge;
		this.mGender = eGender;
		this.mPhone = ePhone;
		this.mEmail = eEmail;
		this.mImagePath = eImagePath;
	}
	
	public ProfileModelClass(Integer eUserId,String eUserName ,String eAge ,String eGender ,String ePhone ,String eEmail ,String eImagePath ){
		this.mUserId = eUserId;
		this.mUserName = eUserName;
		this.mAge = eAge;
		this.mGender = eGender;
		this.mPhone = ePhone;
		this.mEmail = eEmail;
		this.mImagePath = eImagePath;
	}

	public Integer getmUserId() {
		return mUserId;
	}

	public void setmUserId(Integer mUserId) {
		this.mUserId = mUserId;
	}

	public String getmUserName() {
		return mUserName;
	}

	public void setmUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public String getmAge() {
		return mAge;
	}

	public void setmAge(String mAge) {
		this.mAge = mAge;
	}

	public String getmGender() {
		return mGender;
	}

	public void setmGender(String mGender) {
		this.mGender = mGender;
	}

	public String getmPhone() {
		return mPhone;
	}

	public void setmPhone(String mPhone) {
		this.mPhone = mPhone;
	}

	public String getmEmail() {
		return mEmail;
	}

	public void setmEmail(String mEmail) {
		this.mEmail = mEmail;
	}

	public String getmImagePath() {
		return mImagePath;
	}

	public void setmImagePath(String mImagePath) {
		this.mImagePath = mImagePath;
	}
}
