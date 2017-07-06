package com.aircast.photobag.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.aircast.photobag.R;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * Confirm the migration code and send code through mail
 * */
public class PBSettingLicenseActivity extends PBAbsActionBarActivity
implements OnClickListener {
	
    private FButton mDoneButton;    
    private static final int MSG_SEND_MIGRATION_CODE = 1;
    private static String urlForPC;
    private TextView migrationCode;
    private Context cxt;
	private View mLvLoadingWaiting;
	private Handler backgroundHandler = new Handler();

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pb_layout_setting_license);
        
        ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(headerBar, getString(R.string.pb_setting_license_title));
        
        cxt = PBApplication.getBaseApplicationContext();
    

    }

	

	@Override
	protected void handleHomeActionListener() {
		finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	

}
