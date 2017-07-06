package com.smartux.photocollage.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smartux.photocollage.R;

import java.util.ArrayList;

public class RecyclerView_StickerAdapter extends
        RecyclerView.Adapter<RecyclerView_StickerAdapter.StickerViewHolder> {// Recyclerview will
    // extend to
    // recyclerview adapter
    public ArrayList<Integer> arrayList;
    public int selectedPosition = -1;
    OnItemClickListener mItemClickListener;
    boolean isPurchase;

    public RecyclerView_StickerAdapter(Context context, ArrayList<Integer> list,
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
        final int model = arrayList.get(position);

        StickerViewHolder frameHolder = (StickerViewHolder) holder;// holder

        frameHolder.stickerImage.setImageResource(model);

        frameHolder.image_lock.setVisibility(View.GONE);
//        if (isPurchase) {
//            frameHolder.image_lock.setVisibility(View.GONE);
//        } else {
//            if ( position > 9) {
//                frameHolder.image_lock.setColorFilter(
//                        Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
////				frameHolder.image_lock.setColorFilter(
////						Color.parseColor("#989C9F"), PorterDuff.Mode.SRC_ATOP);
//                frameHolder.image_lock.setVisibility(View.VISIBLE);
//            } else {
//                frameHolder.image_lock.setVisibility(View.GONE);
//            }
//        }

    }

    @Override
    public StickerViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                 int viewType) {

        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.sticker_item, viewGroup, false);
        StickerViewHolder listHolder = new StickerViewHolder(
                mainGroup);
        return listHolder;

    }

    public class StickerViewHolder extends RecyclerView.ViewHolder
            implements OnClickListener {
        // View holder for gridview recycler view as we used in listview
        public ImageView stickerImage, image_lock;

        public StickerViewHolder(View view) {
            super(view);
            // Find all views ids
            this.stickerImage = (ImageView) view
                    .findViewById(com.smartux.photocollage.R.id.image_sticker_item);
            this.image_lock = (ImageView) view
                    .findViewById(com.smartux.photocollage.R.id.sticker_lock);
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