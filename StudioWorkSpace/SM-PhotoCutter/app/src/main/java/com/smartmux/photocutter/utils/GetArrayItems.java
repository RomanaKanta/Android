package com.smartmux.photocutter.utils;

import com.smartmux.photocutter.R;
import com.smartmux.photocutter.modelclass.ListData;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by tanvir-android on 6/13/16.
 */
public  class GetArrayItems {

    public static ArrayList<ListData> getEditOption() {

        ArrayList<ListData> array = new ArrayList<ListData>();

        ArrayList<String> textarray = new ArrayList<String>();
        String[] text = { "Left Rotate", "Right Rotate", "Flip" };

        ArrayList<Integer> iconarray = new ArrayList<Integer>();
        Integer[] icon = { R.drawable.rotate_left, R.drawable.rotate_right,
                R.drawable.flip_icon
        };

        textarray.addAll(Arrays.asList(text));
        iconarray.addAll(Arrays.asList(icon));

        for (int i = 0; i < textarray.size(); i++) {
            array.add(new ListData(i, iconarray.get(i), textarray.get(i)));
        }

        return array;
    }

    public static ArrayList<ListData> getCropShapes() {

        ArrayList<ListData> array = new ArrayList<ListData>();

        ArrayList<String> textarray = new ArrayList<String>();
        String[] text = { "Rectangle", "Oval", "Hexagon", "Octagon", "Pentagon",
                "Diamond"};

        ArrayList<Integer> iconarray = new ArrayList<Integer>();
        Integer[] icon = { R.drawable.rectangular_crop, R.drawable.oval_crop,
                R.drawable.hexagon_crop, R.drawable.octagon_crop,
                R.drawable.pentagon_crop, R.drawable.daimond_crop
        };

        textarray.addAll(Arrays.asList(text));
        iconarray.addAll(Arrays.asList(icon));

        for (int i = 0; i < textarray.size(); i++) {
            array.add(new ListData(i, iconarray.get(i), textarray.get(i)));
        }

        return array;
    }

    public static ArrayList<ListData> getFilter() {

        ArrayList<ListData> array = new ArrayList<ListData>();

        ArrayList<String> textarray = new ArrayList<String>();

        String[] text = { "None", "GrayScale", "Relief", "Aged Photo", "Neon",
                "Old TV", "Invert Color", "Block", "Sharpen", "Light", "HDR",
                "Sketch", "Gotham", "Lomo", "Soft Glow", "Posterize", "Pixelate" };


        ArrayList<Integer> iconarray = new ArrayList<Integer>();

        Integer[] icon = { R.drawable.none, R.drawable.grayscale,
                R.drawable.relief, R.drawable.aged_photo,
                R.drawable.neon, R.drawable.old_tv, R.drawable.invert_color,
                R.drawable.block, R.drawable.sharpen, R.drawable.light,
                R.drawable.hrd, R.drawable.sketch,R.drawable.gotham,
                R.drawable.lomo, R.drawable.soft_glow, R.drawable.oil_painting,
                R.drawable.pixelate
        };

        textarray.addAll(Arrays.asList(text));
        iconarray.addAll(Arrays.asList(icon));

        for (int i = 0; i < textarray.size(); i++) {
            array.add(new ListData(i, iconarray.get(i), textarray.get(i)));
        }
        return array;
    }

}
