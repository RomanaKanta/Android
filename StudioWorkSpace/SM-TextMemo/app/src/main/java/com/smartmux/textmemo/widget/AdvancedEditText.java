package com.smartmux.textmemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Scroller;

import com.smartmux.textmemo.util.PBPreferenceUtils;


public class AdvancedEditText extends EditText implements OnKeyListener,
		OnGestureListener {

	private Context ctx;

	/**
	 * @param context
	 *            the current context
	 * @param attrs
	 *            some attributes
	 * @category ObjectLifecycle
	 */
	public AdvancedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctx = context;
		mPaintNumbers = new Paint();
		mPaintNumbers.setTypeface(Typeface.MONOSPACE);
		mPaintNumbers.setAntiAlias(true);
		mPaintNumbers.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaintNumbers.setColor(Color.parseColor("#D3D3D3"));

		mPaintHighlight = new Paint();

		mScale = context.getResources().getDisplayMetrics().density;
		mPadding = (int) (mPaddingDP * mScale);

		mHighlightedLine = mHighlightStart = -1;

		mDrawingRect = new Rect();
		mLineBounds = new Rect();

		mGestureDetector = new GestureDetector(getContext(), this);
		mRect = new Rect();
		updateFromSettings();
	}

	/**
	 * @see android.widget.TextView#computeScroll()
	 * @category View
	 */
	public void computeScroll() {

		if (mTedScroller != null) {
			if (mTedScroller.computeScrollOffset()) {
				scrollTo(mTedScroller.getCurrX(), mTedScroller.getCurrY());
			}
		} else {
			super.computeScroll();
		}
	}

	/**
	 * @see android.widget.EditText#onDraw(android.graphics.Canvas)
	 * @category View
	 */
	public void onDraw(Canvas canvas) {
		int count, lineX, baseline;

		count = getLineCount();

		int padding = (int) (Math.floor(Math.log10(count)) + 1);

		int fontSize = PBPreferenceUtils.getIntPref(ctx,
				"TextMemo", "font_size", 15);
		boolean isLineNumberShow  = PBPreferenceUtils.getBoolPref(ctx,"TextMemo", "line_show", true);
		if(isLineNumberShow){
			
			padding = (int) ((padding * mPaintNumbers.getTextSize()) + mPadding + (fontSize * mScale * 0.5));
			if (mLinePadding != padding) {
				mLinePadding = padding;
				setPadding(mLinePadding, mPadding, mPadding, mPadding);
			}
			
		}
		

		getDrawingRect(mDrawingRect);
		computeLineHighlight();
		lineX = (int) (mDrawingRect.left + mLinePadding - (fontSize * mScale * 0.5));
		int min = 0;
		int max = count;
		getLineBounds(0, mLineBounds);
		int startBottom = mLineBounds.bottom;
		int startTop = mLineBounds.top;
		getLineBounds(count - 1, mLineBounds);
		int endBottom = mLineBounds.bottom;
		int endTop = mLineBounds.top;
		if (count > 1 && endBottom > startBottom && endTop > startTop) {
			min = Math.max(min,
					((mDrawingRect.top - startBottom) * (count - 1))
							/ (endBottom - startBottom));
			max = Math.min(max,
					((mDrawingRect.bottom - startTop) * (count - 1))
							/ (endTop - startTop) + 1);
		}
		for (int i = min; i < max; i++) {
			baseline = getLineBounds(i, mLineBounds);
			if ((mMaxSize != null) && (mMaxSize.x < mLineBounds.right)) {
				mMaxSize.x = mLineBounds.right;
			}
			if(isLineNumberShow){
				
				canvas.drawText("" + (i + 1), mDrawingRect.left + mPadding,
						baseline, mPaintNumbers);
			}
			
			
			if(isLineNumberShow){
				canvas.drawLine(lineX, mDrawingRect.top, lineX,
						mDrawingRect.bottom, mPaintNumbers);
				
			}
			
		}
		getLineBounds(count - 1, mLineBounds);
		if (mMaxSize != null) {
			mMaxSize.y = mLineBounds.bottom;
			mMaxSize.x = Math.max(mMaxSize.x + mPadding - mDrawingRect.width(),
					0);
			mMaxSize.y = Math.max(
					mMaxSize.y + mPadding - mDrawingRect.height(), 0);
		}
		
	     int height = getHeight();
	        int line_height = getLineHeight();

	         count = height / line_height;

	        if (getLineCount() > count)
	            count = getLineCount();

	        Rect r = mRect;
	         baseline = getLineBounds(0, r);

	        for (int i = 0; i < count; i++) {

	            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, mPaintNumbers);
	            baseline += getLineHeight();
	            
	        }

		super.onDraw(canvas);
	}

	/**
	 * @see android.view.View.OnKeyListener#onKey(android.view.View, int,
	 *      android.view.KeyEvent)
	 */
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		return false;
	}

	/**
	 * @see android.widget.TextView#onTouchEvent(android.view.MotionEvent)
	 * @category GestureDetection
	 */
	public boolean onTouchEvent(MotionEvent event) {

		super.onTouchEvent(event);
		if (mGestureDetector != null) {
			return mGestureDetector.onTouchEvent(event);
		}

		return true;
	}

	/**
	 * @see android.view.GestureDetector.OnGestureListener#onDown(android.view.MotionEvent)
	 * @category GestureDetection
	 */
	public boolean onDown(MotionEvent e) {
		return true;
	}

	/**
	 * @see android.view.GestureDetector.OnGestureListener#onSingleTapUp(android.view.MotionEvent)
	 * @category GestureDetection
	 */
	public boolean onSingleTapUp(MotionEvent e) {
		if (isEnabled()) {
			((InputMethodManager) getContext().getSystemService(
					Context.INPUT_METHOD_SERVICE)).showSoftInput(this,
					InputMethodManager.SHOW_IMPLICIT);
		}
		return true;
	}

	/**
	 * @see android.view.GestureDetector.OnGestureListener#onShowPress(android.view.MotionEvent)
	 * @category GestureDetection
	 */
	public void onShowPress(MotionEvent e) {
	}

	/**
	 * @see android.view.GestureDetector.OnGestureListener#onLongPress(android.view.MotionEvent)
	 */
	public void onLongPress(MotionEvent e) {

	}

	/**
	 * @see android.view.GestureDetector.OnGestureListener#onScroll(android.view.MotionEvent,
	 *      android.view.MotionEvent, float, float)
	 */
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// mTedScroller.setFriction(0);
		return true;
	}

	/**
	 * @see android.view.GestureDetector.OnGestureListener#onFling(android.view.MotionEvent,
	 *      android.view.MotionEvent, float, float)
	 */
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		if (mTedScroller != null) {
			mTedScroller.fling(getScrollX(), getScrollY(), -(int) velocityX,
					-(int) velocityY, 0, mMaxSize.x, 0, mMaxSize.y);
		}
		return true;
	}

	/**
	 * Update view settings from the app preferences
	 * 
	 * @category Custom
	 */
	public void updateFromSettings() {

		if (isInEditMode()) {
			return;
		}
		
		int fontSize = PBPreferenceUtils.getIntPref(ctx,
				"TextMemo", "font_size", 15);
		int fontColor = PBPreferenceUtils.getIntPref(ctx,
				"TextMemo", "color_value", Color.BLACK);

		 
		  mPaintHighlight.setColor(fontColor);	
		  mPaintHighlight.setAlpha(48);

		// text size
		setTextSize(fontSize);
		mPaintNumbers.setTextSize(fontSize * mScale * 0.85f);
		
		// refresh view
		postInvalidate();
		refreshDrawableState();
		mLinePadding = mPadding;
		int count = getLineCount();
		
		boolean isLineNumberShow = PBPreferenceUtils.getBoolPref(ctx,"TextMemo", "line_show", true);
		if(isLineNumberShow){
			
			mLinePadding = (int) (Math.floor(Math.log10(count)) + 1);
			mLinePadding = (int) ((mLinePadding * mPaintNumbers.getTextSize())
					+ mPadding + (fontSize * mScale * 0.5));
			setPadding(mLinePadding, mPadding, mPadding, mPadding);
			
		}else{
			setPadding(mPadding, mPadding, mPadding, mPadding);
			
		}
			
	}

	/**
	 * Compute the line to highlight based on selection
	 */
	protected void computeLineHighlight() {
		int i, line, selStart;
		String text;

		if (!isEnabled()) {
			mHighlightedLine = -1;
			return;
		}

		selStart = getSelectionStart();
		if (mHighlightStart != selStart) {
			text = getText().toString();

			line = i = 0;
			while (i < selStart) {
				i = text.indexOf("\n", i);
				if (i < 0) {
					break;
				}
				if (i < selStart) {
					++line;
				}
				++i;
			}

			mHighlightedLine = line;
		}
	}

	/** The line numbers paint */
	protected Paint mPaintNumbers;
	/** The line numbers paint */
	protected Paint mPaintHighlight;
	/** the offset value in dp */
	protected int mPaddingDP = 6;
	/** the padding scaled */
	protected int mPadding, mLinePadding;
	/** the scale for desnity pixels */
	protected float mScale;
	protected Rect mRect;
	/** the scroller instance */
	protected Scroller mTedScroller;
	/** the velocity tracker */
	protected GestureDetector mGestureDetector;
	/** the Max size of the view */
	protected Point mMaxSize;

	/** the highlighted line index */
	protected int mHighlightedLine;
	protected int mHighlightStart;

	protected Rect mDrawingRect, mLineBounds;
}
