package com.smartmux.videodownloader;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmux.videodownloader.adapter.BookmarkListAdapter;
import com.smartmux.videodownloader.database.VisitedSiteDataSource;
import com.smartmux.videodownloader.lockscreen.utils.AppExtra;
import com.smartmux.videodownloader.modelclass.VisitedSiteListModelClass;
import com.smartmux.videodownloader.utils.SMConstant;
import com.smartmux.videodownloader.utils.SMSharePref;

public class BrowserListActivity extends Activity implements OnClickListener {

	BookmarkListAdapter mBookmarkAdapter;
	ArrayList<VisitedSiteListModelClass> mBookMarkModel = new ArrayList<VisitedSiteListModelClass>();

	VisitedSiteDataSource mVisitedSiteDataSource;
	VisitedSiteListModelClass mSiteListModelClass;

	TextView tv_done, tv_title = null;
	ListView browserList = null;
	ImageView iv_add = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.activity_bookmark_list);

		mVisitedSiteDataSource = new VisitedSiteDataSource(this);

		tv_title = (TextView) findViewById(R.id.textview_bookmark_title);

		tv_done = (TextView) findViewById(R.id.textview_bookmark_done);
		browserList = (ListView) findViewById(R.id.listview_bookmark_list);
		iv_add = (ImageView) findViewById(R.id.button_add);

		tv_done.setOnClickListener(this);
		iv_add.setOnClickListener(this);

		if (getIntent().hasExtra("bookmark")) {

			mBookMarkModel = mVisitedSiteDataSource.getBookmarkSiteList();

			mBookmarkAdapter = new BookmarkListAdapter(this, mBookMarkModel);

			tv_title.setText(R.string.bookmark);

			iv_add.setVisibility(View.GONE);

		} else {

			mBookMarkModel = mVisitedSiteDataSource.getSiteList();

			mBookmarkAdapter = new BookmarkListAdapter(this, mBookMarkModel);

			tv_title.setText(R.string.visited_site);

			iv_add.setVisibility(View.VISIBLE);

		}

		for (int i = 0; i < mBookMarkModel.size(); i++) {
			for (int j = i + 1; j < mBookMarkModel.size(); j++) {
				if (mBookMarkModel.get(i).getmVsiteUrl()
						.equals(mBookMarkModel.get(j).getmVsiteUrl())) {
					mBookMarkModel.remove(j);
					j--;
				}
			}
		}

		browserList.setAdapter(mBookmarkAdapter);

		browserList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub

				SMSharePref.saveBookmarkUrl(BrowserListActivity.this,mBookMarkModel.get(position).getmVsiteUrl());
				
				Intent intent = new Intent(BrowserListActivity.this,
						MainActivity.class);

				intent.putExtra("settab", "Browser");
				startActivity(intent);
				finish();
				overridePendingTransition(0, R.anim.bottom_down);
				
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.textview_bookmark_done:
			SMSharePref.setBackCode(getApplicationContext());
			finish();
			overridePendingTransition(0, R.anim.bottom_down);
			break;

		case R.id.button_add:

			finish();
			overridePendingTransition(0, R.anim.bottom_down);
			break;
		default:
			break;

		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		String security = SMSharePref.getSecurity(BrowserListActivity.this);
		int event_code = SMSharePref.getReturnCode(getApplicationContext());
		if (security.equals(SMConstant.on) && event_code == AppExtra.HOME_CODE) {
//			Toast.makeText(getApplicationContext(),
//					"event_code" + event_code, 1000).show();
			
		Intent i = new Intent(BrowserListActivity.this, AppLockActivity.class);
		i.putExtra("passcode", "password_match");
        startActivity(i);
        overridePendingTransition(R.anim.bottom_up, 0);
	}
		
	}

	 @Override
		protected void onUserLeaveHint() {
		 SMSharePref.setHomeCode(getApplicationContext());
			super.onUserLeaveHint();
	}

	
	
}
