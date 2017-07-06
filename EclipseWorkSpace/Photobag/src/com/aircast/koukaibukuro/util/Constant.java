package com.aircast.koukaibukuro.util;

/**
 * @author class contain parametor for app all
 */
public class Constant {
//	public static final boolean isProductionLib = false;
	public static final boolean isProductionLib = true;
	public static final String MAIN_URL_LIB = "http://photobag.in/public_password/";
	public static final String DEVELOPMENT_URL_LIB = "http://aircast.jp/public_password/";
	public static final String MAIN_DEVELOPMENT_URL_LIB = isProductionLib? MAIN_URL_LIB 
			: DEVELOPMENT_URL_LIB;
	public static final String TAG = "Koukaibukuro";
	public static final int HISTORY_THUMB_WIDTH = 126;
	public static final int HISTORY_THUMB_HEIGHT = 126;
	public static final String CACHE_FOLDER_NAME = ".photobag_cache";
	public static final String CACHE_FOLDER_FOR_SEARCH_DATA_NAME = "openbag_search_cache";


	public static final String PREF_NAME = "com.aircast.koukaibukuro";
	public static final String APP_FIRST_BOOT_MY_TAB = "first_my_tab_screen";
	
	public static final String SELECTED_TAB_ID_IN_MY_PAGE_FRAGMENT = "selected_tab_id_in_my_page_fragment";
	public static final String KB_MAIN_PAGE_TAG = "MainPageTab";
	public static final String KB_SEARCH_PAGE_TAG = "SearchPageTab";
	public static final String KB_MY_PAGE_TAG = "MyPageTab";
	public static final String KB_UPLOAD_TAG = "MyUploadTab";

	//public static final String BASE_URL = "http://aircast.jp/public_password/";
	public static final String Get_Password_List_Url = MAIN_DEVELOPMENT_URL_LIB+"public_password/getListByArrival";
	//public static final String Get_Password_List_Url = MAIN_DEVELOPMENT_URL_LIB+"public_password/getListByArrival";
	public static final String Get_Password_List_Url_With_Ranking = MAIN_DEVELOPMENT_URL_LIB+"public_password/getListByRanking";
	public static final String Get_Password_List_For_Search_Url_With_New_Arrival = MAIN_DEVELOPMENT_URL_LIB+"public_password/searchListByArrival";
	public static final String Get_Password_List_For_Search_Url_With_Ranking = MAIN_DEVELOPMENT_URL_LIB+"public_password/searchListByRanking";
	public static final String Add_Favourite_Url = MAIN_DEVELOPMENT_URL_LIB+"favourite/update";
	public static final String NickName_Registration_Url = MAIN_DEVELOPMENT_URL_LIB+"nickname/update";
	public static final String Get_NickName_Url = MAIN_DEVELOPMENT_URL_LIB+"nickname/get_nickname";
	public static final String Get_Favourite_List_Url = MAIN_DEVELOPMENT_URL_LIB+"favourite/get_list";
	public static final String Get_Ranking_List_Url = MAIN_DEVELOPMENT_URL_LIB+"public_password/getListByRanking";
	public static final String Get_Arrival_List_Url = MAIN_DEVELOPMENT_URL_LIB+"public_password/getListByArrival";
	
	//public static final String Get_Recommened_List_Url = MAIN_DEVELOPMENT_URL_LIB+"public_password/get_list_test";
	public static final String Get_Main_Url_For_Get_List = MAIN_DEVELOPMENT_URL_LIB+"public_password/get_list";
	//curl -v -s "http://aircast.jp/public_password/public_password/get_list_test" -d"uid=XXX&sortby=recommended" -H "x-appid:photobag"
	//http://aircast.jp/public_password/public_password/getListByRanking
	
	public static final String openid_login_url= MAIN_DEVELOPMENT_URL_LIB+"public_password/login_with_openid";
	
	 public static final String HISTORY_ITEM_ID_K = "Id";
    public static final String HISTORY_COLLECTION_ID_K = "Collection_id";
    public static final String HISTORY_PASSWORD_K = "PWD";
    public static final String HISTORY_CATEGORY_INBOX_K = "HISTORYInbox";
    public static final String HISTORY_CREATE_DATE_K = "CreateDate";
    public static final String HISTORY_CHARGE_DATE_K = "ChargeDate";
    public static final String HISTORY_ADDIBILITY_K = "HistoryAddibility";
    public static final String HISTORY_IS_UPDATABLE_K = "HistoryIsUpdatable";
    public static final String HISTORY_UPDATED_AT_K = "HistoryUpdatedAt";
    public static final String HISTORY_SAVE_MARK_K = "HistorySaveMark";
    public static final String HISTORY_SAVE_DAYS_K = "HistorySaveDays";
    public static final String HISTORY_AD_LINK_K = "HistoryAdLink";
    public static final String COLLECTION_THUMB_K = "COLLECTION_THUMB";

    
	 public static final String C_ID = "_id"; 
	 public static final String C_COLECTION_ID = "history_id";    // 1 origin    // NhatVT save from download screen
	 public static final String C_PASSWORD = "c_history_password"; // NhatVT save from download screen
	 public static final String C_CREATED_AT = "c_history_created";// NhatVT save from download screen
	 public static final String C_CHARGES_AT = "c_history_charges";// NhatVT save from download screen
	 public static final String C_DOWNLOAD_COUNT = "c_history_download_count";
	 public static final String C_PHOTO_COUNT = "c_history_photo_count";
	 public static final String C_TYPE = "c_history_type";   // for different inbox, sent
	 public static final String C_THUMB = "c_history_photo_thumb"; // NhatVT save from download screen
	 public static final String C_ADDIBILITY = "c_history_addibility";
	 public static final String C_EXTRA = "c_history_extra";
	 public static final String C_IS_UPDATABLE = "c_is_updatable";
	 public static final String C_UPDATED_AT = "c_updated_at";
	 public static final String C_HONEY_NUM = "c_number_of_honey";
	 public static final String C_MAPLE_NUM = "c_number_of_maple";
	 public static final String C_SAVE_MARK = "c_history_save_mark";
	 public static final String C_SAVE_DAYS = "c_history_save_days";
	 public static final String C_AD_LINK = "c_ad_link";
	 public static final String TOKEN = "lib_token";
	 public static final String UID = "lib_uid";
	 public static final String C_IS_PUBLIC = "c_is_public";
	 public static final String C_ACCEPTED = "c_accepted";

	 public static final String KB_ARRIVAL_PASSWORD_JSON = "arrival_password_json";
	 public static final String KB_RANKING_PASSWORD_JSON = "ranking_password_json";
	 public static final String KB_RECOMMENED_PASSWORD_JSON = "recommened_password_json";
//	 public static final String KB_FAVOURITE_JSON = "favourite_json";
	 public static final String KB_ALL_PASSWORD_JSON = "all_password_json";

	 
	 public static final String KB_ARRIVAL_PASSWORD_JSON_ISSAVED = "arrival_password_json_issaved";
	 public static final String KB_RANKING_PASSWORD_JSON_ISSAVED = "ranking_password_json_issaved";
	 public static final String KB_FAVOURITE_JSON_ISSAVED = "favourite_json_issaved";
	 public static final String KB_RECOMMENED_JSON_ISSAVED = "recommened_json_issaved";
	 public static final String KB_ALL_PASSWORD_JSON_ISSAVED = "all_password_json_issaved";

	 
	 public static final String KB_NICKNAME = "kb_nickname";
	 public static final String KB_POSITION = "kb_position";
	 public static final String ANALYTICS_RESPONSE_CODE_DOWNLOAD_JSON = "analytics_json_response_code_in_download";

	 
}
