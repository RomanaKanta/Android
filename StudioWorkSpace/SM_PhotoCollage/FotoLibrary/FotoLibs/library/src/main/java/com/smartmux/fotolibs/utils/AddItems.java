package com.smartmux.fotolibs.utils;

import android.content.Context;

import com.smartmux.fotolibs.R;
import com.smartmux.fotolibs.modelclass.ListData;

import java.util.ArrayList;
import java.util.Arrays;

public class AddItems {

	public static ArrayList<ListData> getFirstListItems() {

		ArrayList<ListData> array = new ArrayList<ListData>();

		ArrayList<String> textarray = new ArrayList<String>();
		ArrayList<Integer> iconarray = new ArrayList<Integer>();

		String[] text = { "Filter", "Effect", "Blur", "Adjustment",
				"Rotate", "Draw" };
		textarray.addAll(Arrays.asList(text));

		Integer[] icon = { R.drawable.filter_tool,
				R.drawable.effect_tool, R.drawable.blur_tool,
				R.drawable.adjustment_tool, R.drawable.rotate_tool,
				R.drawable.draw_tool};

		iconarray.addAll(Arrays.asList(icon));

		for (int i = 0; i < textarray.size(); i++) {
			array.add(new ListData(i, iconarray.get(i), textarray.get(i),
					"#262626"));
		}

		return array;

	}

	public static ArrayList<ListData> getFilterItems(Context c) {

		ArrayList<ListData> array = new ArrayList<ListData>();

		ArrayList<String> textarray = new ArrayList<String>();
		ArrayList<Integer> thumbarray = new ArrayList<Integer>();

		String[] text = { "None", "GrayScale", "Relief", "Aged photo", "Neon",
				"Old TV", "Invert Color", "Block", "Sharpen", "Light", "HDR",
				"Sketch", "Gotham" };

		Integer[] thumb = { R.drawable.none, R.drawable.grayscale,
				R.drawable.relief, R.drawable.aged_photo, R.drawable.neon,
				R.drawable.old_tv, R.drawable.invert_color, R.drawable.block,
				R.drawable.sharpen, R.drawable.light, R.drawable.hrd,
				R.drawable.sketch, R.drawable.gotham };

		textarray.addAll(Arrays.asList(text));
		thumbarray.addAll(Arrays.asList(thumb));

		for (int i = 0; i < textarray.size(); i++) {
			array.add(new ListData(i, thumbarray.get(i), textarray.get(i),
					"#373737"));
		}

		return array;

	}

	public static ArrayList<ListData> getEffectListItems(Context c) {

		ArrayList<ListData> array = new ArrayList<ListData>();

		ArrayList<String> textarray = new ArrayList<String>();
		ArrayList<Integer> thumbarray = new ArrayList<Integer>();

		String[] text = c.getResources().getStringArray(
				R.array.effect_list_text);

		textarray.addAll(Arrays.asList(text));

		Integer[] thumb = { R.drawable.none, R.drawable.lomo,
				R.drawable.soft_glow, R.drawable.oil_painting,
				R.drawable.pixelate };

		thumbarray.addAll(Arrays.asList(thumb));

		for (int i = 0; i < textarray.size(); i++) {
			array.add(new ListData(i, thumbarray.get(i), textarray.get(i),
					"#373737"));
		}

		return array;

	}

	public static ArrayList<ListData> getRotateListItems(Context c) {

		ArrayList<ListData> array = new ArrayList<ListData>();

		array.add(new ListData(1, R.drawable.btn_rotate, "Clockwise", "#373737"));
		array.add(new ListData(2, R.drawable.btn_flipv, "Vertical", "#373737"));
		array.add(new ListData(3, R.drawable.btn_fliph, "Horizontal", "#373737"));

		return array;

	}
}
