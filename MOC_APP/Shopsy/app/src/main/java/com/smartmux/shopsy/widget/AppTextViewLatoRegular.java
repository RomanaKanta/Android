package com.smartmux.shopsy.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AppTextViewLatoRegular extends TextView{

	 public AppTextViewLatoRegular(Context context) {
	        super(context);
	    }

	    public AppTextViewLatoRegular(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        Typeface tf = Typeface.createFromAsset(context.getAssets(),
					"font/Lato-Regular.ttf");
	        setTypeface(tf);
	    }

	    
	    

	    @SuppressLint("DefaultLocale")
		@Override
	    public void setText(CharSequence text, BufferType type) {
	        super.setText(text.toString(), type);
	    }

}
