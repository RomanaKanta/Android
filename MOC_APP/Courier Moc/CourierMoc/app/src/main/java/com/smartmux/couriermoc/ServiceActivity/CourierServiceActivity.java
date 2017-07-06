package com.smartmux.couriermoc.ServiceActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.modelclass.DeliverInfo;
import com.smartmux.couriermoc.utils.JsonUtils;
import com.smartmux.couriermoc.utils.ProgressHUD;
import com.smartmux.couriermoc.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CourierServiceActivity extends AppCompatActivity implements
        FragmentParcelDetail.OnFragmentInteractionListener
        , FragmentReceiverDetail.OnFragmentInteractionListener
        , FragmentStatus.OnFragmentInteractionListener {

//    @OnClick(R.id.btn_parcel_info)
//    public void parcelInfo(){
//        startActivity(new Intent(this, MainActivity.class));
//    }
//
//    @OnClick(R.id.btn_recever_info)
//    public void receiverInfo(){
//        startActivity(new Intent(this, CourierServiceActivity.class));
//    }
//
//    @OnClick(R.id.btn_status)
//    public void status(){
//        startActivity(new Intent(this, CourierServiceActivity.class));
//    }

    private String[] tabItemsTitle = {"Parcel Detail", "Recipient's Detail", "Status"};
    @Bind(R.id.segment_tablayout)
    SegmentTabLayout tabLayout;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    private PagerAdapter adapter;
    @Bind(R.id.textview_parcel_id)
    TextView order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service);

        ButterKnife.bind(this);

        new SM_AsyncTaskForGetInfo().execute();

        tabLayout.setTabData(tabItemsTitle);

        tabLayout.setCurrentTab(0);

    }
    class SM_AsyncTaskForGetInfo extends AsyncTask<String, String, Boolean> {

        ProgressHUD mProgressHUD;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Log.e("onPreExecute", "call");
            mProgressHUD = ProgressHUD.show(CourierServiceActivity.this,
                    "Loading",true);

        }

        @Override
        protected Boolean doInBackground(String... params) {

            return JsonUtils.getDeliverInfo(Utils.loadJSONFromAsset(CourierServiceActivity.this, "deliver.json"));
        }

        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);

            if(result != null && result==true){

                order_id.setText(DeliverInfo.id);

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
            if (mProgressHUD.isShowing()) {

                mProgressHUD.dismiss();
            }
        }
    }

    public  class PagerAdapter extends FragmentPagerAdapter {
        private  int NUM_ITEMS = 3;

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
                    return FragmentParcelDetail.newInstance("Page # 1", "Page # 1");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return FragmentReceiverDetail.newInstance("Page # 1", "Page # 1");
                case 2: // Fragment # 0 - This will show FirstFragment different title
                    return FragmentStatus.newInstance("Page # 1", "Page # 1");

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

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

}

