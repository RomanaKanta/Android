package com.smartmux.shopsy.utils;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;

import java.util.TimerTask;

/**
 * Created by tanvir-android on 9/20/16.
 */
public class CustomTimerTask extends TimerTask {

    private Context context;
    ViewPager pager;
   public static int page = 1;
    private Handler mHandler = new Handler();

    // Write Custom Constructor to pass Context
    public CustomTimerTask(Context con, ViewPager pager, int page) {
        this.context = con;
        this.pager = pager;
        this.page = page;
    }

    @Override
    public void run() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        pager.setCurrentItem(page++);
                    }
                });
            }
        }).start();

    }

}
