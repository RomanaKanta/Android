package com.smartmux.videodownloader.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.smartmux.videodownloader.modelclass.FileListModelClass;

public class VideoDataSource {
	private SQLiteDatabase db;
	private DataBaseHelper dbHelper;

	public VideoDataSource(Context context) {
		dbHelper = new DataBaseHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	// Adding new
	public long addNewVideo(FileListModelClass video) {
		open();
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.KEY_VIDEO_TITLE, video.getmFileName());
		values.put(DataBaseHelper.KEY_VIDEO_DURATION, video.getmFileDuration());
		values.put(DataBaseHelper.KEY_VIDEO_SIZE, video.getmFileSize());
		values.put(DataBaseHelper.KEY_VIDEO_DATE, video.getmFileDate());
		values.put(DataBaseHelper.KEY_VIDEO_TIME, video.getmFileTime());
		values.put(DataBaseHelper.KEY_VIDEO_URL, video.getmFileUrl());
	
		long inserted = db.insert(DataBaseHelper.TABLE_NAME_VIDEO, null, values);
		close();
		return inserted;
	}

	// delete data form database.
	public boolean deleteVideoData(Integer eId) {
		this.open();
		try {
			db.delete(DataBaseHelper.TABLE_NAME_VIDEO, DataBaseHelper.KEY_VIDEO_ID + "="
					+ eId, null);
		} catch (Exception ex) {
			Log.e("ERROR", "data not deleted");
			return false;
		}
		this.close();
		return true;
	}

	// update database by Id
	public long updateVideoData(Integer id, FileListModelClass video) {
		open();
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.KEY_VIDEO_TITLE, video.getmFileName());
		values.put(DataBaseHelper.KEY_VIDEO_DURATION, video.getmFileDuration());
		values.put(DataBaseHelper.KEY_VIDEO_SIZE, video.getmFileSize());
		values.put(DataBaseHelper.KEY_VIDEO_DATE, video.getmFileDate());
		values.put(DataBaseHelper.KEY_VIDEO_TIME, video.getmFileTime());
		values.put(DataBaseHelper.KEY_VIDEO_URL, video.getmFileUrl());
		long updated = 0;
		try {
			updated = db.update(DataBaseHelper.TABLE_NAME_VIDEO, values,
					DataBaseHelper.KEY_VIDEO_ID + "=" + id, null);
		} catch (Exception ex) {
			Log.e("ERROR", "data upgraion problem");
		}
		close();
		return updated;
	}

	// Getting All  list
	public ArrayList<FileListModelClass> getVideoList() {
		ArrayList<FileListModelClass> video_list = new ArrayList<FileListModelClass>();
		open();
		Cursor cursor = db.query(DataBaseHelper.TABLE_NAME_VIDEO, null, null, null,
				null, null, null);

		// looping through all rows and adding to list
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				int id = cursor.getInt(cursor
						.getColumnIndex(DataBaseHelper.KEY_VIDEO_ID));
				String name = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_VIDEO_TITLE));
				String duration = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_VIDEO_DURATION));
				String size = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_VIDEO_SIZE));
				String date = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_VIDEO_DATE));
				String time = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_VIDEO_TIME));
				String url = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_VIDEO_URL));

				FileListModelClass video = new FileListModelClass( id, name, duration, size,
						date,  time, url);
				
				video_list.add(video);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();

		// return user list
		return video_list;
	}

	// Getting  detail
	public FileListModelClass getVideoDetail(int id) {
		FileListModelClass video_detail;
		open();

		String selectQuery = "SELECT  * FROM " + DataBaseHelper.TABLE_NAME_VIDEO
				+ " WHERE " + DataBaseHelper.KEY_VIDEO_ID + "=" + id;

		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		String name = cursor.getString(cursor
				.getColumnIndex(DataBaseHelper.KEY_VIDEO_TITLE));
		String duration = cursor.getString(cursor
				.getColumnIndex(DataBaseHelper.KEY_VIDEO_DURATION));
		String size = cursor.getString(cursor
				.getColumnIndex(DataBaseHelper.KEY_VIDEO_SIZE));
		String date = cursor.getString(cursor
				.getColumnIndex(DataBaseHelper.KEY_VIDEO_DATE));
		String time = cursor.getString(cursor
				.getColumnIndex(DataBaseHelper.KEY_VIDEO_TIME));
		String url = cursor.getString(cursor
				.getColumnIndex(DataBaseHelper.KEY_VIDEO_URL));		
				
		 video_detail = new FileListModelClass( id, name, duration, size,
				date,  time, url);

		cursor.moveToNext();

		cursor.close();
		db.close();

		return video_detail;
	}

	public boolean isEmpty() {
		this.open();
		Cursor cursor = db.query(DataBaseHelper.TABLE_NAME_VIDEO, new String[] {
				DataBaseHelper.KEY_VIDEO_ID, DataBaseHelper.KEY_VIDEO_TITLE,
				DataBaseHelper.KEY_VIDEO_DURATION, DataBaseHelper.KEY_VIDEO_SIZE,
				DataBaseHelper.KEY_VIDEO_DATE, DataBaseHelper.KEY_VIDEO_TIME,
				DataBaseHelper.KEY_VIDEO_URL, }, null,
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
