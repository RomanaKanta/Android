package com.aircast.photobag.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.inappbilling.PBIabHelper;
import com.aircast.photobag.inappbilling.PBIabResult;
import com.aircast.photobag.inappbilling.PBInventory;
import com.aircast.photobag.inappbilling.PBPurchase;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.PBCustomWaitingProgress;
import com.aircast.photobag.widget.actionbar.ActionBar;

public class PBPurchaseActivity extends PBAbsActionBarActivity {

	private static final String TAG = "PBPurchaseActivity";
	private ActionBar mHeaderBar;
	private WebView mWebView;
	private String htmlContent;

	private PBIabHelper mHelper;
	private PBPurchase mPurchase;

	public final int MSG_START_PURCHASE = 10001;
	private final int MSG_CONFIRM_SUC = 20001;
	private final int MSG_PURCHASE_ERROR = 20002;

	private boolean mIsMultyRequest = false;

	private PBCustomWaitingProgress waitingProgress;
	private LinearLayout mHistoryAD;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pb_buy_honey_together);

		mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
		setHeader(mHeaderBar, getString(R.string.pb_buy_honey_together));
		
 		if(getIntent().hasExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY)){
	       	 boolean isFromLibrary = getIntent().getExtras().getBoolean(PBConstant.PREF_PASSWORD_FROM_LIBRARY);
	 		 if(isFromLibrary){
	 			 
	 			mHeaderBar.setHomeLogo(R.drawable.openbag_actionbar_icon);
	 		 }		 
		}
		
		waitingProgress = new PBCustomWaitingProgress(PBPurchaseActivity.this);

		mHistoryAD = (LinearLayout) findViewById(R.id.layout_history_ad);

		
		mWebView = (WebView) findViewById(R.id.webView_purchase_info);
		mHelper = new PBIabHelper(PBPurchaseActivity.this, null);
		mHelper.startSetup(new PBIabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(PBIabResult result, Bundle data) {
				if (!result.isSuccess()) {
					Message msg = Message.obtain(null, MSG_PURCHASE_ERROR,
							getString(R.string.pb_honey_shop_error_init));

					System.out.println("startSetup not success");
					mHandler.sendMessage(msg);
				}
				showWaitingLayout(true);
				initGoodsList(data);
			}
		});
		new GetHoneyShopInfo().execute();
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        // V2 google analytics has been comment out
        /*if (PBTabBarActivity.gaTracker != null) {
        	PBTabBarActivity.gaTracker.trackPageView("PBSettingMainActivity");
        }*/
        
		boolean hasInternet = PBApplication.hasNetworkConnection();
		mHistoryAD.setVisibility(hasInternet ? View.VISIBLE : View.GONE);

		if (!hasInternet) {
       	 	Toast.makeText(PBPurchaseActivity.this, 
    			 getString(R.string.pb_network_not_available_general_message), 1000).show();
			finish();
		}
    }

	private void initGoodsList(Bundle data) {

		mHelper.queryInventoryAsync(mGotInventoryListener);
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
				if (decodeUrl.contains(PBAPIContant.PB_WEBVIEW_URL_PARSE_INAPP)) {
					String sku = decodeUrl.replace("inapp://", "");
					System.out.println("Atik SKU value is:"+sku);
					startPurchase(sku);

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

	private String decodeURIComponent(String string) {
		try {
			return URLDecoder.decode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	private void startPurchase(String sku) {
		System.out.println("sku- " + sku);
		if (mHelper == null) {
			showErrorMsg(getString(R.string.pb_honey_shop_error_init));
			return;
		}

		showWaitingLayout(true);
		mHelper.launchPurchaseFlow(PBPurchaseActivity.this, sku,
				10001, mPurchaseFinishedListener);

	}

	private PBIabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new PBIabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(PBIabResult result,
				PBPurchase purchase) {
			System.out
					.println("mPurchaseFinishedListener " + result.toString());
			if (result.isFailure()) {
				if (PBIabHelper.IABHELPER_USER_CANCELLED == result
						.getResponse()) {
					showWaitingLayout(false);
					return;
				}

				if (ResponseHandle.CODE_400 == result.getResponse()) {
					showWaitingLayout(false);
					showErrorMsg(result.getMessage());
					return;
				}
			}

			if (purchase != null && purchase.isAvailable()) {
				System.out.println("purchase ok");
				mPurchase = purchase;

				postPurchaseToServer();
				return;
			}

			showErrorMsg(getString(R.string.pb_honey_shop_error_extra)); 
			showWaitingLayout(false);

		}
	};

	private void showWaitingLayout( boolean show) {

				if (waitingProgress != null) {

					if (show) {

						waitingProgress.showWaitingLayout();
					} else {

						waitingProgress.hideWaitingLayout();
					}

				}

	}

	@Override
	protected void handleHomeActionListener() {
		finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		// TODO Auto-generated method stub

	}

	private class GetHoneyShopInfo extends AsyncTask<String, Void, Response> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (waitingProgress != null)
				waitingProgress.showWaitingLayout();
		}

		@Override
		protected Response doInBackground(String... params) {

			String uuid = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, "0");
			Response mResponse = PBAPIHelper.getHoneyShopInfo(uuid);

			return mResponse;
		}

		@Override
		protected void onPostExecute(Response result) {
			// go to uploading screen
			super.onPostExecute(result);

			if (waitingProgress != null)
				waitingProgress.hideWaitingLayout();
			try {
				if (result != null) {
					String mResponseDescription = result.decription;
					int mResponseError = result.errorCode;
					Log.d("response", mResponseDescription);
					JSONObject jObject;

					jObject = new JSONObject(mResponseDescription);
					if (mResponseError == ResponseHandle.CODE_200_OK) {

						if (jObject.has("message")) {

							htmlContent = jObject.getString("message");
							mWebView.loadData(htmlContent,
									"text/html; charset=UTF-8", null);
							loadOpenPage(mWebView);
						}

					} else {

						if (jObject.has("message")) {

							showErrorMsg(jObject.getString("message"));
						}

					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case MSG_CONFIRM_SUC:
				if (mIsMultyRequest) {
					mIsMultyRequest = false;
				} else {

					System.out.println("call MSG_CONFIRM_SUC");
				}
				updateHoneyCount();

				mPurchase = null;

				break;

			case MSG_PURCHASE_ERROR:
				showErrorMsg(msg.obj == null ? null : String.valueOf(msg.obj));

				break;
			default:
				break;
			}
		}
	};

	// this listener is called if there are unfinished purchase goods
	private PBIabHelper.QueryInventoryFinishedListener mGotInventoryListener = new PBIabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(PBIabResult result,
				PBInventory inventory) {
			
			System.out.println("Atik called QueryInventoryFinishedListener");
			if (result.isFailure()) {
				System.out.println("Atik called QueryInventoryFinishedListener if failed");
				showWaitingLayout(false);
				return;
			}

			mPurchase = inventory.getAvailadPurchase();
			if (mPurchase != null) {
				System.out.println("Atik called QueryInventoryFinishedListener if purchase available");
				postPurchaseToServer();
				return;
			}

			showWaitingLayout(false);
		}
	};

	private void postPurchaseToServer() {
		new Thread() {
			@Override
			public void run() {
				System.out.println("Atik called postPurchaseToServer");
				System.out.println("call postPurchaseToServer");
				String token = PBPreferenceUtils.getStringPref(
						PBPurchaseActivity.this, PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_TOKEN, "");
				if (TextUtils.isEmpty(token)) {
					return;
				}

				try {

					final PBPurchase purchase = new PBPurchase(
							mPurchase.getOriginalJson(),
							mPurchase.getSignature());

					System.out.println("purchase.getOriginalJson() "
							+ purchase.getOriginalJson());
					System.out.println("purchase.getSignature() "
							+ purchase.getSignature());

					Response response = PBAPIHelper.postPurchaseDataToServer(
							token, purchase.getOriginalJson(),
							purchase.getSignature());
					System.out.println("post response errod code "
							+ response.errorCode);
					System.out.println("post response.decription "
							+ response.decription);
					//showWaitingLayout(false); // Atik commented out
					System.out.println("Atik called postPurchaseToServer before waiting dialog display");
					showWaitingLayout(true);

					if (response != null
							&& response.errorCode == ResponseHandle.CODE_200_OK) {

						if (responseCorrect(response.decription)) {

							mHandler.post(new Runnable() {
								@Override
								public void run() {
									System.out.println("Atik called postPurchaseToServer consumeAsync CODE_200_OK ");

									mHelper.consumeAsync(purchase,
											mConsumeFinishedListener);
								}
							});
							return;
						}
					} else if (response != null
							&& response.errorCode == ResponseHandle.CODE_400
							&& response.decription.contains("duplicated")) {

						mIsMultyRequest = true;
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								System.out.println("Atik called postPurchaseToServer consumeAsync CODE_400 or duplicated ");
								mHelper.consumeAsync(purchase,
										mConsumeFinishedListener);
							}
						});
						return;
					} else if (response != null
							&& response.errorCode == ResponseHandle.CODE_400
							&& response.decription.contains("invalid")) {
						System.out.println("Atik called postPurchaseToServer consumeAsync CODE_400 or invalid ");


						// mIsMultyRequest = true;
//						 mHandler.post(new Runnable() {
//						 @Override
//						 public void run() {
//						 mHelper.consumeAsync(purchase,
//						 mConsumeFinishedListener);
//						 }
//						 });
						return;
					} else if (response != null && response.errorCode == 999) { // TEST
						// ACCOUNT

						mHandler.post(new Runnable() {
							@Override
							public void run() {
								System.out.println("Atik called postPurchaseToServer consumeAsync errorCode == 999 ");
								mHelper.consumeAsync(purchase,
										mConsumeFinishedListener);
							}
						});
						return;
					}
					
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						System.out.println("Atik called postPurchaseToServer show error message ");
						showErrorMsg(getString(R.string.pb_honey_shop_error_extra));
					}
				});
			}
		}.start();
	}

	private boolean responseCorrect(String response) {
		try {
			Log.d("PURCHASE", response);

			JSONObject result = new JSONObject(response);

			int maple = result.getInt(PBConstant.PREF_MAPLE_COUNT);
			if (maple > 0) {
				PBPreferenceUtils.saveIntPref(PBPurchaseActivity.this,
						PBConstant.PREF_NAME, PBConstant.PREF_MAPLE_COUNT,
						maple);

				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private void updateHoneyCount() {

		int maple = PBPreferenceUtils.getIntPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_MAPLE_COUNT, 0);

		try {

			AlertDialog.Builder mapleUpdateDialog = new AlertDialog.Builder(
					new ContextThemeWrapper(PBPurchaseActivity.this,
							R.style.popup_theme));
			mapleUpdateDialog.setTitle("");
			mapleUpdateDialog.setMessage(String.format(
					getString(R.string.pb_buy_maple_result), maple));
			mapleUpdateDialog.setCancelable(false);
			mapleUpdateDialog.setPositiveButton(
					getString(R.string.dialog_ok_btn),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							// Atik no need to finish the activity Instead do the dismiss of dialog
							/*if (!PBPurchaseActivity.this.isFinishing()) {
								PBPurchaseActivity.this.finish();
							}*/
							dialog.dismiss();
						}
					});
			System.out.println("Atik called updateHoneyCount before dialog display ");
			showWaitingLayout(false); // Added by Atik
			mapleUpdateDialog.show();

		} catch (Exception e) {
		}
	}

	private void showErrorMsg(String errorMsg) {
		if (PBPurchaseActivity.this.isFinishing()) {
			return;
		}
		if (TextUtils.isEmpty(errorMsg)) {
			errorMsg = getString(R.string.pb_honey_shop_error_default);
		}
		AlertDialog.Builder errorDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(PBPurchaseActivity.this,
						R.style.popup_theme));
		errorDialog.setTitle(getString(R.string.pb_honey_shop_error_title));
		errorDialog.setMessage(errorMsg);
		errorDialog.setCancelable(false);
		errorDialog.setPositiveButton(getString(R.string.dialog_ok_btn),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Atik no need to finish the activity Instead do the dismiss of dialog
						/*if (!PBPurchaseActivity.this.isFinishing()) {
							PBPurchaseActivity.this.finish();
						}*/
						dialog.dismiss();
					}
				});
		showWaitingLayout(false); // Added by Atik
		errorDialog.show();

	}

	private PBIabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new PBIabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(PBPurchase purchase, PBIabResult result) {
			System.out.println("Atik called mConsumeFinishedListener whenever consumed ");

			mHandler.sendEmptyMessage(result.isSuccess() && result != null ? MSG_CONFIRM_SUC
					: MSG_PURCHASE_ERROR);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("onActivityResult ", "requestCode " + requestCode
				+ " resultCode " + resultCode);

		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null)
			mHelper.dispose();
		mHelper = null;
	}
}
