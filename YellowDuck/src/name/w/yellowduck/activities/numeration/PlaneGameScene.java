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


package name.w.yellowduck.activities.numeration;

import name.w.yellowduck.Category;
import name.w.yellowduck.Schema;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor3B;

import android.view.MotionEvent;


public class PlaneGameScene extends name.w.yellowduck.YDActLayerBase {
	public final int kTagNumber          =100;
	public final int MAXSPEED            =7;
	
    private CCLabel promptLabel;
    private CCSprite chopper;
    private CGPoint ptChopperStarter;
    private float xLeft, xRight, yTop, yBottom;
    
    private boolean stop;
    private int cloudHeightPixels;
    private float fallSpeed, cloudSpeed, tmElapsed;
    private int  plane_target;
    
    private int planespeed_x, planespeed_y;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new PlaneGameScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
		super.onEnter();
	    mMaxLevel=2;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.shufflePlayBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionArrows);
	    //sublevel
	    promptLabel = CCLabel.makeLabel("10/10", super.sysFontName(), super.mediumFontSize());
	    promptLabel.setColor(ccColor3B.ccBLACK);
	    promptLabel.setPosition(szWin.width-promptLabel.getContentSize().width/2-4, bottomOverhead()+promptLabel.getContentSize().height/2);
	    super.addChild(promptLabel, 6);
	    
	    cloudHeightPixels=(int)szWin.height/8;
	    //the helicopter
	    chopper=spriteFromExpansionFile("image/activities/math/numeration/planegame/tuxhelico.png");
	    chopper.setScale(cloudHeightPixels*1.5f/chopper.getContentSize().height);
	    chopper.setPosition(chopper.getContentSize().width*chopper.getScale()/2, chopper.getContentSize().height*chopper.getScale()/2);
	    super.addChild(chopper, 10);
	    ptChopperStarter=chopper.getPosition();
	    float w=chopper.getContentSize().width*chopper.getScale();
	    float h=chopper.getContentSize().height*chopper.getScale();
	    xLeft=w/2; xRight=szWin.width - w/2;
	    yBottom=h/2; yTop=szWin.height-h/2;
	    
	    stop=true;
	    super.setIsTouchEnabled(true);
	    super.scheduleUpdate();
	    super.afterEnter();
	}
	
	public void onExit() {
	    stop=true;
	    super.onExit();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
	
	    stop=true;
	    promptLabel.setVisible(mLevel<=1);
	
	    fallSpeed=1.0f*(10000-mLevel*1000)/1000;
	    cloudSpeed=60.0f; //seconds, move out of screen
	    plane_target=1;
	    promptLabel.setString(String.format("%d/10",plane_target));
	
	    chopper.stopAllActions();
	    planespeed_x=planespeed_y=0;
	    chopper.setPosition(ptChopperStarter);
	    tmElapsed=fallSpeed;
	    stop=false;
	}
	public void update(float dt) {
	    if (stop)
	        return;
	    this.checkCollision();
	    tmElapsed += dt;
	    if (tmElapsed < fallSpeed)
	        return;
	    tmElapsed=0;
	
	    // Random cloud number 
	    int num=0;
	    if(super.nextInt(2)==0) {
	        num = plane_target;
	    }
	    else
	    {
	        int min = plane_target-1;
	        if (min < 1)
	            min=1;
	        num   = min + super.nextInt(plane_target - min + 3);
	    }
	    
	    int offset=cloudHeightPixels/2;
	    int lower=bottomOverhead()+offset;
	    int up=(int)(szWin.height - topOverhead() - offset);
	    String img=renderSVG2Img("image/activities/math/numeration/planegame/cloud.svg", 0, cloudHeightPixels);
	    CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(img);
	    CCSprite sprite=CCSprite.sprite(texture);
	    sprite.setTag(kTagNumber+num);
	    sprite.setPosition(szWin.width, randomBetween(lower, up));
	    super.addChild(sprite, 1);
	    floatingSprites.add(sprite);
	    
	    //the label
	    CCLabel label = CCLabel.makeLabel(""+num, super.sysFontName(), super.mediumFontSize());
	    label.setColor(ccColor3B.ccRED);
	    label.setPosition(sprite.getPosition());
	    super.addChild(label, 2);
	    floatingSprites.add(label);
	    //link them together
	    sprite.setUserData(label);
	
	    //moving to left
	    CCMoveTo moveAction = CCMoveTo.action(cloudSpeed, CGPoint.ccp(0-sprite.getContentSize().width*sprite.getScale(), sprite.getPosition().y));
	    CCCallFuncN moveDone = CCCallFuncN.action(this,  "removeMe");
	    sprite.runAction(CCSequence.actions(moveAction, moveDone));
	    //cocos2d does not allow to reuse actions
	    CCMoveTo move1Action = CCMoveTo.action(cloudSpeed, CGPoint.ccp(0-sprite.getContentSize().width*sprite.getScale(), sprite.getPosition().y));
	    CCCallFuncN move1Done = CCCallFuncN.action(this,  "removeMe");
	    label.runAction(CCSequence.actions(move1Action, move1Done));
	}
	public void removeMe(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    sender.stopAllActions();
	    sender.removeFromParentAndCleanup(true);
	    floatingSprites.remove(sender);
	}
	
	public boolean ccTouchesEnded(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
	    int xStep=0, yStep=0;
        if (p1.x > chopper.getPosition().x)
            xStep=1;
        else if (p1.x < chopper.getPosition().x) {
            xStep=-1;
        }
        if (p1.y > chopper.getPosition().y)
            yStep=1;
        else if (p1.y < chopper.getPosition().y) {
            yStep=-1;
        }
	    this.movePlane(xStep, yStep);
	    return true;
	}
	
	private void checkCollision() {
	    //stop the chopper if required
	    if ((chopper.getPosition().x <= xLeft && planespeed_x < 0) ||
	            (chopper.getPosition().x >= xRight && planespeed_x > 0) ||
	            (chopper.getPosition().y <= yBottom && planespeed_y < 0) ||
	            (chopper.getPosition().y >= yTop && planespeed_y > 0)) {
	        chopper.stopAllActions();
	        planespeed_x=planespeed_y=0;
	    }
	    
	    //check collision
	    CCNode collided=null;
	    float w=chopper.getContentSize().width*chopper.getScale()/4;
	    float h=chopper.getContentSize().height*chopper.getScale()/4;
	    CGRect rcChopper=CGRect.make(chopper.getPosition().x-w/2, chopper.getPosition().y-h/2, w, h);
	    for (CCNode node : floatingSprites) {
	        if ((node.getTag() >= kTagNumber) && (node.getTag() - kTagNumber == plane_target)) {
	            float wNode=node.getContentSize().width * node.getScale();
	            float hNode=node.getContentSize().height * node.getScale();
	            CGRect rc=CGRect.make(node.getPosition().x-wNode/2, node.getPosition().y-hNode/2, wNode, hNode);
	            if (CGRect.intersects(rc, rcChopper)) {
	                collided=node;
	                break;
	            }
	        }
	    }
	    if (collided!=null) {
	        int theNumber=collided.getTag()-kTagNumber;
	        String strVoice=String.format("alphabet/U%04x.mp3",0x30+theNumber);
	        if (theNumber >= 10)
	            strVoice=String.format("alphabet/%d.mp3",theNumber);
	        super.playVoice(strVoice);
	
	        ++plane_target;
	        
	        collided.stopAllActions();
	        CCLabel _label=(CCLabel)collided.getUserData();
	        _label.stopAllActions();
	        
	        collided.removeFromParentAndCleanup(true);
	        floatingSprites.remove(collided);
	        
	        _label.removeFromParentAndCleanup(true);
	        floatingSprites.remove(_label);
	        
	        if (plane_target > 10) {
	            stop=true;
	            planespeed_x=planespeed_y=0;
	            chopper.stopAllActions();
	            for (CCNode node : floatingSprites) {
	                node.stopAllActions();
	            }
	            super.flashAnswerWithResult(true,  true,  null,  null,  2);
	        }
	        else {
	            promptLabel.setString(String.format("%d/10",plane_target));
	        }
	    }
	}
	private void movePlane(int xStep, int yStep) {
	    chopper.stopAllActions();
	    
	    planespeed_x += xStep;
	    planespeed_y += yStep;
	    
	    if (planespeed_x < 0-MAXSPEED) {
	        planespeed_x = 0-MAXSPEED;
	    }
	    else if (planespeed_x > MAXSPEED) {
	        planespeed_x = MAXSPEED;
	    }
	    if (planespeed_y < 0-MAXSPEED) {
	        planespeed_y = 0-MAXSPEED;
	    }
	    else if (planespeed_y > MAXSPEED) {
	        planespeed_y = MAXSPEED;
	    }
	    if ((chopper.getPosition().x <= xLeft && planespeed_x < 0) || (chopper.getPosition().x >= xRight && planespeed_x > 0))
	        planespeed_x=0;
	    if ((chopper.getPosition().y <= yBottom && planespeed_y < 0) ||  (chopper.getPosition().y >= yTop && planespeed_y > 0))
	        planespeed_y=0;
	    
	    if (planespeed_x != 0 || planespeed_y != 0) {
	        //Plane is a little fast than the cloud
	        float tmLimitation=cloudSpeed*0.8f/szWin.width;
	        CCMoveBy moveAction = CCMoveBy.action(tmLimitation, CGPoint.ccp(planespeed_x, planespeed_y));
	        chopper.runAction(CCRepeatForever.action(moveAction));
	    }
	}
	
	//Override
	public void toolbarBtnTouched(Object _sender) {
	    super.toolbarBtnTouched(_sender);
	    CCNode sender=(CCNode)_sender;
	    switch (sender.getTag()) {
	        case Schema.kSvgArrowUp:
	            this.movePlane(0, 1);
	            break;
	        case Schema.kSvgArrowLeft:
	            this.movePlane(-1, 0);
	            break;
	        case Schema.kSvgArrowRight:
	            this.movePlane(1, 0);
	            break;
	        case Schema.kSvgArrowDown:
	            this.movePlane(0, -1);
	            break;
	    }
	}
}
