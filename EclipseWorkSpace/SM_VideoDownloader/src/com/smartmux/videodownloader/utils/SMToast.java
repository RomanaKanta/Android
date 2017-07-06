package com.smartmux.videodownloader.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smartmux.videodownloader.R;

public class SMToast {
    private static Toast toast;
    private static Handler handler = new Handler();
    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            toast.cancel();
        }
    };

    public static void show(Context context, int stringResId) {
        show(context, context.getString(stringResId));
    }

    public static void show(Context context, String text) {
        handler.removeCallbacks(runnable);
        if (toast != null) {
            toast.setText(text);
        } else {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        handler.postDelayed(runnable, 2000);
        toast.show();
    }
    
    
	public static void showToast(Activity activity,String mgs) {

	LayoutInflater inflater = activity.getLayoutInflater();
	View layouttoast = inflater.inflate(R.layout.toastcustom,
			(ViewGroup)activity.findViewById(R.id.toastcustom));
	((TextView) layouttoast.findViewById(R.id.texttoast)).setText(mgs);

	Toast mytoast = new Toast(activity);
	mytoast.setView(layouttoast);
	mytoast.setGravity(Gravity.CENTER, 0, 0);
	mytoast.setDuration(500);
	mytoast.show();

}
}
