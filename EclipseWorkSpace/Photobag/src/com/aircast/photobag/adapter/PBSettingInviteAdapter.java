package com.aircast.photobag.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aircast.photobag.R;

/**
 * @author thetv class: show contact. set adapter for listview.
 */
public class PBSettingInviteAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<DetailData> mItems;
    private boolean  mIsDetail = false;
    /**
     * @param context
     *            main context.
     * @param list
     *            list of contact.
     */
    public PBSettingInviteAdapter(Context context, ArrayList<DetailData> list, boolean isDetail) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
        mItems = list;
        mIsDetail = isDetail;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    public int getCount() {
        if (mItems == null) {
            return 0;
        }
        return mItems.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    public Object getItem(int position) {
        return position;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView tvData, tvType;

        if (convertView == null) {
            if(mIsDetail){
                convertView = mInflater.inflate(
                        R.layout.pb_adapter_list_setting_invite_detail_item, null);
            }else{
                convertView = mInflater.inflate(
                        R.layout.pb_adapter_list_setting_invite_item, null);
            }
        } 

        // fix check out_of_bound
        if(mItems != null && position < mItems.size()){
            DetailData item = mItems.get(position);

            if(item != null){
                tvData = (TextView) 
                        convertView.findViewById(R.id.pb_tv_adapter_setting_invite);        

                tvData.setText(item.data);

                if(mIsDetail && !TextUtils.isEmpty(item.id)){
                    tvType = (TextView) convertView
                    .findViewById(R.id.pb_tv_adapter_setting_invite_type);
                    tvType.setText(item.id);
                }
            }
        }
        return convertView;
    }

    /** class info contact detail phone/email */
    public static class DetailData{
        public String id;
        public String data;

        /**
         * construct DetailData 
         * @param type
         * @param data
         */
        public DetailData(String id, String data) {
            this.id = id;
            this.data = data;
        }
    }
}
