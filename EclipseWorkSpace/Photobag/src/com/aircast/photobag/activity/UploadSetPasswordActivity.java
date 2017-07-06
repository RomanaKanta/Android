package com.aircast.photobag.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.services.SaveDBTask;
import com.aircast.photobag.services.UploadService;
import com.aircast.photobag.services.UploadServiceUtils;
import com.aircast.photobag.services.UploadService.MediaInfo;
import com.aircast.photobag.services.UploadService.ServiceUpdateUIListener;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.utils.PBVideoUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * "Set your password  （´ ・(ｪ)・` ）クマ "
 * */
public class UploadSetPasswordActivity extends PBAbsActionBarActivity implements
		OnClickListener {

	private static String mToken;
	ArrayList<String> mSelectedMediaList;
	long[] mSelectedMediaTypeList;
	ArrayList<MediaInfo> mUploadedUrlList;
	EditText mPasswordInput;
	public String mPassword, mLongPassword;
	private Response mErrorDescription = null;
	private LinearLayout mLvWaitingLayout;
	private boolean mIsActivityDestroyed = false;
	private String mCollectionId;
	private WebView myWebView;
	private EasyTracker easyTracker = null;
	private final String TAG = "UploadSetPasswordActivity";
	public static AlertDialog mCompleteDlgForSetPasswordToPublic;
	public static AlertDialog mConfirmationForSetPasswordToPublic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		easyTracker = EasyTracker.getInstance(this);
		mIsActivityDestroyed = false;
		mSelectedMediaList = getIntent().getStringArrayListExtra(
				PBConstant.INTENT_SELECTED_MEDIA);
		getVideoDuration();
		mToken = PBPreferenceUtils.getStringPref(getBaseContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");

		setContentView(R.layout.pb_upload_set_password_layout);

		myWebView = (WebView) findViewById(R.id.webview);
		if (!PBApplication.hasNetworkConnection()) {
			myWebView.setVisibility(View.INVISIBLE);
		}

		// @lent5 add action bar #S
		ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
		setHeader(headerBar, getString(R.string.screen_title_upload_input_pwd));
		// @lent5 add action bar #E
		mPasswordInput = (EditText) findViewById(R.id.upload_set_password);
		if (getIntent().getStringExtra(PBConstant.INTENT_PASSWORD) != null) {
			mPasswordInput.setText(getIntent().getStringExtra(
					PBConstant.INTENT_PASSWORD));
		}
		FButton mSetPasswordBtn1 = (FButton) findViewById(R.id.btn_set_password1);
		FButton mSetPasswordBtn2 = (FButton) findViewById(R.id.btn_set_password2);
		mSetPasswordBtn1.setOnClickListener(this);
		mSetPasswordBtn2.setOnClickListener(this);

		mLvWaitingLayout = (LinearLayout) findViewById(R.id.ll_loading_panel_waiting);

		final LinearLayout uploadContent = (LinearLayout) findViewById(R.id.uploadcontent);

		uploadContent.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if ((uploadContent.getRootView().getHeight() - uploadContent
								.getHeight()) > uploadContent.getRootView()
								.getHeight() / 3) {

							findViewById(R.id.btn_set_password1).setVisibility(
									View.GONE);
							findViewById(R.id.btn_set_password2).setVisibility(
									View.VISIBLE);

						} else {

							findViewById(R.id.btn_set_password1).setVisibility(
									View.VISIBLE);
							findViewById(R.id.btn_set_password2).setVisibility(
									View.GONE);

						}
					}
				});

		// set listener to tracking photo uploading
		UploadService.setUpdateListener(new ServiceUpdateUIListener() {
			@Override
			public void updateUI(int uploadedPhotos,
					ArrayList<MediaInfo> uploadedUrlPhoto) {
				mUploadedUrlList = uploadedUrlPhoto;
			}

			@Override
			public void updateProgressBar(int percent) {
				// no need to implement!
			}

			@Override
			public void updateCompressBar(int totalFile, int percent) {
			}

			@Override
			public void onUploadForbidden() {

			}

			@Override
			public void updateAnalytics(String data) {
				// TODO Auto-generated method stub
				Log.d("setpassword", data);

				easyTracker.send(MapBuilder.createEvent(
						"ScreenName-> UploadSetPasswordActivity",
						"VideoCompression", data, null).build());
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		// V2 google analytics has been comment out
		/*
		 * if (PBTabBarActivity.gaTracker != null) {
		 * PBTabBarActivity.gaTracker.trackPageView
		 * ("UploadSetPasswordActivity"); }
		 */

		if (myWebView != null && PBApplication.hasNetworkConnection()) {
			loadPasswordLengthInformation(myWebView);
		} else {
			myWebView.setVisibility(View.GONE);
		}

		mIsActivityDestroyed = false;
		// check internet
		UploadServiceUtils.checkInternetConenction(
				UploadSetPasswordActivity.this, (!mIsActivityDestroyed), true);
	}
	

	@SuppressLint("SetJavaScriptEnabled")
	protected void loadPasswordLengthInformation(final WebView wv) {

		wv.setVisibility(View.VISIBLE);
		wv.setInitialScale(30);
		wv.getSettings().setUseWideViewPort(true);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				wv.setVisibility(View.INVISIBLE);
				return false;
			}
		});

		// @HaiNT15: transparent the white color of the web view <S>
		wv.setBackgroundColor(0xECE9E5);
		(new Thread(new Runnable() {
			public void run() {
				wv.loadUrl(getString(R.string.pb_password_length_url));
			}
		})).run();
	}

	private void getVideoDuration() {
		mSelectedMediaTypeList = getIntent().getLongArrayExtra(
				PBConstant.INTENT_SELECTED_MEDIA_TYPE);

		for (int i = 0; i < mSelectedMediaList.size(); i++) {

			long duration = -1;
			String mediaPath = mSelectedMediaList.get(i);

			if (PBGeneralUtils.checkVideoIsValid(mediaPath)) {
				PBVideoUtils mVideoUtils = new PBVideoUtils();
				duration = mVideoUtils.getDuration(mediaPath);
			}

			mSelectedMediaTypeList[i] = duration;

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_set_password1:
			setPasswordAction();
			break;
		case R.id.btn_set_password2:
			setPasswordAction();
			break;
		default:
			break;
		}
	}

	private void setPasswordAction() {

		mPassword = mPasswordInput.getText().toString();
		findViewById(R.id.btn_set_password1).setEnabled(false);
		findViewById(R.id.btn_set_password2).setEnabled(false);
		// @lent check password is empty before execute #S
		if (TextUtils.isEmpty(mPassword) || mPassword.length() < 3) {
			String message = getString(R.string.pb_apihelper_password_empty);
			if (mPassword.length() > 0 && mPassword.length() < 3) {
				message = getString(R.string.err_desc_password_to_short);
			}
			if (!mIsActivityDestroyed) {
				PBGeneralUtils.showAlertDialogActionWithOnClick(
						UploadSetPasswordActivity.this,
						android.R.drawable.ic_dialog_alert,
						getString(R.string.dialog_error_title), message,
						getString(R.string.pb_btn_OK),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								findViewById(R.id.btn_set_password1)
										.setEnabled(true);
								findViewById(R.id.btn_set_password2)
										.setEnabled(true);
								dialog.dismiss();
							}
						});
				findViewById(R.id.btn_set_password1).setEnabled(true);
				findViewById(R.id.btn_set_password2).setEnabled(true);
			}
		} else {
			// 20120223 @lent avoid fc
			mLvWaitingLayout.setVisibility(View.VISIBLE);
			boolean tag = PBPreferenceUtils.getBoolPref(
					getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.UPLOAD_TAG, true);
			if (tag) {
				if (mConfirmationForSetPasswordToPublic == null) {
					mConfirmationForSetPasswordToPublic = new AlertDialog.Builder(
							new ContextThemeWrapper(this, R.style.popup_theme))
							.setTitle(
									getString(R.string.pb_upload_koukaibukuro_dialog_publsh_passwd_confirmation_title))
							.setMessage(
									getString(R.string.pb_upload_koukaibukuro_dialog_publsh_passwd_confirmation_message))
							.setCancelable(false)
							.setPositiveButton(getString(R.string.dialog_ok_btn),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											// requestAuthorization();
											findViewById(R.id.btn_set_password1)
											.setEnabled(true);
									findViewById(R.id.btn_set_password2)
											.setEnabled(true);
											dialog.dismiss();
											new ConfirmPasswordTask().execute();

										}
									})
							.setNegativeButton(
									getString(R.string.dialog_cancel_btn),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {

											dialog.dismiss();
											
											findViewById(R.id.btn_set_password1)
											.setEnabled(true);
									findViewById(R.id.btn_set_password2)
											.setEnabled(true);
										}
									}).create();

					mConfirmationForSetPasswordToPublic.show();
					mConfirmationForSetPasswordToPublic = null;
				}
			} else {
				new ConfirmPasswordTask().execute();
			}
			
		}
		// @lent check password is empty before execute #E
	}

	// @lent5 add action bar #S
	@Override
	protected void handleHomeActionListener() {
		stopUploadService();
		finish();
	}

	/**
	 * Stop the upload service that has been started in select photo activity
	 */
	private void stopUploadService() {
		// 20120225 @lent fix issue key back during uploading
		UploadingActivity.cancelUpload(UploadSetPasswordActivity.this,
				mUploadedUrlList);
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {

	}

	// @lent5 add action bar #E

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		PBPreferenceUtils.saveBoolPref(getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.UPLOAD_TAG, false);
		stopUploadService();
	}

	private class ConfirmPasswordTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			findViewById(R.id.btn_set_password1).setEnabled(true);
			findViewById(R.id.btn_set_password2).setEnabled(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			mCollectionId = PBPreferenceUtils.getStringPref(getBaseContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_COLLECTION_ID, "");
			mToken = PBPreferenceUtils.getStringPref(getBaseContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
			Response result = PBAPIHelper.confirmPasswordForUploadOnly(
					getBaseContext(), mPassword, mCollectionId, mToken);
			if (result != null) {
				if (result.errorCode == ResponseHandle.CODE_200_OK) {// password
					// is valid
					String jsonResult = result.decription;
					mLongPassword = parseLongPassword(jsonResult);

					// Atik koukaibukuro setting will be opened from here
					/*
					 * Intent intent = new Intent(getBaseContext(),
					 * PBUploadConfirmActivity.class);
					 * intent.putStringArrayListExtra(
					 * PBConstant.INTENT_SELECTED_MEDIA, mSelectedMediaList);
					 * intent.putExtra( PBConstant.INTENT_SELECTED_MEDIA_TYPE,
					 * mSelectedMediaTypeList);
					 * intent.putExtra(PBConstant.INTENT_LONG_PASSWORD,
					 * mLongPassword);
					 * intent.putExtra(PBConstant.INTENT_PASSWORD,
					 * mPasswordInput.getText().toString());
					 * startActivity(intent); finish();
					 */

					// TODO check Status
					boolean connected = UploadServiceUtils
							.checkInternetConenction(
									UploadSetPasswordActivity.this,
									(!mIsActivityDestroyed), false);
					if (connected) {

						// if (!PBDownloadFragment.isKoukaibukuroDisabled) {
						boolean tag = PBPreferenceUtils.getBoolPref(
								getApplicationContext(), PBConstant.PREF_NAME,
								PBConstant.UPLOAD_TAG, true);
						if (tag) {
							showOpenIdScreen();
						} else {
							showFourDigitScreen();
						}

						// } else {
						// skipKoukaibukuroScreen();
						// }
						/*
						 * // new SetPasswordToPublic().execute(); new
						 * GetStatusOfKoukaibukuro().execute();
						 */

					}

				} else {// Handle error
					// mErrorDescription = result.decription;
					mErrorDescription = result;
					mHandler.sendEmptyMessage(SHOW_ERROR_DIALOG);
				}
			}
			return null;
		}

		private String parseLongPassword(String jsonResult) {
			JSONObject jObject;
			if (!TextUtils.isEmpty(jsonResult)
					&& jsonResult.contains("longer_password")) {
				try {
					jObject = new JSONObject(jsonResult);
					if (jObject != null && jObject.has("longer_password")) {
						return jObject.getString("longer_password");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		private final int SHOW_ERROR_DIALOG = 0;
		private Handler mHandler = new Handler(new Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case SHOW_ERROR_DIALOG:
					if (mErrorDescription == null)
						return false;
					if (mLvWaitingLayout != null) {
						mLvWaitingLayout.setVisibility(View.GONE);
					}
					if (!mIsActivityDestroyed) {

						CharSequence dialogMessage = ResponseHandle
								.getNotifyStringErrorRelatedPassword(
										mErrorDescription.errorCode,
										mErrorDescription.decription);

						AlertDialog.Builder errorDialog = new AlertDialog.Builder(
								new ContextThemeWrapper(
										UploadSetPasswordActivity.this,
										R.style.popup_theme));
						errorDialog.setIcon(android.R.drawable.ic_dialog_alert);
						errorDialog.setTitle(R.string.dialog_error_title);
						errorDialog.setMessage(dialogMessage);
						errorDialog.setCancelable(true);
						errorDialog.setPositiveButton(
								getString(R.string.dialog_ok_btn),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										dialog.dismiss();
									}
								});

						errorDialog.show();
					}
					break;
				default:
					break;
				}
				return false;
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mIsActivityDestroyed = true;
	}

	@Override
	public void onStart() {

		super.onStart();
		System.out.println("Atik start Easy Tracker");
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mIsActivityDestroyed = true;
		System.out.println("Atik stop Easy Tracker");
		EasyTracker.getInstance(this).activityStop(this);
	}

	private void showOpenIdScreen() {
		
		boolean connected = UploadServiceUtils
				.checkInternetConenction(
						UploadSetPasswordActivity.this,
						(!mIsActivityDestroyed),
						false);
		if (connected) {
			mCollectionId = PBPreferenceUtils
					.getStringPref(
							getBaseContext(),
							PBConstant.UPLOAD_SERVICE_PREF,
							PBConstant.PREF_COLLECTION_ID,
							"");
			mToken = PBPreferenceUtils
					.getStringPref(
							getBaseContext(),
							PBConstant.PREF_NAME,
							PBConstant.PREF_NAME_TOKEN,
							"");
			// new
			// SetPasswordToPublic().execute();
			new SetPasswordTask().execute();

		} else {
			// mConfirmPassBtn.setEnabled(true);
		}

	}

	private static String mResponseDescriptionSetPassword;
	private static int mResponseErrorSetPassword;
	private Response mResponseSetPassword;

	private class SetPasswordTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			mResponseSetPassword = PBAPIHelper.setPassword(mPassword,
					mCollectionId, mToken, false, "");
			if (mResponseSetPassword != null) {
				mResponseDescriptionSetPassword = mResponseSetPassword.decription;
				mResponseErrorSetPassword = mResponseSetPassword.errorCode;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// go to uploading screen
			super.onPostExecute(result);
			// @lent add progress bar
			// mLvWaiting.setVisibility(View.GONE);
			// mConfirmPassBtn.setEnabled(true);

			if (mResponseErrorSetPassword != ResponseHandle.CODE_200_OK) { // Handle
																			// error
																			// here
				Log.d(TAG, "[set password] " + mResponseDescriptionSetPassword);
				String errorNotifyMes = ResponseHandle
						.getNotifyStringErrorRelatedPassword(
								mResponseSetPassword.errorCode,
								mResponseSetPassword.decription);

				// mMakeLongPassBtn.setEnabled(false);
				if (!mIsActivityDestroyed) {
					PBGeneralUtils.showAlertDialogActionWithOnClick(
							UploadSetPasswordActivity.this,
							android.R.drawable.ic_dialog_alert,
							getString(R.string.dialog_error_title),
							errorNotifyMes,
							getString(R.string.upload_confirm_pass_ok_btn),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
				}
			} else { // set password successful
				String passwordId = null;
				JSONObject jObject;
				if (!TextUtils.isEmpty(mResponseDescriptionSetPassword)
						&& mResponseDescriptionSetPassword
								.contains("collection")) {
					try {
						jObject = new JSONObject(
								mResponseDescriptionSetPassword);
						if (jObject != null) {
							if (jObject.has("collection")) {
								JSONObject jObjCollection = jObject
										.getJSONObject("collection");
								if (jObjCollection.has("id")) {
									passwordId = jObjCollection.getString("id");
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				PBPreferenceUtils.saveStringPref(getBaseContext(),
						PBConstant.UPLOAD_SERVICE_PREF,
						PBConstant.PREF_COLLECTION_ID, passwordId);
				PBPreferenceUtils.saveStringPref(getBaseContext(),
						PBConstant.UPLOAD_SERVICE_PREF,
						PBConstant.PREF_UPLOAD_PASS, mPassword);
				PBPreferenceUtils.saveStringPref(getBaseContext(),
						PBConstant.UPLOAD_SERVICE_PREF,
						PBConstant.PREF_PUBLIC_COLLECTION_ID, passwordId);

				makePasswordToPublic(mPassword, passwordId, mSelectedMediaList,
						mSelectedMediaTypeList);
			}
		}
	}

	private void makePasswordToPublic(String password, String passwordId,
			ArrayList<String> selectedImages, long[] selectedTypeImages) {

		// If password set successfully then go for publish password
		new SetPasswordToPublic().execute();
	}

	private static int mResponseError;
	private Response mResponse;

	private class SetPasswordToPublic extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			mResponse = PBAPIHelper.setPasswordToPublic(mPassword,
					mCollectionId, mToken);
			if (mResponse != null) {
				mResponseError = mResponse.errorCode;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// go to uploading screen
			super.onPostExecute(result);

			if (mResponseError == ResponseHandle.CODE_200_OK) {
				// Show complete dialog
				showFinishPublishDialog();
			}

		}
	}

	private void showFinishPublishDialog() {
		mCompleteDlgForSetPasswordToPublic = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.popup_theme))
				.setTitle(
						getString(R.string.pb_upload_koukaibukuro_dialog_publsh_passwd_done_title))
				.setMessage(
						getString(R.string.pb_upload_koukaibukuro_dialog_publsh_passwd_done_message))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.dialog_ok_btn),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Finalize the uploading task
								(new SetAddibilityTaskForKoukaibukuro())
										.execute(false);

							}
						}).create();

		mCompleteDlgForSetPasswordToPublic.show();
	}

	private class SetAddibilityTaskForKoukaibukuro extends
			AsyncTask<Boolean, Void, Void> {

		private Boolean mAddibility;

		@Override
		protected Void doInBackground(Boolean... addibilities) {

			{
				boolean checkSdCard = PBGeneralUtils.checkSdcard(
						getBaseContext(), true,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						});
				if (!checkSdCard) {
					UploadingActivity.cancelUpload(getBaseContext(),
							mUploadedUrlList);
					finish();
					return null;
				}
			}

			mAddibility = addibilities.length > 0 ? addibilities[0] : false;
			PBPreferenceUtils.saveIntPref(getBaseContext(),
					PBConstant.UPLOAD_SERVICE_PREF, PBConstant.PREF_ADDIBILITY,
					mAddibility ? 1 : 0);

			PBPreferenceUtils.saveIntPref(getBaseContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_IS_UPDATABLE, 0);
			PBPreferenceUtils.saveBoolPref(getBaseContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_INPUT_SEQUENCE_FINISH, true);

			// check photos uploading finish ?
			if (PBPreferenceUtils.getBoolPref(getBaseContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
					PBConstant.PREF_UPLOAD_FINISH, false)) {
				// upload service has finish, call UploadFinish API, save
				// database
				// and start PBConfirmPassword activity
				Response respond = PBAPIHelper.finishUploading(mCollectionId,
						mToken, mAddibility);
				if (respond.errorCode != ResponseHandle.CODE_200_OK) {
					// error when finish upload
					// 20120229 @lent5 fixed bug save thumb not completed
					PBPreferenceUtils.saveBoolPref(getApplicationContext(),
							PBConstant.PREF_NAME,
							PBConstant.PREF_PASSWORD_FROM_LIBRARY, true);
					startUploadingActivity(mPassword, mCollectionId,
							mSelectedMediaList, mSelectedMediaTypeList);
				} else {// everything is ok
					long chargeDate = 0;
					if (!TextUtils.isEmpty(respond.decription)
							&& respond.decription.contains("collection")) {
						try {
							JSONObject jObject = new JSONObject(
									respond.decription);
							if (jObject != null && jObject.has("collection")) {
								JSONObject jObjCollection = jObject
										.getJSONObject("collection");
								if (jObjCollection != null
										&& jObjCollection.has("charges_at")) {
									chargeDate = jObjCollection
											.getLong("charges_at");
									System.out
											.println("Atik upload charge date from koukaibukuro:"
													+ chargeDate);
									PBPreferenceUtils.saveLongPref(
											getBaseContext(),
											PBConstant.UPLOAD_SERVICE_PREF,
											PBConstant.PREF_CHARGE_DATE,
											chargeDate);
								}
								if (jObjCollection != null
										&& jObjCollection.has("updated_at")) {
									PBPreferenceUtils.saveLongPref(
											getBaseContext(),
											PBConstant.UPLOAD_SERVICE_PREF,
											PBConstant.PREF_UPDATED_AT,
											jObjCollection
													.getLong("updated_at"));
								}
								if (jObjCollection != null
										&& jObjCollection.has("can_add")) {
									PBPreferenceUtils.saveIntPref(
											getBaseContext(),
											PBConstant.UPLOAD_SERVICE_PREF,
											PBConstant.PREF_ADDIBILITY,
											Integer.parseInt(jObjCollection
													.getString("can_add")));
								}
								if (jObjCollection != null
										&& jObjCollection.has("can_save")) {
									PBPreferenceUtils.saveIntPref(
											getBaseContext(),
											PBConstant.UPLOAD_SERVICE_PREF,
											PBConstant.PREF_SAVEMARK,
											Integer.parseInt(jObjCollection
													.getString("can_save")));
								}
								if (jObjCollection != null
										&& jObjCollection
												.has("client_keep_days")) {
									PBPreferenceUtils
											.saveIntPref(
													getBaseContext(),
													PBConstant.UPLOAD_SERVICE_PREF,
													PBConstant.PREF_SAVEDAYS,
													Integer.parseInt(jObjCollection
															.getString("client_keep_days")));
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					final long chargeDateTmp = chargeDate;
					SaveDBTask saveDatabase = new SaveDBTask(
							PBMainTabBarActivity.sMainContext, mSelectedMediaList,
							mUploadedUrlList, new ServiceUpdateUIListener() {
								@Override
								public void updateUI(int uploadedPhotos,
										ArrayList<MediaInfo> uploadedUrlPhoto) {
									if (uploadedPhotos == PBConstant.UPLOAD_FINISH_COMPLETED) {
										startConfirmUploadActivity(mPassword,
												chargeDateTmp);
										finish();
									}
								}

								@Override
								public void updateProgressBar(int percent) {
									// not implement!
								}

								@Override
								public void updateCompressBar(int totalFile,
										int percent) {
								}

								@Override
								public void onUploadForbidden() {

								}

								@Override
								public void updateAnalytics(String data) {
									// TODO Auto-generated method stub
									easyTracker
											.send(MapBuilder
													.createEvent(
															"ScreenName-> PBUploadKoukaibukuroActivity",
															"VideoCompression",
															data, null).build());

								}
							});
					saveDatabase.execute();

					// stop upload service
					// 20120229 @lent5 fixed bug save thumb not completed
					Intent serviceIntent = new Intent(getBaseContext(),
							UploadService.class);
					stopService(serviceIntent);
					// finish();
				}
			} else { // upload service has not finish, start uploading activity
				// 20120229 @lent5 fixed bug save thumb not completed
				startUploadingActivity(mPassword, mCollectionId,
						mSelectedMediaList,
						mSelectedMediaTypeList);
			}

			return null;
		}

		private void startConfirmUploadActivity(String password, long chargeDate) {
			Bundle extras = new Bundle();
			extras.putString(PBConstant.COLLECTION_PAGE_NAME,
					UploadingActivity.class.getName());
			extras.putString(PBConstant.COLLECTION_ID, PBPreferenceUtils
					.getStringPref(getBaseContext(),
							PBConstant.UPLOAD_SERVICE_PREF,
							PBConstant.PREF_COLLECTION_ID, ""));
			extras.putString(PBConstant.COLLECTION_PASSWORD, password/* mPassword */);
			extras.putLong(PBConstant.COLLECTION_CHARGE_AT, chargeDate);
			extras.putString(PBConstant.COLLECTION_THUMB, PBPreferenceUtils
					.getStringPref(getBaseContext(),
							PBConstant.UPLOAD_SERVICE_PREF,
							PBConstant.PREF_COLLECTION_THUMB, ""));
			Intent intent = new Intent(getBaseContext(),
					PBConfirmPasswordActivity.class);
			intent.putExtra("data", extras);
			intent.putExtra(PBConstant.IS_OWNER, true);
			startActivityForResult(intent,
					PBConstant.REQUEST_CODE_OPEN_CONFIRMPASS);
			Log.d("TinhNH1", "PBUpload Confirm: startConfirmUploadActivity");
		}

		private void startUploadingActivity(String password, String passwordId,
				ArrayList<String> selectedImages, long[] selectedTypeImages) {
			Intent intent = new Intent(UploadSetPasswordActivity.this,
					UploadingActivity.class);
			intent.putExtra(PBConstant.INTENT_PASSWORD, password/* mPassword */);
			intent.putExtra(PBConstant.INTENT_PASSWORD_ID, passwordId/* passwordId */);
			intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA, selectedImages/* mSelectedImages */);
			intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE,
					selectedTypeImages/* mSelectedImages */);
			startActivity(intent);
			finish();
		}

		@Override
		protected void onPostExecute(Void result) {
			// mAddibilityYesBtn.setEnabled(true);
			// mAddibilityNoBtn.setEnabled(true);
			// mLayoutWaiting.setVisibility(View.GONE);
		}
	}

	private void showFourDigitScreen() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getBaseContext(),
				PBUploadConfirmActivity.class);
		intent.putStringArrayListExtra(PBConstant.INTENT_SELECTED_MEDIA,
				mSelectedMediaList);
		intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE,
				mSelectedMediaTypeList);
		intent.putExtra(PBConstant.INTENT_LONG_PASSWORD, mLongPassword);
		intent.putExtra(PBConstant.INTENT_PASSWORD, mPasswordInput.getText()
				.toString());
		startActivity(intent);
		finish();

	}


}
