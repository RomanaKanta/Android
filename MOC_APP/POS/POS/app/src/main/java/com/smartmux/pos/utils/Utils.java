package com.smartmux.pos.utils;

import java.util.Random;

/**
 * Created by smartmux on 6/1/16.
 */
public class Utils {

    public static int getRandomValue(int max,int min){

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
