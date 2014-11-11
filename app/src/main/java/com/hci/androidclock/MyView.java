package com.hci.androidclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

public class MyView extends View{

        // The paint class holds style and color information for drawing
        // See: http://developer.android.com/reference/android/graphics/Paint.html
        // We'll use it as our one and only one paint object for doodling. More complex
        // apps will have multiple paint objects configured with different colors, styles, etc.
        private Paint _paintDoodle = new Paint();

        // The path class encapsulates geometric paths such as straight lines or cubic curves
        // See: http://developer.android.com/reference/android/graphics/Path.html
        // In this case, we use the path object to store the touch down and touch move locations,
        // which are then drawn to the screen
        private Path _path = new Path();

        public MyView(Context context) {
            super(context);
            init(null, 0);
        }

        public MyView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(attrs, 0);
        }

        public MyView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init(attrs, defStyle);
        }

        /**
         * Because we have more than one constructor (i.e., overloaded constructors), we use
         * a separate initialization method
         * @param attrs
         * @param defStyle
         */
        private void init(AttributeSet attrs, int defStyle){
            _paintDoodle.setColor(Color.RED);
            _paintDoodle.setAntiAlias(true);
            _paintDoodle.setStyle(Paint.Style.STROKE);
        }

        @Override
        public void onDraw(Canvas canvas){
            super.onDraw(canvas);

            //canvas.drawLine(0, 0, getWidth(), getHeight(), _paintDoodle);
            //canvas.drawPath(_path, _paintDoodle);
//    public Grid(int screenHeight, int screenWidth, int h, int w, Canvas c, Paint p, int a, int b) {
            Paint paint = new Paint();

            Grid g = new Grid(getHeight(), getWidth(), 20, 20, canvas, paint, Color.RED, Color.BLUE);
            g.addSquareToColumn(1);

        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent){
            float touchX = motionEvent.getX();
            float touchY = motionEvent.getY();

            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    _path.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    _path.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }

            invalidate();
            return true;
        }
}