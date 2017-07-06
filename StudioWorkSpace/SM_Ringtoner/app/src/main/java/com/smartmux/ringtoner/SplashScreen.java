package com.smartmux.ringtoner;

//https://github.com/google/ringdroid

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.smartmux.update.NewRingdroidSelectActivity;


public class SplashScreen extends Activity {
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable() {



            @Override
            public void run() {

                    Intent i = new Intent(SplashScreen.this, NewRingdroidSelectActivity.class);
                    startActivity(i);

                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
