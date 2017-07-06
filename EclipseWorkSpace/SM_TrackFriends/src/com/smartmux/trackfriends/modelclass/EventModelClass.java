package com.smartmux.trackfriends.modelclass;

public class EventModelClass {

	Integer mEventId = 0;
	String mEventTitle = "";

	String mEventDescription = "";
	String mEventAddress = "";
	double mEventLatitude = 0;
	double mEventLongitude = 0;

	Integer mEventMemberNo = 0;

	Integer mEventYear = 0;
	Integer mEventDay = 0;
	Integer mEventMonth = 0;

	String mEventDate = "";

	Integer mEventStartHour = 0;
	Integer mEventStartMinute = 0;

	String mEventStartTime = "";

	Integer mEventEndHour = 0;
	Integer mEventEndMinute = 0;

	String mEventEndTime = "";

	EventModelClass() {

	}

	public EventModelClass( String eEventTitle,
			String eEventDescription, String eEventAddress,
			double eEventLatitude, double eEventLongitude,
			Integer eEventMemberNo, String eEventDate, String eEventStartTime,
			String eEventEndTime) {

		this.mEventTitle = eEventTitle;

		this.mEventDescription = eEventDescription;
		this.mEventAddress = eEventAddress;
		this.mEventLatitude = eEventLatitude;
		this.mEventLongitude = eEventLongitude;
		this.mEventMemberNo = eEventMemberNo;

		this.mEventDate = eEventDate;
		this.mEventStartTime = eEventStartTime;
		this.mEventEndTime = eEventEndTime;

	}

	public EventModelClass(Integer eEventId, String eEventTitle,
			String eEventDescription, String eEventAddress,
			double eEventLatitude, double eEventLongitude,
			Integer eEventMemberNo, String eEventDate, String eEventStartTime,
			String eEventEndTime) {

		this.mEventId = eEventId;
		this.mEventTitle = eEventTitle;

		this.mEventDescription = eEventDescription;
		this.mEventAddress = eEventAddress;
		this.mEventLatitude = eEventLatitude;
		this.mEventLongitude = eEventLongitude;
		this.mEventMemberNo = eEventMemberNo;

		this.mEventDate = eEventDate;
		this.mEventStartTime = eEventStartTime;
		this.mEventEndTime = eEventEndTime;

	}
	
	public EventModelClass(Integer eEventId, String eEventTitle,
			String eEventDescription, String eEventAddress,
			double eEventLatitude, double eEventLongitude,
			Integer eEventMemberNo, Integer eEventYear, Integer eEventDay,
			Integer eEventMonth, Integer eEventStartHour,
			Integer eEventStartMinute, Integer eEventEndHour,
			Integer eEventEndMinute) {

		this.mEventId = eEventId;
		this.mEventTitle = eEventTitle;

		this.mEventDescription = eEventDescription;
		this.mEventAddress = eEventAddress;
		this.mEventLatitude = eEventLatitude;
		this.mEventLongitude = eEventLongitude;
		this.mEventMemberNo = eEventMemberNo;

		this.mEventYear = eEventYear;
		this.mEventDay = eEventDay;
		this.mEventMonth = eEventMonth;

		this.mEventStartHour = eEventStartHour;
		this.mEventStartMinute = eEventStartMinute;
		this.mEventEndHour = eEventEndHour;
		this.mEventEndMinute = eEventEndMinute;

	}

	public EventModelClass(String eEventTitle, String eEventDescription,
			String eEventAddress, double eEventLatitude,
			double eEventLongitude, Integer eEventMemberNo, Integer eEventYear,
			Integer eEventDay, Integer eEventMonth, Integer eEventStartHour,
			Integer eEventStartMinute, Integer eEventEndHour,
			Integer eEventEndMinute) {

		this.mEventTitle = eEventTitle;

		this.mEventDescription = eEventDescription;
		this.mEventAddress = eEventAddress;
		this.mEventLatitude = eEventLatitude;
		this.mEventLongitude = eEventLongitude;
		this.mEventMemberNo = eEventMemberNo;

		this.mEventYear = eEventYear;
		this.mEventDay = eEventDay;
		this.mEventMonth = eEventMonth;

		this.mEventStartHour = eEventStartHour;
		this.mEventStartMinute = eEventStartMinute;
		this.mEventEndHour = eEventEndHour;
		this.mEventEndMinute = eEventEndMinute;

	}

	public Integer getmEventId() {
		return mEventId;
	}

	public void setmEventId(Integer mEventId) {
		this.mEventId = mEventId;
	}

	public String getmEventTitle() {
		return mEventTitle;
	}

	public void setmEventTitle(String mEventTitle) {
		this.mEventTitle = mEventTitle;
	}

	public String getmEventDescription() {
		return mEventDescription;
	}

	public void setmEventDescription(String mEventDescription) {
		this.mEventDescription = mEventDescription;
	}

	public String getmEventAddress() {
		return mEventAddress;
	}

	public void setmEventAddress(String mEventAddress) {
		this.mEventAddress = mEventAddress;
	}

	public double getmEventLatitude() {
		return mEventLatitude;
	}

	public void setmEventLatitude(double mEventLatitude) {
		this.mEventLatitude = mEventLatitude;
	}

	public double getmEventLongitude() {
		return mEventLongitude;
	}

	public void setmEventLongitude(double mEventLongitude) {
		this.mEventLongitude = mEventLongitude;
	}

	public Integer getmEventMemberNo() {
		return mEventMemberNo;
	}

	public void setmEventMemberNo(Integer mEventMemberNo) {
		this.mEventMemberNo = mEventMemberNo;
	}

	public Integer getmEventYear() {
		return mEventYear;
	}

	public void setmEventYear(Integer mEventYear) {
		this.mEventYear = mEventYear;
	}

	public Integer getmEventDay() {
		return mEventDay;
	}

	public void setmEventDay(Integer mEventDay) {
		this.mEventDay = mEventDay;
	}

	public Integer getmEventMonth() {
		return mEventMonth;
	}

	public void setmEventMonth(Integer mEventMonth) {
		this.mEventMonth = mEventMonth;
	}

	public Integer getmEventStartHour() {
		return mEventStartHour;
	}

	public void setmEventStartHour(Integer mEventStartHour) {
		this.mEventStartHour = mEventStartHour;
	}

	public Integer getmEventStartMinute() {
		return mEventStartMinute;
	}

	public void setmEventStartMinute(Integer mEventStartMinute) {
		this.mEventStartMinute = mEventStartMinute;
	}

	public Integer getmEventEndHour() {
		return mEventEndHour;
	}

	public void setmEventEndHour(Integer mEventEndHour) {
		this.mEventEndHour = mEventEndHour;
	}

	public Integer getmEventEndMinute() {
		return mEventEndMinute;
	}

	public void setmEventEndMinute(Integer mEventEndMinute) {
		this.mEventEndMinute = mEventEndMinute;
	}

	public String getmEventDate() {
		return mEventDate;
	}

	public void setmEventDate(String mEventDate) {
		this.mEventDate = mEventDate;
	}

	public String getmEventStartTime() {
		return mEventStartTime;
	}

	public void setmEventStartTime(String mEventStartTime) {
		this.mEventStartTime = mEventStartTime;
	}

	public String getmEventEndTime() {
		return mEventEndTime;
	}

	public void setmEventEndTime(String mEventEndTime) {
		this.mEventEndTime = mEventEndTime;
	}

}
