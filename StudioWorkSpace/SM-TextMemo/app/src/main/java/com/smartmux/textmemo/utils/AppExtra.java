package com.smartmux.textmemo.utils;

import android.os.Environment;

public class AppExtra {

	public static String NOTE_MODE = "NOTE_MODE";
	public static String NOTE_MODE_CREATE = "NOTE_MODE_CREATE";
	public static String NOTE_MODE_EDIT = "NOTE_MODE_EDIT";
	public static String NOTE_FILENAME = "NOTE_FILENAME";
	public static String RETURN_TAG = "return_code";
	public static String PURCHASE_TAG = "isPurchased";

	public static final String PREFS_NAME = "MyPrefsFile";

	public static String DELETE_TEXT = "Delete Note(Can't Undo)";
	public static String RENAME_TEXT = "Rename Note";
	public static String SHARE_TEXT = "Share Note";
	public static int TOTAL_PASSWORD_DIGIT = 4;
	public static int SETTING_CODE = 5;
	public static int LOCK_CODE = 6;
	public static int HOME_CODE = 3;
	public static int BACK_CODE = 2;
	public static int MAIN_ACTIVITY_CODE = 1;
	public static int DEFAULT_CODE = 0;

	public static int TOTAL_TEXT_LIMIT = 10;

	public static String AVENIRLSTD_BLACK = "AvenirLTStd-Black.otf";
	public static String AVENIRLSTD_LIGHT = "AvenirLTStd-Light.otf";
	public static String APP_ROOT_FOLDER = Environment
			.getExternalStorageDirectory() + "/.Text-Memo-Data";
	
	// Notification JSON Part 
		public static final String NF_TEXT = "text";
		public static final String NF_THUMB = "thumb_url";
		public static final String NF_ACTION = "action_url";

    public static final String BANNER_LIST_URL = "https://smartmux.com/apps.smartmux/api/app_banners/list";
	
}
