package com.smartmux.filevaultfree.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.widget.TextView;

import com.smartmux.filevaultfree.R;

public class GeneralUtils {
	
public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
	            Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	   int borderSizePx = 3;
	   int  cornerSizePx = 12;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);

	    // prepare canvas for transfer
	    paint.setAntiAlias(true);
	    paint.setColor(0xFFFFFFFF);
	    paint.setStyle(Paint.Style.FILL);
	    canvas.drawARGB(0, 0, 0, 0);
	    canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

	    // draw bitmap
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);

	    // draw border
	    paint.setColor(Color.LTGRAY);
	    paint.setStyle(Paint.Style.STROKE);
	    paint.setStrokeWidth((float) borderSizePx);
	    canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

	    return output;
    }

 public static boolean checkSdcard() {
    boolean rs = android.os.Environment.getExternalStorageState().equals(
            android.os.Environment.MEDIA_MOUNTED);


    return rs;
}
 
 @SuppressWarnings("deprecation")
public static boolean checkSdcard(Context context, 
         boolean showNotify) {
     boolean rs = android.os.Environment.getExternalStorageState().equals(
             android.os.Environment.MEDIA_MOUNTED);

     // show notification dialog
     if(showNotify && !rs && context != null){
    	 
    	 AppToast.show(context, "No Sd Card Found");
         try{
             CustomDialog dialog = new CustomDialog(context);                
             dialog.setButton(context.getString(R.string.btn_OK), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
             dialog.setTitle(R.string.sdcard_checking_title);
             dialog.setIcon(android.R.drawable.ic_dialog_alert);
             dialog.setMessage(context.getString(R.string.sdcard_checking_message));
             dialog.show();
         }catch (Exception e) {
             Log.w("Tag", e.getMessage());
         }
     }

     return rs;
 }
  
 public static boolean isApplicationBroughtToBackground(Context context) {
     ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
     List<RunningTaskInfo> tasks = am.getRunningTasks(1000);
     if (!tasks.isEmpty()) {
         ComponentName topActivity = tasks.get(0).topActivity;
         if (!topActivity.getPackageName().equals(context.getPackageName())) {
             return true;
         }
     }

     return false;
 }
 
 public static TextView setModeTitleColor(Context context,int checkedCount ) {
	  TextView text=new TextView(context);
	  text.setText(String.valueOf(checkedCount));
	  text.setTextColor(Color.parseColor("#ffffff"));
	  text.setTextSize(18);
	     return text;
	 }
}

