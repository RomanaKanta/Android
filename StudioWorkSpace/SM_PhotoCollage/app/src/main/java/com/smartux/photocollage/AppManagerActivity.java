package com.smartux.photocollage;

import android.app.Activity;

public class AppManagerActivity {
	
	private static Activity currentActivity;
	
	public static void setLastActivity(Activity activity){
		//AppToast.show(activity, "Last Activity is:" + activity.toString());
		currentActivity = activity;
	}
	
	public static Activity getLastActivity(){
		return currentActivity;
	}
}
