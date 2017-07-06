package com.aircast.photobag.openid;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import com.aircast.koukaibukuro.util.ApiHelper;
import com.aircast.koukaibukuro.util.ApiHelper.Response;
import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.facebook.Request;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class KBOpenIdRegistrationActivity extends Activity implements OnClickListener{
	
	private final String TAG = "KBOpenIdRegistrationActivity";
	private String getUserEmail = "";
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// Remove title bar
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.layout_tmp);

			
			findViewById(R.id.button_share_type_mail).setOnClickListener(this);
			findViewById(R.id.content_openid_back).setOnClickListener(this);
			findViewById(R.id.login_button).setOnClickListener(this);
			findViewById(R.id.button_share_type_facebook).setOnClickListener(this);
			
			


		}
		
		
		@Override
		protected void onResume() {
			super.onResume();
			String shareType = PBPreferenceUtils.getStringPref(getApplicationContext(), PBConstant.PREF_NAME, PBConstant.PREF_OPENID_SHARE_TYPE, "");
			if(shareType.equals("facebook")){
				findViewById(R.id.login_button).setVisibility(View.GONE);
		    	findViewById(R.id.button_share_type_facebook).setVisibility(View.VISIBLE);
			}else{
				
				findViewById(R.id.login_button).setVisibility(View.VISIBLE);
		    	findViewById(R.id.button_share_type_facebook).setVisibility(View.GONE);
			}
		}

		@Override
		protected void onPause() {
			super.onPause();
		}
		



		@Override
		public void onClick(View v) {
			
			if(v.getId() == R.id.button_share_type_mail){
				
				Intent intent = new Intent();
				intent.setClass(KBOpenIdRegistrationActivity.this, PBOpenIdMailActivity.class);
				startActivity(intent);
				
//				String mailId = PBPreferenceUtils.getStringPref(getApplicationContext(), PBConstant.PREF_NAME, PBConstant.PREF_OPENID_SHAREID,"");
//				if(mailId.equals("")){
//					
//					Intent intent = new Intent();
//					intent.setClass(KBOpenIdRegistrationActivity.this, PBOpenIdMailActivity.class);
//					startActivity(intent);
//				}else{
//					
//					Toast.makeText(getApplicationContext(), "Mail Already Registered.", Toast.LENGTH_SHORT).show();
//					//メールが既に登録されています。
//				}
				
			}else if(v.getId() == R.id.content_openid_back){
				
				finish();
				PBPreferenceUtils.saveBoolPref(getApplicationContext(),
						PBConstant.PREF_NAME, PBConstant.UPLOAD_TAG, false);
			}else if(v.getId() == R.id.button_share_type_facebook){
				
					Toast.makeText(getApplicationContext(), "FaceBook Already Registered.", Toast.LENGTH_SHORT).show();
					
			}else if(v.getId() == R.id.login_button){
				

				
				Session currentSession = Session.getActiveSession();
			    if (currentSession == null || currentSession.getState().isClosed()) {
			        Session session = new Session.Builder(KBOpenIdRegistrationActivity.this).build();
			        Session.setActiveSession(session);
			        currentSession = session;
			    }

			    if (currentSession.isOpened()) {
			        // Do whatever u want. User has logged in
//			  	Toast.makeText(getApplicationContext(), "FaceBook Already Registered", Toast.LENGTH_SHORT).show();
//			    	return;

			    } else if (!currentSession.isOpened()) {
			        // Ask for username and password
			        OpenRequest op = new Session.OpenRequest(KBOpenIdRegistrationActivity.this);

			        op.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
			        op.setCallback(null);

			        List<String> permissions = new ArrayList<String>();
			       // permissions.add("publish_stream");
			      //  permissions.add("user_likes");
			        permissions.add("email");
			      //  permissions.add("user_birthday");
			        op.setPermissions(permissions);

			        Session session = new Session.Builder(KBOpenIdRegistrationActivity.this).build();
			        Session.setActiveSession(session);
			        session.openForPublish(op);
			    }
			}
			
		}
		
		public void call(Session session, SessionState state, Exception exception) {
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
		    super.onActivityResult(requestCode, resultCode, data);
		    if (Session.getActiveSession() != null)
		        Session.getActiveSession().onActivityResult(this, requestCode,
		                resultCode, data);

		    Session currentSession = Session.getActiveSession();
		    if (currentSession == null || currentSession.getState().isClosed()) {
		        Session session = new Session.Builder(KBOpenIdRegistrationActivity.this).build();
		        Session.setActiveSession(session);
		        currentSession = session;
		    }

		    if (currentSession.isOpened()) {
		        Session.openActiveSession(this, true, new Session.StatusCallback() {

		            @Override
		            public void call(final Session session, SessionState state,
		                    Exception exception) {

		                if (session.isOpened()) {
		                	
							Request.newMeRequest(session, new Request.GraphUserCallback() {
							
	
							@Override
							public void onCompleted(GraphUser user,
									com.facebook.Response response) {
	
								if (response != null) {
									try {
	
										getUserEmail =user.getInnerJSONObject().getString("email");
										findViewById(R.id.login_button).setVisibility(View.GONE);
								    	findViewById(R.id.button_share_type_facebook).setVisibility(View.VISIBLE);
										  new OpenIdFBMailRegistration().execute();
	
									} catch (Exception e) {
										e.printStackTrace();
										Log.d("Exception", "Exception e");
									}
	
								}
	
							}
						}).executeAsync();


		                }
		            }
		        });
		    }
		}
		private class OpenIdFBMailRegistration extends
		AsyncTask<String, Void, Response> {


	@Override
	protected Response doInBackground(String... params) {

		String deviceUUID = PBPreferenceUtils.getStringPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, "0");

		Response response = ApiHelper.openIDRegistration(deviceUUID,
				getUserEmail, "facebook");

		return response;
	}

	@Override
	protected void onPostExecute(Response response) {
		// go to uploading screen
		super.onPostExecute(response);

		if (response != null) {
			int response_code = response.errorCode;
			String response_body = response.decription;

			if (PBAPIContant.DEBUG) {

				System.out.println(response_body);
			}

			if (response_code == 200) {

				PBPreferenceUtils.saveStringPref(getApplicationContext(), PBConstant.PREF_NAME, PBConstant.PREF_OPENID_SHARE_TYPE, "facebook");
				PBPreferenceUtils.saveStringPref(getApplicationContext(), PBConstant.PREF_NAME, PBConstant.PREF_OPENID_SHAREID,getUserEmail);
				Toast.makeText(getApplicationContext(), "Successfully logged in.", Toast.LENGTH_SHORT).show();

			}

		}

	}
}

}
