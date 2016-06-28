package com.octopocus.octopocus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class MyView extends View {

    private Paint mFeedbackPaint;
    private Paint mLabelPaint;

    private Path mFeebackPath = new Path();
    private Path mFeedforwardPath = new Path();
    private Path mPrefixPath = new Path();

    private PointF mCurrentPos;
    private PointF mInitPos;

    Map<String, Object> mObjects = new HashMap<>();
    TemplateData mObjectData = new TemplateData();
    public Dollar mDollar = new Dollar(1);

    private float mObjectScale = 3; // Scale of objects
    private int mPrefixLength = 1200;

    private boolean mCursorMoves = false;
    private boolean mTouchUp = false;
    private boolean mSaveNewPath = false;
    private Object mSelectedObject = null;

    private List<Integer> newPath = new ArrayList<>();
    private String mNewObjectName = "";



    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMenu();
        initPaint();
    }

    private void initMenu() {
        mObjects.put("Copy", new Object("Copy", mObjectData.copyPoints, "#ccccff", "#7f7fff", 10));
        mObjects.put("New Path: Copy", new Object("New Path: Copy", mObjectData.newCopyPath, "#ccccff", "#7f7fff", 10));

        mObjects.put("Paste", new Object("Paste", mObjectData.checkPoints, "#8ae32b", "#208a18", 10));
        mObjects.put("New Path: Paste", new Object("New Path: Paste", mObjectData.newPastePath, "#ccccff", "#7f7fff", 10));

        mObjects.put("Select", new Object("Select", mObjectData.caretPointsCW, "#FE642E", "#B43104", 10));
        mObjects.put("New Path: Select", new Object("New Path: Select", mObjectData.newSelectPath, "#ccccff", "#7f7fff", 10));

        mObjects.put("Cut", new Object("Cut", mObjectData.caretPointsCW2, "#ccccff", "#7f7fff", 10));
        mObjects.put("New Path: Cut", new Object("New Path: Cut", mObjectData.newCutPath, "#ccccff", "#7f7fff", 10));
    }

    private void initPaint() {
        mFeedbackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFeedbackPaint.setStyle(Paint.Style.STROKE);
        mFeedbackPaint.setStrokeWidth(10);

        mLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLabelPaint.setStyle(Paint.Style.STROKE);
        mLabelPaint.setStrokeWidth(2);
        mLabelPaint.setTextSize(60);
    }

    // source:
    // http://stackoverflow.com/questions/31901364/android-using-ontouchlistener-in-canvas

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchUp = false;
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
                //mDollar.recognize();
                //((MainActivity) this.getContext()).writeDollar(mDollar);
                if (mSaveNewPath) {
                    System.out.println("Hallo");
                    Object object = mObjects.get(mNewObjectName);
                    setNewPath(object);
                    mSaveNewPath = false;
                }

                for (String objectName : mObjects.keySet()) {
                    Object object = mObjects.get(objectName);
                    if (object.mExcecute) {
                        System.out.println(object.mName + " EXCECUTE!!!!!!!!!!!!!!");
                        ((MainActivity) this.getContext()).excecuteCommand(object.mName);
                        if (object.mName.length() < 10) {
                            mSaveNewPath = false;
                        } else {
                            String substring = object.mName.substring(0, 10);
                            System.out.println(substring + "hi");
                            if (substring.equals("New Path: ")) {
                                mSaveNewPath = true;
                                String substringName = object.mName.substring(10, object.mName.length());
                                System.out.println(substringName);
                                mNewObjectName = substringName;
                            } else {
                                mSaveNewPath = false;
                            }
                        }
//                        if (mSelectedObject != null) {
                            mSelectedObject = object;
                            mTouchUp = true;
                            invalidate();
//                        }
                    }
                }
                clear();

            case MotionEvent.ACTION_CANCEL:
                mCurrentPos = null;
                invalidate();
                break;
        }
        return true;
    }


    private void setNewPath(Object object) {
        int[] points = new int[newPath.size()];
        for (int i = 0; i < newPath.size(); i++) {
            points[i] = newPath.get(i);
        }

        object.mPoints = points;
        System.out.println("New object");
    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (mSelectedObject != null && mTouchUp) {
            int offset = (int) (mSelectedObject.mName.length() / 2);
            int width = (int) (this.getWidth() / 2) - (offset * 20);
            int height = (int) (this.getHeight() / 2) - offset;
            canvas.drawText(mSelectedObject.mName, width, height, mLabelPaint);
            mSelectedObject = null;
        } else {

            if (mCurrentPos != null) {
                // mDollar.addPoint((int) mCurrentPos.x, (int) mCurrentPos.y);

                if (mSaveNewPath) {
                    int x_local = (int)(mCurrentPos.x / mObjectScale);
                    int y_local = (int)(mCurrentPos.y / mObjectScale);
//
                    newPath.add(x_local);
                    newPath.add(y_local);
                    System.out.println(x_local + "," + y_local);

                }

                if (mCursorMoves) {
                    for (String object : mObjects.keySet()) {
                        Object obj = mObjects.get(object);
                        setStartPosition(obj); // current start position in object
                        setError(obj);
                    }
                }
                for (String object : mObjects.keySet()) {
                    if (mObjects.get(object).mError > 150) {
                        mObjects.get(object).mStartDrawPos = 0;
                    }
                    drawObject(canvas, mObjects.get(object));
                }

            }

        }
    }

    private void setError(Object obj) {
        int[] points = obj.mPoints;
        int i = obj.mStartDrawPos;

        float x_pos = points[i] * mObjectScale + (int) mInitPos.x - points[0] * mObjectScale; // objects points to global space
        float y_pos = points[i + 1] * mObjectScale + (int) mInitPos.y - points[1] * mObjectScale; // objects points to global space

        float x_err = mCurrentPos.x - x_pos;
        float y_err = mCurrentPos.y - y_pos;
        obj.mError = Math.sqrt((x_err * x_err) + (y_err * y_err));
    }

    // which point (index) in the object lies nearest to the cursor
    private void setStartPosition(Object object) {
        int[] points = object.mPoints;
        int threshold = 500;
        double min_distance = 10000;
        float distance_sum = 0;
        int index = 0;

        int prefix_end_pos = getObjectPosOfPrefix(object);
        for (int i = object.mStartDrawPos; i < prefix_end_pos; i += 2) {
            float object_x = points[i] * mObjectScale + (int) mInitPos.x - points[0] * mObjectScale; // objects points to global space
            float object_y = points[i + 1] * mObjectScale + (int) mInitPos.y - points[1] * mObjectScale; // objects points to global space

            float offset_x = Math.abs(object_x - (int) mCurrentPos.x);
            float offset_y = Math.abs(object_y - (int) mCurrentPos.y);

            double dist = Math.sqrt(offset_x * offset_x + offset_y * offset_y);
            distance_sum += dist;

            if (dist < min_distance) {
                min_distance = dist;
                index = i;
            }

        }

        object.mStartDrawPos = index;
        // triangle is not shown when following the first part of check
        int maxThickness = 20;
        int thickness = (int)  (threshold / (distance_sum / ((prefix_end_pos) - object.mStartDrawPos + 1) ));
        if (thickness > 1000 || thickness < 4) {
            thickness = 0;
            object.mStartDrawPos = 0;
        } else if (thickness > maxThickness) {
            thickness = maxThickness;
        }

        object.setThickness(thickness);
//        System.out.println("Thickness2: " + thickness);
    }

    // mDrawObject options here
    private void drawObject(Canvas canvas, Object object) {


        mFeedforwardPath = new Path();
        mPrefixPath = new Path();
        mFeebackPath = new Path();

        int[] points = object.mPoints;

        // could store transformed_triangle
        mFeebackPath.moveTo((int) mInitPos.x, (int) mInitPos.y);


        int index = getObjectPosOfPrefix(object);
        for (int x = 0; x < points.length; x += 2) {
            float x_pos = points[x] * mObjectScale + (int) mInitPos.x - points[0] * mObjectScale; // objects points to global space
            float y_pos = points[x + 1] * mObjectScale + (int) mInitPos.y - points[1] * mObjectScale; // objects points to global space
            if (x < object.mStartDrawPos) {
                mFeebackPath.lineTo(x_pos, y_pos);
                mFeedbackPaint.setStrokeWidth(object.mThickness);

            } else if (x == object.mStartDrawPos) {
//                float x_err = mCurrentPos.x - x_pos;
//                float y_err = mCurrentPos.y - y_pos;
//                object.mError = Math.sqrt((x_err * x_err) + (y_err * y_err));
                mFeebackPath.lineTo(x_pos, y_pos);
                mPrefixPath.moveTo(x_pos, y_pos);
                mFeedforwardPath.moveTo(x_pos, y_pos);
                if (object.mStartDrawPos >= points.length - 10) {
                    object.mExcecute = true;
                    mSelectedObject = object;
                    //mSelectedText = ;
                } else {
                    object.mExcecute = false;
                }

            } else if (x <= index) {
                mPrefixPath.lineTo(x_pos, y_pos);
                if (x == index) {
                    canvas.drawText(object.mName, x_pos, y_pos, mLabelPaint);
                    mFeedforwardPath.moveTo(x_pos, y_pos);
                }

            } else {
                mFeedforwardPath.lineTo(x_pos, y_pos);
            }
        }
        // noch nicht richtig!!!
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
        newPath = new ArrayList<>();
        for (String object : mObjects.keySet()) {
            Object obj = mObjects.get(object);
            obj.mStartDrawPos = 0;
            obj.mExcecute = false;
            obj.mError = 0.0;
            obj.setThickness(10);
        }
    }

}