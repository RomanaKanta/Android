package com.smartmux.banglaebook.plus.util;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Util {

    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {

            InputStream is = context.getAssets().open(fileName);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

	public static String loadJSONFromAsset(Context context) {
		String json = null;
		try {

			InputStream is = context.getAssets().open("data.json");

			int size = is.available();

			byte[] buffer = new byte[size];

			is.read(buffer);

			is.close();

			json = new String(buffer, "UTF-8");

		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return json;

	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

//	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
//
//		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
//				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//		Canvas canvas = new Canvas(output);
//		int borderSizePx = 2;
//		int cornerSizePx = 12;
//		final Paint paint = new Paint();
//		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//		final RectF rectF = new RectF(rect);
//
//		// prepare canvas for transfer
//		paint.setAntiAlias(true);
//		paint.setColor(0xFFFFFFFF);
//		paint.setStyle(Paint.Style.FILL);
//		canvas.drawARGB(0, 0, 0, 0);
//		canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);
//
//		// draw bitmap
//		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//		canvas.drawBitmap(bitmap, rect, rect, paint);
//
//		// draw border
//		paint.setColor(Color.LTGRAY);
//		paint.setStyle(Paint.Style.STROKE);
//		paint.setStrokeWidth((float) borderSizePx);
//		canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);
//
//		return output;
//	}

	public static boolean isOnline(Context ctx) {
		try {
			ConnectivityManager cm = (ConnectivityManager) ctx
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		} catch (Exception e) {
			return false;
		}
	}


}
