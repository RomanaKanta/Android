package com.smartmux.shopsy.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by tanvir-android on 9/20/16.
 */
public class MyPagerAdapter extends PagerAdapter {



    Context context;
    ArrayList<Integer> imageArray;
    int PAGES;
    int LOOPS = 1000;
    public MyPagerAdapter(Context context, ArrayList<Integer> images) {
        this.context = context;
        this.imageArray = images;
        this.PAGES = images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ViewHolder holder = new ViewHolder();
//        if (view == null) {
//            holder = new ViewHolder();
//            view = holder.imageView = new ImageView(context);
//            view.setTag(holder);
//        } else {
//            holder = (ViewHolder)view.getTag();
//        }
        if (container == null) {
            return null;
        }

        holder.imageView = new ImageView(context);
//        LayoutInflater inflater = LayoutInflater.from(context);
//
//        View view = inflater.inflate(R.layout.page, container, false);


        position = position % PAGES;

//        ImageView image=(ImageView)view.findViewById(R.id.pagerimage);
//        image.setImageResource(imageArray.get(position));

        holder.imageView.setImageResource(imageArray.get(position));

        container.addView(holder.imageView);
        return holder.imageView;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return PAGES * LOOPS;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    private static class ViewHolder {

        ImageView imageView;
    }

}