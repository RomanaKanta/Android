package com.smartmux.videodownloader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.smartmux.videodownloader.fragment.BrowserFragment;
import com.smartmux.videodownloader.fragment.Tab1Container;
import com.smartmux.videodownloader.fragment.Tab2Container;
import com.smartmux.videodownloader.fragment.Tab3Container;
import com.smartmux.videodownloader.fragment.Tab4Container;
import com.smartmux.videodownloader.fragment.Tab5Container;
import com.smartmux.videodownloader.lockscreen.utils.AppExtra;
import com.smartmux.videodownloader.utils.SMConstant;
import com.smartmux.videodownloader.utils.SMSharePref;

public class MainActivity extends FragmentActivity implements
		FragmentTabHost.OnTabChangeListener {

	private static final String TAB_1_TAG = "Browser";
	private static final String TAB_2_TAG = "Downloads";
	private static final String TAB_3_TAG = "Videos";
	private static final String TAB_4_TAG = "Playlist";
	private static final String TAB_5_TAG = "Settings";

	public static FragmentTabHost mTabHost;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.activity_main);

		String security = SMSharePref.getSecurity(MainActivity.this);
	
//		SMSharePref.setBackCode(getApplicationContext());
//		int event_code = SMSharePref.getReturnCode(getApplicationContext());
//		if (security.equals(SMConstant.on) && event_code != AppExtra.BACK_CODE) {
//		Intent i = new Intent(MainActivity.this, AppLockActivity.class);
//		i.putExtra("passcode", "password_match");
//        startActivity(i);
//        overridePendingTransition(R.anim.bottom_up, 0);
//	}
		
		InitTabView(savedInstanceState);
		if (savedInstanceState != null) {
			SMSharePref.setBackCode(getApplicationContext());
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

		if (getIntent().hasExtra("settab")) {
			
			SMSharePref.setBackCode(getApplicationContext());
			mTabHost.setCurrentTabByTag(getIntent().getExtras().getString(
					"settab"));
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		String security = SMSharePref.getSecurity(MainActivity.this);
		int event_code = SMSharePref.getReturnCode(getApplicationContext());
		if (security.equals(SMConstant.on) && event_code != AppExtra.BACK_CODE) {
		Intent i = new Intent(MainActivity.this, AppLockActivity.class);
		i.putExtra("passcode", "password_match");
        startActivity(i);
        overridePendingTransition(R.anim.bottom_up, 0);
	}
		
		setColorOnTab();

	}
	
	@Override
	protected void onUserLeaveHint() {
		SMSharePref.setDefaultCode(getApplicationContext());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//				
//		String security = SMSharePref.getSecurity(MainActivity.this);
//		if (security.equals(SMConstant.on)) {
//			Intent i = new Intent(MainActivity.this, AppLockActivity.class);
//			i.putExtra("passcode", "password");
//	        startActivity(i);
//	        overridePendingTransition(R.anim.bottom_up, 0);
//		}
	}

	private void InitTabView(Bundle args) {
		mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		mTabHost.addTab(
				setIndicator(MainActivity.this, mTabHost.newTabSpec(TAB_1_TAG),
						R.drawable.tab_back, "Browser",
						R.drawable.tab_icon_browser_up), Tab1Container.class,
				args);

		mTabHost.addTab(
				setIndicator(MainActivity.this, mTabHost.newTabSpec(TAB_2_TAG),
						R.drawable.tab_back, "Downloads",
						R.drawable.tab_icon_download_up), Tab2Container.class,
				args);

		mTabHost.addTab(
				setIndicator(MainActivity.this, mTabHost.newTabSpec(TAB_3_TAG),
						R.drawable.tab_back, "Videos",
						R.drawable.tab_icon_file_up), Tab3Container.class, args);

		mTabHost.addTab(
				setIndicator(MainActivity.this, mTabHost.newTabSpec(TAB_4_TAG),
						R.drawable.tab_back, "Playlist",
						R.drawable.tab_icon_playlist_up), Tab4Container.class,
				args);

		mTabHost.addTab(
				setIndicator(MainActivity.this, mTabHost.newTabSpec(TAB_5_TAG),
						R.drawable.tab_back, "Settings",
						R.drawable.tab_icon_settings_up), Tab5Container.class,
				args);

		mTabHost.setOnTabChangedListener(this);

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.push_left_in, 0);
	}

	private TabSpec setIndicator(Context ctx, TabSpec spec, int indicator,
			String string, int genresIcon) {
		View v = LayoutInflater.from(ctx).inflate(R.layout.tabbar_item, null);

		v.setBackgroundResource(indicator);

		TextView tv = (TextView) v.findViewById(R.id.tabsText);
		ImageView img = (ImageView) v.findViewById(R.id.tabsIcon);

		tv.setText(string);
		img.setImageResource(genresIcon);

		return spec.setIndicator(v);
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub

		setColorOnTab();

	}

	public void setColorOnTab() {
		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {

			TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i)
					.findViewById(R.id.tabsText);
			ImageView img = (ImageView) mTabHost.getTabWidget().getChildAt(i)
					.findViewById(R.id.tabsIcon);

			tv.setTextColor(Color.parseColor("#6D6D6E"));
			img.setColorFilter(Color.parseColor("#6D6D6E"),
					PorterDuff.Mode.SRC_ATOP);

		}

		TextView tv = (TextView) mTabHost.getCurrentTabView().findViewById(
				R.id.tabsText);
		ImageView img = (ImageView) mTabHost.getCurrentTabView().findViewById(
				R.id.tabsIcon);

		tv.setTextColor(Color.parseColor("#FFFFFF"));
		img.setColorFilter(Color.parseColor("#FFFFFF"),
				PorterDuff.Mode.SRC_ATOP);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (new BrowserFragment() != null) {
			new BrowserFragment().onActivityResult(requestCode, resultCode,
					intent);
		}
	}

}