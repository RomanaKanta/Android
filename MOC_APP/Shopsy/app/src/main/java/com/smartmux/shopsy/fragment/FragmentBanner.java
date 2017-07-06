package com.smartmux.shopsy.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.smartmux.shopsy.R;
import com.smartmux.shopsy.adapter.MyPagerAdapter;
import com.smartmux.shopsy.utils.CustomTimerTask;
import com.smartmux.shopsy.widget.PagerContainer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tanvir-android on 7/26/16.
 */
public class FragmentBanner extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String catagory;
    private String subcatagory;

    private OnFragmentInteractionListener mListener;


    public FragmentBanner() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentBanner newInstance(String param1, String param2) {
        FragmentBanner fragment = new FragmentBanner();
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
            catagory = getArguments().getString(ARG_PARAM1);
            subcatagory = getArguments().getString(ARG_PARAM2);
        }
    }


    @Bind(R.id.pager_container)
    PagerContainer mContainer;
    @Bind(R.id.viewPagerCountDots)
    LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    ViewPager pager;
    MyPagerAdapter adapter;
    int page = 1;
    Timer timer;
    TimerTask updateProfile;
    float BIG_SCALE = 1.0f;
    float SMALL_SCALE = 0.7f;
    float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_banner_content, container, false);
        ButterKnife.bind(this, view);

        setBannerPager();


        return view;
    }

    private void setBannerPager() {

        ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.image1);
        images.add(R.drawable.image2);
        images.add(R.drawable.image1);
        images.add(R.drawable.image2);
        images.add(R.drawable.image1);
        images.add(R.drawable.image2);

        pager = mContainer.getViewPager();
        adapter = new MyPagerAdapter(getActivity(), images);
        pager.setAdapter(adapter);
        //Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(adapter.getCount());
        //A little space between pages
        pager.setPageMargin(15);
//        pager.setInterval(4000);
//        pager.startAutoScroll();
//        pager.setCycle(true);
        //If hardware acceleration is enabled, you should also remove
        // clipping on the pager for its children.
        pager.setClipChildren(false);


        dotsCount = images.size();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));


        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                page = position;
                CustomTimerTask.page = position;

                position = position % dotsCount;

                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
                }

                dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


//        pager.setPageTransformer(false, new ViewPager.PageTransformer() {
//            @Override
//            public void transformPage(View page, float position) {
//                MyLinearLayout myLinearLayout = (MyLinearLayout) page.findViewById(R.id.root);
//                float scale = BIG_SCALE;
//                if (position > 0) {
//                    scale = scale - position * DIFF_SCALE;
//                } else {
//                    scale = scale + position * DIFF_SCALE;
//                }
//                if (scale < 0) scale = 0;
//                myLinearLayout.setScaleBoth(scale);
//            }
//        });


    }

    @Override
    public void onPause() {
        super.onPause();

        if (timer != null) {
            timer.cancel();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        timer = new Timer();
        updateProfile = new CustomTimerTask(getActivity(), pager, page);
        timer.scheduleAtFixedRate(updateProfile, 500, 3000);
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
