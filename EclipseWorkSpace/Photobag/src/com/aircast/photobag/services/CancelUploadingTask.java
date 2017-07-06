package com.aircast.photobag.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;

/**
 * This class do save uploaded photo to cache folder
 * 
 * @author FPT-HaiVT
 * 
 */
public class CancelUploadingTask extends AsyncTask<Void, Void, Void> {
    Context mContext;

    public CancelUploadingTask(Context context) {

    }

    @Override
    protected Void doInBackground(Void... params) {
        mContext = PBApplication.getBaseApplicationContext();
        Response response = PBAPIHelper.cancelUploading(PBPreferenceUtils
                .getStringPref(mContext, PBConstant.UPLOAD_SERVICE_PREF,
                        PBConstant.PREF_COLLECTION_ID, ""), PBPreferenceUtils
                .getStringPref(mContext, PBConstant.PREF_NAME,
                        PBConstant.PREF_NAME_TOKEN, ""));
        Log.e(PBConstant.TAG, "Cancel upload" + response.decription + " Code: "
                + response.errorCode);

        // Reset all upload service preference
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
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

}
