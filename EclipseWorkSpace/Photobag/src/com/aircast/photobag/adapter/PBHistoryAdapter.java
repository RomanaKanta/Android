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
import com.aircast.photobag.model.PBHistoryEntryModel;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBImageLoaderUtils;

public class PBHistoryAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<PBHistoryEntryModel> mDataList;
    private PBImageLoaderUtils mImageLoader;
    private boolean mIsInInBox;

    public PBHistoryAdapter(Context context, ArrayList<PBHistoryEntryModel> list) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDataList = list;
        mImageLoader = new PBImageLoaderUtils(context, true);
    }

    /**
     * Refresh data in history
     * 
     * @param data
     * @param isInInbox
     */
    public void resetAdapter(ArrayList<PBHistoryEntryModel> data,
            boolean isInInbox) {
        synchronized (this) {
            mDataList = data;
            mIsInInBox = isInInbox;
            notifyDataSetChanged();
        }
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
     * The number of items in the list is determined by the number of speeches
     * in our array.
     * 
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        if (mDataList == null) {
            return 0;
        }
        return mDataList.size();
        // return DATA.length;
    }

    /**
     * Since the data comes from an array, just returning the index is sufficent
     * to get at the data. If we were using a more complex data structure, we
     * would return whatever object represents one row in the list.
     * 
     * @see android.widget.ListAdapter#getItem(int)
     */
    public Object getItem(int position) {
        return position;
    }

    /**
     * Use the array index as a unique id.
     * 
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * Make a view to hold each row.
     * 
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (mDataList == null || mDataList.size() == 0) {
            return convertView;
        }
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.pb_adapter_list_history_item, null);

            holder = new ViewHolder();
            holder.itemDisplayedPassword = (TextView) convertView
                    .findViewById(R.id.tv_history_item_password);
            holder.itemReceivedDateCaption = (TextView) convertView
                    .findViewById(R.id.tv_history_item_date_caption);
            holder.itemReceivedDate = (TextView) convertView
                    .findViewById(R.id.tv_history_item_date);
            holder.itemNumofDownload = (TextView) convertView
                    .findViewById(R.id.tv_history_item_num_download);
            holder.itemImageThump = (ImageView) convertView
                    .findViewById(R.id.history_list_item_thum);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position < 0 || position > mDataList.size()) {
            return convertView;
        }
        PBHistoryEntryModel item = mDataList.get(position);
        if (item != null) {
            holder.itemDisplayedPassword.setText(item.getEntryPassword());
            String dateCaption = mContext.getString(R.string.pb_history_info_date);
            if (!mIsInInBox) {
                dateCaption = mContext.getString(R.string.pb_history_info_date_outbox);
            }
            holder.itemReceivedDateCaption.setText(dateCaption);
            holder.itemReceivedDate.setText(PBGeneralUtils.convertMilisecondToDate(item
                    .getEntryId()));
            holder.itemNumofDownload.setText(String.valueOf(item.getEntryNumofDownload()));

            if (!TextUtils.isEmpty(item.getEntryThump())) {
                mImageLoader.displayImage(PBGeneralUtils.getPathFromCacheFolder(item.getEntryThump()),
                        holder.itemImageThump);
            }
        }
        return convertView;
    }

    static class ViewHolder {
        TextView itemDisplayedPassword;
        TextView itemReceivedDateCaption;
        TextView itemReceivedDate;
        TextView itemNumofDownload;
        ImageView itemImageThump;
    }
}
