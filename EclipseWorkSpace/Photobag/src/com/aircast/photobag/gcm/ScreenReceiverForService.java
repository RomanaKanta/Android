package com.aircast.photobag.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aircast.photobag.services.UploadService;

public class ScreenReceiverForService extends BroadcastReceiver {
    
	 private boolean screenOff;
	 
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
	            screenOff = true;
		        Intent i = new Intent(context, UploadService.class);
		        i.putExtra("screen_state", screenOff);
		        context.startService(i);
	        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
	            screenOff = false;
	        }

	    }
 
}