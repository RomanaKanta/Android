package com.smartmux.photocutter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmux.photocutter.R;
import com.smartmux.photocutter.modelclass.ListData;

import java.util.ArrayList;

public class RecyclerView_CommonAdapter extends
        RecyclerView.Adapter<RecyclerView_CommonAdapter.StickerViewHolder> {// Recyclerview will
    // extend to
    // recyclerview adapter
    public ArrayList<ListData> arrayList;
    public int selectedPosition = -1;
    OnItemClickListener mItemClickListener;
    boolean isPurchase;

    public RecyclerView_CommonAdapter(Context context, ArrayList<ListData> list,
                                      boolean isPur) {
        this.arrayList = list;
        this.isPurchase = isPur;

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

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    @Override
    public void onBindViewHolder(StickerViewHolder holder,
                                 final int position) {
        final ListData model = arrayList.get(position);

        StickerViewHolder frameHolder = (StickerViewHolder) holder;// holder

        frameHolder.shapeImage.setImageResource(model.getmImage());
        frameHolder.textView.setText(model.getmText());

    }

    @Override
    public StickerViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                 int viewType) {

        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.row_item, viewGroup, false);
        StickerViewHolder listHolder = new StickerViewHolder(
                mainGroup);
        return listHolder;

    }

    public class StickerViewHolder extends RecyclerView.ViewHolder
            implements OnClickListener {
        // View holder for gridview recycler view as we used in listview
        public ImageView shapeImage;
        public TextView textView;

        public StickerViewHolder(View view) {
            super(view);
            // Find all views ids
            this.shapeImage = (ImageView) view
                    .findViewById(R.id.row_image);
            this.textView = (TextView) view.findViewById(R.id.row_title);

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