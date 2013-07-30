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


package name.w.yellowduck.activities.memory;

import name.w.yellowduck.Category;
import name.w.yellowduck.Schema;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import android.view.MotionEvent;


public class RailroadScene  extends name.w.yellowduck.YDActLayerBase {
	private final int kTagLoco                =0;
	private final int kTagWagon               =100;
	private final int kTotalLoco          =9;
	private final int kTotalWagon         =13;

	private final String kFlagStock              ="Stock";
	private final String kFlagTarget             ="Target";
	private final String kFlagPicked             ="Picked";

	private final int kSceneReplay            =0;
	private final int kSceneUserPlaying       =1;
	
    private int selectedLoco, selectedWagon[]=new int[3];
    
    private int scene;
    private float firstTrack; //the first track v position
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new RailroadScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public RailroadScene() {
		super();
        firstTrack=0.8f;
	}

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=3;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionRepeatButton|kOptionOk);

	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}

	//Override
	public void repeat(Object _sender) {
	    this.makeTrainRun();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    
	    selectedLoco=super.nextInt(kTotalLoco);
	    for (int i = 0; i < mLevel; ++i) {
	        selectedWagon[i]=super.nextInt(kTotalWagon); //maybe duplicated
	    }
	
	    //wagons
	    for (int i = 0; i < mLevel; ++i) {
	        String resource=this.wagonResource(selectedWagon[i]);
	        CCSprite sprite=spriteFromExpansionFile(resource);
	        sprite.setTag(selectedWagon[i]+kTagWagon);
	        sprite.setUserData(kFlagTarget);
	        super.addChild(sprite,1);
	        floatingSprites.add(sprite);
	    }
	    //the loco
	    String resource=this.locoResource(selectedLoco);
	    CCSprite _loco=spriteFromExpansionFile(resource);
	    _loco.setTag(selectedLoco+kTagLoco);
	    _loco.setUserData(kFlagTarget);
	    super.addChild(_loco,1);
	    floatingSprites.add(_loco);
	    
	    //all locos & wagons for selection
	    float xPos=0, yPos=szWin.height*0.65f;
	    float distanceFactor=0.152f;
	    
	    java.util.ArrayList<CCNode> rowSprites=new java.util.ArrayList<CCNode>();
	    
	    for (int i = 0; i < kTotalLoco; ++i) {
	        CCSprite loco=spriteFromExpansionFile(this.locoResource(i));
	        loco.setTag(kTagLoco+i);
	        loco.setUserData(kFlagStock);
	        loco.setVisible(false);
	        loco.setPosition(xPos+loco.getContentSize().width/2, yPos);
	
	        super.addChild(loco,1);
	        floatingSprites.add(loco);
	        
	        rowSprites.add(loco);
	        
	        xPos += loco.getContentSize().width * loco.getScale();
	        if (i == 3) {
	            xPos = 0;
	            yPos -= szWin.height * distanceFactor;
	
	            this.distribute(rowSprites);
	            rowSprites.clear();
	        }
	    }
	    this.distribute(rowSprites);
	    rowSprites.clear();
	    
	    xPos = 4;
	    yPos -= szWin.height * distanceFactor;
	    for (int i = 0; i < kTotalWagon; ++i) {
	        CCSprite wagon=spriteFromExpansionFile(this.wagonResource(i));
	        wagon.setTag(kTagWagon+i);
	        wagon.setUserData(kFlagStock);
	        wagon.setVisible(false);
	        wagon.setPosition(xPos+_loco.getContentSize().width/2, yPos);
	        
	        super.addChild(wagon,1);
	        floatingSprites.add(wagon);
	        
	        rowSprites.add(wagon);
	        
	        xPos += _loco.getContentSize().width * _loco.getScale();
	        if ((i % 4) == 3 && i <= 8) {
	            xPos = 0;
	            yPos -= szWin.height * distanceFactor;
	            
	            this.distribute(rowSprites);
	            rowSprites.clear();
	        }
	    }
	    this.distribute(rowSprites);
	    rowSprites.clear();
	    
	    for (CCNode one : floatingSprites) {
	        if (this.isTarget(one)) {
	            CCNode match=null;
	            for (CCNode another : floatingSprites) {
	                if (this.isStock(another) && another.getTag()==one.getTag()) {
	                    match=another;
	                    break;
	                }
	            }
	            if (match!=null)
	                one.setScale(match.getScale());
	        }
	    }
	    
	    this.makeTrainRun();
	}
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
        CCNode clicked=null;
        for (CCNode item : floatingSprites) {
            if (item.getVisible() && (this.isPicked(item) || this.isStock(item))) {
                if (super.isNodeHit(item, p1)) {
                    clicked=item;
                    break;
                }
            }
        }
        if (clicked!=null) {
            CCSprite sparkle=spriteFromExpansionFile("image/misc/buttonmask.png");
            sparkle.setPosition(p1);
            sparkle.setScale(clicked.getContentSize().height*clicked.getScaleY()/sparkle.getContentSize().height*0.8f);
            super.addChild(sparkle,100);
            CCScaleTo scaleDownAction=CCScaleTo.action(0.4f, 0.2f);
            CCCallFuncN actionDone = CCCallFuncN.action(this, "removeMe");
            sparkle.runAction(CCSequence.actions(scaleDownAction, actionDone));
            
            if (this.isPicked(clicked)) {
                clicked.removeFromParentAndCleanup(true);
                floatingSprites.remove(clicked);
                
                this.arrangePicked();
            }
            else {
                CGPoint pt=this.arrangePicked();
                int tag=clicked.getTag();
                //how to make a copy of this sprite?
                String resource=(tag >= kTagWagon) ? this.wagonResource(tag-kTagWagon) : this.locoResource(tag-kTagLoco);
                CCSprite picked=spriteFromExpansionFile(resource);
                picked.setTag(tag);
                picked.setUserData(kFlagPicked);
                picked.setScale(clicked.getScale());
                picked.setPosition(clicked.getPosition());
                super.addChild(picked,1);
                floatingSprites.add(picked);
                
                CCMoveTo moveAction=CCMoveTo.action(0.2f, CGPoint.ccp(pt.x + picked.getContentSize().width*picked.getScale()/2, pt.y + picked.getContentSize().height*picked.getScale()/2));
                picked.runAction(moveAction);
            }
        }
        else if (scene == kSceneReplay) {
            //stop re-playing if user taps
            this.trainDeparted(null);
        }
        return true;
	}
	
	private void makeTrainRun() {
	    float xPos=0, yPos=szWin.height*firstTrack;
	    super.playSound("audio/sounds/train.wav");
	    for (CCNode sprite : floatingSprites) {
	        if (this.isTarget(sprite)) {
	            sprite.stopAllActions();
	            sprite.setPosition(xPos+sprite.getContentSize().width*sprite.getScale()/2, yPos+sprite.getContentSize().height*sprite.getScale()/2);
	            sprite.setVisible(true);
	
	            CCDelayTime idleAction = CCDelayTime.action(1);
	            CCMoveTo moveAction = CCMoveTo.action(7, CGPoint.ccp(sprite.getPosition().x+szWin.width+10, sprite.getPosition().y));
	            CCCallFuncN moveDone = CCCallFuncN.action(this,"trainDeparted");
	            sprite.runAction(CCSequence.actions(idleAction, moveAction, moveDone));
	
	            xPos += sprite.getContentSize().width*sprite.getScale();
	        }
	        else {
	            sprite.setVisible(false);
	        }
	    }
	    scene=kSceneReplay;
	}
	public void trainDeparted(Object sender) {
	    for (CCNode sprite : floatingSprites) {
	        if (this.isTarget(sprite)) {
	            sprite.stopAllActions();
	            sprite.setVisible(false);
	        }
	        else if (this.isPicked(sprite) || this.isStock(sprite)) {
	        	sprite.setVisible(true);
	        }
	    }
	    scene=kSceneUserPlaying;
	}
	
	//evenly distribute items on the track
	private void distribute(java.util.ArrayList<CCNode>items) {
	    if (items.size() <= 1)
	        return;
	
	    CCNode last=items.get(items.size()-1);
	    float ended=last.getPosition().x + last.getContentSize().width/2*last.getScale();
	    float leftover=szWin.width - ended;
	    float distribution=leftover/items.size();
	    
	    int idx=0;
	    for (CCNode one : items) {
	        one.setScale((one.getContentSize().width + distribution)/one.getContentSize().width*0.8f);
	        one.setPosition(one.getPosition().x + distribution*idx+distribution/2, one.getPosition().y+one.getContentSize().height*one.getScale()/2);
	        ++idx;
	    }    
	}
	//Arrange picked items on the track
	private CGPoint arrangePicked() {
	    float xPos=4, yPos=szWin.height*firstTrack;
	    for (CCNode one : floatingSprites) {
	        if (this.isPicked(one)) {
	            CGPoint ptDst=CGPoint.ccp(xPos+one.getContentSize().width*one.getScale()/2, yPos+one.getContentSize().height*one.getScale()/2);
	            CCMoveTo moveAction=CCMoveTo.action(Schema.ANIMATION_SPEED,ptDst);
	            one.runAction(moveAction);
	
	            xPos += one.getContentSize().width*one.getScale();
	        }
	    }
	    return CGPoint.ccp(xPos, yPos);
	}
	
	public void ok(Object _sender) {
		java.util.ArrayList<CCNode> target=new java.util.ArrayList<CCNode>(); 
	    for (CCNode sprite : floatingSprites) {
	        if (this.isTarget(sprite)) {
	            target.add(sprite);
	        }
	    }
		java.util.ArrayList<CCNode> picked=new java.util.ArrayList<CCNode>(); 
	    for (CCNode sprite : floatingSprites) {
	        if (this.isPicked(sprite)) {
	        	picked.add(sprite);
	        }
	    }
	    boolean matched=true;
	    if (target.size()  == picked.size()) {
	        for (int i = 0; i < target.size(); ++i) {
	            CCNode _target=target.get(i);
	            CCNode _picked=picked.get(i);
	            
	            if (_target.getTag() != _picked.getTag()) {
	                matched=false;
	            }
	            
	        }
	    }
	    else {
	        matched=false;
	    }
	    
	    if (matched) {
	        //Make the train leaving
	        for (CCNode sprite : picked) {
	        	CCMoveTo moveAction = CCMoveTo.action(1.5f,  CGPoint.ccp(sprite.getPosition().x+szWin.width+10, sprite.getPosition().y));
	            sprite.runAction(moveAction);
	        }
	    }
	    super.flashAnswerWithResult(matched,matched,null, null, 2.0f);
	}

	private String locoResource(int index) {
	    return String.format("image/activities/discovery/memory_group/railroad/loco%d.png", index+1);
	}
	private String wagonResource(int index) {
	    return String.format("image/activities/discovery/memory_group/railroad/wagon%d.png", index+1);
	}
	private boolean isPicked(CCNode node) {
	    return kFlagPicked.equals(node.getUserData());
	}
	
	private boolean isTarget(CCNode node) {
	    return kFlagTarget.equals(node.getUserData());
	}
	private boolean isStock(CCNode node) {
	    return kFlagStock.equals(node.getUserData());
	}

	public void removeMe(Object _sender) {
		CCNode sender=(CCNode)_sender;
		sender.removeFromParentAndCleanup(true);
	}
}
