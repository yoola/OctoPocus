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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// to do: - adapt the gesture size to display size
//        - only view the branches for copy/paste/select/cut of new path if new path was chosen
//        - hide path menu for experts
//        - an redo/undo function?

public class MyView extends View {

    private Paint mFeedbackPaint;
    private Paint mNewPaint;

    private Path mFeedbackPath = new Path();
    private Path mFeedforwardPath = new Path();
    private Path mPrefixPath = new Path();
    private Path mNewPath = new Path();

    private Point mCurrentPos;
    private Point mInitPos;

    Map<String, Object> mObjects = new HashMap<>();
    TemplateData mObjectData = new TemplateData();

    private float mObjectScale = 3; // Scale of objects
    private int mMaxThickness = 10;
    private double mTime = 0;

    private boolean mOnClick = false;
    private boolean mDisplayNewPathText = false;
    private boolean mNoviceMode = false;
    private boolean mUnclearMode = true;
    private boolean mFirstTouch = true;
    private boolean mTouchUp = false;
    private boolean mMoving = false;
    private boolean mSaveNewPath = false;
    private boolean mTryAgain = false;
    private Object mSelectedObject = null;

    private List<Double> newPath = new ArrayList<>();
    private String mNewObjectName = "";

    private Dollar mDollar = new Dollar(1);


    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mFeedbackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFeedbackPaint.setStyle(Paint.Style.STROKE);
        mFeedbackPaint.setStrokeWidth(10);

        mNewPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNewPaint.setColor(Color.parseColor("#7A7A7A"));
        mNewPaint.setStyle(Paint.Style.STROKE);
        mNewPaint.setStrokeWidth(10);
        mNewPaint.setTextSize(40);
    }

    private void initMenu() {
        int width = (this.getWidth());
        int height = (this.getHeight());

        if (Math.sqrt((width * height)) < 10000) {
            mObjectScale = 3;
            mMaxThickness = 20;
        } else {
            mObjectScale = 6;
            mMaxThickness = 40;
        }

        mObjects.put("Copy", new Object("Copy", mObjectData.copyPoints, "#ccccff", "#7f7fff", mObjectScale, mMaxThickness));
        mObjects.put("New Path: Copy", new Object("New Path: Copy", mObjectData.newCopyPath, "#7a7a7a", "#3b3b3b", mObjectScale, mMaxThickness));

        mObjects.put("Paste", new Object("Paste", mObjectData.pastePoints, "#8ae32b", "#208a18", mObjectScale, mMaxThickness));
        mObjects.put("New Path: Paste", new Object("New Path: Paste", mObjectData.newPastePath, "#7a7a7a", "#3b3b3b", mObjectScale, mMaxThickness));

        mObjects.put("Select", new Object("Select", mObjectData.selectPoints, "#FE642E", "#B43104", mObjectScale, mMaxThickness));
        mObjects.put("New Path: Select", new Object("New Path: Select", mObjectData.newSelectPath, "#7a7a7a", "#3b3b3b", mObjectScale, mMaxThickness));

        mObjects.put("Cut", new Object("Cut", mObjectData.cutPoints,"#c19465", "#513211", mObjectScale, mMaxThickness));
        mObjects.put("New Path: Cut", new Object("New Path: Cut", mObjectData.newCutPath, "#7a7a7a", "#3b3b3b", mObjectScale, mMaxThickness));
    }


    // source:
    // http://stackoverflow.com/questions/31901364/android-using-ontouchlistener-in-canvas

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTime = System.currentTimeMillis();
                //mSelectedObject.mPathPaint.setStrokeWidth(10);
                mOnClick = true;
                if (mFirstTouch) {
                    initMenu();
                    mFirstTouch = false;
                }
                mSelectedObject = null;
                mDisplayNewPathText = false;
                mTouchUp = false;

                mInitPos = new Point(x, y);
                mCurrentPos = new Point(x, y);
                mNewPath = new Path();
                mNewPath.moveTo((int) mInitPos.X, (int) mInitPos.Y);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                mCurrentPos = new Point(x, y);
                mMoving = true;
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                if (mNoviceMode == false) {
                    mDollar.recognize();
                    ((MainActivity) this.getContext()).writeDollar(mDollar);
                    String execute_name = mDollar.result.Name;
                    for (String objectName : mObjects.keySet()) {
                        Object object = mObjects.get(objectName);
                        if (object.getName().equals(execute_name)) {
                            object.setExecute(true);
                        }
                    }

                }
                mUnclearMode = true;
                mOnClick = false;
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

        // decide if Novice or Expert Mode
        double time = System.currentTimeMillis();
        double t = time - mTime;
        if (mUnclearMode && mOnClick && mCurrentPos != null) {
            double p_x = mInitPos.X - mCurrentPos.X;
            double p_y = mInitPos.Y - mCurrentPos.Y;
            double distance = Math.sqrt(p_x * p_x + p_y * p_y);
            if (t > 300) {
                if (distance < 3){ // user was fast enough above threshold
                    mNoviceMode = true;
                    mUnclearMode = false;
                    invalidate();
                } else {
                    mNoviceMode = false;
                    mUnclearMode = false;
                    invalidate();
                }
            } else {
                if (distance > 6) {
                    mNoviceMode = false;
                    mUnclearMode = false;
                    //invalidate();
                }
                invalidate();
            }
        } else {
            if ((mDisplayNewPathText && mTouchUp) || (mTryAgain && mTouchUp)) {
                String text = "";
                if (mTryAgain) {
                    text = "Path to short. Try again: " + mNewObjectName;
                } else {
                    text = "Hold to draw Path for " + mNewObjectName;
                }
                int offset = (text.length() / 2);
                int width = (this.getWidth() / 2) - (offset * 20);
                int height = (this.getHeight() / 2) - offset;
                mNewPaint.setStrokeWidth(5);
                canvas.drawText(text, width, height, mNewPaint);
                mNewPaint.setTextSize(40);
            }

            // if path was selected display name
            if (mSelectedObject != null && mTouchUp) {
                // positioning the name of the selected path in the center of the display
                int offset = (mSelectedObject.getName().length() / 2);
                int width = (this.getWidth() / 2) - (offset * 20);
                int height = (this.getHeight() / 2) - offset;
                canvas.drawText(mSelectedObject.getName(), width, height, mSelectedObject.getTextPaint());

            } else {

                if (mCurrentPos != null) {

                    // save new Path
                    if (mSaveNewPath && mMoving) {
                        mMoving = false;
                        newPath.add(mCurrentPos.X); // getting the points for the new path
                        newPath.add(mCurrentPos.Y);
                    }

                    if (mNoviceMode) { // Novice Mode
                        for (String object : mObjects.keySet()) {
                            mObjects.get(object).setStartPosition(mInitPos, mCurrentPos); // current start position in object
                            mObjects.get(object).setError(mInitPos, mCurrentPos); // error to finger tip

                            if (mSaveNewPath) {
                                drawNewPath(canvas, mObjects.get(object));
                            } else {
                                drawObject(canvas, mObjects.get(object));
                            }
                        }

                    } else { // Expert Mode
                        if (mSaveNewPath) {
                            mDisplayNewPathText = true;
                        } else {
                            mDollar.addPoint((int) mCurrentPos.X, (int) mCurrentPos.Y);
                        }
                    }
                }
            }
        }

    }


    // mDrawObject options here
    private void drawNewPath(Canvas canvas, Object object) {
        mFeedforwardPath = new Path();

        mFeedforwardPath.moveTo((int) mInitPos.X, (int) mInitPos.Y);
        mNewPath.lineTo((int) mCurrentPos.X, (int) mCurrentPos.Y);

        int[] points = object.getPoints();
        for (int x = 0; x < points.length; x += 2) {

            float x_pos = points[x] * mObjectScale + (int) mInitPos.X - points[0] * mObjectScale; // objects points to local space
            float y_pos = points[x + 1] * mObjectScale + (int) mInitPos.Y - points[1] * mObjectScale; // objects points to local space
            mFeedforwardPath.lineTo(x_pos, y_pos);

            if (x == (points.length - 2) && !(object.getName().equals(mNewObjectName))) {
                mNewPaint.setStrokeWidth(4);
                canvas.drawText(object.getName(), x_pos, y_pos,  mNewPaint);
                mNewPaint.setStrokeWidth(mMaxThickness);
            }
        }

        canvas.drawPath(mFeedbackPath, mNewPaint);
        if ((object.getName().equals(mNewObjectName))) {
            mNewPaint.setColor(object.getPathPaint().getColor());
            canvas.drawPath(mNewPath, mNewPaint);
            mNewPaint.setColor(Color.parseColor("#7A7A7A"));
        } else {
            canvas.drawPath(mFeedforwardPath, mNewPaint);
        }
    }

    // draw object
    private void drawObject(Canvas canvas, Object object) {
        mFeedforwardPath = new Path();
        mPrefixPath = new Path();
        mFeedbackPath = new Path();

        mFeedbackPath.moveTo((int) mInitPos.X, (int) mInitPos.Y);

        int prefix_end_index = object.getNearestPointToCursor(mInitPos, mCurrentPos);

        int[] points = object.getPoints();
        for (int x = 0; x < points.length; x += 2) {
            float x_pos = points[x] * mObjectScale + (int) mInitPos.X - points[0] * mObjectScale; // objects points to local space
            float y_pos = points[x + 1] * mObjectScale + (int) mInitPos.Y - points[1] * mObjectScale; // objects points to local space

            if (x < object.getStartPos()) {
                mFeedbackPath.lineTo(x_pos, y_pos);

            } else if (x == object.getStartPos()) {
                mFeedbackPath.lineTo(x_pos, y_pos);
                mPrefixPath.moveTo(x_pos, y_pos);
                if (x >= points.length - 10) { // finger tip is near to the end of path
                    object.setExecute(true);
                    mSelectedObject = object;
                } else {
                    object.setExecute(false);
                }

            } else if (x < prefix_end_index) {
                mPrefixPath.lineTo(x_pos, y_pos);

            } else if (x == prefix_end_index) {
                mPrefixPath.lineTo(x_pos, y_pos);
                mFeedforwardPath.moveTo(x_pos, y_pos);
                canvas.drawText(object.getName(), x_pos, y_pos,  object.getTextPaint());

            } else {
                mFeedforwardPath.lineTo(x_pos, y_pos);
            }
//            if (x == (points.length - 2)) {
//                canvas.drawText(object.mName, x_pos, y_pos,  object.mTextPaint);
//            }
        }

        mFeedbackPaint.setStrokeWidth(object.getThickness());
        canvas.drawPath(mFeedbackPath, mFeedbackPaint);

        if (object.getThickness() != 0) {
            canvas.drawPath(mFeedforwardPath, object.getPathPaint());
            canvas.drawPath(mPrefixPath, object.getPrefixPaint());
        }
    }


    private void clear() {
        if (mSaveNewPath && mNoviceMode) {
            setNewPath();

        } else {
            for (String objectName : mObjects.keySet()) {
                Object object = mObjects.get(objectName);
                if (object.getExcecute()) { // excecute function of command
                    ((MainActivity) this.getContext()).executeCommand(object.getName());
                    if (object.getName().length() < 10) {
                        mSaveNewPath = false;
                    } else {
                        String substring = object.getName().substring(0, 10);
                        if (substring.equals("New Path: ")) {
                            mSaveNewPath = true;
                            mNewObjectName = object.getName().substring(10, object.getName().length());
                            break;
                        } else {
                            mSaveNewPath = false;
                        }
                    }
                    mSelectedObject = object;
                    invalidate();
                }
            }
        }


        mDollar.clear();

        mTouchUp = true;
        newPath = new ArrayList<>();

        for (String object : mObjects.keySet()) {
            mObjects.get(object).clear();
        }
    }

    private void setNewPath() {
        Object object = mObjects.get(mNewObjectName);
        int[] points = new int[newPath.size()];
        for (int i = 0; i < newPath.size(); i++) {
            points[i] = (int) (newPath.get(i) / mObjectScale);
        }
        if (newPath.size() > 40) {
            mDollar.setNewPoints(mNewObjectName, points);
            object.setPoints(points);
            mTryAgain = false;
            mSaveNewPath = false;
        } else {
            mTryAgain = true;
        }

    }



}