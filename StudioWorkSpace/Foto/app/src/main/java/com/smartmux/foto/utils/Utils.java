package com.smartmux.foto.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Utils {

	
	public static Bitmap drawTextToBitmap(Context gContext, 
			  int gResId, 
			  String gText) {
			  Resources resources = gContext.getResources();
			  float scale = resources.getDisplayMetrics().density;
			  Bitmap bitmap = 
			      BitmapFactory.decodeResource(resources, gResId);
				
			  Bitmap.Config bitmapConfig =
			      bitmap.getConfig();
			  // set default bitmap config if none
			  if(bitmapConfig == null) {
			    bitmapConfig = Bitmap.Config.ARGB_8888;
			  }
			  // resource bitmaps are imutable, 
			  // so we need to convert it to mutable one
			  bitmap = bitmap.copy(bitmapConfig, true);
				
			  Canvas canvas = new Canvas(bitmap);
			  // new antialised Paint
			  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			  // text color - #3D3D3D
			  paint.setColor(Color.rgb(61, 61, 61));
			  // text size in pixels
			  paint.setTextSize((int) (60 * scale));
			  // text shadow
			  paint.setShadowLayer(1f, 0f, 1f, Color.RED);
				
			  // draw text to the Canvas center
			  Rect bounds = new Rect();
			  paint.getTextBounds(gText, 0, gText.length(), bounds);
			  int x = (bitmap.getWidth() - bounds.width())/2;
			  int y = (bitmap.getHeight() + bounds.height())/2;
				
			  canvas.drawText(gText, x, y, paint);
				
			  return bitmap;
			}
	
	public static Bitmap textAsBitmap(String text, float textSize, int textColor,int testPosHeight,int textPosWidth) {
	    Paint paint = new Paint();
	    paint.setTextSize(textSize);
	    paint.setColor(textColor);
	    paint.setTextAlign(Paint.Align.CENTER);
		float baseline = -paint.ascent(); // ascent() is negative
//	    int width = (int) (paint.measureText(text) + 3.5f); // round
//	    int height = (int) (baseline + paint.descent() + 3.5f);
	    Bitmap image = Bitmap.createBitmap(textPosWidth, testPosHeight,Bitmap.Config.ARGB_8888);

	    Canvas canvas = new Canvas(image);
	    canvas.drawText(text, 100, 100, paint);
	    return image;
	}
}
