package com.smartmux.couriermoc.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.fragment.FragmentAboutUs;
import com.smartmux.couriermoc.fragment.FragmentArrangeDelivery;
import com.smartmux.couriermoc.fragment.FragmentDeliveryStatus;
import com.smartmux.couriermoc.fragment.FragmentFavourites;
import com.smartmux.couriermoc.fragment.FragmentFeedBack;
import com.smartmux.couriermoc.fragment.FragmentHelp;
import com.smartmux.couriermoc.fragment.FragmentHistory;
import com.smartmux.couriermoc.fragment.FragmentLeftDrawer;
import com.smartmux.couriermoc.fragment.FragmentProfile;
import com.smartmux.couriermoc.fragment.FragmentSettings;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements FragmentLeftDrawer.OnFragmentInteractionListener
        , FragmentDeliveryStatus.OnFragmentInteractionListener
        , FragmentAboutUs.OnFragmentInteractionListener
        , FragmentFavourites.OnFragmentInteractionListener
        , FragmentFeedBack.OnFragmentInteractionListener
        , FragmentHelp.OnFragmentInteractionListener
        , FragmentProfile.OnFragmentInteractionListener
        , FragmentHistory.OnFragmentInteractionListener
        , FragmentArrangeDelivery.OnFragmentInteractionListener
        , FragmentSettings.OnFragmentInteractionListener {

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ButterKnife.bind(this);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        setActionBar();
        setTitle(R.string.arrange);
        FragmentArrangeDelivery arrangeDeliveryFragment = FragmentArrangeDelivery.newInstance("", "");
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, arrangeDeliveryFragment).commit();
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
//        getSupportActionBar().setIcon(R.drawable.drawer);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav fragment_drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open fragment_drawer" description */
                R.string.drawer_close  /* "close fragment_drawer" description */
        ) {

            /** Called when a fragment_drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
//                if (slideOffset > 0.6 && linearLayout.getChildCount() == 0)
//                    viewAnimator.showMenuContent();
            }

            /** Called when a fragment_drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }


    @Override
    public void setDrawerOption(int pos) {

        drawerLayout.closeDrawers();
        if (pos == 0) {

            //arrange delivery
            setTitle(R.string.arrange);
            FragmentArrangeDelivery arrangeDeliveryFragment = FragmentArrangeDelivery.newInstance("", "");
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, arrangeDeliveryFragment).commit();

        } else if (pos == 1) {

            //delivery status
            setTitle(R.string.status);
            FragmentDeliveryStatus deliveryStatusFragment = FragmentDeliveryStatus.newInstance("", "");
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, deliveryStatusFragment).commit();

//        } else if (pos == 2) {
//
//            //favourites
//            setTitle(R.string.favourites);
//            FragmentFavourites favouritesFragment = FragmentFavourites.newInstance("", "");
//            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, favouritesFragment).commit();

        } else if (pos == 2) {

            //history
            setTitle(R.string.history);
            FragmentHistory historyFragment = FragmentHistory.newInstance("", "");
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, historyFragment).commit();

        } else if (pos == 3) {

            //profile
            setTitle(R.string.profile);
            FragmentProfile profileFragment = FragmentProfile.newInstance("", "");
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, profileFragment).commit();

        } else if (pos == 4) {

            //feedback
            setTitle(R.string.feedback);
            FragmentFeedBack feedBackFragment = FragmentFeedBack.newInstance("", "");
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, feedBackFragment).commit();

        } else if (pos == 5) {

            //help
            setTitle(R.string.help);
            FragmentHelp helpFragment = FragmentHelp.newInstance("", "");
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, helpFragment).commit();

        } else if (pos == 6) {

            //about us
            setTitle(R.string.about_us);
            FragmentAboutUs aboutUsFragment = FragmentAboutUs.newInstance("", "");
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, aboutUsFragment).commit();

        } else if (pos == 21) {

            //Setting
            setTitle(R.string.action_settings);
            FragmentSettings settingsFragment = FragmentSettings.newInstance("", "");
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, settingsFragment).commit();

        } else if (pos == 22) {

            //logout
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Logging out...");
            progressDialog.show();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {

                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            progressDialog.dismiss();
                            startActivity(i);
                            finish();
                        }
                    }, 3000);

        } else {

            //default
            setTitle(R.string.arrange);
            FragmentArrangeDelivery arrangeDeliveryFragment = FragmentArrangeDelivery.newInstance("", "");
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, arrangeDeliveryFragment).commit();

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}