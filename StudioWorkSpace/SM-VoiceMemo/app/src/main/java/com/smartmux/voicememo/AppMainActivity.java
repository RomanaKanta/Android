package com.smartmux.voicememo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

public class AppMainActivity extends Activity {

	protected static final String TAG = Activity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    int itemId = item.getItemId();
	    switch (itemId) {
	    case android.R.id.home:
	        onKeyDown(KeyEvent.KEYCODE_BACK,null);
	        break;
	    }
	    return super.onMenuItemSelected(featureId, item);
	}


	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}
}
