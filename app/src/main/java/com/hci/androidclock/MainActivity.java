package com.hci.androidclock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

// References:
// http://www.learn-android-easily.com/2013/09/preferenceactivity-in-android-example.html
// http://stackoverflow.com/questions/6343166/android-os-networkonmainthreadexception

public class MainActivity extends Activity {
    public final String YAHOO_API_KEY = "dj0yJmk9MjJhZnVUZ0R5V281JmQ9WVdrOWJFWlZSRkJITkdFbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD03NQ--";

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

    public String weatherCondition = "";
    public String weatherTemp = "";
    public String weatherCity = "";
    public String weatherState = "";
    public String weatherString = "";

    // preferences
    public final String BLACK_COLOR_STR = "-16777216";
    public final String COLLEGE_PARK_ZIP = "20740"; // default ZIP (if none provided)

    boolean display24HrTime;
    boolean displayTimeZone;
    int clockTextColor;
    boolean displayWeatherInfo;
    String zipCode;

    // Yahoo weather station id (retrieved from XML query)
    String woeid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        restorePreferences();
        getWeatherInfo();
        updateClock();

       //SharedPreferences settings = getSharedPreferences("pref", 0);
       // boolean silent = settings.getBoolean("silentMode", false);
       // setSilent(silent);
    }

    protected void onStart() {
        super.onStart();

        restorePreferences();
    }

    public void restorePreferences() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        display24HrTime = sharedPrefs.getBoolean("prefClock1Military", false);
        displayTimeZone = sharedPrefs.getBoolean("prefClockTimeZone", false);
        clockTextColor = Integer.parseInt(sharedPrefs.getString("prefClockTextColor", BLACK_COLOR_STR));
        displayWeatherInfo = sharedPrefs.getBoolean("prefDisplayWeatherInfo", false);
        zipCode = sharedPrefs.getString("prefZIPCode", COLLEGE_PARK_ZIP);
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
                        textView.setTextSize(12);
                    }
                });
            }
        }, 0, 1000);
    }

    class GetWoeidTask extends AsyncTask<String, Void, Document> {

        private Exception exception;

        protected Document doInBackground(String... urls) {
            try {
                System.out.println("GET XML ALT");
                URL url = new URL(urls[0]);
                URLConnection conn = url.openConnection();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(conn.getInputStream());
                return doc;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(Document doc) {
            getWoeidFromLocationXML(doc);

            // excecute a weather query
            String weatherXMLQuery = "http://weather.yahooapis.com/forecastrss?w=" + woeid;
            new GetWeatherTask().execute(weatherXMLQuery);
        }
    }

    class GetWeatherTask extends AsyncTask<String, Void, Document> {

        private Exception exception;

        protected Document doInBackground(String... urls) {
            try {
                System.out.println("GET WEATHER TASK");
                URL url = new URL(urls[0]);
                URLConnection conn = url.openConnection();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(conn.getInputStream());
                return doc;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(Document doc) {
            getWeatherFromXML(doc);
        }
    }

    public void getWeatherInfo() {
        String locationQuery = "http://where.yahooapis.com/v1/places.q(" + zipCode + ")?appid="
                + YAHOO_API_KEY;
        System.out.println("LOCATION Query:" + locationQuery);
        new GetWoeidTask().execute(locationQuery);
    }

    public void getWeatherFromXML(Document doc) {
        try {
            NodeList nodes = doc.getElementsByTagName("item");
            NodeList elementNodes = ((Element) nodes.item(0)).getElementsByTagName("yweather:condition");
            Element element = (Element) elementNodes.item(0);

            weatherCondition = element.getAttribute("text");
            weatherTemp = element.getAttribute("temp");

            Element locationNode = (Element) doc.getElementsByTagName("yweather:location").item(0);
            weatherCity = locationNode.getAttribute("city");
            weatherState = locationNode.getAttribute("region");

            weatherString = "Weather for " + weatherCity + ", " + weatherState  + ": " +
                    weatherCondition + ", " + weatherTemp + "°";
            System.out.println(weatherString);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void getWoeidFromLocationXML(Document doc) {
        try {
            NodeList nodes = doc.getElementsByTagName("place");
            NodeList elementNodes = ((Element) nodes.item(0)).getElementsByTagName("woeid");
            Element element = (Element) elementNodes.item(0);
            woeid = element.getTextContent();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
