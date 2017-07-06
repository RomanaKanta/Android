package com.aircast.koukaibukuro.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircast.koukaibukuro.util.Constant;
import com.aircast.koukaibukuro.util.GeneralUtils;
import com.aircast.koukaibukuro.util.ImageLoaderUtils;
import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBHistoryInboxDetailActivity;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.model.PBHistoryEntryModel;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;

public class HistoryAdapter extends BaseAdapter {
	private ImageLoaderUtils mImageLoader;
	private ArrayList<PBHistoryEntryModel> list;
	private final LayoutInflater inflater;
	private Context ctx;
	private boolean isSent = false;

	public HistoryAdapter(Context context,ArrayList<PBHistoryEntryModel> _list, boolean isSentFrom) {
		super();
		this.ctx = context;
		this.list = _list;
		this.isSent = isSentFrom;
		this.inflater = LayoutInflater.from(context);
		mImageLoader = new ImageLoaderUtils(ctx, true);
	}

	public int getCount() {
		
		return list.size();
	}

	public Object getItem(int arg0) {
	
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void clearCache() {
		if (mImageLoader != null) {
			mImageLoader.clearCache();
		}
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {

			holder = new ViewHolder();
			convertView =  inflater.inflate(R.layout.kb_adapter_list_history_item, parent,
					false);

			holder.iconThub = (ImageView) convertView
					.findViewById(R.id.imageView_photo_thumb);

			holder.thumbCatagory = (ImageView) convertView
					.findViewById(R.id.imageView_photo_thumb_catagory);
			
			holder.thumbNew = (ImageView) convertView
					.findViewById(R.id.imageView_new_item);
			

			holder.password = (TextView) convertView
					.findViewById(R.id.textView_password);
			holder.posted_user_name = (TextView) convertView
					.findViewById(R.id.textView_posted_user_name);
			holder.expiresTime = (TextView) convertView
					.findViewById(R.id.textView_expires_time);
			
			holder.photo_count = (TextView) convertView
					.findViewById(R.id.textView_photos_count);
			holder.downloaded_users_count = (TextView) convertView
					.findViewById(R.id.textView_downloaded_users_count);
			 holder.photo_thumb_serial = (TextView) convertView
			 .findViewById(R.id.photo_thumb_serial);

			holder.item_category = (TextView) convertView
					.findViewById(R.id.TextView_item_category);
			holder.item_honey = (TextView) convertView
					.findViewById(R.id.TextView_item_honey);

			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		
		
		if(isSent) {
			holder.posted_user_name.setText(ctx.getString(R.string.kb_history_info_sent_date)+" "+GeneralUtils
					.convertMilisecondToDate((list.get(position).getEntryId()
							)));
		} else {
			holder.posted_user_name.setText(ctx.getString(R.string.pb_history_info_date)+" "+GeneralUtils
					.convertMilisecondToDate(list.get(position).getEntryId()));
		}
		

		holder.password.setText(list.get(position).getEntryPassword());

		
		holder.downloaded_users_count.setText(String.valueOf((list.get(position)
				.getEntryNumofDownload())));
		

		if (list.get(position).getEntryNumOfPhoto() != 0) {
			holder.photo_count.setText(String.valueOf(list.get(position).getEntryNumOfPhoto()));
		} else {
			holder.photo_count.setText("0");
			//holder.totalPhotos.setText("");
		}

		String thumbUrl = list.get(position).getEntryThump();
		 if (!TextUtils.isEmpty(thumbUrl)) {
             try{
                 mImageLoader.displayImageForHistoryThumb(
                         PBGeneralUtils.getPathFromCacheFolder(thumbUrl), holder.iconThub);
             }catch (OutOfMemoryError e) {
                 e.printStackTrace();
             }
         }
		notifyDataSetChanged();


		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Bundle extras = new Bundle();

				extras.putLong(
						Constant.HISTORY_ITEM_ID_K,
						list.get(position).getEntryId());
				extras.putString(Constant.HISTORY_COLLECTION_ID_K, list
						.get(position).getCollectionId());
				extras.putString(Constant.HISTORY_PASSWORD_K,
						list.get(position).getEntryPassword());
				extras.putLong(
						Constant.HISTORY_CREATE_DATE_K,
						list.get(position).getEntryCreateDate());
				extras.putLong(
						Constant.HISTORY_CHARGE_DATE_K,
						list.get(position).getEntryChargeDate());
				extras.putString(Constant.COLLECTION_THUMB_K,
						list.get(position).getEntryThump());
				extras.putInt(
						Constant.HISTORY_ADDIBILITY_K,
						list.get(position).getEntryAddibility());
				
				extras.putBoolean(Constant.HISTORY_CATEGORY_INBOX_K, 
				          !isSent);
				
				extras.putInt(
						Constant.HISTORY_IS_UPDATABLE_K,
						(int)list.get(position).getEntryIsUpdatable());
				extras.putLong(
						Constant.HISTORY_UPDATED_AT_K,
						list.get(position).getEntryUpdatedAt());
				extras.putInt(
						Constant.HISTORY_SAVE_MARK_K,
						list.get(position).getEntrySaveMark()
								);
				extras.putInt(
						Constant.HISTORY_SAVE_DAYS_K,
						list.get(position).getEntrySaveDays());
				extras.putString(Constant.HISTORY_AD_LINK_K,
						list.get(position).getEntryAdLink());
				
				PBPreferenceUtils.saveIntPref(ctx.getApplicationContext(), PBConstant.PREF_NAME, 
		    			Constant.KB_POSITION, position);

				Intent intent = new Intent(
						ctx,
						PBHistoryInboxDetailActivity.class);
				intent.putExtra("data", extras);
				intent.putExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY, true);

				((Activity) ctx).startActivity(intent);
			}
		});

		return convertView;

	}
	
	private static class ViewHolder {
		public ImageView iconThub;
	    public ImageView thumbCatagory;
	    public ImageView thumbNew;
		public TextView password;
		public TextView posted_user_name;
		public TextView expiresTime;
		public TextView downloaded_users_count;
		public TextView item_category;
		public TextView photo_thumb_serial;
		public TextView photo_count;
	    public TextView item_honey;
	}


}