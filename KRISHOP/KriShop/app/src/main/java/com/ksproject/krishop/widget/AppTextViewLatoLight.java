package com.ksproject.krishop.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AppTextViewLatoLight extends TextView{

	 public AppTextViewLatoLight(Context context) {
	        super(context);
	    }

	    public AppTextViewLatoLight(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        Typeface tf = Typeface.createFromAsset(context.getAssets(),
					"font/Lato-Light.ttf");
	        setTypeface(tf);
	    }

	    
	    

	    @SuppressLint("DefaultLocale")
		@Override
	    public void setText(CharSequence text, BufferType type) {
	        super.setText(text.toString(), type);
	    }

}
