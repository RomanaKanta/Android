package com.smartmux.textmemo.utils;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ParseException;
import android.widget.TextView;

import com.smartmux.textmemo.modelclass.NoteListItem;

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
 
 public static String removeExtension(String fileName){
	
	 int dotIndex=fileName.lastIndexOf('.');
	 if(dotIndex>=0) { // to prevent exception if there is no dot
	   fileName=fileName.substring(0,dotIndex);
	 }
	 return fileName;
 }
 
 public static class DateComparator implements Comparator<NoteListItem> {
		private Date date1 = null;
		private Date date2 = null;


		@Override
		public int compare(NoteListItem first, NoteListItem second) {
			String firstValue = first.getDateTime();
			String secondValue = second.getDateTime();
			SimpleDateFormat format = new SimpleDateFormat(
					"dd/MM/yyyy HH:mm:ss");
			try {
				if (firstValue.equals("null")) {

					firstValue = String.valueOf(0);
				}
				if (secondValue.equals("null")) {

					secondValue = String.valueOf(0);
				}
				try {
					date1 = format.parse(firstValue);
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
				try {
					date2 = format.parse(secondValue);
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (date1 == null || date2 == null) {
				return 0;
			}

			return (date1.getTime() > date2.getTime() ? -1 : 1);
		}
	}
 
 
 public static class TitleComparator implements Comparator<NoteListItem> {
		public static final int ASC = 1;
		public static final int DESC = -1;

		public int compare(NoteListItem first, NoteListItem second) {

			String arg0 = first.getTitle();
			String arg1 = second.getTitle();

//			if (!(arg0 instanceof Comparable) || !(arg1 instanceof Comparable)) {
//				// throw new
//				// IllegalArgumentException("arg0 & arg1 must implements interface of java.lang.Comparable.");
//			}
//			if (arg0 == null && arg1 == null) {
//				return 0;
//			} else if (arg0 == null) {
//				return 1;
//			} else if (arg1 == null) {
//				return -1;
//			}

//			return arg0.compareTo(arg1);
		         return arg0.toLowerCase().compareTo(arg1.toLowerCase());
		}

	}
 
	public static class NoteSizeComparator implements Comparator<NoteListItem> {
		public static final int ASC = 1;
		public static final int DESC = -1;
		private int sort = ASC;

		public int compare(NoteListItem first, NoteListItem second) {
			long firstValue = first.getFileSizeInLong();
			long secondValue = second.getFileSizeInLong();

			return (firstValue < secondValue ? -1
					: (firstValue == firstValue ? 0 : 1));

		}
	}
 
}

