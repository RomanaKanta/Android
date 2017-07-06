package com.aircast.photobag.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircast.photobag.R;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.model.PBHistoryPhotoModel;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBImageLoaderUtils;

public class PBHistoryDetailAdapter extends BaseAdapter {

    protected LayoutInflater mInflater;
    protected Context mContext;
    protected PBImageLoaderUtils mImageLoader;
    protected ArrayList<PBHistoryPhotoModel> mDataList;
    protected ArrayList<String> mSelection;
    
    private boolean mShowCheckBox;

    public PBHistoryDetailAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mImageLoader = new PBImageLoaderUtils(context, false);
        
        mShowCheckBox = false;
    }

    /**
     * Clear cache bitmap
     */
    public void clearCache() {
        if (mImageLoader != null) {
            mImageLoader.clearCache();
        }
    }

    /**
     * Recycle all bitmap in cache
     */
    public void recycleAll()
    {
        if (mImageLoader != null) {
            mImageLoader.recycleAll();
        }
    }
    
    /**
     * Refresh data in history
     * 
     * @param data
     */
    public void resetAdapter(ArrayList<PBHistoryPhotoModel> data
    		, ArrayList<String> selection) {
        synchronized (this) {
            mDataList = data;
            mSelection = selection;
            notifyDataSetChanged();
        }
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     * 
     * @see android.widget.ListAdapter#getCount()
     */
    @Override
    public int getCount() {
        return mDataList.size();
    }

    /**
     * Since the data comes from an array, just returning the index is sufficent
     * to get at the data. If we were using a more complex data structure, we
     * would return whatever object represents one row in the list.
     * 
     * @see android.widget.ListAdapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
    	if (position < mDataList.size())
    		return mDataList.get(position);
    	
    	return null;
    }

    /**
     * Use the array index as a unique id.
     * 
     * @see android.widget.ListAdapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    public void setCheckBoxStatus(boolean status) {
    	mShowCheckBox = status;
    }

    /**
     * Make a view to hold each row.
     * 
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mDataList == null || mDataList.size() == 0) {
            return convertView;
        }
        // 20120327 added by NhatVT, support display video icon <S>
        // ImageView imageView;
        Holder mHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.pb_history_detail_adapter_item, null);
            mHolder = new Holder();
            mHolder.mThumb = (ImageView)convertView.findViewById(R.id.image_thump);
            mHolder.mVideoIcon = (ImageView)convertView.findViewById(R.id.image_thump_video);
            mHolder.mCheckBox = (ImageView) convertView.findViewById(R.id.image_checkbox);
            mHolder.mVideoDuration = (TextView)convertView.findViewById(R.id.image_thump_video_duration);
            convertView.setTag(mHolder);
        } else {
            // imageView = (ImageView) convertView.getTag();
            mHolder = (Holder) convertView.getTag();
        }
        // fix check out_of_bound
        if(mDataList != null && position < mDataList.size()){
            PBHistoryPhotoModel item = mDataList.get(position);
            // fix check null
            if (item != null && !TextUtils.isEmpty(item.getThumb())
                    && mImageLoader != null) {
                mImageLoader.displayImage(PBGeneralUtils.getPathFromCacheFolder(item.getThumb()), mHolder.mThumb/*imageView*/);
            }
            // show or hide video icon
            mHolder.mVideoIcon.setVisibility(View.GONE);
            mHolder.mVideoDuration.setVisibility(View.GONE);
            if (item != null) {
                if (item.getMediaType() == PBDatabaseDefinition.MEDIA_VIDEO) {
                    mHolder.mVideoIcon.setVisibility(View.VISIBLE);
                    mHolder.mVideoDuration.setVisibility(View.VISIBLE);
                    // Log.i("mapp", ">>> video duration = " + item.getVideoDuration());
                    mHolder.mVideoDuration.setText(PBGeneralUtils.makeTimeString(mContext, item.getVideoDuration()/1000));
                }
            }
            if (mShowCheckBox && mSelection != null) {
            	mHolder.mCheckBox.setVisibility(View.VISIBLE);
            	if (mSelection.contains(mDataList.get(position).getPhoto())) {
            		mHolder.mCheckBox.setImageResource(R.drawable.checkbox_on);
            	} 
            	else {
            		mHolder.mCheckBox.setImageResource(R.drawable.checkbox_off);
            	}
            } 
            else {
            	mHolder.mCheckBox.setVisibility(View.GONE);
            }
        } 
        // 20120327 added by NhatVT, support display video icon <S>
        return convertView;
    }
    
    // 20120327 added by NhatVT, support display video icon <S>
    public class Holder {
        public ImageView mThumb;
        public ImageView mCheckBox;
        public ImageView mVideoIcon;
        public TextView mVideoDuration;
    }
    // 20120327 added by NhatVT, support display video icon <E>
}
