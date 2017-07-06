package com.smartmux.shopsy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.smartmux.shopsy.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ProductBrandAdapter extends RecyclerView.Adapter<ProductBrandAdapter.ViewHolder> {

        ArrayList<String> list = new ArrayList<>();
    Context context;


    public ProductBrandAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_product_brand, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.txtBrand.setText(list.get(position));



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.chk_box_brand)
        CheckBox chk_brand;

        @Bind(R.id.textview_brand)
        TextView txtBrand;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);



        }

    }
}
