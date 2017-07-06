package com.smartmux.couriermoc.activity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.smartmux.couriermoc.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tanvir-android on 8/1/16.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mGoogleMap;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    	Location mLocation = null;
	LatLng mCurrentPosition = null;
	String strtext = "";
	LatLng mPlaceA = null;
	LatLng mPlaceB = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setIcon(R.drawable.drawer);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Map Activity");
        geocodeAddress("DOHS Baridhara Convention Center, Dhaka, Dhaka Division");
        polylines = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

//        // Add a marker in Sydney, Australia, and move the camera.
//        LatLng sydney = new LatLng(-34, 151);
//        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        MapsInitializer.initialize(MapActivity.this);

        mPlaceA = new LatLng(23.8582805, 90.4007752);
        mPlaceB = new LatLng(23.8030226, 90.36198020000006);


        // create marker
        MarkerOptions markerA = new MarkerOptions().position(mPlaceA).title(
                "Place A");
        MarkerOptions markerB = new MarkerOptions().position(mPlaceB).title(
                "Place B");

        // adding marker
        mGoogleMap.addMarker(markerA);
        mGoogleMap.addMarker(markerB);
        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mPlaceA,
                10.0f);
        mGoogleMap.animateCamera(cameraUpdate);

if(mPlaceA!=null && mPlaceB!=null) {
    Routing routing = new Routing.Builder()
            .travelMode(AbstractRouting.TravelMode.DRIVING)
            .withListener(this)
            .alternativeRoutes(true)
            .waypoints(mPlaceA, mPlaceB)
//            .waypoints(start, waypoint, end)
            .build();
    routing.execute();
}

    }


	private void drawMarker(Location location) {
		mGoogleMap.clear();
		mCurrentPosition = new LatLng(location.getLatitude(),
				location.getLongitude());
		mGoogleMap.addMarker(new MarkerOptions()
				.position(mCurrentPosition)
				.snippet(
						" Latitude:" + location.getLatitude() + " Longitude:"
								+ location.getLongitude())
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.title("ME"));
	}


    @Override
    public void onRoutingFailure(RouteException e) {

        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingStart() {

    }

    private ArrayList<Polyline> polylines;
//    private static final int[] COLORS = new int[]{R.color.colorPrimaryDark,R.color.colorPrimary,
//            R.color.colorAccent,R.color.iron,R.color.white};


    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int j) {

//        CameraUpdate center = CameraUpdateFactory.newLatLng(mPlaceA);
//        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
//
//
//        mGoogleMap.moveCamera(center);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mPlaceA,
                10.0f);
        mGoogleMap.animateCamera(cameraUpdate);


        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {



            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(ContextCompat.getColor(MapActivity.this, R.color.green));
            polyOptions.width(5 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mGoogleMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Log.e("DISTENCE", "Route "+ (i+1) +": distance - "+
                    route.get(i).getDistanceValue()/1000+"KM    : duration - "+ route.get(i).getDurationValue());
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(mPlaceA);
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.user));
        mGoogleMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(mPlaceB);
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.truck));
        mGoogleMap.addMarker(options);
    }

    @Override
    public void onRoutingCancelled() {
        Log.i("LOG_TAG", "Routing was cancelled.");

    }

//    private String convertTime(long millis){
//
//
//        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
//                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
//                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
//    return hms;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();

        }
        return (super.onOptionsItemSelected(menuItem));
    }
    double mLatitude = 0;
    double mLongitude = 0;
    public  void geocodeAddress(String addressStr) {
        Address address = null;
        List<Address> addressList = null;
        try {
            if (!TextUtils.isEmpty(addressStr)) {
                addressList = new Geocoder(MapActivity.this).getFromLocationName(addressStr, 5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != addressList && addressList.size() > 0) {
            address = addressList.get(0);
        }
        if (null != address && address.hasLatitude()
                && address.hasLongitude()) {
            mLatitude = address.getLatitude();
            mLongitude = address.getLongitude();
            Log.e("LATLONG", "lat : " + mLatitude +"  long : " + mLongitude);
        }

    }
}