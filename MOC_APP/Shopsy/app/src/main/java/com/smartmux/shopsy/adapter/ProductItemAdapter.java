/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smartmux.shopsy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.smartmux.shopsy.R;
import com.smartmux.shopsy.activity.ProductDetailActivity;
import com.smartmux.shopsy.modelclass.ProductModel;
import com.smartmux.shopsy.utils.Constant;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    Context context;
    private LayoutInflater mInflater;
    private  ArrayList<ProductModel> products;
    private View mHeaderView;

    public ProductItemAdapter(Context context, ArrayList<ProductModel> product, View headerView) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.products = product;
        mHeaderView = headerView;
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null) {
            return products.size();
        } else {
            return products.size() + 1;
        }
    }

    public void setList(ArrayList<ProductModel> product){
        this.products = product;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderViewHolder(mHeaderView);
        } else {
            return new ItemViewHolder(mInflater.inflate(R.layout.row_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder) {

            final ProductModel product = products.get(position-1);

            ((ItemViewHolder) viewHolder).category = product;
            ((ItemViewHolder) viewHolder).txtTitle.setText(product.getProductName());

            ((ItemViewHolder) viewHolder).txtDescription.setText(Html.fromHtml(Html.fromHtml(product.getProductDescription()).toString()));
            ((ItemViewHolder) viewHolder).txtPrice.setText(product.getProductPrice() + " " + product.getCurrency());


        String thumbUrl = Constant.IMAGE_HTTP + product.getProductThumbSmall();
        // Warning: onError() will not be called, if url is null.
        // Empty url leads to app crash.
        if (thumbUrl == null) {
            ((ItemViewHolder) viewHolder).txtTitle.setVisibility(View.VISIBLE);
        }

        if (thumbUrl != null && !TextUtils.isEmpty(thumbUrl)) {

            Glide
                    .with(context)
                    .load(thumbUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .centerCrop()
                    .into(((ItemViewHolder) viewHolder).thumbIcon);
        }


            ((ItemViewHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("PRODUCT",product);
                    context.startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.push_left_in,
                            R.anim.push_left_out);


                }
            });
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.pro_thumb)
        ImageView thumbIcon;
        @Bind(R.id.pro_name)
        TextView txtTitle;
        @Bind(R.id.pro_description)
        TextView txtDescription;
        @Bind(R.id.pro_price)
        TextView txtPrice;
        public ProductModel category;
        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    public ArrayList<ProductModel> getProducts() {
        return products;
    }
    }

