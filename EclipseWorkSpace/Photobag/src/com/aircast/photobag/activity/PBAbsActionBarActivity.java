package com.aircast.photobag.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.aircast.photobag.R;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.aircast.photobag.widget.actionbar.ActionBar.Action;
import com.aircast.photobag.widget.actionbar.ActionBar.PerformActionListener;

/**
 * class Activity implement common methods
 * 
 * @author lent5
 * 
 */
public abstract class PBAbsActionBarActivity extends FragmentActivity {
    
    @SuppressWarnings("unused")
	private static final String TAG = "PBAbsActionBarActivity";

    /** home action bar */
    private Action mHomeAction;

    /** handle listener for home action bar */
    protected abstract void handleHomeActionListener();

    /** handle listener for left action bar */
    protected abstract void setLeftBtnsDetailHeader(ActionBar headerBar);

    /**
     * call onCreate, after inflate layout into activity
     * 
     * @param headerBar
     * @param title
     */
    protected void setHeader(ActionBar headerBar, String title) {
        if (headerBar == null)
            return;

        mHomeAction = new ActionBar.ViewAction(PBAbsActionBarActivity.this,
                createHomeActionListener(), R.drawable.pb_header_logo);
        headerBar.setTitle(title);
        headerBar.setHomeAction(mHomeAction);
        headerBar.setDisplayHomeAsUpEnabled(true, null);
    }

    /**
     * create home action listener
     * 
     * @return
     */
    private ActionBar.PerformActionListener createHomeActionListener() {
        return new PerformActionListener() {
            @Override
            public void performAction(View view) {
                handleHomeActionListener();
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        registerSDCardReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        unregisterReceiver(mSdcardReceiver);
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.slide_in_right_anim,
//                R.anim.slide_out_right_anim);
    }

    @Override
    public void startActivity(Intent intent) {
//        overridePendingTransition(R.anim.slide_in_right_anim,
//                R.anim.slide_out_right_anim);
        super.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
//        overridePendingTransition(R.anim.slide_in_right_anim,
//                R.anim.slide_out_right_anim);
        super.startActivityForResult(intent, requestCode);
    }

    protected void sdcardUnmountChecking() { }

    private void registerSDCardReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addDataScheme("file");
        registerReceiver(mSdcardReceiver, filter);
    }
    
    private BroadcastReceiver mSdcardReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)){
                
                boolean mounted = android.os.Environment.getExternalStorageState().equals(
                        android.os.Environment.MEDIA_MOUNTED);
                if(!mounted 
                        || TextUtils.equals(action, Intent.ACTION_MEDIA_EJECT)){
                    sdcardUnmountChecking();
                }
            }
        }
    };
}
