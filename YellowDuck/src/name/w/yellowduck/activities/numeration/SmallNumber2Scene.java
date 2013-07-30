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
import name.w.yellowduck.EllipseSprite;
import name.w.yellowduck.Schema;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4F;

import android.graphics.Bitmap;

public class SmallNumber2Scene extends  name.w.yellowduck.YDActLayerBase {
	private final int kTagErrorIndicator  =100;
	
	private final int kMaxErrorsAllowed   =3;

    private CCLabel sublevelLabel;
    
    private float tmElapsed;
    private float speed, fallSpeed, imageZoom;
    private boolean stop;
    private int failures;

	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new SmallNumber2Scene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	public void onEnter() {
		super.onEnter();
	    mMaxLevel=9;
	    mMaxSublevel=10;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.shufflePlayBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
    
	    
	    //the numerical keyboard
	    super.setupVirtualKeyboard("1234567890", null);
	    //sublevel
	    sublevelLabel = CCLabel.makeLabel("10/10", super.sysFontName(), super.mediumFontSize());
	    sublevelLabel.setColor(ccColor3B.ccBLACK);
	    sublevelLabel.setPosition(szWin.width-sublevelLabel.getContentSize().width/2-4, bottomOverhead()+sublevelLabel.getContentSize().height/2);
	    super.addChild(sublevelLabel,3);
	    
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
		
	    fallSpeed=30.0f - mLevel;
	    speed=fallSpeed/6;
	    imageZoom=0.4f+(0.5f/mLevel);
	    
	    mSublevel=0;
	    sublevelLabel.setString(String.format("%d/%d", mSublevel+1, mMaxSublevel));
	    stop=true; failures=0;
	    tmElapsed=speed-1;
	
	    //ready menu item
	    String text=localizedString("label_am_ready");
	    Bitmap img=super.createMultipleLineLabel(text, super.sysFontName(), super.largeFontSize(), 0, Schema.kPopUpFontClr, Schema.kPopUpBgClr);
	    Bitmap imgSel=super.buttonize(img);
	    
	    CCSprite sprite=CCSprite.sprite(img, "rdy");
	    CCSprite spriteSelected=CCSprite.sprite(imgSel, "rdysel");
	    CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected,this,"ready");
	    CCMenu menu = CCMenu.menu(menuitem);
	    menu.setPosition(szWin.width/2, szWin.height*0.6f);
	    super.addChild(menu,1);
	    floatingSprites.add(menu);
	    
	    
	    int radius=10;
	    float xPos=2+radius;
	    float yPos=szWin.height-topOverhead() - radius*2;
	    for (int i = 0; i < kMaxErrorsAllowed; ++i) {
	        EllipseSprite ellipse=new EllipseSprite(CGPoint.ccp(xPos, yPos), radius, radius);
	        ellipse.setClr(new ccColor4F(0, 0, 1, 1));
	        super.addChild(ellipse,1);
	        ellipse.setTag(kTagErrorIndicator+i);
	        floatingSprites.add(ellipse);
	
	        xPos += radius * 2 + 2;
	    }
	    
	}
	
	public void letterTouched(Object _sender) {
	    if (stop)
	        return;
	    CCNode sender=(CCNode)_sender;
	    String theKey=(String)sender.getUserData();
	    int number=Integer.parseInt(theKey);
	    int total=floatingSprites.size();
	    int removed=0, dice=0;
	    for (int i = total - 1; i >= 0; --i) {
	        CCNode one=floatingSprites.get(i);
	        if (one.getTag() == number) {
	            one.removeFromParentAndCleanup(true);
	            floatingSprites.remove(i);
	            ++removed;
	        }
	        else if (one.getUserData()!=null && "dice".equals(one.getUserData())) {
	            ++dice;
	        }
	    }
	    if (removed > 0) {
	        playSound("audio/sounds/flip.wav");
	        if (++mSublevel >= mMaxSublevel) {
	            stop=true;
	            super.flashAnswerWithResult(true, true, null, null, 2);
	        }
	        else {
	            sublevelLabel.setString(String.format("%d/%d", mSublevel+1, mMaxSublevel));
	            if (floatingSprites.size() <= 0) {
	                tmElapsed+=speed;
	            }
	        }
	        if (dice <= 0) {
	            tmElapsed=speed; //display next dice now
	        }
	    }
	    else {
	        playSound("audio/sounds/ding.wav");
	        for (CCNode node : floatingSprites) {
	            if (node.getTag() == kTagErrorIndicator + failures) {
	                EllipseSprite sprite=(EllipseSprite)node;
	                sprite.setClr(new ccColor4F(1, 0, 0, 1));
	                break;
	            }
	        }
	        if (++failures >= kMaxErrorsAllowed) {
	            stop=true;
	            for (CCNode node : floatingSprites) {
	                node.stopAllActions();
	            }
	            super.flashAnswerWithResult(false, false, null, null, 2);
	            super.performSelector("__restart", 2);
	        }
	    }
	}
	public void __restart() {
	    this.initGame(false,  null);
	}
	
	public void update(float dt) {
	    if (stop)
	        return;
	    tmElapsed += dt;
	    if (tmElapsed < speed)
	        return;
	    
	    tmElapsed=0;
	    
	    int numbers[]=new int[2];
	    numbers[0]=super.nextInt(6);
	    numbers[1]=super.nextInt(9-numbers[0]);
	    CGPoint pt=CGPoint.ccp(0,0);
	    for (int i = 0; i < 2; ++i) {
	        CCSprite sprite=spriteFromExpansionFile("image/activities/math/numeration/smallnumbers/dice" +numbers[i] + ".png");
	        sprite.setScale(szWin.width/8/sprite.getContentSize().width * imageZoom);
	        if (i <= 0) {
	            float xLeft=sprite.getContentSize().width*sprite.getScale()/2;
	            float xRight=szWin.width-sprite.getContentSize().width*sprite.getScale()*2; //leave some space for next dice
	            pt=CGPoint.ccp(randomBetween((int)xLeft, (int)xRight), szWin.height-sprite.getContentSize().height*sprite.getScale());
	        }
	        else {
	            pt.x += sprite.getContentSize().width*sprite.getScale();
	        }
	        sprite.setPosition(pt);
	        sprite.setTag(numbers[0]+numbers[1]);
	        sprite.setUserData("dice");
	        super.addChild(sprite,3);
	        floatingSprites.add(sprite);
	        
	        CCMoveTo moveAction = CCMoveTo.action(fallSpeed, CGPoint.ccp(sprite.getPosition().x, 0-sprite.getContentSize().height/2));
	        CCCallFuncN moveDone = CCCallFuncN.action(this, "removeMe");
	        sprite.runAction(CCSequence.actions(moveAction, moveDone));
	    }
	    playSound("audio/sounds/level.wav");
	}
	
	public void removeMe(Object _sender){
		CCNode sender=(CCNode)_sender;
	    sender.removeFromParentAndCleanup(true);
	    floatingSprites.remove(sender);
	}
	
	public void ready(Object _sender) {
	    if (!stop)
	        return;
	    CCNode sender=(CCNode)_sender;
	    stop=false; failures=0;
	    sender.getParent().removeFromParentAndCleanup(true);
	    floatingSprites.remove(sender.getParent());
	}
}
