// GCM service 

package com.aircast.photobag.gcm;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBSplashScreenActivity;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	
    public GCMIntentService() {
        super(GCMConstant.PN_GCM_PROJECT_ID); // Project Number
    }

    @Override
    public void onRegistered(Context context, String registrationId) {
        sendMessage("regist", registrationId);
        Log.d("GCM MESSAGE","GCMIntentService ["+registrationId.length() +"] , ["+ registrationId  + "]");
        //Toast.makeText(context, "Here Proper ID : "+registrationId.length() + " , "+ registrationId, Toast.LENGTH_LONG).show();
        //sendRegistrationIdToServer(context,registrationId);
    }
 
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        sendMessage("unregist", registrationId);
    }
 
    @Override
    public void onError(Context context, String errorId) {
        sendMessage("error", "err:" + errorId);
    }
 
    @Override
    protected void onMessage(Context context, Intent intent) {
    	
    	boolean notification = PBPreferenceUtils.getBoolPref(
    			context, PBConstant.PREF_NAME, 
    			PBConstant.PREF_SETTING_SHOW_NOTIFICATION, true);
    	if(!notification){
    		Log.d("GCM NOTIFICATION >>>>>>> ", "User off this option from Settings");
    		return;
    	}
    	
        String str = intent.getStringExtra("message");
       // Log.w("message:", str);
        sendMessage("message", str);
		generateNotification(context, str);
    }
   
    private void sendMessage(String typeStr, String str) {
		Intent broadcastIntent = new Intent();
		broadcastIntent.putExtra(typeStr, str);
		broadcastIntent.setAction("com.kayac.photobag.gcm.GCMBroadcastReceiver");
		sendBroadcast(broadcastIntent);
    }

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(final Context context, final String message) {
		
		ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;
		
		final String title = context.getString(R.string.pb_app_name);
		
		@SuppressWarnings("rawtypes")
		Class topClass = null;
		if(componentInfo.getPackageName().equalsIgnoreCase(PBConstant.PREF_NAME)){
		    //Activity in foreground, broadcast intent
			topClass = componentInfo.getClass();
		} 
		else{
		    //Activity Not Running,so Generate Notification
			topClass = PBSplashScreenActivity.class;
		}
		
		Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable(){
        	public void run() {
        		/*
        		Intent i = new Intent(context,PushDialogActivity.class);
        		i.putExtra("TITLE",title);
        		i.putExtra("MESSAGE",message);
        	    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        	    context.startActivity(i);
        	    */
        		//Toast.makeText(context, "『"+title+"』 "+message, Toast.LENGTH_LONG).show();
        		Toast.makeText(context, title+""+message, Toast.LENGTH_LONG).show();
            }
        });
        
		//int icon 	 = R.drawable.icon_small;
		//int icon 	 = R.drawable.icon;
        int icon_small 	 = R.drawable.icon_small_for_push;
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_small_test);

        Intent notificationIntent = new Intent(context, topClass);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		Builder builder = new NotificationCompat.Builder(context);
		builder.setTicker(message);
		builder.setContentTitle(title);
		builder.setContentText(message);
		builder.setContentInfo("info");
		builder.setWhen(System.currentTimeMillis());
		builder.setContentIntent(pendingIntent);
		builder.setSmallIcon(icon_small);
		builder.setLargeIcon(largeIcon);
		Notification notification = builder.build();

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.vibrate = new long[]{0,200,100,50,100,50,100,200}; 

		NotificationManager notificationManager = (NotificationManager)
			context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, notification);
	}
}
