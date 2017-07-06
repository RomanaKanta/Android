package com.smartmux.pos.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.smartmux.pos.R;
import com.smartmux.pos.fragment.CashFragment;
import com.smartmux.pos.fragment.InventoryFragment;
import com.smartmux.pos.fragment.ProductFragment;
import com.smartmux.pos.fragment.ReportFragment;
import com.smartmux.pos.fragment.SupplierReportFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements CashFragment.OnFragmentInteractionListener,
        ProductFragment.OnProductFragmentInteractionListener,InventoryFragment.OnFragmentInteractionListener,ReportFragment.OnFragmentInteractionListener,
SupplierReportFragment.OnFragmentInteractionListener
{

    private String[] tabItemsTitle = {"Sell Report", "Suppliers Report", "Inventory","Product"};
    @Bind(R.id.segment_tablayout)
    SegmentTabLayout tabLayout;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.report)
    Button bntreport;

    private PagerAdapter adapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        tabLayout.setTabData(tabItemsTitle);

        bntreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TemporaryActivity.class);
                startActivity(intent);
            }
        });

        adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static class PagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 4;

        public PagerAdapter(FragmentManager fragmentManager) {
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
                    return ReportFragment.newInstance("Page # 1", "Page # 1");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return SupplierReportFragment.newInstance("Page # 1", "Page # 1");
                case 2: // Fragment # 0 - This will show FirstFragment different title
                    return InventoryFragment.newInstance("Page # 1", "Page # 1");
                case 3: // Fragment # 1 - This will show SecondFragment
                    return ProductFragment.newInstance("Page # 1", "Page # 1");
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

}
