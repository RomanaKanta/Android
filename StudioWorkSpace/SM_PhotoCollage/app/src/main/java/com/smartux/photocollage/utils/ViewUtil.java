package com.smartux.photocollage.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;

import com.smartux.photocollage.CollageActivity;
import com.smartux.photocollage.model.Boxes;
import com.smartux.photocollage.widget.SAFrameFunPhotoView;

public class ViewUtil {

	private static ArrayList<Point> pathpointsArray;
	private static Path path;
	public static ArrayList<SAFrameFunPhotoView> customViewList=CollageActivity.customViewList;
	
	public static View collageView(Context context,Activity activity,RelativeLayout collageLayout,ArrayList<Boxes> boxArrayList,ArrayList<String> paths,String color){
		CollageActivity.mMemoryCache.evictAll();
		for(int i=0;i<paths.size();i++){
			
			CollageActivity.mMemoryCache.put(paths.get(i), BitmapUtils.generateBitmap(paths.get(i)));
		}
		CollageActivity.customViewList.clear();
		for (int i = 0; i < boxArrayList.size(); i++) {
			pathpointsArray = boxArrayList.get(i).getPathPoints();

			RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(
					boxArrayList.get(i).getBoxWidth(), boxArrayList.get(i)
							.getBoxHeight());
			if(paths.size()>1){
			if (i > 0) {
				viewParams.setMargins(boxArrayList.get(i).getLeftMargin(),
					boxArrayList.get(i).getTopMargin(), boxArrayList.get(i)
								.getRightMargin(), 0);
			}
			}else{
				viewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			}
			SAFrameFunPhotoView m_viewFrame = new SAFrameFunPhotoView(context);
			m_viewFrame.setId(100+i);
			path = new Path();
			path.moveTo(pathpointsArray.get(0).x, pathpointsArray.get(0).y);

			for (int j = 0; j < pathpointsArray.size(); j++) {

				path.lineTo(pathpointsArray.get(j).x, pathpointsArray.get(j).y);

			}
			path.lineTo(pathpointsArray.get(0).x, pathpointsArray.get(0).y);

			m_viewFrame.setLayoutParams(viewParams);
			m_viewFrame.setBackgroundColor(Color.TRANSPARENT);
			m_viewFrame.setBitmap(CollageActivity.mMemoryCache.get(paths.get(i)));
			m_viewFrame.setPath(path);
			m_viewFrame.setColorFilter(color);
			m_viewFrame.setBox(boxArrayList.get(i));  
			m_viewFrame.setActivity(activity);
			m_viewFrame.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			collageLayout.addView(m_viewFrame);
			CollageActivity.customViewList.add(m_viewFrame);
		}
		
		
		return collageLayout;
		
	}
	
	public static Paint getPaint(String color,int stroke) {
	 	Paint paint =new Paint();
	 	paint.setColor(Color.WHITE);                   
	    paint.setStrokeWidth(10);             
	    paint.setDither(true);                    
	    paint.setStyle(Paint.Style.STROKE); 
	    
	    return paint;
}
	public static String getJsonFromAsset(Context context) {

		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(context.getAssets().open(
					"two/image_2_template_14.json")));
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

	public static ColorFilter getFilter(String color) {
		PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(
				Color.parseColor(color), PorterDuff.Mode.SRC_ATOP);
		return colorFilter;
	}

	public static ArrayList<String> Randomize(ArrayList<String> arr) {

		Collections.shuffle(arr);

	    return arr;
	}
	
	public static Rect locateView(SAFrameFunPhotoView v)
	{
	    int[] loc_int = new int[2];
	    if (v == null) return null;
	    try
	    {
	        v.getLocationOnScreen(loc_int);
	    } catch (NullPointerException npe)
	    {
	        //Happens when the view doesn't exist on screen anymore.
	        return null;
	    }
	    Rect location = new Rect();
	    location.left = loc_int[0];
	    location.top = loc_int[1];
	    location.right = location.left + v.getWidth();
	    location.bottom = location.top + v.getHeight();
	    return location;
	}
	
	/**
	 * This method converts dp unit to equivalent pixels, depending on device density. 
	 * 
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static float convertDpToPixel(float dp, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	    return px;
	}

	/**
	 * This method converts device specific pixels to density independent pixels.
	 * 
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	    return dp;
	}
}
