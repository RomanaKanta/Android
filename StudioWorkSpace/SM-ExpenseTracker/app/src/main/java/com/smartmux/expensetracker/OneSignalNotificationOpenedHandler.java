package com.smartmux.expensetracker;

import android.util.Log;

import com.onesignal.OneSignal.NotificationOpenedHandler;

import org.json.JSONObject;



public class OneSignalNotificationOpenedHandler implements
		NotificationOpenedHandler {


	@Override
	public void notificationOpened(String message, JSONObject additionalData,
			boolean isActive) {

		try {
			if (additionalData != null) {

				 if (additionalData.has("actionSelected"))
				 Log.d("OneSignalExample",
				 "OneSignal notification button with id " +
				 additionalData.getString("actionSelected") + " pressed");
				// /thumb_url, action_url, text
//				if (additionalData.has("thumb_url") && additionalData.has("action_url") && additionalData.has("text")) {
//					setDataFromJson(additionalData);
//				}

				Log.d("OneSignalExample", "Full additionalData:\n"
						+ additionalData.toString());
				
				
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	
	

//	private void setDataFromJson(JSONObject json) {
//
//		try {
//           new BannerItem(json.getString(AppExtra.NF_TEXT),json.getString(AppExtra.NF_THUMB),json.getString(AppExtra.NF_ACTION),true);
//
////            banner.text = json.getString(AppExtra.NF_TEXT);
////            banner.thumbUrl = json.getString(AppExtra.NF_THUMB);
////            banner.actionUrl = json.getString(AppExtra.NF_ACTION);
////            banner.notifyShow = true;
////			BannerItem item = new BannerItem( text,  thumbUrl,
////					 actionUrl) ;
////
////			if(item!=null){
////				notifyDialog(item);
////				}
//
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//
//
//	}

}
