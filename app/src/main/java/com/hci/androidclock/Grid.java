package com.hci.androidclock;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by nadeem on 11/10/14.
 */
public class Grid {
    int columnCounts[];

    int primaryColor;
    int bgColor;

    int gridHeight;
    int gridWidth;
    int squareHeight;
    int squareWidth;

    boolean isRandom = false;

    Paint paint;

    public Grid(Canvas c, int screenHeight, int screenWidth, int gridHeight, int gridWidth, int colorA, int colorB) {

        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.squareHeight = (int) Math.ceil(((double) screenHeight) / gridHeight);
        this.squareWidth = (int) Math.ceil(((double) screenWidth) / gridWidth);
        this.paint = new Paint();
        this.primaryColor = colorA;
        this.bgColor = colorB;
        this.columnCounts = new int[gridWidth];

        updateGrid(c);
    }

    // Randomize colors
    public Grid(Canvas c, int screenHeight, int screenWidth, int gridHeight, int gridWidth) {
        this(c, screenHeight, screenWidth, gridHeight, gridWidth, 0, 0);

        isRandom = true;

        primaryColor = ClockColor.getRandomColor();
        bgColor = ClockColor.getDifferentRandomColor(primaryColor);
    }

    public void switchGrid() {

        if (isRandom) {
            bgColor = ClockColor.getDifferentRandomColor(primaryColor);
        }

        int temp = primaryColor;
        primaryColor = bgColor;
        bgColor = temp;

        for (int col = 0; col < gridWidth; col++) {
            columnCounts[col] = 0;
        }
    }

    public void addSquareToColumn(int col) {

        columnCounts[col] += 1;
    }

    public boolean isColumnFilled(int col) {
        return (columnCounts[col] == gridHeight);
    }

    public List<Integer> getUnfilledCols() {
        List<Integer> unfilledIndexes = new ArrayList<Integer>();
        for (int col = 0; col < gridWidth; col++) {
            if (!isColumnFilled(col)) {
                unfilledIndexes.add(col);
            }
        }

        return unfilledIndexes;
    }

    public void updateGrid(Canvas c) {
        for (int col = 0; col < gridWidth; col++) {
            int top = 0;
            int middle = (gridHeight - columnCounts[col]) * squareHeight;
            int bottom = gridHeight * squareHeight;
            int left = col * squareWidth;
            int right = left + squareWidth;

            paint.setColor(bgColor);
            c.drawRect(left, top, right, middle, paint);

            paint.setColor(primaryColor);
            c.drawRect(left, middle, right, bottom, paint);
        }
    }
}
