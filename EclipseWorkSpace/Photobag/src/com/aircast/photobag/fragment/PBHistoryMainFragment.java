package com.aircast.photobag.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.RejectedExecutionException;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import com.aircast.photobag.activity.PBDownloadUpdateActivity;
import com.aircast.photobag.activity.PBHistoryInboxDetailActivity;
import com.aircast.photobag.activity.PBMainTabBarActivity;
import com.aircast.photobag.adapter.PBHistoryCursorAdapter;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.database.PBDatabaseManager;
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
public class PBHistoryMainFragment extends Fragment implements
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
	public static boolean isDeleteModeIsSelected = false;
	private WebView myWebView;
	//private View current_view;
	private View view;
//	public static TextView txtNotificationUpdate;
	
	public static boolean isAttach = false;
	// private List<String> expiredPassword = new ArrayList<String>();

	// ***//
	//private HistoryDataReceiver historyDataReceiver = new HistoryDataReceiver();

	// private UpdateHistoryTask historyTask = null;

	public static PBHistoryMainFragment createInstance() {
		return new PBHistoryMainFragment();
	}
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.pb_layout_history_main, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// and here our view is loaded

		this.view = view;
		

		mContext = getActivity().getApplicationContext();
		hasInternetWhenLoadActivity = PBApplication.hasNetworkConnection();
		mDatabaseManager = PBDatabaseManager.getInstance(mContext);
		mToken = PBPreferenceUtils.getStringPref(mContext,
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");

//		if (getActivity().getIntent().hasExtra(
//				PBConstant.PREF_PASSWORD_FROM_LIBRARY)) {
//			isFromLibrary = getActivity().getIntent().getExtras()
//					.getBoolean(PBConstant.PREF_PASSWORD_FROM_LIBRARY);
//			openpage_actionbar_home = (RelativeLayout) view
//					.findViewById(R.id.openpage_actionbar_home);
//			openpage_actionbar_home.setVisibility(View.VISIBLE);
//			view.findViewById(R.id.openpage_actionbar_content)
//					.setOnClickListener(this);
//		}

		layoutToolbarHistory = (FrameLayout) view
				.findViewById(R.id.layout_toolbar_history); // Added
		// by
		// Atik
		layoutToolbarHistory1 = (FrameLayout) view
				.findViewById(R.id.layout_toolbar_history1); // Added
																// by
																// Atik
		// by
		// Atik

		// PBTabView
//		current_view =PBMainTabBarActivity.sMainContext.mTabHost
//				.getCurrentTabView();
//		txtNotificationUpdate = (TextView)current_view
//				.findViewById(R.id.textView_number_of_update);
//        
		mHistoryAD = (LinearLayout) view.findViewById(R.id.layout_history_ad);

		mBtnInbox = (FButton) view.findViewById(R.id.toolbar_btn_inbox);
		mBtnInbox.setOnClickListener(this);
		mBtnSend = (FButton) view.findViewById(R.id.toolbar_btn_send);
		mBtnSend.setOnClickListener(this);

		mListItems = (ListView) view.findViewById(R.id.list_of_history);

		// Add advertise on listview header
		boolean hasInternet = PBApplication.hasNetworkConnection();
		String deviceLocalLanguage = "";
		deviceLocalLanguage = getString(R.string.device_local_language_japanese);

		if (hasInternet
				&& Locale.getDefault().getDisplayLanguage()
						.equalsIgnoreCase(deviceLocalLanguage)) {

			myWebView = new WebView(getActivity());
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
			// initialHistoryData(false); // initialize adapter added by Rifat
			// san
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
					getActivity().finish();
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
			if ("TRUE".equals(getActivity().getIntent()
					.getStringExtra("OUTBOX"))) {
				isInInbox = false;
			}
		} catch (Exception e) {
		}

		// Bug fix of problem -- when come from Openbag download to History
		// sometimes
		// goes to sent item after download
		if (getActivity().getIntent().hasExtra(
				PBConstant.PREF_PASSWORD_FROM_LIBRARY)) {

			isInInbox = true;
		}

		mUpdateTime = 0;

		layoutToolbarHistory.setVisibility(View.VISIBLE);
		layoutToolbarHistory1.setVisibility(View.GONE);

		mHistoryAD.setVisibility(hasInternet ? View.VISIBLE : View.GONE);

		// V2 google analytics has been comment out
		/*
		 * if (PBTabBarActivity.gaTracker != null) {
		 * PBTabBarActivity.gaTracker.trackPageView("PBHistoryMainFragment"); }
		 */

		boolean sdcardMounted = PBGeneralUtils.checkSdcard(getActivity()
				.getApplicationContext(), true, false,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialoginterface, int i) {
						// finish();
						// /PBTabBar
						// PBTabBarActivity.sMainContext.mTabHost
						// .setCurrentTabByTag(PBConstant.DOWNLOAD_TAG);
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
			// (new PBTaskGetHistoryInfo(mHandler)).execute();
		} else {
			if (PBPreferenceUtils.getBoolPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN, false)) {

				System.out.println("Atik called  when both inbox and sent item"
						+ mUpdateTime);

				PBPreferenceUtils.saveBoolPref(getActivity()
						.getApplicationContext(), PBConstant.PREF_NAME,
						PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN, false);

				PBPreferenceUtils.saveBoolPref(getActivity()
						.getApplicationContext(), PBConstant.PREF_NAME,
						PBConstant.PREF_NO_NEED_UPDATE, true);

			}

			if (PBPreferenceUtils.getBoolPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN_FOR_UPLOAD,
					false)) {

				System.out.println("Atik called  when both inbox and sent item"
						+ mUpdateTime);

				mUpdateTime = System.currentTimeMillis();

			}

		}

		initUI();

		mListItems.setSelected(true);
		initialHistoryData(true);

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

		if (!hidden) {

			layoutToolbarHistory.setVisibility(View.VISIBLE);
			layoutToolbarHistory1.setVisibility(View.GONE);

			boolean hasInternet = PBApplication.hasNetworkConnection();
			mHistoryAD.setVisibility(hasInternet ? View.VISIBLE : View.GONE);

			// V2 google analytics has been comment out
			/*
			 * if (PBTabBarActivity.gaTracker != null) {
			 * PBTabBarActivity.gaTracker
			 * .trackPageView("PBHistoryMainFragment"); }
			 */

			boolean sdcardMounted = PBGeneralUtils.checkSdcard(getActivity()
					.getApplicationContext(), true, false,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialoginterface,
								int i) {
							// finish();
							// /PBTabBar
							PBMainTabBarActivity.sMainContext.mTabHost
									.setCurrentTabByTag(PBConstant.DOWNLOAD_TAG);
						}
					});
			if (!sdcardMounted) {
				return;
			}

			System.out.println("Atik call On resume");
			// (new PBTaskGetHistoryInfoUpload(mHandler)).execute();
			System.out.println("Atik call last update time before "
					+ mUpdateTime);
			System.out.println("Atik call min time before " + MIN_UPDATE_TIME);
			System.out.println("Atik call current time "
					+ System.currentTimeMillis());

			if (System.currentTimeMillis() - mUpdateTime > MIN_UPDATE_TIME) {
				mUpdateTime = System.currentTimeMillis();
				System.out.println("Atik call when start update" + mUpdateTime);
				// (new PBTaskGetHistoryInfo(mHandler)).execute();
			} else {
				if (PBPreferenceUtils.getBoolPref(
						PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN, false)) {

					System.out
							.println("Atik called  when both inbox and sent item"
									+ mUpdateTime);

					PBPreferenceUtils.saveBoolPref(getActivity()
							.getApplicationContext(), PBConstant.PREF_NAME,
							PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN, false);

					PBPreferenceUtils.saveBoolPref(getActivity()
							.getApplicationContext(), PBConstant.PREF_NAME,
							PBConstant.PREF_NO_NEED_UPDATE, true);

				}

				if (PBPreferenceUtils.getBoolPref(
						PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN_FOR_UPLOAD,
						false)) {

					System.out
							.println("Atik called  when both inbox and sent item"
									+ mUpdateTime);

					mUpdateTime = System.currentTimeMillis();
					// (new PBTaskGetHistoryInfo(mHandler)).execute(); // Atik
					// when
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
			//initialHistoryData(true);

		}

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
	public void onResume() {
		super.onResume();
//		if(PBApplication.hasNetworkConnection()) {
//		
//		UpdateHistoryTask task = new UpdateHistoryTask(mHandler);
//		task.execute();
//		}

	}

	// Added below activity life cycle method for Google analytics
	@Override
	public void onStart() {
		super.onStart();
	//	System.out.println("Atik start Easy Tracker for PBHistoryMainActiviy");
		EasyTracker.getInstance(getActivity()).activityStart(getActivity());
		
	}

	// Added below activity life cycle method for Google analytics
	@Override
	public void onStop() {
		super.onStop();
		//System.out.println("Atik stop Easy Tracker for PBHistoryMainActiviy");
		EasyTracker.getInstance(getActivity()).activityStop(getActivity());
		
	}
	
	
	@Override
    public void onDetach() {
        super.onDetach();
        isAttach = false;
//		if(PBApplication.hasNetworkConnection()) { 
//			if(historyDataReceiver != null) {
//				try {
//					getActivity().unregisterReceiver(historyDataReceiver); 
//					historyDataReceiver = null;
//				} catch (Exception e ) {
//					// do nothing
//				}
//				
//			}
//
//		} else {
//			if(historyDataReceiver != null) {
//				try {
//					getActivity().unregisterReceiver(historyDataReceiver); 
//					historyDataReceiver = null;
//				} catch (Exception e ) {
//					// do nothing
//				}
//				
//			}
//		}
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // register here
//    	if(PBApplication.hasNetworkConnection()) {
//			IntentFilter i = new IntentFilter();
//			i.addAction("photobag.history.update");
//			getActivity().registerReceiver(historyDataReceiver, i);
//    	}
        isAttach = true;
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
//	private int updateNumber = 0;

	public void initialHistoryData(boolean forceUpdate) {
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
				mHistoryAdapter = new PBHistoryCursorAdapter(getActivity(),
						mCursor);

				mListItems.setAdapter(null);
				mListItems.setAdapter(mHistoryAdapter);
				mHistoryAdapter.notifyDataSetChanged();

//					if (isInInbox) {
//						updateNumber = 0;
//						if (mCursor.moveToFirst()) {
//							do {
//
//								if (mCursor
//										.getInt(mCursor
//												.getColumnIndex(PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE)) == 1) {
//
//									updateNumber++;
//								}
//							} while (mCursor.moveToNext());
//						}
//
//						showUpdateNotification(updateNumber);
//
//					} else {
//
//						// showUpdateNotification(0);
//					}

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

//	public void showUpdateNotification(int updateNumber) {
//		txtNotificationUpdate.setText(""+updateNumber);
//		if (updateNumber > 0) {
//
//			if (txtNotificationUpdate.getVisibility() != View.VISIBLE) {
//
//				txtNotificationUpdate.setVisibility(View.VISIBLE);
//			}
//
//		} else {
//
//			txtNotificationUpdate.setVisibility(View.GONE);
//
//		}
//
//	}

	@Override
	public void onDestroy() {
		if (mHistoryAdapter != null) {
			mHistoryAdapter.recycleAll();
			mHistoryAdapter.clearAdeper();
		}
		isAttach = false;
		super.onDestroy();

	}

	@Override
	public void onPause() {
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
					if (sSelected == PBHistoryMainFragment.DOWNLOAD_FINISH)
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

			Intent detail = new Intent(getActivity(),
					PBHistoryInboxDetailActivity.class);
			detail.putExtra("data", extras);
//			if (isFromLibrary) {
//
//				detail.putExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY, true);
//			}
			startActivity(detail);

			if (isInInbox
					&& cursor
							.getInt(cursor
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE)) == 1) {
				Intent intent = new Intent(getActivity(),
						PBDownloadUpdateActivity.class);
				intent.putExtra("data", extras);
				startActivity(intent);
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

			getActivity().finish();
			break;
		default:
			break;
		}
	}



	public void setupForDeleteModeWhenBackKeyPressed() {

		isDeleteModeIsSelected = false;
		// layoutToolbarHistory = (FrameLayout)
		// findViewById(R.id.layout_toolbar_history); // Added by Atik
		// layoutToolbarHistory1 = (FrameLayout)
		// findViewById(R.id.layout_toolbar_history1); // Added by Atik
		layoutToolbarHistory.setVisibility(View.VISIBLE);
		layoutToolbarHistory1.setVisibility(View.GONE);
		mHistoryAdapter.setDeleteMode(false);
		mHistoryAdapter.notifyDataSetChanged();
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
			layoutToolbarHistory = (FrameLayout) view
					.findViewById(R.id.layout_toolbar_history); // Added
			// by
			// Atik
			layoutToolbarHistory1 = (FrameLayout) view
					.findViewById(R.id.layout_toolbar_history1); // Added
																	// by
			// Atik
			mDelete = (FButton) view.findViewById(R.id.toolbar_btn_delete);
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

						final Dialog itemDialog = new Dialog(getActivity(),
								R.style.MyCustomDialogTheme);
						itemDialog
								.requestWindowFeature(Window.FEATURE_NO_TITLE);
						itemDialog.setCancelable(true);
						LayoutInflater inflater = LayoutInflater
								.from(getActivity());
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
						getActivity());
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
				LayoutInflater inflater = LayoutInflater.from(getActivity());

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
				getActivity().runOnUiThread(new Runnable() {
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

	// ***//
	// ***//
	// ***//
	
//	public class HistoryDataReceiver extends BroadcastReceiver {
//
//		@SuppressWarnings("unchecked")
//		@Override
//		public void onReceive(Context context, Intent intent) {
//
////			Log.d("new mgs", "new convertion");
////			Log.d("intent.getAction()", intent.getAction());
//
//			  Toast.makeText(getActivity().getApplicationContext(), "HistoryDataReceiver", 200).show();
//
//			if (intent.getExtras() != null) {
//
//				int groupStatus = intent.getExtras().getInt("status");
//				if (groupStatus == 1) {
//
////					Toast.makeText(getActivity().getApplicationContext(),
////							"Recieve Broadcast", 1000).show();
//
//					initialHistoryData(true);
//
//				} else {
//
//				}
//
//			}
//			;
//
//		}
//	}
}
