package com.aircast.photobag.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.aircast.photobag.api.PBAPIHelper.ProgressManagerListener;
import com.aircast.photobag.application.PBApplication;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.log.SdcardException;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.utils.PBPreferenceUtils;

public class HttpUtils {
	private static final String TAG = "HttpConnectionUtils";
	/** the context */
	private Context mContext;

	public static final int STATUS_RUNNING = 0x1;
	public static final int STATUS_ERROR = 0x2;
	public static final int STATUS_FINISHED = 0x3;

	// private static final int SECOND_IN_MILLIS = (int)
	// DateUtils.SECOND_IN_MILLIS;
	// private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	// private static final String ENCODING_GZIP = "gzip";

	/** the instance HttpUtils */
	private static HttpUtils instance = new HttpUtils();
	// private DefaultHttpClient client;
	/** the object handle Reponse */
	private ResponseHandler<String> responseHandler;

	// private ObjectMapper mapper;

	/** COntructor HttpUtils */
	private HttpUtils() {
		super();
		if (mContext == null) {
			mContext = PBApplication.getBaseApplicationContext();
		}

		// client = new DefaultHttpClient();
		responseHandler = new BasicResponseHandler();
		// mapper = new ObjectMapper(); // can reuse, share globally
	}

	/**
	 * return response string body content from server
	 * 
	 * @param response
	 * @return
	 */
	public String responseProcess(HttpResponse response) {
		if (responseHandler == null) {
			responseHandler = new BasicResponseHandler();
		}

		try {
			return responseHandler.handleResponse(response);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * create HttpClient connection support to get/post data to server
	 * 
	 * @return
	 */
	public HttpClient createHttpClient30s() {
		HttpParams params = new BasicHttpParams();
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));

		// 20120316 added by NhatVT, support https connection <S>
		SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
		registry.register(new Scheme("https", sslSocketFactory, 443));
		registry.register(new Scheme("https", sslSocketFactory, 80));
		// 20120316 added by NhatVT, support https connection <E>

		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
				registry);

		HttpClient client = new DefaultHttpClient(cm, params);
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 30000);

		// client.getParams().setCookiePolicy(CookiePolicy.RFC_2109);

		return client;
	}

	/**
	 * create HttpClient connection support to get/post data to server
	 * 
	 * @return
	 */
	public HttpClient createHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));

		SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
		registry.register(new Scheme("https", sslSocketFactory, 443));
		registry.register(new Scheme("https", sslSocketFactory, 80));

		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
				registry);

		HttpClient client = new DefaultHttpClient(cm, params);
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 180000);
		// client.getParams().setCookiePolicy(CookiePolicy.RFC_2109);

		return client;
	}

	// 20120625 @Bac create new httpClient
	public HttpClient createHttpsClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	/** get instance HttpUtils */
	public static HttpUtils getInstance() {
		if (instance == null) {
			instance = new HttpUtils();
		}
		return instance;
	}

	/**
	 * create url with add query version parameter for every request POST/GET
	 * 
	 * @author @le.nguyen TODO
	 * @param @param url
	 * @param @param query
	 * @param @return
	 */
	private String createUrl(String url, String query) {
		String urlRs = url;
		// fix check null
		if (TextUtils.isEmpty(urlRs))
			return url;

		String version = PBApplication.getVersionParams();
		if (!urlRs.endsWith("?") && !urlRs.contains("?")) {
			urlRs += "?";
		}

		if (urlRs.endsWith("?")) {
			urlRs += version;
		} else {
			urlRs += "&" + version;
		}

		if (!TextUtils.isEmpty(query)) {
			if (!TextUtils.isEmpty(version)) {
				urlRs += "&";
			}

			urlRs += query;
		}
		// Log.e("lent5", "url -- " + urlRs );
		return urlRs;
	}

	// private URI createURI(String path, String query) throws
	// URISyntaxException {
	// return URIUtils.createURI("http", PBAPIContant.SERVER_IP, SERVER_PORT,
	// "rest/" + path, query, null);
	// }

	/**
	 * implement http client with POST method without setting cookies should
	 * shut down paramter httpClient after using
	 * 
	 * @param url
	 * @param POSTJson
	 *            parameter tot post server
	 * @param cookies
	 *            could be null
	 * @throws URISyntaxException
	 *             , HttpException, IOException
	 */
	public String doPostRequest(HttpClient httpClient, final String url,
			List<NameValuePair> parameter) throws URISyntaxException,
			HttpException, IOException {

		return doPostRequest(httpClient, url, parameter, null);
	}

	/**
	 * implement http client with POST method should shut down paramter
	 * httpClient after using
	 * 
	 * @param url
	 * @param parameter
	 *            data to post server
	 * @param cookies
	 *            could be null
	 * @return response result String from server
	 * @throws URISyntaxException
	 *             , HttpException, IOException
	 */
	public String doPostRequest(HttpClient httpClient, final String url,
			List<NameValuePair> parameter,
			List<MapEntry<String, String>> cookies) throws URISyntaxException,
			HttpException, IOException {
		HttpResponse response = doPost(httpClient, url, parameter, cookies);
		int statusCode = response.getStatusLine().getStatusCode();
		// fix check null
		if (response != null && response.getStatusLine() != null
				&& statusCode == HttpStatus.SC_OK) {
			return responseHandler.handleResponse(response);
		} else {
			throw new IOException("wrong http status: " + statusCode);
		}
	}

	public static String createBasicCookiesString(String token) {
		String cookies = "";
		if (!TextUtils.isEmpty(token)) {
			cookies = "token=" + token;
		}
		return cookies;
	}

	public static List<NameValuePair> createBasicParams() {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters
				.add(new BasicNameValuePair("version", PBApplication.VERSION));
		parameters.add(new BasicNameValuePair("lang", "ja"/*Locale.getDefault()
				.getLanguage()*/));
		return parameters;
	}

	/**
	 * implement http client with POST method should shut down paramter
	 * httpClient after using should shut down paramter httpClient after using
	 * 
	 * @param url
	 * @param POSTJson
	 *            parameter tot post server
	 * @param cookies
	 *            could be null
	 * @return response result String from server
	 * @throws URISyntaxException
	 *             , HttpException, IOException
	 */
	public HttpResponse doPost(HttpClient httpClient, final String url,
			List<NameValuePair> parameters,
			List<MapEntry<String, String>> cookies) throws URISyntaxException,
			HttpException, IOException {
		HttpClient client = httpClient;
		if (client == null) {
			client = createHttpClient();
		}

		HttpPost httpPost = new HttpPost(createUrl(url, null));
		Log.d("AGUNG", "URL :" +url);
		// set paramter
		if (parameters == null) {
			parameters = new ArrayList<NameValuePair>();
		}
		parameters.add(new BasicNameValuePair("version", PBApplication.VERSION));
		boolean hasLangParam = false;
		for (NameValuePair paramItem : parameters) {
			if(paramItem.getName().toLowerCase().equals("lang")){
				hasLangParam = true;
				break;
			}
		}
		
		if(!hasLangParam)
			parameters.add(new BasicNameValuePair("lang",  "ja"));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters,
				HTTP.UTF_8);
		for (NameValuePair nameValuePair : parameters) {
			Log.d("AGUNG", "param "+nameValuePair.getName() + " -> "+nameValuePair.getValue());
		}
		httpPost.setEntity(entity);
		httpPost.addHeader("Content-Encoding", HTTP.UTF_8);

		if (cookies != null) {
			// set cookies
			String strCookie = "";
			for (MapEntry<String, String> entry : cookies) {
				if (!TextUtils.isEmpty(strCookie))
					strCookie += ";";
				strCookie += entry.toString();
			}
			httpPost.setHeader("Cookie", strCookie);
			Log.d("AGUNG", "Cookie: "+strCookie);
		}

		// set user-agent
		httpPost.setHeader("User-Agent", PBApplication.getUserAgentParams());

		HttpResponse response = client.execute(httpPost);
		return response;
	}
	
	
	/**
	 * implement http client with POST method should shut down paramter
	 * httpClient after using should shut down paramter httpClient after using
	 * 
	 * @param url
	 * @param POSTJson
	 *            parameter tot post server
	 * @param cookies
	 *            could be null
	 * @return response result String from server
	 * @throws URISyntaxException
	 *             , HttpException, IOException
	 */
	public HttpResponse doPostForChat(HttpClient httpClient, final String url,
			List<NameValuePair> parameters,
			List<MapEntry<String, String>> cookies) throws URISyntaxException,
			HttpException, IOException {
		HttpClient client = httpClient;
		if (client == null) {
			client = createHttpClient();
		}

		HttpPost httpPost = new HttpPost(createUrl(url, null));
		Log.d("AGUNG", "URL :" +url);
		// set paramter
		if (parameters == null) {
			parameters = new ArrayList<NameValuePair>();
		}
		parameters.add(new BasicNameValuePair("version", PBApplication.VERSION));
		boolean hasLangParam = false;
		for (NameValuePair paramItem : parameters) {
			if(paramItem.getName().toLowerCase().equals("lang")){
				hasLangParam = true;
				break;
			}
		}
		
		if(!hasLangParam)
			parameters.add(new BasicNameValuePair("lang",  "ja"));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters,
				HTTP.UTF_8);
		for (NameValuePair nameValuePair : parameters) {
			Log.d("AGUNG", "param "+nameValuePair.getName() + " -> "+nameValuePair.getValue());
		}
		httpPost.setEntity(entity);
		httpPost.addHeader("Content-Encoding", HTTP.UTF_8);

		if (cookies != null) {
			// set cookies
			String strCookie = "";
			for (MapEntry<String, String> entry : cookies) {
				if (!TextUtils.isEmpty(strCookie))
					strCookie += ";";
				strCookie += entry.toString();
			}
			httpPost.setHeader("Cookie", strCookie);
			Log.d("AGUNG", "Cookie: "+strCookie);
		}

		// set user-agent
		httpPost.setHeader("User-Agent", PBApplication.getUserAgentParams());
		httpPost.setHeader("X-Appid", "photobag");
		
		HttpResponse response = client.execute(httpPost);
		return response;
	}
	
	
	/**
	 * implement http client with POST method should shut down paramter
	 * httpClient after using should shut down paramter httpClient after using
	 * 
	 * @param url
	 * @param POSTJson
	 *            parameter tot post server
	 * @param cookies
	 *            could be null
	 * @return response result String from server
	 * @throws URISyntaxException
	 *             , HttpException, IOException
	 */
	public HttpResponse doPostForKoukaibukuroStatus(HttpClient httpClient, final String url,
			List<NameValuePair> parameters,
			List<MapEntry<String, String>> cookies) throws URISyntaxException,
			HttpException, IOException {
		HttpClient client = httpClient;
		if (client == null) {
			client = createHttpClient();
		}

		HttpPost httpPost = new HttpPost(createUrl(url, null));
		Log.d("AGUNG", "URL :" +url);
		// set paramter
		if (parameters == null) {
			parameters = new ArrayList<NameValuePair>();
		}
		parameters.add(new BasicNameValuePair("version", PBApplication.TARGET_VERSION));
		//parameters.add(new BasicNameValuePair("target_version", PBApplication.TARGET_VERSION));
		parameters.add(new BasicNameValuePair("category", "koukaibukoro"));
		parameters.add(new BasicNameValuePair("platform", "android"));
		
		boolean hasLangParam = false;
		for (NameValuePair paramItem : parameters) {
			if(paramItem.getName().toLowerCase().equals("lang")){
				hasLangParam = true;
				break;
			}
		}
		
		if(!hasLangParam)
			parameters.add(new BasicNameValuePair("lang",  "ja"));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters,
				HTTP.UTF_8);
		for (NameValuePair nameValuePair : parameters) {
			Log.d("AGUNG", "param "+nameValuePair.getName() + " -> "+nameValuePair.getValue());
		}
		httpPost.setEntity(entity);
		httpPost.addHeader("Content-Encoding", HTTP.UTF_8);

		if (cookies != null) {
			// set cookies
			String strCookie = "";
			for (MapEntry<String, String> entry : cookies) {
				if (!TextUtils.isEmpty(strCookie))
					strCookie += ";";
				strCookie += entry.toString();
			}
			httpPost.setHeader("Cookie", strCookie);
			Log.d("AGUNG", "Cookie: "+strCookie);
		}

		// set user-agent
		httpPost.setHeader("User-Agent", PBApplication.getUserAgentParams());
		httpPost.setHeader("X-Appid", "photobag");
		
		HttpResponse response = client.execute(httpPost);
		return response;
	}

	/**
	 * implement http client with GET method has cookie should shut down
	 * paramter httpClient after using
	 * 
	 * @param url
	 * @param query
	 * @param cookies
	 *            could be null
	 * @return response result string from server
	 * @throws IOException
	 */
	public String doGetRequest(HttpClient httpClient, String url, String query,
			List<MapEntry<String, String>> cookies) throws IOException {
		try {

			HttpResponse response = doGet(httpClient, url, query, cookies);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return responseHandler.handleResponse(response);
			} else {
				// test code
				/*
				 * if(response != null){ Header[] msgs =
				 * response.getAllHeaders(); for(Header header : msgs){
				 * Log.e("Header", header.getName() + " : " +
				 * header.getValue()); } }
				 */
				throw new IOException("wrong http status: " + statusCode);
			}
			/*
			 * } catch (URISyntaxException e) { throw new
			 * IOException("uri syntax error");
			 */} catch (ClientProtocolException e) {
			throw new IOException("protocol error");
		}
	}

	/**
	 * implement http client with GET method without setting cookies, and query
	 * parameter should shut down paramter httpClient after using
	 * 
	 * @param url
	 * @return response result string from server
	 * @throws IOException
	 */
	public String doGetRequest(HttpClient httpClient, String url)
			throws IOException {
		return doGetRequest(httpClient, url, null, null);
	}

	/**
	 * implement http client with GET method without setting query parameter
	 * should shut down paramter httpClient after using
	 * 
	 * @param url
	 * @return response result string from server
	 * @throws IOException
	 */
	public String doGetRequest(HttpClient httpClient, String url,
			List<MapEntry<String, String>> cookies) throws IOException {
		return doGetRequest(httpClient, url, null, cookies);
	}

	/**
	 * implement http client with GET method should shut down paramter
	 * httpClient after using
	 * 
	 * @param url
	 * @param query
	 *            could be null
	 * @param cookies
	 *            could be null
	 * @return response HttpReponse from server
	 * @throws IOException
	 */
	public HttpResponse doGet(HttpClient httpClient, String url, String query,
			List<MapEntry<String, String>> cookies) throws IOException {
		HttpClient client = httpClient;
		if (client == null) {
			client = createHttpClient();
		}
		String urlComplete = createUrl(url, query);
		HttpGet httpGet = new HttpGet(urlComplete);
		// set header cookie
		if (cookies != null) {
			String strCookie = "";
			for (MapEntry<String, String> entry : cookies) {
				if (!TextUtils.isEmpty(strCookie))
					strCookie += ";";
				strCookie += entry.toString();
			}
			httpGet.setHeader("Cookie", strCookie);
			httpGet.addHeader("Content-Encoding", HTTP.UTF_8);
		} else {
			Log.i(TAG, "Null session request get()");
		}
		// set user-agent
		httpGet.setHeader("User-Agent", PBApplication.getUserAgentParams());
		HttpResponse response = client.execute(httpGet);

		// shut down connection client
		// client.getConnectionManager( ).shutdown( );

		return response;
	}
	
	
	/**
	 * Atik get request for Signup only
	 * 
	 * implement http client with GET method should shut down parameter
	 * httpClient after using
	 * 
	 * @param url
	 * @param query
	 *            could be null
	 * @param cookies
	 *            could be null
	 * @return response HttpReponse from server
	 * @throws IOException
	 */
	public HttpResponse doGetForSignUpOnly(HttpClient httpClient, String url, String query,
			List<MapEntry<String, String>> cookies) throws IOException {
		HttpClient client = httpClient;
		if (client == null) {
			client = createHttpClient();
		}
		String urlComplete = createUrl(url, query);
		HttpGet httpGet = new HttpGet(urlComplete);
		// set header cookie
		if (cookies != null) {
			String strCookie = "";
			for (MapEntry<String, String> entry : cookies) {
				if (!TextUtils.isEmpty(strCookie))
					strCookie += ";";
				strCookie += entry.toString();
			}
			httpGet.setHeader("Cookie", strCookie);
			httpGet.addHeader("Content-Encoding", HTTP.UTF_8);
		} else {
			Log.i(TAG, "Null session request get()");
		}
		// set user-agent
		httpGet.setHeader("User-Agent", PBApplication.getUserAgentParams());
		
		// Atik set parameter for device
	    String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		String deviceName = "";
		if (model.startsWith(manufacturer)) {
			deviceName = model.toUpperCase();
		} else {
			deviceName = manufacturer.toUpperCase() + " "
					+ model.toUpperCase();
		}
          
		System.out.println("Atik device name is during registration me:"+deviceName);
        // Atik added new parameters for security strengthen
		if(!deviceName.isEmpty()) {
			httpGet.getParams().setParameter("device", deviceName);
		}  else {
			httpGet.getParams().setParameter("device", "");
		}  

		
		//Atik execute the get request
		HttpResponse response = client.execute(httpGet);

		// shut down connection client
		// client.getConnectionManager( ).shutdown( );

		return response;
	}

	/************************************************************************************
	 *********** functions support request/parsing data for downloading **********
	 ************************************************************************************/
	/**
	 * implement http client with GET method has cookie for download screen.
	 * 
	 * @param url
	 * @param cookies
	 *            could be null
	 * @return response result string from server
	 * @throws IOException
	 */
	public ResponseHandle.Response doGetDownloadRequest(HttpClient httpClient,
			String url, List<MapEntry<String, String>> cookies,
			List<NameValuePair> param,
			String password, boolean isPostMethod) throws IOException {
		if (TextUtils.isEmpty(url))
			return null;
		if (TextUtils.isEmpty(password))
			return null;

		// saving password
		PBPreferenceUtils.saveStringPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_DL_PHOTOLIST_PASS,
				password);

		String JSONline = "";
		try {
			HttpResponse response = null;
			if (isPostMethod) {
				try {
					response = doPost(httpClient, url, param, cookies);
				} catch (URISyntaxException e) {
					e.printStackTrace();
					return new ResponseHandle.Response(
							ResponseHandle.CODE_HTTP_FAIL, "URISyntaxException");
				} catch (HttpException e) {
					e.printStackTrace();
					return new ResponseHandle.Response(
							ResponseHandle.CODE_HTTP_FAIL, "HttpException");
				} catch (IOException e) {
					e.printStackTrace();
					return new ResponseHandle.Response(
							ResponseHandle.CODE_HTTP_FAIL, "IOException");
				}
			} else {
				response = doGet(httpClient, url, null, cookies);
			}

			int statusCode = response.getStatusLine().getStatusCode();
			JSONline = EntityUtils.toString(response.getEntity());
			switch (statusCode) {
			case ResponseHandle.CODE_200_OK:
				// TODO need to parse data info and start download photo
				Log.d(TAG, "JSON " + JSONline.toString());

				storeLocalTmpDownloadData(password, JSONline);

				// parsePhotoCollectionInfo(JSONline);
				return new ResponseHandle.Response(ResponseHandle.CODE_200_OK,
						"");

			case ResponseHandle.CODE_404:
				// "No collection found with that password!";
				return new ResponseHandle.Response(ResponseHandle.CODE_404,
				/*
				 * PBApplication.getBaseApplicationContext().getString(com.kayac.
				 * photobag.R.string.dl_input_pwd_error_404)
				 */
				"No collection found with that password!");
				
			case ResponseHandle.CODE_403:	
//				Log.d(TAG, "JSON " + JSONline.toString());
//				if(JSONline.equals("not enough")) {
//					return new ResponseHandle.Response(statusCode,
//							JSONline);
//            	}
				System.out.println(JSONline);
				return new ResponseHandle.Response(statusCode,
						getErrorDescriptionFromJSON(JSONline));
				
			case ResponseHandle.CODE_406:
				return new ResponseHandle.Response(statusCode,
						getErrorDescriptionFromJSON(JSONline));

			case ResponseHandle.CODE_400:
				// saving photo list pass
				PBPreferenceUtils.saveStringPref(
						PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_PURCHASE_INFO_PASSWORD, password);
				// saving json data
				PBPreferenceUtils.saveStringPref(
						PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_PURCHASE_INFO_JSON_DATA, JSONline);
				return new ResponseHandle.Response(ResponseHandle.CODE_400,
						getErrorDescriptionFromJSON(JSONline));

			default:
				// JSONline = EntityUtils.toString(response.getEntity());
				return new ResponseHandle.Response(
						ResponseHandle.CODE_INVALID_PARAMS,
						getErrorDescriptionFromJSON(JSONline));
			}

		} catch (Exception e) {
			throw new IOException("protocol error");
		}
	}

	public static void storeLocalTmpDownloadData(String passwd,
			String reponseSuccess) {
		// saving password
		PBPreferenceUtils
				.saveStringPref(PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME,
						PBConstant.PREF_DL_PHOTOLIST_PASS, passwd);
		// saving json data
		PBPreferenceUtils.saveStringPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, PBConstant.PREF_DL_PHOTOLIST_JSON_DATA,
				reponseSuccess);
		
		
		
		// saving init
		PBPreferenceUtils.saveIntPref(
				PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME,
				PBConstant.PREF_DL_PHOTOLIST_DOWNLOAD_STATE, 0);
	}

	/**
	 * Util for downloading photo and save it to memory.
	 * 
	 * @param url photo url.
	 * @param cookies the cookies section.
	 * @param isExternal is save in external sdcard.
	 * @param listenner listen downloading progress.
	 * @return <b>true</b> if saving successful.
	 * @throws IOException
	 * @throws SdcardException
	 */
	public boolean doDownloadPhoto(HttpClient httpClient, String url,
			List<MapEntry<String, String>> cookies,
			boolean isExternal,	ProgressManagerListener listenner) throws IOException,
			SdcardException {
		try {

			File iconFile = null;
			File dir = new File(PBGeneralUtils.getCacheFolderPath(isExternal));
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			String fileName = isExternal
					? String.valueOf(url.hashCode())
					: String.valueOf((url + "?can_save=0").hashCode());

			// 20120420 added by NhatVT, support video function <S>
			// iconFile = new File(dir, String.valueOf(url.hashCode())); // save
			// with md5 code from url
			// FIXME maybe we need to check this condition in future when
			// changing API
			if (url.contains("/video") && !url.contains("?width")) {
				iconFile = new File(dir, fileName);
				if (iconFile.exists()) {
					if (listenner != null) {
						listenner.updateProgressStatus(1);
					}
					if (PBGeneralUtils.checkVideoIsValid(iconFile
							.getAbsolutePath())) {
						// Log.i("mapp",
						// ">>> cached video file is OK, re-used old file!");
						// should update progress bar in this case
						if (listenner != null) {
							listenner.updateProgressStatus(100);
						}
						return true;
					} else {
						iconFile.delete(); // delete corrupt file!
					}
				}
			} else {
				iconFile = new File(dir, fileName);
				// 20120220 check the valid of image file <S>
				if (iconFile.exists()) {
					if (listenner != null) {
						listenner.updateProgressStatus(1);
					}
					if (!com.aircast.photobag.utils.PBBitmapUtils
							.isPhotoValid(iconFile.getAbsolutePath())) {
						iconFile.delete();
					} else {
						// Log.i("mapp",
						// ">>> cached photo file is OK, re-used old file!");
						// should update progress bar in this case
						if (listenner != null) {
							listenner.updateProgressStatus(100);
						}
						return true;
					}
				}
				// 20120220 check the valid of image file <E>
			}

			long t1 = System.currentTimeMillis();
			HttpResponse response = doGet(httpClient, url, null, cookies);
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {

				// 20120420 added by NhatVT, support video function <E>

				HttpEntity mHttpEntity = response.getEntity();
				if (mHttpEntity == null) {
					return false;
				}
				// Log.d("mapp", ">>> file content lenght=" +
				// mHttpEntity.getContentLength());
				// InputStream inputStream = null;
				// inputStream = mHttpEntity.getContent();
				FileOutputStream fos = null;

				if (android.os.Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED)) {
					fos = new FileOutputStream(iconFile);
				} else {
					// 20120306 mod by NhatVT, support throw exception when
					// sdcard not exist, <S>
					/*
					 * // save on internal memory if sdcard is invalid. fos =
					 * PBApplication
					 * .getBaseApplicationContext().openFileOutput(String
					 * .valueOf(url.hashCode()), 0); // in case we want get
					 * image that stored in internal memory just call like this
					 * // BitmapFactory.decodeFile(context.getFilesDir()+ "/" +
					 * iconName);
					 */
					throw new SdcardException();
					// 20120306 mod by NhatVT, support throw exception when
					// sdcard not exist, <E>
				}

				// change to using EntityUtils for getting image from server!
				// long tStart = System.currentTimeMillis();
				// need to split data when saving response from server to avoid
				// memory issue.
				boolean result = getEntityToByteArray(mHttpEntity, listenner,
						fos);

				if (fos != null) {
					fos.flush();
					fos.close();
					fos = null;
				}
				Log.d(TAG,
						"saving photo from server cost:"
								+ (System.currentTimeMillis() - t1));
				return result;
			}
		} catch (ClientProtocolException e) {
			throw new IOException("protocol error :"+e.getMessage());
			
		}
		return false;
	}

	/**
	 * Method for getting data from server and convert it to byte array.
	 * 
	 * @param entity
	 *            the HttpEntity want to get from.
	 * @param listenner
	 *            listen downloading progress.
	 * @param fos
	 *            FileOutputStream for writing data to sdcard.
	 * @return true if we can save file to sdcard.
	 * @throws IOException
	 */
	private boolean getEntityToByteArray(final HttpEntity entity,
			ProgressManagerListener listenner, FileOutputStream fos)
			throws IOException {
		if (entity == null) {
			throw new IllegalArgumentException("HTTP entity may not be null");
		}

		if (fos == null) {
			return false;
		}

		// number of bytes downloaded
		double downloaded = 0;

		InputStream instream = entity.getContent();
		if (instream == null) {
			return false;
		}
		if (entity.getContentLength() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"HTTP entity too large to be buffered in memory");
		}
		int fileSize = (int) entity.getContentLength();
		if (fileSize < 0) {
			fileSize = 4096;
		}
		// ByteArrayBuffer buffer = new ByteArrayBuffer(fileSize);
		try {
			byte[] tmp = new byte[4096];// 8192
			int byteRead;
			while ((byteRead = instream.read(tmp)) != -1) {
				downloaded += byteRead;
				// Log.d("mapp", "--------downloaded: " + ((int) ((downloaded /
				// fileSize) * 100)));
				if (listenner != null) {
					listenner
							.updateProgressStatus((int) ((downloaded / fileSize) * 100));
					if (listenner.cancelDownloadTask()) {
						Log.w("mapp", ">>> CANCEL downloading task!");
						break;
					}
				}
				if (fos != null) {
					fos.write(tmp, 0, byteRead);
				}
				// buffer.append(tmp, 0, byteRead);
			}
		} catch (NullPointerException e) {
			Log.e(PBConstant.TAG, ">>> get entity ERROR >>>> " + e.toString());
			return false;
		} catch (IndexOutOfBoundsException e) {
			Log.e(PBConstant.TAG, ">>> get entity ERROR >>>> " + e.toString());
			return false;
		} catch (IOException e) {
			Log.e(PBConstant.TAG, ">>> get entity ERROR >>>> " + e.toString());
			return false;
		} catch (Exception e) {
			Log.e(PBConstant.TAG, ">>> get entity ERROR >>>> " + e.toString());
			return false;
		} finally {
			if (instream != null) {
				instream.close();
			}
			if (fos != null) {
				fos.flush();
				fos.close();
				fos = null;
			}
		}
		return true;
	}

	/**
	 * parse data from JSON data for getting "error_description" tag info.
	 * 
	 * @param JSONstring
	 *            JSON string want to parse.
	 * @return
	 */
	private String getErrorDescriptionFromJSON(String JSONstring) {
		if (TextUtils.isEmpty(JSONstring))
			return null;
		// parse JSON data
		try {
			JSONObject result = new JSONObject(JSONstring);
			// {"expires_at":1329883162,"charges_at":1328763562,"error_description":"payment required","error":"fetch list failed","downloaded_users_count":"2"}
			if (result != null) {
				if (result.has("error_description")) {
					return result.getString("error_description");
				}
				if (result.has("error")) {
					return result.getString("error") +" && "+result.getString("status") ;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}
}
