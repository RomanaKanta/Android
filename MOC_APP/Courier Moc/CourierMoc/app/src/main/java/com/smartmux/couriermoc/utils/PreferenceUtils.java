package com.smartmux.couriermoc.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tanvir-android on 7/26/16.
 */
public class PreferenceUtils {


    public static void setUserInfo(Context context, String userInfo) {
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(Constant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.USER_INFO, userInfo);
        editor.commit();
    }

    public static String getUserInfo(Context context){
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(Constant.PREFS_NAME, 0);
        String return_userInfo = sharedPreferences.getString(Constant.USER_INFO, "");
        return return_userInfo;
    }

}
