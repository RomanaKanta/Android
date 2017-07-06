package com.aircast.photobag.activity;

import android.app.Activity;
import android.content.Intent;

/**
 * <b>TODO Do nothing but call gc() in onDestroy(), also canbe removed,
 * just let activity call gc() itself when needed?</b>
 * */
public class PBAbsTransition extends Activity{
    @Override
    protected void onPause() {
        super.onPause();
        // overridePendingTransition(R.anim.slide_in_right_anim, R.anim.slide_out_right_anim);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // overridePendingTransition(R.anim.slide_in_right_anim, R.anim.slide_out_right_anim);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        // overridePendingTransition(R.anim.slide_in_right_anim, R.anim.slide_out_right_anim);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        // overridePendingTransition(R.anim.slide_in_right_anim, R.anim.slide_out_right_anim);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}