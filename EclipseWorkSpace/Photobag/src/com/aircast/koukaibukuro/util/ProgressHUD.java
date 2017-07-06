package com.aircast.koukaibukuro.util;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.aircast.photobag.R;


public class ProgressHUD extends Dialog {
	public ProgressHUD(Context context) {
		super(context,R.style.ProgressHUD);
	}

	public ProgressHUD(Context context, int theme) {
		super(context, theme);
	}


	public void onWindowFocusChanged(boolean hasFocus){
		ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        spinner.start();

    }
	
	
	public ProgressHUD show(boolean indeterminate) {
		
		setTitle("");
		setContentView(R.layout.kb_progress_hud);
		
		setCancelable(false);
		getWindow().getAttributes().gravity=Gravity.CENTER;
		WindowManager.LayoutParams lp = getWindow().getAttributes();  
		lp.dimAmount=0.2f;
		getWindow().setAttributes(lp); 
		//dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		show();
		return this;
	}	
}
