package com.smartmux.pos.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by smartmux on 6/1/16.
 */
public class SmartPOSApplication extends Application
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences sharedPreferences;
    public Boolean shouldAutoStart = null;
    public String url = null;
    public String tag = null;
    public String login = null;
    public String password = null;
    public String deviceID = null;
    public static Context appContext;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        appContext =  getApplicationContext();
       /* sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        String device_id = sharedPreferences.getString("device_id", null);


        if(device_id == null) {

            TelephonyManager mTM= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            sharedPreferences.edit()
                    .putString("device_id", String.format("%s-%s",mTM.getDeviceId(), DateFormat.format("yyyy-MM-dd-kk-mm-ss", System.currentTimeMillis())))
                    .commit();
        }*/


    }


    public String getDeviceID(){
        if (deviceID == null) {
            deviceID = sharedPreferences.getString("device_id","<not set>");
        }
        return deviceID;
    }

    public Boolean getShouldAutoStart() {

        if (shouldAutoStart == null) {

            shouldAutoStart = Boolean.valueOf(sharedPreferences.getBoolean("boot", false));

        }
        return shouldAutoStart;

    }

    public String getUrl() {
        if (url == null) {
            url = sharedPreferences.getString("url", "");
        }
        return url;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // TODO Auto-generated method stub

        url = null;
        shouldAutoStart = null;
      //  FusionInventory.log(this, "FusionInventoryApp = " + this.toString(), Log.VERBOSE);
    }

    public String getCredentialsLogin() {
        if (login == null) {
            login = sharedPreferences.getString("login", "");
        }
        return login;
    }

    public String getCredentialsPassword() {
        if (password == null) {
            password = sharedPreferences.getString("password", "");
        }
        return password;
    }

    public String getTag() {
        if (tag == null) {
            tag = sharedPreferences.getString("tag", "");
        }
        return tag;
    }

}
