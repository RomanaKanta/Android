package com.smartmux.videodownloader.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.smartmux.videodownloader.modelclass.DownloadListModelClass;

public class DownloadDataSource {

	private SQLiteDatabase db;
	private DataBaseHelper dbHelper;

	public DownloadDataSource(Context context) {
		dbHelper = new DataBaseHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	// Adding new
	public long addNewFolder(DownloadListModelClass down) {
		open();
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.KEY_DOWNLOAD_SERIAL_ID, down.getmSerialID());
		values.put(DataBaseHelper.KEY_DOWNLOAD_TITLE, down.getmRowTitle());
		values.put(DataBaseHelper.KEY_DOWNLOAD_SIZE, down.getmRowSize());
		values.put(DataBaseHelper.KEY_DOWNLOAD_PROGRESS, down.getmProgress());
	
		long inserted = db.insert(DataBaseHelper.TABLE_NAME_DOWNLOAD, null, values);
		close();
		return inserted;
	}

	// delete data form database.
	public boolean deleteFolder(Integer eId) {
		this.open();
		try {
			db.delete(DataBaseHelper.TABLE_NAME_DOWNLOAD, DataBaseHelper.KEY_DOWNLOAD_ID + "="
					+ eId, null);
		} catch (Exception ex) {
			Log.e("ERROR", "data not deleted");
			return false;
		}
		this.close();
		return true;
	}

	// update database by Id
	public long updateFolderData(Integer id, DownloadListModelClass down) {
		open();
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.KEY_DOWNLOAD_SERIAL_ID, down.getmSerialID());
		values.put(DataBaseHelper.KEY_DOWNLOAD_TITLE, down.getmRowTitle()); 
		values.put(DataBaseHelper.KEY_DOWNLOAD_SIZE, down.getmRowSize());
		values.put(DataBaseHelper.KEY_DOWNLOAD_PROGRESS, down.getmProgress());
		long updated = 0;
		try {
			updated = db.update(DataBaseHelper.TABLE_NAME_DOWNLOAD, values,
					DataBaseHelper.KEY_DOWNLOAD_ID + "=" + id, null);
		} catch (Exception ex) {
			Log.e("ERROR", "data upgraion problem");
		}
		close();
		return updated;
	}

	// Getting All  list
	public ArrayList<DownloadListModelClass> getFolderList() {
		ArrayList<DownloadListModelClass> down_list = new ArrayList<DownloadListModelClass>();
		open();
		Cursor cursor = db.query(DataBaseHelper.TABLE_NAME_DOWNLOAD, null, null, null,
				null, null, null);

		// looping through all rows and adding to list
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				int id = cursor.getInt(cursor
						.getColumnIndex(DataBaseHelper.KEY_DOWNLOAD_ID));
				int serial_id = cursor.getInt(cursor
						.getColumnIndex(DataBaseHelper.KEY_DOWNLOAD_SERIAL_ID));
				String name = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_DOWNLOAD_TITLE));
				String size = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_DOWNLOAD_SIZE));
				String progress = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_DOWNLOAD_PROGRESS));
				

				DownloadListModelClass folder = new DownloadListModelClass(id, serial_id, name, size, progress);
				
				down_list.add(folder);
				cursor.moveToNext();
				Log.d("PLAYFolder",""+id);
			}
		}
		cursor.close();
		db.close();

		// return user list
		return down_list;
	}

	// Getting  detail
	public DownloadListModelClass getFolderDetail(int serinal_id) {
		DownloadListModelClass video_detail;
		open();

		String selectQuery = "SELECT  * FROM " + DataBaseHelper.TABLE_NAME_DOWNLOAD
				+ " WHERE " + DataBaseHelper.KEY_DOWNLOAD_SERIAL_ID + "=" + serinal_id;

		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		int id = cursor.getInt(cursor
				.getColumnIndex(DataBaseHelper.KEY_DOWNLOAD_ID));
		String name = cursor.getString(cursor
				.getColumnIndex(DataBaseHelper.KEY_DOWNLOAD_TITLE));
		String size = cursor.getString(cursor
				.getColumnIndex(DataBaseHelper.KEY_DOWNLOAD_SIZE));
		String progress = cursor.getString(cursor
				.getColumnIndex(DataBaseHelper.KEY_DOWNLOAD_PROGRESS));

		 video_detail = new DownloadListModelClass(id, serinal_id, name, size, progress);

		cursor.moveToNext();

		cursor.close();
		db.close();

		return video_detail;
	}

	public boolean isEmpty() {
		this.open();
		Cursor cursor = db.query(DataBaseHelper.TABLE_NAME_DOWNLOAD, new String[] {
				DataBaseHelper.KEY_DOWNLOAD_ID, 
				DataBaseHelper.KEY_DOWNLOAD_SERIAL_ID, 
				DataBaseHelper.KEY_DOWNLOAD_TITLE, 
				DataBaseHelper.KEY_DOWNLOAD_SIZE, 
				DataBaseHelper.KEY_DOWNLOAD_PROGRESS,}, null,
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
