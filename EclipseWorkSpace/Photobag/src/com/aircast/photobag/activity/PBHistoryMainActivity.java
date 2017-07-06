package com.aircast.photobag.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aircast.koukaibukuro.util.Constant;
import com.aircast.photobag.R;
import com.aircast.photobag.adapter.PBHistoryCursorAdapter;
import com.aircast.photobag.adapter.PBHistoryManager;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.model.PBHistoryEntryModel;
import com.aircast.photobag.model.PBHistoryPhotoModel;
import com.aircast.photobag.utils.PBBitmapUtils;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.PBCustomWaitingProgress;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Show password list, this control both inbox and outbox.
 * <p>
 * <b>TODO Cursor used here sometimes caused crash, fix it. maybe set 2 Cursor
 * for in and out may work?</b><br>
 * added change check and time limit and works.
 * </p>
 * */
public class PBHistoryMainActivity extends Activity implements
		OnItemClickListener, OnClickListener {
	private FButton mBtnInbox, mBtnSend, mDelete;
	public static boolean isInInbox = true;
	private ListView mListItems;
	private LinearLayout mHistoryAD;
	private PBDatabaseManager mDatabaseManager;
	private PBHistoryCursorAdapter mHistoryAdapter;
	private Context mContext;
	private String mToken;
	private Cursor mCursor;
	private long mUpdateTime;
	private final long MIN_UPDATE_TIME = 1000 * 3 * 60; // comment by atik
	public static int sSelected = 0;
	public static final int DOWNLOAD_FINISH = -999;
	boolean hasInternetWhenLoadActivity = false;
	private RelativeLayout openpage_actionbar_home;
	private FrameLayout layoutToolbarHistory;
	private FrameLayout layoutToolbarHistory1;
	private boolean isDeleteModeIsSelected = false;
	private WebView myWebView;
	//private List<String> expiredPassword = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		hasInternetWhenLoadActivity = PBApplication.hasNetworkConnection();
		mDatabaseManager = PBDatabaseManager
				.getInstance(getApplicationContext());
		mToken = PBPreferenceUtils.getStringPref(mContext,
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");

		setContentView(R.layout.pb_layout_history_main);
		openpage_actionbar_home = (RelativeLayout) findViewById(R.id.openpage_actionbar_home);
		openpage_actionbar_home.setVisibility(View.VISIBLE);
		findViewById(R.id.openpage_actionbar_content).setOnClickListener(
				this);

		layoutToolbarHistory = (FrameLayout) findViewById(R.id.layout_toolbar_history); // Added
																						// by
																						// Atik
		layoutToolbarHistory1 = (FrameLayout) findViewById(R.id.layout_toolbar_history1); // Added
																							// by
																							// Atik
		// by
		// Atik


		mHistoryAD = (LinearLayout) findViewById(R.id.layout_history_ad);

		mBtnInbox = (FButton) findViewById(R.id.toolbar_btn_inbox);
		mBtnInbox.setOnClickListener(this);
		mBtnSend = (FButton) findViewById(R.id.toolbar_btn_send);
		mBtnSend.setOnClickListener(this);

		mListItems = (ListView) findViewById(R.id.list_of_history);
		
		// Add advertise on listview header
		boolean hasInternet = PBApplication.hasNetworkConnection();
		String deviceLocalLanguage = "";
		deviceLocalLanguage = getString(R.string.device_local_language_japanese);

		if (hasInternet
				&& Locale.getDefault().getDisplayLanguage()
						.equalsIgnoreCase(deviceLocalLanguage)) {

			myWebView = new WebView(this);
			myWebView.getSettings().setJavaScriptEnabled(true);

			int dpHistoryScreenListviewHeight = (int) (getResources()
					.getDimension(R.dimen.pb_history_screen_listview_height) / getResources()
					.getDisplayMetrics().density);

			int intrinsictTop = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP,
					(float) dpHistoryScreenListviewHeight, getResources()
							.getDisplayMetrics());

			myWebView.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, intrinsictTop));
			myWebView.setVerticalScrollBarEnabled(false); // stop
															// verticalscrollbar
															// to show
			myWebView.setHorizontalScrollBarEnabled(false); // stop
															// horizontalscrollbar
															// to show
			myWebView.setVisibility(View.INVISIBLE);
			myWebView.setLayoutParams(new ListView.LayoutParams(
					ListView.LayoutParams.FILL_PARENT, 1));


			mListItems.addHeaderView(myWebView);
			//initialHistoryData(false); // initialize adapter added by Rifat san 
			myWebView.addJavascriptInterface(
					new WebViewJavaScriptInterfaceForUI(), "HTMLOUT");

			myWebView.setWebViewClient(new WebViewClient() {

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					System.out.println("Atik URL Inside details.php: "
							+ url.toString());

					Intent intentForOpeningBrowser = new Intent(
							Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(intentForOpeningBrowser);
					finish();
					return false; // solve redirect problem
				}

				@Override
				public void onPageFinished(WebView view, String url) {

					myWebView
							.loadUrl("javascript:window.HTMLOUT."
									+ "processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
				}
			});
			
			myWebView
					.loadUrl(/* "http://yahoo.com" */PBConstant.ADD_UNIMEDIA_LIST);

		}

		mListItems.setOnItemClickListener(this);

		try {
			if ("TRUE".equals(getIntent().getStringExtra("OUTBOX"))) {
				isInInbox = false;
			}
		} catch (Exception e) {
		}

		// Bug fix of problem -- when come from Openbag download to History
		// sometimes
		// goes to sent item after download
		if (getIntent().hasExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY)) {

			isInInbox = true;
		}

		mUpdateTime = 0;



	}

	public static int getItemHeightofListView(ListView listView) {

		ListAdapter mAdapter = listView.getAdapter();

		int listviewElementsheight = 0;

		View childView = mAdapter.getView(1, null, listView);
		childView.measure(
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		listviewElementsheight += childView.getMeasuredHeight();

		return listviewElementsheight;

	}

	@Override
	protected void onResume() {
		super.onResume();

		layoutToolbarHistory.setVisibility(View.VISIBLE);
		layoutToolbarHistory1.setVisibility(View.GONE);

		boolean hasInternet = PBApplication.hasNetworkConnection();
		mHistoryAD.setVisibility(hasInternet ? View.VISIBLE : View.GONE);

		// V2 google analytics has been comment out
		/*
		 * if (PBTabBarActivity.gaTracker != null) {
		 * PBTabBarActivity.gaTracker.trackPageView("PBHistoryMainActivity"); }
		 */

		boolean sdcardMounted = PBGeneralUtils.checkSdcard(this, true, false,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialoginterface, int i) {
						 finish();
					}
				});
		if (!sdcardMounted) {
			return;
		}

		System.out.println("Atik call On resume");
		// (new PBTaskGetHistoryInfoUpload(mHandler)).execute();
		System.out.println("Atik call last update time before " + mUpdateTime);
		System.out.println("Atik call min time before " + MIN_UPDATE_TIME);
		System.out.println("Atik call current time "
				+ System.currentTimeMillis());

		if (System.currentTimeMillis() - mUpdateTime > MIN_UPDATE_TIME) {
			mUpdateTime = System.currentTimeMillis();
			System.out.println("Atik call when start update" + mUpdateTime);
			(new PBTaskGetHistoryInfo(mHandler)).execute();
		} else {
			if (PBPreferenceUtils.getBoolPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN, false)) {

				System.out.println("Atik called  when both inbox and sent item"
						+ mUpdateTime);

				PBPreferenceUtils.saveBoolPref(getApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN, false);

				PBPreferenceUtils.saveBoolPref(getApplicationContext(),
						PBConstant.PREF_NAME, PBConstant.PREF_NO_NEED_UPDATE,
						true);

			}

			if (PBPreferenceUtils.getBoolPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN_FOR_UPLOAD,
					false)) {

				System.out.println("Atik called  when both inbox and sent item"
						+ mUpdateTime);

				mUpdateTime = System.currentTimeMillis();
				(new PBTaskGetHistoryInfo(mHandler)).execute(); // Atik when
																// uploaded item
																// exists in
																// both inbox
																// and sent,
																// then do
																// upload
																// Atik also get
																// history again
																// after
																// uploading
																// items to
																// server

			}

		}

		initUI();

		mListItems.setSelected(true);
		initialHistoryData(true);
	}

	// Added below activity life cycle method for Google analytics
	@Override
	public void onStart() {
		super.onStart();
		System.out.println("Atik start Easy Tracker for PBHistoryMainActiviy");
		EasyTracker.getInstance(this).activityStart(this);
	}

	// Added below activity life cycle method for Google analytics
	@Override
	public void onStop() {
		super.onStop();
		System.out.println("Atik stop Easy Tracker for PBHistoryMainActiviy");
		EasyTracker.getInstance(this).activityStop(this);
	}

	/**
	 * Init UI of history
	 */
	private void initUI() {
		mBtnInbox.setSelected(isInInbox);
		mBtnSend.setSelected(!isInInbox);

		if (mBtnInbox.isSelected()) {

			mBtnInbox.setButtonColor(getResources().getColor(
					R.color.fbutton_color_shape_orange));
			mBtnSend.setButtonColor(getResources().getColor(
					R.color.fbutton_color_inbox_sent_btn_on_off));
		} else {

			mBtnSend.setButtonColor(getResources().getColor(
					R.color.fbutton_color_shape_orange));
			mBtnInbox.setButtonColor(getResources().getColor(
					R.color.fbutton_color_inbox_sent_btn_on_off));
		}
	}

	int listItemsHeight = 0;
	private int updateNumber = 0;

	private void initialHistoryData(boolean forceUpdate) {
		try {
			mCursor = mDatabaseManager
					.getHistoriesCursor(isInInbox ? PBDatabaseDefinition.HISTORY_INBOX
							: PBDatabaseDefinition.HISTORY_SENT);

			if (!mCursor.isClosed()
					&& ((mHistoryAdapter == null || mHistoryAdapter.getCount() != mCursor
							.getCount()) || forceUpdate)) {
				if (mHistoryAdapter != null) {
					mHistoryAdapter.clearAdeper();
				}
				mHistoryAdapter = new PBHistoryCursorAdapter(
						PBHistoryMainActivity.this, mCursor);

				mListItems.setAdapter(null);
				mListItems.setAdapter(mHistoryAdapter);
				mHistoryAdapter.notifyDataSetChanged();



				if (mCursor.getCount() > 0) {

					listItemsHeight = getItemHeightofListView(mListItems);
				}

			} else {
				// need to add logic here
				mHistoryAdapter.setDeleteMode(false);
				mHistoryAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void onDestroy() {
		if (mHistoryAdapter != null) {
			mHistoryAdapter.recycleAll();
			mHistoryAdapter.clearAdeper();
		}

		super.onDestroy();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PBConstant.PB_HANDLER_UPDATE_DATA_HISTORY:
				initialHistoryData(true);

				if (sSelected < 0) {
					// focus after download
					if (sSelected == PBHistoryMainActivity.DOWNLOAD_FINISH)
						sSelected = 0;
					sSelected = Math.abs(sSelected);
					setFocusListView(sSelected);
				}
				break;
			default:
				break;
			}
		};
	};

	private void setFocusListView(int position) {
		mListItems.setSelected(true);
		mListItems.setSelection(position);
		mListItems.requestFocus();
		mListItems.requestFocusFromTouch();
		mListItems.setSelection(position);
	}

	/**
	 * Download photo if file is not exist in sdcard
	 */
	private void downloadPhoto() {
		System.out.println("Atik call call download photo");

		// Check sdcard exist or not
		boolean sdcardMounted = PBGeneralUtils.checkSdcard(mContext, true,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialoginterface, int i) {
						finish();
					}
				});
		if (!sdcardMounted)
			return;

		// Get photo from server in history download
		ArrayList<PBHistoryEntryModel> download = PBHistoryManager
				.getInstance().getDownloadList();
		for (int i = 0; i < download.size(); i++) {
			PBHistoryEntryModel model = download.get(i);
			String path = PBGeneralUtils.getPathFromCacheFolder(model
					.getEntryThump());
			if (!PBGeneralUtils.checkExistFile(path)) {
				try {
					// Get thump from server to save local
					boolean isSaveThumSuccess = PBAPIHelper.savePhoto(mToken,
							model.getEntryThump(), model.getEntryPassword(),
							model.canSave(), null);
					if (!isSaveThumSuccess) {
						// Get photo from server to save local if thump not
						// found
						String urlPhoto = model.getEntryThump().substring(0,
								model.getEntryThump().lastIndexOf("?"));
						boolean isSaveSuccess = PBAPIHelper.savePhoto(mToken,
								urlPhoto, model.getEntryPassword(),
								model.canSave(), null);
						if (isSaveSuccess) {
							createThumpFile(urlPhoto, model.getEntryThump());
						}
					}
					// mIsUpdateThump = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		// Get photo from server in history upload
		ArrayList<PBHistoryEntryModel> upload = PBHistoryManager.getInstance()
				.getUploadList();
		for (int i = 0; i < upload.size(); i++) {
			PBHistoryEntryModel model = upload.get(i);

			if (PBPreferenceUtils.getBoolPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN_FOR_UPLOAD,
					false)) {

				System.out.println("Atik call entered when upload done");
				// Atik check all the photos exists in SDCard or not
				ArrayList<PBHistoryPhotoModel> mLocalPhotos = mDatabaseManager
						.getPhotos(model.getCollectionId());
				for (int j = 0; j < mLocalPhotos.size(); j++) {
					PBHistoryPhotoModel modelLocal = mLocalPhotos.get(j);
					String pathForThumb = PBGeneralUtils
							.getPathFromCacheFolder(modelLocal.getThumb());

					if (!PBGeneralUtils.checkExistFile(pathForThumb)) {
						// Atik save the file if not exist for the photo list of
						// collection id
						try {
							// Get thump from server to save local
							boolean isSaveThumSuccess = PBAPIHelper.savePhoto(
									mToken, modelLocal.getThumb(),
									model.getEntryPassword(), model.canSave(),
									null);
							if (!isSaveThumSuccess) {
								// Get photo from server to save local if thump
								// not found
								String urlPhoto = modelLocal.getThumb()
										.substring(
												0,
												modelLocal.getThumb()
														.lastIndexOf("?"));
								boolean isSaveSuccess = PBAPIHelper.savePhoto(
										mToken, urlPhoto,
										model.getEntryPassword(),
										model.canSave(), null);
								if (isSaveSuccess) {
									createThumpFile(urlPhoto,
											model.getEntryThump());
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}

			// Atik check Only thumb file exists or not
			String path = PBGeneralUtils.getPathFromCacheFolder(model
					.getEntryThump());
			System.out
					.println("Atik call entry for all file for video thumb check "
							+ path);
			if (!PBGeneralUtils.checkExistFile(path)) {
				try {
					// Get thump from server to save local
					boolean isSaveThumSuccess = PBAPIHelper.savePhoto(mToken,
							model.getEntryThump(), model.getEntryPassword(),
							model.canSave(), null);
					if (!isSaveThumSuccess) {
						// Get photo from server to save local if thump not
						// found
						String urlPhoto = model.getEntryThump().substring(0,
								model.getEntryThump().lastIndexOf("?"));
						boolean isSaveSuccess = PBAPIHelper.savePhoto(mToken,
								urlPhoto, model.getEntryPassword(),
								model.canSave(), null);
						if (isSaveSuccess) {
							createThumpFile(urlPhoto, model.getEntryThump());
						}
					}
					// mIsUpdateThump = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {

			}
		}

		// Atik update shared preference value after upload
		if (PBPreferenceUtils.getBoolPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME,
				PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN_FOR_UPLOAD, false)) {
			System.out
					.println("Atik call update shared preference when upload done");

			PBPreferenceUtils.saveBoolPref(getApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN_FOR_UPLOAD,
					false);
		}
	}

	/**
	 * Creat thum file get from server
	 * 
	 * @param urlPhoto
	 * @param urlThump
	 */
	private void createThumpFile(String urlPhoto, String urlThump) {
		Log.i("mapp", ">>> process crop real image to create thumb!");
		String realImgPath = PBGeneralUtils.getPathFromCacheFolder(urlPhoto);
		String thumbImgPath = PBGeneralUtils.getPathFromCacheFolder(urlThump);
		Bitmap bmp = null;
		try {
			Options mOptions = new Options();
			mOptions.inSampleSize = PBBitmapUtils.sampleSizeNeeded(realImgPath,
					PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD);
			bmp = PBBitmapUtils
					.centerCropImage(
							BitmapFactory.decodeFile(realImgPath, mOptions),
							PBConstant.PHOTO_THUMB_WIDTH,
							PBConstant.PHOTO_THUMB_HEIGHT);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError oom) {
			Log.e("PBHistoryMainActivity",
					">>> Create thumb file, OOM when decode image "
							+ realImgPath);
		}
		FileOutputStream fos = null;
		try {
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				fos = new FileOutputStream(new File(thumbImgPath));
			} else {
				// save on internal memory if sdcard is invalid.
				fos = PBApplication.getBaseApplicationContext().openFileOutput(
						String.valueOf(urlThump.hashCode()), 0);
			}
			if (bmp != null) {
				bmp.compress(PBConstant.COMPRESS_FORMAT,
						PBConstant.DECODE_COMPRESS_PRECENT, fos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.flush();
				fos.close();
				fos = null;
				bmp.recycle();
				bmp = null;
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String deviceLocalLanguage = "";
		deviceLocalLanguage = getString(R.string.device_local_language_japanese);
		Cursor cursor = null;
		// boolean hasInternet = PBApplication.hasNetworkConnection();
		/* if(hasInternet) { */
		if (hasInternetWhenLoadActivity
				&& Locale.getDefault().getDisplayLanguage()
						.equalsIgnoreCase(deviceLocalLanguage)) {
			cursor = (Cursor) mHistoryAdapter.getItem(arg2 - 1);

		} else {
			cursor = (Cursor) mHistoryAdapter.getItem(arg2);

		}

		// Cursor cursor = (Cursor) mHistoryAdapter.getItem(arg2);
		if (cursor != null) {
			// sSelected = -arg2;
			Bundle extras = new Bundle();
			extras.putLong(PBConstant.HISTORY_ITEM_ID, cursor.getLong(cursor
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
			extras.putBoolean(PBConstant.HISTORY_CATEGORY_INBOX, isInInbox);

			Intent detail = new Intent(PBHistoryMainActivity.this,
					PBHistoryInboxDetailActivity.class);
			detail.putExtra("data", extras);
			detail.putExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY, true);
			mContext.startActivity(detail);

			if (isInInbox
					&& cursor
							.getInt(cursor
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE)) == 1) {
				Intent intent = new Intent(PBHistoryMainActivity.this,
						PBDownloadUpdateActivity.class);
				intent.putExtra("data", extras);
				mContext.startActivity(intent);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.toolbar_btn_inbox:
			mBtnInbox.setSelected(true);
			mBtnSend.setSelected(false);
			mBtnInbox.setButtonColor(getResources().getColor(
					R.color.fbutton_color_shape_orange));
			mBtnSend.setButtonColor(getResources().getColor(
					R.color.fbutton_color_inbox_sent_btn_on_off));
			isInInbox = true;
			initialHistoryData(true);
			break;
		case R.id.toolbar_btn_send:
			mBtnInbox.setSelected(false);
			mBtnSend.setSelected(true);
			mBtnSend.setButtonColor(getResources().getColor(
					R.color.fbutton_color_shape_orange));
			mBtnInbox.setButtonColor(getResources().getColor(
					R.color.fbutton_color_inbox_sent_btn_on_off));
			isInInbox = false;
			initialHistoryData(true);
			break;

		case R.id.openpage_actionbar_content:

			finish();
			break;
		default:
			break;
		}
	}

	public class PBTaskGetHistoryInfo extends AsyncTask<Void, Void, Void> {

		private PBHistoryManager mHistory;
		private Handler mHandlerUpdateData;
		// private ProgressDialog mProgressDialog;

		private String mDeleteName;

		public PBTaskGetHistoryInfo(Handler handler) {
			mHistory = PBHistoryManager.getInstance();
			mHandlerUpdateData = handler;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			System.out.println("Atik call PBTaskGetHistoryInfo ");
			mDeleteName = "";
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String photoUrl = null;
			int saveMark = 1;
			String thumb = null;
			int addibility = 0;
			long updatedAtForJson = 0;
			int numberDownlaod = 1;
			int localSave = 0;
			boolean mIsCanSaveDataToDB = true;
			String historyId = null;
			long createDate = 0;
			long chargeDate = 0;
			String mPhotoListPassword = "";
			String adLink = "";
			String mDefaultPhotoListThumbUrl = null;
			int numOfPhoto = 0;
			int isPublic = 0;
			int accepted = 0;

			Context cxt = PBApplication.getBaseApplicationContext();
			String token = PBPreferenceUtils.getStringPref(cxt,
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");

			System.out.println("Atik call device token is " + token);

			Response response = PBAPIHelper.getHistory(token);
			Log.d(PBConstant.TAG, "[history]" + response.errorCode);
			Log.d(PBConstant.TAG, response.decription);
			if (response.errorCode != ResponseHandle.CODE_200_OK) {
				return null;
			}

			try {
				JSONObject result = new JSONObject(response.decription);

				if (result != null) {
					if (cxt == null) {
						return null;
					}

					if (result.has("upload")) {
						JSONArray upload = result.getJSONArray("upload");
						PBHistoryEntryModel entryForInsertIntoSentItem = null;
						if (upload != null) {
							boolean existAikotobaInHistorySent = false;
							//boolean isPasswordExistsInHistorySentItems = false;
							for (int i = 0; i < upload.length(); i++) {
								PBHistoryEntryModel model = new PBHistoryEntryModel();

								JSONObject objInfo = upload.getJSONObject(i);
								try {
									existAikotobaInHistorySent = mDatabaseManager
											.isPasswordExistsInHistorySent(objInfo
													.getString("id"));
//									isPasswordExistsInHistorySentItems = mDatabaseManager
//											.isPasswordExistsInHistorySentItems(objInfo
//													.getString("password"));

									if (existAikotobaInHistorySent
											) {
										model.setEntryCollectionId(objInfo
												.getString("id"));
										model.setEntryPassword(objInfo
												.getString("password"));
										model.setEntryCreateDate(objInfo
												.getLong("created_at"));
										model.setEntryNumofDownload(Integer.parseInt(objInfo
												.getString("downloaded_users_count")));
										model.setEntryNumOfPhoto(Integer.parseInt(objInfo
												.getString("photos_count")));
										model.setEntryChargeDate(objInfo
												.getLong("charges_at"));
										model.setEntryAddibility(Integer
												.parseInt(objInfo
														.getString("can_add")));
										model.setEntryMapleUsed(Integer.parseInt(objInfo
												.getString("used_maple_count")));
										model.setEntryHoneyUsed(Integer.parseInt(objInfo
												.getString("used_honey_count")));
										model.setEntrySaveMark(Integer
												.parseInt(objInfo
														.getString("can_save")));
										model.setEntrySaveDays(Integer.parseInt(objInfo
												.getString("client_keep_days")));
										model.setIsPublic(Integer.parseInt(objInfo
												.getString("is_public")));
										model.setAccepted(Integer
												.parseInt(objInfo
														.getString("accepted")));
										model.setmMessageCount(Integer.parseInt(objInfo
												.getString("message_count")));
										
										
										
									} else {

										if (objInfo != null
												&& objInfo.has("id")) {
											historyId = objInfo.getString("id");
										}

										if (objInfo.has("can_save")) {
											saveMark = objInfo
													.getInt("can_save");
										}

										if (objInfo.has("created_at")) {
											createDate = objInfo
													.getLong("created_at");
										}
										if (objInfo.has("charges_at")) {
											chargeDate = objInfo
													.getLong("charges_at");
										}
										if (objInfo
												.has("downloaded_users_count")) {
											numberDownlaod = objInfo
													.getInt("downloaded_users_count");
										}
										if (objInfo.has("can_add")) {
											addibility = objInfo
													.getInt("can_add");
										}
										if (objInfo.has("updated_at")) {
											updatedAtForJson = objInfo
													.getLong("updated_at");
										}
										if (objInfo.has("can_save")) {
											saveMark = objInfo
													.getInt("can_save");
										}
										if (objInfo.has("client_keep_days")) {
											localSave = objInfo
													.getInt("client_keep_days");
										}
										if (objInfo.has("last_photo_link")) {
											adLink = objInfo
													.getString("last_photo_link");
										}

										if (objInfo.has("password")) {
											mPhotoListPassword = objInfo
													.getString("password");
										}

										if (objInfo.has("is_public")) { // added
																		// by
																		// Atik
																		// for
																		// koukaibukuro
											isPublic = objInfo
													.getInt("is_public");
										}

										if (objInfo.has("accepted")) { // added
																		// by
																		// Atik
																		// for
																		// koukaibukuro
											isPublic = objInfo
													.getInt("accepted");
										}

										Options mOptions = new Options();

										Response res = PBAPIHelper
												.doDownloadUploadedPhotoListJsonOfPassword(
														token,
														objInfo.getString("password"),
														false);
//										System.out
//												.println("Atik call response for download:"
//														+ res.errorCode);
//										System.out
//												.println("Atik call response for download"
//														+ res.decription);

										// TODO Atik need response checking then
										// parse and update history table for
										// photos

										JSONObject jsonRoot;
										boolean saveImgThumbResult = false;
										boolean saveRealImgResult = false;
										FileInputStream fis = null;

										try {
											jsonRoot = new JSONObject(
													res.decription);
											JSONArray jsonPhotosArray;
											jsonPhotosArray = jsonRoot
													.getJSONArray("photos");
											mDefaultPhotoListThumbUrl = jsonPhotosArray
													.getJSONObject(0)
													.getString("thumb");

											numOfPhoto = jsonPhotosArray
													.length();

											for (int j = 0; j < jsonPhotosArray
													.length(); j++) {

												JSONObject jsonObject = jsonPhotosArray
														.getJSONObject(j);
												String imageURL = jsonObject
														.optString("url")
														.toString();
												System.out
														.println("Atik call image url"
																+ imageURL);
												String thumbURL = jsonObject
														.optString("thumb")
														.toString();
												System.out
														.println("Atik call thumb url"
																+ thumbURL);

												int mediaType = 0;
												if (!TextUtils
														.isEmpty(imageURL)
														&& imageURL
																.contains("video")) {
													mediaType = PBDatabaseDefinition.MEDIA_VIDEO;
												} else { // default in this case
															// is a photo.
													mediaType = PBDatabaseDefinition.MEDIA_PHOTO;
												}

												mDefaultPhotoListThumbUrl = saveMark != 0 ? mDefaultPhotoListThumbUrl
														: mDefaultPhotoListThumbUrl
																+ "?can_save=0";

												if (jsonObject != null
														&& jsonObject
																.has("thumb")) {
													thumb = jsonObject
															.getString("thumb");
													try {
														saveImgThumbResult = PBAPIHelper
																.savePhoto(
																		token,
																		thumb,
																		objInfo.getString("password"),
																		(saveMark != 0),
																		null);
													} catch (Exception e) {
														Log.w("mapp",
																"[download] getting thumb from server error: "
																		+ e.getMessage());
													}
													Log.i("mapp",
															"save image thumb OK --> "
																	+ saveImgThumbResult);
												}

												// Atik coding start from here
												// 2014-08-06
												// parse "url" for real image
												if (jsonObject.has("url")) {
													photoUrl = jsonObject
															.getString("url");
													// 20120521 add support
													// download format with
													// video file
													// <S>
													if (photoUrl
															.contains("video")) {
														photoUrl = photoUrl
																+ PBConstant.VIDEO_FORMAT_3GP;
													}
													try {
														saveRealImgResult = PBAPIHelper
																.savePhoto(
																		token,
																		photoUrl,
																		objInfo.getString("password"),
																		(jsonRoot
																				.getInt("can_save") != 0),
																		null);

													} catch (IOException e) {
														// Atik TODO check
														// SDCARD full or not
													}

												}

												if (!TextUtils
														.isEmpty(photoUrl)
														&& photoUrl
																.contains("video")) {
													mediaType = PBDatabaseDefinition.MEDIA_VIDEO;
												} else { // default in this case
															// is a photo.
													mediaType = PBDatabaseDefinition.MEDIA_PHOTO;
												}

												// 20120809 added by NhatVT,
												// support saving photo or video
												// <E>

												// 20120215 add by NhatVT <S>
												// if we cannot get thumb from
												// server -> crop real image to
												// 150x150
												// 20120419 moved realImgPath
												// from if clause by TinhNH1 <S>
												String realImgPath = PBGeneralUtils
														.getPathFromCacheFolder(saveMark != 0 ? photoUrl
																: photoUrl
																		+ "?can_save=0");

												// 20120419 by TinhNH1 <E>
												if (!saveImgThumbResult
														&& mediaType == PBDatabaseDefinition.MEDIA_PHOTO) {
													Log.i("mapp",
															">>> process crop real image to create thumb!");
													String thumbImgPath = PBGeneralUtils
															.getPathFromCacheFolder(thumb);
													if (PBBitmapUtils
															.isPhotoValid(realImgPath)) {
														mOptions.inSampleSize = PBBitmapUtils
																.sampleSizeNeeded(
																		realImgPath,
																		PBConstant.MAXIMUM_IMAGE_SIZE_UPLOAD);
														Bitmap bmp = PBBitmapUtils
																.centerCropImage(
																		BitmapFactory
																				.decodeFile(
																						realImgPath,
																						mOptions),
																		PBConstant.PHOTO_THUMB_WIDTH,
																		PBConstant.PHOTO_THUMB_HEIGHT);
														try {
															FileOutputStream fos = null;
															if (android.os.Environment
																	.getExternalStorageState()
																	.equals(android.os.Environment.MEDIA_MOUNTED)) {
																fos = new FileOutputStream(
																		new File(
																				thumbImgPath));
															} else {

															}
															bmp.compress(
																	PBConstant.COMPRESS_FORMAT,
																	PBConstant.DECODE_COMPRESS_PRECENT,
																	fos);
															// release resources
															// after saving
															// photo DONE!
															if (fos != null) {
																fos.flush();
																fos.close();
																fos = null;
															}
															if (bmp != null) {
																bmp.recycle();
																bmp = null;
															}
														} catch (Exception e) {
															e.printStackTrace();
														}
													} else {
														Log.w("Atik call",
																"cannot create thumb from photo, photo is invalid!");
													}
													// 20120215 add by NhatVT
													// <S>
												}

												if (mIsCanSaveDataToDB) {
													long duration = 0;
													if (mediaType == PBDatabaseDefinition.MEDIA_VIDEO) {
														try {
															File media = new File(
																	realImgPath);
															fis = new FileInputStream(
																	media);
															MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
															mediaMetadataRetriever
																	.setDataSource(fis
																			.getFD());

															duration = Long
																	.parseLong(mediaMetadataRetriever
																			.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
															Bitmap thumb_video;
															thumb_video = ThumbnailUtils
																	.createVideoThumbnail(
																			realImgPath,
																			MediaStore.Video.Thumbnails.MINI_KIND);

															if (thumb_video != null) {
																Log.i("mapp",
																		"> downloader > process saving video frame thumb! "
																				+ thumb_video
																						.getWidth()
																				+ "x"
																				+ thumb_video
																						.getHeight());
																boolean savingResult = PBBitmapUtils
																		.saveBitmapToSdcard(
																				thumb_video,
																				photoUrl,
																				true,
																				true,
																				true);
																Log.i("mapp",
																		"> downloader > process saving video frame thumb! "
																				+ savingResult);
															} else {
																Log.e("mapp",
																		"> downloader > CAN NOT extract video thumbnail");
															}
														} catch (Exception e) {
															Log.e("Atik call",
																	"Cannot get video information, cause: "
																			+ e.toString());
														}
														// 20120427 only get
														// video frame when this
														// file is a
														// video file <E>
													}
													// 20120416 <E>
													photoUrl = saveMark != 0 ? photoUrl
															: photoUrl
																	+ "?can_save=0";
													thumb = saveMark != 0 ? thumb
															: thumb
																	+ "?can_save=0";

													if (!existAikotobaInHistorySent
															) {
														mDatabaseManager
																.setPhoto(
																		new PBHistoryPhotoModel(
																				photoUrl,
																				thumb,
																				historyId,
																				mediaType,
																				mediaType == PBDatabaseDefinition.MEDIA_VIDEO ? duration
																						: 0),
																		PBDatabaseDefinition.HISTORY_SENT);
													}

												}

											}
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}

								} catch (Exception ex) {
									ex.printStackTrace();
								}
								
								
								

								// Atik check whether password exists in history
								// inbox or not

								existAikotobaInHistorySent = mDatabaseManager
										.isPasswordExistsInHistorySent(objInfo
												.getString("id"));
//								isPasswordExistsInHistorySentItems = mDatabaseManager
//										.isPasswordExistsInHistorySentItems(objInfo
//												.getString("password"));

								if (!existAikotobaInHistorySent
										) {

									entryForInsertIntoSentItem = new PBHistoryEntryModel(
											System.currentTimeMillis(),
											historyId, mPhotoListPassword,
											createDate, chargeDate, numOfPhoto,
											numberDownlaod,
											mDefaultPhotoListThumbUrl,
											addibility, updatedAtForJson,
											saveMark, localSave, isPublic,
											accepted); // added by Atik for
														// koukaibukuro
									// Update information of upload into
									// database
									
									int dublicateCounter = 0;
									
									Cursor cursor = mDatabaseManager.getHistoriesCursor(PBDatabaseDefinition.HISTORY_INBOX);
									//System.out.println("Atik number of Inbox cursor is:"+cursor.getCount());
									String currentCollectionId = entryForInsertIntoSentItem.getCollectionId();
									if (cursor.moveToFirst()){
										   while(!cursor.isAfterLast()){
											  String collection_Id = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
											  String password = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PASSWORD));
											  System.out.println("collection_Id:"+collection_Id);
											  if(!collection_Id.equalsIgnoreCase(currentCollectionId) && password.equalsIgnoreCase(entryForInsertIntoSentItem.getEntryPassword())) {
										    	 // collection_Id = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
										    	  dublicateCounter++;
										      }
										      
										      cursor.moveToNext();
										   }
										}
									
									if(dublicateCounter > 0){
										
										String tmpPassword = entryForInsertIntoSentItem.getEntryPassword();
										entryForInsertIntoSentItem.setEntryPassword(tmpPassword+" ("+dublicateCounter+")");
									}
									if(PBAPIContant.DEBUG){
										
										System.out.println(dublicateCounter);
										System.out.println(entryForInsertIntoSentItem.getEntryPassword());
									}

								

									mDatabaseManager.insertHistory(
											entryForInsertIntoSentItem,
											PBDatabaseDefinition.HISTORY_SENT);
									entryForInsertIntoSentItem
											.setEntryAdLink(adLink);

								}

								// @lent5 check key upload photos fail with
								// number of photos = 0
								// Log.w("testing", "" +
								// Integer.parseInt(objInfo.getString("photos_count")));
								if (model.getEntryNumOfPhoto() != 0) {
									existAikotobaInHistorySent = mDatabaseManager
											.isPasswordExistsInHistorySent(objInfo
													.getString("id"));
//									isPasswordExistsInHistorySentItems = mDatabaseManager
//											.isPasswordExistsInHistorySentItems(objInfo
//													.getString("password"));
									if (existAikotobaInHistorySent
											) {
										
										String secretCode = mDatabaseManager.getPassswordSecretDigit(model.getCollectionId());
										model.setFourDigit(secretCode);
										Log.d("upload history ", ""+objInfo
												.getString("id4"));
									
									if(objInfo.has("id4")){
										model.setFourDigit(objInfo
												.getString("id4"));
									}
										mDatabaseManager
												.setHistory(
														model,
														PBDatabaseDefinition.HISTORY_SENT);
									} else {
									}
								}
							}

							if (upload.length() > 0) {
								ArrayList<PBHistoryEntryModel> uploadLocal = mDatabaseManager
										.getHistories(PBDatabaseDefinition.HISTORY_SENT);
								System.out
										.println("Atik call total histor sent item size"
												+ uploadLocal.size());
								mHistory.setUploadList(uploadLocal);
							}
						}
					}

					if (result.has("download")) {
						JSONArray download = result.getJSONArray("download");
						if (download != null) {

							List<PBHistoryEntryModel> historyArray = mDatabaseManager
									.getHistories(PBDatabaseDefinition.HISTORY_INBOX);
							Map<String, PBHistoryEntryModel> histories = new HashMap<String, PBHistoryEntryModel>();
							for (PBHistoryEntryModel history : historyArray) {
								String collectionId = history.getCollectionId();
								if (histories.containsKey(collectionId)) {
									if (history.getEntryUpdatedAt() <= histories
											.get(collectionId)
											.getEntryUpdatedAt()) {
										continue;
									}
								}
								if (isSaveExpired(history)) {
									if (!TextUtils.isEmpty(mDeleteName)) {
										mDeleteName += "、";
									}
									mDeleteName = mDeleteName + "「"
											+ history.getEntryPassword() + "」";
									mDatabaseManager.deleteHistory(String
											.valueOf(history.getEntryId()),
											history.getCollectionId());
									PBAPIHelper.deleteDownloadedCollection(
											history.getCollectionId(), token);
								} else {
									histories.put(collectionId, history);
								}
							}

							for (int i = 0; i < download.length(); i++) {
								PBHistoryEntryModel model = new PBHistoryEntryModel();
								JSONObject objInfo = download.getJSONObject(i);
								try {
									model.setEntryCollectionId(objInfo
											.getString("id"));
									model.setEntryPassword(objInfo
											.getString("password"));
									model.setEntryCreateDate(objInfo
											.getLong("created_at"));
									model.setEntryNumofDownload(Integer.parseInt(objInfo
											.getString("downloaded_users_count")));
									model.setEntryChargeDate(objInfo
											.getLong("charges_at"));
									model.setEntryNumOfPhoto(Integer
											.parseInt(objInfo
													.getString("photos_count")));
									model.setEntryAddibility(Integer
											.parseInt(objInfo
													.getString("can_add")));
									model.setEntryMapleUsed(Integer.parseInt(objInfo
											.getString("used_maple_count")));
									model.setEntryHoneyUsed(Integer.parseInt(objInfo
											.getString("used_honey_count")));
									model.setEntrySaveMark(Integer
											.parseInt(objInfo
													.getString("can_save")));
									model.setEntrySaveDays(Integer.parseInt(objInfo
											.getString("client_keep_days")));
									model.setIsPublic(Integer.parseInt(objInfo
											.getString("is_public")));
									model.setAccepted(Integer.parseInt(objInfo
											.getString("accepted")));
									model.setmMessageCount(Integer.parseInt(objInfo
											.getString("message_count")));
									
//									model.setFourDigit(objInfo
//											.getString("id4"));
									
									model.setmExpiresAt(objInfo
											.getLong("expires_at"));
									
									
								} catch (Exception ex) {
									ex.printStackTrace();
								}

								// set updatability
								model.setEntryIsUpdatable(0);
								
									if (objInfo.getLong("updated_at") > 0) {
										if (histories.containsKey(model
												.getCollectionId())) {
											String key = model.getCollectionId();
											long updatedAt = histories.get(key)
													.getEntryUpdatedAt();
											if (updatedAt > 0) {
												model.setEntryUpdatedAt(updatedAt);
												if (objInfo.getLong("updated_at") > updatedAt) {
													model.setEntryIsUpdatable(1);
												}
											} else {
												model.setEntryUpdatedAt(objInfo
														.getLong("updated_at"));
											}
											histories.remove(key);
										} else {
											try {
												model.setEntryUpdatedAt(objInfo
														.getLong("updated_at"));
											} catch (Exception ex) {
												ex.printStackTrace();
											}
										}
									}
									
									if(Long.parseLong(objInfo.getString("expires_at")) < Long.parseLong(objInfo.getString("updated_at"))){
									
									   model.setEntryIsUpdatable(0);
									   model.setEntryAddibility(0);
									   //expiredPassword.add(model.getEntryPassword());
									   
								       }
								

								// Update information of download into database
								if (isSaveExpired(model)
										&& model.getEntryId() > 0) {
									PBAPIHelper.deleteDownloadedCollection(
											model.getCollectionId(), token);
								} else {
									
//									String password = model.getEntryPassword();
//									  if(mDatabaseManager.isPasswordExistsInHistoryInbox(model.getCollectionId())){
//										  
//										  password = mDatabaseManager.getPassord(model.getCollectionId(), PBDatabaseDefinition.HISTORY_INBOX);
//									  }
//									  
//									  if(mDatabaseManager.isPasswordExistsInHistorySent(model.getCollectionId())){
//										  
//										  password = mDatabaseManager.getPassord(model.getCollectionId(), PBDatabaseDefinition.HISTORY_SENT);
//									  }
//									  model.setEntryPassword(password);
									mDatabaseManager.setHistory(model,
											PBDatabaseDefinition.HISTORY_INBOX);
								}
							}

							// set entries non-updatable if it is removed from
							// server because of expiration
							for (PBHistoryEntryModel localHistory : histories
									.values()) {

								if (localHistory.getEntryIsUpdatable() == 1
										|| localHistory.getEntryAddibility() == 1) {
									localHistory.setEntryIsUpdatable(0);
									localHistory.setEntryAddibility(0);
//									String password = localHistory.getEntryPassword();
//									  if(mDatabaseManager.isPasswordExistsInHistoryInbox(localHistory.getCollectionId())){
//										  
//										  password = mDatabaseManager.getPassord(localHistory.getCollectionId(), PBDatabaseDefinition.HISTORY_INBOX);
//									  }
//									  
//									  if(mDatabaseManager.isPasswordExistsInHistorySent(localHistory.getCollectionId())){
//										  
//										  password = mDatabaseManager.getPassord(localHistory.getCollectionId(), PBDatabaseDefinition.HISTORY_SENT);
//									  }
//									  localHistory.setEntryPassword(password);
									mDatabaseManager.setHistory(localHistory,
											PBDatabaseDefinition.HISTORY_INBOX);
								}
							}

							if (download.length() > 0) {
								ArrayList<PBHistoryEntryModel> downloadLocal = mDatabaseManager
										.getHistories(PBDatabaseDefinition.HISTORY_INBOX);

								if (downloadLocal == null
										|| downloadLocal.size() == 0) {
									return null;
								}
								PBHistoryManager.getInstance().setDownloadList(
										downloadLocal);
							}
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			downloadPhoto();
			return null;
		}

		private boolean isSaveExpired(PBHistoryEntryModel model) {
			final long SAVE_TIME = model.getEntrySaveDays() * 24 * 60 * 60
					* 1000;
			if (SAVE_TIME == 0)
				return false;
			try {
				return System.currentTimeMillis() - model.getEntryId() > SAVE_TIME;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return false;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (mHandlerUpdateData != null) {
				mHandlerUpdateData
						.sendEmptyMessage(PBConstant.PB_HANDLER_UPDATE_DATA_HISTORY);
			}
			if (!TextUtils.isEmpty(mDeleteName) && mHandlerUpdateData != null) {
				mHandlerUpdateData.post(new Runnable() {
					@Override
					public void run() {
						PBGeneralUtils
								.showAlertDialogActionWithOnClick(
										PBHistoryMainActivity.this,
										android.R.drawable.ic_dialog_alert,
										getString(R.string.pb_local_save_expire_title),
										String.format(
												getString(R.string.pb_local_save_expire_content),
												mDeleteName),
										getString(R.string.dialog_ok_btn),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
											}
										}, false);
					}
				});
			}
		}
	}


	@Override
	public void onBackPressed() {

			finish();

	}

	private DeleteCollectionHistory mDeleteCollectionHistory;
	private String mCollectionId;
	private long mHistoryId;
	private SparseBooleanArray mCheckStates;

	public void refreshContent() {

		// Toast.makeText(getApplicationContext(), "click history Atik",
		// 500).show();

		// layoutToolbarHistory.setV
		if (mHistoryAdapter.getCount() > 0) {
			mHistoryAdapter.setselectedList();
			isDeleteModeIsSelected = true;
			layoutToolbarHistory = (FrameLayout) findViewById(R.id.layout_toolbar_history); // Added
																							// by
																							// Atik
			layoutToolbarHistory1 = (FrameLayout) findViewById(R.id.layout_toolbar_history1); // Added
																								// by
			// Atik
			mDelete = (FButton) findViewById(R.id.toolbar_btn_delete);
			mDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					boolean isDialogOpen = false;
					mCheckStates = mHistoryAdapter.getselectedItem();
					/* if (mCheckStates.size() > 0) { */
					for (int i = 0; i < mHistoryAdapter.getCount(); i++) {
						if (mCheckStates.get(i)) {
							isDialogOpen = true;
						}
					}
					if (isDialogOpen) {

						final Dialog itemDialog = new Dialog(
								PBHistoryMainActivity.this,
								R.style.MyCustomDialogTheme);
						itemDialog
								.requestWindowFeature(Window.FEATURE_NO_TITLE);
						itemDialog.setCancelable(true);
						LayoutInflater inflater = LayoutInflater
								.from(PBHistoryMainActivity.this);
						final View dialoglayout = inflater.inflate(
								R.layout.history_item_delete_dialog, null);

						List<String> passwords = new ArrayList<String>();
						for (int i = 0; i < mHistoryAdapter.getCount(); i++) {
							if (mCheckStates.get(i)) {

								Cursor cursor = (Cursor) mHistoryAdapter
										.getItem(i);

								passwords.add(cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PASSWORD)));
							}

						}

						ListView listView = (ListView) dialoglayout
								.findViewById(R.id.listView_delete_password);

						listView.setClickable(false);
						listView.setAdapter(new DeletePasswordAdapter(passwords));
						dialoglayout.findViewById(R.id.button_delete_cancel)
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										itemDialog.cancel();

									}

								});

						dialoglayout.findViewById(R.id.button_delete_ok)
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										itemDialog.cancel();

										mDeleteCollectionHistory = new DeleteCollectionHistory(
												mContext, isInInbox);
										try {
											mDeleteCollectionHistory.execute();
										} catch (RejectedExecutionException e) {
											e.printStackTrace();
										}

									}

								});

						itemDialog.setContentView(dialoglayout);
						itemDialog.getWindow().setBackgroundDrawable(
								new ColorDrawable(
										android.graphics.Color.TRANSPARENT));

						itemDialog.show();

					}

				}
			});

			layoutToolbarHistory.setVisibility(View.GONE);
			layoutToolbarHistory1.setVisibility(View.VISIBLE);
			mHistoryAdapter.setDeleteMode(true);
			mHistoryAdapter.notifyDataSetChanged();
			// Toast.makeText(getApplicationContext(), "click history",
			// 500).show();

		}

	}

	/**
	 * Delete collection id in history send
	 */
	public class DeleteCollectionHistory extends AsyncTask<Void, Void, Void> {

		private Context mContext;
		private Response mResponse;
		private boolean isInbox;
		private PBCustomWaitingProgress mCustomWaitingLayout;
		private boolean isError = false;

		public DeleteCollectionHistory(Context context, boolean isInbox) {
			mContext = context;
			this.isInbox = isInbox;
		}

		@Override
		protected Void doInBackground(Void... params) {

			for (int i = 0; i < mHistoryAdapter.getCount(); i++) {
				if (mCheckStates.get(i)) {
					String token = PBPreferenceUtils.getStringPref(mContext,
							PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN,
							"");
					Cursor cursor = (Cursor) mHistoryAdapter.getItem(i);
					mCollectionId = cursor
							.getString(cursor
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));

					mHistoryId = cursor
							.getLong(cursor
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ID));
					if (isInbox) {

						mResponse = PBAPIHelper.deleteDownloadedCollection(
								mCollectionId, token);

					} else {

						mResponse = PBAPIHelper.deleteUploadedCollection(
								mCollectionId, token);
					}

					if (mResponse.errorCode == HttpStatus.SC_OK
							|| mResponse.errorCode == HttpStatus.SC_NOT_FOUND) {

						PBDatabaseManager.getInstance(mContext).deleteHistory(
								String.valueOf(mHistoryId), mCollectionId);

					} else {

						isError = true;
					}

				}

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mCustomWaitingLayout != null) {
				mCustomWaitingLayout.hideWaitingLayout();
			}

			if (isError) {

				PBGeneralUtils
						.showAlertDialogAction(
								mContext,
								mContext.getString(R.string.dialog_network_error_title),
								mContext.getString(R.string.dialog_network_error_body),
								null, mContext
										.getString(R.string.dialog_ok_btn),
								null);

			}

			mHistoryAdapter.setselectedList();
			initialHistoryData(false);

			layoutToolbarHistory.setVisibility(View.VISIBLE);
			layoutToolbarHistory1.setVisibility(View.GONE);

			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			if (mContext != null) {
				mCustomWaitingLayout = new PBCustomWaitingProgress(
						PBHistoryMainActivity.this);
				mCustomWaitingLayout.showWaitingLayout();
			}
			super.onPreExecute();
		}
	}


	private class DeletePasswordAdapter extends BaseAdapter {

		List<String> list;

		public DeletePasswordAdapter(List<String> list) {
			super();
			this.list = list;
		}

		public int getCount() {

			return list.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {

			ViewHolder holder;
			if (convertView == null) {

				holder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater
						.from(PBHistoryMainActivity.this);

				convertView = inflater.inflate(R.layout.delete_password_item,
						parent, false);

				holder.passwornName = (TextView) convertView
						.findViewById(R.id.textView_password);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			holder.passwornName.setText(list.get(position));

			return convertView;

		}

		private class ViewHolder {
			public TextView passwornName;
		}

	}


	private class WebViewJavaScriptInterfaceForUI {
		@JavascriptInterface
		@SuppressWarnings("unused")
		public void processHTML(String htmlDataFromWebView) {

			// process the html as needed by the app
			System.out.println("Atik get html data from webview data length:"
					+ htmlDataFromWebView.length());
			System.out.println("Atik get html data from webview:"
					+ htmlDataFromWebView);
			int dataSizeLimit = Constant.isProductionLib ? PBConstant.MINIMUM_SIZE_ADVERTISE_DATA
					: PBConstant.MINIMUM_SIZE_ADVERTISE_DATA_DEVELOPMENT;
			if (htmlDataFromWebView.length() > dataSizeLimit) {
				// isWebViewDataLoadedProperly = true;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						if (listItemsHeight == 0) {

							listItemsHeight = 100;
						}

						myWebView.setVisibility(View.VISIBLE);

						int dpHistoryScreenListviewHeight = (int) (getResources()
								.getDimension(
										R.dimen.pb_history_screen_listview_height) / getResources()
								.getDisplayMetrics().density);

						int intrinsictTop = (int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP,
								(float) dpHistoryScreenListviewHeight,
								getResources().getDisplayMetrics());
						myWebView.setLayoutParams(new AbsListView.LayoutParams(
								LayoutParams.MATCH_PARENT, intrinsictTop));

						myWebView.setLayoutParams(new ListView.LayoutParams(
								ListView.LayoutParams.FILL_PARENT,
								intrinsictTop));

						mHistoryAdapter.notifyDataSetChanged();
					}
				});
			}

		}
	}

}
