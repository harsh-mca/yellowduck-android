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
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import android.graphics.Bitmap;
import android.view.MotionEvent;

public class LandSafeScene extends name.w.yellowduck.YDActLayerBase {
    private CCSprite spaceship, landingArea, landingAreaGreen;
    private CCSprite moveFlame, decreaseFlame;
    private CCLabel labelHeight, labelVelocity, labelGravity, labelFuel;
    private float delta;
    private CGPoint ptOriginal;
    
    private int key_vertical;
    private float x,y, fuel_amt, gravity;
    private float zoom;
    private boolean ready;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new LandSafeScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=4;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionArrows);
	    
	    String fontName=super.sysFontName();
	    int fontSize=super.smallFontSize();
	
	    zoom=1.0f*szWin.height/320;
	    float yPos=szWin.height-topOverhead();
	    float xOffset=4;
	
	    labelGravity=CCLabel.makeLabel(trimLabel("landsafe_gravity"), fontName, fontSize);
	    labelGravity.setAnchorPoint(CGPoint.ccp(0,0));
	    labelGravity.setPosition(xOffset, yPos-labelGravity.getContentSize().height);
	    super.addChild(labelGravity, 1);
	    yPos -= labelGravity.getContentSize().height * 2;
	    
	    labelHeight=CCLabel.makeLabel(trimLabel("landsafe_height"), fontName, fontSize);
	    labelHeight.setAnchorPoint(CGPoint.ccp(0,0));
	    labelHeight.setPosition(xOffset, yPos-labelHeight.getContentSize().height);
	    super.addChild(labelHeight,1);;
	    yPos -= labelGravity.getContentSize().height;
	
	    labelFuel=CCLabel.makeLabel(trimLabel("landsafe_fuel"), fontName, fontSize);
	    labelFuel.setAnchorPoint(CGPoint.ccp(0,0));
	    labelFuel.setPosition(xOffset, yPos-labelFuel.getContentSize().height);
	    super.addChild(labelFuel, 1);
	    yPos -= labelFuel.getContentSize().height;
	
	    labelVelocity=CCLabel.makeLabel(trimLabel("landsafe_velocity"), fontName, fontSize);
	    labelVelocity.setAnchorPoint(CGPoint.ccp(0,0));
	    labelVelocity.setPosition(xOffset, yPos-labelVelocity.getContentSize().height);
	    super.addChild(labelVelocity,1);
	
	    //landing area
	    landingArea=spriteFromExpansionFile("image/activities/experience/land_safe/landing_area_red.png");
	    landingArea.setScale(preferredContentScale(true));
	    landingArea.setPosition(226.0f/810*szWin.width,77.0f/527*szWin.height);
	    super.addChild(landingArea, 1);
	    
	    landingAreaGreen=spriteFromExpansionFile("image/activities/experience/land_safe/landing_area_green.png");
	    landingAreaGreen.setScale(landingArea.getScale()*1.1f);
	    landingAreaGreen.setPosition(landingArea.getPosition());
	    landingAreaGreen.setVisible(true);
	    super.addChild(landingAreaGreen,2);
	    
	    //spaceship
	    spaceship=spriteFromExpansionFile("image/activities/experience/land_safe/rocket.png");
	    spaceship.setScale(landingArea.getContentSize().width * landingArea.getScale() / spaceship.getContentSize().width * 0.6f);
	    spaceship.setPosition(szWin.width/2, szWin.height-spaceship.getContentSize().height*spaceship.getScale()/2);
	    spaceship.setVisible(false);
	    super.addChild(spaceship,1);
	    
	    super.setIsTouchEnabled(true);
	    super.scheduleUpdate();
	    super.afterEnter();
	}
	private String trimLabel(String key) {
	    String msg=localizedString(key);
	    int find=msg.indexOf("%");
	    if (find > 0) {
	        msg=msg.substring(0, find);
	    }
	    return msg;
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
	    
	    if (moveFlame!=null) {
	        moveFlame.stopAllActions();
	        moveFlame.removeFromParentAndCleanup(true);
	        moveFlame=null;
	    }
	    if (decreaseFlame!=null) {
	        decreaseFlame.removeFromParentAndCleanup(true);
	        decreaseFlame=null;
	    }
	    
	    float xPos=randomBetween((int)(szWin.width*0.25f), (int)(szWin.width * 0.75f));
	    landingArea.setPosition(xPos, landingArea.getPosition().y);
	    landingAreaGreen.setPosition(landingArea.getPosition());
	    landingAreaGreen.setVisible(true);
	    
	    spaceship.setVisible(false);
	    spaceship.setPosition(szWin.width/2, szWin.height-topOverhead()-spaceship.getContentSize().height*spaceship.getScale()/2);
	    ready=false;
	    delta=100;
	    
	    gravity=0.58f * mLevel;
	    String key=localizedString("landsafe_gravity");
	    labelGravity.setString(String.format(key, gravity));
	    
	    x=0; y=0.005f; key_vertical=4;
	    fuel_amt=100;
	
	    String text=localizedString("label_am_ready");
	    int fontSize=super.largeFontSize();
	    Bitmap img=super.createMultipleLineLabel(text, super.sysFontName(), fontSize, 0, Schema.kPopUpFontClr, Schema.kPopUpBgClr);
	    Bitmap imgSel=super.buttonize(img);
	    
	    CCSprite sprite=CCSprite.sprite(img, "rdy");
	    CCSprite spriteSelected=CCSprite.sprite(imgSel, "rdysel");
	    CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected, this, "ready");
	    CCMenu menu = CCMenu.menu(menuitem);
	    menu.setPosition(szWin.width/2, szWin.height / 2 - sprite.getContentSize().height);
	    super.addChild(menu,1);
	    floatingSprites.add(menu);
	}
	public void update(float dt) {
	    if (!ready)
	        return;
	    delta += dt;
	    if (delta < 30.0f/1000)
	        return;
	    delta=0;
	    
	    if (fuel_amt <= 0) {
	        key_vertical = 4;
	        x = 0;
	    }
	    //handle increase in velocity and flame
	    if (key_vertical == 1) {
	        y -= 0.005;
	    }
	    else if (key_vertical == 2) {
	        y -= 0.002;
	    }
	    else if (key_vertical == 3) {
	        y +=0.002 * mLevel;
	    }
	    else if (key_vertical == 4) {
	        y +=0.005 * mLevel;
	    }
	    //move spaceship and flame
	    float xOffset=x*zoom;
	    float yOffset=y*zoom;
	    
	    float x0=spaceship.getPosition().x + xOffset;
	    float y0=spaceship.getPosition().y - yOffset;
	    spaceship.setPosition(x0, y0);
	    if (moveFlame!=null) {
	        x0=moveFlame.getPosition().x + xOffset;
	        y0=moveFlame.getPosition().y - yOffset;
	        moveFlame.setPosition(x0, y0);
	    }
	    if (decreaseFlame!=null) {
	        x0=decreaseFlame.getPosition().x + xOffset;
	        y0=decreaseFlame.getPosition().y - yOffset;
	        decreaseFlame.setPosition(x0, y0);
	    }
	    this.update_velocity();
	    this.update_height();
	    this.update_fuel();
	    this.check_landing();
	}
	
	public void ready(Object _sender) {
	    if (ready)
	        return;
	    ready=true;
	    spaceship.setVisible(true);
	    CCNode sender=(CCNode)_sender;
	    sender.getParent().removeFromParentAndCleanup(true);
	    floatingSprites.remove(sender.getParent());
	}
	
	private void update_fuel() {
	    if (decreaseFlame!=null) {
	        if  (mLevel <= 1)
	            fuel_amt -= 1 * 0.25f;
	        else
	            fuel_amt -= 1 * 0.5f;
	    }
	    if (fuel_amt <= 0) {
	        fuel_amt=0;
	        if (moveFlame!=null) {
	            moveFlame.stopAllActions();
	            moveFlame.removeFromParentAndCleanup(true);
	            moveFlame=null;
	        }
	        if (decreaseFlame!=null) {
	            decreaseFlame.removeFromParentAndCleanup(true);
	            decreaseFlame=null;
	        }
	    }
	    String key=localizedString("landsafe_fuel");
	    labelFuel.setString(String.format(key, (int)fuel_amt));
	}
	private void update_velocity() {
	    int velocity=(int)(y*10);
	    String key=localizedString("landsafe_velocity");
	    labelVelocity.setString(String.format(key, velocity));
	    
	    landingAreaGreen.setVisible((velocity < 8));
	}
	private void update_height() {
	    float yTop=landingArea.getPosition().y;
	    float yBottom=spaceship.getPosition().y - spaceship.getContentSize().height * spaceship.getScaleY()/2;
	    float height=yBottom - yTop;
	    if (height < 0)
	        height = 0;
	    String key=localizedString("landsafe_height");
	    labelHeight.setString(String.format(key,  (int)height));
	}
	private void check_landing() {
	    float yTop=landingArea.getPosition().y;
	    float yBottom=spaceship.getPosition().y - spaceship.getContentSize().height * spaceship.getScaleY()/2;
	    if (yBottom <= yTop) {
	        ready=false;
	        if (decreaseFlame!=null) {
	            decreaseFlame.removeFromParentAndCleanup(true);
	            decreaseFlame=null;
	        }
	        
	        boolean gamewon=false;
	        if (landingAreaGreen.getVisible()) {
	            float range=landingAreaGreen.getContentSize().width * landingAreaGreen.getScale() / 4;
	            if (spaceship.getPosition().x >= landingAreaGreen.getPosition().x - range && spaceship.getPosition().x < landingAreaGreen.getPosition().x + range)
	                gamewon=true;
	        }
	        else {
	            CCSprite crash=spriteFromExpansionFile("image/activities/experience/land_safe/crash.png");
	            crash.setPosition(spaceship.getPosition());
	            super.addChild(crash, 4);
	            floatingSprites.add(crash);
	        }
	        flashAnswerWithResult(gamewon, gamewon, null, null, 2);
	        if (!gamewon)
	            super.performSelector("__restart", 2.2f);
	    }
	}
	
	//Override
	public void toolbarBtnTouched(Object _sender) {
	    super.toolbarBtnTouched(_sender);
	    CCNode sender=(CCNode)_sender;
	    switch (sender.getTag()) {
	        case Schema.kSvgArrowUp:
	        case Schema.kSvgArrowLeft:
	        case Schema.kSvgArrowRight:
	        case Schema.kSvgArrowDown:
	            this.handle_key(sender.getTag());
	            break;
	    }
	}
	private void handle_key(int key) {
	    if (!ready)
	        return;
	    
	    if (key == Schema.kSvgArrowLeft) {
	        x -= 0.05f;
	        if (fuel_amt > 0) {
	            if (moveFlame!=null) {
	    	        moveFlame.stopAllActions();
	    	        moveFlame.removeFromParentAndCleanup(true);
	    	        moveFlame=null;
	            }
	            moveFlame=spriteFromExpansionFile("image/activities/experience/land_safe/flame_right.png");
	            moveFlame.setPosition(spaceship.getPosition().x + spaceship.getContentSize().width*spaceship.getScale()/2+moveFlame.getContentSize().width/2, spaceship.getPosition().y-spaceship.getContentSize().height*spaceship.getScale()/2+moveFlame.getContentSize().height/2);
	            super.addChild(moveFlame, 1);
	            
	            CCDelayTime idleAction = CCDelayTime.action(0.3f);
	            CCCallFuncN actionDone = CCCallFuncN.action(this, "removeMovementFlame");
	            moveFlame.runAction(CCSequence.actions(idleAction, actionDone));
	        }
	    }
	    else if (key == Schema.kSvgArrowRight) {
	        x += 0.05f;
	        if (fuel_amt > 0) {
	            if (moveFlame!=null) {
	    	        moveFlame.stopAllActions();
	    	        moveFlame.removeFromParentAndCleanup(true);
	    	        moveFlame=null;
	            }
	            moveFlame=spriteFromExpansionFile("image/activities/experience/land_safe/flame_right.png");
	            moveFlame.setPosition(spaceship.getPosition().x - spaceship.getContentSize().width*spaceship.getScale()/2-moveFlame.getContentSize().width/2, spaceship.getPosition().y-spaceship.getContentSize().height*spaceship.getScale()/2+moveFlame.getContentSize().height/2);
	            super.addChild(moveFlame, 1);
	            
	            CCDelayTime idleAction = CCDelayTime.action(0.3f);
	            CCCallFuncN actionDone = CCCallFuncN.action(this, "removeMovementFlame");
	            moveFlame.runAction(CCSequence.actions(idleAction, actionDone));
	        }
	    }
	    else {
	        int previous=key_vertical;
	        if (key == Schema.kSvgArrowUp) {
	            if (key_vertical > 1)
	                key_vertical -= 1;
	        }
	        else if (key == Schema.kSvgArrowDown) {
	            if (key_vertical < 4) {
	                key_vertical += 1;
	            }
	        }
	        if (fuel_amt > 0 && key_vertical != previous) {
	            if (decreaseFlame!=null) {
	                decreaseFlame.removeFromParentAndCleanup(true);
	                decreaseFlame=null;
	            }
	            if (key_vertical>=1&&key_vertical<=3)
	                decreaseFlame=spriteFromExpansionFile(String.format("image/activities/experience/land_safe/flame%d.png", 4-key_vertical));
	            if (decreaseFlame!=null) {
	                decreaseFlame.setScale(spaceship.getContentSize().width * spaceship.getScale()/decreaseFlame.getContentSize().width);
	                decreaseFlame.setPosition(spaceship.getPosition().x, spaceship.getPosition().y-spaceship.getContentSize().height*spaceship.getScaleY()/2-decreaseFlame.getContentSize().height*decreaseFlame.getScale()/2);
	                super.addChild(decreaseFlame, 1);
	            }
	        }
	    }
	}
	
	public void removeMovementFlame(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    sender.removeFromParentAndCleanup(true);
	    moveFlame=null;
	}
	
	public boolean ccTouchesBegan(MotionEvent event) {
		ptOriginal = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
		return true;
	}
	
	public boolean ccTouchesEnded(MotionEvent event) {
		CGPoint pt = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
        float xDiff=pt.x - ptOriginal.x;
        float yDiff=pt.y - ptOriginal.y;

        float xAbs=(xDiff > 0)?xDiff:0-xDiff;
        float yAbs=(yDiff > 0)?yDiff:0-yDiff;
        if (xAbs > yAbs) {
            this.handle_key((xDiff>0)?Schema.kSvgArrowRight:Schema.kSvgArrowLeft);
        }
        else {
            this.handle_key((yDiff>0)?Schema.kSvgArrowUp:Schema.kSvgArrowDown);
        }
		return true;
	}
	
	public void __restart(Object _sender) {
	    if (!isShuttingDown())
	        this.initGame(false,  null);
	}
}
