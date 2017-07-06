package com.smartmux.photocutter.modelclass;

import android.graphics.Bitmap;
import android.net.Uri;

public class BitmapHolder {

	private static Bitmap bm;
	private static Uri path;

	
	public BitmapHolder() {
//		bm = null;
	}

	public synchronized void setBm(Bitmap abm) {
		if (bm != null && bm != abm && !bm.isRecycled()) {
//			bm.recycle();
			bm = null;
		}
		bm = abm;
	}

	public synchronized void drop() {
		
		if (bm != null ) {
			bm.recycle();
			bm = null;
		}
	}

	public synchronized Bitmap getBm() {
		return bm;
	}

	
	
//	public static Bitmap getBitmap() {
//		return bm;
//	}
//
//	public static void setBitmap(Bitmap bmp_main) {
//		
//		if(BitmapHolder.bm!=null){
//			BitmapHolder.bm = null;
//		}
//		
//		BitmapHolder.bm = bmp_main;
//	}
//
	public static Uri getPath() {
		return path;
	}

	public static void setPath(Uri path) {
		BitmapHolder.path = path;
	}

	
	
}
