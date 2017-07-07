package com.smartmux.trackyourkids.mapactivities;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.UUID;
/**
 * Created by Romana on 7/3/17.
 */
public class Utils {

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public static boolean isOnline(Context ctx) {
        try {
            ConnectivityManager cm = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

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

//	public static String getUID(Context context) {
//		String DEVICE_ID = "";
//		String ANDROID_ID = "";
//		try {
//			final TelephonyManager tm = (TelephonyManager) context
//					.getSystemService(Context.TELEPHONY_SERVICE);
//			DEVICE_ID = tm.getDeviceId();
//			ANDROID_ID = Secure.getString(context.getContentResolver(),
//					Secure.ANDROID_ID);
//		} catch (Exception e) {
//		}
//
//		if (TextUtils.isEmpty(DEVICE_ID)) {
//			DEVICE_ID = "478193"; // FOR NON TELEPHONE DEVICE
//			generateDeviceId(context);
//		}
//
//		return DEVICE_ID + "-" + ANDROID_ID;
//	}

    public static String getUID(Context context) {
        final String macAddr, androidId;

        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();

        macAddr = wifiInf.getMacAddress();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), macAddr.hashCode());

        // Log.d("UUID", deviceUuid.toString());
        return deviceUuid.toString();
        // Maybe save this: deviceUuid.toString()); to the preferences.
    }



    public static String getLocalCountryName() {
        return Locale.getDefault().getCountry();
    }

    public static String getLocalLanguage() {

        return Locale.getDefault().getLanguage();
    }

    public static boolean isMobileOrWifiConnectivityAvailable(Context ctx) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;


        try {
            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected()) {
                        haveConnectedWifi = true;
                    }
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected()) {
                        haveConnectedMobile = true;
                    }
            }
        } catch (Exception e) {
            System.out.print("[ConnectionVerifier] inside isInternetOn() Exception is : " + e.toString());
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}
