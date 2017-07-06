package com.smartmux.couriermoc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.modelclass.HistoryData;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by smartmux on 3/21/16.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.AdapterViewHolder> {


    private ArrayList<HistoryData> list;
    private Context context;

    public HistoryAdapter(Context context, ArrayList<HistoryData> list) {
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
    public void onBindViewHolder(final AdapterViewHolder holder, int position) {

        HistoryData model = list.get(position);

        holder.txtID.setText(model.getOrderID() + "(" + model.getStatus() + ")");
        holder.txtDate.setText(model.getDate());

    }

    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        return new AdapterViewHolder(View.inflate(parent.getContext(), R.layout.history_data_row, null));
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder {


        @Bind(R.id.order_id)
        TextView txtID;
        @Bind(R.id.date)
        TextView txtDate;


        public AdapterViewHolder(View convertView) {
            super(convertView);
            ButterKnife.bind(this, convertView);

        }

    }


}
