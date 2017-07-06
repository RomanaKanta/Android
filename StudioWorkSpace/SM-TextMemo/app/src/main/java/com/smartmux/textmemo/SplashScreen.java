package com.smartmux.textmemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.smartmux.textmemo.utils.AppUserInfo;


public class SplashScreen extends AppMainActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    private AppUserInfo appUserinfo;
    private String user_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        appUserinfo = new AppUserInfo(this);
        user_password = appUserinfo.getPassword();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if (user_password == null || user_password.length() == 0) {

                    Intent i = new Intent(SplashScreen.this,
                            RegistrationActivity.class);
                    startActivity(i);
                    finish();

                } else {

                    Intent i = new Intent(SplashScreen.this,
                            NoteListActivity.class);

                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

}
