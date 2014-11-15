package com.hci.androidclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener  {
    // GLOBAL CONSTANTS
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
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


    // preferences
    public final String blackColorStr = "-16777216";
    boolean display24HrTime;
    boolean displayTimeZone;
    int clockTextColor;

    LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(this, this, this);

        setContentView(R.layout.activity_main);
        restorePreferences();

        updateClock();
    }

    /* called when the activity becomes visible */
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();

        restorePreferences();
    }

    /*
 * Called when the Activity is no longer visible.
 */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    public void restorePreferences() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        display24HrTime = sharedPrefs.getBoolean("prefClock1Military", false);
        displayTimeZone = sharedPrefs.getBoolean("prefClockTimeZone", false);

        // update clock text color
        clockTextColor = Integer.parseInt(sharedPrefs.getString("prefClockTextColor", blackColorStr));
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
            Intent intent = new Intent(MainActivity.this, UserSettingActivity.class);
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

                if(display24HrTime) {
                    hour = currentCalendarTime.get(Calendar.HOUR_OF_DAY);
                }
                int minute = currentCalendarTime.get(Calendar.MINUTE);
                int sec = currentCalendarTime.get(Calendar.SECOND);

                String timeZoneStr = "";
                if(displayTimeZone) {
                    timeZoneStr = currentCalendarTime.getTimeZone().getDisplayName();
                    timeStr = String.format("%02d:%02d:%02d %s %s", hour, minute, sec, amPmText, timeZoneStr);
                }else {
                    timeStr = String.format("%02d:%02d:%02d %s", hour, minute, sec, amPmText);
                }

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
                        textView.setTextColor(clockTextColor);
                    }
                });
            }
        }, 0, 1000);
    }



    // Methods needed for Google Play Services (which is needed to get user location)
    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }


    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
                        break;
                }
        }
    }
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason.
            // resultCode holds the error code.
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(this.getFragmentManager(),
                        "Location Updates");
            }
        }

        return false;
    }


    // Location services callbacks
    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        //String locStr = "LOCATION: " + mLocationClient.getLastLocation().getLatitude() + ", " + mLocationClient.getLastLocation().getLongitude();
        //Toast.makeText(this, locStr, Toast.LENGTH_SHORT).show();
        String locStr = "CONNECTED";
        System.out.println(locStr);
    }

    /*
    * Called by Location Services if the connection to the
    * location client drops because of an error.
    */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Toast.makeText(this, "Connection to Google Play services failed", Toast.LENGTH_SHORT).show();
        }
    }

}



