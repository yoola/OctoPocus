package com.octopocus.octopocus;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;


public class MyView extends View {

    private Paint mPaint;
    private Paint mObjectPaint;
    private PointF point;
    private Path path = new Path();
    public Dollar dollar = new Dollar(1);
    private int init_pos_x;
    private int init_pos_y;
    private boolean move = false;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
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
                point = new PointF(x, y);
                path = new Path();
                path.moveTo(point.x, point.y);
                init_pos_x = (int) x;
                init_pos_y = (int)y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                point.set(x, y);
                move = true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                dollar.recognize();
                ((MainActivity) this.getContext()).writeDollar(dollar);
                dollar.clear();
                move = false;


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
            dollar.addPoint((int)point.x, (int)point.y);

            drawObject(canvas);


        }
    }

    // draw options here
    private void drawObject(Canvas canvas) {
        Path path_objects = new Path();
        TemplateData data = new TemplateData();
        int[] trianglePoints = data.trianglePoints;
        float scale = 3;
        path_objects.moveTo(trianglePoints[0] * scale + init_pos_x - trianglePoints[0] * scale, trianglePoints[1] * scale + init_pos_y - trianglePoints[1] * scale);
        for (int x = 0; x < trianglePoints.length; x += 2) {
            path_objects.lineTo(trianglePoints[x] * scale + init_pos_x - trianglePoints[0] * scale, trianglePoints[x + 1] * scale + init_pos_y - trianglePoints[1] * scale);
        }
        canvas.drawPath(path_objects, mObjectPaint);
    }
}