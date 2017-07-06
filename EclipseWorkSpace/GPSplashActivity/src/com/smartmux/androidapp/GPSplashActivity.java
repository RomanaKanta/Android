package com.smartmux.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class GPSplashActivity extends Activity {

	private static int SPLASH_TIME_OUT = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash_view);
		
			
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					//
					Intent intent = new Intent(GPSplashActivity.this,
							HomeActivity.class);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
						

				}
			}, SPLASH_TIME_OUT);
			
			

		
	}


}
