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

import name.w.yellowduck.Category;
import name.w.yellowduck.PolygonSprite;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor4F;

import android.util.FloatMath;
import android.view.MotionEvent;

public class HexagonScene extends name.w.yellowduck.YDActLayerBase {
    private float radius;
    private int seed, xSeed, ySeed;
    private float maxDistance;

	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new HexagonScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
		super.onEnter();
	    mMaxLevel=4;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
	

	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
	    
	    float cyWorkingArea=szWin.height-topOverhead() - bottomOverhead();
	    int numbers[]={15,18,22,26};
	    int nbx=numbers[mLevel-1];
	    radius=szWin.width / nbx / 2;
	    float hexRadius=radius/FloatMath.cos(30.0f/180*3.14159265f);
	    int nby=(int)(cyWorkingArea/(radius -(hexRadius-radius))/2);
	    maxDistance=FloatMath.sqrt(nbx * nbx + nby*nby);
	    int total=nbx*nby-nby/2;
	    seed=super.nextInt(total);
	    total=0;
	    for (int y=0; y < nby; ++y) {
	        for (int x=0; x < (((y&1) != 0)?nbx:nbx-1); ++x) {
	            float xCenter=x * radius*2 + radius;
	            float yCenter=y * radius*2 + radius + bottomOverhead();
	            if ((y&1)==0) {
	                xCenter += radius;
	            }
	            yCenter-=(hexRadius-radius)*2*y;
	            PolygonSprite sprite=new PolygonSprite(6);
	            sprite.setSolid(true);
	            sprite.setClr(new ccColor4F(0, 1.0f*0x99/0xff, 1, 1.0f*0xcc/0xff));
	            sprite.setClrBorder(new ccColor4F(0,0,0,1));
	            sprite.setLineWidth(2);
	            sprite.setTag(y*100+x);
	            for (int i = 0; i < 6; ++i) {
	                float _x=FloatMath.sin(i*60.0f/180*3.14159265f)*hexRadius+xCenter;
	                float _y=FloatMath.cos(i*60.0f/180*3.14159265f)*hexRadius+yCenter;
	                sprite.setVertix(i, CGPoint.ccp(_x,_y));
	            }
	            super.addChild(sprite,1);
	            floatingSprites.add(sprite);
	
	            if (seed == total) {
	                xSeed=x; ySeed=y;
	            }
	            ++total;
	        }
	    }
	}
	
	public boolean ccTouchesEnded(MotionEvent event) {
		CGPoint pt = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
	        
        PolygonSprite clicked=null;
        for (CCNode node : floatingSprites) {
        	PolygonSprite sprite=(PolygonSprite)node;
            if (sprite.inside(pt)) {
                clicked=sprite;
                break;
            }
        }
        if (clicked!=null) {
            playSound("audio/sounds/drip.wav");
            int xPos=clicked.getTag() % 100;
            int yPos=clicked.getTag() / 100;
            
            if (xPos == xSeed && yPos == ySeed) {
                CCSprite sprite=spriteFromExpansionFile("image/activities/puzzle/hexagon/strawberry.png");
                sprite.setScale(radius*2/sprite.getContentSize().width * 0.85f);
                CGPoint p1=clicked.getVertix(0);
                CGPoint p2=clicked.getVertix(3);
                sprite.setPosition((p1.x+p2.x)/2, (p1.y+p2.y)/2);
                super.addChild(sprite, 3);
                floatingSprites.add(sprite);
                
                super.flashAnswerWithResult(true, true, null, null, 2);
            }
            else {
                int xDiff=xSeed-xPos;
                int yDiff=ySeed-yPos;
                float distance=FloatMath.sqrt(xDiff * xDiff + yDiff * yDiff);
                float relative=distance/maxDistance;
                
                float r=0,g=0,b=0;
                if (relative  < 0.25f) {
                    r = 1;
                    g = 4*relative;
                }
                else if (relative <0.5) {
                    g = 1;
                    r = 1-4*(relative-0.25f);
                }
                else if (relative <0.75f) {
                    g = 1-4*(relative-0.5f);
                    b = 4*(relative-0.5f);
                }
                else if (relative < 1) {
                    b = 1;
                    r = 4*(relative-0.75f);
                }
                
                clicked.setClr(new ccColor4F(r, g, b, 1.0f));
            }
        }
        return true;
	}
}
