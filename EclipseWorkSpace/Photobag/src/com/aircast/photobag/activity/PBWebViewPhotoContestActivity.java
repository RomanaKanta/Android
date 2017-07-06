package com.aircast.photobag.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.fragment.PBDownloadFragment;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * Webview activity. 
 * <p>Show "Photo contest" web page</p>
 * */
public class PBWebViewPhotoContestActivity extends PBAbsActionBarActivity{
    
	private ActionBar mHeaderBar;
    private LinearLayout mLvWaiting;
    
    private String mUrl;
    private String mTitle;
    
    @SuppressLint("SetJavaScriptEnabled")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pb_layout_webview_photo_contest);
        
        /*if (!PBApplication.hasNetworkConnection()) {
	        PBGeneralUtils.showAlertDialogActionWithOnClick(
	        		PBWebViewPhotoContestActivity.this, 
					android.R.drawable.ic_dialog_alert, 
					getString(R.string.dialog_error_network_title), 
					getString(R.string.pb_exchange_acorn_empty), 
					getString(R.string.dialog_ok_btn), 
					new DialogInterface.OnClickListener() {							
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PBWebViewPhotoContestActivity.this.finish();
						}
					}, false);
        }*/

        
        
        getUrlAndTitle();

        WebView webView = (WebView) findViewById(R.id.pb_webview_photo_contest);
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
                PBWebViewPhotoContestActivity.this.setProgress(progress * 100);
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
            	/*if(overrideUrlListener!=null && overrideUrlListener.shouldOverrideUrlLoading(view, url)) {
            		return true;
            	}*/
            	
            	System.out.println("Atik URL of clicked button:"+url);
            	
        		/*Intent intent = new Intent(PBTabBarActivity.sMainContext,
        				PBUploadMainActivity.class);
        		intent.putExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY,
        				true); // Added parameter from going to koukaibukuro library
*/        		//activity.startActivity(intent);
            	if(url.contains("photobagout")) {
            		

            		
                	Intent intent = new Intent(PBWebViewPhotoContestActivity.this,SelectMultipleImageActivity.class);
                	intent.putExtra(PBConstant.PREF_FOR_PHOTO_CONTEST,
            				true); // Added parameter from going to koukaibukuro library
                	startActivity(intent);
                	return true;
            	} else if(url.contains("twitter.com/photobag_in"))  {
            		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            		startActivity(browserIntent);
                	return true;
            	} else if(url.contains("mailto:info@photobag.in")) {
            		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            		startActivity(browserIntent);
                	return true;
            	}else if(url.contains("photobagin://download?password")) {
            		
            		
            		Uri uri = Uri.parse(url);
        			if (uri.getAuthority().contains("download")) {
        				String pwd = uri.getQueryParameter("password");
        				PBDownloadFragment.passwordFromUrl = pwd;
        				PBMainTabBarActivity.sMainContext.mTabHost
    					.setCurrentTabByTag(PBConstant.DOWNLOAD_TAG);
        			}
            		finish();
                	return true;
            	}

            	//finish();
                view.loadUrl(url);
                return true;
            }
        });
        // Log.w(PBConstant.TAG, url);
        
      //  photobagin://download?password=%E6%B5%B7%EF%BC%81%E6%B0%B4%E7%9D%80JK%40%E5%A4%8F%E5%A5%B3%E5%AD%90

		String uuid = PBPreferenceUtils.getStringPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, "0");
        
 
		String modifiedURL= mUrl+"?uid="+uuid+"&platform=android"+"&app_version="+PBApplication.TARGET_VERSION
        		+"&LANG="+PBApplication.LANG+"&os_version="+PBApplication.OS_VERSION;
		
        //System.out.println("Atik url is:"+modifiedURL);
        webView.loadUrl(modifiedURL);
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
    
    /*public OverrideUrlListener getOverrideUrlListener() {
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
	};*/
}
