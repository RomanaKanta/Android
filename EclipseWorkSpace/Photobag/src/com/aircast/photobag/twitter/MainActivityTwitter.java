package com.aircast.photobag.twitter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBAbsActionBarActivity;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.actionbar.ActionBar;

public class MainActivityTwitter extends PBAbsActionBarActivity implements OnClickListener {
	
	/* Shared preference keys */
	/*private static final String PREF_NAME = "sample_twitter_pref";
	private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
	private static final String PREF_USER_NAME = "twitter_user_name";*/

	/* Any number for uniquely distinguish your request */
	public static final int WEBVIEW_REQUEST_CODE = 100;

	private ProgressDialog pDialog;

	private static Twitter twitter;
	private static RequestToken requestToken;
	
	private static SharedPreferences mSharedPreferences;

	private EditText mShareEditText;
	private TextView userName;
	private View loginLayout;
	private View shareLayout;

	/*private String consumerKey = "oyAZiiG6Nfuq8RYR6fUK1WOR4";
	private String consumerSecret = "UKqaQtIbZE8bd0JMuxKuphy3I5Z2a4UQqprSxVXobDz8NTP2Yq";
	private String callbackUrl = "oauth://t4jsample";*/
	private String consumerKey = "jKkqSmfHcBjuazHP0f2w";
	private String consumerSecret = "5QyHAG0UUViuOEnjmgaqJkdRLo7xpBgLcClrZmmyjo";
	//private String callbackUrl = "myTweet://OAuthTwitter";
	private String callbackUrl = "oauth://t4jsample";
	
	private String oAuthVerifier = "oauth_verifier";
	private String twitMessage = "";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* initializing twitter parameters from string.xml */
		initTwitterConfigs();
		
		//Bundle b = getIntent().getExtras();
		
		Bundle extras = getIntent().getExtras();
		
		//mSlug = savedInstanceState.getString(KEY_SLUG, null);
		
	    if(extras != null) {
	    	  twitMessage = extras.getString("TWIT_MESSAGE");
	    }

		/* Enabling strict mode */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		/* Setting activity layout file */
		setContentView(R.layout.activity_main_twitter);
		//setContentView(R.layout.activity_main_twitter);
		
        ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(headerBar, getString(R.string.pb_twitter_title));

		loginLayout = (RelativeLayout) findViewById(R.id.login_layout);
		shareLayout = (LinearLayout) findViewById(R.id.share_layout);
		mShareEditText = (EditText) findViewById(R.id.share_text);
		mShareEditText.setText(twitMessage);
		
		//userName = (TextView) findViewById(R.id.user_name);
		
		/* register button click listeners */
		findViewById(R.id.btn_login).setOnClickListener(this);
		findViewById(R.id.btn_share).setOnClickListener(this);

		/* Check if required twitter keys are set */
		if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret)) {
			Toast.makeText(this, "Twitter key and secret not configured",
					Toast.LENGTH_SHORT).show();
			return;
		}

		/* Initialize application preferences */
		mSharedPreferences = getSharedPreferences(PBConstant.PREF_NAME, 0);

		boolean isLoggedIn = mSharedPreferences.getBoolean(PBConstant.PREF_KEY_TWITTER_LOGIN, false);
		
		/*  if already logged in, then hide login layout and show share layout */
		if (isLoggedIn) {
			
			System.out.println("Atik logged in");
			loginLayout.setVisibility(View.GONE);
			shareLayout.setVisibility(View.VISIBLE);

			//String username = mSharedPreferences.getString(PREF_USER_NAME, "");
			/*userName.setText(getResources ().getString(R.string.hello)
					+ username);*/

		} else {
			loginLayout.setVisibility(View.VISIBLE);
			shareLayout.setVisibility(View.GONE);
			System.out.println("Atik not logged in");
			loginToTwitter();
			/*Uri uri = getIntent().getData();
			
			if (uri != null && uri.toString().startsWith(callbackUrl)) {
			
				String verifier = uri.getQueryParameter(oAuthVerifier);

				try {
					
					 Getting oAuth authentication token 
					AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

					 Getting user id form access token 
					long userID = accessToken.getUserId();
					final User user = twitter.showUser(userID);
					final String username = user.getName();

					 save updated token 
					saveTwitterInfo(accessToken);

					loginLayout.setVisibility(View.GONE);
					shareLayout.setVisibility(View.VISIBLE);
					userName.setText(getString(R.string.hello) + username);
					
				} catch (Exception e) {
					Log.e("Failed to login Twitter!!", e.getMessage());
				}
			}*/

		}
	}

	
	/**
	 * Saving user information, after user is authenticated for the first time.
	 * You don't need to show user to login, until user has a valid access token
	 */
	private void saveTwitterInfo(AccessToken accessToken) {
		
		long userID = accessToken.getUserId();
		
		User user;
		try {
			user = twitter.showUser(userID);
		
			//String username = user.getName();


			PBPreferenceUtils.saveStringPref(getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
			PBPreferenceUtils.saveStringPref(getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
			PBPreferenceUtils.saveBoolPref(getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_KEY_TWITTER_LOGIN, true);
			


		} catch (TwitterException e1) {
			e1.printStackTrace();
		}
	}

	/* Reading twitter essential configuration parameters from strings.xml */
	private void initTwitterConfigs() {
		consumerKey = getString(R.string.twitter_consumer_key);
		consumerSecret = getString(R.string.twitter_consumer_secret);
		callbackUrl = getString(R.string.twitter_callback);
		oAuthVerifier = getString(R.string.twitter_oauth_verifier);
	}

	
	private void loginToTwitter() {
		boolean isLoggedIn = mSharedPreferences.getBoolean(PBConstant.PREF_KEY_TWITTER_LOGIN, false);
		
		if (!isLoggedIn) {
			//final ConfigurationBuilder builder = new ConfigurationBuilder();
			//ConfigurationBuilder builder = new ConfigurationBuilder();
			ConfigurationBuilder  builder = new ConfigurationBuilder();
			//final ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(consumerKey);
			builder.setOAuthConsumerSecret(consumerSecret);

			final Configuration configuration = builder.build();
			final TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			try {
				requestToken = twitter.getOAuthRequestToken(callbackUrl);

				/**
				 *  Loading twitter login page on webview for authorization 
				 *  Once authorized, results are received at onActivityResult
				 *  */
				final Intent intent = new Intent(this, WebViewActivity.class);
				intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
				startActivityForResult(intent, WEBVIEW_REQUEST_CODE);
				
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {

			loginLayout.setVisibility(View.GONE);
			shareLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(data.hasExtra("back")){
			 PBPreferenceUtils.saveBoolPref(getApplicationContext(), PBConstant.PREF_NAME,
					PBConstant.PREF_TWITTER_LOGIN_BACK_ONLY, false);
			   /*Toast.makeText(getApplicationContext(), "come from back "+resultCode, 500).show();*/
			   finish();
			   
        }
		
		if (resultCode == Activity.RESULT_OK) {
			String verifier = data.getExtras().getString(oAuthVerifier);
			try {
				AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

				long userID = accessToken.getUserId();
				final User user = twitter.showUser(userID);
				//String username = user.getName();
				
				saveTwitterInfo(accessToken);

				loginLayout.setVisibility(View.GONE);
				shareLayout.setVisibility(View.VISIBLE);
				//userName.setText(MainActivityTwitter.this.getResources().getString(
				//		R.string.hello) + username);

			} catch (Exception e) {
				//Log.e("Twitter Login Failed", e.getMessage());
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			loginToTwitter();
			break;
		case R.id.btn_share:
			final String status = mShareEditText.getText().toString();
			
			Calendar c = Calendar.getInstance();
	        System.out.println("Current time => "+c.getTime());

	        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String formattedDate = df.format(c.getTime());
	        
	        String statusWithTime = status+"\n"+ formattedDate;
			
			System.out.println("Atik Twitter message is:"+status);
			
			if (status.trim().length() > 0) {
				//new updateTwitterStatus().execute(status);
				new updateTwitterStatus().execute(statusWithTime);
			} else {
				Toast.makeText(this, "Message is empty!!", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	class updateTwitterStatus extends AsyncTask<String, String, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pDialog = new ProgressDialog(MainActivityTwitter.this);
			pDialog.setMessage(getString(R.string.pb_twitter_progress_do_twitting)/*"Posting to twitter..."*/);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected Void doInBackground(String... args) {

			String status = args[0];
			try {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(consumerKey);
				builder.setOAuthConsumerSecret(consumerSecret);
				
				// Access Token
				String access_token = mSharedPreferences.getString(PBConstant.PREF_KEY_OAUTH_TOKEN, "");
				// Access Token Secret
				String access_token_secret = mSharedPreferences.getString(PBConstant.PREF_KEY_OAUTH_SECRET, "");

				AccessToken accessToken = new AccessToken(access_token, access_token_secret);
				Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

				// Update status
				StatusUpdate statusUpdate = new StatusUpdate(status);
				//InputStream is = getResources().openRawResource(R.drawable.lakeside_view);
				//statusUpdate.setMedia("test.jpg", is);
				
				twitter4j.Status response = twitter.updateStatus(statusUpdate);

				Log.d("Status", response.getText());
				
			} catch (TwitterException e) {
				Log.d("Failed to post!", e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			/* Dismiss the progress dialog after sharing */
			pDialog.dismiss();
			
			Toast.makeText(MainActivityTwitter.this, getString(R.string.pb_twitter_toast_twit_done)/*"Posted to Twitter!"*/, 
					Toast.LENGTH_SHORT).show();

			// Clearing EditText field
			mShareEditText.setText("");
			finish();
		}

	}

	@Override
	protected void handleHomeActionListener() {
		finish();			
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		return;	
	}
	@Override
	public void onResume() {
		super.onResume();
		/*boolean isBackFromLoginScreen = mSharedPreferences.getBoolean(PBConstant.PREF_TWITTER_LOGIN_BACK_ONLY, false);
		if(isBackFromLoginScreen) {
			 //Storing oAuth tokens to shared preferences 
			Editor e = mSharedPreferences.edit();
			e.putBoolean(PBConstant.PREF_TWITTER_LOGIN_BACK_ONLY, false);
			//e.putString(PREF_USER_NAME, username);
			e.commit();
			//finish(); //when just back from login screen 
		} else {
			finish();
		}

		System.out.println("Atik come from login screen");*/
	}
}