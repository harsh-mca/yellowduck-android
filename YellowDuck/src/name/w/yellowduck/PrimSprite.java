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

import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor4F;

import android.util.FloatMath;

public abstract class PrimSprite extends CCNode {
	protected final float M_PI=3.1415f;
	
	public static final int kTypePrimLine           =666;
	public static final int kTypePrimPloygon        =667;
	public static final int kTypePrimEllipse        =668;

	protected final int kDistanceAsSelected     =15;
	
    protected float lineWidth;
    protected ccColor4F clr;
    protected ccColor4F clrBorder;
    private boolean solid;
    private boolean  selected;
    private int type;
    
	public PrimSprite() {
		super();
		this.clr=new ccColor4F(0, 0, 0, 1.0f);
        this.clrBorder=new ccColor4F(0, 0, 0, 0); //transparent
        this.solid=true;
        this.selected=false;
        this.lineWidth=1;
	}

	public float getLineWidth() {
		return lineWidth;
	}
	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}
	public ccColor4F getClr() {
		return clr;
	}
	public void setClr(ccColor4F clr) {
		this.clr = clr;
	}
	public ccColor4F getClrBorder() {
		return clrBorder;
	}
	public void setClrBorder(ccColor4F clrBorder) {
		this.clrBorder = clrBorder;
	}
	public boolean isSolid() {
		return solid;
	}
	public void setSolid(boolean solid) {
		this.solid = solid;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	protected boolean tooFarFromPoint(CGPoint pt, CGPoint p1, CGPoint p2)  {
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
	    xMin -= kDistanceAsSelected;
	    xMax += kDistanceAsSelected;
	    yMin -= kDistanceAsSelected;
	    yMax += kDistanceAsSelected;
	    
	    return  (pt.x < xMin || pt.x > xMax || pt.y < yMin || pt.y > yMax);
	}
	//distance of a point to a line defined by p1 and p2
	protected float distanceFromPoint(CGPoint p0, CGPoint p1, CGPoint p2) {
	    float distance=(p2.x-p1.x)*(p1.y-p0.y)-(p1.x-p0.x)*(p2.y-p1.y);
	    if (distance < 0)
	        distance=0-distance;
	    float diff1=p2.x-p1.x;
	    float diff2=p2.y-p1.y;
	    float diff=FloatMath.sqrt(diff1*diff1 + diff2*diff2);
	    distance=distance/diff;
	    
	    return distance;
	}

	//to be overrided by subclass
	public abstract void moveWithOffsetX(float xOffset, float yOffset);

	//to be overrided by subclass
	public abstract CGRect enclosedArea();
	public abstract boolean hit(CGPoint pt);

	public boolean isLine() {
	    return type == kTypePrimLine;
	}
	public boolean isPolygon() {
	    return type == kTypePrimPloygon;
	}
	public boolean isEllipse() {
	    return type == kTypePrimEllipse;
	}
}
