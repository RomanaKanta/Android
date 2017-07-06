package com.aircast.photobag.utils;
//package com.kayac.photobag.utils;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Handler;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.kayac.photobag.R;
//import com.kayac.photobag.activity.PBAcornForestActivity;
//import com.kayac.photobag.activity.PBDisplayCompleteActivity;
//import com.kayac.photobag.activity.PBDownloadConfirmActivity;
//import com.kayac.photobag.activity.PBDownloadDownloadingActivity;
//import com.kayac.photobag.activity.PBDownloadPurchaseActivity;
//import com.kayac.photobag.activity.PBDownloadWrongPassActivity;
//import com.kayac.photobag.activity.PBMainTabBarActivity;
//import com.kayac.photobag.api.PBAPIHelper;
//import com.kayac.photobag.api.ResponseHandle;
//import com.kayac.photobag.api.ResponseHandle.Response;
//import com.kayac.photobag.application.PBApplication;
//import com.kayac.photobag.application.PBConstant;
//import com.kayac.photobag.widget.PBCustomWaitingProgress;
//
//public class ChatDownLoadManager {
//
//	private Activity activity;
//	private String password;
//
//	private static String message_response_check_lock_status = "";
//	private static String titte_after_start_migration = "";
//
//	private final static int MSG_CHECK_PWD_DONE = 1000;
//	private final static int MSG_SHOW_PROGRESS_BAR = 1001;
//	private final static int MSG_SHOW_NOT_FOUND_OR_PURCHASE_SCREEN = 1002;
//	private final static int MSG_SHOW_UNFINISH = 1004;
//	private final static int MSG_SHOW_EXCEEDED_SCREEN = 1003;
//
//	private final static int DIALOG_NO_SDCARD = 2000;
//	private final static int DIALOG_CANNOT_FIND_WITH_PASS = 2001;
//	private final static int DIALOG_UNFINISH = 2002;
//	private final static int DIALOG_IN_REVIEW = 2003;
//	private final static int DIALOG_NEED_UPDATE = 2012;
//	private final static int DIALOG_AIKOTOBA_NOUPDATE = 2013;
//
//	private boolean isDeviceLock = false;
//	private boolean isFromMori = false;
//
//	private static String resultCode = "";
//	private ResponseHandle.Response mResponeResult;
//
//	private PBCustomWaitingProgress mCustomWaitingLayout;
//	public static String passwordFromUrl = "";
//
//	private int mNumberOfRetryCount = 1;
//
//	public ChatDownLoadManager(Activity activity, String password) {
//		super();
//		this.activity = activity;
//		this.password = password;
//		//this.tabPosition =  tabPosition;
//		if (!PBGeneralUtils.checkSdcard(PBMainTabBarActivity.sMainContext, true,
//				null)) {
//			return;
//		}
//
//		if (!PBApplication.hasNetworkConnection()) {
//			PBApplication.makeToastMsg(activity
//					.getString(R.string.pb_network_error_content));
//			return;
//		}
//
//		if (TextUtils.isEmpty(password)) {
//			return;
//		}
//
//		mCustomWaitingLayout = new  PBCustomWaitingProgress(activity);
//		mCustomWaitingLayout.showWaitingLayout();
//		/*PBPreferenceUtils.saveIntPref(activity.getApplicationContext(), PBConstant.PREF_NAME, 
//    			PBConstant.TAB_POSITION, tabPosition);*/
//		
//		System.out.println("Atik before check device lock status");
//		PBTaskCheckDeviceLockStatus task = new PBTaskCheckDeviceLockStatus();
//		task.execute(); // Need to set the URL from constants
//	}
//
//	/**
//	 * Async task class for checking whether device is locked or not use API
//	 * "https://"+API_HOST+"/info_migration"
//	 * 
//	 * @author atikur
//	 * 
//	 */
//	private class PBTaskCheckDeviceLockStatus extends
//			AsyncTask<Void, Void, Void> {
//
//		boolean result200Ok = false;
//		boolean result400 = false;
//
//		@Override
//		protected Void doInBackground(Void... params) {
//
//			System.out
//					.println("Atik called for device lock status check doInBackground");
//
//			String migrationCode = PBPreferenceUtils.getStringPref(
//					PBApplication.getBaseApplicationContext(),
//					PBConstant.PREF_NAME,
//					PBConstant.PREF_MIGRATON_CODE_SUCCESS, "");
//
//			if (TextUtils.isEmpty(migrationCode)) {
//				System.out.println("Atik no migration code is set");
//				return null;
//			}
//			System.out.println("Atik  migration code is :" + migrationCode);
//			Response res = PBAPIHelper
//					.checkDeviceLockForDeviceChange(migrationCode);
//			System.out.println("atik response:" + res.errorCode);
//			Log.d("MIGRATION_CODE", "res: " + res.errorCode + " "
//					+ res.decription);
//			if (res != null) {
//				if (res.errorCode == ResponseHandle.CODE_200_OK) {
//
//					result200Ok = true;
//					System.out.println("atik MIGRATION_CHECK_LOCK_STATUS"
//							+ "200 OK:" + res.decription);
//
//					try {
//						JSONObject result = new JSONObject(res.decription);
//						if (result.has("message")) {
//							System.out.println("200 OK message is:"
//									+ result.getString("message"));
//							message_response_check_lock_status = result
//									.getString("message");
//
//						}
//						if (result.has("result")) {
//							result.getString("result");
//							System.out.println("200 OK Result code is:"
//									+ resultCode);
//						}
//
//						if (result.has("title")) {
//							titte_after_start_migration = result
//									.getString("title");
//							System.out
//									.println("200 OK title  is:" + resultCode);
//						}
//
//						System.out.println("Atik device lock status is 200 OK");
//						isDeviceLock = true;
//
//					} catch (JSONException e) {
//						System.out.println("MIGRATION_CODE"
//								+ " Json parse exception occured");
//					}
//
//				} else if (res.errorCode == ResponseHandle.CODE_400) {
//					result400 = true;
//					System.out.println("Atik device lock status is 400");
//
//					isDeviceLock = false;
//
//				} else {
//
//				}
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			super.onPostExecute(result);
//
//			if (result200Ok) {
//				updateUISuccessfull(message_response_check_lock_status,
//						titte_after_start_migration);
//				isDeviceLock = true;
//				System.out.println("Atik device set lock status 200"
//						+ isDeviceLock);
//				System.out
//						.println("Atik inside else block of aysn task inside 200 ");
//
//			} else if (result400) {
//				isDeviceLock = false;
//				System.out.println("Atik device set lock status false"
//						+ isDeviceLock);
//				System.out
//						.println("Atik inside else block of aysn task inside 400 ");
//				updateUIWhenDeviceIsNotLocked();
//			} else {
//				System.out
//						.println("Atik inside else block of aysn task other ");
//				updateUIWhenDeviceIsNotLocked();
//			}
//
//		}
//	}
//
//	// Update UI when received merge code successfully
//	private void updateUISuccessfull(final String message, final String title) {
//
//		final Handler handler = new Handler();
//		handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				// Delay dialog display after 1s = 1000ms
//				PBGeneralUtils.showAlertDialogActionWithOnClick(activity,
//						title, message,
//						activity.getString(R.string.dialog_ok_btn),
//						mOnClickOkDialogMigrationVerified);
//
//			}
//		}, 1000);
//
//	}
//
//	private DialogInterface.OnClickListener mOnClickOkDialogMigrationVerified = new DialogInterface.OnClickListener() {
//		@Override
//		public void onClick(DialogInterface dialog, int which) {
//			dialog.dismiss();
//		}
//	};
//
//	private void showWrongPassPage() {
//		mNumberOfRetryCount++;
//		Intent intent = new Intent(PBMainTabBarActivity.sMainContext,
//				PBDownloadWrongPassActivity.class);
//		activity.startActivity(intent);
//	}
//
//	// Update UI when received response code 400
//	private void updateUIWhenDeviceIsNotLocked() {
//
//		if (isFromMori) {
//			isFromMori = false;
//			Intent intentReward = new Intent(activity,
//					PBAcornForestActivity.class);
//			activity.startActivity(intentReward);
//		} else {
//			
//			new PBTaskCheckPhotoListInCollection().execute();
//			}
//	}
//
//	/**
//	 * 
//	 * @param passwordToCheck
//	 * @return return <b>true</b> if user reach 10 times of input password or
//	 *         error.
//	 */
//	private boolean checkPasswordInputExceeded(String passwordToCheck) {
//		if (TextUtils.isEmpty(passwordToCheck)) {
//			return true;
//		}
//
//		// check to reset exceeeded status if over 5 mins!
//		if (PBPreferenceUtils.getBoolPref(activity.getApplicationContext(),
//				PBConstant.PREF_NAME, PBConstant.PREF_IS_IN_EXCEEDED_MODE,
//				false)) {
//			long startCountdownTime = PBPreferenceUtils.getLongPref(
//					activity.getApplicationContext(), PBConstant.PREF_NAME,
//					PBConstant.PREF_DL_INPUT_PASS_RETRY_START_TIME, 0);
//			long currenttime = System.currentTimeMillis();
//			currenttime = currenttime - startCountdownTime; // currenttime + (5
//															// * 60 * 1000);
//			long maxCountDownTime = PBConstant.TIME_COUNT_DOWN_EXCEEDED_SCREEN * 60 * 1000;
//			if (currenttime > maxCountDownTime) {
//				// reset retry count
//				mNumberOfRetryCount = 1;
//				resetCountDownTimer();
//				return false;
//			}
//			return true;
//		}
//
//		// prepare to show exceeded screen
//		if (mNumberOfRetryCount > PBConstant.MAX_INPUT_PASSWORD_CHECK_COUNT - 1) {
//			long startTimeCount = System.currentTimeMillis(); /*
//															 * + (PBConstant.
//															 * TIME_COUNT_DOWN_EXCEEDED_SCREEN
//															 * * 60 * 1000)
//															 */
//			PBPreferenceUtils.saveLongPref(activity.getApplicationContext(),
//					PBConstant.PREF_NAME,
//					PBConstant.PREF_DL_INPUT_PASS_RETRY_START_TIME,
//					startTimeCount);
//			PBPreferenceUtils.saveBoolPref(activity.getApplicationContext(),
//					PBConstant.PREF_NAME, PBConstant.PREF_IS_IN_EXCEEDED_MODE,
//					true);
//			// reset retry count
//			mNumberOfRetryCount = 1;
//
//			mHandler.sendEmptyMessage(MSG_SHOW_NOT_FOUND_OR_PURCHASE_SCREEN);
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * method for reset count down timer in case user input one password over 10
//	 * times.
//	 */
//	private void resetCountDownTimer() {
//		// reset PREF_IS_IN_EXCEEDED_MODE to false
//		PBPreferenceUtils.saveBoolPref(activity.getApplicationContext(),
//				PBConstant.PREF_NAME, PBConstant.PREF_IS_IN_EXCEEDED_MODE,
//				false);
//		// reset start check time
//		PBPreferenceUtils.saveLongPref(activity.getApplicationContext(),
//				PBConstant.PREF_NAME,
//				PBConstant.PREF_DL_INPUT_PASS_RETRY_START_TIME, 0);
//		// reset prev pass
//		PBPreferenceUtils.saveStringPref(activity.getApplicationContext(),
//				PBConstant.PREF_NAME, PBConstant.PREF_DL_INPUT_PASS_PREV_PASS,
//				"");
//	}
//
//	private final Handler mHandler = new Handler() {
//		@SuppressWarnings("deprecation")
//		@Override
//		public void handleMessage(android.os.Message msg) {
//			
//			
//			switch (msg.what) {
//			// @lent add
//			case MSG_SHOW_UNFINISH:
//				// reset exceeded count
//				mNumberOfRetryCount = 0;
//				activity.showDialog(DIALOG_UNFINISH);
//				break;
//
//			case MSG_SHOW_NOT_FOUND_OR_PURCHASE_SCREEN:
//				// reset exceeded count
//				mNumberOfRetryCount = 0;
//				showWrongPassPage();
//				break;
//
//			case MSG_SHOW_EXCEEDED_SCREEN:
//				// reset exceeded count
//				mNumberOfRetryCount = 0;
//				Intent mShowExceededIntent = new Intent(
//						PBMainTabBarActivity.sMainContext,
//						PBDisplayCompleteActivity.class);
//				mShowExceededIntent.putExtra(PBConstant.START_EXCEEDED, true);
//				activity.startActivity(mShowExceededIntent);
//				break;
//
//			case MSG_CHECK_PWD_DONE:
//
//				
//
//				Log.d("MSG_CHECK_PWD_DONE", "MSG_CHECK_PWD_DONE");
//				if (mResponeResult != null) {
//					String errorDesc = mResponeResult.decription;
//					Intent mIntent = null;
//
//					switch (mResponeResult.errorCode) {
//					case ResponseHandle.CODE_200_OK:
//
//						/*PBPreferenceUtils.saveBoolPref(
//								activity.getApplicationContext(),
//								PBConstant.PREF_NAME,
//								PBConstant.PREF_PASSWORD_FROM_LIBRARY, true);*/
//						/*mIntent = new Intent(PBTabBarActivity.sMainContext,
//								PBDownloadDownloadingActivity.class);
//						activity.startActivity(mIntent);*/
//						
//						mIntent = new Intent(PBMainTabBarActivity.sMainContext,
//								PBDownloadDownloadingActivity.class);
//						activity.startActivity(mIntent);
//
//						break;
//
//					case ResponseHandle.CODE_400:
//						if (errorDesc.contains("unfinished")) { // case
//																// unfinished
//																// upload
//							activity.showDialog(DIALOG_UNFINISH);
//						} else if (errorDesc.contains("password")) { // case ot
//																		// found
//																		// with
//																		// password
//							// mShowPurchaseScreen = false;
//							showWrongPassPage();
//						} else if (errorDesc.contains("expired")) { // case
//																	// expires
//							showWrongPassPage();
//						} else if (errorDesc.contains("use honey")
//								|| errorDesc.contains("payment")) {
//							/*PBPreferenceUtils
//									.saveBoolPref(
//											activity.getApplicationContext(),
//											PBConstant.PREF_NAME,
//											PBConstant.PREF_PASSWORD_FROM_LIBRARY,
//											true);*/
//							if (needConfirm()) {
//
//								mIntent = new Intent(
//										PBMainTabBarActivity.sMainContext,
//										PBDownloadConfirmActivity.class);
//								mIntent.putExtra("NEED_PURCHASE", true);
//								activity.startActivity(mIntent);
//							} else {
//								mIntent = new Intent(
//										PBMainTabBarActivity.sMainContext,
//										PBDownloadPurchaseActivity.class);
//								mIntent.putExtra(
//										PBConstant.START_PURCHASE_NOTICE, true);
//								activity.startActivity(mIntent);
//							}
//						} else if (errorDesc
//								.contains("client confirmation needed")) {
//							/*PBPreferenceUtils
//							.saveBoolPref(
//									activity.getApplicationContext(),
//									PBConstant.PREF_NAME,
//									PBConstant.PREF_PASSWORD_FROM_LIBRARY,
//									true);*/
//							mIntent = new Intent(PBMainTabBarActivity.sMainContext,
//									PBDownloadConfirmActivity.class);
//							activity.startActivity(mIntent);
//						} else {
//							// mShowPurchaseScreen = false;
//							showWrongPassPage();
//						}
//
//						break;
//					case ResponseHandle.CODE_403:
//						activity.showDialog(DIALOG_IN_REVIEW);
//						break;
//					case ResponseHandle.CODE_406:
//						if (errorDesc.contains("最新のバージョン")) {
//							activity.showDialog(DIALOG_NEED_UPDATE);
//						}
//						break;
//					case ResponseHandle.CODE_404:
//					case ResponseHandle.CODE_INVALID_PARAMS:
//						showWrongPassPage();
//						break;
//					default:
//						break;
//					}
//				} else {
//					Log.e("TAG", ">>> response from server is NULL!");
//				}
//				break;
//
//			case MSG_SHOW_PROGRESS_BAR:
//
//				if (mCustomWaitingLayout == null) {
//					
//				
//					mCustomWaitingLayout.showWaitingLayout();
//				}
//
//				break;
//			default:
//				break;
//
//			}
//			
//			if (mCustomWaitingLayout != null) {
//				mCustomWaitingLayout.hideWaitingLayout();
//			}
//		};
//	};
//
//	private boolean needConfirm() {
//		String downloadInfo = PBPreferenceUtils.getStringPref(activity,
//				PBConstant.PREF_NAME, PBConstant.PREF_PURCHASE_INFO_JSON_DATA,
//				null);
//		if (TextUtils.isEmpty(downloadInfo)) {
//			return false;
//		}
//		try {
//			JSONObject jInfo = new JSONObject(downloadInfo);
//			if (jInfo.getInt("client_keep_days") > 0) {
//				return true;
//			}
//			if (jInfo.getInt("can_save") == 0) {
//				return true;
//			}
//
//		} catch (Exception e) {
//		}
//
//		return false;
//	}
//
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
//	
//	
//	/**
//	 * Async task class for checking whether device is locked or not use API
//	 * "https://"+API_HOST+"/info_migration"
//	 * 
//	 * @author atikur
//	 * 
//	 */
//	private class PBTaskCheckPhotoListInCollection extends
//			AsyncTask<Void, Void, Void> {
//
//		PBTaskCheckPhotoListInCollection(){
//			
//			boolean isExceeded = PBPreferenceUtils.getBoolPref(
//					activity.getApplicationContext(),
//					PBConstant.PREF_NAME,
//					PBConstant.PREF_IS_IN_EXCEEDED_MODE, false);
//			if (isExceeded) {
//				mHandler.sendEmptyMessage(MSG_SHOW_EXCEEDED_SCREEN);
//				return;
//			}
//
//			if (checkPasswordInputExceeded(password)) {
//				return;
//			}
//		}
//
//		@Override
//		protected Void doInBackground(Void... params) {
//
//			
//
//
//					PBPreferenceUtils.saveStringPref(
//							activity.getApplicationContext(),
//							PBConstant.PREF_NAME,
//							PBConstant.INTENT_PASSWORD_HONEY_PHOTO,
//							password);
//
//					String tokenKey = PBPreferenceUtils.getStringPref(
//							activity.getApplicationContext(),
//							PBConstant.PREF_NAME,
//							PBConstant.PREF_NAME_TOKEN, "");
//
//					mResponeResult = PBAPIHelper
//							.checkPhotoListInCollection(password,
//									tokenKey, false, null,false,"");
//
//					
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			super.onPostExecute(result);
//			mHandler.sendEmptyMessage(MSG_CHECK_PWD_DONE);
//			
//
//		}
//	}
//
//
//}