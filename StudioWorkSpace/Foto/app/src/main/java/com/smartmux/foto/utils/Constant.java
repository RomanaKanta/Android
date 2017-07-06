package com.smartmux.foto.utils;

import android.os.Environment;

public class Constant {

	public static final String FOOTER = "Footer_fragment";
	public static final String HEADER = "Header_fragment";
	public static final String MAIN = "Main_fragment";
	public static final String FILTER = "Filter_fragment";
	public static final String ADJUST = "Adjustment_fragment";
	public static final String EFFECT = "Effect_fragment";
	public static final String BLUR = "Blur_fragment";
	public static final String ROTATE = "Rotate_fragment";
	public static final String DRAW = "Draw_fragment";
	public static final String CROP = "Crop_fragment";
	public static final String RESIZE = "Resize_fragment";
	public static final String CURVE = "Curve_fragment";
	public static final String FRAME = "Frame_fragment";
	public static final String STICKER = "Sticker_fragment";
	public static final String TEXT = "Text_fragment";
	
	
	public static final String PLAYSTORE_LINK = "";
	public static final String SHARE_ICON = "https://smartmux.com/publisher/files/clients/com.smartmux.publisherapp.sucheepatra/Icon.png";
	public static final String SHARE_CAPTION = "Foto edit app";
	public static final String SHARE_DESC = "Read Books from Shucheepatra Store";
	
	public static String APP_ROOT_FOLDER = Environment.getExternalStorageDirectory() + "/.Foto";
	
	public static final String PREF_NAME = "com.aircast.koukaibukuro";
    public static final String FB_PACKAGE = "com.facebook.katana";
    public static final String FB_IMAGE_PATH = "fb_image_path";
	
	////// EFFECT\\\\\
	
	public static final int NONE = 0; 
	public static final int SPOT = 1; 
//	public static final int HUE = 2; 
//	public static final int HIGHLITE = 3; 
	public static final int SOFTGLOW = 2; 
	public static final int POSTERIZE = 3; 
	public static final int PIXELATE = 4; 

//	   <string-array name="effect_list_text">
//       <item>None</item>
//       <item>Spot</item>
//       <item>Hue</item>
//       <item>Hightlight</item>
//       <item>Bloom</item>
//       <item>Gloom</item>
//       <item>Sepia</item>
//       <item>Chrome</item>
//       <item>Posterize</item>
//       <item>Pixelate</item>
//   </string-array>
	
}
