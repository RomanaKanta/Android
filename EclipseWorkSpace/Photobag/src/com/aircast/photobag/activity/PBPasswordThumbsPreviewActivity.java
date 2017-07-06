package com.aircast.photobag.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircast.koukaibukuro.util.Constant;
import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.log.SdcardException;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.google.analytics.tracking.android.EasyTracker;

public class PBPasswordThumbsPreviewActivity extends PBAbsActionBarActivity implements OnClickListener{

	private static final String TAG = "PBPasswordThumbsPreviewActivity";

	private ActionBar mHeaderBar;
	private TextView mTvPurchaseNoticePhotoCount;
	private TextView mTvPurchaseNoticeDownloadedCount;
	private TextView mPurchaseNoticeThumbListCount;
	private ImageView mImvPurchasePhotoThumb;
    private FrameLayout mFrameThumbLoadLayout;
	private String mPassWord;
	private int counterForTappedItem = 0;
	
	private int mCountForCacheThumb = 0;
    private int totalNumberThumbFromServer = 0;
    private int numberOfCacheThumpTouched = 0;
    private int numberOfCacheThumpTouchedForTextView = 0;

    private ArrayList<String> mThumbListCacheList = new ArrayList<String>();

	private boolean isFromKoukaibukuro;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pb_layout_download_preview);
		
		Log.d(TAG, TAG);
		
		mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
		setHeader(mHeaderBar, getString(R.string.pb_download_preview_title));
		FButton mDownloadFlatButton = (FButton) findViewById(R.id.btn_download_preview_confirm);
		Spannable buttonLabel = new SpannableString("  "
				+ getString(R.string.dl_btn_input_pwd_text));
		Drawable drawable = getResources().getDrawable(R.drawable.btn_ico_dl);
		int intrinsicHeightWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, (float) 20, getResources()
						.getDisplayMetrics()); // convert 20 dip to int

		drawable.setBounds(0, 0, intrinsicHeightWidth, intrinsicHeightWidth);
		ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);

		AlignmentSpan.Standard center_span = new AlignmentSpan.Standard(
				Layout.Alignment.ALIGN_CENTER);
		buttonLabel.setSpan(center_span, 0, 1,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		buttonLabel
				.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mDownloadFlatButton.setText(buttonLabel);
		mDownloadFlatButton.setOnClickListener(this);
		findViewById(R.id.btn_download_preview_cancel).setOnClickListener(this);
		
		
		isFromKoukaibukuro = PBPreferenceUtils.getBoolPref(getApplicationContext(),
				PBConstant.PREF_NAME,
				PBConstant.PREF_PASSWORD_FROM_LIBRARY, false);
		 if(isFromKoukaibukuro){
			 
				mHeaderBar.setHomeLogo(R.drawable.openbag_actionbar_icon);
				// Atik put analytics information
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
				
			
				int responseCode = PBPreferenceUtils
    			         .getIntPref(PBApplication
	           			           .getBaseApplicationContext(),  PBConstant.PREF_NAME, 
	           			           Constant.ANALYTICS_RESPONSE_CODE_DOWNLOAD_JSON, 0);
	           			           

  			
               /*String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
               
               System.out.println("Label:"+"端末ID:"+"["+deviceUUID+"]"+"_合言葉:"+"["+passForBug+"]"+"_処理名(1)：[サーバからJSONデータダウンロード-公開袋から]"+"_処理開始時間："+"["+currentDateTimeString+"]"+"_SCREEN名:"+"[PBPasswordThumbsPreviewActivity]"+"_レスポンスコード："+"["+responseCode+"]");
               System.out.println("Action:"+"端末ID:"+"["+deviceUUID+"]"+"_合言葉:"+"["+passForBug+"]");
               System.out.println("Category:"+"[ダウンロード_履歴_不具合]");
     
              	// Analytics Implementaiton
              	// Analytics bug fix data when from download step 1
              	EasyTracker easyTracker = EasyTracker.getInstance(PBPasswordThumbsPreviewActivity.this);
              	easyTracker.set(Fields.SCREEN_NAME, ""+deviceUUID+":PBPasswordThumbsPreviewActivity-受信履歴不具合");
              	easyTracker.send(MapBuilder
                  	      .createEvent("[ダウンロード_履歴_チェック]",     // Event category (required)
                  	                   "端末ID:"+"["+deviceUUID+"]"+"_合言葉:"+"["+passForBug+"]",  // Event action (required)
                  	                   "端末ID:"+"["+deviceUUID+"]"+"_合言葉:"+"["+passForBug+"]"+"_処理名(1):[サーバからJSONデータダウンロード-公開袋から]"+"_処理開始時間:"+"["+currentDateTimeString+"]"+"_SCREEN名:"+"[PBPasswordThumbsPreviewActivity]"+"_レスポンスコード:"+"["+responseCode+"]",   // Event label
                  	                   null)            // Event value
                  	      .build()
                  	  );*/
		 }
		
		
		 
		 
		mTvPurchaseNoticePhotoCount = (TextView) findViewById(R.id.tv_purchase_notice_photo_count);
		mTvPurchaseNoticeDownloadedCount = (TextView) findViewById(R.id.tv_purchase_notice_downloaded_count);
		mPurchaseNoticeThumbListCount = (TextView) findViewById(R.id.thumb_count_cache);
		mImvPurchasePhotoThumb = (ImageView) findViewById(R.id.imv_purchase_notice_thumb);
		mFrameThumbLoadLayout = (FrameLayout) findViewById(R.id.purchase_notice_thumb_click);

		mFrameThumbLoadLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				mFrameThumbLoadLayout.setEnabled(false);
				if(totalNumberThumbFromServer > 0 && 
						totalNumberThumbFromServer == mCountForCacheThumb ) {
						System.out.println("Atik now show from thumb");

						if(numberOfCacheThumpTouched == totalNumberThumbFromServer ) {
							numberOfCacheThumpTouched = 0;
							numberOfCacheThumpTouchedForTextView = 0;
						}

						System.out.println("Atik inside cache touched " + numberOfCacheThumpTouched);
						System.out.println("Atik inside cache touched image " + mThumbListCacheList.get(numberOfCacheThumpTouched));
						
						Bitmap bmp = BitmapFactory.decodeFile(mThumbListCacheList.get(numberOfCacheThumpTouched));
						numberOfCacheThumpTouched ++;
						numberOfCacheThumpTouchedForTextView++;
						
						if (bmp != null) {
							if (mImvPurchasePhotoThumb != null) {
								mImvPurchasePhotoThumb.setImageBitmap(bmp);
								
								System.out.println("Atik numberOfCacheThumpTouchedForTextView" + numberOfCacheThumpTouchedForTextView);
								mPurchaseNoticeThumbListCount.setText(numberOfCacheThumpTouchedForTextView+"/"
										+ totalNumberThumbFromServer);
								
							}
						} else {
							if (mImvPurchasePhotoThumb != null) {
								mImvPurchasePhotoThumb.setImageResource(R.drawable.thumb_error);
							}
						}
						mFrameThumbLoadLayout.setEnabled(true); // thumbnail tap enable again
						
				} else {
					(new GetThumbListForPassword()).execute(); // execute server API for getting thumbs and save
					//mFrameThumbLoadLayout.setEnabled(true);
				}		
			}
		});

        
        mPassWord = PBPreferenceUtils.getStringPref(getApplicationContext(),
				PBConstant.PREF_NAME,
				PBConstant.PREF_DL_PHOTOLIST_PASS, "");
        
        Log.d("mPassWord", mPassWord);

		// start asynctask for downloading photo list
		(new GetThumbListForPassword()).execute();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_download_preview_confirm:
			//giveHoneyToPassword();
			PBPreferenceUtils.saveBoolPref(PBPasswordThumbsPreviewActivity.this, PBConstant.PREF_NAME, 
	    			PBConstant.ISDOWNLOAD, true);
			Intent mIntent = new Intent(PBPasswordThumbsPreviewActivity.this,
					PBDownloadDownloadingActivity.class);
			startActivity(mIntent);
			finish();
			
			break;
		case R.id.btn_download_preview_cancel:
			finish();
			break;
			
			
		default:
			break;
		}
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	//Added below activity life cycle method for Google analytics
	@Override
    public void onStart() {
 	    super.onStart();
	    System.out.println("Atik start Easy Tracker for PBPasswordThumbsPreviewActivity");
	    EasyTracker.getInstance(this).activityStart(this);
    }
  
    //Added below activity life cycle method for Google analytics
    @Override
    public void onStop() {
	    super.onStop();
	    System.out.println("Atik stop Easy Tracker for PBPasswordThumbsPreviewActivity");
	    EasyTracker.getInstance(this).activityStop(this);
    }
    
	@Override
	protected void handleHomeActionListener() {

		finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		
	};
	
	/** class for downloading thumnail list for a specific password*/
	private class GetThumbListForPassword extends AsyncTask<Void, Integer, Void> {
		String thumbImgPath = "";
        String[] mStrings = null;
		/** total photo of collection */
		int mTotalPhotos = 0;
		
		private String jsonDataString = "";
		private String photoListPassword = "";
		private String tokenKey = "";

		public GetThumbListForPassword() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			this.jsonDataString = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_DL_PHOTOLIST_JSON_DATA, "");
			this.photoListPassword = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_DL_PHOTOLIST_PASS, "");
			this.tokenKey = PBPreferenceUtils.getStringPref(
					getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_NAME_TOKEN, "");
			Log.d("DOWNLOAD", "JSON STR:"+jsonDataString);
			if (TextUtils.isEmpty(this.jsonDataString))
				return;

			Log.d("jsonDataString", jsonDataString.toString());
			// parse JSON data
			try {
				JSONObject result = new JSONObject(this.jsonDataString);
				if (result != null) {
					if (result.has("downloaded_users_count")) {
						if (mTvPurchaseNoticeDownloadedCount != null)
							mTvPurchaseNoticeDownloadedCount.setText(""
									+ result.getInt("downloaded_users_count"));
					}
					if (result.has("photos_count")) {
						if (mTvPurchaseNoticePhotoCount != null)
							mTvPurchaseNoticePhotoCount.setText(""
									+ result.getInt("photos_count"));
					}
				}
				result = null;
			} catch (JSONException e) {
				e.printStackTrace();
			}
	
		}

		@Override
		protected Void doInBackground(Void... params) {
			
		String tokenKey = PBPreferenceUtils.getStringPref(
		getApplicationContext(), PBConstant.PREF_NAME,
		PBConstant.PREF_NAME_TOKEN, "");
		Response response;
		response = PBAPIHelper.getThumbnailFromServerForPurchasePassword(tokenKey, mPassWord);
		System.out.println("Atik response code"+response.errorCode);
		if (response.errorCode == ResponseHandle.CODE_200_OK) {
			try {
				String thumbUrl = null;
				JSONObject jsonCompleteObject = null;
				jsonCompleteObject = new JSONObject(response.decription);
				JSONArray jsonPhotosArray = jsonCompleteObject.getJSONArray("photos");
				System.out.println("Atik thumb length" + jsonPhotosArray.length());
				mTotalPhotos = jsonPhotosArray.length();
				totalNumberThumbFromServer = mTotalPhotos;
				
				mStrings = new String[mTotalPhotos];
				for (int i = 0; i < mTotalPhotos; i++) {
					JSONObject jsonObject = jsonPhotosArray.getJSONObject(i);
					mStrings[i] = jsonObject.getString("thumb");
					
				}
				
				if(counterForTappedItem == 0) {
					if(mStrings.length == 1) {  // When there is only one item , index need to be fix
						/*thumbUrl = mStrings[1];
						thumbImgPath = PBGeneralUtils
								.getPathFromCacheFolder(thumbUrl);
						counterForTappedItem ++;	*/		
						thumbUrl = mStrings[0];
						thumbImgPath = PBGeneralUtils
								.getPathFromCacheFolder(thumbUrl);
						//counterForTappedItem ++;	
					} else {
						thumbUrl = mStrings[1];
						thumbImgPath = PBGeneralUtils
								.getPathFromCacheFolder(thumbUrl);
						counterForTappedItem ++;
						Log.d("counterForTappedItem", ""+counterForTappedItem);
					}

				} else {
					if(counterForTappedItem == mTotalPhotos-1 ) {
						System.out.println("Atik all photos have showed");
						counterForTappedItem = 0;
						thumbUrl = mStrings[counterForTappedItem];
						thumbImgPath = PBGeneralUtils
								.getPathFromCacheFolder(thumbUrl);
						//counterForTappedItem ++;
					} else if(counterForTappedItem>0) {
						int countThumbIndex = counterForTappedItem;
						thumbUrl = mStrings[countThumbIndex+1];
						thumbImgPath = PBGeneralUtils
								.getPathFromCacheFolder(thumbUrl);
						counterForTappedItem ++;
					}
				}
				
				
				
				mThumbListCacheList.add(thumbImgPath);
				mCountForCacheThumb ++;
				System.out.println("Atik total number click for cache" + mCountForCacheThumb);
				
				// Atik If file not exists , then need to save into SDCard
				// TODO need to create this file into temporary folder and after exiting from this activity delete those temporary files
				File file = new File(thumbImgPath);
				if (!file.exists()) {
					boolean saveImgThumbResult = false;
					try {
						saveImgThumbResult = PBAPIHelper.savePhoto(
								tokenKey, thumbUrl, mPassWord, true, null);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SdcardException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i("mapp", "save purchase thumb OK --> "
							+ saveImgThumbResult);
				}

				/*for(int i = 0; i < mStrings.length ; i ++) 
				{
					System.out.println("Atik All photo list" + mStrings[i]);
				}*/
			
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// getting photo from cache
			
			Log.d("totalNumberThumbFromServer", ""+totalNumberThumbFromServer);
			Log.d("mCountForCacheThumb", ""+mCountForCacheThumb);
			Log.d("mTotalPhotos", ""+mTotalPhotos);
			Log.d("mThumbListCacheList", ""+mThumbListCacheList.size());
			Bitmap bmp = BitmapFactory.decodeFile(thumbImgPath);
			if (bmp != null) {
				if (mImvPurchasePhotoThumb != null) {
					mImvPurchasePhotoThumb.setImageBitmap(bmp);
					
//					if(mTotalPhotos == mCountForCacheThumb ) {
//						mPurchaseNoticeThumbListCount.setText(1+"/"
//								+ mTotalPhotos);
//						Log.d("mTotalPhotos == mCountForCacheThumb", "click");
//					} else {
						mPurchaseNoticeThumbListCount.setText(mCountForCacheThumb+"/"
								+ mTotalPhotos);
//					}

				}
			} else {
				if (mImvPurchasePhotoThumb != null) {
					mImvPurchasePhotoThumb.setImageResource(R.drawable.thumb_error);
				}
			}

			mFrameThumbLoadLayout.setEnabled(true); // Again thumbnail button enabled after server operation done
		}

	
	}
	
	@Override
	public boolean dispatchKeyEvent(android.view.KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			
		 finish();
			return true;
		}
		return super.dispatchKeyEvent(event);
	};
	


}
