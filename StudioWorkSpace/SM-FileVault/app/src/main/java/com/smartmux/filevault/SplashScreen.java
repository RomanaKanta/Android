package com.smartmux.filevault;

import com.smartmux.filevault.R;
import com.smartmux.filevault.utils.AppUserInfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends AppMainActivity{

    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        AppUserInfo appUserinfo = new AppUserInfo(this);
        final String user_password = appUserinfo.getPassword();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(user_password == null || user_password.length() == 0){

                    Intent i = new Intent(SplashScreen.this, RegistrationActivity.class);
                    startActivity(i);
                    finish();
                }else {


                    Intent i = new Intent(SplashScreen.this, FolderListActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }

}


