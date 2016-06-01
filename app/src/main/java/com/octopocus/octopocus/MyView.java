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

    private Paint mPaint;
    private PointF point;
    private Path path = new Path();

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
    }


    // source:
    // http://stackoverflow.com/questions/31901364/android-using-ontouchlistener-in-canvas

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                point = new PointF(x, y);
                path = new Path();
                path.moveTo(point.x, point.y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                point.set(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                point = null;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (point != null) {

            path.lineTo(point.x, point.y);
            canvas.drawPath(path, mPaint);
        }
    }
}