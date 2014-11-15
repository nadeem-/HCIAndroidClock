package com.hci.androidclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class DrawClockActivity extends Activity {

    MyView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_clock);
        gridView = (MyView) findViewById(R.id.grid);
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

        if (id == R.id.action_settings) {
            Intent intent = new Intent(DrawClockActivity.this, UserSettingActivity.class);
            startActivityForResult(intent, 1);
        } else if (id == R.id.switch_clock) {
            Intent intent = new Intent(DrawClockActivity.this, MainActivity.class);
            startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }
}
