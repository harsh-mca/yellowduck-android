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

public class EllipseSprite extends PrimSprite {
    private CGPoint center;
    private float rx, ry;
    

    public EllipseSprite(CGPoint pt, float x, float y) {
    	super();
    	this.center=pt;
    	this.rx=x;
    	this.ry=y;
    	super.setType(kTypePrimEllipse);
    }

    public CGPoint getCenter() {
		return center;
	}

	public void setCenter(CGPoint center) {
		this.center = center;
	}

	public float getRx() {
		return rx;
	}

	public void setRx(float rx) {
		this.rx = rx;
	}

	public float getRy() {
		return ry;
	}

	public void setRy(float ry) {
		this.ry = ry;
	}

	@Override
    public void draw(GL10 gl) {
        if (!visible_)
            return;
        
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        
    	float vertices[] = this.segments();
        int segs=vertices.length/2;
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer _vertex_buffer = vbb.asFloatBuffer();
        _vertex_buffer.put(vertices);
        _vertex_buffer.position(0);
	    gl.glVertexPointer(2, GL10.GL_FLOAT, 0, _vertex_buffer);
        
        ccColor4F _clr=clrBorder;
        if (super.isSolid()) {
            gl.glColor4f(clr.r, clr.g, clr.b, clr.a);
    	    gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, segs);
	    	_clr=clrBorder;
	    }
	    else {
	    	_clr=clr;
	    }
	    if (_clr.a > 0) {
	        gl.glLineWidth(super.getLineWidth());
	        gl.glColor4f(_clr.r, _clr.g, _clr.b, _clr.a);
		    gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, super.isSolid()?segs-1:segs);
	    }
        
	    // restore original values
	    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);	    
	}

    //segment coordinations of this ellipse
    private float[] segments() {
        float a=M_PI/2;
        int segs=(int)((rx+ry)/60*20);
        if (segs < 20)
            segs=20;
        float coef = 2.0f * M_PI / segs;
        
        float vertices[] = new float[2*(segs+1+(super.isSolid()?1:0))];
        
        float rads, distance, angle, j, k;
        for(int i = 0; i <= segs; ++i) {
            rads = i * coef;
            float xd=FloatMath.sin(rads) * rx;
            float yd=FloatMath.cos(rads) * ry;
            distance = FloatMath.sqrt(xd*xd + yd*yd);
            angle = (float)Math.atan2(FloatMath.sin(rads) * rx, FloatMath.cos(rads) * ry);
            j = distance * FloatMath.cos(angle + a) + center.x;
            k = distance * FloatMath.sin(angle + a) + center.y;

            vertices[i*2] = j; 
            vertices[i*2+1] = k;
        }
        if (super.isSolid()) {
            vertices[(segs+1)*2] = center.x;
            vertices[(segs+1)*2+1] = center.y;
        }
        return vertices;
    }

	@Override
	public void moveWithOffsetX(float xOffset, float yOffset) {
		center.x += xOffset; center.y += yOffset;
	}

	@Override
	public CGRect enclosedArea() {
        float xMin=center.x - rx, xMax=center.x+rx;
        float yMin=center.y - ry, yMax=center.y+ry;
        return CGRect.make(xMin, yMin, xMax-xMin, yMax-yMin);
	}

	@Override
	public boolean hit(CGPoint pt) {
        if (super.isSolid()) {
            float xDiff=pt.x-center.x;
            float yDiff=pt.y-center.y;
            
            float value=xDiff*xDiff/(rx*rx) + yDiff*yDiff/(ry*ry);
            return value <= 1;
        }
        else {
            float xDiff=pt.x-center.x;
            float yDiff=pt.y-center.y;

            float valueMin=xDiff*xDiff/((rx-kDistanceAsSelected)*(rx-kDistanceAsSelected)) + yDiff*yDiff/((ry-kDistanceAsSelected)*(ry-kDistanceAsSelected));
            float valueMax=xDiff*xDiff/((rx+kDistanceAsSelected)*(rx+kDistanceAsSelected)) + yDiff*yDiff/((ry+kDistanceAsSelected)*(ry+kDistanceAsSelected));
            return valueMin >= 1.0f && valueMax <= 1.0f;
        }
	}

}
