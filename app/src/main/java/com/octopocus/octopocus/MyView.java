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


public class MyView extends View {

    private Paint mFeedbackPaint;
    private Paint mObjectPaint;

    private Path mFeebackPath = new Path();
    private Path mFeedworwardPath = new Path();
    private Path mPrefixPath = new Path();

    private PointF mCurrentPos;
    private PointF mInitPos;

    TemplateData mObjectData = new TemplateData();
    public Dollar mDollar = new Dollar(1);
    private float mObjectScale = 3; // Scale of objects
    private int mStartDrawPosObject = 0;

    private boolean mCursorMoves = false;
    private boolean mDrawObject = true;


    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mFeedbackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFeedbackPaint.setStyle(Paint.Style.STROKE);
        mFeedbackPaint.setStrokeWidth(10);
        mObjectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mObjectPaint.setStyle(Paint.Style.STROKE);
        mObjectPaint.setColor(Color.BLUE);
        mObjectPaint.setStrokeWidth(10);
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
                mDollar.clear();
                mCursorMoves = false;

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
                int[] trianglePoints = mObjectData.trianglePoints;
                int index_of_position = getObjectPosition(trianglePoints);
                if (index_of_position != 0) { // current mpath on triangle
                    mStartDrawPosObject = index_of_position;
                }
            }
            if (mDrawObject) {
                drawObject(canvas);
            }


        }
    }

    private int getObjectPosition(int[] objectPoints) {
        int threshold = 180;
        float min_distance = 10000;
        float distance_sum = 0;
        int index = 0;
        
        for (int i = 0; i < objectPoints.length; i++) {
            if ((i % 2) == 0) { // even
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
        }
        if ((distance_sum / objectPoints.length) < threshold) {
            mDrawObject = true;
            return index;
        } else {
            mDrawObject = false;
            return 0;
        }
    }

    // mDrawObject options here
    private void drawObject(Canvas canvas) {
        mFeedworwardPath = new Path();
        mPrefixPath = new Path();
        int[] trianglePoints = mObjectData.trianglePoints;

        // could store tranformed_triangle
        mPrefixPath.moveTo((int) mCurrentPos.x, (int) mCurrentPos.y);
        mFeedworwardPath.moveTo((int) mCurrentPos.x, (int) mCurrentPos.y);
//        mPrefixPath.moveTo(trianglePoints[0] * mObjectScale + init_pos_x - trianglePoints[0] * mObjectScale, trianglePoints[1] * mObjectScale + init_pos_y - trianglePoints[1] * mObjectScale);
//        mFeedworwardPath.moveTo(trianglePoints[0] * mObjectScale + init_pos_x - trianglePoints[0] * mObjectScale, trianglePoints[1] * mObjectScale + init_pos_y - trianglePoints[1] * mObjectScale);
        int prefix_length = 50;
        for (int x = mStartDrawPosObject; x < trianglePoints.length; x += 2) {
            if (x < (mStartDrawPosObject + prefix_length)) {
                mPrefixPath.lineTo(trianglePoints[x] * mObjectScale + (int) mInitPos.x - trianglePoints[0] * mObjectScale, trianglePoints[x + 1] * mObjectScale + (int) mInitPos.y - trianglePoints[1] * mObjectScale);
            }
            mFeedworwardPath.lineTo(trianglePoints[x] * mObjectScale + (int) mInitPos.x - trianglePoints[0] * mObjectScale, trianglePoints[x + 1] * mObjectScale + (int) mInitPos.y - trianglePoints[1] * mObjectScale);
        }
        mObjectPaint.setColor(Color.parseColor("#ccccff"));
        canvas.drawPath(mFeedworwardPath, mObjectPaint);
        mObjectPaint.setColor(Color.parseColor("#7f7fff"));
        canvas.drawPath(mPrefixPath, mObjectPaint);
    }

}