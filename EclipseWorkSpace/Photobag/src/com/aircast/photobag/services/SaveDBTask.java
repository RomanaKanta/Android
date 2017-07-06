package com.aircast.photobag.services;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.model.PBHistoryEntryModel;
import com.aircast.photobag.model.PBHistoryPhotoModel;
import com.aircast.photobag.services.UploadService.MediaInfo;
import com.aircast.photobag.services.UploadService.ServiceUpdateUIListener;
import com.aircast.photobag.utils.PBPreferenceUtils;

/**
 * This class do save uploaded photo to cache folder
 * 
 * @author FPT-HaiVT
 * 
 */
public class SaveDBTask extends AsyncTask<Void, Void, Void> {
    private PBHistoryPhotoModel mPhotoModel;
    private PBHistoryEntryModel mHistoryEntryModel;
    private static Context mContext;
    private long mChargeDate;
    private ArrayList<MediaInfo> mUploadUrlList;
    private ArrayList<String> mUploadPhotoList;
    private ServiceUpdateUIListener mListener;
    /**
     * Constructor
     * 
     * @param context
     * @param localPhotoList
     * @param urlPhotoList
     */
    public SaveDBTask(Context context, ArrayList<String> localPhotoList,
            ArrayList<MediaInfo> urlPhotoList, ServiceUpdateUIListener updateListener) {
        mContext = context;
        mUploadUrlList = urlPhotoList;
        // @lent5 fix avoid null 
        if(mUploadUrlList == null){
            mUploadUrlList = new ArrayList<MediaInfo>();
        }

        mUploadPhotoList = localPhotoList;
        // @lent5 fix avoid null 
        if(mUploadPhotoList == null){
            mUploadPhotoList = new ArrayList<String>();
        }

        mListener = updateListener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String collectionId = PBPreferenceUtils.getStringPref(mContext,
                PBConstant.UPLOAD_SERVICE_PREF, PBConstant.PREF_COLLECTION_ID, "");

        PBDatabaseManager dbManager = PBDatabaseManager
                .getInstance(PBApplication.getBaseApplicationContext());
        int photoNum = mUploadUrlList.size();
        // 20120228 @lent5 fix avoid FC
        if(mUploadUrlList.size() > 0){
            mHistoryEntryModel = new PBHistoryEntryModel(
                    System.currentTimeMillis(),
                    collectionId,
                    PBPreferenceUtils.getStringPref(mContext,
                            PBConstant.UPLOAD_SERVICE_PREF,
                            PBConstant.PREF_UPLOAD_PASS, ""),
                    System.currentTimeMillis(), 
                    mChargeDate, photoNum, 
                    0,
                    mUploadUrlList.get(0).url + "?width=150&height=150",
                    PBPreferenceUtils.getIntPref(mContext,
                            PBConstant.UPLOAD_SERVICE_PREF,
                            PBConstant.PREF_ADDIBILITY, 0),
                    "",
                    PBPreferenceUtils.getIntPref(mContext,
                            PBConstant.UPLOAD_SERVICE_PREF,
                            PBConstant.PREF_IS_UPDATABLE, 0),
                    PBPreferenceUtils.getLongPref(mContext,
                            PBConstant.UPLOAD_SERVICE_PREF,
                            PBConstant.PREF_UPDATED_AT, 0));
            
            
//            Toast.makeText(mContext.getApplicationContext(), ""+PBPreferenceUtils.getIntPref(mContext,
//                            PBConstant.UPLOAD_SERVICE_PREF,
//                            PBConstant.PREF_FOUR_DIGIT, 0), 500);
            
            Log.d("st up ", ""+PBPreferenceUtils.getStringPref(mContext.getApplicationContext(),
                            PBConstant.UPLOAD_SERVICE_PREF,
                            PBConstant.PREF_FOUR_DIGIT, "12"));
            
            mHistoryEntryModel.setFourDigit(PBPreferenceUtils.getStringPref(mContext.getApplicationContext(),
                            PBConstant.UPLOAD_SERVICE_PREF,
                            PBConstant.PREF_FOUR_DIGIT,  "12"));
        
            if (dbManager != null) {
            	
            	
                dbManager.insertHistory(mHistoryEntryModel, PBDatabaseDefinition.HISTORY_SENT);
            }
            
            PBPreferenceUtils.saveStringPref(mContext,
                    PBConstant.UPLOAD_SERVICE_PREF,
                    PBConstant.PREF_FOUR_DIGIT, "12");
        }

        for (int i = 0; i < mUploadUrlList.size(); i++) {
            if (mUploadUrlList.size() > i) {
                MediaInfo uploadItem = mUploadUrlList.get(i);
                String photoUrl = uploadItem.url;
                if (photoUrl.contains("video")) {
                    photoUrl = photoUrl + PBConstant.VIDEO_FORMAT_3GP;
                }
                mPhotoModel = new PBHistoryPhotoModel(/*uploadItem.url*/photoUrl,
                        uploadItem.url + "?width=150&height=150",
                        collectionId, uploadItem.type, uploadItem.duration);
                
                if (dbManager != null) {
                    dbManager.setPhoto(mPhotoModel,
                            PBDatabaseDefinition.HISTORY_SENT);
                }
                // change solution, store cache when uploaded successfully
                // saveFileToCacheFolder(i);
            }
        }
        
        // 20120229 @lent5 fixed not uploading completely #S
        // notify after update completed update DB
        if(mListener != null){
            mListener.updateUI(PBConstant.UPLOAD_FINISH_COMPLETED, mUploadUrlList);
        }
        // 20120229 @lent5 fixed not uploading completely #E
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // reset all upload pref
        PBPreferenceUtils.saveBoolPref(mContext,
                PBConstant.UPLOAD_SERVICE_PREF, PBConstant.PREF_UPLOAD_FINISH,
                false);
        PBPreferenceUtils
                .saveStringPref(mContext, PBConstant.UPLOAD_SERVICE_PREF,
                        PBConstant.PREF_UPLOAD_PASS, "");
        PBPreferenceUtils.saveStringPref(mContext,
                PBConstant.UPLOAD_SERVICE_PREF, PBConstant.PREF_COLLECTION_ID,
                "");
        PBPreferenceUtils.saveStringPref(mContext,
                PBConstant.UPLOAD_SERVICE_PREF,
                PBConstant.PREF_COLLECTION_THUMB, "");
        PBPreferenceUtils.saveIntPref(mContext,
                PBConstant.UPLOAD_SERVICE_PREF,
                PBConstant.PREF_ADDIBILITY, 0);
        PBPreferenceUtils.saveIntPref(mContext,
                PBConstant.UPLOAD_SERVICE_PREF,
                PBConstant.PREF_IS_UPDATABLE, 0);
        PBPreferenceUtils.saveLongPref(mContext,
                PBConstant.UPLOAD_SERVICE_PREF,
                PBConstant.PREF_UPDATED_AT, 0);
    }

}
