package com.smartmux.batterymonitor.calculation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.BatteryManager;

import com.smartmux.batterymonitor.utills.*;

public class TimeCalculation {
	
	long diff = 0;
	int status = 0;
	public long timeDurationForBatteryDown = 0;
	public long timeDurationForBatteryCharging = 0;
	int charginglevel = 0;

	public String timeRemainingForDown = "";
	public String timeRemainingForFull = "";
	SM_Sharedpreferance mSM_Sharedpreferance = null;
	Editor editor;
	
	public TimeCalculation() {
		super();
	}

	public TimeCalculation(Context context, Intent intent) {
		super();
		mSM_Sharedpreferance = new SM_Sharedpreferance(context);

		charginglevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		editor = mSM_Sharedpreferance.sharedPref.edit();
		editor.putInt(SM_Constants.current_level, charginglevel);
		editor.commit();
		
		status = mSM_Sharedpreferance.sharedPref.getInt(SM_Constants.status, 0);

		if (status == BatteryManager.BATTERY_STATUS_CHARGING) {

			timeDurationForBatteryCharging = mSM_Sharedpreferance.sharedPref.getLong(
					SM_Constants.batteryCharging, 0);

			int remaininglevel = 100 - charginglevel;

			long tmp = timeDurationForBatteryCharging * remaininglevel;

			timeRemainingForFull = getTimeInHourMunite(tmp);
			editor.putString(SM_Constants.estimatetime, timeRemainingForFull);
			editor.commit();
		}

		if (status == BatteryManager.BATTERY_STATUS_DISCHARGING
				|| status == BatteryManager.BATTERY_STATUS_FULL
				|| status == BatteryManager.BATTERY_STATUS_UNKNOWN) {

			timeDurationForBatteryDown = mSM_Sharedpreferance.sharedPref.getLong(SM_Constants.batteryDown, 0);

			long temp = timeDurationForBatteryDown * charginglevel;

			timeRemainingForDown = getTimeInHourMunite(temp);
			editor.putString(SM_Constants.remainingtime, timeRemainingForDown);
			editor.commit();
		}
	}

	public TimeCalculation(Context context, Intent intent, int status) {
		super();
		mSM_Sharedpreferance = new SM_Sharedpreferance(context);

		charginglevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		editor = mSM_Sharedpreferance.sharedPref.edit();
		editor.putInt(SM_Constants.current_level, charginglevel);
		editor.commit();
		
		this.status = status;

		if (status == BatteryManager.BATTERY_STATUS_CHARGING) {

			timeDurationForBatteryCharging = mSM_Sharedpreferance.sharedPref.getLong(
					SM_Constants.batteryCharging, 0);

			int remaininglevel = 100 - charginglevel;

			long tmp = timeDurationForBatteryCharging * remaininglevel;

			timeRemainingForFull = getTimeInHourMunite(tmp);
			editor.putString(SM_Constants.estimatetime, timeRemainingForFull);
			editor.commit();
		}

		if (status == BatteryManager.BATTERY_STATUS_DISCHARGING
				|| status == BatteryManager.BATTERY_STATUS_FULL
				|| status == BatteryManager.BATTERY_STATUS_UNKNOWN) {

			timeDurationForBatteryDown = mSM_Sharedpreferance.sharedPref.getLong(SM_Constants.batteryDown, 0);

			long temp = timeDurationForBatteryDown * charginglevel;

			timeRemainingForDown = getTimeInHourMunite(temp);
			editor.putString(SM_Constants.remainingtime, timeRemainingForDown);
			editor.commit();
		}
	}

	
	
	public String getTimeInHourMunite(long tmp) {
		long diffSeconds = tmp / 1000 % 60;
		long diffMinutes = tmp / (60 * 1000) % 60;
		long diffHours = tmp / (60 * 60 * 1000) % 24;
		long diffDays = tmp / (24 * 60 * 60 * 1000);

		String hour = "";
		String min = "";

		if (diffHours > 1) {

			hour = diffHours + " H(s)";
		} else {

			hour = diffHours + " H";
		}

		if (diffMinutes > 1) {

			min = diffMinutes + " M(s)";
		} else {

			min = diffMinutes + " M";
		}
		String remaining = " " + hour + " and " + min;
		return remaining;
	}

}
