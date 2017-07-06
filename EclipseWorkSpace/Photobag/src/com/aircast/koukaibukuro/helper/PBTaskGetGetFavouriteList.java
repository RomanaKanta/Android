//package com.aircast.koukaibukuro.helper;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.AsyncTask;
//
//import com.aircast.koukaibukuro.database.DatabaseHandler;
//import com.aircast.koukaibukuro.util.ApiHelper;
//import com.aircast.koukaibukuro.util.ApiHelper.Response;
//import com.aircast.koukaibukuro.util.Constant;
//import com.aircast.koukaibukuro.util.ProgressHUD;
//import com.aircast.koukaibukuro.util.SPreferenceUtils;
//import com.kayac.photobag.activity.PBMainTabBarActivity;
//import com.kayac.photobag.database.PBDatabaseManager;
//
//public class PBTaskGetGetFavouriteList extends
//		AsyncTask<Void, String, Response> {
//
//	Context ctx;
//	boolean isBroadCast = false;
//	ProgressHUD mProgressHUD;
//	private DatabaseHandler db;
//	boolean isFirst = false;
//
//	public PBTaskGetGetFavouriteList(Context context, boolean isBroadCast,
//			boolean isFirst) {
//		super();
//		this.ctx = context;
//		mProgressHUD = new ProgressHUD(ctx);
//		this.isBroadCast = isBroadCast;
//		db = new DatabaseHandler(ctx);
//		this.isFirst = isFirst;
//	}
//
//	@Override
//	protected void onPreExecute() {
//		if (isFirst) {
//			mProgressHUD.show(true);
//		}
//
//		super.onPreExecute();
//	}
//
//	@Override
//	protected Response doInBackground(Void... params) {
//
//		String uid = SPreferenceUtils.getStringPref(ctx, Constant.PREF_NAME,
//				Constant.UID, "");
//		Response response = ApiHelper.getFavouriteList(uid);
//		return response;
//	}
//
//	@Override
//	protected void onPostExecute(Response response) {
//		super.onPostExecute(response);
//		if (mProgressHUD.isShowing()) {
//
//			mProgressHUD.dismiss();
//		}
//		if (response != null) {
//			int response_code = response.errorCode;
//			String response_body = response.decription;
//			System.out.println("Atik get favourite list json response"
//					+ response_body);
//
//			try {
//				JSONObject jObject;
//				jObject = new JSONObject(response_body);
//
//				if (response_code == 200) {
//					
//					PBDatabaseManager mDatabaseManager = PBDatabaseManager
//							.getInstance(PBMainTabBarActivity.sMainContext);
//
//					if (jObject.has("passwords")) {
//						JSONArray jsonArray = jObject.getJSONArray("passwords");
//
//						SPreferenceUtils.saveBoolPref(ctx, Constant.PREF_NAME,
//								Constant.KB_FAVOURITE_JSON_ISSAVED, true);
//
//						for (int i = 0; i < jsonArray.length(); i++) {
//
//							JSONObject obj = jsonArray.getJSONObject(i);
//							String strDownload = "0";
//							
//							if (mDatabaseManager.isPasswordExistsInSentItems(obj
//									.getString("password"))) {
//
//								strDownload = "1";
//							}
//
//							db.addFavouriteItem(obj.getString("thumb_url"),
//									obj.getString("nickname"),
//									obj.getString("password"),
//									obj.getString("created"),
//									obj.getString("expires_at"),
//									obj.getString("expires_time"),
//									obj.getString("photos_count"),
//									obj.getString("downloaded_users_count"),
//									"1", obj.getString("honey"),
//									obj.getString("new"), strDownload,obj.getString("recommended"));
//
//						}
//
//						if (isBroadCast) {
//
//							Intent intent = new Intent(
//									"koukaibukuro.favouritelist");
//							ctx.sendBroadcast(intent);
//
//						}
//
//					}
//
//				} else {
//
//				}
//
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//
//}
