package com.smartmux.filevault.lib;
//package com.smartmux.filevaultfree.lib;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.media.MediaPlayer;
//import android.media.ThumbnailUtils;
//import android.net.Uri;
//
//import java.io.File;
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.concurrent.TimeUnit;
//
//@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
//public class FileUtil {
//	SharedPreferences sharedPreferences = null;
//	public static final String PREFS_NAME = "MyPrefsFile";
//	
//	public static String getMediaDuration(Context context,File file){
//		MediaPlayer mp = MediaPlayer.create(context, Uri.fromFile(file));
//		int duration = mp.getDuration();
//		mp.release();
//		return String.format("%d:%d", 
//		        TimeUnit.MILLISECONDS.toMinutes(duration),
//		        TimeUnit.MILLISECONDS.toSeconds(duration) - 
//		        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
//		    );
//	}
//	
//	public static Bitmap getThumbImage(File file){
//		final int THUMBSIZE = 64;
//
//		Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), 
//		                    THUMBSIZE, THUMBSIZE);
//		return thumbImage;
//	}
//	
//	public static String getPhotoDimensions(File file){
//		
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//
//		//Returns null, sizes are in the options variable
//		BitmapFactory.decodeFile(file.getAbsolutePath(), options);
//		int width = options.outWidth;
//		int height = options.outHeight;
//		//If you want, the MIME type will also be decoded (if possible)
//		//String type = options.outMimeType;
//		
//		return width +"PX , "+ height+"PX";
//	}
//
//	// To save a Photo/Video Pick a dynamic name
//	public static String getDynamicFileName()
//	{
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
//		return sdf.format(new Date());
//	}
//	
//	// Get Just Current DateTime
//	public static String getJustCurrentDateTime()
//	{
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//		return sdf.format(new Date());
//	}
//	
//	public static String getDateTime(File file)
//	{
//		Date lastModified = new Date(file.lastModified()); 
//		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//		return formatter.format(lastModified);
//	}
//	
//	public static String getSizeInFormat(long size) {
//        if (size <= 0)
//            return "0 byte";
//        final String[] units = new String[] { "bytes", "KB", "MB", "GB", "TB" };
//        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
//        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
//    }
//	
//	public static long getDirectorySize(File dir) {
//
//	    if (dir.exists()) {
//	        long result = 0;
//	        File[] fileList = dir.listFiles();
//	        for(int i = 0; i < fileList.length; i++) {
//	            // Recursive call if it's a directory
//	            if(fileList[i].isDirectory()) {
//	                result += getDirectorySize(fileList [i]);
//	            } else {
//	                // Sum the file size in bytes
//	                result += fileList[i].length();
//	            }
//	        }
//	        return result;
//	    }
//	    return 0;
//	}
//	
//	public void keepTrack(Context context){
//		sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
//    	SharedPreferences.Editor editor = sharedPreferences.edit();  
//	    editor.putInt("return_code", 2);
//	    editor.commit();
//	}
//
//}
