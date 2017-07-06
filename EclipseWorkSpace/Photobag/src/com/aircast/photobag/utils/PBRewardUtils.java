package com.aircast.photobag.utils;

import jp.co.CAReward_Media.CARMIntent;
import net.adways.appdriver.sdk.AppDriverFactory;
import net.adways.appdriver.sdk.AppDriverTracker;
import net.gree.reward.sdk.GreeRewardFactory;
import net.metaps.sdk.MetapsFactory;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aircast.photobag.activity.PBWebViewActivity;
import com.aircast.photobag.api.PBAPIContant;
import com.aircast.photobag.application.PBConstant;

public class PBRewardUtils {
	
	private static boolean mIsAppdriverSetup = false;
	private static boolean mIsMetapsSetup = false;
	
	public static void setupMetaps(Context context) {
    	try {
    		String invite_code = PBPreferenceUtils.getStringPref(context,
                    PBConstant.PREF_NAME, PBConstant.PREF_NAME_INVITE_CODE,
                    null);
    		
    		MetapsFactory.startReward((Activity) context, invite_code, "");
    	} catch (Exception e) {}
    }
    
    public static void showMetapsWallList(Context context) {
    	try {
    		if (!mIsMetapsSetup) {
    			setupMetaps(context);
    			mIsMetapsSetup = true;
    		}    		
    		
    		String invite_code = PBPreferenceUtils.getStringPref(context,
                    PBConstant.PREF_NAME, PBConstant.PREF_NAME_INVITE_CODE,
                    null);
    		
			Intent intent = MetapsFactory.getIntent((Activity)context, invite_code, "");
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static Intent getGreeIntent(Context context) {
    	String uid = PBPreferenceUtils.getStringPref(
				context, 
				PBConstant.PREF_NAME,
                PBConstant.PREF_NAME_UID, 
                "");
    	
    	Intent intent = new Intent(context, 
    			GreeRewardFactory.getPromotionClass(context));
    	intent.putExtra("MEDIA_ID", 726);
    	intent.putExtra("IDENTIFIER", uid);
    	
    	return intent;
    }
    
    public static Intent getCAIntent(Context context) {
    	String uid = PBPreferenceUtils.getStringPref(
				context, 
				PBConstant.PREF_NAME,
                PBConstant.PREF_NAME_UID, 
                "");
    	
    	Intent caIntent = new Intent(context, CARMIntent.class);
		caIntent.putExtra("m_id", "1849");
		caIntent.putExtra("m_owner_id", "346");
		caIntent.putExtra("user_id", uid);
		caIntent.putExtra("url", "http://car.mobadme.jp/spg/sp/346/1849/index.php");
		caIntent.putExtra("api_key", "136513ed473b8151");
		caIntent.putExtra("app_key", "ncIdX3la");
		caIntent.putExtra("loading_msg", "Now Loading...");
		
		return caIntent;
    }
    
    public static Intent getBridgeIntent(Context context) {
    	String uid = PBPreferenceUtils.getStringPref(
				context, 
				PBConstant.PREF_NAME,
                PBConstant.PREF_NAME_UID, 
                "");
    	
    	Intent webIntent = new Intent(context, PBWebViewActivity.class);
		webIntent.putExtra(PBAPIContant.PB_SETTING_EXTRA_URL, 
				String.format("http://rewardplatform.jp/abukuro/%s", uid));
		webIntent.putExtra(PBAPIContant.PB_SETTING_EXTRA_TITLE, "ゆめゆめ橋");
		
		return webIntent;
    }
    
    public static void setupAppdriverInit(Context context){
    	try {
    		String uid = PBPreferenceUtils.getStringPref(
    				context, 
    				PBConstant.PREF_NAME,
                    PBConstant.PREF_NAME_UID, 
                    "");
    		
	    	AppDriverTracker.requestAppDriver(context, 
	    			PBConstant.APPDRIVER_SITE_ID,
					PBConstant.APPDRIVER_SITE_KEY,
					AppDriverTracker.DEFAULT_MODE,
					uid);
			//AppDriverTracker.setTestMode(true);
			AppDriverTracker.setRefresh(PBConstant.APPDRIVER_REFRESH_TIME,
					PBConstant.APPDRIVER_REFRESH_URL);
			AppDriverTracker.setVerboseMode(true);
			Log.d("UID", uid);

    	} catch (Exception e) { e.printStackTrace(); }
    }
    
    public static Intent getAppdriverIntent(Context context) {
    	System.out.println("Atik enter into mori crash");
    	if (!mIsAppdriverSetup) {
    		setupAppdriverInit(context);
    		mIsAppdriverSetup = true;
    	}
    	
    	String uid = PBPreferenceUtils.getStringPref(
				context, 
				PBConstant.PREF_NAME,
                PBConstant.PREF_NAME_UID, 
                "");
    	
    	Intent intent = new Intent(context, AppDriverFactory.getPromotionClass(context));
		intent.putExtra(AppDriverFactory.MEDIA_ID, PBConstant.APPDRIVER_MEDIA_ID);
		intent.putExtra(AppDriverFactory.IDENTIFIER, uid);
		//intent.putExtra(AppDriverFactory.PROMOTION_ID, 0);
		return intent;
    }
}