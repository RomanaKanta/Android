package com.smartmux.videodownloader.utils;

import com.smartmux.videodownloader.lockscreen.utils.AppExtra;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SMSharePref {
	
	public static void setHomeCode(Context context) {
		SharedPreferences sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(AppExtra.RETURN_TAG, AppExtra.HOME_CODE);
		editor.commit();
	}

	public static void setBackCode(Context context) {
		SharedPreferences sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(AppExtra.RETURN_TAG, AppExtra.BACK_CODE);
		editor.commit();
	}

	public static void setDefaultCode(Context context) {
		SharedPreferences sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(AppExtra.RETURN_TAG, AppExtra.DEFAULT_CODE);
		editor.commit();
	}

	public static int getReturnCode(Context context) {
		SharedPreferences sharedPreferences = context
				.getSharedPreferences(AppExtra.PREFS_NAME, 0);
		int return_code = sharedPreferences.getInt(AppExtra.RETURN_TAG, 0);
		return return_code;
	}

	public static void saveUrl(Context c,String url){
	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    Editor editor = sp.edit();
	    editor.putString(SMConstant.save_url, url);
	    editor.commit();
	}

	public static String getUrl(Context c){

	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    //If you haven't saved the url before, the default value will be google's page
	    return sp.getString(SMConstant.save_url, "http://google.com");

	}
	
	public static void saveBookmarkUrl(Context c,String url){
	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    Editor editor = sp.edit();
	    editor.putString(SMConstant.save_bookmark_url, url);
	    editor.commit();
	}

	public static String getBookmarkUrl(Context c){

	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    //If you haven't saved the url before, the default value will be google's page
	    return sp.getString(SMConstant.save_bookmark_url, "");

	}

	public static void saveDownloadUrl(Context c,String url, String title){
	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    Editor editor = sp.edit();
	    editor.putString(SMConstant.download_url, url);
	    editor.putString(SMConstant.url_title, title);
	    editor.commit();
	}
	
	public static String getDownloadUrl(Context c){

	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	   
	    return sp.getString(SMConstant.download_url, "");

	}
	
	public static String getUrlTitle(Context c){

	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	   
	    return sp.getString(SMConstant.url_title, "");

	}
	
	public static void savePlaylistEditState(Context c,String edit){
	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    Editor editor = sp.edit();
	    editor.putString(SMConstant.playlist_edit_state, edit);
	    editor.commit();
	}

	public static String getPlaylistEditState(Context c){

	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    //If you haven't saved the url before, the default value will be google's page
	    return sp.getString(SMConstant.playlist_edit_state, "Edit");

	}
	
	public static void saveVideoEditState(Context c,String edit){
	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    Editor editor = sp.edit();
	    editor.putString(SMConstant.video_edit_state, edit);
	    editor.commit();
	}

	public static String getVideoEditState(Context c){

	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    //If you haven't saved the url before, the default value will be google's page
	    return sp.getString(SMConstant.video_edit_state, "Edit");

	}
	
	public static void saveRowPosition(Context c,int position){
	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    Editor editor = sp.edit();
	    editor.putInt(SMConstant.prow_position, position);
	    editor.commit();
	}

	public static int getRowPosition(Context c){

	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    //If you haven't saved the url before, the default value will be google's page
	    return sp.getInt(SMConstant.prow_position, 0);

	}
	
	public static void saveDataPosition(Context c,int position){
	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    Editor editor = sp.edit();
	    editor.putInt(SMConstant.data_id, position);
	    editor.commit();
	}

	public static int getDataPosition(Context c){

	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    //If you haven't saved the url before, the default value will be google's page
	    return sp.getInt(SMConstant.data_id, 0);

	}
	
	
	
	public static void saveBackgroundPlay(Context c,String bp){
	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    Editor editor = sp.edit();
	    editor.putString(SMConstant.background_play, bp);
	    editor.commit();
	}

	public static String getBackgroundPlay(Context c){

	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    //If you haven't saved the url before, the default value will be google's page
	    return sp.getString(SMConstant.background_play, "off");

	}
	
	public static void saveSecurity(Context c,String sec){
	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    Editor editor = sp.edit();
	    editor.putString(SMConstant.security, sec);
	    editor.commit();
	}

	public static String getSecurity(Context c){

	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    //If you haven't saved the url before, the default value will be google's page
	    return sp.getString(SMConstant.security, "off");

	}
	
	public static void save3DDownload(Context c,String d){
	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    Editor editor = sp.edit();
	    editor.putString(SMConstant.threeD, d);
	    editor.commit();
	}

	public static String get3DDownload(Context c){

	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    //If you haven't saved the url before, the default value will be google's page
	    return sp.getString(SMConstant.threeD, "off");

	}
	
	public static void saveClearDownload(Context c,String cd){
	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    Editor editor = sp.edit();
	    editor.putString(SMConstant.clean, cd);
	    editor.commit();
	}

	public static String getClearDownload(Context c){

	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    //If you haven't saved the url before, the default value will be google's page
	    return sp.getString(SMConstant.clean, "off");

	}
	
	public static void saveThumbnails(Context c,String thumb){
	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    Editor editor = sp.edit();
	    editor.putString(SMConstant.thumbnail, thumb);
	    editor.commit();
	}

	public static String getThumbnails(Context c){

	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    //If you haven't saved the url before, the default value will be google's page
	    return sp.getString(SMConstant.thumbnail, "off");

	}
	
	public static void saveCapitalize(Context c,String capt){
	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    Editor editor = sp.edit();
	    editor.putString(SMConstant.capitalize, capt);
	    editor.commit();
	}

	public static String getCapitalize(Context c){

	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    //If you haven't saved the url before, the default value will be google's page
	    return sp.getString(SMConstant.capitalize, "off");

	}
	
	
	public static void saveSpinnerPosition(Context c,int position){
	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    Editor editor = sp.edit();
	    editor.putInt(SMConstant.spinner, position);
	    editor.commit();
	}

	public static int getSpinnerPosition(Context c){

	    SharedPreferences sp = c.getSharedPreferences(SMConstant.pref_name, c.MODE_PRIVATE);
	    //If you haven't saved the url before, the default value will be google's page
	    return sp.getInt(SMConstant.spinner, 0);

	}
	
}
