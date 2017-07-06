package com.aircast.photobag.openid;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBAbsActionBarActivity;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.actionbar.ActionBar;

public class PBOpenIDMailRegistrationResultActivity extends
		PBAbsActionBarActivity implements OnClickListener {
	private final String TAG = "PBOpenIDMailRegistrationResultActivity";
	private EditText editMailId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mail_reg_result);
		// @lent5 add action bar #S

		// @lent add progress bar

		editMailId = (EditText) findViewById(R.id.input_openid_mail);
		
		String mailId = PBPreferenceUtils.getStringPref(getApplicationContext(), PBConstant.PREF_NAME, PBConstant.PREF_OPENID_SHAREID,"");
		editMailId.setText(mailId);

		findViewById(R.id.button_openid_mail_registration).setOnClickListener(
				this);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	// @lent add progress bar


	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.button_openid_mail_registration) {

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

}
