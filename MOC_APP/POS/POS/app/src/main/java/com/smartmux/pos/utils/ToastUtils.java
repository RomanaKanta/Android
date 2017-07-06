package com.smartmux.pos.utils;

import android.widget.Toast;

import com.smartmux.pos.application.SmartPOSApplication;


public class ToastUtils{
    private ToastUtils() {
    }

    public static void showShort(int resId) {
        Toast.makeText(SmartPOSApplication.appContext, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(String message) {
        Toast.makeText(SmartPOSApplication.appContext, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(int resId) {
        Toast.makeText(SmartPOSApplication.appContext, resId, Toast.LENGTH_LONG).show();
    }

    public static void showLong(String message) {
        Toast.makeText(SmartPOSApplication.appContext, message, Toast.LENGTH_LONG).show();
    }

}
