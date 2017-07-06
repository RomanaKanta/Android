package com.aircast.photobag.api.c2dm;

import com.aircast.photobag.api.PBAPIContant;

/**
 * C2DM's contants
 * @author lent5
 */
public class C2DMConstant {
    // support Push Notification
    public static final String PN_C2DM_URL_REGISTER =  "https://api."+PBAPIContant.MAIN_DEVELOPMENT_URL+"/2/c2dm";;
    public static final String PN_C2DM_AUTH = "authentication";
    public static final String PN_C2DM_DEVICE_AUTH = "authentication";
    public static final String PN_C2DM_REGISTER    = "com.google.android.c2dm.intent.REGISTER";  
    public static final String PN_C2DM_UNREGISTER = "com.google.android.c2dm.intent.UNREGISTER";
    public static final String PN_C2DM_REGISTRATION ="com.google.android.c2dm.intent.REGISTRATION";
    public static final String PN_C2DM_RECEIVER = "com.google.android.c2dm.intent.RECEIVE";

    public static final String PN_C2DM_SENDER_EMAIL= "photobag.kayac@gmail.com";
}
