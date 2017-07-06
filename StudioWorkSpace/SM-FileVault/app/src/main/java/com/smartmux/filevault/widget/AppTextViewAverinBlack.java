package com.smartmux.filevault.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AppTextViewAverinBlack extends TextView{

	 public AppTextViewAverinBlack(Context context) {
	        super(context);
	    }

	    public AppTextViewAverinBlack(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        Typeface tf = Typeface.createFromAsset(context.getAssets(),
					"AvenirLTStd-Black.otf");
	        setTypeface(tf);
	    }

	    
	    

	    @SuppressLint("DefaultLocale")
		@Override
	    public void setText(CharSequence text, BufferType type) {
	        super.setText(text.toString(), type);
	    }

}
