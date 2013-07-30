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

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public class MatchScene extends ShapeGameBase {
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new MatchScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=7;
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    
	    super.preGameInit(null);
	    java.util.ArrayList<String> items=new java.util.ArrayList<String>();
	    switch (mLevel) {
	        case 1:
	            items.add("light");
	            items.add("lamp");
	            items.add("postcard");
	            items.add("postpoint");
	            items.add("fishingboat");
	            items.add("sailingboat");
	            break;
	        case 2:
	            items.add("bottle");
	            items.add("glass");
	            items.add("egg");
	            items.add("eggpot");
	            items.add("flower");
	            items.add("flowerpot");
	            break;
	        case 3:
	            items.add("fusee");
	            items.add("star");
	            items.add("sofa");
	            items.add("house");
	            items.add("lighthouse");
	            items.add("sailingboat");
	            break;
	        case 4:
	            items.add("apple");
	            items.add("tree");
	            items.add("bicycle");
	            items.add("car");
	            items.add("carrot");
	            items.add("rape");
	            break;
	        case 5:
	            items.add("pencil");
	            items.add("postcard");
	            items.add("tuxhelico");
	            items.add("tuxplane");
	            items.add("truck");
	            items.add("minivan");
	            break;
	        case 6:
	            items.add("castle");
	            items.add("crown");
	            items.add("sailingboat");
	            items.add("windflag5");
	            items.add("raquette");
	            items.add("football");
	            break;
	        case 7:
	            items.add("sun");
	            items.add("lamp");
	            items.add("sound");
	            items.add("bell");
	            items.add("umbrella");
	            items.add("lifebuoy");
	            break;
	    }
	
	    float rightItemWidth=(szWin.width - separator) / (items.size() / 2);
	    float rightItemHeight=(szWin.height - super.topOverhead() - super.bottomOverhead()) / 4;
	    float yPos=szWin.height - rightItemHeight;
	    for (int i = 0; i < items.size(); i+= 2) {
	        float xPos=separator + rightItemWidth * (i/2);
	        YDShape shape=new YDShape(String.format("image/activities/discovery/miscelaneous/babymatch/%s.png", items.get(i)), YDShape.kShapeLabelImage);
	        shape.setPosition(CGPoint.ccp(xPos + rightItemWidth/2, yPos - rightItemHeight/2));
	        shape.setFit2(CGSize.make(rightItemWidth*0.9f, rightItemHeight*0.9f));
	        super.addShape(shape);
	
	        YDShape stock=new YDShape(String.format("image/activities/discovery/miscelaneous/babymatch/%s.png", items.get(i+1)), YDShape.kShapeStock);
	        stock.setPosition(CGPoint.ccp(xPos + rightItemWidth/2, yPos - rightItemHeight*2));
	        stock.setFit2(CGSize.make(rightItemWidth*0.9f, rightItemHeight*0.9f));
	        super.addShape(stock);
	    }
	    super.postGameInit(true,  false);
	    
	    items.clear();
	}
}
