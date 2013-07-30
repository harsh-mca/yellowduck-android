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

import name.w.yellowduck.Category;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;

//
//  YellowDuck
//
//  Created by ASTI on 12/16/12.
//
//
public class AlgorithmScene extends name.w.yellowduck.YDActLayerBase {
	private final int kTotalItems         =8;

	private java.util.ArrayList<String> items;
    private CCLabel questionmark;
    
    private int selection[]=new int[kTotalItems];
    private boolean missionAccomplished;
    private int sequence;
    private float ySlot, xSlot[]=new float[3];
    private int next2Fill;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new AlgorithmScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	public AlgorithmScene() {
		super();
		items=new java.util.ArrayList<String>();
        items.add("image/activities/discovery/miscelaneous/algorithm/apple.png");
        items.add("image/activities/discovery/miscelaneous/algorithm/strawberry.png");
        items.add("image/activities/discovery/miscelaneous/algorithm/peer.png");
        items.add("image/activities/discovery/miscelaneous/algorithm/football.png");
        items.add("image/activities/discovery/miscelaneous/algorithm/cerise.png");
        items.add("image/activities/discovery/miscelaneous/algorithm/egg.png");
        items.add("image/activities/discovery/miscelaneous/algorithm/glass.png");
        items.add("image/activities/discovery/miscelaneous/algorithm/eggpot.png");
	}

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=4;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
	
	    for (int i = 0; i < kTotalItems; ++i) {
	        selection[i]=i;
	    }
	    questionmark = CCLabel.makeLabel("?", super.sysFontName(), super.mediumFontSize());
	    questionmark.setColor(ccColor3B.ccRED);
	    super.addChild(questionmark,0);
	    
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    
	    super.randomIt(selection,kTotalItems);
	    
	    float xMargin=szWin.width/10;
	    float yPos=szWin.height * 0.802f;
	    float xRoom=(szWin.width - xMargin * 2) / 8;
	    int index=0;
	    //revealed items
	    for (sequence = 0; sequence < 13; ++sequence) {
	        int sel=this.algo(sequence);
	        CCSprite sprite=spriteFromExpansionFile(items.get(selection[sel]));
	        sprite.setScale(super.preferredContentScale(true));
	        sprite.setPosition(xMargin + xRoom * index + xRoom / 2, yPos+sprite.getContentSize().height*sprite.getScale()/2);
	        sprite.setTag(-1);
	        super.addChild(sprite,1);
	        floatingSprites.add(sprite);
	        
	        if (++index >= 8) {
	            index = 0;
	            yPos -= szWin.height * 0.2f;
	        }
	    }
	    ySlot=yPos;
	    for (int i = 0; i < 3; ++i) {
	        xSlot[i]=xMargin + xRoom * index + xRoom / 2;
	        ++index;
	    }
	    next2Fill=0;
	    questionmark.setPosition(xSlot[next2Fill], ySlot+questionmark.getContentSize().height/2);
	    
	    //0.14f
	    yPos = szWin.height * 0.14f;
	    for (int i = 0; i < items.size(); ++i) {
	        CCSprite sprite=spriteFromExpansionFile(items.get(i));
	        String dn=super.buttonize(items.get(i));
	        CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(dn);
	        CCSprite spriteDn=CCSprite.sprite(texture);
	        CCMenuItemSprite cover=CCMenuItemImage.item(sprite,spriteDn,this,"itemSelected");
	        cover.setTag(i);
	        cover.setPosition(xMargin + xRoom * i + xRoom / 2, yPos+sprite.getContentSize().height/2);
	        cover.setScale(super.preferredContentScale(true));
	        CCMenu menu=CCMenu.menu(cover);
	        menu.setPosition(0,0);
	        super.addChild(menu,1);
	        floatingSprites.add(menu);
	    }
	    
	    missionAccomplished=false;
	}
	
	public void itemSelected(Object _sender) {
	    if (missionAccomplished)
	        return;
	    CCNode clicked=(CCNode)_sender;
	    if (clicked.getTag()==selection[this.algo(sequence)]) {
	        CCSprite sprite=spriteFromExpansionFile(items.get(clicked.getTag()));
	        sprite.setScale(super.preferredContentScale(true));
	        sprite.setPosition(clicked.getPosition());
	        sprite.setTag(clicked.getTag());
	        super.addChild(sprite,1);
	        floatingSprites.add(sprite);
	        
	        CGPoint dst=CGPoint.ccp(xSlot[next2Fill], ySlot+sprite.getContentSize().height*sprite.getScale()/2);
	        CCMoveTo moveAction=CCMoveTo.action(0.2f, dst);
	        sprite.runAction(moveAction);
	        super.playSound("audio/sounds/apert2.wav");
	
	        ++next2Fill;
	        ++sequence;
	        if (next2Fill >= 3) {
	            //finished
	            missionAccomplished=true;
	            super.flashAnswerWithResult(true, true, null, null, 2);
	        }
	        else {
	            questionmark.setPosition(xSlot[next2Fill], ySlot+questionmark.getContentSize().height/2);
	        }
	    }
	    else {
	        super.playSound("audio/sounds/brick.wav");
	    }
	}
	
	
	private int algo(int index) {
	    int ret=0;
	    switch (mLevel) {
	        case 1:
	            ret=index % 4;
	            break;
	        case 2:
	            ret=index % 3;
	            break;
	        case 3:
	            ret=index % 5;
	            break;
	        case 4:
	            if ((index%6) > 2) {
	                ret= 2 - index %3;
	            }
	            else
	                ret=index %3;
	            break;
	    }
	    return ret;
	}
}
