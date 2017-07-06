package com.smartmux.pos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartmux.pos.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by smartmux on 3/21/16.
 */
public class ItemOptionAdapter extends RecyclerView.Adapter<ItemOptionAdapter.OptionViewHolder> {


    private List<String> list;
    private Context context;


    public ItemOptionAdapter(Context context, List<String> list) {
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
    public void onBindViewHolder(final OptionViewHolder holder, int position) {

        holder.txtTitle.setText(list.get(position));

    }

    @Override
    public OptionViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        return new OptionViewHolder(View.inflate(parent.getContext(), R.layout.option_item_row, null));
    }

    public class OptionViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.textview_item)
        TextView txtTitle;

        public OptionViewHolder(View convertView) {
            super(convertView);
            ButterKnife.bind(this, convertView);

        }
    }

}
