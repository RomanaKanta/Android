package com.aircast.photobag.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;

/**
 * PBTaskDealADAchievements
 * <p>Get AD achievements list from server. then check while they
 * are installed or not. post installed list to server.</p>
 * */
public class PBTaskDealADAchievements extends AsyncTask<Object, Object, Object> {
	
	@Override
	protected Object doInBackground(Object... arg0) {
		String token = PBPreferenceUtils.getStringPref(
                PBApplication.getBaseApplicationContext(),
                PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
		
		Response response = PBAPIHelper.getAdAchievements(token);
		if (response.errorCode != ResponseHandle.CODE_200_OK) {
			return null;
		}
		
		ArrayList<String> list = new ArrayList<String>();
		try {
			JSONObject result = new JSONObject(response.decription);
			
			JSONArray adList = result.getJSONArray("achievements");
			for (int i=0 ; !adList.isNull(i) ; i++) {
				JSONObject ad = adList.getJSONObject(i);
				
				String adName = ad.names().toString();
				Log.d("AD", "name:" + adName);
				
				String adScheme = ((JSONObject)ad.get(adName)).getString("scheme");
				if (isAdInstalled(adScheme)) {
					list.add(adName);
				}
			}
		} catch (Exception e) {
			Log.w("AD", "Get Ad failed.");
		}
		
		if (!list.isEmpty()) {
			PBAPIHelper.postAdAchievements(list, token);
		}
		
		return null;
	}
	
	private boolean isAdInstalled(String scheme) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
			
			PackageManager packageManager = PBApplication.getBaseApplicationContext().getPackageManager();
	        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
	                PackageManager.GET_ACTIVITIES);
	        if (list.size() != 0){    	
	    	    return true;
	        }
		} catch (Exception e) {}
		return false;
	}
}