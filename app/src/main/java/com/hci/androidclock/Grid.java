package com.hci.androidclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
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
    int blocksAdded;

    Context mContext;

    Paint paint;

    // Randomize colors
    public Grid(Context context, Canvas c, int screenHeight, int screenWidth, int gridHeight, int gridWidth) {


        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.squareHeight = (int) Math.ceil(((double) screenHeight) / gridHeight);
        this.squareWidth = (int) Math.ceil(((double) screenWidth) / gridWidth);
        this.paint = new Paint();
        this.columnCounts = new int[gridWidth];
        this.blocksAdded = 0;
        this.mContext = context;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int savedCol1 = sharedPrefs.getInt("color1", -1);
        int savedCol2 = sharedPrefs.getInt("color2", -1);

        if (savedCol1 == -1 || savedCol2 == -1) {
            randomize();
        } else {
            primaryColor = savedCol1;
            bgColor = savedCol2;
        }

        updateGrid(c);
    }

    public void randomize() {
        primaryColor = ClockColor.getRandomColor();
        bgColor = ClockColor.getDifferentRandomColor(primaryColor);
    }

    public void switchGrid() {
        blocksAdded = 0;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        int savedCol1 = sharedPrefs.getInt("color1", -1);
        int savedCol2 = sharedPrefs.getInt("color2", -1);

        if ((savedCol1 == -1) || (savedCol2 == -1)) {
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
        blocksAdded += 1;
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
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        int savedCol1 = sharedPrefs.getInt("color1", -1);
        int savedCol2 = sharedPrefs.getInt("color2", -1);

        if (savedCol1 != -1 && savedCol2 != -1) {
            primaryColor = savedCol1;
            bgColor = savedCol2;
        }

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

    public double getPercentFilled() {
        return ((double) blocksAdded)/ (gridHeight * gridWidth);
    }

    public void fillToPercent(double percent) {
        double currentPercent = getPercentFilled();

        if (percent == 1) {
            switchGrid();
            return;
        }

        if (currentPercent > percent + 0.5) {
            switchGrid();
        }

        while (currentPercent < percent) {
            List<Integer> unfilled = getUnfilledCols();

            Random rand = new Random();

            int colToAdd = unfilled.get(rand.nextInt(unfilled.size()));
            addSquareToColumn(colToAdd);

            currentPercent = getPercentFilled();
        }
    }
}
