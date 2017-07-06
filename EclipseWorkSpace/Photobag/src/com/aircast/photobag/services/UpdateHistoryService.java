package com.aircast.photobag.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.aircast.photobag.activity.PBMainTabBarActivity;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.gcm.ScreenReceiver;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;

public class UpdateHistoryService extends Service  {

	public static int UPDATE_TIME_PERIOD = 1000 * 3 * 60 ;

	private Handler handler;
	private BroadcastReceiver mReceiver=null;
   // private UpdateHistoryTask task;
	@Override
	public void onCreate() {


		handler = new Handler();
		handler.postDelayed(runnable, 100);

		PBDatabaseManager
				.getInstance(PBMainTabBarActivity.sMainContext);

		PBPreferenceUtils.getStringPref(getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
		
		// INITIALIZE RECEIVER for screen on/off
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		mReceiver = new ScreenReceiver();
		registerReceiver(mReceiver, filter);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
  
	@Override
	public void onDestroy() {
		
		handler.removeCallbacks(runnable);
		if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
		super.onDestroy();
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {

			boolean isAppBacground = PBGeneralUtils
					.isApplicationBroughtToBackground(getApplicationContext());
			if (!isAppBacground && ScreenReceiver.wasScreenOn && PBApplication.hasNetworkConnection()) {
				
				
				checkForUpdate();
				if(UPDATE_TIME_PERIOD == 100){
					UPDATE_TIME_PERIOD = 40000;
				}
			} 

			handler.postDelayed(this, UPDATE_TIME_PERIOD);
		}
	};

	public void checkForUpdate() {

		Log.d("UPDATE_TIME_PERIOD "," "+ UPDATE_TIME_PERIOD);
		UpdateHistoryTask task = new UpdateHistoryTask();
		task.execute();
		
	}



	public int getUpdateTimePeriod() {

		return UPDATE_TIME_PERIOD;
	}

	public void getUpdateTimePeriod(int time) {

		UpdateHistoryService.UPDATE_TIME_PERIOD = time;
	}
}
