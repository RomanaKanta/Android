package com.smartmux.filevault;

import android.app.Activity;

public class AppActivityManager {
	
	private static Activity currentActivity;
	
	public static void setLastActivity(Activity activity){
		//AppToast.show(activity, "Last Activity is:" + activity.toString());
		currentActivity = activity;
	}
	
	public static Activity getLastActivity(){
		return currentActivity;
	}
}
