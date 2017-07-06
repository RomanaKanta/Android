package com.smartmux.couriermoc.ServiceActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.widget.SimpleDrawingView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tanvir-android on 7/26/16.
 */
public class FragmentStatus extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public FragmentStatus() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentStatus newInstance(String param1, String param2) {
        FragmentStatus fragment = new FragmentStatus();
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

    @Bind(R.id.check_pick_parcel)
    View pick;

    @OnClick(R.id.layer_pick_parcel)
    public void pickupDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(),
                        R.style.popup_theme));
        alert.setMessage("Do you get Parcel?");

        alert.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pick.setBackgroundColor(Color.GREEN);
                        dialog.cancel();
                    }

                });

        alert.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pick.setBackgroundColor(Color.RED);
                        dialog.cancel();

                    }
                });
        alert.show();
    }

    @Bind(R.id.check_deliver_parcel)
    View complete;

    @Bind(R.id.layer_deliver_parcel)
    RelativeLayout completeLayer;

    @OnClick(R.id.layer_deliver_parcel)
    public void completeDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(),
                        R.style.popup_theme));
        alert.setMessage("is Parcel Delivery Complete?");

        alert.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        complete.setBackgroundColor(Color.GREEN);
                        dialog.cancel();
                    }

                });

        alert.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        complete.setBackgroundColor(Color.RED);
                        dialog.cancel();

                    }
                });
        alert.show();
    }


    @Bind(R.id.draw_signature)
    SimpleDrawingView signature;

    @OnClick(R.id.btn_clear)
    public void clearField() {
        signature.onClickClear();
    }

    @OnClick(R.id.btn_save)
    public void saveSignature() {
        completeLayer.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_status, container, false);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }


}
