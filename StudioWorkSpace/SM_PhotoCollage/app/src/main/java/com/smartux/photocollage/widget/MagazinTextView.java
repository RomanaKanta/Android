package com.smartux.photocollage.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.smartux.photocollage.MagazineActivity;
import com.smartux.photocollage.model.Boxes;

public class MagazinTextView extends View {

    String TEXT = "Magazine";
	private Bitmap m_bitmap = null;
	private int width;
	private int height;
	private Matrix transform = new Matrix();

	private Vector2D position = new Vector2D();
	private float scale = 1;
	private float angle = 0;
    int textSize = 16;

    private float MIN_VALUE = 1.0f;
    private float MAX_VALUE = 100;

	private TouchManager touchManager = new TouchManager(2);
	private boolean isInitialized = false;
	private Boxes box;
	Path m_path;
	Paint paint;
	String colorFilter = null;
	String imagePath;
	int imageIndex;
	PointF m_startPoint = new PointF();
	Activity m_activity = null;
    int textColor = Color.BLACK;
//	public Canvas mCanvas;
	public static String borderColor = "#ffffff";
	public static int borderStroke = 20;

	Region region;
	boolean down,move = false;
    Context mContext;

    long  action_down_time;
    private static int LONG_PRESS_TIME = 1000;


	public MagazinTextView(final Context context) {
		super(context);
        mContext = context;
		init();

	}

	public void setActivity(Activity activity) {
		m_activity = activity;
	}

	private void init() {
		m_path = null;
		setFocusable(true);
	}

	public void removeBitmap() {
		if (null != m_bitmap) {
			m_bitmap.recycle();
			m_bitmap = null;
			System.gc();
		}
	}

    public void setText(String tx){
        TEXT = tx;
        invalidate();
    }

	public void setColorFilter(String color) {
		colorFilter = color;
		invalidate();
	}

	public void setBorderColor(String color) {
		borderColor = color;
		invalidate();
	}

	public void setBorderStroke(int stroke) {
		borderStroke = stroke;
		invalidate();
	}

	public void setImagePath(String impath) {
		this.imagePath = impath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImageIndex(int indx) {
		this.imageIndex = indx;
	}

	public Integer getImageIndex() {
		return imageIndex;
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
//
// mCanvas = canvas;
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
		if (null != m_path) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
					&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				setLayerType(LAYER_TYPE_SOFTWARE, null);
			}

			try {
				canvas.clipPath(m_path);
			} catch (UnsupportedOperationException e) {
				Log.e("Clip", "clipPath() not supported");
			}
		}
			canvas.drawBitmap(m_bitmap, transform, paint);



            paint = new Paint();
            paint.setColor(Color.parseColor(borderColor)); // set the color
            paint.setStrokeWidth(borderStroke); // set the size
            paint.setDither(true); // set the dither to true
            paint.setStyle(Paint.Style.STROKE); // set to STOKE

            canvas.drawPath(m_path, paint);

            /////////////////######################/////////////////////////
            RectF rectF = new RectF();
            m_path.computeBounds(rectF, true);
            region = new Region();
            region.setPath(m_path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            /////////////////######################/////////////////////////

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize * scale);
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(TEXT,position.getX(), position.getY(),textPaint);


		canvas.restore();
	}

	@SuppressLint("ClickableViewAccessibility") @Override
	public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                action_down_time = System.currentTimeMillis();
//                ((MagazineActivity) m_activity).EnterText(this);
                break;
            case MotionEvent.ACTION_MOVE:
//                ((MagazineActivity) m_activity).removeHandler();
                break;
            case MotionEvent.ACTION_UP:

                long action_up_time = System.currentTimeMillis()- action_down_time;

                if(action_up_time>LONG_PRESS_TIME){
                    ((MagazineActivity) m_activity).showRenameFolderDialog(this);
                }

//                ((MagazineActivity) m_activity).removeHandler();
                break;
        }
		
		m_startPoint.set(event.getX(), event.getY());
		
		 if(region.contains((int)m_startPoint.x,(int) m_startPoint.y))
		 {

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



		 }
		    else{
		        return false;
		    }



        return true;
	}


	public void setPath(Path path) {
		m_path = path;
	}

}
