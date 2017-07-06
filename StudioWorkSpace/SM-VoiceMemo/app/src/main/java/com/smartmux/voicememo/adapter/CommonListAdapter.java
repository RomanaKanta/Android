package com.smartmux.voicememo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmux.voicememo.R;
import com.smartmux.voicememo.modelclass.CommonItemRow;

import org.apache.commons.io.FilenameUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommonListAdapter extends ArrayAdapter<CommonItemRow> {

	Context context;
    int layoutResourceId;   
    ArrayList<CommonItemRow> data=new ArrayList<CommonItemRow>();
    SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
    public CommonListAdapter(Context context, int layoutResourceId, ArrayList<CommonItemRow> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RegardingTypeHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new RegardingTypeHolder();
            holder.itemImage= (ImageView)row.findViewById(R.id.itemIcon);
            holder.itemTitle=(TextView)row.findViewById(R.id.itemTitle);
            holder.itemSize=(TextView)row.findViewById(R.id.itemSize);
            holder.itemDateTime=(TextView)row.findViewById(R.id.itemDateTime);
            holder.itemOptinal=(TextView)row.findViewById(R.id.itemOptional);
            row.setTag(holder);
        }
        else
        {
            holder = (RegardingTypeHolder)row.getTag();
        }
       
        CommonItemRow address = data.get(position);
        holder.itemImage.setImageResource(address.getImage());
        holder.itemTitle.setText(FilenameUtils.removeExtension(address.getTitle()));
        holder.itemSize.setText(address.getSize());
        holder.itemDateTime.setText(""+format.format(new Date(address.getDateTime()))+" "+sdf.format(new Date(address.getDateTime())));
        holder.itemOptinal.setText(address.getOptional()+" mins");
        return row;
    }
   
    static class RegardingTypeHolder
    {
    	ImageView	itemImage;
        TextView 	itemTitle;
        TextView 	itemSize;
        TextView 	itemDateTime;
        TextView 	itemOptinal;
    }
}

