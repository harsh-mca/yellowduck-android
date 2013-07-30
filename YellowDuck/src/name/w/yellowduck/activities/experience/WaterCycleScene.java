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


package name.w.yellowduck.activities.experience;

import name.w.yellowduck.Category;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.base.CCRepeatForever;
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
import org.cocos2d.types.CGRect;

import android.view.MotionEvent;

public class WaterCycleScene extends name.w.yellowduck.YDActLayerBase {
	private final int zItemAboveBg        =5;
	private final int kTuxHome            =100;

	private final int kStepIdle                   =0;

	private final int kStep2ClickSun              =1;
	private final int kStep2ClickCloud            =2;
	private final int kStepRainning               =3;
	private final int kStepRainStopped            =4;
	
    private CCSprite sun, waiting, shower, cloud, drops, river;
    private CGPoint ptSunRises;
    
    private int step;
    private boolean pumpStationWorking, cleanupStationWorking;
    private boolean gamewon;
	
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new WaterCycleScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=1;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    CCSprite spriteBg=super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    spriteBg.removeFromParentAndCleanup(true);
	    super.addChild(spriteBg, 2);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp);
	
	    //the sky
	    CCSprite spriteSky=spriteFromExpansionFile("image/activities/experience/watercycle/sky.png");
	    spriteSky.setScaleX(spriteBg.getScaleX());
	    spriteSky.setScaleY(spriteBg.getScaleY());
	    spriteSky.setPosition(szWin.width/2, szWin.height-spriteSky.getContentSize().height*spriteSky.getScaleY()/2);
	    super.addChild(spriteSky, 0);
	    //the sun
	    sun=spriteFromExpansionFile("image/activities/experience/watercycle/sun.png");
	    sun.setScale(60.0f/525*szWin.width/sun.getContentSize().width);
	    sun.setPosition(87.0f/1024*szWin.width, 466.0f/666*szWin.height-sun.getContentSize().height*sun.getScale()*0.25f);
	    super.addChild(sun, 1);
	    ptSunRises=sun.getPosition();
	
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();

	    //the tux heading home
	    CCSprite tux=spriteFromExpansionFile("image/activities/experience/watercycle/boat_sailing.png");
	    tux.setScale(70.0f/666*szWin.height/tux.getContentSize().height);
	    tux.setPosition(0, tux.getContentSize().height*tux.getScale()/2+15.0f/666*szWin.height);
	    super.addChild(tux, zItemAboveBg);
	    CCMoveTo moveAction=CCMoveTo.action(8, CGPoint.ccp(930.f/1024*szWin.width, tux.getPosition().y));
	    CCCallFuncN doneAction = CCCallFuncN.action(this, "arrivedHome");
	    tux.runAction(CCSequence.actions(moveAction, doneAction));
	    super.playSound("audio/sounds/Harbor1.wav");
	    
	    step=kStep2ClickSun;
	}
	
	public boolean ccTouchesEnded(MotionEvent event) {
		CGPoint pt = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
	
        int clickedItem=this.getClickedItem(pt);
        String msg=null;
        if (clickedItem >= 0) {
            switch (clickedItem) {
                case 0://pump station
                    msg=localizedString("watercycle_pumpstation");
                    super.flashMsg(msg, 2);
                    if (step == kStepRainning || step == kStepRainStopped) {
                        super.playSound("audio/sounds/bubble.wav");
                        if (!pumpStationWorking) {
                            pumpStationWorking=true;
                            CCSprite sprite=super.setupBackground("image/activities/experience/watercycle/pipe.png", kBgModeFit);
                            sprite.removeFromParentAndCleanup(true);
                            super.addChild(sprite, 3);
                            
                            if (cleanupStationWorking && pumpStationWorking)
                                this.startWaterRecycling();
                        }
                    }
                    break;
                case 1://clean up station
                    msg=localizedString("watercycle_cleanupstation");
                    super.flashMsg(msg,2);
                    if (step == kStepRainning || step == kStepRainStopped) {
                        super.playSound("audio/sounds/bubble.wav");
                        if (!cleanupStationWorking) {
                            cleanupStationWorking=true;
                            CCSprite sprite=super.setupBackground("image/activities/experience/watercycle/sewage.png", kBgModeFit);
                            sprite.removeFromParentAndCleanup(true);
                            super.addChild(sprite, 3);
                            
                            if (cleanupStationWorking && pumpStationWorking)
                                this.startWaterRecycling();
                        }
                    }
                    break;
                case kTuxHome:
                    if (pumpStationWorking && cleanupStationWorking) {
                        if (!shower.getVisible()) {
                            shower.setVisible(true);
                            waiting.setVisible(false);
                            
                            CCDelayTime idleAction = CCDelayTime.action(6);
                            CCCallFuncN actionDone = CCCallFuncN.action(this, "stopShower");
                            shower.runAction(CCSequence.actions(idleAction, actionDone));
                            if (gamewon)
                                super.playSound("audio/sounds/apert2.wav");
                            else {
                                super.flashAnswerWithResult(true, false, null, null, 2);
                                gamewon=true;
                            }
                        }
                    }
                    else {
                        super.playSound("audio/sounds/crash.wav");
                    }
                    break;
            }//switch
        }
        else {
            if (step == kStep2ClickSun || step == kStepRainStopped) {
                if (super.isNodeHit(sun, pt)) {
                    super.playSound("audio/sounds/bleep.wav");
                    step=kStepIdle;
                    this.makeSunRises();
                }
            }
            else if (step == kStep2ClickCloud) {
                if (super.isNodeHit(cloud, pt)) {
                    playSound("audio/sounds/Water5.wav");
                    this.startRaining();
                }
            }
        }
		return true;
	}
	
	private void makeSunRises() {
		CCMoveTo moveAction=CCMoveTo.action(3, CGPoint.ccp(sun.getPosition().x, szWin.height-sun.getContentSize().height*sun.getScaleY()/2));
		CCCallFuncN doneAction = CCCallFuncN.action(this, "sunRose");
	    sun.runAction(CCSequence.actions(moveAction, doneAction));
	    
	}
	private void startRaining() {
	    if (drops == null) {
	        drops=spriteFromExpansionFile("image/activities/experience/watercycle/drops.png");
	        drops.setScale(cloud.getContentSize().width * cloud.getScaleX() / drops.getContentSize().width);
	        drops.setPosition(cloud.getPosition().x, cloud.getPosition().y - cloud.getContentSize().height*cloud.getScaleY()/2-drops.getContentSize().height*drops.getScaleY()/2);
	        super.addChild(drops, zItemAboveBg);
	    }
	    //the river
	    if (river == null) {
	        river=super.setupBackground("image/activities/experience/watercycle/river.png", kBgModeFit);
	        river.removeFromParentAndCleanup(true);
	        super.addChild(river, 3);
	    }
	    step=kStepRainning;
	    String msg=localizedString("watercycle_rain");
	    super.flashMsg(msg, 6);
	    
	    CCDelayTime idleAction = CCDelayTime.action(8);
	    CCCallFuncN actionDone = CCCallFuncN.action(this, "stopRainning");
	    drops.runAction(CCSequence.actions(idleAction, actionDone));
	}
	
	public void sunRose(Object _sender) {
	    //the vapor
	    CCSprite vapor=spriteFromExpansionFile("image/activities/experience/watercycle/vapor.png");
	    vapor.setScale(sun.getContentSize().width * sun.getScaleX() / vapor.getContentSize().width);//same width as the sun
	    vapor.setPosition(ptSunRises);
	    super.addChild(vapor, zItemAboveBg);
	
	    CCMoveTo moveAction=CCMoveTo.action(3, CGPoint.ccp(vapor.getPosition().x, szWin.height-vapor.getContentSize().height*vapor.getScaleY()/2));
	    CCCallFuncN doneAction = CCCallFuncN.action(this, "cloudBorn");
	    vapor.runAction(CCSequence.actions(moveAction, doneAction));
	    
	    String msg=localizedString("watercycle_heat");
	    super.flashMsg(msg, 6);
	}
	
	public void cloudBorn(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    //remove vapor
	    sender.removeFromParentAndCleanup(true);
	
	    cloud=spriteFromExpansionFile("image/activities/experience/watercycle/cloud.png");
	    cloud.setScale(sun.getContentSize().width * sun.getScaleX() / cloud.getContentSize().width);
	    cloud.setPosition(ptSunRises.x, szWin.height-cloud.getContentSize().height*cloud.getScale()/2);
	    super.addChild(cloud, zItemAboveBg);
	    //move to middle
	    CCMoveTo moveAction=CCMoveTo.action(3,  CGPoint.ccp(450.0f/1024*szWin.width, cloud.getPosition().y));
	    CCCallFuncN doneAction = CCCallFuncN.action(this, "rainComing");
	    cloud.runAction(CCSequence.actions(moveAction, doneAction));
	}
	public void rainComing(Object _sender) {
	    //sunset
		CCMoveTo moveAction=CCMoveTo.action(3, ptSunRises);
	    sun.runAction(moveAction);
	    
	    step=kStep2ClickCloud;
	    this.prompt2clickAt(cloud.getPosition());
	}
	public void stopRainning(Object _sender) {
	    if (cloud!=null) {
	        cloud.removeFromParentAndCleanup(true);
	        cloud=null;
	    }
	    if (drops!=null) {
	        drops.removeFromParentAndCleanup(true);
	        drops=null;
	    }
	    step=kStepRainStopped;
	
	    this.prompt2clickAt(CGPoint.ccp(454.0f/1024*szWin.width,394.0f/666*szWin.height));
	    this.prompt2clickAt(CGPoint.ccp(720.0f/1024*szWin.width,120.0f/666*szWin.height));
	}
	public void stopShower(Object _sender) {
	    waiting.setVisible(true);
	    shower.setVisible(false);
	    //play sound?
	}
	
	public void arrivedHome(Object _sender) {
		CCNode node=(CCNode)_sender;
	    node.removeFromParentAndCleanup(true);
	    CCSprite boat=spriteFromExpansionFile("image/activities/experience/watercycle/boat.png");
	    boat.setScale(node.getScale());
	    boat.setPosition(node.getPosition());
	    super.addChild(boat, zItemAboveBg);
	    
	    //waiting for shower
	    waiting=spriteFromExpansionFile("image/activities/experience/watercycle/waiting.png");
	    waiting.setScale(116.0f/1024*szWin.width/waiting.getContentSize().width);
	    waiting.setPosition(842.0f/1024*szWin.width + waiting.getContentSize().width*waiting.getScale()/2, 280.0f/666*szWin.height);
	    super.addChild(waiting, zItemAboveBg);
	    super.playSound("audio/sounds/Harbor3.wav");
	    
	
	    shower=spriteFromExpansionFile("image/activities/experience/watercycle/shower.png");
	    shower.setScale(waiting.getScale());
	    shower.setPosition(waiting.getPosition());
	    shower.setVisible(false);
	    super.addChild(shower, zItemAboveBg);
	    
	    this.prompt2clickAt(CGPoint.ccp(ptSunRises.x, 466.0f/666*szWin.height));
	}
	
	private void prompt2clickAt(CGPoint pt) {
	    //flash the start
	    CCSprite sparkle=spriteFromExpansionFile("image/misc/star.png");
	    sparkle.setPosition(pt);
	    sparkle.setScale(0.2f);
	    super.addChild(sparkle, 7);
	    floatingSprites.add(sparkle);
	    CCScaleTo scaleUpAction=CCScaleTo.action(2, 2);
	    CCScaleTo scaleDownAction=CCScaleTo.action(1, 0.2f);
	    CCCallFuncN actionDone = CCCallFuncN.action(this, "removeMe");
	    sparkle.runAction(CCSequence.actions(scaleUpAction, scaleDownAction, actionDone));
	}
	
	public void removeMe(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    sender.removeFromParentAndCleanup(true);
	    floatingSprites.remove(sender);
	}
	
	//determine if user clicked at an item with fixed position
	private int getClickedItem(CGPoint pt) {
	    float positions[]={454,394,66,54, //0:pump
	                       720,132,122,94 //1:cleanup
	    };
	    for (int i = 0; i < positions.length; i+=4) {
	        float x=1.0f * positions[i] / 1024 * szWin.width;
	        float y=1.0f * positions[i+1] /666 * szWin.height;
	        float w=1.0f * positions[i+2]/1024 * szWin.width;
	        float h=1.0f * positions[i+3]/ 666 * szWin.height;
	        CGRect rc=CGRect.make(x-w/2,y-h/2, w, h);
	        if (rc.contains(pt.x, pt.y)) {
	            return i/4;
	        }
	    }
	    if (shower!=null) {
	        if (super.isNodeHit(shower, pt)) {
	            return kTuxHome;
	        }
	    }
	    return -1;
	}

	private void startWaterRecycling() {
	    CCSprite ripple=spriteFromExpansionFile("image/activities/experience/watercycle/ripple.png");
	    ripple.setPosition(813.0f/1024*szWin.width,485.0f/666*szWin.height);
	    float  minScale=4.0f/ripple.getContentSize().width;
	    float  maxScale=39.0f/1024*szWin.width/ripple.getContentSize().width;
	    ripple.setScale(minScale);
	    super.addChild(ripple, zItemAboveBg);
	
	    CCScaleTo scaleBig=CCScaleTo.action(5, maxScale);
	    CCScaleTo scaleSmall=CCScaleTo.action(5, minScale);
	    CCSequence sequence=CCSequence.actions(scaleBig, scaleSmall);
	    ripple.runAction(CCRepeatForever.action(sequence));
	}
}
