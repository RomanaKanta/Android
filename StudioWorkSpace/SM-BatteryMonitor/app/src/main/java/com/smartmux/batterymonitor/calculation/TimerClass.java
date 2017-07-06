package com.smartmux.batterymonitor.calculation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.BatteryManager;

import com.smartmux.batterymonitor.utills.SM_Constants;
import com.smartmux.batterymonitor.utills.SM_Sharedpreferance;

public class TimerClass {
	Context context = null;
	long endTime = 0;
	long startTime = 0;
	long timeDurationForBatteryDown = 600000;
	long timeDurationForBatteryCharging = 300000;
	int level = 0;
	int current_level = 0;
	int status ;
	SM_Sharedpreferance mSM_Sharedpreferance = null;
	Editor editor = null;

	public TimerClass() {
		super();
	}

	public TimerClass(Context context, Intent intent, int status) {
		super();
		this.context = context;
		this.status = status;
		this.current_level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

	mSM_Sharedpreferance = new SM_Sharedpreferance(context);
		editor = mSM_Sharedpreferance.sharedPref.edit();

		
		level = mSM_Sharedpreferance.sharedPref.getInt(SM_Constants.level, 0);
		
		if(level==0 || !(level==(level+1)) || !(level==(level-1))){
			
			editor.putInt(SM_Constants.level, current_level);
			editor.commit();
		}
		
		
		
		if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
			if (current_level==level) {
				this.startTime = System.currentTimeMillis();
			}
			if (current_level==(level +1)) {
				this.endTime = System.currentTimeMillis();

				this.timeDurationForBatteryCharging = Math.round(endTime
						- startTime);

			}
		}
		if (status == BatteryManager.BATTERY_STATUS_DISCHARGING
				|| status == BatteryManager.BATTERY_STATUS_FULL
				|| status == BatteryManager.BATTERY_STATUS_UNKNOWN) {
			if (level == current_level) {
				this.startTime = System.currentTimeMillis();
			}
			if (current_level == (level - 1)) {
				this.endTime = System.currentTimeMillis();

				this.timeDurationForBatteryDown = Math
						.round((endTime - startTime));

			}
		}

		editor.putLong(SM_Constants.batteryCharging, timeDurationForBatteryCharging);
		editor.putLong(SM_Constants.batteryDown, timeDurationForBatteryDown);
		editor.commit();
	}
}
