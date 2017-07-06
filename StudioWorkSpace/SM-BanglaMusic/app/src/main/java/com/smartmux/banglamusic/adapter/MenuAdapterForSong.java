package com.smartmux.banglamusic.adapter;


	import java.util.List;

	import com.smartmux.banglamusic.R;
	import com.smartmux.banglamusic.model.RowItemForSong;

	import android.app.Activity;
	import android.content.Context;
	import android.view.LayoutInflater;
	import android.view.View;
	import android.view.ViewGroup;
	import android.widget.ArrayAdapter;
	import android.widget.TextView;

	public class MenuAdapterForSong extends ArrayAdapter<RowItemForSong> {

		Context context; 
	    public MenuAdapterForSong(Context context, int resourceId,
	            List<RowItemForSong> items) {
	        super(context, resourceId, items);
	        this.context = context;
	    }
	 
	    /*private view holder class*/
	    private class ViewHolder {
	        TextView txtSong;
	        TextView txtArtist;
	    }
	 
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder = null;
	        RowItemForSong rowItem = getItem(position);
	 
	        LayoutInflater mInflater = (LayoutInflater) context
	                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.song_item_row, null);
	            holder = new ViewHolder();
	            holder.txtSong = (TextView) convertView.findViewById(R.id.songTitle);
	            holder.txtArtist = (TextView) convertView.findViewById(R.id.songArtist);
	            convertView.setTag(holder);
	        } else
	            holder = (ViewHolder) convertView.getTag();
	 
	        holder.txtSong.setText(rowItem.getTitleSong());
	        holder.txtArtist.setText(rowItem.getSongArtist());
	       
	        return convertView;
	    }
	}


