package com.smartmux.fotolibs.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.smartmux.fotolibs.R;
import com.smartmux.fotolibs.widget.ProgressWheel;

public class ProgressHUDLib extends Dialog {
	public ProgressHUDLib(Context context) {
		super(context);
	}

	public ProgressHUDLib(Context context, int theme) {
		super(context, theme);
	}


	public void onWindowFocusChanged(boolean hasFocus){
        
        ProgressWheel progressBar = (ProgressWheel) findViewById(R.id.foto_progress);
		progressBar.spin();
    }
	
	public void setMessage(CharSequence message) {
		if(message != null && message.length() > 0) {
			findViewById(R.id.message).setVisibility(View.VISIBLE);			
			TextView txt = (TextView)findViewById(R.id.message);
			txt.setText(message);
			txt.invalidate();
		}
	}
	
	public static ProgressHUDLib show(Context context, CharSequence message, boolean indeterminate) {
		ProgressHUDLib dialog = new ProgressHUDLib(context,R.style.ProgressHUDLib);
		dialog.setTitle("");
		dialog.setContentView(R.layout.progress_hud_lib);
		if(message == null || message.length() == 0) {
			dialog.findViewById(R.id.message).setVisibility(View.GONE);			
		} else {
			TextView txt = (TextView)dialog.findViewById(R.id.message);
			txt.setText(message);
		}
		
		dialog.setCancelable(false);
		dialog.getWindow().getAttributes().gravity=Gravity.CENTER;
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();  
		lp.dimAmount=0.2f;
		dialog.getWindow().setAttributes(lp); 
		//dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		dialog.show();
		return dialog;
	}	
}
