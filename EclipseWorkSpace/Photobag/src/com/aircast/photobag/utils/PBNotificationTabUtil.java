package com.aircast.photobag.utils;

import com.aircast.photobag.activity.PBMainTabBarActivity;

public class PBNotificationTabUtil {
	public static void showNotificationAt(int tabNumber, String text){
		if(PBMainTabBarActivity.sMainContext!=null)
			PBMainTabBarActivity.sMainContext.showNotificationTab(tabNumber, text);
	}
	
	public static void hideNotificationAt(int tabNumber){
		if(PBMainTabBarActivity.sMainContext!=null){
			PBMainTabBarActivity.sMainContext.hideNotificationTab(tabNumber);
		}
	}
}
