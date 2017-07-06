package com.smartmux.batterymonitor.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.smartmux.batterymonitor.fragment.*;

public class FragmentAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 2;

    public FragmentAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
        case 0: // Fragment # 0 - This will show FirstFragment
            return FirstFragment.newInstance(0, "Page # 1");
        case 1: // Fragment # 0 - This will show FirstFragment different title
            return SecondFragment.newInstance(1, "Page # 2");
        default:
            return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }


}
