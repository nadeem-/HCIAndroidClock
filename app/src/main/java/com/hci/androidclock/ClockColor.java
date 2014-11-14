package com.hci.androidclock;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by jamesbwills on 11/13/14.
 */
public class ClockColor {

    // Colors taken from http://flatuicolors.com/
    public static final int TURQUOISE = Color.parseColor("#1ABC9C");
    public static final int DARK_TURQUOISE = Color.parseColor("#16A085");
    public static final int YELLOW = Color.parseColor("#f1C40f");
    public static final int ORANGE = Color.parseColor("#f39C12");
    public static final int CARROT = Color.parseColor("#E67E22");
    public static final int PUMPKIN = Color.parseColor("#d35400");
    public static final int RED = Color.parseColor("#e74c3c");
    public static final int DARK_RED = Color.parseColor("#c0392b");
    public static final int WHITE = Color.parseColor("#ecf0f1");
    public static final int LIGHT_GRAY = Color.parseColor("#bdc3c7");
    public static final int MED_GRAY = Color.parseColor("#95a5a6");
    public static final int DARK_GRAY = Color.parseColor("#7f8c8d");
    public static final int GREEN = Color.parseColor("#2ecc71");
    public static final int DARK_GREEN = Color.parseColor("#27ae60");
    public static final int PURPLE = Color.parseColor("#9b59b6");
    public static final int DARK_PURPLE = Color.parseColor("#8e44ad");
    public static final int LIGHT_BLUE = Color.parseColor("#3498db");
    public static final int MED_BLUE = Color.parseColor("#2980b9");
    public static final int DARK_BLUE = Color.parseColor("#34495e");
    public static final int MIDNIGHT_BLUE = Color.parseColor("#2c3e50");

    private static final int[] allColors = new int[] {
        TURQUOISE,
        DARK_TURQUOISE,
        YELLOW,
        ORANGE,
        CARROT,
        PUMPKIN,
        RED,
        DARK_RED,
        WHITE,
        LIGHT_GRAY,
        MED_GRAY,
        DARK_GRAY,
        GREEN,
        DARK_GREEN,
        PURPLE,
        DARK_PURPLE,
        LIGHT_BLUE,
        MED_BLUE,
        DARK_BLUE,
        MIDNIGHT_BLUE
    };

    public static int[] getAllColors() {
        return allColors;
    }

    public static int getRandomColor() {
        Random r = new Random();
        return allColors[r.nextInt(allColors.length)];
    }

    public static int getDifferentRandomColor(int oldColor) {
        Random r = new Random();

        int newColor = allColors[r.nextInt(allColors.length)];
        while (newColor == oldColor) {
            newColor = allColors[r.nextInt(allColors.length)];
        }

        return newColor;
    }
}
