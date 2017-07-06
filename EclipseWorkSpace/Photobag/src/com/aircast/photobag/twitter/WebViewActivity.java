package com.aircast.photobag.twitter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.aircast.photobag.R;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;

public class WebViewActivity extends Activity {
	
	private WebView webView;
	
	public static String EXTRA_URL = "extra_url";
	private static SharedPreferences mSharedPreferences;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_webview_twitter);
		//setContentView(R.layout.activity_webview);
		/* Initialize application preferences */
		mSharedPreferences = getSharedPreferences(PBConstant.PREF_NAME, 0);
		System.out.println("Atik Enter onCreate of WebViewActivity");
		/* Storing oAuth tokens to shared preferences */
		Editor e = mSharedPreferences.edit();
		e.putBoolean(PBConstant.PREF_TWITTER_LOGIN_BACK_ONLY, false);
		e.commit();
		/* Initialize application preferences */
		mSharedPreferences = getSharedPreferences(PBConstant.PREF_NAME, 0);
		
		setTitle("Login");

		final String url = this.getIntent().getStringExtra(EXTRA_URL);
		if (null == url) {
			Log.e("Twitter", "URL cannot be null");
			finish();
		}

		webView = (WebView) findViewById(R.id.webView);
		webView.setWebViewClient(new MyWebViewClient());
		webView.loadUrl(url);
	}


	class MyWebViewClient extends WebViewClient {
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			System.out.println("Atik Enter shouldOverrideUrlLoading of WebViewActivity");

			if (url.contains(getResources().getString(R.string.twitter_callback))) {
				Uri uri = Uri.parse(url);
				
				/* Sending results back */
				String verifier = uri.getQueryParameter(getString(R.string.twitter_oauth_verifier));
				Intent resultIntent = new Intent();
				resultIntent.putExtra(getString(R.string.twitter_oauth_verifier), verifier);
				setResult(RESULT_OK, resultIntent);
				

				
				/* closing webview */
				finish();
				return true;
			}
			return false;
		}
	}
	
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
		//        Storing oAuth tokens to shared preferences /
		      
		Intent resultIntent = new Intent();
		resultIntent.putExtra("back", 0);
		setResult(RESULT_OK, resultIntent);
		      
		      System.out.println("Atik back is pressed");
		//Editor e = mSharedPreferences.edit();
		//e.putBoolean();
		//e.commit();
		PBPreferenceUtils.saveBoolPref(getApplicationContext(), PBConstant.PREF_NAME,
				PBConstant.PREF_TWITTER_LOGIN_BACK_ONLY, true);
		finish();
    }
	

}
