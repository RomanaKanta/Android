package com.smartmux.trackfriends.utils;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class Picker {
	
	public int mHour = 0;
	public int mMinute = 0;
	public int mYear = 0;
	public int mDay = 0;
	public int mMonth = 0;
	public Integer mHourSetting = 0;
	public Integer mMinuteSetting = 0;
	final Calendar mCalendar = Calendar.getInstance();
	TextView mTextView = null;
	Context mContext = null;
	public DatePickerDialog dateDialog ;
	public TimePickerDialog timeDialog;
	public Picker(){
		
	}
	public Picker(Context context,TextView tv) {
		// TODO Auto-generated constructor stub
		
		this.mContext = context;
		this.mTextView = tv;
		
		mYear = mCalendar.get(Calendar.YEAR);
		mMonth = mCalendar.get(Calendar.MONTH);
		mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
		
		mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
		mMinute = mCalendar.get(Calendar.MINUTE);
		
		 dateDialog = new DatePickerDialog(
				mContext, mDateSetListener, mYear,
				mMonth, mDay);
		//dialog.show();
		
		 timeDialog = new TimePickerDialog(
				mContext, startTimePickerListener, mHour,
				mMinute, false);
//		timeDialog.show();
	}
	// call DateSetListener for set selected date in edittext field
		private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				mYear = year;
				mMonth = monthOfYear;
				mDay = dayOfMonth;
				mTextView.setText(new StringBuilder()
						.append(new DecimalFormat("00").format(mDay)).append("-")
						.append(new DecimalFormat("00").format(mMonth + 1))
						.append("-").append(mYear));
			}
		};

		// call TimeSetListener for set selected time in edittext field
		private TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mHourSetting = hourOfDay;
				mMinuteSetting = minute;
				int hour = 0;
				String st = "";
				if (hourOfDay > 12) {
					hour = hourOfDay - 12;
					st = "PM";
				}

				else if (hourOfDay == 12) {
					hour = hourOfDay;
					st = "PM";
				}

				else if (hourOfDay == 0) {
					hour = hourOfDay + 12;
					st = "AM";
				} else {
					hour = hourOfDay;
					st = "AM";
				}
				mTextView.setText(new StringBuilder().append(hour).append(" : ")
						.append(minute).append(" ").append(st));
			}
		};
}
