package com.smartmux.videodownloader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends Activity{

	private static int SPLASH_TIME_OUT = 2000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.spalsh);
		
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				//
				Intent intent = new Intent(SplashScreen.this,
						MainActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
					

			}
		}, SPLASH_TIME_OUT);
		
	}
	
}
