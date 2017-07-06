package com.smartmux.videodownloader.utils;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class NetworkChecking {

	private static final String LOG_TAG = "NetworkChangeReceiver";
	private boolean isConnected = false;
	Context mContext = null;

	public NetworkChecking(Context context) {
		super();
		this.mContext = context;
		isNetworkAvailable(mContext);

	}

	public boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		boolean wifiEnabled = wifiManager.isWifiEnabled();

		NetworkInfo mobileInfo = connectivity
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

//		boolean mobileEnabled = mobileInfo.getState() == NetworkInfo.State.CONNECTED;
//		String reason = mobileInfo.getReason();

//		boolean mobileDisabled = mobileInfo.getState() == NetworkInfo.State.DISCONNECTED
//				&& (reason == null || reason.equals("specificDisabled"));
		
		String threeG = SMSharePref.get3DDownload(context);

		// if (threeG.equals(SMConstant.on)) {

		if (wifiEnabled) {
			// wifi is enabled
		
			isConnected = true;
			return true;
			
		} else {
			if (threeG.equals(SMConstant.on)) {
				if (mobileInfo.getState() == NetworkInfo.State.CONNECTED) {
					
//					Toast.makeText(mContext, "3G available", 1000).show();
					isConnected = true;
					return true;

				} else{
//					Toast.makeText(mContext, "3G not available", 1000).show();
				}
			}
			
//			Toast.makeText(mContext, "wifi not available", 1000).show();
		}
//		 if (connectivity != null) {
//		 NetworkInfo[] info = connectivity.getAllNetworkInfo();
//		 if (info != null) {
//		 for (int i = 0; i < info.length; i++) {
//		 if (info[i].getState() == NetworkInfo.State.CONNECTED) {
//		 if (!isConnected) {
//		 Log.v(LOG_TAG, "Now you are connected to Internet!");
//		 isConnected = true;
//		 // do your processing here ---
//		 // if you need to post any data to the server or get
//		 // status
//		 // update from the server
//		 }
//		 return true;
//		 }
//		 }
//		 }
//		 }
		Log.v(LOG_TAG, "You are not connected to Internet!");
		isConnected = false;
		return false;
	}

	public Boolean isMobileDataEnabled() {
		Object connectivityService = mContext
				.getSystemService(mContext.CONNECTIVITY_SERVICE);
		ConnectivityManager cm = (ConnectivityManager) connectivityService;

		try {
			Class<?> c = Class.forName(cm.getClass().getName());
			Method m = c.getDeclaredMethod("getMobileDataEnabled");
			m.setAccessible(true);
			return (Boolean) m.invoke(cm);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}