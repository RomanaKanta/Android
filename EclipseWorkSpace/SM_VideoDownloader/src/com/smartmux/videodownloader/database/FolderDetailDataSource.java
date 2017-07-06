package com.smartmux.videodownloader.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.smartmux.videodownloader.modelclass.FileListModelClass;

public class FolderDetailDataSource {

	private SQLiteDatabase db;
	private DataBaseHelper dbHelper;
	private Context context;

	public FolderDetailDataSource(Context context) {
		dbHelper = new DataBaseHelper(context);
		this.context=context;
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	// Adding new
	public long addNewVideoinFolder(FileListModelClass video_file) {
		open();
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.KEY_VFOLDER_ID, video_file.getmPlayListId());
		values.put(DataBaseHelper.KEY_VFOLDER_NAME,
				video_file.getmPlayListName());
		values.put(DataBaseHelper.KEY_FVIDEO_ID,
				video_file.getmFileId());
//		values.put(DataBaseHelper.KEY_FVIDEO_TITLE, video_file.getmFileName());
//		values.put(DataBaseHelper.KEY_FVIDEO_DURATION,
//				video_file.getmFileDuration());
//		values.put(DataBaseHelper.KEY_FVIDEO_SIZE, video_file.getmFileSize());
//		values.put(DataBaseHelper.KEY_FVIDEO_DATE, video_file.getmFileDate());
//		values.put(DataBaseHelper.KEY_FVIDEO_TIME, video_file.getmFileTime());
//		values.put(DataBaseHelper.KEY_FVIDEO_URL, video_file.getmFileUrl());

		long inserted = db.insert(DataBaseHelper.TABLE_NAME_FOLDER_DETAIL,
				null, values);
		close();
		return inserted;
	}

	// delete data form database.
	public boolean deleteVideoinFolderData(Integer eId) {
		this.open();
		try {
			db.delete(DataBaseHelper.TABLE_NAME_FOLDER_DETAIL,
					DataBaseHelper.KEY_ID + "=" + eId, null);
		} catch (Exception ex) {
			Log.e("ERROR", "data not deleted");
			return false;
		}
		this.close();
		return true;
	}

	// update database by Id
	public long updateFolderVideoData(Integer id, FileListModelClass video_file) {
		open();
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.KEY_VFOLDER_ID, video_file.getmPlayListId());
		values.put(DataBaseHelper.KEY_VFOLDER_NAME,
				video_file.getmPlayListName());
		values.put(DataBaseHelper.KEY_FVIDEO_ID,
				video_file.getmFileId());
//		values.put(DataBaseHelper.KEY_FVIDEO_TITLE, video_file.getmFileName());
//		values.put(DataBaseHelper.KEY_FVIDEO_DURATION,
//				video_file.getmFileDuration());
//		values.put(DataBaseHelper.KEY_FVIDEO_SIZE, video_file.getmFileSize());
//		values.put(DataBaseHelper.KEY_FVIDEO_DATE, video_file.getmFileDate());
//		values.put(DataBaseHelper.KEY_FVIDEO_TIME, video_file.getmFileTime());
//		values.put(DataBaseHelper.KEY_FVIDEO_URL, video_file.getmFileUrl());
		long updated = 0;
		try {
			updated = db.update(DataBaseHelper.TABLE_NAME_FOLDER_DETAIL,
					values, DataBaseHelper.KEY_ID + "=" + id, null);
		} catch (Exception ex) {
			Log.e("ERROR", "data upgraion problem");
		}
		close();
		return updated;
	}

	// Getting All list
	public ArrayList<FileListModelClass> getFolderVideoList() {
		ArrayList<FileListModelClass> video_list = new ArrayList<FileListModelClass>();
		open();
		Cursor cursor = db.query(DataBaseHelper.TABLE_NAME_FOLDER_DETAIL, null,
				null, null, null, null, null);

		// looping through all rows and adding to list
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				int id = cursor.getInt(cursor
						.getColumnIndex(DataBaseHelper.KEY_ID));

				int folderid = cursor.getInt(cursor
						.getColumnIndex(DataBaseHelper.KEY_VFOLDER_ID));
				String folder_name = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_VFOLDER_NAME));
				int vdoid = cursor.getInt(cursor
						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_ID));
//				String name = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_TITLE));
//				String duration = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_DURATION));
//				String size = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_SIZE));
//				String date = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_DATE));
//				String time = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_TIME));
//				String url = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_URL));

				FileListModelClass folder = new FileListModelClass(id,
						folderid, folder_name, vdoid);

				video_list.add(folder);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();

		// return user list
		return video_list;
	}
	
	public ArrayList<FileListModelClass> getSelectedFolderVdoList(int PFid) {
		ArrayList<FileListModelClass> video_list = new ArrayList<FileListModelClass>();
		open();
		String selectQuery = "SELECT  * FROM "
				+ DataBaseHelper.TABLE_NAME_FOLDER_DETAIL+ " WHERE "
						+ DataBaseHelper.KEY_VFOLDER_ID + "=" + PFid;
		
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				int id = cursor.getInt(cursor
						.getColumnIndex(DataBaseHelper.KEY_ID));
//
//				int folderid = cursor.getInt(cursor
//						.getColumnIndex(DataBaseHelper.KEY_VFOLDER_ID));
//				Log.d("Folder",""+PFid);
				String folder_name = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_VFOLDER_NAME));
				int vdoid = cursor.getInt(cursor
						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_ID));
//				String name = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_TITLE));
//				String duration = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_DURATION));
//				String size = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_SIZE));
//				String date = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_DATE));
//				String time = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_TIME));
//				String url = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_URL));

				FileListModelClass folder = new FileListModelClass(id, folder_name, vdoid);

				video_list.add(folder);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();

		return video_list;
	}
	
	public int getVideoCount(String folder_name) {
		ArrayList<FileListModelClass> video_list = new ArrayList<FileListModelClass>();
		open();
		String selectQuery = "SELECT  * FROM "
				+ DataBaseHelper.TABLE_NAME_FOLDER_DETAIL + " WHERE "
				+ DataBaseHelper.KEY_VFOLDER_NAME + "=" + folder_name;

		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				int id = cursor.getInt(cursor
						.getColumnIndex(DataBaseHelper.KEY_ID));

				int folderid = cursor.getInt(cursor
						.getColumnIndex(DataBaseHelper.KEY_VFOLDER_ID));
				int vdoid = cursor.getInt(cursor
						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_ID));
//				String name = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_TITLE));
//				String duration = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_DURATION));
//				String size = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_SIZE));
//				String date = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_DATE));
//				String time = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_TIME));
//				String url = cursor.getString(cursor
//						.getColumnIndex(DataBaseHelper.KEY_FVIDEO_URL));

				FileListModelClass folder = new FileListModelClass(id,
						folderid, folder_name,vdoid);

				video_list.add(folder);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();

		// return user list
		return video_list.size();
	}
	

	// Getting detail
	public FileListModelClass getFolderVideoDetail(int id) {
		FileListModelClass video_detail;
		open();

		String selectQuery = "SELECT  * FROM "
				+ DataBaseHelper.TABLE_NAME_FOLDER_DETAIL + " WHERE "
				+ DataBaseHelper.KEY_ID + "=" + id;

		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		int folderid = cursor.getInt(cursor
				.getColumnIndex(DataBaseHelper.KEY_VFOLDER_ID));
		String folder_name = cursor.getString(cursor
				.getColumnIndex(DataBaseHelper.KEY_VFOLDER_NAME));
		int vdoid = cursor.getInt(cursor
				.getColumnIndex(DataBaseHelper.KEY_FVIDEO_ID));
//		String name = cursor.getString(cursor
//				.getColumnIndex(DataBaseHelper.KEY_FVIDEO_TITLE));
//		String duration = cursor.getString(cursor
//				.getColumnIndex(DataBaseHelper.KEY_FVIDEO_DURATION));
//		String size = cursor.getString(cursor
//				.getColumnIndex(DataBaseHelper.KEY_FVIDEO_SIZE));
//		String date = cursor.getString(cursor
//				.getColumnIndex(DataBaseHelper.KEY_FVIDEO_DATE));
//		String time = cursor.getString(cursor
//				.getColumnIndex(DataBaseHelper.KEY_FVIDEO_TIME));
//		String url = cursor.getString(cursor
//				.getColumnIndex(DataBaseHelper.KEY_FVIDEO_URL));

		video_detail = new FileListModelClass(id, folderid, folder_name,vdoid);

		cursor.moveToNext();

		cursor.close();
		db.close();

		return video_detail;
	}

	public boolean isEmpty() {
		this.open();
		Cursor cursor = db.query(DataBaseHelper.TABLE_NAME_FOLDER_DETAIL,
				new String[] { DataBaseHelper.KEY_ID,
						DataBaseHelper.KEY_VFOLDER_ID,
						DataBaseHelper.KEY_VFOLDER_NAME,
						DataBaseHelper.KEY_FVIDEO_ID, }, null, null, null,
				null, null);

		if (cursor.getCount() == 0) {
			this.close();
			return true;
		} else {
			this.close();
			return false;
		}
	}

}
