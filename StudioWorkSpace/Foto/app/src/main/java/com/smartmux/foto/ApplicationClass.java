package com.smartmux.foto;

import android.app.Application;
import android.content.Context;

import com.onesignal.OneSignal;
import com.smartmux.foto.utils.OneSignalNotificationOpenedHandler;

/**
 * @author Nikolai Doronin {@literal <lassana.nd@gmail.com>}
 * @since 11/26/14.
 */
public class ApplicationClass extends Application {
	
	   @Override
	    public void onCreate() {
	        super.onCreate();
	        OneSignal.startInit(this)
	        .setNotificationOpenedHandler(new OneSignalNotificationOpenedHandler())
	        .init();
	    }

    public static ApplicationClass getApplication(Context context) {
        if (context instanceof ApplicationClass) {
            return (ApplicationClass) context;
        }
        return (ApplicationClass) context.getApplicationContext();
    }

  
 
}
