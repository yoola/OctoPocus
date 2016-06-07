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
    private Paint mObjectPaint;
    private PointF point;
    private Path path = new Path();
    public Dollar dollar = new Dollar(1);
    public Dollar dollar_drawn_path = new Dollar(1);

    private float scale = 3;

    private int init_pos_x;
    private int init_pos_y;
    private int current_pos_x;
    private int current_pos_y;
    private int start_draw_position = 0;
    private boolean move = false;
    private boolean draw = true;


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
            current_pos_x = (int) point.x;
            current_pos_y = (int) point.y;

            path.lineTo(point.x, point.y);
            canvas.drawPath(path, mPaint);
            dollar.addPoint((int)point.x, (int)point.y);


            dollar_drawn_path.addPoint((int)point.x, (int)point.y);
            if (move) {
                TemplateData data = new TemplateData();
                int[] trianglePoints = data.trianglePoints;
                int index_of_position = getObjectPosition(trianglePoints,(int)point.x, (int)point.y );
                if (index_of_position != 0) { // current path on triangle
                    start_draw_position = index_of_position;
                    draw = true;
                } else {
                    draw = false;
                }
                int drawn_path[];

            }
            if (draw) {
                drawObject(canvas);
            }


        }
    }

    private int getObjectPosition(int[] trianglePoints, int x, int y) {
        int threshold = 180;
        float min_distance = 10000;
        float distance_sum = 0;
        int index = 0;
        for (int i = 0; i < trianglePoints.length; i++) {
            if ((i % 2) == 0) { // even
                float object_x = trianglePoints[i] * scale + init_pos_x - trianglePoints[0] * scale;
                float object_y = trianglePoints[i + 1] * scale + init_pos_y - trianglePoints[1] * scale;
                float offset_x = Math.abs(object_x - x);
                float offset_y = Math.abs(object_y - y);
                float off = offset_x + offset_y;
                distance_sum += off;
                if (off < min_distance) {
                    min_distance = off;
                    index = i;
                }
            }
        }
        if ((distance_sum / trianglePoints.length) < threshold) {
            return index;
        } else {
            return 0;
        }
    }

    // draw options here
    private void drawObject(Canvas canvas) {
        Path path_feedforward = new Path();
        Path path_prefix = new Path();
        TemplateData data = new TemplateData();
        int[] trianglePoints = data.trianglePoints;

        // could store tranformed_triangle
        path_prefix.moveTo(current_pos_x, current_pos_y);
        path_feedforward.moveTo(current_pos_x, current_pos_y);
//        path_prefix.moveTo(trianglePoints[0] * scale + init_pos_x - trianglePoints[0] * scale, trianglePoints[1] * scale + init_pos_y - trianglePoints[1] * scale);
//        path_feedforward.moveTo(trianglePoints[0] * scale + init_pos_x - trianglePoints[0] * scale, trianglePoints[1] * scale + init_pos_y - trianglePoints[1] * scale);
        int threshold = 50;
        for (int x = start_draw_position; x < trianglePoints.length; x += 2) {
            if (x < (start_draw_position + threshold)) {
                path_prefix.lineTo(trianglePoints[x] * scale + init_pos_x - trianglePoints[0] * scale, trianglePoints[x + 1] * scale + init_pos_y - trianglePoints[1] * scale);
            }
            path_feedforward.lineTo(trianglePoints[x] * scale + init_pos_x - trianglePoints[0] * scale, trianglePoints[x + 1] * scale + init_pos_y - trianglePoints[1] * scale);
        }
        mObjectPaint.setColor(Color.parseColor("#ccccff"));
        canvas.drawPath(path_feedforward, mObjectPaint);
        mObjectPaint.setColor(Color.parseColor("#7f7fff"));
        canvas.drawPath(path_prefix, mObjectPaint);
    }

    private boolean inObjectPath() {

        return true;
    }
}