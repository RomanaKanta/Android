package com.smartmux.batterymonitor;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

public class ApplicationCore extends Application {

	private static final String TAG = "ApplicationCore";

	public static boolean isServiceRunning( final Context ctx, final String serviceName ) {
		Log.v( ApplicationCore.TAG, "Checking if the monitoring service is running or not..." );
		boolean serviceRunning = false;
		final ActivityManager am = (ActivityManager) ctx.getSystemService( Context.ACTIVITY_SERVICE );
		final List<ActivityManager.RunningServiceInfo> l = am.getRunningServices( 50 );
		final Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
		while ( i.hasNext() ) {
			final ActivityManager.RunningServiceInfo runningServiceInfo = i.next();

			if ( runningServiceInfo.service.getClassName().equals( serviceName ) && runningServiceInfo.started ) {
				serviceRunning = true;
			}
		}
		return serviceRunning;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
}
