package com.ksproject.krishop.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AppTextViewLatoBold extends TextView{

	 public AppTextViewLatoBold(Context context) {
	        super(context);
	    }

	    public AppTextViewLatoBold(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                    "font/Lato-Bold.ttf");
	        setTypeface(tf);
	    }

	    
	    

	    @SuppressLint("DefaultLocale")
		@Override
	    public void setText(CharSequence text, BufferType type) {
	        super.setText(text.toString(), type);
	    }

}
