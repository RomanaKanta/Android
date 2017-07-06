package com.smartmux.pos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartmux.pos.R;
import com.smartmux.pos.model.Product;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by smartmux on 3/21/16.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.CoachViewHolder> {


    private List<Product> list;
    private Context context;

    public ProductAdapter(Context context, List<Product> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public void onBindViewHolder(final CoachViewHolder holder, int position) {


        Product product = list.get(position);

        holder.txtCardName.setText(product.getProductName());
        holder.txtQuantity.setText(product.getProductQuantity() + "("+ product.getProductUnit() +")");
        holder.content.setBackgroundColor(Integer.parseInt(product.getProductThumb()));

    }

    @Override
    public CoachViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        return new CoachViewHolder(View.inflate(parent.getContext(), R.layout.item_product, null));
    }

    public class CoachViewHolder extends RecyclerView.ViewHolder {



        @Bind(R.id.item_product_title)
        TextView txtCardName;
        @Bind(R.id.item_product_quantity)
        TextView txtQuantity;
        @Bind(R.id.item_product)
        RelativeLayout content;



        public CoachViewHolder(View convertView) {
            super(convertView);
            ButterKnife.bind(this, convertView);

        }

    }


}
