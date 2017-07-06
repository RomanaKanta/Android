package smartmux.ntv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import smartmux.ntv.R;
import smartmux.ntv.adapter.ExpandableListAdapter;
import smartmux.ntv.fragment.LiveTVFragment;
import smartmux.ntv.fragment.PhotoFragment;
import smartmux.ntv.fragment.TopNewsFragment;
import smartmux.ntv.fragment.VideoFragment;
import smartmux.ntv.model.MenuModel;
import smartmux.ntv.utils.JsonParser;
import smartmux.ntv.utils.ViewFindUtils;
import smartmux.ntv.widget.AnimatedExpandableListView;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    //    private PagerSlidingTabStrip tabs;
//    private SlidingTabLayout tabs;
//    private ViewPager pager;
//    private MyPagerAdapter adapter;
    @Bind(R.id.headline)
    TextView txtHeasline;

    @Bind(R.id.navList)
    AnimatedExpandableListView mExpandableListView;
//    ExpandableListView mExpandableListView;


    private ExpandableListAdapter mExpandableListAdapter;
    private List<MenuModel> mExpandableListTitle;
    private Map<MenuModel, List<String>> mExpandableListData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        setHeadlineLayer();
        setTabLayer();
        setDrawerLayer();

    }

    private void setHeadlineLayer() {

        ArrayList<String> headlineArray = JsonParser.getHeadlineData(this);

        String headlines = "";

        for (int i = 0; i < headlineArray.size(); i++) {

            headlines = headlines + "       >  " + headlineArray.get(i);

        }

        txtHeasline.setText(headlines);

    }

    private void setTabLayer() {

        View decorView = getWindow().getDecorView();
        ViewPager vp = ViewFindUtils.find(decorView, R.id.pager);
        vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        SlidingTabLayout tabLayout = ViewFindUtils.find(decorView, R.id.tabs);
        tabLayout.setViewPager(vp);

    }

    private void setDrawerLayer() {

//        mExpandableListView = (ExpandableListView) findViewById(R.id.navList);
        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.nav_header_main, null, false);
        mExpandableListView.addHeaderView(listHeaderView);

        mExpandableListData = JsonParser.getExpandableListData(this);

        mExpandableListTitle = new ArrayList(mExpandableListData.keySet());
//        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        addDrawerItems();

    }

    private void addDrawerItems() {

        mExpandableListAdapter = new ExpandableListAdapter(this, mExpandableListTitle, mExpandableListData);
        mExpandableListView.setAdapter(mExpandableListAdapter);
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (mExpandableListData.get(mExpandableListTitle.get(groupPosition))
                        .size() == 0) {
                    drawer.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(MainActivity.this, RSSReaderActivity.class);
                    intent.putExtra("rss_url", mExpandableListTitle.get(groupPosition).getRssUrl());
                    intent.putExtra("title", mExpandableListTitle.get(groupPosition).getName());
                    startActivity(intent);
                }else {

                    if (mExpandableListView.isGroupExpanded(groupPosition)) {
                        mExpandableListView.collapseGroupWithAnimation(groupPosition);
                    } else {
                        mExpandableListView.expandGroupWithAnimation(groupPosition);
                    }
                }
                return true;
            }
        });
//        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                // getSupportActionBar().setTitle(mExpandableListTitle.get(groupPosition).toString());
//
//                if (mExpandableListData.get(mExpandableListTitle.get(groupPosition))
//                        .size() == 0) {
//                    drawer.closeDrawer(GravityCompat.START);
//                    Intent intent = new Intent(MainActivity.this, RSSReaderActivity.class);
//                    intent.putExtra("rss_url", mExpandableListTitle.get(groupPosition).getRssUrl());
//                    intent.putExtra("title", mExpandableListTitle.get(groupPosition).getName());
//                    startActivity(intent);
//                }
//
//
//            }
//        });

//        mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                //getSupportActionBar().setTitle(R.string.film_genres);
//            }
//        });

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String selectedItem = ((List) (mExpandableListData.get(mExpandableListTitle.get(groupPosition))))
                        .get(childPosition).toString();

                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MainActivity.this, RSSReaderActivity.class);
                intent.putExtra("rss_url", mExpandableListTitle.get(groupPosition).getRssUrl());
                intent.putExtra("title", mExpandableListTitle.get(groupPosition).getName() + " : " + selectedItem);
                startActivity(intent);


                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"TOP NEWS", "LIVE TV", "VIDEO", "PHOTO"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
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
        public Fragment getItem(int position) {
            if (position == 0) {
                return TopNewsFragment.newInstance(String.valueOf(position), "");
            } else if (position == 1) {
                return LiveTVFragment.newInstance(String.valueOf(position), "");
            } else if (position == 2) {
                return VideoFragment.newInstance(String.valueOf(position), "");
            } else if (position == 3) {
                return PhotoFragment.newInstance(String.valueOf(position), "");
            } else {
                return null;
            }
        }
    }
}
