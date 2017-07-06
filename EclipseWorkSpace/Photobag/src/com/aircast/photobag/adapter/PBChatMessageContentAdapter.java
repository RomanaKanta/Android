package com.aircast.photobag.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aircast.koukaibukuro.util.KBDownLoadManager;
import com.aircast.photobag.R;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.utils.PBPreferenceUtils;


public class PBChatMessageContentAdapter extends BaseAdapter {
	
	private Context mContext;
	private ArrayList<HashMap<String, String>> listOfchatMessages = null;
	String deviceUUID = PBPreferenceUtils.getStringPref(
			PBApplication.getBaseApplicationContext(),
			PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID, "0");
	private AlertDialog alertDialog;
	private PBDatabaseManager mDatabaseManager;

	public PBChatMessageContentAdapter(Context context , ArrayList<HashMap<String, String>> listOfMessages) {
		super();
		listOfchatMessages = listOfMessages;
		this.mContext = context;
		
		mDatabaseManager = PBDatabaseManager
				.getInstance(mContext);

	}

	@Override
	public int getCount() {

		return listOfchatMessages.size();
	}

	@Override
	public Object getItem(int position) {

		 return listOfchatMessages.get(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.chat_history_receiver, parent, false);

			holder.icon_owner = (ImageView) convertView
					.findViewById(R.id.imageView_owner);

			holder.icon_user = (ImageView) convertView
					.findViewById(R.id.imageView_user);
			
			holder.icon_admin = (ImageView) convertView
					.findViewById(R.id.imageView_admin);
			
			
			
			holder.nickName = (TextView) convertView
			.findViewById(R.id.textView_nickname);
			
			holder.dateRight = (TextView) convertView
					.findViewById(R.id.textView_message_date_right);

			holder.dateRight = (TextView) convertView
					.findViewById(R.id.textView_message_date_right);
			holder.message = (TextView) convertView
					.findViewById(R.id.textView_sender_message);
			holder.dateLeft = (TextView) convertView
					.findViewById(R.id.textView_message_date_left);

			holder.mgs_content = (LinearLayout) convertView
					.findViewById(R.id.LinearLayout_message_content);
			holder.mgs_content_root = (LinearLayout) convertView
					.findViewById(R.id.LinearLayout_message_content_root);
			
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (listOfchatMessages.get(position).get("uid").equals(deviceUUID)) {
			holder.nickName.setText("\u2605"+listOfchatMessages.get(position).get("nickname"));
			holder.mgs_content_root.setGravity(Gravity.RIGHT);
			holder.mgs_content.setGravity(Gravity.RIGHT);
			holder.message.setText(listOfchatMessages.get(position).get("message"));
			holder.message.setGravity(Gravity.LEFT);
			holder.nickName.setGravity(Gravity.RIGHT);
			holder.message
					.setBackgroundResource(R.drawable.me_chat_bubble_bg);
			holder.dateLeft.setText(listOfchatMessages.get(position).get("created"));
			holder.dateLeft.setVisibility(View.VISIBLE);
			holder.dateRight.setVisibility(View.GONE);
			holder.icon_owner.setVisibility(View.VISIBLE);
			holder.icon_user.setVisibility(View.INVISIBLE);
			holder.icon_admin.setVisibility(View.INVISIBLE);
			
			holder.icon_admin.setVisibility(View.INVISIBLE);
			
		
			if (listOfchatMessages.get(position).get("color").equals("owner")) {
				Log.d("mgs", "self+owner->"+listOfchatMessages.get(position).get("message"));
				holder.icon_owner.setBackgroundResource(R.drawable.chat_owner_shape);
				holder.icon_owner.setImageResource(R.drawable.owner_icon);
			}else{
				Log.d("mgs", "self->"+listOfchatMessages.get(position).get("message"));
				holder.icon_owner.setBackgroundResource(R.drawable.chat_user_shape);
				holder.icon_owner.setImageResource(R.drawable.user_icon);
				((GradientDrawable)holder.icon_owner.getBackground()).setColor(Color.parseColor(listOfchatMessages.get(position).get("color")));
				
			}
			
			

		} else if (listOfchatMessages.get(position).get("uid").equals(PBConstant.ADMIN_UID)) {
			holder.nickName.setText("写真袋_運営委員会");
			holder.mgs_content_root.setGravity(Gravity.LEFT);
			holder.mgs_content.setGravity(Gravity.LEFT);
			holder.message.setText(listOfchatMessages.get(position).get("message"));
			holder.message.setGravity(Gravity.LEFT);
			holder.nickName.setGravity(Gravity.LEFT);
			holder.message.setBackgroundResource(R.drawable.other_chat_buble_bg);
			holder.dateRight.setText(listOfchatMessages.get(position).get("created"));
			holder.dateLeft.setVisibility(View.GONE);
			holder.dateRight.setVisibility(View.VISIBLE);
			holder.icon_owner.setVisibility(View.INVISIBLE);
			holder.icon_user.setVisibility(View.INVISIBLE);
			holder.icon_admin.setVisibility(View.VISIBLE);
			
			Log.d("mgs", "admin ->"+listOfchatMessages.get(position).get("message"));
			
		}else {
			holder.nickName.setText(listOfchatMessages.get(position).get("nickname"));
			holder.mgs_content_root.setGravity(Gravity.LEFT);
			holder.mgs_content.setGravity(Gravity.LEFT);
			holder.message.setText(listOfchatMessages.get(position).get("message"));
			holder.message.setGravity(Gravity.LEFT);
			holder.nickName.setGravity(Gravity.LEFT);
			holder.message.setBackgroundResource(R.drawable.other_chat_buble_bg);
			holder.dateRight.setText(listOfchatMessages.get(position).get("created"));
			holder.dateLeft.setVisibility(View.GONE);
			holder.dateRight.setVisibility(View.VISIBLE);
			holder.icon_owner.setVisibility(View.INVISIBLE);
			holder.icon_user.setVisibility(View.VISIBLE);
			holder.icon_admin.setVisibility(View.INVISIBLE);
//			holder.icon_user.setBackgroundResource(R.drawable.chat_user_shape);
//			holder.icon_user.setImageResource(R.drawable.user_icon);
			
			if (listOfchatMessages.get(position).get("color").equals("owner")) {
				Log.d("mgs", "user+owner->"+listOfchatMessages.get(position).get("message"));
				holder.icon_user.setBackgroundResource(R.drawable.chat_owner_shape);
				holder.icon_user.setImageResource(R.drawable.owner_icon);
			}else{
				Log.d("mgs", "user->"+listOfchatMessages.get(position).get("message"));
				holder.icon_user.setBackgroundResource(R.drawable.chat_user_shape);
				holder.icon_user.setImageResource(R.drawable.user_icon);
				((GradientDrawable)holder.icon_user.getBackground()).setColor(Color.parseColor(listOfchatMessages.get(position).get("color")));
				
			}
		}
		
		holder.message.setOnLongClickListener(new OnLongClickListener() {
			   @SuppressLint("InflateParams") @Override
			   public boolean onLongClick(final View arg0) {
				   
				   
				    String names[] ={mContext.getString(R.string.pb_chat_tap_dialog_copy),mContext.getString(R.string.pb_chat_tap_dialog_download)};
			        
					alertDialog = new AlertDialog.Builder(
							new ContextThemeWrapper(mContext,
									R.style.popup_theme)).create();
					
					alertDialog.setCancelable(true);
			        
			        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			        View convertView = (View) inflater.inflate(R.layout.custom_dialog_layout_in_chat, null);
			        alertDialog.setView(convertView);
			        ListView lv = (ListView) convertView.findViewById(R.id.listViewForChat);
			        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,names);
			        lv.setAdapter(adapter);
			        lv.setOnItemClickListener(new OnItemClickListener() {

						@SuppressWarnings("deprecation")
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position1, long id) {
							// TODO Auto-generated method stub
							if(position1 == 0) {
				               	int sdk = android.os.Build.VERSION.SDK_INT;
			                	if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
									android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
			                	    clipboard.setText(listOfchatMessages.get(position).get("message"));
			                	} else {
			                	    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE); 
			                	    android.content.ClipData clip = android.content.ClipData.newPlainText("text label",listOfchatMessages.get(position).get("message"));
			                	    clipboard.setPrimaryClip(clip);
			                	}
			                	Toast.makeText(mContext, mContext.getString(R.string.pb_chat_tap_dialog_copy),1000 ).show();
			                	alertDialog.dismiss();

							} else {
								FragmentActivity activity = (FragmentActivity) mContext;
								String collectionId = mDatabaseManager.getCollectionId(listOfchatMessages.get(position).get("message"));
								 if (mDatabaseManager.isPasswordExistsInInboxItems(collectionId) ){
									 
									 AlertDialog alertDownloadNoUpdate = new AlertDialog.Builder(new ContextThemeWrapper(mContext,
												R.style.popup_theme))
							            .setIcon(android.R.drawable.ic_dialog_alert)
							            .setTitle(R.string.pb_download_need_update_title)
							            .setMessage(R.string.pb_download_aikotoba_already_uptodate)
							            .setPositiveButton(R.string.dialog_ok_btn, 
							                    new DialogInterface.OnClickListener() {
							                public void onClick(DialogInterface dialog,
							                        int which) {
							                    dialog.dismiss();
							                }
							            }).create();
									 alertDownloadNoUpdate.show();
									

				                 } else {
				                    
				                	   new KBDownLoadManager(activity,listOfchatMessages.get(position).get("message"),0);
				                }  
								
								
			                	alertDialog.dismiss();

			                	
							}
							
						}
					});
			        alertDialog.show();
				
			    return true;
			   }
			   
			   
			   
	 });
		
		return convertView;
	}

	private class ViewHolder {
		TextView nickName;
		TextView message;
		TextView dateLeft;
		TextView dateRight;
		ImageView icon_owner;
		ImageView icon_user;
		ImageView icon_admin;
		LinearLayout mgs_content;
		LinearLayout mgs_content_root;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
		
		 
}
