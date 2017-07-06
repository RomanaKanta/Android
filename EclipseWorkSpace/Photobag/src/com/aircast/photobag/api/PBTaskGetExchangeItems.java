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
import com.aircast.photobag.widget.PBCustomWaitingProgress;

/**
 * Task to get exchange item available
 * @deprecated since it is not used by api anymore
 * @author agung
 */
public class PBTaskGetExchangeItems extends AsyncTask<Void, Void, Void>{
    /** mHandle use invoke update UI */
    private Handler mHandler;
    private Response mResponse;
    private PBCustomWaitingProgress mCustomWaitingLayout;
    /** use to notify to upper layer has new version */
    public void setHandler(Handler handler){
        this.mHandler = handler;
    }
    
    public void setWaitingLayout(PBCustomWaitingProgress progressWait){
    	this.mCustomWaitingLayout = progressWait;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(this.mCustomWaitingLayout!=null)
        	this.mCustomWaitingLayout.showWaitingLayout();
    }

    @Override
    protected Void doInBackground(Void... arg0) {        
        String token = PBPreferenceUtils.getStringPref(
                PBApplication.getBaseApplicationContext(),
                PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
        
        if (!TextUtils.isEmpty(token)) {
            Log.d(PBConstant.TAG, "[check free period and free honey]");
            mResponse = new Response();//PBAPIHelper.getExchangeItems(token);
            // if invite code is empty
            if (mResponse.errorCode == ResponseHandle.CODE_400) {
                return null;
            } else if (mResponse.errorCode == ResponseHandle.CODE_200_OK) {
                // Log.w(PBConstant.TAG, "[setFreePeriod] " + mResponse.decription);
                // if errorcode = 200 - invite successfully
                Log.d(PBConstant.TAG, "[check free period and free honey response] " + mResponse.decription);
                ResponseHandle.parseMapleExchangeRateAndSaveToPreference(mResponse.decription);
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if(this.mCustomWaitingLayout!=null)
        	this.mCustomWaitingLayout.hideWaitingLayout();
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
