package com.ksproject.krishop.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {

    SharedPreferences sharedPreferences;

    public PrefUtils(){}

    public void introShowed(Context context,boolean show) {
        sharedPreferences = context
                .getSharedPreferences(Constant.INTRO_SHOWED, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constant.INTRO_SHOWED, show);
        editor.commit();
    }

    public boolean isIntroShowed(Context context) {
        sharedPreferences = context
                .getSharedPreferences(Constant.INTRO_SHOWED, 0);
        return sharedPreferences.getBoolean(Constant.INTRO_SHOWED,false);
    }

}
