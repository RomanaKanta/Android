package com.smartmux.videodownloader.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.smartmux.videodownloader.AppLockActivity;
import com.smartmux.videodownloader.DownloladSettingActivity;
import com.smartmux.videodownloader.R;
import com.smartmux.videodownloader.utils.SMConstant;
import com.smartmux.videodownloader.utils.SMSharePref;
import com.smartmux.videodownloader.utils.SMToast;

public class SettingFragment extends Fragment implements OnClickListener {

	LinearLayout layout1, layout2, layout_back;
	ToggleButton settings_Bg_video, settings_security = null;
	RelativeLayout set_start_page, set_dwn_file, set_security = null;

	TextView start_url, setting_edit = null;

	Animation viewOpen, viewClose;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_setting, container,
				false);

		viewOpen = AnimationUtils.loadAnimation(getActivity(),
				R.anim.push_left_in);

		viewClose = AnimationUtils.loadAnimation(getActivity(),
				R.anim.push_left_out);

		layout1 = (LinearLayout) rootView.findViewById(R.id.layout_first_page);

		start_url = (TextView) rootView
				.findViewById(R.id.textView_setting_browser_url);
		start_url.setText(SMSharePref.getUrl(getActivity()));

		setting_edit = (TextView) rootView
				.findViewById(R.id.textView_edit_setting);

		settings_Bg_video = (ToggleButton) rootView
				.findViewById(R.id.toggleButton_settings_bg);

		String bg_vdo = SMSharePref.getBackgroundPlay(getActivity());

		if (bg_vdo.equals(SMConstant.on)) {
			settings_Bg_video.setChecked(true);
		} else {
			settings_Bg_video.setChecked(false);
		}

		settings_Bg_video
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub

						if (isChecked) {

							SMSharePref.saveBackgroundPlay(getActivity(),
									SMConstant.on);

						} else {
							SMSharePref.saveBackgroundPlay(getActivity(),
									SMConstant.off);
						}

					}
				});

		settings_security = (ToggleButton) rootView
				.findViewById(R.id.toggleButton_settings_security);

		String security = SMSharePref.getSecurity(getActivity());

		if (security.equals(SMConstant.on)) {
			settings_security.setChecked(true);
		} else {
			settings_security.setChecked(false);
		}

		settings_security
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub

						if (isChecked) {

//							SMSharePref.saveSecurity(getActivity(),
//									SMConstant.on);
							
							Intent i = new Intent(getActivity(), AppLockActivity.class);
							i.putExtra("passcode", "password_on");
					        startActivity(i);
					        getActivity().overridePendingTransition(R.anim.bottom_up, 0);

						} else {
//							SMSharePref.saveSecurity(getActivity(),
//									SMConstant.off);
							
							Intent i = new Intent(getActivity(), AppLockActivity.class);
							i.putExtra("passcode", "password_off");
					        startActivity(i);
					        getActivity().overridePendingTransition(R.anim.bottom_up, 0);
						}

					}
				});

		set_start_page = (RelativeLayout) rootView
				.findViewById(R.id.layout_setting_browser);
		set_dwn_file = (RelativeLayout) rootView
				.findViewById(R.id.layout_setting_download);
		set_security =(RelativeLayout)rootView.findViewById(R.id.layout_setting_security);
		
		
		set_start_page.setOnClickListener(this);
		set_dwn_file.setOnClickListener(this);
		set_security.setOnClickListener(this);
		setting_edit.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return super.getView();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.layout_setting_browser:

			showInputDialog("Set HomePage", false);

			break;

		case R.id.layout_setting_download:

			Intent eventsIntent = new Intent(getActivity(),
					DownloladSettingActivity.class);
			startActivity(eventsIntent);
			SMSharePref.setBackCode(getActivity());
			getActivity().overridePendingTransition(R.anim.push_left_in, 0);

			break;
			
		case R.id.layout_setting_security:

				Intent i = new Intent(getActivity(), AppLockActivity.class);
				i.putExtra("passcode", "change");
		        startActivity(i);
		        getActivity().overridePendingTransition(R.anim.slide_down, R.anim.nothing);

			break;

		case R.id.textView_edit_setting:

			if (setting_edit.getText().equals("Edit")) {

				setting_edit.setText(R.string.cancel);

			} else {
				setting_edit.setText(R.string.edit);
			}

			break;

		default:
			break;
		}
	}

	protected void showInputDialog(String pTitle, boolean flag) {

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
		alertDialogBuilder.setView(promptView);

		final TextView title = (TextView) promptView
				.findViewById(R.id.textView_playlist_title);

		title.setText(pTitle);

		if (flag) {
			final TextView subtitle = (TextView) promptView
					.findViewById(R.id.textView_playlist_sub_title);

			subtitle.setVisibility(View.VISIBLE);
		}

		final EditText editText = (EditText) promptView
				.findViewById(R.id.edittext_playlist_title);
		// setup a dialog window
		alertDialogBuilder.setCancelable(false);
		// Add the buttons
		alertDialogBuilder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});

		alertDialogBuilder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int id) {

						String url = editText.getText().toString();

						if (Patterns.WEB_URL.matcher(url).matches()) {

							SMSharePref.saveUrl(getActivity(), url);

							start_url.setText(url);
						} else {
							SMToast.showToast(getActivity(),
									getString(R.string.invalid_url));
							dialog.dismiss();
						}
					}
				});

		// create an alert dialog
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

}
