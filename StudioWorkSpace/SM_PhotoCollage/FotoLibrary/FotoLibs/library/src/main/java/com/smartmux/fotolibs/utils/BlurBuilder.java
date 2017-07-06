package com.smartmux.fotolibs.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class BlurBuilder {
	private static final float BITMAP_SCALE = 0.4f;
//	private static final float BLUR_RADIUS = 7.5f;
	static Bitmap inputBitmap, outputBitmap = null;

	@SuppressLint("NewApi")
	public static Bitmap blur(Context context, Bitmap image, float radious) {
		int width = Math.round(image.getWidth() * BITMAP_SCALE);
		int height = Math.round(image.getHeight() * BITMAP_SCALE);

		if (inputBitmap != null || outputBitmap != null) {
			inputBitmap = null;
			outputBitmap = null;
		}

		inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);

		outputBitmap = Bitmap.createBitmap(inputBitmap);

		RenderScript rs = RenderScript.create(context);
		ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs,
				Element.U8_4(rs));
		Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
		Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
		theIntrinsic.setRadius(radious);
		theIntrinsic.setInput(tmpIn);
		theIntrinsic.forEach(tmpOut);
		tmpOut.copyTo(outputBitmap);

		return outputBitmap;
	}
}