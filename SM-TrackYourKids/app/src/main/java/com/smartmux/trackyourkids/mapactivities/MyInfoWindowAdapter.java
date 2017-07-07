package com.smartmux.trackyourkids.mapactivities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.smartmux.trackyourkids.R;

/**
 * Created by Romana on 7/4/17.
 */
public class MyInfoWindowAdapter implements InfoWindowAdapter{

    private final View myContentsView;

    public MyInfoWindowAdapter(Context context){
        myContentsView = LayoutInflater.from(context).inflate(R.layout.custom_info_content, null);
    }

    @Override
    public View getInfoContents(Marker marker) {

        TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
        tvTitle.setText(marker.getTitle());
        TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
        tvSnippet.setText(marker.getSnippet());

        return myContentsView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Auto-generated method stub
        return null;
    }

}