package com.aircast.photobag.openid;
//package com.kayac.photobag.openid;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.ContextThemeWrapper;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.facebook.FacebookAuthorizationException;
//import com.facebook.FacebookException;
//import com.facebook.FacebookOperationCanceledException;
//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.UiLifecycleHelper;
//import com.facebook.model.GraphUser;
//import com.facebook.widget.LoginButton;
//import com.facebook.widget.LoginButton.OnErrorListener;
//import com.facebook.widget.LoginButton.UserInfoChangedCallback;
//import com.google.analytics.tracking.android.EasyTracker;
//import com.google.analytics.tracking.android.MapBuilder;
//import com.kayac.photobag.R;
//import com.kayac.photobag.activity.PBAbsActionBarActivity;
//import com.kayac.photobag.activity.PBConfirmPasswordActivity;
//import com.kayac.photobag.activity.PBMainTabBarActivity;
//import com.kayac.photobag.activity.PBUploadConfirmActivity;
//import com.kayac.photobag.activity.PBUploadKoukaibukuroActivity;
//import com.kayac.photobag.activity.UploadingActivity;
//import com.kayac.photobag.api.PBAPIContant;
//import com.kayac.photobag.api.PBAPIHelper;
//import com.kayac.photobag.api.ResponseHandle;
//import com.kayac.photobag.api.ResponseHandle.Response;
//import com.kayac.photobag.application.PBApplication;
//import com.kayac.photobag.application.PBConstant;
//import com.kayac.photobag.services.UploadService.MediaInfo;
//import com.kayac.photobag.services.UploadService.ServiceUpdateUIListener;
//import com.kayac.photobag.services.SaveDBTask;
//import com.kayac.photobag.services.UploadService;
//import com.kayac.photobag.services.UploadServiceUtils;
//import com.kayac.photobag.utils.PBGeneralUtils;
//import com.kayac.photobag.utils.PBPreferenceUtils;
//import com.kayac.photobag.widget.actionbar.ActionBar;
//
//public class OpenIdHomeActivity extends PBAbsActionBarActivity implements
//		OnClickListener {
//	private final String TAG = "OpenIdHomeActivity";
//	ArrayList<MediaInfo> mUploadedUrlList;
//	private ArrayList<String> mSelectedMedia;
//	private long[] mSelectedTypeMedia;
//	private String mPassword, mLongPas;
//	private boolean mIsActivityDestroyed = false;
//	private EasyTracker easyTracker = null;
//	private LoginButton loginButton;
//	private UiLifecycleHelper uiHelper;
//	public static AlertDialog mConfirmationForSetPasswordToPublic;
//	public static AlertDialog mCompleteDlgForSetPasswordToPublic;
//	private String mToken;
//	private String mCollectionId;
//	private LinearLayout mLvWaiting;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		
//		 uiHelper = new UiLifecycleHelper(this, statusCallback);
//	
//		         uiHelper.onCreate(savedInstanceState);
//
//		setContentView(R.layout.layout_tmp);
//	
//		easyTracker = EasyTracker.getInstance(this);
//		mIsActivityDestroyed = false;
//		Intent intent = getIntent();
//		mPassword = intent.getStringExtra(PBConstant.INTENT_PASSWORD);
//		mLongPas = intent.getStringExtra(PBConstant.INTENT_LONG_PASSWORD);
//		mSelectedMedia = intent
//				.getStringArrayListExtra(PBConstant.INTENT_SELECTED_MEDIA);
//		mSelectedTypeMedia = intent
//				.getLongArrayExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE);
//
//		ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
//		setHeader(headerBar,
//				getString(R.string.screen_title_confirm_pass_upload));
//		
//		TextView txtPassword = (TextView) findViewById(R.id.textView_openid_share_type_title);
//		txtPassword.setText(mPassword);		
//		
//		mLvWaiting = (LinearLayout) findViewById(R.id.ll_loading_panel_waiting);
//
//		loginButton = (LoginButton) findViewById(R.id.login_button);
//
//		loginButton.setOnErrorListener(new OnErrorListener() {
//			@Override
//			public void onError(FacebookException error) {
//				Log.d(TAG, "loginButton onError():" + error.getMessage());
//				if (error != null) {
//					if (error instanceof FacebookOperationCanceledException) {
//						Log.d(TAG, "FacebookOperationCanceledException");
//					} else if (error instanceof FacebookAuthorizationException) {
//						Log.d(TAG, "FacebookAuthorizationException");
//						new AlertDialog.Builder(
//								OpenIdHomeActivity.this)
//								.setMessage(
//										"ログイン処理でエラーが発生しました。\n公式facebookアプリにログインしている場合には、一旦ログアウトした後再度ログインしてください。")
//								.setPositiveButton("Ok", null).show();
//					} else {
//						Log.d(TAG, "other Exception");
//					}
//				}
//			}
//		});
//
//		loginButton.setReadPermissions(Arrays.asList("email"));
//		loginButton.setUserInfoChangedCallback(new UserInfoChangedCallback() {
//			@Override
//			public void onUserInfoFetched(GraphUser user) {
//				if (user != null) {
//					
//					findViewById(R.id.button_share_type_facebook).setVisibility(View.VISIBLE);
//					findViewById(R.id.login_button).setVisibility(View.GONE);
//					//username.setText("You are currently logged in as " + user.getName());
//					
//
//						try {
//							String	userEmail = user.getInnerJSONObject().getString("email");
//							
//							PBPreferenceUtils.saveStringPref(getApplicationContext(), PBConstant.PREF_NAME, PBConstant.PREF_OPENID_FACEBOOK,userEmail);
//							if(PBAPIContant.DEBUG){
//							Log.d(TAG, userEmail);
//						}
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
////						PBPreferenceUtils.saveStringPref(
////								getApplicationContext(), "FBDATA",
////								"response", response.toString());
//						
//				
//				} else {
//					
//					findViewById(R.id.login_button).setVisibility(View.VISIBLE);
//					findViewById(R.id.button_share_type_facebook).setVisibility(View.GONE);
//					//username.setText("You are not logged in.");
//				}
//			}
//		});
//		//loginButton.setOnClickListener(this);
//		findViewById(R.id.button_share_type_mail).setOnClickListener(this);
//		findViewById(R.id.button_share_type_facebook).setOnClickListener(this);
//
//		// set listener to tracking photo uploading
//		UploadService.setUpdateListener(new ServiceUpdateUIListener() {
//			@Override
//			public void updateUI(int uploadedPhotos,
//					ArrayList<MediaInfo> uploadedUrlPhoto) {
//				mUploadedUrlList = uploadedUrlPhoto;
//			}
//
//			@Override
//			public void updateProgressBar(int percent) {
//				// no need to implement!
//			}
//
//			@Override
//			public void updateCompressBar(int totalFile, int percent) {
//			}
//
//			@Override
//			public void onUploadForbidden() {
//
//			}
//
//			@Override
//			public void updateAnalytics(String data) {
//				// TODO Auto-generated method stub
//				Log.d("setpassword", data);
//
//				easyTracker.send(MapBuilder.createEvent(
//						"ScreenName-> OpenIdHomeActivity", "VideoCompression",
//						data, null).build());
//			}
//		});
//
//	}
//	
//	
//	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
//		@Override
//		public void call(Session session, SessionState state,
//				Exception exception) {
//			if (state.isOpened()) {
//				Log.d("MainActivity", "Facebook session opened.");
//			} else if (state.isClosed()) {
//				Log.d("MainActivity", "Facebook session closed.");
//			}
//		}
//	};
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		mIsActivityDestroyed = false;
//		mLvWaiting.setVisibility(View.GONE);
//		uiHelper.onResume();
//		UploadServiceUtils.checkInternetConenction(OpenIdHomeActivity.this,
//				(!mIsActivityDestroyed), true);
//	}
//	
//	@Override
//	public void onPause() {
//		super.onPause();
//		uiHelper.onPause();
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.button_share_type_mail:
//			String shareIdForMail = PBPreferenceUtils.getStringPref(getApplicationContext(), PBConstant.PREF_NAME, PBConstant.PREF_OPENID_MAIL, "");
//			
//			
//			if(!shareIdForMail.equals("")){
//				
//				boolean hasInternetForFacebook = PBApplication
//						.hasNetworkConnection();
//				if (hasInternetForFacebook) { // Check Internet Connectivity
//
//					shareOpenPassword();
//
//				} else {
//
//					Toast.makeText(
//							OpenIdHomeActivity.this,
//							getString(R.string.pb_network_not_available_general_message),
//							Toast.LENGTH_SHORT).show();
//
//				
//
//				}
//			}else{
//			PBPreferenceUtils.saveStringPref(getApplicationContext(), PBConstant.PREF_NAME, PBConstant.PREF_OPENID_SHARE_TYPE, "mail");
//			PBPreferenceUtils.saveStringPref(getApplicationContext(), PBConstant.PREF_NAME, PBConstant.PREF_OPENID_SHAREID,shareIdForMail );
//			
//			Intent intentMail = new Intent(OpenIdHomeActivity.this,
//					PBOpenIdMailActivity.class);
//			intentMail.putStringArrayListExtra(PBConstant.INTENT_SELECTED_MEDIA,
//					mSelectedMedia);
//			intentMail.putExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE,
//					mSelectedTypeMedia);
//			intentMail.putExtra(PBConstant.INTENT_LONG_PASSWORD, mLongPas);
//			intentMail.putExtra(PBConstant.INTENT_PASSWORD, mPassword);
//			startActivity(intentMail);
//			finish();
//			
//			}
//			break;
//
//		case R.id.button_share_type_facebook:
//			
//			String shareIdForFaceBook = PBPreferenceUtils.getStringPref(getApplicationContext(), PBConstant.PREF_NAME, PBConstant.PREF_OPENID_FACEBOOK, "");
//			
//			PBPreferenceUtils.saveStringPref(getApplicationContext(), PBConstant.PREF_NAME, PBConstant.PREF_OPENID_SHARE_TYPE, "facebook");
//			
//			PBPreferenceUtils.saveStringPref(getApplicationContext(), PBConstant.PREF_NAME, PBConstant.PREF_OPENID_SHAREID,shareIdForFaceBook);
//
//			boolean hasInternetForFacebook = PBApplication
//					.hasNetworkConnection();
//			if (hasInternetForFacebook) { // Check Internet Connectivity
//
//				shareOpenPassword();
//
//			} else {
//
//				Toast.makeText(
//						OpenIdHomeActivity.this,
//						getString(R.string.pb_network_not_available_general_message),
//						Toast.LENGTH_SHORT).show();
//
//			
//
//			}
//			break;
//		case R.id.button_gotoFourDigit:
//
//			Intent intentSecretDigit = new Intent(OpenIdHomeActivity.this,
//					PBUploadConfirmActivity.class);
//			intentSecretDigit.putStringArrayListExtra(PBConstant.INTENT_SELECTED_MEDIA,
//					mSelectedMedia);
//			intentSecretDigit.putExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE,
//					mSelectedTypeMedia);
//			intentSecretDigit.putExtra(PBConstant.INTENT_LONG_PASSWORD, mLongPas);
//			intentSecretDigit.putExtra(PBConstant.INTENT_PASSWORD, mPassword);
//			startActivity(intentSecretDigit);
//			finish();
//			break;
//
//		default:
//			break;
//		}
//	}
//	
//	
//	private void shareOpenPassword(){
//		
//		if (mConfirmationForSetPasswordToPublic == null) {
//			mConfirmationForSetPasswordToPublic = new AlertDialog.Builder(
//					new ContextThemeWrapper(this, R.style.popup_theme))
//					.setTitle(
//							getString(R.string.pb_upload_koukaibukuro_dialog_publsh_passwd_confirmation_title))
//					.setMessage(
//							getString(R.string.pb_upload_koukaibukuro_dialog_publsh_passwd_confirmation_message))
//					.setCancelable(false)
//					.setPositiveButton(getString(R.string.dialog_ok_btn),
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// requestAuthorization();
//									dialog.dismiss();
//
//									// DO server API call to do make
//
//									// mConfirmPassBtn.setEnabled(false);
//
//									boolean connected = UploadServiceUtils
//											.checkInternetConenction(
//													OpenIdHomeActivity.this,
//													(!mIsActivityDestroyed),
//													false);
//									if (connected) {
//										
//										mLvWaiting.setVisibility(View.VISIBLE);
//										mCollectionId = PBPreferenceUtils
//												.getStringPref(
//														getBaseContext(),
//														PBConstant.UPLOAD_SERVICE_PREF,
//														PBConstant.PREF_COLLECTION_ID,
//														"");
//										mToken = PBPreferenceUtils
//												.getStringPref(
//														getBaseContext(),
//														PBConstant.PREF_NAME,
//														PBConstant.PREF_NAME_TOKEN,
//														"");
//										// new
//										// SetPasswordToPublic().execute();
//										new SetPasswordTask().execute();
//
//									} else {
//										mLvWaiting.setVisibility(View.GONE);
//										// mConfirmPassBtn.setEnabled(true);
//									}
//
//									// showFinishPublishDialog();
//
//								}
//							})
//					.setNegativeButton(
//							getString(R.string.dialog_cancel_btn),
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//
//									dialog.dismiss();
//
//								}
//							}).create();
//
//			mConfirmationForSetPasswordToPublic.show();
//			mConfirmationForSetPasswordToPublic = null;
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		mIsActivityDestroyed = true;
//		uiHelper.onDestroy();
//	}
//
//	@Override
//	public void onStart() {
//
//		super.onStart();
//		System.out.println("Atik start Easy Tracker");
//		EasyTracker.getInstance(this).activityStart(this);
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//		mIsActivityDestroyed = true;
//		System.out.println("Atik stop Easy Tracker");
//		EasyTracker.getInstance(this).activityStop(this);
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		uiHelper.onActivityResult(requestCode, resultCode, data);
//	}
//
//	@Override
//	public void onSaveInstanceState(Bundle savedState) {
//		super.onSaveInstanceState(savedState);
//		uiHelper.onSaveInstanceState(savedState);
//	}
//	@Override
//	protected void handleHomeActionListener() {
//
//		stopUploadService();
//		finish();
//
//	}
//
//	/**
//	 * Stop the upload service that has been started in select photo activity
//	 */
//	private void stopUploadService() {
//		// 20120225 @lent fix issue key back during uploading
//		UploadingActivity.cancelUpload(OpenIdHomeActivity.this,
//				mUploadedUrlList);
//	}
//
//	// @lent5 add action bar #E
//
//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//		stopUploadService();
//	}
//
//	@Override
//	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
//		// TODO Auto-generated method stub
//
//	}
//	
//	
//	private static String mResponseDescriptionSetPassword;
//	private static int mResponseErrorSetPassword;
//	private Response mResponseSetPassword;
//
//	private class SetPasswordTask extends AsyncTask<String, Void, Void> {
//
//		@Override
//		protected Void doInBackground(String... params) {
//			mResponseSetPassword = PBAPIHelper.setPassword(mPassword,
//					mCollectionId, mToken, false, "");
//			if (mResponseSetPassword != null) {
//				mResponseDescriptionSetPassword = mResponseSetPassword.decription;
//				mResponseErrorSetPassword = mResponseSetPassword.errorCode;
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			// go to uploading screen
//			super.onPostExecute(result);
//			// @lent add progress bar
//			// mLvWaiting.setVisibility(View.GONE);
//			// mConfirmPassBtn.setEnabled(true);
//
//			if (mResponseErrorSetPassword != ResponseHandle.CODE_200_OK) { // Handle
//																			// error
//																			// here
//				Log.d(TAG, "[set password] " + mResponseDescriptionSetPassword);
//				String errorNotifyMes = ResponseHandle
//						.getNotifyStringErrorRelatedPassword(
//								mResponseSetPassword.errorCode,
//								mResponseSetPassword.decription);
//
//				// mMakeLongPassBtn.setEnabled(false);
//				if (!mIsActivityDestroyed) {
//					PBGeneralUtils.showAlertDialogActionWithOnClick(
//							OpenIdHomeActivity.this,
//							android.R.drawable.ic_dialog_alert,
//							getString(R.string.dialog_error_title),
//							errorNotifyMes,
//							getString(R.string.upload_confirm_pass_ok_btn),
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									dialog.dismiss();
//								}
//							});
//				}
//			} else { // set password successful
//				String passwordId = null;
//				JSONObject jObject;
//				if (!TextUtils.isEmpty(mResponseDescriptionSetPassword)
//						&& mResponseDescriptionSetPassword
//								.contains("collection")) {
//					try {
//						jObject = new JSONObject(
//								mResponseDescriptionSetPassword);
//						if (jObject != null) {
//							if (jObject.has("collection")) {
//								JSONObject jObjCollection = jObject
//										.getJSONObject("collection");
//								if (jObjCollection.has("id")) {
//									passwordId = jObjCollection.getString("id");
//								}
//							}
//						}
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//				PBPreferenceUtils.saveStringPref(getBaseContext(),
//						PBConstant.UPLOAD_SERVICE_PREF,
//						PBConstant.PREF_COLLECTION_ID, passwordId);
//				PBPreferenceUtils.saveStringPref(getBaseContext(),
//						PBConstant.UPLOAD_SERVICE_PREF,
//						PBConstant.PREF_UPLOAD_PASS, mPassword);
//				PBPreferenceUtils.saveStringPref(getBaseContext(),
//						PBConstant.UPLOAD_SERVICE_PREF,
//						PBConstant.PREF_PUBLIC_COLLECTION_ID, passwordId);
//
//				makePasswordToPublic(mPassword, passwordId, mSelectedMedia,
//						mSelectedTypeMedia);
//			}
//		}
//	}
//
//	private void makePasswordToPublic(String password, String passwordId,
//			ArrayList<String> selectedImages, long[] selectedTypeImages) {
//
//		// If password set successfully then go for publish password
//		new SetPasswordToPublic().execute();
//	}
//	private static int mResponseError;
//	private Response mResponse;
//
//	private class SetPasswordToPublic extends AsyncTask<String, Void, Void> {
//
//		@Override
//		protected Void doInBackground(String... params) {
//			mResponse = PBAPIHelper.setPasswordToPublic(mPassword,
//					mCollectionId, mToken,PBConstant.PREF_OPENID_SHARE_TYPE,PBConstant.PREF_OPENID_SHAREID);
//			if (mResponse != null) {
//				mResponseError = mResponse.errorCode;
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			// go to uploading screen
//			super.onPostExecute(result);
//
//			if (mResponseError == ResponseHandle.CODE_200_OK) {
//				// Show complete dialog
//				showFinishPublishDialog();
//			}
//
//		}
//	}
//	
//	private void showFinishPublishDialog() {
//		mCompleteDlgForSetPasswordToPublic = new AlertDialog.Builder(
//				new ContextThemeWrapper(this, R.style.popup_theme))
//				.setTitle(
//						getString(R.string.pb_upload_koukaibukuro_dialog_publsh_passwd_done_title))
//				.setMessage(
//						getString(R.string.pb_upload_koukaibukuro_dialog_publsh_passwd_done_message))
//				.setCancelable(false)
//				.setPositiveButton(getString(R.string.dialog_ok_btn),
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								// Finalize the uploading task
//								(new SetAddibilityTaskForKoukaibukuro())
//										.execute(false);
//
//							}
//						}).create();
//
//		mCompleteDlgForSetPasswordToPublic.show();
//	}
//	private class SetAddibilityTaskForKoukaibukuro extends
//			AsyncTask<Boolean, Void, Void> {
//
//		private Boolean mAddibility;
//
//		@Override
//		protected Void doInBackground(Boolean... addibilities) {
//
//			{
//				boolean checkSdCard = PBGeneralUtils.checkSdcard(
//						getBaseContext(), true,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								finish();
//							}
//						});
//				if (!checkSdCard) {
//					UploadingActivity.cancelUpload(getBaseContext(),
//							mUploadedUrlList);
//					finish();
//					return null;
//				}
//			}
//
//			mAddibility = addibilities.length > 0 ? addibilities[0] : false;
//			PBPreferenceUtils.saveIntPref(getBaseContext(),
//					PBConstant.UPLOAD_SERVICE_PREF, PBConstant.PREF_ADDIBILITY,
//					mAddibility ? 1 : 0);
//
//			PBPreferenceUtils.saveIntPref(getBaseContext(),
//					PBConstant.UPLOAD_SERVICE_PREF,
//					PBConstant.PREF_IS_UPDATABLE, 0);
//			PBPreferenceUtils.saveBoolPref(getBaseContext(),
//					PBConstant.UPLOAD_SERVICE_PREF,
//					PBConstant.PREF_INPUT_SEQUENCE_FINISH, true);
//
//			// check photos uploading finish ?
//			if (PBPreferenceUtils.getBoolPref(getBaseContext(),
//					PBConstant.UPLOAD_SERVICE_PREF,
//					PBConstant.PREF_UPLOAD_FINISH, false)) {
//				// upload service has finish, call UploadFinish API, save
//				// database
//				// and start PBConfirmPassword activity
//				Response respond = PBAPIHelper.finishUploading(mCollectionId,
//						mToken, mAddibility);
//				if (respond.errorCode != ResponseHandle.CODE_200_OK) {
//					// error when finish upload
//					// 20120229 @lent5 fixed bug save thumb not completed
//					PBPreferenceUtils.saveBoolPref(
//							getApplicationContext(),
//							PBConstant.PREF_NAME,
//							PBConstant.PREF_PASSWORD_FROM_LIBRARY, true);
//					startUploadingActivity(mPassword, mCollectionId,
//							mSelectedMedia, mSelectedTypeMedia);
//				} else {// everything is ok
//					long chargeDate = 0;
//					if (!TextUtils.isEmpty(respond.decription)
//							&& respond.decription.contains("collection")) {
//						try {
//							JSONObject jObject = new JSONObject(
//									respond.decription);
//							if (jObject != null && jObject.has("collection")) {
//								JSONObject jObjCollection = jObject
//										.getJSONObject("collection");
//								if (jObjCollection != null
//										&& jObjCollection.has("charges_at")) {
//									chargeDate = jObjCollection
//											.getLong("charges_at");
//									System.out
//											.println("Atik upload charge date from koukaibukuro:"
//													+ chargeDate);
//									PBPreferenceUtils.saveLongPref(
//											getBaseContext(),
//											PBConstant.UPLOAD_SERVICE_PREF,
//											PBConstant.PREF_CHARGE_DATE,
//											chargeDate);
//								}
//								if (jObjCollection != null
//										&& jObjCollection.has("updated_at")) {
//									PBPreferenceUtils.saveLongPref(
//											getBaseContext(),
//											PBConstant.UPLOAD_SERVICE_PREF,
//											PBConstant.PREF_UPDATED_AT,
//											jObjCollection
//													.getLong("updated_at"));
//								}
//								if (jObjCollection != null
//										&& jObjCollection.has("can_add")) {
//									PBPreferenceUtils.saveIntPref(
//											getBaseContext(),
//											PBConstant.UPLOAD_SERVICE_PREF,
//											PBConstant.PREF_ADDIBILITY,
//											Integer.parseInt(jObjCollection
//													.getString("can_add")));
//								}
//								if (jObjCollection != null
//										&& jObjCollection.has("can_save")) {
//									PBPreferenceUtils.saveIntPref(
//											getBaseContext(),
//											PBConstant.UPLOAD_SERVICE_PREF,
//											PBConstant.PREF_SAVEMARK,
//											Integer.parseInt(jObjCollection
//													.getString("can_save")));
//								}
//								if (jObjCollection != null
//										&& jObjCollection
//												.has("client_keep_days")) {
//									PBPreferenceUtils
//											.saveIntPref(
//													getBaseContext(),
//													PBConstant.UPLOAD_SERVICE_PREF,
//													PBConstant.PREF_SAVEDAYS,
//													Integer.parseInt(jObjCollection
//															.getString("client_keep_days")));
//								}
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//
//					final long chargeDateTmp = chargeDate;
//					SaveDBTask saveDatabase = new SaveDBTask(
//							PBMainTabBarActivity.sMainContext, mSelectedMedia,
//							mUploadedUrlList, new ServiceUpdateUIListener() {
//								@Override
//								public void updateUI(int uploadedPhotos,
//										ArrayList<MediaInfo> uploadedUrlPhoto) {
//									if (uploadedPhotos == PBConstant.UPLOAD_FINISH_COMPLETED) {
//										startConfirmUploadActivity(mPassword,
//												chargeDateTmp);
//										finish();
//									}
//								}
//
//								@Override
//								public void updateProgressBar(int percent) {
//									// not implement!
//								}
//
//								@Override
//								public void updateCompressBar(int totalFile,
//										int percent) {
//								}
//
//								@Override
//								public void onUploadForbidden() {
//
//								}
//
//								@Override
//								public void updateAnalytics(String data) {
//									// TODO Auto-generated method stub
//									easyTracker
//											.send(MapBuilder
//													.createEvent(
//															"ScreenName-> PBUploadKoukaibukuroActivity",
//															"VideoCompression",
//															data, null).build());
//
//								}
//							});
//					saveDatabase.execute();
//
//					// stop upload service
//					// 20120229 @lent5 fixed bug save thumb not completed
//					Intent serviceIntent = new Intent(getBaseContext(),
//							UploadService.class);
//					stopService(serviceIntent);
//					// finish();
//				}
//			} else { // upload service has not finish, start uploading activity
//				// 20120229 @lent5 fixed bug save thumb not completed
//				startUploadingActivity(mPassword, mCollectionId,
//						mSelectedMedia, mSelectedTypeMedia);
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			// mAddibilityYesBtn.setEnabled(true);
//			// mAddibilityNoBtn.setEnabled(true);
//			// mLayoutWaiting.setVisibility(View.GONE);
//		}
//	}
//
//	private void startUploadingActivity(String password, String passwordId,
//			ArrayList<String> selectedImages, long[] selectedTypeImages) {
//		Intent intent = new Intent(OpenIdHomeActivity.this,
//				UploadingActivity.class);
//		intent.putExtra(PBConstant.INTENT_PASSWORD, password/* mPassword */);
//		intent.putExtra(PBConstant.INTENT_PASSWORD_ID, passwordId/* passwordId */);
//		intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA, selectedImages/* mSelectedImages */);
//		intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE,
//				selectedTypeImages/* mSelectedImages */);
//		startActivity(intent);
//		finish();
//	}
//
//	private void startConfirmUploadActivity(String password, long chargeDate) {
//		Bundle extras = new Bundle();
//		extras.putString(PBConstant.COLLECTION_PAGE_NAME,
//				UploadingActivity.class.getName());
//		extras.putString(PBConstant.COLLECTION_ID, PBPreferenceUtils
//				.getStringPref(getBaseContext(),
//						PBConstant.UPLOAD_SERVICE_PREF,
//						PBConstant.PREF_COLLECTION_ID, ""));
//		extras.putString(PBConstant.COLLECTION_PASSWORD, password/* mPassword */);
//		extras.putLong(PBConstant.COLLECTION_CHARGE_AT, chargeDate);
//		extras.putString(PBConstant.COLLECTION_THUMB, PBPreferenceUtils
//				.getStringPref(getBaseContext(),
//						PBConstant.UPLOAD_SERVICE_PREF,
//						PBConstant.PREF_COLLECTION_THUMB, ""));
//		Intent intent = new Intent(getBaseContext(),
//				PBConfirmPasswordActivity.class);
//		intent.putExtra("data", extras);
//		intent.putExtra(PBConstant.IS_OWNER, true);
//		startActivityForResult(intent, PBConstant.REQUEST_CODE_OPEN_CONFIRMPASS);
//		Log.d("TinhNH1", "PBUpload Confirm: startConfirmUploadActivity");
//	}
//
//}
