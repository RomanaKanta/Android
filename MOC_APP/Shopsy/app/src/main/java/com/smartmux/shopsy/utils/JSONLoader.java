package com.smartmux.shopsy.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class JSONLoader {
 
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
	private JSONArray array = null;;
 
    // constructor
    public JSONLoader() {
 
    }
    

    @SuppressLint("NewApi")
	public JSONObject getApiResult(String requestURL){

    	
        URL url;
        String response = new JSONObject().toString();
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
//            conn.setRequestProperty("x-appid", "smartapps");
//            conn.setRequestMethod("POST");
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // conn.setFixedLengthStreamingMode(response.getBytes().length);

            conn.connect();

           /* OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();*/


            int responseCode = conn.getResponseCode();

            //   System.out.println(responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream in = conn.getInputStream();

                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                json = stringBuilder.toString();
                try {
                    jObj = new JSONObject(json);
                    // jObj.put("code",  responseCode);
                    // Log.d("jObj", jObj.toString());
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }

                in.close();

            } else {
                InputStream in = conn.getErrorStream();

                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                json = stringBuilder.toString();
                try {
                    jObj = new JSONObject(json);
//                    jObj.put("code",  responseCode);
//                    Log.d("jObj", jObj.toString());
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }

                in.close();
            }

            //  writer.close();
            conn.disconnect();


        }catch (SocketTimeoutException se){


            try {
                jObj = new JSONObject();

                jObj.put("JSONException", "Connection Time Out.");

            }
            catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
        }
        catch (ConnectException ce){

            try {
                jObj = new JSONObject();

                jObj.put("JSONException", "Connection Time Out.");

            }
            catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

        }
        catch (ConnectTimeoutException te){
            try {
                jObj = new JSONObject();

                jObj.put("JSONException", "Connection Time Out.");

            }
            catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return jObj;

    }



    public String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
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

    public String getJSON(String url) {
        HttpURLConnection conn = null;
        try {
            URL u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();

            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            int status = conn.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (Exception ex) {
            return ex.toString();
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception ex) {
                    //disconnect error
                }
            }
        }
        return null;
    }

    public JSONObject getJsonfromURL(String requestURL){

        JSONObject jObj = null;
        String json = "";
        URL url;
        String response = new JSONObject().toString();
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(15000);
//            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // conn.setFixedLengthStreamingMode(response.getBytes().length);

            conn.connect();


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
//            writer.write(getPostDataString(postDataParams));

            writer.flush();


            int responseCode=conn.getResponseCode();

            //   System.out.println(responseCode);
            if(responseCode == HttpURLConnection.HTTP_OK){

                InputStream in = conn.getInputStream();

                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                json = stringBuilder.toString();
                try {
                    jObj = new JSONObject(json);
                    jObj.put("code",  responseCode);
//                    Log.d("HTTP_OK_jObj", jObj.toString());
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }

                in.close();

            }else {
                InputStream in = conn.getErrorStream();

                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                json = stringBuilder.toString();
                try {
                    jObj = new JSONObject(json);
                    jObj.put("code",  responseCode);
                    Log.d("jObj", jObj.toString());
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }

                in.close();
            }

            writer.close();
            conn.disconnect();



        } catch (Exception e) {
            e.printStackTrace();
        }


        return jObj;

    }
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {

            InputStream is = context.getAssets().open(fileName);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }


}
