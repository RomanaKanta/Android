package com.smartmux.trackfriends.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;

import com.smartmux.trackfriends.utils.Constant;
import com.smartmux.trackfriends.utils.JSONParser;
import com.smartmux.trackfriends.utils.PBPreferenceUtils;
import com.smartmux.trackfriends.utils.Utils;

@SuppressWarnings("deprecation")
public class LocationUpdateService extends Service implements LocationListener{

	@SuppressWarnings("unused")
	private static final String TAG = "LocationUpdateService";

	double mLatitude = 0;
	double mLongitude = 0;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
//		Toast.makeText(getApplicationContext(), "service start", Toast.LENGTH_LONG).show();
		
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		 mLatitude = location.getLatitude();
		 mLongitude = location.getLongitude() ;
//		 Toast.makeText(getApplicationContext(), "location changed", Toast.LENGTH_LONG).show();
		new sendLocationUpdateIdToServer( String.valueOf(mLatitude), String.valueOf(mLongitude))
		.execute();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

//    	Toast.makeText(getApplicationContext(), "service stop", Toast.LENGTH_LONG).show();
	}

	private class sendLocationUpdateIdToServer extends
			AsyncTask<String, String, JSONObject> {

		String lat, lng;

		public sendLocationUpdateIdToServer(String latitude, String longitude) {
			super();
			this.lat = latitude;
			this.lng = longitude;
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			// Creating new JSON Parser
			JSONParser jParser = new JSONParser();

			String uid = Utils.getUID(getApplicationContext());

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("uid", uid));
			nameValuePairs.add(new BasicNameValuePair("latitude", lat));
			nameValuePairs.add(new BasicNameValuePair("longitude", lng));
			nameValuePairs.add(new BasicNameValuePair("debug", Constant.debug));

			// String analyticsDataFormat =
			// "["+uid+"]-[NULL TOKEN]-["+device+"]-["+version+"]";

			// easyTracker.send(MapBuilder.createEvent("Bondona Kabir"+"-"+Constant.debug,
			// "Sign Up", "Sign Up : "+analyticsDataFormat,
			// null).build());

			JSONObject json = jParser.getJSONFromUrlwithParameter(
					Constant.URL_FOR_LOCATION_UPDATE, nameValuePairs);

			return json;
		}

		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			if (result != null) {

				try {

					if (result.has("status")) {

						if (result.getString("status").equals("OK")) {

							PBPreferenceUtils.saveBoolPref(
									getApplicationContext(),
									Constant.PREF_NAME,
									Constant.ISLOCATIONUPDATE, true);

						} else if (result.getString("status").equals("NG")) {

							PBPreferenceUtils.saveBoolPref(
									getApplicationContext(),
									Constant.PREF_NAME,
									Constant.ISLOCATIONUPDATE, false);

							// 07-01 21:06:31.411: D/json(10213):
							// {"status":"NG","message":"UID already exists."}

							if (result.getString("message").equals(
									"User added successfully.")) {

								PBPreferenceUtils.saveBoolPref(
										getApplicationContext(),
										Constant.PREF_NAME,
										Constant.ISLOCATIONUPDATE, true);
							}
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	

}
