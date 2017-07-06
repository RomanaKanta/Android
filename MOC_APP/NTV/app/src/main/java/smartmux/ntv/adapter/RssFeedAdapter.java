package smartmux.ntv.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import smartmux.ntv.R;
import smartmux.ntv.activity.NewsDetailActivity;


public class RssFeedAdapter extends RecyclerView.Adapter<RssFeedAdapter.ViewHolder> {

    private final ArrayList<RssItem> products;
    Context context;


    public RssFeedAdapter(Context context, ArrayList<RssItem> categories) {
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
                .inflate(R.layout.list_feeds_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final RssItem product = products.get(position);
        final Context context = holder.mView.getContext();

          /*  ///   description /////
        <div><a href='http://en.ntvbd.com/sports/33489/Oldest-surviving-former-Test-player-dies-aged-97'>
        <img title='porimoni' src='http://ntv-en-cdn.s3.amazonaws.com/site_images/photo-1473133447.jpg'
        alt='Oldest surviving former Test player dies aged 97' width='200' height='133' />
        </a>
        <span>Johannesburg: South African Lindsay Tuckett, who was the oldest surviving former Test cricketer,
                died Monday in Bloemfontein aged 97, his family said. On behalf of the Cricket South Africa
        family I extend our deepest condolences to his family, friends and cricketing colleagues, said chief
        executive Haroon Lorgat. A tall fast bowler, he ...
        <a class='more_link' href='http://en.ntvbd.com/sports/33489/Oldest-surviving-former-Test-player-dies-aged-97'>more</a><
        /span></div>*/
        String html = product.getDescription();
        Document doc = Jsoup.parseBodyFragment(html);

        Element image = doc.select("img").first();
        String thumbUrl = image.attr("abs:src");

        Element link = doc.select("a").first();
        final String linkUrl = link.attr("href");

        Element text = doc.select("span").first();
        String description = text.text();

        holder.txtTitle.setText(product.getTitle());
        holder.txtDate.setText(product.getPubDate());
        holder.txtDescription.setText(description + "...");

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

        @Bind(R.id.wish_image)
        ImageView thumbIcon;
        @Bind(R.id.title)
        TextView txtTitle;
        @Bind(R.id.description)
        TextView txtDescription;
        @Bind(R.id.date)
        TextView txtDate;


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


    public Html.ImageGetter getImageHTML() {
        Html.ImageGetter imageGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {
                try {
                    Drawable drawable = Drawable.createFromStream(new URL(source).openStream(), "src name");
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    return drawable;
                } catch (IOException exception) {
                    Log.v("IOException", exception.getMessage());
                    return null;
                }
            }
        };
        return imageGetter;
    }

    public class HttpGetDrawableTask extends AsyncTask<String, Void, Drawable> {
        TextView taskTextView;
        String taskHtmlString;

        HttpGetDrawableTask(TextView v, String s) {
            taskTextView = v;
            taskHtmlString = s;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            Drawable drawable = null;
            URL sourceURL;
            try {
                sourceURL = new URL(params[0]);
                URLConnection urlConnection = sourceURL.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(
                        inputStream);
                Bitmap bm = BitmapFactory.decodeStream(bufferedInputStream);

                // convert Bitmap to Drawable
                drawable = new BitmapDrawable(context.getResources(), bm);

                drawable.setBounds(0, 0, bm.getWidth(), bm.getHeight());

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {

            final Drawable taskDrawable = result;
            if (taskDrawable != null) {
                taskTextView.setText(Html.fromHtml(taskHtmlString,
                        new Html.ImageGetter() {

                            @Override
                            public Drawable getDrawable(String source) {
                                return taskDrawable;
                            }
                        }, null));
            }

        }
    }
}
