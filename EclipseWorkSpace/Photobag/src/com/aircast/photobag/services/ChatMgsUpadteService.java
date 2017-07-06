package com.aircast.photobag.services;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.aircast.photobag.api.PBAPIHelper;
import com.aircast.photobag.api.ResponseHandle;
import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.ChatDatabaseHandler;
import com.aircast.photobag.utils.PBPreferenceUtils;

public class ChatMgsUpadteService extends Service implements OnTaskCompleted {

	private final int UPDATE_TIME_PERIOD = 60000;
	private Handler handler;
	private Response response;
	private boolean isStart = true;
	//private ArrayList<HashMap<String, String>> listOfMgs = new ArrayList<HashMap<String, String>>();


	@Override
	public void onCreate() {
		

		handler = new Handler();
		handler.postDelayed(runnable, 100);
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(runnable);
		super.onDestroy();
	}
	
	

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			checkAvailableMessage();
			handler.postDelayed(this, UPDATE_TIME_PERIOD);

		}
	};

	public void checkAvailableMessage() {
		
		//Toast.makeText(getApplicationContext(), "checkAvailableMessage", 1000).show();
		PBGroupStatus task = new PBGroupStatus();
		task.execute();
		

	}
	
	
private class PBGroupStatus extends AsyncTask<Void, Void, Response> {
		
		private String token;
		private String groupName;
		private String deviceUUID;

		@Override
		protected Response doInBackground(Void... params) {
			
			 token = PBPreferenceUtils.getStringPref(
					PBApplication.getBaseApplicationContext(),
					PBConstant.PREF_NAME, PBConstant.PREF_NAME_TOKEN, "");
			 groupName = PBPreferenceUtils.getStringPref(PBApplication.getBaseApplicationContext(),
				  	   PBConstant.PREF_NAME, PBConstant.PREF_GROUP_NAME,"");
			  deviceUUID =  PBPreferenceUtils.getStringPref(PBApplication
	                .getBaseApplicationContext(), PBConstant.PREF_NAME,
	                PBConstant.PREF_NAME_UID, "0");
			if (!TextUtils.isEmpty(token)) {
				Response response = PBAPIHelper.groupStatus(token, deviceUUID,
						groupName);
				
				return response;
				
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Response reslut) {
			
			
			if (reslut != null) {

				int response_code = reslut.errorCode;
				String response_body = reslut.decription;
				
				
				try {
					JSONObject jObject;
					jObject = new JSONObject(response_body);
				if (response_code == ResponseHandle.CODE_200_OK) {

					long timerValue = jObject.getLong("message");
					
					System.out.println("Atik timer value inside service:"+timerValue);
					
					PBPreferenceUtils.saveLongPref(PBApplication.getBaseApplicationContext(),
							PBConstant.PREF_NAME,
							PBConstant.PREF_GROUP_EXPIRED_TIME,timerValue);
				
					Intent intent = new Intent(
							"photobag.chat.groupstatus");
					intent.putExtra("status", 1);
					sendBroadcast(intent);
					
					
					int count = PBPreferenceUtils.getIntPref(PBApplication
			                .getBaseApplicationContext(),  PBConstant.PREF_NAME,
							PBConstant.PREF_MGS_COUNT, 0);
					
						if(isStart){
							
							ChatDatabaseHandler db = new ChatDatabaseHandler(getApplicationContext());
							db.resetTables();
							count = 0;
							isStart = false;
						}
						
						PBMessageListTask task = new PBMessageListTask(ChatMgsUpadteService.this,groupName,count);
						try {
							response = task.execute().get();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
				}else{
					
					//listOfMgs.clear();
					Log.d("sendBroadcast", "sendBroadcast");
					Intent intent = new Intent(
							"photobag.chat.mgslist");
					intent.putExtra("noDataExist", 1);
					//intent.putExtra("mgslist", listOfMgs);
					sendBroadcast(intent);
					
					Intent intent1 = new Intent(
							"photobag.chat.groupstatus");
					intent1.putExtra("status", 0);
					intent1.putExtra("mgs", jObject.getString("message"));
					sendBroadcast(intent1);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
			
		}

	}
	

@Override
public void onTaskCompleted() {
	if (response != null) {
		int response_code = response.errorCode;
		String respone_description = response.decription;
		System.out.println("Atik get message list response: " +respone_description );
		if (response_code == ResponseHandle.CODE_200_OK) {

			JSONObject jObject;
			try {
				jObject = new JSONObject(respone_description);
				
				if (jObject != null) {
					if (jObject.has("messages")) {

						JSONArray resultArray = jObject
								.getJSONArray("messages");
						System.out.println("Atik Json response:"+resultArray.length());
						
						if(resultArray.length()>0) {
							//listOfMgs.clear();
							ChatDatabaseHandler db = new ChatDatabaseHandler(
									getApplicationContext());
							String collectionid = PBPreferenceUtils.getStringPref(
									PBApplication.getBaseApplicationContext(),
									PBConstant.PREF_NAME, PBConstant.PREF_CURRENT_HISTORY_COLLECTION_ID, "");
							
							for (int i = 0; i < resultArray.length(); i++) {
								JSONObject obj = resultArray.getJSONObject(i);
								HashMap<String, String> map = new HashMap<String, String>();

								//map.put("nickname", obj.getString("nickname"));
								/*map.put("message", obj.getString("message"));
								map.put("uid", obj.getString("uid"));
								map.put("created", obj.getString("created"));
								listOfMgs.add(map);*/
								db.addMessage(obj.getString("uid"), obj.getString("message"), obj.getString("created")
										, collectionid,obj.getString("nickname"),obj.getString("color"));
							}
							Log.d("sendBroadcast", "sendBroadcast1");
							Intent intent = new Intent(
									"photobag.chat.mgslist");
							intent.putExtra("noDataExist", 2);
							//intent.putExtra("mgslist", listOfMgs);
							sendBroadcast(intent);
							
						} else if(resultArray.length() == 0){
							//listOfMgs.clear();
							Log.d("sendBroadcast", "sendBroadcast2");
							Intent intent = new Intent(
									"photobag.chat.mgslist");
							intent.putExtra("noDataExist", 1);
							//intent.putExtra("mgslist", listOfMgs);
							sendBroadcast(intent);
						}


						/*if (listOfMgs.size() = 0) {

							Log.d("sendBroadcast", "sendBroadcast");
							Intent intent = new Intent(
									"photobag.chat.mgslist");
							intent.putExtra("mgslist", listOfMgs);
							sendBroadcast(intent);

						}*/

					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}else{
			
			//listOfMgs.clear();
			Log.d("sendBroadcast", "sendBroadcast3");
			Intent intent = new Intent(
					"photobag.chat.mgslist");
			intent.putExtra("noDataExist", 1);
			//intent.putExtra("mgslist", listOfMgs);
			sendBroadcast(intent);
		}

	}

}

}
