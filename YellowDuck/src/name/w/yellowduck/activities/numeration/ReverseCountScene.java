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
import org.cocos2d.menus.CCMenuItemToggle;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor4F;

public class ReverseCountScene extends name.w.yellowduck.YDActLayerBase {
	
	private final int kTagIce                     =100;
	private final int kTagErrorIndicator          =10;
	
	private final int kMaxErrorsAllowed           =3;
	
    private int number_of_item_x, number_of_item_y, number_of_dices, max_dice_number, number_of_fish;
    private int number_of_item, failures;
    
    private int diceValue[]=new int[3];
    private float cxIce, cyIce, diceSize;
    
    private CCSprite tuxSprite, fishSprite;
    private int tuxPosSequence, tuxDstPosSequence, fishPosSequence;
    private boolean busy;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ReverseCountScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
		super.onEnter();
	    mMaxLevel=7;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.shufflePlayBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
		    
	    tuxSprite=spriteFromExpansionFile("image/activities/math/numeration/reversecount/tux_top_south.png");
	    tuxSprite.setPosition(0,0);
	    super.addChild(tuxSprite, 6);
	
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
	    /* Select level difficulty */
	    switch(mLevel)
	    {
	        case 1:
	            number_of_item_x = 5;
	            number_of_item_y = 5;
	            number_of_dices = 1;
	            max_dice_number = 3;
	            number_of_fish = 3;
	            break;
	        case 2:
	            number_of_item_x = 5;
	            number_of_item_y = 5;
	            number_of_dices = 1;
	            max_dice_number = 6;
	            number_of_fish = 6;
	            break;
	        case 3:
	            number_of_item_x = 6;
	            number_of_item_y = 6;
	            number_of_dices = 1;
	            max_dice_number = 9;
	            number_of_fish = 6;
	            break;
	        case 4:
	            number_of_item_x = 8;
	            number_of_item_y = 6;
	            number_of_dices = 1;
	            max_dice_number = 3;
	            number_of_fish = 6;
	            break;
	        case 5:
	            number_of_item_x = 8;
	            number_of_item_y = 6;
	            number_of_dices = 2;
	            max_dice_number = 6;
	            number_of_fish = 10;
	            break;
	        case 6:
	            number_of_item_x = 8;
	            number_of_item_y = 8;
	            number_of_dices = 2;
	            max_dice_number = 9;
	            number_of_fish = 10;
	            break;
	        default:
	            number_of_item_x = 10;
	            number_of_item_y = 10;
	            number_of_dices = 3;
	            max_dice_number = 9;
	            number_of_fish = 10;
	            break;
	    }
	    number_of_item = number_of_item_x * 2 + (number_of_item_y - 2) * 2;
	    
	    cxIce=szWin.width / number_of_item_x;
	    cyIce=(szWin.height-topOverhead() - bottomOverhead())/number_of_item_y;
	    
	    tuxSprite.stopAllActions();
	    tuxSprite.setScale(cxIce/tuxSprite.getContentSize().width);
	    tuxSprite.setRotation(-90);
	    int sequence=0;
	    //bottom
	    for (int i = 0; i < number_of_item_x; ++i) {
	        CCSprite sprite=spriteFromExpansionFile("image/activities/math/numeration/reversecount/iceblock.png");
	        sprite.setScaleX(cxIce/sprite.getContentSize().width);
	        sprite.setScaleY(cyIce/sprite.getContentSize().height);
	        sprite.setPosition(i * cxIce + cxIce/2, bottomOverhead() + cyIce/2);
	        sprite.setTag(kTagIce+sequence++);
	        super.addChild(sprite,1);
	        floatingSprites.add(sprite);
	    }
	    //right
	    for (int i = 0; i < number_of_item_y - 2; ++i) {
	        CCSprite sprite=spriteFromExpansionFile("image/activities/math/numeration/reversecount/iceblock.png");
	        sprite.setScaleX(cxIce/sprite.getContentSize().width);
	        sprite.setScaleY(cyIce/sprite.getContentSize().height);
	        sprite.setPosition(szWin.width-cxIce/2,bottomOverhead()+ (i + 1) * cyIce + cyIce/2);
	        sprite.setTag(kTagIce+sequence++);
	        super.addChild(sprite,1);
	        floatingSprites.add(sprite);
	    }
	    //top ice blocks
	    for (int i = 0; i < number_of_item_x; ++i) {
	        CCSprite sprite=spriteFromExpansionFile("image/activities/math/numeration/reversecount/iceblock.png");
	        sprite.setScaleX(cxIce/sprite.getContentSize().width);
	        sprite.setScaleY(cyIce/sprite.getContentSize().height);
	        sprite.setPosition(szWin.width-i * cxIce - cxIce/2, szWin.height-topOverhead() - cyIce/2);
	        sprite.setTag(kTagIce+sequence++);
	        super.addChild(sprite,1);
	        floatingSprites.add(sprite);
	    }
	    tuxPosSequence=sequence-1;
	    tuxSprite.setPosition(sequence2ScreenPos(tuxPosSequence));
	    
	    //left
	    for (int i = 0; i < number_of_item_y - 2; ++i) {
	        CCSprite sprite=spriteFromExpansionFile("image/activities/math/numeration/reversecount/iceblock.png");
	        sprite.setScaleX(cxIce/sprite.getContentSize().width);
	        sprite.setScaleY(cyIce/sprite.getContentSize().height);
	        sprite.setPosition(cxIce/2, szWin.height -topOverhead() -  (i+1) * cyIce - cyIce/2);
	        sprite.setTag(kTagIce+sequence++);
	        super.addChild(sprite,1);
	        floatingSprites.add(sprite);
	    }
	    //dice area
	    diceSize=szWin.width/8;
	    CCSprite sprite=spriteFromExpansionFile("image/activities/math/numeration/reversecount/dice_area.png");
	    sprite.setScale(diceSize*3.0f/sprite.getContentSize().width);
	    sprite.setPosition(szWin.width-cxIce-sprite.getContentSize().width*sprite.getScale()/2, szWin.height-topOverhead()-cyIce-sprite.getContentSize().height*sprite.getScale()/2);
	    super.addChild(sprite, 2);
	    floatingSprites.add(sprite);
	    //the ok button
	    String ok=renderSkinSVG2Button(Schema.kSvgButtonOk, buttonSize()*2);
	    String okTouched=buttonize(ok);
	    CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(ok);
	    CCTexture2D texture2=CCTextureCache.sharedTextureCache().addImageExternal(okTouched);
	    
	    CCSprite spriteOk=CCSprite.sprite(texture1);
	    CCSprite spriteSelected=CCSprite.sprite(texture2);
	    CCMenuItemSprite confirm=CCMenuItemImage.item(spriteOk, spriteSelected, this, "ok");
	    confirm.setPosition(sprite.getPosition().x-sprite.getContentSize().width*sprite.getScale()/2-spriteOk.getContentSize().width/2, sprite.getPosition().y);
	    confirm.setTag(Schema.kSvgButtonOk);
	    CCMenu menu = CCMenu.menu(confirm);
	    menu.setPosition(0,0);
	    super.addChild(menu, 10);
	    floatingSprites.add(menu);
	    //the dices
	    max_dice_number=(max_dice_number+2)/3*3;
	    for (int col=0; col < number_of_dices; ++col) {
	        diceValue[col]=0;
	        CCMenuItemSprite choices[]=new CCMenuItemSprite[10];
	        for (int i = 0; i <= max_dice_number; ++i) {
	        	String imgDice=resizeImage2("image/activities/math/numeration/smallnumbers/dice"+i+".png", (int)diceSize, (int)diceSize);
	            String imgDiceSel=buttonize(imgDice);
	    	    CCTexture2D texture3=CCTextureCache.sharedTextureCache().addImageExternal(imgDice);
	    	    CCTexture2D texture4=CCTextureCache.sharedTextureCache().addImageExternal(imgDiceSel);
	
	            CCSprite spriteDice=CCSprite.sprite(texture3);
	            CCSprite spriteDiceSelected=CCSprite.sprite(texture4);
	            choices[i]=CCMenuItemImage.item(spriteDice, spriteDiceSelected, this, "ignore");
	        }
	        CCMenuItemToggle toggleItems=null;
	        if (max_dice_number == 3)
	            toggleItems = CCMenuItemToggle.item(this, "diceTouched", choices[0],choices[1],choices[2],choices[3]);
	        else if (max_dice_number == 6)
	            toggleItems = CCMenuItemToggle.item(this, "diceTouched", choices[0],choices[1],choices[2],choices[3],choices[4],choices[5],choices[6]);
	        else
	            toggleItems = CCMenuItemToggle.item(this, "diceTouched", choices[0],choices[1],choices[2],choices[3],choices[4],choices[5],choices[6],choices[7],choices[8],choices[9]);
	
	        toggleItems.setSelectedIndex(0);
	        toggleItems.setTag(col);
	        menu = CCMenu.menu(toggleItems);
	        menu.setPosition(sprite.getPosition().x-sprite.getContentSize().width*sprite.getScale()/2+col*diceSize+diceSize/2, sprite.getPosition().y);
	        super.addChild(menu, 3);
	        floatingSprites.add(menu);
	    }
	    //fishes
	    this.fishBorn();
	    failures=0;
	    int radius=10;
	    for (int i = 0; i < kMaxErrorsAllowed; ++i) {
	        float xPos=szWin.width - cxIce - 4 - i * (radius + 2)*2 - radius;
	        float yPos=bottomOverhead() + cyIce + radius + 4;
	        EllipseSprite ellipse=new EllipseSprite(CGPoint.ccp(xPos, yPos), radius, radius);
	        ellipse.setClr(new ccColor4F(0, 1, 0, 1));
	        super.addChild(ellipse, 2);
	        ellipse.setTag(kTagErrorIndicator+(kMaxErrorsAllowed-i-1));
	        floatingSprites.add(ellipse);
	    }
	    
	    busy=false;
	}
	
	//The zero point is located at the left-bottom corner
	/*
	private CGPoint gridToScreenPos(int x, int y) {
	    float xPos=x*cxIce+cxIce/2;
	    float yPos=bottomOverhead() + y * cyIce + cyIce/2;
	    return CGPoint.ccp(xPos, yPos);
	}
	*/
	//anti-clockwise
	private CGPoint sequence2ScreenPos(int seq){
	    for (CCNode node : floatingSprites) {
	        if (node.getTag() >= kTagIce && node.getTag()-kTagIce == seq)
	            return node.getPosition();
	    }
	    return CGPoint.ccp(0, 0);
	}
	public void diceTouched(Object _sender){
	    playSound("audio/sounds/bleep.wav");
	    
	    CCMenuItemToggle menuitem=(CCMenuItemToggle)_sender;
	    diceValue[menuitem.getTag()]=menuitem.selectedIndex();
	}
	public void ok(Object _sender) {
	    if (busy)
	        return;
	    
	    int totalDiceValues=0;
	    for (int i = 0; i < number_of_dices; ++i)
	        totalDiceValues+=diceValue[i];
	    if (totalDiceValues <= 0)
	        return;
	    busy=true;
	    //move forward the tux
	    tuxDstPosSequence=tuxPosSequence-totalDiceValues;
	    while (tuxDstPosSequence < 0)
	        tuxDstPosSequence += number_of_item;
	    this.moveForward(null);
	}
	public void moveForward(Object _sender) {
	    if (super.isShuttingDown())
	        return;
	    if (tuxPosSequence == tuxDstPosSequence) {
	        busy=false;
	        if (tuxPosSequence == fishPosSequence) {
	            fishSprite.removeFromParentAndCleanup(true);
	            floatingSprites.remove(fishSprite);
	            fishSprite=null; fishPosSequence=-1;
	            playSound("audio/sounds/eat.wav");
	
	            if (--number_of_fish > 0) {
	                this.fishBorn();
	            }
	            else {
	                busy=true;
	                flashAnswerWithResult(true, true, null, null, 2);
	            }
	        }
	        else {
	            //fail
	            ++failures;
	            for (CCNode node : floatingSprites) {
	                if (node.getTag() >= kTagErrorIndicator && node.getTag()-kTagErrorIndicator < failures) {
	                    EllipseSprite ellipse=(EllipseSprite )node;
	                    ellipse.setClr(new ccColor4F(1, 0, 0, 1));
	                }
	            }
	            playSound("audio/sounds/crash.wav");
	            if (failures >= kMaxErrorsAllowed) {
	                busy=true;
	                flashAnswerWithResult(false, false, null, null, 2);
	                super.performSelector("__restart", 2);
	            }
	        }
	    }
	    else {
	        if (--tuxPosSequence < 0)
	            tuxPosSequence += number_of_item;
	        CGPoint scrPos=sequence2ScreenPos(tuxPosSequence);
	        int rotation=0;
	        if (scrPos.x < tuxSprite.getPosition().x) {
	            //moving left
	            rotation=90;
	        }
	        else if (scrPos.x > tuxSprite.getPosition().x) {
	            //moving right
	            rotation=-90;
	        }
	        if (scrPos.y < tuxSprite.getPosition().y) {
	            //moving down
	            rotation=0;
	        }
	        else if (scrPos.y > tuxSprite.getPosition().y) {
	            //moving up
	            rotation=180;
	        }
	        tuxSprite.setRotation(rotation);
	        
	        CCMoveTo moveAction = CCMoveTo.action(0.4f, scrPos);
	        CCCallFuncN moveDone = CCCallFuncN.action(this, "moveForward");
	        tuxSprite.runAction(CCSequence.actions(moveAction, moveDone));
	    }
	}
	
	//fishes
	private void fishBorn() {
	    fishPosSequence=tuxPosSequence - randomBetween(1, max_dice_number*number_of_dices);
	    while (fishPosSequence < 0)
	        fishPosSequence += number_of_item;
	    
	    fishSprite=spriteFromExpansionFile("image/activities/math/numeration/reversecount/fish.png");
	    float scale1=cxIce/fishSprite.getContentSize().width;
	    float scale2=cyIce/fishSprite.getContentSize().height;
	    float scale=(scale2 > scale1) ? scale1: scale2;
	    fishSprite.setScale(scale*0.8f);
	    fishSprite.setPosition(sequence2ScreenPos(fishPosSequence));
	    if (super.nextInt(2)==1) {
	        fishSprite.setScaleX(fishSprite.getScaleX()*-1);
	    }
	    super.addChild(fishSprite, 2);
	    floatingSprites.add(fishSprite);
	}
	
	public void __restart() {
		this.initGame(false, null);
	}
}
