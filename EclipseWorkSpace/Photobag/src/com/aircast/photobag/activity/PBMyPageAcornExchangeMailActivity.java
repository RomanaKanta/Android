package com.aircast.photobag.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircast.photobag.R;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.actionbar.ActionBar;

public class PBMyPageAcornExchangeMailActivity extends PBAbsActionBarActivity 
	implements OnClickListener {
	
	private int mGoodsType = -1;
	
	private final int GOODS_TYPE_ITUNE = 1;
	private final int GOODS_TYPE_AMAZON = 2;
	
	private TextView mTextGoodsTitle;
	private ImageView mImageGoodsIcon;
	private TextView mTextGoodsPrice;
	private TextView mTextAcornCount;
	private TextView mTextGoodsIntro;
	private TextView mTextConfirm;
	
	private LinearLayout mLayoutGoodsIntro;
	private LinearLayout mLayoutConfirmInfo;
	private LinearLayout mLayoutGoodsHeader;
	private LinearLayout mLayoutAfter;
	
	private int mAcornCount;
	private int mAcornRate;
	
	private final int MAIL_SEND_CODE = 9527;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pb_layout_my_page_exchange_mail);
		
		ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(headerBar, getString(R.string.pb_exchange_acorn_title));
        
        try {
        	String tmp = getIntent().getStringExtra("type");
        	if ("itune".equals(tmp)) {
        		mGoodsType = GOODS_TYPE_ITUNE;
        	} 
        	if ("amazon".equals(tmp)) {
        		mGoodsType = GOODS_TYPE_AMAZON;
        	}
        } catch (Exception e) {
        	mGoodsType = -1;
        }
        if (mGoodsType == -1) {
        	finish();
        }
        
        mTextGoodsTitle = (TextView) findViewById(R.id.tv_acorn_goods_title);
        mTextGoodsPrice = (TextView) findViewById(R.id.tv_acorn_goods_price);
        mTextAcornCount = (TextView) findViewById(R.id.tv_acorn_total_num);
        mTextGoodsIntro = (TextView) findViewById(R.id.tv_acorn_goods_details);
        mLayoutGoodsHeader = (LinearLayout) findViewById(R.id.layout_acorn_goods_header);
        mTextConfirm = (TextView) findViewById(R.id.tv_acorn_confirm_info);
        
        mImageGoodsIcon = (ImageView) findViewById(R.id.icon_acorn_mail_goods);
        
        mLayoutGoodsIntro = (LinearLayout) findViewById(R.id.layout_acorn_exchange_intro);
        mLayoutConfirmInfo = (LinearLayout) findViewById(R.id.layout_acorn_exchange_confirm);
        mLayoutAfter = (LinearLayout) findViewById(R.id.layout_acorn_exchange_after);
        
        findViewById(R.id.btn_acorn_start_exchange).setOnClickListener(this);
        findViewById(R.id.btn_acorn_confirm_exchange).setOnClickListener(this);
        findViewById(R.id.btn_acorn_exit).setOnClickListener(this);
        
        showGoodsInfo(mGoodsType);
	}
	
	private void showGoodsInfo(int type) {
		mAcornCount = PBPreferenceUtils.getIntPref(
				getApplicationContext(), PBConstant.PREF_NAME,
				PBConstant.PREF_GOLD_COUNT, 0);
		mTextAcornCount.setText(mAcornCount + "");
		
		String name = GOODS_TYPE_ITUNE == mGoodsType
				? PBConstant.PREF_DONGURI_ITUNE_EXCHANGE_RATE
				: PBConstant.PREF_DONGURI_AMAZON_EXCHANGE_RATE;
		mAcornRate = PBPreferenceUtils.getIntPref(
				getApplicationContext(), 
				PBConstant.PREF_NAME, name, 0);
		
				
		if (GOODS_TYPE_ITUNE == mGoodsType) {
			mTextGoodsTitle.setText(R.string.pb_exchange_acorn_goods_itune);
			mTextGoodsPrice.setText(mAcornRate + "");
			mTextGoodsIntro.setText(R.string.pb_exchange_acorn_detail_itune);
			mTextConfirm.setText(String.format(getString(R.string.pb_exchange_acorn_confirm_itune), mAcornRate));
			
			mImageGoodsIcon.setImageResource(R.drawable.acorn_list_itune);
		}
		if (GOODS_TYPE_AMAZON == mGoodsType) {
			mTextGoodsTitle.setText(R.string.pb_exchange_acorn_goods_amazon);
			mTextGoodsPrice.setText(mAcornRate + "");
			mTextGoodsIntro.setText(R.string.pb_exchange_acorn_detail_amazon);
			mTextConfirm.setText(String.format(getString(R.string.pb_exchange_acorn_confirm_amazon), mAcornRate));
			
			mImageGoodsIcon.setImageResource(R.drawable.acorn_list_amazon);
		}
	}
	
	@Override
	protected void handleHomeActionListener() {
		finish();
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		// however this don't work at all.
		if (MAIL_SEND_CODE == requestCode) {
			mLayoutAfter.setVisibility(View.VISIBLE);
			mLayoutConfirmInfo.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onBackPressed() {
		if (mLayoutConfirmInfo.getVisibility() == View.VISIBLE) {
			mLayoutGoodsIntro.setVisibility(View.VISIBLE);
			mLayoutConfirmInfo.setVisibility(View.GONE);
			mLayoutGoodsHeader.setVisibility(View.VISIBLE);
		}
		else {
			super.onBackPressed();
		}
	}
	
	private boolean isAcornEnough() {
		return (mAcornCount >= mAcornRate) ? true : false;
	}
	
	private void sendExchangeEmail() {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
    	sendIntent.putExtra(Intent.EXTRA_EMAIL, 
    			new String[] { getString(R.string.pb_setting_contact_mail) });
    	sendIntent.setType(PBConstant.INTENT_TYPE_SENDMAIL);
    	
    	String sendMessage = String.format(getString(
                R.string.pb_setting_contactweb_send_message,
                PBPreferenceUtils.getStringPref(
                		PBApplication.getBaseApplicationContext(), 
                		PBConstant.PREF_NAME,
                        PBConstant.PREF_NAME_UID, 
                        getString(R.string.pb_setting_contact_code))));
        sendMessage += "\nMobile Type: " + Build.MODEL; 
        sendMessage += "\nOs: Android v" + Build.VERSION.RELEASE;
        sendMessage += "\nAPI lv:" + Build.VERSION.SDK_INT;
		sendMessage += "\nApp version:" + PBApplication.getAppVersion();
    	sendIntent.putExtra(Intent.EXTRA_TEXT, sendMessage);
    	
		if (GOODS_TYPE_ITUNE == mGoodsType) {
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.pb_exchange_acorn_email_itune));
		}
		if (GOODS_TYPE_AMAZON == mGoodsType) {
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.pb_exchange_acorn_email_amazon));
		}
		
		startActivityForResult(sendIntent, MAIL_SEND_CODE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_acorn_confirm_exchange:
			sendExchangeEmail();
			break;
		case R.id.btn_acorn_start_exchange:
			if (isAcornEnough()) {
				mLayoutGoodsIntro.setVisibility(View.GONE);
				mLayoutConfirmInfo.setVisibility(View.VISIBLE);
				mLayoutGoodsHeader.setVisibility(View.GONE);
			}
			else {
				PBGeneralUtils.showAlertDialogActionWithOkButton(
						PBMyPageAcornExchangeMailActivity.this, 
						getString(R.string.pb_exchange_acorn_ok_title), 
						getString(R.string.pb_exchange_acorn_ok_content), 
						getString(R.string.pb_exchange_acorn_ok_button));
			}
			break;
		case R.id.btn_acorn_exit:
			finish();
		default:
			break;
		}
	}
}