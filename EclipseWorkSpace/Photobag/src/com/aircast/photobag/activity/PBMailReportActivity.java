package com.aircast.photobag.activity;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * Setting page.
 * <p><b>TODO: </b>Too many deprecated or never used code here. remove them.
 * Notice that deprecated not only exists here but also in the layout file.
 * Some of the deprecated elements only set the visibility to gone but code still remains.</p>
 */
public class PBMailReportActivity extends PBAbsActionBarActivity implements OnClickListener {

    private ActionBar mHomeBar;
    private WebView myWebView;
    
    public static PBMailReportActivity sSettingContext;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.pb_layout_mail_report);
        mHomeBar = (ActionBar)findViewById(R.id.actionBar);
        mHomeBar.setTitle(getString(R.string.back_text));
        setHeader(mHomeBar, getString(R.string.back_text));
        
        myWebView = (WebView) findViewById(R.id.webview);
        
        myWebView.setVisibility(View.VISIBLE);
        myWebView.setInitialScale(30);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            	
            	if(url.startsWith("mailto:")){
            		
            		Intent intentView = new Intent(android.content.Intent.ACTION_SEND);
                    String[] recipients = new String[] {};
                    intentView.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
                    intentView.putExtra(android.content.Intent.EXTRA_SUBJECT,getString(R.string.pb_app_name));
                    String sendMessage = String.format(getString(
                            R.string.pb_setting_contactweb_send_message,
                            PBPreferenceUtils.getStringPref(PBApplication
                                    .getBaseApplicationContext(), PBConstant.PREF_NAME,
                                    PBConstant.PREF_NAME_UID, getString(R.string.pb_setting_contact_code))));
                    
                    sendMessage += "\nMobile Type: " + Build.MODEL; 
                    sendMessage += "\nOs: Android v" + Build.VERSION.RELEASE;
                    sendMessage += "\nAPI lv:" + Build.VERSION.SDK_INT;
                    try {
        				sendMessage += "\nApp version:"
        							+ getPackageManager().getPackageInfo(getPackageName(),0).versionName;
        			} catch (NameNotFoundException e) {
        				e.printStackTrace();
        			}
                    
                    
                    //TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    //String code = tm.getNetworkCountryIso();
                    //String locale = getApplicationContext().getResources().getConfiguration().locale.getCountry();
                    
                    sendMessage += "\nLanguage :" + Locale.getDefault().getDisplayLanguage();
                    //sendMessage += "\nCountry :" + Locale.getDefault().getCountry();

                    String[] emailRecipients = new String[] { getString(R.string.pb_setting_contact_mail)};
                    intentView.putExtra(android.content.Intent.EXTRA_EMAIL,
                            emailRecipients);
                    intentView.putExtra(android.content.Intent.EXTRA_TEXT, sendMessage);
                    intentView.setType("plain/text");
                    startActivity(intentView);
                    //onBackPressed();
            		myWebView.reload();
                    return false;
                }
                return false;
            }
        });
        
        //@HaiNT15: transparent the white color of the web view <S>
        myWebView.setBackgroundColor(0xECE9E5);
        (new Thread(new Runnable(){
        	public void run(){
        		myWebView.loadUrl(PBAPIContant.PB_REPORT_URL);
        	}
        })).run();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {    
        super.onPause();
        myWebView.loadUrl(PBAPIContant.PB_REPORT_URL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
