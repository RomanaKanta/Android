package smartmux.ntv.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ShareCompat.IntentBuilder;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class Utils {


	/*public static void setBadgeCount(Context context, LayerDrawable icon, String count) {

		BadgeDrawable badge;

		// Reuse drawable if possible
		Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
		if (reuse != null && reuse instanceof BadgeDrawable) {
			badge = (BadgeDrawable) reuse;
		} else {
			badge = new BadgeDrawable(context);
		}

		badge.setCount(count);
		icon.mutate();
		icon.setDrawableByLayerId(R.id.ic_badge, badge);
	}*/


	public static final int getColor(Context context, int id) {
		final int version = Build.VERSION.SDK_INT;
		if (version >= 23) {
			return ContextCompat.getColor(context, id);
		} else {
			return context.getResources().getColor(id);
		}
	}

	public static final String md5(final String s) {
		final String MD5 = "MD5";
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest
					.getInstance(MD5);
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte aMessageDigest : messageDigest) {
				String h = Integer.toHexString(0xFF & aMessageDigest);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}


	public static final Drawable getDrawable(Context context, int id){

		Drawable drawable = null;


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			drawable = context.getResources().getDrawable(id, context.getTheme());
		} else {
			drawable =  context.getResources().getDrawable(id);
		}


		return drawable;
	}

	public static final Drawable getDrawable(Context context, int id,int colorFilter){

		Drawable drawable = null;


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			drawable = context.getResources().getDrawable(id, context.getTheme());
		} else {
			drawable =  context.getResources().getDrawable(id);
		}

		drawable.setColorFilter(getColor(context,colorFilter), PorterDuff.Mode.SRC_ATOP);

		return drawable;
	}

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
		String deviceName = Build.MODEL;
		String deviceMan = Build.MANUFACTURER;

		OS_VERSION = Build.VERSION.RELEASE;

		return deviceName + " " + deviceMan + "-" + OS_VERSION;
	}


	
	public static String getUID(Context context) {
	    final String macAddr, androidId;

	    WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    WifiInfo wifiInf = wifiMan.getConnectionInfo();

	    macAddr = wifiInf.getMacAddress();
	    androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    //UUID deviceUuid = new UUID(androidId.hashCode(), macAddr.hashCode());
	    
	   // Log.d("UUID", deviceUuid.toString());
	    return "Emulator-9998";
	    // Maybe save this: deviceUuid.toString()); to the preferences.
	}



	
	public static String getLocalCountryName() {
		return Locale.getDefault().getCountry();
	}

	public static String getLocalLanguage() {

		return Locale.getDefault().getLanguage();
	}



	public static void startShareIntentChooser(Activity activity, String message, int titleResId) {
		IntentBuilder ib = IntentBuilder.from(activity);
		ib.setText(message);
		ib.setChooserTitle(titleResId);
		ib.setType("text/plain");
		try {
			ib.startChooser();
		} catch (ActivityNotFoundException e) {
			// no activity available to handle the intent
		}
	}

}
