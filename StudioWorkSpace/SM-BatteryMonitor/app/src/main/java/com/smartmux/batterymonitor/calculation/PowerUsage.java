package com.smartmux.batterymonitor.calculation;

import com.smartmux.batterymonitor.utills.SM_Constants;
import com.smartmux.batterymonitor.utills.SM_Sharedpreferance;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class PowerUsage {
	SM_Sharedpreferance mSM_Sharedpreferance = null;
	Editor editor = null;
	Context mContext;
	int wifiResult;
	int brightResult;
	int gpsResult;
	int btResult;
	
	public PowerUsage(Context context){
	}
	
	public PowerUsage(Context context, double bCapacity, double wifitotal, double bright,double gps,double bluetooth){
		
		this.mContext = context;
		mSM_Sharedpreferance = new SM_Sharedpreferance(mContext);
		editor = mSM_Sharedpreferance.sharedPref.edit();
		this.wifiResult = (int) (Math.ceil((100/bCapacity) * wifitotal));
		this.brightResult = (int) (Math.ceil(((100/bCapacity) * bright) * 0.7));
		this.gpsResult = (int) (Math.ceil(((100/bCapacity) * gps) * 0.7));
		this.btResult = (int) (Math.ceil(((100/bCapacity) * bluetooth) * 0.7));
		
		editor.putInt(SM_Constants.wifiusage, wifiResult);
		editor.putInt(SM_Constants.brightnessusage, brightResult);
		editor.putInt(SM_Constants.gpsusage, gpsResult);
		editor.putInt(SM_Constants.bluetoothusage, btResult);
		editor.commit();
	}

}
