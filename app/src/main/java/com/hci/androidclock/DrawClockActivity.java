package com.hci.androidclock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class DrawClockActivity extends Activity {

    MyView gridView;

    private String timeStr = "";

    // Timers schedule one-shot or recurring tasks for execution.
    // http://developer.android.com/reference/java/util/Timer.html
    private Timer _timerCount = new Timer();

    // There are two main uses for a Handler: (1) to schedule messages and runnables to be
    // executed as some point in the future; and (2) to enqueue an action to be performed
    // on a different thread than your own. We are using it for #2. To enqueue an action
    // from the Timer thread to the UI thread.
    // http://developer.android.com/reference/android/os/Handler.html
    private Handler _uiHandler = new Handler();


    // preferences
    public final String blackColorStr = "-16777216";
    boolean display24HrTime;
    boolean displayTimeZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_clock);
        gridView = (MyView) findViewById(R.id.grid);

        updateClock();
    }

    protected void onStart() {
        super.onStart();

        restorePreferences();
    }

    public void restorePreferences() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        display24HrTime = sharedPrefs.getBoolean("prefClock1Military", false);
        displayTimeZone = sharedPrefs.getBoolean("prefClockTimeZone", false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.draw_clock, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        gridView.activityPaused();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.color_picker) {
            Intent intent = new Intent(DrawClockActivity.this, ColorPickerActivity.class);
            startActivityForResult(intent, 1);
        } else if (id == R.id.switch_clock) {
            Intent intent = new Intent(DrawClockActivity.this, MainActivity.class);
            startActivityForResult(intent, 1);
        } else if (id == R.id.random_colors) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt("color1", -1).apply();
            editor.putInt("color2", -1).apply();
            gridView.randomize();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateClock() {
        // The schedule method of Timer takes in a TimerTask,
        // followed by a long delay (in milliseconds) and a long period (in milliseconds)
        _timerCount.schedule(new TimerTask() {

            @Override
            public void run() {
                Calendar currentCalendarTime = Calendar.getInstance();

                // set clock time variables
                int hour = currentCalendarTime.get(Calendar.HOUR);

                if(display24HrTime) {
                    hour = currentCalendarTime.get(Calendar.HOUR_OF_DAY);
                }
                int minute = currentCalendarTime.get(Calendar.MINUTE);

                timeStr = String.format("%d:%02d", hour, minute);

                // Use the handler to marshal/invoke the Runnable code on the UI thread
                _uiHandler.post(new Runnable(){
                    @Override
                    public void run(){

                        TextView textView = (TextView)findViewById(R.id.textViewClock);
                        textView.setText(timeStr);
                    }
                });
            }
        }, 0, 500);
    }
}
