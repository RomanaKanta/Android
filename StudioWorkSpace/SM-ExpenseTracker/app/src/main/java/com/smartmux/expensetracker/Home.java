/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.smartmux.expensetracker;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.vinsol.expensetracker.entry.CameraEntry;
import com.vinsol.expensetracker.entry.FavoriteEntry;
import com.vinsol.expensetracker.entry.Text;
import com.vinsol.expensetracker.entry.Voice;
import com.vinsol.expensetracker.expenselisting.ExpenseListing;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DatabaseAdapter;
import com.vinsol.expensetracker.helpers.GraphHelper;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.helpers.UnfinishedEntryCount;
import com.vinsol.expensetracker.models.Entry;

import java.util.ArrayList;
import java.util.Calendar;

//import com.flurry.android.FlurryAgent;

public class Home extends BaseActivity implements OnClickListener {
	
	private Bundle bundle;
	private GraphHelper mHandleGraph;
	private ProgressBar graphProgressBar;
	private UnfinishedEntryCount unfinishedEntryCount;
	private ConvertCursorToListString mConvertCursorToListString;
    boolean isGranted = false;
	
	@Override
	protected void onStart() {
		super.onStart();
//		FlurryAgent.onStartSession(this, getString(R.string.flurry_key));
//		FlurryAgent.onEvent(getString(R.string.home_screen));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		//FlurryAgent.onEndSession(this);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		bundle = new Bundle();
		mConvertCursorToListString = new ConvertCursorToListString(this);
		////// ********* Adding Click Listeners to HomeActivity ********** /////////

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(NoteListActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                isGranted = true;
                ((Button) findViewById(R.id.home_text)).setOnClickListener(Home.this);
                ((Button) findViewById(R.id.home_voice)).setOnClickListener(Home.this);
                ((Button) findViewById(R.id.home_camera)).setOnClickListener(Home.this);
                ((Button) findViewById(R.id.home_favorite)).setOnClickListener(Home.this);
                ((Button) findViewById(R.id.home_save_reminder)).setOnClickListener(Home.this);
                ((ImageView) findViewById(R.id.home_listview)).setOnClickListener(Home.this);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //	Toast.makeText(NoteListActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                isGranted = false;

            }


        };
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                        //	.setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ,Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
		
		
		setColorFilter(((ImageView)findViewById(R.id.home_listview)));
//		setColorFilter(findViewById(R.id.home_text));
//		setColorFilter(findViewById(R.id.home_voice));
//		setColorFilter(findViewById(R.id.home_camera));
//		setColorFilter(findViewById(R.id.home_favorite));
		
		ImageView mainGenerateReport = (ImageView) findViewById(R.id.home_generate_report);
		mainGenerateReport.setVisibility(View.VISIBLE);
		mainGenerateReport.setOnClickListener(this);
		//setColorFilter(mainGenerateReport);
		
		graphProgressBar = (ProgressBar) findViewById(R.id.graph_progress_bar);
		graphProgressBar.setVisibility(View.VISIBLE);
		
		if(ExpenseTrackerApplication.toSync) {
//			SyncHelper.syncHelper = new SyncHelper(this);
//        	SyncHelper.syncHelper.execute();
		}





	}
	
	private void setColorFilter(final View view){
		
		if(view.isPressed()){
			view.getBackground().setColorFilter(getResources().getColor(R.color.top_bar_color), PorterDuff.Mode.SRC_ATOP);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
						
					view.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
				}
			}, 200);
		}else{
			view.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
		}
		
	}
	
   private void setColorFilter(final ImageView view){
		
		if(view.isPressed()){
			view.setColorFilter(getResources().getColor(R.color.top_bar_color), PorterDuff.Mode.SRC_ATOP);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
						
					view.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
				}
			}, 200);
		}else{
			view.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
		}
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		//finding current location
        if(isGranted) {
            LocationHelper mLocationHelper = new LocationHelper();
            Location location = mLocationHelper.getBestAvailableLocation();
            if (location == null) {
                mLocationHelper.requestLocationUpdate();
            }
            mHandleGraph = new GraphHelper(Home.this, graphProgressBar);
            unfinishedEntryCount = new UnfinishedEntryCount(mConvertCursorToListString.getEntryList(false, ""), null, null, null, ((TextView) findViewById(R.id.home_unfinished_entry_count)));
            unfinishedEntryCount.execute();
            mHandleGraph.execute();
        }
	}
	
	@Override
	public void onClick(View clickedView) {
		
		
		boolean isMediaMounted = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if(isMediaMounted) {
			if(!ExpenseTrackerApplication.isInitialized){ExpenseTrackerApplication.Initialize();}
		}
		int idOfClickedView = clickedView.getId();
		cancelHandleGraphTask();
		cancelUnfinishedEntryTask();
		switch (idOfClickedView) {
			case R.id.home_text:
				//setColorFilter(clickedView);
				Intent intentTextEntry = new Intent(this, Text.class);
				intentTextEntry.putExtras(bundle);
				startActivity(intentTextEntry);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				break;
				
			case R.id.home_voice:
				//setColorFilter(clickedView);
				if (isMediaMounted) {
					Intent intentVoice = new Intent(this, Voice.class);
					intentVoice.putExtras(bundle);
					startActivity(intentVoice);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
				} else {
					Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
				break;
	
			case R.id.home_camera:
				//setColorFilter(clickedView);
				if (isMediaMounted) {
					Intent intentCamera = new Intent(this, CameraEntry.class);
					intentCamera.putExtras(bundle);
					startActivity(intentCamera);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
				} else {
					Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
				break;
				
			case R.id.home_favorite:
				//setColorFilter(clickedView);
				Intent intentFavorite = new Intent(this, FavoriteEntry.class);
				intentFavorite.putExtras(bundle);
				startActivity(intentFavorite);	
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				break;
				
			case R.id.home_save_reminder:
				//FlurryAgent.onEvent(getString(R.string.save_reminder));
				insertToDatabase(R.string.unknown);
				Intent intentListView = new Intent(this, ExpenseListing.class);
				startActivity(intentListView);
//				SyncHelper.startSync();
				overridePendingTransition(R.anim.push_up_in,
						R.anim.push_up_out);
				break;
			
			case R.id.home_listview:
				setColorFilter(((ImageView)findViewById(R.id.home_listview)));
				Intent intentListView2 = new Intent(this, ExpenseListing.class);
				startActivity(intentListView2);
				overridePendingTransition(R.anim.push_up_in,
						R.anim.push_up_out);
				break;
				
			case R.id.home_generate_report:
				//setColorFilter(((ImageView)findViewById(R.id.home_generate_report)));
				startGenerateReportActivity();
				break;
		}//end switch
	}//end onClick
	
//	private void createDatabaseEntry(int typeOfEntry) {
//		bundle.putLong(Constants.KEY_ID, Long.parseLong(insertToDatabase(typeOfEntry).toString()));
//		
//		if(LocationHelper.currentAddress != null && !LocationHelper.currentAddress.trim().equals("")) {
//			bundle.putBoolean(Constants.KEY_SET_LOCATION, false);
//		} else {
//			bundle.putBoolean(Constants.KEY_SET_LOCATION, true);
//		}
//	}

	///////// ******** function to mark entry into the database and returns the id of the new entry ***** //////
	private Long insertToDatabase(int type) {
		Entry list = new Entry();
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		
		list.timeInMillis = mCalendar.getTimeInMillis();

		if (LocationHelper.currentAddress != null && !LocationHelper.currentAddress.trim().equals("")) {
			list.location = LocationHelper.currentAddress;
		}
		list.type = getString(type);
		DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(this);
		mDatabaseAdapter.open();
		long id = mDatabaseAdapter.insertToEntryTable(list);
		mDatabaseAdapter.close();
		return id;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		cancelHandleGraphTask();
		cancelUnfinishedEntryTask();
	}
	
	private void cancelUnfinishedEntryTask() {
		if(unfinishedEntryCount != null && !unfinishedEntryCount.isCancelled()) {
			unfinishedEntryCount.cancel(true);
		}
	}
	
	private void cancelHandleGraphTask() {
		if(mHandleGraph != null && !mHandleGraph.isCancelled()) {
			mHandleGraph.cancel(true);
		}
	}
	
}