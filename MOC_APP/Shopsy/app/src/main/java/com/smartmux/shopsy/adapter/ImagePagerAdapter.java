package com.smartmux.shopsy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * ImagePagerAdapter
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-23
 */
public class ImagePagerAdapter extends RecyclingPagerAdapter {

//    private Context       context;
//    private ArrayList<BannerItem> BannerList;
//
//    private int           size;
    private boolean       isInfiniteLoop;
//    ImageLoader mImageLoader;
//
//    public ImagePagerAdapter(Context context, ArrayList<BannerItem> list) {
//        this.context = context;
//        this.BannerList = list;
//        this.size = list.size();
//        isInfiniteLoop = false;
//        mImageLoader = new ImageLoader(context);
//    }

    Context context;
    ArrayList<Integer> imageArray;
    int PAGES;
    public ImagePagerAdapter(Context context, ArrayList<Integer> images) {
        this.context = context;
        this.imageArray = images;
        this.PAGES = images.size();
        isInfiniteLoop = false;
    }

    @Override
    public int getCount() {
        // Infinite loop
        return isInfiniteLoop ? Integer.MAX_VALUE : imageArray.size();
    }

    /**
     * get really position
     *
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return isInfiniteLoop ? position % PAGES : position;
    }

    @Override
    public View getView( int position, View view, ViewGroup container) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = holder.imageView = new ImageView(context);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

//        if (!TextUtils.isEmpty(BannerList.get(position).getThumbUrl())) {
//
//			mImageLoader.DisplayImage(BannerList.get(position).getThumbUrl(), holder.imageView);
//
//        }
//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!TextUtils.isEmpty(BannerList.get(position).getActionUrl())) {
//                    Intent i = new Intent(Intent.ACTION_VIEW, Uri
//                            .parse(BannerList.get(position).getActionUrl()));
//                    context.startActivity(i);
//                }
//            }
//        });

        holder.imageView.setImageResource(imageArray.get(position));

        return view;
    }



    private static class ViewHolder {

        ImageView imageView;
    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop the isInfiniteLoop to set
     */
    public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }
}