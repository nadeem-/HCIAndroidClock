package com.hci.androidclock;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MyView extends View {

    private final int SECOND = 1000;
    private final int MINUTE = 60000;

    // Should make user editable in settings
    private final int SQUARE_DENSITY = 6;
    private final int TIME_INTERVAL = MINUTE;

    private int viewHeight;
    private int viewWidth;
    private int numCols;
    private int numRows;

    private Context mContext;

    private Timer _timer = new Timer();

    private Grid blockGrid;

    public MyView(Context context) {
        super(context);
        mContext = context;
        init();
    }
    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
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

        final MyView thisView = this;

        _timer = new Timer();

        _timer.schedule(new TimerTask() {

            @Override
            public void run() {

                ((Activity) thisView.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (blockGrid != null) {
                            double percent;

                            Calendar currentCalendarTime = Calendar.getInstance();
                            if (TIME_INTERVAL == SECOND) {
                                double time = currentCalendarTime.get(Calendar.MILLISECOND);
                                percent = time / SECOND;
                            } else if (TIME_INTERVAL == MINUTE) {
                                int seconds = currentCalendarTime.get(Calendar.SECOND) * 1000;
                                double time = currentCalendarTime.get(Calendar.MILLISECOND) + seconds;
                                percent = time / MINUTE;
                            }

                            blockGrid.fillToPercent(percent);

                            invalidate();
                        }

                    }
                });
            }
        }, 0, 20);

    }

    public void randomize() {
        blockGrid.randomize();
    }

    public void activityPaused() {
        _timer.cancel();
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if (blockGrid == null) {
            blockGrid = new Grid(mContext, canvas, viewHeight, viewWidth, numRows, numCols);
        } else {
            blockGrid.updateGrid(canvas);
        }

    }
}