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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmux.trackfriends.modelclass.EventModelClass;
import com.smartmux.trackfriends.utils.Constant;
import com.smartmux.trackfriends.utils.JSONParser;
import com.smartmux.trackfriends.utils.PBPreferenceUtils;
import com.smartmux.trackfriends.utils.Picker;
import com.smartmux.trackfriends.utils.Utils;

@SuppressWarnings("deprecation")
public class EventCreationActivity extends Activity implements OnClickListener {

	Button mCalcel, mCreate = null;
	TextView eventDate, startTime, endTime = null;
	ImageView location = null;
	EditText eventTitle, eventMemberNumber, eventDescription,
			eventAddress = null;
	Picker mDateTimePicker = null;
	Boolean profile = false;
	EventModelClass mEventModel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.event_creation);

		mDateTimePicker = new Picker();

		profile = PBPreferenceUtils.getBoolPref(getApplicationContext(),
				Constant.PREF_NAME, Constant.ISPROFILECREATED, false);

		eventTitle = (EditText) findViewById(R.id.event_name);
		eventMemberNumber = (EditText) findViewById(R.id.member_no);
		eventDescription = (EditText) findViewById(R.id.event_description);
		eventAddress = (EditText) findViewById(R.id.event_address);
		location = (ImageView) findViewById(R.id.ImageView_event_address_position);
		eventDate = (TextView) findViewById(R.id.event_date);
		startTime = (TextView) findViewById(R.id.event_start);
		endTime = (TextView) findViewById(R.id.event_end);
		mCalcel = (Button) findViewById(R.id.cancel);
		mCreate = (Button) findViewById(R.id.create);
		
		this.eventDate.setOnClickListener(this);
		this.startTime.setOnClickListener(this);
		this.endTime.setOnClickListener(this);
		this.mCalcel.setOnClickListener(this);
		this.mCreate.setOnClickListener(this);
		this.location.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.event_date:

			mDateTimePicker = new Picker(EventCreationActivity.this, eventDate);
			mDateTimePicker.dateDialog.show();

			break;

		case R.id.event_start:

			mDateTimePicker = new Picker(EventCreationActivity.this, startTime);
			mDateTimePicker.timeDialog.show();

			break;

		case R.id.event_end:

			mDateTimePicker = new Picker(EventCreationActivity.this, endTime);
			mDateTimePicker.timeDialog.show();

			break;

		case R.id.cancel:
			Intent mIntent = new Intent(getApplicationContext(),
					MainActivity.class);

			startActivity(mIntent);
			
			finish();
			this.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);//(intent activity, current activity)
			break;

		case R.id.create:

			if (profile == false) {
				Intent intent = new Intent(getApplicationContext(),
						ProfileCreateActivity.class);

				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
				
			} else {
				new sendEventIdToServer(eventTitle.getText().toString(), "23.8582805", "90.4007752")
						.execute();

				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);

				startActivity(intent);
				finish();
				
				  this.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
			}

			break;

		case R.id.ImageView_event_address_position:

			   // Creating Bundle object
            Bundle mBundle = new Bundle();
             
            // Storing data into bundle
            mBundle.putInt("position",5);
      
			Intent iIntent = new Intent(EventCreationActivity.this,
					MainActivity.class);
		    iIntent.putExtras(mBundle);	       
			startActivity(iIntent);


			break;
			
		default:
			break;

		}
	}

	private class sendEventIdToServer extends
			AsyncTask<String, String, JSONObject> {

		String title, lat, lng;

		public sendEventIdToServer(String eventTitle, String latitude,
				String longitude) {
			super();
			this.title = eventTitle;
			this.lat = latitude;
			this.lng = longitude;
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			// Creating new JSON Parser
			JSONParser jParser = new JSONParser();

			String uid = Utils.getUID(EventCreationActivity.this);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("uid", uid));
			nameValuePairs.add(new BasicNameValuePair("title", title));
			nameValuePairs.add(new BasicNameValuePair("latitude", lat));
			nameValuePairs.add(new BasicNameValuePair("longitude", lng));
			nameValuePairs.add(new BasicNameValuePair("debug", Constant.debug));

//			nameValuePairs.add(new BasicNameValuePair("uid", uid));
//			nameValuePairs.add(new BasicNameValuePair("event_id", String.valueOf(mEventModel.getmEventId())));
//			nameValuePairs.add(new BasicNameValuePair("title", mEventModel.getmEventTitle()));
//			nameValuePairs.add(new BasicNameValuePair("date", mEventModel.getmEventDate()));
//			nameValuePairs.add(new BasicNameValuePair("start_time", mEventModel.getmEventStartTime()));
//			nameValuePairs.add(new BasicNameValuePair("end_time", mEventModel.getmEventEndTime()));
//			nameValuePairs.add(new BasicNameValuePair("member_no", String.valueOf(mEventModel.getmEventMemberNo())));
//			nameValuePairs.add(new BasicNameValuePair("description", mEventModel.getmEventDescription()));
//			nameValuePairs.add(new BasicNameValuePair("address", mEventModel.getmEventAddress()));
//			nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(mEventModel.getmEventLatitude())));
//			nameValuePairs.add(new BasicNameValuePair("longitude", String.valueOf(mEventModel.getmEventLongitude())));
//			nameValuePairs.add(new BasicNameValuePair("modified", Constant.is_modified));
//			nameValuePairs.add(new BasicNameValuePair("debug", Constant.debug));
//			
			
			// String analyticsDataFormat =
			// "["+uid+"]-[NULL TOKEN]-["+device+"]-["+version+"]";

			// easyTracker.send(MapBuilder.createEvent("Bondona Kabir"+"-"+Constant.debug,
			// "Sign Up", "Sign Up : "+analyticsDataFormat,
			// null).build());

			JSONObject json = jParser.getJSONFromUrlwithParameter(
					Constant.URL_FOR_EVENT_CREATE, nameValuePairs);

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
									Constant.ISEVENTCREATED, true);

						} else if (result.getString("status").equals("NG")) {

							PBPreferenceUtils.saveBoolPref(
									getApplicationContext(),
									Constant.PREF_NAME,
									Constant.ISEVENTCREATED, false);

							// 07-01 21:06:31.411: D/json(10213):
							// {"status":"NG","message":"UID already exists."}

							if (result.getString("message").equals(
									"Event Created successfully.")) {

								PBPreferenceUtils.saveBoolPref(
										getApplicationContext(),
										Constant.PREF_NAME,
										Constant.ISEVENTCREATED, true);
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
