package com.smartmux.banglaebook.plus.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
public class FileUtil {

	public static String getMediaDuration(Context context,File file){
		MediaPlayer mp = MediaPlayer.create(context, Uri.fromFile(file));
		int duration = mp.getDuration();
		mp.release();
		return String.format("%d:%d", 
		        TimeUnit.MILLISECONDS.toMinutes(duration),
		        TimeUnit.MILLISECONDS.toSeconds(duration) - 
		        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
		    );
	}
	
	public static Bitmap getThumbImage(File file){
		final int THUMBSIZE = 64;

		Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), 
		                    THUMBSIZE, THUMBSIZE);
		return thumbImage;
	}
	
	public static String getPhotoDimensions(File file){
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		int width = options.outWidth;
		int height = options.outHeight;
		
		return width +"PX , "+ height+"PX";
	}

	public static String getDynamicFileName()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
		return sdf.format(new Date());
	}

	public static String getJustCurrentDateTime()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return sdf.format(new Date());
	}
	
	public static String getDateTime(File file)
	{
		Date lastModified = new Date(file.lastModified()); 
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return formatter.format(lastModified);
	}
	
	public static String getSizeInFormat(long size) {
        if (size <= 0)
            return "0 byte";
        final String[] units = new String[] { "bytes", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
	
	public static long getDirectorySize(File dir) {

	    if (dir.exists()) {
	        long result = 0;
	        File[] fileList = dir.listFiles();
	        for(int i = 0; i < fileList.length; i++) {
	            if(fileList[i].isDirectory()) {
	                result += getDirectorySize(fileList [i]);
	            } else {
	                result += fileList[i].length();
	            }
	        }
	        return result;
	    }
	    return 0;
	}
	
	
}
