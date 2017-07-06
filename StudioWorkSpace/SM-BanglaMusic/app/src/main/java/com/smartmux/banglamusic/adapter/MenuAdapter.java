package com.smartmux.banglamusic.adapter;


import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.smartmux.banglamusic.R;
import com.smartmux.banglamusic.model.ItemRow;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;;

public class MenuAdapter extends ArrayAdapter<ItemRow> {
	Context context; 
    public MenuAdapter(Context context, int resourceId,
            List<ItemRow> items) {
        super(context, resourceId, items);
        this.context = context;
    }
 
    /*private view holder class*/
    private class ViewHolder {
        TextView txtTitle;
        TextView txtBengTitle;
        TextView txtArtist;
        TextView txtTotalSongs;
        ImageView thumbImage;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ItemRow rowItem = getItem(position);
 
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_row, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.txtBengTitle = (TextView) convertView.findViewById(R.id.bengaliTitle);
            holder.thumbImage = (ImageView) convertView.findViewById(R.id.thumbAlbum);
            holder.txtArtist = (TextView) convertView.findViewById(R.id.artist);
            holder.txtTotalSongs = (TextView) convertView.findViewById(R.id.totalSongs);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
 
        holder.txtTitle.setText(rowItem.getTitle());
        holder.txtBengTitle.setText(rowItem.getBengaliTitle());
        holder.txtArtist.setText(rowItem.getArtist());
        holder.txtTotalSongs.setText(rowItem.getTotalSongs());
        String imageUrl = rowItem.getImage();
        UrlImageViewHelper.setUrlDrawable(holder.thumbImage, imageUrl, R.drawable.placeholder_gaan);

        return convertView;
    }
   
}

