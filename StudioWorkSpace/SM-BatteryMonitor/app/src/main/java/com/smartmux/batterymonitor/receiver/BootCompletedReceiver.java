package com.smartmux.batterymonitor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.smartmux.batterymonitor.service.BatteryStateService;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive( final Context context, final Intent intent ) {
		// start the monitoring service
		context.startService( new Intent( context, BatteryStateService.class ) );
	}

}
