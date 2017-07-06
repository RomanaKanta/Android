package com.aircast.photobag.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;

import com.aircast.photobag.api.ResponseHandle.Response;
import com.aircast.photobag.api.c2dm.C2DMConstant;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseDefinition;
import com.aircast.photobag.log.SdcardException;
import com.aircast.photobag.utils.PBBitmapUtils;
import com.aircast.photobag.utils.PBCustomMultiPartEntity;
import com.aircast.photobag.utils.PBPreferenceUtils;

/**
 * class helper api support communicate photobag server
 */
public class PBAPIHelper {
	
    public static final String ERROR_DESC = "Conenction timeout";
    
    /**
     * use to update progress status during uploading/downloading
     * @author lent5
     */
    public interface ProgressManagerListener{
        public boolean cancelDownloadTask();
        public void updateProgressStatus(int percent);
    }
    
    /**
    * <b>1  Fetch a token in security way</b>
    * <p>This func run when user first run photobag, Old user already have token will not call this function anymore.</p>
    * response {"free_period":0,"invite_code":"gzztsq","invited_users":0,"uid":"0ea877a0bc9dddeee2b66d2038e11283dce8b5e2"}</p>
    * <br>
    * @param context : activity context.
    * @return response from server.
    */
    public static String getTokenV3(Context context){ 
        
        // check expires and token is exist #E
        String rsData = "";
        HttpResponse response;
        HttpUtils mHttpUtils = HttpUtils.getInstance(); 
        int statusCode = ResponseHandle.CODE_200_OK;
        
        if(mHttpUtils != null){
            DefaultHttpClient httpclient = (DefaultHttpClient)mHttpUtils.createHttpsClient();
            if(httpclient == null) return rsData;
            
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            
            nameValuePairs.add(new BasicNameValuePair("random", PBApplication.getMobileCode()));
            nameValuePairs.add(new BasicNameValuePair("signature", 
            		hmacSha1(PBApplication.getMobileCode(), PBAPIContant.API_SECRET_KEY)));
            
            String manufacturer = Build.MANUFACTURER;
			String model = Build.MODEL;
			String deviceName = "";
			if (model.startsWith(manufacturer)) {
				deviceName = model.toUpperCase();
			} else {
				deviceName = manufacturer.toUpperCase() + " "
						+ model.toUpperCase();
			}
            
			System.out.println("Atik device name is during registration:"+deviceName);
			
            // Atik added new parameters for security strengthen
            nameValuePairs.add(new BasicNameValuePair("st", PBAPIContant.API_ST_VALUE));
            
			if(!deviceName.isEmpty()) {
				nameValuePairs.add(new BasicNameValuePair("device", deviceName));
			}   else {
				nameValuePairs.add(new BasicNameValuePair("device", ""));

			}           
            
			
			
            Log.d("UUID", PBApplication.getMobileCode());
            int tryAgain = 3;
            do{
                try {
                    
                    response = mHttpUtils.doPost(httpclient, 
                    		PBAPIContant.API_FETCH_TOKEN_URL_V3, 
                    		nameValuePairs, 
                    		null);
                    
                    if(response != null && response.getStatusLine() != null){
                        statusCode = response.getStatusLine().getStatusCode();
                    }
                    
                    if(PBAPIContant.DEBUG){
                    	
                    	 Log.d("getToken response",""+ response.getStatusLine().getStatusCode());
                    }
                    if (statusCode == HttpStatus.SC_OK) {

                        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
                        Date expires = null;
                        String token = "";
                        for(Cookie cookie: cookies){
                            if(TextUtils.equals(cookie.getName(), "token")){
                                token = cookie.getValue();
                            }
                            expires = cookie.getExpiryDate();
                        }

                        if(!TextUtils.isEmpty(token)
                        	&& TextUtils.isEmpty(PBPreferenceUtils.getStringPref(
                        			context, 
                        			PBConstant.PREF_NAME, 
                        			PBConstant.PREF_NAME_TOKEN, 
                        			null))){
                            PBPreferenceUtils.saveStringPref(context, PBConstant.PREF_NAME, 
                                    PBConstant.PREF_NAME_TOKEN, token);
                            Log.d("TOKEN", token);
                        }   
                        
                        if(expires != null){
                            PBPreferenceUtils.saveStringPref(context, PBConstant.PREF_NAME, 
                                    PBConstant.PREF_NAME_EXPIRES, expires.toString());
                        }
                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        rsData = responseHandler.handleResponse(response);                        
                    } else { 
                    	// Atik Need to show the message to user
                    	//statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    	/*HttpEntity entity = response.getEntity();
                    	String result = EntityUtils.toString(entity);*/
                    	
                    	
                    	String encoding = EntityUtils.getContentCharSet(response.getEntity());
                    	encoding = encoding == null ? "UTF-8" : encoding;
                    	InputStream stream = AndroidHttpClient.getUngzippedContent(response.getEntity());
                    	InputStreamEntity unzEntity = new InputStreamEntity(stream,-1);
                    	String result = EntityUtils.toString(unzEntity, encoding);
                    	System.out.println("Atik error response is "+result);
                    	
                        throw new IOException("wrong http status: " + statusCode);
                    }
                } catch (Exception e) {
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            } while (statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0);
            
            if(httpclient != null){
                httpclient.getConnectionManager().shutdown();
            }
        }

        return rsData;
    }
    
    private static String hmacSha1(String value, String key) {
        try {
            byte[] keyBytes = key.getBytes();           
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(value.getBytes());

            return new String(new Hex().encode(rawHmac), "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * <b>2. Upload media</b>
     * <p>Upload single media file to photobag server.</p>
     * <p>curl -s -D - "http://api.photobag.in/" -H "Cookie: token=b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420" --form "upload=@t/controller/02_upload_test.jpg"<br>
     * response {"photo":{"url":"http://photobag.in/photo/d73b4dc7f037bb8a50bd54d3bf0f6ab9b2170de2","id":"22"}}</p>
     * <br>
     * @param photo : Media's absolutely file path.
     * @param collectionId : Upload collection id.
     * @param token : User's token.
     * @param type : Media type ( 0: photo / 1: video )
     * @param listener : ProgressManagerListener used to update upload progress, null if not use.
     * @return response from server.
     */
    public static Response uploadMedia(String photo, String collectionId, String token, int type, ProgressManagerListener listener ){
        HttpUtils httpUtils = HttpUtils.getInstance();
        // 20120312 @lent support upload/download video #S
        if(httpUtils != null){
            if(type == PBDatabaseDefinition.MEDIA_PHOTO){
                return __uploadPhoto(httpUtils, photo, collectionId, token, listener);
            }else{
                return __uploadVideo(httpUtils, photo, collectionId, token, listener);
            }
        }
        // 20120312 @lent support upload/download video #E
        return new Response(ResponseHandle.CODE_HTTP_FAIL, ERROR_DESC);
    }
    
    private static Response __uploadPhoto(HttpUtils httpUtils, String path, String collectionId, String token, final ProgressManagerListener listener){
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;     

        if(httpUtils != null){
            int tryAgain = 3;
            totalSize = 1;
            
            // String fileUpload = PBBitmapUtils.compressBitmap(path);
            String fileUpload = path;
            if(!PBBitmapUtils.checkCompressedFile(fileUpload)){
                return new Response(ResponseHandle.CODE_FILE_COMPRESS_FAIL, "Compress file fail");
            }
            File file = new File( fileUpload );
            
            do{
                try {
                    // DEBUG
                    Log.d( "TSET", "FILE::" + file.exists( ) ); 

                    // MultipartEntity mpEntity  = new MultipartEntity( );
                    PBCustomMultiPartEntity mpEntity = new PBCustomMultiPartEntity(
                            new com.aircast.photobag.utils.PBCustomMultiPartEntity.ProgressListener() {
                                @Override
                                public void transferred(long num) {
                                    int percent = (int) ((num / (float) totalSize) * 100);
                                    if (listener != null) {
                                        listener.updateProgressStatus(percent);
                                    }
                                }

                                @Override
                                public boolean cancelUpload() {
                                    if (listener != null) {
                                        return listener.cancelDownloadTask();
                                    }
                                    return false;
                                }
                            });
                    ContentBody cbFile        = new FileBody( file, "image/png" );
                    
                    // ContentBody cbMessage     = new StringBody( "TEST TSET" );
                    ContentBody cbAccessToken = new StringBody( token );
                    mpEntity.addPart( "access_token", cbAccessToken );
                    mpEntity.addPart( "upload",       cbFile        );
                    mpEntity.addPart( "version", new StringBody(PBApplication.VERSION));
                    mpEntity.addPart( "lang", new StringBody(/*Locale.getDefault().getLanguage()*/"ja"));
                    
                    mpEntity.addPart("collection", new StringBody(collectionId));
                    
                    totalSize = mpEntity.getContentLength();
                    DefaultHttpClient  httpclient = (DefaultHttpClient)httpUtils.createHttpsClient();

                    HttpPost httppost = new HttpPost(PBAPIContant.API_UPLOAD_IMAGE_URL);
                    httppost.setHeader("Cookie", HttpUtils.createBasicCookiesString(token));
                    
                    httppost.setEntity( mpEntity );

                    // DEBUG
                    Log.d( "TEST", "executing request " + httppost.getRequestLine( ) );
                    HttpResponse response = httpclient.execute( httppost );
                    
                    // fix check null
                    if(response != null && response.getStatusLine() != null){
                        statusCode = response.getStatusLine().getStatusCode();
                    }

                    content = parseReponseBody(statusCode, response.getEntity());

                    httpclient.getConnectionManager( ).shutdown( );
                    return new Response(statusCode, content);              

                }catch (UnsupportedEncodingException e) {
                    content = "UnsupportedEncodingException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        
        }

        return new Response(statusCode, content);
    }

    private static long totalSize;
    
    private static Response __uploadVideo(HttpUtils httpUtils, String path, String collectionId, String token, final ProgressManagerListener listener){
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;     

        if(httpUtils != null){
            int tryAgain = 3;
            totalSize = 1;
            File file = new File( path );
            
            do{
                try {
                    // DEBUG
                    Log.d( "TSET", "FILE::" + file.exists( ) );
                    PBCustomMultiPartEntity mpEntity = new PBCustomMultiPartEntity(
                            new com.aircast.photobag.utils.PBCustomMultiPartEntity.ProgressListener() {
                                @Override
                                public void transferred(long num) {
                                    int percent = (int) ((num / (float) totalSize) * 100);
                                    if (listener != null) {
                                        listener.updateProgressStatus(percent);
                                    }
                                    // Log.i("mapp", ">>> upload video percent = " + percent);
                                }

                                @Override
                                public boolean cancelUpload() {
                                    if (listener != null) {
                                        return listener.cancelDownloadTask();
                                    }
                                    return false;
                                }
                            });
                    ContentBody cbFile        = new FileBody( file, "video/mpg" );
                    
                    // ContentBody cbMessage     = new StringBody( "TEST TSET" );
                    ContentBody cbAccessToken = new StringBody( token );
                    mpEntity.addPart( "access_token", cbAccessToken );
                    mpEntity.addPart( "upload",       cbFile        );
                    mpEntity.addPart( "version", new StringBody(PBApplication.VERSION));
                    mpEntity.addPart( "lang", new StringBody(/*Locale.getDefault().getLanguage()*/"ja"));
                    
                    mpEntity.addPart("collection", new StringBody(collectionId));

                    totalSize = mpEntity.getContentLength();
                    // response = mHttpUtils.doGet(PBAPIContant.API_FETCH_TOKEN_URL, null, null);       
                    DefaultHttpClient  httpclient = (DefaultHttpClient)httpUtils.createHttpsClient();

                    HttpPost httppost = new HttpPost(PBAPIContant.API_UPLOAD_VIDEO_URL);
                    httppost.setHeader("Cookie", HttpUtils.createBasicCookiesString(token));

                    httppost.setEntity( mpEntity );

                    // DEBUG
                    Log.d( "TEST", "executing request " + httppost.getRequestLine( ) );
                    HttpResponse response = httpclient.execute( httppost );
                    
                    // fix check null
                    if(response != null && response.getStatusLine() != null){
                        statusCode = response.getStatusLine().getStatusCode();
                    }

                    content = parseReponseBody(statusCode, response.getEntity());

                    httpclient.getConnectionManager( ).shutdown( );
                    return new Response(statusCode, content);              

                }catch (UnsupportedEncodingException e) {
                    content = "UnsupportedEncodingException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        
        }

        return new Response(statusCode, content);
    }

    /**
     * <b>3. Confirm password</b>
     * <p>Get more complex password from server.(ex. "password" -> "password_arkljip")</p>
     * <p>curl -s -D - "http://api.photobag.in/password/confirm" -d "password=pass" -H "Cookie: token=519214b3667ace0882cf107fa217dfae807ad216"<br>
     * response: {"longer_password":"your password_arkljip"}</p>
     * <br>
     * @param pwdToLonger : Password want to be changed.
     * @param collectionId : Upload collection id.
     * @param token : User's token.
     * @return response from server.
     */
    public static Response confirmPassword(String pwdToLonger, String collectionId, String token){
        if (TextUtils.isEmpty(pwdToLonger))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Password is empty!");
        if (TextUtils.isEmpty(collectionId))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "collectionId is empty!");
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;
        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null) return new Response(statusCode, content);
            
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("password", pwdToLonger));            

            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));

            int tryAgain = 3;
            do{
                try {
                	String url = String.format(PBAPIContant.API_CONFIRM_PASSWORD_URL, collectionId);
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, url, nameValuePairs, cookies);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    
                    
                    // shut down connection client
                    httpClient.getConnectionManager( ).shutdown( );
                    
                    return new Response(statusCode, content);
                    
                } catch (URISyntaxException e) {
                    content = "URISyntaxException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (HttpException e) {
                    content = "HttpException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        
            if(httpClient != null){
                httpClient.getConnectionManager().shutdown();
            }
        }

        return new Response(statusCode, content);
    }

    

    /**
     * <b>3. Confirm password</b>
     * <p>Get more complex password from server.(ex. "password" -> "password_arkljip")</p>
     * <p>curl -s -D - "http://api.photobag.in/password/confirm" -d "password=pass" -H "Cookie: token=519214b3667ace0882cf107fa217dfae807ad216"<br>
     * response: {"longer_password":"your password_arkljip"}</p>
     * <br>
     * @param pwdToLonger : Password want to be changed.
     * @param collectionId : Upload collection id.
     * @param token : User's token.
     * @return response from server.
     */
    public static Response confirmPasswordForUploadOnly(Context context, String pwdToLonger, String collectionId, String token){
        if (TextUtils.isEmpty(pwdToLonger))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Password is empty!");
        if (TextUtils.isEmpty(collectionId))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "collectionId is empty!");
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;
        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null) return new Response(statusCode, content);
            
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("password", pwdToLonger));            

            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));

            int tryAgain = 3;
            do{
                try {
                	String url = String.format(PBAPIContant.API_CONFIRM_PASSWORD_URL, collectionId);
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, url, nameValuePairs, cookies);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    
                   // content = parseReponseBody(statusCode, httpResponse.getEntity());
                    content = parseReponseBodyForPasswordErrorCheckOnly(context, statusCode, httpResponse.getEntity());
                    
                    // shut down connection client
                    httpClient.getConnectionManager( ).shutdown( );
                    
                    return new Response(statusCode, content);
                    
                } catch (URISyntaxException e) {
                    content = "URISyntaxException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (HttpException e) {
                    content = "HttpException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        
            if(httpClient != null){
                httpClient.getConnectionManager().shutdown();
            }
        }

        return new Response(statusCode, content);
    }
    
    /**
     * <b>4. Set password</b>
     * <p>Confirm password to server.</p>
     * <p>curl -s -D - "http://api.photobag.in/password" -d "password=pass" -H "Cookie: token=b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420"<br>
     * response: {"collection":{"password":"pass","id":"14"}}</p>
     * <br>
     * @param pwdToSet : Password needed to be confirmed.
     * @param collectionId : Upload collection id.
     * @param token : User's token.
     * @return response from server.
     */
    public static Response setPassword(String pwdToSet, String collectionId, String token,boolean isFourDigit,String fourDigit){
        if (TextUtils.isEmpty(pwdToSet))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Password is empty!");
        if (TextUtils.isEmpty(collectionId))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "collectionId is empty!");
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        
        

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;
        if(mHttpUtils != null){   
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null) return new Response(statusCode, content);
            
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("password", pwdToSet));           

            if(isFourDigit){
            	
            	   nameValuePairs.add(new BasicNameValuePair("id4", fourDigit));       
            }
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));
            int tryAgain = 3;
            do{
                try {
                	String url = String.format(PBAPIContant.API_SET_PASSWORD_URL, collectionId);
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, url, nameValuePairs, cookies);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    
                    // shut down connection client
                    httpClient.getConnectionManager( ).shutdown( );
                    
                    return new Response(statusCode, content);
                    
                } catch (URISyntaxException e) {
                    content = "URISyntaxException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (HttpException e) {
                    content = "HttpException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (IOException e) {                    
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        
            if(httpClient != null){
                httpClient.getConnectionManager().shutdown();
            }
        }
       
        return new Response(ResponseHandle.CODE_INVALID_PARAMS, content);
    }
    
    /**
     * <b>5. Post purchase result to photobag server</b>
     * <p>Post purchase date to server after user buy honey in shop, server will add user's honey count if success.<br>
     * * server will return <b>400</b> if same data post mutli times.</p>
     * <p>curl -s -D - "https://api.photobag.in/2/purchase" -d "platform=android" -d "inapp_signed_data=xxxxx" 
     * -d "inapp_signature=xxxxx" -H "Cookie: token=b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420"<br>
     * response: 200 {"honey_count":1, "maple_count":1, "acorn_count":29}</p>
     * <br>
     * @param signedData : signed data get from google play.
     * @param signature : signature get from google play.
     * @param token : User's token.
     * @return response from server.
     */
    public static Response postPurchaseDataToServer(String token, String signedData, String signature) {
    	if (TextUtils.isEmpty(signature) || TextUtils.isEmpty(signedData))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "SignedData or Signature is empty!");
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        
        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;
        if(mHttpUtils != null){   
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null) return new Response(statusCode, content);
            
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("inapp_signed_data", signedData));
            nameValuePairs.add(new BasicNameValuePair("inapp_signature", signature));
            nameValuePairs.add(new BasicNameValuePair("platform", "android"));

            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));
            int tryAgain = 3;
            do{
                try {
                    String url = PBAPIContant.API_POST_PURCHASE_URL;
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, url, nameValuePairs, cookies);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    
                    // shut down connection client
                    httpClient.getConnectionManager().shutdown();
                    
                    return new Response(statusCode, content);
                    
                } catch (Exception e) {
                    content = e.getMessage();
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } 
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        
            if(httpClient != null){
                httpClient.getConnectionManager().shutdown();
            }
        }
       
        return new Response(ResponseHandle.CODE_INVALID_PARAMS, content);
    }
    
    /**
     * <b>5.1 Bring back old purchase func</b>
     * <p>Post purchase date to server after user buy honey in shop, server will add user's honey count if success.<br>
     * * server will return <b>400</b> if same data post mutli times.</p>
     * <p>curl -s -D - "https://api.photobag.in/2/purchase" -d "platform=android" -d "inapp_signed_data=xxxxx" 
     * -d "inapp_signature=xxxxx" -H "Cookie: token=b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420"<br>
     * response: same with ConfirmPassword</p>
     * <br>
     * @param signedData : signed data get from google play.
     * @param signature : signature get from google play.
     * @param token : User's token.
     * @param password : password to download.
     * @return response from server.
     */
    public static Response downloadPhotoByPurchase(String token, String signedData, String signature, String password) {
    	if (TextUtils.isEmpty(signature) || TextUtils.isEmpty(signedData))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "SignedData or Signature is empty!");
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        
        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;
        if(mHttpUtils != null){   
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null) return new Response(statusCode, content);
            
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("inapp_signed_data", signedData));
            nameValuePairs.add(new BasicNameValuePair("inapp_signature", signature));
            nameValuePairs.add(new BasicNameValuePair("platform", "android"));

            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));
            cookies.add(new MapEntry<String, String>("password", encodingPasswordUTF8(password)));
            
            int tryAgain = 3;
            do{
                try {
                    String url = PBAPIContant.API_SIGNED_DATA_URL;
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, url, nameValuePairs, cookies);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    
                    // shut down connection client
                    httpClient.getConnectionManager().shutdown();
                    
                    return new Response(statusCode, content);
                    
                } catch (Exception e) {
                    content = e.getMessage();
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } 
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        
            if(httpClient != null){
                httpClient.getConnectionManager().shutdown();
            }
        }
       
        return new Response(ResponseHandle.CODE_INVALID_PARAMS, content);
    }

    /**
     * <b>6. Finish uploading</b>
     * <p>Tell server upload is finished and set addibility.<br>
     * <p>curl -s -D - "http://api.photobag.in/finish" -d "collection=1" -H "Cookie: token=b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420"<br>
     * response: {"collection":{"password":"pass","charges_at":1323473984,"id":"1"}}</p>
     * <br>
     * @param collectionId : Collection id.
     * @param addibility : Other user can add photo to this password.
     * @param token : User's token.
     * @return response from server.
     */
    public static Response finishUploading(String collectionId, String token, Boolean addibility){
    	if (TextUtils.isEmpty(collectionId))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "collectionId is empty!");
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_INVALID_PARAMS;
        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null) return new Response(statusCode, content);
            
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("can_add", addibility ? "1" : "0"));     

            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));
            int tryAgain = 3;
            do{            
                try {
                	String url = String.format(PBAPIContant.API_FINISH_UPLOAD_URL, collectionId);
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, url, nameValuePairs, cookies);
                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    HttpEntity entity = httpResponse.getEntity();

                    if (statusCode == HttpStatus.SC_OK) {
                        content = EntityUtils.toString(entity);
                    }
                    Log.w(PBConstant.TAG, content);
                    // shut down connection client
                    httpClient.getConnectionManager( ).shutdown( );

                    return new Response(statusCode, content);
                    
                } catch (URISyntaxException e) {
                    content = "URISyntaxException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (HttpException e) {
                    content = "HttpException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
            
            if(httpClient != null){
                httpClient.getConnectionManager().shutdown();
            }
        }

        return new Response(statusCode, content);
    }

    /**
     * <b>7. Cancel uploading</b>
     * <p>Cancel upload task.<br>
     * <p>curl -s -D - "http://api.photobag.in/cancel" -H "Cookie: token=b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420"<br>
     * response: {}</p>
     * <br>
     * @param collectionId : Collection id.
     * @param token : User's token.
     * @return response from server.
     */
    public static Response cancelUploading(String collectionId, String token){
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        if (TextUtils.isEmpty(collectionId))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "collectionId is empty! Please try again later!");

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_INVALID_PARAMS;

        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null) return new Response(statusCode, content);
            
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));
            int tryAgain = 3;
            do{
                try {
                	String url = String.format(PBAPIContant.API_CANCEL_UPLOAD_URL, collectionId);
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, url, null, cookies);
                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    HttpEntity entity = httpResponse.getEntity();

                    if (statusCode == HttpStatus.SC_OK) {
                        content = EntityUtils.toString(entity);
                    }
                    
                    // shut down connection client
                    httpClient.getConnectionManager( ).shutdown( );

                    return new Response(statusCode, content);
                    
                } catch (URISyntaxException e) {
                    content = "URISyntaxException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (HttpException e) {
                    content = "HttpException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        
            if(httpClient != null){
                httpClient.getConnectionManager().shutdown();
            }
        }

        return new Response(statusCode, content);
    }

    /**
     * <b>9. Check password.</b>
     * <p>Check password if is valid for download, this function will save password's photo information in 
     * SharedPreferences if the password is valuable.<br>
     * <p>curl -s -D - "http://api.photobag.in/?honey=0" -H "Cookie: token=b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420; password=pass"<br>
     * response: {"expires_at":1325083645,"charges_at":1323895645,"error_description":"payment required","error":"fetch list failed","downloaded_users_count":"1"}
     * {"photos":[{"url":"http://photobag.in/photo/488077922820336d06b7ca05ec5b3d6da18dbd61",
     * "thumb":"http://photobag.in/photo/488077922820336d06b7ca05ec5b3d6da18dbd61"}], "created_at":1323366768,"charges_at":1323453197,"id":"1"}</p>
     * <br>
     * @param pwdNeedToCheck : Password need to be checked.
     * @param token : User's token.
     * @param isUsingHoney : isUsingHoney set <b>true</b> in case validate with "honey user", otherwise.
     * @param confirmFlag : confirmFlag decide if need to add confirm message in cookie
     * @return response from server.
     */
    public static ResponseHandle.Response checkPhotoListInCollection(String pwdNeedToCheck,
            String token, boolean isUsingHoney, String confirmFlag,boolean isFourDigit,String fourDigit) {
    	
    	
        if (TextUtils.isEmpty(pwdNeedToCheck))
            return null;
        if (TextUtils.isEmpty(token))
            return null;
        
        
        Pattern pattern = Pattern.compile("\\s");

		Matcher matcher = pattern.matcher(pwdNeedToCheck);

		pwdNeedToCheck = matcher.find() ? pwdNeedToCheck.substring(0,pwdNeedToCheck.indexOf(' ')) : pwdNeedToCheck;
        
        HttpUtils mHttpUtils = HttpUtils.getInstance();
        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            try {
                ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
                cookies.add(new MapEntry<String, String>("token", token));
                cookies.add(new MapEntry<String, String>("password", encodingPasswordUTF8(pwdNeedToCheck)));
                
                ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
                if ("SAVE".equals(confirmFlag)) {
                	parameters.add(new BasicNameValuePair("confirmed_with", "accept_unsavable"));
                }
                if ("DELETE".equals(confirmFlag)) {
                	parameters.add(new BasicNameValuePair("confirmed_with", "accept_client_delete"));
                }
                
                if(isFourDigit){
                	parameters.add(new BasicNameValuePair("id4", fourDigit));
                }
                return mHttpUtils.doGetDownloadRequest(httpClient, 
                		isUsingHoney ? PBAPIContant.API_LIST_COLLECTION_URL_HONEY : PBAPIContant.API_LIST_COLLECTION_NO_HONEY_URL, 
                		cookies,
                		parameters,
                		pwdNeedToCheck, 
                		true);
                
            } catch (IOException e) {
            	Log.e("DOWNLOAD", e.getMessage());
                e.printStackTrace();
            } 
        }
        return null;
    }
    
    /**
     * <p>Download single photo to password's collection from given url.<br></p>
     * <br>
     * @param token : User's token.
     * @param photoUrl : Photo's url
     * @param pwd : password of photo collection.
     * @param isExternal : is save in external sdcard.
     * @param listenner : ProgressManagerListener used to update download progress, null if not used.
     * @return true if save successful.
     */
    public static boolean savePhoto(String token, String photoUrl, String pwd,
    		boolean isExternal, ProgressManagerListener listenner) throws IOException,
            SdcardException {
        if (TextUtils.isEmpty(token))
            throw new IllegalArgumentException("token empty");
        if (TextUtils.isEmpty(photoUrl))
        	throw new IllegalArgumentException("photo url empty");
        if (TextUtils.isEmpty(pwd))
        	throw new IllegalArgumentException("password empty");

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));

            // 20120229 mod by NhatVT, fix encoding issue with " " character <S> 
            // cookies.add(new MapEntry<String, String>("password", URLEncoder.encode(pwd, HTTP.UTF_8)));
            cookies.add(new MapEntry<String, String>("password", encodingPasswordUTF8(pwd)));
            // 20120229 mod by NhatVT, fix encoding issue with " " character <E> 

            // 20120220 mod by NhatVT, should release resources in this case <S>
            // return mHttpUtils.doDownloadPhoto(httpClient, photoUrl, cookies);
            boolean result = mHttpUtils.doDownloadPhoto(httpClient, photoUrl, cookies, isExternal, listenner);
            httpClient.getConnectionManager().shutdown();
            return result;
            // 20120220 mod by NhatVT, should release resources in this case <E>
        }
        return false;
    }
    
    private static String encodingPasswordUTF8(String sourceString) {
        if (TextUtils.isEmpty(sourceString)) {
            return sourceString;
        }
        try {
            sourceString = URLEncoder.encode(sourceString, HTTP.UTF_8);
            // revert " " again after encoding follow UTF-8
            sourceString = sourceString.replace("+", " ");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sourceString;
    }

    /**
     * <b>10. Delete upload collection.</b>
     * <p>Delete upload collection, this password will disappear from history and cannot be download anymore.<br>
     * * This function will <b>NOT</b> delete photo in local storage and database.</p>
     * <p>curl -s -D - "http://api.photobag.in/delete" -d "collection=1" -H "Cookie: token=b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420"<br>
     * response: {}</p>
     * <br>
     * @param collectionId : Collection's id.
     * @param token : User's token.
     * @return response from server.
     */
    public static Response deleteUploadedCollection(String collectionId, String token){
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if (mHttpUtils != null) {
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if (httpClient == null) {
                return new Response(statusCode , content);
            }
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String, String>>();
            cookies.add(new MapEntry<String, String>("token", token));

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("collection", collectionId));         
            int tryAgain = 3;
            do {
                try {
                	String url = String.format(PBAPIContant.API_DELETE_COLLECTION_URL, collectionId);
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, url, nameValuePairs, cookies);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }

                    content = parseReponseBody(statusCode, httpResponse.getEntity());

                    // shut down connection client
                    httpClient.getConnectionManager().shutdown();

                } catch (Exception e) {
                    content = "Exception";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            } while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        }

        return new Response(statusCode, content);
    }
    
    /**
     * <b>11. Delete photos from upload collection.</b>
     * <p>Delete parts of photo in upload collection, the collection and remain photos will still available for download.<br>
     * * This function will <b>NOT</b> delete photo in local storage and database.</p>
     * <p>curl -s -D - "https://api.photobag.in/2/collection/b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420/photos/delete" -d "photos=uid1,uid2,...,uidn" -H "Cookie: token=[token]<br>
     * response: {}</p>
     * <br>
     * @param collectionId : Collection's id.
     * @param token : User's token.
     * @param list : photo's uid list want to be deleted. 
     * @return response from server.
     */
    public static Response deletePartsCollection(String collectionId, String token, ArrayList<String> list){
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        if (list.isEmpty())
        	return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Selection is empty!");

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if (mHttpUtils != null) {
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if (httpClient == null) {
                return new Response(statusCode , content);
            }
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String, String>>();
            cookies.add(new MapEntry<String, String>("token", token));

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            
            String uidList = null;
            for (String photoUrl : list) {
            	try {
	            	if (TextUtils.isEmpty(uidList)) {
	            		uidList = photoUrl.substring(photoUrl.lastIndexOf("/") + 1);
	            	} 
	            	else {
	            		uidList += "," + photoUrl.substring(photoUrl.lastIndexOf("/") + 1);
	            	}
            	} catch (Exception e) { continue; }
            }
            
            nameValuePairs.add(new BasicNameValuePair("photos", uidList));         
            int tryAgain = 3;
            do {
                try {
                	String url = String.format(PBAPIContant.API_DELETE_PHOTOS_URL, collectionId);
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, url, nameValuePairs, cookies);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }

                    content = parseReponseBody(statusCode, httpResponse.getEntity());

                    // shut down connection client
                    httpClient.getConnectionManager().shutdown();

                } catch (Exception e) {
                    content = "Exception";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            } while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        }

        return new Response(statusCode, content);
    }
    
    /**
     * <b>12. Delete Download collection.</b>
     * <p>Delete download collection, this password will disappear from history.<br>
     * * This function will <b>NOT</b> delete photo in local storage and database.</p>
     * <p>curl -s -d "" - "https://api.photobag.in/2/collection/[collectionid]/download_history/delete" -H "Cookie: token=b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420"<br>
     * response: {}</p>
     * <br>
     * @param collectionId : Collection's id.
     * @param token : User's token.
     * @return response from server.
     */
    public static Response deleteDownloadedCollection(String collectionId, String token){
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if (mHttpUtils != null) {
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if (httpClient == null) {
                return new Response(statusCode , content);
            }
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String, String>>();
            cookies.add(new MapEntry<String, String>("token", token));

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("collection", collectionId));         
            int tryAgain = 3;
            do {
                try {
                	String url = String.format(PBAPIContant.API_DELETE_FROM_DOWNLOAD_HISTORY_URL, collectionId);
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, url, nameValuePairs, cookies);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }

                    content = parseReponseBody(statusCode, httpResponse.getEntity());

                    // shut down connection client
                    httpClient.getConnectionManager().shutdown();

                } catch (Exception e) {
                    content = "Exception";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            } while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        }

        return new Response(statusCode, content);
    }
    
    /**
     * <b>13. Set C2DM registration to server</b>
     * <p>Post C2DM registration information to server.</p>
     * <p>curl -s -D - "http://api.photobag.in/apns" -d "device_token=dddd" -H "Cookie: token=4ea028501b391a56a87d817ae8eddae31304dc3a"<br>
     * response: {}</p>
     * <br>
     * @param collectionId : Collection id.
     * @param registrationId : C2DM's register id.
     * @return response from server.
     */
    public static Response sendRegistrationIdToServer(Context context, String registrationId){
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;
        
        HttpClient client = new DefaultHttpClient();
        
        HttpPost post = new HttpPost(C2DMConstant.PN_C2DM_URL_REGISTER);         

        int tryAgain = 3;
        do{
            try{
                String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                // Get the deviceID
                nameValuePairs.add(new BasicNameValuePair("device_token", deviceId));
                // nameValuePairs.add(new BasicNameValuePair("registrationid",
                //      registrationId));

                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = client.execute(post);

                // fix check null
                if(httpResponse != null && httpResponse.getStatusLine() != null){
                    statusCode = httpResponse.getStatusLine().getStatusCode();
                }

                content = parseReponseBody(statusCode, httpResponse.getEntity());

                client.getConnectionManager().shutdown();
                
                Log.d("HttpResponse", ">>>> registratrion device id" + statusCode + " - " + content);
                return new Response(statusCode, content);
            } catch (IOException e) {
                content = "IOException";
                statusCode = ResponseHandle.CODE_HTTP_FAIL;
                e.printStackTrace();
            }
        }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        
        return new Response(statusCode, content);
    }

    /**
     * <b>14. Get user information from server.</b>
     * <p>Get latest user information from server, include honey, acorn count.</p>
     * <p>curl -s -D - "https://api.photobag.in/2/me" -H "Cookie: token=4ea028501b391a56a87d817ae8eddae31304dc3a"<br>
     * response: {"free_period":0,"invite_code":"gzztsq","invited_users":0,"uid":"0ea877a0bc9dddeee2b66d2038e11283dce8b5e2", ...}</p>
     * <br>
     * @param since_id : Dont know why this param set, always 0.
     * @param token : User's token.
     * @return response from server.
     */
    public static Response fetchMyFreePeriod(int since_id, String token){
        if (TextUtils.isEmpty(token)){
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        }
        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null){
                return new Response(statusCode, content);
            }

            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));

            int tryAgain = 3;
            do{
                try {
                    //HttpResponse httpResponse = mHttpUtils.doGet(httpClient, PBAPIContant.API_FREE_DOWNLOAD_TIME_URL, null, cookies); 
                    HttpResponse httpResponse = mHttpUtils.doGetForSignUpOnly(httpClient, PBAPIContant.API_FREE_DOWNLOAD_TIME_URL, null, cookies); // Atik comment out for security fix
                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    // shut down connection client

                } catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
            
            if(httpClient != null){
                httpClient.getConnectionManager( ).shutdown( );
            }
        }

        return new Response(statusCode, content);
    }

    /**
     * <b>15. Get user's history from server.</b>
     * <p>Get user's upload/download history information from server, include each collection's information.</p>
     * <p>curl -s -D - "http://api.photobag.in/2/history" -H "Cookie: token=4ea028501b391a56a87d817ae8eddae31304dc3a"<br>
     * response: {...}</p>
     * <br>
     * @param token : User's token.
     * @return response from server.
     */
    public static Response getHistory(String token){
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if (mHttpUtils != null) {
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String, String>>();
            cookies.add(new MapEntry<String, String>("token", token));
            
            int tryAgain = 3;
            do {
                try {
                    HttpResponse httpResponse = mHttpUtils.doGet(httpClient, PBAPIContant.API_HISTORY_INFO_URL, null, cookies);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }

                    content = parseReponseBody(statusCode, httpResponse.getEntity());

                    // shut down connection client
                    httpClient.getConnectionManager().shutdown();

                } catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            } while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        }

        return new Response(statusCode, content);
    }

    /**
     * <b>16. Get latest APP version.</b>
     * <p>Get if current version is latest version, response also contains acorn/gold acorn's exchange rate.</p>
     * <p>curl -s -D - "http://api.photobag.in/version?platform=android&version=1.0" -H "Cookie: token=4ea028501b391a56a87d817ae8eddae31304dc3a"<br>
     * response: {"newest":"1.00","url":"xxoo","message":"new version available", "items_for_exchange":{"maple":{"rate":30}}}}</p>
     * <br>
     * @param token : User's token.
     * @param version : APP's version.
     * @return response from server.
     */
    public static Response getVersionAPI(String token, String version) {
        if (TextUtils.isEmpty(token))
        	return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null) return new Response(statusCode, content);
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));
            int tryAgain = 3;
            do{
                try {
                	 // Log.e("lent5", PBAPIContant.API_VERSION_API_URL + "?" + PBApplication.getVersionParams());
                    HttpResponse httpResponse = mHttpUtils.doGet(httpClient, PBAPIContant.API_VERSION_API_URL , PBApplication.getVersionParams(), cookies);
                    
                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }

                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    // shut down connection client
                    httpClient.getConnectionManager( ).shutdown( );

                } catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail            
            
            if(httpClient != null){
                httpClient.getConnectionManager().shutdown();
            }
        }

        return new Response(statusCode, content);
    }

    /**
     * parse body from httpResponse
     * in case 4xx error response, parse the body content to get error desciption
     * @param statusCode error code reponse
     * @param entityReponse the Entity contain the body contain of HttpReponse
     * @return the content result after parsing
     */
    private static String parseReponseBody(int statusCode, HttpEntity entityReponse) throws ParseException, IOException{
        if(entityReponse == null) return "";
        
        String reponseBody = EntityUtils.toString(entityReponse);
        
        if(TextUtils.isEmpty(reponseBody)) return "";
        // parse error description in case error
        if (statusCode != HttpStatus.SC_OK && statusCode != ResponseHandle.CODE_404) {
            if(reponseBody.contains("error")){
                try {
                    JSONObject error = new JSONObject(reponseBody);
    
                    if(error != null){                          
                        if(error.has("error_description")){
                            reponseBody = error.getString("error_description");
                        }
                    }                       
                }catch (Exception e) {
                    // reponseBody = "JSONObject parsing";
                    e.printStackTrace();
                }
            }
        }

        return reponseBody;
    }
    
    /**
     * parse body from httpResponse
     * in case 4xx error response, parse the body content to get error desciption
     * @param statusCode error code reponse
     * @param entityReponse the Entity contain the body contain of HttpReponse
     * @return the content result after parsing
     */
    private static String parseReponseBodyForPasswordErrorCheckOnly(Context context, int statusCode, HttpEntity entityReponse) throws ParseException, IOException{
        if(entityReponse == null) return "";
        
        String reponseBody = EntityUtils.toString(entityReponse);
        
        if(TextUtils.isEmpty(reponseBody)) return "";
        // parse error description in case error
        if (statusCode != HttpStatus.SC_OK && statusCode != ResponseHandle.CODE_404) {
            if(reponseBody.contains("error")){
                try {
                    JSONObject error = new JSONObject(reponseBody);
    
                    if(error != null){   
                    	// Invalid character Exists
                        if(error.has("invalid_characters")){
                            //reponseBody = error.getString("invalid_characters");
							PBPreferenceUtils.saveBoolPref(context,
									PBConstant.PREF_NAME, PBConstant.PREF_INVALID_CHARACTER_EXISTS,
									true);
						    PBPreferenceUtils.saveStringPref(
						    		context,
									PBConstant.PREF_NAME,
									PBConstant.PREF_INVALID_CHARACTER,
									error.getString("invalid_characters"));
                        }
                        
                        if(error.has("error_description")){
                            reponseBody = error.getString("error_description");
                        }
                    }                       
                }catch (Exception e) {
                    // reponseBody = "JSONObject parsing";
                    e.printStackTrace();
                }
            }
        }

        return reponseBody;
    }
    
    /**
     * <b>17. Public password.</b>
     * <p>Set password to public, deprecated as this function not used anymore.</p>
     * <p>curl -s -D - "https://api.photobag.in/2/collections/recommend" -H "Cookie: token=4ea028501b391a56a87d817ae8eddae31304dc3a"<br>
     * response: {...}</p>
     * <br>
     * @param token : User's token.
     * @param pwdIDToPublic : Password want to public.
     * @return response from server.
     */
    @Deprecated
    public static Response publicPassword(String pwdIDToPublic, String token){
        if (TextUtils.isEmpty(pwdIDToPublic))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Password is empty!");
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;
        if(mHttpUtils != null){   
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null) return new Response(statusCode, content);
            
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("collection", pwdIDToPublic));

            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));
            int tryAgain = 3;
            do{
                try {
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, PBAPIContant.API_PUBLIC_PASSWORD_URL, nameValuePairs, cookies);
                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    
                    content = parseReponseBody(statusCode, httpResponse.getEntity());

                    // shut down connection client
                    httpClient.getConnectionManager( ).shutdown( );
                    
                    return new Response(statusCode, content);

                } catch (Exception e) {                    
                    content = "Exception";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        
            if(httpClient != null){
                httpClient.getConnectionManager().shutdown();
            }
        }
       
        return new Response(ResponseHandle.CODE_INVALID_PARAMS, content);
    }
    
    /**
     * <b>18. Start uploading.</b>
     * <p>Start uploading for new collection.</p>
     * <p>curl -s -D - "https://api.photobag.in/2/collections/new" -d "" -H "Cookie: token=4ea028501b391a56a87d817ae8eddae31304dc3a"<br>
     * response: {"collection":"697da219a487a1869db373246c4d3cdcb4248c0d"}</p>
     * <br>
     * @param token : User's token.
     * @return response from server.
     */
    public static Response startUploading(String token){
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null) return new Response(statusCode, content);
            
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));
            
            int tryAgain = 3;
            do{
                try {
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, PBAPIContant.API_START_UPLOAD_URL, null, cookies);
                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    HttpEntity entity = httpResponse.getEntity();

                    if (statusCode == HttpStatus.SC_OK) {
                        content = EntityUtils.toString(entity);
                    }
                    
                    // shut down connection client
                    httpClient.getConnectionManager( ).shutdown( );

                    return new Response(statusCode, content);
                    
                } catch (Exception e) {
                    content = "Exception";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        
            if(httpClient != null){
                httpClient.getConnectionManager().shutdown();
            }
        }

        return new Response(statusCode, content);
    }
    
    /**
     * <b>19. Start adding.</b>
     * <p>Start uploading for exist collection.</p>
     * <p>curl -s -D - "https://api.photobag.in/2/collection/697da219a487a1869db373246c4d3cdcb4248c0d/start_adding" 
     * -d "password=pass" -H "Cookie: token=b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420"<br>
     * response: {"collection":"697da219a487a1869db373246c4d3cdcb4248c0d"}</p>
     * <br>
     * @param token : User's token.
     * @param collectionId : Collection Id.
     * @param password : Password want to add.
     * @return response from server.
     */
    public static Response startAdding(String token, String collectionId, String password){
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        if (TextUtils.isEmpty(collectionId))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "collectionId is empty!");

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null) return new Response(statusCode, content);
            
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("password", password));
            
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));
            
            int tryAgain = 3;
            do{
                try {
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, 
                    		String.format(PBAPIContant.API_START_ADD_URL, collectionId), nameValuePairs, cookies);
                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    HttpEntity entity = httpResponse.getEntity();

                    if (statusCode == HttpStatus.SC_OK) {
                        content = EntityUtils.toString(entity);
                    }
                    
                    // shut down connection client
                    httpClient.getConnectionManager().shutdown();

                    return new Response(statusCode, content);
                    
                } catch (Exception e) {
                    content = "Exception";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        
            if(httpClient != null){
                httpClient.getConnectionManager().shutdown();
            }
        }

        return new Response(statusCode, content);
    }
    
    
    private static String encodeURL(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    /**
     * <b>20. Get PC URL.</b>
     * <p>Get an url that allowed upload from PC.</p>
     * <p>curl -s -d "" -D - "https://api.photobag.in/2/pc/signin" -H "Cookie: token=xxxx"<br>
     * response: {"url":"http://up.photobag.in/signin?pc_token=xxxx"}</p>
     * <br>
     * @param token : User's token.
     * @param collectionId : Collection Id.
     * @param password : Password want to add.
     * @return response from server.
     */
    public static Response getPCSigninURL(String token){
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if (mHttpUtils != null) {
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String, String>>();
            cookies.add(new MapEntry<String, String>("token", token));
            
            int tryAgain = 3;
            do {
                try {
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, PBAPIContant.API_PC_SIGNIN_URL, null, cookies);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }

                    content = parseReponseBody(statusCode, httpResponse.getEntity());

                    // shut down connection client
                    httpClient.getConnectionManager().shutdown();

                } catch (Exception e) {                    
                    content = "Exception";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            } while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        }

        return new Response(statusCode, content);
    }
    
    /**
     * <b>21. Exchange acorns.</b>
     * <p>Exchange acorns to honey, post exchange message to server.</p>
     * <p>curl -v "https://api.photobag.in/2/exchange" -d "acorn_count=30&item=maple&item_count=1" 
     * -H "Cookie: token=b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420"<br>
     * response: {"acorn_count":30,"item":"maple","item_count":1}</p>
     * <br>
     * @param token : User's token.
     * @param acornCount : Acorn's count for exchange.
     * @param itemCount : Maple's count want to exchange.
     * @param isGold : is using gold acorn.
     * @return response from server.
     */
    public static Response exchangeMaple(String token, int acornCount, int itemCount, boolean isGold){
        if (TextUtils.isEmpty(token)){
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        }
        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null){
                return new Response(statusCode, content);
            }

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            if (isGold) {
            	nameValuePairs.add(new BasicNameValuePair("goldacorn_count", "" + acornCount)); 
            } else {
            	nameValuePairs.add(new BasicNameValuePair("acorn_count", "" + acornCount)); 
            }
            nameValuePairs.add(new BasicNameValuePair("item", "maple")); 
            nameValuePairs.add(new BasicNameValuePair("item_count", "" + itemCount)); 
            
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));

            int tryAgain = 3;
            do{
                try {

                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, PBAPIContant.API_POST_EXCHANGE_DONGURI_TO_ITEM, nameValuePairs, cookies);
                    
                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    // shut down connection client

                } catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                	content = "URISyntaxException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
				} catch (HttpException e) {
					content = "HttpException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
				}
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
            
            if(httpClient != null){
                httpClient.getConnectionManager( ).shutdown( );
            }
        }

        return new Response(statusCode, content);
    }
    
    /**
     * <b>22. Get notification history.</b>
     * <p>Get honey / acorn notification history from server.</p>
     * <p>curl -s -D - "https://api.photobag.in/2/point_history?rows=20&older_than=321&lang=ja" 
     * -H "Cookie: token=519214b3667ace0882cf107fa217dfae807ad215"<br>
     * response: {"now":1323939273,"histories":[{"id":320,"created_at":1323929273,"type":"acorn","description":"123。"},
     * {"id":318,"created_at":1323926273,"type":"honey","description":"456。"}]}</p>
     * <br>
     * @param rows : Notification's max number, must less than 180.
     * @param lastId : Id less than this wont be returned.
     * @param language : another useless param.
     * @param token : User's token.
     * @return response from server.
     */
    public static Response getTimelineHistory(int rows, int lastId, String language, String token){
        if (TextUtils.isEmpty(token)){
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        }
        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null){
                return new Response(statusCode, content);
            }

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            String params = "";
            if(rows > 0)
            	nameValuePairs.add(new BasicNameValuePair("rows",""+rows));
            if(lastId >= 0)
            	nameValuePairs.add(new BasicNameValuePair("older_than", ""+lastId)); 
            if(!TextUtils.isEmpty(language))
            	nameValuePairs.add(new BasicNameValuePair("lang", /*language*/"ja")); 
           
            for(int i = 0;i<nameValuePairs.size();i++){
            	if(i == 0){
            		params+=nameValuePairs.get(i).getName()+"="+nameValuePairs.get(i).getValue();
            	}else{
            		params+="&"+nameValuePairs.get(i).getName()+"="+nameValuePairs.get(i).getValue();
            	}
            }
            
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));

            int tryAgain = 3;
            do{
                try {
                	params = encodeURL(params);
                	
                    HttpResponse httpResponse = mHttpUtils.doGet(httpClient, PBAPIContant.API_GET_POINT_HISTORY, params, cookies);
                    
                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    // shut down connection client

                } catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }

            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
            
            if(httpClient != null){
                httpClient.getConnectionManager( ).shutdown( );
            }
        }

        return new Response(statusCode, content);
    }
    
    /**
     * <b>23. Update charge time.</b>
     * <p>Change collection's free download time.<br>
     * * new charge time must <b>LESS</b> than current charge time.</p>
     * <p>curl -s -D - "https://api.photobag.in/2/collection/697da219a487a1869db373246c4d3cdcb4248c0d" -d "charges_at=1325083645"
     *  -H "Cookie: token=b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420"<br>
     * response: {...}</p>
     * <br>
     * @param collectionId : Collection Id.
     * @param chargesAtTime : New charge time.
     * @param token : User's token.
     * @return response from server.
     */
    public static Response updateChargesTime(String collectionId, long chargesAtTime, String token){
        if (TextUtils.isEmpty(token)){
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        }
        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null){
                return new Response(statusCode, content);
            }

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("charges_at", ""+chargesAtTime));
            
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));

            int tryAgain = 3;
            do{
                try {

                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient,String.format(PBAPIContant.API_UPDATE_CHARGES_TIME, collectionId), nameValuePairs, cookies);
                    
                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    // shut down connection client

                } catch (Exception e) {
					content = "Exception";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
				}
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
            
            if(httpClient != null){
                httpClient.getConnectionManager( ).shutdown( );
            }
        }

        return new Response(statusCode, content);
    }
    
    /**
     * <b>24. Get AD Achievements.</b>
     * <p>Get all available ad achievements from server.</p>
     * <p>curl -s -D - "https://api.photobag.in/2/ad/achievements" -H "Cookie: token=xxxx"<br>
     * response: {"achievements":{"d50cb1f164b5b2d44520fcaeae4ecff971be1f56":{"scheme":"dummy3://"}}}</p>
     * <br>
     * @param token : User's token.
     * @return response from server.
     */
    public static Response getAdAchievements(String token){
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if (mHttpUtils != null) {
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String, String>>();
            cookies.add(new MapEntry<String, String>("token", token));
            
            int tryAgain = 3;
            do {
                try {
                    HttpResponse httpResponse = mHttpUtils.doGet(httpClient, PBAPIContant.API_ACHIEVEMENTS_GET, null, cookies);

                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }

                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    httpClient.getConnectionManager().shutdown();

                } catch (Exception e) {                    
                    content = "Exception";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            } while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        }

        return new Response(statusCode, content);
    }
    
    /**
     * <b>25. Post AD Achievements.</b>
     * <p>Post installed achievements list to server.<br>
     * <p>curl -s -D - "https://api.photobag.in/2/ad/achievements/made" <br>
     * -d "achievements=ddc8bfbcb92d91bef2cdd6602e5b400e076987f2,6e8957d6920a4b6d66f71ef8d402ebf935cdfdd1"<br>
     * -H "Cookie: token=b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420"<br>
     * response: {...}</p>
     * <br>
     * @param list : list of reward app that have installed.
     * @param token : User's token.
     * @return response from server.
     */
    public static Response postAdAchievements(ArrayList<String> list, String token){
        if (TextUtils.isEmpty(token)){
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        }
        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null){
                return new Response(statusCode, content);
            }

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            String appList = "";
            for (String appUid : list) {
            	appList = TextUtils.isEmpty(appList) ? appUid : appList + "," + appUid;
            }
            nameValuePairs.add(new BasicNameValuePair("achievements", appList));
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));

            int tryAgain = 3;
            do{
                try {

                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, PBAPIContant.API_ACHIEVEMENTS_POST, nameValuePairs, cookies);
                    
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                } catch (Exception e) {
					content = "Exception";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
				}
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
            
            if(httpClient != null){
                httpClient.getConnectionManager( ).shutdown( );
            }
        }

        return new Response(statusCode, content);
    }
    
    /**
     * <b>26.  Download photo list from server for corresponding password.</b>
     * <p>curl -s -D - "https://"+API_HOST+"/2/migration/verify" -H   "migration_code=cdatPabzfz" "Cookie: token=4ea028501b391a56a87d817ae8eddae31304dc3a"<br>
     * response: {"message":"引き継ぎコードを確認しました。","result_code":0,"honey_count":0,"acorn_count":0,"maple_count":0,"goldacorn_count":0, ...}</p>
     * <br>
     * @param token : User's token.
     * @param migration code : Received code for  migration
     * @return response from server.
     */
    public static Response doDownloadUploadedPhotoListJsonOfPassword(String token, String pwdNeedToCheck, boolean isUsingHoney){
        if (TextUtils.isEmpty(token)){
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        }
        
        if (TextUtils.isEmpty(pwdNeedToCheck)){
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Migration code is empty! Please try again later!");
        }
        
        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null){
                return new Response(statusCode, content);
            }

            /*List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("migration_code", migrationCode));*/
            
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));
            cookies.add(new MapEntry<String, String>("password", encodingPasswordUTF8(pwdNeedToCheck)));

            int tryAgain = 3;
            do{
                try {
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, isUsingHoney ? PBAPIContant.API_LIST_COLLECTION_URL_HONEY : PBAPIContant.API_LIST_COLLECTION_NO_HONEY_URL,
                    		null, cookies);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    // shut down connection client

                } catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
            
            if(httpClient != null){
                httpClient.getConnectionManager( ).shutdown( );
            }
        }

        return new Response(statusCode, content);
    }
    
    
    /**
     * <b>26. Create Migration code by server.</b>
     * <p>curl -s -D - "https://"+API_HOST+"/2/migration" -H "Cookie: token=4ea028501b391a56a87d817ae8eddae31304dc3a"<br>
     * response: {migration code if response code 200 }</p>
     * <br>
     * @param token : User's token.
     * @return response from server.
     */
    public static Response getMigrationCode(String token){
        if (TextUtils.isEmpty(token)){
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        }
        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null){
                return new Response(statusCode, content);
            }

            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));

            int tryAgain = 3;
            do{
                try {
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, PBAPIContant.API_MIGRATION_CODE_CREATION, null, cookies);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    // shut down connection client

                } catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
            
            if(httpClient != null){
                httpClient.getConnectionManager( ).shutdown( );
            }
        }

        return new Response(statusCode, content);
    }
    
    /**
     * <b>27.  Migration code verification from server.</b>
     * <p>curl -s -D - "https://"+API_HOST+"/2/migration/verify" -H   "migration_code=cdatPabzfz" "Cookie: token=4ea028501b391a56a87d817ae8eddae31304dc3a"<br>
     * response: {"message":"引き継ぎコードを確認しました。","result_code":0,"honey_count":0,"acorn_count":0,"maple_count":0,"goldacorn_count":0, ...}</p>
     * <br>
     * @param token : User's token.
     * @param migration code : Received code for  migration
     * @return response from server.
     */
    public static Response migrationCodeVerification(String token, String migrationCode){
        if (TextUtils.isEmpty(token)){
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        }
        
        if (TextUtils.isEmpty(migrationCode)){
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Migration code is empty! Please try again later!");
        }
        
        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null){
                return new Response(statusCode, content);
            }

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("migration_code", migrationCode));
            
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));

            int tryAgain = 3;
            do{
                try {
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, PBAPIContant.API_MIGRATION_CODE_VERIFICATION, nameValuePairs, cookies);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    // shut down connection client

                } catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
            
            if(httpClient != null){
                httpClient.getConnectionManager( ).shutdown( );
            }
        }

        return new Response(statusCode, content);
    }
    
    /**
     * <b>28.  Start data migration in Server</b>
     * <p>curl -s -D - "https://"+API_HOST+"/2/migration/veried" -H  "migration_code=FYzHhnXkSN" "Cookie: token=4ea028501b391a56a87d817ae8eddae31304dc3a" <br>
     * response: {"message":"引き継ぎを完了しました。","result_code":0}</p>
     * <br>
     * @param token : User's token.
     * @param migration code : Received code for  migration
     * @return response from server.
     */
    public static Response startDataMigration(String token, String migrationCode){
        if (TextUtils.isEmpty(token)){
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        }
        
        if (TextUtils.isEmpty(migrationCode)){
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Migration code is empty! Please try again later!");
        }
        
        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null){
                return new Response(statusCode, content);
            }

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("migration_code", migrationCode));
            
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));

            int tryAgain = 3;
            do{
                try {
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, PBAPIContant.API_MIGRATION_CODE_VERIFIED, nameValuePairs, cookies);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    // shut down connection client

                } catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
            
            if(httpClient != null){
                httpClient.getConnectionManager( ).shutdown( );
            }
        }

        return new Response(statusCode, content);
    }
    
    /**
     * <b>29.  Check lock status API for device change</b>
     * <p>curl -s -D - "https://"+API_HOST+"/2/info_migration" -H  "migration_code=FYzHhnXkSN" "Cookie: token=4ea028501b391a56a87d817ae8eddae31304dc3a" <br>
     * response: {"message":"引き継ぎを完了しました。","result_code":0}</p>
     * <br>
     * @param token : User's token.
     * @param migration code : Received code for data  migration
     * @return response from server.
     */
    public static Response checkDeviceLockForDeviceChange( String migrationCode){

        
        if (TextUtils.isEmpty(migrationCode)){
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Migration code is empty! Please try again later!");
        }
        
        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null){
                return new Response(statusCode, content);
            }

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("migration_code", migrationCode));


            int tryAgain = 3;
            do{
                try {
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, PBAPIContant.API_MIGRATION_DEVICE_LOCK_STATUS, 
                    		nameValuePairs, null);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    // shut down connection client

                } catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
            
            if(httpClient != null){
                httpClient.getConnectionManager( ).shutdown( );
            }
        }

        return new Response(statusCode, content);
    }
    
    /**
     * <b>30.  Get thumbnail list from the server of specific password</b>
     * <p>curl -s -D - "https://"+API_HOST+"/2/photo_thumbnails" -H   "Cookie: token=4ea028501b391a56a87d817ae8eddae31304dc3a"<br>
     * response: {"message":"引き継ぎコードを確認しました。","result_code":0,"honey_count":0,"acorn_count":0,"maple_count":0,"goldacorn_count":0, ...}</p>
     * <br>
     * @param token : User's token.
     * @param password  :  Password name
     * @return response from server.
     */
    public static Response getThumbnailFromServerForPurchasePassword(String token, String password){
        if (TextUtils.isEmpty(token)){
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");
        }
        
        if (TextUtils.isEmpty(password)){
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Password is empty! Please try again later!");
        }

        
        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;

        if(mHttpUtils != null){
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null){
                return new Response(statusCode, content);
            }

            /*List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("password", password));*/
            
            ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));
            cookies.add(new MapEntry<String, String>("password", encodingPasswordUTF8(password)));

            
            int tryAgain = 3;
            do{
                try {
                    HttpResponse httpResponse = mHttpUtils.doPost(httpClient, PBAPIContant.PB_GET_THUMBNAIL_ARRAY_FOR_PASSWORD, null, cookies);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    // shut down connection client

                } catch (IOException e) {
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
            
            if(httpClient != null){
                httpClient.getConnectionManager( ).shutdown( );
            }
        }

        return new Response(statusCode, content);
    }
    
    /**
	 * <b>31. nick name registration for group chat.</b>
	 * 
	 * @param token
	 *            : User's token.
	 * @param name
	 *            : User nick name
	 * 
	 * @param uuid
	 *            : device uuid
	 * @param platfrom
	 *            : Android
	 * @param device
	 *            : device manufacture name
	 * 
	 * @return response from server.
	 */
	public static Response nickNameRegistration(String token, String name,
			String uuid, String platfrom, String device) {
		if (TextUtils.isEmpty(token)) {
			return new Response(ResponseHandle.CODE_INVALID_PARAMS,
					"Token is empty! Please try again later!");
		}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = ResponseHandle.CODE_HTTP_FAIL;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("platform", platfrom));
			nameValuePairs.add(new BasicNameValuePair("device_info", device));
			nameValuePairs.add(new BasicNameValuePair("uid", uuid));
			
			
			ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String, String>>();
			cookies.add(new MapEntry<String, String>("token", token));

			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							PBConstant.CHAT_USER_ADD, nameValuePairs, cookies);

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = ResponseHandle.CODE_HTTP_FAIL;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode == ResponseHandle.CODE_HTTP_FAIL
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}
	
	

	/**
	 * <b>32. group status for group chat.</b>
	 * 
	 * @param token
	 *            : User's token.
	 * @param uuid
	 *            : device uuid
	 * @param groupName
	 *            : Group Name
	 * @return response from server.
	 */
	public static Response groupStatus(String token,
			String uuid, String groupName) {
		if (TextUtils.isEmpty(token)) {
			return new Response(ResponseHandle.CODE_INVALID_PARAMS,
					"Token is empty! Please try again later!");
		}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = ResponseHandle.CODE_HTTP_FAIL;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			nameValuePairs.add(new BasicNameValuePair("chatroom_name", groupName));
			
		//	nameValuePairs.add(new BasicNameValuePair("group_name", "SmartMux"));
			
			
			
			
			ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String, String>>();
			cookies.add(new MapEntry<String, String>("token", token));

			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							PBConstant.CHAT_GROUP_STATUS, nameValuePairs,cookies);
					
					

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					
					
					
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = ResponseHandle.CODE_HTTP_FAIL;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode == ResponseHandle.CODE_HTTP_FAIL
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}
	
	
	/**
	 * <b>33. send message for group chat.</b>
	 * 
	 * @param token
	 *            : User's token.
	 * @param uuid
	 *            : device uuid
	 * @param groupName
	 *            : Group Name
	 * @param message
	 *            : message
	 * @return response from server.
	 */
	public static Response sendMessage(String token,
			String uuid, String groupName,String message) {
		if (TextUtils.isEmpty(token)) {
			return new Response(ResponseHandle.CODE_INVALID_PARAMS,
					"Token is empty! Please try again later!");
		}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = ResponseHandle.CODE_HTTP_FAIL;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			 
			//nameValuePairs.add(new BasicNameValuePair("uid", "1410e70dd720e3278b47ba96eb36e7f8c17c7009"));
			//nameValuePairs.add(new BasicNameValuePair("group_name", "SmartMux"));
			
			nameValuePairs.add(new BasicNameValuePair("uid", uuid));
			nameValuePairs.add(new BasicNameValuePair("chatroom_name", groupName));
			nameValuePairs.add(new BasicNameValuePair("message", message));
			
			ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String, String>>();
			cookies.add(new MapEntry<String, String>("token", token));

			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							PBConstant.CHAT_SEND_GROUP_MGS, nameValuePairs,cookies);
					
					

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = ResponseHandle.CODE_HTTP_FAIL;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode == ResponseHandle.CODE_HTTP_FAIL
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}
	
	
	/**
	 * <b>34. get all message for group chat.</b>
	 * 
	 * @param token
	 *            : User's token.
	 * @param uuid
	 *            : device uuid
	 * @param groupName
	 *            : Group Name
	 * @param last_mgs_count
	 *            : count of messages which were show on ui
	 * @return response from server.
	 */
	public static Response getAllMessage(String token,
			String uuid, String groupName,int last_mgs_count) {
		if (TextUtils.isEmpty(token)) {
			return new Response(ResponseHandle.CODE_INVALID_PARAMS,
					"Token is empty! Please try again later!");
		}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = ResponseHandle.CODE_HTTP_FAIL;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			//nameValuePairs.add(new BasicNameValuePair("uid", "1410e70dd720e3278b47ba96eb36e7f8c17c7009"));
			//nameValuePairs.add(new BasicNameValuePair("group_name", "SmartMux"));
			nameValuePairs.add(new BasicNameValuePair("uid", uuid));
		    nameValuePairs.add(new BasicNameValuePair("chatroom_name", groupName));
			nameValuePairs.add(new BasicNameValuePair("last_message_count", String.valueOf(last_mgs_count)));
			
			
			ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String, String>>();
			cookies.add(new MapEntry<String, String>("token", token));

			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							PBConstant.CHAT_GROUP_MGS_LIST, nameValuePairs,cookies);
					
					

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = ResponseHandle.CODE_HTTP_FAIL;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode == ResponseHandle.CODE_HTTP_FAIL
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}
	
	
	   /**
     * <b>35. Set password to public</b>
     * <p>Confirm password to server.</p>
     * <p>curl -s -D - "http://api.photobag.in/password" -d "password=pass" -H "Cookie: token=b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420"<br>
     * response: {"collection":{"password":"pass","id":"14"}}</p>
     * <br>
     * @param pwdToSet : Password needed to be confirmed.
     * @param collectionId : Upload collection id.
     * @param token : User's token.
     * @return response from server.
     */
    public static Response setPasswordToPublic(String pwdToSet, String collectionId, String token){
        if (TextUtils.isEmpty(pwdToSet))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Password is empty!");
        /*if (TextUtils.isEmpty(collectionId))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "collectionId is empty!");
        if (TextUtils.isEmpty(token))
            return new Response(ResponseHandle.CODE_INVALID_PARAMS, "Token is empty! Please try again later!");*/

        HttpUtils mHttpUtils = HttpUtils.getInstance();
        String content = ERROR_DESC;
        int statusCode = ResponseHandle.CODE_HTTP_FAIL;
        if(mHttpUtils != null){   
            HttpClient httpClient = mHttpUtils.createHttpsClient();
            
            if(httpClient == null) return new Response(statusCode, content);
            
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("password", pwdToSet));    
//            nameValuePairs.add(new BasicNameValuePair("share_type", share_type));  
//            nameValuePairs.add(new BasicNameValuePair("share_id", share_id));  

           /* ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String,String>>() ;
            cookies.add(new MapEntry<String, String>("token", token));*/
            int tryAgain = 3;
            do{
                try {
                	String url = String.format(PBAPIContant.API_SET_PUBLISH_PASSWORD_URL, collectionId);
                    HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient, url, nameValuePairs, null);

                    // fix check null
                    if(httpResponse != null && httpResponse.getStatusLine() != null){
                        statusCode = httpResponse.getStatusLine().getStatusCode();
                    }
                    
                    content = parseReponseBody(statusCode, httpResponse.getEntity());
                    
                    // shut down connection client
                    httpClient.getConnectionManager( ).shutdown( );
                    
                    return new Response(statusCode, content);
                    
                } catch (URISyntaxException e) {
                    content = "URISyntaxException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (HttpException e) {
                    content = "HttpException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                } catch (IOException e) {                    
                    content = "IOException";
                    statusCode = ResponseHandle.CODE_HTTP_FAIL;
                    e.printStackTrace();
                }
            }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
        
            if(httpClient != null){
                httpClient.getConnectionManager().shutdown();
            }
        }
       
        return new Response(ResponseHandle.CODE_INVALID_PARAMS, content);
    }
    
	   /**
  * <b>35. Set password to public</b>
  * <p>Confirm password to server.</p>
  * <p>curl -s -D - "http://api.photobag.in/password" -d "password=pass" -H "Cookie: token=b1ecd7d8ae0dcf744ae975b23ff9a39ad163f420"<br>
  * response: {"collection":{"password":"pass","id":"14"}}</p>
  * <br>
  * @param pwdToSet : Password needed to be confirmed.
  * @param collectionId : Upload collection id.
  * @param token : User's token.
  * @return response from server.
  */
 public static Response getStatusKoukaiBukuro(){


     HttpUtils mHttpUtils = HttpUtils.getInstance();
     String content = ERROR_DESC;
     int statusCode = ResponseHandle.CODE_HTTP_FAIL;
     if(mHttpUtils != null){   
         HttpClient httpClient = mHttpUtils.createHttpsClient();
         
         if(httpClient == null) return new Response(statusCode, content);
         

         int tryAgain = 3;
         do{
             try {
             	 String url = String.format(PBAPIContant.API_GET_STATUS_PUBLISH_PASSWORD_URL);
                 HttpResponse httpResponse = mHttpUtils.doPostForKoukaibukuroStatus(httpClient, url, null, null);

                 // fix check null
                 if(httpResponse != null && httpResponse.getStatusLine() != null){
                     statusCode = httpResponse.getStatusLine().getStatusCode();
                 }
                 
                 content = parseReponseBody(statusCode, httpResponse.getEntity());
                 
                 // shut down connection client
                 httpClient.getConnectionManager( ).shutdown( );
                 
                 return new Response(statusCode, content);
                 
             } catch (URISyntaxException e) {
                 content = "URISyntaxException";
                 statusCode = ResponseHandle.CODE_HTTP_FAIL;
                 e.printStackTrace();
             } catch (HttpException e) {
                 content = "HttpException";
                 statusCode = ResponseHandle.CODE_HTTP_FAIL;
                 e.printStackTrace();
             } catch (IOException e) {                    
                 content = "IOException";
                 statusCode = ResponseHandle.CODE_HTTP_FAIL;
                 e.printStackTrace();
             }
         }while(statusCode == ResponseHandle.CODE_HTTP_FAIL && (--tryAgain) > 0); // try 3 time if connection fail
     
         if(httpClient != null){
             httpClient.getConnectionManager().shutdown();
         }
     }
    
     return new Response(ResponseHandle.CODE_INVALID_PARAMS, content);
 }
 
 
 /**
	 * <b>36. get honey shop info for in-app billing.</b>
	 * 
	 * @param token
	 *            : User's token.
	 * @param uuid
	 *            : device uuid
	 * @return response from server.
	 */
	public static Response getHoneyShopInfo(String uuid) {
//		if (TextUtils.isEmpty(token)) {
//			return new Response(ResponseHandle.CODE_INVALID_PARAMS,
//					"Token is empty! Please try again later!");
//		}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = ResponseHandle.CODE_HTTP_FAIL;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			nameValuePairs.add(new BasicNameValuePair("uid", uuid));
			nameValuePairs.add(new BasicNameValuePair("platform", "android"));
			
			ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String, String>>();

			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							PBAPIContant.PB_HONEY_PURCHASE_URL, nameValuePairs,cookies);
					
					

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = ResponseHandle.CODE_HTTP_FAIL;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode == ResponseHandle.CODE_HTTP_FAIL
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}
	
	/**
	 * @param token
	 *            : User's token.
	 * @param uuid
	 *            : device uuid
	 * @param chatroom_name
	 *            : Group Name
	 * @param last_mgs_count
	 *            : count of messages which were show on ui
	 * @return response from server.
	 */
	public static Response getUnreadMessageCount(String token,
			String uuid, String chatroom_name,int last_message_count) {
		if (TextUtils.isEmpty(token)) {
			return new Response(ResponseHandle.CODE_INVALID_PARAMS,
					"Token is empty! Please try again later!");
		}

		HttpUtils mHttpUtils = HttpUtils.getInstance();
		String content = ERROR_DESC;
		int statusCode = ResponseHandle.CODE_HTTP_FAIL;

		if (mHttpUtils != null) {
			HttpClient httpClient = mHttpUtils.createHttpsClient();

			if (httpClient == null) {
				return new Response(statusCode, content);
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			nameValuePairs.add(new BasicNameValuePair("uid", uuid));
		    nameValuePairs.add(new BasicNameValuePair("chatroom_name", chatroom_name));
			nameValuePairs.add(new BasicNameValuePair("last_message_count", String.valueOf(last_message_count)));
			
			
			ArrayList<MapEntry<String, String>> cookies = new ArrayList<MapEntry<String, String>>();
			cookies.add(new MapEntry<String, String>("token", token));

			int tryAgain = 3;
			do {
				try {
					HttpResponse httpResponse = mHttpUtils.doPostForChat(httpClient,
							PBAPIContant.PB_UNREAD_MESSAGE_COUNT, nameValuePairs,cookies);
					
					

					// fix check null
					if (httpResponse != null
							&& httpResponse.getStatusLine() != null) {
						statusCode = httpResponse.getStatusLine()
								.getStatusCode();
					}
					content = parseReponseBody(statusCode,
							httpResponse.getEntity());
					// shut down connection client

				} catch (IOException e) {
					content = "IOException";
					statusCode = ResponseHandle.CODE_HTTP_FAIL;
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (HttpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (statusCode == ResponseHandle.CODE_HTTP_FAIL
					&& (--tryAgain) > 0); // try 3 time if connection fail

			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}

		return new Response(statusCode, content);
	}
    
}
