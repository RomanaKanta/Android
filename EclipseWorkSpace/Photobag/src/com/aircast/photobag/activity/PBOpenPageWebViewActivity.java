package com.aircast.photobag.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * Webview activity. 
 * <p>Show Opensite web page for public password</p>
 * */
public class PBOpenPageWebViewActivity extends PBAbsActionBarActivity{
    
    private LinearLayout mLvWaiting;
    
    private String mUrl;
    
    @SuppressLint("SetJavaScriptEnabled")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pb_layout_open_page_webview);
        
        if (!PBApplication.hasNetworkConnection()) {
	        PBGeneralUtils.showAlertDialogActionWithOnClick(
	        		PBOpenPageWebViewActivity.this, 
					android.R.drawable.ic_dialog_alert, 
					getString(R.string.dialog_error_network_title), 
					getString(R.string.pb_exchange_acorn_empty), 
					getString(R.string.dialog_ok_btn), 
					new DialogInterface.OnClickListener() {							
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PBOpenPageWebViewActivity.this.finish();
						}
					}, false);
        }

        getUrl();

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
                PBOpenPageWebViewActivity.this.setProgress(progress * 100);
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
    	//System.out.println("Atik URL is loading" + mUrl);

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
    
    private void getUrl() {        
        mUrl = null;
        
        Intent intent = getIntent();
        if(intent != null) {
        	// default if contains intent
        	mUrl = intent.getStringExtra(PBAPIContant.PB_SETTING_EXTRA_URL);
        }

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
			System.out.println("Atik URL when clicked on anything: " + url.toString());

			// close the webview activity when press exit button
			if(url.startsWith(PBAPIContant.PB_OPEN_PAGE_SCHEMA) && url.contains(PBAPIContant.PB_OPEN_PAGE_CLOSE_URL_CONTAINS)) {
				/*Intent intentSendPassword=new Intent();
				// put the Aikotoba in Intent
				intentSendPassword.putExtra("PASSWORD","");
				// Set The Result in Intent
				setResult(1,intentSendPassword);*/
				finish();
				return true;
			}
			
			
			// Open the URL in new browser other than close button pressed
			if(url.contains(PBAPIContant.PB_OPEN_PAGE_ITEM_BROWSE_URL_CONTAINS)) {
				System.out.println("Atik URL Inside details.php: " + url.toString());
				Intent intentForOpeningBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(intentForOpeningBrowser);
				finish();
				return true;
			}
			
			
			/*// when tap on aikotoba
			if(url.startsWith(schema)) {
				String urlFromServer = null;
				int length = schema.length();
				String originalString = null;
				try {
					urlFromServer = java.net.URLDecoder.decode(url, "UTF-8");
					originalString = urlFromServer.substring(length, urlFromServer.length());
				} catch (UnsupportedEncodingException e) {
					System.out.println("UnSupported exception in Password parsing");
				}
				System.out.println("URL from server" + originalString);	
				
				if(!TextUtils.isEmpty(originalString)) {
					Intent intentSendPassword=new Intent();
					// put the Aikotoba in Intent
					intentSendPassword.putExtra("PASSWORD",originalString);
					// Set The Result in Intent
					setResult(2,intentSendPassword);
					// finish The activity 
			        finish();
					return true;
					
				}
			}*/
			
			return false;
		}
	};
	
	@Override
    protected void onActivityResult(int request, int result, Intent data) {
        /* handle activity result here */
    }
}
