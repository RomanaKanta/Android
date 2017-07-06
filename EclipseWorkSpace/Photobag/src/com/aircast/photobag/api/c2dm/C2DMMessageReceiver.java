package com.aircast.photobag.api.c2dm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBMainTabBarActivity;
import com.aircast.photobag.activity.PBSettingMainActivity;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;

/**
 * Message receiver C2DM extends {@link BroadcastReceiver}
 * @author lent5
 */
public class C2DMMessageReceiver extends BroadcastReceiver{
    /*{
        data.m: 'Invite success! You achieved a bonus free period!',
        data.t: 'invited'
      } */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (C2DMConstant.PN_C2DM_RECEIVER.equals(action)) {
            final String dataMsg = intent.getStringExtra("m");
            final String dataType = intent.getStringExtra("t");

            updateFreeTimeFromServer(context);
            
            if (PBPreferenceUtils.getBoolPref(context, 
            		PBConstant.PREF_NAME,
            		PBConstant.PREF_SETTING_SHOW_NOTIFICATION, 
            		true)) {
            	createNotification(context, dataType, dataMsg);
            }
            Log.d("AGUNG_C2DM", "Got Notification :"+dataType + ", message:"+dataMsg);
        }
    }

    private void updateFreeTimeFromServer(Context context){
        // get data from server
        // 20120220 @lent check internet connection
        if(PBApplication.hasNetworkConnection()){
            String token = PBPreferenceUtils.getStringPref(
                    PBApplication.getBaseApplicationContext(),
                    PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
            
            if (!TextUtils.isEmpty(token)) {
                Response rs = PBAPIHelper.fetchMyFreePeriod(0, token);
                // fix check null
                if(rs == null) return;

                if (rs.errorCode == ResponseHandle.CODE_400) {
                    return;
                } else if (rs.errorCode == ResponseHandle.CODE_200_OK) {
                    // Log.w(PBConstant.TAG, "[setFreePeriod] " + rs.decription);
                    ResponseHandle.parseFetchFreeTimeAndSaveToPreference(rs.decription);
                    
                    // send to GUI
                    // TODO change this to mypageActivity?
                    if(PBSettingMainActivity.sSettingContext != null){}
                }
            }
        }else{
            Toast.makeText(context, 
                    context.getString(R.string.pb_msg_no_conenction), 0)
                    .show();
        }
    }
    
    private void createNotification(Context context, String dataType, String dataMess) {
        if(TextUtils.isEmpty(dataMess)){
            dataMess = context.getString(R.string.pn_notification_title_bar);
        }
        NotificationManager notificationManager = (NotificationManager) context
        .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.icon,
                dataMess , System.currentTimeMillis());
        
        // Hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        
        Intent intent = new Intent(context, PBMainTabBarActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if ("added".equals(dataType))
        	intent.putExtra(PBConstant.TAB_SET_BY_TAG, PBConstant.HISTORY_TAG);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        notification.setLatestEventInfo(context, 
                context.getString(R.string.pb_app_name), 
                dataMess,
                pendingIntent);
        notificationManager.notify(0, notification);
    }
}
