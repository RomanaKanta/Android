package com.aircast.koukaibukuro;

import org.json.JSONException;
import org.json.JSONObject;

import com.aircast.koukaibukuro.util.ApiHelper;
import com.aircast.koukaibukuro.util.Constant;
import com.aircast.koukaibukuro.util.ProgressHUD;
import com.aircast.koukaibukuro.util.SPreferenceUtils;
import com.aircast.koukaibukuro.util.Util;
import com.aircast.koukaibukuro.util.ApiHelper.Response;
import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.openid.KBOpenIdRegistrationActivity;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class KBSettingsActivity extends Activity implements OnClickListener {

	//String shareType, shareID;
	private EditText nickName;
	private TextView txtNickName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.kb_settings_layout);
		
		
		txtNickName = (TextView) findViewById(R.id.kb_settings_nick_name);
		
		String nickname = SPreferenceUtils.getStringPref(getApplicationContext(),
				Constant.PREF_NAME,
				Constant.KB_NICKNAME,
				"");
		
		txtNickName.setText(nickname);
		
		findViewById(R.id.content_kb_settings_nickname).setOnClickListener(this);
		findViewById(R.id.content_kb_settings_openid).setOnClickListener(this);
		findViewById(R.id.content_kb_settings_back).setOnClickListener(this);
		
		
		if (Util.isOnline(KBSettingsActivity.this)) {
			PBTaskGetNickName task = new PBTaskGetNickName();
			task.execute();
		}


	}

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) 
				getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(nickName.getWindowToken(), 0);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	private class PBTaskGetNickName extends AsyncTask<Void, Void, Response> {

		@Override
		protected Response doInBackground(Void... params) {
			String uid = SPreferenceUtils.getStringPref(getApplicationContext(),
					Constant.PREF_NAME, Constant.UID, "");
			Response response = ApiHelper.getNickName(uid);
			return response;
		}

		@Override
		protected void onPostExecute(Response response) {
			super.onPostExecute(response);

			if (response != null) {
				int response_code = response.errorCode;
				String response_body = response.decription;

				try {
					JSONObject jObject;
					jObject = new JSONObject(response_body);

					if (response_code == 200) {
						jObject = new JSONObject(response_body);

						if (jObject != null) {
							if (jObject.has("message")) {

								
								/*
								 * defaultNickName =
								 * jObject.getString("message");
								 */
								SPreferenceUtils.saveStringPref(getApplicationContext(),
										Constant.PREF_NAME,
										Constant.KB_NICKNAME,
										jObject.getString("message"));
								
								txtNickName.setText(jObject.getString("message"));
							}

						}
					} else {
						// Toast when error occured
						jObject = new JSONObject(response_body);

						if (jObject != null) {
							if (jObject.has("message")) {

								// show toast
//								Toast.makeText(getApplicationContext(),
//										jObject.getString("message"),
//										Toast.LENGTH_SHORT).show();

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

	private class PBTaskNickNameRegistration extends
			AsyncTask<Void, String, Response> {

		String name;
		Context ctx;
		//ProgressHUD mProgressHUD;

		public PBTaskNickNameRegistration(Context context, String name) {
			super();
			this.name = name;
			this.ctx = context;
			//mProgressHUD = new ProgressHUD(getApplicationContext());
		}

		@Override
		protected void onPreExecute() {
			//mProgressHUD.show(true);
			super.onPreExecute();
		}

		@Override
		protected Response doInBackground(Void... params) {

			String uid = SPreferenceUtils.getStringPref(ctx,
					Constant.PREF_NAME, Constant.UID, "");

			Response response = ApiHelper.nickNameRegistration(uid, name);
			return response;
		}

		// @Override
		// protected void onProgressUpdate(String... values) {
		// mProgressHUD.setMessage(values[0]);
		// super.onProgressUpdate(values);
		// }

		@Override
		protected void onPostExecute(Response response) {
			super.onPostExecute(response);
//			if (mProgressHUD.isShowing()) {
//
//				mProgressHUD.dismiss();
//			}

			if (response != null) {
				int response_code = response.errorCode;
				String response_body = response.decription;

				try {
					JSONObject jObject;
					jObject = new JSONObject(response_body);

					if (response_code == 200) {
						jObject = new JSONObject(response_body);

						if (jObject != null) {
							if (jObject.has("message")) {
								Toast.makeText(ctx,
										jObject.getString("message"),
										Toast.LENGTH_SHORT).show();

								SPreferenceUtils.saveStringPref(getApplicationContext(),
										Constant.PREF_NAME,
										Constant.KB_NICKNAME, nickName
												.getText().toString().trim());
								
								txtNickName.setText(nickName
										.getText().toString().trim());

							}

						}
					} else {
						// Toast when error occured
						jObject = new JSONObject(response_body);

						if (jObject != null) {
							if (jObject.has("message")) {

								// show toast
								Toast.makeText(ctx,
										jObject.getString("message"),
										Toast.LENGTH_SHORT).show();

								String default_nickName = SPreferenceUtils
										.getStringPref(getApplicationContext(),
												Constant.PREF_NAME,
												Constant.KB_NICKNAME, "");

								nickName.setText(default_nickName);
								txtNickName.setText(default_nickName);

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



	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.content_kb_settings_nickname){
			
			Holder holder = new ViewHolder(
					R.layout.kb_mypage_nickname);
			
			final DialogPlus dialogPlus = DialogPlus
					.newDialog(KBSettingsActivity.this)
					.setContentHolder(holder)
					.setGravity(Gravity.CENTER)
					// .setAdapter(null)
					// .setOnItemClickListener(itemClickListener)
					.setOnDismissListener(
							new OnDismissListener() {
								@Override
								public void onDismiss(
										DialogPlus dialog) {
								}
							})
					.setOnBackPressListener(
							new OnBackPressListener() {
								@Override
								public void onBackPressed(
										DialogPlus dialog) {
									
									

								}
							})
					// .setOnCancelListener(cancelListener)
					// .setExpanded(expanded)
					.setCancelable(true).create();

			dialogPlus.show();
			
			String name = SPreferenceUtils.getStringPref(getApplicationContext(),
					Constant.PREF_NAME,
					Constant.KB_NICKNAME,
					"");
			nickName = (EditText) dialogPlus.findViewById(R.id.editText_nickname);
			nickName.setText(name);
			
			dialogPlus.findViewById(R.id.button_nickname_registration_cancle).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialogPlus.dismiss();
					
				}
			});
			
			dialogPlus.findViewById(R.id.button_nickname_registration).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					dialogPlus.dismiss();
					
					if (nickName.getText().toString().length() > 0) {

						hideSoftKeyboard();

						boolean isWhitespace;

						isWhitespace = false;
						isWhitespace = nickName.getText().toString().startsWith(" ");

						isWhitespace = nickName.getText().toString().endsWith(" ");
						if (isWhitespace) {

							Toast.makeText(getApplicationContext(),
									R.string.kb_search_whitespace, Toast.LENGTH_SHORT)
									.show();
						} else {
							String default_nickName = SPreferenceUtils.getStringPref(
									getApplicationContext(), Constant.PREF_NAME,
									Constant.KB_NICKNAME, "");

							if (!nickName.getText().toString()
									.equalsIgnoreCase(default_nickName)) {

								if (Util.isOnline(KBSettingsActivity.this)) {
									PBTaskNickNameRegistration task = new PBTaskNickNameRegistration(
											getApplicationContext(), nickName.getText()
													.toString());
									task.execute();
								} else {

									Toast toast = Toast
											.makeText(
													getApplicationContext(),
													getString(R.string.pb_network_not_available_general_message),
													1000);
									TextView v1 = (TextView) toast.getView()
											.findViewById(android.R.id.message);
									if (v1 != null)
										v1.setGravity(Gravity.CENTER);
									toast.show();
								}
							} else {

								Toast.makeText(getApplicationContext(),
										getString(R.string.kb_nickname_exist),
										Toast.LENGTH_SHORT).show();
							}

						}

					} else {

						Toast.makeText(getApplicationContext(),
								getString(R.string.kb_empty_nickname_warning),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}else if (v.getId() == R.id.content_kb_settings_openid){
			
			Intent intent = new Intent(KBSettingsActivity.this, KBOpenIdRegistrationActivity.class);
			startActivity(intent);
			
			
		}else if(v.getId() == R.id.content_kb_settings_back){
			
			finish();
			overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
		}

	}

}
