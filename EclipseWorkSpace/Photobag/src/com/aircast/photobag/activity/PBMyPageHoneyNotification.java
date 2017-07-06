package com.aircast.photobag.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteMisuseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.model.PBTimelineHistoryModel;
import com.aircast.photobag.model.PBTimelineHistoryModel.Type;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Show Notification list.
 * 
 * <p><b>TODO:</b>Same as HistoryMainAcitivity, cursor here sometimes causes crash too.</p>
 * <p>Also, its better to move TimelineNotificationAdapter defined here to Adapter package.</p>
 * */
public class PBMyPageHoneyNotification extends PBAbsActionBarActivity implements
		OnClickListener, OnScrollListener{
	private ActionBar mHomeBar;

	private ListView notificationList;
	private TimelineNotificationAdapter notificationAdapter;
	private Cursor notificationCursor;

	private PBDatabaseManager mDatabaseManager;
	private Handler backgroundHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mDatabaseManager = PBDatabaseManager.getInstance(PBMainTabBarActivity.sMainContext);
		
		setContentView(R.layout.pb_layout_my_page_list_notification);

		mHomeBar = (ActionBar) findViewById(R.id.actionBar);
		mHomeBar.setTitle(getString(R.string.pb_notification_title));
		setHeader(mHomeBar, getString(R.string.pb_notification_title));
		
		// if come from koukaibukuro
		if(getIntent().hasExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY)){
	       	 boolean isFromLibrary = getIntent().getExtras().getBoolean(PBConstant.PREF_PASSWORD_FROM_LIBRARY);
		 if(isFromLibrary){
			 
			 mHomeBar.setHomeLogo(R.drawable.openbag_actionbar_icon);
		 }
		 
		}
		mUpdateTime = System.currentTimeMillis();
		notificationList = (ListView) findViewById(R.id.listViewHoneyNotification);
	}

	private void initialTimelineData(){
		Cursor mNewCursor = mDatabaseManager.getTimelineHistoryCursor();
		try{
			if(mNewCursor !=null && !mNewCursor.isClosed()){
				if(notificationAdapter == null){
					notificationCursor = mNewCursor;
					notificationAdapter = new TimelineNotificationAdapter(this, null, mNewCursor);
					notificationList.setAdapter(notificationAdapter);
					notificationAdapter.notifyDataSetChanged();
				}else{
					notificationAdapter.changeCursor(mNewCursor);
					notificationAdapter.notifyDataSetChanged();
					if(notificationCursor!=null && !notificationCursor.isClosed())
					{
						notificationCursor.close();
						notificationCursor = null;
					}
					notificationCursor = mNewCursor;
				}
			}
		    
        }catch (IllegalStateException e) {
            e.printStackTrace();
        }catch (SQLiteMisuseException e) {
            e.printStackTrace();
        }

		this.notificationList.setOnScrollListener(this);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//PBTimelineHistoryModel newest = PBDatabaseManager.getInstance(getApplicationContext()).getNewestTimelineHistory();
				int lastId = -1;
				final int lastIdParam = lastId;
				backgroundHandler.post(new Runnable() {
					
					@Override
					public void run() {
						PBTaskGetPointHistories task = new PBTaskGetPointHistories(lastIdParam, 20);
						task.execute();
					}
				});
			}
		}).start();
	}
	
	private void updateTimelineUI(){
		Cursor mNewCursor = mDatabaseManager.getTimelineHistoryCursor();
		try{
			if(mNewCursor !=null && !mNewCursor.isClosed()){
				if(notificationAdapter == null){
					notificationCursor = mNewCursor;
					notificationAdapter = new TimelineNotificationAdapter(this, null, mNewCursor);
					this.notificationList.setAdapter(notificationAdapter);
					notificationAdapter.notifyDataSetChanged();
				}else{
					notificationAdapter.changeCursor(mNewCursor);
					notificationAdapter.notifyDataSetChanged();
					if(this.notificationCursor!=null && !this.notificationCursor.isClosed())
					{
						this.notificationCursor.close();
						this.notificationCursor = null;
					}
					this.notificationCursor = mNewCursor;
				}
			}
		    
        }catch (IllegalStateException e) {
            e.printStackTrace();
        }catch (SQLiteMisuseException e) {
            e.printStackTrace();
        }
		setNewDissapearFromDB();
	}
	
	private void setNewDissapearFromDB(){
		
		if(PBMainTabBarActivity.sMainContext!=null){
			PBMainTabBarActivity.sMainContext.setMarkAllNotificationRead();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	    // V2 google analytics has been comment out 
		/*if(PBTabBarActivity.gaTracker!=null){
			PBTabBarActivity.gaTracker.trackPageView("PBMyPageHoneyNotification");
		}*/
		
		if(this.notificationCursor!=null && this.notificationCursor.isClosed()){
			this.notificationAdapter = null;
			this.notificationList.setAdapter(null);
		}
		
		initialTimelineData();
	}

	//Added below activity life cycle method for Google analytics
	@Override
    public void onStart() {
 	    super.onStart();
	    System.out.println("Atik start Easy Tracker for PBMyPageHoneyNofitication");
	    EasyTracker.getInstance(this).activityStart(this);
    }
  
    //Added below activity life cycle method for Google analytics
    @Override
    public void onStop() {
	    super.onStop();
	    System.out.println("Atik stop Easy Tracker for PBMyPageHoneyNofitication");
	    EasyTracker.getInstance(this).activityStop(this);
    }	
	
	@Override
	protected void onDestroy() {
		if(this.notificationCursor!=null && !this.notificationCursor.isClosed()){
			this.notificationCursor.close();
			this.notificationCursor = null;
		}

		if(this.notificationList!=null)
			this.notificationList.setAdapter(null);
		if(this.notificationAdapter!=null){
			this.notificationAdapter = null;
		}
		super.onDestroy();

// 		try {
// 			AdstirTerminate.init(this);
// 		} catch (Exception e) {}
	}

	@Override
	protected void onPause() {
		PBPreferenceUtils.saveIntPref(this,
				PBConstant.PREF_NAME,
				PBConstant.PREF_NAME_NOTIF_HONEY_NEW, 0);
		PBPreferenceUtils.saveIntPref(this,
				PBConstant.PREF_NAME,
				PBConstant.PREF_NAME_NOTIF_MAPLE_NEW, 0);
		PBPreferenceUtils.saveIntPref(this,
				PBConstant.PREF_NAME,
				PBConstant.PREF_NAME_NOTIF_DONGURI_NEW, 0);
		PBPreferenceUtils.saveIntPref(this,
				PBConstant.PREF_NAME,
				PBConstant.PREF_NAME_NOTIF_GOLD_NEW, 0);
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		default:
			break;
		}
	}

	@Override
	protected void handleHomeActionListener() {
		finish();

	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		
	}

	private class TimelineNotificationAdapter extends CursorAdapter {
		private LayoutInflater inflater;

		private int ID = 0;
		private int CREATED_AT = 1;
		private int TYPE = 2;
		private int DESCRIPTION = 3;
		private int IS_NEW = 4;

		public TimelineNotificationAdapter(Context context, SQLiteDatabase db,
				Cursor c) {
			super(context, c);
			if (c == null || c.isClosed())
				return;
			inflater = LayoutInflater.from(context);
			ID = c.getColumnIndex(PBDatabaseDefinition.TimelineHistoryData.C_ID);
			CREATED_AT = c.getColumnIndex(PBDatabaseDefinition.TimelineHistoryData.C_CREATED_AT);
			TYPE = c.getColumnIndex(PBDatabaseDefinition.TimelineHistoryData.C_TYPE);
			DESCRIPTION = c.getColumnIndex(PBDatabaseDefinition.TimelineHistoryData.C_DESCRIPTION);
			IS_NEW = c.getColumnIndex(PBDatabaseDefinition.TimelineHistoryData.C_IS_NEW);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return inflater.inflate(R.layout.honey_notification_item, null);
		}

		@Override
		public void bindView(View convertView, Context context, Cursor cursor) {
			if (cursor == null || cursor.isClosed())
				return;
			mHolder = new Holder(); // actually the holder is not needed here,
									// but just for best practice,
			// the same pattern is used as another adapter;
			mHolder.tvDescription = (TextView) convertView
					.findViewById(R.id.tvDescription);
			mHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			mHolder.icon = (ImageView) convertView
					.findViewById(R.id.imageIcon);
			mHolder.newNotif = convertView.findViewById(R.id.notificationView);
			if (cursor != null && !cursor.isClosed()) {
				int createdAt = cursor.getInt(CREATED_AT);
				String type = cursor.getString(TYPE);
				int isNew = cursor.getInt(IS_NEW);
				String description = cursor.getString(DESCRIPTION);
				PBTimelineHistoryModel.Type typeEnum =  PBTimelineHistoryModel.Type.valueOf(type.toUpperCase());
				if (typeEnum == Type.ACORN) {
					mHolder.icon.setImageResource(R.drawable.acron);
				} else if (typeEnum == Type.GOLDACORN) {
					mHolder.icon.setImageResource(R.drawable.acron_gold);
				} else { 
					mHolder.icon.setImageResource(R.drawable.honey); 
				}
				
				mHolder.newNotif.setVisibility(isNew == 1 ? View.VISIBLE : View.GONE);
				mHolder.tvDescription.setText(description);
				mHolder.tvTime.setText(getTimeString(createdAt));

			}
		}
		
		private String getTimeString(int miliseconds){
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(miliseconds * 1000L);
		 
			DateFormat formatter = new SimpleDateFormat("MM.dd");
			return formatter.format(calendar.getTime());
		}

		private Holder mHolder;

		private class Holder {
			public TextView tvDescription;
			public TextView tvTime;
			public View newNotif;
			public ImageView icon;
		}

	}
	
	private class PBTaskGetPointHistories extends AsyncTask<Void, Void, Void>{
		private int lastId, rowCount;
		
		public PBTaskGetPointHistories(int lastId, int rowCount){
			this.lastId = lastId;
			this.rowCount = rowCount;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			String token = PBPreferenceUtils.getStringPref(getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_NAME_TOKEN, "");
			
			if(TextUtils.isEmpty(token))
				return null;
			Log.d("AGUNG", "Called in geting - token:"+token);
			Response res = PBAPIHelper.getTimelineHistory(rowCount, lastId, "ja", token);
			Log.d("AGUNG", "res: "+res.errorCode + " "+res.decription);
			if(res!=null){
				if(res.errorCode == ResponseHandle.CODE_200_OK){
					ResponseHandle.parseTimelineHistoryAndSaveToDatabase(getApplicationContext(), res.decription);
				}else{
					
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			updateTimelineUI();
		}
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		final int lastItem = firstVisibleItem + visibleItemCount;
		if(lastItem == totalItemCount){
			loadOlderDataFromServer();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}
	
	private void loadOlderDataFromServer(){
		if (System.currentTimeMillis() - mUpdateTime < MIN_UPDATE_TIME) {
			return;
		}
		
		mUpdateTime = System.currentTimeMillis();
		if(checkOlderNotif == null || (checkOlderNotif.getStatus() != AsyncTask.Status.RUNNING)){
			checkOlderNotif = new PBTaskCheckNotification();
			checkOlderNotif.execute();
		}
	}
	
	private long mUpdateTime;
    private final long MIN_UPDATE_TIME = 1000 * 1 * 60;
	
	private void onNewDataArrive(){
		updateTimelineUI();
		this.notificationList.setOnClickListener(this);
	}
	
	private PBTaskCheckNotification checkOlderNotif;
	
	/**
	 * Task to check new notification from server. Until now, no push
	 * notification from server, then current implementation just doing polling
	 * from server.
	 */
	public class PBTaskCheckNotification extends AsyncTask<Void, Void, Boolean> {
		private PBTimelineHistoryModel oldestEntry;
		private int rows = 20;

		@Override
		protected Boolean doInBackground(Void... params) {
			String token = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
			if (TextUtils.isEmpty(token)) {
				return false;
			}
			oldestEntry = PBDatabaseManager
					.getInstance(getApplicationContext())
					.getOldestTimelineHistory();
			int older_than = -1;
			if (oldestEntry != null) {
				older_than = oldestEntry.getId();
			}
			if(older_than == -1)
				return false;
			
			Response response = PBAPIHelper.getTimelineHistory(rows,
					older_than, "ja", token);

			Log.d("AGUNG", "CHECK TIME LINE NOTIF TASK :" + response.decription);
			if (response.errorCode == ResponseHandle.CODE_200_OK) {
				int prevOlderId = older_than;
				
				try {
					ResponseHandle.parseTimelineHistoryAndSaveToDatabase(
							getApplicationContext(), response.decription);
					
					oldestEntry = PBDatabaseManager
							.getInstance(getApplicationContext())
							.getOldestTimelineHistory();
					

				} catch (Exception e) {
					Log.e("AGUNG",
							"error at fetch check notification"
									+ e.getMessage());
					
					return false;
				}			
				
				older_than = -1;
				if (oldestEntry != null) {
					older_than = oldestEntry.getId();
				}
				return (older_than == -1 || older_than == prevOlderId)
						? false
						: true;

			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				onNewDataArrive();
			}
		}
	}
}
