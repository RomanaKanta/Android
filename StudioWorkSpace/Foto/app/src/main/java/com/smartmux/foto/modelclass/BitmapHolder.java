package com.smartmux.foto.modelclass;

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
		bm = null;
		if (bm != null ) {
			bm.recycle();
			bm = null;
		}
	}

	public synchronized Bitmap getBm() {
		return bm;
	}

	public static Uri getPath() {
		return path;
	}

	public static void setPath(Uri path) {
		BitmapHolder.path = path;
	}
}
