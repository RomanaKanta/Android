package com.smartmux.textmemo.utils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.widget.TextView;

import com.smartmux.textmemo.R;

public class AppActionBar{

	@SuppressLint("NewApi")
	public static void updateAppActionBar(ActionBar actionBar,Context context,boolean iconEnable,boolean backButtonEnable)
	{
		int color = Color.parseColor("#50382e");
		actionBar.setBackgroundDrawable(new ColorDrawable(color));

//		actionBar.setHomeAsUpIndicator(R.drawable.setting_button);
		actionBar.setDisplayHomeAsUpEnabled(backButtonEnable);
		actionBar.setDisplayShowHomeEnabled(iconEnable);

        if(Build.VERSION.SDK_INT >=14 ){
            actionBar.setIcon(R.drawable.icon);
        }
	}
	
	public static void changeActionBarFont(Context context,Activity activity){
		int titleId = context.getResources().getIdentifier("action_bar_title", "id",
	            "android");
	    TextView yourTextView = (TextView) activity.findViewById(titleId);
	    yourTextView.setTextColor(context.getResources().getColor(R.color.white));
	    Typeface tf = Typeface.createFromAsset(context.getAssets(),
				AppExtra.AVENIRLSTD_BLACK);
	    yourTextView.setTypeface(tf);
//	    yourTextView.setGravity(Gravity.CENTER);
//	    int screenWidth = CommonUtil.getInstance().getWidthInPixel(context);
//	    yourTextView.setWidth(screenWidth);
	  
	  }
	
	
}
