package com.aircast.photobag.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.aircast.photobag.R;

/**
 * Application's splash screen, （´ ・(ｪ)・` ）クマ 
 */
public class PBSplashScreenActivity extends Activity{
    private static final long DELAY_TIME = 500;
    private static final int SPLASH_SCREEN_END = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    
        super.onCreate(savedInstanceState);
        getWindow().setWindowAnimations(android.R.style.Animation_Dialog);

        setContentView(R.layout.pb_layout_splashscreen);        
        mHandler.sendEmptyMessageDelayed(SPLASH_SCREEN_END, DELAY_TIME);
    }

    //handler for splash screen
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SPLASH_SCREEN_END:
                PBSplashScreenActivity.this.finish();
                
                Intent eventsIntent = new Intent(PBSplashScreenActivity.this, PBMainTabBarActivity.class);                
                startActivity(eventsIntent);
                // end SplashScreen from view
                break;
            default:
                break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onBackPressed() {
        PBSplashScreenActivity.this.finish();
        mHandler.removeMessages(SPLASH_SCREEN_END);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
