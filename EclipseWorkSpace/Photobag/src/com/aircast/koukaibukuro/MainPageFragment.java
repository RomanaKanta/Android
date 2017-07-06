package com.aircast.koukaibukuro;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.koukaibukuro.adapter.CommonAdapter;
import com.aircast.koukaibukuro.database.DatabaseHandler;
import com.aircast.koukaibukuro.model.Password;
import com.aircast.koukaibukuro.util.ApiHelper;
import com.aircast.koukaibukuro.util.ApiHelper.Response;
import com.aircast.koukaibukuro.util.Constant;
import com.aircast.koukaibukuro.util.KBDownLoadManager;
import com.aircast.koukaibukuro.util.ProgressHUD;
import com.aircast.koukaibukuro.util.SPreferenceUtils;
import com.aircast.koukaibukuro.util.Util;
import com.aircast.koukaibukuro.widget.MultipleOrientationSlidingDrawer;
import com.aircast.koukaibukuro.widget.MultipleOrientationSlidingDrawer.OnDrawerCloseListener;
import com.aircast.koukaibukuro.widget.MultipleOrientationSlidingDrawer.OnDrawerOpenListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBMainTabBarActivity;
import com.aircast.photobag.activity.PBMyPageAcornExchangeListActivity;
import com.aircast.photobag.activity.PBPurchaseActivity;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.utils.PBPreferenceUtils;

public class MainPageFragment extends Fragment implements OnClickListener {

	private View view;
	boolean isOpen = false;
	private CommonAdapter adapter;
	private List<Password> list = new ArrayList<Password>();
	private List<Password> listWithRanking = new ArrayList<Password>();

	private List<Password> listWithRecommened = new ArrayList<Password>();
	//private PullToRefreshListView listview;
	private PullToRefreshListView pullRefreshListview;
	private ListView mainPageListview;
	//private PullToRefreshListView mPullRefreshListView;


	private ArrayList<HashMap<String, String>> historyItemList;
	private static ProgressHUD mProgressHUD;
	private boolean isLocalArrivalUpdate = true;
	private boolean isLocalRankingUpdate = true;
	private boolean isLocalRecommenedUpdate = true;

	private TextView arrivalTextView = null;
	private TextView rankingTextView = null;
	private ImageView arrivalImageView = null;
	private ImageView rankingImageView = null;
	private ImageView recommendedImageView = null;
	private TextView recommendedTextView = null;
	private boolean isArrivalTabSelected = true;
	private boolean isRankingTabSelected = false;
	private boolean isRecommendedTabSelected = false;
	//private Button btn_refresh;
	private Animation rotation;
	private PBDatabaseManager mDatabaseManager;
	private static final String TAG = MainPageFragment.class.getName();
	private WebView webview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater
				.inflate(R.layout.kb_mainpage_fragment, container, false);
		DatabaseHandler db = new DatabaseHandler(getActivity()
				.getApplicationContext());
		mDatabaseManager = PBDatabaseManager
				.getInstance(PBMainTabBarActivity.sMainContext);
		mProgressHUD = new ProgressHUD(getActivity());
		pullRefreshListview = (PullToRefreshListView) view
				.findViewById(R.id.listView_common);
		pullRefreshListview.setMode(Mode.BOTH);
		mainPageListview = pullRefreshListview.getRefreshableView();
		
		// Set a listener to be invoked when the list should be refreshed.
		pullRefreshListview.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				new GetNewDataTask().execute();
			}
		});

		view.findViewById(R.id.mainpage_segmented_btn1)
				.setOnClickListener(this);
		view.findViewById(R.id.mainpage_segmented_btn2)
				.setOnClickListener(this);
		view.findViewById(R.id.mainpage_segmented_btn3)
				.setOnClickListener(this);
		arrivalTextView = (TextView) view.findViewById(R.id.arrivalTextView);
		rankingTextView = (TextView) view.findViewById(R.id.rankingTextView);
		arrivalImageView = (ImageView) view.findViewById(R.id.arrivalImageView);
		rankingImageView = (ImageView) view.findViewById(R.id.rankingImageView);

		recommendedTextView = (TextView) view
				.findViewById(R.id.recommendedTextView);
		recommendedImageView = (ImageView) view
				.findViewById(R.id.recommendedImageView);

		adapter = new CommonAdapter(getActivity(), list, false, false,
				historyItemList, false);
		final MultipleOrientationSlidingDrawer drawer = (MultipleOrientationSlidingDrawer) view
				.findViewById(R.id.drawer);
		final Button btnUpArrow = new Button(getActivity());
		btnUpArrow.setBackgroundResource(R.drawable.down_arrow);
		final RelativeLayout handel = (RelativeLayout) view
				.findViewById(R.id.handle_c);

		int intrinsicWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, (float) 26, getResources()
						.getDisplayMetrics()); // convert 20 dip to int

		
		

		int intrinsicHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, (float) 30, getResources()
						.getDisplayMetrics()); // convert 10 dip to int

		
		if(PBAPIContant.DEBUG){
			System.out.println("Atik intrinsic width:" + intrinsicWidth);
			System.out.println("Atik intrinsic height:" + intrinsicHeight);
		}
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				intrinsicWidth, intrinsicHeight);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		handel.addView(btnUpArrow, params);
		drawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				btnUpArrow.setBackgroundResource(R.drawable.up_arrow);
				pullRefreshListview.setMode(Mode.DISABLED);
				mainPageListview.setScrollContainer(false);
				mainPageListview.setClickable(false);
			}

		});

		drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				pullRefreshListview.setMode(Mode.PULL_FROM_START);
				mainPageListview.setScrollContainer(true);
				mainPageListview.setClickable(true);
				btnUpArrow.setBackgroundResource(R.drawable.down_arrow);
			}

		});


		view.findViewById(R.id.listView_common).setOnTouchListener(
				new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {

						if (drawer.isOpened()) {

							drawer.close();

						}

						return false;
					}

				});

		rotation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.refresh_button_rotate);
		rotation.setRepeatCount(Animation.INFINITE);

//		btn_refresh = (Button) view.findViewById(R.id.button_refresh);
//
//		btn_refresh.setOnClickListener(this);
		view.findViewById(R.id.button_donguri).setOnClickListener(this);
		view.findViewById(R.id.button_goto_matomegaipage).setOnClickListener(this);

		if (Util.isOnline(getActivity())) {

			mProgressHUD.show(true);
			OpenPagePasswordListTask task = new OpenPagePasswordListTask();
			task.execute();
		}

		try {
			historyItemList = mDatabaseManager.getAllHistoriesCursor();
			loadDataFromLocal();
		} catch (Exception e) {
			e.printStackTrace();
		}

		webview = (WebView) view
			    .findViewById(R.id.webview_mainpage_recommendation);
		return view;
	}

	boolean isarrivalDataSync = false;
	boolean isRankingDataSync = false;
	boolean isRecommenedDataSync = false;
	private int position = 0;

	@Override
	public void onResume() {
		super.onResume();

		position = 0;

		int acronsCounter = PBPreferenceUtils.getIntPref(getActivity()
				.getApplicationContext(), PBConstant.PREF_NAME,
				PBConstant.PREF_DONGURI_COUNT, 0);

		int mapleCounter = PBPreferenceUtils.getIntPref(getActivity()
				.getApplicationContext(), PBConstant.PREF_NAME,
				PBConstant.PREF_MAPLE_COUNT, 0);

		int goldCounter = PBPreferenceUtils.getIntPref(getActivity()
				.getApplicationContext(), PBConstant.PREF_NAME,
				PBConstant.PREF_GOLD_COUNT, 0);

		TextView txtMapleCounterContent = (TextView) view
				.findViewById(R.id.textView113);
		TextView txtAcronsCounterContent = (TextView) view
				.findViewById(R.id.textView123);
		TextView txtGoldCounterContent = (TextView) view
				.findViewById(R.id.textVie133);

		txtMapleCounterContent.setText(String.valueOf(acronsCounter));
		txtAcronsCounterContent.setText(String.valueOf(mapleCounter));

		txtGoldCounterContent.setText(String.valueOf(goldCounter));
		int tabPosition = PBPreferenceUtils.getIntPref(getActivity()
				.getApplicationContext(), PBConstant.PREF_NAME,
				PBConstant.TAB_POSITION, 0);

		if (tabPosition == 0) {

			boolean isFromDownload = PBPreferenceUtils.getBoolPref(
					getActivity().getApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.ISDOWNLOAD, false);
			if (isFromDownload) {

				PBPreferenceUtils.saveBoolPref(getActivity()
						.getApplicationContext(), PBConstant.PREF_NAME,
						PBConstant.ISDOWNLOAD, false);

				try {
					if (isArrivalTabSelected) {

						isLocalArrivalUpdate = true;

					} else if (isRankingTabSelected) {

						isLocalRankingUpdate = true;

					} else if (isRecommendedTabSelected) {

						isLocalRecommenedUpdate = true;

					}
					position = PBPreferenceUtils.getIntPref(getActivity()
							.getApplicationContext(), PBConstant.PREF_NAME,
							Constant.KB_POSITION, 0);
					PBPreferenceUtils.saveIntPref(getActivity()
							.getApplicationContext(), PBConstant.PREF_NAME,
							Constant.KB_POSITION, 0);
					historyItemList = mDatabaseManager.getAllHistoriesCursor();
					loadDataFromLocal();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		/*boolean hasInternet = PBApplication.hasNetworkConnection();
		if (hasInternet) {
			webview = (WebView) view
					.findViewById(R.id.webview_mainpage_recommendation);
			webview.setVisibility(View.VISIBLE);
			//webview.setBackgroundColor(color)
			webview.loadUrl(PBAPIContant.SAKURA_API_HOST);
			loadRecommendedPage(webview);
		} else {
			webview.setVisibility(View.INVISIBLE);
		}*/
		
		if(isRecommendedTabSelected){
			   
			   boolean hasInternet = PBApplication.hasNetworkConnection();
			   if (hasInternet) {
			    webview.setVisibility(View.VISIBLE);
			    //webview.setBackgroundColor(color)
			    webview.loadUrl(PBAPIContant.SAKURA_API_HOST);
			    loadRecommendedPage(webview);
			   } else {
			    webview.setVisibility(View.INVISIBLE);
			   } 
		}

	}
	boolean loadingFinished = true;
	boolean redirect = false;
	protected void loadRecommendedPage(final WebView wv) {
		
		if (android.os.Build.VERSION.SDK_INT < 16) {
			wv.setBackgroundColor(0x00000000);
			} else {
				wv.setBackgroundColor(Color.WHITE);
			}
		
		wv.setBackgroundColor(0xFFFFFF);
		//wv.setBackgroundColor(0);
		wv.setVerticalScrollBarEnabled(false);
		wv.setHorizontalScrollBarEnabled(false);
		wv.getSettings().setUseWideViewPort(true);
		wv.getSettings().setJavaScriptEnabled(true);
		
		// Atik sample code for fit screen scale to fit
		//wv.getSettings().setLoadWithOverviewMode(true);
		//wv.getSettings().setUseWideViewPort(true);
		
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				/*if (mProgressHUD.isShowing()) {

					mProgressHUD.dismiss();
				}*/
				
				webview.setVisibility(View.VISIBLE);
				if(!redirect){
			          loadingFinished = true;
			          
			       }

			       if(loadingFinished && !redirect){
			         //HIDE LOADING IT HAS FINISHED
			    	   mProgressHUD.dismiss();
			       } else{
			          redirect = false; 
			       }
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				loadingFinished = false;
				if (!mProgressHUD.isShowing()) {
					webview.setVisibility(View.VISIBLE);
					mProgressHUD.show();
				}
			}
			
			
			@Override
			   public boolean shouldOverrideUrlLoading(WebView view, String url) {
			    String decodeUrl = decodeURIComponent(url);

			    
				boolean hasInternet = PBApplication.hasNetworkConnection();
				if (!loadingFinished) {
			          redirect = true;
			       }

			   loadingFinished = false;
			   
				if (hasInternet) {
	                System.out.println("Atik URL for recommended webpage:"+decodeUrl);
	                if ((decodeUrl.contains("//"))&&(decodeUrl.contains("?"))) {
	                 
	                 String webviewAction = decodeUrl.substring(decodeUrl.indexOf("//") + 2,
	                    decodeUrl.indexOf("?"));
	                 String webviewData = decodeUrl.substring(decodeUrl.indexOf("?") + 1,
	                    decodeUrl.length());
	                 
	                 if (webviewAction.equals("download")) {
	                  
	                  if (webviewData.startsWith("password=")) {
	                   String password = webviewData.substring(9, 
	                      webviewData.length());
	                   String collectionId = mDatabaseManager.getCollectionId(password);
	   				if (mDatabaseManager.isPasswordExistsInSentItems(collectionId)) {
	                  // if (mDatabaseManager.isPasswordExistsInSentItems(password) ){
	                	   // Dialog display
	                	   
	                   }else{
	                    
	                    new KBDownLoadManager(getActivity(),password,0);
	                   }      
	                  // new KBDownLoadManager(getActivity(),password,0);
	                  }

	                 } else {
	                  wv.loadUrl(decodeUrl);
	                 }
	                 if  (webviewAction.equals("openview")) {
	                  
	                 }
	                 
	                } else {
	            
	                 if (decodeUrl.contains("photobag.in/p/")) {
	                	 String password = decodeUrl.replace("http://photobag.in/p/", "");
	         /*new KBDownLoadManager(getActivity(),password,0);*/
	                	    String collectionId = mDatabaseManager.getCollectionId(password);
	    	   				if (mDatabaseManager.isPasswordExistsInSentItems(collectionId)) {
	        // if (mDatabaseManager.isPasswordExistsInSentItems(password) ){
            	   // Dialog display
	        	 Toast.makeText(getActivity().getApplicationContext(), 
	        			 getString(R.string.pb_download_aikotoba_already_uptodate), 1000).show();
               }else{
                
                new KBDownLoadManager(getActivity(),password,0);
               } 
	         

	        } else {
	         wv.loadUrl(decodeUrl);
	        }
	                }
	                return true;
				} else {
					if (mProgressHUD.isShowing()) {

						mProgressHUD.dismiss();
					}
		        	 /*Toast.makeText(getActivity().getApplicationContext(), 
		        			 getString(R.string.pb_network_error_toast), 1000).show();*/
					// Change it to dialog
 					AlertDialog.Builder	exitDialog =  new AlertDialog.Builder(new ContextThemeWrapper(getActivity(),
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
					return true;
				}
			    

			    }




			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Log.e(TAG, String.format(
						"[onReceivedError] error:%d , desc:%s , url:%s",
						errorCode, description, failingUrl));
				/*boolean hasInternet = PBApplication.hasNetworkConnection();
				if(!hasInternet) {
					System.out.println("Atik no net connection here");
				}*/
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

	private void loadDataFromLocal() {

		try {

			if (isArrivalTabSelected) {
				if (isLocalArrivalUpdate) {
					list.clear();
					isLocalArrivalUpdate = false;
					String arrivalJson = SPreferenceUtils.getStringPref(
							getActivity(), Constant.PREF_NAME,
							Constant.KB_ARRIVAL_PASSWORD_JSON, "");
					JSONArray jsonArray = new JSONArray(arrivalJson);
					for (int i = 0; i < jsonArray.length(); i++) {

						JSONObject obj = jsonArray.getJSONObject(i);

						Password mPassword = new Password();
						mPassword.setNickName(obj.getString("nickname"));
						mPassword.setPassword(obj.getString("password"));
						mPassword.setCreatedDate(obj.getString("created"));
						mPassword.setPhotoCount(Integer.parseInt(obj
								.getString("photos_count")));
						mPassword.setNumberOfDownload(Integer.parseInt(obj
								.getString("downloaded_users_count")));
						mPassword.setFavorite(Integer.parseInt(obj
								.getString("favourite")));
						mPassword.setHoney(Integer.parseInt(obj
								.getString("honey")));
						mPassword.setThumbURL(obj.getString("thumb_url"));
						mPassword.setExpiresAT(obj.getString("expires_at"));
						mPassword.setExpiredTime(obj.getString("expires_time"));
						mPassword.setChargesTime(obj.getString("charges_time"));
						mPassword.setNewItem(Integer.parseInt(obj
								.getString("new")));
						mPassword.setDownload(false);
					    String collectionId = mDatabaseManager.getCollectionId(obj
								.getString("password"));
		   				if (mDatabaseManager.isPasswordExistsInSentItems(collectionId)) {
//						if (mDatabaseManager.isPasswordExistsInSentItems(obj
//								.getString("password"))) {

							mPassword.setDownload(true);
						}
						mPassword.setRecommend(Integer.parseInt(obj
								.getString("recommended")));

						list.add(mPassword);

					}

				}

				adapter = new CommonAdapter(getActivity(), list, false, false,
						historyItemList, false);
				mainPageListview.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				if(pullRefreshListview.getCurrentMode() != Mode.PULL_FROM_START){
					
					if (position != 0) {
						mainPageListview.requestFocusFromTouch();
						mainPageListview.setSelection(list.size() -1);
					}
				}

			} else if (isRankingTabSelected) {
				if (isLocalRankingUpdate) {
					listWithRanking.clear();
					isLocalRankingUpdate = false;
					String rankingJson = SPreferenceUtils.getStringPref(
							getActivity(), Constant.PREF_NAME,
							Constant.KB_RANKING_PASSWORD_JSON, "");
					JSONArray jsonArray = new JSONArray(rankingJson);
					for (int i = 0; i < jsonArray.length(); i++) {

						JSONObject obj = jsonArray.getJSONObject(i);
						Password mPassword = new Password();
						mPassword.setNickName(obj.getString("nickname"));
						mPassword.setPassword(obj.getString("password"));
						mPassword.setCreatedDate(obj.getString("created"));
						mPassword.setPhotoCount(Integer.parseInt(obj
								.getString("photos_count")));
						mPassword.setNumberOfDownload(Integer.parseInt(obj
								.getString("downloaded_users_count")));
						mPassword.setFavorite(Integer.parseInt(obj
								.getString("favourite")));
						mPassword.setHoney(Integer.parseInt(obj
								.getString("honey")));
						mPassword.setThumbURL(obj.getString("thumb_url"));
						mPassword.setExpiresAT(obj.getString("expires_at"));
						mPassword.setExpiredTime(obj.getString("expires_time"));
						mPassword.setChargesTime(obj.getString("charges_time"));
						mPassword.setNewItem(Integer.parseInt(obj
								.getString("new")));
						mPassword.setDownload(false);

						mPassword.setRecommend(Integer.parseInt(obj
								.getString("recommended")));

//						if (mDatabaseManager.isPasswordExistsInSentItems(obj
//								.getString("password"))) {
						
						 String collectionId = mDatabaseManager.getCollectionId(obj
									.getString("password"));
			   				if (mDatabaseManager.isPasswordExistsInSentItems(collectionId)) {

							mPassword.setDownload(true);
						}

						listWithRanking.add(mPassword);

					}

				}

				adapter = new CommonAdapter(getActivity(), listWithRanking,
						false, true, historyItemList, false);
				mainPageListview.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				if(pullRefreshListview.getCurrentMode() != Mode.PULL_FROM_START){
					
					if (position != 0) {
						mainPageListview.requestFocusFromTouch();
						mainPageListview.setSelection(listWithRanking.size() -1);
					}
				}
			} else if (isRecommendedTabSelected) {
				if (isLocalRecommenedUpdate) {

					Log.d("isRecommendedTabSelected", ""
							+ isRecommendedTabSelected);
					listWithRecommened.clear();
					isLocalRecommenedUpdate = false;
					String rankingJson = SPreferenceUtils.getStringPref(
							getActivity(), Constant.PREF_NAME,
							Constant.KB_RECOMMENED_PASSWORD_JSON, "");
					JSONArray jsonArray = new JSONArray(rankingJson);
					for (int i = 0; i < jsonArray.length(); i++) {

						JSONObject obj = jsonArray.getJSONObject(i);
						Password mPassword = new Password();
						mPassword.setNickName(obj.getString("nickname"));
						mPassword.setPassword(obj.getString("password"));
						mPassword.setCreatedDate(obj.getString("created"));
						mPassword.setPhotoCount(Integer.parseInt(obj
								.getString("photos_count")));
						mPassword.setNumberOfDownload(Integer.parseInt(obj
								.getString("downloaded_users_count")));
						mPassword.setFavorite(Integer.parseInt(obj
								.getString("favourite")));
						mPassword.setHoney(Integer.parseInt(obj
								.getString("honey")));
						mPassword.setThumbURL(obj.getString("thumb_url"));
						mPassword.setExpiresAT(obj.getString("expires_at"));
						mPassword.setExpiredTime(obj.getString("expires_time"));
						mPassword.setChargesTime(obj.getString("charges_time"));
						mPassword.setNewItem(Integer.parseInt(obj
								.getString("new")));
						mPassword.setRecommend(Integer.parseInt(obj
								.getString("recommended")));
						mPassword.setDownload(false);
						boolean isDownload = false;

//						if (mDatabaseManager.isPasswordExistsInSentItems(obj
//								.getString("password"))) {
						
						 String collectionId = mDatabaseManager.getCollectionId(obj
									.getString("password"));
			   				if (mDatabaseManager.isPasswordExistsInSentItems(collectionId)) {

							isDownload = true;
						}

						if (!isDownload) {

							listWithRecommened.add(mPassword);
						}

					}

				}

				adapter = new CommonAdapter(getActivity(), listWithRecommened,
						false, false, historyItemList, true);
				mainPageListview.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}

			//listview.setSelected(selected)
			// Atik problem here in code 
			if (position != 0) {

				mainPageListview.setSelection(position);
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

	private class OpenPagePasswordListTask extends
			AsyncTask<Void, String, Response> {

		@Override
		protected Response doInBackground(Void... params) {
			Response response = null;
			String uid = SPreferenceUtils.getStringPref(getActivity(),
					Constant.PREF_NAME, Constant.UID, "");

			if (isArrivalTabSelected) {
				response = ApiHelper.getPasswordList(uid, "arrival");

			} else if (isRankingTabSelected) {

				response = ApiHelper.getPasswordList(uid, "ranking");

			} else if (isRecommendedTabSelected) {

				response = ApiHelper.getPasswordList(uid, "recommended");

			}

			return response;
		}

		@Override
		protected void onPostExecute(Response response) {
			super.onPostExecute(response);
			if (mProgressHUD.isShowing()) {

				mProgressHUD.dismiss();
			}

			if (rotation != null) {

				//btn_refresh.clearAnimation();
			}

			if (response != null) {
				int response_code = response.errorCode;
				String response_body = response.decription;
				System.out.println("Atik Every time enter into koukaibukuro and response data is :"+response_body);

				try {
					JSONObject jObject;
					jObject = new JSONObject(response_body);

					if (response_code == 200) {

						if (jObject.has("passwords")) {

							JSONArray jsonArray = jObject
									.getJSONArray("passwords");
							

							if (isArrivalTabSelected) {

								SPreferenceUtils.saveStringPref(getActivity(),
										Constant.PREF_NAME,
										Constant.KB_ARRIVAL_PASSWORD_JSON,
										jsonArray.toString());
								SPreferenceUtils
										.saveBoolPref(
												getActivity(),
												Constant.PREF_NAME,
												Constant.KB_ARRIVAL_PASSWORD_JSON_ISSAVED,
												true);

								list = Util.getPasswordList(jsonArray);

								isarrivalDataSync = true;

								adapter = new CommonAdapter(getActivity(),
										list, false, false, historyItemList,
										false);
								mainPageListview.setAdapter(adapter);
								adapter.notifyDataSetChanged();

							} else if (isRankingTabSelected) {
								SPreferenceUtils.saveStringPref(getActivity(),
										Constant.PREF_NAME,
										Constant.KB_RANKING_PASSWORD_JSON,
										jsonArray.toString());
								SPreferenceUtils
										.saveBoolPref(
												getActivity(),
												Constant.PREF_NAME,
												Constant.KB_RANKING_PASSWORD_JSON_ISSAVED,
												true);

								listWithRanking = Util
										.getPasswordList(jsonArray);
								isRankingDataSync = true;

								adapter = new CommonAdapter(getActivity(),
										listWithRanking, false, true,
										historyItemList, false);
								mainPageListview.setAdapter(adapter);
								adapter.notifyDataSetChanged();

							} else if (isRecommendedTabSelected) {
								SPreferenceUtils.saveStringPref(getActivity(),
										Constant.PREF_NAME,
										Constant.KB_RECOMMENED_PASSWORD_JSON,
										jsonArray.toString());
								SPreferenceUtils.saveBoolPref(getActivity(),
										Constant.PREF_NAME,
										Constant.KB_RECOMMENED_JSON_ISSAVED,
										true);

								listWithRecommened = Util
										.getRecommenedPasswordList(jsonArray);
								isRecommenedDataSync = true;

								adapter = new CommonAdapter(getActivity(),
										listWithRecommened, false, false,
										historyItemList, true);
								mainPageListview.setAdapter(adapter);
								adapter.notifyDataSetChanged();

							}

						}

					}

				} catch (JSONException e) {

					e.printStackTrace();
				}

			}

		}

	}

	private class GetNewDataTask extends AsyncTask<Void, String, Response> {

		@Override
		protected Response doInBackground(Void... params) {
			
			// Simulates a background job.
			/*try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}*/
			
			Response response = null;
			String uid = SPreferenceUtils.getStringPref(getActivity(),
					Constant.PREF_NAME, Constant.UID, "");

			if (isArrivalTabSelected) {


				if(pullRefreshListview.getCurrentMode() == Mode.PULL_FROM_START){
					
					if (list.size() > 0) {

						response = ApiHelper.getUpdatePasswordListForType(uid, list.get(0)
								.getPassword(), "arrival","top");
					} else {

						response = ApiHelper.getUpdatePasswordListForType(uid, "",
								"arrival","top");
				}
					
				}else{
					
					if (list.size() > 0) {

						response = ApiHelper.getUpdatePasswordListForType(uid, list.get(list.size()-1)
								.getPassword(), "arrival","bottom");
					} else {

						response = ApiHelper.getUpdatePasswordListForType(uid, "",
								"arrival","bottom");
				}
					
				}
				
				
			} else if (isRankingTabSelected) {

				if(pullRefreshListview.getCurrentMode() == Mode.PULL_FROM_START){
					
					if (listWithRanking.size() > 0) {

						response = ApiHelper.getUpdatePasswordListForType(uid,
								listWithRanking.get(0).getPassword(), "ranking","top");
					} else {

						response = ApiHelper.getUpdatePasswordListForType(uid, "",
								"ranking","top");
					}
				}else{
					
					if (listWithRanking.size() > 0) {

						response = ApiHelper.getUpdatePasswordListForType(uid,
								listWithRanking.get(listWithRanking.size()-1).getPassword(), "ranking","bottom");
					} else {

						response = ApiHelper.getUpdatePasswordListForType(uid, "",
								"ranking","bottom");
					}
				}
				

			} 
			
//			else if (isRecommendedTabSelected) {
//				
//				if(pullRefreshListview.getCurrentMode() == Mode.PULL_FROM_START){
//					if (listWithRecommened.size() > 0) {
//
//						response = ApiHelper.getUpdatePasswordList(uid,
//								listWithRecommened.get(0).getPassword(),
//								"recommended");
//					} else {
//
//						response = ApiHelper.getUpdatePasswordList(uid, "",
//								"recommended");
//					}
//					
//				}else{
//					if (listWithRecommened.size() > 0) {
//
//						response = ApiHelper.getUpdatePasswordList(uid,
//								listWithRecommened.get(listWithRecommened.size()).getPassword(),
//								"recommended");
//					} else {
//
//						response = ApiHelper.getUpdatePasswordList(uid, "",
//								"recommended");
//					}
//					
//				}
//
//				
//
//			}

			return response;
		}

		@Override
		protected void onPostExecute(Response response) {
			super.onPostExecute(response);
			// Call onRefreshComplete when the list has been refreshed.
			view.findViewById(R.id.progressBar_refresh)
					.setVisibility(View.GONE);
			pullRefreshListview.onRefreshComplete();
			// listview.state = listview.state.PULL_TO_REFRESH;

			try {

				int response_code = response.errorCode;
				String response_body = response.decription;
				Log.d("update response", response_body);
				JSONObject jObject;
				jObject = new JSONObject(response_body);

				if (isArrivalTabSelected) {
					if (response_code == 200) {

						if (jObject.has("passwords")) {

							JSONArray jsonArray = jObject
									.getJSONArray("passwords");
							
							if(PBAPIContant.DEBUG){
								System.out.println("new "+jsonArray.length());
							}

							String arrivalJson = SPreferenceUtils
									.getStringPref(getActivity(),
											Constant.PREF_NAME,
											Constant.KB_ARRIVAL_PASSWORD_JSON,
											"");
							JSONArray arrivalJsonArray = new JSONArray(
									arrivalJson);
							
							if(PBAPIContant.DEBUG){
								System.out.println("old "+arrivalJsonArray.length());
							}

							JSONArray updateArray = new JSONArray();
							if(pullRefreshListview.getCurrentMode() == Mode.PULL_FROM_START){
								
								for (int i = 0; i < jsonArray.length(); i++) {

									JSONObject obj = jsonArray.getJSONObject(i);

									updateArray.put(obj);

								}

//								for (int i = 0; i < arrivalJsonArray.length(); i++) {
//
//									JSONObject obj = arrivalJsonArray
//											.getJSONObject(i);
//
//									updateArray.put(obj);
//
//								}
							}else{
								for (int i = 0; i < arrivalJsonArray.length(); i++) {

									JSONObject obj = arrivalJsonArray
											.getJSONObject(i);

									updateArray.put(obj);

								}
								for (int i = 0; i < jsonArray.length(); i++) {

									JSONObject obj = jsonArray.getJSONObject(i);

									updateArray.put(obj);

								}

								
							}
							

							if (jsonArray.length() > 0) {
								SPreferenceUtils.saveStringPref(getActivity(),
										Constant.PREF_NAME,
										Constant.KB_ARRIVAL_PASSWORD_JSON,
										updateArray.toString());
								isLocalArrivalUpdate = true;
								loadDataFromLocal();
							}

						}
					}

				} else if (isRankingTabSelected) {

					if (response_code == 200) {

						if (jObject.has("passwords")) {

							JSONArray jsonArray = jObject
									.getJSONArray("passwords");
							String rankingJson = SPreferenceUtils
									.getStringPref(getActivity(),
											Constant.PREF_NAME,
											Constant.KB_RANKING_PASSWORD_JSON,
											"");
							JSONArray rankingJsonArray = new JSONArray(
									rankingJson);
							if(PBAPIContant.DEBUG){
								Log.d("new jsonArray", jsonArray.toString());
							}
							
							JSONArray updateArray = new JSONArray();
							if(pullRefreshListview.getCurrentMode() == Mode.PULL_FROM_START){
								for (int i = 0; i < jsonArray.length(); i++) {

									JSONObject obj = jsonArray.getJSONObject(i);

									updateArray.put(obj);

								}

//								for (int i = 0; i < rankingJsonArray.length(); i++) {
//
//									JSONObject obj = rankingJsonArray
//											.getJSONObject(i);
//
//									updateArray.put(obj);
//
//								}
							}else{
								
								for (int i = 0; i < rankingJsonArray.length(); i++) {

									JSONObject obj = rankingJsonArray
											.getJSONObject(i);

									updateArray.put(obj);

								}
								for (int i = 0; i < jsonArray.length(); i++) {

									JSONObject obj = jsonArray.getJSONObject(i);

									updateArray.put(obj);

								}

								
								
							}
							

							if (jsonArray.length() > 0) {
								SPreferenceUtils.saveStringPref(getActivity(),
										Constant.PREF_NAME,
										Constant.KB_RANKING_PASSWORD_JSON,
										updateArray.toString());
								isLocalRankingUpdate = true;
								loadDataFromLocal();
							}

						}
					}

				} 
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.mainpage_segmented_btn1) {

			isRankingTabSelected = false;
			isRecommendedTabSelected = false;
			isArrivalTabSelected = true;
			view.findViewById(R.id.mainpage_segmented_btn1)
					.setBackgroundResource(R.drawable.radio_checked_left);
			view.findViewById(R.id.mainpage_segmented_btn2)
					.setBackgroundResource(R.drawable.radio_unchecked_middle);
			view.findViewById(R.id.mainpage_segmented_btn3)
					.setBackgroundResource(R.drawable.radio_unchecked_right);

			arrivalTextView
					.setTextColor(getResources().getColor(R.color.white)); // change
																			// textcolor
			rankingTextView.setTextColor(getResources().getColor(
					R.color.radio_button_selected_color)); // change textcolor

			arrivalImageView.setImageResource(R.drawable.a_open_new_3); // change
																		// imageview
																		// image
			rankingImageView.setImageResource(R.drawable.a_open_ranking_2_tab); // change
																				// imageview
																				// image

			recommendedTextView.setTextColor(getResources().getColor(
					R.color.radio_button_selected_color));
			recommendedImageView
					.setImageResource(R.drawable.icon_recommended_on_segment_enable);

			loadDataFromLocal();

			if (!isarrivalDataSync) {

				if (Util.isOnline(getActivity())) {

					mProgressHUD.show(true);

					OpenPagePasswordListTask task = new OpenPagePasswordListTask();
					task.execute();

				}
			}

			pullRefreshListview.setVisibility(View.VISIBLE);
			webview.setVisibility(View.GONE);
		//	btn_refresh.setVisibility(View.VISIBLE);

		} else if (v.getId() == R.id.mainpage_segmented_btn2) {

			isRankingTabSelected = true;
			isRecommendedTabSelected = false;
			isArrivalTabSelected = false;
			view.findViewById(R.id.mainpage_segmented_btn1)
					.setBackgroundResource(R.drawable.radio_unchecked_left);
			view.findViewById(R.id.mainpage_segmented_btn2)
					.setBackgroundResource(R.drawable.radio_checked_middle);
			view.findViewById(R.id.mainpage_segmented_btn3)
					.setBackgroundResource(R.drawable.radio_unchecked_right);

			arrivalTextView.setTextColor(getResources().getColor(
					R.color.radio_button_selected_color)); // change textcolor
			rankingTextView
					.setTextColor(getResources().getColor(R.color.white)); // change
																			// textcolor
			recommendedTextView.setTextColor(getResources().getColor(
					R.color.radio_button_selected_color));

			arrivalImageView.setImageResource(R.drawable.a_open_new_2); // change
																		// imageview
																		// image
			rankingImageView.setImageResource(R.drawable.a_open_ranking_5_tab); // change
																				// imageview
																				// image
			recommendedImageView
					.setImageResource(R.drawable.icon_recommended_on_segment_enable);

			loadDataFromLocal();
			if (!isRankingDataSync) {

				if (Util.isOnline(getActivity())) {

					mProgressHUD.show(true);
					OpenPagePasswordListTask task = new OpenPagePasswordListTask();
					task.execute();

				}
			}

			pullRefreshListview.setVisibility(View.VISIBLE);
			webview.setVisibility(View.GONE);
			//btn_refresh.setVisibility(View.VISIBLE);

		} else if (v.getId() == R.id.mainpage_segmented_btn3) {
			boolean hasInternet = PBApplication.hasNetworkConnection();
			if (hasInternet) {
				webview.loadUrl(PBAPIContant.SAKURA_API_HOST);
				loadRecommendedPage(webview);
			} else {
				
				Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.pb_network_not_available_general_message), 
						1000);
				TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
				if( v1 != null) v1.setGravity(Gravity.CENTER);
				toast.show();
				
				webview.setVisibility(View.INVISIBLE);
			}
			
			isRecommendedTabSelected = true;
			isRankingTabSelected = false;
			isArrivalTabSelected = false;

			view.findViewById(R.id.mainpage_segmented_btn1)
					.setBackgroundResource(R.drawable.radio_unchecked_left);
			view.findViewById(R.id.mainpage_segmented_btn2)
					.setBackgroundResource(R.drawable.radio_unchecked_middle);
			view.findViewById(R.id.mainpage_segmented_btn3)
					.setBackgroundResource(R.drawable.radio_checked_right);

			arrivalTextView.setTextColor(getResources().getColor(
					R.color.radio_button_selected_color)); // change textcolor
			rankingTextView.setTextColor(getResources().getColor(
					R.color.radio_button_selected_color));
			arrivalImageView.setImageResource(R.drawable.a_open_new_2); // change
																		// imageview
																		// image
			rankingImageView.setImageResource(R.drawable.a_open_ranking_2_tab); // change
																				// imageview
																				// image
			recommendedTextView.setTextColor(getResources().getColor(
					R.color.white));
			recommendedImageView
					.setImageResource(R.drawable.icon_recommended_on_segment_disable);

			pullRefreshListview.setVisibility(View.GONE);
			webview.setVisibility(View.VISIBLE);
			//btn_refresh.setVisibility(View.GONE);

		} else if (v.getId() == R.id.button_refresh) {

			if (Util.isOnline(getActivity())) {

				//btn_refresh.startAnimation(rotation);

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {

						OpenPagePasswordListTask task = new OpenPagePasswordListTask();
						task.execute();

					}
				}, 1500);

			} else {
           	 	/*Toast.makeText(getActivity(), 
              			 getString(R.string.pb_network_not_available_general_message), 1000).show();*/
				Toast toast = Toast.makeText(getActivity(), getString(R.string.pb_network_not_available_general_message), 
						1000);
				TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
				if( v1 != null) v1.setGravity(Gravity.CENTER);
				toast.show();
           	 	
			}
		} else if (v.getId() == R.id.button_donguri) {

			Intent honeyExchangeAct = new Intent(getActivity(),
					PBMyPageAcornExchangeListActivity.class);
			honeyExchangeAct.putExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY,
					true);
			startActivity(honeyExchangeAct);
		} else if (v.getId() == R.id.button_goto_matomegaipage) {
			boolean hasInternetBeforeGoingToMatomegai = PBApplication.hasNetworkConnection();
			
			if (hasInternetBeforeGoingToMatomegai) {
				
            	Intent matomegaiIntent = new Intent(getActivity(),
    					PBPurchaseActivity.class);
            	matomegaiIntent.putExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY,
    					true);
    			startActivity(matomegaiIntent);
            	
        	} else {
				AlertDialog.Builder	exitDialog =  new AlertDialog.Builder(new ContextThemeWrapper(getActivity(),
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
        	}
		}

	}

}