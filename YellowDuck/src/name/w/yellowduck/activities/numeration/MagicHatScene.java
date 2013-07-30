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
import name.w.yellowduck.LineSprite;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateTo;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor3B;

import android.view.MotionEvent;

public class MagicHatScene extends name.w.yellowduck.YDActLayerBase {

	private final int OP_MINUS            =0;
	private final int OP_PLUS             =1;
	
	private final int kTagSelected        =100;

    private CCSprite spriteHat, spriteHatPoint;
    int mode;
    
    private int firstNumbers[]=new int[3];
    private int secondNumbers[]=new int[3];
    private int resultNumbers[]=new int[3];
    private int answerNumbers[]=new int[3];
    
    private int starSize;
    private boolean  magicFinished;

	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new MagicHatScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
		super.onEnter();
	    mMaxLevel=9;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    mode="-".equals(activeCategory.getSettings()) ? OP_MINUS:OP_PLUS;
	    super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.shufflePlayBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
		    
	    spriteHat=spriteFromExpansionFile("image/activities/math/numeration/magic_hat_minus/hat.png");
	    spriteHat.setScale(preferredContentScale(true));
	    spriteHat.setAnchorPoint(0,0);
	    spriteHat.setPosition(310.f/800*szWin.width-spriteHat.getContentSize().width*spriteHat.getScale()-10, 176.0f/520*szWin.height);
	    super.addChild(spriteHat,5);
	    //the bottom line
	    LineSprite line=new LineSprite(CGPoint.ccp(spriteHat.getPosition().x, spriteHat.getPosition().y-4),CGPoint.ccp(spriteHat.getPosition().x+spriteHat.getContentSize().width*spriteHat.getScale(), spriteHat.getPosition().y-4));
	    super.addChild(line,1);
	    
	    spriteHatPoint=spriteFromExpansionFile("image/activities/math/numeration/magic_hat_minus/hat-point.png");
	    spriteHatPoint.setScale(spriteHat.getScale());
	    spriteHatPoint.setAnchorPoint(spriteHat.getAnchorPoint());
	    spriteHatPoint.setPosition(spriteHat.getPosition());
	    spriteHatPoint.setVisible(false);
	    super.addChild(spriteHatPoint, 6);
	
	    magicFinished=true;
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
	
	    spriteHatPoint.setVisible(false);
	    spriteHat.stopAllActions();
	    spriteHat.setRotation(0);
	    
	    for (int j = 0; j < 3; ++j) {
	        firstNumbers[j]=0;
	        secondNumbers[j]=0;
	        resultNumbers[j]=0;
	        
	        answerNumbers[j]=0;
	    }
	    // Description of the 9 levels for substraction
	    // Level 1 : one list (yellow stars), from 2 to 4 stars in frame 1
	    // Level 2 : one list (yellow stars), from 2 to 7 stars in frame 1
	    // Level 3 : one list (yellow stars), from 2 to 10 stars in frame 1
	    // Level 4 : two lists (yellow and green stars), from 2 to 4 stars in frame 1
	    // Level 5 : two lists (yellow and green stars), from 2 to 7 stars in frame 1
	    // Level 6 : two lists (yellow and green stars), from 2 to 10 stars in frame 1
	    // Level 7 : three lists (yellow, green and blue stars), from 2 to 4 stars in frame 1
	    // Level 8 : three lists (yellow, green and blue stars), from 2 to 7 stars in frame 1
	    // Level 9 : three lists (yellow, green and blue stars), from 2 to 10 stars in frame 1
	    //
	    // Description of the 9 levels for addition
	    // Level 1 : one list (yellow stars), from 2 to 4 for the total
	    // Level 2 : one list (yellow stars), from 2 to 7 for the total
	    // Level 3 : one list (yellow stars), from 2 to 10 for the total
	    // Level 4 : two lists (yellow and green stars), from 2 to 4 for the total
	    // Level 5 : two lists (yellow and green stars), from 2 to 7 for the total
	    // Level 6 : two lists (yellow and green stars), from 2 to 10 for the total
	    // Level 7 : three lists (yellow, green and blue stars), from 2 to 4 for the total
	    // Level 8 : three lists (yellow, green and blue stars), from 2 to 7 for the total
	    // Level 9 : three lists (yellow, green and blue stars), from 2 to 10 for the total
	    int lines=(mLevel+2)/3;
	    int ceiling=(mLevel%3);
	    if (ceiling == 0)
	        ceiling=3;
	    ceiling = ceiling * 3 + 1;
	    if (mode == OP_MINUS) {
	        for (int i = 0; i < lines; ++i) {
	            firstNumbers[i]=randomBetween(2, ceiling);
	            secondNumbers[i]=randomBetween(1, firstNumbers[i]-1);
	            resultNumbers[i]=firstNumbers[i]-secondNumbers[i];
	        }
	    }
	    else {
	        for (int i = 0; i < lines; ++i) {
	            int total=randomBetween(2,ceiling);
	            firstNumbers[i]=randomBetween(1, total-1);
	            secondNumbers[i]=total-firstNumbers[i];
	            resultNumbers[i]=total;
	        }
	    }
	
	    float xPos=448.0f/800*szWin.width, yPos=438.0f/520*szWin.height;
	    float xRoom=320.0f/800*szWin.width / 10;
	
	    starSize=(int)(xRoom * 0.8f);
	    String img=renderSVG2Img("image/activities/math/numeration/magic_hat_minus/star-clear.svg", starSize, starSize);
	    String starDn=super.buttonize(img);
	    CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(img);
	    CCTexture2D texture2=CCTextureCache.sharedTextureCache().addImageExternal(starDn);
	    //first numbers
	    for (int i = 0; i < 3; ++i) {
	        if (firstNumbers[i] <= 0)
	            continue;
	        for (int j = 0; j < 10; ++j) {
	            CCSprite sprite=CCSprite.sprite(texture1);
	            if (j < firstNumbers[i]) {
	                sprite.setColor(getColor(i));
	                sprite.setTag(kTagSelected+i*10+j);
	            }
	            sprite.setPosition(xPos+j*xRoom, yPos);
	            super.addChild(sprite,1 );
	            floatingSprites.add(sprite);
	        }
	        yPos -= xRoom;
	    }
	    //second number
	    yPos=310.f/520*szWin.height;
	    for (int i = 0; i < 3; ++i) {
	        if (secondNumbers[i] <= 0)
	            continue;
	        for (int j = 0; j < 10; ++j) {
	            CCSprite sprite=CCSprite.sprite(texture1);
	            sprite.setPosition(xPos+j*xRoom, yPos);
	            if (j < secondNumbers[i]) {
	                if (mode == OP_PLUS)
	                    sprite.setColor(this.getColor(i));
	                sprite.setTag(kTagSelected*2+i*10+j);
	            }
	            super.addChild(sprite, 1);
	            floatingSprites.add(sprite);
	        }
	        yPos -= xRoom;
	    }
	    //result
	    yPos=130.f/520*szWin.height;
	    for (int i = 0; i < 3; ++i) {
	        if (resultNumbers[i] <= 0)
	            continue;
	        for (int j = 0; j < 10; ++j) {
	            CCSprite sprite=CCSprite.sprite(texture1);
	            CCSprite spriteDn=CCSprite.sprite(texture2);
	
	            CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite,  spriteDn, this, "starTouched");
	            menuitem.setPosition(xPos + j*xRoom, yPos);
	            menuitem.setTag(kTagSelected*3+i*10+j);
	            CCMenu menu = CCMenu.menu(menuitem);
	            menu.setPosition(0,0);
	            super.addChild(menu, 1);
	            floatingSprites.add(menu);
	        }
	        yPos -= xRoom;
	    }
	    //flash the start
	    CCSprite sparkle=spriteFromExpansionFile("image/misc/star.png");
	    sparkle.setPosition(spriteHat.getPosition().x +spriteHat.getContentSize().width*spriteHat.getScale()/2, spriteHat.getPosition().y +spriteHat.getContentSize().height*spriteHat.getScale()/2);
	    sparkle.setScale(0.2f);
	    super.addChild(sparkle,7);
	    floatingSprites.add(sparkle);
	    CCScaleTo scaleUpAction=CCScaleTo.action(0.5f, 2);
	    CCScaleTo scaleDownAction=CCScaleTo.action(0.5f, 0.2f);
	    CCCallFuncN actionDone = CCCallFuncN.action(this, "removeMe");
	    sparkle.runAction(CCSequence.actions(scaleUpAction, scaleDownAction, actionDone));
	    
	    magicFinished=false;
	}
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
		//Its anchor point is not at the center
		CGRect rc=CGRect.make(spriteHat.getPosition().x, spriteHat.getPosition().y, spriteHat.getContentSize().width, spriteHat.getContentSize().height);
		if (rc.contains(p1.x,  p1.y)) {
            this.startMagic();
	    }
		return true;
	}

	private void startMagic() {
	    if (magicFinished) {
	        playSound("audio/sounds/ding.wav");
	    }
	    else {
	        magicFinished=true;
	        playSound("audio/sounds/level.wav");
	
	        float speed=4.0f; //seconds
	        CCRotateTo rotate1Action = CCRotateTo.action(speed/2, -40.0f);
	        CCRotateTo rotate2Action = CCRotateTo.action(speed/2, 0);
	        CCCallFuncN moveDone = CCCallFuncN.action(this, "magicDone");
	        spriteHat.runAction(CCSequence.actions(rotate1Action,rotate2Action, moveDone));
	
	        //We will add new sprites to floatingSprites
	        int total=floatingSprites.size();
	        String star=super.renderSVG2Img("image/activities/math/numeration/magic_hat_minus/star-clear.svg", starSize, starSize);
	        CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(star);
	        for (int i = 0; i < total; ++i) {
	            CCNode node=floatingSprites.get(i);
	            //first numbers
	            if (node.getTag() >= kTagSelected && node.getTag() < kTagSelected * 2) {
	                int row=(node.getTag()-kTagSelected)/10;
	                int col=(node.getTag()-kTagSelected)%10;
	                CCSprite org=(CCSprite)node;
	                //duplicate this sprite
	                CCSprite sprite=CCSprite.sprite(texture);
	                sprite.setColor(org.getColor());
	                sprite.setPosition(org.getPosition());
	                super.addChild(sprite, 1);
	                floatingSprites.add(sprite);
	
	                float shift=randomBetween(1, 10)-5;
	                CCMoveTo move1Action = CCMoveTo.action(speed/2, CGPoint.ccp(spriteHat.getPosition().x+spriteHat.getContentSize().width/2+shift, spriteHat.getPosition().y+sprite.getContentSize().height));
	                if (mode == OP_MINUS) {
	                    if (col < secondNumbers[row]) {
	                        //move out, find its target in the second numbers row
	                        CGPoint pt=CGPoint.ccp(0,0);
	                        for (int j = 0; j < total; ++j) {
	                            CCNode _node=floatingSprites.get(j);
	                            //first numbers
	                            if (_node.getTag() >= kTagSelected*2 && _node.getTag() < kTagSelected * 3) {
	                                int _row=(_node.getTag()-kTagSelected*2)/10;
	                                int _col=(_node.getTag()-kTagSelected*2)%10;
	                                if (_row == row && _col == col) {
	                                    pt=_node.getPosition();
	                                    break;
	                                }
	                            }
	                        }
	                        CCMoveTo move2Action = CCMoveTo.action(speed/2, pt);
	                        sprite.runAction(CCSequence.actions(move1Action, move2Action));
	                    }
	                    else {
	                        //stay inside the hat
	                        sprite.runAction(move1Action);
	                    }
	                }
	                else {
	                    //plus, stay inside the hat
	                    sprite.runAction(move1Action);
	                }
	            }
	            else if (mode == OP_PLUS && node.getTag() >= kTagSelected*2 && node.getTag() < kTagSelected * 3) {
	                CCSprite org=(CCSprite)node;
	                //duplicate this sprite
	                CCSprite sprite=CCSprite.sprite(texture);
	                sprite.setColor(org.getColor());
	                sprite.setPosition(org.getPosition());
	                super.addChild(sprite,1);
	                floatingSprites.add(sprite);
	                
	                float shift=randomBetween(1, 10)-5;
	                CCMoveTo moveAction = CCMoveTo.action(speed/2, CGPoint.ccp(spriteHat.getPosition().x+spriteHat.getContentSize().width/2+shift, spriteHat.getPosition().y+sprite.getContentSize().height));
	                sprite.runAction(moveAction);
	            }
	        }
	    }
	}

	public void magicDone(Object _sender) {
	    spriteHatPoint.setVisible(true);
	}
	
	public void starTouched(Object _sender) {
	    if (!magicFinished)
	        return;
	    CCMenuItemSprite menuitem=(CCMenuItemSprite)_sender;
	    
	    int row=(menuitem.getTag() - kTagSelected*3) / 10;
	    if (menuitem.getUserData()==null) {
	        menuitem.setUserData("selected");
	        menuitem.setColor(getColor(row));
	        ++answerNumbers[row];
	    }
	    else {
	        menuitem.setUserData(null);
	        menuitem.setColor(ccColor3B.ccWHITE);
	        --answerNumbers[row];
	    }
	    boolean correct=true;
	    for (int j = 0; j < 3; ++j) {
	        if (answerNumbers[j] != resultNumbers[j]) {
	            correct=false;
	            break;
	        }
	    }
	    if (correct)
	        super.flashAnswerWithResult(true,  true,  null,  null,  2);
	}

	private ccColor3B getColor(int row) {
	    if (row == 0)
	        return ccColor3B.ccYELLOW;
	    else if (row == 1)
	        return ccColor3B.ccBLUE;
	    return ccColor3B.ccGREEN;
	}
	
	public void removeMe(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    sender.removeFromParentAndCleanup(true);
	    floatingSprites.remove(sender);
	}
}
