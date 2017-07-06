package com.smartmux.batterymonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.smartmux.batterymonitor.R;


public class SplashScreen extends Activity {

	// Splash screen timer
		private static int SPLASH_TIME_OUT = 2000;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.splash_screen);
			
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {

					Intent intent = new Intent(SplashScreen.this,
							MainActivity.class);
					startActivity(intent);

					finish();
				}
			}, SPLASH_TIME_OUT);
		}

	}
