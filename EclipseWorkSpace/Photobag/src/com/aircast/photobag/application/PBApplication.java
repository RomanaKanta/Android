package com.aircast.photobag.application;

import java.io.File;
import java.util.Random;
import java.util.concurrent.RejectedExecutionException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.api.PBTaskGetToken;
import com.aircast.photobag.api.c2dm.C2DMConstant;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;

/**
 * Application class
 * 
 * @author lent5
 * 
 */
public class PBApplication extends Application {
	private static Context sContext;
	private static Toast mAppToast;
	private static String SDK_VERSION = "Android OS " + Build.VERSION.RELEASE;
	private static String VERSION_CODE = "1.0";
	public static String VERSION = "android1.0";
	public static String TARGET_VERSION = "1.0.0";
	public static String LANG = "ja";// Locale.getDefault().toString();
	private static PBTaskGetToken sTaskGetToken;
	
	private static String DEVICE_ID = "";
	private static String ANDROID_ID = "";
	
	public static String OS_VERSION = "1.0.0";


	@Override
	public void onCreate() {
		/*
		 * if (PBConstant.ENABLE_STRICT_MODE) { StrictMode.setThreadPolicy(new
		 * StrictMode.ThreadPolicy.Builder()
		 * .detectNetwork().penaltyLog().penaltyDeath().build());
		 * StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		 * .detectAll().penaltyLog().penaltyDeath().build()); }
		 */
		super.onCreate();
		sContext = this.getApplicationContext();

		try {
			PackageInfo manager = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			
			TARGET_VERSION  = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionName;
			OS_VERSION = Build.VERSION.RELEASE;
			VERSION_CODE = manager.versionName;
			VERSION = "android" + VERSION_CODE;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			final TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			DEVICE_ID = tm.getDeviceId();
			ANDROID_ID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		} catch (Exception e) {}
		
		
		if (TextUtils.isEmpty(DEVICE_ID)) {
			DEVICE_ID = "478193";	// FOR NON TELEPHONE DEVICE
		}
		
		if (TextUtils.isEmpty(ANDROID_ID)) {
			ANDROID_ID = createRandomIntString();
		}

		// get token from server
		startGetParameters();

		// @lent register push notification to C2DM service
		// register();

		// 20120222 added by NhatVT, prevent urgly case app crash when
		// downloading photo <S>
		// reset current photo position
		PBPreferenceUtils.saveIntPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME,
				PBConstant.PREF_RESUME_CURRENT_DOWNLOAD_POS, 0);
		// reset current progress dialog pos
		PBPreferenceUtils.saveIntPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_RESUME_CURRENT_PROGRESS,
				0);
	}
	
	private String createRandomIntString() {
		try {
			Random rdm = new Random(System.currentTimeMillis());
            return "" + Math.abs(rdm.nextInt()) % 1000000;
		}
		catch (Exception e) {}		
		return "9527";
	}

	protected void parseXML() {
		try {
			// will getting info from "/system/etc/media_profiles.xml"
			File f = new File("/system/etc/media_profiles.xml");
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			FileFormatHandler handler = new FileFormatHandler();
			saxParser.parse(f, handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class FileFormatHandler extends DefaultHandler {

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			// super.startElement(uri, localName, qName, attributes);
			if (localName.equals("EncoderOutputFileFormat")) {
				String supportFileType = attributes.getValue("name");
				Log.i("mapp", "--supportFileType = " + supportFileType);
				// PBPreferenceUtils.saveBoolPref(sContext,
				// PBConstant.PREF_NAME, "", true);
			}
		}
	}

	public static void startGetParameters() {
		String token = PBPreferenceUtils.getStringPref(
				PBApplication.getBaseApplicationContext(), 
                PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
        if (TextUtils.isEmpty(token)) {
			token = PBGeneralUtils.getTokenFromCacheFolder();			
		} else {
			PBGeneralUtils.saveTokenToCacheFolder();
		}
        if(!TextUtils.isEmpty(token)) return;
		
		if (sTaskGetToken == null
				|| (sTaskGetToken != null && (sTaskGetToken.getStatus() != AsyncTask.Status.RUNNING))) {
			sTaskGetToken = new PBTaskGetToken();
			try {
				sTaskGetToken.execute();
			} catch (RejectedExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * set handler for task get token from server
	 * 
	 * @param handler
	 */
	public static void setHandlerToTokenUpdateTask(Handler handler) {
		if (sTaskGetToken != null) {
			sTaskGetToken.setHandler(handler);
		}
	}

	/**
	 * stop task get token if application close
	 */
	public static void stopTokenUpdateTask() {
		if (sTaskGetToken != null
				&& sTaskGetToken.getStatus() == AsyncTask.Status.RUNNING) {
			try {
				sTaskGetToken.cancel(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * return the version number of application
	 * 
	 * @return
	 */
	public static String getVersionParams() {
		StringBuilder basicParams = new StringBuilder("version=" + VERSION);
		basicParams.append("&lang=ja");// + Locale.getDefault().getLanguage());
		// Log.e(PBConstant.TAG, "getVersionParams: " + basicParams.toString());
		basicParams.append("&platform=" + "android");
		return basicParams.toString();
	}

	/**
	 * return the user agent string
	 * 
	 * @return
	 */
	public static String getUserAgentParams() {
		String agent_name = getBaseApplicationContext().getString(
				R.string.useragent);
		StringBuilder basicParams = new StringBuilder(agent_name + " "
				+ VERSION_CODE);
		basicParams.append(" (Android;");
		basicParams.append(SDK_VERSION);
		basicParams.append(";");
		basicParams.append(LANG);
		basicParams.append(")");
		// Log.e(PBConstant.TAG, "User-Agent: " + basicParams.toString());
		return basicParams.toString();
	}

	/**
	 * get Context for application
	 */
	public static Context getBaseApplicationContext() {
		return sContext;
	}

	// 20120208 added by NhatVT <S>
	/**
	 * Convenient for showing Toast message throughout application.
	 * 
	 * @param msg
	 *            message want to show.
	 */
	public static void makeToastMsg(String msg) {
		if (mAppToast == null) {
			mAppToast = Toast.makeText(sContext, msg, Toast.LENGTH_SHORT);
			mAppToast.show();
		} else {
			mAppToast.setText(msg);
			// mAppToast.cancel();
			mAppToast.show();
		}
	}

	// 20120208 added by NhatVT <E>

	/**
	 * check Internet is connected or disconnected
	 * 
	 * @return boolean has connected is true
	 */
	public static boolean hasNetworkConnection() {
		if (sContext == null)
			return false;
		
		// Kayac san source code for network check has been commented by Atik
		
		/*boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) sContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo[] netInfo = cm.getAllNetworkInfo();
			if (netInfo != null) {
				for (NetworkInfo ni : netInfo) {
					if (ni.getTypeName().equalsIgnoreCase("WIFI"))
						if (ni.isConnected())
							haveConnectedWifi = true;
					if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
						if (ni.isConnected())
							haveConnectedMobile = true;
				}
			}
		}

	   return haveConnectedWifi || haveConnectedMobile;*/
		
	   ConnectivityManager cm = (ConnectivityManager) sContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
	   
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo == null) {
            return false;
        }

        if (!networkInfo.isAvailable()) {
            return false;
        } else if (!cm.getActiveNetworkInfo().isConnected()) {
            return false;
        } else {
            return true;
        }
	}

	/*************************************************************************************
	 ************************ related c2dm support Push Notification *******************
	 *************************************************************************************/
	/**
	 * unregister c2dm service for PN with Android cloud Service
	 */
	public void unregister() {
		Intent unregIntent = new Intent(C2DMConstant.PN_C2DM_UNREGISTER);
		unregIntent.putExtra("app",
				PendingIntent.getBroadcast(this, 0, new Intent(), 0));
		startService(unregIntent);
	}

	/**
	 * register c2dm service for PN with Android cloud Service
	 */
	public static void register() {
		Intent intent = new Intent(C2DMConstant.PN_C2DM_REGISTER);
		intent.putExtra("app",
				PendingIntent.getBroadcast(sContext, 0, new Intent(), 0));
		intent.putExtra("sender", C2DMConstant.PN_C2DM_SENDER_EMAIL);
		sContext.startService(intent);
	}

	/**
	 * display the Registration ID returned from C2DM service
	 * 
	 * @author @le.nguyen
	 * @param @param view
	 */
	public void showRegistrationId() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String string = prefs.getString(C2DMConstant.PN_C2DM_AUTH, "n/a");
		// Toast.makeText(this, string, 0).show();
		Log.d("C2DM RegId", string);
	}
	
	public static String getMobileCode() {
		
		Log.d("RANDOM", ANDROID_ID + DEVICE_ID);
		int dHash =  DEVICE_ID.hashCode();
		int aHash =  ANDROID_ID.hashCode();
		return dHash + "" + aHash;
 	}
	
	public static String getAppVersion() {
		return VERSION_CODE;
	}
}
