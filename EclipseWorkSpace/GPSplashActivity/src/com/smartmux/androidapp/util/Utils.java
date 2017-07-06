package com.smartmux.androidapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Utils {

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

	public static int randInt(int min, int max) {

		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public static List<Integer> getGameModel(int count, String type,
			List<Integer> gameNumberList) {

		int size = count;

		Log.d("size", "" + size);
		Log.d("gameNumberList", "" + gameNumberList);
		List<Integer> sortingListRandomOrder = getRandomArray(size - 1, size);

		Log.d("sortingListRandomOrder", "" + sortingListRandomOrder);

		List<Integer> sortingListOrder = new ArrayList<Integer>();

		/* if easy */
		if (type.equals(Constant.EASY)) {

			int middleIndexValue = sortingListRandomOrder.get(4);
			int index1 = sortingListRandomOrder.indexOf(4);
			int index2 = sortingListRandomOrder.indexOf(middleIndexValue);
			Collections.swap(sortingListRandomOrder, index1, index2);
		}

		sortingListOrder = getListSerials(size);

		for (int i = 0; i < gameNumberList.size(); i++) {
			sortingListOrder.set(sortingListRandomOrder.get(i),
					gameNumberList.get(i));
		}
		if (type.equals(Constant.EASY)) {

			sortingListOrder.set(gameNumberList.size(), -1);
			for (int j = gameNumberList.size() + 1; j < size; j++) {

				sortingListOrder.set(sortingListRandomOrder.get(j),
						gameNumberList.get(j - (gameNumberList.size() + 1)));
			}
		} else {

			for (int j = gameNumberList.size(); j < size; j++) {

				Log.d("j", "" + j);
				sortingListOrder.set(sortingListRandomOrder.get(j),
						gameNumberList.get(j - gameNumberList.size()));
			}
		}

		return sortingListOrder;
	}

	private static List<Integer> getListSerials(int size) {

		List<Integer> list = new ArrayList<Integer>();

		for (int i = 0; i < size; i++) {

			list.add(i);
		}
		return list;

	}

	private static List<Integer> getRandomArray(int size, int range) {

		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {

			int rand = Utils.randInt(0, size);
			if (!list.contains(rand)) {
				list.add(rand);
			}
			if (i == (size - 1) && list.size() != range) {

				i = 0;
			}
		}
		return list;

	}

	public static Drawable getImage(Context context, String name) {
		try {
			InputStream ims = context.getAssets().open("images/" + name);
			Drawable drawable = Drawable.createFromStream(ims, null);

			return drawable;
		} catch (IOException ex) {
			return null;
		}

	}

}
