package com.smartmux.musiclist.dbhandler;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class Playlist {
	private long _id;
	private String name;
	private String data;
	private long dateAdded;
	private long dateModified;
	private int totalSong;
	private long totalDuration = 0;

	private Playlist() {
	}

	public static Cursor queryPlaylists(ContentResolver resolver) {
		Uri media = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
		String[] projection = { MediaStore.Audio.Playlists._ID,
				MediaStore.Audio.Playlists.NAME };
		String sort = MediaStore.Audio.Playlists.NAME;
		return resolver.query(media, projection, null, null, sort);
	}

	public static Playlist get(ContentResolver resolver, long id) {
		Cursor cursor = resolver.query(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, "_id = "
						+ id, null, null);
		if (!cursor.moveToFirst()) {
			return null;
		}
		Playlist playlist = Playlist.fromCursor(cursor, resolver);
		cursor.close();
		return playlist;
	}

	public static Playlist fromCursor(Cursor cursor, ContentResolver resolver) {
		Playlist playlist = new Playlist();

		playlist._id = cursor.getLong(cursor.getColumnIndex("_id"));
		playlist.data = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.PlaylistsColumns.DATA));
		playlist.name = cursor.getString(cursor
				.getColumnIndex(MediaStore.Audio.PlaylistsColumns.NAME));
		playlist.dateAdded = cursor.getLong(cursor
				.getColumnIndex(MediaStore.Audio.PlaylistsColumns.DATE_ADDED));
		playlist.dateModified = cursor
				.getLong(cursor
						.getColumnIndex(MediaStore.Audio.PlaylistsColumns.DATE_MODIFIED));

		
		Cursor mCursor = getPlayListDetails(resolver, playlist.getId());
		playlist.totalSong  = mCursor.getCount();
		if(mCursor.moveToFirst()){
			while(mCursor.moveToNext()){
				
			
				playlist.totalDuration = playlist.totalDuration + Long.parseLong(mCursor
						.getString(5));
			}
		}

		return playlist;
	}

	   private static Cursor getPlayListDetails(ContentResolver resolver, long playListId) {
	
			String[] SONG_SUMMARY_PROJECTION = { MediaStore.Audio.Media._ID,
					MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME,
					MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM_ID };
		        Cursor cursor = resolver.query(MediaStore.Audio.Playlists.Members
				.getContentUri("external", playListId), SONG_SUMMARY_PROJECTION, null, null, null);

		
		return cursor;
	}

	public static void addToPlaylist(ContentResolver resolver, int audioId,
			long playListId) {

		Log.d("playListId", "" + playListId);

		Log.d("audioId", "" + audioId);
		String[] cols = new String[] { "count(*)" };
		Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external",
				playListId);
		Cursor cur = resolver.query(uri, cols, null, null, null);
		cur.moveToFirst();
		final int base = cur.getInt(0);
		cur.close();
		ContentValues values = new ContentValues();
		values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER,
				Integer.valueOf(base + audioId));
		values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
		resolver.insert(uri, values);
	}

	public static ArrayList<Playlist> getAllPlaylists(ContentResolver resolver) {
		ArrayList<Playlist> playlist = new ArrayList<Playlist>();
		Cursor cursor = resolver.query(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, null,
				null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
		while (cursor.moveToNext()) {
			playlist.add(Playlist.fromCursor(cursor, resolver));
		}
		cursor.close();
		return playlist;
	}

    public static ArrayList<String> getAllPlaylistsName(ContentResolver resolver) {
        ArrayList<String> playlist = new ArrayList<String>();
        Cursor cursor = resolver.query(
                MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
        while (cursor.moveToNext()) {
            playlist.add(Playlist.fromCursor(cursor, resolver).getName());
        }
        cursor.close();
        return playlist;
    }

	public static Playlist get(ContentResolver resolver, String name) {
		Cursor cursor = resolver.query(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				null,
				MediaStore.Audio.PlaylistsColumns.NAME + " = '"
						+ name.replace("'", "''") + "'", null, null);
		if (!cursor.moveToFirst()) {
			return null;
		}
		Playlist playlist = Playlist.fromCursor(cursor, resolver);
		cursor.close();
		return playlist;
	}

	public static Playlist create(ContentResolver resolver, String name) {
		if (name == null || name.trim().isEmpty()) {
			return null;
		}
		ContentValues values = new ContentValues();
		Calendar now = Calendar.getInstance();
		values.put(MediaStore.Audio.Playlists.NAME, name.trim());
		values.put(MediaStore.Audio.Playlists.DATE_ADDED, now.getTimeInMillis());
		values.put(MediaStore.Audio.Playlists.DATE_MODIFIED,
				now.getTimeInMillis());
		Uri uri = resolver.insert(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
		Cursor cursor = resolver.query(uri, null, null, null, null);
		cursor.moveToFirst();
		Playlist playlist = Playlist.fromCursor(cursor, resolver);
		cursor.close();
		return playlist;
	}

	public long getId() {
		return _id;
	}

	public String getName() {
		return name;
	}

	public String getData() {
		return data;
	}

	public int getTotalSong() {

		return totalSong;
	}

	public long getTotalDuration() {

		return totalDuration;
	}

	public Calendar getDateAdded() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateAdded);
		return cal;
	}

	public Calendar getDateModified() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateModified);
		return cal;
	}

	public static long getPlaylist(ContentResolver resolver, String name) {
		long id = -1;

		Cursor cursor = resolver.query(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Playlists._ID },
				MediaStore.Audio.Playlists.NAME + "=?", new String[] { name },
				null);

		if (cursor != null) {
			if (cursor.moveToNext())
				id = cursor.getLong(0);
			cursor.close();
		}

		return id;
	}

	public static long createPlaylist(ContentResolver resolver, String name) {
		long id = getPlaylist(resolver, name);

		if (id == -1) {
			// We need to create a new playlist.
			ContentValues values = new ContentValues(1);
			values.put(MediaStore.Audio.Playlists.NAME, name);
			Uri uri = resolver.insert(
					MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
			id = Long.parseLong(uri.getLastPathSegment());
		} else {
			// We are overwriting an existing playlist. Clear existing songs.
			Uri uri = MediaStore.Audio.Playlists.Members.getContentUri(
					"external", id);
			resolver.delete(uri, null, null);
		}

		return id;
	}

	public static int deletePlaylist(ContentResolver resolver, long id) {
		Uri uri = ContentUris.withAppendedId(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
		return resolver.delete(uri, null, null);
	}

	public static void renamePlaylist(ContentResolver resolver, long id,
			String newName) {
		long existingId = getPlaylist(resolver, newName);
		// We are already called the requested name; nothing to do.
		if (existingId == id)
			return;
		// There is already a playlist with this name. Kill it.
		if (existingId != -1)
			deletePlaylist(resolver, existingId);

		ContentValues values = new ContentValues(1);
		values.put(MediaStore.Audio.Playlists.NAME, newName);
		resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				values, "_id=" + id, null);
	}
}
