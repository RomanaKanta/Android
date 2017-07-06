package com.smartmux.videodownloader.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.smartmux.videodownloader.modelclass.PlayListModelClass;

public class FolderDataSource {


	private SQLiteDatabase db;
	private DataBaseHelper dbHelper;

	public FolderDataSource(Context context) {
		dbHelper = new DataBaseHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	// Adding new
	public long addNewFolder(PlayListModelClass vfile) {
		open();
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.KEY_FOLDER_NAME, vfile.getmPlayListName());
		values.put(DataBaseHelper.KEY_SONG_NUMBER, vfile.getmSong());
	
		long inserted = db.insert(DataBaseHelper.TABLE_NAME_FOLDER, null, values);
		close();
		return inserted;
	}

	// delete data form database.
	public boolean deleteFolder(Integer eId) {
		this.open();
		try {
			db.delete(DataBaseHelper.TABLE_NAME_FOLDER, DataBaseHelper.KEY_FOLDER_ID + "="
					+ eId, null);
		} catch (Exception ex) {
			Log.e("ERROR", "data not deleted");
			return false;
		}
		this.close();
		return true;
	}

	// update database by Id
	public long updateFolderData(Integer id, PlayListModelClass vfile) {
		open();
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.KEY_FOLDER_NAME, vfile.getmPlayListName());
		values.put(DataBaseHelper.KEY_SONG_NUMBER, vfile.getmSong());
		long updated = 0;
		try {
			updated = db.update(DataBaseHelper.TABLE_NAME_FOLDER, values,
					DataBaseHelper.KEY_FOLDER_ID + "=" + id, null);
		} catch (Exception ex) {
			Log.e("ERROR", "data upgraion problem");
		}
		close();
		return updated;
	}

	// Getting All  list
	public ArrayList<PlayListModelClass> getFolderList() {
		ArrayList<PlayListModelClass> video_list = new ArrayList<PlayListModelClass>();
		open();
		Cursor cursor = db.query(DataBaseHelper.TABLE_NAME_FOLDER, null, null, null,
				null, null, null);

		// looping through all rows and adding to list
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				int id = cursor.getInt(cursor
						.getColumnIndex(DataBaseHelper.KEY_FOLDER_ID));
				String folder_name = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_FOLDER_NAME));
				
				String song_no = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_SONG_NUMBER));
				

				PlayListModelClass folder = new PlayListModelClass( id,  folder_name,song_no);
				
				video_list.add(folder);
				cursor.moveToNext();
//				Log.d("PLAYFolder",""+id);
			}
		}
		cursor.close();
		db.close();

		// return user list
		return video_list;
	}

	// Getting  detail
	public PlayListModelClass getFolderDetail(int  id) {
		PlayListModelClass video_detail;
		open();

		String selectQuery = "SELECT  * FROM " + DataBaseHelper.TABLE_NAME_FOLDER
				+ " WHERE " + DataBaseHelper.KEY_FOLDER_ID + "=" + id;

		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		String folder_name = cursor.getString(cursor
				.getColumnIndex(DataBaseHelper.KEY_FOLDER_NAME));
		
		String song_no = cursor.getString(cursor
				.getColumnIndex(DataBaseHelper.KEY_SONG_NUMBER));

		 video_detail = new PlayListModelClass( id,  folder_name,song_no);

		cursor.moveToNext();

		cursor.close();
		db.close();

		return video_detail;
	}

	public boolean isEmpty() {
		this.open();
		Cursor cursor = db.query(DataBaseHelper.TABLE_NAME_FOLDER, new String[] {
				DataBaseHelper.KEY_FOLDER_ID, 
				DataBaseHelper.KEY_FOLDER_NAME, 
				DataBaseHelper.KEY_SONG_NUMBER,}, null,
				null, null, null, null);

		if (cursor.getCount() == 0) {
			this.close();
			return true;
		} else {
			this.close();
			return false;
		}
	}





}
