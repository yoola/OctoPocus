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

public class TemplateData
{
	public static int copyPoints[] =
			{137,139,135,141,133,144,132,146,130,149,128,151,126,155,123,160,120,166,116,171,112,177,107,183,102,188,100,191,95,195,90,199,86,203,82,206,80,209,75,213,73,213,70,216,67,219,64,221,61,223,60,225,62,226};

	public static int pastePoints[] = {91,185,93,185,95,185,97,185,100,188,102,189,104,190,106,193,108,195,110,198,112,201,114,204,115,207,117,210,118,212,120,214,121,217,122,219,123,222,124,224,126,226,127,229,129,231,130,233,129,231,129,228,129,226,129,224,129,221,129,218,129,212,129,208,130,198,132,189,134,182,137,173,143,164,147,157,151,151,155,144};

	public static int selectPoints[] =
	{79,245,79,242,79,239,80,237,80,234,81,232,82,230,84,224,86,220,86,218,87,216,88,213,90,207,91,202,92,200,93,194,94,192,96,189,97,186,100,179,102,173,105,165,107,160,109,158,112,151,115,144,117,139,119,136,119,134,120,132};

	public static int cutPoints[] =
			{79,245,79,242,79,239,80,237,80,234,81,232,82,230,84,224,86,220,86,218,87,216,88,213,90,207,91,202,92,200,93,194,94,192,96,189,97,186,100,179,102,173,102,173,90,173,80,173,70,173,60,173,50,173};


	// Path from origin to split point --> join with Parts
	private static int splitPart[] = {100,146,100,149,100,152,100,153,100,156,100,157,100,159,100,159,100,161,100,163,100,164,100,165,100,166,100,167,100,169,100,170,100,172,100,173,100,175,100,177,100,179,100,181,100,183,100,185,100,188,100,189,100,192,100,194,100,195,100,198,100,198,100,200,100,202,100,204,100,206,100,208,100,210,100,212,100,214,100,216,100,217,100,219,100,220,100,223,100,224,100,226,100,227,100,228,100,229,100,230,100,231,100,232,100,233,100,234,100,235,100,236,100,236,100,237,100,238,100,238,100,239,100,240,100,240,100,241,100,242,100,242,100,243,100,244,100,249};

	private static int CopyPart[] = {100,250,93,252,92,251,91,251,90,250,88,250,87,249,85,248,84,247,83,246,82,245,81,244,80,243,79,242,78,241,77,240,76,240,76,238,76,238,75,237,74,236,74,235,73,234,72,233,71,232,70,231,70,230,69,229,69,228,68,227,68,225,67,224,67,222,66,221,66,218,66,217,65,216,65,214,65,213,65,212,65,212,65,211,65,210};

	private static int PastePart[] = {100,250,98,251,97,251,96,252,95,253,95,254,94,255,93,257,92,258,92,259,92,260,91,261,91,262,90,263,89,264,89,265,88,265,88,266,87,267,86,267,86,268,85,270,84,270,83,272,82,272,81,274,80,274,79,276,79,276,78,277,77,277,77,278,76,279,75,280,74,281,73,282,73,283,72,284,71,284,70,285,69,285,68,286,67,286,67,287,66,287,65,288,65,288,64,288,64,289,63,289,63,289,62,289,61,290};

	private static int SelectPart[] = {100,250,101,251,101,251,102,252,101,252,102,253,103,255,103,255,103,257,104,257,104,258,104,259,104,259,105,260,105,261,105,262,106,263,107,264,107,265,107,266,108,268,109,269,109,270,110,271,110,272,111,273,111,274,112,275,113,276,113,277,114,278,114,279,115,280,115,281,116,282,118,283,118,284,119,285,120,286,120,287,121,288,122,288,122,290,122,290,124,291,124,292,125,292,125,293,126,294,126,294,127,295,128,296,128,296,129,296,130,297,130,297};

	public static int CutPart[] = {100,250,104,250,104,251,105,252,105,253,106,253,106,254,107,255,108,255,108,256,109,257,110,257,111,257,112,258,112,258,114,259,114,259,116,260,117,261,118,261,119,261,121,261,122,261,123,262,124,262,125,262,126,262,127,262,128,262,129,263,130,263,132,263,133,263,134,263,135,263,136,263,137,263,138,263,140,263,140,263,142,263,143,263,145,262,145,262,147,261,148,261,150,260,151,260,153,259,154,258,156,257,157,255,159,254,160,252,162,251,163,249,164,248,165,248,166,246,166,246,167,244,167,244,168,243,168,242,169,241,169,240,169,240,169,239};

	public static int newCopyPath[];
	public static int newPastePath[];
	public static int newSelectPath[];
	public static int newCutPath[];

	static void copyMirror(int dst[], int src[])
	{		
		for (int i = 0; i < src.length/2; i++)
		{
			dst[ i*2 ] = src[src.length - i*2 - 2];  
			dst[ i*2 + 1 ] = src[src.length - i*2 + 1 - 2];  
	
		}
	
/*		for (int i = 0; i < dst.length/2; i++)
			System.out.println(dst[ i*2 ] + ", " + dst[ i*2 + 1 ] + "\t" + 
								src[ i*2 ] + ", " + src[ i*2 + 1 ]);		
*/	}
	
	static
	{
		// join split part and normal part
		newCopyPath = new int[splitPart.length + CopyPart.length];
		System.arraycopy( splitPart, 0, newCopyPath, 0, splitPart.length);
		System.arraycopy( CopyPart, 0, newCopyPath, splitPart.length, CopyPart.length );


		newPastePath = new int[splitPart.length + PastePart.length];
		System.arraycopy( splitPart, 0, newPastePath, 0, splitPart.length);
		System.arraycopy( PastePart, 0, newPastePath, splitPart.length, PastePart.length );

		newSelectPath = new int[splitPart.length + SelectPart.length];
		System.arraycopy( splitPart, 0, newSelectPath, 0, splitPart.length);
		System.arraycopy( SelectPart, 0, newSelectPath, splitPart.length, SelectPart.length );

		newCutPath = new int[splitPart.length + CutPart.length];
		System.arraycopy( splitPart, 0, newCutPath, 0, splitPart.length);
		System.arraycopy( CutPart, 0, newCutPath, splitPart.length, CutPart.length );

	}

}