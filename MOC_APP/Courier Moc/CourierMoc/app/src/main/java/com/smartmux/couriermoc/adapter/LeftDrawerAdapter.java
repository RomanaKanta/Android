package com.smartmux.couriermoc.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.modelclass.DrawerOption;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by smartmux on 3/21/16.
 */
public class LeftDrawerAdapter extends RecyclerView.Adapter<LeftDrawerAdapter.AdapterViewHolder> {


    private ArrayList<DrawerOption> list;
    private Context context;

    public LeftDrawerAdapter(Context context, ArrayList<DrawerOption> list) {
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

        DrawerOption model = list.get(position);

        holder.txtTitle.setText(model.getTitle());
        holder.imgIcon.setImageResource(model.getIcon());
        holder.imgIcon.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        return new AdapterViewHolder(View.inflate(parent.getContext(), R.layout.item_option, null));
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder {


        @Bind(R.id.item_title)
        TextView txtTitle;
        @Bind(R.id.item_icon)
        ImageView imgIcon;


        public AdapterViewHolder(View convertView) {
            super(convertView);
            ButterKnife.bind(this, convertView);

        }

    }


}
