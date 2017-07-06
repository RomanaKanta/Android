package com.smartmux.videodownloader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "SM_VIDEO_DOWNLOADER_DB";
	
	
	/********************* DOWNLOAD TABLE************************/
	//table name
	public static final String TABLE_NAME_DOWNLOAD = "download_information";

	// Table Columns names
	public static final String KEY_DOWNLOAD_ID = "download_id";
	public static final String KEY_DOWNLOAD_SERIAL_ID = "download_serial_id";
	public static final String KEY_DOWNLOAD_TITLE = "download_title";
	public static final String KEY_DOWNLOAD_SIZE = "download_size";
	public static final String KEY_DOWNLOAD_PROGRESS = "download_progress";


	// table information
	public String CREATE_DOWNLOAD_TABLE = "create table " + TABLE_NAME_DOWNLOAD + "("
			+ KEY_DOWNLOAD_ID + " integer primary key autoincrement, " 
			+ KEY_DOWNLOAD_SERIAL_ID + " integer not null, " 
			+ KEY_DOWNLOAD_TITLE + " text not null, "
			+ KEY_DOWNLOAD_SIZE + " text not null, " 
			+ KEY_DOWNLOAD_PROGRESS + " text not null);";
	
	

	/********************* VIDEO TABLE************************/
	//table name
	public static final String TABLE_NAME_VIDEO = "video_information";

	// Table Columns names
	public static final String KEY_VIDEO_ID = "video_id";
	public static final String KEY_VIDEO_TITLE = "video_title";
	public static final String KEY_VIDEO_DATE = "video_date";
	public static final String KEY_VIDEO_TIME = "video_time";
	public static final String KEY_VIDEO_DURATION = "video_duration";
	public static final String KEY_VIDEO_SIZE = "video_size";
	public static final String KEY_VIDEO_URL = "video_url";


	// table information
	public String CREATE_VIDEO_TABLE = "create table " + TABLE_NAME_VIDEO + "("
			+ KEY_VIDEO_ID + " integer primary key autoincrement, " 
			+ KEY_VIDEO_TITLE + " text not null, " 
			+ KEY_VIDEO_DATE + " text not null, "
			+ KEY_VIDEO_TIME + " text not null, " 
			+ KEY_VIDEO_DURATION + " text not null, "
			+ KEY_VIDEO_SIZE + " text not null, " 
			+ KEY_VIDEO_URL + " text not null);";
	
	
	/********************* FOLDER DETAIL TABLE************************/
	// table name
	public static final String TABLE_NAME_FOLDER = "folder_table";

	// Table Columns names
	public static final String KEY_FOLDER_ID = "folder_id";
	public static final String KEY_FOLDER_NAME = "folder_name";
	public static final String KEY_SONG_NUMBER= "no_of_song";

	// table information
	public String CREATE_FOLDER_TABLE = "create table " + TABLE_NAME_FOLDER + "(" 
			+ KEY_FOLDER_ID + " integer primary key autoincrement, "
			+ KEY_FOLDER_NAME + " text not null, "
			+ KEY_SONG_NUMBER + " text not null);";


	/********************* FOLDER DETAIL TABLE************************/
	// table name
	public static final String TABLE_NAME_FOLDER_DETAIL = "folder_information";

	// Table Columns names
	public static final String KEY_ID = "fvideo_id";
	public static final String KEY_VFOLDER_ID = "vfolder_id";
	public static final String KEY_VFOLDER_NAME = "vfolder_name";
	public static final String KEY_FVIDEO_ID = "folder_video_id";
//	public static final String KEY_FVIDEO_TITLE = "fvideo_title";
//	public static final String KEY_FVIDEO_DATE = "fvideo_date";
//	public static final String KEY_FVIDEO_TIME = "fvideo_time";
//	public static final String KEY_FVIDEO_DURATION = "fvideo_duration";
//	public static final String KEY_FVIDEO_SIZE = "fvideo_size";
//	public static final String KEY_FVIDEO_URL = "fvideo_url";

	// table information
	public String CREATE_FOLDER_DETAIL_TABLE = "create table " + TABLE_NAME_FOLDER_DETAIL + "(" 
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_VFOLDER_ID + " integer not null, "
			+ KEY_VFOLDER_NAME + " text not null, "
			+ KEY_FVIDEO_ID + " integer not null);";
	
//	+ KEY_FVIDEO_TITLE + " text not null, " 
//	+ KEY_FVIDEO_DATE + " text not null, "
//	+ KEY_FVIDEO_TIME + " text not null, " 
//	+ KEY_FVIDEO_DURATION + " text not null, "
//	+ KEY_FVIDEO_SIZE + " text not null, " 


	/********************* VIDEO TABLE************************/
	//table name
	public static final String TABLE_NAME_VISITED_SITE = "visited_site";

	// Table Columns names
	public static final String KEY_VSITE_ID = "vsite_id";
	public static final String KEY_VSITE_TITLE = "vsite_title";
	public static final String KEY_VSITE_URL = "vsite_url";
	public static final String KEY_BOOKMARK = "bookmark";


	// table information
	public String CREATE_VSITE_TABLE = "create table " + TABLE_NAME_VISITED_SITE + "("
			+ KEY_VSITE_ID + " integer primary key autoincrement, " 
			+ KEY_VSITE_TITLE + " text not null, " 
			+ KEY_VSITE_URL + " text not null, " 
			+ KEY_BOOKMARK + " text not null);";
	
	
	/*********************** Create Database **********************/
	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/********************** Creating Tables *********************/
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DOWNLOAD_TABLE);
		db.execSQL(CREATE_VIDEO_TABLE);
		db.execSQL(CREATE_FOLDER_DETAIL_TABLE);
		db.execSQL(CREATE_FOLDER_TABLE);
		db.execSQL(CREATE_VSITE_TABLE);
	}

	/************************* Upgrading database **********************/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DataBaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");

		db.execSQL("DROP TABLE IF EXISTS " + CREATE_DOWNLOAD_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_VIDEO_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_FOLDER_DETAIL_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_FOLDER_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_VSITE_TABLE);
		onCreate(db);
	}

}
