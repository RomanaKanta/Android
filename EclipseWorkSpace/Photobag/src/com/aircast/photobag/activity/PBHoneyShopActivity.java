package com.aircast.photobag.activity;

import java.util.Random;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.adapter.PBHoneyShopListAdapter;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.inappbilling.PBIabHelper;
import com.aircast.photobag.inappbilling.PBIabResult;
import com.aircast.photobag.inappbilling.PBInventory;
import com.aircast.photobag.inappbilling.PBPurchase;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * Honey Shop.
 * <p>Use in-app-billing V3.</p>
 * <p>Show goods [honey1, honey3, honey6], if want to add some, need change <b>everywhere</b>_(:3 」∠)_</p>
 * */
public class PBHoneyShopActivity extends PBAbsActionBarActivity
	implements OnClickListener{
	
	private LinearLayout mLayoutWaiting;
	private ScrollView mLayoutIntro;
	private TextView mTextHoneyCount;
	private FButton mBtnPurchaseIntro;
	
	private PBHoneyShopListAdapter mListAdapter;
	private ListView mListViewGoods;
	private ActionBar mHeaderBar;
	
	private PBIabHelper mHelper;	
	private PBPurchase mPurchase;
	
	private boolean mIntroStatus;
	private int mStatus;
	private static final int STATUS_IDLE = 1;
	private static final int STATUS_BUSY = 2;
	
	public final int MSG_START_PURCHASE = 10001;
	private final int MSG_CONFIRM_SUC = 20001;
	private final int MSG_PURCHASE_ERROR = 20002;
	
	private boolean mIsMultyRequest = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pb_layout_honey_shop_activity);
		
		mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(mHeaderBar, getString(R.string.pb_honey_shop_title));
		
        mListViewGoods = (ListView) findViewById(R.id.listview_honey_shop_list);
        mTextHoneyCount = (TextView) findViewById(R.id.text_honey_shop_count);
        mLayoutWaiting = (LinearLayout) findViewById(R.id.ll_honey_shop_loading);
        mLayoutIntro = (ScrollView) findViewById(R.id.ll_honey_shop_intro);
        mLayoutWaiting.setVisibility(View.GONE);
        updateHoneyCount();
        
        mBtnPurchaseIntro = (FButton) findViewById(R.id.btn_show_purchase_info);
        mBtnPurchaseIntro.setOnClickListener(this);
        findViewById(R.id.text_show_purchase_info).setOnClickListener(this);
        
        mIntroStatus = false;
        updateUI(STATUS_BUSY);
        
        // init IAB
        mHelper = new PBIabHelper(PBHoneyShopActivity.this, null);
        mHelper.startSetup(new PBIabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(PBIabResult result, Bundle data) {
                if (!result.isSuccess()) {
                	Message msg = Message.obtain(null, 
                			MSG_PURCHASE_ERROR, 
        					getString(R.string.pb_honey_shop_error_init));
        			
        			mHandler.sendMessage(msg);
                }
                
                initGoodsList(data);
            }
        });
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
	
	private void updateUI(int status) {
		mStatus = status;
		
		mLayoutWaiting.setVisibility(mStatus == STATUS_BUSY
				? View.VISIBLE
				: View.GONE);
	}
	
	private void updateHoneyCount() {
		int honey = PBPreferenceUtils.getIntPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_HONEY_BONUS, 0);
		int maple = PBPreferenceUtils.getIntPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_MAPLE_COUNT, 0);
		
		try {
			mTextHoneyCount.setText(
					String.format(getString(R.string.pb_honey_shop_honey_count), 
							maple, honey));
		} catch (Exception e) {}
	}
	
	private void initGoodsList(Bundle data) {		
		mListAdapter = new PBHoneyShopListAdapter(PBHoneyShopActivity.this, data);
		mListViewGoods.setAdapter(mListAdapter);		
		
		mHelper.queryInventoryAsync(mGotInventoryListener);
	}
	
	public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_START_PURCHASE:
				sendPurchaseRequest(String.valueOf(msg.obj));
				
				break;
				
			case MSG_CONFIRM_SUC:
				if (mIsMultyRequest) {
					mIsMultyRequest = false;
				} else {
					Toast.makeText(PBHoneyShopActivity.this, 
							getString(R.string.pb_honey_shop_purchase_suc), 
							Toast.LENGTH_SHORT).show();
				}
				updateHoneyCount();
				updateUI(STATUS_IDLE);
				
				mPurchase = null;
				
				break;
				
			case MSG_PURCHASE_ERROR:		
				showErrorMsg(msg.obj == null
						? null				
						: String.valueOf(msg.obj));
				
				break;
			default:
				break;
			}
		}
	};
	
	private void showErrorMsg(String errorMsg) {
		if (PBHoneyShopActivity.this.isFinishing()) {
			return;
		}
		if (TextUtils.isEmpty(errorMsg)) {
			errorMsg = getString(R.string.pb_honey_shop_error_default);
		}
		
		PBGeneralUtils.showAlertDialogActionWithOnClick(
				PBHoneyShopActivity.this, 
				android.R.drawable.ic_dialog_alert, 
				getString(R.string.pb_honey_shop_error_title), 
				errorMsg, 
				getString(R.string.dialog_ok_btn), 
				new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!PBHoneyShopActivity.this.isFinishing()) {
							PBHoneyShopActivity.this.finish();
						}
					}
				}, false);
	}
	
	private void sendPurchaseRequest(String id) {
		
		if (STATUS_IDLE != mStatus) {
			return;
		}
		Log.d("PURCHASE", id);
		
		updateUI(STATUS_BUSY);
		
		//Response comes back @onActivityResult, then the listener get callback
		mHelper.launchPurchaseFlow(PBHoneyShopActivity.this, 
				id, 
				createRandomInt(),
				mPurchaseFinishedListener);
	}
	
	private int createRandomInt() {
		try {
			Random rdm = new Random(System.currentTimeMillis());
            return Math.abs(rdm.nextInt()) % 1000000;
		}
		catch (Exception e) {}
		return 9527;
	}
	
	// this listener is called if there are unfinished purchase goods
	private PBIabHelper.QueryInventoryFinishedListener mGotInventoryListener = new PBIabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(PBIabResult result, PBInventory inventory) {
			if (result.isFailure()) {
				mHandler.sendEmptyMessage(MSG_PURCHASE_ERROR);
				return;
			}
	            
			mPurchase = inventory.getAvailadPurchase();
			if (mPurchase != null) {
				updateUI(STATUS_BUSY);
				postPurchaseToServer();
				return;
			}

	        updateUI(STATUS_IDLE);
		}
	};
	
	// this listener is called when purchase in google play over
	private PBIabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new PBIabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(PBIabResult result, PBPurchase purchase) {
			if (result.isFailure()) {
				if (PBIabHelper.IABHELPER_USER_CANCELLED == result.getResponse()) {
					updateUI(STATUS_IDLE);
				} else {
					mHandler.sendEmptyMessage(MSG_PURCHASE_ERROR);
				}
				return;
			}           

			if (purchase != null
				&& purchase.isAvailable()) {				
				mPurchase = purchase;
				
				postPurchaseToServer();
			}
			else {
				mHandler.sendEmptyMessage(MSG_PURCHASE_ERROR);
			}
		}
	};
	
	private PBIabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new PBIabHelper.OnConsumeFinishedListener() {
	    public void onConsumeFinished(PBPurchase purchase, PBIabResult result) {
	    	
			mHandler.sendEmptyMessage(result.isSuccess() && result != null
					? MSG_CONFIRM_SUC
					: MSG_PURCHASE_ERROR);
	    }
	};
	
	private void postPurchaseToServer() {
		new Thread() {
			@Override
			public void run() {
				String token = PBPreferenceUtils.getStringPref(
						PBHoneyShopActivity.this, 
                        PBConstant.PREF_NAME,
                        PBConstant.PREF_NAME_TOKEN, "");
				if (TextUtils.isEmpty(token)) {
					return;
				}
				
				try {
					final PBPurchase purchase = new PBPurchase(
							mPurchase.getOriginalJson(), 
							mPurchase.getSignature());
					
					Response response = PBAPIHelper.postPurchaseDataToServer(
							token, 
							purchase.getOriginalJson(), 
							purchase.getSignature());
					if (response != null
						&& response.errorCode == ResponseHandle.CODE_200_OK) {
						
						if (responseCorrect(response.decription)) {
							
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									mHelper.consumeAsync(mPurchase, mConsumeFinishedListener);
								}	
							});
							return;
						}
					}
					if (response != null
						&& response.errorCode == ResponseHandle.CODE_400
						&& response.decription.contains("duplicated")) {
						
						mIsMultyRequest = true;
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mHelper.consumeAsync(mPurchase, mConsumeFinishedListener);
							}	
						});
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				mHandler.sendEmptyMessage(MSG_PURCHASE_ERROR);
			}
		}.start();
	}
	
	private boolean responseCorrect(String response) {
		try {
			Log.d("PURCHASE", response);
			JSONObject result = new JSONObject(response);
			
			int maple = result.getInt(PBConstant.PREF_MAPLE_COUNT);
			if (maple > 0) {
				PBPreferenceUtils.saveIntPref(
						PBHoneyShopActivity.this, 
						PBConstant.PREF_NAME, 
						PBConstant.PREF_MAPLE_COUNT, 
						maple);
				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_show_purchase_info:
			changeIntroStatus();
			break;
		case R.id.text_show_purchase_info:
			changeIntroStatus();
			break;
		default:
			break;
		}
	}
	
	private void changeIntroStatus() {
		mIntroStatus = !mIntroStatus;
		
		if (mIntroStatus) {
			mLayoutIntro.setVisibility(View.VISIBLE);
			mTextHoneyCount.setVisibility(View.GONE);
			mBtnPurchaseIntro.setVisibility(View.VISIBLE);
			mBtnPurchaseIntro.setText(R.string.pb_honey_shop_btn_back);
			setHeader(mHeaderBar, getString(R.string.pb_honey_shop_intro_title));
		}
		else {
			mLayoutIntro.setVisibility(View.GONE);
			mTextHoneyCount.setVisibility(View.VISIBLE);
			mBtnPurchaseIntro.setVisibility(View.GONE);
			setHeader(mHeaderBar, getString(R.string.pb_honey_shop_title));
		}
	}

	@Override
	protected void handleHomeActionListener() {
		if (mIntroStatus) {
			changeIntroStatus();
		} 
		else { 
			finish();
		}
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		
	}
}