package com.smartmux.foto.utils;

import android.graphics.Bitmap;

public class PhotoUtil {

	public static final int PROTRATE = 1; 
	public static final int LANDSCAPE = 2; 
	public static final int SQUARE = 3; 
	
	public static Integer getImageOrientation( Bitmap bit) {

		if(bit!=null){
		long original_ht = bit.getHeight();
		long original_wt = bit.getWidth();

		if(original_ht>original_wt){
			
			return PROTRATE;
			
		}else if(original_wt>original_ht){
			
			return LANDSCAPE;
			
		}else if(original_ht==original_wt){
			
			return SQUARE;
			
		}
		}
		return 0;
		
	}
	
}
