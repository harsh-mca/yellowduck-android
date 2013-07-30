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

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor3B;

import android.view.MotionEvent;

public class CanalLockScene extends name.w.yellowduck.YDActLayerBase {
	private final int BOAT_POS_LEFT			=1;
	private final int BOAT_POS_MIDDLE		=2;
	private final int BOAT_POS_RIGHT		=3;

	private final float WATER_LOWER         =272.0f;
	private final float WATER_HIGH          =406.0f;

    private int boat_position;
    private int from;
    private boolean lock_left_up; //top locker
    private boolean lock_right_up; //top locker
    private boolean lock_water_low;
    private boolean canallock_left_up; //bottom locker
    private boolean canallock_right_up; //bottom locker
    
    private boolean animation, gamewon;
    
    private CCSprite boat, leftToplocker, rightToplocker, leftBottomlocker, rightBottomlocker, leftLightRed,leftLightGreen, rightLightRed,rightLightGreen, water;
    private float maxTopScaleY, maxBottomScaleY, maxWaterScaleY;
    private float xLeftBoat, xRightBoat, yBoatLower, yBoatHigh;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new CanalLockScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=1;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp);
	    //the boat
	    {
	        boat=spriteFromExpansionFile("image/activities/experience/canal_lock/boat.png");
	        boat.setScale(186.0f/1024*szWin.width/boat.getContentSize().width*0.8f);
	        boat.setPosition(szWin.width * 0.18f, WATER_LOWER/666*szWin.height+boat.getContentSize().height*boat.getScale()/2-18.0f/256*boat.getContentSize().height*boat.getScale());
	        super.addChild(boat,2);
	        floatingSprites.add(boat);
	        
	        xLeftBoat=boat.getPosition().x;
	        xRightBoat=szWin.width - xLeftBoat;
	        
	        yBoatLower=boat.getPosition().y;
	        yBoatHigh=WATER_HIGH/666*szWin.height+boat.getContentSize().height*boat.getScale()/2-18.0f/256*boat.getContentSize().height*boat.getScale();
	    }
	    {
	        //top left lock
	        leftToplocker=spriteFromExpansionFile("image/misc/layermaskwhite.png");
	        leftToplocker.setScaleX(20.0f/1024*szWin.width/leftToplocker.getContentSize().width);
	        leftToplocker.setScaleY(296.0f/666*szWin.height/leftToplocker.getContentSize().height);
	        leftToplocker.setAnchorPoint(CGPoint.ccp(0.5f, 0));
	        leftToplocker.setPosition(419.0f/1024*szWin.width, 157.0f/666*szWin.height);
	        leftToplocker.setColor(new ccColor3B(0x90, 0xee, 0x90));
	        super.addChild(leftToplocker,1);
	        floatingSprites.add(leftToplocker);
	        
	        maxTopScaleY=leftToplocker.getScaleY();
	    }
	    {
	        //top right lock
	        rightToplocker=spriteFromExpansionFile("image/misc/layermaskwhite.png");
	        rightToplocker.setScaleX(leftToplocker.getScaleX());
	        rightToplocker.setScaleY(maxTopScaleY);
	        rightToplocker.setAnchorPoint(CGPoint.ccp(0.5f, 0));
	        rightToplocker.setPosition(607.0f/1024*szWin.width, 157.0f/666*szWin.height);
	        rightToplocker.setColor(ccColor3B.ccc3(0x90, 0xee, 0x90));
	        super.addChild(rightToplocker,1);
	        floatingSprites.add(rightToplocker);
	    }
	    {
	        //bottom left lock
	        leftBottomlocker=spriteFromExpansionFile("image/misc/layermaskwhite.png");
	        leftBottomlocker.setScaleX(16.0f/1024*szWin.width/leftBottomlocker.getContentSize().width);
	        leftBottomlocker.setScaleY(52.0f/666*szWin.height/leftBottomlocker.getContentSize().height);
	        leftBottomlocker.setAnchorPoint(CGPoint.ccp(0.5f, 0));
	        leftBottomlocker.setPosition(450.0f/1024*szWin.width, 52.0f/666*szWin.height);
	        leftBottomlocker.setColor(ccColor3B.ccYELLOW);
	        super.addChild(leftBottomlocker,1);
	        floatingSprites.add(leftBottomlocker);
	        
	        maxBottomScaleY=leftBottomlocker.getScaleY();
	    }
	    {
	        //bottom right lock
	        rightBottomlocker=spriteFromExpansionFile("image/misc/layermaskwhite.png");
	        rightBottomlocker.setScaleX(leftBottomlocker.getScaleX());
	        rightBottomlocker.setScaleY(maxBottomScaleY);
	        rightBottomlocker.setAnchorPoint(CGPoint.ccp(0.5f, 0));
	        rightBottomlocker.setPosition(576.0f/1024*szWin.width, 52.0f/666*szWin.height);
	        rightBottomlocker.setColor(ccColor3B.ccYELLOW);
	        super.addChild(rightBottomlocker,1);
	        floatingSprites.add(rightBottomlocker);
	    }
	    {
	        //left light
	        leftLightRed=spriteFromExpansionFile("image/activities/experience/canal_lock/light_red.png");
	        leftLightRed.setScale(20.0f/1024*szWin.width/leftLightRed.getContentSize().width);
	        leftLightRed.setPosition(305.0f/1024*szWin.width - leftLightRed.getContentSize().width * leftLightRed.getScale()/2, 360.0f/666*szWin.height);
	        super.addChild(leftLightRed, 1);
	        
	        leftLightGreen=spriteFromExpansionFile("image/activities/experience/canal_lock/light_green.png");
	        leftLightGreen.setScale(leftLightRed.getScale());
	        leftLightGreen.setPosition(leftLightRed.getPosition());
	        super.addChild(leftLightGreen,1);
	        
	    }
	    {
	        //right light
	        rightLightRed=spriteFromExpansionFile("image/activities/experience/canal_lock/light_red.png");
	        rightLightRed.setScale(20.0f/1024*szWin.width/rightLightRed.getContentSize().width);
	        rightLightRed.setPosition(732.0f/1024*szWin.width + rightLightRed.getContentSize().width * rightLightRed.getScale()/2, 474.0f/666*szWin.height);
	        rightLightRed.setScaleX(rightLightRed.getScaleX()*(-1.0f));
	        super.addChild(rightLightRed,1);
	        
	        rightLightGreen=spriteFromExpansionFile("image/activities/experience/canal_lock/light_green.png");
	        rightLightGreen.setScaleY(rightLightRed.getScaleY());
	        rightLightGreen.setScaleX(rightLightRed.getScaleX());
	        rightLightGreen.setPosition(rightLightRed.getPosition());
	        super.addChild(rightLightGreen, 1);
	        
	    }
	    {
	        //water in the canal
	        water=spriteFromExpansionFile("image/misc/layermaskwhite.png");
	        water.setScaleX(190.0f/1024*szWin.width/water.getContentSize().width);
	        water.setScaleY(0);
	        water.setAnchorPoint(CGPoint.ccp(0.5f, 0));
	        water.setPosition(512.0f/1024*szWin.width, WATER_LOWER/666*szWin.height);
	        water.setColor(ccColor3B.ccc3(0, 0, 0xcd));
	        super.addChild(water, 0);
	        floatingSprites.add(water);
	        
	        maxWaterScaleY=(WATER_HIGH-WATER_LOWER)/666*szWin.height/water.getContentSize().height;
	    }
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    for (CCNode node : floatingSprites) {
	        node.stopAllActions();
	    }
	    
	    from = 0;
	    boat_position = BOAT_POS_LEFT;
	    boat.setPosition(xLeftBoat, yBoatLower);
	    CCTexture2D boatTexture = textureFromExpansionFile("image/activities/experience/canal_lock/boat.png");
	    boat.setTexture(boatTexture);
	    
	    lock_left_up = true;
	    leftToplocker.setScaleY(maxTopScaleY);
	
	    lock_right_up = true;
	    rightToplocker.setScaleY(maxTopScaleY);
	
	    lock_water_low = true;
	    water.setScaleY(0);
	
	    canallock_left_up = true;
	    leftBottomlocker.setScaleY(maxBottomScaleY);
	    
	    canallock_right_up = true;
	    rightBottomlocker.setScaleY(maxBottomScaleY);
	    
	    animation=gamewon=false;
	    
	    this.update_lights();
	}
	
	
	public boolean ccTouchesEnded(MotionEvent event) {
	    /* If there is already an animation do nothing */
	    if (animation)
	        return true;
		CGPoint pt = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
	    
        CCSprite item=null;
        if (super.isNodeHit(boat, pt)) {
            item=boat;
        }
        else {
            //touch on any locker?
            CCSprite lockers[]={leftToplocker, rightToplocker, leftBottomlocker, rightBottomlocker};
            for (int i = 0; i < 4; ++i) {
                float w=lockers[i].getContentSize().width * lockers[i].getScaleX();
                float h=lockers[i].getContentSize().height * ((i < 2)? maxTopScaleY:maxBottomScaleY);
                CGRect rc=CGRect.make(lockers[i].getPosition().x - w, lockers[i].getPosition().y, w * 2, h);
                if (rc.contains(pt.x, pt.y)) {
                    item=lockers[i];
                    break;
                }
            }
        }
        if (item!=null) {
            if(item == leftToplocker) {
                if(lock_water_low && canallock_right_up)
                    this.toggle_lock(item);
                else {
                    item=null;
                }
            }
            else if(item == rightToplocker){
                if(!lock_water_low && canallock_left_up)
                    this.toggle_lock(item);
                else
                    item=null;
            }
            else if(item == leftBottomlocker && canallock_right_up){
                if(lock_right_up)
                    this.toggle_lock(item);
                else
                    item=null;
            }
            else if(item == rightBottomlocker && canallock_left_up) {
                if(lock_left_up)
                    this.toggle_lock(item);
                else {
                    item=null;
                }
            }
            else if(item == boat){
                this.move_boat();
            }
        }
        if (item!=null) {
            this.update_lights();
        }
        else {
            super.playSound("audio/sounds/crash.wav");
        }
        return true;
	}
	
	private void updateBoat(boolean sailing) {
		String img=sailing?"image/activities/experience/canal_lock/boat_sailing.png": "image/activities/experience/canal_lock/boat.png";
	    boat.setTexture(super.textureFromExpansionFile(img));
	}
	
	private void update_lights() {
	    boolean leftGreen=(lock_water_low && !lock_left_up);
	    boolean rightGreen=(!lock_water_low && !lock_right_up);
	    
	    leftLightRed.setVisible(!leftGreen);
	    leftLightGreen.setVisible(leftGreen);
	    rightLightRed.setVisible(!rightGreen);
	    rightLightGreen.setVisible(rightGreen);
	}
	
	/* ==================================== */
	/* Toggle the given lock */
	private void toggle_lock(CCSprite item) {
	    boolean status = true;
	    float scale=0;
	    
	    animation = true;
	    super.playSound("audio/sounds/bleep.wav");
	    
	    if(item == leftToplocker) {
	        status = lock_left_up;
	        lock_left_up = !lock_left_up;
	        scale=maxTopScaleY;
	    }
	    else if(item == rightToplocker)
	    {
	        status = lock_right_up;
	        lock_right_up = !lock_right_up;
	        scale=maxTopScaleY;
	    }
	    else if(item == leftBottomlocker)
	    {
	        status = canallock_left_up;
	        canallock_left_up = !canallock_left_up;
	        scale=maxBottomScaleY;
	    }
	    else if(item == rightBottomlocker)
	    {
	        status = canallock_right_up;
	        canallock_right_up = !canallock_right_up;
	        scale=maxBottomScaleY;
	    }
	    CCScaleTo action=null;
	    if (status) {
	        //lock to unlock
	        action=CCScaleTo.action(2.0f, item.getScaleX(), scale * 0.2f);
	    }
	    else {
	        //unlock to lock
	        action=CCScaleTo.action(2.0f, item.getScaleX(), scale);
	    }
	    CCCallFuncN doneAction = CCCallFuncN.action(this, "ydAnimationDidFinished");
	    item.runAction(CCSequence.actions(action, doneAction));
	    this.update_water();
	}
	
	/* ==================================== */
	/* Move the boat to the next possible position */
	private void move_boat() {
	    animation = true;
	    int pre_boat_position=boat_position;
	    if (boat_position == BOAT_POS_LEFT && !lock_left_up)  {
	        boat_position = BOAT_POS_MIDDLE;
	    }
	    else if(boat_position == BOAT_POS_MIDDLE && !lock_left_up) {
	        boat_position = BOAT_POS_LEFT;
	        if (from == 1) {
	            gamewon = true;
	            from = 0;
	        }
	    }
	    else if(boat_position == BOAT_POS_MIDDLE && !lock_right_up)
	    {
	        boat_position = BOAT_POS_RIGHT;
	        if (from == 0){
	            gamewon = true;
	            from = 1;
	        }
	    }
	    else if(boat_position == BOAT_POS_RIGHT && !lock_right_up){
	        boat_position = BOAT_POS_MIDDLE;
	    }
	    if (boat_position == pre_boat_position) {
	        /* No possible move */
	        super.playSound("audio/sounds/crash.wav");
	        animation=false;
	    }
	    else {
	        super.playSound("audio/sounds/eraser2.wav");
	        this.updateBoat(true);
	        float xPos=0;
	        if (boat_position == BOAT_POS_LEFT)
	            xPos=xLeftBoat;
	        else if (boat_position == BOAT_POS_MIDDLE) {
	            xPos=(xLeftBoat+xRightBoat)/2;
	        }
	        else {
	            xPos=xRightBoat;
	        }
	        CCMoveTo moveAction=CCMoveTo.action(2, CGPoint.ccp(xPos, boat.getPosition().y));
	        CCCallFuncN doneAction = CCCallFuncN.action(this, "ydAnimationDidFinished");
	        boat.runAction(CCSequence.actions(moveAction, doneAction));
	    }
	}
	
	/* ==================================== */
	/* Update the water level if necessary */
	private void update_water() {
	    boolean  status=lock_water_low;
	    if (!canallock_left_up)
	        lock_water_low=true;
	    else if (!canallock_right_up)
	        lock_water_low=false;
	    if (lock_water_low == status) {
	        /* The water level is correct */
	    }
	    else {
	    	CCScaleTo waterAction=null;
	    	CCMoveTo boatAction=null;
	        if (status) {
	            //from low to up
	            waterAction=CCScaleTo.action(2, water.getScaleX(), maxWaterScaleY);
	            boatAction=CCMoveTo.action(2, CGPoint.ccp(boat.getPosition().x, yBoatHigh));
	        }
	        else {
	            //from up to low
	            waterAction=CCScaleTo.action(2, water.getScaleX(), 0);
	            boatAction=CCMoveTo.action(2, CGPoint.ccp(boat.getPosition().x, yBoatLower));
	        }
	        CCCallFuncN doneAction = CCCallFuncN.action(this, "ydAnimationDidFinished");
	        water.runAction(CCSequence.actions(waterAction, doneAction));
	        if (boat_position == BOAT_POS_MIDDLE) {
	            boat.runAction(boatAction);
	        }
	        else {
	            boatAction=null;
	        }
	    }
	}
	public void ydAnimationDidFinished(Object _sender) {
	    animation=false;
	    if (_sender == boat) {
	        this.updateBoat(false);
	        if (gamewon) {
	            super.flashAnswerWithResult(true, false, null, null, 2);
	            gamewon=false;
	        }
	    }
	}
}
