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

public class Object
{
	String mName;
	int[] mPoints;
	int mStartDrawPos = 0;
	boolean mDraw;
	Paint mPathPaint = new Paint();
	Paint mPrefixPaint = new Paint();

	Object(String name, int[] points, String pathColor, String prefixColor)
	{
		this.mName = name;

		this.mPoints = points;

        this.mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPathPaint.setStyle(Paint.Style.STROKE);
        this.mPathPaint.setColor(Color.parseColor(pathColor));
        this.mPathPaint.setStrokeWidth(10);

        this.mPrefixPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPrefixPaint.setStyle(Paint.Style.STROKE);
        this.mPrefixPaint.setColor(Color.parseColor(prefixColor));
        this.mPrefixPaint.setStrokeWidth(10);

		mDraw = true;
	}




}