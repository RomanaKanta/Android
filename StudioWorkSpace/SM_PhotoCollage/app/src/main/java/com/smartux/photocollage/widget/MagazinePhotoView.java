package com.smartux.photocollage.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.MotionEvent;
import android.view.View;

public class MagazinePhotoView extends View {
	private Bitmap m_bitmap = null;
	private int width;
	private int height;
	private Matrix transform = new Matrix();

	private Vector2D position = new Vector2D();
	private float scale = 1;
	private float angle = 0;
    private float MIN_VALUE = 1.0f;
    private float MAX_VALUE = 100;

	private TouchManager touchManager = new TouchManager(2);
	private boolean isInitialized = false;
	Paint paint;
	String colorFilter = null;
	PointF m_startPoint = new PointF();


	public MagazinePhotoView(final Context context) {
		super(context);
		init();

	}

	private void init() {
		setFocusable(true);
	}

	public void removeBitmap() {
		if (null != m_bitmap) {
			m_bitmap.recycle();
			m_bitmap = null;
			System.gc();
		}
	}

	public void setColorFilter(String color) {
		colorFilter = color;
		invalidate();
	}

	public void setBitmap(Bitmap bitmap) {
		if (null != m_bitmap) {
			m_bitmap.recycle();
			m_bitmap = null;
			System.gc();
		}

		if (null == bitmap) {
			m_bitmap = null;
			invalidate();
			return;
		}

		m_bitmap = bitmap;
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
		isInitialized = false;

		invalidate();
	}

	private static float getDegreesFromRadians(float angle) {
		return (float) (angle * 180.0 / Math.PI);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
				MeasureSpec.getSize(heightMeasureSpec));
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@SuppressLint({ "DrawAllocation", "ResourceAsColor" })
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

        if (null == m_bitmap) {
            return;
        }

		if (!isInitialized) {
			int w = getWidth();
			int h = getHeight();
			position.set(w / 2, h / 2);

            if (width * canvas.getHeight()  > height * canvas.getWidth()) {
                scale = (float) canvas.getHeight() / (float) height;
            } else {
                scale = (float) canvas.getWidth() / (float) width;
            }

			isInitialized = true;

		}

		
		transform.reset();
		transform.postTranslate(-width / 2.0f, -height / 2.0f);
		transform.postRotate(getDegreesFromRadians(angle));
		transform.postScale(scale, scale);
		transform.postTranslate(position.getX(), position.getY());

		canvas.save();

		if (colorFilter == null) {
			canvas.drawBitmap(m_bitmap, transform, paint);
		} else {
			Paint mPaintForGlow = new Paint();
			mPaintForGlow.setDither(true);
			mPaintForGlow.setAntiAlias(true);
			mPaintForGlow.setFilterBitmap(true);
//			ColorFilter colorFilterTint = new LightingColorFilter(Color.WHITE,
//					Color.parseColor(colorFilter));
			ColorFilter colorFilterTint = new PorterDuffColorFilter(Color.parseColor(colorFilter), PorterDuff.Mode.MULTIPLY);
			mPaintForGlow.setColorFilter(colorFilterTint);
			canvas.drawBitmap(m_bitmap, transform, mPaintForGlow);
		}

		canvas.restore();
	}

	@SuppressLint("ClickableViewAccessibility") @Override
	public boolean onTouchEvent(MotionEvent event) {
		
		m_startPoint.set(event.getX(), event.getY());
		


		try {
			touchManager.update(event);

			if (touchManager.getPressCount() == 1) {
				position.add(touchManager.moveDelta(0));
			} else {
				if (touchManager.getPressCount() == 2) {
					Vector2D current = touchManager.getVector(0, 1);
					Vector2D previous = touchManager.getPreviousVector(0, 1);
					float currentDistance = current.getLength();
					float previousDistance = previous.getLength();

					if (previousDistance != 0) {
						scale *= currentDistance / previousDistance;

                        //set minimum & maximum scale value
                        scale = Math.max(MIN_VALUE, Math.min(scale, MAX_VALUE));
					}
					angle -= Vector2D.getSignedAngleBetween(current, previous);
					invalidate();
					return false;
				}
			}

			invalidate();
			

			return true; 
		} catch (Throwable t) {
			// So lazy...
		}

		return false;
	}

}
