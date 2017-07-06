package com.smartmux.couriermoc.ServiceActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.modelclass.DeliverInfo;
import com.smartmux.couriermoc.utils.ProgressHUD;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tanvir-android on 7/26/16.
 */
public class FragmentReceiverDetail extends Fragment implements OnMapReadyCallback, RoutingListener {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public FragmentReceiverDetail() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentReceiverDetail newInstance(String param1, String param2) {
        FragmentReceiverDetail fragment = new FragmentReceiverDetail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private GoogleMap mGoogleMap;
    Location mLocation = null;
    LatLng mCurrentPosition = new LatLng(23.934854, 90.497614);
    LatLng mDestinationPosition = null;
    private ArrayList<Polyline> polylines;
    SupportMapFragment fragment;

    @Bind(R.id.recipient_name)
    TextView txtName;
    @Bind(R.id.textview_phone)
    TextView txtPhone;
    @Bind(R.id.deliver_to)
    TextView txDeliverTo;
    @Bind(R.id.instruction)
    TextView txtInstruction;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receiver_detail, container, false);
        ButterKnife.bind(this, view);

        txtName.setText(DeliverInfo.recipientName);
        txtPhone.setText(DeliverInfo.recipientPhone);
        txDeliverTo.setText(DeliverInfo.deliverTo);
        txtInstruction.setText(DeliverInfo.instruction);

//        if(DeliverInfo.deliverTo!= null && !DeliverInfo.deliverTo.equals("")){
            geocodeAddress(DeliverInfo.deliverTo);
//        }else {
//
//        }
//        geocodeAddress("DOHS Baridhara Convention Center, Dhaka, Dhaka Division");
        polylines = new ArrayList<>();

        FragmentManager fm = getChildFragmentManager();
         fragment = (SupportMapFragment) fm.findFragmentById(R.id.rec_map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.rec_map, fragment).commit();
        }

//        new SM_AsyncTaskForGetInfo().execute();

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleMap == null) {
            fragment.getMapAsync(this);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        try {
            java.lang.reflect.Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(googleMap!=null) {

            mGoogleMap = googleMap;

            MapsInitializer.initialize(getActivity());

//            mDestinationPosition = new LatLng(23.8030226, 90.36198020000006);

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean enabledGPS = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Check if enabled and if not send user to the GSP settings
            // Better solution would be to display a dialog and suggesting to
            // go to the settings
            if (!enabledGPS) {
                Toast.makeText(getActivity(), "GPS not available", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
            // Enabling MyLocation Layer of Google Map
            mGoogleMap.setMyLocationEnabled(true);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            mLocation = locationManager.getLastKnownLocation(provider);

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location arg0) {
                    // redraw the marker when get location update.
                    mGoogleMap.clear();
                    mCurrentPosition = new LatLng(mLocation.getLatitude(),
                            mLocation.getLongitude());

                    drawMarker();
                }

                @Override
                public void onProviderDisabled(String provider) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onProviderEnabled(String provider) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onStatusChanged(String provider, int status,
                                            Bundle extras) {
                    // TODO Auto-generated method stub
                }
            };
            locationManager.requestLocationUpdates(provider, 1000 * 30 * 1, 10,
                    (android.location.LocationListener) locationListener);


            drawMarker();

        }else{

            Toast.makeText(getActivity(),"Some problem occur in Map Initialize",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int j) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentPosition,
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
            polyOptions.color(ContextCompat.getColor(getActivity(), R.color.green));
            polyOptions.width(5 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mGoogleMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Log.e("DISTENCE", "Route " + (i + 1) + ": distance - " +
                    route.get(i).getDistanceValue() / 1000 + "KM    : duration - " + route.get(i).getDurationValue());
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(mCurrentPosition);
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.user));
        mGoogleMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(mDestinationPosition);
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.truck));
        mGoogleMap.addMarker(options);
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingCancelled() {
        Log.i("LOG_TAG", "Routing was cancelled.");
    }


    private void drawMarker() {

        // create marker
        MarkerOptions markerA = new MarkerOptions().position(mCurrentPosition).title(
                DeliverInfo.id);
        MarkerOptions markerB = new MarkerOptions().position(mDestinationPosition).title(
                "Recipient");

        // adding marker
        mGoogleMap.addMarker(markerA);
        mGoogleMap.addMarker(markerB);
        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentPosition,
                10.0f);
        mGoogleMap.animateCamera(cameraUpdate);

        if (mCurrentPosition != null && mDestinationPosition != null) {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(mCurrentPosition, mDestinationPosition)
//            .waypoints(start, waypoint, end)
                    .build();
            routing.execute();
        }
    }

    public  boolean geocodeAddress(String addressStr) {
        Address address = null;
        List<Address> addressList = null;
        try {
            if (!TextUtils.isEmpty(addressStr)) {
                addressList = new Geocoder(getActivity()).getFromLocationName(addressStr, 5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != addressList && addressList.size() > 0) {
            address = addressList.get(0);
        }
        if (null != address && address.hasLatitude()
                && address.hasLongitude()) {

            mDestinationPosition = new LatLng(address.getLatitude(), address.getLongitude());
            return true;
        }
        return false;

    }



    class SM_AsyncTaskForGetInfo extends AsyncTask<String, String, Boolean> {

        ProgressHUD mProgressHUD;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Log.e("onPreExecute", "call");
            mProgressHUD = ProgressHUD.show(getActivity(),
                    "Loading",true);

        }

        @Override
        protected Boolean doInBackground(String... params) {

            return  geocodeAddress(DeliverInfo.deliverTo);
        }

        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);

            if(result != null && result==true){

                drawMarker();

            }
            if (mProgressHUD.isShowing()) {

                mProgressHUD.dismiss();
            }
        }
    }

}
