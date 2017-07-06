package com.smartmux.weatherapp;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkChecking {
	 
		private static final String LOG_TAG = "NetworkChangeReceiver";
		private boolean isConnected = false;
		Context mContext =  null;
		
		public NetworkChecking(Context context){
			super();
			isNetworkAvailable(context);
			isGpsEnable(context);
		}
		
		
		public boolean isNetworkAvailable(Context context) {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							if (!isConnected) {
								Log.v(LOG_TAG, "Now you are connected to Internet!");
								isConnected = true;
								// do your processing here ---
								// if you need to post any data to the server or get
								// status
								// update from the server
							}
							return true;
						}
					}
				}
			}
			Log.v(LOG_TAG, "You are not connected to Internet!");
			isConnected = false;
			return false;
		}
		
		public boolean isGpsEnable(Context context) {
			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				return true;
			} else {
				return false;
			}
		}
		
//		public void showSettingsAlert(Context context) {
//			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//					context);
//
//			// Setting Dialog Title
//			alertDialog.setTitle("GPS is settings");
//
//			// Setting Dialog Message
//			alertDialog
//					.setMessage("GPS is not enabled. Do you want to go to settings menu to enable gps?");
//
//			// Setting Icon to Dialog
//			// alertDialog.setIcon(R.drawable.delete);
//
//			// On pressing Settings button
//			alertDialog.setPositiveButton("Settings",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int which) {
//							Intent intent = new Intent(
//									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//							context.startActivity(intent);
//							dialog.cancel();
//
//						}
//					});
//
//			// on pressing cancel button
//			alertDialog.setNegativeButton("Cancel",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int which) {
//							dialog.cancel();
//
//						}
//					});
//
//			// Showing Alert Message
//			alertDialog.show();
//		}

	}