package com.aircast.koukaibukuro.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;

import com.aircast.koukaibukuro.model.Password;
import com.aircast.photobag.activity.PBMainTabBarActivity;
import com.aircast.photobag.database.PBDatabaseManager;

public class Util {

	public static List<Password> getPasswordList(JSONArray jsonArray) {
		PBDatabaseManager mDatabaseManager = PBDatabaseManager
				.getInstance(PBMainTabBarActivity.sMainContext);
		List<Password> list = new ArrayList<Password>();

		try {

			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject obj = jsonArray.getJSONObject(i);
				Password mPassword = new Password();
				mPassword.setNickName(obj.getString("nickname"));
				mPassword.setPassword(obj.getString("password"));
				mPassword.setCreatedDate(obj.getString("created"));
				mPassword.setPhotoCount(Integer.parseInt(obj
						.getString("photos_count")));
				mPassword.setNumberOfDownload(Integer.parseInt(obj
						.getString("downloaded_users_count")));
				mPassword.setFavorite(Integer.parseInt(obj
						.getString("favourite")));
				mPassword.setHoney(Integer.parseInt(obj.getString("honey")));
				mPassword.setThumbURL(obj.getString("thumb_url"));
				mPassword.setExpiresAT(obj.getString("expires_at"));
				mPassword.setExpiredTime(obj.getString("expires_time"));
				mPassword.setChargesTime(obj.getString("charges_time"));
				mPassword.setNewItem(Integer.parseInt(obj.getString("new")));
				mPassword.setDownload(false);
				String collectionId = mDatabaseManager.getCollectionId(obj
						.getString("password"));
				if (mDatabaseManager.isPasswordExistsInSentItems(collectionId)) {

					mPassword.setDownload(true);
				}

				mPassword.setRecommend(Integer.parseInt(obj
						.getString("recommended")));

				list.add(mPassword);

			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return list;
	}

	public static List<Password> getRecommenedPasswordList(JSONArray jsonArray) {
		PBDatabaseManager mDatabaseManager = PBDatabaseManager
				.getInstance(PBMainTabBarActivity.sMainContext);
		List<Password> list = new ArrayList<Password>();

		try {

			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject obj = jsonArray.getJSONObject(i);
				Password mPassword = new Password();
				mPassword.setNickName(obj.getString("nickname"));
				mPassword.setPassword(obj.getString("password"));
				mPassword.setCreatedDate(obj.getString("created"));
				mPassword.setPhotoCount(Integer.parseInt(obj
						.getString("photos_count")));
				mPassword.setNumberOfDownload(Integer.parseInt(obj
						.getString("downloaded_users_count")));
				mPassword.setFavorite(Integer.parseInt(obj
						.getString("favourite")));
				mPassword.setHoney(Integer.parseInt(obj.getString("honey")));
				mPassword.setThumbURL(obj.getString("thumb_url"));
				mPassword.setExpiresAT(obj.getString("expires_at"));
				mPassword.setExpiredTime(obj.getString("expires_time"));
				mPassword.setChargesTime(obj.getString("charges_time"));
				mPassword.setNewItem(Integer.parseInt(obj.getString("new")));
				mPassword.setDownload(false);
				mPassword.setRecommend(Integer.parseInt(obj
						.getString("recommended")));
				boolean isDownload = false;
				String collectionId = mDatabaseManager.getCollectionId(obj
						.getString("password"));
				if (mDatabaseManager.isPasswordExistsInSentItems(collectionId)) {

					isDownload = true;
				}

				if (!isDownload) {

					list.add(mPassword);
				}

			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return list;
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

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		int borderSizePx = 2;
		int cornerSizePx = 12;
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

	public static boolean isOnline(Context ctx) {
		try {
			ConnectivityManager cm = (ConnectivityManager) ctx
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		} catch (Exception e) {
			return false;
		}
	}

	public static class DateComparator implements Comparator<Password> {
		private Date date1 = null;
		private Date date2 = null;

		@SuppressLint("SimpleDateFormat")
		public int compare(Password first, Password second) {
			// TODO: Null checking, both for maps and values
			String firstValue = first.getCreatedDate();
			String secondValue = second.getCreatedDate();
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			try {
				if (firstValue.equals("null")) {

					firstValue = String.valueOf(0);
				}
				if (secondValue.equals("null")) {

					secondValue = String.valueOf(0);
				}
				date1 = format.parse(firstValue);
				date2 = format.parse(secondValue);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (date1 == null || date2 == null) {
				return 0;
			}

			return (date1.getTime() > date2.getTime() ? -1 : 1);
		}
	}

	public static class NumberOfDownloadComparator implements
			Comparator<Map<String, String>> {
		private final String key;

		public NumberOfDownloadComparator(String key) {
			this.key = key;
		}

		public int compare(Map<String, String> first, Map<String, String> second) {
			// TODO: Null checking, both for maps and values
			int firstValue = Integer.parseInt(first.get(key));
			int secondValue = Integer.parseInt(second.get(key));

			if (firstValue == secondValue) {
				return 0;
			}

			if (firstValue != secondValue) {
				return (firstValue > secondValue ? -1 : 1);
			}

			return 0;
		}
	}

	public static class NumberOfDownloadComparatorAtik implements
			Comparator<Map<String, String>> {
		private final String key;
		public static final int ASC = 1;
		public static final int DESC = -1;
		private int sort = DESC;

		public NumberOfDownloadComparatorAtik(String key) {
			this.key = key;
		}

		public int compare(Map<String, String> first, Map<String, String> second) {
			// TODO: Null checking, both for maps and values
			String firstValue = first.get(key);
			String secondValue = second.get(key);

			if (!(firstValue instanceof Comparable)
					|| !(secondValue instanceof Comparable)) {
				// throw new
				// IllegalArgumentException("arg0 & arg1 must implements interface of java.lang.Comparable.");
				System.out
						.println("Throw exception illegal argument  for compare");
			}
			if (firstValue == null && secondValue == null) {
				return 0; // arg0 = arg1
			} else if (firstValue == null) {
				return 1 * sort; // arg1 > arg2
			} else if (secondValue == null) {
				return -1 * sort; // arg1 < arg2
			}

			// return
			// ((Comparable)firstValue).compareTo((Comparable)secondValue) *
			// sort;
			return (firstValue).compareTo(secondValue) * sort;

		}
	}

	public static class NumberOfDownloadComparatorUpdated implements
			Comparator<Password> {
		public static final int ASC = 1;
		public static final int DESC = -1;

		public int compare(Password first, Password second) {

			int firstValue = first.getNumberOfDownload();
			int secondValue = second.getNumberOfDownload();

			// if (!(arg0 instanceof Comparable) || !(arg1 instanceof
			// Comparable)) {
			// // throw new
			// IllegalArgumentException("arg0 & arg1 must implements interface of java.lang.Comparable.");
			// }
			// if (firstValue == null && secondValue == null) {
			// return 0;
			// } else if (firstValue == null) {
			// return 1 ;
			// } else if (secondValue == null) {
			// return -1 ;
			// }

			if (firstValue > secondValue) {
				return -1;
			}
			if (firstValue < secondValue) {
				return 1;
			}

			return 0;
		}

	}

	public static List<Password> cloneList(List<Password> passwordList) {
		List<Password> clonedList = new ArrayList<Password>(passwordList.size());
		for (Password password : passwordList) {
			clonedList.add(password);
		}
		return clonedList;
	}

}
