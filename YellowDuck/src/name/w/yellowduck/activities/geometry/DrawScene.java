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

import name.w.yellowduck.Category;
import name.w.yellowduck.PolygonSprite;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor4F;

public class DrawScene extends DrawSceneBase {
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new DrawScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public void onEnter() { 
		super.onEnter();
		mMaxLevel=1;
	
		Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
		//change a background music
		super.shufflePlayBackgroundMusic();
		super.setupTitle(activeCategory);
		super.setupNavBar(activeCategory);
		super.setupSideToolbar(activeCategory,kOptionIntr|kOptionHelp|kOptionRepeatButton);
	
	    
	    
	    int tools[]={kTooltipSelect, kTooltipLine, kTooltipRectangle, kTooltipRectangleFilled,
	                kTooltipCircle, kTooltipCircleFilled, kTooltipFill, kTooltipDel, kTooltipUp, kTooltipDown, kTooltipClr};
	    super.setupTooltips(tools, tools.length);
	
	    int margin=4;
	    CGRect rc=CGRect.make(tooltipsCanvasWidth+margin, bottomOverhead(), szWin.width-margin*2-tooltipsCanvasWidth, szWin.height-topOverhead()-bottomOverhead());
	    super.setupWorkingArea(rc);
	    
	    PolygonSprite sprite=new PolygonSprite(4);
	    sprite.setClr(new ccColor4F(1.0f, 1.0f, 1.0f, 1.0f));
	    sprite.setVertix(0, rc.origin);
	    sprite.setVertix(1, CGPoint.ccp(rc.origin.x+rc.size.width, rc.origin.y));
	    sprite.setVertix(2, CGPoint.ccp(rc.origin.x+rc.size.width, rc.origin.y+rc.size.height));
	    sprite.setVertix(3, CGPoint.ccp(rc.origin.x, rc.origin.y+rc.size.height));
	    super.addChild(sprite,1);

	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    this.__reset();
	}
	
	public void repeat(Object sender) {
	    this.__reset();
	}

	private void __reset() {
	    super.clearFloatingSprites();
	}
}
