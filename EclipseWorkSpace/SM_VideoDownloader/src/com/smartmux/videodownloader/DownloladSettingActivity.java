package com.smartmux.videodownloader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.smartmux.videodownloader.lockscreen.utils.AppExtra;
import com.smartmux.videodownloader.utils.SMConstant;
import com.smartmux.videodownloader.utils.SMSharePref;

public class DownloladSettingActivity extends FragmentActivity {

	LinearLayout layout2, layout_back;
	ToggleButton download_3G, clean_download, thumb, capitalize = null;

	Spinner mSpinner = null;
	String[] spinnerValues = { "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"10" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.activity_setting_download);

		layout2 = (LinearLayout) findViewById(R.id.layout_second_page);
		layout2.setVisibility(View.VISIBLE);
		download_3G = (ToggleButton) findViewById(R.id.toggleButton_3d);

		String threeG = SMSharePref
				.get3DDownload(DownloladSettingActivity.this);

		if (threeG.equals(SMConstant.on)) {
			download_3G.setChecked(true);
		} else {
			download_3G.setChecked(false);
		}

		download_3G.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub

				if (isChecked) {

					SMSharePref.save3DDownload(DownloladSettingActivity.this,
							SMConstant.on);

				} else {
					SMSharePref.save3DDownload(DownloladSettingActivity.this,
							SMConstant.off);
				}

			}
		});

		clean_download = (ToggleButton) findViewById(R.id.toggleButton_clean_dwnl);

		String clear = SMSharePref
				.getClearDownload(DownloladSettingActivity.this);

		if (clear.equals(SMConstant.on)) {
			clean_download.setChecked(true);
		} else {
			clean_download.setChecked(false);
		}

		clean_download
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub

						if (isChecked) {

							SMSharePref.saveClearDownload(
									DownloladSettingActivity.this,
									SMConstant.on);

						} else {
							SMSharePref.saveClearDownload(
									DownloladSettingActivity.this,
									SMConstant.off);
						}

					}
				});

		thumb = (ToggleButton) findViewById(R.id.toggleButton_thumb);

		String thumbnails = SMSharePref
				.getThumbnails(DownloladSettingActivity.this);

		if (thumbnails.equals(SMConstant.on)) {
			thumb.setChecked(true);
		} else {
			thumb.setChecked(false);
		}

		thumb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {

					SMSharePref.saveThumbnails(DownloladSettingActivity.this,
							SMConstant.on);

				} else {
					SMSharePref.saveThumbnails(DownloladSettingActivity.this,
							SMConstant.off);
				}

			}
		});

		capitalize = (ToggleButton) findViewById(R.id.toggleButton_capital);

		String capital = SMSharePref
				.getCapitalize(DownloladSettingActivity.this);

		if (capital.equals(SMConstant.on)) {
			capitalize.setChecked(true);
		} else {
			capitalize.setChecked(false);
		}

		capitalize.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub

				if (isChecked) {

					SMSharePref.saveCapitalize(DownloladSettingActivity.this,
							SMConstant.on);

				} else {
					SMSharePref.saveCapitalize(DownloladSettingActivity.this,
							SMConstant.off);
				}

			}
		});

		layout_back = (LinearLayout) findViewById(R.id.layout_setting_back);

		layout_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent eventsIntent = new Intent(DownloladSettingActivity.this,
//						MainActivity.class);
//
//				eventsIntent.putExtra("settab", "Settings");
//				startActivity(eventsIntent);
				SMSharePref.setBackCode(getApplicationContext());
				finish();
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}
		});

		mSpinner = (Spinner) findViewById(R.id.sim_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.custom_spinner, spinnerValues);
		mSpinner.setAdapter(adapter);
		
		mSpinner.setSelection(SMSharePref.getSpinnerPosition(DownloladSettingActivity.this));
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				
				SMSharePref.saveSpinnerPosition(DownloladSettingActivity.this,
						position);
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		// String item = mSpinner.getSelectedItem().toString();
		// Toast.makeText(DownloladSettingActivity.this, ""+ item, 1000).show();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		SMSharePref.setBackCode(getApplicationContext());
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		String security = SMSharePref.getSecurity(DownloladSettingActivity.this);
		int event_code = SMSharePref.getReturnCode(getApplicationContext());
		if (security.equals(SMConstant.on) && event_code == AppExtra.HOME_CODE) {
//			Toast.makeText(getApplicationContext(),
//					"event_code" + event_code, 1000).show();
			
		Intent i = new Intent(DownloladSettingActivity.this, AppLockActivity.class);
		i.putExtra("passcode", "password_match");
        startActivity(i);
        overridePendingTransition(R.anim.bottom_up, 0);
	}
		
	}

	 @Override
		protected void onUserLeaveHint() {
		 SMSharePref.setHomeCode(getApplicationContext());
			super.onUserLeaveHint();
	}

	
}
