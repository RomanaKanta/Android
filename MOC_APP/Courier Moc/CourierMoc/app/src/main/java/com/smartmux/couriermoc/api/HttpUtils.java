package com.smartmux.couriermoc.api;

import android.content.Context;
import android.text.TextUtils;


import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class HttpUtils {
    private static final String TAG = "HttpConnectionUtils";
    private Context mContext;

    public static final int STATUS_RUNNING = 0x1;
    public static final int STATUS_ERROR = 0x2;
    public static final int STATUS_FINISHED = 0x3;
    private static HttpUtils instance = new HttpUtils();


    /**
     * Constructor HttpUtils
     */
    private HttpUtils() {
        super();
        if (mContext == null) {
//            mContext = BoiGhorApplication.appContext;
        }
    }

    /**
     * get instance HttpUtils
     */
    public static HttpUtils getInstance() {
        if (instance == null) {
            instance = new HttpUtils();
        }
        return instance;
    }

    public static String createBasicCookiesString(String token) {
        String cookies = "";
        if (!TextUtils.isEmpty(token)) {
            cookies = "token=" + token;
        }
        return cookies;
    }

    private static class NullX509TrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            System.out.println();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            System.out.println();
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class NullHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public ResponseHandle.Response doPost(String requestURL,
                                          HashMap<String, String> postDataParams) throws UnsupportedEncodingException {

        URL url;

        int responseCode = ResponseHandle.CODE_HTTP_FAIL;
        String response = PBAPIHelper.ERROR_DESC;
        try {
            url = new URL(requestURL);

//            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostnameVerifier());
//            SSLContext context = SSLContext.getInstance("TLS");
//            context.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            conn.setRequestMethod("POST");
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("x-appid", "smartapps");
            if (postDataParams != null) {
                response = "";
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
            }

            System.out.println("response length " + conn.getContentLength());
            System.out.println("responseCode " + responseCode);
            if (conn.getInputStream() != null) {
                responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    System.out.println("HttpsURLConnection.HTTP_OK   start" + System.currentTimeMillis());
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }

                    System.out.println("HttpsURLConnection.HTTP_OK   end" + System.currentTimeMillis());
                } else {

                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getErrorStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                }



            } else {

                responseCode = ResponseHandle.CODE_HTTP_FAIL;
                response = PBAPIHelper.ERROR_DESC;

            }

            conn.disconnect();
        } catch (ConnectTimeoutException e) {
            // Log.e("Timeout Exception: ", e.toString());
            responseCode = ResponseHandle.CODE_HTTP_FAIL;
            response = PBAPIHelper.ERROR_DESC;
        } catch (SocketTimeoutException ste) {
            // Log.e("Timeout Exception: ", ste.toString());
            responseCode = ResponseHandle.CODE_HTTP_FAIL;
            response = PBAPIHelper.ERROR_DESC;
        } catch (ConnectException cEX) {
            responseCode = ResponseHandle.CODE_HTTP_FAIL;
            response = PBAPIHelper.ERROR_DESC;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.d("AGUNG", response);
        return new ResponseHandle.Response(responseCode, response);
    }


    public ResponseHandle.Response doPostWithHttp(String requestURL,
                                                  HashMap<String, String> postDataParams) {

        URL url;
        int responseCode = ResponseHandle.CODE_HTTP_FAIL;
        String response = PBAPIHelper.ERROR_DESC;
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setRequestProperty("x-appid", "smartapps");
            conn.setRequestProperty("platform", "android");
            if (postDataParams != null) {
                response = "";
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
            }
            if (conn.getInputStream() != null) {
                responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;


                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }

                  //  System.out.println(book.results.size());

                } else {

                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getErrorStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                }

            } else {

                responseCode = ResponseHandle.CODE_HTTP_FAIL;
                response = PBAPIHelper.ERROR_DESC;

            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.d("AGUNG", response);
        return new ResponseHandle.Response(responseCode, response);
    }


    private String getPostDataString(HashMap<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    public ResponseHandle.Response doGet(String url) throws Exception {

        URL obj = new URL(url);

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs,
                                           String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs,
                                           String authType) {
            }
        }};

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, SSLSession session) {
                // TODO Auto-generated method stub
                return true;
            }
        };
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        con.setRequestProperty("x-appid", "smartapps");
        con.setRequestProperty("platform", "android");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // con.disconnect();
        // print result
        System.out.println(response.toString());

        return new ResponseHandle.Response(responseCode, response.toString());
    }


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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
