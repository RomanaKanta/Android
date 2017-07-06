package com.aircast.koukaibukuro.helper;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.aircast.koukaibukuro.util.ApiHelper;
import com.aircast.koukaibukuro.util.ApiHelper.Response;
import com.aircast.koukaibukuro.util.Constant;
import com.aircast.koukaibukuro.util.ProgressHUD;
import com.aircast.koukaibukuro.util.SPreferenceUtils;

public class PBTaskNickNameRegistration extends AsyncTask<Void, String, Response> {

	String name;
	Context ctx;
	ProgressHUD mProgressHUD;
	public PBTaskNickNameRegistration(Context context,String name) {
		super();
		this.name = name;
		this.ctx = context;
		mProgressHUD = new ProgressHUD(ctx);
	}
	
	
	@Override
	protected void onPreExecute() {
		mProgressHUD.show(true);
		super.onPreExecute();
	}

	@Override
	protected Response doInBackground(Void... params) {

			String uid = SPreferenceUtils.getStringPref(ctx,
					Constant.PREF_NAME, Constant.UID, "");


		Response response = ApiHelper.nickNameRegistration(uid, name);
		return response;
	}
	
//	@Override
//	protected void onProgressUpdate(String... values) {
//		mProgressHUD.setMessage(values[0]);
//		super.onProgressUpdate(values);
//	}

	@Override
	protected void onPostExecute(Response response) {
		super.onPostExecute(response);
		if (mProgressHUD.isShowing()) {

			mProgressHUD.dismiss();
		}
		
		if (response != null) {
			int response_code = response.errorCode;
			String response_body = response.decription;

			try {
				JSONObject jObject;
				jObject = new JSONObject(response_body);

				if (response_code == 200) {
					jObject = new JSONObject(response_body);

					if (jObject != null) {
						if (jObject.has("message")) {
							Toast.makeText(ctx,
									jObject.getString("message"),
									Toast.LENGTH_SHORT).show();

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
