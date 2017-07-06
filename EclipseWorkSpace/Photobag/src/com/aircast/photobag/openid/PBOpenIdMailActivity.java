package com.aircast.photobag.openid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.koukaibukuro.util.ApiHelper;
import com.aircast.koukaibukuro.util.ApiHelper.Response;
import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBAbsActionBarActivity;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.utils.Validation;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;

public class PBOpenIdMailActivity extends PBAbsActionBarActivity implements
		OnClickListener {
	private final String TAG = "PBOpenIdMailActivity";
	private EditText editMailId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_mail);
		// @lent5 add action bar #S

		// @lent add progress bar
		mLvWaiting = (LinearLayout) findViewById(R.id.ll_loading_panel_waiting);

		editMailId = (EditText) findViewById(R.id.input_openid_mail);

		findViewById(R.id.button_openid_mail_registration).setOnClickListener(
				this);
		
		findViewById(R.id.content_openid_mail_back).setOnClickListener(
				this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		mLvWaiting.setVisibility(View.GONE);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	// @lent add progress bar
	private LinearLayout mLvWaiting;

	private boolean checkEmailValidation() {
		boolean ret = true;
		if (!Validation.isEmailAddress(editMailId, true))
			ret = false;
		if (!Validation.hasText(editMailId))
			ret = false;

		return ret;
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.button_openid_mail_registration) {

			if (!checkEmailValidation()) {
				
				Toast.makeText(getApplicationContext(), "正しいメール形式を入力してください。", Toast.LENGTH_SHORT).show();
				return;
			}
			
			Holder holder = new ViewHolder(
					R.layout.kb_mypage_nickname);
			
			final DialogPlus dialogPlus = DialogPlus
					.newDialog(PBOpenIdMailActivity.this)
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
			
			TextView txtTitle = (TextView) dialogPlus.findViewById(R.id.textView_nickname_title);
			txtTitle.setText("仮登録済み"
+"このアドレスは既に仮登録されています。"
+"登録完了メールを確認してください。");
			
			dialogPlus.findViewById(R.id.editText_nickname).setVisibility(View.GONE);
			dialogPlus.findViewById(R.id.button_nickname_registration_cancle).setVisibility(View.GONE);
			
			dialogPlus.findViewById(R.id.button_nickname_registration).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					dialogPlus.dismiss();
					
					new OpenIdMailRegistration().execute();
				}
			});

			
		}else if(v.getId() == R.id.content_openid_mail_back){
			
			finish();
		}

	}

	@Override
	protected void handleHomeActionListener() {
		finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		// Do nothing here
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private class OpenIdMailRegistration extends
			AsyncTask<String, Void, Response> {

		public OpenIdMailRegistration() {
			super();

			mLvWaiting.setVisibility(View.VISIBLE);
		}

		@Override
		protected Response doInBackground(String... params) {

			String deviceUUID = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, "0");

			Response response = ApiHelper.openIDRegistration(deviceUUID,
					editMailId.getText().toString(), "email");

			return response;
		}

		@Override
		protected void onPostExecute(Response response) {
			// go to uploading screen
			super.onPostExecute(response);
			mLvWaiting.setVisibility(View.GONE);

			if (response != null) {
				int response_code = response.errorCode;
				String response_body = response.decription;

				if (PBAPIContant.DEBUG) {

					System.out.println(response_body);
				}

				if (response_code == 200) {

					PBPreferenceUtils.saveStringPref(getApplicationContext(),
							PBConstant.PREF_NAME,
							PBConstant.PREF_OPENID_SHARE_TYPE, "email");
					PBPreferenceUtils.saveStringPref(getApplicationContext(),
							PBConstant.PREF_NAME,
							PBConstant.PREF_OPENID_SHAREID, editMailId
									.getText().toString());

					Intent intent = new Intent();
					intent.setClass(PBOpenIdMailActivity.this,
							PBOpenIDMailRegistrationResultActivity.class);
					startActivity(intent);
					finish();

				}

			}

		}
	}

}
