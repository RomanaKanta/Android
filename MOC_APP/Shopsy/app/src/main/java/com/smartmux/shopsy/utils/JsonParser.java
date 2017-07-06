package com.smartmux.shopsy.utils;

import android.util.Log;

import com.smartmux.shopsy.modelclass.ProductModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by tanvir-android on 6/17/16.
 */
public class JsonParser {

    public static ArrayList<ProductModel> getProduct(String jsonString) {


        ArrayList<ProductModel> list = new ArrayList<ProductModel>();

        String productID = "";
        String productName = "";
        String productDescription = "";
        String productPrice = "";
        String currency = "";
        String productThumb = "";
        String sub_catarogy = "";
        String type = "";
        String quantity = "";
        String stock_status = "";
        String href = "";
        String category = "";

        try {

            Object json = new JSONTokener(jsonString).nextValue();
            if (json instanceof JSONObject) {

            } else if (json instanceof JSONArray) {

                JSONArray array = (JSONArray) json;

                for (int i = 0; i < array.length(); i++) {


                    ArrayList<String> imageArray = new ArrayList<>();

                    JSONObject obj = array.getJSONObject(i);

                    if (obj.has(Constant.PRO_ID)) {
                        productID = obj.getString(Constant.PRO_ID);
                    }
                    if (obj.has(Constant.PRO_NAME)) {
                        productName = obj.getString(Constant.PRO_NAME);
                    }
                    if (obj.has(Constant.PRO_DESCRIPTION)) {
                        productDescription = obj.getString(Constant.PRO_DESCRIPTION);
                    }
                    if (obj.has(Constant.PRO_PRICE)) {
                        productPrice = obj.getString(Constant.PRO_PRICE);
                    }
                    if (obj.has(Constant.CURRENCY)) {
                        currency = obj.getString(Constant.CURRENCY);
                    }

                    if (obj.has(Constant.PRO_THUMB)) {
                        productThumb = obj.getString(Constant.PRO_THUMB);
                    }
                    if (obj.has(Constant.PRO_TYPE)) {
                        type = obj.getString(Constant.PRO_TYPE);
                    }

                    if (obj.has(Constant.CATAGORY)) {
                        category = obj.getString(Constant.CATAGORY);
                    }
                    if (obj.has(Constant.SUB_CAYAGORY)) {
                        sub_catarogy = obj.getString(Constant.SUB_CAYAGORY);
                    }

                    if (obj.has(Constant.QUANTITY)) {
                        quantity = obj.getString(Constant.QUANTITY);
                    }
                    if (obj.has(Constant.HREF)) {
                        href = obj.getString(Constant.HREF);
                    }

                    if (obj.has(Constant.STOCK)) {
                        stock_status = obj.getString(Constant.STOCK);
                    }

                    if (obj.has(Constant.PRO_IMAGE_ARRAY)) {

                        JSONArray sizejson = obj.getJSONArray(Constant.PRO_IMAGE_ARRAY);

                        if (imageArray.size() != 0) {
                            imageArray.clear();
                        }
                        for (int j = 0; j < sizejson.length(); j++) {

                            String str = "";
                            JSONObject imageobj = sizejson.getJSONObject(j);

                            if (imageobj.has(Constant.IMAGE_TAG)) {
                                str = imageobj.getString(Constant.IMAGE_TAG);
                            }
                            imageArray.add(str);

                        }

                    }




                    ProductModel pro_model = new ProductModel( productID,  productName,  productDescription,
                             productPrice,  currency,  productThumb,
                             stock_status,  quantity,  href,  category,
                             sub_catarogy,  type,  imageArray);

                    list.add(pro_model);
                }
            }

        } catch (JSONException je) {
            Log.e("JSONException", "" + je);
        }
        return list;
    }



//    public static ArrayList<ProductModelClass> getProduct(String jsonString) {
//
//
//        ArrayList<ProductModelClass> list = new ArrayList<ProductModelClass>();
//
//        String productID = "";
//        String productName = "";
//        String productDescription = "";
//        String productPrice = "";
//        String productThumb = "";
//        String productType = "";
//        String productImage = "";
//        String tag = "";
//        String brand_id = "";
//        String brand = "";
//
//        String category = "";
//        String sub_category = "";
//
//
//        try {
//
//            Object json = new JSONTokener(jsonString).nextValue();
//            if (json instanceof JSONObject) {
//
//            } else if (json instanceof JSONArray) {
//
//                JSONArray array = (JSONArray) json;
//
//                for (int i = 0; i < array.length(); i++) {
//
//
//                    ArrayList<String> productSize = new ArrayList<>();
//                    ArrayList<String> productColor = new ArrayList<>();
//
//                    JSONObject obj = array.getJSONObject(i);
////                    Iterator<String> iterator = object.keys();
////                    while (iterator.hasNext()) {
////                        String currentKey = iterator.next();
////
////                        JSONObject obj  = object.getJSONObject(currentKey);
//
//                    if (obj.has(Constant.PRO_ID)) {
//                        productID = obj.getString(Constant.PRO_ID);
//                    }
//                    if (obj.has(Constant.PRO_NAME)) {
//                        productName = obj.getString(Constant.PRO_NAME);
//                    }
//                    if (obj.has(Constant.PRO_DESCRIPTION)) {
//                        productDescription = obj.getString(Constant.PRO_DESCRIPTION);
//                    }
//                    if (obj.has(Constant.PRO_PRICE)) {
//                        productPrice = obj.getString(Constant.PRO_PRICE);
//                    }
//                    if (obj.has(Constant.PRO_IMAGE_ARRAY)) {
//                        productImage = obj.getString(Constant.PRO_IMAGE_ARRAY);
//                    }
//                    if (obj.has(Constant.PRO_THUMB)) {
//                        productThumb = obj.getString(Constant.PRO_THUMB);
//                    }
//                    if (obj.has(Constant.PRO_TYPE)) {
//                        productType = obj.getString(Constant.PRO_TYPE);
//                    }
//                    if (obj.has(Constant.BRAND)) {
//                        brand = obj.getString(Constant.BRAND);
//                    }
//                    if (obj.has(Constant.BRAND_ID)) {
//                        brand_id = obj.getString(Constant.BRAND_ID);
//                    }
//                    if (obj.has(Constant.CATAGORY)) {
//                        category = obj.getString(Constant.CATAGORY);
//                    }
//                    if (obj.has(Constant.SUB_CAYAGORY)) {
//                        sub_category = obj.getString(Constant.SUB_CAYAGORY);
//                    }
//
//                    if (obj.has(Constant.PRO_TYPE)) {
//                        tag = obj.getString(Constant.PRO_TYPE);
//                    }
//
//                    if (obj.has(Constant.PRO_SIZE)) {
//
//
//                        JSONArray sizejson = obj.getJSONArray(Constant.PRO_SIZE);
//
//                        if (productSize.size() != 0) {
//                            productSize.clear();
//                        }
//                        for (int j = 0; j < sizejson.length(); j++) {
//
//                            String str = sizejson.getString(j);
//                            productSize.add(str);
//
//                        }
//
//                    }
//
//
//                    if (obj.has(Constant.PRO_COLOR)) {
//
//
//                        JSONArray colorjson = obj.getJSONArray(Constant.PRO_COLOR);
//
//                        if (productColor.size() != 0) {
//                            productColor.clear();
//                        }
//                        for (int j = 0; j < colorjson.length(); j++) {
//
//                            String str = colorjson.getString(j);
//                            productColor.add(str);
//
//                        }
//
//                    }
//
//
//                    ProductModelClass pro_model = new ProductModelClass( brand_id,  brand,  category,
//                             sub_category,  productID,  productName,
//                             productPrice,  productType,  productThumb,
//                             productDescription,  productImage,  tag,
//                             productColor,  productSize);
//
//                    list.add(pro_model);
//                }
//            }
//
//        } catch (JSONException je) {
//            Log.e("JSONException", "" + je);
//        }
//        return list;
//    }
//



//    public static ArrayList<BannerItem> getBannerArray(String jsonString) {
//
//
//        ArrayList<BannerItem> list = new ArrayList<BannerItem>();
//
//        String id = "";
//        String type = "";
//        String platfrom = "";
//        String thumbUrl = "";
//        String actionUrl = "";
//        String title = "";
//
//        try {
//
//            Object json = new JSONTokener(jsonString).nextValue();
//            if (json instanceof JSONObject){
//            }
//
//            else if (json instanceof JSONArray){
//
//                JSONArray jsonContent = (JSONArray) json;
//
//                for (int i = 0; i < jsonContent.length(); i++) {
//
//                    JSONObject obj = jsonContent.getJSONObject(i);
//
//                    if(obj.has(Constant.BN_ID)) {
//                        id = obj.getString(Constant.BN_ID);
//                    }
//                    if(obj.has(Constant.BN_TYPE)) {
//                        type = obj.getString(Constant.BN_TYPE);
//                    }
//                    if(obj.has(Constant.BN_PLATFORM)) {
//                        platfrom = obj.getString(Constant.BN_PLATFORM);
//                    }
//                    if(obj.has(Constant.BN_THUMB)) {
//                        thumbUrl = obj.getString(Constant.BN_THUMB);
//                    }
//                    if(obj.has(Constant.BN_ACTION)) {
//                        actionUrl = obj.getString(Constant.BN_ACTION);
//                    }
//                    if(obj.has(Constant.BN_TITLE)) {
//                        title = obj.getString(Constant.BN_TITLE);
//                    }
//
//
//                    BannerItem model = new BannerItem(id, type, platfrom, thumbUrl, actionUrl, title);
//
//                    list.add(model);
//                }
//
//
//            }
//
//        } catch (JSONException je) {
//            Log.e("JSONException", "" + je);
//        }
//        return list;
//    }

    public void setDataFromJson(JSONObject json, String key) {


        JSONArray interventionJsonArray;
        JSONObject interventionObject;

        try {

//                JSONObject obj  = json.getJSONObject(key);

//                Log.e("obj", ""+ obj );
            Object intervention = json.get("intervention");
            if (intervention instanceof JSONArray) {
                // It's an array
                interventionJsonArray = (JSONArray) intervention;
            } else if (intervention instanceof JSONObject) {
                // It's an object
                interventionObject = (JSONObject) intervention;
            } else {
                // It's something else, like a string or number
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
