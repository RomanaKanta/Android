package com.smartmux.textmemo.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmux.textmemo.R;

public class AppToast {

	public static Toast toast;
	static Typeface tf;

	public static void show(Context context, String message) {
		// cancel current toast
		if (toast != null)
			toast.cancel();
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = li.inflate(R.layout.custom_toast, null);
		TextView tv = (TextView) view.findViewById(R.id.texttoast);
		tf = Typeface.createFromAsset(context.getAssets(),
				AppExtra.AVENIRLSTD_BLACK);
		tv.setTypeface(tf);
		tv.setText(message);
		toast = new Toast(context);
		toast.setView(view);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}

	// public static void show(Context context,String text){
	// Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	// }
}