package com.smartmux.batterymonitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;

import com.smartmux.batterymonitor.adapter.FragmentAdapter;
import com.smartmux.batterymonitor.calculation.PowerUsage;
import com.smartmux.batterymonitor.fragment.FragmentBatteryStatus;
import com.smartmux.batterymonitor.service.BatteryStateService;
import com.smartmux.batterymonitor.R;
import com.smartmux.batterymonitor.widget.CirclePageIndicator;

public class MainActivity extends FragmentActivity {
	public static final int NOTIFICATION_ID = 1;
	ViewPager viewPager = null;
	FragmentAdapter adapterViewPager = null;
	private CirclePageIndicator mIndicator;
	PowerUsage mPowerUsage = null;
	android.support.v4.app.FragmentManager manager = null;
	android.support.v4.app.FragmentTransaction transaction = null;
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		// check if the service is running, if not start it
				if (!ApplicationCore.isServiceRunning(this,
						BatteryStateService.class.getName())) {
					Log.v("BatteryStateDisplay",
							"Monitoring service is not running,starting it");
					this.getApplicationContext().startService(
							new Intent(this.getApplicationContext(),
									BatteryStateService.class));
				}
		
		manager = getSupportFragmentManager();

		viewPager = (ViewPager) findViewById(R.id.myviewpager);
		adapterViewPager = new FragmentAdapter(getSupportFragmentManager());
		
		transaction = manager.beginTransaction();
		transaction.replace(R.id.fragemnt_battery_status, new FragmentBatteryStatus());
		transaction.commit();

	
		viewPager.setAdapter(adapterViewPager);
		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(viewPager);
		getBatteryUsage();
		

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}

	public void getBatteryUsage() {
		Object mPowerProfile_ = null;

		final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

		try {
			mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
					.getConstructor(Context.class).newInstance(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			double batteryCapacity = (Double) Class
					.forName(POWER_PROFILE_CLASS)
					.getMethod("getAveragePower", String.class)
					.invoke(mPowerProfile_, "battery.capacity");

			double wifion = (Double) Class.forName(POWER_PROFILE_CLASS)
					.getMethod("getAveragePower", String.class)
					.invoke(mPowerProfile_, "wifi.on");

			double wifiactive = (Double) Class.forName(POWER_PROFILE_CLASS)
					.getMethod("getAveragePower", String.class)
					.invoke(mPowerProfile_, "wifi.active");

			double wifiscan = (Double) Class.forName(POWER_PROFILE_CLASS)
					.getMethod("getAveragePower", String.class)
					.invoke(mPowerProfile_, "wifi.scan");

			double wifi = wifiactive + wifion + wifiscan;

			double brightOn = (Double) Class.forName(POWER_PROFILE_CLASS)
					.getMethod("getAveragePower", String.class)
					.invoke(mPowerProfile_, "screen.on");

			int curBrightnessValue = android.provider.Settings.System.getInt(
					MainActivity.this.getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS, -1);

			double brightFull = (Double) Class.forName(POWER_PROFILE_CLASS)
					.getMethod("getAveragePower", String.class)
					.invoke(mPowerProfile_, "screen.full");

			double bright = brightOn
					+ ((brightFull * curBrightnessValue) / 100);

			double gpsOn = (Double) Class.forName(POWER_PROFILE_CLASS)
					.getMethod("getAveragePower", String.class)
					.invoke(mPowerProfile_, "gps.on");

			double bluetoothOn = (Double) Class.forName(POWER_PROFILE_CLASS)
					.getMethod("getAveragePower", String.class)
					.invoke(mPowerProfile_, "bluetooth.on");

			double bluetoothActive = (Double) Class
					.forName(POWER_PROFILE_CLASS)
					.getMethod("getAveragePower", String.class)
					.invoke(mPowerProfile_, "bluetooth.active");

			double bluetooth = bluetoothOn + bluetoothActive;

			// Toast.makeText(MainActivity.this, batteryCapacity + " mah"+
			// bluetooth + "",
			// Toast.LENGTH_LONG).show();

			mPowerUsage = new PowerUsage(MainActivity.this, batteryCapacity,
					wifi, bright, gpsOn, bluetooth);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}