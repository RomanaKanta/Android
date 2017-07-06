package com.aircast.photobag.services;


import android.os.AsyncTask;
import android.text.TextUtils;

import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;

public class PBMessageListTask extends AsyncTask<Void, Void, Response> {
	
	private OnTaskCompleted listener;
	private String groupName;
	private int count;

	public PBMessageListTask(OnTaskCompleted listener,String groupName,int last_mgs_count) {
		super();
		this.listener = listener;
		this.groupName = groupName;
		this.count = last_mgs_count;
		
	}
	
	

	@Override
	protected Response doInBackground(Void... params) {
		String token = PBPreferenceUtils.getStringPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
		String deviceUUID =  PBPreferenceUtils.getStringPref(PBApplication
                .getBaseApplicationContext(), PBConstant.PREF_NAME,
                PBConstant.PREF_NAME_UID, "0");
		

		if (!TextUtils.isEmpty(token)) {
			Response response = PBAPIHelper.getAllMessage(token, deviceUUID, groupName,count);
			return response;
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Response result) {

		listener.onTaskCompleted();

	}

}
