/* 
 *
 * Copyright (C) 2013 The PlayTractor Team (support@playtractor.com)
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, see <http://www.gnu.org/licenses/>.
 */


package name.w.yellowduck;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor4F;

import android.util.FloatMath;

public class PolygonSprite extends PrimSprite {
	private CGPoint vertices[];
	private int numOfVertices;

	public PolygonSprite(int num) {
		super();
		this.numOfVertices=num;
		vertices=new CGPoint[num];
    	super.setType(kTypePrimPloygon);
	}
	
	public void setVertix(int idx, CGPoint pt) {
	    vertices[idx]=pt;
	}

	public CGPoint getVertix(int idx) {
	    return vertices[idx];
	}

	public int getNumOfVertices() {
	    return numOfVertices;
	}
	
    public void draw(GL10 gl) {
        if (!visible_)
            return;
        
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        
        
        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * numOfVertices);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer _vertex_buffer = vbb.asFloatBuffer();
	    for (int i = 0; i < numOfVertices; i++) {
	    	_vertex_buffer.put(vertices[i].x);
	    	_vertex_buffer.put(vertices[i].y);
	    }
        _vertex_buffer.position(0);
	    gl.glVertexPointer(2, GL10.GL_FLOAT, 0, _vertex_buffer);
	    
        ccColor4F _clr=clrBorder;
	    if (super.isSolid()) {
	        gl.glColor4f(clr.r, clr.g, clr.b, clr.a);
	    	gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, numOfVertices);
	    	_clr=clrBorder;
	    }
	    else {
	    	_clr=clr;
	    }
	    if (_clr.a > 0) {
	        gl.glLineWidth(super.getLineWidth());
	        gl.glColor4f(_clr.r, _clr.g, _clr.b, _clr.a);
		    gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, numOfVertices);
	    }
	    // restore original values
	    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);	    
    }
        
	@Override
	public void moveWithOffsetX(float xOffset, float yOffset) {
	    for (int i = 0; i < numOfVertices; ++i) {
	        vertices[i].x+= xOffset;
	        vertices[i].y+= yOffset;
	    }
	}

	@Override
	public CGRect enclosedArea() {
	    float xMin=0, xMax=0, yMin=0, yMax=0;
	    for (int i = 0; i < numOfVertices; ++i) {
	        if (i <= 0) {
	            xMin=xMax=vertices[i].x;
	            yMin=yMax=vertices[i].y;
	        }
	        else {
	            if (vertices[i].x < xMin)
	                xMin=vertices[i].x;
	            else if (vertices[i].x > xMax)
	                xMax=vertices[i].x;
	            if (vertices[i].y < yMin)
	                yMin=vertices[i].y;
	            else if (vertices[i].y > yMax)
	                yMax=vertices[i].y;
	        }
	    }    
	    return CGRect.make(xMin, yMin, xMax-xMin, yMax-yMin);
	}

	@Override
	public boolean hit(CGPoint pt) {
	    if (super.isSolid()) {
	        boolean isInside=this.inside(pt);
	        if (isInside)
	            return true;
	        //near enough to the center in case the rect is very small
	        for (int i = 0; i < numOfVertices; ++i) {
	            CGPoint center=vertices[i];
	            float xDiff=center.x-pt.x;
	            float yDiff=center.y-pt.y;
	            float distance=FloatMath.sqrt(xDiff*xDiff+yDiff*yDiff);
	            if (distance < kDistanceAsSelected)
	                return true;
	        }
	        return false;
	    }
	    else {
	        for (int i = 0; i < numOfVertices-1; ++i) {
	            if (!super.tooFarFromPoint(pt, vertices[i], vertices[i+1]) &&
	                    super.distanceFromPoint(pt, vertices[i],vertices[i+1]) <= kDistanceAsSelected)
	                return true;
	        }
	        return  !super.tooFarFromPoint(pt, vertices[0],vertices[numOfVertices-1]) && (super.distanceFromPoint(pt, vertices[0], vertices[numOfVertices-1]) <= kDistanceAsSelected);
	    }
	}

	public boolean inside(CGPoint pt) {
	    //  Globals which should be set before calling this function:
	    //
	    //  int    polySides  =  how many corners the polygon has
	    //  float  polyX[]    =  horizontal coordinates of corners
	    //  float  polyY[]    =  vertical coordinates of corners
	    //  float  x, y       =  point to be tested
	    //
	    //  (Globals are used in this example for purposes of speed.  Change as
	    //  desired.)
	    //
	    //  The function will return YES if the point x,y is inside the polygon, or
	    //  NO if it is not.  If the point is exactly on the edge of the polygon,
	    //  then the function may return YES or NO.
	    //
	    //  Note that division by zero is avoided because the division is protected
	    //  by the "if" clause which surrounds it.
	    //  http://alienryderflex.com/polygon/
	    
	    int   i, j=numOfVertices-1 ;
	    boolean  oddNodes=false;
	    float x=pt.x, y=pt.y;
	    CGPoint ptLast=this.getVertix(j);
	    for (i=0; i<numOfVertices; i++) {
	        CGPoint ptThis=this.getVertix(i);
	        if (((ptThis.y< y && ptLast.y>=y) || (ptLast.y< y && ptThis.y>=y)) &&  (ptThis.x<=x || ptLast.x<=x)) {
	            oddNodes^=(ptThis.x+(y-ptThis.y)/(ptLast.y-ptThis.y)*(ptLast.x-ptThis.x)<x);
	        }
	        j=i;
	        ptLast=this.getVertix(j);
	    }
	    return oddNodes;
	}
}
