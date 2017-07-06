package com.smartmux.filevaultfree.utils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import com.smartmux.filevaultfree.R;

public class AppActionBar{

	
	
	@SuppressLint("NewApi")
	public static void updateAppActionBar(ActionBar actionBar,Context context,boolean backButtonEnable)
	{
		//actionBar.setIcon(android.R.color.transparent);   
		//Drawable d = context.getResources().getDrawable(R.drawable.toolbar);
		int color = Color.parseColor("#1976d2");
		actionBar.setBackgroundDrawable(new ColorDrawable(color));
		//mActionBar.setBackgroundDrawable(new ColorDrawable(0xff00DDED));
		actionBar.setDisplayHomeAsUpEnabled(backButtonEnable);
		actionBar.setDisplayShowHomeEnabled(false);
	}
	
	public static void changeActionBarFont(Context context,Activity activity){
		int titleId = context.getResources().getIdentifier("action_bar_title", "id",
	            "android");
	    TextView yourTextView = (TextView) activity.findViewById(titleId);
	    yourTextView.setTextColor(context.getResources().getColor(R.color.white));
	    Typeface tf = Typeface.createFromAsset(context.getAssets(),
				AppExtra.AVENIRLSTD_BLACK);
	    yourTextView.setTypeface(tf);
	  }
}
