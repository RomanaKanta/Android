package com.smartmux.shopsy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartmux.shopsy.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ProductSizeAdapter extends RecyclerView.Adapter<ProductSizeAdapter.ViewHolder> {

        ArrayList<String> list;
    Context context;
    boolean selected = false;


    public ProductSizeAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_product_size, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.txtSize.setText(list.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selected){

                    holder.txtSize.setTextColor(Color.BLACK);
                    holder.txtSize.setBackgroundResource(R.drawable.size_row_bg);
                    selected = false;

                }else {

                    holder.txtSize.setTextColor(Color.WHITE);
                    holder.txtSize.setBackgroundResource(R.drawable.size_row_selected_bg);
                    selected = true;
                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {



        @Bind(R.id.txt_product_size)
        TextView txtSize;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);



        }

    }
}
