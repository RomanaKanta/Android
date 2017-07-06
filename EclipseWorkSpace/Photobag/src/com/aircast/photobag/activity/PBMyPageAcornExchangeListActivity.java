package com.aircast.photobag.activity;

import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.application.PBNetwork;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.actionbar.ActionBar;

public class PBMyPageAcornExchangeListActivity extends PBAbsActionBarActivity 
	implements OnClickListener {

	private LinearLayout mLayoutWaiting;
	
	private RelativeLayout mLayoutHoney;
	private RelativeLayout mLayoutItune;
	private RelativeLayout mLayoutAmazon;
	private RelativeLayout mLayoutGold;
	
	boolean isJapan;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pb_layout_my_page_exchange_list);
		
		isJapan = new PBNetwork().isJapan((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE));
		
		ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(headerBar, getString(R.string.pb_exchange_acorn_title));
        // if come from koukaibukuro
 		if(getIntent().hasExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY)){
 	       	 boolean isFromLibrary = getIntent().getExtras().getBoolean(PBConstant.PREF_PASSWORD_FROM_LIBRARY);
	 		 if(isFromLibrary){
	 			 
	 			headerBar.setHomeLogo(R.drawable.openbag_actionbar_icon);
	 		 }		 
 		}
        LinearLayout layoutGolderAcornCount = (LinearLayout) findViewById(R.id.golden_acorn_count);
        layoutGolderAcornCount.setVisibility(isJapan ? View.VISIBLE : View.GONE);
        
        LinearLayout layoutGolderAcornInfo = (LinearLayout) findViewById(R.id.golden_acorn_info);
        layoutGolderAcornInfo.setVisibility(isJapan ? View.VISIBLE : View.GONE);

        
        mLayoutWaiting = (LinearLayout) findViewById(R.id.ll_loading_panel_waiting);
        mLayoutWaiting.setVisibility(View.VISIBLE);
        
        mLayoutHoney = (RelativeLayout) findViewById(R.id.btn_acorn_list_honey);
        mLayoutItune = (RelativeLayout) findViewById(R.id.btn_acorn_list_itune);
        mLayoutAmazon = (RelativeLayout) findViewById(R.id.btn_acorn_list_amazon);
        mLayoutGold = (RelativeLayout) findViewById(R.id.btn_acorn_list_gold);
        
        mLayoutHoney.setOnClickListener(this);
        mLayoutItune.setOnClickListener(this);
        mLayoutAmazon.setOnClickListener(this);
        mLayoutGold.setOnClickListener(this);
        
        TextView textViewAcornListForestLink2 = (TextView) findViewById(R.id.btn_acorn_list_forest_link_2);
        View viewAcornListForestLink2 = (View) findViewById(R.id.view_acorn_list_forest_link_2);
        if(!isJapan){
        	textViewAcornListForestLink2.setVisibility(View.GONE);
        	viewAcornListForestLink2.setVisibility(View.GONE);
        }else{
        	textViewAcornListForestLink2.setOnClickListener(this);
        }
        
        findViewById(R.id.btn_acorn_list_forest_link_1).setOnClickListener(this);
        
        (new TaskGetExchangeList()).execute();
        initAcornNumber();
	}
	
	private void initAcornNumber() {
		int acronsCounter = PBPreferenceUtils.getIntPref(
				getApplicationContext(), PBConstant.PREF_NAME,
				PBConstant.PREF_DONGURI_COUNT, 0);
		int goldCounter = PBPreferenceUtils.getIntPref(
				getApplicationContext(), PBConstant.PREF_NAME,
				PBConstant.PREF_GOLD_COUNT, 0);
		TextView acornNumber = (TextView) findViewById(R.id.text_acorn_count);	
		TextView goldNumber = (TextView) findViewById(R.id.text_gold_count);	
		
		acornNumber.setText(String.format(getString(R.string.pb_exchange_acorn_total_count), acronsCounter));
		goldNumber.setText(String.format(getString(R.string.pb_exchange_acorn_total_count), goldCounter));
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
		switch (v.getId()) {
		case R.id.btn_acorn_list_honey:
			Intent honeyExchangeAct = new Intent(PBMyPageAcornExchangeListActivity.this,
					PBMyPageHoneyExchangeActivity.class);
			honeyExchangeAct.putExtra("IS_GOLD", false);
			startActivity(honeyExchangeAct);
			
			break;
		case R.id.btn_acorn_list_gold:
			Intent goldExchangeAct = new Intent(PBMyPageAcornExchangeListActivity.this,
					PBMyPageHoneyExchangeActivity.class);
			goldExchangeAct.putExtra("IS_GOLD", true);
			startActivity(goldExchangeAct);
			
			break;
		case R.id.btn_acorn_list_itune:
			Intent ituneExchangeAct = new Intent(PBMyPageAcornExchangeListActivity.this,
					PBMyPageAcornExchangeMailActivity.class);
			ituneExchangeAct.putExtra("type", "itune");
			startActivity(ituneExchangeAct);
			
			break;
			
		case R.id.btn_acorn_list_amazon:
			Intent amazonExchangeAct = new Intent(PBMyPageAcornExchangeListActivity.this,
					PBMyPageAcornExchangeMailActivity.class);
			amazonExchangeAct.putExtra("type", "amazon");
			startActivity(amazonExchangeAct);
			
			break;
		case R.id.btn_acorn_list_forest_link_1:
		case R.id.btn_acorn_list_forest_link_2:
        	boolean hasInternet = PBApplication.hasNetworkConnection();
        	if (hasInternet) {
    			PBGeneralUtils.openAcornWebview(PBMyPageAcornExchangeListActivity.this);
    			break;
        	} else {
           	 	/*Toast.makeText(PBMyPageAcornExchangeListActivity.this, 
           			 getString(R.string.pb_network_not_available_general_message), 1000).show();*/
			   Toast toast = Toast.makeText(PBMyPageAcornExchangeListActivity.this, getString(R.string.pb_network_not_available_general_message), 
						1000);
			   TextView v1 = (TextView) toast.getView().findViewById(android.R.id.message);
			   if( v1 != null) v1.setGravity(Gravity.CENTER);
			   toast.show();
               break;        		
            }
		}
	}
	
	private class TaskGetExchangeList extends AsyncTask<Void, Void, Void> {
		
		private int mHoneyRate;
		private int mItuneRate;
		private int mAmazonRate;
		private int mGoldRate;
		
		public TaskGetExchangeList() {
			mHoneyRate = -1;
			mItuneRate = -1;
			mAmazonRate = -1;
			mGoldRate = -1;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			String token = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, 
					PBConstant.PREF_NAME_TOKEN, 
					"");			
			String version = PBApplication.getAppVersion();
			
			Response res = PBAPIHelper.getVersionAPI(token, version);
			try {
				JSONObject result = new JSONObject(res.decription);
				
				if (result != null) {
					if (result.has("items_for_exchange")) {
						JSONObject list = result.getJSONObject("items_for_exchange");
						
						if (list.has("maple")) {
							mHoneyRate = list.getJSONObject("maple").optInt("rate");
						}
					}
					if (result.has("items_for_exchange_with_goldacorns")) {
						JSONObject list = result.getJSONObject("items_for_exchange_with_goldacorns");
						
						if (list.has("maple")) {
							mGoldRate = list.getJSONObject("maple").optInt("rate");
						}
						if (list.has("itunes500")) {
							mItuneRate = list.getJSONObject("itunes500").optInt("rate");
						}
						if (list.has("amazon1000")) {
							mAmazonRate = list.getJSONObject("amazon1000").optInt("rate");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);		
			
			if (mHoneyRate < 0) {
				mHoneyRate = PBPreferenceUtils.getIntPref(
						PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_DONGURI_HONEY_EXCHANGE_RATE, 
						-1);
			}
			if (mHoneyRate > 0) {
				mLayoutHoney.setVisibility(View.VISIBLE);				
				((TextView)findViewById(R.id.text_acorn_list_honey)).setText(
						mHoneyRate + getString(R.string.pb_exchange_acorn_price));
				
				PBPreferenceUtils.saveIntPref(
						PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_DONGURI_HONEY_EXCHANGE_RATE,
						mHoneyRate);
			} 
			
			if (mItuneRate < 0) {
				mItuneRate = PBPreferenceUtils.getIntPref(
						PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_DONGURI_ITUNE_EXCHANGE_RATE, 
						-1);
			}
			if (mItuneRate > 0) {
				mLayoutItune.setVisibility(isJapan ? View.VISIBLE : View.GONE);				
				((TextView)findViewById(R.id.text_acorn_list_itune)).setText(
						mItuneRate + getString(R.string.pb_exchange_acorn_price_gold));
				
				PBPreferenceUtils.saveIntPref(
						PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_DONGURI_ITUNE_EXCHANGE_RATE,
						mItuneRate);
			}
			
			if (mAmazonRate < 0) {
				mAmazonRate = PBPreferenceUtils.getIntPref(
						PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_DONGURI_AMAZON_EXCHANGE_RATE, 
						-1);
			}
			if (mAmazonRate > 0) {
				mLayoutAmazon.setVisibility(isJapan ? View.VISIBLE : View.GONE);				
				((TextView)findViewById(R.id.text_acorn_list_amazon)).setText(
						mAmazonRate + getString(R.string.pb_exchange_acorn_price_gold));
				
				PBPreferenceUtils.saveIntPref(
						PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_DONGURI_AMAZON_EXCHANGE_RATE,
						mAmazonRate);
			} 
			
			if (mGoldRate < 0) {
				mGoldRate = PBPreferenceUtils.getIntPref(
						PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_DONGURI_GOLD_EXCHANGE_RATE, 
						-1);
			}
			if (mGoldRate > 0) {
				mLayoutGold.setVisibility(isJapan ? View.VISIBLE : View.GONE);				
				((TextView)findViewById(R.id.text_acorn_list_gold)).setText(
						mGoldRate + getString(R.string.pb_exchange_acorn_price_gold));
				
				PBPreferenceUtils.saveIntPref(
						PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_DONGURI_GOLD_EXCHANGE_RATE,
						mGoldRate);
			} 
			
			if (mHoneyRate <= 0 && mItuneRate <= 0 && mAmazonRate <= 0) {
				if (PBMyPageAcornExchangeListActivity.this.isFinishing()) {
					return;
				}
				PBGeneralUtils.showAlertDialogActionWithOnClick(
						PBMyPageAcornExchangeListActivity.this, 
						android.R.drawable.ic_dialog_alert, 
						getString(R.string.dialog_error_network_title), 
						getString(R.string.pb_exchange_acorn_empty), 
						getString(R.string.dialog_ok_btn), 
						new DialogInterface.OnClickListener() {							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								PBMyPageAcornExchangeListActivity.this.finish();
							}
						}, false);
			}
			mLayoutWaiting.setVisibility(View.GONE);
		}
	}
}