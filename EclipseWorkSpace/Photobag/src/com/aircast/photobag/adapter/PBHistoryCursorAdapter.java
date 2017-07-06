package com.aircast.photobag.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircast.photobag.R;
import com.aircast.photobag.database.ChatDatabaseHandler;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.fragment.PBDownloadFragment;
import com.aircast.photobag.fragment.PBHistoryMainFragment;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBImageLoaderUtils;

public class PBHistoryCursorAdapter extends CursorAdapter{
    private Context mActivity;
    private LayoutInflater mInflater;
    private PBImageLoaderUtils mImageLoader;
    private Cursor mCursor;

    private int ID = 0;
    private int COLLECTION_ID = 1;
    private int PASSWORD = 2;
    private int CREATED_DATE = 3;
    private int CHARGEAT_DATE = 4;
    private int NUM_OF_DOWNLOAD = 5;
    private int NUM_OF_PHOTO = 6;
    private int THUMB = 7;
    private int ADDIBILITY = 8;
    private int EXTRA = 9;
    private int IS_UPDATABLE = 10;
    private int UPDATED_AT = 11;
    private int HONEY_NUM = 12;
    private int MAPLE_NUM = 13;
    private int SAVE_MARK = 14;
    private int SAVE_DAYS = 15;
    private int AD_LINK = 16;
    private int IS_PUBLIC = 17;
    private int ACCEPTED = 18;
    private int MEASSGE_COUNT = 19;
    private int EXPIRES_AT = 20;
    private int ST = 21;
    private boolean isDeleteModeEnable = false;
    SparseBooleanArray mCheckStates;
    private ChatDatabaseHandler db;

    public PBHistoryCursorAdapter(Context activity, Cursor c) {
        super(activity, c, true);
        
        if (c == null || c.isClosed()) {
            return;
        }
        
        mCursor = c;
        mActivity = activity;
        mImageLoader = new PBImageLoaderUtils(activity, true);
        mInflater = LayoutInflater.from(mActivity);
        mCheckStates = new SparseBooleanArray(c.getCount());
        db = new ChatDatabaseHandler(
				mActivity);
        updateDateByCursor(mCursor);
    }
    
    private void updateDateByCursor(Cursor c) {
    	if(c != null && !c.isClosed()){
            ID = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ID);
            COLLECTION_ID = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_COLECTION_ID);
            PASSWORD = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PASSWORD);
            CREATED_DATE = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_CREATED_AT);
            CHARGEAT_DATE = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_CHARGES_AT);
            NUM_OF_DOWNLOAD = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_DOWNLOAD_COUNT);
            NUM_OF_PHOTO = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_PHOTO_COUNT);
            THUMB = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_THUMB);
            ADDIBILITY = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ADDIBILITY);
            EXTRA = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_EXTRA);
            IS_UPDATABLE = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_IS_UPDATABLE);
            UPDATED_AT = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_UPDATED_AT);
            HONEY_NUM = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_HONEY_NUM);
            MAPLE_NUM = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_MAPLE_NUM);
            SAVE_MARK = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_SAVE_MARK);
            SAVE_DAYS = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_SAVE_DAYS);
            AD_LINK = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_AD_LINK);
            IS_PUBLIC = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_IS_PUBLIC);
            ACCEPTED = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ACCEPTED);
            MEASSGE_COUNT = c.getColumnIndex(PBDatabaseDefinition.HistoryData.MEASSGE_COUNT);
            
            EXPIRES_AT  = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_Expires_AT);
            
            ST = c.getColumnIndex(PBDatabaseDefinition.HistoryData.C_ST_Digit);
        }
    }
    
    public void clearAdeper() {
    	mActivity = null;
    	
    	try {
			mCursor.close();
			mCursor = null;
		} catch  (Exception e) {}
    }
    
    @Override
    public void bindView(View convertView, Context context, final Cursor cursor) {
        if (cursor == null || cursor.isClosed()) {
            return;
        }
        
        final ViewHolder holder = new ViewHolder();
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
        holder.itemImageUpdate = (TextView) convertView
                .findViewById(R.id.textView__history_list_number_update);
        holder.itemUpdateText = (TextView) convertView
                .findViewById(R.id.textView_update_txt);
        holder.itemUpdateIcon = (TextView) convertView
                .findViewById(R.id.textView_update_icon);
        holder.itemNumofHoney = (TextView) convertView
        		.findViewById(R.id.tv_history_item_num_honey);
        // String fbID = cursor.getString(COLLECTION_ID);
        holder.itemTotalPhoto = (TextView) convertView
        		.findViewById(R.id.tv_histroy_total_num_photos);
        holder.itemTotalVedio = (TextView) convertView
        		.findViewById(R.id.tv_histroy_total_num_vedios);
        holder.totalPhotos = (TextView) convertView
        		.findViewById(R.id.tv_history_item_num_photos);
        holder.totalVedios = (TextView) convertView
        		.findViewById(R.id.tv_history_item_num_vedios);
        holder.deleteButton = (CheckBox) convertView
        		.findViewById(R.id.history_list_item_delete);

        holder.totalUnreadMessage = (TextView) convertView
        		.findViewById(R.id.textView__history_list_number_of_unread_mgs);
        holder.totalMessage = (TextView) convertView
        		.findViewById(R.id.textView_total_mgs);
        
        
        
        if(mActivity != null && cursor != null && !cursor.isClosed()) {
        	

        	
        	// Enable view for delete
        	if(isDeleteModeEnable) {
        		
        		
        		holder.deleteButton.setVisibility(View.VISIBLE);
        	} else {
        		holder.deleteButton.setVisibility(View.GONE);
        	}
        	
        	holder.deleteButton.setTag(cursor.getPosition());
			holder.deleteButton.setChecked(mCheckStates.get(
					cursor.getPosition(), false));
			
    		holder.deleteButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					buttonView.playSoundEffect(android.view.SoundEffectConstants.CLICK); // Sound effects
					mCheckStates.put((Integer) buttonView.getTag(), isChecked);
				}
    			
    			
    			
    		});
    		
    		
            holder.itemDisplayedPassword.setText(cursor.getString(PASSWORD));
            String dateCaption = mActivity.getString(R.string.pb_history_info_date);
            if (!PBHistoryMainFragment.isInInbox) {
                dateCaption = mActivity.getString(R.string.pb_history_info_date_outbox);
            }
            holder.itemReceivedDateCaption.setText(dateCaption);
            holder.itemReceivedDate.setText(
                    PBGeneralUtils.convertMilisecondToDate(cursor.getLong(ID)));

            holder.itemNumofDownload.setText(Long.toString(cursor.getLong(NUM_OF_DOWNLOAD)));
            
            holder.itemNumofHoney.setText(""+(cursor.getInt(HONEY_NUM) + cursor.getInt(MAPLE_NUM)));
            
            
            
            if (!PBHistoryMainFragment.isInInbox) { 
                if(cursor.getInt(IS_UPDATABLE) != 1) {
                	holder.itemImageUpdate.setVisibility(View.GONE);
            		//holder.itemImageUpdate.setVisibility(View.INVISIBLE);
                	if(PBDownloadFragment.isKoukaibukuroDisabled) { // koukaibukuro status is not visible
                		holder.itemUpdateIcon.setVisibility(View.INVISIBLE);
                		holder.itemUpdateText.setVisibility(View.INVISIBLE);
                	} else {
                    	if(cursor.getInt(IS_PUBLIC) == 1) {
                    		holder.itemUpdateIcon.setVisibility(View.VISIBLE);
                    		holder.itemUpdateText.setVisibility(View.VISIBLE);
                    	   if(cursor.getInt(ACCEPTED) == 0) {
                    		   holder.itemUpdateIcon.setBackgroundResource(R.drawable.history_review_shape);
                       		holder.itemUpdateText.setText(context.getString(R.string.review));
                    		    // show in Review	   
                    	   } else if (cursor.getInt(ACCEPTED) == 1) {
                    		   holder.itemUpdateIcon.setBackgroundResource(R.drawable.history_publish_shape);
                          		holder.itemUpdateText.setText(context.getString(R.string.publish));
                    		 // show in published	  
                    	   } else if (cursor.getInt(ACCEPTED) == 2) { 
                          		holder.itemUpdateText.setText(context.getString(R.string.reject));
                          		 holder.itemUpdateIcon.setBackgroundResource(R.drawable.history_reject_shape);
                    		   // show in Reject	  
                    	   }
                    	} else if(cursor.getInt(IS_PUBLIC) == 0) {
                    		holder.itemUpdateIcon.setVisibility(View.INVISIBLE);
                    		holder.itemUpdateText.setVisibility(View.INVISIBLE);
                    	}
                		
                	}
                	/*} else if(cursor.getInt(IS_PUBLIC) == 0) {
                		holder.itemImageUpdate.setVisibility(Vie
                		w.INVISIBLE);
                	} else {
                		holder.itemImageUpdate.setVisibility(View.INVISIBLE);
                	}*/
                } else if(cursor.getInt(IS_UPDATABLE) == 1) {
                	//holder.itemImageUpdate.setVisibility(View.VISIBLE);
                	
              		
              		holder.itemImageUpdate.setVisibility(View.VISIBLE);
                	holder.itemUpdateIcon.setVisibility(View.INVISIBLE);
            		holder.itemUpdateText.setVisibility(View.INVISIBLE);
                } 
            } else {  // for Inbox koukai mark not needed
            	
                if(cursor.getInt(IS_UPDATABLE) != 1) {
                	holder.itemImageUpdate.setVisibility(View.GONE);
                	holder.itemUpdateIcon.setVisibility(View.INVISIBLE);
            		holder.itemUpdateText.setVisibility(View.INVISIBLE);
                } else if(cursor.getInt(IS_UPDATABLE) == 1) {
                	holder.itemImageUpdate.setVisibility(View.VISIBLE);
                	holder.itemUpdateIcon.setVisibility(View.INVISIBLE);
            		holder.itemUpdateText.setVisibility(View.INVISIBLE);
                } 
            	
            }
            
            
            /*else {
            	holder.itemImageUpdate.setVisibility(View.INVISIBLE);
            }*/
            
            // Always visible icon..But this need to refactored
            //holder.itemImageUpdate.setVisibility(cursor.getInt(IS_UPDATABLE) == 1 ? View.VISIBLE : View.INVISIBLE);
            
            if (NUM_OF_PHOTO !=0) {
				holder.totalPhotos.setText("" 
						+ cursor.getInt(NUM_OF_PHOTO));
			}
            else{
            	holder.itemTotalPhoto.setText("No Contents");
            	holder.totalPhotos.setText("");
            }

            String thumbUrl = cursor.getString(THUMB);            
            if (!TextUtils.isEmpty(thumbUrl)) {
                try{
                	
                    mImageLoader.displayImageForHistoryThumb(
                            PBGeneralUtils.getPathFromCacheFolder(thumbUrl), holder.itemImageThump);
                }catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
        } else {
        	if(isDeleteModeEnable) {
        		holder.deleteButton.setVisibility(View.VISIBLE);
        	} else {
        		holder.deleteButton.setVisibility(View.GONE);
        	}
        }
        String collectionID = cursor.getString(COLLECTION_ID);    
        int totalMGS = cursor.getInt(MEASSGE_COUNT);
   		int last_message_count = db.getChatRowCount(String.valueOf(collectionID));
   		int unReadMGS = totalMGS - last_message_count;
   
   		holder.totalMessage.setText(String.format(mActivity.getString(R.string.total_chat_item), totalMGS));
   		
           if(unReadMGS > 0){
           	 
                 holder.totalUnreadMessage.setVisibility(View.VISIBLE);
               //  String password = cursor.getString(PASSWORD); 
                 long expiresat = cursor.getLong(EXPIRES_AT);
                 long updateat = cursor.getLong(UPDATED_AT);
                 if(expiresat <= updateat){
              	   
              	   holder.totalUnreadMessage.setVisibility(View.GONE);
                 }else{
              	   
              	   holder.totalUnreadMessage.setVisibility(View.VISIBLE);
                 }
                 
           }else{
           	
                 holder.totalUnreadMessage.setVisibility(View.GONE);
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
    public void recycleAll() {
        if (mImageLoader != null) {
            mImageLoader.recycleAll();
        }
    }
    
    /*
     *  Set Delete mode on list item
     */
    public void setDeleteMode(boolean setValue) {
    	isDeleteModeEnable = setValue;
    }
    
    /*
     *  Get delete mode status
     */
    
    public boolean getDeleteMode() {
    	return isDeleteModeEnable;
    }
    
    public boolean isChecked(int position) {
		return mCheckStates.get(position, false);
	}

	public void setChecked(int position, boolean isChecked) {
		mCheckStates.put(position, isChecked);

	}

	public void toggle(int position) {
		setChecked(position, !isChecked(position));

	}

	public SparseBooleanArray getselectedItem() {

		return mCheckStates;
	}
	
	public void setselectedList() {

		 mCheckStates.clear();
	}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.pb_adapter_list_history_item, null);
    }

    private static class ViewHolder {
        public TextView itemDisplayedPassword;
        public TextView itemReceivedDateCaption;
        public TextView itemReceivedDate;
        public TextView itemNumofDownload;
        public TextView itemNumofHoney;
        public ImageView itemImageThump;
        public TextView itemImageUpdate;
        public TextView itemUpdateText;
        public TextView itemUpdateIcon;
        public TextView itemTotalPhoto;
        public TextView itemTotalVedio;
        public TextView totalPhotos;
        public TextView totalVedios;
        public CheckBox deleteButton;
        public TextView totalUnreadMessage;
        public TextView totalMessage;
    }    
}
