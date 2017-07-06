package com.smartmux.pos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmux.pos.R;
import com.smartmux.pos.model.Sell;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by smartmux on 3/21/16.
 */
public class CashAdapter extends RecyclerView.Adapter<CashAdapter.CashViewHolder> {


    private List<Sell> list;
    private Context context;

    public CashAdapter(Context context, List<Sell> list) {
        super();
        this.context = context;
        this.list = list;
    }

    public void addSell(Sell sell){

        list.add(sell);
        notifyDataSetChanged();

    }

    public void deleteSell(int index){

        list.remove(index);
        notifyDataSetChanged();;
    }

    public List<Sell> getList() {
        return list;
    }

    public void setList(List<Sell> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public void onBindViewHolder(final CashViewHolder holder, int position) {


        Sell sell = list.get(position);


        holder.txtProductName.setText(sell.getProduct().getProductName()+" X "+sell.getQuantity());
        holder.txtProductPrice.setText(String.format(context.getString(R.string.total_amount),Float.parseFloat(sell.getProduct().getProductPrice())));
        holder.productIcon.setBackgroundColor(Integer.parseInt(sell.getProduct().getProductThumb()));

    }

    @Override
    public CashViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        return new CashViewHolder(View.inflate(parent.getContext(), R.layout.item_cash, null));
    }

    public class CashViewHolder extends RecyclerView.ViewHolder {



        @Bind(R.id.product_name)
        TextView txtProductName;
        @Bind(R.id.product_price)
        TextView txtProductPrice;
        @Bind(R.id.product_thumb)
        ImageView productIcon;


        public CashViewHolder(View convertView) {
            super(convertView);
            ButterKnife.bind(this, convertView);

        }



    }


}
