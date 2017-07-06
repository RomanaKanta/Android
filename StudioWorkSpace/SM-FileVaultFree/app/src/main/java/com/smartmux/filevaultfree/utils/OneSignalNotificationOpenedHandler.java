package com.smartmux.filevaultfree.utils;

import android.util.Log;

import com.onesignal.OneSignal.NotificationOpenedHandler;

import org.json.JSONObject;

public class OneSignalNotificationOpenedHandler implements NotificationOpenedHandler {
    @Override
    public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
      try {
        if (additionalData != null) {
          if (additionalData.has("actionSelected"))
            Log.d("OneSignalExample", "OneSignal notification button with id " + additionalData.getString("actionSelected") + " pressed");

          Log.d("OneSignalExample", "Full additionalData:\n" + additionalData.toString());
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
}
