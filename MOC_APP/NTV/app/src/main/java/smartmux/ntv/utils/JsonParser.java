package smartmux.ntv.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smartmux.ntv.model.MenuModel;

/**
 * Created by tanvir-android on 9/7/16.
 */
public class JsonParser {

    public static Map<MenuModel, List<String>> getExpandableListData(Context context) {
        Map<MenuModel, List<String>> expandableListData = new HashMap<>();

        List<MenuModel> menuList = new ArrayList<>();

        String data =  Utils.loadJSONFromAsset(context,"ntv_menu.json");

        try {
            JSONObject objMenu = new JSONObject(data);
            JSONArray arrayMenu = objMenu.getJSONArray("menu");
            for(int i= 0;i<arrayMenu.length();i++){
                List<String> subMenu = new ArrayList<>();
                JSONObject object = arrayMenu.getJSONObject(i);

                MenuModel menu = new MenuModel();
                menu.setId(object.getString("menu_item_name_id"));
                menu.setName(object.getString("menu_item_name"));
                menu.setRssUrl(object.getString("rss_url"));

                JSONArray arraySubMenu = object.getJSONArray("sub_menu");
                for(int j= 0;j<arraySubMenu.length();j++){
                    JSONObject obj = arraySubMenu.getJSONObject(j);
                    subMenu.add(obj.getString("submenu_item_name"));
                }

                expandableListData.put(menu, subMenu);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return expandableListData;
    }

    public static ArrayList<String> getHeadlineData(Context context){

        ArrayList<String> headline = new ArrayList<>();

        String data =  Utils.loadJSONFromAsset(context,"headline.json");

        try {
            JSONObject objMenu = new JSONObject(data);
            JSONArray arrayheadline = objMenu.getJSONArray("headline");
            for(int i= 0;i<arrayheadline.length();i++){

                JSONObject object = arrayheadline.getJSONObject(i);
                headline.add(object.getString("news"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return headline;

    }
}
