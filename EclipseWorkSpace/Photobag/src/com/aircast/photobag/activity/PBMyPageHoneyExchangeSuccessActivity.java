package com.aircast.photobag.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.aircast.photobag.R;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;

public class PBMyPageHoneyExchangeSuccessActivity extends PBAbsActionBarActivity implements  OnClickListener{
	private ActionBar mHomeBar;
	
	private FButton buttonBackToMain;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.pb_layout_my_page_exchange_success);

        mHomeBar = (ActionBar)findViewById(R.id.actionBar);
        mHomeBar.setTitle(getString(R.string.pb_exchange_honey_title));
        setHeader(mHomeBar, getString(R.string.pb_exchange_honey_title));
        
        buttonBackToMain = (FButton) findViewById(R.id.buttonBackToMyPage);
        buttonBackToMain.setOnClickListener(this);

        Bundle extras = null;
        if (getIntent() != null) {
        	extras = getIntent().getExtras();
	        if(extras!=null && extras.getInt(PBConstant.PREV_PAGE) == R.layout.pb_layout_download_purchase_notice){
	        	buttonBackToMain.setText(R.string.back_to_my_page);
	        	buttonBackToMain.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						handleHomeActionListener();
					}
				});
	        	((TextView) findViewById(R.id.pb_layout_exchange_success_text))
	        		.setText(R.string.pb_my_page_exchange_over);
	        }
        }      
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
//         try {
// 			AdstirTerminate.init(this);
// 		} catch (Exception e) {}
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.buttonBackToMyPage:
        	Intent backToMain = new Intent(this, PBMainTabBarActivity.class);
        	backToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	backToMain.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        	backToMain.putExtra(PBConstant.TAB_SET_BY_TAG, PBConstant.MY_PAGE_TAG);
        	startActivity(backToMain);
        	
        	finish();
        	break;
        default:
            break;
        }
    }

	@Override
	protected void handleHomeActionListener() {
		if (getParent() == null) {
		    setResult(RESULT_OK, null);
		} else {
		    getParent().setResult(RESULT_OK, null);
		}
		finish();		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		handleHomeActionListener();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		
	}
}
