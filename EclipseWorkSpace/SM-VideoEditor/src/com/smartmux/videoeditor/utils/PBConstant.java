package com.smartmux.videoeditor.utils;


import android.graphics.Bitmap;


/**
 * @author thetv
 * class contain parametor for app all
 */
public class PBConstant {
    public static final String TAG = "VideoEditor";

    public static final String PREF_NAME = "com.smartmux.videoeditor";
    // 20120208 added by NhatVT, support switching layout in download screen <S>
    public static final String PREF_DOWNLOAD_CURRENT_LAYOUT_KEY = "dl_screen_current_layout";
    public static final String CACHE_FOLDER_NAME = ".VideoEditor";// 20120210 add cache folder name
    public static final String CACHE_SHARE_FILE_NAME = "photobox";
    // 20120208 added by NhatVT, support switching layout in download screen <E>
    // 20120213 added to support download photo list <S>
    public static final String PREF_DL_PHOTOLIST_PASS = "pref_photo_list_pass";
    public static final String PREF_DL_PHOTOLIST_JSON_DATA = "pref_photo_list_json_data";
    public static final String PREF_PURCHASE_INFO_PASSWORD = "pref_purchase_info_password";
    public static final String PREF_PURCHASE_INFO_JSON_DATA = "pref_purchase_info_json_data";
    /**
     * set to 0 - for init state; 1 - running task; 2 - task done
     */
    public static final String PREF_DL_PHOTOLIST_DOWNLOAD_STATE = "pref_photo_list_download_state";
    public static final String PREF_DL_PHOTOLIST_DOWNLOADED = "pref_photo_list_downloaded";
    
    // public static final String PREF_DL_INPUT_PASS_RETRY_COUNT = "pref_input_pass_count";
    public static final String PREF_DL_INPUT_PASS_PREV_PASS = "pref_input_pass_prev_pass";
    public static final String PREF_MIGRATON_CODE = "pref_migraton_code";
    public static final String PREF_MIGRATON_CODE_SUCCESS = "pref_migraton_code_success";
    public static final String PREF_MIGRATON_CODE_INPUT = "pref_migraton_code_input";
    public static final String PREF_IS_IN_EXCEEDED_MODE = "pref_is_in_exceeded_mode";
    public static final String PREF_IS_APP_FIRST_INSTALL = "pref_is_app_first_install";
    public static final int MAX_INPUT_PASSWORD_CHECK_COUNT = 10;
    public static final String PREF_DL_INPUT_PASS_RETRY_START_TIME = "pref_input_pass_retry_start_time";
    public static final int TIME_COUNT_DOWN_EXCEEDED_SCREEN = 5; // in minute.
    
    public static final int PHOTO_THUMB_WIDTH = 150;
    public static final int PHOTO_THUMB_HEIGHT = 150;
    // 20120213 added to support download photo list <E>
    public static final int REQUEST_VIEW_WEB_PAGE = 104;
    public static final int REQUEST_CODE_OPEN_CONFIRMPASS = 105;
    // @lent add request code #E
    
    public static final int PROCESSBAR_LOAD_TIME = 10;
    // 24 value for demo
    public static final int MSG_UPDATE_UI = 103;

    // ++Hai's constants
    public static final String INTENT_SELECTED_MEDIA_TYPE = "SELECTED_MEDIA_TYPE";
    public static final String INTENT_SELECTED_MEDIA = "SELECTED_MEDIA";
    public static final String INTENT_PASSWORD = "USER_PASSWORD";
    public static final String INTENT_PASSWORD_ID = "PASSWORD_ID";
    public static final String INTENT_LONG_PASSWORD = "LONG_PASSWORD";
    public static final String INTENT_IS_PUBLIC_PASSWORD = "IS_PUBLIC_PASSWORD";
    public static final int UPLOAD_COMPRESS_FAIL = -5;
    public static final int UPLOAD_COMPRESS_ERROR = -7; // 20120504 support showing error when compress failed!
    public static final int UPLOAD_ERROR = -2;
    public static final int UPLOAD_PROCESS_COMPRESS_VIDEO = -6; // 20120426 added for support video compress function!
    public static final int UPLOAD_PROCESS_COMPRESS_VIDEO_DONE = -10; // 20140626 added for support count for video compress done
    public static final int UPLOAD_FORBIDDEN = -9;
    public static final int UPLOAD_FINISH = -1; // notify upload file to server completely
    public static final int UPLOAD_FINISH_COMPLETED = -3; // notify cache file upload completely
    public static final int UPLOAD_ANALYTICS = -44;
    public static final String UPLOAD_SERVICE_PREF = "UploadServicePreference";
    public static final String PREF_CHARGE_DATE = "PrefenceChargeDate";
    public static final String PREF_UPLOAD_FINISH = "PreferenceIsUploadFinish";
    public static final String PREF_COLLECTION_THUMB = "UploadedCollectionThumb";
    public static final String PREF_COLLECTION_ID = "UploadCollectionId";
    public static final String PREF_COLLECTION_EX_ID = "UploadCollectionExId";
    public static final String PREF_PUBLIC_COLLECTION_ID = "UploadCollectionIdToPublic";
    public static final String PREF_UPLOAD_PASS = "UploadPassword";
    public static final String INTENT_UPLOADED_PHOTO_NUM = "number_of_uploaded_photo";
    public static final String PREF_ADDIBILITY = "PreferenceAddibility";
    public static final String PREF_SAVEMARK = "PreferenceSaveMark";
    public static final String PREF_SAVEDAYS = "PreferenceSaveDays";
    public static final String PREF_IS_UPDATABLE = "PreferenceIsUpdatable";
    public static final String PREF_UPDATED_AT = "PreferenceUpdatedAt";
    public static final String PREF_INPUT_SEQUENCE_FINISH = "PreferenceInputSequenceHasBeenFinished";
    // --Hai's constants
    
    /**
     * connect to web page timeout.
     */
    public static final int CONECT_WEB_TIMEOUT = 8;//timeout 10s for this value - 8
    
    
    // 20120215 add constans for start download complete or purchase complete or start purchase screen <S>
    public static final String START_DOWNLOAD_COMPLETE = "start_download_complete";
    public static final String START_PURCHASE_COMPLETE = "start_purchase_complete";
    public static final String START_PURCHASE_NOTICE = "start_purchase_notice";
    public static final String START_EXCEEDED = "start_exceeded";
    // 20120215 add constans for start download complete or purchase complete or start purchase screen <E>
    
    /** update freetime if user's invited.*/
    public static final String INVITATION_COMPLETED = "com.kobayashi.photobox.invitation.success";
    
    /** desire photo width for calculate bitmap sample size. */
    public static final int MAX_IMAGE_WIDTH = 960;
    /**desire photo height for calculate bitmap sample size.*/
    public static final int MAX_IMAGE_HEIGHT = 960;
    
    // 20120222 added by NhatVT, support resume download <S>
    public static final String PREF_RESUME_CURRENT_DOWNLOAD_POS = "pref_current_download_pos";
    public static final String PREF_RESUME_CURRENT_PROGRESS = "pref_resume_current_progress";
    // 20120222 added by NhatVT, support resume download <E>
    
    // 20120223 added by NhatVT, update code flow for purchase honey <S>
    public static final String CUSTOM_INTENT_PURCHASE_STATE_CHANGED = "com.kayack.photobag.billing.PURCHASE_STATE_CHANGED";
    public static final String CUSTOM_INTENT_PURCHASE_RESPONSE_ERROR = "com.kayack.photobag.billing.RESPONSE_ERROR";
    // 20120223 added by NhatVT, update code flow for purchase honey <E>
    
    // 20120224 added by NhatVT, compress image before upload <S>
    public static final int MAXIMUM_IMAGE_SIZE_UPLOAD = 960;
    public static final int COMPRESS_PRECENT = 90;
    public static final int MAX_SINGLE_IMAGE_PIX = 1024*1024; // control max single image in 1MB
    // 20120224 added by NhatVT, compress image before upload <E>
    public static final int DECODE_COMPRESS_PRECENT = 90;
    public static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    public static final String PREF_TOKEN = "pref_token";
    public static final String PREF_TOKEN_SECRET = "pref_token_secret";
    
    public static final String PREF_THE_FIRST_DOWNLOAD_TAB_OPEN = "theFistOnEditExchange";

    // 20120420 added by NhatVT, support video feature <S>
    public static final String MEDIA_VIDEO_EXT_MP4 = ".mp4";
    public static final String MEDIA_VIDEO_EXT_3GP = ".3gp";
    public static final String MEDIA_PHOTO_EXT_JPG = ".jpg";
    public static final String SPLASH_CHAR = "/";
    public static final String VIDEO_THUMB_STR = "_videothumb";
    
    public static final int VIDEO_LENGTH_LIMIT = 15 * 60 * 1000; // minisec
    public static final long VIDEO_SIZE_LIMIT = 100 * 1024 * 1024; // byte
    public static final String PREF_IS_VIDEO_PLAYING = "pref_is_video_playing";
    // 20120420 added by NhatVT, support video feature <E>
    public static final String INTENT_START_SERVICE_FROM_SELECT_IMG = "start_service_from_select_img"; // 20120508 not update mLocalMediaList when not start from SelectMultipleImageActivity
    
    // 20120521 add support download format with video file <S>
    public static final String VIDEO_FORMAT_3G2 = "?format=3g2";
    public static final String VIDEO_FORMAT_3GP = "?format=3gp";
    public static final String VIDEO_FORMAT_MP4 = "?format=mp4";
    public static final String VIDEO_FORMAT_MOV = "?format=mov";
    // 20120521 add support download format with video file <E>
    public static boolean COMPRESSORUPDLOAD = false;
    
    //20120601 added by Bac add honey bonus count
    public static final String PREF_HONEY_BONUS = "honey_bonus_count";
    public static boolean HONEY_FIRST_RUN_CHECK = true;
    public static final String PREF_HONEY_FIRST_RUN = "honey_bonus_first_run";
    public static final String PREF_INPUT_INVITE_CODE_DONE = "input_invite_code_done";
    
    //20130220 added by @agungpratama add acron and maple count
    public static final String PREF_DONGURI_COUNT = "donguri_count";
    public static final String PREF_MAPLE_COUNT = "maple_count";
    public static final String PREF_GOLD_COUNT = "gold_count";
    
    //20120605 Bac using honey bonus to download
    public static final String INTENT_PASSWORD_HONEY_PHOTO = "intent_password_honey_photo";
    
    public static final String UPLOAD_ADD = "UploadAdd";
    public static final String DOWNLOAD_UPDATE_OLD_BAG_ID = "DownloadUpdateOldBagId";
    
    public static final String TAB_SET_BY_TAG = "TabSetByTag";
    
    public static final String ACTION = "Action";
    
    //20121101 add for send mail to pc
    public static final String INTENT_TYPE_SENDMAIL = "message/rfc822";
    public static final String INTENT_TYPE_SENDMAIL_TEST = "text/plain";
    
    //20121115 add for facebook
    public static final String FACEBOOK_APP_ID = "133262513482101";
    public static final String PREF_FACEBOOK_ACCESS_TOKEN = "access_token";
    public static final String PREF_FACEBOOK_ACCESS_EXPIRES = "access_expires";
    public static final String FACEBOOK_PHOTOBAG_ALBUM = "/photobag/photos";
    
    //donguri exchange
    public static final String PREF_DONGURI_HONEY_EXCHANGE_RATE = "donguri_to_honey_rate";
    public static final String PREF_DONGURI_ITUNE_EXCHANGE_RATE = "donguri_to_itune_rate";
    public static final String PREF_DONGURI_AMAZON_EXCHANGE_RATE = "donguri_to_amazon_rate";
    public static final String PREF_DONGURI_GOLD_EXCHANGE_RATE = "gold_to_honey_rate";
    
    //for advertisements
    //public static final String PB_URL_ACORN_FOREST = PBAPIContant.SERVER_URL + "/acorn/android?lang=ja";
    public static final int APPDRIVER_SITE_ID = 11547;
    public static final String APPDRIVER_SITE_KEY = "8427536e83ca076cb7b7c3bfd055bf5e";
    public static final int APPDRIVER_MEDIA_ID = 3086;
    public static final int APPDRIVER_REFRESH_TIME = 0;
    public static final String APPDIRVER_IDENTIFIER = "com.kobayashi.photobox.indentifier.appdriver";
    public static final String APPDRIVER_REFRESH_URL = "photoboxxyz://download";

	public static final String PURCHASE_BY_MAPLE = "purchase_photo_by_maple";
	
	//notification purpose timeline
	public static final String PREF_NAME_NOTIF_EXISITING = "newest_counter_existing";
	public static final String PREF_NAME_NOTIF_HONEY_NEW = "newest_honey_count";
	public static final String PREF_NAME_NOTIF_MAPLE_NEW = "newest_maple_count";
	public static final String PREF_NAME_NOTIF_DONGURI_NEW = "newest_donguri_count";
	public static final String PREF_NAME_NOTIF_GOLD_NEW = "newest_gold_count";
	public static final String PREF_NAME_NOTIF_NEWEST_TIMELINE_HISTORY = "latest_newest_timeline_created_at";

	public static final int REQUEST_EXCHANGE_DONGURY = 1296412;

	public static final String PREV_PAGE = "previous_page";
	
	public static final String PREF_NEW_TIMELINE_COUNTER = "new_timeline_counter";

	public static final String IS_OWNER = "is_owner_of_collection";
	
	public static final int PASSWORD_LENGTH = 36;
	
	// video compress quality bitrate
    public static final String PREF_VIDEO_COMPRESS_HIGH_QUALITY = "video.high";
    public static final String PREF_VIDEO_COMPRESS_MEDIUM_QUALITY = "video.medium";
    public static final String PREF_VIDEO_COMPRESS_LOW_QUALITY = "video.low";
    
    
    public static final String PREF_NEED_REFRESH_HISTORY_SCREEN = "pref_need_refresh_history_screen";
    public static final String PREF_NEED_REFRESH_HISTORY_SCREEN_FOR_UPLOAD = "pref_need_refresh_history_screen_for_upload";
    public static final String PREF_NO_NEED_DELETE = "pref_no_need_delete";
    public static final String PREF_NO_NEED_UPDATE = "pref_no_need_update";

    //// chat module //////
    public static final String PREF_NICK_NAME = "nick_name";
    public static final String PREF_GROUP_NAME = "group_name";
    public static final String PREF_MGS_COUNT = "last_message_count";
    
    public static final String CHAT_HOST_URL = "http://smartmux.com/aircast/chat/"; 
    public static final String CHAT_USER_ADD = "http://smartmux.com/aircast/chat/users/add";

    public static final String PREF_LISTVIEW_SELECTION = "pref_listview_selection";
    public static final int CHAT_MAXIMUM_INPUT_CHARACTER = 100;
    public static final String FACEBOOK_APP_PACKAGE_NAME = "com.facebook.katana";
    public static final String   INDEX_PAGE="index.php";
    public static final String   OPEN_PAGE_TOKEN_PARAM_NAME="token=";
    public static final String   OPEN_PAGE_UID_PARAM_NAME="uuid=";
    public static final String   OPEN_PAGE_PLATFORM_PARAM_NAME="platform=";
    public static final String   OPEN_PAGE_PLATFORM_PARAM_NAME_VALUE="android";
    
    public static final String PREF_CURRENT_HISTORY_COLLECTION_ID = "current_collectionid";
    
	/**
	 * true  -  when first time started  My page screen 
	 * false -  when already started  once
	 */
    public static final String  APP_FIRST_BOOT_MY_PAGE = "first_my_page_screen";
    public static final String PREF_PASSWORD_FROM_LIBRARY = "library_password";
    public static final String IS_DOWNLOAD_PASSWORD = "isdownload_password";

//    public static final String IS_TAG = "i s_lib_tag";
//    public static final String IS_DOWNLOAD = "is_lib_download";
//    public static final String IS_DOWNLOAD_TAB = "is_lib_download_tab";


    public static final String TAG_NAME = "tag_name";
    public static  boolean IS_HISTORY_TAG = false;
    
    public static final String ISDOWNLOAD = "is_download";
    public static final String ISDOWNLOAD_FOR_CHAT = "is_download_for_chat";

    public static final String ISDOWNLOAD_FROM_MAINPAGE = "is_download_mypage";
    public static final String ISDOWNLOAD_FROM_SEARCHPAGE  = "is_download_searchpage";
    public static final String ISDOWNLOAD_FROM_MYPAGE  = "is_download_mypage";
    public static final String TAB_POSITION = "tab_position";
    
    
	/* Shared preference keys */
	//public static final String PREF_NAME = "sample_twitter_pref";
	public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	public static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
	public static final String PREF_USER_NAME = "twitter_user_name";
	public static final String PREF_TWITTER_LOGIN_BACK_ONLY = "pref_twitter_login_back_only";
    
    public static final String LOCAL_GIFT_EXCHANGE_HISTORY_DATA_FOLDER_NAME = ".photobox_gift_history_data";

    
}
