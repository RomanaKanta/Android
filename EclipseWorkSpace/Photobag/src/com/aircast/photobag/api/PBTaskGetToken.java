package com.aircast.photobag.api;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.api.c2dm.C2DMConstant;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;

/**
 * class PBTaskGetToken support get token for Photobag app in background
 * 
 * @author lent5
 * 
 */
public class PBTaskGetToken extends AsyncTask<Void, Void, Void> {
	/** mHandle use invoke update UI */
	private Handler mHandler;

	/** use to notify to upper layer has new version */
	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}

	private final int UPDATE_UI = 103;

	/** check new update applciaiton version */
	private void checkUpdateVersion(String token) {
		Context context = PBApplication.getBaseApplicationContext();
		if (context == null)
			return;

		// check version package start
		String versionName = "1.0";
		try {
			PackageInfo manager = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			versionName = manager.versionName;
		} catch (NameNotFoundException e) {
			Log.e(PBConstant.TAG,
					"package name fail when check application's verion name");
		}

		// request to server and parser data response
		Response rs = PBAPIHelper.getVersionAPI(token, versionName);
		// {"newest":"1.00","url":"http://itunes.apple.com/jp/app/id405548206?mt=8","message":"new version available","items_for_exchange":{"maple":{"rate":30}}}
		String newestVersion = versionName;
		String url = "";
		Log.d("AGUNG", "GET TOKEN : " +rs.decription);
		try {
			JSONObject result = new JSONObject(rs.decription);

			if (result != null) {
				if (result.has("url")) {
					url = result.getString("url");
					PBPreferenceUtils.saveStringPref(context,
							PBConstant.PREF_NAME,
							PBConstant.PREF_NAME_MARKET_URL, url);
				}

				if (result.has("newest")) {
					newestVersion = result.getString("newest");
				}

				int honeyRate = -1;
				int goldRate = -1;
				if (result.has("items_for_exchange")) {
					JSONObject list = result.getJSONObject("items_for_exchange");					
					if (list.has("maple")) {
						honeyRate = list.getJSONObject("maple").optInt("rate");
						PBPreferenceUtils.saveIntPref(
								PBApplication.getBaseApplicationContext(),
								PBConstant.PREF_NAME,
								PBConstant.PREF_DONGURI_HONEY_EXCHANGE_RATE,
								honeyRate);
					}
				}
				if (result.has("items_for_exchange_with_goldacorns")) {
					JSONObject list = result.getJSONObject("items_for_exchange_with_goldacorns");					
					if (list.has("maple")) {
						goldRate = list.getJSONObject("maple").optInt("rate");
						PBPreferenceUtils.saveIntPref(
								PBApplication.getBaseApplicationContext(),
								PBConstant.PREF_NAME,
								PBConstant.PREF_DONGURI_GOLD_EXCHANGE_RATE,
								goldRate);
					}
				}
			}
		} catch (JSONException e) {
			Log.e(PBConstant.TAG, e.getMessage());
		}

		// notify to user if has newest update
		Log.d(PBConstant.TAG, "[response reuest newest version] "
				+ rs.decription);
		if (!TextUtils.isEmpty(url)
				&& !TextUtils.equals(newestVersion, versionName)) {
			try {
				if (versionName.equals(newestVersion)) {
					// if(Double.parseDouble(versionName) >
					// Double.parseDouble(newestVersion)){
					return;
				}
			} catch (NumberFormatException e) {
				Log.e(PBConstant.TAG, "parsing version code error");
				return;
			}
			if (mHandler != null) {
				Message msg = new Message();
				msg.what = UPDATE_UI;
				msg.obj = rs.decription;
				mHandler.sendMessage(msg);
			}
		}
		// check version package end
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... arg0) {

		Context cxt = PBApplication.getBaseApplicationContext();
		String response = "";
		String uid = "";
		if (PBPreferenceUtils.getStringPref(cxt, PBConstant.PREF_NAME,
				PBConstant.PREF_NAME_TOKEN, "").equals("")
			||PBPreferenceUtils.getStringPref(cxt, PBConstant.PREF_NAME,
					PBConstant.PREF_NAME_UID, "").equals(""))
				response = PBAPIHelper.getTokenV3(cxt);

		if (!TextUtils.isEmpty(response)) {
			// parse token
			try {
				JSONObject result = new JSONObject(response);

				if (result != null) {
					if (cxt == null)
						return null;

					if (result.has("uid")) {
						uid = result.getString("uid");
						PBPreferenceUtils.saveStringPref(cxt,
								PBConstant.PREF_NAME, PBConstant.PREF_NAME_UID,
								uid);
						Log.d("AGUNG", "uid : " + uid);
					}

					if (result.has("invite_code")) {
						PBPreferenceUtils.saveStringPref(cxt,
								PBConstant.PREF_NAME,
								PBConstant.PREF_NAME_INVITE_CODE,
								result.getString("invite_code"));
					}

					if (result.has("free_period")) {
						PBPreferenceUtils.saveIntPref(cxt,
								PBConstant.PREF_NAME,
								PBConstant.PREF_NAME_FREE_PERIOD,
								result.getInt("free_period"));
					}

					if (result.has("invited_users")) {
						PBPreferenceUtils.saveIntPref(cxt,
								PBConstant.PREF_NAME,
								PBConstant.PREF_NAME_INVITATION_USERS,
								result.getInt("invited_users"));
					}

				}

			} catch (JSONException e) {
				Log.e(PBConstant.TAG, e.getMessage());
			}
		}
		
		PBGeneralUtils.saveTokenToCacheFolder();

		String token = PBPreferenceUtils.getStringPref(cxt,
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(PBApplication
						.getBaseApplicationContext());
		
		// String registrationId = prefs.getString(C2DMConstant.PN_C2DM_AUTH,
		// "");
		String deviceId = prefs.getString(C2DMConstant.PN_C2DM_DEVICE_AUTH, "");
		// check registration c2dm successfule existing
		// if not do register
		if (TextUtils.isEmpty(deviceId) && !TextUtils.isEmpty(token)) {
			//PBApplication.register();
		}

		// check newest version update
		checkUpdateVersion(token);

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}
}
