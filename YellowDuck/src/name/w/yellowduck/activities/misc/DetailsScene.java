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

import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.types.CGPoint;

//
//  YellowDuck
//
//  Created by ASTI on 12/16/12.
//
//
public class DetailsScene extends ShapeGameBase {
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new DetailsScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=2;
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);

	    switch (mLevel) {
	        case 1:
	            mMaxSublevel=8;
	            break;
	        case 2:
	            mMaxSublevel=13;
	            break;
	    }
	    String config=String.format("image/activities/discovery/miscelaneous/details/board%d_%d.xml.in", mLevel, mSublevel);
	    java.util.ArrayList<YDShape> shapes=createShapesFromConfiguration(config);
	    
	    //find the background specified by shape name
	    YDShape bgShape=null;
	    for (YDShape shape : shapes) {
	        if (shape.getType() == YDShape.kShapeBackground) {
	            if (shape.getName()!=null && "background".equalsIgnoreCase(shape.getName())) {
	                bgShape=shape;
	                break;
	            }
	            else {
	                bgShape=shape;
	            }
	        }
	    }
	    String bg="image/activities/discovery/miscelaneous/"+bgShape.getResource();
	    super.preGameInit(bg);
	    //fix the shape position
	    for (YDShape shape : shapes) {
	        if (shape == bgShape)
	            continue;
	        float xOffset=(shape.getPosition().x-bgShape.getPosition().x)*backgroundSprite.getScale();
	        float yOffset=(shape.getPosition().y-bgShape.getPosition().y)*backgroundSprite.getScale();
	        shape.setPosition(CGPoint.ccp(backgroundSprite.getPosition().x+xOffset, backgroundSprite.getPosition().y-yOffset));
	        
	        shape.setResource("image/activities/discovery/miscelaneous/"+shape.getResource());
	        super.addShape(shape);
	    }
	    shapes.clear();
	    
	    super.postGameInit(false, false);
	}
}
