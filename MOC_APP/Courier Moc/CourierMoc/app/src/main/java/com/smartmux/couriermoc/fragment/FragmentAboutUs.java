package com.smartmux.couriermoc.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smartmux.couriermoc.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tanvir-android on 7/26/16.
 */
public class FragmentAboutUs extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public FragmentAboutUs() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentAboutUs newInstance(String param1, String param2) {
        FragmentAboutUs fragment = new FragmentAboutUs();
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

    @OnClick(R.id.office_address)
    public void showAddress() {

    }

    @OnClick(R.id.office_contact)
    public void setCall() {
        showCallAlart();

    }

    @OnClick(R.id.office_mail)
    public void sendMail() {

        ConnectivityManager connection = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connection.getActiveNetworkInfo();
        if (network == null) {
            Toast.makeText(getActivity(), R.string.net_con,
                    Toast.LENGTH_SHORT).show();

        } else {

            emailService();

        }

    }

    @OnClick(R.id.office_website)
    public void showWebsite() {

        Intent i = new Intent(Intent.ACTION_VIEW, Uri
                .parse(getString(R.string.office_website)));
        getActivity().startActivity(i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    private void showCallAlart() {

        AlertDialog.Builder alert = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(),
                        R.style.popup_theme));

        alert.setTitle("Call?");
        alert.setMessage(R.string.office_contact);
        alert.setCancelable(false);

        alert.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + getString(R.string.office_contact)));
                getActivity().startActivity(intent);

                dialog.cancel();
            }

        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }


    private void emailService() {


        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.office_email)});
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
//        emailIntent.putExtra(Intent.EXTRA_TEXT, emailContent);
        /// use below 2 commented lines if need to use BCC an CC feature in email
        //emailIntent.putExtra(Intent.EXTRA_CC, new String[]{ to});
        //emailIntent.putExtra(Intent.EXTRA_BCC, new String[]{to});
        ////use below 3 commented lines if need to send attachment
//        emailIntent .setType("image/jpeg");
//        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "My Picture");
//        emailIntent .putExtra(Intent.EXTRA_STREAM, Uri.parse("file://sdcard/captureimage.png"));

        //need this to prompts email client only
        emailIntent.setType("message/rfc822");

        try {
            getActivity().startActivity(Intent.createChooser(emailIntent,
                    getString(R.string.app_name)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), R.string.email_client,
                    Toast.LENGTH_SHORT).show();
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

    }


}
