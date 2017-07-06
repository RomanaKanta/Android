package com.aircast.koukaibukuro.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBAcornForestActivity;
import com.aircast.photobag.activity.PBDisplayCompleteActivity;
import com.aircast.photobag.activity.PBDownloadConfirmActivity;
import com.aircast.photobag.activity.PBDownloadPurchaseActivity;
import com.aircast.photobag.activity.PBDownloadWrongPassActivity;
import com.aircast.photobag.activity.PBMainTabBarActivity;
import com.aircast.photobag.activity.PBPasswordThumbsPreviewActivity;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.fragment.PBDialogFragment;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.PBCustomWaitingProgress;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;

public class KBDownLoadManager {

	private FragmentActivity activity;
	private String password;

	private static String message_response_check_lock_status = "";
	private static String titte_after_start_migration = "";

	private final static int MSG_CHECK_PWD_DONE = 1000;
	private final static int MSG_SHOW_PROGRESS_BAR = 1001;
	private final static int MSG_SHOW_NOT_FOUND_OR_PURCHASE_SCREEN = 1002;
	private final static int MSG_SHOW_UNFINISH = 1004;
	private final static int MSG_SHOW_EXCEEDED_SCREEN = 1003;

//	private final static int DIALOG_NO_SDCARD = 2000;
//	private final static int DIALOG_CANNOT_FIND_WITH_PASS = 2001;
//	private final static int DIALOG_UNFINISH = 2002;
//	private final static int DIALOG_IN_REVIEW = 2003;
//	private final static int DIALOG_NEED_UPDATE = 2012;
//	private final static int DIALOG_AIKOTOBA_NOUPDATE = 2013;

	private boolean isDeviceLock = false;
	private boolean isFromMori = false;

	private static String resultCode = "";
	private ResponseHandle.Response mResponeResult;

	private PBCustomWaitingProgress mCustomWaitingLayout;
	public static String passwordFromUrl = "";

	private int mNumberOfRetryCount = 1;
	private EditText mEdtPwdSecretDigit;
	private boolean isOpenFourDigit = false;
	public KBDownLoadManager(FragmentActivity activity, String password,int tabPosition) {
		super();
		this.activity = activity;
		this.password = password;
		//this.tabPosition =  tabPosition;
		if (!PBGeneralUtils.checkSdcard(PBMainTabBarActivity.sMainContext, true,
				null)) {
			return;
		}

		if (!PBApplication.hasNetworkConnection()) {
			PBApplication.makeToastMsg(activity
					.getString(R.string.pb_network_error_content));
			return;
		}

		if (TextUtils.isEmpty(password)) {
			return;
		}

		mCustomWaitingLayout = new  PBCustomWaitingProgress(activity);
		mCustomWaitingLayout.showWaitingLayout();
		PBPreferenceUtils.saveIntPref(activity.getApplicationContext(), PBConstant.PREF_NAME, 
    			PBConstant.TAB_POSITION, tabPosition);
		
		System.out.println("Atik before check device lock status");
		PBTaskCheckDeviceLockStatus task = new PBTaskCheckDeviceLockStatus();
		task.execute(); // Need to set the URL from constants
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

			System.out
					.println("Atik called for device lock status check doInBackground");

			String migrationCode = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_MIGRATON_CODE_SUCCESS, "");

			if (TextUtils.isEmpty(migrationCode)) {
				System.out.println("Atik no migration code is set");
				return null;
			}
			System.out.println("Atik  migration code is :" + migrationCode);
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

					} catch (JSONException e) {
						System.out.println("MIGRATION_CODE"
								+ " Json parse exception occured");
					}

				} else if (res.errorCode == ResponseHandle.CODE_400) {
					result400 = true;
					System.out.println("Atik device lock status is 400");

					isDeviceLock = false;

				} else {

				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (result200Ok) {
				updateUISuccessfull(message_response_check_lock_status,
						titte_after_start_migration);
				isDeviceLock = true;
				System.out.println("Atik device set lock status 200"
						+ isDeviceLock);
				System.out
						.println("Atik inside else block of aysn task inside 200 ");

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
				PBGeneralUtils.showAlertDialogActionWithOnClick(activity,
						title, message,
						activity.getString(R.string.dialog_ok_btn),
						mOnClickOkDialogMigrationVerified);

			}
		}, 1000);

	}

	private DialogInterface.OnClickListener mOnClickOkDialogMigrationVerified = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};

	private void showWrongPassPage() {
		mNumberOfRetryCount++;
		
		Intent intent = new Intent(PBMainTabBarActivity.sMainContext,
				PBDownloadWrongPassActivity.class);
		intent.putExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY,
				true); // Added parameter from going to koukaibukuro library
		activity.startActivity(intent);
	}

	// Update UI when received response code 400
	private void updateUIWhenDeviceIsNotLocked() {

		if (isFromMori) {
			isFromMori = false;
			Intent intentReward = new Intent(activity,
					PBAcornForestActivity.class);
			activity.startActivity(intentReward);
		} else {
			
			new PBTaskCheckPhotoListInCollection().execute();
			}
	}

	/**
	 * 
	 * @param passwordToCheck
	 * @return return <b>true</b> if user reach 10 times of input password or
	 *         error.
	 */
	private boolean checkPasswordInputExceeded(String passwordToCheck) {
		if (TextUtils.isEmpty(passwordToCheck)) {
			return true;
		}

		// check to reset exceeeded status if over 5 mins!
		if (PBPreferenceUtils.getBoolPref(activity.getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_IS_IN_EXCEEDED_MODE,
				false)) {
			long startCountdownTime = PBPreferenceUtils.getLongPref(
					activity.getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_DL_INPUT_PASS_RETRY_START_TIME, 0);
			long currenttime = System.currentTimeMillis();
			currenttime = currenttime - startCountdownTime; // currenttime + (5
															// * 60 * 1000);
			long maxCountDownTime = PBConstant.TIME_COUNT_DOWN_EXCEEDED_SCREEN * 60 * 1000;
			if (currenttime > maxCountDownTime) {
				// reset retry count
				mNumberOfRetryCount = 1;
				resetCountDownTimer();
				return false;
			}
			return true;
		}

		// prepare to show exceeded screen
		if (mNumberOfRetryCount > PBConstant.MAX_INPUT_PASSWORD_CHECK_COUNT - 1) {
			long startTimeCount = System.currentTimeMillis(); /*
															 * + (PBConstant.
															 * TIME_COUNT_DOWN_EXCEEDED_SCREEN
															 * * 60 * 1000)
															 */
			PBPreferenceUtils.saveLongPref(activity.getApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_DL_INPUT_PASS_RETRY_START_TIME,
					startTimeCount);
			PBPreferenceUtils.saveBoolPref(activity.getApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_IS_IN_EXCEEDED_MODE,
					true);
			// reset retry count
			mNumberOfRetryCount = 1;

			mHandler.sendEmptyMessage(MSG_SHOW_NOT_FOUND_OR_PURCHASE_SCREEN);
			return true;
		}
		return false;
	}

	/**
	 * method for reset count down timer in case user input one password over 10
	 * times.
	 */
	private void resetCountDownTimer() {
		// reset PREF_IS_IN_EXCEEDED_MODE to false
		PBPreferenceUtils.saveBoolPref(activity.getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_IS_IN_EXCEEDED_MODE,
				false);
		// reset start check time
		PBPreferenceUtils.saveLongPref(activity.getApplicationContext(),
				PBConstant.PREF_NAME,
				PBConstant.PREF_DL_INPUT_PASS_RETRY_START_TIME, 0);
		// reset prev pass
		PBPreferenceUtils.saveStringPref(activity.getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_DL_INPUT_PASS_PREV_PASS,
				"");
	}

	private final Handler mHandler = new Handler() {
		private DialogPlus dialogPlus;

		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(android.os.Message msg) {
			
			
			switch (msg.what) {
			// @lent add
			case MSG_SHOW_UNFINISH:
				// reset exceeded count
				mNumberOfRetryCount = 0;
				//activity.showDialog(DIALOG_UNFINISH);
				showDialog(R.string.dl_alert_upload_unfinished_title,R.string.dl_alert_upload_unfinished_msg);
				break;

			case MSG_SHOW_NOT_FOUND_OR_PURCHASE_SCREEN:
				// reset exceeded count
				mNumberOfRetryCount = 0;
				showWrongPassPage();
				break;

			case MSG_SHOW_EXCEEDED_SCREEN:
				// reset exceeded count
				mNumberOfRetryCount = 0;
				Intent mShowExceededIntent = new Intent(
						PBMainTabBarActivity.sMainContext,
						PBDisplayCompleteActivity.class);
				mShowExceededIntent.putExtra(PBConstant.START_EXCEEDED, true);
				activity.startActivity(mShowExceededIntent);
				break;

			case MSG_CHECK_PWD_DONE:

				

				Log.d("MSG_CHECK_PWD_DONE", "MSG_CHECK_PWD_DONE");
				if (mResponeResult != null) {
					String errorDesc = mResponeResult.decription;
					Intent mIntent = null;

					switch (mResponeResult.errorCode) {
					case ResponseHandle.CODE_200_OK:
						if(dialogPlus != null){
							dialogPlus.dismiss();
							
							PBPreferenceUtils.saveStringPref(activity.getApplicationContext(),
					                 PBConstant.UPLOAD_SERVICE_PREF,
					                 PBConstant.PREF_FOUR_DIGIT, mEdtPwdSecretDigit.getText().toString());
						}
						
						PBPreferenceUtils.saveBoolPref(
								activity.getApplicationContext(),
								PBConstant.PREF_NAME,
								PBConstant.PREF_PASSWORD_FROM_LIBRARY, true);
						mIntent = new Intent(PBMainTabBarActivity.sMainContext,
								PBPasswordThumbsPreviewActivity.class);
						activity.startActivity(mIntent);

						break;

					case ResponseHandle.CODE_400:
						if (errorDesc.contains("unfinished")) { // case
																// unfinished
																// upload
						//	activity.showDialog(DIALOG_UNFINISH);
							showDialog(R.string.dl_alert_upload_unfinished_title,R.string.dl_alert_upload_unfinished_msg);
						} else if (errorDesc.contains("password")) { // case ot
																		// found
																		// with
																		// password
							// mShowPurchaseScreen = false;
							showWrongPassPage();
						} else if (errorDesc.contains("expired")) { // case
																	// expires
							showWrongPassPage();
						} else if (errorDesc.contains("use honey")
								|| errorDesc.contains("payment")) {
							PBPreferenceUtils
									.saveBoolPref(
											activity.getApplicationContext(),
											PBConstant.PREF_NAME,
											PBConstant.PREF_PASSWORD_FROM_LIBRARY,
											true);
							if (needConfirm()) {

								mIntent = new Intent(
										PBMainTabBarActivity.sMainContext,
										PBDownloadConfirmActivity.class);
								mIntent.putExtra("NEED_PURCHASE", true);
								activity.startActivity(mIntent);
							} else {
								mIntent = new Intent(
										PBMainTabBarActivity.sMainContext,
										PBDownloadPurchaseActivity.class);
								mIntent.putExtra(
										PBConstant.START_PURCHASE_NOTICE, true);
								activity.startActivity(mIntent);
							}
						} else if (errorDesc
								.contains("client confirmation needed")) {
							PBPreferenceUtils
							.saveBoolPref(
									activity.getApplicationContext(),
									PBConstant.PREF_NAME,
									PBConstant.PREF_PASSWORD_FROM_LIBRARY,
									true);
							mIntent = new Intent(PBMainTabBarActivity.sMainContext,
									PBDownloadConfirmActivity.class);
							activity.startActivity(mIntent);
						} else {
							// mShowPurchaseScreen = false;
							showWrongPassPage();
						}

						break;
					case ResponseHandle.CODE_403:
						
						if(mResponeResult.decription == null){
							
							showDialog(R.string.pb_password_banned_title,R.string.pb_password_banned_context);
							return;
						}
						
						
						if (mResponeResult.decription.equals("not enough")) {
							System.out
							.println(mResponeResult.decription);
							Toast.makeText(activity.getApplicationContext(),
									mResponeResult.decription, Toast.LENGTH_SHORT)
									.show();
							isOpenFourDigit = true;
							Holder holder = new ViewHolder(
									R.layout.dl_four_digit_screen);
							// mEdtPwdSecretDigit.setVisibility(View.VISIBLE);
							
							
							 dialogPlus = DialogPlus
									.newDialog(activity)
									.setContentHolder(holder)
									.setGravity(Gravity.TOP)
									// .setAdapter(null)
									// .setOnItemClickListener(itemClickListener)
									.setOnDismissListener(
											new OnDismissListener() {
												@Override
												public void onDismiss(
														DialogPlus dialog) {
													isOpenFourDigit = false;
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

							mEdtPwdSecretDigit = (EditText) dialogPlus
									.findViewById(R.id.edt_dl_pwd_secret_digit);
							TextView mBtnDlInputReceiveImg = (TextView) dialogPlus
									.findViewById(R.id.btn_dl_four_digitinput_get_img);
							Spannable buttonLabel = new SpannableString("  "
									+ activity.getString(R.string.dl_btn_input_pwd_text));
							Drawable drawable = activity.getResources().getDrawable(
									R.drawable.btn_ico_dl);
							int intrinsicHeightWidth = (int) TypedValue
									.applyDimension(
											TypedValue.COMPLEX_UNIT_DIP,
											(float) 20, activity.getResources()
													.getDisplayMetrics()); // convert
																			// 20
																			// dip
																			// to
																			// int

							drawable.setBounds(0, 0, intrinsicHeightWidth,
									intrinsicHeightWidth);
							ImageSpan imageSpan = new ImageSpan(drawable,
									ImageSpan.ALIGN_BASELINE);

							AlignmentSpan.Standard center_span = new AlignmentSpan.Standard(
									Layout.Alignment.ALIGN_CENTER);
							buttonLabel.setSpan(center_span, 0, 1,
									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							buttonLabel.setSpan(imageSpan, 0, 1,
									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							mBtnDlInputReceiveImg.setText(buttonLabel);
							mBtnDlInputReceiveImg
									.setOnClickListener(mOnClickListener);
						} 
						break;
					case ResponseHandle.CODE_406:
						if (errorDesc.contains("最新のバージョン")) {
							//activity.showDialog(DIALOG_NEED_UPDATE);
							showDialog(R.string.dl_alert_upload_unfinished_title,R.string.dl_alert_upload_unfinished_msg);
						}
						break;
					case ResponseHandle.CODE_404:
					case ResponseHandle.CODE_INVALID_PARAMS:
						showWrongPassPage();
						break;
					default:
						break;
					}
				} else {
					Log.e("TAG", ">>> response from server is NULL!");
				}
				break;

			case MSG_SHOW_PROGRESS_BAR:

				if (mCustomWaitingLayout == null) {
					
				
					mCustomWaitingLayout.showWaitingLayout();
				}

				break;
			default:
				break;

			}
			
			if (mCustomWaitingLayout != null) {
				mCustomWaitingLayout.hideWaitingLayout();
			}
		};
	};

	private boolean needConfirm() {
		String downloadInfo = PBPreferenceUtils.getStringPref(activity,
				PBConstant.PREF_NAME, PBConstant.PREF_PURCHASE_INFO_JSON_DATA,
				null);
		if (TextUtils.isEmpty(downloadInfo)) {
			return false;
		}
		try {
			JSONObject jInfo = new JSONObject(downloadInfo);
			if (jInfo.getInt("client_keep_days") > 0) {
				return true;
			}
			if (jInfo.getInt("can_save") == 0) {
				return true;
			}

		} catch (Exception e) {
		}

		return false;
	}
	
	void showDialog(int title,int mgs) {
		PBDialogFragment newFragment = PBDialogFragment.newInstance(
				title,mgs);
	    newFragment.show(activity.getSupportFragmentManager(), "dialog");
	}

//	protected Dialog onCreateDialog(int id) {
//		switch (id) {
//
//		case DIALOG_NO_SDCARD:
//			return new AlertDialog.Builder(PBMainTabBarActivity.sMainContext)
//					.setIcon(android.R.drawable.ic_dialog_alert)
//					.setTitle(R.string.dl_alert_sdcard_missing_title)
//					.setMessage(
//							activity.getString(R.string.dl_alert_sdcard_missing_msg))
//					.setPositiveButton(R.string.dialog_ok_btn,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int which) {
//									return;
//								}
//							}).create();
//
//		case DIALOG_CANNOT_FIND_WITH_PASS:
//			return new AlertDialog.Builder(PBMainTabBarActivity.sMainContext)
//					.setIcon(android.R.drawable.ic_dialog_alert)
//					.setTitle(R.string.dl_alert_cannot_find_album_title)
//					.setMessage(
//							activity.getString(R.string.dl_alert_cannot_find_album_msg))
//					.setPositiveButton(R.string.dialog_ok_btn,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int which) {
//
//									mNumberOfRetryCount++;
//									Log.d("TAG", ">>> retry count="
//											+ mNumberOfRetryCount);
//									dialog.dismiss();
//									return;
//								}
//							}).create();
//		case DIALOG_UNFINISH:
//			return new AlertDialog.Builder(PBMainTabBarActivity.sMainContext)
//					.setIcon(android.R.drawable.ic_dialog_alert)
//					.setTitle(R.string.dl_alert_upload_unfinished_title)
//					.setMessage(
//							activity.getString(R.string.dl_alert_upload_unfinished_msg))
//					.setPositiveButton(R.string.dialog_ok_btn,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int which) {
//									dialog.dismiss();
//								}
//							}).create();
//		case DIALOG_IN_REVIEW:
//			return new AlertDialog.Builder(PBMainTabBarActivity.sMainContext)
//					.setIcon(android.R.drawable.ic_dialog_alert)
//					.setTitle(R.string.pb_password_banned_title)
//					.setMessage(
//							activity.getString(R.string.pb_password_banned_context))
//					.setPositiveButton(R.string.dialog_ok_btn,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int which) {
//									dialog.dismiss();
//								}
//							}).create();
//		case DIALOG_NEED_UPDATE:
//			return new AlertDialog.Builder(PBMainTabBarActivity.sMainContext)
//					.setIcon(android.R.drawable.ic_dialog_alert)
//					.setTitle(R.string.pb_download_need_update_title)
//					.setMessage(
//							activity.getString(R.string.pb_download_need_update_content))
//					.setPositiveButton(R.string.dialog_ok_btn,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int which) {
//									dialog.dismiss();
//								}
//							}).create();
//
//		case DIALOG_AIKOTOBA_NOUPDATE:
//			return new AlertDialog.Builder(PBMainTabBarActivity.sMainContext)
//					.setIcon(android.R.drawable.ic_dialog_alert)
//					.setTitle(R.string.pb_download_need_update_title)
//					.setMessage(
//							activity.getString(R.string.pb_download_aikotoba_already_uptodate))
//					.setPositiveButton(R.string.dialog_ok_btn,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int which) {
//									dialog.dismiss();
//								}
//							}).create();
//
//		default:
//			break;
//		}
//
//		return null;
//	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.btn_dl_four_digitinput_get_img:
				downloadButtonAction();


			default:
				break;
			}
		}
	};

	private void downloadButtonAction() {

		String deviceUUID = PBPreferenceUtils.getStringPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, "0");

		System.out
				.println("Atik Easy Tracker is called upon on download button press");
		EasyTracker easyTracker = EasyTracker.getInstance(activity);
		easyTracker.set(Fields.SCREEN_NAME, "" + deviceUUID
				+ ":PBDownloadMainActivity");
		// MapBuilder.createEvent().build() returns a Map of event fields and
		// values
		// that are set and sent with the hit.
		easyTracker.send(MapBuilder.createEvent("ui_action", // Event category
																// (required)
				"button_press_for_download", // Event action (required)
				password, // Event label
				null) // Event value
				.build());

		// 20120221 check and show AlertDialog if missing sdcard <S>
		if (!PBGeneralUtils.checkSdcard(PBMainTabBarActivity.sMainContext,
				true, null)) {
			return;
		}

		// 20120221 check network coonection <S>
		if (!PBApplication.hasNetworkConnection()) {
			// PBApplication.makeToastMsg(getString(R.string.pb_network_error_content));
			// Change it to dialog
			AlertDialog.Builder exitDialog = new AlertDialog.Builder(
					new ContextThemeWrapper(activity, R.style.popup_theme));
			exitDialog
					.setMessage(activity.getString(R.string.pb_network_not_available_general_message));
			exitDialog.setCancelable(false);
			exitDialog.setPositiveButton(activity.getString(R.string.dialog_ok_btn),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();
						}
					});

			exitDialog.show();
			return;
		}

		System.out.println("Atik before check device lock status");
		// Atik need to check the device is lock or not
		PBTaskCheckDeviceLockStatus task = new PBTaskCheckDeviceLockStatus();
		task.execute(); // Need to set the URL from constants

	}
	/**
	 * Async task class for checking whether device is locked or not use API
	 * "https://"+API_HOST+"/info_migration"
	 * 
	 * @author atikur
	 * 
	 */
	private class PBTaskCheckPhotoListInCollection extends
			AsyncTask<Void, Void, Void> {

		PBTaskCheckPhotoListInCollection(){
			
			boolean isExceeded = PBPreferenceUtils.getBoolPref(
					activity.getApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_IS_IN_EXCEEDED_MODE, false);
			if (isExceeded) {
				mHandler.sendEmptyMessage(MSG_SHOW_EXCEEDED_SCREEN);
				return;
			}

			if (checkPasswordInputExceeded(password)) {
				return;
			}
		}

		@Override
		protected Void doInBackground(Void... params) {


					PBPreferenceUtils.saveStringPref(
							activity.getApplicationContext(),
							PBConstant.PREF_NAME,
							PBConstant.INTENT_PASSWORD_HONEY_PHOTO,
							password);

					String tokenKey = PBPreferenceUtils.getStringPref(
							activity.getApplicationContext(),
							PBConstant.PREF_NAME,
							PBConstant.PREF_NAME_TOKEN, "");

//					mResponeResult = PBAPIHelper
//							.checkPhotoListInCollection(password,
//									tokenKey, false, null,false,"");
					
					if (isOpenFourDigit) {

						mResponeResult = PBAPIHelper
								.checkPhotoListInCollection(
										password, tokenKey,
										false, null, true,
										mEdtPwdSecretDigit.getText()
												.toString());
					} else {

						mResponeResult = PBAPIHelper
								.checkPhotoListInCollection(
										password, tokenKey,
										false, null, false, "");
					}
					
					// Atik Analytics information for Koukaibukuro Download
					  System.out.println("Atik download password name is :"+password);
                    
        			/*String deviceUUID = PBPreferenceUtils
       			         .getStringPref(PBApplication
       			           .getBaseApplicationContext(),
       			           PBConstant.PREF_NAME,
       			           PBConstant.PREF_NAME_UID, "0");*/
        			
		            PBPreferenceUtils.saveStringPref(activity.getApplicationContext(),
	                         PBConstant.PREF_NAME,
	                         PBConstant.PREF_PASSWORD_NAME_FOR_HISTORY_BUG, password);
		            
					PBPreferenceUtils.saveBoolPref(activity.getApplicationContext(),
	    					PBConstant.PREF_NAME,
	    					PBConstant.PREF_PASSWORD_FROM_LIBRARY_ANALYTICS, false);
        			
					PBPreferenceUtils.saveIntPref(activity.getApplicationContext(),
							PBConstant.PREF_NAME, 
							Constant.ANALYTICS_RESPONSE_CODE_DOWNLOAD_JSON, 
							mResponeResult.errorCode);

					
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mHandler.sendEmptyMessage(MSG_CHECK_PWD_DONE);
			

		}
	}


}