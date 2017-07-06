package com.aircast.photobag.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;

import com.aircast.photobag.R;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.application.PBNetwork;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * Showed when password input is error.
 * */
public class PBDownloadWrongPassActivity extends PBAbsActionBarActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pb_layout_download_wrong_pass);
        
        ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(headerBar, getString(R.string.pb_download_wrong_title));
        
 		if(getIntent().hasExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY)){
	       	 boolean isFromLibrary = getIntent().getExtras().getBoolean(PBConstant.PREF_PASSWORD_FROM_LIBRARY);
	 		 if(isFromLibrary){
	 			 
	 			headerBar.setHomeLogo(R.drawable.openbag_actionbar_icon);
	 		 }		 
		}
        
        findViewById(R.id.btn_back_to_download).setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
        findViewById(R.id.layout_reward_forest).setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				Intent intentReward = new Intent(PBDownloadWrongPassActivity.this,
						PBAcornForestActivity.class);
				startActivity(intentReward);
			}
		});
        
        
        // Hiding Forest thumb is user out of Japan
        ImageView imageViewAcorn = (ImageView)findViewById(R.id.forest_thumb_wrong_pass);

        boolean isJapan = new PBNetwork().isJapan((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE));
        if(isJapan){
        	imageViewAcorn.setOnClickListener(new View.OnClickListener() {	
    			@Override
    			public void onClick(View v) {
    				Intent intentReward = new Intent(PBDownloadWrongPassActivity.this,
        					PBAcornForestActivity.class);
        			startActivity(intentReward);
    			}
    		});
        }else{
        	imageViewAcorn.setVisibility(View.GONE);
        }
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
    };
	
	@Override
	protected void handleHomeActionListener() {
		finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		
	}
}