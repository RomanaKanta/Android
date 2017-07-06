package com.smartux.photocollage.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.smartux.photocollage.model.Boxes;
import com.smartux.photocollage.model.SizeHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JsonManager {

	public static Context context;
	private static JSONArray coordinates;

    public JsonManager(Context context){


        this.context = context;
//        getScreenSize();
    }

//    public void getScreenSize() {
//        WindowManager wm = (WindowManager) context
//                .getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        DisplayMetrics metrics = new DisplayMetrics();
//        display.getMetrics(metrics);
//
//        int mrgnht = (int) context.getResources().getDimension(
//                R.dimen.bottom_ht)
//                + (int) context.getResources().getDimension(R.dimen.topbar_ht);
//        int mrgnwt = (int) context.getResources().getDimension(
//                R.dimen.mrgn_left)
//                + (int) context.getResources().getDimension(R.dimen.mrgn_left);
//
//        int deviceWidht = (metrics.widthPixels - mrgnwt);
//        int deviceHeight = (metrics.heightPixels - mrgnht);
//
//        SizeHolder.setHt(deviceHeight);
//        SizeHolder.setWt(deviceWidht);
//
//    }


    public  ArrayList<Boxes> getDataFromJson(String json, boolean isText){

        ArrayList<Boxes> boxArrayList = null;
        String jsonString = getJsonFromAsset(json);

        if(jsonString!=null && !jsonString.isEmpty()){

            if(isText) {
                boxArrayList = makeBoxObjectWithTextFromJson(jsonString);
            }else{
                boxArrayList = makeBoxObjectFromJson(jsonString);
            }
        }

        return boxArrayList;
    }

    private  String getJsonFromAsset(String json) {
        // Log.d("getJsonFromAsset", "" + json);
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets()
                    .open(json)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String myjsonstring = sb.toString();
        return myjsonstring;

    }

	@SuppressLint("NewApi")
	public  int getScreenWidht(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getRealMetrics(metrics);
		int width = metrics.widthPixels;
		return width;
	}

	@SuppressLint("NewApi")
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getRealMetrics(metrics);
		int height = metrics.heightPixels;
		return height;
	}

	public  int getStatusBarHeight(Activity context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;

	}

	public  float dpFromPx(final Context context, final float px) {
		return px / context.getResources().getDisplayMetrics().density;
	}

	public  float pxFromDp(final Context context, final float dp) {
		return dp * context.getResources().getDisplayMetrics().density;
	}

	public  int getNavBarHeight(Context c) {
		int result = 0;
		boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
		boolean hasBackKey = KeyCharacterMap
				.deviceHasKey(KeyEvent.KEYCODE_BACK);

		if (!hasMenuKey && !hasBackKey) {
			// The device has a navigation bar
			Resources resources = c.getResources();

			int orientation = c.getResources().getConfiguration().orientation;
			int resourceId;
			if (isTablet(c)) {
				resourceId = resources
						.getIdentifier(
								orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height"
										: "navigation_bar_height_landscape",
								"dimen", "android");
			} else {
				resourceId = resources
						.getIdentifier(
								orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height"
										: "navigation_bar_width", "dimen",
								"android");
			}

			if (resourceId > 0) {
				return c.getResources().getDimensionPixelSize(resourceId);
			}
		}
		return result;
	}

	private  boolean isTablet(Context c) {
		return (c.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public  int convertX(int value) {
		return (value * SizeHolder.getWt()) / 1242;
	}

	public  int convertY(int value) {
		return (value * SizeHolder.getHt() + getNavBarHeight(context)) / 2208;
	}

	public  ArrayList<Boxes> makeBoxObjectFromJson(String stringbuffer) {
		ArrayList<Boxes> boxArray = new ArrayList<Boxes>();
		ArrayList<Point> pointsArray;
		ArrayList<Point> realPathPointsArray;
		try {
			// Creating JSONObject from String
			JSONObject jsonObjMain = new JSONObject(stringbuffer);

			// Creating JSONArray from JSONObject
			JSONArray jsonArray = jsonObjMain.getJSONArray("Boxes");

			// JSONArray has x JSONObject
//			Log.e("SCREENMNG", "JSON ARRAY"  + jsonArray.length());
			for (int i = 0; i < jsonArray.length(); i++) {
				pointsArray = new ArrayList<Point>();
				realPathPointsArray = new ArrayList<Point>();
				// // Creating JSONObject from JSONArray
				JSONArray boxes = jsonArray.getJSONArray(i);
				int viewWidth = 0;
				int viewHeight = 0;
				int leftMargin = 0;
				int topMargin = 0;
				int rightMargin = 0;
				int bottomMargin = 0;
				int max_X = 0;
				int max_Y = 0;
				int min_X = SizeHolder.getWt();
				int min_Y = SizeHolder.getHt();
				
//				Log.e("SCREENMNG", "BOX"  + boxes.length());
				
				for (int j = 0; j < boxes.length(); j++) {
					coordinates = boxes.getJSONArray(j);

					Point point = new Point();
					point.x = convertX(coordinates.getInt(0));
					point.y = convertY(coordinates.getInt(1));
					pointsArray.add(point);

					if (max_X < convertX(coordinates.getInt(0))) {
						max_X = convertX(coordinates.getInt(0));

					}
					if (max_Y < convertY(coordinates.getInt(1))) {
						max_Y = convertY(coordinates.getInt(1));

					}
					if (min_X > convertX(coordinates.getInt(0))) {
						min_X = convertX(coordinates.getInt(0));
					}
					if (min_Y > convertY(coordinates.getInt(1))) {
						min_Y = convertY(coordinates.getInt(1));
						
//						Log.e("JSON", "JSON" + coordinates.getInt(1));
					}

				}
				leftMargin = min_X;
				rightMargin = SizeHolder.getWt() - max_X;
				topMargin = min_Y;
				bottomMargin = SizeHolder.getHt()
						- max_Y;
				viewWidth = SizeHolder.getWt()
						- (leftMargin + rightMargin);
				viewHeight = SizeHolder.getHt()
						- (topMargin + bottomMargin);

				for (int k = 0; k < pointsArray.size(); k++) {
					Point pathPoint = new Point();
					if (pointsArray.get(k).x - leftMargin < 0) {
						pathPoint.x = -(pointsArray.get(k).x - leftMargin);
					} else {
						pathPoint.x = (pointsArray.get(k).x - leftMargin);
					}
					if (pointsArray.get(k).y - topMargin < 0) {
						pathPoint.y = -(pointsArray.get(k).y - topMargin);
					} else {
						pathPoint.y = pointsArray.get(k).y - topMargin;
					}
					realPathPointsArray.add(pathPoint);

				}

				Boxes box = new Boxes(leftMargin, topMargin, rightMargin,
						bottomMargin);
				box.setPoints(pointsArray);
				box.setPathPoints(realPathPointsArray);
				box.setBoxWidth(viewWidth);
				box.setBoxHeight(viewHeight);
				box.setMax_X(max_X);
				box.setMax_Y(max_Y);
				box.setMin_X(min_X);
				box.setMin_Y(min_Y);

				boxArray.add(box);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return boxArray;

	}


    public  ArrayList<Boxes> makeBoxObjectWithTextFromJson(String stringbuffer) {
        ArrayList<Boxes> boxArray = new ArrayList<Boxes>();
        ArrayList<Point> pointsArray;
        ArrayList<Point> realPathPointsArray;
        try {
            // Creating JSONObject from String
            JSONObject jsonObjMain = new JSONObject(stringbuffer);

            String position = jsonObjMain.getString("Position");

            // Creating JSONArray from JSONObject
            JSONArray jsonArray = jsonObjMain.getJSONArray("Boxes");

            // JSONArray has x JSONObject
//			Log.e("SCREENMNG", "JSON ARRAY"  + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                pointsArray = new ArrayList<Point>();
                realPathPointsArray = new ArrayList<Point>();
                // // Creating JSONObject from JSONArray
                JSONArray boxes = jsonArray.getJSONArray(i);
                int viewWidth = 0;
                int viewHeight = 0;
                int leftMargin = 0;
                int topMargin = 0;
                int rightMargin = 0;
                int bottomMargin = 0;
                int max_X = 0;
                int max_Y = 0;
                int min_X = SizeHolder.getWt();
                int min_Y = SizeHolder.getHt();

//				Log.e("SCREENMNG", "BOX"  + boxes.length());

                for (int j = 0; j < boxes.length(); j++) {
                    coordinates = boxes.getJSONArray(j);

                    Point point = new Point();
                    point.x = convertX(coordinates.getInt(0));
                    point.y = convertY(coordinates.getInt(1));
                    pointsArray.add(point);

                    if (max_X < convertX(coordinates.getInt(0))) {
                        max_X = convertX(coordinates.getInt(0));

                    }
                    if (max_Y < convertY(coordinates.getInt(1))) {
                        max_Y = convertY(coordinates.getInt(1));

                    }
                    if (min_X > convertX(coordinates.getInt(0))) {
                        min_X = convertX(coordinates.getInt(0));
                    }
                    if (min_Y > convertY(coordinates.getInt(1))) {
                        min_Y = convertY(coordinates.getInt(1));

//						Log.e("JSON", "JSON" + coordinates.getInt(1));
                    }

                }
                leftMargin = min_X;
                rightMargin = SizeHolder.getWt() - max_X;
                topMargin = min_Y;
                bottomMargin = SizeHolder.getHt()
                        - max_Y;
                viewWidth = SizeHolder.getWt()
                        - (leftMargin + rightMargin);
                viewHeight = SizeHolder.getHt()
                        - (topMargin + bottomMargin);

                for (int k = 0; k < pointsArray.size(); k++) {
                    Point pathPoint = new Point();
                    if (pointsArray.get(k).x - leftMargin < 0) {
                        pathPoint.x = -(pointsArray.get(k).x - leftMargin);
                    } else {
                        pathPoint.x = (pointsArray.get(k).x - leftMargin);
                    }
                    if (pointsArray.get(k).y - topMargin < 0) {
                        pathPoint.y = -(pointsArray.get(k).y - topMargin);
                    } else {
                        pathPoint.y = pointsArray.get(k).y - topMargin;
                    }
                    realPathPointsArray.add(pathPoint);

                }

                Boxes box = new Boxes(position,leftMargin, topMargin, rightMargin,
                        bottomMargin);
                box.setPoints(pointsArray);
                box.setPathPoints(realPathPointsArray);
                box.setBoxWidth(viewWidth);
                box.setBoxHeight(viewHeight);
                box.setMax_X(max_X);
                box.setMax_Y(max_Y);
                box.setMin_X(min_X);
                box.setMin_Y(min_Y);

                boxArray.add(box);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return boxArray;

    }

}
