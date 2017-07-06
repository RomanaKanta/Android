package com.smartmux.musiclist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smartmux.musiclist.R;
import com.smartmux.musiclist.dbhandler.Playlist;

import java.util.ArrayList;

/**
 * Created by tanvir-android on 5/16/16.
 */
public class AddSongtoPlaylist extends BaseAdapter {


    private final LayoutInflater mInflater;
    ArrayList<Playlist> playlist;
    private Context mContext;
    int audioId;

    public AddSongtoPlaylist(Context context,ArrayList<Playlist>  playlist,int audioId) {
        super();
        mInflater=LayoutInflater.from(context);
        this.playlist = playlist;
        this.mContext = context;
        this.audioId = audioId;

    }

    static class ViewHolder {
        TextView playListTitle;
    }

    @Override
    public int getCount() {
        return playlist.size();
    }
    @Override
    public Object getItem(int position) {
        return playlist.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder ;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.add_song_in_palylist,parent,false);

            holder.playListTitle = (TextView) convertView.findViewById(R.id.playlist_name);


            convertView.setTag(holder);
        }else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.playListTitle.setText(playlist.get(position).getName());


        return convertView;
    }

}
