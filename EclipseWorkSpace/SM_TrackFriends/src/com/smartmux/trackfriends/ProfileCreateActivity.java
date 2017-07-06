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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smartmux.trackfriends.modelclass.ProfileModelClass;
import com.smartmux.trackfriends.utils.Constant;
import com.smartmux.trackfriends.utils.JSONParser;
import com.smartmux.trackfriends.utils.PBPreferenceUtils;
import com.smartmux.trackfriends.utils.Utils;

@SuppressWarnings("deprecation")
public class ProfileCreateActivity extends Activity {

	Button submit = null;
	EditText userName, age, phone, email = null;
	ProfileModelClass mProfileModel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.profile_creation);

		userName = (EditText) findViewById(R.id.EditText_name);
		age = (EditText) findViewById(R.id.EditText_age);
		phone = (EditText) findViewById(R.id.EditText_phone);
		email = (EditText) findViewById(R.id.EditText_email);
		submit = (Button) findViewById(R.id.submit);

		// on click activity of save button
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				new sendUserProfileIdToServer("SUMAN", "1",
						"sumancsc@yahoo.com", "0191010").execute();

				Intent mIntent = new Intent(getApplicationContext(),
						MainActivity.class);

				startActivity(mIntent);
				finish();
				 overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
			}
		});
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    this.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
	}
	
	private class sendUserProfileIdToServer extends
			AsyncTask<String, String, JSONObject> {

		String name, gender, email, phone;

		public sendUserProfileIdToServer(String userName, String gnd,
				String mail, String phn) {
			super();
			this.name = userName;
			this.gender = gnd;
			this.email = mail;
			this.phone = phn;
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			// Creating new JSON Parser
			JSONParser jParser = new JSONParser();

			String uid = Utils.getUID(ProfileCreateActivity.this);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("uid", uid));
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("gender", gender));
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("phone", phone));
			nameValuePairs.add(new BasicNameValuePair("debug", Constant.debug));
			
//			nameValuePairs.add(new BasicNameValuePair("uid", uid));
//			nameValuePairs.add(new BasicNameValuePair("name", mProfileModel.getmUserName()));
//			nameValuePairs.add(new BasicNameValuePair("age", mProfileModel.getmAge()));
//			nameValuePairs.add(new BasicNameValuePair("gender", mProfileModel.getmGender()));
//			nameValuePairs.add(new BasicNameValuePair("email", mProfileModel.getmEmail()));
//			nameValuePairs.add(new BasicNameValuePair("phone", mProfileModel.getmPhone()));
//			nameValuePairs.add(new BasicNameValuePair("image_path", mProfileModel.getmImagePath()));
//			nameValuePairs.add(new BasicNameValuePair("debug", Constant.debug));

			// String analyticsDataFormat =
			// "["+uid+"]-[NULL TOKEN]-["+device+"]-["+version+"]";

			// easyTracker.send(MapBuilder.createEvent("Bondona Kabir"+"-"+Constant.debug,
			// "Sign Up", "Sign Up : "+analyticsDataFormat,
			// null).build());

			JSONObject json = jParser.getJSONFromUrlwithParameter(
					Constant.URL_FOR_PROFILE_UPDATE, nameValuePairs);

			return json;
		}

		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			Toast.makeText(getApplicationContext(), "" + result,
					Toast.LENGTH_LONG).show();

			if (result != null) {

				try {

					if (result.has("status")) {

						if (result.getString("status").equals("OK")) {

							PBPreferenceUtils.saveBoolPref(
									getApplicationContext(),
									Constant.PREF_NAME,
									Constant.ISPROFILECREATED, true);

						} else if (result.getString("status").equals("NG")) {

							PBPreferenceUtils.saveBoolPref(
									getApplicationContext(),
									Constant.PREF_NAME,
									Constant.ISPROFILECREATED, false);

							// 07-01 21:06:31.411: D/json(10213):
							// {"status":"NG","message":"UID already exists."}

							if (result.getString("message").equals(
									"User Profile Updated.")
									|| result.getString("message").equals(
											"Email Already Exist [" + email
													+ "]")) {

								PBPreferenceUtils.saveBoolPref(
										getApplicationContext(),
										Constant.PREF_NAME,
										Constant.ISPROFILECREATED, true);

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
