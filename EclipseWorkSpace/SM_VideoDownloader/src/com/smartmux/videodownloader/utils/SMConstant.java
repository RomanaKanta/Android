package com.smartmux.videodownloader.utils;

import java.io.File;

import android.os.Environment;

public class SMConstant {

	public static final String DOWNLOAD_TAG = "DownloadTab";
	public static final String BROWSER_TAG = "BrowserTab";
	public static final String FILES_TAG = "FilesTab";
	public static final String SETTINGS_TAG = "SettingTab";
	public static final String PLAYLIST_TAG = "PlaylistTab";
	public static String pref_name = "SM_WEBVIEW_PREFS";
	public static String save_url = "SAVED_URL";
	public static String save_bookmark_url = "SAVED_BOOKMARK_URL";
	public static String download_url = "DOWNLOAD_URL";
	public static String url_title = "URL_TITLE";
	public static String playlist_edit_state = "PLAYLIST_EDIT_STATE";
	public static String video_edit_state = "VIDEO_EDIT_STATE";
	public static String spinner = "SPINNER";
	public static String background_play = "BACKGROUND_PLAY";
	public static String security = "SECURITY";
	public static String thumbnail = "THUMBNAILS";
	public static String capitalize = "CAPITALIZE";
	public static String clean = "CLEAR_DOWNLOAD";
	public static String threeD = "3D_DOWNLOAD";
	public static String on = "ON";
	public static String off = "OFF";
	public static String prow_position = "PLAY_ROW_POSITIONS";
	public static String data_id = "PLAY_DATA_POSITIONS";
	public static final String FOLDER_NAME = "SM_Gallery";
	public static File mediaStorageDir = new File(
			Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
			FOLDER_NAME);
	
	
	
	//////////********************domain name*********************\\\\\\\\\\\\\\
	
	public static final String youtube = "youtube.com/";
	public static final String rdbell = "3rdbell.com/";
	

}
