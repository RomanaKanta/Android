package com.smartmux.foto.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartmux.foto.R;
import com.smartmux.foto.adapter.RecycleView_HorizontalListAdapter.RecyclerFooterViewHolder;
import com.smartmux.foto.modelclass.ListData;

import java.util.ArrayList;

public class RecycleView_HorizontalListAdapter extends
		RecyclerView.Adapter<RecyclerFooterViewHolder> {// Recyclerview will
    // extend to
    // recyclerview adapter
    private ArrayList<ListData> arrayList;
    OnItemClickListener mItemClickListener;
    Context context;
    int pos = 0;
    boolean isPurchase;

    public RecycleView_HorizontalListAdapter(Context context,
                                             ArrayList<ListData> list,boolean purc) {
        this.arrayList = list;
        this.context = context;
        this.isPurchase = purc;
    }

    public boolean isPurchase() {
        return isPurchase;
    }

    public void setPurchase(boolean isPurchase) {
        this.isPurchase = isPurchase;
    }

    public long getItemId(int position) {
        return arrayList.get(position).getMid();
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);

    }

    @Override
    public void onBindViewHolder(RecyclerFooterViewHolder holder,
                                 final int position) {
        final ListData model = arrayList.get(position);

        RecyclerFooterViewHolder viewHolder = (RecyclerFooterViewHolder) holder;// holder

        viewHolder.textView.setText(model.getmText());
        viewHolder.layout.setBackgroundColor(Color.parseColor(model
                .getmBackground_color()));
        viewHolder.imageView.setImageResource(model.getmImage());

        if(isPurchase){

            viewHolder.imageView_lock.setVisibility(View.GONE);
            viewHolder.image_lock_bg.setVisibility(View.GONE);

        }else{

            if(position==2|| position==9||position==10){


                viewHolder.imageView_lock.setVisibility(View.VISIBLE);
                viewHolder.image_lock_bg.setVisibility(View.VISIBLE);
//				viewHolder.imageView_lock.setAlpha(200);//value: [0-255]. Where 0 is fully transparent and 255 is fully opaque.
            }else{
                viewHolder.imageView_lock.setVisibility(View.GONE);
                viewHolder.image_lock_bg.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public RecyclerFooterViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                       int viewType) {

        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.listitem,
                viewGroup, false);
        RecyclerFooterViewHolder listHolder = new RecyclerFooterViewHolder(
                mainGroup);
        return listHolder;

    }

    public void setValue(ArrayList<ListData> value) {
        this.arrayList = value;
        notifyDataSetChanged();
    }

    public void setSelected(int p) {
        this.pos = p;
        notifyDataSetChanged();
    }

    public class RecyclerFooterViewHolder extends RecyclerView.ViewHolder
            implements OnClickListener {
        // View holder for gridview recycler view as we used in listview
        public TextView textView;
        public ImageView imageView,imageView_lock,image_lock_bg;
        public RelativeLayout layout;

        public RecyclerFooterViewHolder(View view) {
            super(view);
            // Find all views ids
            this.layout = (RelativeLayout) view.findViewById(R.id.id_layout);
            this.textView = (TextView) view.findViewById(R.id.title);
            this.imageView = (ImageView) view.findViewById(R.id.image);
            this.imageView_lock = (ImageView) view.findViewById(R.id.image_lock);
            this.image_lock_bg= (ImageView) view.findViewById(R.id.image_lock_bg);
            view.setOnClickListener(this);
        }

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