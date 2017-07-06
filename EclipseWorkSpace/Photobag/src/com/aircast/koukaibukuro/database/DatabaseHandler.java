package com.aircast.koukaibukuro.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aircast.koukaibukuro.model.Password;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 3;

	private static final String DATABASE_NAME = "koukaibukuro";

	private static final String TABLE_FAVOURITE = "kb_favorite";
	
	private static final String TEMP_TABLE_FAVOURITE = "temp_kb_favorite";

	private static final String KEY_ID = "id";
	private static final String KEY_THUMB_URL= "thumb_url";
	private static final String KEY_NICKNAME = "nickname";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_CREATED = "created";
	private static final String KEY_EXPIRES_AT = "expires_at";
	private static final String KEY_EXPIRES_TIME = "expires_time";
	private static final String KEY_PHOTOS_COUNT = "photos_count";
	private static final String KEY_DOWNLOAD_USER_COUNT = "downloaded_users_count";
	private static final String KEY_FAVOURITE = "favourite";
	private static final String KEY_HONEY = "honey";
	private static final String KEY_NEW = "newItem";
	private static final String KEY_ISDOWNLOAD = "isDownload";
	private static final String KEY_RECOMMENDED = "recommended";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/*@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + TABLE_FAVOURITE + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_THUMB_URL + " TEXT,"
				+ KEY_NICKNAME + " TEXT,"+ KEY_PASSWORD + " TEXT,"
				+ KEY_CREATED + " TEXT,"+ KEY_EXPIRES_AT + " TEXT,"
				+ KEY_EXPIRES_TIME + " TEXT,"+ KEY_PHOTOS_COUNT + " TEXT,"
				+ KEY_DOWNLOAD_USER_COUNT + " TEXT,"+ KEY_FAVOURITE + " TEXT,"
				+ KEY_HONEY + " TEXT,"+ KEY_NEW + " TEXT,"+ KEY_ISDOWNLOAD + " TEXT" + ")";


		db.execSQL(CREATE_FAVOURITE_TABLE);
	}*/
	
	@Override
	 public void onCreate(SQLiteDatabase db) {
	  String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + TABLE_FAVOURITE + "("
	    + KEY_ID + " INTEGER PRIMARY KEY," + KEY_THUMB_URL + " TEXT,"
	    + KEY_NICKNAME + " TEXT,"+ KEY_PASSWORD + " TEXT,"
	    + KEY_CREATED + " TEXT,"+ KEY_EXPIRES_AT + " TEXT,"
	    + KEY_EXPIRES_TIME + " TEXT,"+ KEY_PHOTOS_COUNT + " TEXT,"
	    + KEY_DOWNLOAD_USER_COUNT + " TEXT,"+ KEY_FAVOURITE + " TEXT,"
	    + KEY_HONEY + " TEXT,"+ KEY_NEW + " TEXT,"+ KEY_ISDOWNLOAD + " TEXT," + KEY_RECOMMENDED+ " TEXT" + ")";


	  db.execSQL(CREATE_FAVOURITE_TABLE);
	 }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE);
//
//		onCreate(db);
		String CREATE_TEMP_FAVOURITE_TABLE = "CREATE TABLE " + TEMP_TABLE_FAVOURITE + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_THUMB_URL + " TEXT,"
				+ KEY_NICKNAME + " TEXT,"+ KEY_PASSWORD + " TEXT,"
				+ KEY_CREATED + " TEXT,"+ KEY_EXPIRES_AT + " TEXT,"
				+ KEY_EXPIRES_TIME + " TEXT,"+ KEY_PHOTOS_COUNT + " TEXT,"
				+ KEY_DOWNLOAD_USER_COUNT + " TEXT,"+ KEY_FAVOURITE + " TEXT,"
				+ KEY_HONEY + " TEXT,"+ KEY_NEW + " TEXT,"+ KEY_ISDOWNLOAD + " TEXT" + ")";
	       db.execSQL(CREATE_TEMP_FAVOURITE_TABLE);

	    // Create an temporaty table that can store data of older version
	     
	       db.execSQL("INSERT INTO " + TEMP_TABLE_FAVOURITE + " SELECT " +  KEY_ID + ", "
	         +  KEY_THUMB_URL + ", " +  KEY_NICKNAME + ", " +  KEY_PASSWORD + ", " +  KEY_CREATED + ", " 
	    		   +  KEY_EXPIRES_AT + ", " +  KEY_EXPIRES_TIME + ", " +  KEY_PHOTOS_COUNT 
	    		   + ", " +  KEY_DOWNLOAD_USER_COUNT + ", " +  KEY_FAVOURITE 
	    		   + ", " +  KEY_HONEY + ", " +  KEY_NEW 
	    		   + ", " +  KEY_ISDOWNLOAD + " FROM " + TABLE_FAVOURITE);

	// Insert data into temporary table from existing older version database (that doesn't contains email //column)
	 
	       db.execSQL("DROP TABLE "+ TABLE_FAVOURITE);
	// Remove older version database table
	     
	       String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + TABLE_FAVOURITE + "("
					+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_THUMB_URL + " TEXT,"
					+ KEY_NICKNAME + " TEXT,"+ KEY_PASSWORD + " TEXT,"
					+ KEY_CREATED + " TEXT,"+ KEY_EXPIRES_AT + " TEXT,"
					+ KEY_EXPIRES_TIME + " TEXT,"+ KEY_PHOTOS_COUNT + " TEXT,"
					+ KEY_DOWNLOAD_USER_COUNT + " TEXT,"+ KEY_FAVOURITE + " TEXT,"
					+ KEY_HONEY + " TEXT,"+ KEY_NEW + " TEXT,"+ KEY_ISDOWNLOAD + " TEXT," + KEY_RECOMMENDED + " TEXT"+ ")";
	       db.execSQL(CREATE_FAVOURITE_TABLE);
	      
	// Create new table with email column
	       db.execSQL("INSERT INTO " + TABLE_FAVOURITE +" SELECT " +  KEY_ID + ", "
	  	         +  KEY_THUMB_URL + ", " +  KEY_NICKNAME + ", " +  KEY_PASSWORD + ", " +  KEY_CREATED + ", " 
	  	    		   +  KEY_EXPIRES_AT + ", " +  KEY_EXPIRES_TIME + ", " +  KEY_PHOTOS_COUNT + ", "
	  	    		  +  KEY_DOWNLOAD_USER_COUNT + ", "+  KEY_FAVOURITE + ", "
	  	    		  +  KEY_HONEY + ", " +  KEY_NEW + ", "
	  	    		   +  KEY_ISDOWNLOAD + ", " +"0"+ " FROM " + TEMP_TABLE_FAVOURITE);
	// Insert data ffrom temporary table that doesn't have email column so left it that column name as null.     
	       db.execSQL("DROP TABLE " + TEMP_TABLE_FAVOURITE);
	}

	public int updateProfile(String password,String isDownload) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ISDOWNLOAD, isDownload);

		// updating row
		return db.update(TABLE_FAVOURITE, values, KEY_PASSWORD + " = '" + password
				+ "'", null);

	}
	public int addFavouriteItem(String thumbUrl,String nickName,String password,String created,String expires_at,String
			 expires_time,String photosCont,String downloadCount,String favorite, String honey, String newItem,String historyDownload,String recommend) {
		int value = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		 String Query = "Select * from " + TABLE_FAVOURITE + " where " + KEY_PASSWORD + "='" + password
				+ "'";
		 Cursor cursor = db.rawQuery(Query, null);
         if(cursor.getCount() < 1){
        	ContentValues values = new ContentValues();
    		values.put(KEY_THUMB_URL, thumbUrl);
    		values.put(KEY_NICKNAME, nickName);
    		values.put(KEY_PASSWORD, password);
    		values.put(KEY_CREATED, created);
    		values.put(KEY_EXPIRES_AT, expires_at);
    		values.put(KEY_EXPIRES_TIME, expires_time);
    		values.put(KEY_PHOTOS_COUNT, photosCont);
    		values.put(KEY_DOWNLOAD_USER_COUNT, downloadCount);
    		values.put(KEY_FAVOURITE, favorite);
    		values.put(KEY_HONEY, honey);
    		values.put(KEY_NEW, newItem);
    		values.put(KEY_ISDOWNLOAD, historyDownload);
    		values.put(KEY_RECOMMENDED, recommend);
    		
    		db.insert(TABLE_FAVOURITE, null, values);
    		db.close();
    		value = 1;
      }
         
         return value;
		
	}
	
	public  boolean isFavouriteItemExists(String password) {


		SQLiteDatabase db = this.getWritableDatabase();
			
			Cursor cursor = db.rawQuery("SELECT 1 FROM "+TABLE_FAVOURITE+" WHERE "+KEY_PASSWORD+"=?", new String[] {password});
		    boolean exists = cursor.moveToFirst();
		    cursor.close();
	    	 return exists;
	}
	
//	public  String getFavouriteItem(String Password) {
//
//		String query = "SELECT * FROM " + TABLE_FAVOURITE + " WHERE "
//				+ KEY_PASSWORD + " = '" + Password + "'";
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(query, null);
//		String str = "0";
//		if (cursor.getCount() > 0) {
//			 cursor.moveToFirst();
//				str = cursor.getString(cursor.getColumnIndex(KEY_FAVOURITE));
//		}
//
//		cursor.close();
//		db.close();
//		return str;
//	}
	
	public List<Password> getAllFavouriteItem() {

		List<Password> list = new ArrayList<Password>();
		String query = "SELECT * FROM " + TABLE_FAVOURITE;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				
				Password mPassword = new Password();
				
				mPassword.setNickName(cursor.getString(cursor.getColumnIndex(KEY_NICKNAME)));
				mPassword.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
				mPassword.setCreatedDate(cursor.getString(cursor.getColumnIndex(KEY_CREATED)));
				mPassword.setPhotoCount(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_PHOTOS_COUNT))));
				mPassword.setNumberOfDownload(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_DOWNLOAD_USER_COUNT))));
				mPassword.setFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FAVOURITE))));
				mPassword.setHoney(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_HONEY))));
				mPassword.setThumbURL(cursor.getString(cursor.getColumnIndex(KEY_THUMB_URL)));
				mPassword.setExpiresAT(cursor.getString(cursor.getColumnIndex(KEY_EXPIRES_AT)));
				mPassword.setExpiredTime(cursor.getString(cursor.getColumnIndex(KEY_EXPIRES_TIME)));
				mPassword.setNewItem(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_NEW))));
				
				boolean flag = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ISDOWNLOAD))) == 1? true :false;
				mPassword.setDownload(flag);
				mPassword.setRecommend(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_RECOMMENDED))));
				
				list.add(mPassword);
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		Log.d("list", ""+list);
		return list;
	}
	



	public boolean deleteFavouriteItem(String Password) {

		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(TABLE_FAVOURITE, KEY_PASSWORD + "='" + Password
				+ "'", null) > 0;
	}
	
	
	public int getRowCount() {
		String countQuery = "SELECT  * FROM " + TABLE_FAVOURITE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();

		return rowCount;
	}
	

	public void resetTables() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_FAVOURITE, null, null);
		db.close();
	}

}