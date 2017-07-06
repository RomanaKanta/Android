package com.smartmux.filevaultfree.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AppTextViewAverinLight extends TextView{

	 public AppTextViewAverinLight(Context context) {
	        super(context);
	    }

	    public AppTextViewAverinLight(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        Typeface tf = Typeface.createFromAsset(context.getAssets(),
					"AvenirLTStd-Light.otf");
	        setTypeface(tf);
	    }

	    
	    

	    @SuppressLint("DefaultLocale")
		@Override
	    public void setText(CharSequence text, BufferType type) {
	        super.setText(text.toString(), type);
	    }

}
