package com.smartmux.trackfriends.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class Utils {

	public static String loadJSONFromAsset(Context context, String fileName) {
		String json = null;
		try {

			InputStream is = context.getAssets().open(fileName);

			int size = is.available();

			byte[] buffer = new byte[size];

			is.read(buffer);

			is.close();

			json = new String(buffer, "UTF-8");

		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return json;

	}

	/*
	 * Network available check function
	 */
	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		boolean isAvailable = false;
		if (networkInfo != null && networkInfo.isConnected()) {
			isAvailable = true;

		}
		return isAvailable;
	}

	/*
	 * Get application package name
	 */

	public static String getAppPackageName(Context context) {

		return context.getApplicationContext().getPackageName();
	}

	/*
	 * Get application version number
	 */

	public static String getAppVersionNumber(Context context) {

		String version = "1.0";
		try {
			version = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "app-" + version;
	}

	/*
	 * Get application platfrom
	 */
	public static String getAppPlatfrom() {

		return "android";
	}

	public static String getDeviceModel() {
		String OS_VERSION = "1.0.0";
		String deviceName = android.os.Build.MODEL;
		String deviceMan = android.os.Build.MANUFACTURER;

		OS_VERSION = Build.VERSION.RELEASE;

		return deviceName + " " + deviceMan + "-" + OS_VERSION;
	}

	public static String getUID(Context context) {
		String DEVICE_ID = "";
		String ANDROID_ID = "";
		try {
			final TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			DEVICE_ID = tm.getDeviceId();
			ANDROID_ID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		} catch (Exception e) {
		}

		if (TextUtils.isEmpty(DEVICE_ID)) {
			DEVICE_ID = "478193"; // FOR NON TELEPHONE DEVICE
		}

		return DEVICE_ID+"-"+ANDROID_ID;
	}

}
