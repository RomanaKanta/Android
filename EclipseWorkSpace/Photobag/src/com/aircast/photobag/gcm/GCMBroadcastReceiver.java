package com.aircast.photobag.gcm;

/*
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import com.kayac.photobag.api.HttpUtils;
import com.kayac.photobag.api.MapEntry;
import com.kayac.photobag.api.PBAPIHelper;
import com.kayac.photobag.api.ResponseHandle;
import com.kayac.photobag.application.PBApplication;
import com.kayac.photobag.application.PBConstant;
import com.kayac.photobag.utils.PBPreferenceUtils;


import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
*/
import android.content.Context;
public class GCMBroadcastReceiver extends com.google.android.gcm.GCMBroadcastReceiver {

	@Override
	protected String getGCMIntentServiceClassName(Context context) {
		return "com.kayac.photobag.gcm.GCMIntentService";
	}
}
