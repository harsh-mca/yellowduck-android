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


package name.w.yellowduck.activities.misc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor4F;

import android.util.FloatMath;

public class ClockSprite extends CCNode {
	private final int kTagSecondHandle        =1;
	private final int kTagMinuteHandle        =2;
	private final int kTagHourHandle          =3;
	public static  final float kRulePosition  =0.8f;
	

    private CGPoint center;
    private float radius;
    
    private int hour, minute, second;
    
    private int graspOn, graspValue;
    private float graspAngle;
	
    private boolean secondHand;
    private boolean timeRule;
    private boolean detailedTimeRule;
    
	public ClockSprite(CGPoint pt, float rd) {
		super();
        center=pt;
        radius=rd;
        graspOn=0;//unknown
    }
    
    public boolean isSecondHand() {
		return secondHand;
	}

	public void setSecondHand(boolean secondHand) {
		this.secondHand = secondHand;
	}

	public boolean isTimeRule() {
		return timeRule;
	}

	public void setTimeRule(boolean timeRule) {
		this.timeRule = timeRule;
	}

	public boolean isDetailedTimeRule() {
		return detailedTimeRule;
	}

	public void setDetailedTimeRule(boolean detailedTimeRule) {
		this.detailedTimeRule = detailedTimeRule;
	}

	public int getHour() {
		return hour;
	}
	public int getMinute() {
		return minute;
	}
	public int getSecond() {
		return second;
	}

	public void setTimeHour(int h, int m, int s) {
	    hour=h;
	    minute=m;
	    second=s;
	}
	
	public void draw(GL10 gl) {
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		
	    //draw hour hand
	    float length=radius * 0.6f;
	    CGPoint pt[]=new CGPoint[4];
	    pt[0]=CGPoint.ccp(0, 0);
	    pt[1]=CGPoint.ccp(length*0.8f, -4);
	    pt[2]=CGPoint.ccp(length, 0);
	    pt[3]=CGPoint.ccp(length*0.8f, 4);
	    float _hour=((1.0f * second) / 60 + minute) / 60 + hour;
	    float angle=_hour * -30 + 90;
	
	    for (int i = 0; i < pt.length; ++i) {
	        pt[i]=this.rotatePoint(pt[i], angle);
	        pt[i].x += center.x;
	        pt[i].y += center.y;
	    }
	    this.drawSolidPoly(gl, pt, new ccColor4F(0, 0, 0.5f, 1.0f));
	    
	    //draw minute hand
	    length=radius * 0.8f;
	    pt[0]=CGPoint.ccp(0, 0);
	    pt[1]=CGPoint.ccp(length*0.8f, -2);
	    pt[2]=CGPoint.ccp(length, 0);
	    pt[3]=CGPoint.ccp(length*0.8f, 2);
	    float _minute=((1.0f * second) / 60 + minute);
	    angle=_minute * -6 + 90;
	    for (int i = 0; i < pt.length; ++i) {
	        pt[i]=this.rotatePoint(pt[i], angle);
	        pt[i].x += center.x;
	        pt[i].y += center.y;
	    }
	    this.drawSolidPoly(gl, pt, new ccColor4F(0, 0, 1.0f, 1.0f));
	    if (secondHand) {
	        //draw second hand
	        length=radius * 0.9f;
	        pt[0]=CGPoint.ccp(0, -1);
	        pt[1]=CGPoint.ccp(length, -1);
	        pt[2]=CGPoint.ccp(length, 1);
	        pt[3]=CGPoint.ccp(0, 1);
	        angle=second * -6 + 90;
	        for (int i = 0; i < pt.length; ++i) {
	            pt[i]=this.rotatePoint(pt[i], angle);
	            pt[i].x += center.x;
	            pt[i].y += center.y;
	        }
		    this.drawSolidPoly(gl, pt, new ccColor4F(1, 0, 0, 1.0f));
	    }
	
	    //short rule
	    for (int i = 0; i < 360; i+= 6) {
	        CGPoint pt1=CGPoint.ccp(radius * (kRulePosition+0.06f), 0);
	        CGPoint pt2=CGPoint.ccp(radius * (kRulePosition+0.1f), 0);
	        
	        pt1=this.rotatePoint(pt1, i);
	        pt2=this.rotatePoint(pt2, i);
	        
	        pt1.x += center.x; pt1.y += center.y;
	        pt2.x += center.x; pt2.y += center.y;
	        
	        this.drawLine(gl, pt1, pt2, new ccColor4F(0,0,0,1));
	    }
	    //long rule
	    for (int i = 0; i < 360; i+= 30) {
	        CGPoint pt1=CGPoint.ccp(radius * kRulePosition, 0);
	        CGPoint pt2=CGPoint.ccp(radius * (kRulePosition+0.1f), 0);
	        
	        pt1=this.rotatePoint(pt1, i);
	        pt2=this.rotatePoint(pt2, i);
	        
	        pt1.x += center.x; pt1.y += center.y;
	        pt2.x += center.x; pt2.y += center.y;
	
	        this.drawLine(gl, pt1, pt2, new ccColor4F(0,0,1,1));
	    }
	    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);	    
	}
	private void drawSolidPoly(GL10 gl, CGPoint pts[], ccColor4F clr) {
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
	    
        gl.glColor4f(clr.r, clr.g, clr.b, clr.a);
    	gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, numOfVertices);
	}
	private void drawLine(GL10 gl, CGPoint p1, CGPoint p2, ccColor4F clr) {
	        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * 2);
	        vbb.order(ByteOrder.nativeOrder());
	        FloatBuffer vertices = vbb.asFloatBuffer();

	        vertices.put(p1.x);
	        vertices.put(p1.y);
	        vertices.put(p2.x);
	        vertices.put(p2.y);
	        vertices.position(0);

	        gl.glColor4f(clr.r, clr.g, clr.b, clr.a);
	        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertices);
	        gl.glDrawArrays(GL10.GL_LINES, 0, 2);
	}
	
	private CGPoint rotatePoint(CGPoint pt, float _angle) {
	    float PI = 3.14159265f;
	    float rad_angle = _angle*PI/180.0f;
	    CGPoint dst=CGPoint.ccp(pt.x*FloatMath.cos(rad_angle) - pt.y*FloatMath.sin(rad_angle) ,
	    						pt.y*FloatMath.cos(rad_angle) + pt.x*FloatMath.sin(rad_angle));
	    return dst;
	}
	public void graspHand(CGPoint pt) {
	    float angle =this.adjustAngle(180 * (float)(Math.atan2(pt.y-center.y, pt.x-center.x))/3.14f);
	    float secondAngle=this.adjustAngle(secondHand ? second * -6 + 90 : 0);
	    
	    float _minute=((1.0f * second) / 60 + minute);
	    float minuteAngle=this.adjustAngle(_minute * -6 + 90);
	    
	    if (!secondHand || (Math.abs(minuteAngle-angle) < Math.abs(secondAngle-angle))) {
	        graspOn=kTagMinuteHandle;
	        graspAngle=minuteAngle;
	        graspValue=minute;
	    }
	    else {
	        graspOn=kTagSecondHandle;
	        graspAngle=secondAngle;
	        graspValue=second;
	    }
	    
	    float _hour=((1.0f * second) / 60 + minute) / 60 + hour;
	    float hourAngle=this.adjustAngle(_hour * -30 + 90);
	    
	    if (Math.abs(hourAngle-angle) < Math.abs(graspAngle-angle)) {
	        graspOn=kTagHourHandle;
	        graspAngle=hourAngle;
	        graspValue=hour;
	    }
	//    NSLog(@"Grasp angle:%f, hour %f, min %f, sec %f, ON=%d", angle, hourAngle, minuteAngle, secondAngle, graspOn);
	}
	public void moveHand(CGPoint pt) {
	    int angle =(int)(180 * Math.atan2(pt.y-center.y, pt.x-center.x)/3.14f);
	    if (angle < 0)
	    	angle += 360;
	    int diff=0;
	    switch (graspOn) {
	        case kTagSecondHandle:
	            second =(angle - 90) / (-6);
	            if (second < 0)
	                second += 60;
	            second %= 60;
	            diff=second - graspValue;
	            if (diff <= -30) {
	                //forward
	                ++minute;
	            }
	            else if (diff >= 30) {
	                --minute;
	            }
	            graspValue=second;
	            break;
	        case kTagMinuteHandle:
	            minute =(angle - 90) / (-6);
	            if (minute < 0)
	                minute += 60;
	            minute %= 60;
	            diff=minute - graspValue;
	            if (diff <= -30) {
	                //forward
	                ++hour;
	            }
	            else if (diff >= 30) {
	                --hour;
	            }
	            graspValue=minute;
	            break;
	        case kTagHourHandle:
	            hour = (angle - 90) / (- 30);
	            if (hour < 0)
	                hour += 12;
	            hour %= 12;
	            break;
	    }
	    if (second >= 60) {
	        second -= 60;
	        ++minute;
	    }
	    else if (second < 0) {
	        second += 60;
	        --minute;
	    }
	    if (minute >= 60) {
	        minute -= 60;
	        ++hour;
	    }
	    else if (minute < 0) {
	        minute += 60;
	        --hour;
	    }
	    if (hour >= 12)
	        hour -= 12;
	    else if (hour < 0) {
	        hour += 12;
	    }
	}
	
	private float adjustAngle(float input) {
	    while (input < 0)
	        input += 360;
	    while (input >= 360)
	        input -= 360;
	    return input;
	}
}
