package com.aircast.photobag.api;

public class PBAPIContant {
	
//	public static final boolean DEBUG = true;
//	public static final boolean isProduction = false;
	
	public static final boolean DEBUG = false;
	public static final boolean isProduction = true;
	public static final String MAIN_URL = "photobag.in"; 
	//public static final String MAIN_URL = "test04.photobag.in"; // Sawada san main server test
	public static final String DEVELOPMENT_URL = "aircast.jp";
	public static final String MAIN_DEVELOPMENT_URL = isProduction? MAIN_URL 
														: DEVELOPMENT_URL;
	public static final String API_HOST = isProduction ? "api.photobag.in":
														 "api.aircast.jp";
	
	//public static final String API_HOST = isProduction ? "api04.photobag.in":
	//	 "api.aircast.jp"; // Sawada san main server test
	
	/*public static final String SERVER_URL = "http://photobag.in";*/
	public static final String SERVER_URL = isProduction? "http://photobag.in":
														  "http://aircast.jp";
	public static final String SAKURA_API_HOST = "http://openpage.photobag.in"; // URL for open page
	public static final String PB_REPORT_URL = SERVER_URL +"/support2.html";
	
    public static final String SERVER_IP = "https://"+API_HOST+"/";
    public static final String SERVER_PORT = "";

    public static final String API_REPORT_GCM_URL       = "https://"+API_HOST+"/2/gcm";
    
//    public static final String API_FETCH_TOKEN_URL      = "https://"+API_HOST+"/2/signup";
    public static final String API_FETCH_TOKEN_URL_V3   = "https://"+API_HOST+"/3/signup";
    public static final String API_UPLOAD_IMAGE_URL     = "https://"+API_HOST+"/2/photos/new";
    public static final String API_UPLOAD_VIDEO_URL     = "https://"+API_HOST+"/2/videos/new";
    public static final String API_CONFIRM_PASSWORD_URL = "https://"+API_HOST+"/2/collection/%s/password/confirm";
    public static final String API_SET_PASSWORD_URL     = "https://"+API_HOST+"/2/collection/%s/password";
    public static final String API_PUBLIC_PASSWORD_URL  = "https://"+API_HOST+"/2/collections/recommend";
    public static final String API_FINISH_UPLOAD_URL    = "https://"+API_HOST+"/2/collection/%s/finish";
    public static final String API_CANCEL_UPLOAD_URL    = "https://"+API_HOST+"/2/collection/%s/cancel";
    public static final String API_PC_SIGNIN_URL        = "https://"+API_HOST+"/2/pc/signin";
    //13062012 @HaiNT15: them phan GET /2/me    v�ｽPOST /2/photos <E>
    public static final String API_LIST_COLLECTION_URL_HONEY = "https://"+API_HOST+"/2/photos?honey=1";// "http://"+API_HOST+"/?honey=1";
    public static final String API_SIGNED_DATA_URL           = "https://"+API_HOST+"/2/photos";
    //13062012 @HaiNT15: them phan GET /2/me    v�ｽPOST /2/photos <S>
    public static final String API_POST_PURCHASE_URL    = "https://"+API_HOST+"/2/purchase";
    
    public static final String API_ACHIEVEMENTS_GET     = "https://"+API_HOST+"/2/ad/achievements";
    public static final String API_ACHIEVEMENTS_POST    = "https://"+API_HOST+"/2/ad/achievements/made";
    
    //public static final String API_SECRET_KEY 			= "0ebe205b179fdfafffcece6af21c179593fcaf7e"; // Atik This is Original secret key which has been hacked
    //public static final String API_SECRET_KEY 			= "0be13d89295099ae3275fec54506df8b7bd86391"; // Atik this value will be change for every version -- this is for dev - 2.7.10
    // public static final String API_SECRET_KEY 			= "79dca2a411994e292c883e9ada0ff4667a7643c0"; // Atik this value will be change for every version -- this is for  android honban 2.8.0 to 2.8.0
  // public static final String API_SECRET_KEY 			= "e92d14f2e23a02c313d23fd390f5c5c7dac12447"; // Atik this value will be change for every version -- this is for  android dev 2.8.0 to 2.8.0
    //public static final String API_SECRET_KEY 			= "ac73937ed103db713f1ae9a4136fd5a9368a8144"; // Atik this value will be change for every version -- this is for  android honban beta sinsei 2.7.9.1 to 2.7.9.1
    													   
    //public static final String API_ST_VALUE		= "46ec4417f9dd48058a434419767af00be6eed7df"; // Atik this value will be change for every version
    //public static final String API_ST_VALUE		= "46ec4417f9dd48058a434419767af00be6eed7df"; // Atik this value will be change for every version -- this is for dev - 2.7.10
    //public static final String API_ST_VALUE		= "26d5aa86038512a920d75fd466564291c12dcae0"; // Atik this value will be change for every version -- this is for  android honban 2.8.0 to 2.8.0
   // public static final String API_ST_VALUE		= "0f33bdabaaf4ffeb0ad499cd24b1b9d1c06412c5"; // Atik this value will be change for every version -- this is for  android dev 2.8.0 to 2.8.0
    //public static final String API_ST_VALUE		= "45fdd631e19c34560546c420a99a71a3cef5147a"; // Atik this value will be change for every version -- this is for  android honban beta sinsei 2.7.9.1 to 2.7.9.1
    
//    public static final String API_SECRET_KEY 			= "f50ac87ccd11afd125565ca8ec6beb0302cc1e64"; // Rifat this value will be change for every version -- this is for  android dev 2.8.2 to 2.8.2
//    public static final String API_ST_VALUE		= "b6d131aeb74cf396245ce7e5f6a1477973470876"; // Rifat this value will be change for every version -- this is for  android dev 2.8.2 to 2.8.2

    
    /*for version 2.8.3*/
    public static final String API_SECRET_KEY 			= "6f9e4ffc2fe5ad31a1f361e7cfa7e501e31fe108"; 
    public static final String API_ST_VALUE		= "ac25d070d805eeed0d482874bd07a1b6266b6806"; 
    
    
    public static final String API_LIST_COLLECTION_NO_HONEY_URL = "https://"+API_HOST+"/2/photos?honey=0";
    public static final String API_UPDATE_CHARGES_TIME 			= "https://"+API_HOST+"/2/collection/%s";
    public static final String API_DELETE_COLLECTION_URL        = "https://"+API_HOST+"/2/collection/%s/delete";
    public static final String API_DELETE_PHOTOS_URL       		= "https://"+API_HOST+"/2/collection/%s/photos/delete";
    public static final String API_HISTORY_INFO_URL             = "https://"+API_HOST+"/2/history";
    public static final String API_REPORT_C2DM_URL              = "https://"+API_HOST+"/2/c2dm";
    public static final String API_FREE_DOWNLOAD_TIME_URL       = "https://"+API_HOST+"/2/me";
    public static final String API_INVITE_COMPLETED_URL         = "https://"+API_HOST+"/2/invitations/new";
    public static final String API_VERSION_API_URL              = "https://"+API_HOST+"/version";
    public static final String API_START_UPLOAD_URL             = "https://"+API_HOST+"/2/collections/new";
    public static final String API_START_ADD_URL                = "https://"+API_HOST+"/2/collection/%s/start_adding";
    public static final String API_DELETE_FROM_DOWNLOAD_HISTORY_URL = "https://"+API_HOST+"/2/collection/%s/download_history/delete";
	//public static final String API_GET_EXCHANGE_ITEM 			= "https://"+API_HOST+"/2/exchange/items";
	public static final String API_POST_EXCHANGE_DONGURI_TO_ITEM= "https://"+API_HOST+"/2/exchange";
	public static final String API_GET_POINT_HISTORY			= "https://"+API_HOST+"/2/point_history";

    public static final String PB_SETTING_EXTRA_URL = "web_url";
    public static final String PB_SETTING_EXTRA_TITLE = "web_title";
    public static final String PB_SETTING_TWEET_TYPE = "tweet.type";
    public static final String PB_SETTING_TWEET_MESSAGE = "tweet.message";
    public static final int PB_SETTING_TWEET_STARTED_TWEET = 101;
    public static final int PB_SETTING_TWEET_INVITE = 102;
    public static final int PB_SETTING_TWEET_TELL_MAGIC_PHRASE = 103;
    public static final String PB_CODE_TWITTER= "twitter";
    public static final String PB_UPDATE_VERSION_CHECK_URL= "http://photobag.in/android_receive.html";
       
	// URL for device kishuhenkou function
       
    public static final String API_MIGRATION_CODE_CREATION     = "https://"+API_HOST+"/2/get_migration_code";
    
    public static final String API_MIGRATION_CODE_VERIFICATION = "https://"+API_HOST+"/2/verify_migration";

    public static final String API_MIGRATION_CODE_VERIFIED     = "https://"+API_HOST+"/2/start_migration";

    public static final String API_MIGRATION_DEVICE_LOCK_STATUS     = "https://"+API_HOST+"/2/info_migration";
  
    public static final String PB_GET_THUMBNAIL_ARRAY_FOR_PASSWORD= "https://"+API_HOST+"/2/photo_thumbnails";
    
    public static final String PB_OPEN_PAGE_CLOSE_URL_CONTAINS= "closepressed";
    public static final String PB_OPEN_PAGE_ITEM_BROWSE_URL_CONTAINS= "details.php";
    public static final String PB_OPEN_PAGE_SCHEMA= "photobag://";
    
    public static final String PB_DOWNLOAD_WEBVIEW_URL ="http://"+MAIN_DEVELOPMENT_URL+"/wv/bear_update?type=receive&has_kaeru=1&version=";
    public static final String PB_UPLOAD_WEBVIEW_URL ="http://"+MAIN_DEVELOPMENT_URL+"/wv/bear_update?type=send1&has_kaeru=1&version=";
    public static final String PB_UPLOAD_SET_PASSWORD_WEBVIEW_URL ="http://"+MAIN_DEVELOPMENT_URL+"/wv/bear_update?type=send2&has_kaeru=1&version=";
    public static final String PB_WEBVIEW_URL_PARSE_REWARD ="reward";
    public static final String PB_WEBVIEW_URL_PARSE_ACORN_FOREST ="url=acornForest";
    public static final String PB_WEBVIEW_URL_PARSE_ACORN_OPENPAGE ="url=openpage";
    public static final String PB_WEBVIEW_URL_PARSE_INAPP ="inapp";
    public static final String API_SET_PUBLISH_PASSWORD_URL     = "https://"+MAIN_DEVELOPMENT_URL+"/public_password/public_password/update";
    public static final String API_GET_STATUS_PUBLISH_PASSWORD_URL     = "https://"+MAIN_DEVELOPMENT_URL+"/public_password/public_password/status";
    public static final String PB_HONEY_PURCHASE_URL     = "https://"+MAIN_DEVELOPMENT_URL+"/honeyshop/honeyshop/index";
    //public static final String PB_PHOTO_CONTEST_URL     = "http://192.168.0.113/PhotoBag/PhotoContest/";
    public static final String PB_PHOTO_CONTEST_URL     = "http://"+MAIN_DEVELOPMENT_URL+"/photocontest/";
    
    public static final String PB_UNREAD_MESSAGE_COUNT     = "https://"+MAIN_DEVELOPMENT_URL+"/chat/chat_message/unread_message_count"; // Added for chat message count

}
