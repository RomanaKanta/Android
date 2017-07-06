package com.aircast.photobag.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * Webview activity. 
 * <p>Show "what is acorn" web page</p>
 * */
public class PBWebViewActivity extends PBAbsActionBarActivity{
    
	private ActionBar mHeaderBar;
    private LinearLayout mLvWaiting;
    
    private String mUrl;
    private String mTitle;
    
    @SuppressLint("SetJavaScriptEnabled")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pb_layout_webview);
        
        if (!PBApplication.hasNetworkConnection()) {
	        PBGeneralUtils.showAlertDialogActionWithOnClick(
	        		PBWebViewActivity.this, 
					android.R.drawable.ic_dialog_alert, 
					getString(R.string.dialog_error_network_title), 
					getString(R.string.pb_exchange_acorn_empty), 
					getString(R.string.dialog_ok_btn), 
					new DialogInterface.OnClickListener() {							
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PBWebViewActivity.this.finish();
						}
					}, false);
        }

        getUrlAndTitle();

        WebView webView = (WebView) findViewById(R.id.pb_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setPluginState(PluginState.ON);
        webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);//check again

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                // Log.w(PBConstant.TAG, "[progress]" + progress);
                if(mLvWaiting != null && progress == 100){
                    mLvWaiting.setVisibility(View.GONE);
                }
                PBWebViewActivity.this.setProgress(progress * 100);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
                // Handle the error
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
            	if(overrideUrlListener!=null && overrideUrlListener.shouldOverrideUrlLoading(view, url)) {
            		return true;
            	}
                view.loadUrl(url);
                return true;
            }
        });
        // Log.w(PBConstant.TAG, url);
        webView.loadUrl(mUrl);
        mLvWaiting = (LinearLayout) findViewById(R.id.ll_loading_panel_waiting);
        mLvWaiting.setVisibility(View.VISIBLE);

    }
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		
// 		try {
// 			AdstirTerminate.init(this);
// 		} catch (Exception e) {}
	}
    
    private void getUrlAndTitle() {        
        mTitle = null;
        mUrl = null;
        
        Intent intent = getIntent();
        if(intent != null) {
        	// default if contains intent
        	mUrl = intent.getStringExtra(PBAPIContant.PB_SETTING_EXTRA_URL);
            mTitle = intent.getStringExtra(PBAPIContant.PB_SETTING_EXTRA_TITLE);
        }
        
        mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(mHeaderBar, mTitle);
    }   

    @Override
    protected void handleHomeActionListener() {
        finish();
    }

    @Override
    protected void setLeftBtnsDetailHeader(ActionBar headerBar) {}
    
    public OverrideUrlListener getOverrideUrlListener() {
		return overrideUrlListener;
	}

	public void setOverrideUrlListener(OverrideUrlListener overrideUrlListener) {
		this.overrideUrlListener = overrideUrlListener;
	}

	public interface OverrideUrlListener{
    	public boolean shouldOverrideUrlLoading(WebView view, String url);
    }
    
    private OverrideUrlListener overrideUrlListener = new OverrideUrlListener() {
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// on acorn intro page exchange clicked
			if (url.contains("photobag://photobag.in/?scene=exchangehoneypage")) { //TODO
				Intent intent = new Intent(getApplicationContext(), 
						PBMyPageHoneyExchangeActivity.class);
				startActivity(intent);
				finish();
				return true;
			}
			
			if (url.contains("acornforest") && url.startsWith("photobag://")) {
				Intent intentReward = new Intent(getApplicationContext(),
    					PBAcornForestActivity.class);
    			startActivity(intentReward);
				finish();
				return true;
			}
			
			if (url.contains("honeyshop") && url.startsWith("photobagin://")) {
				return true;
			}
			
			if (url.contains("exchange") && url.startsWith("photobagin://")) {
				Intent intentExchange = new Intent(getApplicationContext(),
						PBMyPageAcornExchangeListActivity.class);
    			startActivity(intentExchange);
				finish();
				return true;
			}
			
			return false;
		}
	};
}
