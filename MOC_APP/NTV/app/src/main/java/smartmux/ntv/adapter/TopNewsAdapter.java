package smartmux.ntv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crazyhitty.chdev.ks.rssmanager.RssItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import smartmux.ntv.R;
import smartmux.ntv.activity.NewsDetailActivity;


public class TopNewsAdapter extends RecyclerView.Adapter<TopNewsAdapter.ViewHolder> {

    private final ArrayList<RssItem> products;
    Context context;


    public TopNewsAdapter(Context context, ArrayList<RssItem> categories) {
        this.products = categories;
        this.context = context;
    }

    public void add(List<RssItem> categories) {
        this.products.clear();
        this.products.addAll(categories);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.top_news_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final RssItem product = products.get(position);
        final Context context = holder.mView.getContext();

        String html = product.getDescription();
        Document doc = Jsoup.parseBodyFragment(html);

        Element image = doc.select("img").first();
        String thumbUrl = image.attr("abs:src");

        Element link = doc.select("a").first();
        final String linkUrl = link.attr("href");

        Element text = doc.select("span").first();
        String description = text.text();

        holder.txtTitle.setText(product.getTitle());


        if (thumbUrl != null && !TextUtils.isEmpty(thumbUrl)) {

            Glide
                    .with(context)
                    .load(thumbUrl)
                    .centerCrop()
                    .placeholder(R.drawable.main_logo)
                    .crossFade()
                    .into(holder.thumbIcon);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, NewsDetailActivity.class);
                intent.putExtra("link",linkUrl);
                context.startActivity(intent);

            }
        });

    }



    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @Bind(R.id.news_image)
        ImageView thumbIcon;
        @Bind(R.id.news_title)
        TextView txtTitle;



        //        public ProductModelClass category;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;

        }

    }

    public ArrayList<RssItem> getProducts() {
        return products;
    }

}
