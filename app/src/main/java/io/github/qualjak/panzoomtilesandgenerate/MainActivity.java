package io.github.qualjak.panzoomtilesandgenerate;

import android.graphics.Point;
import android.support.v4.view.GestureDetectorCompat;
//import android.support.v4.view.ScaleGestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
//import android.view.ScaleGestureDetector;
//import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;
import android.widget.RelativeLayout;
import android.widget.Scroller;
//import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureDetector;
    //    private ScaleGestureDetector scaleDetector;
    //private float scaleFactor = 1.f, scrollOffsetX = 0.f, scrollOffsetY = 0.f;
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

//        TextView hello = (TextView)findViewById(R.id.hello);
//        hello.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Point dimensions = new Point();
        getWindowManager().getDefaultDisplay().getSize(dimensions);
        screenWidth = dimensions.x;
        screenHeight = dimensions.y;

        gestureDetector = new GestureDetectorCompat(this, new SimpleGestureListener());
//        scaleDetector = new ScaleGestureDetector(this, new SimpleScaleListener());
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
//            Log.d(DEBUG_TAG, "onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString() + '\n' + "**"
                    + velocityX + "***" + velocityY);

            scroller.fling(layout.getScrollX(), layout.getScrollY(),
                    ((int) (-velocityX / scaleFactor)), ((int) (-velocityY / scaleFactor)),
                    //FIX THIS AND OTHER APPROPRIATE PLACES (mainly the springBack)
                    (int)-scrollOffsetX,
                    (int) ((layout.getWidth() - screenWidth)*scaleFactor - scrollOffsetX),
                    (int)-scrollOffsetY,
                    (int) ((layout.getHeight() - screenHeight)*scaleFactor - scrollOffsetY));
            (new updateScroll(scroller)).start();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //Log.d(DEBUG_TAG, "onScroll: " + e1.toString()+e2.toString());
            int newX = layout.getScrollX() + (int) distanceX;
            int newY = layout.getScrollY() + (int) distanceY;
            layout.scrollTo(newX, newY);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            scaleFactor += ZOOM_AMNT;
            //scrollOffsetX = scrollOffsetX + layout.getScrollX() + screenWidth/2;
            //scrollOffsetY = scrollOffsetY + layout.getScrollY() + screenHeight/2;
            Log.d(DEBUG_TAG, "scrollX: " + layout.getScrollX() + "| scrollY: " +
                    layout.getScrollY() + '\n' + "| layoutX: " + layout.getWidth()
                    + "| layoutY: " + layout.getHeight());
            layout.scrollTo(0, 0);
            layout.setScaleX(scaleFactor);
            layout.setScaleY(scaleFactor);
            Log.d(DEBUG_TAG, "POST: scrollX: " + layout.getScrollX() + "| scrollY: " +
                    layout.getScrollY() + '\n' + "| layoutX: " + layout.getWidth()
                    + "| layoutY: " + layout.getHeight());
            return true;
        }

    }

//    private class SimpleScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            scaleFactor *= detector.getScaleFactor();
//            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
//            return true;
//        }
//    }

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
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        layout.invalidate();
//                    }
//                });
//                Log.d(DEBUG_TAG, sc.getCurrX() + " " + sc.getCurrY());
                Log.d(DEBUG_TAG, "scrollX: " + layout.getScrollX() + "| scrollY: " +
                        layout.getScrollY() /* '\n' + "| currX: " + currX + "| currY: " + currY*/);
                try {
                    Thread.sleep(SCROLL_REFRESH_RATE);
                } catch (InterruptedException e) {
                    Log.d(DEBUG_TAG, e.getMessage());
                }
            }
            sc.springBack(layout.getScrollX(), layout.getScrollY(),
                    (int)-scrollOffsetX, (int)(layout.getWidth() - scrollOffsetX),
                    (int)-scrollOffsetY, (int)(layout.getHeight() - scrollOffsetY));
//            Log.d(DEBUG_TAG, "finished scrolling");
        }
    }
}