package com.aircast.photobag.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import com.aircast.photobag.R;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * Seems to be twitter, as this was write long ago, <b>never be tested</b>.
 */
public class PBInviteTwitterActivity extends PBAbsActionBarActivity {

    /**
     * Button cancel download.
     */
    private Button mBtnPostToTwitter;

    /**
     * AsynTask for checking Twitter information.
     */
   // private CheckTwitterInfoTask mChekTwitterInfoTask;

    /**
     * Activity action bar.
     */
    private ActionBar mHeaderBar;

    /**
     * Tweet status want to set.
     */
    private String mPhotobagTweetStatus;

    /**
     * Layout for display waiting progress.
     */
    private LinearLayout mLayoutWaiting;

    private final int DIALOG_SHOW_LOGIN = 1000;

    /**
     * message for showing waiting progress.
     */
    private final int MSG_SHOW_WAITING_PROGRESS = 2000;
    /**
     * message for hide waiting progress.
     */
    private final int MSG_HIDE_WAITING_PROGRESS = 2001;
    /**
     * message for showing login dialog screen.
     */
    private final int MSG_LOGIN_DIALOG = 2002;
    /**
     * message for notice cannot update status and finish this activity.
     */
    private final int MSG_CANNOT_POST_TWEET = 2003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pb_layout_twitter_invite);

       /* mHeaderBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(mHeaderBar, getString(R.string.tweet_dialog_title));

        mPhotobagTweetStatus = getString(R.string.tweet_started_photobag_content);

        mBtnPostToTwitter = (Button) findViewById(R.id.btn_twitter_post_tweet);
        mBtnPostToTwitter.setOnClickListener(mOnClickListener);

        mLayoutWaiting = (LinearLayout) findViewById(R.id.ll_panel_waiting);

        // get token key from PREF
        String tokenKey = PBPreferenceUtils.getStringPref(
                getApplicationContext(), PBConstant.PREF_NAME,
                PBConstant.PREF_TOKEN, "");
        String tokenSecretKey = PBPreferenceUtils.getStringPref(
                getApplicationContext(), PBConstant.PREF_NAME,
                PBConstant.PREF_TOKEN_SECRET, "");
        if (TextUtils.isEmpty(tokenKey) || TextUtils.isEmpty(tokenSecretKey)) {
            showDialog(DIALOG_SHOW_LOGIN);
        }*/

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    };

    /**
     * Handler for handling Message.
     *//*
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

            case MSG_SHOW_WAITING_PROGRESS:
                if (mLayoutWaiting != null) {
                    mLayoutWaiting.setVisibility(View.VISIBLE);
                }
                break;

            case MSG_HIDE_WAITING_PROGRESS:
                if (mLayoutWaiting != null) {
                    mLayoutWaiting.setVisibility(View.GONE);
                }
                break;

            case MSG_LOGIN_DIALOG:
                showDialog(DIALOG_SHOW_LOGIN);
                break;
                
            case MSG_CANNOT_POST_TWEET:
                PBApplication
                        .makeToastMsg(getString(R.string.pb_dl_already_post_status_tweet_text));
                finish();
                break;

            default:
                // no default case
                break;
            }
        };
    };

    @Override
    public boolean dispatchKeyEvent(android.view.KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
        }
        return super.dispatchKeyEvent(event);
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_SHOW_LOGIN:
            LayoutInflater factory = LayoutInflater.from(this);
            final View loginEntryView = factory.inflate(
                    R.layout.pb_layout_login_twitter, null);
            final EditText edtUsername = (EditText) loginEntryView
                    .findViewById(R.id.pb_edt_twitter_username);
            final EditText edtPassword = (EditText) loginEntryView
                    .findViewById(R.id.pb_edt_twitter_password);
            return new AlertDialog.Builder(PBInviteTwitterActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert).setTitle(
                            "Login to Twitter") // TODO move to string.xml later
                    .setView(loginEntryView).setPositiveButton(
                            R.string.pb_btn_OK,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    String usernameStr = edtUsername
                                            .getEditableText().toString();
                                    String passStr = edtPassword
                                            .getEditableText().toString();
                                    if (TextUtils.isEmpty(usernameStr)
                                            || TextUtils.isEmpty(passStr)) {
                                        PBApplication//TODO
                                                .makeToastMsg("user or pass is empty, please try again");
                                        mHandler
                                                .sendEmptyMessage(MSG_LOGIN_DIALOG);
                                        return;
                                    }
                                    // start asynctask for checking twitter info
                                    if (mChekTwitterInfoTask == null
                                            || (mChekTwitterInfoTask
                                                    .getStatus() == AsyncTask.Status.PENDING || mChekTwitterInfoTask
                                                    .getStatus() == AsyncTask.Status.FINISHED)) {
                                        mChekTwitterInfoTask = new CheckTwitterInfoTask(
                                                getApplicationContext(),
                                                mHandler);
                                        mChekTwitterInfoTask.execute(
                                                usernameStr, passStr);
                                    }
                                }
                            }).setNegativeButton(R.string.pb_btn_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    finish();
                                }
                            }).create();

        default:
            // do nothing in this case
            break;
        }
        return null;
    }

    *//**
     * Views OnClickListener.
     *//*
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            case R.id.btn_twitter_post_tweet:
                mLayoutWaiting.setVisibility(View.VISIBLE);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(MSG_SHOW_WAITING_PROGRESS);
                        try {
                            Twitter mTwitterObj = TwitterHelper
                                    .getTwitterObj(getApplicationContext());
                            TwitterHelper.setUpdateStatus(mTwitterObj,
                                    mPhotobagTweetStatus);
                            // PBApplication.makeToastMsg("Post to Twitter OK");
                        } catch (TwitterException e) {
                            // e.printStackTrace();
                            // PBApplication.makeToastMsg("Post to Twitter error");
                            Log.e(PBConstant.TAG, "Post to Twitter ERROR >>> "
                                    + e.toString());
                        }
                        mHandler.sendEmptyMessage(MSG_HIDE_WAITING_PROGRESS);
                    }
                });
                thread.start();
                break;

            default:
                // do nothing.
                break;
            }
        }
    };

    *//**
     * Support class for saving photo from server!
     * 
     * @author NhatVT
     *//*
    private class CheckTwitterInfoTask extends AsyncTask<String, Integer, Void> {
        private Context mContext;
        private Handler mHandler;
        private boolean mIsLogInOk = false;
        private boolean mIsStatusDulicate = false;

        *//**
         * Constructer for this helper class.
         * 
         * @param context
         *            Context.
         * @param handler
         *            Handler for handling Message.
         *//*
        public CheckTwitterInfoTask(Context context, Handler handler) {
            this.mContext = context;
            this.mHandler = handler;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mLayoutWaiting != null) {
                mLayoutWaiting.setVisibility(View.VISIBLE);
            }
            mIsLogInOk = false;
            mIsStatusDulicate = false;
        }

        @Override
        protected Void doInBackground(String... userAndPass) {
            try {
                // get login for the first time and save username and pass to pref
                Twitter mTwitterObj = TwitterHelper.getLogin(this.mContext,
                        userAndPass[0], userAndPass[1], false);
                if (mTwitterObj != null) {
                    try {
                        TwitterHelper.setUpdateStatus(mTwitterObj,
                                mPhotobagTweetStatus);
                        Log.i("mapp", "Post to Twitter OK");
                        // TODO if post to twitter OK, save the result for tracking later.
                        PBPreferenceUtils.saveBoolPref(
                                PBApplication.getBaseApplicationContext(),
                                PBConstant.PREF_NAME,
                                PBConstant.PREF_SETTING_TWEET_STARTED,
                                true);
                        mIsLogInOk = true;
                    } catch (TwitterException e) {
                        // e.printStackTrace();
                        // PBApplication.makeToastMsg("Post to Twitter error");
                        String response = e.getMessage();
                        Log.e(PBConstant.TAG, "Post to Twitter ERROR >>> "
                                + response);
                        if (response.contains("403")) {
                            mIsStatusDulicate = true;
                            mHandler.sendEmptyMessage(MSG_CANNOT_POST_TWEET);
                        }
                    }
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d("mapp", "check twitter task was canceled!");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (mLayoutWaiting != null) {
                mLayoutWaiting.setVisibility(View.GONE);
            }
            if (mIsLogInOk) {
                PBApplication.makeToastMsg(getString(R.string.pb_dl_post_status_tweet_ok_text));
                finish();
            } else {
                if (!mIsStatusDulicate) {
                    PBApplication.makeToastMsg(getString(R.string.pb_dl_check_username_pass_text));
                    showDialog(DIALOG_SHOW_LOGIN);
                }
            }
        }

    }*/

    @Override
    protected void handleHomeActionListener() {
        finish();
    }

    @Override
    protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
        // do nothing in this case!
    }
    
}
