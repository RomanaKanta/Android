package com.aircast.koukaibukuro.helper;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.aircast.koukaibukuro.database.DatabaseHandler;
import com.aircast.koukaibukuro.model.Password;
import com.aircast.koukaibukuro.util.ApiHelper;
import com.aircast.koukaibukuro.util.ApiHelper.Response;
import com.aircast.koukaibukuro.util.Constant;
import com.aircast.koukaibukuro.util.ProgressHUD;
import com.aircast.koukaibukuro.util.SPreferenceUtils;

public class PBTaskAddFavourite extends AsyncTask<Void, String, Response> {

	private Password mPassword;
	Context ctx;
	ProgressHUD mProgressHUD;
	private boolean isRemoved = false;
	private DatabaseHandler db;

	public PBTaskAddFavourite(Context context, Password mPassword,
			boolean isRemoved) {
		super();
		this.ctx = context;
		this.isRemoved = isRemoved;
		this.mPassword = mPassword;
		mProgressHUD = new ProgressHUD(ctx);
		db = new DatabaseHandler(ctx);
	}

	@Override
	protected void onPreExecute() {
		mProgressHUD.show(true);
		super.onPreExecute();
	}

	@Override
	protected Response doInBackground(Void... params) {
		String uid = SPreferenceUtils.getStringPref(ctx, Constant.PREF_NAME,
				Constant.UID, "");
		Response response = ApiHelper
				.addFavourite(uid, mPassword.getPassword());

		return response;
	}

	@Override
	protected void onPostExecute(Response response) {
		super.onPostExecute(response);
		if (mProgressHUD.isShowing()) {

			mProgressHUD.dismiss();
		}

		if (response != null) {
			int response_code = response.errorCode;
			String response_body = response.decription;

			Log.d("respone", response.decription);
			try {
				JSONObject jObject;
				jObject = new JSONObject(response_body);

				if (response_code == 200) {
					jObject = new JSONObject(response_body);

					if (jObject != null) {
						if (jObject.has("message")) {

							Toast.makeText(ctx, jObject.getString("message"),
									Toast.LENGTH_SHORT).show();
							if (isRemoved) {

								db.deleteFavouriteItem(mPassword.getPassword());
								Intent intent = new Intent(
										"koukaibukuro.favouritelist");
								ctx.sendBroadcast(intent);
							} else {

								/*db.addFavouriteItem(itemMap.get("thumb_url"),
										itemMap.get("nickname"),
										itemMap.get("password"),
										itemMap.get("created"),
										itemMap.get("expires_at"), "",
										itemMap.get("photos_count"),
										itemMap.get("downloaded_users_count"),
										"1", itemMap.get("honey"),
										itemMap.get("new"),
										itemMap.get("isDownload"));*/
								int flag = (mPassword.isDownload())? 1 : 0;
								db.addFavouriteItem(mPassword.getThumbURL(),
										mPassword.getNickName(),
										mPassword.getPassword(),
										mPassword.getCreatedDate(),
										mPassword.getExpiresAT(),mPassword.getExpiredTime(),
										String.valueOf(mPassword.getPhotoCount()),
										String.valueOf(mPassword.getNumberOfDownload()),
								          "1",String.valueOf(mPassword.getHoney()),
								        		  String.valueOf( mPassword.getNewItem()),
								         String.valueOf(flag),String.valueOf(mPassword.getRecommend()));
							}

						}

					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
