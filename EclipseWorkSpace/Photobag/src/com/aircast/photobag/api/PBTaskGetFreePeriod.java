package com.aircast.photobag.api;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;
/**
 * class PBTaskGetToken support fetch free time for Photobag app in background
 * @author lent5
 *
 */
public class PBTaskGetFreePeriod extends AsyncTask<Void, Void, Void>{
    /** mHandle use invoke update UI */
    private Handler mHandler;
    private Response mResponse;
    /** use to notify to upper layer has new version */
    public void setHandler(Handler handler){
        this.mHandler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0) {        
        String token = PBPreferenceUtils.getStringPref(
                PBApplication.getBaseApplicationContext(),
                PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
        
        if (!TextUtils.isEmpty(token)) {
            Log.d(PBConstant.TAG, "[check free period and free honey]");
            mResponse = PBAPIHelper.fetchMyFreePeriod(0, token);
            // if invite code is empty
            if (mResponse.errorCode == ResponseHandle.CODE_400) {
                return null;
            } else if (mResponse.errorCode == ResponseHandle.CODE_200_OK) {
                // Log.w(PBConstant.TAG, "[setFreePeriod] " + mResponse.decription);
                // if errorcode = 200 - invite successfully
                Log.d(PBConstant.TAG, "[check free period and free honey response] " + mResponse.decription);
                ResponseHandle.parseFetchFreeTimeAndSaveToPreference(mResponse.decription);
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if(mHandler != null){
            Message msg = new Message();
            msg.what = PBConstant.MSG_UPDATE_UI;
            if(mResponse != null){
                msg.arg1 = mResponse.errorCode;
                
            }else{
                msg.arg1 = ResponseHandle.CODE_200_OK;
            }
            mHandler.sendMessage(msg);
        }
        super.onPostExecute(result);
    }
}
