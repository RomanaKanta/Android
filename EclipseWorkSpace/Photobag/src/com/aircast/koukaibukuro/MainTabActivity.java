package com.aircast.koukaibukuro;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.aircast.koukaibukuro.util.Constant;
import com.aircast.koukaibukuro.util.DialogTutorial;
import com.aircast.koukaibukuro.util.SPreferenceUtils;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBMyPageHoneyNotification;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.fragment.PBUploadMainFragment;
import com.aircast.photobag.model.PBTimelineHistoryModel;
import com.aircast.photobag.utils.PBPreferenceUtils;

@SuppressLint("InflateParams")
public class MainTabActivity extends FragmentActivity implements
		TabHost.OnTabChangeListener {
	private TabInfo mLastTab = null;
	private TabHost mTabHost;
	private HashMap mapTabInfo = new HashMap();
	private TextView notifyTxT;
	// private TextView title;
	private Button topleftSideButton;
	int kumaMemoUpdateNumber;
	boolean isShowKumaMemo = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.kb_home_screen);

		// *****************************使い方の実装*******************************************//
		// 初回表示だったらチュートリアル表示

		boolean appFirstBootMyPage = SPreferenceUtils.getBoolPref(
				getApplicationContext(), Constant.PREF_NAME,
				Constant.APP_FIRST_BOOT_MY_TAB, false);
		if (!appFirstBootMyPage) {

			ArrayList<Integer> imageId = new ArrayList<Integer>();
			imageId.add(R.drawable.a_tutorial_1);
			imageId.add(R.drawable.a_tutorial_2);
			imageId.add(R.drawable.a_tutorial_3);
			imageId.add(R.drawable.a_tutorial_4);
			imageId.add(R.drawable.a_tutorial_5);

			DialogTutorial dialog = new DialogTutorial(MainTabActivity.this,
					imageId, false);
			dialog.show();

			// チュートリアル表示フラグを立てる
			SPreferenceUtils.saveBoolPref(getApplicationContext(),
					Constant.PREF_NAME, Constant.APP_FIRST_BOOT_MY_TAB, true);
		}

		// *****************************使い方の実装終わり*******************************************//

		// title = (TextView) findViewById(R.id.textView_actionbar_title);

		topleftSideButton = (Button) findViewById(R.id.Button_actionbar_back);
		notifyTxT = (TextView) findViewById(R.id.textView_kumaMemoUpdateNumber);
		// title.setText("公開袋");
		// topleftSideButton.setText(getString(R.string.kb_kumamemo_button_text));
		
		
		// Added google analytics
		String deviceUUID = PBPreferenceUtils
				.getStringPref(PBApplication
						.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_UID, "0");
        	
       System.out.println("Atik Easy Tracker is called upon when enter in Koukaibukuro screen");
  	   EasyTracker easyTracker = EasyTracker.getInstance(MainTabActivity.this);
  	   easyTracker.set(Fields.SCREEN_NAME, ""+deviceUUID+":MainTabActivity");
  	   // MapBuilder.createEvent().build() returns a Map of event fields and values
  	   // that are set and sent with the hit.
  	   easyTracker.send(MapBuilder
  	       .createEvent("ui_action",     // Event category (required)
  	                    "Come to Koukaibukuro screen",  // Event action (required)
  	                     deviceUUID+" In Koukaibukuro Screen",   // Event label
  	                    null)            // Event value
  	       .build()
  	   );

		Bundle bundel = getIntent().getExtras();
		if (getIntent().hasExtra("token")) {

			String token = bundel.getString("token");

			Log.d("token", token);
			SPreferenceUtils.saveStringPref(getApplicationContext(),
					Constant.PREF_NAME, Constant.TOKEN, token);

		}

		if (getIntent().hasExtra("uid")) {

			String uid = bundel.getString("uid");
			Log.d("uid", uid);
			SPreferenceUtils.saveStringPref(getApplicationContext(),
					Constant.PREF_NAME, Constant.UID, uid);

		}

		findViewById(R.id.imageView_actionbar_title).setOnClickListener(
				mClickListener);

		findViewById(R.id.Button_actionbar_back).setOnClickListener(
				mClickListener);

		findViewById(R.id.textView_actionbar_rightside).setOnClickListener(
				mClickListener);
		
		findViewById(R.id.imageView_settiongs).setOnClickListener(
				mClickListener);
		LinearLayout bodycontent = (LinearLayout) findViewById(R.id.bodycontent);

		bodycontent.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				hideKeyboard(); // hide
								// softkeyboard
				return false;
			}
		});
		initialiseTabHost(savedInstanceState);

		if (getIntent().hasExtra("tab")) {

			int tagPosition = getIntent().getExtras().getInt("tab");
			mTabHost.setCurrentTab(tagPosition);

		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		int newHoney = PBPreferenceUtils.getIntPref(getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_NOTIF_HONEY_NEW, 0);
		int newMaple = PBPreferenceUtils.getIntPref(getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_NOTIF_MAPLE_NEW, 0);
		int newDonguri = PBPreferenceUtils
				.getIntPref(getApplicationContext(), PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_NOTIF_DONGURI_NEW, 0);
		int newGold = PBPreferenceUtils.getIntPref(getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_NOTIF_GOLD_NEW, 0);

		if (newHoney + newMaple + newDonguri + newGold > 0) {
			kumaMemoUpdateNumber = newHoney + newMaple + newDonguri + newGold;
		} else {
			kumaMemoUpdateNumber = 0;
		}
		boolean isShowKumaMemo = false;
		PBTimelineHistoryModel newestTimelineData = PBDatabaseManager
				.getInstance(getApplicationContext())
				.getNewestTimelineHistory();
		if (newestTimelineData != null) {

			isShowKumaMemo = true;
		}

		if (isShowKumaMemo) {

			topleftSideButton.setVisibility(View.VISIBLE);
		} else {

			topleftSideButton.setVisibility(View.VISIBLE);
		}

		if (kumaMemoUpdateNumber > 0) {

			notifyTxT.setText(String.valueOf(kumaMemoUpdateNumber));
		} else {

			notifyTxT.setVisibility(View.GONE);

		}
		
		
		
		

	}
	
	  // Added below activity life cycle method for Google analytics 
	  @Override
	  public void onStart() {
		  
		    super.onStart();
		    //System.out.println("Atik start Easy Tracker");
		    EasyTracker.getInstance(this).activityStart(this);
	    }
	  
	   //Added below activity life cycle method for Google analytics
	    
	   @Override
	   public void onStop() {
		    super.onStop();
		    //System.out.println("Atik stop Easy Tracker");
		    EasyTracker.getInstance(this).activityStop(this);
	   }

	private class TabInfo {
		private String tag;
		private Class clss;
		private Bundle args;
		private Fragment fragment;

		TabInfo(String tag, Class clazz, Bundle args) {
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
		}

	}

	class TabFactory implements TabHost.TabContentFactory {

		private final Context mContext;

		public TabFactory(Context context) {
			mContext = context;
		}

		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}

	}

	@SuppressWarnings("unchecked")
	private void initialiseTabHost(Bundle args) {

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;
		View tabViewMain = createTabView(mTabHost.getContext(),
				getString(R.string.main_page_title),
				R.drawable.icon_tab_main_page);
		MainTabActivity.addTab(this, this.mTabHost,
				this.mTabHost.newTabSpec(Constant.KB_MAIN_PAGE_TAG)
						.setIndicator(tabViewMain),
				(tabInfo = new TabInfo(Constant.KB_MAIN_PAGE_TAG,
						MainPageFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		View tabViewsearch = createTabView(mTabHost.getContext(),
				getString(R.string.search_page_title),
				R.drawable.icon_tab_search_page);
		MainTabActivity.addTab(this, this.mTabHost,
				this.mTabHost.newTabSpec(Constant.KB_SEARCH_PAGE_TAG)
						.setIndicator(tabViewsearch), (tabInfo = new TabInfo(
								Constant.KB_SEARCH_PAGE_TAG, SearchPageFragment.class,
						args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		
		View tabViewUpload = createTabView(mTabHost.getContext(),
				getString(R.string.tab_mnu_upload_main), R.drawable.icon_tab_upload);
		MainTabActivity.addTab(
				this,
				this.mTabHost,
				this.mTabHost.newTabSpec(Constant.KB_UPLOAD_TAG).setIndicator(
						tabViewUpload), (tabInfo = new TabInfo(
								Constant.KB_UPLOAD_TAG, KBUploadMainFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		
		
		View tabViewMy = createTabView(mTabHost.getContext(),
				getString(R.string.my_page_title), R.drawable.icon_tab_my_page);
		MainTabActivity.addTab(
				this,
				this.mTabHost,
				this.mTabHost.newTabSpec(Constant.KB_MY_PAGE_TAG).setIndicator(
						tabViewMy), (tabInfo = new TabInfo(
								Constant.KB_MY_PAGE_TAG, MyPageFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		this.onTabChanged(Constant.KB_MAIN_PAGE_TAG);
		mTabHost.setOnTabChangedListener(this);

	}

	private View createTabView(Context context, String tag, int resID) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.kb_tab_layout, null);
		TextView tv = (TextView) view.findViewById(R.id.tabLabel);
		if (!TextUtils.isEmpty(tag)) {
			tv.setText(tag);
		}

		FrameLayout lvItem = (FrameLayout) view
				.findViewById(R.id.customTabLayout);
		lvItem.setBackgroundResource(resID);
		return view;
	}

	private static void addTab(MainTabActivity activity, TabHost tabHost,
			TabHost.TabSpec tabSpec, TabInfo tabInfo) {

		tabSpec.setContent(activity.new TabFactory(activity));
		String tag = tabSpec.getTag();
		tabInfo.fragment = activity.getSupportFragmentManager()
				.findFragmentByTag(tag);
		if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
			FragmentTransaction ft = activity.getSupportFragmentManager()
					.beginTransaction();
			ft.detach(tabInfo.fragment);
			ft.commit();
			activity.getSupportFragmentManager().executePendingTransactions();
		}

		tabHost.addTab(tabSpec);
	}


	@Override
	public void onTabChanged(String tabId) {
		 //hideKeyboard();
//		if (tabId.equals("Tab1")) {
//			// title.setText("公開袋");
//
//			if (kumaMemoUpdateNumber > 0) {
//				notifyTxT.setVisibility(View.VISIBLE);
//				notifyTxT.setText(String.valueOf(kumaMemoUpdateNumber));
//			}
//
//			// topleftSideButton.setText(getString(R.string.kb_kumamemo_button_text));
//			if (isShowKumaMemo) {
//
//				topleftSideButton.setVisibility(View.VISIBLE);
//			} else {
//
//				topleftSideButton.setVisibility(View.GONE);
//			}
//		} else if (tabId.equals("Tab2")) {
//			// title.setText("検索");
//			notifyTxT.setVisibility(View.INVISIBLE);
//			// topleftSideButton.setText("");
//		} else if (tabId.equals("Tab3")) {
//
//			// title.setText("マイページ");
//
//			if (kumaMemoUpdateNumber > 0) {
//				notifyTxT.setVisibility(View.VISIBLE);
//				notifyTxT.setText(String.valueOf(kumaMemoUpdateNumber));
//			}
//			// topleftSideButton.setText(getString(R.string.kb_kumamemo_button_text));
//			if (isShowKumaMemo) {
//
//				topleftSideButton.setVisibility(View.VISIBLE);
//			} else {
//
//				topleftSideButton.setVisibility(View.GONE);
//			}
//		}

		TabInfo newTab = (TabInfo) this.mapTabInfo.get(tabId);

		if (mLastTab != newTab) {
			FragmentTransaction ft = this.getSupportFragmentManager()
					.beginTransaction();

			if (mLastTab != null) {
				if (mLastTab.fragment != null) {

					ft.hide(mLastTab.fragment);
				}
			}
			if (newTab != null) {

				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(this,
							newTab.clss.getName(), newTab.args);
					ft.add(R.id.bodycontent, newTab.fragment, newTab.tag);
				} else {

					ft.show(newTab.fragment);
				}
			}
			mLastTab = newTab;
			ft.commit();

			//prevoiusPosition = currentPosition;

		}

	}

	View.OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			int id = v.getId();

			if (id == R.id.Button_actionbar_back) {

				Intent notificationIntent = new Intent(MainTabActivity.this,
						PBMyPageHoneyNotification.class);
				notificationIntent.putExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY, true);
				startActivity(notificationIntent);

			} else if (id == R.id.textView_actionbar_rightside) {

				// Remove dialog when press X button from Openbag
				finish();

			} else if (id == R.id.imageView_actionbar_title) {

				ArrayList<Integer> imageId = new ArrayList<Integer>();
				imageId.add(R.drawable.a_tutorial_1);
				imageId.add(R.drawable.a_tutorial_2);
				imageId.add(R.drawable.a_tutorial_3);
				imageId.add(R.drawable.a_tutorial_4);
				imageId.add(R.drawable.a_tutorial_5);

				DialogTutorial dialog = new DialogTutorial(
						MainTabActivity.this, imageId, false);
				dialog.show();

			}else if(id == R.id.imageView_settiongs){
				
				Intent intent = new Intent(MainTabActivity.this,
						KBSettingsActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
				
			}

		}

	};

	private void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
