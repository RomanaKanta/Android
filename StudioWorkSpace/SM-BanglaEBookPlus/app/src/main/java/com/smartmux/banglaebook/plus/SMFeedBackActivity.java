package com.smartmux.banglaebook.plus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.smartmux.banglaebook.plus.R;
import com.smartmux.banglaebook.plus.util.Constant;
import com.smartmux.banglaebook.plus.util.ProgressHUD;

public class SMFeedBackActivity extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.feedback_layout);

		this.findViewById(R.id.btnCancel).setOnClickListener(this);

	}

	@Override
	public void onResume() {
		super.onResume();

		loadUrl();

	}

	private void loadUrl() {

		final ProgressHUD mProgressHUD = ProgressHUD.show(
				SMFeedBackActivity.this, "", true);

		WebView webviewFeedBack = (WebView) findViewById(R.id.webView_feedback);
		WebSettings webSettings = webviewFeedBack.getSettings();
		webSettings.setJavaScriptEnabled(true);

		webviewFeedBack.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// Handle the error
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {

				if (mProgressHUD.isShowing()) {

					mProgressHUD.dismiss();
				}
			}

		});

		webviewFeedBack.loadUrl(Constant.FEEDBACK_URL);

	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btnCancel) {

			finish();
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
		}

	}

	@Override
	public void onBackPressed() {
		
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//		super.onBackPressed();
	}

}
