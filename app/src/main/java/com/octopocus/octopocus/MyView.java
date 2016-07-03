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

    private PointF mCurrentPos;
    private PointF mInitPos;

    Map<String, Object> mObjects = new HashMap<>();
    TemplateData mObjectData = new TemplateData();

    private float mObjectScale = 3; // Scale of objects

    private boolean mTouchUp = false;
    private boolean mMoving = false;
    private boolean mSaveNewPath = false;
    private Object mSelectedObject = null;

    private List<Float> newPath = new ArrayList<>();
    private String mNewObjectName = "";



    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMenu();
        initPaint();
    }

    private void initMenu() {
        mObjects.put("Copy", new Object("Copy", mObjectData.copyPoints, "#ccccff", "#7f7fff"));
        mObjects.put("New Path: Copy", new Object("New Path: Copy", mObjectData.newCopyPath, "#7a7a7a", "#3b3b3b"));

        mObjects.put("Paste", new Object("Paste", mObjectData.checkPoints, "#8ae32b", "#208a18"));
        mObjects.put("New Path: Paste", new Object("New Path: Paste", mObjectData.newPastePath, "#7a7a7a", "#3b3b3b"));

        mObjects.put("Select", new Object("Select", mObjectData.caretPointsCW, "#FE642E", "#B43104"));
        mObjects.put("New Path: Select", new Object("New Path: Select", mObjectData.newSelectPath, "#7a7a7a", "#3b3b3b"));

        mObjects.put("Cut", new Object("Cut", mObjectData.caretPointsCW2,"#c19465", "#513211"));
        mObjects.put("New Path: Cut", new Object("New Path: Cut", mObjectData.newCutPath, "#7a7a7a", "#3b3b3b"));
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

    // source:
    // http://stackoverflow.com/questions/31901364/android-using-ontouchlistener-in-canvas

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //mSelectedObject.mPathPaint.setStrokeWidth(10);
                mSelectedObject = null;
                mTouchUp = false;

                mInitPos = new PointF(x, y);
                mCurrentPos = new PointF(x, y);
                mNewPath = new Path();
                mNewPath.moveTo((int) mInitPos.x, (int) mInitPos.y);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                mCurrentPos.set(x, y);
                mMoving = true;
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
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

        // if path was selected display name
        if (mSelectedObject != null && mTouchUp) {
            // positioning the name of the selected path in the center of the display
            int offset = (mSelectedObject.getName().length() / 2);
            int width = (this.getWidth() / 2) - (offset * 20);
            int height = (this.getHeight() / 2) - offset;
            canvas.drawText(mSelectedObject.getName(), width, height, mSelectedObject.getTextPaint());

        } else {

            if (mCurrentPos != null) {

                if (mSaveNewPath && mMoving) {
                    mMoving = false;
                    newPath.add(mCurrentPos.x); // getting the points for the new path
                    newPath.add(mCurrentPos.y);
                }

                for (String object : mObjects.keySet()) {
                    mObjects.get(object).setStartPosition(mInitPos, mCurrentPos); // current start position in object
                    mObjects.get(object).setError(mInitPos, mCurrentPos); // error to finger tip

                    if (mSaveNewPath) {
                        drawNewPath(canvas, mObjects.get(object));
                    } else {
                        drawObject(canvas, mObjects.get(object));
                    }


                }
            }
        }
    }


    // mDrawObject options here
    private void drawNewPath(Canvas canvas, Object object) {
        mFeedforwardPath = new Path();

        mFeedforwardPath.moveTo((int) mInitPos.x, (int) mInitPos.y);
        mNewPath.lineTo((int) mCurrentPos.x, (int) mCurrentPos.y);

        int[] points = object.getPoints();
        for (int x = 0; x < points.length; x += 2) {

            float x_pos = points[x] * mObjectScale + (int) mInitPos.x - points[0] * mObjectScale; // objects points to global space
            float y_pos = points[x + 1] * mObjectScale + (int) mInitPos.y - points[1] * mObjectScale; // objects points to global space
            mFeedforwardPath.lineTo(x_pos, y_pos);

            if (x == (points.length - 2) && !(object.getName().equals(mNewObjectName))) {
                mNewPaint.setStrokeWidth(4);
                canvas.drawText(object.getName(), x_pos, y_pos,  mNewPaint);
                mNewPaint.setStrokeWidth(10);
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

        mFeedbackPath.moveTo((int) mInitPos.x, (int) mInitPos.y);

        int prefix_end_index = object.getNearestPointToCursor(mInitPos, mCurrentPos);

        int[] points = object.getPoints();
        for (int x = 0; x < points.length; x += 2) {
            float x_pos = points[x] * mObjectScale + (int) mInitPos.x - points[0] * mObjectScale; // objects points to global space
            float y_pos = points[x + 1] * mObjectScale + (int) mInitPos.y - points[1] * mObjectScale; // objects points to global space

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
        if (mSaveNewPath) {
            setNewPath();
            mSaveNewPath = false;
        }

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
                    } else {
                        mSaveNewPath = false;
                    }
                }
                mSelectedObject = object;
                invalidate();
            }
        }

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
        object.setPoints(points);
    }



}