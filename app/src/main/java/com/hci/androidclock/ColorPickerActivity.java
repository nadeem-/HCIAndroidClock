package com.hci.androidclock;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;


public class ColorPickerActivity extends Activity {

    private final int NO_COLOR = -1;


    private final int STATE_CHOOSING_1 = 1;
    private final int STATE_CHOOSING_2 = 2;

    private Context mContext;
    private GridView mGridView;
    private Button selectColorsButton;
    private Button colorButton1;
    private Button colorButton2;

    private int color1 = NO_COLOR;
    private int color2 = NO_COLOR;

    private int state = STATE_CHOOSING_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);

        mContext = getApplicationContext();
        mGridView = (GridView) findViewById(R.id.gridView);

        mGridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (state == STATE_CHOOSING_1) {
                    color1 = ClockColor.getAllColors()[position];
                    state = STATE_CHOOSING_2;
                } else {
                    color2 = ClockColor.getAllColors()[position];
                    state = STATE_CHOOSING_1;
                }

                update();
            }
        });


        selectColorsButton = (Button) findViewById(R.id.selectColors);
        selectColorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FINISH
            }
        });

        colorButton1 = (Button) findViewById(R.id.colorButton1);
        colorButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = STATE_CHOOSING_1;

                update();
            }
        });

        colorButton2 = (Button) findViewById(R.id.colorButton2);
        colorButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = STATE_CHOOSING_2;

                update();
            }
        });
    }

    private void update() {
        if (color1 != NO_COLOR) {
            colorButton1.setBackgroundColor(color1);
        }

        if (color2 != NO_COLOR) {
            colorButton2.setBackgroundColor(color2);
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        final int screenHeight = mGridView.getHeight();

        mGridView.setAdapter(new ListAdapter() {

            private int[] colors = ClockColor.getAllColors();

            @Override
            public boolean areAllItemsEnabled() {
                return true;
            }

            @Override
            public boolean isEnabled(int position) {
                return true;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public int getCount() {
                return colors.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                int col = colors[position];
                View v;
                if (convertView == null) {  // if it's not recycled, initialize some attributes
                    v = new View(mContext);
                    v.setBackgroundColor(col);

                    int minHeight;
                    if ((position / 4) == 4) {
                        minHeight = screenHeight - (4 * (screenHeight / 5));
                    } else {
                        minHeight = screenHeight / 5;
                    }
                    v.setMinimumHeight(minHeight);
                } else {
                    v = convertView;
                }

                return v;
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return colors.length;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


}
