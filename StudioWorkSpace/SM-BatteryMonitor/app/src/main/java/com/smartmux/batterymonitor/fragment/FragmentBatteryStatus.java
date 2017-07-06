package com.smartmux.batterymonitor.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmux.batterymonitor.calculation.TimeCalculation;
import com.smartmux.batterymonitor.calculation.TimerClass;
import com.smartmux.batterymonitor.utills.BatteryLevel;
import com.smartmux.batterymonitor.utills.SM_Constants;
import com.smartmux.batterymonitor.utills.SM_Sharedpreferance;
import com.smartmux.batterymonitor.R;

public class FragmentBatteryStatus extends Fragment {

	BroadcastReceiver batteryLevelReceiver =null;

	private BatteryLevel bl;
	private ImageView batteryImage;
	TextView textViewChargeState = null;
	TextView textViewTemperature = null;
	TextView textViewRemainingTime = null;
	TextView textViewTechnology = null;
	TextView textViewLevel = null;
	SM_Sharedpreferance mSM_Sharedpreferance = null;
	Editor editor = null;
	TimerClass mTimerClass = null;
	TimeCalculation mTimeCalculation = null;
	int batteryLevel = 0;
	int batteryState = 0;
	int batteryPlug = 0;
	String estimatedTime = "";
	String remainingTime = "";

	public static FragmentBatteryStatus newInstance() {
		FragmentBatteryStatus fragment = new FragmentBatteryStatus();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_battery_status,
				container, false);

		bl = new BatteryLevel(getActivity(), getResources().getInteger(
				R.integer.bl_inSampleSize));
		batteryImage = (ImageView) view.findViewById(R.id.chargeimage);
		batteryImage.setImageBitmap(bl.getBitmap());

		textViewChargeState = (TextView) view
				.findViewById(R.id.textview_text_current_chargingstate);
		textViewTemperature = (TextView) view
				.findViewById(R.id.textview_text_temperature);
		textViewRemainingTime = (TextView) view
				.findViewById(R.id.textview_text_remainingtime);
		textViewTechnology = (TextView) view
				.findViewById(R.id.textview_text_technology);
		textViewLevel = (TextView) view
				.findViewById(R.id.textView_battery_percentage);

		mSM_Sharedpreferance = new SM_Sharedpreferance(getActivity());
		editor = mSM_Sharedpreferance.sharedPref.edit();

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

		textViewLevel.setText("" + batteryLevel + "%");
			
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();	
		this.updateBatteryInformation();
	}


	private void updateBatteryInformation() {
		// get the current battery state and show it on the main activity
		 batteryLevelReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(final Context context, final Intent intent) {
				try {
					// ensure that we're not updating this receiver anymore (to
					// save battery)
					
					
					// get some important values into local variables
					final int rawlevel = intent.getIntExtra(
							BatteryManager.EXTRA_LEVEL, -1);

					bl.setLevel(rawlevel);
					batteryImage.invalidate();
					textViewLevel.setText("" + rawlevel + "%");
					editor.putInt(SM_Constants.current_level, rawlevel);
					editor.commit();

					final int scale = intent.getIntExtra(
							BatteryManager.EXTRA_SCALE, -1);

					final int status = intent.getIntExtra(
							BatteryManager.EXTRA_STATUS, -1);

					editor.putInt(SM_Constants.status, status);
					editor.commit();

					final float temp = (intent.getIntExtra(
							BatteryManager.EXTRA_TEMPERATURE, -1)) / 10.0f;
					textViewTemperature.setText("" + (int) temp
							+ "°C (Battery Temperature)");

					final int plugged = intent.getIntExtra(
							BatteryManager.EXTRA_PLUGGED, -1);
					editor.putInt(SM_Constants.plug, plugged);
					editor.commit();

					String technology = intent.getExtras().getString(
							BatteryManager.EXTRA_TECHNOLOGY);

					textViewTechnology.setText(technology);

					mTimerClass = new TimerClass(getActivity()
							.getApplicationContext(), intent, status);
					mTimeCalculation = new TimeCalculation(getActivity()
							.getApplicationContext(), intent);

					// if the device is plugged in, remember that
					if (plugged > 0) {
						textViewRemainingTime
								.setText(mTimeCalculation.timeRemainingForFull);

					}

					else {
						textViewRemainingTime
								.setText(mTimeCalculation.timeRemainingForDown);

					}

					// do a potential level scaling (most of the times not
					// required, but to be sure)
					@SuppressWarnings("unused")
					int level = -1;
					if ((rawlevel >= 0) && (scale > 0)) {
						level = (rawlevel * 100) / scale;
					}

					// set the text for the state of he main battery
					switch (status) {
					case BatteryManager.BATTERY_STATUS_CHARGING:
						textViewChargeState
								.setText(getString(R.string.battery_state_charging));
						break;
					case BatteryManager.BATTERY_STATUS_DISCHARGING:
						textViewChargeState
								.setText(getString(R.string.battery_state_discharging));
						break;
					case BatteryManager.BATTERY_STATUS_FULL:
						textViewChargeState
								.setText(getString(R.string.battery_state_full));
						break;
					default:
						textViewChargeState
								.setText(getString(R.string.battery_state_discharging));
						break;
					}

			
				} catch (final IllegalStateException e) {
					Log.e(SM_Constants.TAG,
							"The fragment was in an illegal state while it received the battery information. This should be handled in a different (and better way), The exception message was: ",
							e); // TODO
				}
			}
		};
		IntentFilter batteryLevelFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);

		getActivity()
				.registerReceiver(batteryLevelReceiver, batteryLevelFilter);
	}

	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(batteryLevelReceiver);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		bl.recycle();

	}

}
