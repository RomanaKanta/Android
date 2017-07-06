package com.smartmux.videoeditor.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.smartmux.videoeditor.R;

public class VideoFrameAdapter extends BaseAdapter {

	private Context context;
    List<Bitmap> imageList = new ArrayList<Bitmap>();
 
    public VideoFrameAdapter(Context c) {
        context = c; 
    }
    public VideoFrameAdapter(Context c,List<Bitmap> imageList) {
        context = c; 
        this.imageList = imageList;
    }
 
    public void add(Bitmap bitmap){
        imageList.add(bitmap); 
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
            row = inflater.inflate(R.layout.frame_item, parent, false);
           
            holder = new VideoHolder();
            holder.itemImage= (ImageView)row.findViewById(R.id.imageView_thumb);
            row.setTag(holder);
        }
        else
        {
            holder = (VideoHolder)row.getTag();
        }
        holder.itemImage.setImageBitmap(imageList.get(position));
        return row;
    }
    
    static class VideoHolder
    {
    	ImageView	itemImage;
    }
 
//    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
// 
//        Bitmap bm = null;
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(path, options);
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
// 
//        options.inJustDecodeBounds = false;
//        bm = BitmapFactory.decodeFile(path, options); 
// 
//        return bm;   
//    }
// 
//    public int calculateInSampleSize(
// 
//        BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
// 
//        if (height > reqHeight || width > reqWidth) {
//            if (width > height) {
//                inSampleSize = Math.round((float)height / (float)reqHeight);    
//            } else {
//                inSampleSize = Math.round((float)width / (float)reqWidth);    
//            }   
//        }
// 
//        return inSampleSize;    
//    }
 
}
