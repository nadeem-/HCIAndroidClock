package com.hci.androidclock;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MyView extends View {

    private final int SECOND = 1000;
    private final int MINUTE = 60000;

    // Should make user editable in settings
    private final int SQUARE_DENSITY = 6;
    private final int COLOR_1 = Color.BLACK;
    private final int COLOR_2 = Color.LTGRAY;
    private final int TIME_INTERVAL = MINUTE;

    private int viewHeight;
    private int viewWidth;
    private int numCols;
    private int numRows;

    private Timer _timer = new Timer();

    private Grid blockGrid;

    public MyView(Context context) {
        super(context);
        init();
    }
    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        viewHeight = getHeight();
        viewWidth = getWidth();

        double ratio = viewHeight / (double) viewWidth;
        numRows = (int) (ratio * SQUARE_DENSITY * 5);
        numCols = (int) ((1 / ratio) * SQUARE_DENSITY * 5);

        startTimer();
    }

    private void startTimer() {
        int interval = TIME_INTERVAL / (numRows * numCols);

        final MyView thisView = this;

        _timer = new Timer();

        _timer.schedule(new TimerTask() {

            @Override
            public void run() {

                ((Activity) thisView.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (blockGrid != null) {

                            addABlock();
                        }
                    }
                });
            }
        }, 0, interval);

    }

    public void addABlock() {
        List<Integer> unfilled = blockGrid.getUnfilledCols();

        Random rand = new Random();

        if (unfilled.isEmpty()) {
            blockGrid.switchGrid();
            unfilled = blockGrid.getUnfilledCols();
        }

        int colToAdd = unfilled.get(rand.nextInt(unfilled.size()));
        blockGrid.addSquareToColumn(colToAdd);

        invalidate();
    }

    public void activityPaused() {
        _timer.cancel();
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if (blockGrid == null) {
            blockGrid = new Grid(canvas, viewHeight, viewWidth, numRows, numCols);
        } else {
            blockGrid.updateGrid(canvas);
        }

    }
}