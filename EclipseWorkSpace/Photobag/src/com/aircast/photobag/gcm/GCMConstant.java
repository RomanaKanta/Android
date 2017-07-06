package com.aircast.photobag.gcm;

import com.aircast.photobag.api.PBAPIContant;

/**
 * GCM's contants
 * @author lent5
 */
public class GCMConstant {
    // support Push Notification
    public static final String PN_GCM_URL_REGISTER 		= "https://api."+PBAPIContant.MAIN_DEVELOPMENT_URL+"/2/gcm";
    public static final String PN_GCM_AUTH 		   		= "gcm_authentication";
    public static final String PN_GCM_DEVICE_AUTH  		= "gcm_authentication";
    public static final String PN_GCM_PROJECT_ID	  	= PBAPIContant.isProduction?"798279668265" : "780576592869";   
}
