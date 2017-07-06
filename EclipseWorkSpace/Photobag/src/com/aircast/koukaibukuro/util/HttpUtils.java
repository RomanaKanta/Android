package com.aircast.koukaibukuro.util;

import java.io.IOException;
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

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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

import android.content.Context;
import android.util.Log;


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
		}

		// client = new DefaultHttpClient();
		responseHandler = new BasicResponseHandler();
		// mapper = new ObjectMapper(); // can reuse, share globally
	}

	/** get instance HttpUtils */
	public static HttpUtils getInstance() {
		if (instance == null) {
			instance = new HttpUtils();
		}
		return instance;
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
			List<NameValuePair> parameters) throws URISyntaxException,
			HttpException, IOException {
		HttpClient client = httpClient;
		if (client == null) {
			client = createHttpClient();
		}

		HttpPost httpPost = new HttpPost(url);
		Log.d("AGUNG", "URL :" +url);
		// set paramter
		if (parameters == null) {
			parameters = new ArrayList<NameValuePair>();
		}
		//parameters.add(new BasicNameValuePair("version", PBApplication.VERSION));
		parameters.add(new BasicNameValuePair("platform",  "android"));
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

//		if (cookies != null) {
//			// set cookies
//			String strCookie = "";
//			for (MapEntry<String, String> entry : cookies) {
//				if (!TextUtils.isEmpty(strCookie))
//					strCookie += ";";
//				strCookie += entry.toString();
//			}
//			httpPost.setHeader("Cookie", strCookie);
//			Log.d("AGUNG", "Cookie: "+strCookie);
//		}

		// set user-agent
		//httpPost.setHeader("User-Agent", PBApplication.getUserAgentParams());
		httpPost.setHeader("X-Appid", "photobag");
		
		HttpResponse response = client.execute(httpPost);
		return response;
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
