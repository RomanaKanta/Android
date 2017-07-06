package smartmux.ntv.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crazyhitty.chdev.ks.rssmanager.OnRssLoadListener;
import com.crazyhitty.chdev.ks.rssmanager.RssItem;
import com.crazyhitty.chdev.ks.rssmanager.RssReader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import smartmux.ntv.R;
import smartmux.ntv.adapter.RssFeedAdapter;

public class RSSReaderActivity extends AppCompatActivity implements OnRssLoadListener {

//    private ListView mListViewFeeds;

    @Bind(R.id.list_view_feeds)
    RecyclerView mListViewFeeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssreader);
        ButterKnife.bind(this);
//        mListViewFeeds = (ListView) findViewById(R.id.list_view_feeds);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getExtras().getString("title"));

        setList();
        String url = getIntent().getExtras().getString("rss_url");
        System.out.println(url);
        loadFeeds( url);
    }

    private void setList() {


        mListViewFeeds.setLayoutManager(new GridLayoutManager(RSSReaderActivity.this, 1));
        final int gridMargin = getResources().getDimensionPixelOffset(R.dimen.grid_margin);
        mListViewFeeds.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.set(gridMargin, gridMargin, gridMargin, gridMargin);
            }
        });

        mListViewFeeds.setPadding(gridMargin, gridMargin, gridMargin, gridMargin);

    }

    private void loadFeeds(String url) {
        String[] urlArr = {url};

        //deprecated since v 0.20
        /*RssReader rssReader = new RssReader(MainActivity.this, urlArr, null, null, null, this);
        rssReader.readRssFeeds();*/

        new RssReader(RSSReaderActivity.this)
                .showDialog(true)
                .urls(urlArr)
                .parse(this);
    }

    @Override
    public void onSuccess(List<RssItem> rssItems) {
        ArrayList<RssItem> rssTitles = new ArrayList<>();

        rssTitles.addAll(rssItems);

//        Log.e("Image" , rssTitles.get(0).getImageUrl());

//        for (RssItem rssItem : rssItems) {
//            rssTitles.add(rssItem.getTitle());
//        }



//        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(RSSReaderActivity.this, android.R.layout.simple_list_item_1, rssTitles);
        mListViewFeeds.setAdapter(new RssFeedAdapter(RSSReaderActivity.this,rssTitles));
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(RSSReaderActivity.this, "Error:\n" + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }
}
