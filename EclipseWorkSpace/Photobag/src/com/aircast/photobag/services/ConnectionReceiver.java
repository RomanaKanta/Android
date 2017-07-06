package com.aircast.photobag.services;

import java.util.concurrent.RejectedExecutionException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.aircast.photobag.api.PBTaskGetToken;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;
/**
 * class receive event when has change conectivity
 * @author lent5
 *
 */
public class ConnectionReceiver extends BroadcastReceiver{
    public static PBTaskGetToken mTaskGetToken;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(TextUtils.isEmpty(action)) return;
        
        if(TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)){
            if(intent.getExtras()!=null) {
                NetworkInfo ni=(NetworkInfo) intent.getExtras()
                                                    .get(ConnectivityManager.EXTRA_NETWORK_INFO);
                
                if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
                    Log.i(PBConstant.TAG,"Network " + ni.getTypeName() + " connected");
                    startUpdateTokenTask(context);
                }
             }
            
             if(intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
                    Log.d(PBConstant.TAG,"There's no network connectivity");
             }
        }
    }

    /**
     * start task to get token if not existed when have internet connected
     */
    private void startUpdateTokenTask(Context context){
        String token = PBPreferenceUtils.getStringPref(context, 
                PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
        if (TextUtils.isEmpty(token)) {
			token = PBGeneralUtils.getTokenFromCacheFolder();
		} else {
			PBGeneralUtils.saveTokenToCacheFolder();
		}
        if(!TextUtils.isEmpty(token)) return;
        mTaskGetToken = new PBTaskGetToken();
        if(mTaskGetToken.getStatus() == AsyncTask.Status.PENDING || 
                mTaskGetToken.getStatus() == AsyncTask.Status.FINISHED){
            try{
                mTaskGetToken.execute();
            }catch (RejectedExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
