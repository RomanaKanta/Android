package com.aircast.photobag.database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;

import com.aircast.photobag.model.PBHistoryEntryModel;
import com.aircast.photobag.model.PBHistoryPhotoModel;
import com.aircast.photobag.model.PBTimelineHistoryModel;
import com.aircast.photobag.model.PBTwitterFriendEntryModel;
import com.aircast.photobag.utils.PBGeneralUtils;

public class PBDatabaseManager {
	/** PBDatabaseManager instance */
	private static PBDatabaseManager _instance;

	/** Constuctor PBDatabaseManager */
	private PBDatabaseManager() {
	};

	/** the context */
	// private Context _context;
	/** database helper */
	private static PBDatabaseHelper _datastore;

	/**
	 * get instance class PBDatabaseManager
	 * 
	 * @param context
	 * @return PBDatabaseManager
	 */
	public static PBDatabaseManager getInstance(Context context) {
		if (_instance == null) {
			_instance = new PBDatabaseManager();
			_datastore = new PBDatabaseHelper(context);
		}
		return _instance;
	}

	/**
	 * get Cursor of friend list of Twitter that stored in db.
	 * 
	 * @return Cursor of Twitter friend list.
	 */
	public Cursor getTwitterFriendListCursor() {
		Cursor c = null;
		try {
			SQLiteDatabase db = _datastore.getReadableDatabase();

			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder.setTables(PBDatabaseDefinition.TwitterFriends.TABLE);
			String[] projectionIn = {
					PBDatabaseDefinition.TwitterFriends.C_TWITTER_SCREEN_NAME,
					PBDatabaseDefinition.TwitterFriends.C_TWITTER_NAME,
					PBDatabaseDefinition.TwitterFriends.C_TWITTER_URL };
			c = queryBuilder.query(db // db
					, projectionIn // projectionIn
					, null // selection
					, null // selectionArgs
					, null // groupBy
					, null // having
					, PBDatabaseDefinition.TwitterFriends.C_TWITTER_SCREEN_NAME
							+ " DESC"); // sortOrder
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return c;
	}

	/**
	 * get Cursor of history inbox/sent store db
	 * 
	 * @param entry
	 * @param type
	 * @return
	 */
	public Cursor getHistoriesCursor(int type) {
		Cursor c = null;
		try {
			SQLiteDatabase db = _datastore.getReadableDatabase();

			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder.setTables(PBDatabaseDefinition.HistoryData.TABLE);
			String[] projectionIn = {
					PBDatabaseDefinition.HistoryData.C_ID,
					PBDatabaseDefinition.HistoryData.C_COLECTION_ID,
					PBDatabaseDefinition.HistoryData.C_PASSWORD,
					PBDatabaseDefinition.HistoryData.C_CREATED_AT,
					PBDatabaseDefinition.HistoryData.C_CHARGES_AT,
					PBDatabaseDefinition.HistoryData.C_DOWNLOAD_COUNT,
					PBDatabaseDefinition.HistoryData.C_PHOTO_COUNT,
					PBDatabaseDefinition.HistoryData.C_THUMB,
					PBDatabaseDefinition.HistoryData.C_ADDIBILITY,
					PBDatabaseDefinition.HistoryData.C_EXTRA,
					PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE,
					PBDatabaseDefinition.HistoryData.C_UPDATED_AT,
					PBDatabaseDefinition.HistoryData.C_HONEY_NUM,
					PBDatabaseDefinition.HistoryData.C_MAPLE_NUM,
					PBDatabaseDefinition.HistoryData.C_SAVE_MARK,
					PBDatabaseDefinition.HistoryData.C_SAVE_DAYS,
					PBDatabaseDefinition.HistoryData.C_AD_LINK,
					PBDatabaseDefinition.HistoryData.C_IS_PUBLIC, // added by
																	// atik for
																	// koukaibukuro
					PBDatabaseDefinition.HistoryData.C_ACCEPTED,
					PBDatabaseDefinition.HistoryData.MEASSGE_COUNT,
					PBDatabaseDefinition.HistoryData.C_Expires_AT,
					PBDatabaseDefinition.HistoryData.C_ST_Digit };
			c = queryBuilder.query(db // db
					, projectionIn // projectionIn
					, PBDatabaseDefinition.HistoryData.C_TYPE + "=?" // selection
					, new String[] { String.valueOf(type) } // selectionArgs
					, null // groupBy
					, null // having
					, PBDatabaseDefinition.HistoryData.C_ID + " DESC"); // sortOrder

			return c;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		return c;
	}

	public ArrayList<HashMap<String, String>> getAllHistoriesCursor() {

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		// Select All Query
		String selectQuery = "SELECT  * FROM "
				+ PBDatabaseDefinition.HistoryData.TABLE;
		SQLiteDatabase db = _datastore.getReadableDatabase();
		try {

			Cursor cursor = db.rawQuery(selectQuery, null);
			try {
				// looping through all rows and adding to list
				if (cursor.moveToFirst()) {
					do {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(PBDatabaseDefinition.HistoryData.C_ID,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ID)));
						map.put(PBDatabaseDefinition.HistoryData.C_COLECTION_ID,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID)));
						map.put(PBDatabaseDefinition.HistoryData.C_PASSWORD,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PASSWORD)));
						map.put(PBDatabaseDefinition.HistoryData.C_CREATED_AT,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_CREATED_AT)));
						map.put(PBDatabaseDefinition.HistoryData.C_CHARGES_AT,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_CHARGES_AT)));
						map.put(PBDatabaseDefinition.HistoryData.C_DOWNLOAD_COUNT,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_DOWNLOAD_COUNT)));
						map.put(PBDatabaseDefinition.HistoryData.C_PHOTO_COUNT,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PHOTO_COUNT)));
						map.put(PBDatabaseDefinition.HistoryData.C_THUMB,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_THUMB)));
						map.put(PBDatabaseDefinition.HistoryData.C_ADDIBILITY,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ADDIBILITY)));
						map.put(PBDatabaseDefinition.HistoryData.C_EXTRA,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_EXTRA)));
						map.put(PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE)));
						map.put(PBDatabaseDefinition.HistoryData.C_UPDATED_AT,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_UPDATED_AT)));
						map.put(PBDatabaseDefinition.HistoryData.C_HONEY_NUM,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_HONEY_NUM)));
						map.put(PBDatabaseDefinition.HistoryData.C_MAPLE_NUM,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_MAPLE_NUM)));
						map.put(PBDatabaseDefinition.HistoryData.C_SAVE_MARK,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_SAVE_MARK)));
						map.put(PBDatabaseDefinition.HistoryData.C_SAVE_DAYS,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_SAVE_DAYS)));
						map.put(PBDatabaseDefinition.HistoryData.C_AD_LINK,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_AD_LINK)));
						map.put(PBDatabaseDefinition.HistoryData.C_TYPE,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_TYPE)));
						map.put(PBDatabaseDefinition.HistoryData.C_IS_PUBLIC,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_IS_PUBLIC)));
						map.put(PBDatabaseDefinition.HistoryData.C_ACCEPTED,
								cursor.getString(cursor
										.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ACCEPTED)));

						list.add(map);
					} while (cursor.moveToNext());
				}

			} finally {
				try {
					cursor.close();
				} catch (Exception ignore) {
				}
			}

		} finally {
			try {
				db.close();
			} catch (Exception ignore) {
			}
		}

		return list;
	}

	public Cursor getTimelineHistoryCursor() {
		Cursor c = null;
		try {
			SQLiteDatabase db = _datastore.getReadableDatabase();

			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder
					.setTables(PBDatabaseDefinition.TimelineHistoryData.TABLE);
			String[] projectionIn = {
					PBDatabaseDefinition.TimelineHistoryData.C_ID,
					PBDatabaseDefinition.TimelineHistoryData.C_CREATED_AT,
					PBDatabaseDefinition.TimelineHistoryData.C_TYPE,
					PBDatabaseDefinition.TimelineHistoryData.C_IS_NEW,
					PBDatabaseDefinition.TimelineHistoryData.C_DESCRIPTION, };
			c = queryBuilder.query(db // db
					, projectionIn // projectionIn
					, null // selection
					, null // selectionArgs
					, null // groupBy
					, null // having
					, PBDatabaseDefinition.TimelineHistoryData.C_CREATED_AT
							+ " DESC"); // sortOrder

			return c;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		return c;
	}

	/**
	 * get list history inbox/sent store db
	 * 
	 * @param entry
	 * @param type
	 * @return
	 */
	public ArrayList<PBHistoryEntryModel> getHistories(int type) {
		synchronized (this) {
			ArrayList<PBHistoryEntryModel> result = new ArrayList<PBHistoryEntryModel>();
			SQLiteDatabase db = null;
			try {
				db = _datastore.getReadableDatabase();
				result = __getHistories(db, type);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null)
					db.close();
			}
			return result;
		}
	}

	/**
	 * get list history inbox/sent store db
	 * 
	 * @param entry
	 * @param type
	 * @return
	 */
	public ArrayList<PBHistoryEntryModel> getAcceptedHistories(int type) {
		synchronized (this) {
			ArrayList<PBHistoryEntryModel> result = new ArrayList<PBHistoryEntryModel>();
			SQLiteDatabase db = null;
			try {
				db = _datastore.getReadableDatabase();
				result = __getAcceptedHistories(db, type);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null)
					db.close();
			}
			return result;
		}
	}

	/**
	 * get list history inbox/sent store db
	 * 
	 * @param entry
	 * @param type
	 * @return
	 */
	public ArrayList<String> getHistories1() {
		synchronized (this) {
			ArrayList<String> result = new ArrayList<String>();
			SQLiteDatabase db = null;
			String selectQuery = "SELECT  * FROM "
					+ PBDatabaseDefinition.HistoryData.TABLE;
			try {
				db = _datastore.getReadableDatabase();
				// result = __getHistories(db, type);
				Cursor cursor = db.rawQuery(selectQuery, null);
				try {

					// looping through all rows and adding to list
					if (cursor.moveToFirst()) {
						do {
							// MyObject obj = new MyObject();
							// only one column
							// obj.setId(cursor.getString(0));
							result.add(cursor.getString(1));
							// you could add additional columns here..

							// list.add(obj);
						} while (cursor.moveToNext());
					}

				} finally {
					try {
						cursor.close();
					} catch (Exception ignore) {
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null)
					db.close();
			}
			return result;
		}
	}

	public ArrayList<PBHistoryEntryModel> getHistoriesWithCollectionId(
			int type, String CollectionId) {
		synchronized (this) {
			ArrayList<PBHistoryEntryModel> result = new ArrayList<PBHistoryEntryModel>();
			SQLiteDatabase db = null;
			try {
				db = _datastore.getReadableDatabase();
				result = __getHistories(
						db,
						PBDatabaseDefinition.HistoryData.C_TYPE
								+ "=?"
								+ " AND "
								+ PBDatabaseDefinition.HistoryData.C_COLECTION_ID
								+ "=?", new String[] { String.valueOf(type),
								CollectionId }, null, null,
						PBDatabaseDefinition.HistoryData.C_ID + " DESC");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null)
					db.close();
			}
			return result;
		}
	}

	/**
	 * support get list history inbox/sent store db
	 * 
	 * @param db
	 * @param type
	 * @return
	 */
	private ArrayList<PBHistoryEntryModel> __getHistories(
			final SQLiteDatabase db, int type) {
		return __getHistories(db, PBDatabaseDefinition.HistoryData.C_TYPE
				+ "=?", new String[] { String.valueOf(type) }, null, null,
				PBDatabaseDefinition.HistoryData.C_ID + " DESC");
	}

	/**
	 * rifat add support get list history inbox/sent store db
	 * 
	 * @param db
	 * @param type
	 * @return
	 */
	private ArrayList<PBHistoryEntryModel> __getAcceptedHistories(
			final SQLiteDatabase db, int type) {
		return __getAcceptedHistories(db,
				PBDatabaseDefinition.HistoryData.C_TYPE + "=?" + " AND "
						+ PBDatabaseDefinition.HistoryData.C_ACCEPTED + "= 1",
				new String[] { String.valueOf(type) }, null, null,
				PBDatabaseDefinition.HistoryData.C_ID + " DESC");
	}

	private ArrayList<PBHistoryEntryModel> __getAcceptedHistories(
			final SQLiteDatabase db, String selection, String[] selectionArgs,
			String groupBy, String having, String sortOrder) {
		ArrayList<PBHistoryEntryModel> result = new ArrayList<PBHistoryEntryModel>();

		Cursor c = null;
		try {
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder.setTables(PBDatabaseDefinition.HistoryData.TABLE);
			String[] projectionIn = { PBDatabaseDefinition.HistoryData.C_ID,
					PBDatabaseDefinition.HistoryData.C_COLECTION_ID,
					PBDatabaseDefinition.HistoryData.C_PASSWORD,
					PBDatabaseDefinition.HistoryData.C_CREATED_AT,
					PBDatabaseDefinition.HistoryData.C_CHARGES_AT,
					PBDatabaseDefinition.HistoryData.C_DOWNLOAD_COUNT,
					PBDatabaseDefinition.HistoryData.C_PHOTO_COUNT,
					PBDatabaseDefinition.HistoryData.C_THUMB,
					PBDatabaseDefinition.HistoryData.C_ADDIBILITY,
					PBDatabaseDefinition.HistoryData.C_EXTRA,
					PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE,
					PBDatabaseDefinition.HistoryData.C_UPDATED_AT,
					PBDatabaseDefinition.HistoryData.C_HONEY_NUM,
					PBDatabaseDefinition.HistoryData.C_MAPLE_NUM,
					PBDatabaseDefinition.HistoryData.C_SAVE_MARK,
					PBDatabaseDefinition.HistoryData.C_SAVE_DAYS };
			c = queryBuilder.query(db, projectionIn, selection, selectionArgs,
					groupBy, having, sortOrder);

			if (c != null && !c.isClosed() && c.moveToFirst()) {
				do {
					PBHistoryEntryModel entry = new PBHistoryEntryModel(
							Long.parseLong(c.getString(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ID))),
							c.getString(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID)),
							c.getString(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PASSWORD)),
							c.getLong(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_CREATED_AT)),
							c.getLong(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_CHARGES_AT)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PHOTO_COUNT)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_DOWNLOAD_COUNT)),
							c.getString(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_THUMB)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ADDIBILITY)),
							c.getString(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_EXTRA)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE)),
							c.getLong(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_UPDATED_AT)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_HONEY_NUM)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_MAPLE_NUM)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_SAVE_MARK)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_SAVE_DAYS)));
					result.add(entry);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null && !c.isClosed())
				c.close();
		}

		return result;
	}

	private ArrayList<PBHistoryEntryModel> __getHistories(
			final SQLiteDatabase db, String selection, String[] selectionArgs,
			String groupBy, String having, String sortOrder) {
		ArrayList<PBHistoryEntryModel> result = new ArrayList<PBHistoryEntryModel>();

		Cursor c = null;
		try {
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder.setTables(PBDatabaseDefinition.HistoryData.TABLE);
			String[] projectionIn = { PBDatabaseDefinition.HistoryData.C_ID,
					PBDatabaseDefinition.HistoryData.C_COLECTION_ID,
					PBDatabaseDefinition.HistoryData.C_PASSWORD,
					PBDatabaseDefinition.HistoryData.C_CREATED_AT,
					PBDatabaseDefinition.HistoryData.C_CHARGES_AT,
					PBDatabaseDefinition.HistoryData.C_DOWNLOAD_COUNT,
					PBDatabaseDefinition.HistoryData.C_PHOTO_COUNT,
					PBDatabaseDefinition.HistoryData.C_THUMB,
					PBDatabaseDefinition.HistoryData.C_ADDIBILITY,
					PBDatabaseDefinition.HistoryData.C_EXTRA,
					PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE,
					PBDatabaseDefinition.HistoryData.C_UPDATED_AT,
					PBDatabaseDefinition.HistoryData.C_HONEY_NUM,
					PBDatabaseDefinition.HistoryData.C_MAPLE_NUM,
					PBDatabaseDefinition.HistoryData.C_SAVE_MARK,
					PBDatabaseDefinition.HistoryData.C_SAVE_DAYS };
			c = queryBuilder.query(db, projectionIn, selection, selectionArgs,
					groupBy, having, sortOrder);

			if (c != null && !c.isClosed() && c.moveToFirst()) {
				do {
					PBHistoryEntryModel entry = new PBHistoryEntryModel(
							Long.parseLong(c.getString(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ID))),
							c.getString(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID)),
							c.getString(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PASSWORD)),
							c.getLong(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_CREATED_AT)),
							c.getLong(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_CHARGES_AT)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PHOTO_COUNT)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_DOWNLOAD_COUNT)),
							c.getString(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_THUMB)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ADDIBILITY)),
							c.getString(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_EXTRA)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE)),
							c.getLong(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_UPDATED_AT)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_HONEY_NUM)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_MAPLE_NUM)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_SAVE_MARK)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_SAVE_DAYS)));
					result.add(entry);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null && !c.isClosed())
				c.close();
		}

		return result;
	}

	public void insertTwitter(PBTwitterFriendEntryModel entry) {
		synchronized (this) {
			SQLiteDatabase db = null;
			if (_datastore == null) {
				return;
			}
			try {
				db = _datastore.getWritableDatabase();
				db.beginTransaction();
				{
					ContentValues values = new ContentValues();
					values.put(
							PBDatabaseDefinition.TwitterFriends.C_TWITTER_SCREEN_NAME,
							entry.getTwitterScreenName());
					values.put(
							PBDatabaseDefinition.TwitterFriends.C_TWITTER_NAME,
							entry.getTwitterName());
					values.put(
							PBDatabaseDefinition.TwitterFriends.C_TWITTER_URL,
							entry.getProfileIconUrl());
					db.replaceOrThrow(
							PBDatabaseDefinition.TwitterFriends.TABLE, null,
							values);
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null) {
					db.endTransaction();
					db.close();
				}
			}
		}
	}

	/**
	 * add hitory(indox/sent) data get from server to update local storage
	 * 
	 * @param entry
	 * @param type
	 */
	public void insertHistory(PBHistoryEntryModel entry, int type) {
		synchronized (this) {
			SQLiteDatabase db = null;
			try {
				db = _datastore.getWritableDatabase();
				db.beginTransaction();

				{
					ContentValues values = new ContentValues();
					values.put(PBDatabaseDefinition.HistoryData.C_ID,
							entry.getEntryId());
					values.put(PBDatabaseDefinition.HistoryData.C_COLECTION_ID,
							entry.getCollectionId());
					values.put(PBDatabaseDefinition.HistoryData.C_PASSWORD,
							entry.getEntryPassword());
					values.put(PBDatabaseDefinition.HistoryData.C_CREATED_AT,
							entry.getEntryCreateDate());
					values.put(PBDatabaseDefinition.HistoryData.C_CHARGES_AT,
							entry.getEntryChargeDate());
					values.put(
							PBDatabaseDefinition.HistoryData.C_DOWNLOAD_COUNT,
							entry.getEntryNumofDownload());
					values.put(PBDatabaseDefinition.HistoryData.C_PHOTO_COUNT,
							entry.getEntryNumOfPhoto());
					if (!TextUtils.isEmpty(entry.getEntryThump())) {
						values.put(PBDatabaseDefinition.HistoryData.C_THUMB,
								entry.getEntryThump());
					}
					values.put(PBDatabaseDefinition.HistoryData.C_ADDIBILITY,
							entry.getEntryAddibility());
					values.put(PBDatabaseDefinition.HistoryData.C_EXTRA,
							entry.getEntryExtra());
					values.put(PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE,
							entry.getEntryIsUpdatable());
					values.put(PBDatabaseDefinition.HistoryData.C_UPDATED_AT,
							entry.getEntryUpdatedAt());
					values.put(PBDatabaseDefinition.HistoryData.C_TYPE, type);

					values.put(PBDatabaseDefinition.HistoryData.C_HONEY_NUM,
							entry.getEntryHoneyUsed());
					values.put(PBDatabaseDefinition.HistoryData.C_MAPLE_NUM,
							entry.getEntryMapleUsed());
					values.put(PBDatabaseDefinition.HistoryData.C_SAVE_MARK,
							entry.getEntrySaveMark());
					values.put(PBDatabaseDefinition.HistoryData.C_SAVE_DAYS,
							entry.getEntrySaveDays());
					values.put(PBDatabaseDefinition.HistoryData.C_AD_LINK,
							entry.getEntryAdLink());
					values.put(PBDatabaseDefinition.HistoryData.C_IS_PUBLIC,
							entry.getIsPublic());
					values.put(PBDatabaseDefinition.HistoryData.C_ACCEPTED,
							entry.getAccepted());

					values.put(PBDatabaseDefinition.HistoryData.C_ST_Digit,
							entry.getFourDigit());

					db.replaceOrThrow(PBDatabaseDefinition.HistoryData.TABLE,
							null, values);
				}

				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null) {
					db.endTransaction();
					db.close();
				}
			}
		}
	}

	/**
	 * update hitory(indox/sent) data get from server to update local storage
	 * 
	 * @param entry
	 * @param type
	 */
	public int setHistory(PBHistoryEntryModel entry, int type) {

		int updatedCount = 0;
		synchronized (this) {
			SQLiteDatabase db = null;

			try {
				db = _datastore.getWritableDatabase();

				String password = entry.getEntryPassword();
				Cursor cursor = null;
				try {

					boolean exists = false;
					cursor = db
							.rawQuery(
									"SELECT 1 FROM "
											+ PBDatabaseDefinition.HistoryData.TABLE
											+ " WHERE "
											+ PBDatabaseDefinition.HistoryData.C_COLECTION_ID
											+ "=?"
											+ " AND "
											+ PBDatabaseDefinition.HistoryData.C_TYPE
											+ "=?",
									new String[] {
											entry.getCollectionId(),
											String.valueOf(type) });
					exists = cursor.moveToFirst();

					if (exists) {

						String query = "SELECT "
								+ PBDatabaseDefinition.HistoryData.C_PASSWORD
								+ " FROM "
								+ PBDatabaseDefinition.HistoryData.TABLE
								+ " WHERE "
								+ PBDatabaseDefinition.HistoryData.C_COLECTION_ID
								+ "=?" + " AND "
								+ PBDatabaseDefinition.HistoryData.C_TYPE
								+ "=?";
						cursor = db.rawQuery(
								query,
								new String[] { entry.getCollectionId(),
										String.valueOf(type) });

						if (cursor != null && cursor.moveToFirst()) {

							password = cursor
									.getString(cursor
											.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PASSWORD));
						}

					}

					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (cursor != null && !cursor.isClosed())
						cursor.close();
				}

				entry.setEntryPassword(password);

				db.beginTransaction();

				{
					ContentValues values = new ContentValues();
					// values.put(PBDatabaseDefinition.HistoryData.C_COLECTION_ID,
					// entry.getCollectionId());
					// values.put(PBDatabaseDefinition.HistoryData.C_PASSWORD,
					// entry.getEntryPassword());
					values.put(PBDatabaseDefinition.HistoryData.C_CREATED_AT,
							entry.getEntryCreateDate());
					values.put(PBDatabaseDefinition.HistoryData.C_CHARGES_AT,
							entry.getEntryChargeDate());
					values.put(
							PBDatabaseDefinition.HistoryData.C_DOWNLOAD_COUNT,
							entry.getEntryNumofDownload());
					values.put(PBDatabaseDefinition.HistoryData.C_PHOTO_COUNT,
							entry.getEntryNumOfPhoto());
					values.put(PBDatabaseDefinition.HistoryData.C_ADDIBILITY,
							entry.getEntryAddibility());
					values.put(PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE,
							entry.getEntryIsUpdatable());
					values.put(PBDatabaseDefinition.HistoryData.C_UPDATED_AT,
							entry.getEntryUpdatedAt());
					values.put(PBDatabaseDefinition.HistoryData.C_HONEY_NUM,
							entry.getEntryHoneyUsed());
					values.put(PBDatabaseDefinition.HistoryData.C_MAPLE_NUM,
							entry.getEntryMapleUsed());
					values.put(PBDatabaseDefinition.HistoryData.C_SAVE_MARK,
							entry.getEntrySaveMark());
					values.put(PBDatabaseDefinition.HistoryData.C_SAVE_DAYS,
							entry.getEntrySaveDays());

					values.put(PBDatabaseDefinition.HistoryData.C_IS_PUBLIC,
							entry.getIsPublic());

					values.put(PBDatabaseDefinition.HistoryData.C_ACCEPTED,
							entry.getAccepted());
					values.put(PBDatabaseDefinition.HistoryData.MEASSGE_COUNT,
							entry.getmMessageCount());

					values.put(PBDatabaseDefinition.HistoryData.C_Expires_AT,
							entry.getmExpiresAt());

					values.put(PBDatabaseDefinition.HistoryData.C_ST_Digit,
							entry.getFourDigit());

					updatedCount = db
							.update(PBDatabaseDefinition.HistoryData.TABLE,
									values,
									PBDatabaseDefinition.HistoryData.C_COLECTION_ID
											+ "=? AND "
											+ PBDatabaseDefinition.HistoryData.C_PASSWORD
											+ "=? AND "
											+ PBDatabaseDefinition.HistoryData.C_TYPE
											+ "=?",
									new String[] { entry.getCollectionId(),
											entry.getEntryPassword(),
											String.format("%d", type) });
				}

				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					db.endTransaction();
					db.close();
					db = null;
				} catch (Exception e) {
				}
			}
		}

		return updatedCount;
	}

	// return whether a collection id which is given as collectionId exists on
	// the db
	public boolean isExistItemHistory(SQLiteDatabase db, String collectionId) {
		synchronized (this) {
			Cursor dataCount = null;
			try {
				String sql = "select count(*) from "
						+ PBDatabaseDefinition.HistoryData.TABLE + " where "
						+ PBDatabaseDefinition.HistoryData.C_COLECTION_ID
						+ "=?";
				dataCount = db.rawQuery(sql, new String[] { collectionId });
				// fix check null
				if (dataCount == null)
					return false;

				dataCount.moveToFirst();
				int jcount = dataCount.getInt(0);
				if (jcount > 0)
					return true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dataCount != null) {
					dataCount.close();
				}
			}
			return false;
		}
	}

	/**
	 * delete history(inbox/sent) at local storage
	 * 
	 * @param historyId
	 */
	public void deleteHistory(String historyId, String collectionId) {
		synchronized (this) {
			SQLiteDatabase db = null;
			Cursor c = null;
			try {
				db = _datastore.getWritableDatabase();

				try {
					db.beginTransaction();

					db.delete(PBDatabaseDefinition.HistoryData.TABLE,
							PBDatabaseDefinition.HistoryData.C_ID + " = ? ",
							new String[] { historyId });

					// remove photos of the collection id if there is no another
					// bag with the collection id
					if ((collectionId != null)
							&& !isExistItemHistory(db, collectionId)) {
						SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

						queryBuilder
								.setTables(PBDatabaseDefinition.HistoryPhoto.TABLE);
						String[] projectionIn = {
								PBDatabaseDefinition.HistoryPhoto.C_PHOTO_URL,
								PBDatabaseDefinition.HistoryPhoto.C_PHOTO_THUMB };
						c = queryBuilder.query(
								db // db
								,
								projectionIn // projectionIn
								,
								PBDatabaseDefinition.HistoryPhoto.C_HISTORY_ID
										+ " = ?" // selection
								, new String[] { String.valueOf(collectionId) } // selectionArgs
								, null // groupBy
								, null // having
								, null); // sortOrder
						// fix check null
						if (c != null && c.moveToFirst()) {
							do {
								String filePathUrl = c
										.getString(c
												.getColumnIndex(PBDatabaseDefinition.HistoryPhoto.C_PHOTO_URL));
								PBGeneralUtils.deleteCacheFile(filePathUrl);

								String filePathThump = c
										.getString(c
												.getColumnIndex(PBDatabaseDefinition.HistoryPhoto.C_PHOTO_THUMB));
								PBGeneralUtils.deleteCacheFile(filePathThump);
							} while (c.moveToNext());
						}

						db.delete(PBDatabaseDefinition.HistoryPhoto.TABLE,
								PBDatabaseDefinition.HistoryPhoto.C_HISTORY_ID
										+ " = ? ",
								new String[] { collectionId });
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (c != null)
						c.close();
				}

				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null) {
					db.endTransaction();
					db.close();
				}
			}
		}
	}

	/**
	 * delete history(inbox/sent) at local storage
	 * 
	 * @param historyId
	 */
	public void deletePhotos(String collectionId, ArrayList<String> list) {
		synchronized (this) {
			SQLiteDatabase db = null;
			Cursor c = null;
			try {
				db = _datastore.getWritableDatabase();

				try {
					db.beginTransaction();

					if (collectionId != null) {
						SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

						queryBuilder
								.setTables(PBDatabaseDefinition.HistoryPhoto.TABLE);
						String[] projectionIn = {
								PBDatabaseDefinition.HistoryPhoto.C_PHOTO_URL,
								PBDatabaseDefinition.HistoryPhoto.C_PHOTO_THUMB };
						c = queryBuilder.query(db, projectionIn,
								PBDatabaseDefinition.HistoryPhoto.C_HISTORY_ID
										+ " = ?",
								new String[] { String.valueOf(collectionId) },
								null, null, null);
						// fix check null
						if (c != null && c.moveToFirst()) {
							do {
								String filePathUrl = c
										.getString(c
												.getColumnIndex(PBDatabaseDefinition.HistoryPhoto.C_PHOTO_URL));

								if (!list.contains(filePathUrl)) {
									continue;
								}
								PBGeneralUtils.deleteCacheFile(filePathUrl);

								String filePathThump = c
										.getString(c
												.getColumnIndex(PBDatabaseDefinition.HistoryPhoto.C_PHOTO_THUMB));
								PBGeneralUtils.deleteCacheFile(filePathThump);

								db.delete(
										PBDatabaseDefinition.HistoryPhoto.TABLE,
										PBDatabaseDefinition.HistoryPhoto.C_PHOTO_URL
												+ " = ? ",
										new String[] { filePathUrl });
							} while (c.moveToNext());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (c != null)
						c.close();
				}

				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					db.endTransaction();
					db.close();
					db = null;
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * get list photos of history item store db
	 * 
	 * @param entry
	 * @param type
	 * @return ArrayList<PBHistoryPhotoModel>
	 */
	public ArrayList<PBHistoryPhotoModel> getPhotos(String historyId) {
		synchronized (this) {
			ArrayList<PBHistoryPhotoModel> result = new ArrayList<PBHistoryPhotoModel>();
			Cursor c = null;
			SQLiteDatabase db = null;
			try {
				db = _datastore.getReadableDatabase();
				result = __getPhotos(db, historyId);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (c != null)
					c.close();
				if (db != null)
					db.close();
			}
			return result;
		}
	}

	/**
	 * support get list history inbox/sent store db
	 * 
	 * @param db
	 * @param type
	 * @return ArrayList<PBHistoryPhotoModel>
	 */
	private ArrayList<PBHistoryPhotoModel> __getPhotos(final SQLiteDatabase db,
			String historyId) {
		ArrayList<PBHistoryPhotoModel> result = new ArrayList<PBHistoryPhotoModel>();

		Cursor c = null;
		try {
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder.setTables(PBDatabaseDefinition.HistoryPhoto.TABLE);
			String[] projectionIn = { PBDatabaseDefinition.HistoryPhoto.C_ID,
					PBDatabaseDefinition.HistoryPhoto.C_HISTORY_ID,
					PBDatabaseDefinition.HistoryPhoto.C_PHOTO_THUMB,
					PBDatabaseDefinition.HistoryPhoto.C_PHOTO_URL,
					PBDatabaseDefinition.HistoryPhoto.C_MEDIA_TYPE,
					PBDatabaseDefinition.HistoryPhoto.C_MEDIA_DURATION };
			c = queryBuilder.query(db // db
					, projectionIn // projectionIn
					, PBDatabaseDefinition.HistoryPhoto.C_HISTORY_ID + " = ?"// selection
					, new String[] { historyId } // selectionArgs
					, null // groupBy
					, null // having
					, null);
			// , PBDatabaseDefinition.HistoryPhoto.C_PHOTO_URL + " ASC"); //
			// sortOrder
			// fix check null
			if (c != null && c.moveToFirst()) {
				do {
					PBHistoryPhotoModel entry = new PBHistoryPhotoModel(
							c.getString(c
									.getColumnIndex(PBDatabaseDefinition.HistoryPhoto.C_PHOTO_URL)),
							c.getString(c
									.getColumnIndex(PBDatabaseDefinition.HistoryPhoto.C_PHOTO_THUMB)),
							c.getString(c
									.getColumnIndex(PBDatabaseDefinition.HistoryPhoto.C_HISTORY_ID)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryPhoto.C_MEDIA_TYPE)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.HistoryPhoto.C_MEDIA_DURATION)));

					result.add(entry);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null)
				c.close();
		}

		return result;
	}

	/**
	 * add/ update hitory(indox/sent) data get from server to update local
	 * storage
	 * 
	 * @param entry
	 * @param type
	 */
	public void setPhoto(PBHistoryPhotoModel entry, int type) {
		synchronized (this) {
			SQLiteDatabase db = null;
			try {
				db = _datastore.getWritableDatabase();
				db.beginTransaction();

				ContentValues values = new ContentValues();
				values.put(PBDatabaseDefinition.HistoryPhoto.C_ID,
						Integer.toString(entry.getPhoto().hashCode()));
				values.put(PBDatabaseDefinition.HistoryPhoto.C_HISTORY_ID,
						entry.getHistoryId());
				values.put(PBDatabaseDefinition.HistoryPhoto.C_PHOTO_URL,
						entry.getPhoto());
				values.put(PBDatabaseDefinition.HistoryPhoto.C_PHOTO_THUMB,
						entry.getThumb());
				values.put(PBDatabaseDefinition.HistoryPhoto.C_MEDIA_TYPE,
						entry.getMediaType());
				if (entry.getVideoDuration() > 0) {
					values.put(
							PBDatabaseDefinition.HistoryPhoto.C_MEDIA_DURATION,
							entry.getVideoDuration());
				} else {
					values.put(
							PBDatabaseDefinition.HistoryPhoto.C_MEDIA_DURATION,
							0);
				}
				db.replaceOrThrow(PBDatabaseDefinition.HistoryPhoto.TABLE,
						null, values);

				db.setTransactionSuccessful();
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				if (db != null) {
					db.endTransaction();
					db.close();
				}
			}
		}
	}

	/**
	 * delete Photos of history(inbox/sent) item at local storage
	 * 
	 * @param historyId
	 */
	public void deletePhotos(String historyId) {
		synchronized (this) {
			SQLiteDatabase db = null;
			Cursor c = null;
			try {
				db = _datastore.getWritableDatabase();

				try {
					SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

					queryBuilder
							.setTables(PBDatabaseDefinition.HistoryPhoto.TABLE);
					String[] projectionIn = { PBDatabaseDefinition.HistoryPhoto.C_ID };
					c = queryBuilder.query(db // db
							, projectionIn // projectionIn
							, PBDatabaseDefinition.HistoryPhoto.C_HISTORY_ID
									+ " = ?" // selection
							, new String[] { String.valueOf(historyId) } // selectionArgs
							, null // groupBy
							, null // having
							, null); // sortOrder
					// fix check null
					if (c != null && c.moveToFirst()) {
						do {

						} while (c.moveToNext());
					}

					db.delete(PBDatabaseDefinition.HistoryPhoto.TABLE,
							PBDatabaseDefinition.HistoryPhoto.C_HISTORY_ID
									+ " = ? ", new String[] { historyId });
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (c != null)
						c.close();
				}

				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					db.endTransaction();
					db.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * delete single photo at local storage and database
	 * 
	 * @param photoUrl
	 */
	public void deleteSinglePhoto(String photoUrl) {
		synchronized (this) {
			SQLiteDatabase db = null;
			try {
				PBGeneralUtils.deleteCacheFile(photoUrl);

				db = _datastore.getWritableDatabase();

				db.beginTransaction();

				db.delete(
						PBDatabaseDefinition.HistoryPhoto.TABLE,
						PBDatabaseDefinition.HistoryPhoto.C_PHOTO_URL + " = ? ",
						new String[] { photoUrl });

				db.setTransactionSuccessful();
			} catch (Exception e) {
				return;
			} finally {
				if (db != null) {
					db.endTransaction();
					db.close();
				}
			}
		}
	}

	public PBTimelineHistoryModel getNewestTimelineHistory() {
		synchronized (this) {
			ArrayList<PBTimelineHistoryModel> result = new ArrayList<PBTimelineHistoryModel>();
			SQLiteDatabase db = null;
			try {
				db = _datastore.getReadableDatabase();
				String selection = null;
				String[] selectionArgs = null;
				String groupBy = null;
				String having = null;
				String sortOrder = PBDatabaseDefinition.TimelineHistoryData.C_ID
						+ " DESC";

				result = __getTimelineHistoryModel(db, selection,
						selectionArgs, groupBy, having, sortOrder, 1);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null)
					db.close();
			}
			if (result.size() > 0)
				return result.get(0);
			else
				return null;

		}
	}

	public PBTimelineHistoryModel getOldestTimelineHistory() {
		synchronized (this) {
			ArrayList<PBTimelineHistoryModel> result = new ArrayList<PBTimelineHistoryModel>();
			SQLiteDatabase db = null;
			try {
				db = _datastore.getReadableDatabase();
				String selection = null;
				String[] selectionArgs = null;
				String groupBy = null;
				String having = null;
				String sortOrder = PBDatabaseDefinition.TimelineHistoryData.C_ID
						+ " ASC";

				result = __getTimelineHistoryModel(db, selection,
						selectionArgs, groupBy, having, sortOrder, 1);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null)
					db.close();
			}
			if (result.size() > 0)
				return result.get(0);
			else
				return null;

		}
	}

	public int getTotalNewDonguri() {
		synchronized (this) {
			Cursor c = null;
			SQLiteDatabase db = null;
			try {
				db = _datastore.getReadableDatabase();
				String selection = "SELECT * FROM "
						+ PBDatabaseDefinition.TimelineHistoryData.TABLE
						+ " WHERE "
						+ PBDatabaseDefinition.TimelineHistoryData.C_IS_NEW
						+ " =1 AND "
						+ PBDatabaseDefinition.TimelineHistoryData.C_TYPE
						+ "='acorn' COLLATE NOCASE;";

				c = db.rawQuery(selection, null);
				return c != null ? c.getCount() : 0;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (c != null)
					c.close();
				if (db != null)
					db.close();
			}
		}
		return 0;
	}

	public int getTotalNewMaple() {
		synchronized (this) {
			SQLiteDatabase db = null;
			Cursor c = null;
			try {
				db = _datastore.getReadableDatabase();
				String selection = "SELECT * FROM "
						+ PBDatabaseDefinition.TimelineHistoryData.TABLE
						+ " WHERE "
						+ PBDatabaseDefinition.TimelineHistoryData.C_IS_NEW
						+ " = 1 AND "
						+ PBDatabaseDefinition.TimelineHistoryData.C_TYPE
						+ "='maple' COLLATE NOCASE;";
				c = db.rawQuery(selection, null);
				return c != null ? c.getCount() : 0;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (c != null)
					c.close();
				if (db != null)
					db.close();
			}
		}
		return 0;
	}

	public int getTotalNewHoney() {
		synchronized (this) {
			SQLiteDatabase db = null;
			Cursor c = null;
			try {
				db = _datastore.getReadableDatabase();
				String selection = "SELECT * FROM "
						+ PBDatabaseDefinition.TimelineHistoryData.TABLE
						+ " WHERE "
						+ PBDatabaseDefinition.TimelineHistoryData.C_IS_NEW
						+ " =1 AND "
						+ PBDatabaseDefinition.TimelineHistoryData.C_TYPE
						+ "='honey' COLLATE NOCASE;";
				c = db.rawQuery(selection, null);
				return c != null ? c.getCount() : 0;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (c != null)
					c.close();
				if (db != null)
					db.close();
			}
		}
		return 0;
	}

	public int getTotalNewGold() {
		synchronized (this) {
			SQLiteDatabase db = null;
			Cursor c = null;
			try {
				db = _datastore.getReadableDatabase();
				String selection = "SELECT * FROM "
						+ PBDatabaseDefinition.TimelineHistoryData.TABLE
						+ " WHERE "
						+ PBDatabaseDefinition.TimelineHistoryData.C_IS_NEW
						+ " =1 AND "
						+ PBDatabaseDefinition.TimelineHistoryData.C_TYPE
						+ "='goldhoney' COLLATE NOCASE;";
				c = db.rawQuery(selection, null);
				return c != null ? c.getCount() : 0;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (c != null)
					c.close();
				if (db != null)
					db.close();
			}
		}
		return 0;
	}

	private ArrayList<PBTimelineHistoryModel> __getTimelineHistoryModel(
			final SQLiteDatabase db, String selection, String[] selectionArgs,
			String groupBy, String having, String sortOrder, int limit) {
		ArrayList<PBTimelineHistoryModel> result = new ArrayList<PBTimelineHistoryModel>();
		Cursor c = null;
		try {
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder
					.setTables(PBDatabaseDefinition.TimelineHistoryData.TABLE);
			String[] projection = {
					PBDatabaseDefinition.TimelineHistoryData.C_ID,
					PBDatabaseDefinition.TimelineHistoryData.C_CREATED_AT,
					PBDatabaseDefinition.TimelineHistoryData.C_TYPE,
					PBDatabaseDefinition.TimelineHistoryData.C_IS_NEW,
					PBDatabaseDefinition.TimelineHistoryData.C_DESCRIPTION };
			c = queryBuilder.query(db, projection, selection, selectionArgs,
					groupBy, having, sortOrder, "" + limit);

			if (c != null && c.moveToFirst()) {
				do {
					PBTimelineHistoryModel entry = new PBTimelineHistoryModel(
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.TimelineHistoryData.C_ID)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.TimelineHistoryData.C_CREATED_AT)),
							PBTimelineHistoryModel.Type.valueOf(c.getString(c
									.getColumnIndex(PBDatabaseDefinition.TimelineHistoryData.C_TYPE))),
							c.getString(c
									.getColumnIndex(PBDatabaseDefinition.TimelineHistoryData.C_DESCRIPTION)),
							c.getInt(c
									.getColumnIndex(PBDatabaseDefinition.TimelineHistoryData.C_IS_NEW)) == 1);
					result.add(entry);
				} while (c.moveToNext());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null)
				c.close();
		}
		return result;
	}

	public void insertTimelineHistory(
			ArrayList<PBTimelineHistoryModel> batchModel) {
		synchronized (this) {
			SQLiteDatabase db = null;
			try {
				db = _datastore.getWritableDatabase();
				db.beginTransaction();

				for (PBTimelineHistoryModel item : batchModel) {
					item.setNew(true);
					ContentValues values = new ContentValues();
					values.put(PBDatabaseDefinition.TimelineHistoryData.C_ID,
							item.getId());
					values.put(
							PBDatabaseDefinition.TimelineHistoryData.C_CREATED_AT,
							item.getCreatedAt());
					values.put(PBDatabaseDefinition.TimelineHistoryData.C_TYPE,
							item.getType().toString());
					values.put(
							PBDatabaseDefinition.TimelineHistoryData.C_IS_NEW,
							item.isNew() ? 1 : 0);
					values.put(
							PBDatabaseDefinition.TimelineHistoryData.C_DESCRIPTION,
							item.getDescription());
					// Log.d("AGUNG", "Try inserting entry :" + item +
					// " isNew "+item.isNew());
					try {
						/*
						 * long status = db.insertOrThrow(
						 * PBDatabaseDefinition.TimelineHistoryData.TABLE, null,
						 * values);
						 */
						// Fix kuma memo problem
						long status = db.insertWithOnConflict(
								PBDatabaseDefinition.TimelineHistoryData.TABLE,
								null, values, SQLiteDatabase.CONFLICT_IGNORE);

						if (status >= 0) {
							// Log.d("AGUNG", "Success inserting : "+item +
							// " isNew "+item.isNew());
						}

					} catch (SQLException e) {
						try {
							if (e.toString().contains("is not unique")) {
								db.update(
										PBDatabaseDefinition.TimelineHistoryData.TABLE,
										values,
										PBDatabaseDefinition.TimelineHistoryData.C_ID
												+ "=" + item.getId(), null);
							}
						} catch (Exception ex) {
						}
					}

				}
				// TODO: get from server list of photo and update
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null) {
					db.endTransaction();
					db.close();
				}
			}
		}
	}

	public void setMarkAllReadTimelineHistory() {
		synchronized (this) {
			SQLiteDatabase db = null;
			try {
				db = _datastore.getWritableDatabase();
				db.beginTransaction();

				{
					ContentValues values = new ContentValues();
					values.put(
							PBDatabaseDefinition.TimelineHistoryData.C_IS_NEW,
							0);
					int rows = db.update(
							PBDatabaseDefinition.TimelineHistoryData.TABLE,
							values, null, null);
					// Log.d("AGUNG", "row has been marked as read:" + rows);
				}
				// TODO: get from server list of photo and update
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null) {
					db.endTransaction();
					db.close();
				}
			}
		}
	}

	/**
	 * get Cursor of history inbox/sent store db
	 * 
	 * @param entry
	 * @param type
	 * @return
	 */
	public Cursor getHistoriesCursorAllData() {
		Cursor c = null;
		try {
			SQLiteDatabase db = _datastore.getReadableDatabase();

			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder.setTables(PBDatabaseDefinition.HistoryData.TABLE);
			String[] projectionIn = { PBDatabaseDefinition.HistoryData.C_ID,
					PBDatabaseDefinition.HistoryData.C_COLECTION_ID,
					PBDatabaseDefinition.HistoryData.C_PASSWORD,
					PBDatabaseDefinition.HistoryData.C_CREATED_AT,
					PBDatabaseDefinition.HistoryData.C_CHARGES_AT,
					PBDatabaseDefinition.HistoryData.C_DOWNLOAD_COUNT,
					PBDatabaseDefinition.HistoryData.C_PHOTO_COUNT,
					PBDatabaseDefinition.HistoryData.C_THUMB,
					PBDatabaseDefinition.HistoryData.C_ADDIBILITY,
					PBDatabaseDefinition.HistoryData.C_EXTRA,
					PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE,
					PBDatabaseDefinition.HistoryData.C_UPDATED_AT,
					PBDatabaseDefinition.HistoryData.C_HONEY_NUM,
					PBDatabaseDefinition.HistoryData.C_MAPLE_NUM,
					PBDatabaseDefinition.HistoryData.C_SAVE_MARK,
					PBDatabaseDefinition.HistoryData.C_SAVE_DAYS,
					PBDatabaseDefinition.HistoryData.C_AD_LINK,
					PBDatabaseDefinition.HistoryData.C_IS_PUBLIC,
					PBDatabaseDefinition.HistoryData.C_ACCEPTED };
			c = queryBuilder.query(db // db
					, projectionIn // projectionIn
					, null // selection
					, null // selectionArgs
					, null // groupBy
					, null // having
					, PBDatabaseDefinition.HistoryData.C_ID + " DESC"); // sortOrder

			return c;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		return c;
	}

	/**
	 * delete all unnecessary items from both SD card and local database
	 * 
	 * @param collectionId
	 *            collection ID
	 */
	@SuppressWarnings("unused")
	public ArrayList<String> generateAllValidDataInboxSent(String collectionId) {

		ArrayList<String> listOfFile = new ArrayList<String>();
		synchronized (this) {
			SQLiteDatabase db = null;
			Cursor c = null;
			try {
				db = _datastore.getWritableDatabase();

				try {
					db.beginTransaction();

					if (collectionId != null) {
						SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

						queryBuilder
								.setTables(PBDatabaseDefinition.HistoryPhoto.TABLE);
						String[] projectionIn = {
								PBDatabaseDefinition.HistoryPhoto.C_PHOTO_URL,
								PBDatabaseDefinition.HistoryPhoto.C_PHOTO_THUMB,
								PBDatabaseDefinition.HistoryPhoto.C_ID,
								PBDatabaseDefinition.HistoryPhoto.C_HISTORY_ID };
						c = queryBuilder.query(db, projectionIn,
								PBDatabaseDefinition.HistoryPhoto.C_HISTORY_ID
										+ " = ?",
								new String[] { String.valueOf(collectionId) },
								null, null, null);
						// fix check null
						if (c != null && c.moveToFirst()) {
							do {
								String filePathUrl = c
										.getString(c
												.getColumnIndex(PBDatabaseDefinition.HistoryPhoto.C_PHOTO_URL));

								String filePathID = c
										.getString(c
												.getColumnIndex(PBDatabaseDefinition.HistoryPhoto.C_ID));
								String filePathHistoryID = c
										.getString(c
												.getColumnIndex(PBDatabaseDefinition.HistoryPhoto.C_HISTORY_ID));
								/*
								 * if (!list.contains(filePathUrl)) { continue;
								 * }
								 */
								// PBGeneralUtils.deleteCacheFileForCacheFunction(filePathUrl);

								String filename = PBGeneralUtils
										.getPathFromCacheFolder(filePathUrl);
								System.out.println("file name inbox sent:"
										+ filename);

								listOfFile.add(filename);

								String fileNameThumb = c
										.getString(c
												.getColumnIndex(PBDatabaseDefinition.HistoryPhoto.C_PHOTO_THUMB));
								String fileThumbURL = PBGeneralUtils
										.getPathFromCacheFolder(fileNameThumb);
								listOfFile.add(fileThumbURL);
								// PBGeneralUtils.deleteCacheFileForCacheFunction(filePathThump);

								/*
								 * db.delete(PBDatabaseDefinition.HistoryPhoto.TABLE
								 * ,
								 * PBDatabaseDefinition.HistoryPhoto.C_PHOTO_URL
								 * + " = ? ", new String[] { filePathUrl });
								 */
							} while (c.moveToNext());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (c != null)
						c.close();
				}

				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					db.endTransaction();
					db.close();
					db = null;
				} catch (Exception e) {
				}
			}
		}
		return listOfFile;
	}

	/**
	 * Check collection ID or password exists in Inbox History
	 * 
	 * @param collection
	 *            id
	 * @author atikur
	 */
	public boolean isPasswordExistsInHistoryInbox(String collection_ID) {

		boolean existAikotobaInInbox = false;
		String collectionID = null;
		Cursor cursor = getHistoriesCursor(PBDatabaseDefinition.HISTORY_INBOX);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				collectionID = cursor
						.getString(cursor
								.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
				if (collection_ID.equalsIgnoreCase(collectionID)) {
					System.out.println("Atik call password exists in Inbox");
					existAikotobaInInbox = true;
					if (cursor != null && !cursor.isClosed())
						cursor.close();

					break;
				}
				cursor.moveToNext();
			}
		}
		if (cursor != null && !cursor.isClosed())
			cursor.close();

		return existAikotobaInInbox;
	}

	/**
	 * Check collection ID or password exists in Sent History
	 * 
	 * @param collection
	 *            id
	 * @author atikur
	 */
	public boolean isPasswordExistsInHistorySent(String collection_ID) {

		boolean existAikotobaInInbox = false;
		String collectionID = null;
		// Cursor cursor =
		// getHistoriesCursor(PBDatabaseDefinition.HISTORY_SENT);
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = _datastore.getReadableDatabase();
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder.setTables(PBDatabaseDefinition.HistoryData.TABLE);
			String[] projectionIn = { PBDatabaseDefinition.HistoryData.C_ID,
					PBDatabaseDefinition.HistoryData.C_COLECTION_ID,
					PBDatabaseDefinition.HistoryData.C_PASSWORD,
					PBDatabaseDefinition.HistoryData.C_CREATED_AT,
					PBDatabaseDefinition.HistoryData.C_CHARGES_AT,
					PBDatabaseDefinition.HistoryData.C_DOWNLOAD_COUNT,
					PBDatabaseDefinition.HistoryData.C_PHOTO_COUNT,
					PBDatabaseDefinition.HistoryData.C_THUMB,
					PBDatabaseDefinition.HistoryData.C_ADDIBILITY,
					PBDatabaseDefinition.HistoryData.C_EXTRA,
					PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE,
					PBDatabaseDefinition.HistoryData.C_UPDATED_AT,
					PBDatabaseDefinition.HistoryData.C_HONEY_NUM,
					PBDatabaseDefinition.HistoryData.C_MAPLE_NUM,
					PBDatabaseDefinition.HistoryData.C_SAVE_MARK,
					PBDatabaseDefinition.HistoryData.C_SAVE_DAYS,
					PBDatabaseDefinition.HistoryData.C_AD_LINK,
					PBDatabaseDefinition.HistoryData.C_IS_PUBLIC,
					PBDatabaseDefinition.HistoryData.C_ACCEPTED };
			cursor = queryBuilder.query(db // db
					, projectionIn // projectionIn
					, PBDatabaseDefinition.HistoryData.C_TYPE + "=?" // selection
					, new String[] { String
							.valueOf(PBDatabaseDefinition.HISTORY_SENT) } // selectionArgs
					, null // groupBy
					, null // having
					, PBDatabaseDefinition.HistoryData.C_ID + " DESC"); // sortOrder
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					collectionID = cursor
							.getString(cursor
									.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
					if (collection_ID.equalsIgnoreCase(collectionID)) {
						System.out
								.println("Atik call password exists in Inbox");
						existAikotobaInInbox = true;
						if (cursor != null && !cursor.isClosed())
							cursor.close();
						break;
					}
					cursor.moveToNext();
				}
			}
			// return c;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			if (db.isOpen())
				db.close();
		}

		return existAikotobaInInbox;
	}

	/**
	 * Check collection ID or password exists in Sent History
	 * 
	 * @param collection
	 *            id
	 * @author atikur
	 */
	public boolean isPasswordExistsInHistorySentOriginals(String collection_ID) {

		boolean existAikotobaInInbox = false;
		String collectionID = null;
		Cursor cursor = getHistoriesCursor(PBDatabaseDefinition.HISTORY_SENT);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				collectionID = cursor
						.getString(cursor
								.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
				if (collection_ID.equalsIgnoreCase(collectionID)) {
					System.out.println("Atik call password exists in Inbox");
					existAikotobaInInbox = true;
					if (cursor != null && !cursor.isClosed())
						cursor.close();
					break;
				}
				cursor.moveToNext();
			}
		}
		if (cursor != null && !cursor.isClosed())
			cursor.close();

		return existAikotobaInInbox;
	}

	/**
	 * Check password exists in Sent History with properly close db and cursor
	 * 
	 * @param collection
	 *            id
	 * @author atikur
	 */
	// public boolean isPasswordExistsInHistorySentItems() {
	//
	// boolean existAikotobaInSentItems = false;
	// String collectionID = null;
	// SQLiteDatabase db = null;
	// Cursor cursor = null;
	// try {
	// db = _datastore.getReadableDatabase();
	//
	// SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	// queryBuilder.setTables(PBDatabaseDefinition.HistoryData.TABLE);
	// String[] projectionIn = { PBDatabaseDefinition.HistoryData.C_ID,
	// PBDatabaseDefinition.HistoryData.C_COLECTION_ID,
	// PBDatabaseDefinition.HistoryData.C_PASSWORD,
	// PBDatabaseDefinition.HistoryData.C_CREATED_AT,
	// PBDatabaseDefinition.HistoryData.C_CHARGES_AT,
	// PBDatabaseDefinition.HistoryData.C_DOWNLOAD_COUNT,
	// PBDatabaseDefinition.HistoryData.C_PHOTO_COUNT,
	// PBDatabaseDefinition.HistoryData.C_THUMB,
	// PBDatabaseDefinition.HistoryData.C_ADDIBILITY,
	// PBDatabaseDefinition.HistoryData.C_EXTRA,
	// PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE,
	// PBDatabaseDefinition.HistoryData.C_UPDATED_AT,
	// PBDatabaseDefinition.HistoryData.C_HONEY_NUM,
	// PBDatabaseDefinition.HistoryData.C_MAPLE_NUM,
	// PBDatabaseDefinition.HistoryData.C_SAVE_MARK,
	// PBDatabaseDefinition.HistoryData.C_SAVE_DAYS,
	// PBDatabaseDefinition.HistoryData.C_AD_LINK,
	// PBDatabaseDefinition.HistoryData.C_IS_PUBLIC,
	// PBDatabaseDefinition.HistoryData.C_ACCEPTED};
	// cursor = queryBuilder.query(db // db
	// , projectionIn // projectionIn
	// , PBDatabaseDefinition.HistoryData.C_TYPE + "=?" // selection
	// , new String[] { String.valueOf(PBDatabaseDefinition.HISTORY_SENT) } //
	// selectionArgs
	// , null // groupBy
	// , null // having
	// , PBDatabaseDefinition.HistoryData.C_ID + " DESC"); // sortOrder
	//
	// if (cursor.moveToFirst()){
	// while(!cursor.isAfterLast()){
	// collectionID = cursor.getString(cursor.getColumnIndex(
	// PBDatabaseDefinition.HistoryData.C_PASSWORD));
	// if(password.equalsIgnoreCase(collectionID)) {
	// System.out.println("Atik call password exists in Inbox");
	// existAikotobaInSentItems = true;
	// if (cursor != null && !cursor.isClosed())
	// cursor.close();
	// break;
	// }
	// cursor.moveToNext();
	// }
	// }
	//
	// //return c;
	// } catch (SQLException e) {
	// e.printStackTrace();
	// } catch (IllegalStateException e) {
	// e.printStackTrace();
	// } finally {
	// if( cursor != null && !cursor.isClosed() )
	// cursor.close();
	// if( db.isOpen() )
	// db.close();
	// }
	//
	// return existAikotobaInSentItems;
	// }

	/**
	 * Check password exists in Sent History
	 * 
	 * @param collection
	 *            id
	 * @author atikur
	 */
	// public boolean isPasswordExistsInHistorySentItemsOriginal() {
	//
	// boolean existAikotobaInSentItems = false;
	// String collectionID = null;
	// Cursor cursor = getHistoriesCursor(PBDatabaseDefinition.HISTORY_SENT);
	// if (cursor.moveToFirst()){
	// while(!cursor.isAfterLast()){
	// collectionID = cursor.getString(cursor.getColumnIndex(
	// PBDatabaseDefinition.HistoryData.C_PASSWORD));
	// if(password.equalsIgnoreCase(collectionID)) {
	// System.out.println("Atik call password exists in Inbox");
	// existAikotobaInSentItems = true;
	// if (cursor != null && !cursor.isClosed())
	// cursor.close();
	// break;
	// }
	// cursor.moveToNext();
	// }
	// }
	// if (cursor != null && !cursor.isClosed())
	// cursor.close();
	//
	// return existAikotobaInSentItems;
	// }

	public boolean isPasswordExistsInSentItems(String collectionId) {

		SQLiteDatabase db = _datastore.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT 1 FROM "
				+ PBDatabaseDefinition.HistoryData.TABLE + " WHERE "
				+ PBDatabaseDefinition.HistoryData.C_COLECTION_ID + "=?",
				new String[] { collectionId });
		boolean exists = cursor.moveToFirst();
		cursor.close();
		return exists;
	}

	/*
	 * Check whether password exists on Inbox or not
	 */
	public boolean isPasswordExistsInInboxItems(String collectionId) {
		SQLiteDatabase db = null;
		boolean exists = false;
		try {
			db = _datastore.getReadableDatabase();
			Cursor cursor = db
					.rawQuery(
							"SELECT 1 FROM "
									+ PBDatabaseDefinition.HistoryData.TABLE
									+ " WHERE "
									+ PBDatabaseDefinition.HistoryData.C_COLECTION_ID
									+ "=?" + " AND "
									+ PBDatabaseDefinition.HistoryData.C_TYPE
									+ "=?",
							new String[] {
									collectionId,
									String.valueOf(PBDatabaseDefinition.HISTORY_INBOX) });
			exists = cursor.moveToFirst();
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
		return exists;
	}

	/**
	 * get Cursor of history inbox/sent store db
	 * 
	 * @param entry
	 * @param type
	 * @return
	 */
	public String getPassswordSecretDigit(String collectionId) {

		String secretdigit = "12";
		SQLiteDatabase db = null;
		try {
			db = _datastore.getReadableDatabase();
			String query = "SELECT "
					+ PBDatabaseDefinition.HistoryData.C_ST_Digit + " FROM "
					+ PBDatabaseDefinition.HistoryData.TABLE + " WHERE "
					+ PBDatabaseDefinition.HistoryData.C_COLECTION_ID + "=?";
			// Log.d("query ", query + " "+password);
			Cursor cursor = db.rawQuery(query, new String[] { collectionId });

			if (cursor != null && cursor.moveToFirst()) {

				secretdigit = cursor
						.getString(cursor
								.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ST_Digit));
				// System.out.println("secretdigit  "+secretdigit);
			}

			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}

		return secretdigit;

	}

	public String getCollectionId(String passord) {

		String collectionID = "11";
		SQLiteDatabase db = null;
		try {
			db = _datastore.getReadableDatabase();
			String query = "SELECT "
					+ PBDatabaseDefinition.HistoryData.C_COLECTION_ID
					+ " FROM " + PBDatabaseDefinition.HistoryData.TABLE
					+ " WHERE " + PBDatabaseDefinition.HistoryData.C_PASSWORD
					+ "=?";
			Cursor cursor = db.rawQuery(query, new String[] { passord });

			if (cursor != null && cursor.moveToFirst()) {

				collectionID = cursor
						.getString(cursor
								.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID));
			}

			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}

		return collectionID;

	}

	public String getPassord(String collectionID, int type) {

		String password = "";
		SQLiteDatabase db = null;
		try {
			db = _datastore.getReadableDatabase();
			String query = "SELECT "
					+ PBDatabaseDefinition.HistoryData.C_PASSWORD + " FROM "
					+ PBDatabaseDefinition.HistoryData.TABLE + " WHERE "
					+ PBDatabaseDefinition.HistoryData.C_COLECTION_ID + "=?"
					+ " AND " + PBDatabaseDefinition.HistoryData.C_TYPE + "=?";
			Cursor cursor = db.rawQuery(query, new String[] { collectionID,
					String.valueOf(type) });

			if (cursor != null && cursor.moveToFirst()) {

				password = cursor
						.getString(cursor
								.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PASSWORD));
			}

			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}

		return password;

	}
}