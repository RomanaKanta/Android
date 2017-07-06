package com.aircast.photobag.activity;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;

public class PBDownloadConfirmActivity extends PBAbsActionBarActivity implements OnClickListener {

	private ImageView mCheckBox;	 
	private FButton mButtonConfirm;
	private boolean mIsConfirmed;
	private boolean mIsDeletePass;
	
	private final Handler mHandler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pb_layout_download_confirm);
		
		ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(headerBar, getString(R.string.pb_download_confirm_title));
        
        TextView confirmText = (TextView) findViewById(R.id.text_download_confirm);
		TextView dateView = (TextView) findViewById(R.id.layout_download_confirm_days);
		mCheckBox = (ImageView) findViewById(R.id.icon_checkbox_confirm_download);
		mButtonConfirm = (FButton) findViewById(R.id.btn_confirm_download);
		mButtonConfirm.setOnClickListener(this);
		findViewById(R.id.layout_download_confirm).setOnClickListener(this);
		
		mIsConfirmed = false;
		String downloadInfo = PBPreferenceUtils.getStringPref(
				this,
				PBConstant.PREF_NAME,
				PBConstant.PREF_PURCHASE_INFO_JSON_DATA, null);
		if (TextUtils.isEmpty(downloadInfo)) {
			finish();
		}
		try {
			JSONObject jInfo = new JSONObject(downloadInfo);
			int days = jInfo.getInt("client_keep_days");
			
			if (days > 0) {
				dateView.setText(String.format(getString(R.string.pb_download_confirm_text_3), days));
				confirmText.setText(R.string.pb_download_confirm_text_22);
				mIsDeletePass = true;
			} else {
				dateView.setVisibility(View.GONE);
				confirmText.setText(R.string.pb_download_confirm_text_21);
				mIsDeletePass = false;
			}
		} catch (Exception e) {
			dateView.setVisibility(View.GONE);
		}
	}
	
	private boolean needPurchase() {
		try {
			if (getIntent().getBooleanExtra("NEED_PURCHASE", false)) {
				return true;
			}
		} catch (Exception e) {}
		
		return false;
	}
	
	private void startDownloadWithConfirm() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// confirm password

				String tokenKey = PBPreferenceUtils.getStringPref(
						getApplicationContext(), PBConstant.PREF_NAME,
						PBConstant.PREF_NAME_TOKEN, "");
				String passWord = PBPreferenceUtils
						.getStringPref(getApplicationContext(),
								PBConstant.PREF_NAME,
								PBConstant.INTENT_PASSWORD_HONEY_PHOTO, "");

				Response respone = PBAPIHelper.checkPhotoListInCollection(
						passWord, tokenKey, false, 
						mIsDeletePass ? "DELETE" : "SAVE",false,"");
				
				if (respone.errorCode == ResponseHandle.CODE_200_OK) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							Intent intent = new Intent(PBMainTabBarActivity.sMainContext, 
	                                PBDownloadDownloadingActivity.class);                        
	                        startActivity(intent);
	                        finish();
						}
					});
				}
			}
		});
		thread.start();
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
		case R.id.layout_download_confirm:
			mIsConfirmed = !mIsConfirmed;
			mCheckBox.setImageResource(mIsConfirmed
					? R.drawable.checkbox_red_on
					: R.drawable.checkbox_red_off);
			
			mButtonConfirm.setBackgroundColor(
					mIsConfirmed?
					   (getResources().getColor(R.color.fbutton_color_shape_orange)):
					   (getResources().getColor(R.color.fbutton_color_hard)));
			
			/*mButtonConfirm.setBackgroundResource(mIsConfirmed
					? R.drawable.btn_shape_orange
					: R.drawable.btn_hard);*/
			
			break;
		case R.id.btn_confirm_download:
			if (mIsConfirmed) {
				if (needPurchase()) {
					Intent intent = new Intent(PBMainTabBarActivity.sMainContext, 
                            PBDownloadPurchaseActivity.class);
					intent.putExtra("NEED_CONFIRM", true);
                    startActivity(intent);
                    finish();
				}
				else {
					startDownloadWithConfirm();
				}
			}
			break;
		}
	}
	
}