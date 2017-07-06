package com.smartmux.trackfriends.modelclass;

public class LocationUpdateModelClass {

	String mLatitude = "";
	String mLongitude = "";
	
	LocationUpdateModelClass(){
		
	}
	
	public LocationUpdateModelClass(String eLatitude,String eLongitude){
		this.mLatitude = eLatitude;
		this.mLongitude = eLongitude;
	}

	public String getmLatitude() {
		return mLatitude;
	}

	public void setmLatitude(String mLatitude) {
		this.mLatitude = mLatitude;
	}

	public String getmLongitude() {
		return mLongitude;
	}

	public void setmLongitude(String mLongitude) {
		this.mLongitude = mLongitude;
	}
}
