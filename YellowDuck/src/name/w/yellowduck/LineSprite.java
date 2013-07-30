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

public class LineSprite extends PrimSprite {
    private CGPoint p1, p2;

    public LineSprite(CGPoint _pt1, CGPoint _pt2) {
    	super();
    	this.p1=_pt1;
    	this.p2=_pt2;
    	super.setType(kTypePrimLine);
    }

    
    public CGPoint getP1() {
		return p1;
	}


	public void setP1(CGPoint p1) {
		this.p1 = p1;
	}


	public CGPoint getP2() {
		return p2;
	}


	public void setP2(CGPoint p2) {
		this.p2 = p2;
	}


	@Override
    public void draw(GL10 gl) {
        if (!visible_)
            return;
        
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        
        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * 2);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = vbb.asFloatBuffer();

        vertices.put(p1.x);
        vertices.put(p1.y);
        vertices.put(p2.x);
        vertices.put(p2.y);
        vertices.position(0);

        gl.glLineWidth(super.getLineWidth());
        gl.glColor4f(clr.r, clr.g, clr.b, clr.a);
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertices);
        gl.glDrawArrays(GL10.GL_LINES, 0, 2);
        
	    // restore original values
	    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);	    
	}

	@Override
	public void moveWithOffsetX(float xOffset, float yOffset) {
	    p1.x += xOffset; p1.y += yOffset;
	    p2.x += xOffset; p2.y += yOffset;
	}

	@Override
	public CGRect enclosedArea() {
	    float xMin=p1.x, xMax=p1.x;
	    float yMin=p1.y, yMax=p1.y;
	    if (p2.x < xMin)
	        xMin=p2.x;
	    else if (p2.x > xMax)
	        xMax=p2.x;
	    if (p2.y < yMin)
	        yMin=p2.y;
	    else if (p2.y > yMax)
	        yMax=p2.y;    
	    return CGRect.make(xMin, yMin, xMax-xMin, yMax-yMin);
	}

	@Override
	public boolean hit(CGPoint pt) {
	    if (super.tooFarFromPoint(pt, p1, p2))
	        return false;
	    return super.distanceFromPoint(pt, p1, p2) <= kDistanceAsSelected;
	}
}
