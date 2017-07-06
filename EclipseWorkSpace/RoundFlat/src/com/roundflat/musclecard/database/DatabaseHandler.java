package com.roundflat.musclecard.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.roundflat.musclecard.model.TutorialModel;
import com.roundflat.musclecard.util.Constant;

public class DatabaseHandler extends SQLiteOpenHelper {

	

	public DatabaseHandler(Context context) {
		super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_BOOK_TABLE = "CREATE TABLE " + Constant.TABLE_TUTORIAL + "("
				+ Constant.id + " TEXT PRIMARY KEY,"
				+ Constant.root_title + " TEXT," + Constant.category
				+ " TEXT," + Constant.sub_category + " TEXT,"
				+ Constant.title + " TEXT," + Constant.sub_title
				+ " TEXT," + Constant.label_english + " TEXT," + Constant.label_japanese
				+ " TEXT," + Constant.option_1 + " TEXT,"
				+ Constant.option_2 + " TEXT," + Constant.option_3
				+ " TEXT," + Constant.option_4 + " TEXT," + Constant.isFav + " TEXT" +")";

//		String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + Constant.TABLE_FAVOURITE + "("
//				+ Constant.KEY_ID + " INTEGER PRIMARY KEY," + Constant.id + " TEXT" + ")";

		db.execSQL(CREATE_BOOK_TABLE);
		//db.execSQL(CREATE_FAVOURITE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}


	public void updateFavoriteTutorial(String id,String value) {
		
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Constant.isFav, value );
		db.update(Constant.TABLE_TUTORIAL, values,Constant.id + " = ?",
                new String[] { id });
		db.close();

	}
	
	public boolean isFavoriteTutorialExists(String id) {

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT 1 FROM " +  Constant.TABLE_TUTORIAL + " WHERE "
				+ Constant.id + "=? AND " + Constant.isFav + " = ?", new String[] { id ,"1"});
		boolean exists = cursor.moveToFirst();
		cursor.close();
		return exists;

	}
	


	public int addTutorial(TutorialModel tutorial) {
		int value = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		String Query = "Select * from " + Constant.TABLE_TUTORIAL + " where "
				+ Constant.id + "='" + tutorial.getId() + "'";
		Cursor cursor = db.rawQuery(Query, null);
		if (cursor.getCount() < 1) {
			ContentValues values = new ContentValues();
			values.put(Constant.id, tutorial.getId());
			values.put(Constant.root_title,tutorial.getRoot_title());
			values.put(Constant.category, tutorial.getCategory());
			values.put(Constant.sub_category,tutorial.getSub_category());
			values.put(Constant.title, tutorial.getTitle());
			values.put(Constant.sub_title, tutorial.getSub_title());
			values.put(Constant.label_english, tutorial.getLabel_english());
			values.put(Constant.label_japanese,tutorial.getLabel_japanese());
			values.put(Constant.option_1, tutorial.getOption_1() );
			values.put(Constant.option_2, tutorial.getOption_2() );
			values.put(Constant.option_3 ,tutorial.getOption_3() );
			values.put(Constant.option_4, tutorial.getOption_4() );
			values.put(Constant.isFav, tutorial.getIsFav() );
			db.insert(Constant.TABLE_TUTORIAL, null, values);
			db.close();
			value = 1;
		}else{
			
			ContentValues values = new ContentValues();
			values.put(Constant.root_title,tutorial.getRoot_title());
			values.put(Constant.category, tutorial.getCategory());
			values.put(Constant.sub_category,tutorial.getSub_category());
			values.put(Constant.title, tutorial.getTitle());
			values.put(Constant.sub_title, tutorial.getSub_title());
			values.put(Constant.label_english, tutorial.getLabel_english());
			values.put(Constant.label_japanese,tutorial.getLabel_japanese());
			values.put(Constant.option_1, tutorial.getOption_1() );
			values.put(Constant.option_2, tutorial.getOption_2() );
			values.put(Constant.option_3 ,tutorial.getOption_3() );
			values.put(Constant.option_4, tutorial.getOption_4() );
			values.put(Constant.isFav, tutorial.getIsFav() );

			db.update(Constant.TABLE_TUTORIAL, values,Constant.id + " = ?",
	                new String[] { String.valueOf(tutorial.getId()) });
			db.close();
			value = 2;
			
		}

		return value;

	}

	public boolean isTutorialExists(String id) {

		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery("SELECT 1 FROM " + Constant.TABLE_TUTORIAL + " WHERE "
				+ Constant.id + "=?", new String[] { id });
		boolean exists = cursor.moveToFirst();
		cursor.close();
		return exists;
	}

	public List<TutorialModel> getAllTutorials(String query) {
		Log.d("query", query);
		List<TutorialModel> list = new ArrayList<TutorialModel>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {

				TutorialModel tutorial = new TutorialModel();

				tutorial.setId(cursor.getString(cursor
						.getColumnIndex(Constant.id)));
				tutorial.setRoot_title(cursor.getString(cursor
						.getColumnIndex(Constant.root_title)));
				tutorial.setCategory(cursor.getString(cursor
						.getColumnIndex(Constant.category)));
				tutorial.setSub_category(cursor.getString(cursor
						.getColumnIndex(Constant.sub_category)));
				tutorial.setTitle(cursor.getString(cursor
						.getColumnIndex(Constant.title)));
				tutorial.setSub_title(cursor.getString(cursor
						.getColumnIndex(Constant.sub_title)));
				tutorial.setLabel_english(cursor.getString(cursor
						.getColumnIndex(Constant.label_english)));
				tutorial.setLabel_japanese(cursor.getString(cursor
						.getColumnIndex(Constant.label_japanese)));
				tutorial.setOption_1(cursor.getString(cursor
						.getColumnIndex(Constant.option_1)));
				tutorial.setOption_2(cursor.getString(cursor
						.getColumnIndex(Constant.option_2)));
				tutorial.setOption_3(cursor.getString(cursor
						.getColumnIndex(Constant.option_3)));
				tutorial.setOption_4(cursor.getString(cursor
						.getColumnIndex(Constant.option_4)));
				tutorial.setIsFav(cursor.getString(cursor
						.getColumnIndex(Constant.isFav)));

				list.add(tutorial);
			} while (cursor.moveToNext());
		}
		
		Log.d("get all tutorial", ""+list);
		cursor.close();
		db.close();
		return list;
	}
	
	
	
	public Cursor getData(String query) {

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		//db.close();
		return cursor;
	}
	
	public TutorialModel getDetail(String id) {
		TutorialModel tutorial = new TutorialModel();
		
		String selectQuery = "Select * from " + Constant.TABLE_TUTORIAL + " where "
				+ Constant.id + "='" + id + "'";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		tutorial.setRoot_title(cursor.getString(cursor
				.getColumnIndex(Constant.root_title)));
		tutorial.setCategory(cursor.getString(cursor
				.getColumnIndex(Constant.category)));
		tutorial.setSub_category(cursor.getString(cursor
				.getColumnIndex(Constant.sub_category)));
		tutorial.setTitle(cursor.getString(cursor
				.getColumnIndex(Constant.title)));
		tutorial.setSub_title(cursor.getString(cursor
				.getColumnIndex(Constant.sub_title)));
		tutorial.setLabel_english(cursor.getString(cursor
				.getColumnIndex(Constant.label_english)));
		tutorial.setLabel_japanese(cursor.getString(cursor
				.getColumnIndex(Constant.label_japanese)));
		tutorial.setOption_1(cursor.getString(cursor
				.getColumnIndex(Constant.option_1)));
		tutorial.setOption_2(cursor.getString(cursor
				.getColumnIndex(Constant.option_2)));
		tutorial.setOption_3(cursor.getString(cursor
				.getColumnIndex(Constant.option_3)));
		tutorial.setOption_4(cursor.getString(cursor
				.getColumnIndex(Constant.option_4)));
		tutorial.setIsFav(cursor.getString(cursor
				.getColumnIndex(Constant.isFav)));
	
		cursor.moveToNext();

		cursor.close();

	
		return tutorial;
	}


	public boolean deleteTutorial(String id) {

		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(Constant.TABLE_TUTORIAL, Constant.id + "='" + id + "'", null) > 0;
	}

	public int getRowCount() {
		String countQuery = "SELECT  * FROM " + Constant.TABLE_TUTORIAL;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();

		return rowCount;
	}

	public void resetTables() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(Constant.TABLE_TUTORIAL, null, null);
		db.close();
	}

}