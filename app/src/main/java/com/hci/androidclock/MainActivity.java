package com.hci.androidclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends Activity {


    // This is just a dummy counter that counts upwards. We'll display
    // the current count value in a TextView. Note: I like to prefix my member variables
    // by the underscore character--this is just a convention, others use 'm'. For example,
    // 'mCounter = 0' Either way, I think prefixing your member variables makes your code more readable
    // and cuts down on accidental scoping errors
    private String timeStr = "";
    private String dateStr = "";

    // Timers schedule one-shot or recurring tasks for execution.
    // http://developer.android.com/reference/java/util/Timer.html
    private Timer _timerCount = new Timer();

    // There are two main uses for a Handler: (1) to schedule messages and runnables to be
    // executed as some point in the future; and (2) to enqueue an action to be performed
    // on a different thread than your own. We are using it for #2. To enqueue an action
    // from the Timer thread to the UI thread.
    // http://developer.android.com/reference/android/os/Handler.html
    private Handler _uiHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateClock();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, 1);
        } else if (id == R.id.switch_clock) {
            Intent intent = new Intent(MainActivity.this, DrawClockActivity.class);
            startActivityForResult(intent, 1);
        } else if (id == R.id.color_picker) {
            Intent intent = new Intent(MainActivity.this, ColorPickerActivity.class);
            startActivityForResult(intent, 1);
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

                int amPm = currentCalendarTime.get(Calendar.AM_PM);
                String amPmText = "";

                amPmText = (amPm == Calendar.PM) ? "PM" : "AM";


                // set clock time variables
                int hour = currentCalendarTime.get(Calendar.HOUR);
                int minute = currentCalendarTime.get(Calendar.MINUTE);
                int sec = currentCalendarTime.get(Calendar.SECOND);
                timeStr = String.format("%02d:%02d:%02d %s", hour, minute, sec, amPmText);

                // set date variables
                int month = currentCalendarTime.get(Calendar.MONTH);
                int dayOfMonth = currentCalendarTime.get(Calendar.DAY_OF_MONTH);
                int year = currentCalendarTime.get(Calendar.YEAR);
                dateStr = month + "/" + dayOfMonth + "/" + year;

                // Use the handler to marshal/invoke the Runnable code on the UI thread
                _uiHandler.post(new Runnable(){
                    @Override
                    public void run(){

                        TextView textView = (TextView)findViewById(R.id.textViewCounter);
                        textView.setText(timeStr + "\n" + dateStr);
                    }
                });
            }
        }, 0, 1000);
    }

    /*
        /**
     * This is hooked up in the XML. See 'android:onClick="onButtonStartTimer"'
     * @param view

    public void onButtonStartTimer(View view){
        // The "view" in this case is the view that triggered the event
        // We disable the start timer button so that it cannot be triggered more than once
        view.setEnabled(false);

        // The code below would also work to disable the button
        // I include it here for illustrative purposes
        //View buttonView = findViewById(R.id.buttonStartTimer);
        //view.setEnabled(false);

        // We are using a few anonymous classes here; which makes the code a little more
        // complex looking than it really is. The schedule method of Timer takes in a TimerTask,
        // followed by a long delay (in milliseconds) and a long period (in milliseconds)
        _timerCount.schedule(new TimerTask() {

            @Override
            public void run() {
                _counter++;

                // Use the handler to marshal/invoke the Runnable code on the UI thread
                _uiHandler.post(new Runnable(){
                    @Override
                    public void run(){
                        TextView textView = (TextView)findViewById(R.id.textViewCounter);
                        textView.setText(_counter + "");
                    }
                });
            }
        }, 0, 1000);
    }
     */
}
