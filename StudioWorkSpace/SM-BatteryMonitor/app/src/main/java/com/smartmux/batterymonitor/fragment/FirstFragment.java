package com.smartmux.batterymonitor.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smartmux.batterymonitor.progresscircle.ProgressCircle;
import com.smartmux.batterymonitor.utills.SM_Constants;
import com.smartmux.batterymonitor.utills.SM_Sharedpreferance;
import com.smartmux.batterymonitor.R;

public class FirstFragment extends Fragment {
	// Store instance variables
	@SuppressWarnings("unused")
	private String title;
	@SuppressWarnings("unused")
	private int page;
	
	ImageView firstImage,secondImage,thirdImage,forthImage = null;
	TextView tvwifi,tvbrightness,tvgps,tvblue = null;
	TextView tvwifipower ,tvbrightnesspower,tvgpspower,tvbluepower = null;
	ProgressCircle progressBarForWifi,progressBarForBrightness,progressBarForGPS,progressBarForBluetooth = null;
	
	int wifires, brightres,gpsresult,btresult = 0;
	String strWifiInfo = "";
	String gpsInfo = "";
	String bluetoothInfo = "";
	String brightinfo = "";
	WifiInfo connectionInfo = null;
	WifiManager wifiManager = null;
	SM_Sharedpreferance mSM_Sharedpreferance = null;
	ProgressBar mProgressbar = null;
	// Internet status flag
	ConnectivityManager connManager = null;
	NetworkInfo mWifi = null;
	LocationManager locationManager = null;
	final BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

	// newInstance constructor for creating fragment with arguments
	public static FirstFragment newInstance(int page, String title) {
		FirstFragment fragmentFirst = new FirstFragment();
		Bundle args = new Bundle();
		args.putInt("someInt", page);
		args.putString("someTitle", title);
		fragmentFirst.setArguments(args);
		return fragmentFirst;
	}

	// Store instance variables based on arguments passed
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		page = getArguments().getInt("someInt", 0);
		title = getArguments().getString("someTitle");
//		wifiManager = (WifiManager) getActivity().getSystemService(
//				Context.WIFI_SERVICE);
//		connectionInfo = wifiManager.getConnectionInfo();
//		connManager = (ConnectivityManager) getActivity().getSystemService(
//				Context.CONNECTIVITY_SERVICE);
//		mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//		locationManager = (LocationManager) getActivity().getSystemService(
//				Context.LOCATION_SERVICE);
		
	}
	
	

	
	// Inflate the view for the fragment based on layout XML
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_status, container, false);


		mProgressbar = (ProgressBar) view.findViewById(R.id.progressBar);

		mSM_Sharedpreferance = new SM_Sharedpreferance(getActivity());

		// /////////***************First Element**********///////////////
		

		 firstImage = (ImageView) view.findViewById(R.id.imageView1);
		 firstImage.setImageResource(R.drawable.wifi);
		

		 tvwifi = (TextView) view
				.findViewById(R.id.first_Content_TextView);
		

		 tvwifipower = (TextView) view
				.findViewById(R.id.power_For_First_Content);
	
		 progressBarForWifi = (ProgressCircle) view
				.findViewById(R.id.firstProgressBar);
		

		// /////////***************Second Element**********///////////////
		 brightres = mSM_Sharedpreferance.sharedPref.getInt(
				SM_Constants.brightnessusage, 0);

		 secondImage = (ImageView) view.findViewById(R.id.imageView2);
		secondImage.setImageResource(R.drawable.brigthness);
		secondImage.setColorFilter(Color.parseColor("#83B832"), PorterDuff.Mode.SRC_ATOP);
		
		 tvbrightness = (TextView) view
				.findViewById(R.id.second_Content_textView);

		

		 tvbrightnesspower = (TextView) view
				.findViewById(R.id.power_For_second_Content);
		

		 progressBarForBrightness = (ProgressCircle) view
				.findViewById(R.id.secondProgressBar);
		

		// /////////***************Third Element**********///////////////
	

		 thirdImage = (ImageView) view.findViewById(R.id.imageView3);
		thirdImage.setImageResource(R.drawable.gps);
		
		
		 tvgps = (TextView) view
				.findViewById(R.id.third_Content_textView);

		

		 tvgpspower = (TextView) view
				.findViewById(R.id.power_For_third_Content);
		
		progressBarForGPS = (ProgressCircle) view
				.findViewById(R.id.thirdProgressBar);
		

		// /////////***************Forth Element**********///////////////


		 forthImage = (ImageView) view.findViewById(R.id.imageView4);
		forthImage.setImageResource(R.drawable.bluetooth);

		 tvblue = (TextView) view
				.findViewById(R.id.forth_Content_textView);

		

		tvbluepower = (TextView) view
				.findViewById(R.id.power_For_forth_Content);
		

		progressBarForBluetooth = (ProgressCircle) view
				.findViewById(R.id.forthProgressBar);
	
	
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
		
		wifiManager = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);
		connectionInfo = wifiManager.getConnectionInfo();
		connManager = (ConnectivityManager) getActivity().getSystemService(
				Context.CONNECTIVITY_SERVICE);
		mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		
		setInformation();
	}
	
	void setInformation(){
		
		////////////FOR WIFI/////////////
		if (mWifi.isConnected()) {
			strWifiInfo = "WIFI : Enable";
			wifires = mSM_Sharedpreferance.sharedPref.getInt(
					SM_Constants.wifiusage, 0);
			firstImage.setColorFilter(Color.parseColor("#83B832"), PorterDuff.Mode.SRC_ATOP);
		} else {
			strWifiInfo = "WIFI : Disable";
			wifires = 0;
			firstImage.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
		}
		tvwifi.setText(strWifiInfo);
		
		tvwifipower.setText(getText(R.string.power) + " " + wifires + "%");
		
		progressBarForWifi.setVisibility(View.VISIBLE);
		progressBarForWifi.spin();
		int tmp1 = (int) (3.60 * wifires);
		progressBarForWifi.setProgress(tmp1);

		/////////FOR BRIGHTNESS/////////////
		int curBrightnessValue = android.provider.Settings.System.getInt(
				getActivity().getContentResolver(),
				android.provider.Settings.System.SCREEN_BRIGHTNESS, -1);
		brightinfo = "Brightness : " + Integer.toString(curBrightnessValue);
		tvbrightness.setText(brightinfo);
		
		tvbrightnesspower.setText(getText(R.string.power) + " " + brightres
				+ "%");
		
		progressBarForBrightness.setVisibility(View.VISIBLE);
		progressBarForBrightness.spin();
		int tmp2 = (int) (3.60 * brightres);
		progressBarForBrightness.setProgress(tmp2);
		
		/////////////FOR GPS//////////////
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			gpsInfo = "GPS : Enable";
			gpsresult = mSM_Sharedpreferance.sharedPref.getInt(
					SM_Constants.gpsusage, 0);
			thirdImage.setColorFilter(Color.parseColor("#83B832"), PorterDuff.Mode.SRC_ATOP);
		} else {
			gpsInfo = "GPS : Disable";
			gpsresult = 0 ;
			thirdImage.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
		}
		tvgps.setText(gpsInfo);
		
		tvgpspower.setText(getText(R.string.power) + " " + gpsresult + "%");
		
		progressBarForGPS.setVisibility(View.VISIBLE);
		progressBarForGPS.spin();
		int tmp3 = (int) (3.60 * gpsresult);
		progressBarForGPS.setProgress(tmp3);

		//////////////FOR BLUETOOTH///////////
		if (bluetooth.isEnabled()) {
			bluetoothInfo = "Bluetooth : Enable";
			btresult = mSM_Sharedpreferance.sharedPref.getInt(
					SM_Constants.bluetoothusage, 0);
			forthImage.setColorFilter(Color.parseColor("#83B832"), PorterDuff.Mode.SRC_ATOP);
		} else {
			bluetoothInfo = "Bluetooth : Disable";
			btresult =0;
			forthImage.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
		}
		tvblue.setText(bluetoothInfo);
		
		tvbluepower.setText(getText(R.string.power) + " " + btresult + "%");
		
		progressBarForBluetooth.setVisibility(View.VISIBLE);
		progressBarForBluetooth.spin();
		int tmp4 = (int) (3.60 * btresult);
		progressBarForBluetooth.setProgress(tmp4);
		
		mProgressbar.setVisibility(View.INVISIBLE);

	}
}
