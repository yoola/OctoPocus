package com.octopocus.octopocus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;


public class MyView extends View {

    private Paint mFeedbackPaint;
    private Paint mFalsePaint;

    private Path mFeebackPath = new Path();
    private Path mFeedforwardPath = new Path();
    private Path mPrefixPath = new Path();
    private Path mFalsePath = new Path();

    private PointF mCurrentPos;
    private PointF mInitPos;

    Map<String, Object> mObjects = new HashMap<>();
    TemplateData mObjectData = new TemplateData();
    public Dollar mDollar = new Dollar(1);

    private float mObjectScale = 3; // Scale of objects
    private int mPrefixLength = 1200;

    private boolean mCursorMoves = false;



    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMenu();
        initPaint();
    }

    private void initMenu() {
        mObjects.put("triangle", new Object("triangle", mObjectData.trianglePoints, "#ccccff", "#7f7fff", 10));
        mObjects.put("check", new Object("check", mObjectData.checkPoints, "#8ae32b", "#208a18", 10));
    }

    private void initPaint() {
        mFeedbackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFeedbackPaint.setStyle(Paint.Style.STROKE);
        mFeedbackPaint.setStrokeWidth(10);

        mFalsePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFalsePaint.setStyle(Paint.Style.STROKE);
        mFalsePaint.setColor(Color.RED);
        mFalsePaint.setStrokeWidth(3);
    }

    // source:
    // http://stackoverflow.com/questions/31901364/android-using-ontouchlistener-in-canvas

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCurrentPos = new PointF(x, y);
                mInitPos = new PointF(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentPos.set(x, y);
                mCursorMoves = true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mDollar.recognize();
                ((MainActivity) this.getContext()).writeDollar(mDollar);
                clear();

            case MotionEvent.ACTION_CANCEL:
                mCurrentPos = null;
                invalidate();
                break;
        }
        return true;
    }




    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (mCurrentPos != null) {

            mDollar.addPoint((int) mCurrentPos.x, (int) mCurrentPos.y);

            String min_err_name = "";
            if (mCursorMoves) {
                double min_error = 10000;
                for (String object : mObjects.keySet()) {
                    int[] objectPoints = mObjects.get(object).mPoints;
                    int index_of_position = getNearestObjectPosToCursor(objectPoints, object);
                    if (index_of_position != 0) { // current mpath on triangle
                        mObjects.get(object).mStartDrawPos = index_of_position;
                    }
                    if (mObjects.get(object).mError < min_error) {
                        min_error = mObjects.get(object).mError;
                        min_err_name = mObjects.get(object).mName;
                    }

                }
            }
            for (String object : mObjects.keySet()) {
                if (mObjects.get(object).mName != min_err_name) {
                    mObjects.get(object).mStartDrawPos = 0;
                }
                drawObject(canvas, mObjects.get(object));
            }


        }
    }

    // which point (index) in the object lies nearest to the cursor
    private int getNearestObjectPosToCursor(int[] objectPoints, String objectName) {
        int threshold = 900;
        float min_distance = 10000;
        float distance_sum = 0;
        int index = 0;

        for (int i = 0; i < objectPoints.length; i += 2) {
            float object_x = objectPoints[i] * mObjectScale + (int) mInitPos.x - objectPoints[0] * mObjectScale;
            float object_y = objectPoints[i + 1] * mObjectScale + (int) mInitPos.y - objectPoints[1] * mObjectScale;
            float offset_x = Math.abs(object_x - (int) mCurrentPos.x);
            float offset_y = Math.abs(object_y - (int) mCurrentPos.y);
            float off = offset_x + offset_y;
            distance_sum += off;
            if (off < min_distance) {
                min_distance = off;
                index = i;
            }
        }
//        System.out.println((int) (threshold / (distance_sum / objectPoints.length)));
        mObjects.get(objectName).setThickness((int) (threshold / (distance_sum / objectPoints.length)));
        if (mObjects.get(objectName).mThickness >= 4) {
            return index;
        } else {
            mObjects.get(objectName).setThickness(0);
            return 0;
        }
    }

    // mDrawObject options here
    private void drawObject(Canvas canvas, Object object) {

        mFeedforwardPath = new Path();
        mPrefixPath = new Path();
        mFalsePath = new Path();
        mFeebackPath = new Path();

        int[] points = object.mPoints;

        // could store transformed_triangle
        mFalsePath.moveTo((int) mCurrentPos.x, (int) mCurrentPos.y);
        mFeebackPath.moveTo((int) mInitPos.x, (int) mInitPos.y);

//        mPrefixPath.moveTo(points[0] * mObjectScale + init_pos_x - points[0] * mObjectScale, points[1] * mObjectScale + init_pos_y - points[1] * mObjectScale);
//        mFeedworwardPath.moveTo(points[0] * mObjectScale + init_pos_x - points[0] * mObjectScale, points[1] * mObjectScale + init_pos_y - points[1] * mObjectScale);

        int index = getObjectPosOfPrefix(object);
        for (int x = 0; x < points.length; x += 2) {
            float x_pos = points[x] * mObjectScale + (int) mInitPos.x - points[0] * mObjectScale; // objects points to global space
            float y_pos = points[x + 1] * mObjectScale + (int) mInitPos.y - points[1] * mObjectScale; // objects points to global space
            if (x < object.mStartDrawPos) {
                mFeebackPath.lineTo(x_pos, y_pos);
                mFeedbackPaint.setStrokeWidth(object.mThickness);

            } else if (x == object.mStartDrawPos) {
                float x_err = mCurrentPos.x - x_pos;
                float y_err = mCurrentPos.y - y_pos;
                object.mError = Math.sqrt((x_err * x_err) + (y_err * y_err));
                mFeebackPath.lineTo(x_pos, y_pos);
                mFalsePath.lineTo(x_pos, y_pos);
                mPrefixPath.moveTo(x_pos, y_pos);
                mFeedforwardPath.moveTo(x_pos, y_pos);

            } else if (x <= index) {
                mPrefixPath.lineTo(x_pos, y_pos);
                mFeedforwardPath.moveTo(x_pos, y_pos);

            } else {
                mFeedforwardPath.lineTo(x_pos, y_pos);
            }
        }

        canvas.drawPath(mFeebackPath, mFeedbackPaint);
//        canvas.drawPath(mFalsePath, mFalsePaint);
        if (object.mThickness != 0) {
            canvas.drawPath(mPrefixPath, object.mPrefixPaint);
            canvas.drawPath(mFeedforwardPath, object.mPathPaint);
        }
    }

    // which point (index) in the object lies nearest to the cursor
    private int getObjectPosOfPrefix(Object object) {
        int[] points = object.mPoints;
        float x_pos = points[object.mStartDrawPos] * mObjectScale + (int) mInitPos.x - points[0] * mObjectScale; // objects points to global space
        float y_pos = points[object.mStartDrawPos + 1] * mObjectScale + (int) mInitPos.y - points[1] * mObjectScale; // objects points to global space
        float x_diff = mCurrentPos.x - x_pos;
        float y_diff = mCurrentPos.y - y_pos;
        double sum_distances = Math.sqrt((x_diff * x_diff) + (y_diff * y_diff));

        int index = 0;
        for (int x = object.mStartDrawPos + 2; x < points.length; x += 2) {
            x_pos = points[x] * mObjectScale + (int) mInitPos.x - points[0] * mObjectScale; // objects points to global space
            y_pos = points[x + 1] * mObjectScale + (int) mInitPos.y - points[1] * mObjectScale; // objects points to global space
            x_diff = mCurrentPos.x - x_pos;
            y_diff = mCurrentPos.y - y_pos;
            double distance = Math.sqrt((x_diff * x_diff) + (y_diff * y_diff));
            sum_distances += distance;
            if (sum_distances < mPrefixLength) {
                index = x;
            }
        }
        return index;
    }

    private void clear() {
        mDollar.clear();
        mCursorMoves = false;
        for (String object : mObjects.keySet()) {
            mObjects.get(object).mStartDrawPos = 0;
        }
    }

}