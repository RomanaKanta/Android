package com.aircast.photobag.activity;

// import com.ad_stir.AdstirTerminate;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * Show warning message when user want to add photo in someone else's password.
 * */
public class PBUploadWarningActivity extends PBAbsActionBarActivity
implements OnClickListener {
	
	private ImageView mIconCheckBox;
	private FButton mBtnConfirmUpload;
	
	private boolean mIsConfirmed;
	private String mPassword;
    private String mCollectionId;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pb_layout_warning_upload);
        
        ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(headerBar, getString(R.string.pb_upload_confirm_title));
        
        mIconCheckBox = (ImageView) findViewById(R.id.icon_checkbox_confirm_upload);
        mBtnConfirmUpload  = (FButton) findViewById(R.id.btn_confirm_upload);
        
        mIsConfirmed = false;
        setUIStatus(mIsConfirmed);
        
        mIconCheckBox.setOnClickListener(this);
        mBtnConfirmUpload.setOnClickListener(this);
	}
	
	private void setUIStatus(boolean status) {
		mIsConfirmed = status;
		
		if (mIsConfirmed) {
			mBtnConfirmUpload.setBackgroundColor(getResources().getColor(R.color.fbutton_color_shape_orange));
			//mBtnConfirmUpload.setBackgroundResource(R.drawable.btn_shape_orange);
			//mIconCheckBox.setImageResource(R.drawable.checkbox_red_on);
			mIconCheckBox.setImageResource(R.drawable.checkbox_red_on_flat); // Change the button icon
		} 
		else {
			mBtnConfirmUpload.setBackgroundColor(getResources().getColor(R.color.fbutton_color_hard));
			//mBtnConfirmUpload.setBackgroundResource(R.drawable.btn_hard);
			//mIconCheckBox.setImageResource(R.drawable.checkbox_red_off);
			mIconCheckBox.setImageResource(R.drawable.checkbox_red_off_flat); // Change the button icon
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
// 		try {
// 			AdstirTerminate.init(this);
// 		} catch (Exception e) {}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.icon_checkbox_confirm_upload:
			setUIStatus(!mIsConfirmed);
			
			break;
		case R.id.btn_confirm_upload:
			if (mIsConfirmed) {
				Intent intent = new Intent(PBUploadWarningActivity.this, 
						SelectMultipleImageActivity.class);
				
				
				
				// Atik check whether item exists in sent item or not
				Bundle extras = getIntent().getExtras();
				if (extras == null) {
					System.out.println("Atik no data come in intent");
				}
				mCollectionId = extras.getString(PBConstant.COLLECTION_ID);
				
    			PBDatabaseManager dbMgr = PBDatabaseManager
    					.getInstance(PBApplication.getBaseApplicationContext());
    		   // Cursor cursor = dbMgr.getHistoriesCursor(PBDatabaseDefinition.HISTORY_INBOX);
    		    
    		    boolean existAikotobaInHistoryInbox = false;
       		    /*if (cursor.moveToFirst()){
					   while(!cursor.isAfterLast()){
						  String collectionID = cursor.getString(cursor.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
					      if(mCollectionId.equalsIgnoreCase(collectionID)) {
					    	  System.out.println("Atik call password exists in sent item");
					    	  existAikotobaInSentItem  = true;
					      } else {
					    	  System.out.println("Atik call password not exists in sent item");
					      }
					      cursor.moveToNext();
					   }
					}
			    cursor.close();*/
			    
    		    existAikotobaInHistoryInbox = dbMgr.isPasswordExistsInHistoryInbox(mCollectionId);
			    
			    
			    if(existAikotobaInHistoryInbox) {
					PBPreferenceUtils.saveBoolPref(getApplicationContext(),
							PBConstant.PREF_NAME, PBConstant.PREF_NEED_REFRESH_HISTORY_SCREEN,
							true);
			    }
			    
    		    
				intent.putExtras(getIntent().getExtras());
				startActivity(intent);
				finish();
			}
			else {
				Toast.makeText(PBUploadWarningActivity.this, 
						R.string.pb_upload_confirm_check_hint, 
						Toast.LENGTH_SHORT).show();
			}
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
	
}