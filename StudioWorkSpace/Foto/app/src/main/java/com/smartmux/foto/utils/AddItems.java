package com.smartmux.foto.utils;

import android.content.Context;

import com.smartmux.foto.R;
import com.smartmux.foto.modelclass.FrameModel;
import com.smartmux.foto.modelclass.ListData;

import java.util.ArrayList;
import java.util.Arrays;

public class AddItems {

    public static ArrayList<ListData> getFirstListItems() {

        ArrayList<ListData> array = new ArrayList<ListData>();

        ArrayList<String> textarray = new ArrayList<String>();
        ArrayList<Integer> iconarray = new ArrayList<Integer>();
        ArrayList<String> bgcolorarray = new ArrayList<String>();

        String[] text = { "Filter", "Frame", "Effect", "Blur", "Adjustment",
                "Rotate", "Draw", "Crop", "Resize", "Sticker", "Text" };
        textarray.addAll(Arrays.asList(text));

        Integer[] icon = { R.drawable.filter_tool, R.drawable.frame_tool,
                R.drawable.effect_tool, R.drawable.blur_tool,
                R.drawable.adjustment_tool, R.drawable.rotate_tool,
                R.drawable.draw_tool, R.drawable.clipping_tool,
                R.drawable.resize_tool, R.drawable.sticker_tool,
                R.drawable.text_tool };

        iconarray.addAll(Arrays.asList(icon));

        String[] bg_color = { "#D10042", "#4F2F9E", "#DB4D01", "#29A019",
                "#D10042", "#4F2F9E", "#DB4D01", "#29A019", "#D10042",
                "#4F2F9E", "#DB4D01" };

        bgcolorarray.addAll(Arrays.asList(bg_color));

        for (int i = 0; i < textarray.size(); i++) {
            array.add(new ListData(i, iconarray.get(i), textarray.get(i),
                    bgcolorarray.get(i)));
        }

        return array;

    }

    public static ArrayList<ListData> getFilterItems(Context c) {

        ArrayList<ListData> array = new ArrayList<ListData>();

        ArrayList<String> textarray = new ArrayList<String>();
        ArrayList<Integer> thumbarray = new ArrayList<Integer>();

        // String[] text = { "None", "GrayScale", "Relief", "Average Blur",
        // "Oil Painting", "Neon", "Pixelate", "Old TV", "Invert Color",
        // "Block", "Aged photo", "Sharpen", "Light", "Lomo", "HDR",
        // "Gaussian Blur", "Soft Glow", "Sketch", "Motion Blur", "Gotham" };
        //
        // Integer[] thumb = { R.drawable.none, R.drawable.grayscale,
        // R.drawable.relief, R.drawable.average_blur,
        // R.drawable.oil_painting, R.drawable.neon, R.drawable.pixelate,
        // R.drawable.old_tv, R.drawable.invert_color, R.drawable.block,
        // R.drawable.aged_photo, R.drawable.sharpen, R.drawable.light,
        // R.drawable.lomo, R.drawable.hrd, R.drawable.gaussian_blur,
        // R.drawable.soft_glow, R.drawable.sketch,
        // R.drawable.motion_blur, R.drawable.gotham };

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
        // <string-array name="effect_list_text">
        // <item>None</item>
        // <item>Lomo</item>
        // <!-- <item>Hue</item> -->
        // <!-- <item>Hightlight</item> -->
        // <item>Soft Glow</item>
        // <item>Posterize</item>
        // <item>Pixelate</item>
        // </string-array>
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

    public static ArrayList<ListData> getBlurListItems(Context c) {

        ArrayList<ListData> array = new ArrayList<ListData>();

        ArrayList<String> textarray = new ArrayList<String>();

        String[] text = c.getResources().getStringArray(R.array.blur_list_text);

        textarray.addAll(Arrays.asList(text));

        for (int i = 0; i < textarray.size(); i++) {
            array.add(new ListData(i, R.drawable.blur_tool, textarray.get(i),
                    "#373737"));
        }

        return array;

    }

    public static ArrayList<String> getTextFontItems(Context c) {

        ArrayList<String> array = new ArrayList<String>();

//		ArrayList<String> textarray = new ArrayList<String>();

        String[] text = c.getResources().getStringArray(R.array.text_font);

        array.addAll(Arrays.asList(text));

//		for (int i = 0; i < textarray.size(); i++) {
//			array.add(textarray.get(i));
//		}

        return array;

    }


    public static ArrayList<ListData> getRotateListItems(Context c) {

        ArrayList<ListData> array = new ArrayList<ListData>();

        array.add(new ListData(1, R.drawable.btn_rotate, "Clockwise", "#373737"));
        array.add(new ListData(2, R.drawable.btn_flipv, "Vertical", "#373737"));
        array.add(new ListData(3, R.drawable.btn_fliph, "Horizontal", "#373737"));

        return array;

    }

    public static ArrayList<ListData> getCropVarticalListItems(Context c) {

        ArrayList<ListData> array = new ArrayList<ListData>();

        ArrayList<String> textarray = new ArrayList<String>();
        ArrayList<Integer> thumbarray = new ArrayList<Integer>();

        String[] text = c.getResources().getStringArray(
                R.array.crop_list_varti_text);

        textarray.addAll(Arrays.asList(text));

        Integer[] thumb = { R.drawable.crop_1_1, R.drawable.crop_1_1,
                R.drawable.crop_3_4, R.drawable.crop_2_3, R.drawable.crop_9_16 };

        thumbarray.addAll(Arrays.asList(thumb));

        for (int i = 0; i < textarray.size(); i++) {
            array.add(new ListData(i, thumbarray.get(i), textarray.get(i),
                    "#373737"));
        }

        return array;

    }

    public static ArrayList<ListData> getCropHorizontalListItems(Context c) {

        ArrayList<ListData> array = new ArrayList<ListData>();

        ArrayList<String> textarray = new ArrayList<String>();
        ArrayList<Integer> thumbarray = new ArrayList<Integer>();

        String[] text = c.getResources().getStringArray(
                R.array.crop_list_hori_text);

        textarray.addAll(Arrays.asList(text));

        Integer[] thumb = { R.drawable.crop_1_1, R.drawable.crop_1_1,
                R.drawable.crop_4_3, R.drawable.crop_3_2, R.drawable.crop_16_9 };

        thumbarray.addAll(Arrays.asList(thumb));

        for (int i = 0; i < textarray.size(); i++) {
            array.add(new ListData(i, thumbarray.get(i), textarray.get(i),
                    "#373737"));
        }

        return array;

    }

    public static ArrayList<ListData> getResizeListItems(Context c) {

        ArrayList<ListData> array = new ArrayList<ListData>();

        ArrayList<String> textarray = new ArrayList<String>();

        String[] text = c.getResources().getStringArray(
                R.array.resize_list_text);

        textarray.addAll(Arrays.asList(text));

        for (int i = 0; i < textarray.size(); i++) {
            array.add(new ListData(i, R.drawable.resize_tool, textarray.get(i),
                    "#373737"));
        }

        return array;

    }

    public static ArrayList<ListData> getTextListItems(Context c) {

        ArrayList<ListData> array = new ArrayList<ListData>();

        ArrayList<String> textarray = new ArrayList<String>();
        ArrayList<Integer> iconarray = new ArrayList<Integer>();

        String[] text = c.getResources().getStringArray(R.array.text_list_text);

        textarray.addAll(Arrays.asList(text));

        Integer[] icon = { R.drawable.btn_add, R.drawable.text_tool,
                R.drawable.effect_tool, R.drawable.btn_font,
                R.drawable.btn_align_left, R.drawable.btn_align_center,
                R.drawable.btn_align_right };

        iconarray.addAll(Arrays.asList(icon));

        for (int i = 0; i < textarray.size(); i++) {
            array.add(new ListData(i, iconarray.get(i), textarray.get(i),
                    "#373737"));
        }

        return array;

    }

    public static ArrayList<ListData> getStickerItems(Context c) {

        ArrayList<ListData> array = new ArrayList<ListData>();
        ArrayList<Integer> iconarray = new ArrayList<Integer>();
        Integer[] icon = { R.drawable.illust_006, R.drawable.illust_007,
                R.drawable.illust_008, R.drawable.illust_009,
                R.drawable.illust_010, R.drawable.illust_011,
                R.drawable.illust_012, R.drawable.illust_013,
                R.drawable.illust_014, R.drawable.illust_015,
                R.drawable.illust_016, R.drawable.illust_017,
                R.drawable.illust_018, R.drawable.illust_019,
                R.drawable.illust_020, R.drawable.illust_021,
                R.drawable.illust_022, R.drawable.illust_023,
                R.drawable.illust_024, R.drawable.illust_025,
                R.drawable.illust_026, R.drawable.illust_027,
                R.drawable.illust_028, R.drawable.illust_029,
                R.drawable.illust_030, R.drawable.illust_031,
                R.drawable.illust_032, R.drawable.illust_033,
                R.drawable.illust_034, R.drawable.illust_035,
                R.drawable.illust_036, R.drawable.illust_037,
                R.drawable.illust_0371, R.drawable.illust_0372,
                R.drawable.illust_0373, R.drawable.illust_038,
                R.drawable.illust_039, R.drawable.illust_040,
                R.drawable.illust_041, R.drawable.illust_042,
                R.drawable.illust_046, R.drawable.illust_047,
                R.drawable.illust_048, R.drawable.illust_049,
                R.drawable.illust_050, R.drawable.illust_051,
                R.drawable.illust_052, R.drawable.illust_053,
                R.drawable.illust_054, R.drawable.illust_055,
                R.drawable.illust_056, R.drawable.illust_057,
                R.drawable.illust_058, R.drawable.illust_059,
                R.drawable.illust_060, R.drawable.illust_061,
                R.drawable.illust_062, R.drawable.illust_063,
                R.drawable.illust_064, R.drawable.illust_065,
                R.drawable.illust_066, R.drawable.illust_067,
                R.drawable.illust_068, R.drawable.illust_069,
                R.drawable.illust_070, R.drawable.illust_071,
                R.drawable.illust_072, R.drawable.illust_073,
                R.drawable.illust_074, R.drawable.illust_075,
                R.drawable.illust_076, R.drawable.illust_077,
                R.drawable.illust_078, R.drawable.illust_079,
                R.drawable.illust_080, R.drawable.illust_081,
                R.drawable.illust_082, R.drawable.illust_083,
                R.drawable.illust_084, R.drawable.illust_085,
                R.drawable.illust_086, R.drawable.illust_087 };

        iconarray.addAll(Arrays.asList(icon));

        for (int i = 0; i < iconarray.size(); i++) {
            array.add(new ListData(i, iconarray.get(i), "", "#373737"));
        }

        // array.add(new ListData(1, R.drawable.sunglass, "", "#373737"));
        // array.add(new ListData(2, R.drawable.sunglass_two, "", "#373737"));
        // array.add(new ListData(3, R.drawable.sunglass_three, "", "#373737"));
        // array.add(new ListData(4, R.drawable.sunglass, "", "#373737"));
        // array.add(new ListData(5, R.drawable.sunglass_two, "", "#373737"));
        // array.add(new ListData(6, R.drawable.sunglass_three, "", "#373737"));
        // array.add(new ListData(7, R.drawable.sunglass, "", "#373737"));
        // array.add(new ListData(8, R.drawable.sunglass_two, "", "#373737"));
        // array.add(new ListData(9, R.drawable.sunglass_three, "", "#373737"));
        return array;
    }

    public static ArrayList<FrameModel> getFrameItems(Context c) {

        ArrayList<FrameModel> array = new ArrayList<FrameModel>();

        array = new ArrayList<FrameModel>();
        ArrayList<Integer> thumbarray = new ArrayList<Integer>();
        ArrayList<Integer> framearray = new ArrayList<Integer>();
        // ArrayList<Integer> landarray = new ArrayList<Integer>();

        Integer[] thumb = {R.drawable.frame_ic_1,
                R.drawable.frame_ic_2, R.drawable.frame_ic_3,
                R.drawable.frame_ic_4, R.drawable.frame_ic_5,
                R.drawable.frame_ic_6, R.drawable.frame_ic_7,
                R.drawable.frame_ic_8 , R.drawable.frame_ic_9,
                R.drawable.frame_ic_10 };
        thumbarray.addAll(Arrays.asList(thumb));

        Integer[] frame = { R.drawable.frame1, R.drawable.frame2,
                R.drawable.frame3, R.drawable.frame4,
                R.drawable.frame5, R.drawable.frame6,
                R.drawable.frame7, R.drawable.frame8,
                R.drawable.frame9, R.drawable.frame10 };
        framearray.addAll(Arrays.asList(frame));

        // Integer[] land = { R.drawable.illust_006, R.drawable.illust_007 };
        // landarray.addAll(Arrays.asList(land));

        for (int i = 0; i < thumbarray.size(); i++) {
            array.add(new FrameModel(i, thumbarray.get(i), framearray.get(i)));
        }

        // array.add(new FrameModel(1, R.drawable.thumb_1, R.drawable.port_1,
        // R.drawable.land_1));
        //
        // array.add(new FrameModel(2, R.drawable.thumb_2, R.drawable.port_2,
        // R.drawable.land_2));
        //
        // array.add(new FrameModel(3, R.drawable.thumb_3, R.drawable.port_3,
        // R.drawable.land_3));
        //
        // array.add(new FrameModel(4, R.drawable.thumb_4, R.drawable.port_4,
        // R.drawable.land_4));
        //
        // array.add(new FrameModel(5, R.drawable.thumb_5, R.drawable.port_5,
        // R.drawable.land_5));
        //
        // array.add(new FrameModel(6, R.drawable.thumb_6, R.drawable.port_6,
        // R.drawable.land_6));
        //
        // array.add(new FrameModel(7, R.drawable.thumb_7, R.drawable.port_7,
        // R.drawable.land_7));
        //
        // array.add(new FrameModel(8, R.drawable.thumb_8, R.drawable.port_8,
        // R.drawable.land_8));
        //
        // array.add(new FrameModel(9, R.drawable.thumb_9, R.drawable.port_9,
        // R.drawable.land_9));
        //
        // array.add(new FrameModel(10, R.drawable.thumb_10, R.drawable.port_10,
        // R.drawable.land_10));
        //
        // array.add(new FrameModel(11, R.drawable.thumb_11, R.drawable.port_11,
        // R.drawable.land_11));

        return array;
    }

}
