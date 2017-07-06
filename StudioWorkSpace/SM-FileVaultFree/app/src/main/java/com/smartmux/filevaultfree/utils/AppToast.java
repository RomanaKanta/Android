package com.smartmux.filevaultfree.utils;

import android.content.Context;
import android.widget.Toast;

public class AppToast extends Toast {
	
	public AppToast(Context context) {
		super(context);
	}

	public static void show(Context context,String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
