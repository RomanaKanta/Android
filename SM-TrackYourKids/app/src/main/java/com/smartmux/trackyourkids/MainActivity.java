package com.smartmux.trackyourkids;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.smartmux.trackyourkids.fragment.AppData;
import com.smartmux.trackyourkids.fragment.HomeFragment;
import com.smartmux.trackyourkids.mapactivities.GPSTracker;
import com.smartmux.trackyourkids.mapactivities.JSONLoader;
import com.smartmux.trackyourkids.mapactivities.JsonParser;
import com.smartmux.trackyourkids.mapactivities.ModelClass;
import com.smartmux.trackyourkids.mapactivities.Utils;

import java.util.ArrayList;

import butterknife.Bind;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,HomeFragment.OnFragmentInteractionListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(null);


        gpsTracker = new GPSTracker(MainActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        getDeviceInfo();


        if (Utils.isMobileOrWifiConnectivityAvailable(MainActivity.this)) {
            if (gpsTracker != null && gpsTracker.getIsGPSTrackingEnabled()) {

                new SM_AsyncTaskForGetData(gpsTracker.getLatitude(), gpsTracker.getLongitude()).execute();
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                Toast.makeText(MainActivity.this,"GPS not available!",Toast.LENGTH_LONG).show();
                gpsTracker.showSettingsAlert();

            }
        } else {

            Toast.makeText(MainActivity.this,"Internet not available!",Toast.LENGTH_LONG).show();

        }
    }


    class SM_AsyncTaskForGetData extends AsyncTask<String, String, ArrayList<ModelClass>> {

        ProgressDialog progressDialog;
        double lat;
        double lon;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "", "ProgressDialog");
        }

        public SM_AsyncTaskForGetData(double lat, double lon){
            this.lat = lat;
            this.lon = lon;

        }

        @Override
        protected ArrayList<ModelClass> doInBackground(String... params) {


            ///  get json response from url using POST method with parameter
//            String url = "http://192.168.0.101:8000/api/api_list";
//            HashMap<String, String> postDataParams = new HashMap<>();
//            postDataParams.put("lat", String.valueOf(lat));
//            postDataParams.put("lon", String.valueOf(lon));
//            String obj = new JSONLoader().getJsonDatafromURL(url, postDataParams);


            ///  get json response from url using GET method
            String obj = new JSONLoader().getJsonfromURL("http://smartmux.com/web_apnar_doctor/list_doctors.json");


            if (obj != null) {
                return JsonParser.getList(obj);
            }


            return null;
        }

        protected void onPostExecute(ArrayList<ModelClass> result) {
            super.onPostExecute(result);

            if (result != null) {

                AppData.dataList = result;
                AppData.lat = lat;
                AppData.lon = lon;

                Log.e("CURRENT" , "lat  "+ lat + "  lon   " + lon);

                Fragment fragment = new HomeFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.container,fragment).commit();

            }
            progressDialog.dismiss();
        }
    }

    public void getDeviceInfo(){

        String identifier = Utils
                .getAppPackageName(MainActivity.this);
        String uid = Utils.getUID(MainActivity.this);
        String device = Utils.getDeviceModel();
        String version = Utils
                .getAppVersionNumber(MainActivity.this);
        String platform = Utils.getAppPlatfrom();

        String country = Utils.getLocalCountryName();
        String language =  Utils.getLocalLanguage();


        Log.e("DEVICE INFO" , identifier + "\n" +
                uid + "\n" +
                device + "\n" +
                version + "\n" +
                platform + "\n" +
                country + "\n" +
                language + "\n" );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
