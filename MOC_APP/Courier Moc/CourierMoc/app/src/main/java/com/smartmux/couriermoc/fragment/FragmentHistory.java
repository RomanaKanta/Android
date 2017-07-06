package com.smartmux.couriermoc.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
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
import com.smartmux.couriermoc.adapter.HistoryAdapter;
import com.smartmux.couriermoc.modelclass.HistoryData;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tanvir-android on 7/26/16.
 */
public class FragmentHistory extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public FragmentHistory() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentHistory newInstance(String param1, String param2) {
        FragmentHistory fragment = new FragmentHistory();
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


    private String[] tabItemsTitle = {"Wallet History", "Orders", "Deliveries"};
    @Bind(R.id.segment_tablayout)
    SegmentTabLayout tabLayout;
    @Bind(R.id.recyclerViewHistory)
    RecyclerView recyclerView;
//    @Bind(R.id.viewPager)
//    ViewPager viewPager;
//    PagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

        tabLayout.setTabData(tabItemsTitle);

//        adapter = new PagerAdapter(getActivity().getSupportFragmentManager());
//        viewPager.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLongClickable(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        final int gridMargin = getResources().getDimensionPixelOffset(R.dimen.spacing);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.set(gridMargin, gridMargin, gridMargin, gridMargin);
            }
        });

        recyclerView.setPadding(gridMargin, gridMargin, gridMargin, gridMargin);
        ArrayList<HistoryData> list = new ArrayList<HistoryData>();
        list.add(new HistoryData("$40", "1 June 2016", "+"));
        list.add(new HistoryData("$35", "13 May 2016", "-"));
        list.add(new HistoryData("$50", "1 May 2016", "+"));
        list.add(new HistoryData("$10", "19 April 2016", "-"));
        list.add(new HistoryData("$20", "7 April 2016", "+"));

        recyclerView.setAdapter(new HistoryAdapter(getActivity(), list));


//        ItemClickSupport itemClick = ItemClickSupport
//                .addTo(recyclerView);
//        itemClick
//                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
//                    @Override
//                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//
//
//                    }
//                });

        tabLayout.setCurrentTab(0);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
//                viewPager.setCurrentItem(position);

                if (position == 0) {

                    ArrayList<HistoryData> list = new ArrayList<HistoryData>();
                    list.add(new HistoryData("$40", "1 June 2016", "+"));
                    list.add(new HistoryData("$35", "13 May 2016", "-"));
                    list.add(new HistoryData("$50", "1 May 2016", "+"));
                    list.add(new HistoryData("$10", "19 April 2016", "-"));
                    list.add(new HistoryData("$20", "7 April 2016", "+"));

                    recyclerView.setAdapter(new HistoryAdapter(getActivity(), list));

                } else if (position == 1) {

                    ArrayList<HistoryData> list = new ArrayList<HistoryData>();
                    list.add(new HistoryData("OrderID_1", "1 June 2016", ""));
                    list.add(new HistoryData("OrderID_2", "13 May 2016", ""));
                    list.add(new HistoryData("OrderID_3", "1 May 2016", ""));
                    list.add(new HistoryData("OrderID_4", "19 April 2016", ""));
                    list.add(new HistoryData("OrderID_5", "7 April 2016", ""));

                    recyclerView.setAdapter(new HistoryAdapter(getActivity(), list));

                } else if (position == 2) {

                    ArrayList<HistoryData> list = new ArrayList<HistoryData>();
                    list.add(new HistoryData("OrderID_1", "1 June 2016", "Complete"));
                    list.add(new HistoryData("OrderID_2", "13 May 2016", "Complete"));
                    list.add(new HistoryData("OrderID_3", "1 May 2016", "Complete"));
                    list.add(new HistoryData("OrderID_4", "19 April 2016", "Complete"));
                    list.add(new HistoryData("OrderID_5", "7 April 2016", "Complete"));

                    recyclerView.setAdapter(new HistoryAdapter(getActivity(), list));

                } else {

                }

            }

            @Override
            public void onTabReselect(int position) {
            }
        });

//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                tabLayout.setCurrentTab(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

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

//    public static class PagerAdapter extends FragmentPagerAdapter {
//        private static int NUM_ITEMS = 3;
//
//        public PagerAdapter(FragmentManager fragmentManager) {
//            super(fragmentManager);
//        }
//
//        // Returns total number of pages
//        @Override
//        public int getCount() {
//            return NUM_ITEMS;
//        }
//
//        // Returns the fragment to display for that page
//        @Override
//        public Fragment getItem(int position) {
//            switch (position) {
//                case 0: // Fragment # 0 - This will show FirstFragment
//                    return ReportFragment.newInstance("Page # 1", "Page # 1");
//                case 1: // Fragment # 0 - This will show FirstFragment different title
//                    return SupplierReportFragment.newInstance("Page # 1", "Page # 1");
//                case 2: // Fragment # 0 - This will show FirstFragment different title
//                    return InventoryFragment.newInstance("Page # 1", "Page # 1");
//
//                default:
//                    return null;
//            }
//        }
//
//        // Returns the page title for the top indicator
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return "Page " + position;
//        }
//
//    }
}
