package com.aircast.photobag.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {
    
    // THANKS JASON
    public static boolean wasScreenOn = true;
 
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // Set the value as false
            wasScreenOn = false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // Set the value as true
            wasScreenOn = true;
        }
    }
 
}