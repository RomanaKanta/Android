package com.aircast.photobag.twitter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBAbsActionBarActivity;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * class to support for twitter functions
 */
public class TwitterHandler extends PBAbsActionBarActivity{
    // [9:37:27 AM 2012-02-27] Nguyen Tuan Anh: CONSUMER_KEY jKkqSmfHcBjuazHP0f2w
    // CONSUMER_SECRET 5QyHAG0UUViuOEnjmgaqJkdRLo7xpBgLcClrZmmyjo
   /* public final static String CONSUMER_KEY         = "jKkqSmfHcBjuazHP0f2w";
    public final static String CONSUMER_SECRET     = "5QyHAG0UUViuOEnjmgaqJkdRLo7xpBgLcClrZmmyjo";

    public final static String PREF_TWITTER_USERNAME  = "twitter.username";
    public final static String PREF_TWITTER_TOKEN     = "twitter.token";
    public final static String PREF_TWITTER_SCRECRET  = "twitter.screcret";
    public final static String TOKEN_UNDEFINE     = "twitter.underfine";

    public static final String REQUEST_URL = "http://api.twitter.com/oauth/request_token";
    public static final String ACCESS_URL = "http://api.twitter.com/oauth/access_token";
    public static final String AUTHORIZE_URL = "http://api.twitter.com/oauth/authorize?force_login=true";

    public static final String  OAUTH_CALLBACK_URL = "";*/

    private ActionBar mHeaderBar;
    private LinearLayout mLvWaiting;
    protected boolean isLoadingHandle;
    private int mRequestCode = PBAPIContant.PB_SETTING_TWEET_STARTED_TWEET;
    private String mMessage = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pb_layout_webview);

       /* Intent intent = getIntent();
        String title = getString(R.string.tweet_dialog_title);
        String url = REQUEST_URL;
        if(intent != null){
            url = intent.getStringExtra(PBAPIContant.PB_SETTING_EXTRA_URL);
            title = intent.getStringExtra(PBAPIContant.PB_SETTING_EXTRA_TITLE);
            mRequestCode = intent.getIntExtra(PBAPIContant.PB_SETTING_TWEET_TYPE, 
                    PBAPIContant.PB_SETTING_TWEET_INVITE);
            mMessage = intent.getStringExtra(PBAPIContant.PB_SETTING_TWEET_MESSAGE);
        }

        // @lent5 add action bar #S
        mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(mHeaderBar, title);
        // @lent5 add action bar #E

        WebView webView = (WebView) findViewById(R.id.pb_webview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(mWebChromeClient);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
                String message = getString(R.string.dialog_network_error_body);
                if(mRequestCode ==  PBAPIContant.PB_SETTING_TWEET_STARTED_TWEET){
                    message = getString(R.string.pb_dl_failed_post_started_photobag_text);
                }

                showDialog(TwitterHandler.this, message , true);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                isLoadingHandle = false;
                view.loadUrl(url);
                return true;
            }
        });

        // Log.w(PBConstant.TAG, url);
        webView.loadUrl(url);
        mLvWaiting = (LinearLayout) findViewById(R.id.ll_loading_panel_waiting);
        mLvWaiting.setVisibility(View.VISIBLE);*/

    }

   /* private WebChromeClient mWebChromeClient = new WebChromeClient() {
        public void onProgressChanged(WebView view, int progress)
        {
            // Log.d(PBConstant.TAG, "[onWebProgressChanged] " + progress);
            if(view != null){
                Log.d(PBConstant.TAG, "[onWebProgressChanged] " + progress + " # " 
                        + isLoadingHandle + " # " +  view.getUrl());
            }
            if(mLvWaiting != null && progress > 75){
                mLvWaiting.setVisibility(View.GONE);
                // Log.e(PBConstant.TAG, "[onWebProgressChanged] " + view.getUrl());
            }

            String strUrl = view.getUrl();
            if(!TextUtils.isEmpty(strUrl)){
                if(progress > 0 && strUrl.contains("oauth_verifier") && !isLoadingHandle){
                    isLoadingHandle = true;
                    postTweet(strUrl, mMessage, mRequestCode);
                }
            }
        }
    };*/
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // clear consumer oAuth
        /*if(mWebChromeClient != null){
            mWebChromeClient = null;
        }

        consumer = null;*/
    }

    @Override
    protected void handleHomeActionListener() {
        finish();
    }

    @Override
    protected void setLeftBtnsDetailHeader(ActionBar headerBar) {}

    //private static CommonsHttpOAuthConsumer consumer;
    /*private void postTweet(String strUrl, String msg, int requestCode) {
        int postStatus = STATUS_FAILED;
        String message = msg;

        try {            
            URL url = new URL(strUrl);
            if(url == null) return;

            // String verifier = url.getQuery();
            // consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
            OAuthProvider authProvider = new DefaultOAuthProvider(REQUEST_URL, ACCESS_URL, AUTHORIZE_URL);

            authProvider.retrieveAccessToken(consumer, url.getQuery());
            
            AccessToken accessToken = new AccessToken(consumer.getToken(),
                    consumer.getTokenSecret());

            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
            twitter.setOAuthAccessToken(accessToken);

            storeAccessToken(TwitterHandler.this, twitter.getScreenName(), accessToken);
            // Tweet message to be updated.
            //messsage = message + "\n" + System.currentTimeMillis();
            twitter4j.Status status = twitter.updateStatus(message);
            if(status != null) {
                postStatus = STATUS_SUCCESS;
                setResult(postStatus);
                finish();
            }
        }catch (NullPointerException e) {
            e.printStackTrace();
            postStatus = STATUS_FAILED;
        }catch (Exception e) {
            e.printStackTrace();
            String response = e.getMessage();
            if (!TextUtils.isEmpty(response) && response.contains("403")) {
                if(response.contains("too long")){
                    // deny cause limitation
                    // response = getString(R.string.pb_dl_failed_post_started_photobag_text);
                    postStatus = STATUS_FAILED;
                    finish();
                }else{
                    if(requestCode ==  PBAPIContant.PB_SETTING_TWEET_STARTED_TWEET){
                        // overload
                        PBPreferenceUtils.saveBoolPref(PBApplication
                                .getBaseApplicationContext(), PBConstant.PREF_NAME,
                                PBConstant.PREF_SETTING_TWEET_STARTED, true);
                        response = getString(R.string.pb_dl_already_post_started_photobag_tweet_text);
                        showDialog(TwitterHandler.this, response, true);
                    }else{
                        finish();
                    }
                    postStatus = STATUS_FAILED_OVERLAP;
                }
                // showDialog(TwitterHandler.this, response, true);
            }else{
                // e.printStackTrace();
                postStatus = STATUS_FAILED;
                response = getString(R.string.pb_dl_failed_post_status_tweet_text);
                if(requestCode ==  PBAPIContant.PB_SETTING_TWEET_STARTED_TWEET){
                    response = getString(R.string.pb_dl_failed_post_started_photobag_text);
                }
                response = getString(R.string.dialog_network_error_body);
                showDialog(TwitterHandler.this, response, true);
            }
            setResult(postStatus);
        }

        if(requestCode == PBAPIContant.PB_SETTING_TWEET_STARTED_TWEET){
            PBPreferenceUtils.saveBoolPref(PBApplication
                    .getBaseApplicationContext(), PBConstant.PREF_NAME,
                    PBConstant.PREF_SETTING_TWEET_STARTED, true);
        }
    }*/

    private static void showDialog(final Activity context, String message, final boolean isFinish){
        PBGeneralUtils.showAlertDialogActionWithOnClick(context,
                android.R.drawable.ic_dialog_alert,
                context.getString(R.string.pb_dialog_fail_title), 
                message,
                context.getString(R.string.pb_btn_OK),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(isFinish){
                    context.finish();
                }
            }
        });
    }

    public static final int STATUS_SUCCESS = 301;
    public static final int STATUS_FAILED_OVERLAP = 302;
    public static final int STATUS_FAILED = 303;
    public static final int STATUS_AUTHEN_FAILED = 304;
    public interface TwitterActionListener{
        public void postTweetResult(int status); 
    }

    /**
     * 
     * @param context
     * @param message
     * @param type {@link PBAPIContant#PB_SETTING_TWEET_STARTED_TWEET or PBAPIContant#PB_SETTING_TWEET_INVITE}
     * @param requestCode
     */
    /*public static int getAuthTwitterAndUpdateTweet(Activity context, 
            String message, int requestCode, TwitterActionListener listener) {
        int postStatus = STATUS_FAILED;
        try {
            Status status = updateTweet(message);
            if(status != null){
                postStatus = STATUS_SUCCESS;
            }
        } catch (TwitterException e) {
            String response = e.getMessage();
            if (!TextUtils.isEmpty(response) && response.contains("403")) {
                postStatus = STATUS_FAILED_OVERLAP;
                // showDialog(context, response, false);
            }else{
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            postStatus = STATUS_AUTHEN_FAILED;
            e.printStackTrace();
        }

        if(postStatus != STATUS_FAILED) {
            Log.d("le.nguyen", "start twitter handler ------------ postStatus != STATUS_FAILED");
            if(listener != null){
                listener.postTweetResult(postStatus);
            }
            return postStatus;
        }
        
        // in case still not login twitter
        consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);  // ----- (5)
        OAuthProvider authProvider = new DefaultOAuthProvider(REQUEST_URL, ACCESS_URL, AUTHORIZE_URL);

        try {
            String oAuthURL = authProvider.retrieveRequestToken(consumer, OAUTH_CALLBACK_URL);
            // this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(oAuthURL)));
            Intent intent = new Intent(context, TwitterHandler.class);
            intent.putExtra(PBAPIContant.PB_SETTING_EXTRA_URL, oAuthURL);
            intent.putExtra(PBAPIContant.PB_SETTING_EXTRA_TITLE, context.getString(R.string.tweet_dialog_title));
            intent.putExtra(PBAPIContant.PB_SETTING_TWEET_TYPE, requestCode);
            intent.putExtra(PBAPIContant.PB_SETTING_TWEET_MESSAGE, message);
            Log.d("le.nguyen", "start twitter handler ------------");
            context.startActivityForResult(intent, requestCode);
        } catch (OAuthMessageSignerException e) {
            postStatus = STATUS_AUTHEN_FAILED;
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            postStatus = STATUS_AUTHEN_FAILED;
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            postStatus = STATUS_AUTHEN_FAILED;
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            postStatus = STATUS_AUTHEN_FAILED;
            e.printStackTrace();
        }

        return postStatus;
    }*/


    /*public static twitter4j.Status updateTweet(String mess) throws TwitterException, NullPointerException{
        AccessToken token = TwitterHandler.getAccessToken(PBApplication.getBaseApplicationContext());
        if(token == null){
            return null;
        }
        Twitter myTwitter = null;
        twitter4j.Status status = null;
        // initialize Twitter4J        
        myTwitter = new TwitterFactory().getInstance();
        if(myTwitter != null){
            myTwitter.setOAuthConsumer(TwitterHandler.CONSUMER_KEY, TwitterHandler.CONSUMER_SECRET);
            myTwitter.setOAuthAccessToken(token);
            status = myTwitter.updateStatus(mess + " " + System.currentTimeMillis());
        }

        return status;
    }*/

    /*private static void storeAccessToken(Context context,String username, AccessToken tocken) {
        if(!TextUtils.isEmpty(username)){
            PBPreferenceUtils.saveStringPref(context,PBConstant.PREF_NAME,
                    PREF_TWITTER_USERNAME,username);
        }
        PBPreferenceUtils.saveStringPref(context,PBConstant.PREF_NAME,
                PREF_TWITTER_TOKEN,tocken.getToken());

        PBPreferenceUtils.saveStringPref(context,PBConstant.PREF_NAME,
                PREF_TWITTER_SCRECRET,tocken.getTokenSecret());        
    }   

    public static void logoutTwitter(Context context) {
        PBPreferenceUtils.saveStringPref(context,PBConstant.PREF_NAME,
                PREF_TWITTER_USERNAME, "");

        PBPreferenceUtils.saveStringPref(context,PBConstant.PREF_NAME,
                PREF_TWITTER_TOKEN, "");

        PBPreferenceUtils.saveStringPref(context,PBConstant.PREF_NAME,
                PREF_TWITTER_SCRECRET, "");        
    } 

    public static AccessToken getAccessToken(Context context) {
        String token = PBPreferenceUtils.getStringPref(context, 
                PBConstant.PREF_NAME, PREF_TWITTER_TOKEN, TOKEN_UNDEFINE);

        String tokenSecret = PBPreferenceUtils.getStringPref(context, 
                PBConstant.PREF_NAME, PREF_TWITTER_SCRECRET, TOKEN_UNDEFINE);

        if(token.equals(TOKEN_UNDEFINE))
            return null;

        if (token != null && tokenSecret != null && !"".equals(tokenSecret)
                && !"".equals(token)) {
            return new AccessToken(token, tokenSecret);
        }
        return null;
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        //consumer = null;
    }

}
