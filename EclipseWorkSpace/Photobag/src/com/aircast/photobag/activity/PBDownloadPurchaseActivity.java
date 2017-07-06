package com.aircast.photobag.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircast.photobag.R;
import com.aircast.photobag.api.HttpUtils;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.inappbilling.PBIabHelper;
import com.aircast.photobag.inappbilling.PBIabResult;
import com.aircast.photobag.inappbilling.PBInventory;
import com.aircast.photobag.inappbilling.PBPurchase;
import com.aircast.photobag.log.SdcardException;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Show purchase page when the password input need honey to download.
 * <p>
 * show honey shop and honey exchange btn when user have no honey, show give
 * bear honey btn in the other hand.
 * </p>
 * <p>
 * bring back in-app billing function and deprecated honey shop in v2.7.0.
 * prevent user to buy extra honey as the server will be closed.
 * </p>
 * */
public class PBDownloadPurchaseActivity extends PBAbsActionBarActivity
		implements OnClickListener {

	private static final String TAG = "PBDownloadPurchaseActivity";

	private ActionBar mHeaderBar;
	private TextView mTvPurchaseNoticePhotoCount;
	private TextView mTvPurchaseNoticeDownloadedCount;
	private TextView mPurchaseNoticeThumbListCount;
	private ImageView mImvPurchasePhotoThumb;

	private FrameLayout mFrameWaitingLayout;
	private LinearLayout mLayoutHaveHoney;
	private LinearLayout mLayoutNoHoney;
	private FrameLayout mFrameThumbLoadLayout;

	private TextView mTextBear;
	private ImageView mImageBear;

	// 20120605 Bac Give honey bonus to download photo
	private int mHoneyBonusCount = 0;
	private int mMapleHoneyCount = 0;
	private ResponseHandle.Response mRespone;

	private final static int MSG_CHECK_PWD_HONEY = 2000; // bonus honey
	private final static int MSG_CHECK_PWD_MAPLE = 2001; // maple honey

	private boolean mUserHaveHoney;

	private PBIabHelper mHelper;
	private PBPurchase mPurchase;

	private String mPassWord;
	private int counterForTappedItem = 0;

	private int mCountForCacheThumb = 0;
	private int totalNumberThumbFromServer = 0;
	private int numberOfCacheThumpTouched = 0;
	private int numberOfCacheThumpTouchedForTextView = 0;

	private ArrayList<String> mThumbListCacheList = new ArrayList<String>();

	private boolean isFromKoukaibukuro;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pb_layout_download_purchase_notice);

		mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
		setHeader(mHeaderBar,
				getString(R.string.screen_title_dowload_purchase_require));

		findViewById(R.id.btn_download_purchase_confirm).setOnClickListener(
				this);
		findViewById(R.id.btn_download_purchase_cancel)
				.setOnClickListener(this);
		findViewById(R.id.btn_download_purchase_shop).setOnClickListener(this);
		findViewById(R.id.btn_download_purchase_exchange).setOnClickListener(
				this);
		findViewById(R.id.btn_download_purchase_buyMapelFromShop1)
				.setOnClickListener(this);
		findViewById(R.id.btn_download_purchase_buyMapelFromShop2)
				.setOnClickListener(this);

		mLayoutHaveHoney = (LinearLayout) findViewById(R.id.layout_purchase_have_honey);
		mLayoutNoHoney = (LinearLayout) findViewById(R.id.layout_purchase_no_honey);

		mTextBear = (TextView) findViewById(R.id.tv_purchase_kuma_word);
		mImageBear = (ImageView) findViewById(R.id.img_purchase_kuma_bg);

		mTvPurchaseNoticePhotoCount = (TextView) findViewById(R.id.tv_purchase_notice_photo_count);
		mTvPurchaseNoticeDownloadedCount = (TextView) findViewById(R.id.tv_purchase_notice_downloaded_count);
		mPurchaseNoticeThumbListCount = (TextView) findViewById(R.id.thumb_count_cache);
		mImvPurchasePhotoThumb = (ImageView) findViewById(R.id.imv_purchase_notice_thumb);
		mFrameWaitingLayout = (FrameLayout) findViewById(R.id.frame_purchase_waiting);
		mFrameThumbLoadLayout = (FrameLayout) findViewById(R.id.purchase_notice_thumb_click);

		// Atik download thumbnail from server and display onto this screen
		mFrameThumbLoadLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				/*
				 * // TODO chat server testing here String tokenKey =
				 * PBPreferenceUtils.getStringPref( getApplicationContext(),
				 * PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
				 * Response response; response =
				 * PBAPIHelper.nickNameRegistration(tokenKey);
				 * System.out.println("Atik response code"+response.errorCode);
				 * if (response.errorCode == ResponseHandle.CODE_200_OK) { try {
				 * System.out.println(
				 * "Atik got positive response from server for nick name registration"
				 * ); } catch(Exception e) { } }
				 */
				mFrameThumbLoadLayout.setEnabled(false);
				if (totalNumberThumbFromServer > 0
						&& totalNumberThumbFromServer == mCountForCacheThumb) {
					System.out.println("Atik now show from thumb");

					if (numberOfCacheThumpTouched == totalNumberThumbFromServer) {
						numberOfCacheThumpTouched = 0;
						numberOfCacheThumpTouchedForTextView = 0;
					}

					System.out.println("Atik inside cache touched "
							+ numberOfCacheThumpTouched);
					System.out.println("Atik inside cache touched image "
							+ mThumbListCacheList
									.get(numberOfCacheThumpTouched));

					Bitmap bmp = BitmapFactory.decodeFile(mThumbListCacheList
							.get(numberOfCacheThumpTouched));
					numberOfCacheThumpTouched++;
					numberOfCacheThumpTouchedForTextView++;

					if (bmp != null) {
						if (mImvPurchasePhotoThumb != null) {
							mImvPurchasePhotoThumb.setImageBitmap(bmp);

							if (numberOfCacheThumpTouchedForTextView == 0) {
								mPurchaseNoticeThumbListCount.setText(1 + "/"
										+ totalNumberThumbFromServer);
							} else {

								if (numberOfCacheThumpTouchedForTextView == totalNumberThumbFromServer) {
									System.out
											.println("Atik numberOfCacheThumpTouchedForTextView Inside final one"
													+ numberOfCacheThumpTouchedForTextView);
									mPurchaseNoticeThumbListCount.setText(1
											+ "/" + totalNumberThumbFromServer);
								} else {
									System.out
											.println("Atik numberOfCacheThumpTouchedForTextView"
													+ numberOfCacheThumpTouchedForTextView);
									mPurchaseNoticeThumbListCount
											.setText(numberOfCacheThumpTouchedForTextView
													+ 1
													+ "/"
													+ totalNumberThumbFromServer);
								}
							}
						}
					} else {
						if (mImvPurchasePhotoThumb != null) {
							mImvPurchasePhotoThumb
									.setImageResource(R.drawable.thumb_error);
						}
					}
					mFrameThumbLoadLayout.setEnabled(true); // thumbnail tap
															// enable again

				} else {
					(new GetThumbListForPassword()).execute(); // execute server
																// API for
																// getting
																// thumbs and
																// save
					// mFrameThumbLoadLayout.setEnabled(true);
				}
			}
		});

		updateUI();

		mHelper = new PBIabHelper(PBDownloadPurchaseActivity.this, null);
		mHelper.startSetup(new PBIabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(PBIabResult result, Bundle data) {
				if (!result.isSuccess()) {
					mHelper = null;
					return;
				}

				showWaitingLayout(true);
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});

		mPassWord = PBPreferenceUtils.getStringPref(getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.INTENT_PASSWORD_HONEY_PHOTO,
				"");

		// start asynctask for downloading photo list
		(new GetPasswordThumbTask()).execute();
		
		isFromKoukaibukuro = PBPreferenceUtils.getBoolPref(getApplicationContext(),
				PBConstant.PREF_NAME,
				PBConstant.PREF_PASSWORD_FROM_LIBRARY, false);
		 if(isFromKoukaibukuro){
			 
				mHeaderBar.setHomeLogo(R.drawable.openbag_actionbar_icon);
		 }
	}

	private void updateUI() {
		mHoneyBonusCount = PBPreferenceUtils.getIntPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_HONEY_BONUS, 0);
		mMapleHoneyCount = PBPreferenceUtils.getIntPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_MAPLE_COUNT, 0);

		mUserHaveHoney = (mHoneyBonusCount + mMapleHoneyCount) > 0 ? true
				: false;

		if (mUserHaveHoney) {
			mLayoutHaveHoney.setVisibility(View.VISIBLE);
			mLayoutNoHoney.setVisibility(View.GONE);

			mImageBear.setImageResource(R.drawable.img_honey);
			mTextBear
					.setText(R.string.pb_download_purchase_notice_tv_talk_purchase);
		} else {
			mLayoutHaveHoney.setVisibility(View.GONE);
			mLayoutNoHoney.setVisibility(View.VISIBLE);

			mImageBear.setImageResource(R.drawable.img_no_honey);
			mTextBear
					.setText(R.string.pb_download_purchase_notice_tv_talk_no_honey);
		}

		mImageBear.refreshDrawableState();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_download_purchase_confirm:
			giveHoneyToPassword();

			break;
		case R.id.btn_download_purchase_cancel:
			finish();
			break;

		case R.id.btn_download_purchase_shop:
			startPurchase();
			break;

		case R.id.btn_download_purchase_exchange:
			Intent honeyExchangeAct = new Intent(
					PBDownloadPurchaseActivity.this,
					PBMyPageAcornExchangeListActivity.class);
			startActivity(honeyExchangeAct);
			break;
		case R.id.btn_download_purchase_buyMapelFromShop1:
			
	     	boolean hasInternetBeforeGoingToMatomegai = PBApplication.hasNetworkConnection();
        	if (hasInternetBeforeGoingToMatomegai) {
    			Intent mapleIntent1 = new Intent(PBDownloadPurchaseActivity.this,
    					PBPurchaseActivity.class);
    			startActivity(mapleIntent1);

    			break;
        	} else {
        		
				    AlertDialog.Builder	 exitDialog =  new AlertDialog.Builder(new ContextThemeWrapper(PBDownloadPurchaseActivity.this,
					     R.style.popup_theme));
					exitDialog .setMessage(getString(R.string.pb_network_not_available_general_message));
					exitDialog .setCancelable(false);
					exitDialog .setPositiveButton(getString(R.string.dialog_ok_btn),
				       new DialogInterface.OnClickListener() {
				        @Override
				        public void onClick(DialogInterface dialog,
				          int which) {
				        	
				        	
				        	dialog.dismiss();
				        }
					});
					         
					exitDialog.show();
					break;        		
             }

		case R.id.btn_download_purchase_buyMapelFromShop2:
			
	     	boolean hasInternetBeforeGoingToMatomegai1 = PBApplication.hasNetworkConnection();
        	if (hasInternetBeforeGoingToMatomegai1) {
    			Intent mapleIntent2 = new Intent(PBDownloadPurchaseActivity.this,
    					PBPurchaseActivity.class);
    			startActivity(mapleIntent2);

    			break;
        	} else {
        		
				    AlertDialog.Builder	 exitDialog =  new AlertDialog.Builder(new ContextThemeWrapper(PBDownloadPurchaseActivity.this,
					     R.style.popup_theme));
					exitDialog .setMessage(getString(R.string.pb_network_not_available_general_message));
					exitDialog .setCancelable(false);
					exitDialog .setPositiveButton(getString(R.string.dialog_ok_btn),
				       new DialogInterface.OnClickListener() {
				        @Override
				        public void onClick(DialogInterface dialog,
				          int which) {
				        	
				        	
				        	dialog.dismiss();
				        }
					});
					         
					exitDialog.show();
					break;        		
             }
			
		default:
			break;
		}
	}

	private void giveHoneyToPassword() {
		showWaitingLayout(true);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// confirm password

				String tokenKey = PBPreferenceUtils.getStringPref(
						getApplicationContext(), PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_TOKEN, "");

				mRespone = PBAPIHelper.checkPhotoListInCollection(mPassWord,
						tokenKey, true, initConfirmFlag(),false,"");
				mHandler.sendEmptyMessage(mHoneyBonusCount > 0 ? MSG_CHECK_PWD_HONEY
						: MSG_CHECK_PWD_MAPLE);
			}
		});
		thread.start();
	}

	private void startPurchase() {
		if (mHelper == null) {
			showErrorMsg(getString(R.string.pb_honey_shop_error_init));
			return;
		}

		showWaitingLayout(true);
		mHelper.launchPurchaseFlow(PBDownloadPurchaseActivity.this, "honey",// "android.test.0purchased",//
				10001, mPurchaseFinishedListener);
	}

	// this listener is called if there are unfinished purchase goods
	private PBIabHelper.QueryInventoryFinishedListener mGotInventoryListener = new PBIabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(PBIabResult result,
				PBInventory inventory) {
			if (result.isFailure()) {
				showWaitingLayout(false);
				return;
			}

			mPurchase = inventory.getAvailadPurchase();
			if (mPurchase != null) {
				downloadByPurchase();
				return;
			}

			showWaitingLayout(false);
		}
	};

	// this listener is called when purchase in google play over
	private PBIabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new PBIabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(PBIabResult result,
				PBPurchase purchase) {
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
				mPurchase = purchase;

				downloadByPurchase();
				return;
			}

			showErrorMsg(getString(R.string.pb_honey_shop_error_extra));
			showWaitingLayout(false);
		}
	};

	private PBIabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new PBIabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(PBPurchase purchase, PBIabResult result) {

			if (result.isSuccess()) {
				if (mLeftMaple) {
					mLeftMaple = false;
					updateUI();

					showWaitingLayout(false);
					return;
				}

				// start purchase complete screen
				Intent mIntent = new Intent(PBDownloadPurchaseActivity.this,
						PBDisplayCompleteActivity.class);
				mIntent.putExtra(PBConstant.START_PURCHASE_COMPLETE, true);
				mIntent.putExtra(PBConstant.PURCHASE_BY_MAPLE, true);
				startActivity(mIntent);
				finish();
			} else {
				HttpUtils.storeLocalTmpDownloadData("", "");
				showWaitingLayout(false);
			}
		}
	};

	private void downloadByPurchase() {

		new Thread() {
			@Override
			public void run() {
				String token = PBPreferenceUtils.getStringPref(
						PBDownloadPurchaseActivity.this, PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_TOKEN, "");
				if (TextUtils.isEmpty(token)) {
					return;
				}

				try {
					final PBPurchase purchase = new PBPurchase(
							mPurchase.getOriginalJson(),
							mPurchase.getSignature());

					Response response = PBAPIHelper.downloadPhotoByPurchase(
							token, purchase.getOriginalJson(),
							purchase.getSignature(), mPassWord);
					if (response != null
							&& response.errorCode == ResponseHandle.CODE_200_OK) {
						HttpUtils.storeLocalTmpDownloadData(mPassWord,
								response.decription);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mHelper.consumeAsync(purchase,
										mConsumeFinishedListener);
							}
						});
						return;
					} else if (response != null
							&& response.errorCode == ResponseHandle.CODE_400) {
						mLeftMaple = true;
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mHelper.consumeAsync(purchase,
										mConsumeFinishedListener);
							}
						});
						return;
					} else if (response != null && response.errorCode == 999) { // TEST
																				// ACCOUNT
						mLeftMaple = true;
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mHelper.consumeAsync(purchase,
										mConsumeFinishedListener);
							}
						});
						return;
					}

					showWaitingLayout(false);
				} catch (Exception e) {
					e.printStackTrace();
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showErrorMsg(getString(R.string.pb_honey_shop_error_extra));
					}
				});
			}
		}.start();
	}

	private boolean mLeftMaple = false;

	private void showWaitingLayout(final boolean show) {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mFrameWaitingLayout.setVisibility(show ? View.VISIBLE
						: View.GONE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// V2 google analytics has been comment out
		/*
		 * if (PBTabBarActivity.gaTracker != null) {
		 * PBTabBarActivity.gaTracker.trackPageView(TAG); }
		 */

		updateUI();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// Added below activity life cycle method for Google analytics
	@Override
	public void onStart() {
		super.onStart();
		System.out
				.println("Atik start Easy Tracker for PBDownloadPurchaseActivity");
		EasyTracker.getInstance(this).activityStart(this);
	}

	// Added below activity life cycle method for Google analytics
	@Override
	public void onStop() {
		super.onStop();
		System.out
				.println("Atik stop Easy Tracker for PBDownloadPurchaseActivity");
		EasyTracker.getInstance(this).activityStop(this);
	}

	/** class support get info purchase in background */
	private class GetPasswordThumbTask extends AsyncTask<Void, Integer, Void> {
		private String jsonDataString = "";
		private String photoListPassword = "";
		private String tokenKey = "";

		private String thumbImgPath = "";

		public GetPasswordThumbTask() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.jsonDataString = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_PURCHASE_INFO_JSON_DATA, "");
			this.photoListPassword = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_PURCHASE_INFO_PASSWORD, "");
			this.tokenKey = PBPreferenceUtils.getStringPref(
					getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_NAME_TOKEN, "");
			Log.d("DOWNLOAD", "JSON STR:" + jsonDataString);
			if (TextUtils.isEmpty(this.jsonDataString))
				return;

			// parse JSON data
			try {
				JSONObject result = new JSONObject(this.jsonDataString);
				if (result != null) {
					if (result.has("downloaded_users_count")) {
						if (mTvPurchaseNoticeDownloadedCount != null)
							mTvPurchaseNoticeDownloadedCount.setText(""
									+ result.getInt("downloaded_users_count"));
					}
					if (result.has("photos_count")) {
						if (mTvPurchaseNoticePhotoCount != null)
							mTvPurchaseNoticePhotoCount.setText(""
									+ result.getInt("photos_count"));
						mPurchaseNoticeThumbListCount.setText("1/"
								+ result.getInt("photos_count"));
					}
				}
				result = null;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			parsePurchaseInfo();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// getting photo from cache
			Bitmap bmp = BitmapFactory.decodeFile(thumbImgPath);
			if (bmp != null) {
				if (mImvPurchasePhotoThumb != null) {
					mImvPurchasePhotoThumb.setImageBitmap(bmp);
				}
			} else {
				if (mImvPurchasePhotoThumb != null) {
					mImvPurchasePhotoThumb
							.setImageResource(R.drawable.thumb_error);
				}
			}

		}

		/** parse info paurchase from server return */
		private void parsePurchaseInfo() {
			if (TextUtils.isEmpty(this.jsonDataString))
				return;
			String thumUrl = "";
			// parse JSON data
			try {
				JSONObject result = new JSONObject(this.jsonDataString);
				if (result != null) {
					if (result.has("thumb")) {
						thumUrl = result.getString("thumb");
						thumbImgPath = PBGeneralUtils
								.getPathFromCacheFolder(thumUrl);
						File file = new File(thumbImgPath);
						if (!file.exists()) {
							boolean saveImgThumbResult = PBAPIHelper.savePhoto(
									this.tokenKey, thumUrl,
									this.photoListPassword, true, null);
							Log.i("mapp", "save purchase thumb OK --> "
									+ saveImgThumbResult);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			showWaitingLayout(false);

			if (msg.what == MSG_CHECK_PWD_HONEY) {
				if (mRespone != null) {
					if (mRespone.errorCode == ResponseHandle.CODE_200_OK) {

						mHoneyBonusCount--;
						PBPreferenceUtils.saveIntPref(
								PBApplication.getBaseApplicationContext(),
								PBConstant.PREF_NAME,
								PBConstant.PREF_HONEY_BONUS, mHoneyBonusCount);
						Intent mIntent = new Intent(
								PBMainTabBarActivity.sMainContext,
								PBDisplayCompleteActivity.class);
						mIntent.putExtra(PBConstant.START_PURCHASE_COMPLETE,
								true);
						mIntent.putExtra(PBConstant.PURCHASE_BY_MAPLE, false);
						startActivity(mIntent);
						finish();
					}
				}
			} else if (msg.what == MSG_CHECK_PWD_MAPLE) {
				if (mRespone != null) {
					if (mRespone.errorCode == ResponseHandle.CODE_200_OK) {

						mMapleHoneyCount--;
						PBPreferenceUtils.saveIntPref(
								PBApplication.getBaseApplicationContext(),
								PBConstant.PREF_NAME,
								PBConstant.PREF_MAPLE_COUNT, mMapleHoneyCount);

						Intent mIntent = new Intent(
								PBMainTabBarActivity.sMainContext,
								PBDisplayCompleteActivity.class);
						mIntent.putExtra(PBConstant.START_PURCHASE_COMPLETE,
								true);
						mIntent.putExtra(PBConstant.PURCHASE_BY_MAPLE, true);
						startActivity(mIntent);
						finish();
					}
				}
			}
		}
	};

	private void showErrorMsg(String errorMsg) {
		if (PBDownloadPurchaseActivity.this.isFinishing()) {
			return;
		}
		if (TextUtils.isEmpty(errorMsg)) {
			errorMsg = getString(R.string.pb_honey_shop_error_default);
		}

		PBGeneralUtils.showAlertDialogActionWithOnClick(
				PBDownloadPurchaseActivity.this,
				android.R.drawable.ic_dialog_alert,
				getString(R.string.pb_honey_shop_error_title), errorMsg,
				getString(R.string.dialog_ok_btn),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!PBDownloadPurchaseActivity.this.isFinishing()) {
							PBDownloadPurchaseActivity.this.finish();
						}
					}
				}, false);
	}

	private String initConfirmFlag() {
		String downloadInfo = PBPreferenceUtils.getStringPref(
				PBDownloadPurchaseActivity.this, PBConstant.PREF_NAME,
				PBConstant.PREF_PURCHASE_INFO_JSON_DATA, null);
		if (TextUtils.isEmpty(downloadInfo)) {
			return null;
		}
		try {
			JSONObject jInfo = new JSONObject(downloadInfo);
			if (jInfo.getInt("client_keep_days") > 0) {
				return "DELETE";
			}
			if (jInfo.getInt("can_save") == 0) {
				return "SAVE";
			}

		} catch (Exception e) {
		}

		return null;
	}

	@Override
	protected void handleHomeActionListener() {
		// boolean isDownload =
		// PBPreferenceUtils.getBoolPref(PBDownloadPurchaseActivity.this,
		// PBConstant.PREF_NAME,
		// PBConstant.IS_DOWNLOAD, false);
		//
		// if(isDownload){
		//
		// PBPreferenceUtils.saveBoolPref(PBDownloadPurchaseActivity.this,
		// PBConstant.PREF_NAME,
		// PBConstant.IS_TAG, true);
		// PBPreferenceUtils.saveBoolPref(PBDownloadPurchaseActivity.this,
		// PBConstant.PREF_NAME,
		// PBConstant.IS_DOWNLOAD, false);
		//
		//
		// }

		finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {

	};

	/** class for downloading thumnail list for a specific password */
	private class GetThumbListForPassword extends
			AsyncTask<Void, Integer, Void> {
		String thumbImgPath = "";
		String[] mStrings = null;
		/** total photo of collection */
		int mTotalPhotos = 0;

		public GetThumbListForPassword() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String tokenKey = PBPreferenceUtils.getStringPref(
					getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_NAME_TOKEN, "");
			Response response;
			response = PBAPIHelper.getThumbnailFromServerForPurchasePassword(
					tokenKey, mPassWord);
			System.out.println("Atik response code" + response.errorCode);
			if (response.errorCode == ResponseHandle.CODE_200_OK) {
				try {
					String thumbUrl = null;
					JSONObject jsonCompleteObject = null;
					jsonCompleteObject = new JSONObject(response.decription);
					JSONArray jsonPhotosArray = jsonCompleteObject
							.getJSONArray("photos");
					System.out.println("Atik thumb length"
							+ jsonPhotosArray.length());
					mTotalPhotos = jsonPhotosArray.length();
					totalNumberThumbFromServer = mTotalPhotos;

					mStrings = new String[mTotalPhotos];
					for (int i = 0; i < mTotalPhotos; i++) {
						JSONObject jsonObject = jsonPhotosArray
								.getJSONObject(i);
						mStrings[i] = jsonObject.getString("thumb");

					}

					if (counterForTappedItem == 0) {
						if (mStrings.length == 1) { // When there is only one
													// item , index need to be
													// fix
							/*
							 * thumbUrl = mStrings[1]; thumbImgPath =
							 * PBGeneralUtils .getPathFromCacheFolder(thumbUrl);
							 * counterForTappedItem ++;
							 */
							thumbUrl = mStrings[0];
							thumbImgPath = PBGeneralUtils
									.getPathFromCacheFolder(thumbUrl);
							// counterForTappedItem ++;
						} else {
							thumbUrl = mStrings[1];
							thumbImgPath = PBGeneralUtils
									.getPathFromCacheFolder(thumbUrl);
							counterForTappedItem++;
						}

					} else {
						if (counterForTappedItem == mTotalPhotos - 1) {
							System.out.println("Atik all photos have showed");
							counterForTappedItem = 0;
							thumbUrl = mStrings[counterForTappedItem];
							thumbImgPath = PBGeneralUtils
									.getPathFromCacheFolder(thumbUrl);
							// counterForTappedItem ++;
						} else if (counterForTappedItem > 0) {
							int countThumbIndex = counterForTappedItem;
							thumbUrl = mStrings[countThumbIndex + 1];
							thumbImgPath = PBGeneralUtils
									.getPathFromCacheFolder(thumbUrl);
							counterForTappedItem++;
						}
					}

					mThumbListCacheList.add(thumbImgPath);
					mCountForCacheThumb++;
					System.out.println("Atik total number click for cache"
							+ mCountForCacheThumb);

					// Atik If file not exists , then need to save into SDCard
					// TODO need to create this file into temporary folder and
					// after exiting from this activity delete those temporary
					// files
					File file = new File(thumbImgPath);
					if (!file.exists()) {
						boolean saveImgThumbResult = false;
						try {
							saveImgThumbResult = PBAPIHelper.savePhoto(
									tokenKey, thumbUrl, mPassWord, true, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SdcardException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Log.i("mapp", "save purchase thumb OK --> "
								+ saveImgThumbResult);
					}

					/*
					 * for(int i = 0; i < mStrings.length ; i ++) {
					 * System.out.println("Atik All photo list" + mStrings[i]);
					 * }
					 */

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// getting photo from cache
			Bitmap bmp = BitmapFactory.decodeFile(thumbImgPath);
			if (bmp != null) {
				if (mImvPurchasePhotoThumb != null) {
					mImvPurchasePhotoThumb.setImageBitmap(bmp);
					if (mTotalPhotos == mCountForCacheThumb) {
						mPurchaseNoticeThumbListCount.setText(1 + "/"
								+ mTotalPhotos);
					} else {
						mPurchaseNoticeThumbListCount
								.setText(mCountForCacheThumb + 1 + "/"
										+ mTotalPhotos);
					}

				}
			} else {
				if (mImvPurchasePhotoThumb != null) {
					mImvPurchasePhotoThumb
							.setImageResource(R.drawable.thumb_error);
				}
			}

			mFrameThumbLoadLayout.setEnabled(true); // Again thumbnail button
													// enabled after server
													// operation done
		}

	}

	@Override
	public boolean dispatchKeyEvent(android.view.KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			// boolean isDownload =
			// PBPreferenceUtils.getBoolPref(PBDownloadPurchaseActivity.this,
			// PBConstant.PREF_NAME,
			// PBConstant.IS_DOWNLOAD, false);
			//
			// if(isDownload){
			//
			// PBPreferenceUtils.saveBoolPref(PBDownloadPurchaseActivity.this,
			// PBConstant.PREF_NAME,
			// PBConstant.IS_TAG, true);
			// PBPreferenceUtils.saveBoolPref(PBDownloadPurchaseActivity.this,
			// PBConstant.PREF_NAME,
			// PBConstant.IS_DOWNLOAD, false);
			//
			//
			// }
			finish();
			return true;
		}
		return super.dispatchKeyEvent(event);
	};

}
