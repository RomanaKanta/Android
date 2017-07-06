package com.smartmux.couriermoc.fragment;

import android.content.Context;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.Place;
import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.modelclass.UserInfo;
import com.smartmux.couriermoc.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tanvir-android on 7/26/16.
 */
public class FragmentArrangeDelivery extends Fragment implements RoutingListener {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public FragmentArrangeDelivery() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentArrangeDelivery newInstance(String param1, String param2) {
        FragmentArrangeDelivery fragment = new FragmentArrangeDelivery();
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

    @Bind(R.id.image1)
    ImageView image1;

    @OnClick(R.id.image1)
    public void page1() {

        setFirstPageContent(true);
    }

    @Bind(R.id.image2)
    ImageView image2;

    @OnClick(R.id.image2)
    public void page2() {
        if(page1Validate()) {
            setSecondPageContent(true);
        }
    }

    @Bind(R.id.image3)
    ImageView image3;

    boolean filed = false;
    @OnClick(R.id.image3)
    public void page3() {

        if(filed) {
            if (page1Validate() && page2Validate()) {
                setThirdPageContent();
            }
        }
    }

    @Bind(R.id.delivery_info_pages)
    LinearLayout layer;
    int count = 0;
    @Bind(R.id.btn_next)
    Button btn_next;

    @OnClick(R.id.btn_next)
    public void next() {

        if(count==0 && page1Validate()) {
                setSecondPageContent(false);
            filed = true;
        } else if(count==1 && page2Validate()){


                setThirdPageContent();

        } else {

//            Intent i = new Intent(getActivity(), MapActivity.class);
//            getActivity().startActivity(i);

        }

    }


    EditText senderName;
    PlacesAutocompleteTextView senderAddress;
    EditText receiverName;
    PlacesAutocompleteTextView receiverAddress;
    EditText recipientName;
    EditText recipientPhone;
    EditText recipientInstruction;
    MaterialSpinner parcelType;
    EditText parcelWeight;
    TextView txtDistance;
    TextView txtCost;
    EditText promoCode;
    Button applyPromo;

    String sName;
    String sAddress;
    String rName;
    String rAdderss;
    String rpName;
    String rpPhone;
    String rpInstruction;
    int pType;
    String pWeight;
    ArrayList unitList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arrange_delivery, container, false);
        ButterKnife.bind(this, view);

        unitList.add("Extra Small");
        unitList.add("Small");
        unitList.add("Medium");
        unitList.add("Large");
        unitList.add("Extra Large");

        setFirstPageContent(false);


        return view;
    }


//    PlacesAutocompleteTextView address;

    private void setFirstPageContent(boolean setValue) {

        btn_next.setText("Next");
        if (layer != null) {
            layer.removeAllViews();
        }
        RelativeLayout page1 = (RelativeLayout) View.inflate(getActivity(),
                R.layout.delivery_info_page1, null);
        layer.addView(page1);
        count = 0;

        senderName = (EditText) page1.findViewById(R.id.editText_sender_name);
        senderAddress = (PlacesAutocompleteTextView) page1.findViewById(R.id.sender_places_autocomplete);
        receiverName = (EditText) page1.findViewById(R.id.editText_recever_name);
        receiverAddress = (PlacesAutocompleteTextView) page1.findViewById(R.id.receiver_places_autocomplete);
//        receiverAddress = (EditText) page1.findViewById(R.id.editText_recever_address);

        if(setValue){
            senderName.setText(sName);
            senderAddress.setText(sAddress);
            receiverName.setText(rName);
            receiverAddress.setText(rAdderss);

        }

        image1.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), PorterDuff.Mode.SRC_ATOP);
        image2.setColorFilter(ContextCompat.getColor(getActivity(), R.color.iron), PorterDuff.Mode.SRC_ATOP);
        image3.setColorFilter(ContextCompat.getColor(getActivity(), R.color.iron), PorterDuff.Mode.SRC_ATOP);

        Gson gson = new Gson();
        UserInfo obj = gson.fromJson(PreferenceUtils.getUserInfo(getActivity()), UserInfo.class);

        senderName.setText(obj.getName());
        senderAddress.setText(obj.getAddress() +", " + obj.getCity() +", " + obj.getState() +", " + obj.getCountry());


        senderAddress.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        // do something awesome with the selected place
                        Log.e("PLACE" , "" + place);
                    }
                }
        );

        receiverAddress.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        // do something awesome with the selected place
                        Log.e("PLACE" , "" + place);
                    }
                }
        );
    }

    private void setSecondPageContent(boolean setValue) {

        btn_next.setText("Next");
        image1.setColorFilter(ContextCompat.getColor(getActivity(), R.color.iron), PorterDuff.Mode.SRC_ATOP);
        image2.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), PorterDuff.Mode.SRC_ATOP);
        image3.setColorFilter(ContextCompat.getColor(getActivity(), R.color.iron), PorterDuff.Mode.SRC_ATOP);
        layer.removeAllViews();
        RelativeLayout page2 = (RelativeLayout) View.inflate(getActivity(),
                R.layout.delivery_info_page2, null);
        layer.addView(page2);
        count = 1;

        recipientName = (EditText) page2.findViewById(R.id.editText_recipient_name);
        recipientPhone = (EditText) page2.findViewById(R.id.editText_recipient_phone);
        recipientInstruction = (EditText) page2.findViewById(R.id.editText_instruction);
        parcelWeight = (EditText) page2.findViewById(R.id.editText_parcel_weight);
        parcelType = (MaterialSpinner) page2.findViewById(R.id.spinner_parcel_type);

        parcelType.setItems(unitList);
        parcelType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                pType = position;
                Toast.makeText(getActivity(), " " + item, Toast.LENGTH_SHORT).show();
            }
        });

        if(setValue){
            recipientName.setText(rpName);
            recipientPhone.setText(rpPhone);
            recipientInstruction.setText(rpInstruction);
            parcelWeight.setText(pWeight);
            parcelType.setSelectedIndex(pType);

        }

    }

    LatLng mPlaceS = null;
    LatLng mPlaceR = null;

    private void setThirdPageContent() {
        btn_next.setText("Arrange");
        image1.setColorFilter(ContextCompat.getColor(getActivity(), R.color.iron), PorterDuff.Mode.SRC_ATOP);
        image2.setColorFilter(ContextCompat.getColor(getActivity(), R.color.iron), PorterDuff.Mode.SRC_ATOP);
        image3.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), PorterDuff.Mode.SRC_ATOP);
        layer.removeAllViews();
        RelativeLayout page3 = (RelativeLayout) View.inflate(getActivity(),
                R.layout.delivery_info_page3, null);
        layer.addView(page3);
        count = 2;

        txtDistance = (TextView) page3.findViewById(R.id.textview_distance);
        txtCost = (TextView) page3.findViewById(R.id.textview_cost);
        promoCode = (EditText) page3.findViewById(R.id.editText_promo);
        applyPromo = (Button) page3.findViewById(R.id.btn_promo);

//        mPlaceS = geocodeAddress("DOHS Baridhara Convention Center, Dhaka, Dhaka Division");
//        mPlaceR = geocodeAddress("Mirpur 1 Dhaka");
        mPlaceS = geocodeAddress(sAddress);
        mPlaceR = geocodeAddress(rAdderss);

        if(mPlaceS!=null && mPlaceR!=null) {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(mPlaceS, mPlaceR)
//            .waypoints(start, waypoint, end)
                    .build();
            routing.execute();
        }

    }

    public boolean page1Validate() {
        boolean valid = true;

        sName = senderName.getText().toString();
        sAddress = senderAddress.getText().toString();
        rName = receiverName.getText().toString();
        rAdderss = receiverAddress.getText().toString();

        if (sName.isEmpty()) {
            senderName.setError("Name can't be empty");
            valid = false;
        } else {
            senderName.setError(null);
        }

        if (sAddress.isEmpty()) {
            senderAddress.setError("Address can't be empty");
            valid = false;
        } else {
            senderAddress.setError(null);
        }

        if (rName.isEmpty()) {
            receiverName.setError("Name can't be empty");
            valid = false;
        } else {
            receiverName.setError(null);
        }

        if (rAdderss.isEmpty()) {
            receiverAddress.setError("Address can't be empty");
            valid = false;
        } else {
            receiverAddress.setError(null);
        }

        return valid;
    }

    public boolean page2Validate() {
        boolean valid = true;

        rpName = recipientName.getText().toString();
        rpPhone = recipientPhone.getText().toString();
        pWeight = parcelWeight.getText().toString();
        rpInstruction = recipientInstruction.getText().toString();


        if (rpName.isEmpty()) {
            recipientName.setError("Name can't be empty");
            valid = false;
        } else {
            recipientName.setError(null);
        }

        if (rpPhone.isEmpty()) {
            recipientPhone.setError("Please enter recipient's phone number");
            valid = false;
        } else {
            recipientPhone.setError(null);
        }

        if (pWeight.isEmpty()) {
            parcelWeight.setError("Please enter approximate parcel weight");
            valid = false;
        } else {
            parcelWeight.setError(null);
        }

        return valid;
    }



    public  LatLng geocodeAddress(String addressStr) {
        Address address = null;
        List<Address> addressList = null;
        LatLng  mPlaceA;
        try {
            if (!TextUtils.isEmpty(addressStr)) {
                addressList = new Geocoder(getActivity()).getFromLocationName(addressStr, 5);
            }

        if (null != addressList && addressList.size() > 0) {
            address = addressList.get(0);
        }
        if (null != address && address.hasLatitude()
                && address.hasLongitude()) {
            return new LatLng(address.getLatitude(), address.getLongitude());
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;

    }

    @Override
    public void onRoutingFailure(RouteException e) {

        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int j) {

        float distance = 0.0f;
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            if(route.get(i).getDistanceValue()> distance){
                distance = route.get(i).getDistanceValue();
            }

            Log.e("DISTENCE", "Route "+ (i+1) +": distance - "+
                    route.get(i).getDistanceValue()/1000+"KM    : duration - "+ route.get(i).getDurationValue());
        }

        txtDistance.setText(""+ distance/1000 + " km");
    }

    @Override
    public void onRoutingCancelled() {
        Log.i("LOG_TAG", "Routing was cancelled.");

    }

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * com.smartmux.pos.fragment to allow an interaction in this com.smartmux.pos.fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }


}
