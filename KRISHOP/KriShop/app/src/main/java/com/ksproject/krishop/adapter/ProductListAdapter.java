package com.ksproject.krishop.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ksproject.krishop.R;
import com.ksproject.krishop.activity.ProductDetailActivity;
import com.ksproject.krishop.modelclass.Products;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    Context context;
    ArrayList<Products> modelClass;

    public ProductListAdapter(Context context, ArrayList<Products> modelClass) {
        this.context = context;
        this.modelClass = modelClass;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Products products = modelClass.get(position);

        holder.txtProPrice.setText(products.getPrice());

        holder.txtProName.setText(products.getProduct_name());


        String thumbUrl = products.getProduct_image();


        if (thumbUrl != null && !TextUtils.isEmpty(thumbUrl)) {

            Glide
                    .with(context)
                    .load(thumbUrl)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.productImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("Product", products);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);


            }
        });
    }

    @Override
    public int getItemCount() {
        return modelClass.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.pro_image)
        ImageView productImage;

        @Bind(R.id.pro_name)
        TextView txtProName;

        @Bind(R.id.pro_price)
        TextView txtProPrice;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

}
