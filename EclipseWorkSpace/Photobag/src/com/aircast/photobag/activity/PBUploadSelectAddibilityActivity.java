package com.aircast.photobag.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.services.SaveDBTask;
import com.aircast.photobag.services.UploadService;
import com.aircast.photobag.services.UploadService.MediaInfo;
import com.aircast.photobag.services.UploadService.ServiceUpdateUIListener;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * "You wanna someone else add photos in your password （´ ・(ｪ)・` ）クマ ?"
 * */
public class PBUploadSelectAddibilityActivity extends PBAbsActionBarActivity{
	
	@SuppressWarnings("unused")
	private final String TAG = "PBUploadSelectAddibilityActivity";
	
	private FButton mAddibilityYesBtn;
	private FButton mAddibilityNoBtn; 
	private LinearLayout mLayoutWaiting;
    
    private ArrayList<String> mSelectedMedia;
    private long[] mSelectedTypeMedia;
    private String mPassword;
    private String mToken;
    private String mCollectionId;
    private String mLongPassword;
    private boolean isPasswordPublic = false;
    private EasyTracker easyTracker = null;
    @Override
    public void onStart() {
	     super.onStart();
	     EasyTracker.getInstance(this).activityStart(this);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
	    EasyTracker.getInstance(this).activityStop(this);
    }
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pb_upload_select_addibility_layout);
		easyTracker = EasyTracker.getInstance(this);
		mAddibilityYesBtn = (FButton) findViewById(R.id.btn_addibility_yes);
		mAddibilityYesBtn.setOnClickListener(new OnAddibilityYesClickListener());
		mAddibilityNoBtn = (FButton) findViewById(R.id.btn_addibility_no);
		mAddibilityNoBtn.setOnClickListener(new OnAddibilityNoClickListener());
		mLayoutWaiting = (LinearLayout) findViewById(R.id.ll_loading_panel_waiting);
		
		Intent intent = getIntent();
		mPassword = intent.getStringExtra(PBConstant.INTENT_PASSWORD);
		mCollectionId = intent.getStringExtra(PBConstant.INTENT_PASSWORD_ID);
		mSelectedMedia = intent.getStringArrayListExtra(PBConstant.INTENT_SELECTED_MEDIA);
		mSelectedTypeMedia = intent.getLongArrayExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE);
		mLongPassword = intent.getStringExtra(PBConstant.INTENT_LONG_PASSWORD);
        isPasswordPublic = intent.getBooleanExtra(PBConstant.INTENT_IS_PUBLIC_PASSWORD, false);
		mToken = PBPreferenceUtils.getStringPref(getBaseContext(),
                PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
		// Disable Image add button when set to publish password publicly.
		if(isPasswordPublic) {
			mAddibilityYesBtn.setVisibility(View.GONE);
		}
		
		ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(headerBar, getString(R.string.screen_title_select_addiblity));
	}
	
	@Override
    protected void onResume() {
		super.onResume();
		
	}
	
	private class OnAddibilityYesClickListener implements OnClickListener {
		@Override
		public void onClick(View v){
			addibilityHandler(true);
		}
	}
	
	private class OnAddibilityNoClickListener implements OnClickListener {
		@Override
		public void onClick(View v){
			addibilityHandler(false);
		}
	}
	
	private void addibilityHandler(Boolean addibility){
		mAddibilityYesBtn.setEnabled(false);
		mAddibilityNoBtn.setEnabled(false);
		mLayoutWaiting.setVisibility(View.VISIBLE);
		
		(new SetAddibilityTask()).execute(addibility);
	}
	
	private class SetAddibilityTask extends AsyncTask<Boolean, Void, Void> {
		
		private Boolean mAddibility;
		
		@Override
		protected Void doInBackground(Boolean... addibilities){
			
			{
				boolean checkSdCard = PBGeneralUtils.checkSdcard(getBaseContext(),
						true, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						});
				if (!checkSdCard) {
					UploadingActivity.cancelUpload(getBaseContext(), mUploadedUrlList);
					finish();
					return null;
				}
			}
			
			mAddibility = addibilities.length > 0 ? addibilities[0] : false;
			PBPreferenceUtils.saveIntPref(
            		getBaseContext(),
                    PBConstant.UPLOAD_SERVICE_PREF,
                    PBConstant.PREF_ADDIBILITY,
                    mAddibility ? 1: 0);

			PBPreferenceUtils.saveIntPref(getBaseContext(),
                    PBConstant.UPLOAD_SERVICE_PREF,
                    PBConstant.PREF_IS_UPDATABLE, 0);
			PBPreferenceUtils.saveBoolPref(getBaseContext(),
					PBConstant.UPLOAD_SERVICE_PREF,
                    PBConstant.PREF_INPUT_SEQUENCE_FINISH, true);
			
			// check photos uploading finish ?
            if (PBPreferenceUtils.getBoolPref(getBaseContext(),
                    PBConstant.UPLOAD_SERVICE_PREF,
                    PBConstant.PREF_UPLOAD_FINISH, false)) {
                // upload service has finish, call UploadFinish API, save database 
                // and start PBConfirmPassword activity
                Response respond = PBAPIHelper.finishUploading(mCollectionId, mToken, mAddibility);
                if (respond.errorCode != ResponseHandle.CODE_200_OK) {
                    // error when finish upload
                    // 20120229 @lent5 fixed bug save thumb not completed
                    startUploadingActivity(mPassword, mCollectionId, mSelectedMedia, mSelectedTypeMedia);
                } else {// everything is ok
                    long chargeDate = 0;
                    if (!TextUtils.isEmpty(respond.decription)
                            && respond.decription.contains("collection")) {
                        try {
                        	JSONObject jObject = new JSONObject(respond.decription);
                            if (jObject != null && jObject.has("collection")) {
                                JSONObject jObjCollection = jObject.getJSONObject("collection");
                                if (jObjCollection != null && jObjCollection.has("charges_at")) {
                                    chargeDate = jObjCollection.getLong("charges_at");
                                    PBPreferenceUtils.saveLongPref(
                                            getBaseContext(),
                                            PBConstant.UPLOAD_SERVICE_PREF,
                                            PBConstant.PREF_CHARGE_DATE,
                                            chargeDate);
                                }
                                if (jObjCollection != null && jObjCollection.has("updated_at")) {
                                    PBPreferenceUtils.saveLongPref(
                                            getBaseContext(),
                                            PBConstant.UPLOAD_SERVICE_PREF,
                                            PBConstant.PREF_UPDATED_AT,
                                            jObjCollection.getLong("updated_at"));
                                }
                                if (jObjCollection != null && jObjCollection.has("can_add")) {
                                    PBPreferenceUtils.saveIntPref(
                                    		getBaseContext(),
                                            PBConstant.UPLOAD_SERVICE_PREF,
                                            PBConstant.PREF_ADDIBILITY,
                                            Integer.parseInt(jObjCollection.getString("can_add")));
                                }
                                if (jObjCollection != null && jObjCollection.has("can_save")) {
                                    PBPreferenceUtils.saveIntPref(
                                    		getBaseContext(),
                                            PBConstant.UPLOAD_SERVICE_PREF,
                                            PBConstant.PREF_SAVEMARK,
                                            Integer.parseInt(jObjCollection.getString("can_save")));
                                }
                                if (jObjCollection != null && jObjCollection.has("client_keep_days")) {
                                    PBPreferenceUtils.saveIntPref(
                                    		getBaseContext(),
                                            PBConstant.UPLOAD_SERVICE_PREF,
                                            PBConstant.PREF_SAVEDAYS,
                                            Integer.parseInt(jObjCollection.getString("client_keep_days")));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    final long chargeDateTmp = chargeDate; 
                    SaveDBTask saveDatabase = new SaveDBTask(
                    		PBMainTabBarActivity.sMainContext, mSelectedMedia,
                            mUploadedUrlList, new ServiceUpdateUIListener() {
                                @Override
                                public void updateUI(int uploadedPhotos,
                                        ArrayList<MediaInfo> uploadedUrlPhoto) {
                                    if(uploadedPhotos == PBConstant.UPLOAD_FINISH_COMPLETED){
                                        startConfirmUploadActivity(mPassword, chargeDateTmp);
                                        finish();
                                    }
                                }

                                @Override
                                public void updateProgressBar(int percent) {
                                    // not implement!
                                }

                                @Override
                                public void updateCompressBar(
                                        int totalFile, int percent) {
                                }

								@Override
								public void onUploadForbidden() {
									
								}

								@Override
								public void updateAnalytics(String data) {
									// TODO Auto-generated method stub
									Log.d("upladselect", data);
									 easyTracker.send(MapBuilder.createEvent("ScreenName-> PBUploadSelectAddibilityActivity", "VideoCompression",
											 data, null).build());
								}
                            });
                    saveDatabase.execute();

                    // stop upload service
                    // 20120229 @lent5 fixed bug save thumb not completed
                    Intent serviceIntent = new Intent(getBaseContext(),
                            UploadService.class);
                    stopService(serviceIntent);
                    // finish();
                }
            } else { // upload service has not finish, start uploading activity
                // 20120229 @lent5 fixed bug save thumb not completed
                startUploadingActivity(mPassword, mCollectionId, mSelectedMedia, mSelectedTypeMedia);
            }
						
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			mAddibilityYesBtn.setEnabled(true);
			mAddibilityNoBtn.setEnabled(true);
			mLayoutWaiting.setVisibility(View.GONE);
		}
	}

	
	private void startUploadingActivity(String password, String passwordId,
			ArrayList<String> selectedImages, long[] selectedTypeImages) {
		Intent intent = new Intent(PBUploadSelectAddibilityActivity.this, UploadingActivity.class);
		intent.putExtra(PBConstant.INTENT_PASSWORD, password/* mPassword */);
		intent.putExtra(PBConstant.INTENT_PASSWORD_ID, passwordId/* passwordId */);
		intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA, selectedImages/* mSelectedImages */);
		intent.putExtra(PBConstant.INTENT_SELECTED_MEDIA_TYPE,
				selectedTypeImages/* mSelectedImages */);
		startActivity(intent);
		finish();
	}
   

	private void startConfirmUploadActivity(String password, long chargeDate){
		Bundle extras = new Bundle();
		extras.putString(PBConstant.COLLECTION_PAGE_NAME,
				UploadingActivity.class.getName());
		extras.putString(PBConstant.COLLECTION_ID, PBPreferenceUtils
				.getStringPref(getBaseContext(),
						PBConstant.UPLOAD_SERVICE_PREF,
						PBConstant.PREF_COLLECTION_ID, ""));
		extras.putString(PBConstant.COLLECTION_PASSWORD, password/* mPassword */);
		extras.putLong(PBConstant.COLLECTION_CHARGE_AT, chargeDate);
		extras.putString(PBConstant.COLLECTION_THUMB, PBPreferenceUtils
				.getStringPref(getBaseContext(),
						PBConstant.UPLOAD_SERVICE_PREF,
						PBConstant.PREF_COLLECTION_THUMB, ""));
		Intent intent = new Intent(getBaseContext(),
				PBConfirmPasswordActivity.class);
		intent.putExtra("data", extras);
		intent.putExtra(PBConstant.IS_OWNER, true);
		startActivityForResult(intent, PBConstant.REQUEST_CODE_OPEN_CONFIRMPASS);
		Log.d("TinhNH1", "PBUpload Confirm: startConfirmUploadActivity");
	}

	private static ArrayList<MediaInfo> mUploadedUrlList;

	public static void setPhotoUrlList(ArrayList<MediaInfo> photoUrlList) {
		mUploadedUrlList = photoUrlList;
	}

	@Override
	protected void handleHomeActionListener() {
		
		// Atik need modification for Koukaibukuro setting
		Intent intent = new Intent(getBaseContext(),
				PBUploadConfirmActivity.class);
        intent.putStringArrayListExtra(
                PBConstant.INTENT_SELECTED_MEDIA, mSelectedMedia);
        intent.putExtra(
                PBConstant.INTENT_SELECTED_MEDIA_TYPE, mSelectedTypeMedia);
        intent.putExtra(PBConstant.INTENT_LONG_PASSWORD, mLongPassword);
        intent.putExtra(PBConstant.INTENT_PASSWORD, mPassword);
        if(isPasswordPublic) {
            intent.putExtra(PBConstant.INTENT_IS_PUBLIC_PASSWORD, true);
        }
        startActivity(intent);
        finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {

	}

}
