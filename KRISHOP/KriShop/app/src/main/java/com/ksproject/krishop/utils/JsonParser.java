package com.ksproject.krishop.utils;

import android.util.Log;

import com.ksproject.krishop.modelclass.Products;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;


public class JsonParser {

    public static ArrayList<Products> getProducts(String jsonString) {

        ArrayList<Products> productList = new ArrayList<Products>();

        String  catagory_id = "";
        String  catagory = "";
        String  product_id = "";
        String  product_name = "";
        String  description = "";
        String  price = "";
        String  currency = "";
        String  product_image = "";
        String  quantity = "";
        String  harvest_time = "";
        String  product_area = "";
        String  farmer_id = "";
        String  farmer_name = "";
        String  farmer_image = "";
        String  farmer_address = "";
        String  farmer_phone = "";

        try {

            Object json = new JSONTokener(jsonString).nextValue();
            if (json instanceof JSONObject){

            }

            else if (json instanceof JSONArray){

                JSONArray array = (JSONArray) json;

                for (int i = 0; i < array.length(); i++) {

                    JSONObject obj = array.getJSONObject(i);

                    if(obj.has(Constant.catagory_id)) {
                        catagory_id = obj.getString(Constant.catagory_id);
                    }
                    if(obj.has(Constant.catagory)) {
                        catagory = obj.getString(Constant.catagory);
                    }
                    if(obj.has(Constant.product_id)) {
                        product_id = obj.getString(Constant.product_id);
                    }
                    if(obj.has(Constant.product_name)) {
                        product_name = obj.getString(Constant.product_name);
                    }
                    if(obj.has(Constant.description)) {
                        description = obj.getString(Constant.description);
                    }
                    if(obj.has(Constant.price)) {
                        price = obj.getString(Constant.price);
                    }
                    if(obj.has(Constant.currency)) {
                        currency = obj.getString(Constant.currency);
                    }

                    if(obj.has(Constant.product_image)) {
                        product_image = obj.getString(Constant.product_image);
                    }
                    if(obj.has(Constant.quantity)) {
                        quantity = obj.getString(Constant.quantity);
                    }
                    if(obj.has(Constant.harvest_time)) {
                        harvest_time = obj.getString(Constant.harvest_time);
                    }
                    if(obj.has(Constant.product_area)) {
                        product_area = obj.getString(Constant.product_area);
                    }
                    if(obj.has(Constant.farmer_id)) {
                        farmer_id = obj.getString(Constant.farmer_id);
                    }
                    if(obj.has(Constant.farmer_name)) {
                        farmer_name = obj.getString(Constant.farmer_name);
                    }

                    if(obj.has(Constant.farmer_image)) {
                        farmer_image = obj.getString(Constant.farmer_image);
                    }

                    if(obj.has(Constant.farmer_address)) {
                        farmer_address = obj.getString(Constant.farmer_address);
                    }

                    if(obj.has(Constant.farmer_phone)) {
                        farmer_phone = obj.getString(Constant.farmer_phone);
                    }

                    Products pro_model = new Products(catagory_id, catagory, product_id, product_name,
                            description, price, currency, product_image,
                            quantity, harvest_time, product_area, farmer_id,
                            farmer_name, farmer_image, farmer_address,
                            farmer_phone) ;

                    productList.add(pro_model);
                }
            }

        } catch (JSONException je) {
            Log.e("JSONException", "" + je);
        }
        return productList;
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
