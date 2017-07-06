package com.smartmux.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AppTextView extends TextView{

	 public AppTextView(Context context) {
	        super(context);
	    }

	    public AppTextView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        Typeface tf = Typeface.createFromAsset(context.getAssets(),
					"orbitron-light.otf");
	        setTypeface(tf);
	    }

	    
	    @SuppressLint("DefaultLocale")
		@Override
	    public void setText(CharSequence text, BufferType type) {
	        super.setText(text.toString().toUpperCase(), type);
	    }

}
