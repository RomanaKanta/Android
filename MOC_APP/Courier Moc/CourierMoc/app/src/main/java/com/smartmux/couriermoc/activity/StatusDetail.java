package com.smartmux.couriermoc.activity;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.smartmux.couriermoc.dialogfragment.DialogCancelOrder;
import com.smartmux.couriermoc.modelclass.StatusInfo;
import com.smartmux.couriermoc.utils.JsonUtils;
import com.smartmux.couriermoc.utils.ProgressHUD;
import com.smartmux.couriermoc.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tanvir-android on 8/4/16.
 */
public class StatusDetail extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.textview_sender_address)
    TextView txtSenderAdd;
    @Bind(R.id.textview_receiver_address)
    TextView txtReceiverAdd;
    @Bind(R.id.textview_distance)
    TextView txtDistance;
    @Bind(R.id.textview_cost)
    TextView txtCost;
    @Bind(R.id.textview_type)
    TextView txtType;
    @Bind(R.id.textview_weight)
    TextView txtWeight;
    @Bind(R.id.textview_status)
    TextView txtStatus;
    @Bind(R.id.textview_date)
    TextView txtDate;
    @Bind(R.id.status_layer)
    LinearLayout statesLayer;
    String position;

    private GoogleMap mGoogleMap;
    Location mLocation = null;
    LatLng mCurrentPosition = null;
    String strtext = "";
    LatLng mPlaceA = null;
    LatLng mPlaceB = null;

    StatusInfo detailInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra("id")) {

            getSupportActionBar().setTitle(getIntent().getExtras().getString("id"));
            position = getIntent().getExtras().getString("position");

            new SM_AsyncTaskForGetDetailInfo().execute();



        }

    }

    private void setInfo(){

       txtSenderAdd.setText(detailInfo.getFrom());
        txtReceiverAdd.setText(detailInfo.getTo());
        txtDistance.setText(detailInfo.getDistance());
        txtCost.setText(detailInfo.getCost());
        txtType.setText(detailInfo.getType());
        txtWeight.setText(detailInfo.getWeight());
        txtStatus.setText(detailInfo.getStatus());
        txtDate.setText(detailInfo.getDate());

        if(position.equals("0")){

            setPendingContent();

        }else if(position.equals("1")){

            setOnGoingContent();

        }else if(position.equals("2")){

            setCompletedContent();

        }else if(position.equals("3")){

            setUnfulfilledContent();

        }else {

            txtStatus.setText("");
            statesLayer.setVisibility(View.GONE);

        }
    }

    private void setPendingContent(){

        statesLayer.setVisibility(View.GONE);
        txtStatus.setText(detailInfo.getStatus());
    }

    private void setOnGoingContent(){

        txtStatus.setText(detailInfo.getStatus());
//        if (statesLayer != null) {
//            statesLayer.removeAllViews();
//        }
//        LinearLayout onGoingLayer = (LinearLayout) View.inflate(StatusDetail.this,
//                R.layout.status_on_giong, null);
//        statesLayer.addView(onGoingLayer);

        polylines = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void setCompletedContent(){

        txtStatus.setText(detailInfo.getStatus());
        if (statesLayer != null) {
            statesLayer.removeAllViews();
        }
        LinearLayout completedLayer = (LinearLayout) View.inflate(StatusDetail.this,
                R.layout.status_complete, null);
        statesLayer.addView(completedLayer);

        ImageView signature = (ImageView) completedLayer.findViewById(R.id.image_signature);

        signature.setImageResource(R.drawable.signature);

    }

    private void setUnfulfilledContent(){

        txtStatus.setText(detailInfo.getStatus());
        if (statesLayer != null) {
            statesLayer.removeAllViews();
        }
        LinearLayout unfulfilledLayer = (LinearLayout) View.inflate(StatusDetail.this,
                R.layout.status_fail, null);
        statesLayer.addView(unfulfilledLayer);

        TextView reason = (TextView) unfulfilledLayer.findViewById(R.id.textview_reason);
        reason.setText(detailInfo.getComment());

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

//        // Add a marker in Sydney, Australia, and move the camera.
//        LatLng sydney = new LatLng(-34, 151);
//        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        MapsInitializer.initialize(StatusDetail.this);

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
            polyOptions.color(ContextCompat.getColor(StatusDetail.this, R.color.green));
            polyOptions.width(5 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mGoogleMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Log.e("DISTENCE", "Route " + (i + 1) + ": distance - " +
                    route.get(i).getDistanceValue() / 1000 + "KM    : duration - " + route.get(i).getDurationValue());
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.parcel_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(position.equals("2") || position.equals("3")) {
            menu.findItem(R.id.action_close).setVisible(false);
        }else {
            menu.findItem(R.id.action_close).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_close:

                new DialogCancelOrder().show(getSupportFragmentManager(), "Cancel_order_Dialog_Fragment");
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }


    class SM_AsyncTaskForGetDetailInfo extends AsyncTask<String, String, ArrayList<StatusInfo>> {

        ProgressHUD mProgressHUD;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Log.e("onPreExecute", "call");
            mProgressHUD = ProgressHUD.show(StatusDetail.this,
                    "Loading",true);

        }

        @Override
        protected ArrayList<StatusInfo> doInBackground(String... params) {

            return JsonUtils.getJsonArray(Utils.loadJSONFromAsset(StatusDetail.this, "deliverystatus.json"));
        }

        protected void onPostExecute(final ArrayList<StatusInfo> result) {
            super.onPostExecute(result);

            if(result != null ){


                for(int i=0; i< result.size();i++){

                    if(result.get(i).getOrderID().equals(getIntent().getExtras().getString("id"))){

                        detailInfo = result.get(i);

                        setInfo();

                    }

                }



            }
            if (mProgressHUD.isShowing()) {

                mProgressHUD.dismiss();
            }
        }
    }
}
