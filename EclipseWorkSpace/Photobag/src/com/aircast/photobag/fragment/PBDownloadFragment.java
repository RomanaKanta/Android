package com.aircast.photobag.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.koukaibukuro.MainTabActivity;
import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBAcornForestActivity;
import com.aircast.photobag.activity.PBDisplayCompleteActivity;
import com.aircast.photobag.activity.PBDownloadConfirmActivity;
import com.aircast.photobag.activity.PBDownloadDownloadingActivity;
import com.aircast.photobag.activity.PBDownloadPurchaseActivity;
import com.aircast.photobag.activity.PBDownloadSecretCodeActivity;
import com.aircast.photobag.activity.PBDownloadWrongPassActivity;
import com.aircast.photobag.activity.PBMainTabBarActivity;
import com.aircast.photobag.activity.PBOpenPageWebViewActivity;
import com.aircast.photobag.activity.PBWebViewPhotoContestActivity;
import com.aircast.photobag.api.HttpUtils;
import com.aircast.photobag.api.MapEntry;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.gcm.GCMConstant;
import com.aircast.photobag.model.PBTimelineHistoryModel;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.PBCustomWaitingProgress;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gcm.GCMRegistrar;

/**
 * Show input password page.
 * */
@SuppressWarnings("deprecation")
public class PBDownloadFragment extends Fragment {

	// 1 back from download complete; 2: change to history tab
	private static final String TAG = "PBDownloadMainActivity";
	public static int sDownloadComplete = 0;
	private View view;
	private LinearLayout mDownloadAD;
	private LinearLayout mDownloadMain;
	private LinearLayout mDownloadTransferJet;
	private EditText mEdtPwdToCheck;
	private WebView myWebView;
	private final static int MSG_CHECK_PWD_DONE = 1000;
	private final static int MSG_SHOW_PROGRESS_BAR = 1001;
	private final static int MSG_SHOW_NOT_FOUND_OR_PURCHASE_SCREEN = 1002;
	private final static int MSG_SHOW_UNFINISH = 1004;
	private final static int MSG_SHOW_EXCEEDED_SCREEN = 1003;



	private boolean isRegisteredReceiver = false;

	private static String message_response_check_lock_status = "";
	private static String titte_after_start_migration = "";

	private static String resultCode = "";
	private boolean isDeviceLock = false;
	private boolean isFromMori = false;
	IntentFilter intentFilter;

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

	private WebView webview;
	private WebView webviewAdd;
	private String webviewDownloadScreenURL;

	public static boolean isKoukaibukuroDisabled = false;

	public static PBDownloadFragment createInstance() {
		return new PBDownloadFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.pb_layout_download_input_pwd,
				container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		this.view = view;
		// and here our view is loaded

		new GetStatusOfKoukaibukuroWhenApplicationStart().execute(); // run to
																		// get
																		// koukaibukuro
																		// status

		// Disable scrolling of webview
		webviewAdd = (WebView) view.findViewById(R.id.webview);
		webviewAdd.setVerticalScrollBarEnabled(false);
		webviewAdd.setHorizontalScrollBarEnabled(false);
		webviewAdd.getSettings().setLayoutAlgorithm(
				LayoutAlgorithm.SINGLE_COLUMN);

		webviewAdd.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return (event.getAction() == MotionEvent.ACTION_MOVE);
			}
		});

		boolean hasInternet = PBApplication.hasNetworkConnection();
		if (hasInternet) {
			System.out.println("Atik download koukaibukuro status:"
					+ PBDownloadFragment.isKoukaibukuroDisabled);
			if (!isKoukaibukuroDisabled) {
				webview = (WebView) view
						.findViewById(R.id.webview_download_screen_for_kuma);
				try {
					String versionName = getActivity().getPackageManager()
							.getPackageInfo(
									getActivity().getApplicationContext()
											.getPackageName(), 0).versionName;
					webviewDownloadScreenURL = PBAPIContant.PB_DOWNLOAD_WEBVIEW_URL
							+ versionName;
					System.out.println("Atik download webview URL is:"
							+ webviewDownloadScreenURL);
					webview.loadUrl(webviewDownloadScreenURL);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				loadOpenPage(webview);
			} else {
				view.findViewById(R.id.webview_download_screen_layout_for_kuma)
						.setVisibility(View.GONE);
				view.findViewById(R.id.view_kuma_no_internet_download)
						.setVisibility(View.VISIBLE);
			}

		} else {

			view.findViewById(R.id.webview_download_screen_layout_for_kuma)
					.setVisibility(View.GONE);
			view.findViewById(R.id.view_kuma_no_internet_download)
					.setVisibility(View.VISIBLE);
		}

		TextView mBtnDlInputReceiveImg1 = (TextView) view
				.findViewById(R.id.btn_dl_input_get_img1);
		Spannable buttonLabel = new SpannableString("  "
				+ getString(R.string.dl_btn_input_pwd_text));
		Drawable drawable = getResources().getDrawable(R.drawable.btn_ico_dl);
		int intrinsicHeightWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, (float) 20, getResources()
						.getDisplayMetrics()); // convert 20 dip to int

		drawable.setBounds(0, 0, intrinsicHeightWidth, intrinsicHeightWidth);
		ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);

		AlignmentSpan.Standard center_span = new AlignmentSpan.Standard(
				Layout.Alignment.ALIGN_CENTER);
		buttonLabel.setSpan(center_span, 0, 1,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		buttonLabel
				.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mBtnDlInputReceiveImg1.setText(buttonLabel);

		TextView mBtnDlInputReceiveImg2 = (TextView) view
				.findViewById(R.id.btn_dl_input_get_img2);

		mBtnDlInputReceiveImg2.setText(buttonLabel);

		mBtnDlInputReceiveImg1.setOnClickListener(mOnClickListener);
		mBtnDlInputReceiveImg2.setOnClickListener(mOnClickListener);

		mDownloadAD = (LinearLayout) view.findViewById(R.id.layout_download_ad);

		mDownloadMain = (LinearLayout) view
				.findViewById(R.id.layout_download_main);
		mDownloadMain.setOnClickListener(mOnClickListener);

		// Transfer Jet Button Adding Here
		mDownloadTransferJet = (LinearLayout) view
				.findViewById(R.id.btn_input_get_img_transferJet);
		mDownloadTransferJet.setOnClickListener(mOnClickListener);
		mDownloadTransferJet.setVisibility(View.GONE); // TODO hiding this
														// button for instance

		mEdtPwdToCheck = (EditText) view.findViewById(R.id.edt_dl_pwd_get_img);
		mEdtPwdToCheck.setOnClickListener(mOnClickListener);
		//mEdtPwdToCheck.setText("test1420");
		// mShowPurchaseScreen = false;

		view.findViewById(R.id.layout_reward_forest).setOnClickListener(
				mOnClickListener);

		mNumberOfRetryCount = 1;

		PBPreferenceUtils.saveBoolPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_SETTING_INVITED_CODE,
				true);
		if (!PBPreferenceUtils.getBoolPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME,
				PBConstant.PREF_THE_FIRST_DOWNLOAD_TAB_OPEN, false)) {

			PBPreferenceUtils.saveBoolPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_THE_FIRST_DOWNLOAD_TAB_OPEN, true);

			PBGeneralUtils.showAlertDialogActionWithOnClick(getActivity(),
					getString(R.string.pb_dl_firstclick_message_title),
					getString(R.string.pb_dl_firstclick_message_body),
					getString(R.string.pb_btn_OK),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			mEdtPwdToCheck
					.setText(getString(R.string.pb_dl_firstclick_magicphrase_defaulse));
		}

		myWebView = (WebView) view.findViewById(R.id.webview);
		if (!PBApplication.hasNetworkConnection()) {
			myWebView.setVisibility(View.INVISIBLE);
		}

		String manufacturer = Build.MANUFACTURER;

		if (!manufacturer.equals(PBConstant.MANUFACTURER_AMAZON)) {
			startGCMRegistration(getActivity());
		} else {
			Log.d(PBConstant.TAG, "GCM Registration Skip for Amazon");
		}

		final LinearLayout downloadContent = (LinearLayout) view
				.findViewById(R.id.download_content);

		downloadContent.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if ((downloadContent.getRootView().getHeight() - downloadContent
								.getHeight()) > downloadContent.getRootView()
								.getHeight() / 3) {

							PBDownloadFragment.this.view.findViewById(
									R.id.btn_dl_input_get_img1).setVisibility(
									View.GONE);
							PBDownloadFragment.this.view.findViewById(
									R.id.btn_dl_input_get_img2).setVisibility(
									View.VISIBLE);
							PBDownloadFragment.this.view.findViewById(
									R.id.layout_download_ad).setVisibility(
									View.GONE);

						} else {

							PBDownloadFragment.this.view.findViewById(
									R.id.btn_dl_input_get_img1).setVisibility(
									View.VISIBLE);
							PBDownloadFragment.this.view.findViewById(
									R.id.btn_dl_input_get_img2).setVisibility(
									View.GONE);
							PBDownloadFragment.this.view.findViewById(
									R.id.layout_download_ad).setVisibility(
									View.VISIBLE);

						}
					}
				});

		// V2 google analytics has been comment out
		/*
		 * if (PBTabBarActivity.gaTracker != null) {
		 * PBTabBarActivity.gaTracker.trackPageView("PBDownloadMainActivity"); }
		 */
		if (!TextUtils.isEmpty(passwordFromUrl)) {
			mEdtPwdToCheck.setText(passwordFromUrl);
			passwordFromUrl = "";
		}
		if (sDownloadComplete != 0) {
			mEdtPwdToCheck.setText("");
		}
		if (sDownloadComplete == 2 && PBMainTabBarActivity.sMainContext != null
				&& PBMainTabBarActivity.sMainContext.mTabHost != null) {
			PBMainTabBarActivity.sMainContext.mTabHost
					.setCurrentTabByTag(PBConstant.HISTORY_TAG);
		}

		if (myWebView != null && PBApplication.hasNetworkConnection()) {
			loadPublicPassword(myWebView);
		} else {
			myWebView.setVisibility(View.GONE);
		}

		sDownloadComplete = 0;
		hideSoftKeyboard();

		mDownloadAD.setVisibility(hasInternet ? View.VISIBLE : View.GONE);

		if (!hasInternet || isKoukaibukuroDisabled) {

			view.findViewById(R.id.webview_download_screen_layout_for_kuma)
					.setVisibility(View.GONE);
			view.findViewById(R.id.view_kuma_no_internet_download)
					.setVisibility(View.VISIBLE);

		} else {

			webview = (WebView) view
					.findViewById(R.id.webview_download_screen_for_kuma);
			try {
				String versionName = getActivity().getPackageManager()
						.getPackageInfo(getActivity().getPackageName(), 0).versionName;
				webviewDownloadScreenURL = PBAPIContant.PB_DOWNLOAD_WEBVIEW_URL
						+ versionName;
				System.out.println("Atik download webview URL is:"
						+ webviewDownloadScreenURL);
				webview.loadUrl(webviewDownloadScreenURL);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			loadOpenPage(webview);
			view.findViewById(R.id.webview_download_screen_layout_for_kuma)
					.setVisibility(View.VISIBLE);
			view.findViewById(R.id.view_kuma_no_internet_download)
					.setVisibility(View.GONE);

		}

		// Enable button
	//	System.out.println("Atik call onResume on DownloadMain Activity");
		view.findViewById(R.id.btn_dl_input_get_img1).setEnabled(true);
		view.findViewById(R.id.btn_dl_input_get_img2).setEnabled(true);

	}

	/*
	 * Async task class for make the password to publish to public
	 */
	private static String mResponseDescription;
	private static int mResponseError;
	private Response mResponse;

	private class GetStatusOfKoukaibukuroWhenApplicationStart extends
			AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			mResponse = PBAPIHelper.getStatusKoukaiBukuro();
			if (mResponse != null) {
				mResponseDescription = mResponse.decription;
				mResponseError = mResponse.errorCode;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// go to uploading screen
			super.onPostExecute(result);

			if (mResponseError == ResponseHandle.CODE_200_OK) {
				isKoukaibukuroDisabled = false;
				System.out.println("Atik koukaibukuro status:"
						+ isKoukaibukuroDisabled);
			} else {
				isKoukaibukuroDisabled = true;
				view.findViewById(R.id.webview_download_screen_layout_for_kuma)
						.setVisibility(View.GONE);
				view.findViewById(R.id.view_kuma_no_internet_download)
						.setVisibility(View.VISIBLE);
				System.out.println("Atik koukaibukuro status:"
						+ isKoukaibukuroDisabled);
			}

		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	protected void loadOpenPage(final WebView wv) {

		wv.setBackgroundColor(0x00000000);
		wv.setVerticalScrollBarEnabled(false);
		wv.setHorizontalScrollBarEnabled(false);
		wv.getSettings().setUseWideViewPort(true);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				String decodeUrl = decodeURIComponent(url);

				System.out.println("Atik tapped URL in layout"
						+ decodeUrl.toString());
				if (decodeUrl
						.equals("https://play.google.com/store/apps/details?id=com.kayac.photobag")) {

					Intent intentAndroidMarket = new Intent(Intent.ACTION_VIEW);
					intentAndroidMarket.setData(Uri
							.parse("market://details?id=com.kayac.photobag"));
					startActivity(intentAndroidMarket);

					(new Thread(new Runnable() {
						public void run() {
							wv.loadUrl(webviewDownloadScreenURL);
						}
					})).run();
				}

				if ((decodeUrl.contains("//"))
						&& (decodeUrl.contains("photocon"))) {

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
						System.out
								.println("Atik tapped URL in layout: new URL in Right URL"
										+ decodeUrl.toString());
						// Go TO webview
						Intent intent = new Intent(getActivity(),
								PBWebViewPhotoContestActivity.class);
						// intent.putExtra(PBAPIContant.PB_SETTING_EXTRA_URL,
						// "http://192.168.0.113/test/button.html");
						// intent.putExtra(PBAPIContant.PB_SETTING_EXTRA_URL,
						// PBAPIContant.PB_PHOTO_CONTEST_URL);

						intent.putExtra(PBAPIContant.PB_SETTING_EXTRA_URL,
								decodeUrl);
						intent.putExtra(
								PBAPIContant.PB_SETTING_EXTRA_TITLE,
								getString(R.string.pb_PBWebViewPhotoContestActivity_screen_titile));
						startActivity(intent);

					}
				}

				if ((decodeUrl.contains("//")) && (decodeUrl.contains("?"))) {

					String webviewAction = decodeUrl.substring(
							decodeUrl.indexOf("//") + 2, decodeUrl.indexOf("?"));
					String webviewData = decodeUrl.substring(
							decodeUrl.indexOf("?") + 1, decodeUrl.length());

					if (webviewAction
							.equals(PBAPIContant.PB_WEBVIEW_URL_PARSE_REWARD)) {

						if (webviewData
								.equals(PBAPIContant.PB_WEBVIEW_URL_PARSE_ACORN_FOREST)) {

							isFromMori = true;
							PBTaskCheckDeviceLockStatus taskFromMori = new PBTaskCheckDeviceLockStatus();
							taskFromMori.execute();

							(new Thread(new Runnable() {
								public void run() {
									wv.loadUrl(webviewDownloadScreenURL);
								}
							})).run();

						} else if (webviewData
								.equals(PBAPIContant.PB_WEBVIEW_URL_PARSE_ACORN_OPENPAGE)) {

							Intent intentOpenPage = new Intent(getActivity(),
									PBOpenPageWebViewActivity.class);

							System.out.println("Atik insert here");

							int kumaMemoUpdateNumber = 0;

							int acronsCounter = PBPreferenceUtils.getIntPref(
									getActivity(), PBConstant.PREF_NAME,
									PBConstant.PREF_DONGURI_COUNT, 0);

							int mapleCounter = PBPreferenceUtils.getIntPref(
									getActivity(), PBConstant.PREF_NAME,
									PBConstant.PREF_MAPLE_COUNT, 0);

							int otameshiHoneyCounter = PBPreferenceUtils
									.getIntPref(getActivity(),
											PBConstant.PREF_NAME,
											PBConstant.PREF_HONEY_BONUS, 0);

							int goldCounter = PBPreferenceUtils.getIntPref(
									getActivity(), PBConstant.PREF_NAME,
									PBConstant.PREF_GOLD_COUNT, 0);

							int newHoney = PBPreferenceUtils.getIntPref(
									getActivity(), PBConstant.PREF_NAME,
									PBConstant.PREF_NAME_NOTIF_HONEY_NEW, 0);
							int newMaple = PBPreferenceUtils.getIntPref(
									getActivity(), PBConstant.PREF_NAME,
									PBConstant.PREF_NAME_NOTIF_MAPLE_NEW, 0);
							int newDonguri = PBPreferenceUtils.getIntPref(
									getActivity(), PBConstant.PREF_NAME,
									PBConstant.PREF_NAME_NOTIF_DONGURI_NEW, 0);
							int newGold = PBPreferenceUtils.getIntPref(
									getActivity(), PBConstant.PREF_NAME,
									PBConstant.PREF_NAME_NOTIF_GOLD_NEW, 0);

							if (newHoney + newMaple + newDonguri + newGold > 0) {
								kumaMemoUpdateNumber = newHoney + newMaple
										+ newDonguri + newGold;
							} else {
								kumaMemoUpdateNumber = 0;
							}
							boolean isShowKumaMemo = false;
							PBTimelineHistoryModel newestTimelineData = PBDatabaseManager
									.getInstance(getActivity())
									.getNewestTimelineHistory();
							if (newestTimelineData != null) {

								isShowKumaMemo = true;
							}

							String token = PBPreferenceUtils.getStringPref(
									getActivity(), PBConstant.PREF_NAME,
									PBConstant.PREF_NAME_TOKEN, "");
							String deviceUUID = PBPreferenceUtils
									.getStringPref(PBApplication
											.getBaseApplicationContext(),
											PBConstant.PREF_NAME,
											PBConstant.PREF_NAME_UID, "0");
							// ArrayList<HashMap<String, String>>
							// historyItemList = getHistoryData();
							// Log.d("historyItemList", ""+historyItemList);

							boolean hasInternet = PBApplication
									.hasNetworkConnection();
							Intent intent = new Intent(getActivity(),
									MainTabActivity.class);
							intent.putExtra("acronsCounter", acronsCounter);
							intent.putExtra("mapleCounter", mapleCounter);
							intent.putExtra("otameshiHoneyCounter",
									otameshiHoneyCounter);
							intent.putExtra("goldCounter", goldCounter);
							intent.putExtra("kumaMemoUpdateNumber",
									kumaMemoUpdateNumber);
							intent.putExtra("isShowKumaMemo", isShowKumaMemo);
							intent.putExtra("token", token);
							intent.putExtra("uid", deviceUUID);
							// intent.putExtra("history_data", historyItemList);
							if (hasInternet) {

								startActivity(intent);

							} else {

								/*
								 * Toast.makeText(PBDownloadMainActivity.this,
								 * getString(R.string.
								 * pb_network_not_available_general_message),
								 * 1000).show();
								 */
								Toast toast = Toast
										.makeText(
												getActivity(),
												getString(R.string.pb_network_not_available_general_message),
												1000);
								TextView v1 = (TextView) toast.getView()
										.findViewById(android.R.id.message);
								if (v1 != null)
									v1.setGravity(Gravity.CENTER);
								toast.show();
							}

							/*
							 * (new Thread(new Runnable() { public void run() {
							 * wv.loadUrl(webviewDownloadScreenURL); }
							 * })).run();
							 */

							if (hasInternet) {

								(new Thread(new Runnable() {
									public void run() {
										wv.loadUrl(webviewDownloadScreenURL);
									}
								})).run();

							} else {

								view.findViewById(
										R.id.webview_download_screen_layout_for_kuma)
										.setVisibility(View.GONE);
								view.findViewById(
										R.id.view_kuma_no_internet_download)
										.setVisibility(View.VISIBLE);
							}
						} else {
							// Nothing will happened if URL is not openpage
						}

					}

				} else {


				}
				return true;

			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Log.e(TAG, String.format(
						"[onReceivedError] error:%d , desc:%s , url:%s",
						errorCode, description, failingUrl));
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				Log.e(TAG, "[onReceivedSslError]");
			}
		});

	}

	protected void startGCMRegistration(Context context) {

		// Check GCM Service Available
		if (!checkIsGCMServiceAvailable()) {
			return;
		}

		intentFilter = new IntentFilter();
		intentFilter.addAction("com.kayac.photobag.gcm.GCMBroadcastReceiver");
		getActivity().registerReceiver(mHandleMessageReceiver, intentFilter);
		isRegisteredReceiver = true;

		final String regId = GCMRegistrar.getRegistrationId(getActivity());
		if (regId.equals("")) {
			GCMRegistrar.register(getActivity(), GCMConstant.PN_GCM_PROJECT_ID);
		} else {
			Log.d("GCM MESSAGE", "StartGCMRegistration [" + regId.length()
					+ "] , [" + regId + "]");
		}
	}

	protected boolean checkIsGCMServiceAvailable() {
		try {
			GCMRegistrar.checkDevice(getActivity());
			GCMRegistrar.checkManifest(getActivity());
			return true;
		} catch (Throwable th) {
			return false;
		}
	}

	public void sendRegistrationIdToServer(final Context context,
			final String registrationId) {

		HttpUtils mHttpUtil = HttpUtils.getInstance();
		HttpClient client = mHttpUtil.createHttpsClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 30000);

		// add token cookie
		String token = PBPreferenceUtils.getStringPref(context,
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "none");

		ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String, String>>();
		cookies.add(new MapEntry<String, String>("token", token));
		try {
			String deviceId = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("device_token",
					registrationId/* deviceId */));
			nameValuePairs.add(new BasicNameValuePair("token", token));
			nameValuePairs.add(new BasicNameValuePair("version",
					PBApplication.VERSION));
			nameValuePairs.add(new BasicNameValuePair("lang", "ja"));
			nameValuePairs.add(new BasicNameValuePair("platform", "android"));

			mHttpUtil.doPost(client, GCMConstant.PN_GCM_URL_REGISTER,
					nameValuePairs, cookies);

			HttpResponse httpResponse = mHttpUtil.doPost(client,
					GCMConstant.PN_GCM_URL_REGISTER, nameValuePairs, cookies);// client.execute(httpPost);

			// handle response from server
			if (httpResponse == null)
				return;

			int statusCode = ResponseHandle.CODE_404;

			// Toast.makeText(getApplicationContext(), "GCM Server Response: " +
			// statusCode, Toast.LENGTH_LONG).show();
			if (httpResponse.getStatusLine() != null) {
				statusCode = httpResponse.getStatusLine().getStatusCode();

				if (statusCode == ResponseHandle.CODE_200_OK) { // store
																// preference
					Log.d("GCM MESSAGE",
							"GCM SAVED INTO SERVER - Server ID >>>>>>>>>>>>>>> "
									+ deviceId);
					// Toast.makeText(context, "GCM SAVED INTO SERVER",
					// Toast.LENGTH_LONG).show();
					// saveDeviceIdToServer(context, deviceId);
				}
				HttpEntity entity = httpResponse.getEntity();
				if (statusCode == HttpStatus.SC_OK) {
					EntityUtils.toString(entity);
				}
				// Log.d("C2DM", ">>>>>  GCM_Control: registration with Kayac: "
				// + statusCode + " - "+content );
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} finally {
			if (client != null)
				client.getConnectionManager().shutdown();
		}

	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String newMessage;
			newMessage = intent.getExtras().getString("message");
			if (newMessage != null) {
			}
			if (newMessage == null) {
				newMessage = intent.getExtras().getString("error");
				if (newMessage != null) {
				}
			}
			if (newMessage == null) {
				newMessage = intent.getExtras().getString("regist");
				if (newMessage != null) {
					Log.d("GCM MESSAGE", "HUQ - sendRegistrationIdToServer ["
							+ newMessage.length() + "] , [" + newMessage + "]");
					sendRegistrationIdToServer(context, newMessage);
					// registerInBackground(context,newMessage);
				}
			}
			if (newMessage == null) {
				newMessage = intent.getExtras().getString("unregist");
				if (newMessage != null) {
				}
			}
		}
	};

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

		if (!hidden) {

			// V2 google analytics has been comment out
			/*
			 * if (PBTabBarActivity.gaTracker != null) {
			 * PBTabBarActivity.gaTracker
			 * .trackPageView("PBDownloadMainActivity"); }
			 */
			if (!TextUtils.isEmpty(passwordFromUrl)) {
				mEdtPwdToCheck.setText(passwordFromUrl);
				passwordFromUrl = "";
			}
			if (sDownloadComplete != 0) {
				mEdtPwdToCheck.setText("");
			}
			if (sDownloadComplete == 2
					&& PBMainTabBarActivity.sMainContext != null
					&& PBMainTabBarActivity.sMainContext.mTabHost != null) {
				PBMainTabBarActivity.sMainContext.mTabHost
						.setCurrentTabByTag(PBConstant.HISTORY_TAG);
			}

			if (myWebView != null && PBApplication.hasNetworkConnection()) {
				loadPublicPassword(myWebView);
			} else {
				myWebView.setVisibility(View.GONE);
			}

			sDownloadComplete = 0;
			hideSoftKeyboard();

			boolean hasInternet = PBApplication.hasNetworkConnection();
			mDownloadAD.setVisibility(hasInternet ? View.VISIBLE : View.GONE);

			if (!hasInternet || isKoukaibukuroDisabled) {

				view.findViewById(R.id.webview_download_screen_layout_for_kuma)
						.setVisibility(View.GONE);
				view.findViewById(R.id.view_kuma_no_internet_download)
						.setVisibility(View.VISIBLE);

			} else {

				webview = (WebView) view
						.findViewById(R.id.webview_download_screen_for_kuma);
				try {
					String versionName = getActivity().getPackageManager()
							.getPackageInfo(getActivity().getPackageName(), 0).versionName;
					webviewDownloadScreenURL = PBAPIContant.PB_DOWNLOAD_WEBVIEW_URL
							+ versionName;
					System.out.println("Atik download webview URL is:"
							+ webviewDownloadScreenURL);
					webview.loadUrl(webviewDownloadScreenURL);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				loadOpenPage(webview);
				view.findViewById(R.id.webview_download_screen_layout_for_kuma)
						.setVisibility(View.VISIBLE);
				view.findViewById(R.id.view_kuma_no_internet_download)
						.setVisibility(View.GONE);

			}

			// Enable button
//			System.out.println("Atik call onResume on DownloadMain Activity");
//			System.out.println("ishti   "
//					+ PBDownloadFragment.isKoukaibukuroDisabled);
			view.findViewById(R.id.btn_dl_input_get_img1).setEnabled(true);
			view.findViewById(R.id.btn_dl_input_get_img2).setEnabled(true);

		}

	}

	@Override
	public void onResume() {
		super.onResume();
		mEdtPwdToCheck.setText("");
		if (!TextUtils.isEmpty(passwordFromUrl)) {
			mEdtPwdToCheck.setText(passwordFromUrl);
			passwordFromUrl = "";
		}
	}

	// 13062012 @HaiNT15 create function load webview <S>
	@SuppressLint("SetJavaScriptEnabled")
	protected void loadPublicPassword(final WebView wv) {

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
				String decodeUrl = decodeURIComponent(url);

				// 20121126 add open web view interface
				// url sample:"photobagin://download?password=[xxxx]"
				if ((decodeUrl.contains("//")) && (decodeUrl.contains("?"))) {

					String webviewAction = decodeUrl.substring(
							decodeUrl.indexOf("//") + 2, decodeUrl.indexOf("?"));
					String webviewData = decodeUrl.substring(
							decodeUrl.indexOf("?") + 1, decodeUrl.length());

					if (webviewAction.equals("download")) {
						if (webviewData.startsWith("password=")) {
							String password = webviewData.substring(9,
									webviewData.length());
							mEdtPwdToCheck.setText(password);
						}
					}
					if (webviewAction.equals("openview")) {

					}
				}
				return false;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Log.e(TAG, String.format(
						"[onReceivedError] error:%d , desc:%s , url:%s",
						errorCode, description, failingUrl));
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				Log.e(TAG, "[onReceivedSslError]");
			}
		});

		// 13062012 @HaiNT15 create function load webview <E>
		// @HaiNT15: transparent the white corlor of the web view <S>
		wv.setBackgroundColor(0xECE9E5);

		(new Thread(new Runnable() {
			public void run() {
				wv.loadUrl(PBConstant.PUBLIC_PASSWORD_HTTP);
			}
		})).run();
	}

	@Override
	public void onPause() {
		super.onPause();
		hideSoftKeyboard();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (isRegisteredReceiver) {
			isRegisteredReceiver = false;
			getActivity().unregisterReceiver(mHandleMessageReceiver);
		}

		GCMRegistrar.onDestroy(getActivity().getApplicationContext());
	};

	private final Handler mHandler = new Handler() {

	

		@Override
		public void handleMessage(android.os.Message msg) {

			view.findViewById(R.id.btn_dl_input_get_img1).setEnabled(true);
			view.findViewById(R.id.btn_dl_input_get_img2).setEnabled(true);
			switch (msg.what) {
			// @lent add
			case MSG_SHOW_UNFINISH:
				// reset exceeded count
				mNumberOfRetryCount = 0;
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
				startActivity(mShowExceededIntent);
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
						mJsonPhotoList = PBPreferenceUtils.getStringPref(
								getActivity(), PBConstant.PREF_NAME,
								PBConstant.PREF_DL_PHOTOLIST_JSON_DATA, "");
						mPhotoListPassword = PBPreferenceUtils.getStringPref(
								getActivity(), PBConstant.PREF_NAME,
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
							mEdtPwdToCheck.setText("");
							//getActivity().showDialog(DIALOG_AIKOTOBA_NOUPDATE);
							showDialog(R.string.pb_download_need_update_title,R.string.pb_download_aikotoba_already_uptodate);

						} else {

							if (PBPreferenceUtils.getBoolPref(
									PBApplication.getBaseApplicationContext(),
									PBConstant.PREF_NAME,
									PBConstant.PREF_NO_NEED_UPDATE, false)) {
								mEdtPwdToCheck.setText("");
								PBPreferenceUtils.saveBoolPref(getActivity()
										.getApplicationContext(),
										PBConstant.PREF_NAME,
										PBConstant.PREF_NO_NEED_UPDATE, false);
//								getActivity().showDialog(
//										DIALOG_AIKOTOBA_NOUPDATE);
								showDialog(R.string.pb_download_need_update_title,R.string.pb_download_aikotoba_already_uptodate);

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
									mEdtPwdToCheck.setText("");

								} else {
									// reset exceeded count
									mNumberOfRetryCount = 0;
									mIntent = new Intent(
											PBMainTabBarActivity.sMainContext,
											PBDownloadDownloadingActivity.class);
									startActivity(mIntent);
									mEdtPwdToCheck.setText("");
								}
							}

						}

						// }

						mEdtPwdToCheck.setText("");
						break;

					case ResponseHandle.CODE_400:
						if (errorDesc.contains("unfinished")) { // case
																// unfinished
																// upload
						//	getActivity().showDialog(DIALOG_UNFINISH);
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
							if (needConfirm()) {
								mIntent = new Intent(
										PBMainTabBarActivity.sMainContext,
										PBDownloadConfirmActivity.class);
								mIntent.putExtra("NEED_PURCHASE", true);
								startActivity(mIntent);
							} else {
								mIntent = new Intent(
										PBMainTabBarActivity.sMainContext,
										PBDownloadPurchaseActivity.class);
								mIntent.putExtra(
										PBConstant.START_PURCHASE_NOTICE, true);
								startActivity(mIntent);
							}
						} else if (errorDesc
								.contains("client confirmation needed")) {
							mIntent = new Intent(
									PBMainTabBarActivity.sMainContext,
									PBDownloadConfirmActivity.class);
							startActivity(mIntent);
						} else {
							// mShowPurchaseScreen = false;
							showWrongPassPage();
						}

						break;
					case ResponseHandle.CODE_403:
						
						if(mResponeResult.decription == null){
							
							//getActivity().showDialog(DIALOG_IN_REVIEW);
						//	.setTitle(R.string.pb_password_banned_title)
						//	.setMessage(getString(R.string.pb_password_banned_context))
							showDialog(R.string.pb_password_banned_title,R.string.pb_password_banned_context);
							return ;
						}
						
						
						Intent intentSecretPassword = new Intent(getActivity(), PBDownloadSecretCodeActivity.class);
						intentSecretPassword.putExtra("code", mEdtPwdToCheck.getText().toString());
						startActivity(intentSecretPassword);
						getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);


						break;
					case ResponseHandle.CODE_406:
						if (errorDesc.contains("最新のバージョン")) {
						//	getActivity().showDialog(DIALOG_NEED_UPDATE);
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
					Log.e(TAG, ">>> response from server is NULL!");
				}
				break;

			case MSG_SHOW_PROGRESS_BAR:
				getActivity().findViewById(R.id.btn_dl_input_get_img1)
						.setEnabled(false);
				getActivity().findViewById(R.id.btn_dl_input_get_img2)
						.setEnabled(false);
				if (mCustomWaitingLayout == null) {
					mCustomWaitingLayout = new PBCustomWaitingProgress(
							getActivity());
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
	}

	/**
	 * method support hide soft keyboard on phone screen.
	 */
	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEdtPwdToCheck.getWindowToken(), 0);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	void showDialog(int title,int mgs) {
		PBDialogFragment newFragment = PBDialogFragment.newInstance(
				title,mgs);
	    newFragment.show(getFragmentManager(), "dialog");
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
		if (PBPreferenceUtils.getBoolPref(
				getActivity().getApplicationContext(), PBConstant.PREF_NAME,
				PBConstant.PREF_IS_IN_EXCEEDED_MODE, false)) {
			long startCountdownTime = PBPreferenceUtils.getLongPref(
					getActivity().getApplicationContext(),
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
			PBPreferenceUtils.saveLongPref(getActivity()
					.getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_DL_INPUT_PASS_RETRY_START_TIME,
					startTimeCount);
			PBPreferenceUtils.saveBoolPref(getActivity()
					.getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_IS_IN_EXCEEDED_MODE, true);
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
		PBPreferenceUtils.saveBoolPref(getActivity().getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_IS_IN_EXCEEDED_MODE,
				false);
		// reset start check time
		PBPreferenceUtils.saveLongPref(getActivity().getApplicationContext(),
				PBConstant.PREF_NAME,
				PBConstant.PREF_DL_INPUT_PASS_RETRY_START_TIME, 0);
		// reset prev pass
		PBPreferenceUtils.saveStringPref(getActivity().getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_DL_INPUT_PASS_PREV_PASS,
				"");
	}

	private boolean needConfirm() {
		String downloadInfo = PBPreferenceUtils.getStringPref(getActivity()
				.getApplicationContext(), PBConstant.PREF_NAME,
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

	public String passwordToCheck = "";
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_dl_input_get_img1:

				downloadButtonAction();

				break;

			case R.id.btn_dl_input_get_img2:
				downloadButtonAction();

				break;


			// Transfer Jet Implementation
			case R.id.btn_input_get_img_transferJet:
				Log.d("Transfer Jet", "TransferJet Receive");
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_MAIN);
				intent.setClassName("jp.co.toshiba.tosjetutil",
						"jp.co.toshiba.transferjetdemo.ReceiveActivity");
				startActivity(intent);
				break;
			case R.id.edt_dl_pwd_get_img:
				mDownloadAD.setVisibility(View.GONE);
				break;
			case R.id.layout_download_main:
				mDownloadAD.setVisibility(View.VISIBLE);
				hideSoftKeyboard();
				break;
			/* case R.id.forest_thumb_download_main: */

			case R.id.layout_reward_forest:

				// Check device lock status
				isFromMori = true;
				PBTaskCheckDeviceLockStatus taskFromMori = new PBTaskCheckDeviceLockStatus();
				taskFromMori.execute(); // Need to set the URL from constants

				break;

			default:
				break;
			}
		}
	};

	private void downloadButtonAction() {
		

		String deviceUUID = PBPreferenceUtils.getStringPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, "0");

//		System.out
//				.println("Atik Easy Tracker is called upon on download button press");
		EasyTracker easyTracker = EasyTracker.getInstance(getActivity());
		easyTracker.set(Fields.SCREEN_NAME, "" + deviceUUID
				+ ":PBDownloadMainActivity");
		// MapBuilder.createEvent().build() returns a Map of event fields and
		// values
		// that are set and sent with the hit.
		easyTracker.send(MapBuilder.createEvent("ui_action", // Event category
																// (required)
				"button_press_for_download", // Event action (required)
				mEdtPwdToCheck.getEditableText().toString(), // Event label
				null) // Event value
				.build());

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
					new ContextThemeWrapper(getActivity(), R.style.popup_theme));
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

		passwordToCheck = mEdtPwdToCheck.getEditableText().toString();

		if (TextUtils.isEmpty(passwordToCheck)) {
			view.findViewById(R.id.btn_dl_input_get_img1).setEnabled(true);
			view.findViewById(R.id.btn_dl_input_get_img2).setEnabled(true);
			return;
		}

		view.findViewById(R.id.btn_dl_input_get_img1).setEnabled(false);
		view.findViewById(R.id.btn_dl_input_get_img2).setEnabled(false);

		System.out.println("Atik before check device lock status");
		// Atik need to check the device is lock or not
		PBTaskCheckDeviceLockStatus task = new PBTaskCheckDeviceLockStatus();
		task.execute(); // Need to set the URL from constants

	}

	private String decodeURIComponent(String string) {
		try {
			return URLDecoder.decode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
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

//			System.out
//					.println("Atik called for device lock status check doInBackground");

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
//			Log.d("MIGRATION_CODE", "res: " + res.errorCode + " "
//					+ res.decription);
			if (res != null) {
				if (res.errorCode == ResponseHandle.CODE_200_OK) {

					result200Ok = true;
//					System.out.println("atik MIGRATION_CHECK_LOCK_STATUS"
//							+ "200 OK:" + res.decription);

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
				PBGeneralUtils.showAlertDialogActionWithOnClick(getActivity()
						.getApplicationContext(), title, message,
						getString(R.string.dialog_ok_btn),
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
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// Delay dialog display after 1s = 1000ms
					boolean isExceeded = PBPreferenceUtils.getBoolPref(
							getActivity().getApplicationContext(),
							PBConstant.PREF_NAME,
							PBConstant.PREF_IS_IN_EXCEEDED_MODE, false);
					if (isExceeded) {
						mHandler.sendEmptyMessage(MSG_SHOW_EXCEEDED_SCREEN);
						return;
					}

					if (checkPasswordInputExceeded(passwordToCheck)) {
						return;
					}

					mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_BAR);
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							// confirm password

							PBPreferenceUtils.saveStringPref(getActivity()
									.getApplicationContext(),
									PBConstant.PREF_NAME,
									PBConstant.INTENT_PASSWORD_HONEY_PHOTO,
									passwordToCheck);

							String tokenKey = PBPreferenceUtils.getStringPref(
									getActivity().getApplicationContext(),
									PBConstant.PREF_NAME,
									PBConstant.PREF_NAME_TOKEN, "");

								mResponeResult = PBAPIHelper
										.checkPhotoListInCollection(
												passwordToCheck, tokenKey,
												false, null, false, "");

							
							System.out.println(mResponeResult.errorCode + "  "
									+ mResponeResult.decription);
							System.out
									.println("Atik download password name is :"
											+ passwordToCheck);


							PBPreferenceUtils
									.saveStringPref(
											getActivity().getBaseContext(),
											PBConstant.PREF_NAME,
											PBConstant.PREF_PASSWORD_NAME_FOR_HISTORY_BUG,
											passwordToCheck);


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

	// Added below activity life cycle method for Google analytics
	@Override
	public void onStart() {

		super.onStart();
	//	System.out.println("Atik start Easy Tracker");
		EasyTracker.getInstance(getActivity()).activityStart(getActivity());
	}

	// Added below activity life cycle method for Google analytics

	@Override
	public void onStop() {
		super.onStop();
	//	System.out.println("Atik stop Easy Tracker");
		EasyTracker.getInstance(getActivity()).activityStop(getActivity());
	}

}
