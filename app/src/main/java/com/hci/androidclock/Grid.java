package com.hci.androidclock;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by nadeem on 11/10/14.
 */
public class Grid {
    int height;
    int width;
    int columnCounts[];
    int primaryColor;
    int bgColor;
    Paint paint;
    Canvas canvas;
    int squareHeight;
    int squareWidth;

    public Grid(int screenHeight, int screenWidth, int h, int w, Canvas c, Paint p, int colorA, int colorB) {
        squareHeight = screenHeight / h;
        squareWidth = screenWidth / w;
        this.height = h;
        this.width = w;
        this.canvas = c;
        this.paint = p;
        this.primaryColor = colorA;
        this.bgColor = colorB;
        columnCounts = new int[w];

        updateGrid();
    }

    public void addSquareToColumn(int col) {
        columnCounts[col] += 1;
        updateGrid();
    }

    public void updateGrid() {

        for(int col = 0; col < width; col++) {
            for(int row = 0; row < height; row++) {
                int currRowBottom = height - row;

                if(currRowBottom > columnCounts[col]) {
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.STROKE);
                }else {
                    paint.setColor(Color.RED);
                    paint.setStyle(Paint.Style.FILL);
                }

                int top = row * squareHeight;
                int left = col * squareWidth;
                canvas.drawRect(left, top, left + squareWidth, top + squareHeight, paint);
            }
        }
    }
}
