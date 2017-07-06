package com.smartmux.videodownloader.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.media.MediaMetadataRetriever;

public class VideoInfoUtils {

	public static String getDuration(String filePath) {

		MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		metaRetriever.setDataSource(filePath);
		long duration = Integer.parseInt(metaRetriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

		int h = (int) (duration / 3600000);
		int m = (int) (duration - h * 3600000) / 60000;
		int s = (int) (duration - h * 3600000 - m * 60000) / 1000;
		String hh = h < 10 ? "0" + h : h + "";
		String mm = m < 10 ? "0" + m : m + "";
		String ss = s < 10 ? "0" + s : s + "";

		String time = "" + hh + ":" + mm + ":" + ss;

		return time;
	}

	public static String getSizeInFormat(long size) {
		if (size <= 0)
			return "0 byte";
		final String[] units = new String[] { "bytes", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size
				/ Math.pow(1024, digitGroups))
				+ " " + units[digitGroups];
	}

	public static String getCurrentDate() {

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy");
		String strDate = sdf.format(c.getTime());
		return strDate;

	}

	public static String getCurrentTime() {

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
		String strTime = sdf.format(c.getTime());
		return strTime;

	}
}
