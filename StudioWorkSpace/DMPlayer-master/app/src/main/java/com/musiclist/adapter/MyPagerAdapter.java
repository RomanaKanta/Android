package com.musiclist.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.musiclist.childfragment.ChildFragmentAlbum;
import com.musiclist.childfragment.ChildFragmentArtists;
import com.musiclist.childfragment.ChildFragmentGenres;
import com.musiclist.childfragment.ChildFragmentMostPlay;

/**
 * Created by tanvir-android on 5/16/16.
 */
public class MyPagerAdapter extends FragmentStatePagerAdapter {

    private final String[] TITLES = {"ALBUMS", "ARTISTS", "GENRES", "MOSTPLAY"};
    private ChildFragmentGenres childFragmentGenres;
    private ChildFragmentArtists childFragmentArtists;
    private ChildFragmentAlbum childFragmentAlbum;
    private ChildFragmentMostPlay childFragmentMostplay;
    Context context;

    public MyPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                childFragmentAlbum = ChildFragmentAlbum.newInstance(position, context);
                return childFragmentAlbum;
            case 1:
                childFragmentArtists = ChildFragmentArtists.newInstance(position, context);
                return childFragmentArtists;
            case 2:
                childFragmentGenres = ChildFragmentGenres.newInstance(position, context);
                return childFragmentGenres;
            case 3:
                childFragmentMostplay = ChildFragmentMostPlay.newInstance(position, context);
                return childFragmentMostplay;
        }
        return null;
    }
}

