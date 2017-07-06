package com.smartux.photocollage.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.smartux.photocollage.R;
import com.smartux.photocollage.model.StickerCatagory;
import com.smartux.photocollage.widget.AppButtonView;

import java.util.ArrayList;

public class RecyclerView_StickerCatagoryAdapter extends
        RecyclerView.Adapter<RecyclerView_StickerCatagoryAdapter.StickerCatagoryViewHolder> {// Recyclerview will
    // extend to
    // recyclerview adapter
    Context context;
    OnCatagoryItemClickListener mItemClickListener;

//    Integer[] iconArray = new Integer[] {R.drawable.ic_emoticon, R.drawable.ic_masqarade, R.drawable.ic_sunglass};
//    String[] nameArray = new String[] {"Emoticon", "Masqarade", "Sunglass"};
//    String[] bgColorArray = new String[] {"#8e3606", "#ac80c5", "#73d4b4"};

    ArrayList<StickerCatagory> catagory;

    public RecyclerView_StickerCatagoryAdapter(Context context, ArrayList<StickerCatagory> catagory) {

        this.context = context;
        this.catagory = catagory;
    }

    @Override
    public int getItemCount() {
        return (null != catagory ? catagory.size() : 0);
    }

    @Override
    public void onBindViewHolder(StickerCatagoryViewHolder holder,
                                 final int position) {

        holder.stickerCatagory.setText(catagory.get(position).getTitle());
        holder.stickerCatagory.setCompoundDrawablesWithIntrinsicBounds(catagory.get(position).getIcon(), 0, 0, 0);
        GradientDrawable drawable = (GradientDrawable)  holder.catagoryLayer
                .getBackground();
        drawable.setColor(Color.parseColor(catagory.get(position).getColor()));


    }

    @Override
    public StickerCatagoryViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                 int viewType) {

        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.sticker_catagory_row, viewGroup, false);
        StickerCatagoryViewHolder listHolder = new StickerCatagoryViewHolder(
                mainGroup);
        return listHolder;

    }

public class StickerCatagoryViewHolder extends RecyclerView.ViewHolder
        implements OnClickListener {
    // View holder for gridview recycler view as we used in listview
    public AppButtonView stickerCatagory;
    public FrameLayout catagoryLayer;

    public StickerCatagoryViewHolder(View view) {
        super(view);
        // Find all views ids
        this.stickerCatagory = (AppButtonView) view
                .findViewById(R.id.catarogy_btn);
        this.catagoryLayer = (FrameLayout) view
                .findViewById(R.id.catagory_layout);
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

public interface OnCatagoryItemClickListener {
    public void onItemClick(View view, int position);
}

    public void SetOnCatagoryItemClickListener(
            final OnCatagoryItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}