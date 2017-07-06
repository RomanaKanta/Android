package com.ksproject.krishop.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ksproject.krishop.R;
import com.ksproject.krishop.activity.HomeActivity;
import com.ksproject.krishop.fragment.FragmentList;
import com.ksproject.krishop.modelclass.ProductModel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    Context context;
    ArrayList<ProductModel> modelClass;

    public HomeAdapter(Context context, ArrayList<ProductModel> modelClass) {
        this.context = context;
        this.modelClass = modelClass;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final ProductModel products = modelClass.get(position);
//
//        holder.txtProPrice.setText(products.getPrice());
//
//        holder.txtProName.setText(products.getProduct_name());
//
//
//        String thumbUrl = products.getProduct_image();
//
//
//        if (thumbUrl != null && !TextUtils.isEmpty(thumbUrl)) {
//
//            Glide
//                    .with(context)
//                    .load(thumbUrl)
//                    .placeholder(R.drawable.placeholder)
//                    .centerCrop()
//                    .into(holder.productImage);
//        }
//        final Context context = holder.mView.getContext();

        holder.catagory.setText(products.getCaterogy());

        holder.itemList.setAdapter(new ItemAdapter(context, products.getProductList()));

        holder.seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) context).setFragment(FragmentList.newInstance(products.getCaterogy(),""), products.getCaterogy());
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelClass.size();

    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        @Bind(R.id.recyclerViewList)
        RecyclerView itemList;

        @Bind(R.id.txt_see_all)
        TextView seeAll;

        @Bind(R.id.txt_catagory)
        TextView catagory;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            itemList.setHasFixedSize(true);
            itemList.setLayoutManager(new LinearLayoutManager(
                    context, LinearLayoutManager.HORIZONTAL,
                    false));

            mView = view;
        }


    }

}
