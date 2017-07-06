package com.smartmux.shopsy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.smartmux.shopsy.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ProductColorAdapter extends RecyclerView.Adapter<ProductColorAdapter.ViewHolder> {

    ArrayList<String> list;
    Context context;


    public ProductColorAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_product_color, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        GradientDrawable drawable = (GradientDrawable) holder.color
                .getBackground();
        drawable.setColor(Color.parseColor(list.get(position)));



        holder.color.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(holder.colorSelected.getVisibility()== View.VISIBLE){
                    holder.colorSelected.setVisibility(View.INVISIBLE);
                }else {
                    holder.colorSelected.setVisibility(View.VISIBLE);
                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.product_color)
        RelativeLayout color;

        @Bind(R.id.color_selected)
        ImageView colorSelected;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);



        }

    }
}
