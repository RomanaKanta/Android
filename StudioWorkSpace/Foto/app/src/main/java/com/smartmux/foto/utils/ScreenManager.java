package com.smartmux.foto.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ScreenManager {
	
	public static int deviceWidht;
	public static int deviceHeight;
	
	
	public static void getScreenResolution(Context context)
	{
	    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	    Display display = wm.getDefaultDisplay();
	    DisplayMetrics metrics = new DisplayMetrics();
	    display.getMetrics(metrics);
	    deviceWidht = metrics.widthPixels;
	    deviceHeight = metrics.heightPixels;

	}
	
	
	public static Float eachPartHeight(int number){
		
		float height=Float.valueOf(deviceHeight/number);
		return height;
		
	}
	

}
