package com.smartmux.banglaebook.plus;

import android.app.Application;
import android.content.Context;

import com.onesignal.OneSignal;
import com.smartmux.banglaebook.plus.util.OneSignalNotificationOpenedHandler;

/**
 * @author Nikolai Doronin {@literal <lassana.nd@gmail.com>}
 * @since 11/26/14.
 */
public class AppClass extends Application {
	
	   @Override
	    public void onCreate() {
	        super.onCreate();
	        OneSignal.startInit(this)
	        .setNotificationOpenedHandler(new OneSignalNotificationOpenedHandler())
	        .init();
           OneSignal.sendTag("Development", "android_smartmux");
	    }

    public static AppClass getApplication(Context context) {
        if (context instanceof AppClass) {
            return (AppClass) context;
        }
        return (AppClass) context.getApplicationContext();
    }

  
 
}
