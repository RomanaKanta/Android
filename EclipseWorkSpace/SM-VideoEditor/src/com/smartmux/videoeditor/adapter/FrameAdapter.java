package com.smartmux.videoeditor.adapter;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

import com.smartmux.videoeditor.R;
import com.smartmux.videoeditor.R.id;
import com.smartmux.videoeditor.R.layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class FrameAdapter extends BaseAdapter {

	private Context context;
    ArrayList<String> imageList = new ArrayList<String>();
 
    public FrameAdapter(Context c) {
        context = c; 
    }
 
    public void add(String path){
        imageList.add(path); 
    }
 
    @Override
    public int getCount() {
        return imageList.size();
    }
 
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }
 
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	View row = convertView;
    	VideoHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(R.layout.video_item, parent, false);
           
            holder = new VideoHolder();
            holder.itemImage= (ImageView)row.findViewById(R.id.imageView_thumb);
            holder.itemTitle=(TextView)row.findViewById(R.id.textView_name);
            row.setTag(holder);
        }
        else
        {
            holder = (VideoHolder)row.getTag();
        }
        Bitmap bm = decodeSampledBitmapFromUri(imageList.get(position), 220, 220);
        holder.itemImage.setImageBitmap(bm);
        File file = new File(imageList.get(position));
        holder.itemTitle.setText(FilenameUtils.removeExtension(file.getName()));
        return row;
    }
    
    static class VideoHolder
    {
    	ImageView	itemImage;
        TextView 	itemTitle;
    }
 
    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
 
        Bitmap bm = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
 
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options); 
 
        return bm;   
    }
 
    public int calculateInSampleSize(
 
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
 
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);    
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);    
            }   
        }
 
        return inSampleSize;    
    }
 
}
