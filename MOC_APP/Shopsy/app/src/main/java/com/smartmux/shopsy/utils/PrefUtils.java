/*
package com.smartmux.shopsy.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {

    SharedPreferences sharedPreferences;

    public PrefUtils(){}

    public void setFirstName(Context context,String firstName) {
        sharedPreferences = context
                .getSharedPreferences(Constant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.FIRST_NAME, firstName);
        editor.commit();
    }

    public String getFirstName(Context context) {
        sharedPreferences = context
                .getSharedPreferences(Constant.PREFS_NAME, 0);
        return sharedPreferences.getString(Constant.FIRST_NAME,"");
    }

    public void setLastName(Context context,String lastName) {
        sharedPreferences = context
                .getSharedPreferences(Constant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.LAST_NAME, lastName);
        editor.commit();
    }

    public String getLastName(Context context) {
        sharedPreferences = context
                .getSharedPreferences(Constant.PREFS_NAME, 0);
        return sharedPreferences.getString(Constant.LAST_NAME,"");
    }

    public void setEmail(Context context,String email) {
        sharedPreferences = context
                .getSharedPreferences(Constant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.EMAIL_ID, email);
        editor.commit();
    }

    public String getEmail(Context context) {
        sharedPreferences = context
                .getSharedPreferences(Constant.PREFS_NAME, 0);
        return sharedPreferences.getString(Constant.EMAIL_ID,"");
    }

    public void setPassword(Context context,String pwd) {
        sharedPreferences = context
                .getSharedPreferences(Constant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.PASSWORD, pwd);
        editor.commit();
    }

    public String getPassword(Context context) {
        sharedPreferences = context
                .getSharedPreferences(Constant.PREFS_NAME, 0);
        return sharedPreferences.getString(Constant.PASSWORD,"");
    }

    public void setCustomerID(Context context,String id) {
        sharedPreferences = context
                .getSharedPreferences(Constant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.CUSTOMER_ID, id);
        editor.commit();
    }

    public String getCustomerID(Context context) {
        sharedPreferences = context
                .getSharedPreferences(Constant.PREFS_NAME, 0);
        return sharedPreferences.getString(Constant.CUSTOMER_ID,"");
    }

}
*/
