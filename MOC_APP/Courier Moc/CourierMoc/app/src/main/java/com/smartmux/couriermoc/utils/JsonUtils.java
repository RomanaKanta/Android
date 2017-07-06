package com.smartmux.couriermoc.utils;

import android.util.Log;

import com.smartmux.couriermoc.modelclass.DeliverInfo;
import com.smartmux.couriermoc.modelclass.StatusInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by tanvir-android on 6/17/16.
 */
public class JsonUtils {


    public static ArrayList<StatusInfo> getJsonArray(String jsonString) {


//        ArrayList<String> arrayTitle = new ArrayList<String>();

        ArrayList<StatusInfo> infoArray = new ArrayList<StatusInfo>();

        String orderID = "";
        String status = "";
        String from = "";
        String to = "";
        String distance = "";
        String cost = "";
        String type = "";
        String weight = "";
        String date = "";
        String comment = "";



        try {

            Object json = new JSONTokener(jsonString).nextValue();
            if (json instanceof JSONObject){

                Log.e("Title", "JSONObject");
            }

            else if (json instanceof JSONArray){
                JSONArray jsonContent = (JSONArray)json;
                for (int i = 0; i < jsonContent.length(); i++) {

                    JSONObject obj = jsonContent.getJSONObject(i);

                    if(obj.has(Constant.ORDER_ID)) {
                        orderID = obj.getString(Constant.ORDER_ID);
                    }
                    if(obj.has(Constant.STATUS)) {
                        status = obj.getString(Constant.STATUS);
                    }
                    if(obj.has(Constant.FROM)) {
                        from = obj.getString(Constant.FROM);
                    }
                    if(obj.has(Constant.TO)) {
                        to = obj.getString(Constant.TO);
                    }
                    if(obj.has(Constant.DISTANCE)) {
                        distance = obj.getString(Constant.DISTANCE);
                    }
                    if(obj.has(Constant.COST)) {
                        cost = obj.getString(Constant.COST);
                    }
                    if(obj.has(Constant.TYPE)) {
                        type = obj.getString(Constant.TYPE);
                    }
                    if(obj.has(Constant.WEIGHT)) {
                        weight = obj.getString(Constant.WEIGHT);
                    }
                    if(obj.has(Constant.DATE)) {
                        date = obj.getString(Constant.DATE);
                    }
                    if(obj.has(Constant.COMMENT)) {
                        comment = obj.getString(Constant.COMMENT);
                    }

                    infoArray.add(new StatusInfo(orderID, status, from, to, distance,
                             cost, type, weight, date, comment));
                }
            }

        } catch (JSONException je) {
            Log.e("JSONException", "" + je);
        }
        return infoArray;
    }




    public static Boolean getDeliverInfo(String jsonString) {


        try {

            Object json = new JSONTokener(jsonString).nextValue();
            if (json instanceof JSONObject){
                JSONObject obj  =(JSONObject)json;
                if(obj.has(Constant.ORDER_ID)) {
                    DeliverInfo.id = obj.getString(Constant.ORDER_ID);
                }
                if(obj.has(Constant.SENDER)) {
                    DeliverInfo.senderName = obj.getString(Constant.SENDER);
                }
                if(obj.has(Constant.PICKUP_FROM)) {
                    DeliverInfo.pickupFrom = obj.getString(Constant.PICKUP_FROM);
                }
                if(obj.has(Constant.COST)) {
                    DeliverInfo.cost = obj.getString(Constant.COST);
                }
                if(obj.has(Constant.TYPE)) {
                    DeliverInfo.type = obj.getString(Constant.TYPE);
                }
                if(obj.has(Constant.WEIGHT)) {
                    DeliverInfo.weight = obj.getString(Constant.WEIGHT);
                }
                if(obj.has(Constant.DATE)) {
                    DeliverInfo.date = obj.getString(Constant.DATE);
                }
                if(obj.has(Constant.RECIPIENT_NAME)) {
                    DeliverInfo.recipientName = obj.getString(Constant.RECIPIENT_NAME);
                }
                if(obj.has(Constant.DELIVER_TO)) {
                    DeliverInfo.deliverTo = obj.getString(Constant.DELIVER_TO);
                }
                if(obj.has(Constant.PHONE)) {
                    DeliverInfo.recipientPhone = obj.getString(Constant.PHONE);
                }
                if(obj.has(Constant.INSTRUCTION)) {
                    DeliverInfo.instruction = obj.getString(Constant.INSTRUCTION);
                }

                return true;

            }

            else if (json instanceof JSONArray){
        }

        } catch (JSONException je) {
            Log.e("JSONException", "" + je);
        }
        return false;
    }


    public void setDataFromJson(JSONObject json, String key) {


        JSONArray  interventionJsonArray;
        JSONObject interventionObject;

        try {

//                JSONObject obj  = json.getJSONObject(key);

//                Log.e("obj", ""+ obj );
            Object intervention = json.get("intervention");
            if (intervention instanceof JSONArray) {
                // It's an array
                interventionJsonArray = (JSONArray)intervention;
            }
            else if (intervention instanceof JSONObject) {
                // It's an object
                interventionObject = (JSONObject)intervention;
            }
            else {
                // It's something else, like a string or number
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }





}
