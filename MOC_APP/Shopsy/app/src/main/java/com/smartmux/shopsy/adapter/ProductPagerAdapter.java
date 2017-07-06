package com.smartmux.shopsy.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.smartmux.shopsy.R;
import com.smartmux.shopsy.activity.ProductDetailActivity;
import com.smartmux.shopsy.utils.Constant;

import java.util.ArrayList;

/**
 * Created by tanvir-android on 9/20/16.
 */
public class ProductPagerAdapter extends PagerAdapter {

    Context context;
    ArrayList<String> imageArray;
//    int PAGES;
    public ProductPagerAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.imageArray = images;
//        this.PAGES = images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

      final   ViewHolder holder = new ViewHolder();

        if (container == null) {
            return null;
        }

        holder.imageView = new ImageView(context);


        String imageURL = imageArray.get(position);

        if (imageURL != null && !TextUtils.isEmpty(imageURL)) {


            String url = Constant.IMAGE_HTTP + imageURL;
            Glide
                    .with(context)
                    .load(url)
                    .placeholder(R.drawable.placeholder)
                    .crossFade()
                    .into( holder.imageView);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((ProductDetailActivity)context).zoomImageFromThumb(holder.imageView);
            }
        });


        container.addView(holder.imageView);
        return holder.imageView;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return imageArray.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    private static class ViewHolder {

        ImageView imageView;
    }

}