package com.smartmux.trackyourkids.mapactivities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.smartmux.trackyourkids.R;
import com.smartmux.trackyourkids.fragment.AppData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map2Activity extends AppCompatActivity {

    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton;
    private OnInfoWindowElemTouchListener infoButtonListener;
    Toolbar toolbar;
    GoogleMap map;
    MapFragment mapFragment;
    MapWrapperLayout mapWrapperLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Map");


        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
        map = mapFragment.getMap();

        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        mapWrapperLayout.init(map, getPixelsFromDp(this, 39 + 20));

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        this.infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_info_content, null);
        this.infoTitle = (TextView) infoWindow.findViewById(R.id.title);
        this.infoSnippet = (TextView) infoWindow.findViewById(R.id.snippet);
        this.infoButton = (Button) infoWindow.findViewById(R.id.button);

        setCustomMarkerView();

        final LatLng currentPlace = new LatLng(AppData.lat, AppData.lon);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button

                new ReadTask(currentPlace, marker.getPosition()).execute();
                Toast.makeText(Map2Activity.this, marker.getTitle() + "'s button clicked!", Toast.LENGTH_SHORT).show();
            }
        };
        this.infoButton.setOnTouchListener(infoButtonListener);


        map.setInfoWindowAdapter(new InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());
                infoButtonListener.setMarker(marker);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

        for (int i = 0; i < AppData.dataList.size(); i++) {
            setMarker(AppData.dataList.get(i));

//            new SM_AsyncTaskForGetMarkerImage(AppData.dataList.get(i)).execute();

        }

        MarkerOptions marker = new MarkerOptions()
                .position(currentPlace)
                .title("ME")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        map.addMarker(marker);

//            LatLng currentPlace = new LatLng(AppData.lat, AppData.lon);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPlace,
                10.0f);
        map.animateCamera(cameraUpdate);

    }

    View marker_root_view;
    ImageView iv_marker;


    private void setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(Map2Activity.this).inflate(R.layout.custom_marker, null);
        iv_marker = (ImageView) marker_root_view.findViewById(R.id.marker_image);
    }


    private void setMarker(final ModelClass item) {

        double lat = Double.parseDouble(item.getLat());
        double lon = Double.parseDouble(item.getLon());

        final LatLng mPlace = new LatLng(lat, lon);

//        Log.e("IMG_URL","" + item.getImage_url());

        Glide.with(Map2Activity.this)
                .load(item.getImage_url())
                .asBitmap()
                .override(34,23)
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                        iv_marker.setImageBitmap( resource );

                        // create marker
                        MarkerOptions marker = new MarkerOptions()
                                .position(mPlace)
                                .title(item.getName())
                                .snippet(item.getDistance())
                                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(Map2Activity.this, marker_root_view)));

                        // adding marker
                        map.addMarker(marker);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);

                        MarkerOptions marker = new MarkerOptions()
                                .position(mPlace)
                                .title(item.getName())
                                .snippet(item.getDistance())
                                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(Map2Activity.this, marker_root_view)));

                        // adding marker
                        map.addMarker(marker);

                    }
                });

//        // create marker
//        MarkerOptions marker = new MarkerOptions()
//                .position(mPlace)
//                .title(item.getName())
//                .snippet(item.getDistance())
//                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view)));
//
//        ;
//
//        // adding marker
//        map.addMarker(marker);
    }

    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;

    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private class ReadTask extends AsyncTask<String, Void, String> {

        LatLng origin;
        LatLng dest;

        public ReadTask(LatLng origin, LatLng dest) {
            this.origin = origin;
            this.dest = dest;
        }

        @Override
        protected String doInBackground(String... url) {
            // TODO Auto-generated method stub
            String data = "";
            try {
                MapHttpConnection http = new MapHttpConnection();
                data = http.readUr(getMapsApiDirectionsUrl(origin, dest));


            } catch (Exception e) {
                // TODO: handle exception
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }

    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {
            // TODO Auto-generated method stub
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(5);
                polyLineOptions.color(Color.BLUE);
            }

            map.addPolyline(polyLineOptions);

        }
    }

}
