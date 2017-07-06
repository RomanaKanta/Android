package smartmux.ntv.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crazyhitty.chdev.ks.rssmanager.OnRssLoadListener;
import com.crazyhitty.chdev.ks.rssmanager.RssItem;
import com.crazyhitty.chdev.ks.rssmanager.RssReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import smartmux.ntv.R;
import smartmux.ntv.activity.NewsDetailActivity;
import smartmux.ntv.adapter.TopNewsAdapter;
import smartmux.ntv.model.AppData;

public class TopNewsFragment extends Fragment implements OnRssLoadListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public TopNewsFragment() {
        // Required empty public constructor
    }

    public static TopNewsFragment newInstance(String param1, String param2) {
        TopNewsFragment fragment = new TopNewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Bind(R.id.top_news_feeds)
    RecyclerView mListViewFeeds;

    @Bind(R.id.top_image)
    ImageView topImage;

    @Bind(R.id.top_news)
    TextView topNews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_news, container, false);
        ButterKnife.bind(this, view);

        setList();

//        if(AppData.rssTitles.size()==0) {
//
            loadFeeds("http://en.ntvbd.com/rssfeeds/rss.xml");
//        }else {

//            setView(AppData.rssTitles);
//        }

        return view;
    }



    private void setList() {


        mListViewFeeds.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//        final int gridMargin = getResources().getDimensionPixelOffset(R.dimen.grid_margin);
        mListViewFeeds.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.set(0, 0, 0, 0);
            }
        });

        mListViewFeeds.setPadding(0, 0, 0, 0);
    }

    private void loadFeeds(String url) {
        String[] urlArr = {url};

        //deprecated since v 0.20
        /*RssReader rssReader = new RssReader(MainActivity.this, urlArr, null, null, null, this);
        rssReader.readRssFeeds();*/

        new RssReader(getActivity())
                .showDialog(true)
                .urls(urlArr)
                .parse(this);
    }

    @Override
    public void onSuccess(List<RssItem> rssItems) {
        ArrayList<RssItem> rssTitles = new ArrayList<>();

        rssTitles.addAll(rssItems);

        AppData.rssTitles = rssTitles;
        Log.e("onSuccess", "Call");
        setView(rssTitles);


    }

    private void setView(ArrayList<RssItem> rssTitles){

        String html = rssTitles.get(0).getDescription();
        Document doc = Jsoup.parseBodyFragment(html);

        Element image = doc.select("img").first();
        String thumbUrl = image.attr("abs:src");

        Element link = doc.select("a").first();
        final String linkUrl = link.attr("href");

        topNews.setText(rssTitles.get(0).getTitle());


        if (thumbUrl != null && !TextUtils.isEmpty(thumbUrl)) {

            Glide
                    .with(getActivity())
                    .load(thumbUrl)
                    .centerCrop()
                    .placeholder(R.drawable.main_logo)
                    .crossFade()
                    .into(topImage);

        }

        topImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("link",linkUrl);
                getActivity().startActivity(intent);
            }
        });

        rssTitles.remove(0);
        mListViewFeeds.setAdapter(new TopNewsAdapter(getActivity(),rssTitles));
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(getActivity(), "Error:\n" + message, Toast.LENGTH_SHORT).show();
    }

}
