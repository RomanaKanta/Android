package com.smartmux.trackfriends;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.smartmux.trackfriends.utils.Constant;
import com.smartmux.trackfriends.utils.JSONParser;
import com.smartmux.trackfriends.utils.PBPreferenceUtils;
import com.smartmux.trackfriends.utils.Utils;

@SuppressWarnings("deprecation")
public class SplashScreen extends Activity {
	// Splash screen timer
	private static int SPLASH_TIME_OUT = 2000;
	Boolean signup=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash_screen);
		
		signup = PBPreferenceUtils.getBoolPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.ISSIGNUP, false);
		
		
		if (signup == false) {
			new sendRegistrationIdToServer("1234").execute();
		}

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				Intent intent = new Intent(SplashScreen.this,
						MainActivity.class);
				startActivity(intent);

				finish();
			}
		}, SPLASH_TIME_OUT);

	}

	private class sendRegistrationIdToServer extends
			AsyncTask<String, String, JSONObject> {

		String push_id;

		public sendRegistrationIdToServer(String push_id) {
			super();
			this.push_id = push_id;
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			// Creating new JSON Parser
			JSONParser jParser = new JSONParser();
			String identifier = Utils.getAppPackageName(SplashScreen.this);
			String uid = Utils.getUID(SplashScreen.this);
			String device = Utils.getDeviceModel();
			String version = Utils.getAppVersionNumber(SplashScreen.this);
			String platform = Utils.getAppPlatfrom();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs
					.add(new BasicNameValuePair("identifier", identifier));
			nameValuePairs.add(new BasicNameValuePair("uid", uid));
			nameValuePairs.add(new BasicNameValuePair("device", device));
			nameValuePairs.add(new BasicNameValuePair("version", version));
			nameValuePairs.add(new BasicNameValuePair("platform", platform));
			nameValuePairs.add(new BasicNameValuePair("push_id", push_id));
			nameValuePairs.add(new BasicNameValuePair("debug", Constant.debug));

			// String analyticsDataFormat =
			// "["+uid+"]-[NULL TOKEN]-["+device+"]-["+version+"]";

			// easyTracker.send(MapBuilder.createEvent("Bondona Kabir"+"-"+Constant.debug,
			// "Sign Up", "Sign Up : "+analyticsDataFormat,
			// null).build());

			JSONObject json = jParser.getJSONFromUrlwithParameter(
					Constant.URL_FOR_SIGNUP, nameValuePairs);

			return json;
		}

		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

//			Toast.makeText(getApplicationContext(), "" + result,
//					Toast.LENGTH_LONG).show();

			if (result != null) {

				try {

					if (result.has("status")) {

						if (result.getString("status").equals("OK")) {

							PBPreferenceUtils
									.saveBoolPref(getApplicationContext(),
											Constant.PREF_NAME,
											Constant.ISSIGNUP, true);

						} else if (result.getString("status").equals("NG")) {

							PBPreferenceUtils.saveBoolPref(
									getApplicationContext(),
									Constant.PREF_NAME, Constant.ISSIGNUP,
									false);

							// 07-01 21:06:31.411: D/json(10213):
							// {"status":"NG","message":"UID already exists."}

							if (result.getString("message").equals(
									"User Already Exist")) {

								PBPreferenceUtils.saveBoolPref(
										getApplicationContext(),
										Constant.PREF_NAME, Constant.ISSIGNUP,
										true);

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
