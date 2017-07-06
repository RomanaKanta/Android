package com.smartmux.couriermoc.utils;

/**
 * Created by tanvir-android on 7/26/16.
 */
public class Constant {

    public static final String PREFS_NAME = "CourierPref";
    public static final String USER_INFO = "user_info";
    public static final String USER_PASSWORD = "password";
    public static final int GALLERY_REQUEST = 101;
    public static final int ACTION_TAKE_PHOTO_B = 010;
    public static String mCurrentPhotoPath = "";

    //feedback
    public static final boolean ISDEBUG = false;
    public static final int debug = ISDEBUG ? 1 : 0;
    public static final String status_code = "status_code";
    public static final String responseCode = "responseCode";
    public static final String OK = "OK";
    public static final String NG = "NG";
    public static final String API_BASE_URL = "https://smartmux.com/publisher/api/";
    public static final String SUB_API_FEEDBACK = API_BASE_URL
            + "app_comments/new/";

    public static final String FB_PACKAGE = "com.facebook.katana";
    public static final String THUMB_URL = "https://play.google.com/store/apps/details?id=com.smartux.photocollage";


    //json key
    public static final String ORDER_ID = "id";
    public static final String SENDER = "sender";
    public static final String PICKUP_FROM = "pickup";
    public static final String TYPE = "type";
    public static final String WEIGHT = "weight";
    public static final String COST = "cost";
    public static final String DATE = "date";
    public static final String RECIPIENT_NAME = "name";
    public static final String PHONE = "phone";
    public static final String DELIVER_TO = "deliverto";
    public static final String INSTRUCTION = "instruction";

    public static final String STATUS = "status";
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String DISTANCE = "distance";
    public static final String COMMENT = "comment";

    public static final String COMPLETE = "complete";
    public static final String UNFULFILLED = "unfulfilled";
    public static final String PENDING = "pending";
    public static final String ONGOING = "ongoing";



}
