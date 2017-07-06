package com.aircast.koukaibukuro;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBAcornForestActivity;
import com.aircast.photobag.activity.SelectMultipleImageActivity;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.openid.KBOpenIdRegistrationActivity;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

// import com.ad_stir.AdstirTerminate;

/**
 * Upload main , click upload to select photo.
 * */
public class KBUploadMainFragment extends Fragment {
	private FButton mBtnDlInputUploadImg;
	protected Dialog dialog = null;
	private static String message_response_check_lock_status = "";
	private static String titte_after_start_migration = "";

	private static String resultCode = "";
	private boolean isDeviceLock = false;
	private boolean isFromMori = false;

	private View view;

	private static final String TAG = KBUploadMainFragment.class.getName();

	public static KBUploadMainFragment createInstance() {
		return new KBUploadMainFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.pb_layout_upload_main, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);


		super.onCreate(savedInstanceState);
		this.view = view;

		PBPreferenceUtils.saveBoolPref(getActivity().getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.UPLOAD_TAG, true);

		
		view.findViewById(R.id.layout_upload_ad).setVisibility(View.GONE);

		mBtnDlInputUploadImg = (FButton) view
				.findViewById(R.id.btn_send_pictures);
		Spannable buttonLabel = new SpannableString("  "
				+ getString(R.string.upload_main_btn_text));
		@SuppressWarnings("deprecation")
		Drawable drawable = getResources().getDrawable(R.drawable.btn_ico_up);
		int intrinsicHeightWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, (float) 20, getResources()
						.getDisplayMetrics()); // convert 20 dip to int
		drawable.setBounds(0, 0, intrinsicHeightWidth, intrinsicHeightWidth);
		ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
		buttonLabel
				.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mBtnDlInputUploadImg.setText(buttonLabel);

		mBtnDlInputUploadImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean sdCardMounted = PBGeneralUtils.checkSdcard(
						getActivity(), true,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialoginterface, int i) {
								// finish();
							}
						});
				if (sdCardMounted) {

					String deviceUUID = PBPreferenceUtils.getStringPref(
							PBApplication.getBaseApplicationContext(),
							PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, "0");

					System.out
							.println("Atik Easy Tracker is called upon on upload button press");
					EasyTracker easyTracker = EasyTracker
							.getInstance(getActivity());
					easyTracker.set(Fields.SCREEN_NAME, "" + deviceUUID
							+ ":KBUploadMainFragment");
					// MapBuilder.createEvent().build() returns a Map of event
					// fields and values
					// that are set and sent with the hit.
					easyTracker.send(MapBuilder.createEvent("ui_action", // Event
																			// category
																			// (required)
							"button_press_for_upload_pictures", // Event action
																// (required)
							"button_sent_pictures", // Event label
							null) // Event value
							.build());

					// show error dialog when no Internet available
					if (!PBApplication.hasNetworkConnection()) {
						AlertDialog.Builder networkErorrDialog = new AlertDialog.Builder(
								new ContextThemeWrapper(getActivity(),
										R.style.popup_theme));
						// exitDialog
						// .setTitle(getString(R.string.pb_chat_message_internet_offline_dialog_title));
						networkErorrDialog
								.setMessage(getString(R.string.pb_network_not_available_general_message));
						networkErorrDialog.setCancelable(false);
						networkErorrDialog.setPositiveButton(
								getString(R.string.dialog_ok_btn),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						networkErorrDialog.show();

					} else {
						PBTaskCheckDeviceLockStatus task = new PBTaskCheckDeviceLockStatus();
						task.execute(); // Need to set the URL from constants
					}
				}
			}
		});


		view.findViewById(R.id.webview_upload_screen_layout_for_kuma)
				.setVisibility(View.GONE);
		view.findViewById(R.id.view_kuma_no_internet_upload).setVisibility(
				View.VISIBLE);

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

		if (!hidden) {

			PBPreferenceUtils.saveBoolPref(getActivity()
					.getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.UPLOAD_TAG, true);


			view.findViewById(R.id.webview_upload_screen_layout_for_kuma)
					.setVisibility(View.GONE);
			view.findViewById(R.id.view_kuma_no_internet_upload).setVisibility(
					View.VISIBLE);

		}

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	public void onCrossImageClik(View view) {

		if (dialog != null) {
			dialog.dismiss();
		}

	}

	/**
	 * Async task class for checking whether device is locked or not use API
	 * "https://"+API_HOST+"/info_migration"
	 * 
	 * @author atikur
	 * 
	 */
	private class PBTaskCheckDeviceLockStatus extends
			AsyncTask<Void, Void, Void> {

		boolean result200Ok = false;
		boolean result400 = false;
		@Override
		protected Void doInBackground(Void... params) {

			String migrationCode = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_MIGRATON_CODE, ""); // Atik
																				// modified
																				// this
																				// line
																				// of
																				// code.
																				// It
																				// will
																				// not
																				// work
																				// when
																				// there
																				// is
																				// no
																				// migration
																				// code.

			if (TextUtils.isEmpty(migrationCode)) {
				System.out.println("Atik no migration code is set");
				return null;
			}

			Response res = PBAPIHelper
					.checkDeviceLockForDeviceChange(migrationCode);
			System.out.println("atik response:" + res.errorCode);
			Log.d("MIGRATION_CODE", "res: " + res.errorCode + " "
					+ res.decription);
			if (res != null) {
				if (res.errorCode == ResponseHandle.CODE_200_OK) {

					result200Ok = true;
					System.out.println("atik MIGRATION_CHECK_LOCK_STATUS"
							+ "200 OK:" + res.decription);

					try {
						JSONObject result = new JSONObject(res.decription);
						if (result.has("message")) {
							System.out.println("200 OK message is:"
									+ result.getString("message"));
							message_response_check_lock_status = result
									.getString("message");

						}
						if (result.has("result")) {
							result.getString("result");
							System.out.println("200 OK Result code is:"
									+ resultCode);
						}

						if (result.has("title")) {
							titte_after_start_migration = result
									.getString("title");
							System.out
									.println("200 OK title  is:" + resultCode);
						}

						System.out.println("Atik device lock status is 200 OK");
						isDeviceLock = true;
						// System.out.println("Atik device set lock status 200"+isDeviceLock);

					} catch (JSONException e) {
						System.out.println("MIGRATION_CODE"
								+ " Json parse exception occured");
					}

				} else if (res.errorCode == ResponseHandle.CODE_400) {
					result400 = true;
					System.out.println("Atik device lock status is 400");

					isDeviceLock = false;
					// System.out.println("Atik device set lock status false"+isDeviceLock);

				} else {

				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (result200Ok) {
				// PBApplication.makeToastMsg(message_response_check_lock_status);
				updateUISuccessfull(message_response_check_lock_status,
						titte_after_start_migration);
				isDeviceLock = true;
				System.out.println("Atik device set lock status 200"
						+ isDeviceLock);
				System.out
						.println("Atik inside else block of aysn task inside 200 ");
				// return;
			} else if (result400) {
				isDeviceLock = false;
				System.out.println("Atik device set lock status false"
						+ isDeviceLock);
				System.out
						.println("Atik inside else block of aysn task inside 400 ");
				updateUIWhenDeviceIsNotLocked();

			} else {
				System.out
						.println("Atik inside else block of aysn task other ");
				updateUIWhenDeviceIsNotLocked();
			}

		}
	}

	// Update UI when received merge code successfully
	private void updateUISuccessfull(final String message, final String title) {

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// Delay dialog display after 1s = 1000ms
				PBGeneralUtils.showAlertDialogActionWithOnClick(getActivity(),
						title, message, getString(R.string.dialog_ok_btn),
						mOnClickOkDialogMigrationVerified);

			}
		}, 1000);

	}

	// Update UI when received response code 400
	private void updateUIWhenDeviceIsNotLocked() {

		if (isFromMori) {
			isFromMori = false;
			Intent intentReward = new Intent(getActivity(),
					PBAcornForestActivity.class);
			startActivity(intentReward);
		} else {
			
			String openId = PBPreferenceUtils.getStringPref(getActivity().getApplicationContext(), PBConstant.PREF_NAME, PBConstant.PREF_OPENID_SHAREID,"" );
			if(openId.equals("")){
				
				Intent intent = new Intent();
				intent.setClass(getActivity(), KBOpenIdRegistrationActivity.class);
				startActivity(intent);
			}else{
				
				Intent intent = new Intent();
				intent.setClass(getActivity(), SelectMultipleImageActivity.class);
				startActivity(intent);
			}
			
			

		}

	}

	private DialogInterface.OnClickListener mOnClickOkDialogMigrationVerified = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {

			// atik Call start data migration task
			dialog.dismiss();

		}
	};


	// Added below activity life cycle method for Google analytics
	@Override
	public void onStart() {

		super.onStart();
		System.out.println("Atik start Easy Tracker");
		EasyTracker.getInstance(getActivity()).activityStart(getActivity());
	}

	// Added below activity life cycle method for Google analytics

	@Override
	public void onStop() {
		super.onStop();
		System.out.println("Atik stop Easy Tracker");
		EasyTracker.getInstance(getActivity()).activityStop(getActivity());
	}

}
