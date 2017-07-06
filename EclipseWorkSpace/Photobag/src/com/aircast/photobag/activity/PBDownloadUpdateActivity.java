package com.aircast.photobag.activity;

import java.text.DateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Showed when password can be update, user can choose update now or not.
 * */
public class PBDownloadUpdateActivity extends PBAbsActionBarActivity {

	private ActionBar mHeaderBar;

	private FButton mBtnDownload;
	private FButton mCancel;

	private String mPassword;
	private Long mHistoryId; // bag id
	private String mCollectionId;
	private Long mChargeDate;
	private String mCollectionThumb;
	private int mAddibility;
	private int mUpdatable;

	private String mToken;
	private Context mContext;
	
    private final static int DIALOG_CANNOT_FIND_WITH_PASS = 2001;
    private final static int DIALOG_UNFINISH = 2002;
    private final static int DIALOG_EXPIRED = 2003;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pb_layout_download_update);

		mBtnDownload = (FButton) findViewById(R.id.btn_download_photos);
		mCancel = (FButton) findViewById(R.id.btn_not_download);
		mBtnDownload.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				PBConstant.doUpdate = true;
				mBtnDownload.setEnabled(false);
				mCancel.setEnabled(false);
				update();
				finish();
			}
		});
		mCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mBtnDownload.setEnabled(false);
				mCancel.setEnabled(false);
				finish();
			}
		});

		mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
		setHeader(mHeaderBar, getString(R.string.screen_title_dowload_update));

		Bundle extras = getIntent().getBundleExtra("data");
		if (extras == null) {
			finish();
			return;
		}
		mPassword = extras.getString(PBConstant.HISTORY_PASSWORD);
		mHistoryId = extras.getLong(PBConstant.HISTORY_ITEM_ID);
		mCollectionId = extras.getString(PBConstant.HISTORY_COLLECTION_ID);
		mChargeDate = extras.getLong(PBConstant.HISTORY_CHARGE_DATE);
		mCollectionThumb = extras.getString(PBConstant.COLLECTION_THUMB);
		mAddibility = extras.getInt(PBConstant.HISTORY_ADDIBILITY);
		mUpdatable = extras.getInt(PBConstant.HISTORY_IS_UPDATABLE);

		mContext = this;
		mToken = PBPreferenceUtils.getStringPref(mContext,
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");

	}

	@Override
    protected void onResume() {
        super.onResume();
     // V2 google analytics has been comment out
       /* if (PBTabBarActivity.gaTracker != null) {
        	PBTabBarActivity.gaTracker.trackPageView("PBDownloadUpdateActivity");
        }*/
    }
	
	//Added below activity life cycle method for Google analytics
	@Override
    public void onStart() {
 	    super.onStart();
	    System.out.println("Atik start Easy Tracker for PBDownloadUpdateActivity");
	    EasyTracker.getInstance(this).activityStart(this);
    }
  
    //Added below activity life cycle method for Google analytics
    @Override
    public void onStop() {
	    super.onStop();
	    System.out.println("Atik stop Easy Tracker for PBDownloadUpdateActivity");
	    EasyTracker.getInstance(this).activityStop(this);
    }
	
	protected void update() {
		Response result = PBAPIHelper.checkPhotoListInCollection(mPassword,
				mToken, false, null,false,"");

		if (result == null) {
			return;
		}

		if (result.errorCode == ResponseHandle.CODE_200_OK) {
			
			//Atik Analytics Information
            PBPreferenceUtils.saveStringPref(getApplicationContext(),
                    PBConstant.PREF_NAME,
                    PBConstant.PREF_PASSWORD_NAME_FOR_HISTORY_BUG, mPassword);
           
			PBPreferenceUtils.saveBoolPref(getApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_DOWNLOAD_PASSWORD_FOR_UPDATE, true);
			
			String deviceUUID = PBPreferenceUtils
 			         .getStringPref(PBApplication
 			           .getBaseApplicationContext(),
 			           PBConstant.PREF_NAME,
 			           PBConstant.PREF_NAME_UID, "0");
			
			String passForBug = PBPreferenceUtils
			         .getStringPref(PBApplication
          			           .getBaseApplicationContext(),
          			           PBConstant.PREF_NAME,
          			           PBConstant.PREF_PASSWORD_NAME_FOR_HISTORY_BUG, "");
			
          String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
          
          /*System.out.println("Label:"+"端末ID:"+"["+deviceUUID+"]"+"_合言葉:"+"["+passForBug+"]"+"_処理名(1)：[サーバからJSONデータダウンロード-更新時]"+"_処理開始時間："+"["+currentDateTimeString+"]"+"_SCREEN名:"+"[PBPasswordThumbsPreviewActivity]"+"_レスポンスコード："+"["+result.errorCode+"]");
          System.out.println("Action:"+"端末ID:"+"["+deviceUUID+"]"+"_合言葉:"+"["+passForBug+"]");
          System.out.println("Category:"+"[ダウンロード_履歴_不具合]");*/

         	// Analytics Implementaiton
          	// 	Analytics step 1 for updating adding password
         	/*EasyTracker easyTracker = EasyTracker.getInstance(PBDownloadUpdateActivity.this);
         	easyTracker.set(Fields.SCREEN_NAME, ""+deviceUUID+":PBDownloadUpdateActivity-受信履歴不具合");
         	easyTracker.send(MapBuilder
             	      .createEvent("[ダウンロード_履歴_チェック]",     // Event category (required)
             	                   "端末ID:"+"["+deviceUUID+"]"+"_合言葉:"+"["+passForBug+"]",  // Event action (required)
             	                   "端末ID:"+"["+deviceUUID+"]"+"_合言葉:"+"["+passForBug+"]"+"_処理名(1):[サーバからJSONデータダウンロード-更新時]"+"_処理開始時間:"+"["+currentDateTimeString+"]"+"_SCREEN名:"+"[PBDownloadUpdateActivity]"+"_レスポンスコード:"+"["+result.errorCode+"]",   // Event label
             	                   null)            // Event value
             	      .build()
             	  );*/
			
			Intent intent = new Intent(this, PBDownloadDownloadingActivity.class);
			Bundle data = getIntent().getBundleExtra("data");
			intent.putExtra(PBConstant.DOWNLOAD_UPDATE_OLD_BAG_ID, data.getLong(PBConstant.HISTORY_ITEM_ID, -1));
			intent.putExtra(PBConstant.COLLECTION_ID, data.getString(PBConstant.HISTORY_COLLECTION_ID));
			startActivity(intent);
		}
		else if (result.errorCode == ResponseHandle.CODE_400){
			String desc = result.decription;
			if (desc.contains("unfinished")){
				//showDialog(DIALOG_UNFINISH);
				Toast.makeText(mContext, getString(R.string.dl_alert_upload_unfinished_msg), Toast.LENGTH_SHORT).show();
			}
			else if (desc.contains("password")){
				//showDialog(DIALOG_CANNOT_FIND_WITH_PASS);
				Toast.makeText(mContext, getString(R.string.dl_alert_cannot_find_album_msg), Toast.LENGTH_SHORT).show();
			}
			else if (desc.contains("expired")){
				Toast.makeText(mContext, getString(R.string.dl_alert_cannot_find_album_msg), Toast.LENGTH_SHORT).show();
			}
			else if (desc.contains("use honey")){
				Toast.makeText(mContext, getString(R.string.dl_alert_cannot_find_album_msg), Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(mContext, getString(R.string.dl_alert_cannot_find_album_msg), Toast.LENGTH_SHORT).show();
			}
		} else {
			boolean hasInternet = PBApplication.hasNetworkConnection();
			if (!hasInternet) {
				   Toast toast = Toast.makeText(PBDownloadUpdateActivity.this, getString(R.string.pb_network_not_available_general_message), 
							1000);
				   TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
				   if( v1 != null) v1.setGravity(Gravity.CENTER);
				   toast.show();
			} else {
				Toast.makeText(mContext, getString(R.string.dl_alert_cannot_find_album_msg), Toast.LENGTH_SHORT).show();
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_CANNOT_FIND_WITH_PASS:
		case DIALOG_EXPIRED:
			return new AlertDialog.Builder(PBMainTabBarActivity.sMainContext)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.dl_alert_cannot_find_album_title)
					.setMessage(
							getString(R.string.dl_alert_cannot_find_album_msg))
					.setPositiveButton(R.string.dialog_ok_btn,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create();
		case DIALOG_UNFINISH:
			return new AlertDialog.Builder(PBMainTabBarActivity.sMainContext)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.dl_alert_upload_unfinished_title)
					.setMessage(
							getString(R.string.dl_alert_upload_unfinished_msg))
					.setPositiveButton(R.string.dialog_ok_btn,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create();
		default:
			break;
		}
		return null;
	}

}
