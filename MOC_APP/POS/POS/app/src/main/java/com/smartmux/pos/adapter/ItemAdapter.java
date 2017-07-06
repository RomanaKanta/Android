package com.smartmux.pos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartmux.pos.R;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by smartmux on 3/21/16.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.CoachViewHolder> {


    private List<HashMap<String,String>> list;
    private Context context;

    public ItemAdapter(Context context, List<HashMap<String,String>> list) {
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


       HashMap<String,String> map = list.get(position);

        holder.txtName.setText(map.get("productCategoryName"));
        holder.txtQuantity.setText("Number of items: "+map.get("bq"));
       // holder.content.setBackgroundColor(Integer.parseInt(product.getProductThumb()));

    }

    @Override
    public CoachViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        return new CoachViewHolder(View.inflate(parent.getContext(), R.layout.item_inventory, null));
    }

    public class CoachViewHolder extends RecyclerView.ViewHolder {



        @Bind(R.id.item_name)
        TextView txtName;
        @Bind(R.id.item_quantity)
        TextView txtQuantity;
        @Bind(R.id.item_inventory)
        LinearLayout content;



        public CoachViewHolder(View convertView) {
            super(convertView);
            ButterKnife.bind(this, convertView);

        }

    }


}
