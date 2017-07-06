package com.smartmux.pos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmux.pos.R;
import com.smartmux.pos.model.Product;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by smartmux on 3/21/16.
 */
public class TempProductAdapter extends RecyclerView.Adapter<TempProductAdapter.CoachViewHolder> {


    private List<Product> list;
    private Context context;

    public TempProductAdapter(Context context, List<Product> list) {
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

        holder.txtName.setText(product.getProductName() + " "+ product.getProductQuantity() + "("+ product.getProductUnit() +")");
        holder.imageAdd.setColorFilter(R.color.colorPrimary);

    }

    @Override
    public CoachViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        return new CoachViewHolder(View.inflate(parent.getContext(), R.layout.temp_product_item, null));
    }

    public class CoachViewHolder extends RecyclerView.ViewHolder {



        @Bind(R.id.textView_temp_product)
        TextView txtName;
        @Bind(R.id.image_add)
        ImageView imageAdd;




        public CoachViewHolder(View convertView) {
            super(convertView);
            ButterKnife.bind(this, convertView);

        }

    }


}
