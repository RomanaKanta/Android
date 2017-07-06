package com.smartmux.couriermoc.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.modelclass.UserInfo;
import com.smartmux.couriermoc.utils.PreferenceUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tanvir-android on 7/26/16.
 */
public class FragmentProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public FragmentProfile() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentProfile newInstance(String param1, String param2) {
        FragmentProfile fragment = new FragmentProfile();
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


    @Bind(R.id.user_image)
    ImageView imgUser;
    @Bind(R.id.user_name)
    TextView txtName;
    @Bind(R.id.user_email)
    TextView txtEmail;
    @Bind(R.id.user_phone)
    TextView txtPhone;
    @Bind(R.id.user_country)
    TextView txtCountry;
    @Bind(R.id.user_zip_code)
    TextView txtZipcode;
    @Bind(R.id.user_state)
    TextView txtState;
    @Bind(R.id.user_city)
    TextView txtCity;
    @Bind(R.id.user_address)
    TextView txtAddress;

    @OnClick(R.id.ViewSetting)
    public void setSettingFrag() {

        mListener.setDrawerOption(21);

    }

    @OnClick(R.id.ViewLogout)
    public void logout() {

        mListener.setDrawerOption(22);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        Gson gson = new Gson();
        UserInfo obj = gson.fromJson(PreferenceUtils.getUserInfo(getActivity()), UserInfo.class);

        txtName.setText(obj.getName());
        txtEmail.setText(obj.getEmail());
        txtPhone.setText(obj.getPhone());
        txtCountry.setText(obj.getCountry());
        txtZipcode.setText(obj.getZipCode());
        txtState.setText(obj.getState());
        txtCity.setText(obj.getCity());
        txtAddress.setText(obj.getAddress());
        setPhoto(obj.getImageUrl());

        return view;
    }

    private void setPhoto(String path) {

        if (!TextUtils.isEmpty(path)) {

            Glide.with(getActivity()).load(path).asBitmap().centerCrop()
                    .placeholder(R.drawable.loading)
                    .into(new BitmapImageViewTarget(imgUser) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            imgUser.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        } else {
            imgUser.setImageResource(R.drawable.illust_029);
        }

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void setDrawerOption(int pos);

    }


}
