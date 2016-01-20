package io.github.qualjak.panzoomtilesandgenerate;

import android.graphics.Point;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
//import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;
import android.widget.RelativeLayout;
//import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureDetector;
    private float scaleFactor = 1.f,
            leftBound, rightBound, topBound, bottomBound;
    private static final float ZOOM_AMNT = 0.25f;
    private OverScroller scroller;
    private static final String DEBUG_TAG = "Gestures";
    private static int screenWidth, screenHeight;
    private static final int SCROLL_REFRESH_RATE = 15;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (RelativeLayout) findViewById(R.id.relative);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.height = 3000;
        params.width = 3000;

        Point dimensions = new Point();
        getWindowManager().getDefaultDisplay().getSize(dimensions);
        screenWidth = dimensions.x;
        screenHeight = dimensions.y;

        if(scaleFactor == 1.f) {
            leftBound = 0.f; rightBound = 3000;
            topBound = 0.f; bottomBound = 3000;
        }

        gestureDetector = new GestureDetectorCompat(this, new SimpleGestureListener());
        scroller = new OverScroller(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            scroller.forceFinished(true);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString() + '\n' + "**"
                    + velocityX + "***" + velocityY);

            scroller.fling(layout.getScrollX(), layout.getScrollY(),
                    ((int) (-velocityX / scaleFactor)), ((int) (-velocityY / scaleFactor)),
                    (int)leftBound,
                    (int) rightBound - screenWidth,
                    (int)topBound,
                    (int) bottomBound - screenHeight);
            (new updateScroll(scroller)).start();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int newX = layout.getScrollX() + (int) distanceX;
            int newY = layout.getScrollY() + (int) distanceY;
            layout.scrollTo(newX, newY);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            scaleFactor += ZOOM_AMNT;
            int width = layout.getWidth(), height = layout.getHeight();
            float widthDifference = width*((scaleFactor - 1)/2);
            float heightDifference = height*((scaleFactor - 1)/2);
            rightBound = width + widthDifference;
            leftBound = 0 - widthDifference;
            topBound = 0 - heightDifference;
            bottomBound = height + heightDifference;
            layout.setScaleX(scaleFactor);
            layout.setScaleY(scaleFactor);
            return true;
        }

    }

    private class updateScroll extends Thread {
        private OverScroller sc;

        public updateScroll(OverScroller sc) {
            this.sc = sc;
        }

        @Override
        public void run() {
            while (!sc.isFinished()) {
                int currX = sc.getCurrX(), currY = sc.getCurrY();
                sc.computeScrollOffset();
                layout.scrollTo(currX, currY);
                Log.d(DEBUG_TAG, "scrollX: " + layout.getScrollX() + "| scrollY: " +
                        layout.getScrollY() /* '\n' + "| currX: " + currX + "| currY: " + currY*/);
                try {
                    Thread.sleep(SCROLL_REFRESH_RATE);
                } catch (InterruptedException e) {
                    Log.d(DEBUG_TAG, e.getMessage());
                }
            }
            sc.springBack(layout.getScrollX(), layout.getScrollY(),
                    (int)leftBound, (int)rightBound,
                    (int)topBound, (int)bottomBound);
            Log.d(DEBUG_TAG, leftBound + "*R* " + rightBound + "*T* " + topBound + "*B* " + bottomBound);
        }
    }
}