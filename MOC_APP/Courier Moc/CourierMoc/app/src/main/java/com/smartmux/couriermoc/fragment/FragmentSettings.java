package com.smartmux.couriermoc.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.activity.ProfileEditActivity;
import com.smartmux.couriermoc.dialogfragment.DialogChangePassword;
import com.smartmux.couriermoc.utils.Constant;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tanvir-android on 7/26/16.
 */
public class FragmentSettings extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public FragmentSettings() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentSettings newInstance(String param1, String param2) {
        FragmentSettings fragment = new FragmentSettings();
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

    @OnClick(R.id.content_edit_profile)
    public void editProfile() {

        Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
        getActivity().startActivity(intent);

    }

    @OnClick(R.id.content_edit_password)
    public void editPassword() {
        new DialogChangePassword().show(getActivity().getSupportFragmentManager(), "Change_Password_Dialog_Fragment");
    }

    @OnClick(R.id.content_tell_friend)
    public void tellFriends() {

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net == null) {
            Toast.makeText(getActivity(), R.string.net_con,
                    Toast.LENGTH_SHORT).show();

        } else {

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                public void run() {

                    boolean installed = appInstalledOrNot(Constant.FB_PACKAGE);
                    if (installed) {

//							new FacebookTask().execute();

//							saveInTampFile();
                        shareImage();

                    } else {
                        Toast.makeText(
                                getActivity(),
                                "Sorry, This Service not found in your device",
                                Toast.LENGTH_LONG).show();
                    }

                }
            }, 1000);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

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

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private void shareImage() {


        Intent share = new Intent(Intent.ACTION_SEND);

        // If you want to share a png image only, you can do:
        // setType("image/png"); OR for jpeg: setType("image/jpeg");
        share.setType("image/png");
        Uri uri = Uri.parse(Constant.THUMB_URL);
        share.putExtra(Intent.EXTRA_STREAM, uri);

        share.setPackage(Constant.FB_PACKAGE);
        startActivity(share);
    }

}
