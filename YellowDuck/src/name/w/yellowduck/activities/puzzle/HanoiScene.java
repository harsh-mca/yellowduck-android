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
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;

import android.view.MotionEvent;

public class HanoiScene extends name.w.yellowduck.YDActLayerBase {

	private final int kTagTower           =10;
	
    private float tableWidth, xTable, yTable;
    
    private java.util.ArrayList<CCNode> townerItems[]=new java.util.ArrayList[3];
    
    private int discs;
    private int srcTower;
    private CCNode picked;
    private CGPoint ptOriginalPos;
	
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new HanoiScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
		
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=2;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(), kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
	
	    townerItems[0]=new java.util.ArrayList<CCNode>();
	    townerItems[1]=new java.util.ArrayList<CCNode>();
	    townerItems[2]=new java.util.ArrayList<CCNode>();
	    //table height 260, left 168, bg 1024x768
	    tableWidth=1.0f*(1024-168*2)/1024*szWin.width;
	    xTable=168.0f/1024*szWin.width;
	    yTable=260.f/768*szWin.height;
	    for (int i = 0; i < 3; ++i){
	        CCSprite baseSprite=spriteFromExpansionFile("image/activities/puzzle/hanoi_real/tower.png");
	        baseSprite.setScale(1.0f*(768-260)/768*szWin.height/baseSprite.getContentSize().height*0.68f);
	        baseSprite.setPosition(CGPoint.ccp(xTable+tableWidth/3*i+tableWidth/6, yTable+baseSprite.getContentSize().height*baseSprite.getScale()/2));
	        baseSprite.setTag(kTagTower+1);
	        if (i==2) {
	            //highlight
	            baseSprite.setColor(ccColor3B.ccGREEN);
	        }
	        super.addChild(baseSprite, 1);
	    }
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
		
	    for (int i = 0; i < 3; ++i)
	         townerItems[i].clear();
	    
	    discs=(mLevel<=1)?3:4;
	    float yPos=yTable;
	    
	    float scale=0;
	    for (int i = discs; i > 0; --i) {
	        CCSprite ringSprite=spriteFromExpansionFile("image/activities/puzzle/hanoi_real/ring"+i+".png");
	        if (scale <= 0)
	            scale=tableWidth/3/ringSprite.getContentSize().width*0.68f;
	        ringSprite.setScale(scale);
	        ringSprite.setPosition(xTable + tableWidth/6, yPos+ringSprite.getContentSize().height*ringSprite.getScale()/2);
	        ringSprite.setTag(i);
	        super.addChild(ringSprite, 1);
	        floatingSprites.add(ringSprite);
	        townerItems[0].add(ringSprite);
	
	        yPos += ringSprite.getContentSize().height*ringSprite.getScale();
	    }
	}
	
	public boolean ccTouchesBegan(MotionEvent event) {
	    picked=null;
	
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
        //click above the table
        srcTower=this.isAboveTower(p1, true);
        if (srcTower >= 0 && townerItems[srcTower].size()>0) {
            picked=townerItems[srcTower].get(townerItems[srcTower].size()-1);
            if (picked!=null) {
                ptOriginalPos=picked.getPosition();
                picked.setPosition(p1);
            }
        }
        return true;
	}

	public boolean ccTouchesMoved(MotionEvent event) {
	    if (picked!=null) {
			CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
	        picked.setPosition(p1);
	    }
	    return true;
	}
	
	public boolean ccTouchesEnded(MotionEvent event) {
	    if (picked==null)
	        return true;
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
        boolean moved=false;
        int targetIdx=this.isAboveTower(p1, false);
        if (targetIdx >= 0 && targetIdx != srcTower) {
            CCNode topRing=townerItems[targetIdx].isEmpty()?null: townerItems[targetIdx].get(townerItems[targetIdx].size()-1);
            if (topRing == null || topRing.getTag() > picked.getTag()) {
                moved=true;
                float currentHeight=getTowerItemHigh(targetIdx);
                picked.setPosition(xTable + tableWidth/6 + tableWidth/3*targetIdx, yTable+currentHeight+picked.getContentSize().height*picked.getScale()/2);
                
                townerItems[targetIdx].add(picked);
                townerItems[srcTower].remove(townerItems[srcTower].size()-1);
            }
        }
        if (moved) {
            playSound("audio/sounds/drip.wav");
            if (targetIdx == 2 && townerItems[2].size() >= discs) {
                flashAnswerWithResult(true, true, null,null, 2);
            }
        }
        else {
            //put it back
        	CCMoveTo moveAction=CCMoveTo.action(0.5f, ptOriginalPos);
            picked.runAction(moveAction);
            
            playSound("audio/sounds/brick.wav");
        }
	    return true;
	}
	private float getTowerItemHigh(int  idx) {
		java.util.ArrayList<CCNode>items=townerItems[idx];
	    float height=0;
	    for (CCNode sprite : items) {
	        height += sprite.getContentSize().height *sprite.getScale();
	    }
	    return height;
	}
	private int isAboveTower(CGPoint p1, boolean limit2towerHigh) {
	    int index=-1;
	    if (p1.x >= xTable && p1.y >= yTable && p1.x <= szWin.width-xTable) {
	        index=(int)((p1.x-xTable) / (tableWidth/3));
	        float top=this.getTowerItemHigh(index);
	        if (limit2towerHigh && (p1.y > yTable + top)) {
	            index=-1;
	        }
	    }
	    return index;
	}
}
