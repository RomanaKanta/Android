package com.roundflat.musclecard.view;

import java.util.List;

import com.roundflat.musclecard.model.TutorialModel;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyViewPager extends ViewPager {
	
	//private GestureDetector gestureDetector;
	GestureDetectorCompat motionDetector;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

   
    
    public void setGestureDetector(GestureDetectorCompat motionDetector) {
        this.motionDetector = motionDetector;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (this.motionDetector != null)
            this.motionDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this.motionDetector != null && ev.getActionMasked() == MotionEvent.ACTION_DOWN)
            this.motionDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);

    }

	@Override
	public void removeView(View view) {
		// TODO Auto-generated method stub
		super.removeView(view);
	}
    
    

}
