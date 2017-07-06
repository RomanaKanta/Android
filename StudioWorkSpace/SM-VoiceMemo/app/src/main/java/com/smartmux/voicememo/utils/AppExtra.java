package com.smartmux.voicememo.utils;

import android.os.Environment;

public class AppExtra {

	public static String AUDIO_MODE  			= "AUDIO_MODE";
	public static String AUDIO_MODE_RECORD  	= "AUDIO_MODE_RECORD";
	public static String AUDIO_MODE_PLAY  		= "NOTE_MODE_PLAY";
	public static String AUDIO_FILENAME 		= "AUDIO_FILENAME";
    public static String RETURN_TAG 			= "return_code";
    public static String PURCHASE_TAG 			= "isPurchased";
	
	public static final String PREFS_NAME       = "MyPrefsFile";
	
	public static String DELETE_AUDIO	  		= "Delete Audio(Can't Undo)";
	public static String RENAME_AUDIO	  		= "Rename Audio";
	public static String SHARE_AUDIO	  		= "Share Audio";
	
	public static int	TOTAL_PASSWORD_DIGIT	= 4;
	public static boolean	LOGIN_ADDED			= false;
	
	public static int	HOME_CODE     	        = 3;
	public static int	BACK_CODE     	        = 2;
	public static int	DEFAULT_CODE     	    = -1;
	
	public static String  	APP_PACKAGE_NAME	= "com.smartmux.voicememo";
	
	public static int	TOTAL_VOICE_LIMIT     	= 10;
	
	public static String  	APP_ROOT_FOLDER		= Environment.getExternalStorageDirectory() + "/.Voice-Memo-Data";
}
