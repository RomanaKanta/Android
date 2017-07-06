package com.smartux.photocollage.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class MyView extends View{

	public static String borderColor = "#ffffff";
	public static int borderStroke = 20;
    Bitmap fillBMP;
    BitmapShader fillBMPshader;

	public MyView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int x = getWidth();
		int y = getHeight();

		Paint paint = new Paint();
		paint.setColor(Color.parseColor(borderColor)); // set the color
		paint.setStrokeWidth(borderStroke * 2); // set the size
		paint.setDither(true); // set the dither to true
		paint.setStyle(Paint.Style.STROKE); // set to STOKE



        Path p = new Path();
		p.moveTo(0, y);
		p.lineTo(0, 0);
		p.lineTo(x, 0);
		p.lineTo(x, y);
		p.lineTo(0, y);
		canvas.drawPath(p, paint);



	}

	public void setBorderColor(String color) {
		borderColor = color;
		invalidate();
	}

	public void setBorderStroke(int stroke) {
		borderStroke = stroke;
		invalidate();
	}
}
