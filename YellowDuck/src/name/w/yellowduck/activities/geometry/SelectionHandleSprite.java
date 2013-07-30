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


package name.w.yellowduck.activities.geometry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor4F;

public class SelectionHandleSprite extends CCNode {
	private final int kDefaultHandleSize         =12;
	private CGPoint points[]=new CGPoint[4];
    private int hotspot;

    private CGRect workingArea;
    private int handleSize;

    private ccColor4F clr;
    public SelectionHandleSprite(CGPoint p1_, CGPoint p2_, CGPoint p3_, CGPoint p4_) {
    	super();
        points[0]=p1_;
        points[1]=p2_;
        points[2]=p3_;
        points[3]=p4_;
        
        this.clr=new ccColor4F(0, 1.0f, 1.0f, 0.6f);
        handleSize=kDefaultHandleSize;
        hotspot=-1;//nothing selected
    }
    

    public CGRect getWorkingArea() {
		return workingArea;
	}

	public void setWorkingArea(CGRect workingArea) {
		this.workingArea = workingArea;
	}

	public int getHandleSize() {
		return handleSize;
	}

	public void setHandleSize(int handleSize) {
		this.handleSize = handleSize;
	}

    public void draw(GL10 gl) {
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        
        gl.glLineWidth(2);
        gl.glColor4f(clr.r, clr.g, clr.b, clr.a);

        this.drawSolidPoly(gl, points, this.clr);
	    
	    CGPoint vertices[]=new CGPoint[4];
	    for (int i= 0; i < 8; ++i) {
	        CGPoint pt=this.getHotspotCenterLocation(i);
	        vertices[0]=CGPoint.ccp(pt.x-handleSize, pt.y-handleSize);
	        vertices[1]=CGPoint.ccp(pt.x+handleSize, pt.y-handleSize);
	        vertices[2]=CGPoint.ccp(pt.x+handleSize, pt.y+handleSize);
	        vertices[3]=CGPoint.ccp(pt.x-handleSize, pt.y+handleSize);
	        this.drawSolidPoly(gl, vertices, this.clr);
	    }
	    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);	    
	}
	private void drawSolidPoly(GL10 gl, CGPoint pts[], ccColor4F clr_) {
		int numOfVertices=pts.length;
        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * numOfVertices);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer _vertex_buffer = vbb.asFloatBuffer();
	    for (int i = 0; i < numOfVertices; i++) {
	    	_vertex_buffer.put(pts[i].x);
	    	_vertex_buffer.put(pts[i].y);
	    }
        _vertex_buffer.position(0);
	    gl.glVertexPointer(2, GL10.GL_FLOAT, 0, _vertex_buffer);
	    
        gl.glColor4f(clr_.r, clr_.g, clr_.b, clr_.a);
    	gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, numOfVertices);
	}
    
	public CGPoint getPoint(int idx) {
	    return points[idx];
	}
	//Simple implementation supporting rectangle only
	public boolean hit(CGPoint ptTouched) {
	    hotspot=-1;
	    for (int i= 0; i < 8; ++i) {
	        CGPoint pt=this.getHotspotCenterLocation(i);
	        CGRect rc=CGRect.make(pt.x-handleSize, pt.y-handleSize, handleSize*2, handleSize*2);
	        if (rc.contains(ptTouched.x, ptTouched.y)) {
	            hotspot=i;
	            break;
	        }
	    }
	    //test the body
	    if (hotspot < 0) {
	        CGRect rc=CGRect.make(points[0].x, points[0].y, points[1].x-points[0].x, points[2].y-points[1].y);
	        if (rc.contains(ptTouched.x, ptTouched.y)) {
	            hotspot=8;
	        }
	    }
	    return hotspot>=0;
	}
	public void move(float xOffset, float yOffset) {
	    if (hotspot < 0)
	        return;
	    switch (hotspot) {
	        case 0://left bottom
	            points[0].x+=xOffset;
	            points[0].y+=yOffset;
	            points[1].y+=yOffset;
	            points[3].x+=xOffset;
	            break;
	        case 1: //right bottom
	            points[1].x+=xOffset;
	            points[1].y+=yOffset;
	            points[0].y+=yOffset;
	            points[2].x+=xOffset;
	            break;
	        case 2://top right
	            points[2].x+=xOffset;
	            points[2].y+=yOffset;
	            points[3].y+=yOffset;
	            points[1].x+=xOffset;
	            break;
	        case 3://top left
	            points[3].x+=xOffset;
	            points[3].y+=yOffset;
	            points[2].y+=yOffset;
	            points[0].x+=xOffset;
	            break;
	        case 4: //bottom middle
	            points[0].y+=yOffset;
	            points[1].y+=yOffset;
	            break;
	        case 5: //right middle
	            points[1].x+=xOffset;
	            points[2].x+=xOffset;
	            break;
	        case 6: //top middle
	            points[2].y+=yOffset;
	            points[3].y+=yOffset;
	            break;
	        case 7: //left middle
	            points[0].x+=xOffset;
	            points[3].x+=xOffset;
	            break;
	        case 8: //body
	            for (int i = 0; i < 4; ++i) {
	                points[i].x += xOffset;
	                points[i].y += yOffset;
	            }
	            break;
	    }
	    for (int i = 0; i < 4; ++i) {
	        points[i]=this.clip2workingArea(points[i]);
	    }
	}
	
	private CGPoint getHotspotCenterLocation(int idx) {
	    if (idx < 4) {
	        return points[idx];
	    }
	    CGPoint pt=CGPoint.ccp(0, 0);
	    switch (idx) {
	        case 4://bottom
	            pt=CGPoint.ccp((points[0].x+points[1].x)/2, points[0].y);
	            break;
	        case 5://right
	            pt=CGPoint.ccp(points[1].x, (points[1].y+points[2].y)/2);
	            break;
	        case 6://top
	            pt=CGPoint.ccp((points[0].x+points[1].x)/2, points[2].y);
	            break;
	        case 7://left
	            pt=CGPoint.ccp(points[0].x, (points[1].y+points[2].y)/2);
	            break;
	    }
	    return pt;
	}
	
	private CGPoint clip2workingArea(CGPoint pt_) {
	    CGPoint pt=pt_;
	    if (pt.x < workingArea.origin.x)
	        pt.x=workingArea.origin.x;
	    else if (pt.x > workingArea.origin.x + workingArea.size.width)
	        pt.x=workingArea.origin.x + workingArea.size.width;
	    if (pt.y < workingArea.origin.y)
	        pt.y=workingArea.origin.y;
	    else if (pt.y > workingArea.origin.y + workingArea.size.height)
	        pt.y=workingArea.origin.y + workingArea.size.height;
	    return pt;
	}
}
