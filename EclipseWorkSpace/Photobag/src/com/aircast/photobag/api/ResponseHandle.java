package com.aircast.photobag.api;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.aircast.photobag.R;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.model.PBTimelineHistoryModel;
import com.aircast.photobag.utils.PBPreferenceUtils;

/**
 * class ResponseHandle to support description from HttpReponse
 * @author lent5
 *
 */
public class ResponseHandle {
    public static final int CODE_200_OK = 200;
    public static final int CODE_400 = 400;
    public static final int CODE_404 = 404;
    public static final int CODE_403 = 403;
    public static final int CODE_406 = 406;
    public static final int CODE_INVALID_PARAMS = 999;
    public static final int CODE_HTTP_FAIL = 900;
    public static final int CODE_FILE_COMPRESS_FAIL = 905;

    private static final String ERR_DESC_INVITE_CODE_NOT_FOUND = "invite_code was empty";
    private static final String ERR_DESC_INVITE_USER_NOT_FOUND = "target user not found";
    private static final String ERR_DESC_INVITE_CANT_YOURSELF = "cant invite yourself";
    private static final String ERR_DESC_INVITE_ALREADY_INVITED = "already invited";

    private static final String ERR_DESC_PASSWORD_YOUR_PASS = "your";
    private static final String ERR_DESC_PASSWORD_TAKEN = "taken";
    private static final String ERR_DESC_PASSWORD_TOO_SHORT = "too short";
    private static final String ERR_DESC_PASSWORD_TOO_LONG = "too long";
    private static final String ERR_DESC_PASSWORD_INVALID = "invalid";
    //20122606 @HaiNT15: add error descriotion #S
    private static final String ERR_DESC_PUBLIC_PASSWORD_NOT_FOUND = "not found";
    private static final String ERR_DESC_PUBLIC_PASSWORD_NOT_FINISHED = "not finished";
    private static final String ERR_DESC_PUBLIC_PASSWORD_NOT_YOURS = "not yours";
    private static final String ERR_DESC_PUBLIC_PASSWORD_EXPIRED = "expired";
    //20122606 @HaiNT15: add error descriotion #E
    private static final String ERR_DESC_DOWLOAD_UNFINISHED = "unfinished";
    // private static final String ERR_DESC_DOWLOAD_PAYMENT_REQUIRED = "payment required";
    private static final String ERR_DESC_DOWLOAD_EXPIRED = "expired";

    /** Constructor Reponse */
    public static class Response{
        public int errorCode = 200;
        public String decription = "";

        /** Contructor Response */
        public Response() {
        }

        /**
         * Contructor Response 
         * @param errorCode
         * @param decription
         */
        public Response(int errorCode, String decription) {
            this.errorCode = errorCode;
            this.decription = decription;
        }
    }

    /**
     * return the error dscription when reponse error code in case make a request invite friend
     * @param errDescCode
     * @return
     */
    public static String getNotifyStringErrorWhenInviteFriend(int errorCode, String errDescCode){
        if(TextUtils.isEmpty(errDescCode)) return "";

        Context context = PBApplication.getBaseApplicationContext();
        if(context != null){
            if(errDescCode.contains(ERR_DESC_INVITE_CODE_NOT_FOUND)){
                return context.getString(R.string.err_desc_invite_code_not_found);
            }else if(errDescCode.contains(ERR_DESC_INVITE_USER_NOT_FOUND)){
                return context.getString(R.string.err_desc_invite_user_not_found);
            }else if(errDescCode.contains(ERR_DESC_INVITE_CANT_YOURSELF)){
                return context.getString(R.string.err_desc_invite_cant_yourself);
            }else if(errDescCode.contains(ERR_DESC_INVITE_ALREADY_INVITED)){
                return context.getString(R.string.err_desc_invite_already_invited);
            }
        }
        if(errorCode == ResponseHandle.CODE_HTTP_FAIL){
            return context.getString(R.string.pb_network_error_content);
        }else if(errorCode == ResponseHandle.CODE_INVALID_PARAMS){
            return context.getString(R.string.pb_apihelper_token_empty);
        }
        
        return errDescCode;
    }

    /**
     * return the error dscription when reponse error code in case make a request set/confirm password
     * @param errDescCode
     * @return
     */
    public static String getNotifyStringErrorRelatedPassword(int errorCode, String errDescCode){
        if(TextUtils.isEmpty(errDescCode)) return "";
        Context context = PBApplication.getBaseApplicationContext();
        String invalidCharacter = null;
        if(context != null){
            if(errDescCode.contains(ERR_DESC_PASSWORD_YOUR_PASS)){
                return context.getString(R.string.err_desc_password_your_pass);
            }else if(errDescCode.contains(ERR_DESC_PASSWORD_TAKEN)){
                return context.getString(R.string.err_desc_password_taken);
            }else if(errDescCode.contains(ERR_DESC_PASSWORD_TOO_SHORT)){
                return context.getString(R.string.err_desc_password_to_short);
            }else if(errDescCode.contains(ERR_DESC_PASSWORD_TOO_LONG)){
                return context.getString(R.string.err_desc_password_to_long);
            }else if(errDescCode.contains(ERR_DESC_PASSWORD_INVALID)){
            	// TODO generate message for Invalid character
            	if(PBPreferenceUtils.getBoolPref(
            			context, 
		                PBConstant.PREF_NAME, 
		                PBConstant.PREF_INVALID_CHARACTER_EXISTS, 
		                false)) {
            		
            		
            		invalidCharacter = PBPreferenceUtils.getStringPref(
            				context, 
            				PBConstant.PREF_NAME, 
     		                PBConstant.PREF_INVALID_CHARACTER, 
            				"");
            		
					PBPreferenceUtils.saveBoolPref(context,
							PBConstant.PREF_NAME, PBConstant.PREF_INVALID_CHARACTER_EXISTS,
							false);
				    PBPreferenceUtils.saveStringPref(
				    		context,
							PBConstant.PREF_NAME,
							PBConstant.PREF_INVALID_CHARACTER,
							"");
				    
				    if(!invalidCharacter.isEmpty()) {
				    	invalidCharacter = context.getString(R.string.err_desc_password_invalid_characters) +
				    					   context.getString(R.string.err_desc_password_invalid_character_from_server) 
				    					   + "[" +invalidCharacter+"]";
				    }
            		return invalidCharacter;
            	}
            	
                return context.getString(R.string.err_desc_password_invalid_characters);
            }
        }
        
        if(errorCode == ResponseHandle.CODE_HTTP_FAIL){
            return context.getString(R.string.pb_network_error_content);
        }else if(errorCode == ResponseHandle.CODE_INVALID_PARAMS){
            return context.getString(R.string.pb_apihelper_token_empty);
        }
        
        return errDescCode;
    }
    //20122606 @HaiNT15: handle error description for public magic phrase #S
    /**
     * return the error dscription when reponse error code in case make a request public password
     * @param errDescCode
     * @return
     */
    public static String getNotifyStringErrorPublicPassword(int errorCode, String errDescCode){
        if(TextUtils.isEmpty(errDescCode)) return "";
        Context context = PBApplication.getBaseApplicationContext();
        if(context != null){
            if(errDescCode.contains(ERR_DESC_PUBLIC_PASSWORD_NOT_FOUND)){
                return context.getString(R.string.err_desc_password_public_not_found);
            }else if(errDescCode.contains(ERR_DESC_PUBLIC_PASSWORD_NOT_FINISHED)){
                return context.getString(R.string.err_desc_password_public_not_finised);
            }else if(errDescCode.contains(ERR_DESC_PUBLIC_PASSWORD_NOT_YOURS)){
                return context.getString(R.string.err_desc_password_public_not_yours);
            }else if(errDescCode.contains(ERR_DESC_PUBLIC_PASSWORD_EXPIRED)){
                return context.getString(R.string.err_desc_password_public_expired);
            }
        }
        
        if(errorCode == ResponseHandle.CODE_HTTP_FAIL){
            return context.getString(R.string.pb_network_error_content);
        }else if(errorCode == ResponseHandle.CODE_INVALID_PARAMS){
            return context.getString(R.string.pb_apihelper_token_empty);
        }
        
        return errDescCode;
    }
    //20122606 @HaiNT15: handle error description for public magic phrase #E
    /**
     * return the error dscription when reponse error code in case make a request set/confirm password
     * @param errDescCode
     * @return
     */
    public static String getNotifyStringErrorRelatedDowload(int errorCode, String errDescCode){
        if(TextUtils.isEmpty(errDescCode)) return "";

        Context context = PBApplication.getBaseApplicationContext();
        if(context != null){
            if(errDescCode.contains(ERR_DESC_DOWLOAD_UNFINISHED)){
                return context.getString(R.string.err_desc_dowload_unfinished);
            /*}else if(errDescCode.contains(ERR_DESC_DOWLOAD_PAYMENT_REQUIRED)){
                return context.getString(R.string.err_desc_dowload_payment_required);*/
            }else if(errDescCode.contains(ERR_DESC_DOWLOAD_EXPIRED)){
                return context.getString(R.string.err_desc_dowload_expired);
            }
        }

        if(errorCode == ResponseHandle.CODE_HTTP_FAIL){
            return context.getString(R.string.pb_network_error_content);
        }else if(errorCode == ResponseHandle.CODE_INVALID_PARAMS){
            return context.getString(R.string.pb_apihelper_token_empty);
        }
        
        return context.getString(R.string.err_desc_dowload_not_found);
    }
    
    /**
     * parse data get free time period from server and store shared reference
     * @param jsonReponse
     * @return
     */
    public static int parseFetchFreeTimeAndSaveToPreference(String jsonReponse){
//        int rs = PBPreferenceUtils.getIntPref(
//                PBApplication.getBaseApplicationContext(), PBConstant.PREF_NAME,
//                PBConstant.PREF_NAME_FREE_PERIOD, 0);
        
        int rs = PBPreferenceUtils.getIntPref(
                PBApplication.getBaseApplicationContext(), PBConstant.PREF_NAME,
                PBConstant.PREF_HONEY_BONUS, 0);

        if(TextUtils.isEmpty(jsonReponse)) return rs;
        
        try {
            JSONObject result = new JSONObject(jsonReponse);
            Log.d("AGUNG", "FETCH ME INFO : "+result.toString());
            if (result != null) {
                if (result.has("uid")) {
                    PBPreferenceUtils.saveStringPref(
                            PBApplication.getBaseApplicationContext(), PBConstant.PREF_NAME,
                            PBConstant.PREF_NAME_UID,
                            result.getString("uid"));
                }
                if (result.has("free_period")) {
                    rs = result.getInt("free_period");
                    PBPreferenceUtils.saveIntPref(
                            PBApplication.getBaseApplicationContext(), PBConstant.PREF_NAME,
                            PBConstant.PREF_NAME_FREE_PERIOD, rs);
                }
                if (result.has("invite_code")) {
                    PBPreferenceUtils.saveStringPref(
                            PBApplication.getBaseApplicationContext(), PBConstant.PREF_NAME,
                            PBConstant.PREF_NAME_INVITE_CODE,
                            result.getString("invite_code"));
                }
                if (result.has("invited_users")) {
                    PBPreferenceUtils.saveIntPref(
                            PBApplication.getBaseApplicationContext(), PBConstant.PREF_NAME,
                            PBConstant.PREF_NAME_INVITATION_USERS,
                            result.getInt("invited_users"));
                }
                if (result.has("bonus")) {
                    PBPreferenceUtils.saveIntPref(
                            PBApplication.getBaseApplicationContext(), PBConstant.PREF_NAME,
                            PBConstant.PREF_NAME_BONUS,
                            result.getInt("bonus"));
                }                
                
                if(result.has("honey_count")){
                    PBPreferenceUtils.saveIntPref(
                            PBApplication.getBaseApplicationContext(), 
                            PBConstant.PREF_NAME, 
                            PBConstant.PREF_HONEY_BONUS, 
                            result.getInt("honey_count"));
                }
                
                if(result.has("input_invite_code_done")){
                    PBPreferenceUtils.saveIntPref(
                            PBApplication.getBaseApplicationContext(), 
                            PBConstant.PREF_NAME, 
                            PBConstant.PREF_INPUT_INVITE_CODE_DONE, 
                            result.getInt("input_invite_code_done"));
                }
                
                //new
                if(result.has("acorn_count")){
                	PBPreferenceUtils.saveIntPref(
                			PBApplication.getBaseApplicationContext(),
                			PBConstant.PREF_NAME, PBConstant.PREF_DONGURI_COUNT, 
                			result.getInt("acorn_count"));
                }
                if(result.has("maple_count")){
                	PBPreferenceUtils.saveIntPref(
                			PBApplication.getBaseApplicationContext(),
                			PBConstant.PREF_NAME, PBConstant.PREF_MAPLE_COUNT, 
                			result.getInt("maple_count"));
                }
                if(result.has("goldacorn_count")){
                	PBPreferenceUtils.saveIntPref(
                			PBApplication.getBaseApplicationContext(),
                			PBConstant.PREF_NAME, PBConstant.PREF_GOLD_COUNT, 
                			result.getInt("goldacorn_count"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return rs;
    }
    
    /**
     * parse data of exchange rate from server and store shared reference
     * @param jsonReponse
     * @return
     */
    public static int parseMapleExchangeRateAndSaveToPreference(String jsonReponse){
    	int rs = PBPreferenceUtils.getIntPref(
                PBApplication.getBaseApplicationContext(), PBConstant.PREF_NAME,
                PBConstant.PREF_DONGURI_HONEY_EXCHANGE_RATE, 30);

        if(TextUtils.isEmpty(jsonReponse)) return rs;
        
        try {
            JSONObject result = new JSONObject(jsonReponse);

            if (result != null) {
                if (result.has("maple")) {
                	JSONObject jsonRate = result.getJSONObject("maple");
                	if(jsonRate!=null && jsonRate.has("rate")){
                		Log.d("AGUNG", "Got the maple rate: "+jsonRate.getInt("rate"));
                		PBPreferenceUtils.saveIntPref(
                                PBApplication.getBaseApplicationContext(), PBConstant.PREF_NAME,
                                PBConstant.PREF_DONGURI_HONEY_EXCHANGE_RATE,
                                jsonRate.getInt("rate"));
                		rs = jsonRate.getInt("rate");
                	}
                    
                }
               
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return rs;
    }
    
    public static ArrayList<PBTimelineHistoryModel> parseTimelineHistoryAndSaveToDatabase(Context context, String jsonResponse){
    	ArrayList<PBTimelineHistoryModel> rs = new ArrayList<PBTimelineHistoryModel>();
    	if(TextUtils.isEmpty(jsonResponse)) return rs;
    	
    	try{
    		JSONObject json = new JSONObject(jsonResponse);
    		//int timeServer = json.optInt("now", -1);
    		JSONArray arr = json.optJSONArray("histories");
    		if(arr!=null){
    			for(int i = 0;i<arr.length();i++){
    				JSONObject jsonItem = arr.getJSONObject(i);
    				PBTimelineHistoryModel item = new PBTimelineHistoryModel(
    						jsonItem.getInt("id"), 
    						jsonItem.getInt("created_at"),
    						PBTimelineHistoryModel.Type.valueOf(jsonItem.getString("type").toUpperCase()),
    						jsonItem.getString("description"),
    						true);
    				rs.add(item);
    			}
    		}
    		PBDatabaseManager.getInstance(context).insertTimelineHistory(rs);
    		
    	}catch(JSONException e){
    		e.printStackTrace();
    	}
    	
    	return rs;
    }
    
}
