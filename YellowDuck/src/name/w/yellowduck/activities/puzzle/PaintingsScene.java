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

import name.w.yellowduck.YDConfiguration;
import name.w.yellowduck.activities.misc.YDShape;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.types.CGPoint;

public class PaintingsScene extends name.w.yellowduck.activities.misc.ShapeGameBase {
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new PaintingsScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=6;
	    this.initGame(true, null);
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    
	    switch (mLevel) {
	        case 1:
	            mMaxSublevel=13;
	            break;
	        case 2:
	            mMaxSublevel=5;
	            break;
	        case 3:
	            mMaxSublevel=2;
	            break;
	        default:
	            mMaxSublevel=1;
	            break;
	    }
	    String config=String.format("image/activities/puzzle/paintings/board%d_%d.xml.in", mLevel, mSublevel);
	    java.util.ArrayList<YDShape> shapes=createShapesFromConfiguration(config);
	    super.preGameInit(null);
	    //fix the shape position
	    float yTop=szWin.height-topOverhead();
	    super.setShapeScale(preferredContentScale(true));
	    for (YDShape shape : shapes) {
	        float xOffset=shape.getPosition().x*super.getShapeScale();
	        float yOffset=shape.getPosition().y*super.getShapeScale();
	        shape.setPosition(CGPoint.ccp(xOffset+separator, yTop-yOffset));
	
	        shape.setResource("image/activities/puzzle/" + shape.getResource());
	        super.addShape(shape);
	    }
	    shapes.clear();
	    
	    super.postGameInit(true, false);
	}
}
