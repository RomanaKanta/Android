package com.smartmux.trackyourkids.mapactivities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;


public class JsonParser {

    public static ArrayList<ModelClass> getList(String jsonString) {


        ArrayList<ModelClass> doctorListItemArrayList = new ArrayList<ModelClass>();


        String name         =   "";
        String distance     =   "";
        String image_url    =   "";
        String lat          =   "";
        String lon          =   "";

        try {

            Object json = new JSONTokener(jsonString).nextValue();
            if (json instanceof JSONObject){

            }

            else if (json instanceof JSONArray){

                JSONArray array = (JSONArray) json;

                for (int i = 0; i < array.length(); i++) {


                    JSONObject object = array.getJSONObject(i);

                    if(object.has("name")) {
                        name = object.getString("name");
                    }

                    if(object.has("distance")) {
                        distance = object.getString("distance");
                    }

                    if(object.has("latitude")) {
                        lat = object.getString("latitude");
                    }

                    if(object.has("longitude")) {
                        lon = object.getString("longitude");
                    }

                    if(object.has("thumb_url")) {
                        image_url = object.getString("thumb_url");
                    }


                    doctorListItemArrayList.add(new ModelClass(name, distance, image_url, lat, lon));
                }
            }

        } catch (JSONException je) {
            Log.e("JSONException", "" + je);
        }
        return doctorListItemArrayList;
    }


}
