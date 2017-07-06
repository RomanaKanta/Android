package com.smartmux.batterymonitor.utills;

import android.content.Context;
import android.content.SharedPreferences;

public class SM_Sharedpreferance {
	public SharedPreferences sharedPref = null;
	
	public SM_Sharedpreferance(Context context){
		sharedPref = context.getSharedPreferences(SM_Constants.sharedPreferanceName,
				Context.MODE_PRIVATE);
	}
	
}
