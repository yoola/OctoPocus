/* -------------------------------------------------------------------------
 *
 *	$1 Java
 *
 * 	This is a Java port of the $1 Gesture Recognizer by
 *	Jacob O. Wobbrock, Andrew D. Wilson, Yang Li.
 * 
 *	"The $1 Unistroke Recognizer is a 2-D single-stroke recognizer designed for 
 *	rapid prototyping of gesture-based user interfaces."
 *	 
 *	http://depts.washington.edu/aimgroup/proj/dollar/
 *
 *	Copyright (C) 2009, Alex Olwal, www.olwal.com
 *
 *	$1 Java free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	$1 Java is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with $1 Java.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  -------------------------------------------------------------------------
 */

package com.octopocus.octopocus;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class Object {
	private String mName;
	private int[] mPoints;
	private int mStartPos = 0;
	private int mThickness = 10;
	private boolean mExecute = false;


	private Paint mTextPaint = new Paint();
	private Paint mPathPaint = new Paint();
	private Paint mPrefixPaint = new Paint();

	private int mPrefixLength = 1200;
	private float mObjectScale = 3;
	private float mErrorThreshold = 150; // max distance to finger tip for following



	Object(String name, int[] points, String pathColor, String prefixColor) {
		this.mName = name;

		this.mPoints = points;

		this.mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mTextPaint.setStyle(Paint.Style.STROKE);
		this.mTextPaint.setColor(Color.parseColor(pathColor));
		this.mTextPaint.setStrokeWidth(4);
		this.mTextPaint.setTextSize(40);

		this.mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mPathPaint.setStyle(Paint.Style.STROKE);
		this.mPathPaint.setColor(Color.parseColor(pathColor));
		this.mPathPaint.setStrokeWidth(mThickness);

		this.mPrefixPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mPrefixPaint.setStyle(Paint.Style.STROKE);
		this.mPrefixPaint.setColor(Color.parseColor(prefixColor));
		this.mPrefixPaint.setStrokeWidth(mThickness);
	}


	// Getter and Setter

	// which point (index) in the object lies nearest to the cursor
	public void setStartPosition(PointF initPos, PointF currentPos) {
		int threshold = 500;
		int maxThickness = 20;
		float distance_sum = 0;
		double min_distance = 10000;

		int prefix_end_index = this.getNearestPointToCursor(initPos, currentPos);
		for (int i = mStartPos; i < prefix_end_index; i += 2) {
			float object_x = mPoints[i] * mObjectScale + (int) initPos.x - mPoints[0] * mObjectScale; // objects points to global space
			float object_y = mPoints[i + 1] * mObjectScale + (int) initPos.y - mPoints[1] * mObjectScale; // objects points to global space

			float offset_x = Math.abs(object_x - (int) currentPos.x);
			float offset_y = Math.abs(object_y - (int) currentPos.y);
			double dist = Math.sqrt(offset_x * offset_x + offset_y * offset_y);

			distance_sum += dist;

			if (dist < min_distance) {
				min_distance = dist;
				mStartPos = i;
			}
		}

		int thickness = (int) (threshold / (distance_sum / ((prefix_end_index) - mStartPos + 1)));
		if (thickness > 1000 || thickness < 4) {
			thickness = 0;
			mStartPos = 0;
		} else if (thickness > maxThickness) {
			thickness = maxThickness;
		}
		setThickness(thickness);
	}


	// which point (index) in the object lies nearest to the cursor
	public int getNearestPointToCursor(PointF mInitPos, PointF mCurrentPos) {
		double sum_distances = 0;
		int index = 0;

		for (int x = mStartPos; x < mPoints.length; x += 2) {
			float x_pos = mPoints[x] * mObjectScale + (int) mInitPos.x - mPoints[0] * mObjectScale; // objects points to global space
			float y_pos = mPoints[x + 1] * mObjectScale + (int) mInitPos.y - mPoints[1] * mObjectScale; // objects points to global space
			float x_diff = mCurrentPos.x - x_pos;
			float y_diff = mCurrentPos.y - y_pos;
			double distance = Math.sqrt((x_diff * x_diff) + (y_diff * y_diff));

			sum_distances += distance;
			if (sum_distances < mPrefixLength) {
				index = x;
			}
		}
		return index;
	}

	// error between finger and start position
	public void setError(PointF initPos, PointF currentPos) {
		int[] points = mPoints;
		int i = mStartPos;

		float x_pos = points[i] * mObjectScale + (int) initPos.x - points[0] * mObjectScale; // objects points to global space
		float y_pos = points[i + 1] * mObjectScale + (int) initPos.y - points[1] * mObjectScale; // objects points to global space

		float x_err = currentPos.x - x_pos;
		float y_err = currentPos.y - y_pos;

		double error = Math.sqrt((x_err * x_err) + (y_err * y_err));

		if (error > mErrorThreshold) {
			mStartPos = 0;
		}
	}



	public void setThickness(int thickness) {
		this.mThickness = thickness;
		this.mPathPaint.setStrokeWidth(thickness);
		this.mPrefixPaint.setStrokeWidth(thickness);
	}

	public Paint getTextPaint() {
		return mTextPaint;
	}

	public String getName() {
		return mName;
	}

	public int getStartPos() {
		return mStartPos;
	}

	public int[] getPoints() {
		return mPoints;
	}

	public void setPoints(int[] points) {
		this.mPoints = points;
	}

	public void clear() {
		mStartPos = 0;
		mExecute = false;
		setThickness(10);
	}

	public boolean getExcecute() {
		return mExecute;
	}

	public void setExecute(boolean execute) {
		this.mExecute = execute;
	}

	public float getThickness() {
		return mThickness;
	}

	public Paint getPathPaint() {
		return mPathPaint;
	}

	public Paint getPrefixPaint() {
		return mPrefixPaint;
	}


}
