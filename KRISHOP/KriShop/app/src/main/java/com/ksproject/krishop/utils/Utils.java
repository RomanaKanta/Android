package com.ksproject.krishop.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

	static public void showSnackBar(View root, String messageBody){

		Snackbar bar = Snackbar.make(root, messageBody, Snackbar.LENGTH_LONG);
//                .setAction("Dismiss", new View.OnClickListener(){
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });

		bar.show();
	}


	public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
		int targetWidth = 80;
		int targetHeight = 80;
		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
				targetHeight,Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(targetBitmap);
		Path path = new Path();
		path.addCircle(((float) targetWidth - 1) / 2,
				((float) targetHeight - 1) / 2,
				(Math.min(((float) targetWidth),
						((float) targetHeight)) / 2),
				Path.Direction.CCW);

		canvas.clipPath(path);
		Bitmap sourceBitmap = scaleBitmapImage;
		canvas.drawBitmap(sourceBitmap,
				new Rect(0, 0, sourceBitmap.getWidth(),
						sourceBitmap.getHeight()),
				new Rect(0, 0, targetWidth, targetHeight), null);
		return targetBitmap;
	}

	public static final int getColor(Context context, int id) {
		final int version = Build.VERSION.SDK_INT;
		if (version >= 23) {
			return ContextCompat.getColor(context, id);
		} else {
			return context.getResources().getColor(id);
		}
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

	public static final Drawable getDrawable(Context context, int id){

		Drawable drawable = null;


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			drawable = context.getResources().getDrawable(id, context.getTheme());
		} else {
			drawable =  context.getResources().getDrawable(id);
		}


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
/*
	public static String getAppPackageName(Context context) {

		return context.getApplicationContext().getPackageName();
	}

	*//*
	 * Get application version number
	 *//*

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

	*//*
	 * Get application platfrom
	 *//*
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
	    final String macAddress, androidId;
	    androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		macAddress = getmacaddress(context);
	    UUID deviceUuid = new UUID(androidId.hashCode(), macAddress.hashCode());

		return deviceUuid.toString();
	}


	public  static  String getmacaddress (Context mContext) {
		String MacStr =  "" ;
		WifiManager wifiManager = (WifiManager) mContext
				.getSystemService (Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo ();
		if  (wifiInfo.getMacAddress () !=  null ) {
			MacStr = wifiInfo.getMacAddress();
		} else  {
			MacStr	=   "9999999" ;
		}

		return  MacStr;
	}


	public static String getLocalCountryName() {
		return Locale.getDefault().getCountry();
	}

	public static String getLocalLanguage() {

		return Locale.getDefault().getLanguage();
	}


	public static JSONObject getOIbj(String response) throws JSONException {
		JSONObject jObj = null;
		try {
			if(response != null){
				jObj = new JSONObject(response);
				jObj.put(Constant.responseCode, Constant.OK);
			}else{
				String content = PBAPIHelper.ERROR_DESC;
				int statusCode = ResponseHandle.CODE_INVALID_PARAMS;

				ResponseHandle.Response responseTemp = new ResponseHandle.Response(statusCode, content);
				jObj = new JSONObject(responseTemp.decription);
				jObj.put(Constant.responseCode, Constant.NG);
			}

			// Log.d("jObj", jObj.toString());
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
			jObj = new JSONObject();
			jObj.put(Constant.responseCode, Constant.NG);
		}

		return jObj;
	}*/

}
