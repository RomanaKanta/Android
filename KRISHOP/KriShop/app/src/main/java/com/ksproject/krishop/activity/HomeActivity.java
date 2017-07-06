package com.ksproject.krishop.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ksproject.krishop.R;
import com.ksproject.krishop.adapter.ExpandableListAdapter;
import com.ksproject.krishop.dialogfragment.CartDialog;
import com.ksproject.krishop.dialogfragment.FilterDialog;
import com.ksproject.krishop.fragment.FragmentAboutUs;
import com.ksproject.krishop.fragment.FragmentHistory;
import com.ksproject.krishop.fragment.FragmentHome;
import com.ksproject.krishop.fragment.FragmentList;
import com.ksproject.krishop.fragment.FragmentRecipent;
import com.ksproject.krishop.fragment.FragmentSetting;
import com.ksproject.krishop.modelclass.AppData;
import com.ksproject.krishop.modelclass.Products;
import com.ksproject.krishop.utils.Constant;
import com.ksproject.krishop.utils.JSONLoader;
import com.ksproject.krishop.utils.JsonParser;
import com.ksproject.krishop.widget.AnimatedExpandableListView;
import com.ksproject.krishop.widget.ProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements
//        NavigationView.OnNavigationItemSelectedListener,
        FragmentHome.OnFragmentInteractionListener,
        FragmentList.OnFragmentInteractionListener,
        FragmentHistory.OnFragmentInteractionListener,
        FragmentRecipent.OnFragmentInteractionListener,
        FragmentAboutUs.OnFragmentInteractionListener,
        FragmentSetting.OnFragmentInteractionListener
{

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

//    @Bind(R.id.nav_view)
//    NavigationView navigationView;

    @Bind(R.id.root)
    LinearLayout root;

    @Bind(R.id.navList)
    AnimatedExpandableListView mExpandableListView;

    private ExpandableListAdapter mExpandableListAdapter;
    private ArrayList<String> mExpandableListHeader;
    private HashMap<String, ArrayList<String>> mExpandableListChild;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    @Bind(R.id.action_title)
    TextView title;
    TextView txtCartCount;
    ImageView imageCart;

    @OnClick(R.id.menu_filter)
    public void onFilter(){
        new FilterDialog().show(fragmentManager, "Filter_Dialog_Fragment");
    }

    @OnClick(R.id.menu_cart)
    public void onCart(){
        new CartDialog().show(fragmentManager, "Cart_Dialog_Fragment");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);


        fragmentManager = getSupportFragmentManager();
        setAppBar();

        new SM_AsyncTaskForSetData().execute();

    }
    public void setFragment(Fragment fragment, String pageTitle) {

        title.setText(pageTitle);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    private void setAppBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

    }


    private void setHeaderFooter() {

        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.nav_header, null, false);
        mExpandableListView.addHeaderView(listHeaderView);


        prepareListData();
        addDrawerItems();



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
        mExpandableListHeader.add(getString(R.string.home));
        mExpandableListHeader.add(getString(R.string.category));
        mExpandableListHeader.add(getString(R.string.history));
        mExpandableListHeader.add(getString(R.string.receipt));
        mExpandableListHeader.add(getString(R.string.settings));
        mExpandableListHeader.add(getString(R.string.aboutus));
        mExpandableListHeader.add(getString(R.string.login));

        mExpandableListChild.put(mExpandableListHeader.get(0), new ArrayList<String>());
        mExpandableListChild.put(mExpandableListHeader.get(1), AppData.catagory); // Header, Child data
        mExpandableListChild.put(mExpandableListHeader.get(2), new ArrayList<String>());
        mExpandableListChild.put(mExpandableListHeader.get(3), new ArrayList<String>());
        mExpandableListChild.put(mExpandableListHeader.get(4), new ArrayList<String>());
        mExpandableListChild.put(mExpandableListHeader.get(5), new ArrayList<String>());
        mExpandableListChild.put(mExpandableListHeader.get(6), new ArrayList<String>());
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

                setFragment(FragmentList.newInstance(selectedItem,""), selectedItem);
//                Toast.makeText(HomeActivity.this, "" + selectedItem, Toast.LENGTH_SHORT).show();


                return false;
            }
        });
    }

    private void setDrawerOption(String option) {

        if (option.equals(getString(R.string.home))) {

            setFragment(new FragmentHome(), getString(R.string.home));

        } else if (option.equals(getString(R.string.history))) {

            setFragment(new FragmentHistory(), getString(R.string.history));

        } else if (option.equals(getString(R.string.receipt))) {

            setFragment(new FragmentRecipent(), getString(R.string.receipt));
        } else if (option.equals(getString(R.string.settings))) {
            setFragment(new FragmentSetting(), getString(R.string.settings));

        } else if (option.equals(getString(R.string.aboutus))) {

            setFragment(new FragmentAboutUs(), getString(R.string.aboutus));
        } else if (option.equals(getString(R.string.login))) {

            Intent intent = new Intent(HomeActivity.this, SingUpActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_left_out);


        } else{

        }

        }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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

                JSONObject obj = new JSONObject(new JSONLoader().loadJSONFromAsset(HomeActivity.this, "json/krishop.json"));

                if (obj != null && obj.has(Constant.JSON_ARRAY_NAME)) {



                        if (AppData.allProduct != null && AppData.allProduct.size() > 0) {
                            AppData.allProduct.clear();
                        }


                        AppData.allProduct = JsonParser.getProducts(obj.getString(Constant.JSON_ARRAY_NAME));

                        for (int i = 0; i < AppData.allProduct.size(); i++) {

                            Products product = AppData.allProduct.get(i);

                            AppData.catagory.add(product.getCatagory());
                            AppData.area.add(product.getProduct_area());
                            AppData.harvest_time.add(product.getHarvest_time());

                        }

                    LinkedHashSet<String> cata = new LinkedHashSet<String>();
                    cata.addAll(AppData.catagory);
                    AppData.catagory.clear();
                    AppData.catagory.addAll(cata);

                    LinkedHashSet<String> proArea = new LinkedHashSet<String>();
                    proArea.addAll(AppData.area);
                    AppData.area.clear();
                    AppData.area.addAll(proArea);

                    LinkedHashSet<String> time = new LinkedHashSet<String>();
                    time.addAll(AppData.harvest_time);
                    AppData.harvest_time.clear();
                    AppData.harvest_time.addAll(time);

                        return true;



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

                setHeaderFooter();

                setFragment(new FragmentHome(), getString(R.string.home));

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

}
