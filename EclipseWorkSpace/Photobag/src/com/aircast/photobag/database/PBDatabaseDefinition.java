package com.aircast.photobag.database;

import android.util.Log;

/** 
 * Class define info local database
 * @author lent5
 */
public class PBDatabaseDefinition {
    public static final int HISTORY_INBOX = 1;
    public static final int HISTORY_SENT = 2;
    public static final int MEDIA_PHOTO = 0;//"photo";
    public static final int MEDIA_VIDEO = 1;//"video";
    public static final int HISTORY_PURCHASE_TIMELINE = 3;

    public static final String CREATE_SQL = "createSql";
    public static final String UPDATE_SQL = "updateSql";

    /** update version from 3 to 4 for support video duration. <br>
     from 4 to 5 for add function. <br>
     form 5 to 6 to add new table definition<br>
     to 12 as add many new<br>
     to 13 as bug happened in 12<br>
     to 14 to add can save mark<br>
     to 15 to add local save days<br>
     to 16 to add ad link<br>          
     to 17 to isPublic and accepted          <=== current version*/
    
    //public static final int VERSION = 16;
    public static final int VERSION = 20;  // updated by Atik for koukaibukuro
    public static final String FILE = "photobag.sqlite";
    public static final String DESC = " DESC ";    

    private final static String CREATE_TABLE = "CREATE TABLE ";    
    private final static String T_INTEGER = " INTEGER ";    
    // private final static String T_REAL = " REAL ";    
    private final static String T_TEXT = " TEXT ";    
    private final static String T_BLOB = " BLOB ";    
    private final static String T_NOT_NULL = " NOT NULL ";    
    private final static String T_PRIMARY_KEY = " PRIMARY KEY  ";

    /** tables in dabatase */
    public static final Class<?>[] Tables = {
        HistoryData.class,
        HistoryPhoto.class,
        TimelineHistoryData.class
    };

    /** info collumn table photo's collection */
    public static final class TwitterFriends{
        public static final String TABLE = "twitter_friends_table";
        public static final String C_TWITTER_SCREEN_NAME = "_id";
        public static final String C_TWITTER_NAME = "c_twitter_name";
        public static final String C_TWITTER_URL = "c_twitter_url";    
        public static final String C_TWITTER_ICON = "c_twitter_icon";
        // pass

        /**
         * build a sql string to create twitter friends
         */
        public static String createSql(){
            return CREATE_TABLE + TABLE + " ("
            + C_TWITTER_SCREEN_NAME + T_TEXT + T_PRIMARY_KEY
            + "," + C_TWITTER_NAME + T_TEXT 
            + "," + C_TWITTER_URL + T_TEXT 
            + "," + C_TWITTER_ICON + T_BLOB 
            + ");";
        }
        /**
         * build a sql string to update table twitter friends
         */
        public static String updateSql(int oldVersion){
            return "" ; // "DROP TABLE IF EXISTS " + TABLE;
        }
    }

    /** info collumn table photo's collection */
    public static final class HistoryPhoto{
        public static final String TABLE = "photo_data_table";
        public static final String C_ID = "_id";
        public static final String C_PHOTO_URL = "c_photo_url";
        public static final String C_PHOTO_THUMB = "c_photo_thumb";    
        public static final String C_HISTORY_ID = "c_history_id";
        public static final String C_MEDIA_TYPE = "c_media_type";
        public static final String C_MEDIA_DURATION = "c_duration";
        // pass

        /**
         * build a sql string to create table photo's history
         */
        public static String createSql(){
            return CREATE_TABLE + TABLE + " ("
            + C_ID + T_TEXT + T_PRIMARY_KEY
            + "," + C_PHOTO_URL + T_TEXT + T_NOT_NULL
            + "," + C_PHOTO_THUMB + T_TEXT 
            + "," + C_HISTORY_ID + T_TEXT + T_NOT_NULL
            + "," + C_MEDIA_TYPE + T_INTEGER + T_NOT_NULL
            + "," + C_MEDIA_DURATION + T_INTEGER + T_NOT_NULL
            + ");";
        }
        /**
         * build a sql string to update table photo's history
         */
        public static String updateSql(int oldVersion){
            // return "DROP TABLE IF EXISTS " + TABLE;
        	String sql = "";
        	if (oldVersion < 4){
	            String updateMediaType = "ALTER TABLE " + TABLE 
	                    + " ADD " + C_MEDIA_TYPE + T_INTEGER 
	                    + " DEFAULT " + MEDIA_PHOTO + " NOT NULL";
	            String updateMediaDuration = "ALTER TABLE " + TABLE 
	                    + " ADD " + C_MEDIA_DURATION + T_INTEGER 
	                    + " DEFAULT " + MEDIA_PHOTO + " NOT NULL";
	            sql += updateMediaDuration + "; " + updateMediaType + "; ";
        	}
        	return sql;
        }
    }

    /** Define table history list */
    public static final class HistoryData{
        public static final String TABLE = "history_data_table";
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
		public static final String C_IS_PUBLIC = "c_is_public";
		public static final String C_ACCEPTED = "c_accepted";
		public static final String MEASSGE_COUNT = "message_count";
		public static final String C_Expires_AT = "expires_at";
		public static final String C_ST_Digit = "st_digit";

        /**
         * build a sql string to create table collection's history
         */
        public static String createSql(){
            return CREATE_TABLE + TABLE + " ("
            + C_ID + T_TEXT + T_PRIMARY_KEY
            + "," + C_COLECTION_ID + T_TEXT + T_NOT_NULL
            + "," + C_PASSWORD + T_TEXT + T_NOT_NULL            
            + "," + C_CREATED_AT + T_INTEGER + T_NOT_NULL
            + "," + C_CHARGES_AT + T_INTEGER /*+ T_NOT_NULL*/
            + "," + C_DOWNLOAD_COUNT + T_TEXT + T_NOT_NULL
            + "," + C_PHOTO_COUNT + T_TEXT + T_NOT_NULL
            + "," + C_TYPE + T_TEXT + T_NOT_NULL
            + "," + C_THUMB + T_TEXT
            + "," + C_ADDIBILITY + T_INTEGER
            + "," + C_EXTRA + T_TEXT
            + "," + C_IS_UPDATABLE + T_INTEGER
            + "," + C_UPDATED_AT + T_INTEGER
            + "," + C_HONEY_NUM + T_INTEGER
            + "," + C_MAPLE_NUM + T_INTEGER
            + "," + C_SAVE_MARK + T_INTEGER
            + "," + C_SAVE_DAYS + T_INTEGER
            + "," + C_AD_LINK + T_TEXT
            + "," + C_IS_PUBLIC + T_INTEGER
            + "," + C_ACCEPTED + T_INTEGER
            + "," + MEASSGE_COUNT + T_INTEGER  
            + "," + C_Expires_AT + T_INTEGER
            + "," + C_ST_Digit + T_TEXT
            + ");";
        }

        /** build sql update table colleciton's history */
        public static String updateSql(int oldVersion){
        	String sql = "";
        	if (oldVersion < 5){
        		String updateAddibility = "ALTER TABLE " + TABLE 
	                    + " ADD " + C_ADDIBILITY + T_INTEGER;
        		String updateExtra = "ALTER TABLE " + TABLE 
	                    + " ADD " + C_EXTRA + T_TEXT;
        		String updateIsUpdatable = "ALTER TABLE " + TABLE 
	                    + " ADD " + C_IS_UPDATABLE + T_INTEGER;
        		String updateUpdatedAt = "ALTER TABLE " + TABLE 
	                    + " ADD " + C_UPDATED_AT + T_INTEGER;
        		String update = "UPDATE " + TABLE + " SET " + 
	                    C_ADDIBILITY + " = 0 ," + 
        				C_EXTRA + " = '', " + 
	                    C_IS_UPDATABLE + " = 0, " + 
        				C_UPDATED_AT + " = 0";
        		sql += updateAddibility + "; " + updateExtra + "; " + updateIsUpdatable + "; " + updateUpdatedAt + "; " + update + "; ";
        	}
        	if(oldVersion < 13){
        		String addHoneyNumColumn = "ALTER TABLE " + TABLE
        				+ " ADD " + C_HONEY_NUM + T_INTEGER;
        		String update = "UPDATE " + TABLE + " SET " +
        				C_HONEY_NUM + " = 0";
        		sql+= addHoneyNumColumn+";"+update+";";
        	}
        	if(oldVersion < 13){
        		String addHoneyNumColumn = "ALTER TABLE " + TABLE
        				+ " ADD " + C_MAPLE_NUM + T_INTEGER;
        		String update = "UPDATE " + TABLE + " SET " +
        				C_MAPLE_NUM + " = 0";
        		sql+= addHoneyNumColumn+";"+update+";";
        	}
        	if (oldVersion < 14) {
        		String addSaveMarkColumn = "ALTER TABLE " + TABLE
        				+ " ADD " + C_SAVE_MARK + T_INTEGER;
        		String update = "UPDATE " + TABLE + " SET " +
        				C_SAVE_MARK + " = 0";
        		sql+= addSaveMarkColumn + ";" + update + ";";
        	}
        	if (oldVersion < 15) {
        		String addSaveMarkColumn = "ALTER TABLE " + TABLE
        				+ " ADD " + C_SAVE_DAYS + T_INTEGER;
        		String update = "UPDATE " + TABLE + " SET " +
        				C_SAVE_DAYS + " = 0";
        		sql+= addSaveMarkColumn + ";" + update + ";";
        	}
        	if (oldVersion < 16) {
        		String addAdLinkColumn = "ALTER TABLE " + TABLE
        				+ " ADD " + C_AD_LINK + T_TEXT;
        		String update = "UPDATE " + TABLE + " SET " +
        				C_AD_LINK + " = ''";
        		sql+= addAdLinkColumn + ";" + update + ";";
        	}
        	if (oldVersion < 17) {
        		String isPublic = "ALTER TABLE " + TABLE
        				+ " ADD " + C_IS_PUBLIC + T_INTEGER;
        		String accepted = "ALTER TABLE " + TABLE
        				+ " ADD " + C_ACCEPTED + T_INTEGER;
        		
        		String update = "UPDATE " + TABLE + " SET " +
        				C_IS_PUBLIC + " = 0, " + 
        				C_ACCEPTED + " = 0 " ;
        		sql+= isPublic + ";" + accepted + "; " + update + ";";
        	}
        	if (oldVersion < 18) {
        		String messageCount = "ALTER TABLE " + TABLE
        				+ " ADD " + MEASSGE_COUNT + T_INTEGER;
        		
        		String update = "UPDATE " + TABLE + " SET " +
        				MEASSGE_COUNT + " = 0"  ;
        		sql+= messageCount + ";"  + update + ";";
        	}
        	if (oldVersion < 19) {
        		String expirseAt = "ALTER TABLE " + TABLE
        				+ " ADD " + C_Expires_AT + T_INTEGER;
        		
        		String update = "UPDATE " + TABLE + " SET " +
        				C_Expires_AT + " =  0";
        		sql+= expirseAt + ";" + update + ";";
        	}
        	if (oldVersion < 20) {
        		
        		String addAdLinkColumn = "ALTER TABLE " + TABLE
        				+ " ADD " + C_ST_Digit + T_TEXT;
        		String update = "UPDATE " + TABLE + " SET " +
        				C_ST_Digit + " = ''";
        		sql+= addAdLinkColumn + ";" + update + ";";
        	}
            return sql; //"DROP TABLE IF EXISTS " + TABLE;
        }
    }
    
    /**
     * HistoryPurchase table definition
     * @author agung
     *
     */
    public static final class TimelineHistoryData{
    	public static final String TABLE = "history_purchase_table";
    	public static final String C_ID = "_id";
    	public static final String C_CREATED_AT = "c_created_at";
    	public static final String C_TYPE = "c_item_type";
    	public static final String C_DESCRIPTION = "c_description";
    	public static final String C_IS_NEW = "c_is_new_row";
    	
    	public static String createSql(){
			return CREATE_TABLE + TABLE + " ("
					+ C_ID + T_INTEGER + T_PRIMARY_KEY + " DESC,"
					+ C_CREATED_AT + T_INTEGER + T_NOT_NULL + ","
					+ C_TYPE + T_TEXT + T_NOT_NULL + ","
					+ C_IS_NEW + T_INTEGER + " DEFAULT 1," 
					+ C_DESCRIPTION + T_TEXT + T_NOT_NULL + ");";
    	}
    	
    	public static String updateSql(int oldVersion){
    		
    		if(oldVersion < 8){
    			return "DROP TABLE IF EXISTS " + TABLE + ";" +
    					CREATE_TABLE + TABLE + " ("
    					+ C_ID + T_INTEGER + T_PRIMARY_KEY + " DESC,"
    					+ C_CREATED_AT + T_INTEGER + T_NOT_NULL + ","
    					+ C_TYPE + T_TEXT + T_NOT_NULL + ","
    					+ C_IS_NEW + T_INTEGER + " DEFAULT 1," 
    					+ C_DESCRIPTION + T_TEXT + T_NOT_NULL + ");";
    		}
    		return "";
    	}
    }
}
