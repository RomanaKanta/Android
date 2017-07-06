package com.smartmux.foto.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class BitmapUtils {
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
		    int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inBitmap=BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
		}


	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 2;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	
	public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {

		// output = Bitmap.createBitmap(wantedWidth, wantedHeight,
		// Config.ARGB_8888);
		Bitmap  output = Bitmap.createScaledBitmap(bitmap, wantedWidth, wantedHeight,
				true);
		Canvas canvas = new Canvas(output);
		Matrix m = new Matrix();
		m.setScale((float) wantedWidth / bitmap.getWidth(),
				(float) wantedHeight / bitmap.getHeight());
		canvas.drawBitmap(bitmap, m, new Paint());

		return output;
	}


}
