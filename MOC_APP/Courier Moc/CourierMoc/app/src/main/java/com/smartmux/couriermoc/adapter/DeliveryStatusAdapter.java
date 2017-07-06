package com.smartmux.couriermoc.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.activity.StatusDetail;
import com.smartmux.couriermoc.modelclass.StatusInfo;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by smartmux on 3/21/16.
 */
public class DeliveryStatusAdapter extends RecyclerView.Adapter<DeliveryStatusAdapter.AdapterViewHolder> {


    private ArrayList<StatusInfo> list;
    private Context context;
    String pos;

    public DeliveryStatusAdapter(Context context, ArrayList<StatusInfo> list,String pos) {
        super();
        this.context = context;
        this.list = list;
        this.pos = pos;
    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public void onBindViewHolder(final AdapterViewHolder holder, int position) {

        final StatusInfo model = list.get(position);

            holder.txtID.setText(model.getOrderID());
            holder.txtDate.setText(model.getDate());




        holder.rowLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StatusDetail.class);
                intent.putExtra("position", pos);
                intent.putExtra("id",model.getOrderID());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        return new AdapterViewHolder(View.inflate(parent.getContext(), R.layout.status_data_row, null));
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder {


        @Bind(R.id.textView_id)
        TextView txtID;
        @Bind(R.id.textView_date)
        TextView txtDate;
        @Bind(R.id.status_row)
        RelativeLayout rowLayer;


        public AdapterViewHolder(View convertView) {
            super(convertView);
            ButterKnife.bind(this, convertView);

        }

    }


}
