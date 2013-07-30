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


package name.w.yellowduck.activities.puzzle;

import name.w.yellowduck.PolygonSprite;

import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor4F;

import android.util.FloatMath;

public class TangramShape extends PolygonSprite {
	public static final int kTangramShapeBigTriangle            =0;
	public static final int kTangramShapeMediumTriangle         =1;
	public static final int kTangramShapeSquare                 =2;
	public static final int kTangramShapeParallelogram          =3;
	public static final int kTangramShapeSmallTriangle          =4;

	private int shapeType;
	private boolean flipped;
	private CGPoint shapePosition;
	private float shapeScale;
	private float shapeRotation;
	
	public TangramShape(int type_, CGPoint pt_, float r_, float scale_, boolean flipped_) {
		super((type_ ==kTangramShapeSquare || type_ ==kTangramShapeParallelogram) ? 4:3);

        super.setClr(new ccColor4F(1.0f*0x80/0xff, 1.0f*0x80/0xff, 1.0f*0x80/0xff, 1.0f));
        super.setSolid(true);
        shapeType=type_;
        shapePosition=pt_;
        shapeRotation=r_;
        shapeScale=scale_;
        flipped=flipped_;
        
        initPolygon();
	}

	public void initPolygon() {
	    int reverse=-1;
	    if (shapeType == kTangramShapeSquare) {
	        float rot = shapeRotation*3.14f/180;
	        if (isFlipped()) {
	            rot += 3.14f;
	        }
	        float rotCos = FloatMath.cos(rot);
	        float rotSin = FloatMath.sin(rot);
	        float len=shapeScale * 0.5f;
	        float prex = shapePosition.x - (rotCos + rotSin) * len;
	        float prey = shapePosition.y + (rotSin - rotCos) * len * reverse;
	        setVertix(0, CGPoint.ccp(prex, prey));
	        setVertix(1, CGPoint.ccp(prex + rotCos * shapeScale, prey - rotSin * shapeScale * reverse));
	        setVertix(2, CGPoint.ccp(prex + rotCos * shapeScale + rotSin * shapeScale, prey + (rotCos * shapeScale - rotSin * shapeScale) * reverse));
	        setVertix(3, CGPoint.ccp(prex + rotSin * shapeScale, prey + rotCos * shapeScale * reverse));
	    }
	    else if (shapeType == kTangramShapeParallelogram) {
	        float rot = shapeRotation*3.14f/180;
	        float rotCos = FloatMath.cos(rot);
	        float rotSin = FloatMath.sin(rot);
	        float CL = 1.4142135624f * shapeScale;
	        float CC = CL / 2;
	        
	        float X = CC / 2;
	        float Y = (CC + CL) / 2;
	                        
	        float prex = shapePosition.x - (rotCos * X + rotSin * Y);
	        float prey = shapePosition.y + (rotSin * X - rotCos * Y) * reverse;
	        
	        float xt[]=new float[4], yt[]=new float[4];
	        if (isFlipped()) {
	            xt[0] = prex;
	            yt[0] = prey;
			    
	            xt[1] = prex + rotCos * CC + rotSin * CC;
	            yt[1] = prey - (rotSin * CC - rotCos * CC) * reverse;
			    
	            xt[2] = prex + rotCos * CC + rotSin * (CC + CL);
	            yt[2] = prey - (rotSin * CC - rotCos * (CC + CL)) * reverse;
			    
	            xt[3] = prex               + rotSin * CL;
	            yt[3] = prey               + rotCos * CL * reverse;
	        }
	        else {
	            xt[0] = prex               + rotSin * CC;
	            yt[0] = prey               + rotCos * CC * reverse;
			    
	            xt[1] = prex + rotCos * CC;
	            yt[1] = prey - rotSin * CC * reverse;
			    
	            xt[2] = prex + rotCos * CC + rotSin * CL;
	            yt[2] = prey - (rotSin * CC - rotCos * CL) * reverse;
			    
	            xt[3] = prex               + rotSin * (CC + CL);
	            yt[3] = prey               + rotCos * (CC + CL) * reverse;
	        }
	        for (int i = 0; i < 4; ++i) {
	            setVertix(i, CGPoint.ccp(xt[i], yt[i]));
	        }
	    }
	    else { //triangles
	        float rot = shapeRotation*3.14f/180;
	        if (isFlipped()) {
	            rot += 3.14f;
	        }
	        float rotCos = FloatMath.cos(rot);
	        float rotSin = FloatMath.sin(rot);
	        float triangleSize=0;
	        
	        if (shapeType == kTangramShapeSmallTriangle) {
	            triangleSize=1.0f;
	        }
	        else if (shapeType == kTangramShapeMediumTriangle) {
	            triangleSize=1.4142135624f;
	        }
	        else if (shapeType == kTangramShapeBigTriangle) {
	            triangleSize=2.0f;
	        }

	        float len=triangleSize * shapeScale;
	        float prex = shapePosition.x - (rotCos + rotSin) * len / 3.0f;
	        float prey = shapePosition.y + (rotSin - rotCos) * len / 3.0f * reverse;
	        setVertix(0, CGPoint.ccp(prex, prey));
	        setVertix(1, CGPoint.ccp(prex + rotCos * len, prey - rotSin * len * reverse));
	        setVertix(2, CGPoint.ccp(prex + rotSin * len, prey + rotCos * len * reverse));
	    }
	}
	@Override
	public void moveWithOffsetX(float xOffset, float yOffset) {
	    super.moveWithOffsetX(xOffset,yOffset);
	    CGPoint center=shapePosition;
	    center.x += xOffset;
	    center.y += yOffset;
	    shapePosition=center;
	}
	
	public int getShapeType() {
		return shapeType;
	}
	public void setShapeType(int shapeType) {
		this.shapeType = shapeType;
	}
	public boolean isFlipped() {
		return flipped;
	}
	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}
	public CGPoint getShapePosition() {
		return shapePosition;
	}
	public void setShapePosition(CGPoint shapePosition) {
		this.shapePosition = shapePosition;
	}
	public float getShapeScale() {
		return shapeScale;
	}
	public void setShapeScale(float shapeScale) {
		this.shapeScale = shapeScale;
	}
	public float getShapeRotation() {
		return shapeRotation;
	}
	public void setShapeRotation(float shapeRotation) {
		this.shapeRotation = shapeRotation;
	}
	

}
