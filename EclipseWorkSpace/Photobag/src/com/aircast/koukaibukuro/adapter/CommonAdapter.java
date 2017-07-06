package com.aircast.koukaibukuro.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.koukaibukuro.database.DatabaseHandler;
import com.aircast.koukaibukuro.model.Password;
import com.aircast.koukaibukuro.util.Constant;
import com.aircast.koukaibukuro.util.ImageLoader;
import com.aircast.koukaibukuro.util.KBDownLoadManager;
import com.aircast.koukaibukuro.util.Util;
import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBHistoryInboxDetailActivity;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;

public class CommonAdapter extends BaseAdapter{
	protected ImageLoader mImageLoader;
	private Context ctx;
	private List<Password> list = new ArrayList<Password>();
	private List<HashMap<String, String>> historyList = new ArrayList<HashMap<String, String>>();
	private boolean isMyPage;
	private boolean isRanking;
	private boolean isRecommend;
	private DatabaseHandler db;

	public CommonAdapter(Context context, List<Password> list,
			boolean isMyPage, boolean isRanking,
			List<HashMap<String, String>> historyList,boolean isRecommend) {
		super();
		this.ctx = context;
		this.list = list;
		this.isMyPage = isMyPage;
		this.isRanking = isRanking;
		this.historyList = historyList;
		this.isRecommend = isRecommend;
		mImageLoader = new ImageLoader(context);
		db = new DatabaseHandler(ctx);
	}

	public int getCount() {
		
		return list.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {

			holder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(ctx);
//			if (isRanking) {
//
//				convertView = inflater.inflate(R.layout.kb_common_rankinglist_row,
//						parent, false);
//			} else {
//
//				convertView = inflater.inflate(R.layout.kb_common_list_row, parent,
//						false);
//			}
			
			convertView = inflater.inflate(R.layout.kb_common_list_row, parent,
					false);
			
			holder.iconThub = (ImageView) convertView
					.findViewById(R.id.imageView_photo_thumb);

			holder.thumbCatagory = (ImageView) convertView
					.findViewById(R.id.imageView_photo_thumb_catagory);
			
			holder.thumbNew = (ImageView) convertView
					.findViewById(R.id.imageView_new_item);
			
			holder.thumbRecommend = (ImageView) convertView
					.findViewById(R.id.imageView_recommend_item);
			

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
			
			holder.chargesTime = (TextView) convertView
					.findViewById(R.id.textView_charges_time);
			
			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}


		
		if(!isRecommend){
			if (list.get(position).getRecommend() == 1) {
				holder.thumbRecommend.setVisibility(View.VISIBLE);
			} else if (list.get(position).getRecommend() == 0) { // bug fix
																	// display
				holder.thumbRecommend.setVisibility(View.GONE);
			}	
		}
		
		
		
		if(isRanking || isMyPage){
			   if (list.get(position).getNewItem() == 1) {
			    holder.thumbNew.setVisibility(View.VISIBLE);
			   } else if (list.get(position).getNewItem() == 0) { // bug fix
			                 // display
			    holder.thumbNew.setVisibility(View.GONE);
			   } 
			   
	    }

		holder.password.setText(list.get(position).getPassword());
		holder.posted_user_name.setText(list.get(position).getNickName());
		holder.expiresTime.setText(list.get(position).getExpiredTime());
		holder.chargesTime.setText(""+list.get(position).getChargesTime());
		
		holder.photo_count.setText(""+list.get(position).getPhotoCount());
		holder.downloaded_users_count.setText(""+ list.get(position).getNumberOfDownload());


		if(!TextUtils.isEmpty(list.get(position).getThumbURL())){
			
			mImageLoader.DisplayImage(list.get(position).getThumbURL(),
					holder.iconThub);
			
		}
		

		if (isRanking) {
			
			holder.password.setText((position + 1)+". "+list.get(position).getPassword());
		}
		if (!list.get(position).isDownload()) {
			holder.item_honey.setVisibility(View.INVISIBLE);
			holder.item_category.setVisibility(View.VISIBLE);
			holder.item_category.setBackgroundResource(R.drawable.custom_shape_onlyfree);
			holder.item_category.setText(ctx.getString(R.string.kb_free));
			holder.item_category.setTextColor(ctx.getResources().getColor(R.color.gray_deep_dark));
			holder.chargesTime.setVisibility(View.VISIBLE);
		}

		if (list.get(position).getHoney() > 0) {
			holder.item_honey.setVisibility(View.VISIBLE);
			holder.item_category.setVisibility(View.INVISIBLE);
			holder.chargesTime.setVisibility(View.INVISIBLE);
		}else{
			
			holder.item_honey.setVisibility(View.INVISIBLE);
			holder.item_category.setVisibility(View.VISIBLE);
			holder.chargesTime.setVisibility(View.VISIBLE);
		}
		
		if (list.get(position).isDownload()) {
			holder.item_honey.setVisibility(View.INVISIBLE);
			holder.item_category.setVisibility(View.VISIBLE);
			holder.item_category.setBackgroundResource(R.drawable.custom_shape_freeandopen);
			holder.item_category.setText(ctx.getString(R.string.kb_open));
			holder.item_category.setTextColor(ctx.getResources().getColor(R.color.blue_apple));
			holder.chargesTime.setVisibility(View.INVISIBLE);

		}

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


					if (list.get(position).isDownload()) {
						for (int j = 0; j < historyList.size(); j++) {

							if (list.get(position).getPassword()
									.equals(historyList.get(j).get(
											Constant.C_PASSWORD))) {
								Bundle extras = new Bundle();

								extras.putLong(
										Constant.HISTORY_ITEM_ID_K,
										(Long.parseLong(historyList.get(j).get(
												Constant.C_ID))));
								extras.putString(
										Constant.HISTORY_COLLECTION_ID_K,
										historyList.get(j).get(
												Constant.C_COLECTION_ID));
								extras.putString(
										Constant.HISTORY_PASSWORD_K,
										historyList.get(j).get(
												Constant.C_PASSWORD));
								extras.putLong(
										Constant.HISTORY_CREATE_DATE_K,
										(Long.parseLong(historyList.get(j).get(
												Constant.C_CREATED_AT))));
								extras.putLong(
										Constant.HISTORY_CHARGE_DATE_K,
										(Long.parseLong(historyList.get(j).get(
												Constant.C_CHARGES_AT))));
								extras.putString(Constant.COLLECTION_THUMB_K,
										historyList.get(j)
												.get(Constant.C_THUMB));
								extras.putInt(Constant.HISTORY_ADDIBILITY_K,
										Integer.parseInt(historyList.get(j)
												.get(Constant.C_ADDIBILITY)));
								
								extras.putBoolean(Constant.HISTORY_CATEGORY_INBOX_K, 
								           Integer.parseInt(historyList.get(j).get(Constant.C_TYPE)) == 1? true : false);
								
								extras.putInt(Constant.HISTORY_IS_UPDATABLE_K,
										Integer.parseInt(historyList.get(j)
												.get(Constant.C_IS_UPDATABLE)));
								extras.putLong(
										Constant.HISTORY_UPDATED_AT_K,
										(Long.parseLong(historyList.get(j).get(
												Constant.C_UPDATED_AT))));
								extras.putInt(Constant.HISTORY_SAVE_MARK_K,
										Integer.parseInt(historyList.get(j)
												.get(Constant.C_SAVE_MARK)));
								extras.putInt(Constant.HISTORY_SAVE_DAYS_K,
										Integer.parseInt(historyList.get(j)
												.get(Constant.C_SAVE_DAYS)));
								extras.putString(
										Constant.HISTORY_AD_LINK_K,
										historyList.get(j).get(
												Constant.C_AD_LINK));
								
								
								Intent intent = new Intent(
										ctx,
										PBHistoryInboxDetailActivity.class);
								intent.putExtra("data", extras);
								intent.putExtra(PBConstant.PREF_PASSWORD_FROM_LIBRARY, true);

								((Activity) ctx).startActivity(intent);
								
							}
						}
						
					} else {

						/*final Dialog itemDialog = new Dialog(ctx,R.style.MyCustomDialogTheme);
							itemDialog
									.requestWindowFeature(Window.FEATURE_NO_TITLE);
							itemDialog.setCancelable(true);
							LayoutInflater inflater = LayoutInflater.from(ctx);
							View dialoglayout = inflater.inflate(
									R.layout.kb_dialog_layout, null);
							dialoglayout.findViewById(R.id.button_dl_dialog_no)
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											itemDialog.cancel();

										}

									});

							dialoglayout
									.findViewById(R.id.button_dl_dialog_yes)
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											itemDialog.cancel();
											if (Util.isOnline(ctx)) {
												
												PBPreferenceUtils.saveIntPref(ctx.getApplicationContext(), PBConstant.PREF_NAME, 
										    			Constant.KB_POSITION, position);
												int tabPosition = isMyPage?2:0;
												new KBDownLoadManager((Activity) ctx,list.get(position).getPassword(),tabPosition);
												
											} else {

											Toast.makeText(
													ctx.getApplicationContext(),
													ctx.getString(R.string.pb_chat_message_internet_offline_dialog_message),
													Toast.LENGTH_SHORT).show();
										}
											
										}

									});

							dialoglayout.findViewById(R.id.button_add_favorite)
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {

											itemDialog.cancel();
											PBTaskAddFavourite task = new PBTaskAddFavourite(
													ctx, list.get(position),
													false);
											task.execute();
										}

									});

							dialoglayout.findViewById(
									R.id.button_remove_favorite)
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {

											itemDialog.cancel();
											PBTaskAddFavourite task = new PBTaskAddFavourite(
													ctx, list.get(position),
													true);
											task.execute();
										}

									});

							String value = db.getFavouriteItem(list.get(position).getPassword());
							if (value.equals("1")) {
							if (db.isFavouriteItemExists(list.get(position).getPassword())) {
								dialoglayout.findViewById(
										R.id.button_add_favorite)
										.setVisibility(View.GONE);
								dialoglayout.findViewById(
										R.id.title_add_favorite).setVisibility(
										View.GONE);

								dialoglayout.findViewById(
										R.id.title_remove_favorite)
										.setVisibility(View.VISIBLE);
								dialoglayout.findViewById(
										R.id.button_remove_favorite)
										.setVisibility(View.VISIBLE);
							}

							if (isMyPage
									|| list.get(position).getFavorite() == 1) {
								dialoglayout.findViewById(
										R.id.button_add_favorite)
										.setVisibility(View.GONE);
								dialoglayout.findViewById(
										R.id.title_add_favorite).setVisibility(
										View.GONE);
								
								dialoglayout.findViewById(
										R.id.title_remove_favorite)
										.setVisibility(View.VISIBLE);
								dialoglayout.findViewById(
										R.id.button_remove_favorite)
										.setVisibility(View.VISIBLE);
							}

							itemDialog.setContentView(dialoglayout);
							WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
							lp.copyFrom(itemDialog.getWindow().getAttributes());
							lp.width = WindowManager.LayoutParams.MATCH_PARENT;
							lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
							itemDialog.show();
							itemDialog.getWindow().setAttributes(lp);
							
							itemDialog.setContentView(dialoglayout);
							itemDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	
							final float scale = ctx.getResources().getDisplayMetrics().density;

							//int pixelsWidth = (int) (ctx.getResources().getDimension(R.dimen.kb_dialog_width) * scale + 0.5f);
							//int pixelsHeight = (int) (ctx.getResources().getDimension(R.dimen.kb_dialog_height) * scale + 0.5f);

							
							int pixelsHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
					                (float) ctx.getResources().getDimension(R.dimen.kb_dialog_height), ctx.getResources().getDisplayMetrics()); 
							
							int pixelsWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
					                (float) ctx.getResources().getDimension(R.dimen.kb_dialog_width), ctx.getResources().getDisplayMetrics()); 
							
							
							itemDialog.getWindow().setLayout(pixelsWidth, pixelsHeight);	
						    itemDialog.show();*/
						
						// Removed dialog for tapping items 
						if (Util.isOnline(ctx)) {
							
							PBPreferenceUtils.saveIntPref(ctx.getApplicationContext(), PBConstant.PREF_NAME, 
					    			Constant.KB_POSITION, position);
							int tabPosition = isMyPage?2:0;
							new KBDownLoadManager((FragmentActivity) ctx,list.get(position).getPassword(),tabPosition);
							
						} else {

							Toast.makeText(
									ctx.getApplicationContext(),
									ctx.getString(R.string.pb_chat_message_internet_offline_dialog_message),
									Toast.LENGTH_SHORT).show();
					    }


					}

			}

		});
		

		return convertView;

	}

	private static class ViewHolder {
		public ImageView iconThub;
	    public ImageView thumbCatagory;
	    public ImageView thumbNew;
	    public ImageView thumbRecommend;
		public TextView password;
		public TextView posted_user_name;
		public TextView expiresTime;
		public TextView downloaded_users_count;
		public TextView item_category;
		public TextView photo_thumb_serial;
		public TextView photo_count;
	    public TextView item_honey;
	    public TextView chargesTime;
	}

}
