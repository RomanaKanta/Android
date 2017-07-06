package com.smartmux.videodownloader.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.smartmux.videodownloader.modelclass.VisitedSiteListModelClass;

public class VisitedSiteDataSource {

	private SQLiteDatabase db;
	private DataBaseHelper dbHelper;
	private Context context;

	public VisitedSiteDataSource(Context context) {
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
	public long addNewSite(VisitedSiteListModelClass vsite) {
		open();
		ContentValues values = new ContentValues();
//		values.put(DataBaseHelper.KEY_VSITE_ID, vsite.getmVsiteId());
		values.put(DataBaseHelper.KEY_VSITE_TITLE,
				vsite.getmVsiteTitle());
		values.put(DataBaseHelper.KEY_VSITE_URL,
				vsite.getmVsiteUrl());
		values.put(DataBaseHelper.KEY_BOOKMARK,
				vsite.getmBookmark());

		long inserted = db.insert(DataBaseHelper.TABLE_NAME_VISITED_SITE,
				null, values);
		close();
		return inserted;
	}

	// delete data form database.
	public boolean deleteSitefromList(Integer eId) {
		this.open();
		try {
			db.delete(DataBaseHelper.TABLE_NAME_VISITED_SITE,
					DataBaseHelper.KEY_VSITE_ID + "=" + eId, null);
		} catch (Exception ex) {
			Log.e("ERROR", "data not deleted");
			return false;
		}
		this.close();
		return true;
	}

	// update database by Id
	public long updateSiteData(Integer id, VisitedSiteListModelClass vsite) {
		open();
		ContentValues values = new ContentValues();
		values.put(DataBaseHelper.KEY_VSITE_TITLE, vsite.getmVsiteTitle());
		values.put(DataBaseHelper.KEY_VSITE_URL,
				vsite.getmVsiteUrl());
		values.put(DataBaseHelper.KEY_BOOKMARK,
				vsite.getmBookmark());
		long updated = 0;
		try {
			updated = db.update(DataBaseHelper.TABLE_NAME_VISITED_SITE,
					values, DataBaseHelper.KEY_VSITE_ID + "=" + id, null);
		} catch (Exception ex) {
			Log.e("ERROR", "data upgraion problem");
		}
		close();
		return updated;
	}

	// Getting All list
	public ArrayList<VisitedSiteListModelClass> getSiteList() {
		ArrayList<VisitedSiteListModelClass> site_list = new ArrayList<VisitedSiteListModelClass>();
		open();
		Cursor cursor = db.query(DataBaseHelper.TABLE_NAME_VISITED_SITE, null,
				null, null, null, null, null);

		// looping through all rows and adding to list
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				int id = cursor.getInt(cursor
						.getColumnIndex(DataBaseHelper.KEY_VSITE_ID));

				String title = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_VSITE_TITLE));
				String vurl = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_VSITE_URL));
				String bookmark = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_BOOKMARK));

				VisitedSiteListModelClass folder = new VisitedSiteListModelClass(id, title, vurl, bookmark);
				site_list.add(folder);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();

		// return user list
		return site_list;
	}
	
	public ArrayList<VisitedSiteListModelClass> getBookmarkSiteList() {
		ArrayList<VisitedSiteListModelClass> site_list = new ArrayList<VisitedSiteListModelClass>();
		open();
		String selectQuery = "SELECT  * FROM "
				+ DataBaseHelper.TABLE_NAME_VISITED_SITE+ " WHERE "
						+ DataBaseHelper.KEY_BOOKMARK + "= 1" ;
		
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				int id = cursor.getInt(cursor
						.getColumnIndex(DataBaseHelper.KEY_VSITE_ID));

				String title = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_VSITE_TITLE));
				String vurl = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_VSITE_URL));
				String bookmark = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_BOOKMARK));

				VisitedSiteListModelClass folder = new VisitedSiteListModelClass(id, title, vurl, bookmark);
					site_list.add(folder);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();

		return site_list;
	}
	
	public int getBookmarkListCount(String bookmark) {
		ArrayList<VisitedSiteListModelClass> vsite = new ArrayList<VisitedSiteListModelClass>();
		open();
		String selectQuery = "SELECT  * FROM "
				+ DataBaseHelper.TABLE_NAME_VISITED_SITE + " WHERE "
				+ DataBaseHelper.KEY_BOOKMARK + "=" + bookmark;

		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				int id = cursor.getInt(cursor
						.getColumnIndex(DataBaseHelper.KEY_VSITE_ID));

				String title = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_VSITE_TITLE));
				String vurl = cursor.getString(cursor
						.getColumnIndex(DataBaseHelper.KEY_VSITE_URL));

				VisitedSiteListModelClass folder = new VisitedSiteListModelClass(id, title, vurl, bookmark);

				vsite.add(folder);
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();

		// return user list
		return vsite.size();
	}
	

	// Getting detail
	public VisitedSiteListModelClass getListItemDetail(int id) {
		VisitedSiteListModelClass vsite;
		open();

		String selectQuery = "SELECT  * FROM "
				+ DataBaseHelper.TABLE_NAME_VISITED_SITE + " WHERE "
				+ DataBaseHelper.KEY_VSITE_ID + "=" + id;

		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst();

		String title = cursor.getString(cursor
				.getColumnIndex(DataBaseHelper.KEY_VSITE_TITLE));
		String vurl = cursor.getString(cursor
				.getColumnIndex(DataBaseHelper.KEY_VSITE_URL));
		String bookmark = cursor.getString(cursor
				.getColumnIndex(DataBaseHelper.KEY_BOOKMARK));

		vsite = new VisitedSiteListModelClass(title, vurl, bookmark);

		cursor.moveToNext();

		cursor.close();
		db.close();

		return vsite;
	}

	public boolean isEmpty() {
		this.open();
		Cursor cursor = db.query(DataBaseHelper.TABLE_NAME_VISITED_SITE,
				new String[] { DataBaseHelper.KEY_VSITE_ID,
						DataBaseHelper.KEY_VSITE_TITLE,
						DataBaseHelper.KEY_VSITE_URL,
						DataBaseHelper.KEY_BOOKMARK, }, null, null, null,
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

	

