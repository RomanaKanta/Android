package com.smartmux.fotolibs.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AppHeaderTextView extends TextView{

	 public AppHeaderTextView(Context context) {
	        super(context);
	    }

	    public AppHeaderTextView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        
	        Typeface tf = Typeface.createFromAsset(context
					.getAssets(), "fonts/" + "SinkinSans-600SemiBold.otf");
//	        Typeface tf = Typeface.createFromAsset(context
//					.getAssets(), "fonts/" + "Oduda-Bold-Demo.otf");
	        
	        setTypeface(tf);
	    }

	    
//	    @SuppressLint("DefaultLocale")
//		@Override
//	    public void setText(CharSequence text, BufferType type) {
//	        super.setText(text.toString().toUpperCase(), type);
//	    }

}
