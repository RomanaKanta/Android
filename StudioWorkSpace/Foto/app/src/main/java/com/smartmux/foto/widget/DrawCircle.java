package com.smartmux.foto.widget;

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
		borderpaint.setStrokeWidth(4f);
		borderpaint.setColor(Color.WHITE);
		
		basePaint = new Paint();
		basePaint.setStyle(Paint.Style.FILL);
		basePaint.setColor(Color.parseColor("#272727"));
		
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int x = getWidth();
		int y = getHeight();

		canvas.drawCircle(x / 2, y / 2, (int)(100 *1.3), basePaint);
		//canvas.drawPaint(paint);
		// Use Color.parseColor to define HTML colors
		canvas.drawCircle(x / 2, y / 2, radius, paint);
		canvas.drawCircle(x / 2, y / 2, radius, borderpaint);
	}

	public void setRadius(int radius) {

		if (radius == 0) {
			this.radius = 1;
		} else {
			this.radius = radius;
		}

	}

	public void setColor(int color) {

		this.color = color;
		paint.setColor(color);
		basePaint.setColor(color);
		basePaint.setAlpha(50);
	}
}