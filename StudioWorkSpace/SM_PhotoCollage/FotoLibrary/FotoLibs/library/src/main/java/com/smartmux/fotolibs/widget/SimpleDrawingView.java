package com.smartmux.fotolibs.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class SimpleDrawingView extends View {
	// setup initial color
	public static int paintColor = Color.BLACK;
	// defines paint and canvas
	public ArrayList<Path> paths = new ArrayList<Path>();
	public ArrayList<Path> undonePaths = new ArrayList<Path>();
	public ArrayList<Paint> mPaints = new ArrayList<Paint>();
	// stores next circle
	private Path mPath;
	public static Boolean isErase = false;
	public static int strokewidth = 5;
	private Bitmap mBitmap;

	private Paint drawPaint;
	private Canvas mCanvas;
	private Bitmap canvasBitmap;

	static Bitmap bitmapDraw = null;

	public SimpleDrawingView(Context context) {
		super(context);

	}

	public SimpleDrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setupPaint();
	}

	public void setupPaint() {
		// Setup paint with color and stroke styles
		// mCanvas = new Canvas();
		mBitmap = Bitmap.createBitmap(400, 800, Config.ARGB_8888);
		// mCanvas.setBitmap(mBitmap);
		mPath = new Path();

		drawPaint = new Paint(Paint.DITHER_FLAG);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(strokewidth);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		// Log.d("Stroke......redraw", "" + strokewidth);
	}

	public void setBitmap(Bitmap bitmap) {
		bitmapDraw = bitmap;

	}

	@Override
	protected void onDraw(Canvas canvas) {

		// Log.d("Erase", "" + isErase);
		if (isErase) {

			// mCanvas.drawColor(Color.WHITE);
			// drawPaint.setColor(Color.TRANSPARENT);
			drawPaint
					.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);

			for (int i = 0; i < paths.size(); i++) {
				canvas.drawPath(paths.get(i), mPaints.get(i));
			}

			canvas.drawPath(mPath, drawPaint);

		} else {
			canvas.drawBitmap(mBitmap, 0, 0, drawPaint);
			// canvas.drawColor(Color.WHITE);

			for (int i = 0; i < paths.size(); i++) {
				canvas.drawPath(paths.get(i), mPaints.get(i));
			}
			drawPaint.setColor(paintColor);
			canvas.drawPath(mPath, drawPaint);
			drawPaint.setXfermode(null);

		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		// Checks for the event that occurs
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			drawPaint.setColor(paintColor);
			drawPaint.setStrokeWidth(strokewidth);
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			mPath.lineTo(mX, mY);
			// commit the path to our offscreen
			// mCanvas.drawPath(mPath, drawPaint);
			// kill this so we don't double draw
			paths.add(mPath);
			mPath = new Path();

			mPaints.add(drawPaint);
			drawPaint = new Paint(drawPaint);

			// Log.d("draw", "" + paths.size());
			invalidate();
			break;
		}
		return true;
	}

	private float mX, mY;
	// private Paint paint;
	// private Object mMode;
	private static final float TOUCH_TOLERANCE = 4;

	private void touch_start(float x, float y) {
		undonePaths.clear();
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;

		}
	}

	// private void touch_up() {
	// mPath.lineTo(mX, mY);
	// // commit the path to our offscreen
	// mCanvas.drawPath(mPath, mPaint);
	// // kill this so we don't double draw
	// paths.add(mPath);
	// mPath = new Path();
	//
	// }

	public void onClickUndo() {
		if (paths.size() > 0) {
			undonePaths.add(paths.remove(paths.size() - 1));
			invalidate();
		} else {

		}
		// toast the user
	}

	public void onClickRedo() {
		if (undonePaths.size() > 0) {
			paths.add(undonePaths.remove(undonePaths.size() - 1));
			invalidate();
		} else {

		}
		// toast the user
	}

	public void setStroke(int stroke) {

		if (stroke == 0) {
			this.strokewidth = 1;
		} else {
			this.strokewidth = stroke;
		}

	}

	public void setColor(int color) {
		this.paintColor = color;

	}

}