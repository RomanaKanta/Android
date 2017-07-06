package com.kanta.studio.watchout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        getScreenSize();

        /****** Create Thread that will sleep for 3 seconds *************/
        Thread mSplash = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 3 seconds
                    sleep(3 * 1000);

                    // After 3 seconds redirect to another intent
                    Intent iIntent = new Intent(getBaseContext(),
                            HomeActivity.class);
                    startActivity(iIntent);

                    // Remove activity
                    finish();
                }
                catch (Exception e) {
                }
            }
        };
        // start thread
        mSplash.start();
    }
    public void getScreenSize() {
        WindowManager wm = (WindowManager) SplashActivity.this
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        Log.e("SCREEN", "ht    "+ metrics.heightPixels + "   wt    " + metrics.widthPixels);


    }
}
