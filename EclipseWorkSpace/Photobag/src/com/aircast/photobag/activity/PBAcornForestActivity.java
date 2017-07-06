package com.aircast.photobag.activity;

// import com.ad_stir.AdstirTerminate;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.utils.PBRewardUtils;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * Show acorn forest page
 * */
public class PBAcornForestActivity extends PBAbsActionBarActivity 
	implements OnClickListener {

	private LinearLayout mAcornAD;
	//private boolean hasInternetAvailable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pb_layout_acorn_forest);
		
        /*if (android.os.Build.VERSION.SDK_INT > 9) {  // check to bypass network error on main thread
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/
		
		mAcornAD = (LinearLayout) findViewById(R.id.layout_acorn_ad);
		
		ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(headerBar, getString(R.string.pb_acorn_forest_title));
        
        findViewById(R.id.btn_acorn_forest_road).setOnClickListener(this);
        findViewById(R.id.btn_acorn_forest_cave).setOnClickListener(this);
        findViewById(R.id.btn_acorn_forest_lake).setOnClickListener(this);
        findViewById(R.id.btn_acorn_forest_hill).setOnClickListener(this);
        findViewById(R.id.btn_acorn_forest_bridge).setOnClickListener(this);
        

	}
	
	@Override
	protected void onResume() {
		super.onResume();

        boolean hasInternet = PBApplication.hasNetworkConnection();
        mAcornAD.setVisibility(hasInternet ? View.VISIBLE : View.GONE);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// AdstirTerminate.init(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btn_acorn_forest_road:
	        boolean hasInternetAvailableInForestRoad = PBApplication.hasNetworkConnection(); // Check internet connectivity
			if(hasInternetAvailableInForestRoad) {
				
				Intent intent = PBRewardUtils.getAppdriverIntent(
						getApplicationContext());
				startActivity(intent);
				break;	
				
			} else {
				/*Toast.makeText(PBAcornForestActivity.this, 
		    			 getString(R.string.pb_network_not_available_general_message), 1000).show();*/
				Toast toast = Toast.makeText(PBAcornForestActivity.this, getString(R.string.pb_network_not_available_general_message), 
						1000);
				TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
				if( v1 != null) v1.setGravity(Gravity.CENTER);
				toast.show();
				break;	
			}

		case R.id.btn_acorn_forest_cave:
	        boolean hasInternetAvailableInForestCave = PBApplication.hasNetworkConnection(); // Check internet connectivity
			if(hasInternetAvailableInForestCave) {
				
				PBRewardUtils.showMetapsWallList(PBAcornForestActivity.this);
				break;
				
			} else {
				
				/*Toast.makeText(PBAcornForestActivity.this, 
		    			 getString(R.string.pb_network_not_available_general_message), 1000).show();*/
				Toast toast = Toast.makeText(PBAcornForestActivity.this, getString(R.string.pb_network_not_available_general_message), 
						1000);
				TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
				if( v1 != null) v1.setGravity(Gravity.CENTER);
				toast.show();
				break;	
				
			}
		case R.id.btn_acorn_forest_lake:
			
	        boolean hasInternetAvailableInForestLake = PBApplication.hasNetworkConnection(); // Check internet connectivity
			if(hasInternetAvailableInForestLake) {
			
				Intent caIntent = PBRewardUtils.getCAIntent(
						getApplicationContext());
				startActivity(caIntent);
				break;
			} else {
				
				/*Toast.makeText(PBAcornForestActivity.this, 
		    			 getString(R.string.pb_network_not_available_general_message), 1000).show();*/
				Toast toast = Toast.makeText(PBAcornForestActivity.this, getString(R.string.pb_network_not_available_general_message), 
						1000);
				TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
				if( v1 != null) v1.setGravity(Gravity.CENTER);
				toast.show();
				break;	
			}
			
		case R.id.btn_acorn_forest_bridge:
			
	        boolean hasInternetAvailableInForestBridge = PBApplication.hasNetworkConnection(); // Check internet connectivity
			if(hasInternetAvailableInForestBridge) {
				
				Intent webIntent = PBRewardUtils.getBridgeIntent(
						PBAcornForestActivity.this);
				startActivity(webIntent);
				break;
			} else {
				
				/*Toast.makeText(PBAcornForestActivity.this, 
		    			 getString(R.string.pb_network_not_available_general_message), 1000).show();*/
				Toast toast = Toast.makeText(PBAcornForestActivity.this, getString(R.string.pb_network_not_available_general_message), 
						1000);
				TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
				if( v1 != null) v1.setGravity(Gravity.CENTER);
				toast.show();
				break;	
			}

			
			
		case R.id.btn_acorn_forest_hill:
			
	        boolean hasInternetAvailableInForestHill = PBApplication.hasNetworkConnection(); // Check internet connectivity
			if(hasInternetAvailableInForestHill) {
				
				Intent greeIntent = PBRewardUtils.getGreeIntent(
						getApplicationContext());
				startActivity(greeIntent);
				break;
			} else {
				
				/*Toast.makeText(PBAcornForestActivity.this, 
		    			 getString(R.string.pb_network_not_available_general_message), 1000).show();*/
				Toast toast = Toast.makeText(PBAcornForestActivity.this, getString(R.string.pb_network_not_available_general_message), 
						1000);
				TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
				if( v1 != null) v1.setGravity(Gravity.CENTER);
				toast.show();
				break;	
			}

		}
	}

	@Override
	protected void handleHomeActionListener() {
		finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		
	}
	
}