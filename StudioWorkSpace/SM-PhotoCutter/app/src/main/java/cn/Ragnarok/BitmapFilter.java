package cn.Ragnarok;

import android.graphics.Bitmap;

public class BitmapFilter {
	/**
	 * filter style id;
	 */

	public static final int NONE = 0; // none
	public static final int GRAY_STYLE = 1; // gray scale
	public static final int RELIEF_STYLE = 2; // relief
	public static final int OLD_STYLE = 3; // oil painting
	public static final int NEON_STYLE = 4; // neon
	public static final int TV_STYLE = 5; // Old TV
	public static final int INVERT_STYLE = 6; // invert the colors
	public static final int BLOCK_STYLE = 7; // engraving
	public static final int SHARPEN_STYLE = 8; // sharpen
	public static final int LIGHT_STYLE = 9; // light
	public static final int HDR_STYLE = 10; // HDR
	public static final int SKETCH_STYLE = 11; // sketch style
	public static final int GOTHAM_STYLE = 12; // gotham style
    public static final int SPOT = 13;
    public static final int SOFTGLOW = 14;
    public static final int POSTERIZE = 15;
    public static final int PIXELATE = 16;


    public static final int TOTAL_FILTER_NUM = GOTHAM_STYLE;

	/**
	 * change bitmap filter style
	 * 
	 * @param bitmap
	 * @param /styeNo
	 *            , filter sytle id
	 */
	public static Bitmap changeStyle(Bitmap bitmap, int styleNo,
			Object... options) {

		if (styleNo == GRAY_STYLE) {
			return GrayFilter.changeToGray(bitmap);
		} else if (styleNo == RELIEF_STYLE) {
			return ReliefFilter.changeToRelief(bitmap);
		} else if (styleNo == NEON_STYLE) {
			if (options.length < 3) {
				return NeonFilter.changeToNeon(bitmap, 200, 50, 100);
			}
			return NeonFilter.changeToNeon(bitmap, (Integer) options[0],
					(Integer) options[1], (Integer) options[2]);
		} else if (styleNo == TV_STYLE) {
			return TvFilter.changeToTV(bitmap);
		} else if (styleNo == INVERT_STYLE) {
			return InvertFilter.chageToInvert(bitmap);
		} else if (styleNo == BLOCK_STYLE) {
			return BlockFilter.changeToBrick(bitmap);
		} else if (styleNo == OLD_STYLE) {
			return OldFilter.changeToOld(bitmap);
		} else if (styleNo == SHARPEN_STYLE) {
			return SharpenFilter.changeToSharpen(bitmap);
		} else if (styleNo == LIGHT_STYLE) {
			if (options.length < 3) {
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				return LightFilter.changeToLight(bitmap, width / 2, height / 2,
						Math.min(width / 2, height / 2));
			}
			return LightFilter.changeToLight(bitmap, (Integer) options[0],
					(Integer) options[1], (Integer) options[2]); // centerX,
																	// centerY,
																	// radius
		} else if (styleNo == HDR_STYLE) {
			return HDRFilter.changeToHDR(bitmap);
		} else if (styleNo == SKETCH_STYLE) {
			return SketchFilter.changeToSketch(bitmap);
		} else if (styleNo == GOTHAM_STYLE) {
			return GothamFilter.changeToGotham(bitmap);
		}else if (styleNo == SPOT) {
            double radius = (bitmap.getWidth() / 3) * 95 / 100;
            return LomoFilter.changeToLomo(bitmap, radius);
        }else if (styleNo == SOFTGLOW) {
            return SoftGlowFilter.softGlowFilter(bitmap, 0.6);
        }else if (styleNo == POSTERIZE) {
            return OilFilter.changeToOil(bitmap, 2);
        }else if (styleNo == PIXELATE) {
            return PixelateFilter.changeToPixelate(bitmap, 10);
        }

        return bitmap;
	}

}