package com.octopocus.octopocus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
    private Path mFeedworwardPath = new Path();
    private Path mPrefixPath = new Path();
    private Path mFalsePath = new Path();

    private PointF mCurrentPos;
    private PointF mInitPos;

    Map<String, Object> objects = new HashMap<>();
    TemplateData mObjectData = new TemplateData();
    public Dollar mDollar = new Dollar(1);

    private float mObjectScale = 3; // Scale of objects
    private int mPrefixLength = 30;

    private boolean mCursorMoves = false;



    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMenu();
        initPaint();
    }

    private void initMenu() {
        objects.put("triangle", new Object("triangle", mObjectData.trianglePoints, "#ccccff", "#7f7fff"));
        objects.put("check", new Object("check", mObjectData.checkPoints, "#01ff12", "#6FFF79"));
    }

    private void initPaint() {
        mFeedbackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFeedbackPaint.setStyle(Paint.Style.STROKE);
        mFeedbackPaint.setStrokeWidth(10);

        mFalsePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFalsePaint.setStyle(Paint.Style.STROKE);
        mFalsePaint.setColor(Color.RED);
        mFalsePaint.setStrokeWidth(10);
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
                mFeebackPath = new Path();
                mFeebackPath.moveTo(mCurrentPos.x, mCurrentPos.y);
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

            mFeebackPath.lineTo(mCurrentPos.x, mCurrentPos.y);
            canvas.drawPath(mFeebackPath, mFeedbackPaint);

            mDollar.addPoint((int) mCurrentPos.x, (int) mCurrentPos.y);

            if (mCursorMoves) {
                for (String object : objects.keySet()) {
                    int[] objectPoints = objects.get(object).mPoints;
                    int index_of_position = getObjectPosition(objectPoints, object);
                    if (index_of_position != 0) { // current mpath on triangle
                        objects.get(object).mStartDrawPos = index_of_position;
                    }

                }
            }

            for (String object : objects.keySet()) {
                if (objects.get(object).mDraw) {
                    drawObject(canvas, objects.get(object));
                }
            }


        }
    }

    private int getObjectPosition(int[] objectPoints, String objectName) {
        int threshold = 180;
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
        if ((distance_sum / objectPoints.length) < threshold) {
            objects.get(objectName).mDraw = true;
            return index;
        } else {
            objects.get(objectName).mDraw = false;
            return 0;
        }
    }

    // mDrawObject options here
    private void drawObject(Canvas canvas, Object object) {

        mFeedworwardPath = new Path();
        mPrefixPath = new Path();
        mFalsePath = new Path();

        int[] points = object.mPoints;

        // could store transformed_triangle
        mFalsePath.moveTo((int) mCurrentPos.x, (int) mCurrentPos.y);

//        mPrefixPath.moveTo(points[0] * mObjectScale + init_pos_x - points[0] * mObjectScale, points[1] * mObjectScale + init_pos_y - points[1] * mObjectScale);
//        mFeedworwardPath.moveTo(points[0] * mObjectScale + init_pos_x - points[0] * mObjectScale, points[1] * mObjectScale + init_pos_y - points[1] * mObjectScale);

        for (int x = object.mStartDrawPos; x < points.length; x += 2) {
            float x_pos = points[x] * mObjectScale + (int) mInitPos.x - points[0] * mObjectScale;
            float y_pos = points[x + 1] * mObjectScale + (int) mInitPos.y - points[1] * mObjectScale;
            if (x == object.mStartDrawPos) {
                mFalsePath.lineTo(x_pos, y_pos);
                canvas.drawPath(mFalsePath, mFalsePaint);
                mPrefixPath.moveTo(x_pos, y_pos);
            }
            if (x < (object.mStartDrawPos + mPrefixLength)) {
                mPrefixPath.lineTo(x_pos, y_pos);
                mFeedworwardPath.moveTo(x_pos, y_pos);
            }
            mFeedworwardPath.lineTo(x_pos, y_pos);
        }
        canvas.drawPath(mFeedworwardPath, object.mPathPaint);
        canvas.drawPath(mPrefixPath, object.mPrefixPaint);
    }

    private void clear() {
        mDollar.clear();
        mCursorMoves = false;
        for (String object : objects.keySet()) {
            objects.get(object).mStartDrawPos = 0;
        }
    }

}