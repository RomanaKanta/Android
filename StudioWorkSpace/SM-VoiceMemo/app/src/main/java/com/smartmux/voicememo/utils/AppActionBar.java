package com.smartmux.voicememo.utils;

import com.smartmux.voicememo.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class AppActionBar{

	@SuppressLint("NewApi")
	public static void updateAppActionBar(ActionBar actionBar,Context context,boolean backButtonEnable)
	{
		//actionBar.setIcon(android.R.color.transparent);
		Drawable d = context.getResources().getDrawable(R.drawable.toolbar);
		actionBar.setBackgroundDrawable(d);
		actionBar.setDisplayHomeAsUpEnabled(backButtonEnable);
	}
}
