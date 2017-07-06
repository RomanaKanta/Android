package com.smartmux.batterymonitor.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.smartmux.batterymonitor.MainActivity;
import com.smartmux.batterymonitor.calculation.TimeCalculation;
import com.smartmux.batterymonitor.R;

public class BatteryMonitorExtension extends DashClockExtension {

	private static final String TAG = "BATTERY_MONITOR";
	private final Messenger monitorServiceMessanger = new Messenger(
			new IncomingHandler());
	private int percentageLoaded = 0;
	private Messenger monitorService = null;
	TimeCalculation mTimeCalculation = new TimeCalculation();

	private final ServiceConnection monitorServiceConnection = new ServiceConnection() {

		public void onServiceConnected(final ComponentName className,
				final IBinder service) {
			BatteryMonitorExtension.this.monitorService = new Messenger(service);
			try {
				Log.d(BatteryMonitorExtension.TAG,
						"Trying to connect to the battery monitoring service...");
				final Message msg = Message.obtain(null,
						BatteryStateService.MSG_REGISTER_CLIENT);
				msg.replyTo = BatteryMonitorExtension.this.monitorServiceMessanger;
				BatteryMonitorExtension.this.monitorService.send(msg);
			} catch (final RemoteException e) {
				Log.e(BatteryMonitorExtension.TAG,
						"Failed to connect to the battery monitoring service!");
			}
		}

		public void onServiceDisconnected(final ComponentName className) {
			BatteryMonitorExtension.this.monitorService = null;
		}
	};

	private void doBindService() {
		this.getApplicationContext().bindService(
				new Intent(this.getApplicationContext(),
						BatteryStateService.class),
				this.monitorServiceConnection, Context.BIND_AUTO_CREATE);
	}

	private void doUnbindService() {
		if (this.monitorService != null) {
			try {
				final Message msg = Message.obtain(null,
						BatteryStateService.MSG_UNREGISTER_CLIENT);
				msg.replyTo = this.monitorServiceMessanger;
				this.monitorService.send(msg);
			} catch (final RemoteException e) {
			}
		}
		this.getApplicationContext().unbindService(
				this.monitorServiceConnection);
		this.monitorService = null;
	}

	@Override
	protected void onInitialize(boolean isReconnect) {
		this.setUpdateWhenScreenOn(true);
		super.onInitialize(isReconnect);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//
		this.doBindService();

		//
		this.updateBatteryInformation();

		//
		return super.onStartCommand(intent, flags, startId);
	}

	private void updateBatteryInformation() {
		// query the remaining time
		try {
			if (this.monitorService != null) {
				final Message msg2 = Message.obtain(null,
						BatteryStateService.MSG_REQUEST_REMAINING_TIME);
				msg2.replyTo = BatteryMonitorExtension.this.monitorServiceMessanger;
				this.monitorService.send(msg2);
			} else {
				Log.w(BatteryMonitorExtension.TAG,
						"No monitor service connected, trying to bind service!");
				this.doBindService();
			}
		} catch (RemoteException e) {
			Log.e(BatteryMonitorExtension.TAG,
					"Failed to query the current time estimation.");
		}

		// get the current battery state and show it on the main activity
		final BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(final Context context, final Intent intent) {
				try {
					// ensure that we're not updating this receiver anymore (to
					// save battery)
					context.unregisterReceiver(this);

					// get some important values into local variables
					final int rawlevel = intent.getIntExtra(
							BatteryManager.EXTRA_LEVEL, -1);
					final int scale = intent.getIntExtra(
							BatteryManager.EXTRA_SCALE, -1);

					// do a potential level scaling (most of the times not
					// required, but to be sure)
					int level = -1;
					if ((rawlevel >= 0) && (scale > 0)) {
						level = (rawlevel * 100) / scale;
					}

					Log.d(BatteryMonitorExtension.TAG,
							"Updating the DashClock widget (basic information).");
					BatteryMonitorExtension.this.percentageLoaded = level;
					BatteryMonitorExtension.this.sendPublishedData();
				} catch (final IllegalStateException e) {
					Log.e(BatteryMonitorExtension.TAG,
							"The fragment was in an illegal state while it received the battery information. This should be handled in a different (and better way), The exception message was: ",
							e); // TODO
				}
			}
		};
		final IntentFilter batteryLevelFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		this.getApplicationContext().registerReceiver(batteryLevelReceiver,
				batteryLevelFilter);
	}

	private void sendPublishedData() {
		Log.d(BatteryMonitorExtension.TAG,
				"Constructing and sending DashClock widget update...");

		//
//		if (this.remainingTimeEstimation.isValid) {
//			this.publishUpdate(new ExtensionData()
//					.visible(true)
//					.icon(R.drawable.ic_launcher)
//					.status(this.percentageLoaded + "%")
//					.expandedTitle(
//							String.format("title"))
//					.expandedBody(
//							String.format(mTimeCalculation.timeRemainingForFull))
//					.clickIntent(
//							new Intent(this.getApplicationContext(),
//									MainActivity.class)));
//		} else {
			this.publishUpdate(new ExtensionData()
					.visible(true)
					.icon(R.mipmap.ic_launcher)
					.status(this.percentageLoaded + "%")
					.expandedTitle(
							String.format("Title"))
					.expandedBody(mTimeCalculation.timeRemainingForFull)
					.clickIntent(
							new Intent(this.getApplicationContext(),
									MainActivity.class)));
//		}
	}

	@Override
	public void onDestroy() {
		// this.doUnbindService();
		super.onDestroy();
	}

	protected void onUpdateData(int reason) {
		Log.i(BatteryMonitorExtension.TAG, "Update requested... ");
		this.updateBatteryInformation();
		// this.publishUpdate(this.currentExtensionData);
	}

	class IncomingHandler extends Handler {

		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case BatteryStateService.MSG_REGISTER_CLIENT:
				// since the client is now registered, we can ask the service
				// about the remaining time we have
				try {
					// be sure that the monitor service is available, sometimes
					// (I don't know why) this is not the case
					if (null == BatteryMonitorExtension.this.monitorService) {
						Log.e(BatteryMonitorExtension.TAG,
								"Tried to query the remaining time but the monitor service was not available!");
						return;
					}

					// query the remaining time
					final Message msg2 = Message
							.obtain(null,
									BatteryStateService.MSG_REQUEST_REMAINING_TIME);
					msg2.replyTo = BatteryMonitorExtension.this.monitorServiceMessanger;
					BatteryMonitorExtension.this.monitorService.send(msg2);
				} catch (final RemoteException e1) {
					Log.e(BatteryMonitorExtension.TAG,
							"Failed to query the current time estimation.");
				}
				break;
//			case BatteryStateService.MSG_REQUEST_REMAINING_TIME:
//				BatteryMonitorExtension.this.remainingTimeEstimation = EstimationResult
//						.fromBundle(msg.getData());
//				Log.d(BatteryMonitorExtension.TAG, String.format(
//						"Received an time estimation of %d minutes.",
//						BatteryMonitorExtension.this.remainingTimeEstimation.minutes));
//				BatteryMonitorExtension.this.sendPublishedData();
//				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

}
