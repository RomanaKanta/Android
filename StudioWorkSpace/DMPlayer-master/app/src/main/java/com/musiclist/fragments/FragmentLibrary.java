/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.musiclist.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dmplayer.R;
import com.musiclist.adapter.MyPagerAdapter;
import com.musiclist.tablayout.SlidingTabLayout;

public class FragmentLibrary extends Fragment {

//    private final String[] TITLES = {"ALBUMS", "ARTISTS", "GENRES", "MOSTPLAY"};
    private TypedValue typedValueToolbarHeight = new TypedValue();
//    private ChildFragmentGenres childFragmentGenres;
//    private ChildFragmentArtists childFragmentArtists;
//    private ChildFragmentAlbum childFragmentAlbum;
//    private ChildFragmentMostPlay childFragmentMostplay;

    private MyPagerAdapter pagerAdapter;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    private int tabsPaddingTop;
    int child;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_library, null);
         child =getArguments().getInt("page");

        setupView(v);
        return v;
    }


    private void setupView(View view) {
        pager = (ViewPager) view.findViewById(R.id.pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getActivity(),getFragmentManager());
        pager.setAdapter(pagerAdapter);

        if(child!=-1) {
            pager.setCurrentItem(child);
        }
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(false);
        // Tab indicator color
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.md_white_1000);
            }
        });
        tabs.setViewPager(pager);
    }


    public int convertToPx(int dp) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }
}
