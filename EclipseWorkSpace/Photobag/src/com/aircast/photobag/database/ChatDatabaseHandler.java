package com.aircast.photobag.database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ChatDatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "photbag_chat";
	private static final String TABLE_CHAT = "chat";
	private static final String TEMP_TABLE_CHAT = "temp_chat";
	private static final String KEY_ID = "id";
	private static final String KEY_UID = "uid";
	private static final String KEY_MESSAGE = "message";
	private static final String KEY_NICKNAME = "nickname";
	private static final String KEY_COLOR = "color";
	private static final String KEY_CREATED_DATE = "created";
	private static final String KEY_COLLECTIONID = "collectionid";

	
	public ChatDatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		String CREATE_CHAT_TABLE = "CREATE TABLE " + TABLE_CHAT + "("
//				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_UID + " TEXT,"
//				+ KEY_MESSAGE + " TEXT," + KEY_CREATED_DATE + " TEXT," + KEY_COLLECTIONID + " TEXT" +")";
//
//		db.execSQL(CREATE_CHAT_TABLE);
		
		  String CREATE_CHAT_TABLE = "CREATE TABLE " + TABLE_CHAT + "("
					+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_UID + " TEXT,"
					+ KEY_MESSAGE + " TEXT,"+ KEY_CREATED_DATE + " TEXT,"
					+ KEY_COLLECTIONID + " TEXT,"+ KEY_NICKNAME + " TEXT,"
					 + KEY_COLOR + " TEXT"+ ")";
	       db.execSQL(CREATE_CHAT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		String CREATE_TEMP_CHAT_TABLE = "CREATE TABLE " + TEMP_TABLE_CHAT + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_UID + " TEXT,"
				+ KEY_MESSAGE + " TEXT,"+ KEY_CREATED_DATE + " TEXT,"
				+ KEY_COLLECTIONID + " TEXT" + ")";
	       db.execSQL(CREATE_TEMP_CHAT_TABLE);

	       db.execSQL("INSERT INTO " + TEMP_TABLE_CHAT + " SELECT " +  KEY_ID + ", "
	         +  KEY_UID + ", " +  KEY_MESSAGE + ", " +  KEY_CREATED_DATE + ", " +  KEY_COLLECTIONID + " FROM " + TABLE_CHAT);

	       db.execSQL("DROP TABLE "+ TABLE_CHAT);
	     
	       String CREATE_CHAT_TABLE = "CREATE TABLE " + TABLE_CHAT + "("
					+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_UID + " TEXT,"
					+ KEY_MESSAGE + " TEXT,"+ KEY_CREATED_DATE + " TEXT,"
					+ KEY_COLLECTIONID + " TEXT,"+ KEY_NICKNAME + " TEXT,"
					 + KEY_COLOR + " TEXT"+ ")";
	       db.execSQL(CREATE_CHAT_TABLE);
	      
	       db.execSQL("INSERT INTO " + TABLE_CHAT +" SELECT " +  KEY_ID + ", "
	  	         +  KEY_UID + ", " +  KEY_MESSAGE + ", " +  KEY_CREATED_DATE + ", " +  KEY_COLLECTIONID + ", " 
	  	    		   +  "0" + ", " +"0"+ " FROM " + TEMP_TABLE_CHAT);
	       db.execSQL("DROP TABLE " + TEMP_TABLE_CHAT);
	}

	// group message
	public void addMessage(String uid, String mgs,
			String date,String collectionid,String nickName,String color) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_UID, uid);
		values.put(KEY_MESSAGE, mgs);
		values.put(KEY_CREATED_DATE, date);
		values.put(KEY_COLLECTIONID, collectionid);
		values.put(KEY_NICKNAME, nickName);
		values.put(KEY_COLOR, color);
		
		
		db.insert(TABLE_CHAT, null, values);
		db.close();
	}



	public boolean deleteMessage(String collectionid) {

		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(TABLE_CHAT, KEY_COLLECTIONID + "=" + collectionid, null) > 0;
	}
	
	public int getChatRowCount(String collectionid ) {
		String countQuery = "SELECT  * FROM " + TABLE_CHAT+" WHERE "+KEY_COLLECTIONID +" = '"+collectionid+"'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();

		return rowCount;
	}
	

	public ArrayList<HashMap<String, String>> getAllMessage(String collectionid) {

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		String query = "SELECT * FROM " + TABLE_CHAT+" WHERE "+KEY_COLLECTIONID +" = '"+collectionid+"'";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(KEY_UID,
						cursor.getString(cursor.getColumnIndex(KEY_UID)));
				map.put(KEY_MESSAGE,
						cursor.getString(cursor.getColumnIndex(KEY_MESSAGE)));
				map.put(KEY_CREATED_DATE,
						cursor.getString(cursor.getColumnIndex(KEY_CREATED_DATE)));
				
				map.put(KEY_NICKNAME,
						cursor.getString(cursor.getColumnIndex(KEY_NICKNAME)));
				map.put(KEY_COLOR,
						cursor.getString(cursor.getColumnIndex(KEY_COLOR)));
				
				list.add(map);
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		return list;
	}


	public void resetTables() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CHAT, null, null);
		db.close();
	}

}
