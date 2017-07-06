package com.smartmux.shopsy.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.smartmux.shopsy.R;
import com.smartmux.shopsy.adapter.ExpandableListAdapter;
import com.smartmux.shopsy.fragment.FragmentBanner;
import com.smartmux.shopsy.fragment.FragmentList;
import com.smartmux.shopsy.modelclass.AppData;
import com.smartmux.shopsy.modelclass.ProductModel;
import com.smartmux.shopsy.utils.Constant;
import com.smartmux.shopsy.utils.JSONLoader;
import com.smartmux.shopsy.utils.JsonParser;
import com.smartmux.shopsy.widget.AnimatedExpandableListView;
import com.smartmux.shopsy.widget.ProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements FragmentBanner.OnFragmentInteractionListener,
        FragmentList.OnFragmentInteractionListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    @Bind(R.id.navList)
    AnimatedExpandableListView mExpandableListView;

    private ExpandableListAdapter mExpandableListAdapter;
    private ArrayList<String> mExpandableListHeader;
    private HashMap<String, ArrayList<String>> mExpandableListChild;

    String catagory = Constant.MENS;
    String subcatagory = Constant.ALL;

    FragmentList fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setActionBar();

        fragmentList = FragmentList.newInstance(catagory,subcatagory);

        new SM_AsyncTaskForSetData().execute();

    }

    private void setActionBar() {

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");

        ImageView filter = (ImageView) toolbar.findViewById(R.id.menu_filter);
        ImageView cart = (ImageView) toolbar.findViewById(R.id.menu_cart);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, FilterActivity.class);
                intent.putExtra(Constant.CATAGORY,catagory);
                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in, R.anim.nothing);
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in, R.anim.nothing);
            }
        });

    }

    private void setDrawerLayer() {

        prepareListData();

        setHeaderFooter();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        addDrawerItems();

    }

    private void setHeaderFooter() {

        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.nav_header, null, false);
        mExpandableListView.addHeaderView(listHeaderView);

//        View listFooterView = inflater.inflate(R.layout.nav_footer, null, false);
//        mExpandableListView.addFooterView(listFooterView);

//        ImageView setting = (ImageView) listFooterView.findViewById(R.id.image_setting);
//        setting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                drawer.closeDrawer(GravityCompat.START);
//                Intent intent = new Intent(HomeActivity.this,SettingActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.push_up_in, R.anim.nothing);
//            }
//        });
//
//        ImageView logout = (ImageView) listFooterView.findViewById(R.id.image_logout);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                drawer.closeDrawer(GravityCompat.START);
//                Toast.makeText(HomeActivity.this, "Logout", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    /*
    * Preparing the list data
    */
    private void prepareListData() {


        mExpandableListHeader = new ArrayList<String>();
        mExpandableListChild = new HashMap<String, ArrayList<String>>();

        // Adding child data
        mExpandableListHeader.add(Constant.MENS);
        mExpandableListHeader.add(Constant.WOMENS);
        mExpandableListHeader.add(Constant.WISHLIST);
        mExpandableListHeader.add(Constant.LOGIN);
        mExpandableListHeader.add(Constant.SETTING);

        // Adding child data
        ArrayList<String> Mens = new ArrayList<String>();
        Mens.addAll(AppData.menSubCatagory);
        Mens.add(0,Constant.ALL);

        ArrayList<String> womens = new ArrayList<String>();
        womens.addAll(AppData.womenSubCatagory);
        womens.add(0,Constant.ALL);

        mExpandableListChild.put(mExpandableListHeader.get(0), Mens); // Header, Child data
        mExpandableListChild.put(mExpandableListHeader.get(1), womens);
        mExpandableListChild.put(mExpandableListHeader.get(2), new ArrayList<String>());
        mExpandableListChild.put(mExpandableListHeader.get(3), new ArrayList<String>());
        mExpandableListChild.put(mExpandableListHeader.get(4), new ArrayList<String>());
    }

    private void addDrawerItems() {

        mExpandableListAdapter = new ExpandableListAdapter(this, mExpandableListHeader, mExpandableListChild);
        mExpandableListView.setAdapter(mExpandableListAdapter);
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (mExpandableListChild.get(mExpandableListHeader.get(groupPosition))
                        .size() == 0) {
                    drawer.closeDrawer(GravityCompat.START);

                    setDrawerOption(mExpandableListHeader.get(groupPosition));

                } else {

                    if (mExpandableListView.isGroupExpanded(groupPosition)) {
                        mExpandableListView.collapseGroupWithAnimation(groupPosition);
                    } else {
                        mExpandableListView.expandGroupWithAnimation(groupPosition);
                    }
                }
                return true;
            }
        });


        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String selectedItem = ((ArrayList) (mExpandableListChild.get(mExpandableListHeader.get(groupPosition))))
                        .get(childPosition).toString();

                drawer.closeDrawer(GravityCompat.START);

                catagory = mExpandableListHeader.get(groupPosition);
                subcatagory = selectedItem;

                fragmentList.setProduct(catagory,subcatagory);

                Toast.makeText(HomeActivity.this, "" + selectedItem, Toast.LENGTH_SHORT).show();


                return false;
            }
        });
    }

    private void setDrawerOption(String option){

        if(option.equals(Constant.SETTING)){
            Intent intent = new Intent(HomeActivity.this,SettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_up_in, R.anim.nothing);
        }else  if(option.equals(Constant.WISHLIST)){
            Intent intent = new Intent(HomeActivity.this,WishListActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_up_in, R.anim.nothing);
        }else if(option.equals(Constant.LOGIN)){

            Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_up_in, R.anim.nothing);
        }else {

        }

    }

    class SM_AsyncTaskForSetData extends AsyncTask<String, String, Boolean> {

        ProgressHUD mProgressHUD;
        String error = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressHUD = ProgressHUD.show(HomeActivity.this,
                    "Loading", true);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
//                JSONObject obj = new JSONLoader().getJsonfromURL(Constant.DATA_URL);

                JSONObject obj = new JSONObject(new JSONLoader().loadJSONFromAsset(HomeActivity.this, "json/shopsy.json"));

                if (obj != null && obj.has(Constant.JSON_STATUS)) {

                    if (obj.getString(Constant.JSON_STATUS).equals("true")) {

                        if (AppData.allProduct != null && AppData.allProduct.size() > 0) {
                            AppData.allProduct.clear();
                        }
                        if (AppData.mensProduct != null && AppData.mensProduct.size() > 0) {
                            AppData.mensProduct.clear();
                        }
                        if (AppData.womensProduct != null && AppData.womensProduct.size() > 0) {
                            AppData.womensProduct.clear();
                        }

                        AppData.allProduct = JsonParser.getProduct(obj.getString(Constant.JSON_ARRAY_NAME));

                        for(int i=0; i<AppData.allProduct.size(); i++){

                            ProductModel product = AppData.allProduct.get(i);

                            if(product.getCategory_name().equals(Constant.MEN)){

                                AppData.mensProduct.add(product);
                                AppData.menSubCatagory.add(product.getSub_catarogy());

                            }else if(product.getCategory_name().equals(Constant.WOMEN)){

                                AppData.womensProduct.add(product);
                                AppData.womenSubCatagory.add(product.getSub_catarogy());
                            }else {
                            }

                        }
                        return true;
                    }



                } else if (obj != null && obj.has(Constant.JSON_EXCEPTION)) {

                    error = obj.getString(Constant.JSON_EXCEPTION);

                    return false;
                }




            } catch (JSONException e) {
                e.printStackTrace();
            }


return false;
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result != null && result) {

                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragmentList).commit();

                setDrawerLayer();

                if (mProgressHUD.isShowing()) {
                    mProgressHUD.dismiss();

                }


            } else if (!error.isEmpty()) {

//                if (mProgressHUD.isShowing()) {
//                    mProgressHUD.dismiss();
//
//                }
//                showExceptionAlart( error);
            }


        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
            finish();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
