package com.aircast.photobag.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.PBCustomWaitingProgress;

public class PBDownloadSecretCodeActivity extends Activity {
	
	// 1 back from download complete; 2: change to history tab
		private static final String TAG = "PBDownloadSecretCodeFragment";
		public static int sDownloadComplete = 0;
		private LinearLayout mDownloadAD;
		
		String password;

		private static String message_response_check_lock_status = "";
		private static String titte_after_start_migration = "";

		private static String resultCode = "";
		private boolean isDeviceLock = false;
		private boolean isFromMori = false;
		public static boolean isOpenFourDigit = false;
		IntentFilter intentFilter;
		
		private final static int MSG_CHECK_PWD_DONE = 1000;
	    private final static int MSG_SHOW_PROGRESS_BAR = 1001;
	    private final static int MSG_SHOW_NOT_FOUND_OR_PURCHASE_SCREEN = 1002;
	    private final static int MSG_SHOW_UNFINISH = 1004;
	    private final static int MSG_SHOW_EXCEEDED_SCREEN = 1003;

	    private final static int DIALOG_NO_SDCARD = 2000;
	    private final static int DIALOG_CANNOT_FIND_WITH_PASS = 2001;
	    private final static int DIALOG_UNFINISH = 2002;
	    private final static int DIALOG_IN_REVIEW = 2003;
	    private final static int DIALOG_NEED_UPDATE = 2012;
	    private final static int DIALOG_AIKOTOBA_NOUPDATE=2013;

		/**
		 * Respone result getting from server.
		 */
		private ResponseHandle.Response mResponeResult;
		private int mNumberOfRetryCount = 1;

		private PBCustomWaitingProgress mCustomWaitingLayout;
		public static String passwordFromUrl = "";

		/** result json return from server */
		private String mJsonPhotoList = "";
		/** password using to download collection info */
		private String mPhotoListPassword = "";

		
		private EditText mEdtPwdSecretDigit;




		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			setContentView(R.layout.dl_four_digit_screen);
			password = getIntent().getExtras().getString("code","");
			if(password.equals("")){
				
				finish();
			}
			
			mDownloadAD = (LinearLayout) findViewById(R.id.layout_download_ad);
			mEdtPwdSecretDigit = (EditText) findViewById(R.id.edt_dl_pwd_secret_digit);
			TextView mBtnDlInputReceiveImg = (TextView)findViewById(R.id.btn_dl_four_digitinput_get_img);
			Spannable buttonLabel = new SpannableString("  "
					+ getString(R.string.dl_btn_input_pwd_text));
			@SuppressWarnings("deprecation")
			Drawable drawable = getResources().getDrawable(
					R.drawable.btn_ico_dl);
			int intrinsicHeightWidth = (int) TypedValue
					.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP,
							(float) 20, getResources()
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
			mEdtPwdSecretDigit.setOnEditorActionListener(new OnEditorActionListener() {
        	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        	            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
        	            	//dialogPlus.dismiss();
        	            	hideSoftKeyboard();
        	            }    
        	            return false;
        	        }
        	    });
			
//			mEdtPwdSecretDigit.addTextChangedListener(new TextWatcher(){
//			    public void afterTextChanged(Editable s) {
//			        if(mEdtPwdSecretDigit.getText().toString().length() > 0){
//			        	mEdtPwdSecretDigit.setCursorVisible(true);
//			        }else{
//			        	
//			        	mEdtPwdSecretDigit.setCursorVisible(false);
//			        }
//			    }
//			    public void beforeTextChanged(CharSequence s, int start, int count, int after){}
//			    public void onTextChanged(CharSequence s, int start, int before, int count){}
//			}); 
			
			final LinearLayout downloadContent = (LinearLayout) findViewById(R.id.download_content);

			downloadContent.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							if ((downloadContent.getRootView().getHeight() - downloadContent
									.getHeight()) > downloadContent.getRootView()
									.getHeight() / 3) {

								findViewById(
										R.id.layout_download_ad).setVisibility(
										View.GONE);
								mEdtPwdSecretDigit.setCursorVisible(true);

							} else {

								mEdtPwdSecretDigit.setCursorVisible(false);
							findViewById(
										R.id.layout_download_ad).setVisibility(
										View.VISIBLE);

							}
						}
					});
			
			findViewById(R.id.actionbar_home_is_back).setOnClickListener(mOnClickListener);
			findViewById(R.id.textView_back_to_download).setOnClickListener(mOnClickListener);

		}
		

		@Override
		public void onResume() {
			super.onResume();
		}

		// 13062012 @HaiNT15 create function load webview <S>
	

		@Override
		public void onPause() {
			super.onPause();
			hideSoftKeyboard();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			
		}

		private final Handler mHandler = new Handler() {

			@Override
			public void handleMessage(android.os.Message msg) {
				findViewById(R.id.btn_dl_four_digitinput_get_img).setEnabled(true);
				switch (msg.what) {
				// @lent add
				case MSG_SHOW_UNFINISH:
					// reset exceeded count
					mNumberOfRetryCount = 0;
					 showDialog(DIALOG_UNFINISH);
					
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
					startActivity(mShowExceededIntent);
					finish();
					break;

				case MSG_CHECK_PWD_DONE:

					// hide progress bar
					if (mCustomWaitingLayout != null) {
						mCustomWaitingLayout.hideWaitingLayout();
					}

					// findViewById(R.id.btn_dl_input_get_img1).setEnabled(true);
					// findViewById(R.id.btn_dl_input_get_img2).setEnabled(true);

					if (mResponeResult != null) {
						String errorDesc = mResponeResult.decription;
						Intent mIntent = null;
						Intent intent = null;

						switch (mResponeResult.errorCode) {
						case ResponseHandle.CODE_200_OK:
								PBPreferenceUtils.saveStringPref(getApplicationContext(),
						                 PBConstant.UPLOAD_SERVICE_PREF,
						                 PBConstant.PREF_FOUR_DIGIT, mEdtPwdSecretDigit.getText().toString());
							
							mJsonPhotoList = PBPreferenceUtils.getStringPref(
									getApplicationContext(), PBConstant.PREF_NAME,
									PBConstant.PREF_DL_PHOTOLIST_JSON_DATA, "");
							mPhotoListPassword = PBPreferenceUtils.getStringPref(
									getApplicationContext(), PBConstant.PREF_NAME,
									PBConstant.PREF_DL_PHOTOLIST_PASS, "");

							System.out.println("password list atik:"
									+ mPhotoListPassword);

							int counter = 0;
							int photo_count = 0;
							JSONObject jsonRoot = null;
							JSONArray jsonPhotosArray = null;
							try {
								ArrayList<String> localStorageFileList = new ArrayList<String>();
								localStorageFileList = PBGeneralUtils
										.getAllCacheFileFromStorage();
								jsonRoot = new JSONObject(mJsonPhotoList);

								if (jsonRoot.has("photos_count")) {
									photo_count = jsonRoot.getInt("photos_count");
								}

								jsonPhotosArray = jsonRoot.getJSONArray("photos");
								for (int i = 0, count = jsonPhotosArray.length(); i < count; i++) {
									JSONObject jsonObject = jsonPhotosArray
											.getJSONObject(i);
									String imageURL = jsonObject.optString("url")
											.toString();
									String filename = PBGeneralUtils
											.getPathFromCacheFolder(imageURL);
									System.out
											.println("Inside doInBackground photo path atik:"
													+ filename);
									String thumbURL = jsonObject.optString("thumb")
											.toString();
									String fileThumbname = PBGeneralUtils
											.getPathFromCacheFolder(thumbURL);
									System.out
											.println("Inside doInBackground photo thumb path atik:"
													+ fileThumbname);

									if (localStorageFileList.contains(filename)
											|| localStorageFileList
													.contains(fileThumbname)) {
										counter++;
									}
								}
								System.out.println("counter value:" + counter);
							} catch (JSONException e) {
								e.printStackTrace();
							}

							// Atik code for download aikotoba bug fix
							PBDatabaseManager dbMgr = PBDatabaseManager
									.getInstance(PBApplication
											.getBaseApplicationContext());
							Cursor cursor = dbMgr
									.getHistoriesCursor(PBDatabaseDefinition.HISTORY_INBOX);
							Bundle extras = new Bundle();

							int inc = 0;
							boolean existAiKotoba = false;
							int PHOTO_COUNT = 0;
							long history_id = -1;
							String collection_id = null;
							String currentCollectionId = null;
							try {
								currentCollectionId = jsonRoot.getString("id");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (cursor.moveToFirst()) {
								while (!cursor.isAfterLast()) {
									inc++;
									collection_id = cursor
											.getString(cursor
													.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
									if (currentCollectionId
											.equalsIgnoreCase(collection_id)) {
										existAiKotoba = true;
										PHOTO_COUNT = cursor
												.getInt(cursor
														.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PHOTO_COUNT));
										history_id = cursor
												.getLong(cursor
														.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ID));
										collection_id = cursor
												.getString(cursor
														.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
										extras.putLong(
												PBConstant.HISTORY_ITEM_ID,
												cursor.getLong(cursor
														.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ID)));
										extras.putString(
												PBConstant.HISTORY_COLLECTION_ID,
												cursor.getString(cursor
														.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID)));
										extras.putString(
												PBConstant.HISTORY_PASSWORD,
												cursor.getString(cursor
														.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PASSWORD)));
										extras.putLong(
												PBConstant.HISTORY_CREATE_DATE,
												cursor.getLong(cursor
														.getColumnIndex(PBDatabaseDefinition.HistoryData.C_CREATED_AT)));
										extras.putLong(
												PBConstant.HISTORY_CHARGE_DATE,
												cursor.getLong(cursor
														.getColumnIndex(PBDatabaseDefinition.HistoryData.C_CHARGES_AT)));
										extras.putString(
												PBConstant.COLLECTION_THUMB,
												cursor.getString(cursor
														.getColumnIndex(PBDatabaseDefinition.HistoryData.C_THUMB)));
										extras.putInt(
												PBConstant.HISTORY_ADDIBILITY,
												cursor.getInt(cursor
														.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ADDIBILITY)));
										extras.putInt(
												PBConstant.HISTORY_IS_UPDATABLE,
												cursor.getInt(cursor
														.getColumnIndex(PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE)));
										extras.putLong(
												PBConstant.HISTORY_UPDATED_AT,
												cursor.getLong(cursor
														.getColumnIndex(PBDatabaseDefinition.HistoryData.C_UPDATED_AT)));
										extras.putInt(
												PBConstant.HISTORY_SAVE_MARK,
												cursor.getInt(cursor
														.getColumnIndex(PBDatabaseDefinition.HistoryData.C_SAVE_MARK)));
										extras.putInt(
												PBConstant.HISTORY_SAVE_DAYS,
												cursor.getInt(cursor
														.getColumnIndex(PBDatabaseDefinition.HistoryData.C_SAVE_DAYS)));
										extras.putString(
												PBConstant.HISTORY_AD_LINK,
												cursor.getString(cursor
														.getColumnIndex(PBDatabaseDefinition.HistoryData.C_AD_LINK)));
										extras.putBoolean(
												PBConstant.HISTORY_CATEGORY_INBOX,
												true);

									}
									// do what ever you want here
									cursor.moveToNext();
								}
							}
							cursor.close();

							// Check all the file already downloadd or not
							if (existAiKotoba
									&& photo_count == PHOTO_COUNT) {
								//getActivity().showDialog(DIALOG_AIKOTOBA_NOUPDATE);
								showDialog(DIALOG_AIKOTOBA_NOUPDATE);

							} else {

								if (PBPreferenceUtils.getBoolPref(
										PBApplication.getBaseApplicationContext(),
										PBConstant.PREF_NAME,
										PBConstant.PREF_NO_NEED_UPDATE, false)) {
									PBPreferenceUtils.saveBoolPref(
											getApplicationContext(),
											PBConstant.PREF_NAME,
											PBConstant.PREF_NO_NEED_UPDATE, false);
									showDialog(
											DIALOG_AIKOTOBA_NOUPDATE);

								} else {
									if (existAiKotoba) {
										// reset exceeded count
										mNumberOfRetryCount = 0;
										intent = new Intent(
												PBMainTabBarActivity.sMainContext,
												PBDownloadDownloadingActivity.class);
										intent.putExtra(
												PBConstant.DOWNLOAD_UPDATE_OLD_BAG_ID,
												history_id);
										System.out
												.println("Atik history id before start downloading while aikotoba exists"
														+ history_id);
										intent.putExtra(PBConstant.COLLECTION_ID,
												collection_id);
										System.out
												.println("Atik collection id before start downloading while aikotoba exists"
														+ collection_id);
										startActivity(intent);
										finish();

									} else {
										// reset exceeded count
										mNumberOfRetryCount = 0;
										mIntent = new Intent(
												PBMainTabBarActivity.sMainContext,
												PBDownloadDownloadingActivity.class);
										startActivity(mIntent);
										finish();
									}
								}

							}

							// }

							break;

						case ResponseHandle.CODE_400:
							if (errorDesc.contains("unfinished")) { // case
																	// unfinished
																	// upload
					showDialog(DIALOG_UNFINISH);
								
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
								if (needConfirm()) {
									mIntent = new Intent(
											PBMainTabBarActivity.sMainContext,
											PBDownloadConfirmActivity.class);
									mIntent.putExtra("NEED_PURCHASE", true);
									startActivity(mIntent);
									finish();
								} else {
									mIntent = new Intent(
											PBMainTabBarActivity.sMainContext,
											PBDownloadPurchaseActivity.class);
									mIntent.putExtra(
											PBConstant.START_PURCHASE_NOTICE, true);
									startActivity(mIntent);
									finish();
								}
							} else if (errorDesc
									.contains("client confirmation needed")) {
								mIntent = new Intent(
										PBMainTabBarActivity.sMainContext,
										PBDownloadConfirmActivity.class);
								startActivity(mIntent);
								finish();
							} else {
								// mShowPurchaseScreen = false;
								showWrongPassPage();
							}

							break;
						case ResponseHandle.CODE_403:
							
							if(mResponeResult.decription == null){
								
								showDialog(DIALOG_IN_REVIEW);         	
								return ;
							}
							
							if(mResponeResult.decription.contains("&&")){
								
								List<String> list = new ArrayList<String>(Arrays.asList(mResponeResult.decription.split(" && ")));
								Toast.makeText(getApplicationContext(),
										list.get(0), Toast.LENGTH_SHORT)
										.show();
								if(list.get(1).equals("locked")){
									
									finish();
									overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
								}
							}
							

							break;
						case ResponseHandle.CODE_406:
							if (errorDesc.contains("最新のバージョン")) {
								showDialog(DIALOG_NEED_UPDATE); 
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
						Log.e(TAG, ">>> response from server is NULL!");
					}
					break;

				case MSG_SHOW_PROGRESS_BAR:
					if (mCustomWaitingLayout == null) {
						mCustomWaitingLayout = new PBCustomWaitingProgress(
								PBDownloadSecretCodeActivity.this);
						mCustomWaitingLayout.showWaitingLayout();
					} else {
						mCustomWaitingLayout.showWaitingLayout();
					}

					break;
				default:
					break;

				}
			};
		};

		private void showWrongPassPage() {
			mNumberOfRetryCount++;
			Intent intent = new Intent(PBMainTabBarActivity.sMainContext,
					PBDownloadWrongPassActivity.class);
			startActivity(intent);
			finish();
		}

		/**
		 * method support hide soft keyboard on phone screen.
		 */
		private void hideSoftKeyboard() {
			InputMethodManager imm = (InputMethodManager) 
					getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mEdtPwdSecretDigit.getWindowToken(), 0);
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}
		
//		void showDialog(int title,int mgs) {
//			PBDialogFragment newFragment = PBDialogFragment.newInstance(
//					title,mgs);
//		    newFragment.show(getFragmentManager(), "dialog");
//		}



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
			if (PBPreferenceUtils.getBoolPref(
					getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_IS_IN_EXCEEDED_MODE, false)) {
				long startCountdownTime = PBPreferenceUtils.getLongPref(
						getApplicationContext(),
						PBConstant.PREF_NAME,
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
				PBPreferenceUtils.saveLongPref(getApplicationContext(), PBConstant.PREF_NAME,
						PBConstant.PREF_DL_INPUT_PASS_RETRY_START_TIME,
						startTimeCount);
				PBPreferenceUtils.saveBoolPref(getApplicationContext(), PBConstant.PREF_NAME,
						PBConstant.PREF_IS_IN_EXCEEDED_MODE, true);
				// reset retry count
				mNumberOfRetryCount = 1;

				mHandler.sendEmptyMessage(MSG_SHOW_NOT_FOUND_OR_PURCHASE_SCREEN);
				return true;
			}
			return false;
		}

		
		 @Override
		    protected Dialog onCreateDialog(int id) {
		        switch (id) {

		        case DIALOG_NO_SDCARD:
		            return new AlertDialog.Builder(PBDownloadSecretCodeActivity.this)
		            .setIcon(android.R.drawable.ic_dialog_alert)
		            .setTitle(R.string.dl_alert_sdcard_missing_title)
		            .setMessage(getString(R.string.dl_alert_sdcard_missing_msg))
		            .setPositiveButton(R.string.dialog_ok_btn, 
		                    new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog,
		                        int which) {
		                    return;
		                }
		            }).create();

		        case DIALOG_CANNOT_FIND_WITH_PASS:
		            return new AlertDialog.Builder(PBDownloadSecretCodeActivity.this)
		            .setIcon(android.R.drawable.ic_dialog_alert)
		            .setTitle(R.string.dl_alert_cannot_find_album_title)
		            .setMessage(getString(R.string.dl_alert_cannot_find_album_msg))
		            .setPositiveButton(R.string.dialog_ok_btn, 
		                    new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog,
		                        int which) {

		                    mNumberOfRetryCount++;
		                    Log.d(TAG, ">>> retry count=" + mNumberOfRetryCount);
		                    dialog.dismiss();
		                    return;
		                }
		            }).create();
		        case DIALOG_UNFINISH:
		            return new AlertDialog.Builder(PBDownloadSecretCodeActivity.this)
		            .setIcon(android.R.drawable.ic_dialog_alert)
		            .setTitle(R.string.dl_alert_upload_unfinished_title)
		            .setMessage(getString(R.string.dl_alert_upload_unfinished_msg))
		            .setPositiveButton(R.string.dialog_ok_btn, 
		                    new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog,
		                        int which) {
		                    dialog.dismiss();
		                }
		            }).create();
		        case DIALOG_IN_REVIEW:
		        	return new AlertDialog.Builder(PBDownloadSecretCodeActivity.this)
		            .setIcon(android.R.drawable.ic_dialog_alert)
		            .setTitle(R.string.pb_password_banned_title)
		            .setMessage(getString(R.string.pb_password_banned_context))
		            .setPositiveButton(R.string.dialog_ok_btn, 
		                    new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog,
		                        int which) {
		                    dialog.dismiss();
		                }
		            }).create();
		        case DIALOG_NEED_UPDATE:
		        	return new AlertDialog.Builder(PBDownloadSecretCodeActivity.this)
		            .setIcon(android.R.drawable.ic_dialog_alert)
		            .setTitle(R.string.pb_download_need_update_title)
		            .setMessage(getString(R.string.pb_download_need_update_content))
		            .setPositiveButton(R.string.dialog_ok_btn, 
		                    new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog,
		                        int which) {
		                    dialog.dismiss();
		                }
		            }).create();
		        	
		        case DIALOG_AIKOTOBA_NOUPDATE:
		        	return new AlertDialog.Builder(PBDownloadSecretCodeActivity.this)
		            .setIcon(android.R.drawable.ic_dialog_alert)
		            .setTitle(R.string.pb_download_need_update_title)
		            .setMessage(getString(R.string.pb_download_aikotoba_already_uptodate))
		            .setPositiveButton(R.string.dialog_ok_btn, 
		                    new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog,
		                        int which) {
		                    dialog.dismiss();
		                }
		            }).create();
		        	
		        default:
		            break;
		        }

		        return null;
		    }

		/**
		 * method for reset count down timer in case user input one password over 10
		 * times.
		 */
		private void resetCountDownTimer() {
			// reset PREF_IS_IN_EXCEEDED_MODE to false
			PBPreferenceUtils.saveBoolPref(getApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_IS_IN_EXCEEDED_MODE,
					false);
			// reset start check time
			PBPreferenceUtils.saveLongPref(getApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_DL_INPUT_PASS_RETRY_START_TIME, 0);
			// reset prev pass
			PBPreferenceUtils.saveStringPref(getApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_DL_INPUT_PASS_PREV_PASS,
					"");
		}

		private boolean needConfirm() {
			String downloadInfo = PBPreferenceUtils.getStringPref(getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_PURCHASE_INFO_JSON_DATA, null);
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

		private OnClickListener mOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_dl_four_digitinput_get_img:
					downloadButtonAction();

					break;
					
				case R.id.actionbar_home_is_back:
					finish();
					overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);

					break;

				case R.id.textView_back_to_download:
					finish();
					overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
					break;


				default:
					break;
				}
			}
		};
		
		@Override
		public void onBackPressed(){
			
			finish();
			overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
		}

		private void downloadButtonAction() {
			

			
			findViewById(R.id.btn_dl_four_digitinput_get_img).setEnabled(false);

			hideSoftKeyboard();
			mDownloadAD.setVisibility(View.VISIBLE);
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
						new ContextThemeWrapper(PBDownloadSecretCodeActivity.this, R.style.popup_theme));
				exitDialog
						.setMessage(getString(R.string.pb_network_not_available_general_message));
				exitDialog.setCancelable(false);
				exitDialog.setPositiveButton(getString(R.string.dialog_ok_btn),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

								dialog.dismiss();
							}
						});

				exitDialog.show();
				return;
			}


			if (TextUtils.isEmpty(password)) {
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
		private class PBTaskCheckDeviceLockStatus extends
				AsyncTask<Void, Void, Void> {

			boolean result200Ok = false;
			boolean result400 = false;

			@Override
			protected Void doInBackground(Void... params) {

//				System.out
//						.println("Atik called for device lock status check doInBackground");

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
				//System.out.println("Atik  migration code is :" + migrationCode);
				Response res = PBAPIHelper
						.checkDeviceLockForDeviceChange(migrationCode);
				//System.out.println("atik response:" + res.errorCode);
//				Log.d("MIGRATION_CODE", "res: " + res.errorCode + " "
//						+ res.decription);
				if (res != null) {
					if (res.errorCode == ResponseHandle.CODE_200_OK) {

						result200Ok = true;
//						System.out.println("atik MIGRATION_CHECK_LOCK_STATUS"
//								+ "200 OK:" + res.decription);

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

				// findViewById(R.id.btn_dl_input_get_img1).setEnabled(true);
				// findViewById(R.id.btn_dl_input_get_img2).setEnabled(true);

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
					PBGeneralUtils.showAlertDialogActionWithOnClick(getApplicationContext(), title, message,
							getString(R.string.dialog_ok_btn),
							mOnClickOkDialogMigrationVerified);

				}
			}, 1000);

		}

		// Update UI when received response code 400
		private void updateUIWhenDeviceIsNotLocked() {

			if (isFromMori) {
				isFromMori = false;
				Intent intentReward = new Intent(PBDownloadSecretCodeActivity.this,
						PBAcornForestActivity.class);
				startActivity(intentReward);
				finish();
			} else {
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// Delay dialog display after 1s = 1000ms
						boolean isExceeded = PBPreferenceUtils.getBoolPref(
								getApplicationContext(),
								PBConstant.PREF_NAME,
								PBConstant.PREF_IS_IN_EXCEEDED_MODE, false);
						if (isExceeded) {
							mHandler.sendEmptyMessage(MSG_SHOW_EXCEEDED_SCREEN);
							return;
						}

						if (checkPasswordInputExceeded(password)) {
							return;
						}

						mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_BAR);
						Thread thread = new Thread(new Runnable() {
							@Override
							public void run() {
								// confirm password

								PBPreferenceUtils.saveStringPref(getApplicationContext(),
										PBConstant.PREF_NAME,
										PBConstant.INTENT_PASSWORD_HONEY_PHOTO,
										password);

								String tokenKey = PBPreferenceUtils.getStringPref(
										getApplicationContext(),
										PBConstant.PREF_NAME,
										PBConstant.PREF_NAME_TOKEN, "");
								mResponeResult = PBAPIHelper
										.checkPhotoListInCollection(
												password, tokenKey,
												false, null, true,
												mEdtPwdSecretDigit.getText()
														.toString());

								
								System.out.println(mResponeResult.errorCode + "  "
										+ mResponeResult.decription);
								System.out
										.println("Atik download password name is :"
												+ password);
								
								


								PBPreferenceUtils
										.saveStringPref(
												getBaseContext(),
												PBConstant.PREF_NAME,
												PBConstant.PREF_PASSWORD_NAME_FOR_HISTORY_BUG,
												password);


								mHandler.sendEmptyMessage(MSG_CHECK_PWD_DONE);
							}
						});
						thread.start();

					}
				}, 1000);
			}
		}

		private DialogInterface.OnClickListener mOnClickOkDialogMigrationVerified = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};




}
