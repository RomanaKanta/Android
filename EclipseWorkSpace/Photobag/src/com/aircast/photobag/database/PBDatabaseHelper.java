package com.aircast.photobag.database;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.aircast.photobag.application.PBConstant;

/**
 * class database helper support create/update local database
 * @author lent5
 *
 */
public class PBDatabaseHelper  extends SQLiteOpenHelper {
    // private final String TAG = "PBDatabaseHelper";

    /** Constructor PBDatabseHelper */
    public PBDatabaseHelper(Context context) {
        super(context, PBDatabaseDefinition.FILE, null, PBDatabaseDefinition.VERSION);
        //CopyDatabases.copySharedPrefs(context);//4debug
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(Class<?> cls : PBDatabaseDefinition.Tables){
            try {
                Method method = cls.getMethod(PBDatabaseDefinition.CREATE_SQL);
                String sql = (String) method.invoke(null);    // static method
                if(sql != null){
                	for (String statement : sql.split(";")){
                		try {
                			db.execSQL(statement + ";");
                    	} catch (Exception e) { Log.e(PBConstant.TAG, e.getMessage()); }                		
                	}
                }
            } catch (SecurityException e) {
            	e.printStackTrace();
                // Assert.fail();
                // pass
            } catch (NoSuchMethodException e) {
            	e.printStackTrace();
                // Assert.fail();
                // pass
            } catch (IllegalArgumentException e) {
            	e.printStackTrace();
                // Assert.fail();
                // pass
            } catch (IllegalAccessException e) {
            	e.printStackTrace();
                // Assert.fail();
                // pass
            } catch (InvocationTargetException e) {
            	e.printStackTrace();
                // Assert.fail();
                // pass
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlitedatabase, int oldVersion, int newVersion) {
        for(Class<?> cls : PBDatabaseDefinition.Tables){
            try {
                Method method = cls.getMethod(PBDatabaseDefinition.UPDATE_SQL, int.class);
                String sql = (String) method.invoke(null, oldVersion);    // static method
                if(!TextUtils.isEmpty(sql)){
                    for (String statement : sql.split(";")) {
                    	try {
                    		sqlitedatabase.execSQL(statement + ";");
                    	} catch (Exception e) { Log.e(PBConstant.TAG, e.getMessage()); }
                	}
                }
            } catch (SecurityException e) {
                // Assert.fail();
                // pass
                Log.e(PBConstant.TAG, e.getMessage());
            } catch (NoSuchMethodException e) {
                // Assert.fail();
                // pass
                Log.e(PBConstant.TAG, e.getMessage());
            } catch (IllegalArgumentException e) {
                // Assert.fail();
                // pass
                Log.e(PBConstant.TAG, e.getMessage());
            } catch (IllegalAccessException e) {
                // Assert.fail();
                // pass
                Log.e(PBConstant.TAG, e.getMessage());
            } catch (InvocationTargetException e) {
                // Assert.fail();
                // pass
                Log.e(PBConstant.TAG, e.getMessage());
            } catch (SQLiteException e) {
                // Assert.fail();
                // pass
                Log.e(PBConstant.TAG, e.getMessage());
            }
        }
    }
}
