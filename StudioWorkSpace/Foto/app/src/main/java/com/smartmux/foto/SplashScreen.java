package com.smartmux.foto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SplashScreen extends Activity{

    private static int SPLASH_TIME_OUT = 1000;
    TextView smartmux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);

        smartmux = (TextView)findViewById(R.id.TextView_cm);
        smartmux.setTypeface(Typeface.createFromAsset(getAssets(),
                "fonts/Jura-DemiBold.otf"));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(SplashScreen.this, ImagePicker.class);
                startActivity(i);
                finish();

            }
        }, SPLASH_TIME_OUT);
    }

}


