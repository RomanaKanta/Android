package com.aircast.photobag.api;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.utils.PBPreferenceUtils;
import com.aircast.photobag.widget.PBCustomWaitingProgress;

/**
 * Get rate that use acorn to change honey.
 */
public class PBTaskGetExchangeRate extends AsyncTask<Void, Void, Void> {
	private Handler mHandler;
	private PBCustomWaitingProgress progress;
	public final static int UPDATE_UI = 103;
	private Response response;

	public PBTaskGetExchangeRate(Handler handler,
			PBCustomWaitingProgress progress) {
		this.mHandler = handler;
		this.progress = progress;
	}

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
		this.response = PBAPIHelper.getVersionAPI(token, versionName);
		// {"newest":"1.00","url":"http://itunes.apple.com/jp/app/id405548206?mt=8","message":"new version available","items_for_exchange":{"maple":{"rate":30}}}
		try {
			JSONObject result = new JSONObject(response.decription);

			if (result != null) {
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
				if (honeyRate != -1 || goldRate != -1) {
					mHandler.sendEmptyMessage(UPDATE_UI);
				}
			}
		} catch (JSONException e) {
			Log.e(PBConstant.TAG, e.getMessage());
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (this.progress != null)
			this.progress.showWaitingLayout();
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		Context cxt = PBApplication.getBaseApplicationContext();
		String token = PBPreferenceUtils.getStringPref(cxt,
				PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
		if (!TextUtils.isEmpty(token)) {
			Log.e("Photobag", "TOKEN IS EMPTY");
			return null;
		}
		// check newest version update
		checkUpdateVersion(token);

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (this.progress != null)
			this.progress.hideWaitingLayout();

	}
}
