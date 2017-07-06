package com.smartmux.couriermoc.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.adapter.DeliveryStatusAdapter;
import com.smartmux.couriermoc.modelclass.StatusInfo;
import com.smartmux.couriermoc.utils.Constant;
import com.smartmux.couriermoc.utils.JsonUtils;
import com.smartmux.couriermoc.utils.ProgressHUD;
import com.smartmux.couriermoc.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tanvir-android on 7/26/16.
 */
public class FragmentDeliveryStatus extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public FragmentDeliveryStatus() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentDeliveryStatus newInstance(String param1, String param2) {
        FragmentDeliveryStatus fragment = new FragmentDeliveryStatus();
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

    private String[] tabItemsTitle = {"Pending", "On Going", "Completed", "Unfulfilled"};
    @Bind(R.id.segment_tablayout)
    SegmentTabLayout tabLayout;
    @Bind(R.id.recyclerViewStatus)
    RecyclerView statusList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_status, container, false);
        ButterKnife.bind(this, view);


        tabLayout.setTabData(tabItemsTitle);

//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.content_fragment, FragmentStatusList.newInstance("0", ""))
//                .commit();
        statusList.setHasFixedSize(true);
        statusList.setLongClickable(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        statusList.setLayoutManager(layoutManager);
        final int gridMargin = getResources().getDimensionPixelOffset(R.dimen.spacing);

        statusList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.set(gridMargin, gridMargin, gridMargin, gridMargin);
            }
        });

        statusList.setPadding(gridMargin, gridMargin, gridMargin, gridMargin);

        new SM_AsyncTaskForGetInfo().execute();

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

    class SM_AsyncTaskForGetInfo extends AsyncTask<String, String, ArrayList<StatusInfo>> {

        ProgressHUD mProgressHUD;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Log.e("onPreExecute", "call");
            mProgressHUD = ProgressHUD.show(getActivity(),
                    "Loading", true);

        }

        @Override
        protected ArrayList<StatusInfo> doInBackground(String... params) {

            return JsonUtils.getJsonArray(Utils.loadJSONFromAsset(getActivity(), "deliverystatus.json"));
        }

        protected void onPostExecute(final ArrayList<StatusInfo> list) {
            super.onPostExecute(list);

            if (list != null) {
                final ArrayList<StatusInfo> pendingList = new ArrayList<>();
                final ArrayList<StatusInfo> onGoingList = new ArrayList<>();
                final ArrayList<StatusInfo> completeList = new ArrayList<>();
                final ArrayList<StatusInfo> failedList = new ArrayList<>();


                for (int i = 0; i < list.size(); i++) {
                    StatusInfo model = list.get(i);
                    if (model.getStatus().equals(Constant.PENDING)) {

                        pendingList.add(model);

                    } else if (model.getStatus().equals(Constant.ONGOING)) {

                        onGoingList.add(model);

                    } else if (model.getStatus().equals(Constant.COMPLETE)) {

                        completeList.add(model);

                    } else if (model.getStatus().equals(Constant.UNFULFILLED)) {

                        failedList.add(model);

                    } else {

                    }
                }

                statusList.setAdapter(new DeliveryStatusAdapter(getActivity(), pendingList, "0"));

                tabLayout.setCurrentTab(0);

                tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
                    @Override
                    public void onTabSelect(int position) {

                        if (position == 0) {
                            statusList.setAdapter(new DeliveryStatusAdapter(getActivity(), pendingList, "" + position));
                        } else if (position == 1) {
                            statusList.setAdapter(new DeliveryStatusAdapter(getActivity(), onGoingList, "" + position));
                        } else if (position == 2) {
                            statusList.setAdapter(new DeliveryStatusAdapter(getActivity(), completeList, "" + position));
                        } else if (position == 3) {
                            statusList.setAdapter(new DeliveryStatusAdapter(getActivity(), failedList, "" + position));
                        } else {

                        }


                    }

                    @Override
                    public void onTabReselect(int position) {
                    }
                });


            }
            if (mProgressHUD.isShowing()) {

                mProgressHUD.dismiss();
            }
        }
    }
}
