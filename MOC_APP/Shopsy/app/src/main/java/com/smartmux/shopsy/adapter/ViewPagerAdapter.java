package com.smartmux.shopsy.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.smartmux.shopsy.fragment.FragmentList;

/**
 * Created by tanvir-android on 8/11/16.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private String[] tabItemsTitle  = {"New", "Popular", "Sale"};
    Context context;

    public ViewPagerAdapter(Context context,FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return tabItemsTitle.length;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        return FragmentList.newInstance("Page # " + position, tabItemsTitle[position]);
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return tabItemsTitle[position];
//        Typeface font = Typeface.createFromAsset(context.getAssets(), "font/Lato-Bold.ttf");
//        String title = tabItemsTitle[position];
//
//        SpannableString styled = new SpannableString(title);
//        styled.setSpan(new CustomTypefaceSpan("" , font), 0, styled.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//
//        return styled;

    }

}
