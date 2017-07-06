/*
 * Copyright (C) 2013 Surviving with Android (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smartmux.weatherapp;

import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.smartmux.weatherapp.adapter.CustomArrayAdapter;
import com.smartmux.weatherapp.model.DayForecast;
import com.smartmux.weatherapp.model.Weather;
import com.smartmux.weatherapp.model.WeatherForecast;
import com.smartmux.weatherapp.utils.Constants;
import com.smartmux.weatherapp.utils.FetchAddressIntentService;
import com.smartmux.weatherapp.utils.WeatherPreferenceUtils;

public class ActivityMain extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener {

	private String TAG = "ActivityMain";
	private HorizoantalListView mHlvSimpleList;
	private TextView cityText;
	private TextView windSpeed;
	private TextView temp;
	private TextView pressure;

	Context appContext;
	private TextView humidity;
	Intent intent;
	String addressOutput;
	private static String forecastDaysNum = "7";
	private RelativeLayout layout;
	public NetworkChecking mNetworkChecking = null;

	protected GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation;

	protected boolean mAddressRequested;

	protected String mAddressOutput;
	private AddressResultReceiver mResultReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);
		appContext = this.getApplicationContext();
		mNetworkChecking = new NetworkChecking(ActivityMain.this);

		layout = (RelativeLayout) findViewById(R.id.weather_summery_content);
		mHlvSimpleList = (HorizoantalListView) findViewById(R.id.hlvSimpleList);

		cityText = (TextView) findViewById(R.id.cityText);
		temp = (TextView) findViewById(R.id.temp);

		windSpeed = (TextView) findViewById(R.id.wind);
		humidity = (TextView) findViewById(R.id.Humidity);
		pressure = (TextView) findViewById(R.id.pressure);

		
			mResultReceiver = new AddressResultReceiver(new Handler());
			// Set defaults, then update using values stored in the Bundle.
			mAddressRequested = false;
			mAddressOutput = "";
			updateValuesFromBundle(savedInstanceState);
			updateUIWidgets();
			buildGoogleApiClient();
		
		String todayData = WeatherPreferenceUtils.getStringPref(
				getApplicationContext(), Constants.PREFS_NAME,
				Constants.TODAY_PREFS_KEY, "");
		if(!todayData.equals("")){
			Weather weather = new Weather();
			try {
				weather = JSONWeatherParser.getWeather(todayData);
				
					cityText.setText(mAddressOutput);
				
				float temper = weather.temperature.getTemp();
				temp.setText("" + Math.round((temper - 275.15)) + "°");
				windSpeed.setText("" + weather.wind.getSpeed() + "Kph");
				pressure.setText("" + weather.currentCondition.getPressure()
						+ "hPa");
				humidity.setText("%" + weather.currentCondition.getHumidity());

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

			
			String dataForcast = WeatherPreferenceUtils.getStringPref(
					getApplicationContext(), Constants.PREFS_NAME,
					Constants.WEEK_PREFS_KEY, "");
			
			if(!dataForcast.equals("")){
				
				WeatherForecast forecast = new WeatherForecast();
			   try {
				forecast = JSONWeatherParser.getForecastWeather(dataForcast);
				
				List<DayForecast> forecastList = forecast.getList();

				CustomArrayAdapter adapter = new CustomArrayAdapter(
						getApplicationContext(), forecastList);

				// Assign adapter to HorizontalListView
				mHlvSimpleList.setAdapter(adapter);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		

	}

	private void updateUIWidgets() {
		if (mAddressRequested) {
			// mProgressBar.setVisibility(ProgressBar.VISIBLE);
			// mFetchAddressButton.setEnabled(false);
		} else {
			// mProgressBar.setVisibility(ProgressBar.GONE);
			// mFetchAddressButton.setEnabled(true);
		}
	}

	private void updateValuesFromBundle(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			if (savedInstanceState.keySet().contains(
					Constants.ADDRESS_REQUESTED_KEY)) {
				mAddressRequested = savedInstanceState
						.getBoolean(Constants.ADDRESS_REQUESTED_KEY);
			}
			if (savedInstanceState.keySet().contains(
					Constants.LOCATION_ADDRESS_KEY)) {
				mAddressOutput = savedInstanceState
						.getString(Constants.LOCATION_ADDRESS_KEY);
				getWeatherInfo(mAddressOutput);
			}
			System.out.println(mAddressOutput);
		}
	}

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(mNetworkChecking.isNetworkAvailable(getApplicationContext()) == false){
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					ActivityMain.this);

			alertDialog.setTitle(R.string.no_network);
			alertDialog.setMessage(R.string.alart_msg);
			//alertDialog.setCancelable(false);
			alertDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.cancel();
						}
					});
			alertDialog.show();
		
		}
		
		if(mNetworkChecking.isGpsEnable(getApplicationContext()) == false){

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						ActivityMain.this);
	
				// Setting Dialog Title
				alertDialog.setTitle(R.string.no_gps);
	
				// Setting Dialog Message
				alertDialog
						.setMessage(R.string.gps_alart);
	
				// Setting Icon to Dialog
				// alertDialog.setIcon(R.drawable.delete);
	
				// On pressing Settings button
				alertDialog.setPositiveButton("Settings",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								ActivityMain.this.startActivity(intent);
								dialog.cancel();
	
							}
						});
	
				// on pressing cancel button
				alertDialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
	
							}
						});
	
				// Showing Alert Message
				alertDialog.show();

		
		}
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				if (mGoogleApiClient.isConnected() && mLastLocation != null) {
					startIntentService();
	//				showToast(getString(R.string.start_alart));
				}
				mAddressRequested = true;
				updateUIWidgets();
			}
		}, 500);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	protected void startIntentService() {
		intent = new Intent(this, FetchAddressIntentService.class);

		intent.putExtra(Constants.RECEIVER, mResultReceiver);

		intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

		startService(intent);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {

		Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
				+ result.getErrorCode());
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLastLocation = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);
		if (mLastLocation != null) {
			if (!Geocoder.isPresent()) {
				Toast.makeText(this, R.string.no_geocoder_available,
						Toast.LENGTH_LONG).show();
				return;
			}
			if (mAddressRequested) {
				startIntentService();
			}
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.i(TAG, "Connection suspended");
		mGoogleApiClient.connect();
	}

	protected void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean(Constants.ADDRESS_REQUESTED_KEY,
				mAddressRequested);

		savedInstanceState.putString(Constants.LOCATION_ADDRESS_KEY,
				mAddressOutput);
		super.onSaveInstanceState(savedInstanceState);
	}

	class AddressResultReceiver extends ResultReceiver {
		public AddressResultReceiver(Handler handler) {
			super(handler);
		}

		protected void onReceiveResult(int resultCode, Bundle resultData) {

			mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

			if (!mAddressOutput
					.equals(getString(R.string.service_not_available))) {

				WeatherPreferenceUtils.saveStringPref(getApplicationContext(),
						Constants.PREFS_NAME, Constants.ADDRESS_PREFS_KEY,
						mAddressOutput);
				
				getWeatherInfo(mAddressOutput);

				if (resultCode == Constants.SUCCESS_RESULT) {
					// showToast(getString(R.string.address_found));
				}

				mAddressRequested = false;
				updateUIWidgets();

			} else {
				if (mAddressOutput
						.equals(getString(R.string.service_not_available))) {
				 addressOutput = WeatherPreferenceUtils.getStringPref(
						getApplicationContext(), Constants.PREFS_NAME,
						Constants.ADDRESS_PREFS_KEY, "");
				
				getWeatherInfo(addressOutput);
//				
//				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//						ActivityMain.this);
//
//				alertDialog.setTitle(R.string.no_network);
//				alertDialog.setMessage(R.string.alart_msg);
//				//alertDialog.setCancelable(false);
//				alertDialog.setPositiveButton("OK",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int which) {
//								dialog.cancel();
//							}
//						});
//				alertDialog.show();
			}
			}
		}
	}

	public void getWeatherInfo(String address) {
		String city = address;
		String lang = "en";

		JSONWeatherTask task = new JSONWeatherTask();
		task.execute(new String[] { city, lang });

		JSONForecastWeatherTask task1 = new JSONForecastWeatherTask();
		task1.execute(new String[] { city, lang, forecastDaysNum });
	}

	private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

		@Override
		protected Weather doInBackground(String... params) {
			String data = null;
			Weather weather = new Weather();
			if (mAddressOutput
					.equals(getString(R.string.service_not_available))) {
				
				try {
					if (data == null) {

						data = WeatherPreferenceUtils.getStringPref(
								getApplicationContext(), Constants.PREFS_NAME,
								Constants.TODAY_PREFS_KEY, "");

					}
					weather = JSONWeatherParser.getWeather(data);
					System.out.println("Retrieve fromSF [" + data + "]");
					System.out.println("Weather [" + weather + "]");

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				// if (!mAddressOutput
				// .equals(getString(R.string.service_not_available))) {

				data = ((new WeatherHttpClient()).getWeatherData(params[0],
						params[1]));
				
				
				try {
					WeatherPreferenceUtils.saveStringPref(
							getApplicationContext(), Constants.PREFS_NAME,
							Constants.TODAY_PREFS_KEY, data);
					weather = JSONWeatherParser.getWeather(data);

					System.out.println("Retrieve fromSF [" + data + "]");
					System.out.println("Weather [" + weather + "]");

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return weather;
		}

		@Override
		public void onPostExecute(Weather weather) {
			super.onPostExecute(weather);
			if (mAddressOutput
					.equals(getString(R.string.service_not_available))) {
				cityText.setText(addressOutput);
			}else{
				cityText.setText(mAddressOutput);
			}
			
			float temper = weather.temperature.getTemp();
			temp.setText("" + Math.round((temper - 275.15)) + "°");
			// temp.setText(""
			// + Math.round((weather.temperature.getTemp() - 275.15))
			// + "°");
			windSpeed.setText("" + weather.wind.getSpeed() + "Kph");
			pressure.setText("" + weather.currentCondition.getPressure()
					+ "hPa");
			humidity.setText("%" + weather.currentCondition.getHumidity());

			ActivityMain.this.layout.setVisibility(View.VISIBLE);
		}
	}

	private class JSONForecastWeatherTask extends
			AsyncTask<String, Void, WeatherForecast> {

		@Override
		protected WeatherForecast doInBackground(String... params) {
			String data = null;
			data = ((new WeatherHttpClient()).getForecastWeatherData(params[0],
					params[1], params[2]));

			WeatherForecast forecast = new WeatherForecast();
			try {

				if (data == null) {
					data = WeatherPreferenceUtils.getStringPref(
							getApplicationContext(), Constants.PREFS_NAME,
							Constants.WEEK_PREFS_KEY, "");

				} else {
					WeatherPreferenceUtils.saveStringPref(
							getApplicationContext(), Constants.PREFS_NAME,
							Constants.WEEK_PREFS_KEY, data);
				}
				data = WeatherPreferenceUtils.getStringPref(
						getApplicationContext(), Constants.PREFS_NAME,
						Constants.WEEK_PREFS_KEY, "");
				forecast = JSONWeatherParser.getForecastWeather(data);
				System.out.println("Retrieve Forecast [" + data + "]");
				System.out.println("Weather [" + forecast + "]");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return forecast;
		}

		@Override
		protected void onPostExecute(WeatherForecast forecastWeather) {
			super.onPostExecute(forecastWeather);

			List<DayForecast> forecastList = forecastWeather.getList();

			CustomArrayAdapter adapter = new CustomArrayAdapter(
					getApplicationContext(), forecastList);

			// Assign adapter to HorizontalListView
			mHlvSimpleList.setAdapter(adapter);
		}
	}
}