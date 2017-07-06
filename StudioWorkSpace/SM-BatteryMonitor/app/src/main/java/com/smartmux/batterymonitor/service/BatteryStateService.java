package com.smartmux.batterymonitor.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.smartmux.batterymonitor.MainActivity;
import com.smartmux.batterymonitor.receiver.BatteryChangedReceiver;
import com.smartmux.batterymonitor.utills.SM_Constants;
import com.smartmux.batterymonitor.utills.SM_Sharedpreferance;
import com.smartmux.batterymonitor.R;

public class BatteryStateService extends Service implements
		OnSharedPreferenceChangeListener {

	public static final int MSG_CLEAR_STATISTICS = 3;
	public static final int MSG_COPY_DB_TO_SDCARD = 4;
	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_REQUEST_REMAINING_TIME = 5;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_UPDATE_WIDGETS = 6;
	public static final int MY_NOTIFICATION_ID = 1;
	private static final String TAG = "MonitorBatteryStateService";

	private SharedPreferences appPreferences = null;
	private BatteryChangedReceiver batteryChangedReceiver = null;
	private Notification myNotification = null;
	private NotificationManager notificationManager = null;

	SM_Sharedpreferance mSM_Sharedpreferance = null;
	int batteryLevel = 0;
	int batteryState = 0;
	int batteryPlug = 0;
	String estimatedTime = "";
	String remainingTime = "";
	Editor editor = null;

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
		
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	public void onSharedPreferenceChanged(
			final SharedPreferences sharedPreferences, final String key) {
		
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startid) {
		//
		Log.v(BatteryStateService.TAG,
				"Starting service for collecting battery statistics...");
		mSM_Sharedpreferance = new SM_Sharedpreferance(getApplicationContext());
		this.appPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.getApplicationContext());
		this.appPreferences.registerOnSharedPreferenceChangeListener(this);

		this.notificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
//
		this.batteryChangedReceiver = new BatteryChangedReceiver(this);
		this.registerReceiver(this.batteryChangedReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
			
			showNewPercentageNotification();
		
		Log.v(BatteryStateService.TAG, "Service successfully started");
		return Service.START_STICKY;
	}

	public void showNewPercentageNotification() {

		if (!this.appPreferences.getBoolean("advance.show_notification_bar",
				true)) {
			return;
		}

		mSM_Sharedpreferance = new SM_Sharedpreferance(getApplicationContext());
		batteryState = mSM_Sharedpreferance.sharedPref.getInt(
				SM_Constants.status, 0);
		batteryPlug = mSM_Sharedpreferance.sharedPref.getInt(SM_Constants.plug,
				0);
		batteryLevel = mSM_Sharedpreferance.sharedPref.getInt(
				SM_Constants.current_level, 0);
		estimatedTime = mSM_Sharedpreferance.sharedPref.getString(
				SM_Constants.estimatetime, "");
		remainingTime = mSM_Sharedpreferance.sharedPref.getString(
				SM_Constants.remainingtime, "");

		
		// prepare the notification object
		final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				this.getApplicationContext());
		for (int i = 0; i <= 100; i++) {
			if (i == batteryLevel) {
				notificationBuilder.setSmallIcon(SM_Constants.myImageList[i]);
			}
		}

		// Large icon appears on the left of the notification
		notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(
				getResources(), R.mipmap.ic_launcher));
		switch (batteryState) {
		case BatteryManager.BATTERY_STATUS_CHARGING:
			// Content title, which appears in large type at the top of the
			// notification
			notificationBuilder.setContentTitle(getApplicationContext()
					.getString(R.string.battery_state_charging)
					+ "  ("
					+ batteryLevel + "%)");
			
			break;
		case BatteryManager.BATTERY_STATUS_DISCHARGING:

			notificationBuilder.setContentTitle(getApplicationContext()
					.getString(R.string.battery_state_discharging)
					+ "  ("
					+ batteryLevel + "%)");

			break;
		case BatteryManager.BATTERY_STATUS_FULL:

			notificationBuilder.setContentTitle(getApplicationContext()
					.getString(R.string.battery_state_full)
					+ "  ("
					+ batteryLevel + "%)");

			break;
		default:

			notificationBuilder.setContentTitle(getApplicationContext()
					.getString(R.string.battery_state_discharging)
					+ "  ("
					+ batteryLevel + "%)");
	
			break;
		}

		notificationBuilder.setOngoing(true);
		// This intent is fired when notification is clicked
		Intent intent = new Intent(this.getApplicationContext(),
				MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				this.getApplicationContext(), 0, intent, 0);

		// Set the intent that will fire when the user taps the notification.
		notificationBuilder.setContentIntent(pendingIntent);

		notificationBuilder.setPriority(NotificationCompat.PRIORITY_LOW);

		// if the capacity reaches 15%, use a high priority
		if (batteryLevel <= 15) {
			notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
		}

		// show the notification
		if (batteryPlug > 0) {
			// Content text, which appears in smaller text below the title
			notificationBuilder.setContentText(estimatedTime);
		}

		else {
			notificationBuilder.setContentText(remainingTime);

		}

		// get the created notification and show it
		this.myNotification = notificationBuilder.build();

		this.notificationManager.notify(BatteryStateService.MY_NOTIFICATION_ID,
				this.myNotification);
	}

	public void copyDatabaseToSDCard() {
		// TODO Auto-generated method stub

	}
}