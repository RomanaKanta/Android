package com.smartmux.trackyourkids.mapactivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smartmux.trackyourkids.R;
import com.smartmux.trackyourkids.fragment.AppData;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mGoogleMap;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Map");

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        MapsInitializer.initialize(MapActivity.this);

            for (int i=0; i< AppData.dataList.size(); i++){
                setMarker(AppData.dataList.get(i));
            }

        LatLng currentPlace = new LatLng(23.815789, 90.415684);

        MarkerOptions marker = new MarkerOptions()
                .position(currentPlace)
                .title("PLACE A")
                .snippet("DISTANCE 2.0 KM")
                ;

        mGoogleMap.addMarker(marker);

//            LatLng currentPlace = new LatLng(AppData.lat, AppData.lon);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPlace,
                    10.0f);
            mGoogleMap.animateCamera(cameraUpdate);

        mGoogleMap.setInfoWindowAdapter(new MyInfoWindowAdapter(MapActivity.this));

    }

    private void setMarker(ModelClass item){

        double lat = Double.parseDouble(item.getLat());
        double lon = Double.parseDouble(item.getLon());

        LatLng mPlace = new LatLng(lat, lon);

        // create marker
        MarkerOptions marker = new MarkerOptions()
                .position(mPlace)
                .title(item.getName())
                .snippet(item.getDistance());

        // adding marker
        mGoogleMap.addMarker(marker);
    }

}
