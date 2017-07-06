package com.smartmux.couriermoc.api;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * class helper api support communicate to server
 */
public class PBAPIHelper {
	
    public static final String ERROR_DESC = "Conenction timeout";
    
    public static ResponseHandle.Response getCallToServer(String url,HashMap<String, String> postDataParams) {
//		if (TextUtils.isEmpty(phone))
//			return new Response(ResponseHandle.CODE_INVALID_PARAMS,
//					"phone is empty! Please try again later!");
    	
    	if(postDataParams != null){

    		Log.d("api call", "url "+url +" \nparams" + postDataParams);
    	}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = ResponseHandle.CODE_INVALID_PARAMS;

		ResponseHandle.Response response = new ResponseHandle.Response(statusCode, content);
		if (mHttpUtils != null) {

		//	int tryAgain = 3;
			//do {
				try {
					response = mHttpUtils.doPost(url, postDataParams);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (response != null) {

					statusCode = response.errorCode;
				}

				content = response.decription;
		//	} while (statusCode == ResponseHandle.CODE_HTTP_FAIL
		//			&& (--tryAgain) > 0); // try 3 time if connection fail

			 }

		return new ResponseHandle.Response(statusCode, content);
	}
    
    
    
    public static ResponseHandle.Response getHTTPCallToServer(String url,HashMap<String, String> postDataParams) {
//		if (TextUtils.isEmpty(phone))
//			return new Response(ResponseHandle.CODE_INVALID_PARAMS,
//					"phone is empty! Please try again later!");
    	
//    	if(postDataParams != null){
//    		
//    		Log.d("postDataParams", ""+postDataParams);
//    	}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = ResponseHandle.CODE_INVALID_PARAMS;

		ResponseHandle.Response response = new ResponseHandle.Response(statusCode, content);
		if (mHttpUtils != null) {

			//int tryAgain = 3;
			//do {
				response = mHttpUtils.doPostWithHttp(url, postDataParams);

				if (response != null) {

					statusCode = response.errorCode;
				}

				content = response.decription;
			//} while (statusCode == ResponseHandle.CODE_HTTP_FAIL
			//		&& (--tryAgain) > 0); // try 3 time if connection fail

			 }

		return new ResponseHandle.Response(statusCode, content);
	}
    
    
}