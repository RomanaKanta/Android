package com.smartmux.filevaultfree;

import android.app.Application;
import android.content.Context;

import com.onesignal.OneSignal;
import com.smartmux.filevaultfree.utils.OneSignalNotificationOpenedHandler;

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
	    }

    public static AppClass getApplication(Context context) {
        if (context instanceof AppClass) {
            return (AppClass) context;
        }
        return (AppClass) context.getApplicationContext();
    }

//    private AudioRecorder mAudioRecorder;
//
//    public AudioRecorder createRecorder(String targetFileName) {
//        mAudioRecorder = AudioRecorder.build(this, targetFileName);
//        return mAudioRecorder;
//    }
//
//    public AudioRecorder getRecorder() {
//        return mAudioRecorder;
//    }

 
}
