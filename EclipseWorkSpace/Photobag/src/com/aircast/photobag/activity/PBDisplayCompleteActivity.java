package com.aircast.photobag.activity;

import java.text.DecimalFormat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.aircast.photobag.R;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.fragment.PBDownloadFragment;
import com.aircast.photobag.fragment.PBHistoryMainFragment;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Show when download or give bear honey complete.
 * <p><b>TODO Separate it as many layouts that no business with each other are all controled by this activity.</b></p>
 * */
public class PBDisplayCompleteActivity extends PBAbsActionBarActivity {

	private static final String TAG = "PBDisplayCompleteActivity";
	private FButton mBtnPurchaseComplete;
	private FButton mBtnDownloadComplete;
	private FButton mBtnStartPurchase;

	private TextView mTvCountDownEceeded;
	private ActionBar mHeaderBar;
	private ExceededCount mExceededCount;
	private boolean isInExceededScreen = false;
	private boolean isDownloadComplete = false;

	private View viewOnMapleIsUsed, viewOnTrialHoneyIsUsed;
	private TextView textViewNotif;
	
	boolean isFromKoukaibukuro;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent() == null)
			return;

		isInExceededScreen = false;

		if (getIntent().getBooleanExtra(PBConstant.START_DOWNLOAD_COMPLETE,
				false)) {
			setContentView(R.layout.pb_layout_download_download_complete);
			mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
			setHeader(mHeaderBar,
					getString(R.string.screen_title_dowload_downloading_complete));
			mBtnDownloadComplete = (FButton) findViewById(R.id.btn_dl_download_complete);
			mBtnDownloadComplete.setOnClickListener(mOnClickListener);
			findViewById(R.id.layout_reward_forest).setOnClickListener(mOnClickListener);
			isDownloadComplete = true;
		} else if (getIntent().getBooleanExtra(
				PBConstant.START_PURCHASE_COMPLETE, false)) {
			boolean isUsingMaple = getIntent().getBooleanExtra(
					PBConstant.PURCHASE_BY_MAPLE, false);
			setContentView(R.layout.pb_layout_download_purchase_complete);
			mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
			setHeader(mHeaderBar,
					getString(R.string.screen_title_dowload_purchase_complete));
			mBtnPurchaseComplete = (FButton) findViewById(R.id.btn_dl_purchase_complete);
			
			Spannable buttonLabel = new SpannableString("  "+getString(R.string.dl_btn_input_pwd_text));
			Drawable drawable = getResources().getDrawable(R.drawable.btn_ico_dl);  
	        //drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
	        int intrinsicHeightWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
	                (float) 20, getResources().getDisplayMetrics()); // convert 20 dip  to int
	        drawable.setBounds(0, 0, intrinsicHeightWidth,intrinsicHeightWidth);
			ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);	
	        //Spannable buttonLabel = new SpannableString("  "+getString(R.string.dl_btn_input_pwd_text));	  
		    AlignmentSpan.Standard center_span = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
		    buttonLabel.setSpan(center_span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    buttonLabel.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        //buttonLabel.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        mBtnPurchaseComplete.setText(buttonLabel);
	        
			mBtnPurchaseComplete.setOnClickListener(mOnClickListener);

			viewOnMapleIsUsed = findViewById(R.id.viewOnMapleGive);
			viewOnTrialHoneyIsUsed = findViewById(R.id.viewOnTrialHoneyGive);
			textViewNotif = (TextView) findViewById(R.id.textViewNotification);
			if (isUsingMaple) {

				//PBApplication.makeToastMsg("IS USING MAPLE :" + true);
				viewOnMapleIsUsed.setVisibility(View.VISIBLE);
				viewOnTrialHoneyIsUsed.setVisibility(View.GONE);
				textViewNotif.setText(getString(R.string.receive_honey_notif));
			} else {

				//PBApplication.makeToastMsg("IS USING MAPLE :" + false);
				viewOnMapleIsUsed.setVisibility(View.GONE);
				viewOnTrialHoneyIsUsed.setVisibility(View.VISIBLE);
				textViewNotif
						.setText(getString(R.string.receive_trial_honey_notif));
				TextView tv = (TextView) findViewById(R.id.text_download_purch_third_otameshi); 
				String source = "<font color='black'>おためしハチミツ</font>をあげるとドングリはもらえません。<font color='black'>ハチミツ</font>を持っているときは、ハチミツが優先的に使われ、ドングリがもらえます。";
				tv.setText(Html.fromHtml(source));
			}

		} else if (getIntent().getBooleanExtra(
				PBConstant.START_PURCHASE_NOTICE, false)) {
			setContentView(R.layout.pb_layout_download_purchase_notice);
			mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
			setHeader(mHeaderBar,
					getString(R.string.screen_title_dowload_purchase_require));
			mBtnStartPurchase = (FButton) findViewById(R.id.btn_download_purchase_confirm);//TODO
			mBtnStartPurchase.setOnClickListener(mOnClickListener);
		} else if (getIntent()
				.getBooleanExtra(PBConstant.START_EXCEEDED, false)) {
			setContentView(R.layout.pb_layout_download_input_pass_exceeded);
			mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
			setHeader(mHeaderBar,
					getString(R.string.screen_title_dowload_input_exceeded));
			mTvCountDownEceeded = (TextView) findViewById(R.id.tv_input_pass_exceeded_text);
			isInExceededScreen = true;
		}
		 isFromKoukaibukuro = PBPreferenceUtils.getBoolPref(getApplicationContext(),
				PBConstant.PREF_NAME,
				PBConstant.PREF_PASSWORD_FROM_LIBRARY, false);
		 if(isFromKoukaibukuro){
			 
			// final Action homeAction = new IntentAction(this, null, R.drawable.icon);
			//	homeAction.setBackground(Color.TRANSPARENT);
				mHeaderBar.setHomeLogo(R.drawable.openbag_actionbar_icon);
		 }
		
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_dl_download_complete:
				
				
				
				
				if(isFromKoukaibukuro){
					PBPreferenceUtils.saveBoolPref(getApplicationContext(),
	    					PBConstant.PREF_NAME,
	    					PBConstant.PREF_PASSWORD_FROM_LIBRARY, false);
					finish();
					Intent mIntent = new Intent(PBMainTabBarActivity.sMainContext,
							PBHistoryMainActivity.class);
					startActivity(mIntent);
				} else {
					
					// Atik New SP value for checking in History Screen
					PBPreferenceUtils.saveBoolPref(getApplicationContext(),
	    					PBConstant.PREF_NAME,
	    					PBConstant.PREF_PASSWORD_NOT_FROM_LIBRARY, true);
					
					PBDownloadFragment.sDownloadComplete = 2;
					// PBHistoryInboxActivity.isInInbox = true;
					PBHistoryMainFragment.isInInbox = true;
					PBHistoryMainFragment.sSelected = PBHistoryMainFragment.DOWNLOAD_FINISH;
					finish();
					
				}
				break;

			case R.id.btn_dl_purchase_complete:
				
				
				//Atik Analytics Information
	            
				String password = PBPreferenceUtils.getStringPref(
						PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_PURCHASE_INFO_PASSWORD, "");
				
				System.out.println("Atik purchased password name is : "+password);
				
				PBPreferenceUtils.saveStringPref(getApplicationContext(),
	                    PBConstant.PREF_NAME,
	                    PBConstant.PREF_PASSWORD_NAME_FOR_HISTORY_BUG, password);
	           
				PBPreferenceUtils.saveBoolPref(getApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_DOWNLOAD_PURCHASE_PASSWORD, true);
				
				/*String deviceUUID = PBPreferenceUtils
	 			         .getStringPref(PBApplication
	 			           .getBaseApplicationContext(),
	 			           PBConstant.PREF_NAME,
	 			           PBConstant.PREF_NAME_UID, "0");
				
				String passForBug = PBPreferenceUtils
				         .getStringPref(PBApplication
	          			           .getBaseApplicationContext(),
	          			           PBConstant.PREF_NAME,
	          			           PBConstant.PREF_PASSWORD_NAME_FOR_HISTORY_BUG, "");
				
	          String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());*/
	          
	          /*System.out.println("Label:"+"端末ID:"+"["+deviceUUID+"]"+"_合言葉:"+"["+passForBug+"]"+"_処理名(1)：[サーバからJSONデータダウンロード-購入時]"+"_処理開始時間："+"["+currentDateTimeString+"]"+"_SCREEN名:"+"[PBDisplayCompleteActivity]");
	          System.out.println("Action:"+"端末ID:"+"["+deviceUUID+"]"+"_合言葉:"+"["+passForBug+"]");
	          System.out.println("Category:"+"[ダウンロード_履歴_不具合]");*/

	         	// Analytics Implementaiton
	            // Analytics during purchase password
	         	/*EasyTracker easyTracker = EasyTracker.getInstance(PBDisplayCompleteActivity.this);
	         	easyTracker.set(Fields.SCREEN_NAME, ""+deviceUUID+":PBDisplayCompleteActivity-受信履歴不具合");
	         	easyTracker.send(MapBuilder
	             	      .createEvent("[ダウンロード_履歴_チェック]",     // Event category (required)
	             	                   "端末ID:"+"["+deviceUUID+"]"+"_合言葉:"+"["+passForBug+"]",  // Event action (required)
	             	                   "端末ID:"+"["+deviceUUID+"]"+"_合言葉:"+"["+passForBug+"]"+"_処理名(1):[サーバからJSONデータダウンロード-購入時]"+"_処理開始時間:"+"["+currentDateTimeString+"]"+"_SCREEN名:"+"[PBDisplayCompleteActivity]",   // Event label
	             	                   null)            // Event value
	             	      .build()
	             	  );*/
	         	
	         	
				
				PBPreferenceUtils.saveBoolPref(PBDisplayCompleteActivity.this, PBConstant.PREF_NAME, 
		    			PBConstant.ISDOWNLOAD, true);
				Intent mIntent = new Intent(PBDisplayCompleteActivity.this,
						PBDownloadDownloadingActivity.class);
				startActivity(mIntent);
				finish();
				break;
				
			case R.id.layout_reward_forest:
				Intent intentReward = new Intent(PBDisplayCompleteActivity.this,
						PBAcornForestActivity.class);
				startActivity(intentReward);
				break;

			case R.id.btn_download_purchase_confirm:
				
				
				if(isFromKoukaibukuro){
					PBPreferenceUtils.saveBoolPref(getApplicationContext(),
	    					PBConstant.PREF_NAME,
	    					PBConstant.PREF_PASSWORD_FROM_LIBRARY, false);
					
					Intent intent = new Intent(PBMainTabBarActivity.sMainContext,
							PBHistoryMainFragment.class);
					intent.putExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY, true);
					startActivity(intent);
					
				}
				finish();
				break;
			}
		}
	};

	@Override
	public boolean dispatchKeyEvent(android.view.KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			
			boolean isFromKoukaibukuroPurchase = PBPreferenceUtils.getBoolPref(getApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_PASSWORD_FROM_LIBRARY, false);
			
			
			if(isFromKoukaibukuroPurchase){
				PBPreferenceUtils.saveBoolPref(getApplicationContext(),
    					PBConstant.PREF_NAME,
    					PBConstant.PREF_PASSWORD_FROM_LIBRARY, false);
				
//				Intent intent = new Intent(PBTabBarActivity.sMainContext,
//						PBHistoryMainActivity.class);
//				intent.putExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY, true);
//				startActivity(intent);
				finish();
			}else{
			if (isDownloadComplete)
				PBDownloadFragment.sDownloadComplete = 2;
			finish();
		}
			
			return true;
		}
		return super.dispatchKeyEvent(event);
	};

	@Override
	protected void onResume() {
		super.onResume();
		
		// V2 google analytics has been comment out 
		/*if (PBTabBarActivity.gaTracker != null) {
			PBTabBarActivity.gaTracker.trackPageView(TAG);
		}*/
		
		if (isInExceededScreen) {
			long startCountdownTime = PBPreferenceUtils.getLongPref(
					getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_DL_INPUT_PASS_RETRY_START_TIME, 0);

			if (mExceededCount == null) {
				long currenttime = System.currentTimeMillis();
				currenttime = currenttime - startCountdownTime;
				long maxCountDownTime = PBConstant.TIME_COUNT_DOWN_EXCEEDED_SCREEN * 60 * 1000;
				if (currenttime > maxCountDownTime) {
					resetCountDownTimer();
					finish();
					return;
				}
				mExceededCount = new ExceededCount(maxCountDownTime
						- currenttime, 0);
				mExceededCount.start();
			}
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		if (isInExceededScreen) {
			if (mExceededCount != null) {
				mExceededCount.cancel();
				mExceededCount = null;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	//Added below activity life cycle method for Google analytics
	@Override
    public void onStart() {
 	    super.onStart();
	    System.out.println("Atik start Easy Tracker for PBDisplayCompleteActivity");
	    EasyTracker.getInstance(this).activityStart(this);
    }
  
    //Added below activity life cycle method for Google analytics
    @Override
    public void onStop() {
	    super.onStop();
	    System.out.println("Atik stop Easy Tracker for PBDisplayCompleteActivity");
	    EasyTracker.getInstance(this).activityStop(this);
    }

	/**
	 * method for reset count down timer in case user input one password over 10
	 * times.
	 */
	private void resetCountDownTimer() {
		// reset PREF_IS_IN_EXCEEDED_MODE to false
		PBPreferenceUtils.saveBoolPref(getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_IS_IN_EXCEEDED_MODE,
				false);
		// reset start check time
		PBPreferenceUtils.saveLongPref(getApplicationContext(),
				PBConstant.PREF_NAME,
				PBConstant.PREF_DL_INPUT_PASS_RETRY_START_TIME, 0);
		// reset prev pass
		PBPreferenceUtils.saveStringPref(getApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_DL_INPUT_PASS_PREV_PASS,
				"");
	}

	/**
	 * custom CountDownTimer class for count down time.
	 */
	private class ExceededCount extends CountDownTimer {

		private String stringTimeFormat;
		private DecimalFormat formatter;

		// private static final long mCountDownInterval = 1000; // the number to
		// count down each time (in milliseconds)

		public ExceededCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, 1000);
			formatter = new DecimalFormat("#00");
		}

		@Override
		public void onFinish() {
			resetCountDownTimer();
			finish();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			stringTimeFormat = getString(
					R.string.pb_download_input_pass_exceeded_password_countdown,
					formatter
							.format((int) ((millisUntilFinished / 1000) / 3600)), // hour
					formatter
							.format((int) (((millisUntilFinished / 1000) / 60) % 60)), // min
					formatter.format((int) ((millisUntilFinished / 1000) % 60))); // sec
			mTvCountDownEceeded.setText(stringTimeFormat);
		}

	}

	@Override
	protected void handleHomeActionListener() {
		
		
		boolean isFromKoukaibukuroPurchase = PBPreferenceUtils.getBoolPref(getApplicationContext(),
				PBConstant.PREF_NAME,
				PBConstant.PREF_PASSWORD_FROM_LIBRARY, false);
		
		if(isFromKoukaibukuroPurchase){
			PBPreferenceUtils.saveBoolPref(getApplicationContext(),
					PBConstant.PREF_NAME,
					PBConstant.PREF_PASSWORD_FROM_LIBRARY, false);
			finish();
		}else{
			
			PBDownloadFragment.sDownloadComplete = 1;
			finish();
		}

		

	}
	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		// not doing in this case
	};
	
}
