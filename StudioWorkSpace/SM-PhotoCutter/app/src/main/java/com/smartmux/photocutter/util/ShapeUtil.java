package com.smartmux.photocutter.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import com.smartmux.photocutter.widget.SA_ArbitraryCropDraw;

public class ShapeUtil {
	
	public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
		// TODO Auto-generated method stub
		int targetWidth = scaleBitmapImage.getWidth()/2;
		int targetHeight = scaleBitmapImage.getHeight()/2;
		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
				Bitmap.Config.ARGB_8888);
		
		Canvas canvas = new Canvas(targetBitmap);
		Path path = new Path();
		path.addCircle(((float) targetWidth) / 2, ((float) targetHeight) / 2,
				(Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
				Path.Direction.CW);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		canvas.clipPath(path);
		Bitmap sourceBitmap = scaleBitmapImage;
		canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
				sourceBitmap.getHeight()), new RectF(0, 0, targetWidth,
				targetHeight), paint);
		
		
		return targetBitmap;

	}
	
	public static Bitmap getFreehandShape(Context ctx,Bitmap scaleBitmapImage) {
		// TODO Auto-generated method stub
		int targetWidth = scaleBitmapImage.getWidth();
		int targetHeight = scaleBitmapImage.getHeight();
		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(targetBitmap);
		SA_ArbitraryCropDraw cDraw = new SA_ArbitraryCropDraw(ctx);

		Path pathDraw = cDraw.sendPath();
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		canvas.clipPath(pathDraw);
		Bitmap sourceBitmap = scaleBitmapImage;
		canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
				sourceBitmap.getHeight()), new RectF(0, 0, targetWidth,
				targetHeight), paint);

		return targetBitmap;
	}

}
