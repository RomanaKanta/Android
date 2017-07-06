package com.smartux.photocollage.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartux.photocollage.R;
import com.smartux.photocollage.model.ListData;

import java.util.ArrayList;

/**
 * Created by tanvir-android on 6/3/16.
 */
public class RecyclerView_TextAdapter extends
        RecyclerView.Adapter<RecyclerView_TextAdapter.StickerTextViewHolder> {// Recyclerview will
    // extend to
    // recyclerview adapter
    public ArrayList<ListData> arrayList;
    OnItemClickListener mItemClickListener;
    boolean isPurchase;
    public int views = 0;

    public RecyclerView_TextAdapter(Context context, ArrayList<ListData> list,int viewsize) {
        this.arrayList = list;
        this.views = viewsize;

    }

    public boolean isPurchase() {
        return isPurchase;
    }

    public void setPurchase(boolean isPur) {
        this.isPurchase = isPur;
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);

    }

    public int getviewsize() {
        return views;
    }

    public void setviewsize(int selectedPosition) {
        this.views = selectedPosition;
    }

    @Override
    public void onBindViewHolder(StickerTextViewHolder holder,
                                 final int position) {
        final ListData model = arrayList.get(position);

        StickerTextViewHolder frameHolder = (StickerTextViewHolder) holder;// holder

        // Populate the text
        holder.textView.setText(model.getmText());
        holder.imageView.setImageResource(model.getmImage());
        holder.layout.setBackgroundColor(Color.parseColor(model
                .getmBackground_color()));

        if(views==0){

            if (position == 0) {
                holder.textView.setTextColor(Color.parseColor("#FFFFFF"));
                holder.imageView.setColorFilter(Color.parseColor("#FFFFFF"),
                        PorterDuff.Mode.SRC_ATOP);
            }
            else{
                holder.textView.setTextColor(Color.parseColor("#787878"));
                holder.imageView.setColorFilter(Color.parseColor("#787878"),
                        PorterDuff.Mode.SRC_ATOP);
            }
        }else{
            holder.textView.setTextColor(Color.parseColor("#FFFFFF"));
            holder.imageView.setColorFilter(Color.parseColor("#FFFFFF"),
                    PorterDuff.Mode.SRC_ATOP);
        }


    }

    @Override
    public StickerTextViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                int viewType) {

        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.text_list_item, viewGroup, false);
        StickerTextViewHolder listHolder = new StickerTextViewHolder(
                mainGroup);
        return listHolder;

    }

    public class StickerTextViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // View holder for gridview recycler view as we used in listview
        public TextView textView;
        public ImageView imageView;
        public RelativeLayout layout;

        public StickerTextViewHolder(View view) {
            super(view);
            // Find all views ids
            this.layout = (RelativeLayout) view
                    .findViewById(R.id.text_layout);

            this.textView = (TextView) view
                    .findViewById(R.id.text_title);
            this.imageView = (ImageView) view
                    .findViewById(R.id.text_icon);
            view.setOnClickListener(this);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }

        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(
            final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}