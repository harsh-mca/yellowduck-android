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


package name.w.yellowduck.activities.algebramenu;

import name.w.yellowduck.Category;
import name.w.yellowduck.EllipseSprite;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4F;

import android.util.FloatMath;
import android.view.MotionEvent;

public class TargetScene extends name.w.yellowduck.YDActLayerBase {
	private final int kTagRing                =1001;
	private final int MAX_NUMBER_OF_TARGET =10;

    private CCSprite spriteWind;
    private CCLabel speedLabel, distanceLabel, pointsLabel, promptLabel;

    float bgScale;
    private CGPoint ptTargetCenter;

    private int target_min_wind_speed;
    private int target_max_wind_speed;
    private int number_of_arrow;
    private int target_distance;
    private int windSpeed;
    private int arrowsFired;
    private int points[]=new int[10], totalPoints;
    private float windAngle;
    
    private int number[]=new int[3], numbersEntered;
    private boolean ready;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new TargetScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=4;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    CCSprite bgSprite=super.setupBackground(activeCategory.getBg(), kBgModeFit2Center);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
	    
	    //wind indicator, 660x125 bg 800x520
	    bgScale=bgSprite.getScale();
	    float bgWidth=bgSprite.getContentSize().width*bgSprite.getScale();
	    float bgHeight=bgSprite.getContentSize().height*bgSprite.getScale();
	    float x0=bgSprite.getPosition().x-bgWidth/2;
	    float y0=bgSprite.getPosition().y-bgHeight/2;
	    spriteWind=spriteFromExpansionFile("image/activities/math/algebramenu/target/hand.png");
	    spriteWind.setPosition(x0+663.0f/800*bgSprite.getContentSize().width*bgSprite.getScale(), y0+(520.0f-123.0f)/520*bgSprite.getContentSize().height*bgSprite.getScale());
	    super.addChild(spriteWind,1);
	    
	    float xCenter=x0 + 235.0f/800*bgWidth;
	    float yCenter=y0 + (520.0f-260.0f)/520*bgHeight;
	    ptTargetCenter=CGPoint.ccp(xCenter, yCenter);
	    
	    //speed label
	    speedLabel = CCLabel.makeLabel("wind speed 3km/hr",super.sysFontName(), super.smallFontSize());
	    speedLabel.setPosition(spriteWind.getPosition().x, szWin.height/2+speedLabel.getContentSize().height);
	    super.addChild(speedLabel,1);
	    
	    //distance label
	    distanceLabel = CCLabel.makeLabel("distance to target",super.sysFontName(), super.smallFontSize());
	    distanceLabel.setColor(ccColor3B.ccBLACK);
	    distanceLabel.setPosition(x0+235.0f/800*bgSprite.getContentSize().width*bgSprite.getScale(),bgSprite.getPosition().y-bgSprite.getContentSize().height*bgSprite.getScaleY()/2+distanceLabel.getContentSize().height/2);
	    super.addChild(distanceLabel,1);
	
	    promptLabel = CCLabel.makeLabel("12,12", super.sysFontName(), super.mediumFontSize());
	    promptLabel.setPosition(szWin.width/2, szWin.height-topOverhead()-promptLabel.getContentSize().height/2);
	    super.addChild(promptLabel,1);
	    
	    //the numerical keyboard
	    float keyWidth=super.setupVirtualKeyboard("1234567890", null);
	    //points label
	    pointsLabel = CCLabel.makeLabel("Points=", super.sysFontName(), super.mediumFontSize());
	    pointsLabel.setPosition(szWin.width * 3 / 4, bottomOverhead()+pointsLabel.getContentSize().height + keyWidth);
	    super.addChild(pointsLabel,1);

	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    for (int i = floatingSprites.size()-1; i >= 0; --i) {
	    	CCNode node = floatingSprites.get(i);
	    	if (node.getTag() == kTagKeyboard)
	    		node.setVisible(false);
	    	else {
	    		node.removeFromParentAndCleanup(true);
	    		floatingSprites.remove(i);
	    	}
	    }
	
	    pointsLabel.setVisible(false);
	    distanceLabel.setVisible(true);
	    
	    int target_width_value[]=new int[MAX_NUMBER_OF_TARGET*2];
	    int idx=0;
	    switch (mLevel) {
	        case 1:
	            number_of_arrow=2;
	            target_distance=100;
	            target_min_wind_speed=2;
	            target_max_wind_speed=5;
	            target_width_value[idx++]=40;target_width_value[idx++]=5;
	            target_width_value[idx++]=80;target_width_value[idx++]=3;
	            target_width_value[idx++]=150;target_width_value[idx++]=1;
	            target_width_value[idx++]=0;
	            break;
	        case 2:
	            number_of_arrow=3;
	            target_distance=150;
	            target_min_wind_speed=2;
	            target_max_wind_speed=7;
	            target_width_value[idx++]=30;target_width_value[idx++]=10;
	            target_width_value[idx++]=50;target_width_value[idx++]=5;
	            target_width_value[idx++]=150;target_width_value[idx++]=1;
	            target_width_value[idx++]=0;
	            break;
	        case 3:
	            number_of_arrow=5;
	            target_distance=200;
	            target_min_wind_speed=4;
	            target_max_wind_speed=9;
	            target_width_value[idx++]=20;target_width_value[idx++]=10;
	            target_width_value[idx++]=40;target_width_value[idx++]=5;
	            target_width_value[idx++]=60;target_width_value[idx++]=3;
	            target_width_value[idx++]=150;target_width_value[idx++]=1;
	            target_width_value[idx++]=0;
	            break;
	        case 4:
	            number_of_arrow=5;
	            target_distance=200;
	            target_min_wind_speed=5;
	            target_max_wind_speed=10;
	            target_width_value[idx++]=15;target_width_value[idx++]=100;
	            target_width_value[idx++]=35;target_width_value[idx++]=50;
	            target_width_value[idx++]=55;target_width_value[idx++]=10;
	            target_width_value[idx++]=75;target_width_value[idx++]=5;
	            target_width_value[idx++]=150;target_width_value[idx++]=1;
	            target_width_value[idx++]=0;
	            break;
	    }
	    arrowsFired=0; totalPoints=0;
	    this.windDirection();
	    
	    //target distance
	    distanceLabel.setString(String.format(localizedString("prompt_target_distance"), target_distance));
	
	    int target_colors[] = {
	        0xAA0000FF, 0x00AA00FF, 0x0000AAFF,
	        0xAAAA00FF, 0x00AAAAFF, 0xAA00AAFF,
	        0xAA0000FF, 0x00AA00FF, 0x0000AAFF,
	        0xAA0000AF
	    };
	    //235x260 bg 800x520
	    int zIdx=10;
	    for (int i = 0;;i+=2) {
	        if (target_width_value[i] <= 0)
	            break;
	        target_width_value[i] *=preferredContentScale(false);
	        int _clr=target_colors[i/2];
	        ccColor4F clr=new ccColor4F(1.0f*((_clr>>24) & 0xff)/255, 1.0f*((_clr>>16) & 0xff)/255, 1.0f*((_clr>>8) & 0xff)/255, 1.0f);
	        float radius=target_width_value[i]*bgScale/2;
	        EllipseSprite ring=new EllipseSprite(ptTargetCenter,radius,radius);
	        ring.setClr(clr);
	        ring.setTag(kTagRing);
	        super.addChild(ring, zIdx--);
	        floatingSprites.add(ring);
	
	        CCLabel label = CCLabel.makeLabel(""+target_width_value[i+1], super.sysFontName(), 6*preferredContentScale(false));
	        label.setPosition(ptTargetCenter.x, ptTargetCenter.y-radius+label.getContentSize().height/2);
	        label.setTag(target_width_value[i+1]); //points
	        label.setUserData(Float.valueOf(radius));
	        super.addChild(label,11);
	        floatingSprites.add(label);
	
	        ring.setUserData(label);
	    }
	    
	    this.updatePrompt();
	    
	    ready=true;
	}
	public boolean ccTouchesEnded(MotionEvent event) {
		if (!ready)
			return true;
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
	
        boolean good2fire=false;
        for (CCNode node : floatingSprites) {
            if (node.getTag() == kTagRing) {
                CCNode label=(CCNode)node.getUserData();
                Float radius=(Float)label.getUserData();
                //int points=label.tag;
                if (distanceFrom(p1, ptTargetCenter) <= radius) {
                    good2fire=true;
                    break;
                }
            }
        }
        if (good2fire) {
            //the dart
            CCSprite sprite=spriteFromExpansionFile("image/activities/math/algebramenu/target/dart.png");
            sprite.setColor(ccColor3B.ccc3(0xff, 0xc0, 0xcb));
            sprite.setPosition(p1);
            super.addChild(sprite,20);
            floatingSprites.add(sprite);
            float zoom=1.0f * target_distance/100*4*preferredContentScale(false);
            float dstX=p1.x + windSpeed * FloatMath.cos(windAngle-3.14f/2) * zoom;
            float dstY=p1.y - windSpeed * FloatMath.sin(windAngle-3.14f/2) * zoom;
            float tm=1.0f * target_distance/100; //seconds

            CCMoveTo moveAction = CCMoveTo.action(tm, CGPoint.ccp(dstX, dstY));
            CCCallFuncN moveDone = CCCallFuncN.action(this,  "arrowLanded");
            sprite.runAction(CCSequence.actions(moveAction, moveDone));
            CCScaleTo scaleAction = CCScaleTo.action(tm,  2.0f*preferredContentScale(false)/sprite.getContentSize().width);
            sprite.runAction(scaleAction);

            super.playSound("audio/sounds/brick.wav");

            ready=false;
        }
        return true;
	}

	public void arrowLanded(Object _sender) {
	    playSound("audio/sounds/line_end.wav");
	    CCNode sender=(CCNode)_sender;
	    int point=0;
	    for (CCNode node : floatingSprites) {
	        if (node.getTag() == kTagRing) {
	            CCNode label=(CCNode)node.getUserData();
	            Float radius=(Float)label.getUserData();
	            int pt=label.getTag();
	            if (distanceFrom(sender.getPosition(), ptTargetCenter) <= radius.floatValue()) {
	                if (pt > point)
	                    point=pt;
	            }
	        }
	    }
	    points[arrowsFired++]=point;
	    totalPoints += point;
	    this.updatePrompt();
	
	    if (arrowsFired >= number_of_arrow) {
	        for (CCNode node : floatingSprites) {
	            if (node.getTag()==kTagKeyboard) {
	                node.setVisible(true);
	            }
	        }
	        pointsLabel.setVisible(true);
	        distanceLabel.setVisible(true);
	        numbersEntered=0;
	        this.displayHint();
	    }
	    else {
	        this.windDirection();
	        ready=true;
	    }
	}
	public void letterTouched(Object _sender) {
	    super.playSound("audio/sounds/click.wav");
	    
	    CCNode sender=(CCNode)_sender;
	    String theKey=(String)sender.getUserData();
	    if (numbersEntered < number.length) {
	        number[numbersEntered++]=Integer.parseInt(theKey);
	        this.displayHint();
	        int total=0;
	        for (int i = 0; i < numbersEntered; ++i)
	            total=total * 10 + number[i];
	        if (total == totalPoints) {
	            numbersEntered=100;//prevent user from entering another number
	            super.flashAnswerWithResult(true, true, null, null, 2);
	        }
	        else {
	            int lettersRequired=1;
	            if (totalPoints >= 10) {
	                lettersRequired=2;
	                if (totalPoints >= 100)
	                    lettersRequired=3;
	            }
	            if (numbersEntered >= lettersRequired) {
	                //wrong answer
	                numbersEntered=0;
	                super.performSelector("displayHint", 0.5f);
	            }
	        }
	    }
	}
	private void windDirection() {
	    windSpeed=randomBetween(target_min_wind_speed,target_max_wind_speed);
	    windAngle=super.nextInt(60) * 3.14f/30;
	    spriteWind.setRotation(windAngle * 180/3.14f);
	
	    spriteWind.setScale(1.0f*windSpeed*preferredContentScale(true)/8); //based on above settings, the max speed is 10
	    speedLabel.setString(String.format(localizedString("prompt_wind_speed"), windSpeed));
	}
	
	private void updatePrompt() {
	    String str=" ";
	    for (int i = 0; i < arrowsFired; ++i) {
	        str+="  " + points[i];
	    }
	    promptLabel.setString(str);
	}
	
	private void displayHint() {
	    String str=localizedString("prompt_target_total_points");
	    if (numbersEntered > 0) {
	        str+=""+number[0];
	    }
	    else {
	        str+="?";
	    }
	    if (totalPoints >= 10) {
	        if (numbersEntered > 1) {
	            str+=""+number[1];
	        }
	        else {
	            str+="?";
	        }
	        if (totalPoints >= 100) {
	            if (numbersEntered > 2) {
	                str+=""+number[2];
	            }
	            else {
	                str+="?";
	            }
	        }
	    }
	    pointsLabel.setString(str);
	}
}
