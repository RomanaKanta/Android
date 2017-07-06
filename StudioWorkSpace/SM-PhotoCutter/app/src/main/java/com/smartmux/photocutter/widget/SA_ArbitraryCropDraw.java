package com.smartmux.photocutter.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.smartmux.photocutter.utils.myPoint;

import java.util.ArrayList;


public class SA_ArbitraryCropDraw extends View implements OnTouchListener {

	private Paint paint;
	static ArrayList<myPoint> points = new ArrayList<myPoint>();
	static ArrayList<myPoint> redoPoints = new ArrayList<myPoint>();
	boolean flgPathDraw = true;
	private float mPhase;
	boolean drawPath = false;
	static Bitmap bitmapDraw = null;
	static Path pathSpecial = null;
//    Paint mBackgroundPaint;


	public SA_ArbitraryCropDraw(Context c) {
		super(c);
		setFocusable(true);
		setFocusableInTouchMode(true);

		this.points.clear();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(Color.WHITE);
		mPhase += 1;
		paint.setPathEffect(new DashPathEffect(new float[] { 10, 20, }, mPhase));

		this.setOnTouchListener(this);

	}

	public void setBitmap(Bitmap bitmap) {
		bitmapDraw = bitmap;

	}

	public SA_ArbitraryCropDraw(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		setFocusableInTouchMode(true);
//        mBackgroundPaint = new Paint();
//        mBackgroundPaint.setColor(Color.argb(119, 0, 0, 0));
		this.points.clear();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(Color.WHITE);
		mPhase += 1;
		paint.setPathEffect(new DashPathEffect(new float[] { 10, 20, }, mPhase));

		this.setOnTouchListener(this);

	}

	public void onDraw(Canvas canvas) {

		int centreX = (canvas.getWidth() - bitmapDraw.getWidth()) / 2;

		int centreY = (canvas.getHeight() - bitmapDraw.getHeight()) / 2;

		canvas.drawBitmap(bitmapDraw, centreX, centreY, null);


//        canvas.drawRect(0, 0, canvas.getWidth(),canvas.getHeight(), mBackgroundPaint);

		Path path = new Path();
		Path mpath = new Path();

		boolean first = true;

		for (int i = 0; i < points.size(); i += 2) {
			myPoint point = points.get(i);
			if (first) {
				first = false;
				path.moveTo(point.x, point.y);
				mpath.moveTo(point.x - centreX, point.y - centreY);
			} else if (i < points.size() - 1) {
				myPoint next = points.get(i + 1);
				path.quadTo(point.x, point.y, next.x, next.y);
				mpath.quadTo(point.x - centreX, point.y - centreY, next.x
						- centreX, next.y - centreY);
			} else {
				path.lineTo(point.x, point.y);
				mpath.lineTo(point.x - centreX, point.y - centreY);
			}
		}
		pathSpecial = mpath;
		canvas.drawPath(path, paint);

		mPhase += 1;
		paint.setPathEffect(new DashPathEffect(new float[] { 10, 20, }, mPhase));

		invalidate();
	}


	public boolean onTouch(View view, MotionEvent event) {

		myPoint point = new myPoint();
		point.x = (int) event.getX();
		point.y = (int) event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:

			if (!drawPath) {
				points.clear();
				redoPoints.clear();
				drawPath = true;
			}

			if (flgPathDraw) {
				points.add(point);
			}
			invalidate();
			break;
		case MotionEvent.ACTION_UP:

			if (points.size() > 1) {

				points.add(points.get(0));
				drawPath = false;
			}
			// invalidate();
			break;
		}

		return true;
	}

	public void fillinPartofPath() {
		myPoint point = new myPoint();
		point.x = points.get(0).x;
		point.y = points.get(0).y;

		points.add(point);
		invalidate();
	}

	public void resetView() {
		Log.e("resetView", "" + points.size());

		points.clear();
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		flgPathDraw = true;
		invalidate();
	}

	public Path sendPath() {

		return pathSpecial;
	}

	public void redo() {

		this.points.addAll(redoPoints);
		this.redoPoints.clear();
		invalidate();
	}

	public void undo() {

		this.redoPoints.addAll(points);

		this.points.clear();

		invalidate();

	}

	public int getLength(){
		return points.size();
	}

}
