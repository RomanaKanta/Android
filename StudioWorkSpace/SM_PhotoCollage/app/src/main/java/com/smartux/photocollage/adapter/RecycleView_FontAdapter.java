package com.smartux.photocollage.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartux.photocollage.R;

import java.util.ArrayList;

/**
 * Created by tanvir-android on 6/3/16.
 */
public class RecycleView_FontAdapter extends
        RecyclerView.Adapter<RecycleView_FontAdapter.TextFontViewHolder> {// Recyclerview will
    // extend to
    // recyclerview adapter
    public ArrayList<String> arrayList;
    public int selectedPosition = -1;
    OnItemClickListener mItemClickListener;
    boolean isPurchase;

    public RecycleView_FontAdapter(Context context, ArrayList<String> list) {
        this.arrayList = list;

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
    public void onBindViewHolder(TextFontViewHolder holder,
                                 final int position) {


        TextFontViewHolder frameHolder = (TextFontViewHolder) holder;// holder

        frameHolder.font.setText(""+arrayList.get(position));

    }

    @Override
    public TextFontViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                int viewType) {

        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.font_row, viewGroup, false);
        TextFontViewHolder listHolder = new TextFontViewHolder(
                mainGroup);
        return listHolder;

    }

    public class TextFontViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // View holder for gridview recycler view as we used in listview
        public TextView font;

        public TextFontViewHolder(View view) {
            super(view);
            // Find all views ids
            this.font = (TextView) view.findViewById(R.id.font_item);
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