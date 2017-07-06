package com.smartmux.videodownloader.lockscreen.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;

public class GeneralUtills {
	 public static boolean isApplicationBroughtToBackground(Context context) {
	     ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	     List<RunningTaskInfo> tasks = am.getRunningTasks(1000);
	     if (!tasks.isEmpty()) {
	         ComponentName topActivity = tasks.get(0).topActivity;
	         if (!topActivity.getPackageName().equals(context.getPackageName())) {
	             return true;
	         }
	     }

	     return false;
	 }
}
