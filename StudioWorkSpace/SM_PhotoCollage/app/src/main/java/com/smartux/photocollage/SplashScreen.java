package com.smartux.photocollage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.smartux.photocollage.model.SizeHolder;

public class SplashScreen extends AppMainActivity{

    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        getScreenSize();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
//                Intent intent = new Intent(SplashScreen.this,CollageActivity.class);
                Intent intent = new Intent(SplashScreen.this,CustomGalleryActivity.class);
                intent.setAction(getString(R.string.action_multiple_image_pic));
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public void getScreenSize() {
        WindowManager wm = (WindowManager) SplashScreen.this
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        int mrgnht = (int) SplashScreen.this.getResources().getDimension(
                R.dimen.bottom_ht)
                + (int) SplashScreen.this.getResources().getDimension(R.dimen.topbar_ht);
        int mrgnwt = (int) SplashScreen.this.getResources().getDimension(
                R.dimen.mrgn_left)
                + (int) SplashScreen.this.getResources().getDimension(R.dimen.mrgn_left);

        int deviceWidht = (metrics.widthPixels - mrgnwt);
        int deviceHeight = (metrics.heightPixels - mrgnht);

        SizeHolder.setHt(deviceHeight);
        SizeHolder.setWt(deviceWidht);

    }


}


