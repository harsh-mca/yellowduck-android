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
import name.w.yellowduck.Schema;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import android.view.MotionEvent;

public class ParatrooperScene extends name.w.yellowduck.YDActLayerBase {
	private class ParatrooperItem extends Object {
		private int status;
		private float speedY;
		private float drift;
		private boolean speed_override;
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public float getSpeedY() {
			return speedY;
		}
		public void setSpeedY(float speedY) {
			this.speedY = speedY;
		}
		public float getDrift() {
			return drift;
		}
		public void setDrift(float drift) {
			this.drift = drift;
		}
		public boolean isSpeed_override() {
			return speed_override;
		}
		public void setSpeed_override(boolean speed_override) {
			this.speed_override = speed_override;
		}
	}
	private final int TUX_INPLANE		=(1 << 0);
	private final int TUX_DROPPING      =(1 << 1);
	private final int TUX_FLYING		=(1 << 2);
	private final int TUX_LANDED		=(1 << 3);
	private final int TUX_CRASHED		=(1 << 4);
	
	private final int kTagPlane           =1;
	private final int kTagBoat            =2;
	private final int kTagTux             =3;

	private final int kSeconds2Land       =40;
	
	
    private float planespeed_x; //time to move across the screen
    private float windspeed, winspeed_pixels;
    
    private float imageZoom;
    private CCSprite spriteTux, spriteBoat, spritePlane;
    
    private ParatrooperItem paratrooperItem;
    private float tmDelta;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ParatrooperScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=6;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(),kBgModeStretch);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionUpDnArrows);
	
	    paratrooperItem=new ParatrooperItem();
	    super.setIsTouchEnabled(true);
	    super.scheduleUpdate();
	    super.afterEnter();
	}

	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    
	    paratrooperItem.setStatus(TUX_INPLANE);
	    paratrooperItem.setSpeed_override(false);
	    
	    /* Make the images tend to 0.5 ratio */
	    imageZoom=(0.4f+(0.2f * (2 - (mLevel-1) % 3)))*0.4f*preferredContentScale(true);
	    
	    windspeed = 60.0f-mLevel*8;
	    if (super.nextInt(2)==0)
	        windspeed = 0 - windspeed;
	    winspeed_pixels=szWin.width/windspeed;
	    /* Display the target */
	    this.cloudBorn();
	
	    //the plane
	    {
	        planespeed_x=60-mLevel;
	        spritePlane=spriteFromExpansionFile("image/activities/experience/paratrooper/tuxplane.png");
	        spritePlane.setScale(imageZoom);
	        spritePlane.setPosition(0-spritePlane.getContentSize().width*spritePlane.getScale()/2, szWin.height-topOverhead() - spritePlane.getContentSize().height*spritePlane.getScale()/2-10);
	        spritePlane.setTag(kTagPlane);
	        super.addChild(spritePlane,2);
	        floatingSprites.add(spritePlane);
	        //move to the right of the screen
	        CCMoveTo moveAction=CCMoveTo.action(planespeed_x, CGPoint.ccp(szWin.width+spritePlane.getContentSize().width * spritePlane.getScale()/2, spritePlane.getPosition().y));
	        CCCallFuncN moveDone = CCCallFuncN.action(this, "gameOverIf");
	        spritePlane.runAction(CCSequence.actions(moveAction, moveDone));
	    }
	    
	    //the boat
	    {
	        spriteBoat=spriteFromExpansionFile("image/activities/experience/paratrooper/fishingboat.png");
	        spriteBoat.setScale(130.0f/666*szWin.height/spriteBoat.getContentSize().height);
	        spriteBoat.setPosition(0-spriteBoat.getContentSize().width*spriteBoat.getScale()/2, spriteBoat.getContentSize().height*spriteBoat.getScale()/2*1.4f);
	        spriteBoat.setTag(kTagBoat);
	        super.addChild(spriteBoat,1);
	        floatingSprites.add(spriteBoat);
	        //move to the center of the screen
	        CCMoveTo moveAction=CCMoveTo.action(12.0f, CGPoint.ccp(szWin.width/2, spriteBoat.getContentSize().height*spriteBoat.getScale()/2));
	        spriteBoat.runAction(moveAction);
	    }
	}
	
	public void update(float dt) {
	    if (paratrooperItem.getStatus() != TUX_DROPPING && paratrooperItem.getStatus() != TUX_FLYING)
	        return;
	    tmDelta += dt;
	    float ratio=tmDelta / (kSeconds2Land/8);
	    if (ratio > 1)
	        ratio =1;
	    float offset = winspeed_pixels * 0.3f * ratio + paratrooperItem.getDrift() * (1- ratio);
	    
	    float drop=paratrooperItem.getSpeedY();
	    if (paratrooperItem.getStatus() == TUX_DROPPING) {
	        offset *= 1.05f;
	    }
	    else if (paratrooperItem.getStatus() == TUX_FLYING) {
	        if (!paratrooperItem.isSpeed_override()) {
	            drop /= 1.2f;
	        }
	    }
	    offset *= dt;
	    drop   *= dt;
	    spriteTux.setPosition(spriteTux.getPosition().x+offset, spriteTux.getPosition().y-drop);
	
	    float w =spriteBoat.getContentSize().width * spriteBoat.getScale();
	    float h= spriteBoat.getContentSize().height * spriteBoat.getScale();
	    //landing area
	    CGRect rc=CGRect.make(spriteBoat.getPosition().x-w/2, spriteBoat.getPosition().y-h/2, w, h*0.85f);
	    CGPoint pt=CGPoint.ccp(spriteTux.getPosition().x, spriteTux.getPosition().y-spriteTux.getContentSize().height*spriteTux.getScale()/2);
	    if (rc.contains(pt.x, pt.y)) {
	        if (paratrooperItem.getStatus() == TUX_FLYING) {//parachute is open
	            paratrooperItem.setStatus(TUX_LANDED);
	            //[super playSound:@"Assets/audio/sounds/tuxok.wav"];
	            flashAnswerWithResult(true, true, null, null, 2);
	        }
	    }
	    if (paratrooperItem.getStatus() != TUX_LANDED) {
	        //moved out of the screen
	        if (spriteTux.getPosition().x < 0 || spriteTux.getPosition().x > szWin.width || spriteTux.getPosition().y < rc.origin.y + rc.size.height) {
	            super.playSound("audio/sounds/bubble.wav");
	            paratrooperItem.setStatus(TUX_CRASHED);
	            super.flashAnswerWithResult(false, false, null, null, 1.8f);
	            super.performSelector("__restart", 2);
	        }
	    }
	}
	
	public boolean ccTouchesEnded(MotionEvent event) {
		CGPoint pt = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
        if (paratrooperItem.getStatus() == TUX_INPLANE) {
            if (super.isNodeHit(spritePlane, pt)) {
                super.playSound("audio/sounds/tuxok.wav");
                
                tmDelta=0;
                float w=spritePlane.getContentSize().width * spritePlane.getScale();
                float h=spritePlane.getContentSize().height * spritePlane.getScale();
                //drop the tux
                spriteTux=spriteFromExpansionFile("image/activities/experience/paratrooper/minitux.png");
                spriteTux.setScale(imageZoom);
                spriteTux.setPosition(spritePlane.getPosition().x-w/2, spritePlane.getPosition().y-h/2-spriteTux.getContentSize().height*spriteTux.getScale()/2);
                spriteTux.setTag(kTagTux);
                super.addChild(spriteTux,1);
                floatingSprites.add(spriteTux);
                paratrooperItem.setSpeedY(szWin.height/kSeconds2Land); //seconds fall down to floor
                paratrooperItem.setDrift(szWin.width/planespeed_x);
                paratrooperItem.setStatus(TUX_DROPPING);
            }
        }
        else if (paratrooperItem.getStatus() == TUX_DROPPING) {
            if (super.isNodeHit(spriteTux, pt)) {
                super.playSound("audio/sounds/eraser2.wav");
                
                CCSprite sprite=spriteFromExpansionFile("image/activities/experience/paratrooper/parachute.png");
                sprite.setScale(imageZoom*224/70);
                sprite.setPosition(spriteTux.getPosition().x, spriteTux.getPosition().y-spriteTux.getContentSize().height*spriteTux.getScale()/2+sprite.getContentSize().height*sprite.getScale()/2);
                sprite.setTag(kTagTux);
                super.addChild(sprite,1);
                floatingSprites.add(sprite);

                spriteTux.removeFromParentAndCleanup(true);
                floatingSprites.remove(spriteTux);
                spriteTux=sprite;
                
                paratrooperItem.setStatus(TUX_FLYING);
            }
        }
        return true;
	}
	
	private void cloudBorn() {
	    CCSprite spriteCloud=spriteFromExpansionFile("image/activities/experience/paratrooper/cloud.png");
	    spriteCloud.setScale(imageZoom);
	    float w=spriteCloud.getContentSize().width*spriteCloud.getScale();
	    float h=spriteCloud.getContentSize().height*spriteCloud.getScale();
	    CCMoveTo moveAction=null;
	    if (windspeed>0) {
	        spriteCloud.setPosition(0-w/2, szWin.height-topOverhead()-h*2);
	        //move from left to right
	        moveAction=CCMoveTo.action(windspeed, CGPoint.ccp(szWin.width+w/2, spriteCloud.getPosition().y));
	    }
	    else {
	        spriteCloud.setPosition(szWin.width+w/2, szWin.height-topOverhead()-h*2);
	        //move from right to left
	        moveAction=CCMoveTo.action(0-windspeed, CGPoint.ccp(0-w/2, spriteCloud.getPosition().y));
	    }
	    super.addChild(spriteCloud,1);
	    floatingSprites.add(spriteCloud);
	    
	    CCCallFuncN moveDone = CCCallFuncN.action(this, "removeMe");
	    spriteCloud.runAction(CCSequence.actions(moveAction, moveDone));
	}
	
	public void removeMe(Object _sender) {
		CCNode sender=(CCNode)_sender;
	
	    sender.removeFromParentAndCleanup(true);
	    floatingSprites.remove(sender);
	    this.cloudBorn();
	}
	
	public void gameOverIf(Object _sender) {
	    if(paratrooperItem.getStatus() == TUX_INPLANE) {
	        super.flashAnswerWithResult(false, false, null, null, 1.8f);
	        super.performSelector("__restart", 2);
	    }
	}
	
	public void __restart() {
	    this.initGame(false,  null);
	}
	
	//Override
	public void toolbarBtnTouched(Object _sender) {
	    super.toolbarBtnTouched(_sender);
	    CCNode sender=(CCNode)_sender;
	    switch (sender.getTag()) {
	        case Schema.kSvgArrowUp:
	            if(paratrooperItem.getStatus() == TUX_FLYING && paratrooperItem.getSpeedY() > 2) {
	                paratrooperItem.setSpeed_override(true);
	                paratrooperItem.setSpeedY(paratrooperItem.getSpeedY()-2);
	            }
	            break;
	        case Schema.kSvgArrowDown:
	            if(paratrooperItem.getStatus() == TUX_FLYING && paratrooperItem.getSpeedY() < 20) {
	                paratrooperItem.setSpeed_override(true);
	                paratrooperItem.setSpeedY(paratrooperItem.getSpeedY()+2);
	            }
	            break;
	    }
	}
}
