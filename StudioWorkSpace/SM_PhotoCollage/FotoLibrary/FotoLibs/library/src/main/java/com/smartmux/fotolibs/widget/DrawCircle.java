package com.smartmux.fotolibs.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DrawCircle extends View {
	private int radius;
	private Paint paint, basePaint, borderpaint = null;
	private int color = Color.BLACK;
    int x = 0;
    int y = 0;
    public float red = 1;

	public DrawCircle(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		//setBackgroundColor(Color.GRAY);
	}
	public DrawCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setupPaint();
	}
	private void setupPaint() {
		
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(color);
		
		borderpaint = new Paint();
		borderpaint.setStyle(Paint.Style.STROKE);
		borderpaint.setStrokeWidth(1);
		borderpaint.setColor(Color.WHITE);
		
		basePaint = new Paint();
		basePaint.setStyle(Paint.Style.FILL);
		basePaint.setColor(Color.parseColor("#272727"));
		
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		 x = getWidth();
		 y = getHeight();

         red = (float) y/200;

//        Log.e("red" , ""+ red + "   Y  " + y + "    r  " + radius);

		canvas.drawCircle(x / 2, y / 2, y / 2, basePaint);
		//canvas.drawPaint(paint);
		// Use Color.parseColor to define HTML colors
		canvas.drawCircle(x / 2, y / 2, radius * red, paint);
		canvas.drawCircle(x / 2, y / 2, radius * red, borderpaint);
	}

	public void setRadius(int radius) {

		if (radius == 0) {
			this.radius = 1;
		} else {
			this.radius = (int) (radius);
		}

	}

	public void setColor(int color) {

		this.color = color;
		paint.setColor(color);
		basePaint.setColor(color);
		basePaint.setAlpha(50);
	}
}