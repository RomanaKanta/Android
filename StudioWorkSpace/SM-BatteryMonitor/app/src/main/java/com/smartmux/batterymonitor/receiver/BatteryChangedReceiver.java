package com.smartmux.batterymonitor.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.support.v4.app.NotificationCompat;

import com.smartmux.batterymonitor.MainActivity;
import com.smartmux.batterymonitor.calculation.TimeCalculation;
import com.smartmux.batterymonitor.calculation.TimerClass;
import com.smartmux.batterymonitor.service.BatteryStateService;
import com.smartmux.batterymonitor.utills.SM_Constants;
import com.smartmux.batterymonitor.utills.SM_Sharedpreferance;
import com.smartmux.batterymonitor.R;

public class BatteryChangedReceiver extends BroadcastReceiver {
	public static final int MY_NOTIFICATION_ID = 1;
	SM_Sharedpreferance mSM_Sharedpreferance = null;
	Editor editor = null;
	TimerClass mTimerClass = null;
	TimeCalculation mTimeCalculation = null;
	private Service service = null;
	private Notification myNotification = null;
	private NotificationManager notificationManager = null;
	String estimatedTime = "";
	String remainingTime = "";

 public BatteryChangedReceiver(final BatteryStateService batteryStateService) {
		this.service = batteryStateService;

	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		final int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		final int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		final int powerSource = intent.getIntExtra(
				BatteryManager.EXTRA_PLUGGED, -1);
		final int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,
				-1);
		String technology = intent.getExtras().getString(
				BatteryManager.EXTRA_TECHNOLOGY);
		final int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

		mTimerClass = new TimerClass(context, intent, status);
		mTimeCalculation = new TimeCalculation(context, intent, status);
		mSM_Sharedpreferance = new SM_Sharedpreferance(context);

		estimatedTime = mSM_Sharedpreferance.sharedPref.getString(
				SM_Constants.estimatetime, "");
		remainingTime = mSM_Sharedpreferance.sharedPref.getString(
				SM_Constants.remainingtime, "");
		this.notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				context);
		for (int i = 0; i <= 100; i++) {
			if (i == level) {
				notificationBuilder.setSmallIcon(SM_Constants.myImageList[i]);
			}
		}

		// Large icon appears on the left of the notification
		notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(
				context.getResources(), R.mipmap.ic_launcher));
		switch (status) {
		case BatteryManager.BATTERY_STATUS_CHARGING:
			// Content title, which appears in large type at the top of the
			// notification
			notificationBuilder.setContentTitle(context
					.getString(R.string.battery_state_charging)
					+ "  ("
					+ level
					+ "%)");

			break;
		case BatteryManager.BATTERY_STATUS_DISCHARGING:

			notificationBuilder.setContentTitle(context
					.getString(R.string.battery_state_discharging)
					+ "  ("
					+ level + "%)");

			break;
		case BatteryManager.BATTERY_STATUS_FULL:

			notificationBuilder.setContentTitle(context
					.getString(R.string.battery_state_full)
					+ "  ("
					+ level
					+ "%)");

			break;
		default:

			notificationBuilder.setContentTitle(context
					.getString(R.string.battery_state_discharging)
					+ "  ("
					+ level + "%)");

			break;
		}

		notificationBuilder.setOngoing(true);
		// This intent is fired when notification is clicked
		Intent inten = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				inten, 0);

		// Set the intent that will fire when the user taps the notification.
		notificationBuilder.setContentIntent(pendingIntent);

		notificationBuilder.setPriority(NotificationCompat.PRIORITY_LOW);

		// if the capacity reaches 15%, use a high priority
		if (level <= 15) {
			notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
		}

		// show the notification
		if (powerSource > 0) {
			notificationBuilder
					.setContentText(mTimeCalculation.timeRemainingForFull);
		}

		else {
			notificationBuilder
					.setContentText(mTimeCalculation.timeRemainingForDown);
		}

		// get the created notification and show it
		this.myNotification = notificationBuilder.build();

		this.notificationManager.notify(this.MY_NOTIFICATION_ID,
				this.myNotification);

	}

}
