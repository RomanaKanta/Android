package com.smartmux.trackfriends;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

//import com.google.android.gms.maps.GoogleMap;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.smartmux.trackfriends.adapter.EventListAdapter;
import com.smartmux.trackfriends.adapter.FriendListAdapter;
import com.smartmux.trackfriends.fragment.FragmentMap;
import com.smartmux.trackfriends.modelclass.EventModelClass;
import com.smartmux.trackfriends.service.LocationUpdateService;
import com.smartmux.trackfriends.utils.NetworkChecking;

public class MainActivity extends FragmentActivity implements OnClickListener {

	public NetworkChecking mNetworkChecking = null;
//	GoogleMap mGoogleMap = null;
	ListView eventList, friendList = null;
	ImageView refresh = null;
	EventListAdapter mEventListAdapter = null;
	FriendListAdapter mFriendListAdapter = null;
	EventModelClass mEventModelClass = null;
	FragmentManager manager = null;
	FragmentTransaction transaction = null;
	// ImageView leftHeaderImage, rightHeaderImage, leftFooterImage,
	// rightFooterImage = null;
	View emptyViewForEvent, emptyViewForSetting  = null;
	SlidingMenu leftSlideMenu, rightSlideMenu = null;
	LinearLayout eventLayer, settingLayer = null;
	RelativeLayout headerLayer,footerLayer = null;
	Animation downDrawerOpen, downDrawerClose = null;
//	settingDrawerOpen,
//			settingDrawerClose = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main_activity);
		mNetworkChecking = new NetworkChecking(MainActivity.this);
		manager = getFragmentManager();
		transaction = manager.beginTransaction();
		transaction.replace(R.id.map, new FragmentMap());
		transaction.commit();

		// leftHeaderImage = (ImageView) findViewById(R.id.headerLeftImage);
		// rightHeaderImage = (ImageView) findViewById(R.id.headerRightImage);
		// leftFooterImage = (ImageView) findViewById(R.id.footerLeftImage);
		// rightFooterImage = (ImageView) findViewById(R.id.footerRightImage);
		eventLayer = (LinearLayout) findViewById(R.id.eventportion);
		headerLayer = (RelativeLayout) findViewById(R.id.toplayout);
		footerLayer = (RelativeLayout) findViewById(R.id.downlayout);
		emptyViewForEvent = (View)findViewById(R.id.viewevent);
		emptyViewForSetting= (View)findViewById(R.id.viewsetting);
		if (savedInstanceState == null) {
			 // get the Intent that started this Activity
	        Intent iIntent = getIntent();
	 
	        Bundle mBundle = null;
	        // get the Bundle that stores the data of this Activity
	        mBundle = iIntent.getExtras();
	 
	        // getting data from bundle
    
     
     if(mBundle!=null){
   	  @SuppressWarnings("unused")
	int position = mBundle.getInt("position");
   	headerLayer.setVisibility(View.GONE);
   	footerLayer.setVisibility(View.GONE);
   	  
     }else{
			// on first time display view for first nav item

     }
		}
		eventLayer = (LinearLayout) findViewById(R.id.eventportion);
		downDrawerOpen = AnimationUtils
				.loadAnimation(this, R.anim.bottom_up);
		downDrawerClose = AnimationUtils.loadAnimation(this,
				R.anim.bottom_down);
		

		settingLayer = (LinearLayout) findViewById(R.id.settingportion);
//		settingDrawerOpen = AnimationUtils
//				.loadAnimation(this, R.anim.bottom_up);
//		settingDrawerClose = AnimationUtils.loadAnimation(this,
//				R.anim.bottom_down);

		leftSlideMenu = new SlidingMenu(MainActivity.this);
		leftSlideMenu.setMode(SlidingMenu.LEFT);
		leftSlideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		leftSlideMenu.setShadowWidthRes(R.dimen.shadow_width);
		leftSlideMenu.setShadowDrawable(R.drawable.shadow);
		leftSlideMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		leftSlideMenu.setFadeDegree(0.35f);
		leftSlideMenu.setFadeDegree(0.25f);
		leftSlideMenu.attachToActivity(MainActivity.this,
				SlidingMenu.SLIDING_CONTENT);
		leftSlideMenu.setMenu(R.layout.left_header_drawer_screen);
		leftSlideMenu.setSlidingEnabled(true);

		rightSlideMenu = new SlidingMenu(MainActivity.this);
		rightSlideMenu.setMode(SlidingMenu.RIGHT);
		rightSlideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		rightSlideMenu.setShadowWidthRes(R.dimen.shadow_width);
		rightSlideMenu.setShadowDrawable(R.drawable.shadow);
		rightSlideMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		rightSlideMenu.setFadeDegree(0.35f);
		rightSlideMenu.setFadeDegree(0.25f);
		rightSlideMenu.attachToActivity(MainActivity.this,
				SlidingMenu.SLIDING_CONTENT);
		rightSlideMenu.setMenu(R.layout.right_header_drawer_screen);
		rightSlideMenu.setSlidingEnabled(true);

		// /////////// My Event List of left drawer///////////////////

		eventList = (ListView) findViewById(R.id.eventList);

		String[] mEventName = { "BirthDay Party", "Engagement",
				"Football Game", "Feoneral" };

		String[] mMemberNumber = { "5", "5", "12", "7" };

		ArrayList<String> eventnameList = new ArrayList<String>();
		eventnameList.addAll(Arrays.asList(mEventName));

		ArrayList<String> numberList = new ArrayList<String>();
		numberList.addAll(Arrays.asList(mMemberNumber));

		mEventListAdapter = new EventListAdapter(this, eventnameList,
				numberList);
		eventList.setAdapter(mEventListAdapter);

		// /////////Friend list of right drawer/////////////
		friendList = (ListView) findViewById(R.id.friendList);

		String[] mFriendName = { "Suman", "Rifat", "Biplob", "Rasel" };

		String[] mDetail = { "1000", "Male, Age : 31", "444", "Male, Age : 25" };

		ArrayList<String> friendnameList = new ArrayList<String>();
		friendnameList.addAll(Arrays.asList(mFriendName));

		ArrayList<String> frienddetailList = new ArrayList<String>();
		frienddetailList.addAll(Arrays.asList(mDetail));

		mFriendListAdapter = new FriendListAdapter(this, friendnameList,
				frienddetailList);
		friendList.setAdapter(mFriendListAdapter);

		// /////////on click listener////////////
		refresh = (ImageView)findViewById(R.id.refreshimage);
		this.findViewById(R.id.refreshimage).setOnClickListener(
				this);
		
		this.findViewById(R.id.imageView_headerLeftImage).setOnClickListener(
				this);

		this.findViewById(R.id.imageView_headerRightImage).setOnClickListener(
				this);

		this.findViewById(R.id.imageView_footerLeftImage).setOnClickListener(
				this);

		this.findViewById(R.id.imageView_footerRightImage).setOnClickListener(
				this);

		this.findViewById(R.id.viewsetting).setOnClickListener(this);

		this.findViewById(R.id.viewevent).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.imageView_headerLeftImage:

			leftSlideMenu.toggle();
			break;

		case R.id.imageView_headerRightImage:

			rightSlideMenu.toggle();
			break;
		case R.id.imageView_footerRightImage:

			if (settingLayer.getVisibility() == View.GONE) {
				settingLayer.setVisibility(View.VISIBLE);
//				settingLayer.startAnimation(settingDrawerOpen);
				settingLayer.startAnimation(downDrawerOpen);
				
				setAnimationListeren(downDrawerOpen, emptyViewForSetting);
			}
			eventLayer.setVisibility(View.GONE);
			break;
		case R.id.imageView_footerLeftImage:
			if (eventLayer.getVisibility() == View.GONE) {
				eventLayer.setVisibility(View.VISIBLE);
				eventLayer.startAnimation(downDrawerOpen);
				
				setAnimationListeren(downDrawerOpen, emptyViewForEvent);
			}
			settingLayer.setVisibility(View.GONE);
			break;
			
		case R.id.viewsetting:

			if (settingLayer.getVisibility() == View.VISIBLE) {
				settingLayer.setVisibility(View.GONE);
				settingLayer.startAnimation(downDrawerClose);
			 	emptyViewForSetting.setVisibility(View.GONE);
			}
			break;
		case R.id.viewevent:
			if (eventLayer.getVisibility() == View.VISIBLE) {
				eventLayer.setVisibility(View.GONE);
				eventLayer.startAnimation(downDrawerClose);
				emptyViewForEvent.setVisibility(View.GONE);
			}
			break;
			
		case R.id.refreshimage:
			Animation sampleFadeAnimation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.rotate_animation);
		    sampleFadeAnimation.setRepeatCount(4);
		    refresh.startAnimation(sampleFadeAnimation);
			break;
			
		default:
			break;

		}
	}

	public void setAnimationListeren(Animation anim, final View view){
		anim.setAnimationListener(new Animation.AnimationListener(){
		    @Override
		    public void onAnimationStart(Animation arg0) {
		    }           
		    @Override
		    public void onAnimationRepeat(Animation arg0) {
		    }           
		    @Override
		    public void onAnimationEnd(Animation arg0) {
		    	
		    	view.setVisibility(View.VISIBLE);
		    }
		});
	}
	
	  @Override
	    protected void onResume() {
	    	// TODO Auto-generated method stub
	    	super.onResume();
	    	
	    	if(isMyServiceRunning() ==false){
	     	startService(new Intent(this, LocationUpdateService.class));
	    	}
	    	if(mNetworkChecking.isNetworkAvailable(getApplicationContext()) == false){
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						MainActivity.this);

				alertDialog.setTitle(R.string.no_network);
				alertDialog.setMessage(R.string.alart_msg);
				//alertDialog.setCancelable(false);
				alertDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				alertDialog.show();
			
			}
			
			if(mNetworkChecking.isGpsEnable(getApplicationContext()) == false){

					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							MainActivity.this);
		
					// Setting Dialog Title
					alertDialog.setTitle(R.string.no_gps);
		
					// Setting Dialog Message
					alertDialog
							.setMessage(R.string.gps_alart);
		
					// Setting Icon to Dialog
					// alertDialog.setIcon(R.drawable.delete);
		
					// On pressing Settings button
					alertDialog.setPositiveButton("Settings",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(
											Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									MainActivity.this.startActivity(intent);
									dialog.cancel();
		
								}
							});
		
					// on pressing cancel button
					alertDialog.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
		
								}
							});
		
					// Showing Alert Message
					alertDialog.show();

			
			}
			    
	    }
	    
	  @Override
	    protected void onDestroy() {
	    	// TODO Auto-generated method stub
	    	super.onDestroy();
	    	stopService(new Intent(this, LocationUpdateService.class));
	    }
	  
	  private boolean isMyServiceRunning() {
		    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		    for (RunningServiceInfo service : manager.getRunningServices(50)) {
		        if ("com.smartmux.trackfriends.service.LocationUpdateService".equals(service.service.getClassName())) {
		            return true;
		        }
		    }
		    return false;
		}
}
