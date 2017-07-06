package com.aircast.photobag.api.c2dm;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;

import com.aircast.photobag.api.HttpUtils;
import com.aircast.photobag.api.MapEntry;
import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;

/**
 * 
 * Registration C2DM extends {@link BroadcastReceiver}
 * @author lent5
 *
 */
public class C2DMRegistrationReceiver extends BroadcastReceiver{
    
    //private boolean mFirstTimeRegistC2DM = false;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(C2DMConstant.PN_C2DM_REGISTRATION.equals(action)){
             Log.w("C2DM", ">>>>>> Received registration ID from C2DM");
            final String registrationId = intent.getStringExtra("registration_id");
            // String error = intent.getStringExtra("error");

            if(TextUtils.isEmpty(registrationId)) return;

            // TODO Send this to my application server
            sendRegistrationIdToServer(context,registrationId);
            saveRegistrationId(context, registrationId);
        }
    }

    /**
     * save c2dm registration id to share preference
     * @param context
     * @param registrationId
     */
    private void saveRegistrationId(Context context, String registrationId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = prefs.edit();
        edit.putString(C2DMConstant.PN_C2DM_AUTH, registrationId);
        edit.commit();
    }

    /**
     * save c2dm device id to server
     * @param context
     * @param deviceId
     */
    private void saveDeviceIdToServer(Context context, String deviceId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = prefs.edit();
        edit.putString(C2DMConstant.PN_C2DM_DEVICE_AUTH, deviceId);
        edit.commit();
    }

    // do this in an service and in an own thread
    /**
     * save c2dm registration id to server
     * @param context
     * @param deviceId
     */
    public void sendRegistrationIdToServer(final Context context, final String registrationId){
        // Log.d("C2DM", "Sending registration ID to server");
        Thread regIdThread = new Thread(new Runnable() {
            @Override
            public void run() {
            	HttpUtils mHttpUtil = HttpUtils.getInstance();
                HttpClient client = mHttpUtil.createHttpsClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 30000);

               // HttpPost httpPost = new HttpPost(C2DMConstant.PN_C2DM_URL_REGISTER + "?" + PBApplication.getVersionParams());
                // add token cookie
                String token = PBPreferenceUtils.getStringPref(context, PBConstant.PREF_NAME, 
                        PBConstant.PREF_NAME_TOKEN, "none");
                
                ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
                cookies.add(new MapEntry<String, String>("token", token));
              //  cookies.add(new MapEntry<String, String>("Cookie", HttpUtils.createBasicCookiesString(token)));
                //cookies.add(new MapEntry<String, String>("User-Agent", PBApplication.getUserAgentParams()));
                String content = PBAPIHelper.ERROR_DESC;
                try{
                    String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    // Get the deviceID
                    nameValuePairs.add(new BasicNameValuePair("device_token", registrationId/*deviceId*/));
                    nameValuePairs.add(new BasicNameValuePair("registrationid", registrationId));
                    nameValuePairs.add(new BasicNameValuePair("version", PBApplication.VERSION));
                    nameValuePairs.add(new BasicNameValuePair("lang", "ja"/*Locale.getDefault().getLanguage()*/));
                    nameValuePairs.add(new BasicNameValuePair("platform", "android"));
                    // Log.e(PBConstant.TAG, "getVersionParams: " + basicParams.toString());
                    		
                    mHttpUtil.doPost(client, C2DMConstant.PN_C2DM_URL_REGISTER, nameValuePairs, cookies);
                 //   HttpEntity urlEntity = new UrlEncodedFormEntity(nameValuePairs);
                  //  httpPost.setEntity(urlEntity);

                    HttpResponse httpResponse = mHttpUtil.doPost(client, C2DMConstant.PN_C2DM_URL_REGISTER, nameValuePairs, cookies);// client.execute(httpPost);

                    // handle reponse from server
                    if(httpResponse == null) return;

                    int statusCode = ResponseHandle.CODE_404;
                    if(httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                        
                        if(statusCode == ResponseHandle.CODE_200_OK){ // store preference
                            saveDeviceIdToServer(context, deviceId);
                        }
                        HttpEntity entity = httpResponse.getEntity();
                        if(statusCode == HttpStatus.SC_OK){
                        	content = EntityUtils.toString(entity);
                        }
                        
                        Log.d("C2DM", ">>>>>  C2DM_Control: registration with Kayac: " + statusCode + " - "+content );
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (HttpException e) {
					e.printStackTrace();
				}finally{
					if(client!=null)
						client.getConnectionManager().shutdown();
				}
            }
        });

        if(regIdThread != null && !regIdThread.isAlive()){
            regIdThread.start();
        }
    }
    /*public void createNotificationReponse(Context context, String registrationId) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.icon,
                "Registration successful", System.currentTimeMillis());
        // Hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        Intent intent = new Intent(context, null);
        intent.putExtra("registration_id", registrationId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        notification.setLatestEventInfo(context, "Registration",
                "Successfully registered", pendingIntent);
        notificationManager.notify(0, notification);
    }*/
}
