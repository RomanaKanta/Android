package com.smartmux.trackfriends.fragment;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

//import com.google.android.gms.maps.CameraUpdate;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapView;
//import com.google.android.gms.maps.MapsInitializer;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
import com.smartmux.trackfriends.R;

public class FragmentMap extends Fragment {
	@SuppressWarnings("unused")
	private static final String TAG_FragmentMap = "FragmentMap";
//	MapView mapView;
//	GoogleMap mGoogleMap;
//	Location mLocation = null;
//	LatLng mCurrentPosition = null;
//	String strtext = "";
//	LatLng mPlaceA = null;
//	LatLng mPlaceB = null;

	public static FragmentMap newInstance() {
		FragmentMap fragment = new FragmentMap();
		return fragment;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.map_fragment, container, false);

//		// Gets the MapView from the XML layout and creates it
//		mapView = (MapView) view.findViewById(R.id.mapview);
//		mapView.onCreate(savedInstanceState);
//
//		// Gets to GoogleMap from the MapView and does initialization stuff
//		// mGoogleMap = mapView.getMap();
//		initilizeMap();
//		mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
//		mGoogleMap.setMyLocationEnabled(true);
//
//		if (savedInstanceState == null) {
//
//			Bundle mBundle = null;
//			mBundle = this.getArguments();
//
//			if (mBundle != null) {
//				strtext = getArguments().getString("message");
//				if (strtext.equalsIgnoreCase("standard")) {
//					mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//				} else if (strtext.equalsIgnoreCase("hybrid")) {
//					mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//				} else if (strtext.equalsIgnoreCase("satellite")) {
//					mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//				}
//			} else {
//				// on first time display view for first nav item
//				mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//			}
//		}
//
//		MapsInitializer.initialize(this.getActivity());
//
//		mPlaceA = new LatLng(23.8582805, 90.4007752);
//		mPlaceB = new LatLng(23.8030226, 90.36198020000006);
//		// create marker
//		MarkerOptions markerA = new MarkerOptions().position(mPlaceA).title(
//				"Place A");
//		MarkerOptions markerB = new MarkerOptions().position(mPlaceB).title(
//				"Place B");
//
//		// adding marker
//		mGoogleMap.addMarker(markerA);
//		mGoogleMap.addMarker(markerB);
//		// Updates the location and zoom of the MapView
//		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mPlaceA,
//				8.0f);
//		mGoogleMap.animateCamera(cameraUpdate);

		return view;
	}

	@Override
	public void onResume() {
//		mapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		mapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
//		mapView.onLowMemory();
	}

//	private void initilizeMap() {
//		if (mGoogleMap == null) {
//			mGoogleMap = mapView.getMap();
//
//			// check if map is created successfully or not
//			if (mGoogleMap == null) {
//				Toast.makeText(getActivity(), "Sorry! unable to create maps",
//						Toast.LENGTH_SHORT).show();
//			}
//		}
//	}

//	@SuppressWarnings("unused")
//	private void drawMarker(Location location) {
//		mGoogleMap.clear();
//		mCurrentPosition = new LatLng(location.getLatitude(),
//				location.getLongitude());
//		mGoogleMap.addMarker(new MarkerOptions()
//				.position(mCurrentPosition)
//				.snippet(
//						" Latitude:" + location.getLatitude() + " Longitude:"
//								+ location.getLongitude())
//				.icon(BitmapDescriptorFactory
//						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//				.title("ME"));
//	}

}
