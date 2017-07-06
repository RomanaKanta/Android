package com.smartmux.filevault;

import android.app.Application;
import android.content.Context;

import com.onesignal.OneSignal;
import com.smartmux.filevault.utils.OneSignalNotificationOpenedHandler;

/**
 * @author Nikolai Doronin {@literal <lassana.nd@gmail.com>}
 * @since 11/26/14.
 */
public class ApplicationClass extends Application {

    public static ApplicationClass getApplication(Context context) {
        if (context instanceof ApplicationClass) {
            return (ApplicationClass) context;
        }
        return (ApplicationClass) context.getApplicationContext();
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

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new OneSignalNotificationOpenedHandler())
                .init();

    }
}
