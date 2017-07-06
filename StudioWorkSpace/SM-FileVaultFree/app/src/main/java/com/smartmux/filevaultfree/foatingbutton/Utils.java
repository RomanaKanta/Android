package com.smartmux.filevaultfree.foatingbutton;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.io.File;
import java.util.Random;

public class Utils {

	public static boolean isSdCardAvailable() {
		Boolean isSDPresent = Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED);
		return isSDPresent;
	}

	public static boolean isDirectoryExists() {

		String root = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		File dir = new File(root + "/.VideoTrimmer");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		return dir.exists();
	}
	
	

	public static String setVideoFileName() {
		String root = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		File dir = new File(root + "/.VideoTrimmer");

		if (!dir.exists()) {
			dir.mkdirs();
		}
		Random generator = new Random();
		int n = 10000;
		n = generator.nextInt(n);
		int m = 50000;
		m = generator.nextInt(m);

		return dir.getAbsolutePath()+"/video_"+n + "_" + m + ".mp4";

	}
	
	public static String getVideoFramesName() {
		String root = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		File dir = new File(root + "/.VideoTrimmer/Thumb");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		return dir.getAbsolutePath()+"/frame-%3d.jpeg";

	}
	
	public static String getVideoFrameDirectory(String videoFileName) {
		System.out.println(videoFileName);
		String root = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		File dir = new File(root + "/.VideoTrimmer/Thumb");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		return dir.getAbsolutePath()+"/"+videoFileName.replace(".mp4", ".jpeg");

	}

	public static String getVideoThumbDirectory() {
		String root = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		File dir = new File(root + "/.VideoTrimmer/Thumb");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		return dir.getAbsolutePath();

	}

	/**
	 * Convert Dp to Pixel
	 */
	public static int dpToPx(float dp, Resources resources) {
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				resources.getDisplayMetrics());
		return (int) px;
	}

	public static int getRelativeTop(View myView) {
		// if (myView.getParent() == myView.getRootView())
		if (myView.getId() == android.R.id.content)
			return myView.getTop();
		else
			return myView.getTop() + getRelativeTop((View) myView.getParent());
	}

	public static int getRelativeLeft(View myView) {
		// if (myView.getParent() == myView.getRootView())
		if (myView.getId() == android.R.id.content)
			return myView.getLeft();
		else
			return myView.getLeft()
					+ getRelativeLeft((View) myView.getParent());
	}

	public static long getDuration(String filesource) {

		MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		metaRetriever.setDataSource(filesource);
		int duration = Integer.parseInt(metaRetriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
		return duration;
	}

	public static int[] getVideoResulation(String filesource) {

		MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		metaRetriever.setDataSource(filesource);
		int[] res = new int[2];
		res[0] = Integer
				.parseInt(metaRetriever
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
		res[1] = Integer
				.parseInt(metaRetriever
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

		Log.d("res ", res[0] + " " + res[1]);
		return res;
	}

	public static String getLongToTime(long milliseconds) {

		String finalTimerString = "";
		String secondsString = "";

		// Convert total duration into time
		int hours = (int) (milliseconds / (1000 * 60 * 60));
		int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
		int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
		// Add hours if there
		if (hours > 0) {
			finalTimerString = hours + ":";
		}

		// Prepending 0 to seconds if it is one digit
		if (seconds < 10) {
			secondsString = "0" + seconds;
		} else {
			secondsString = "" + seconds;
		}

		finalTimerString = finalTimerString + minutes + ":" + secondsString;

		// return timer string
		return finalTimerString;
	}

	/**
	 * Function to get Progress percentage
	 * 
	 * @param currentDuration
	 * @param totalDuration
	 * */
	public static int getProgressPercentage(long currentDuration,
			long totalDuration) {
		Double percentage = (double) 0;

		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);

		// calculating percentage
		percentage = (((double) currentSeconds) / totalSeconds) * 100;

		// return percentage
		return percentage.intValue();
	}

	/**
	 * Function to change progress to timer
	 * 
	 * @param progress
	 *            -
	 * @param totalDuration
	 *            returns current duration in milliseconds
	 * */
	public static int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double) progress) / 100) * totalDuration);

		// return current duration in milliseconds
		return currentDuration * 1000;
	}

	public static String getTimeForTrackFormat(int timeInMills,
			boolean display2DigitsInMinsSection) {
		int minutes = (timeInMills / (60 * 1000));
		int seconds = (timeInMills - minutes * 60 * 1000) / 1000;
		String result = display2DigitsInMinsSection && minutes < 10 ? "0" : "";
		result += minutes + ":";
		if (seconds < 10) {
			result += "0" + seconds;
		} else {
			result += seconds;
		}
		return result;
	}
	
	public static Bitmap createThumbnailAtTime(String filePath, int timeInSeconds){
		   MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
		   mMMR.setDataSource(filePath);
		   //api time unit is microseconds
		   return mMMR.getFrameAtTime(timeInSeconds*1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
		}
}
