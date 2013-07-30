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
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;

import android.view.MotionEvent;

public class EnumerateScene extends name.w.yellowduck.YDActLayerBase {
	private final int kTagItem            =100;

	private final int kTagAnswerBg        =300;
	private final int kMaxType                =10;

	private java.util.ArrayList<String> items; //item image names
    private int number_of_item_type, number_of_item_max;
    private int number_of_item_each_type[]=new int[kMaxType], item_type[]=new int[kMaxType];
    private int answer_of_item_each_type[]=new int[kMaxType];
    private int answeringIndex;
    
    private float yKeyboardTop;
    private CCNode pickedSprite;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new EnumerateScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	// on "init" you need to initialize your instance
	public EnumerateScene() {
		super();
        items=new java.util.ArrayList<String>();
        
        items.add("image/activities/math/numeration/enumerate/banana.png");
        items.add("image/activities/math/numeration/enumerate/orange.png");
        items.add("image/activities/math/numeration/enumerate/milk_shake.png");
        items.add("image/activities/math/numeration/enumerate/pear.png");
        items.add("image/activities/math/numeration/enumerate/grapefruit.png");
        items.add("image/activities/math/numeration/enumerate/yahourt.png");
        items.add("image/activities/math/numeration/enumerate/milk_cup.png");
        items.add("image/activities/math/numeration/enumerate/suggar_box.png");
        items.add("image/activities/math/numeration/enumerate/butter.png");
        items.add("image/activities/math/numeration/enumerate/chocolate.png");
        items.add("image/activities/math/numeration/enumerate/cookie.png");
        items.add("image/activities/math/numeration/enumerate/french_croissant.png");
        items.add("image/activities/math/numeration/enumerate/chocolate_cake.png");
        items.add("image/activities/math/numeration/enumerate/marmelade.png");
        items.add("image/activities/math/numeration/enumerate/baby_bottle.png");
        items.add("image/activities/math/numeration/enumerate/bread_slice.png");
        items.add("image/activities/math/numeration/enumerate/round_cookie.png");
	}

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=9;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.shufflePlayBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionOk);
	    
	    //the numerical keyboard
	    float keyWidth=super.setupVirtualKeyboard("1234567890", null);
	    yKeyboardTop=bottomOverhead() + keyWidth;
	    
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
	    
	    switch(mLevel)
	    {
	        case 1:
	            number_of_item_type = 1;
	            number_of_item_max  = 5;
	            break;
	        case 2:
	            number_of_item_type = 2;
	            number_of_item_max  = 5;
	            break;
	        case 3:
	            number_of_item_type = 3;
	            number_of_item_max  = 5;
	            break;
	        case 4:
	            number_of_item_type = 3;
	            number_of_item_max  = 5;
	            break;
	        case 5:
	            number_of_item_type = 4;
	            number_of_item_max  = 5;
	            break;
	        case 6:
	            number_of_item_type = 4;
	            number_of_item_max  = 6;
	            break;
	        case 7:
	            number_of_item_type = 4;
	            number_of_item_max  = 7;
	            break;
	        case 8:
	            number_of_item_type = 4;
	            number_of_item_max  = 9;
	            break;
	        default:
	            number_of_item_type = 5;
	            number_of_item_max = 9;
	    }
	    //make sure the number_of_item_max is less than 10
	    if (number_of_item_max > 9)
	        number_of_item_max=9;
	    
	    int selectedTypes[]=new int[items.size()];
	    for (int i = 0; i < items.size(); ++i)
	        selectedTypes[i]=i;
	    super.randomIt(selectedTypes, items.size());
	    
	    float answerAreaWidth=szWin.width/5;
	    float yAnswer=yKeyboardTop;
	    
	    for (int i = 0; i < number_of_item_type; ++i) {
	        number_of_item_each_type[i]=randomBetween(1, number_of_item_max);
	        answer_of_item_each_type[i]=-1; //set as not answered yet
	        //make sure each type is different from others
	        item_type[i]=selectedTypes[i];
	        //display these items
	        for (int j = 0; j < number_of_item_each_type[i]; ++j) {
	            CCSprite sprite=spriteFromExpansionFile(items.get(item_type[i]));
	            sprite.setScale(preferredContentScale(true));
	            float xRandom=super.nextInt((int)(szWin.width-answerAreaWidth));
	            if (xRandom-sprite.getContentSize().width*sprite.getScale()/2 < 0)
	                xRandom=sprite.getContentSize().width*sprite.getScale()/2;
	            else if (xRandom+sprite.getContentSize().width*sprite.getScale()/2 > szWin.width-answerAreaWidth)
	                 xRandom=szWin.width-answerAreaWidth-sprite.getContentSize().width*sprite.getScale()/2;
	            float yRandom=super.nextInt((int)szWin.height);
	            if (yRandom-sprite.getContentSize().height*sprite.getScale()/2 < yKeyboardTop)
	                yRandom=yKeyboardTop+sprite.getContentSize().height*sprite.getScale()/2;
	            else if (yRandom+sprite.getContentSize().height*sprite.getScale()/2 > szWin.height-topOverhead())
	                yRandom=szWin.height-topOverhead()-sprite.getContentSize().height*sprite.getScale()/2;
	            sprite.setPosition(xRandom, yRandom);
	            sprite.setTag(kTagItem);
	
	            super.addChild(sprite,1);
	            floatingSprites.add(sprite);
	        }
	        //the answer for this item
	        CCSprite sprite=spriteFromExpansionFile("image/activities/math/numeration/enumerate/enumerate_answer.png");
	        sprite.setPosition(szWin.width-answerAreaWidth/2, yAnswer+sprite.getContentSize().height/2);
	        sprite.setTag(kTagAnswerBg+i);
	        super.addChild(sprite,1 );
	        floatingSprites.add(sprite);
	
	        //the icon
	        CCSprite icon=spriteFromExpansionFile(items.get(item_type[i]));
	        icon.setScale(sprite.getContentSize().height/icon.getContentSize().height*0.5f);
	        icon.setPosition(sprite.getPosition().x -sprite.getContentSize().width/2 + 4 + icon.getContentSize().width*icon.getScale()/2, sprite.getPosition().y);
	        super.addChild(icon,1);
	        floatingSprites.add (icon);
	
	        //the lable on it
	        CCLabel label = CCLabel.makeLabel("?", super.sysFontName(),super.mediumFontSize());
	        label.setColor(ccColor3B.ccBLACK);
	        label.setPosition(sprite.getPosition().x+label.getContentSize().width, sprite.getPosition().y);
	        super.addChild(label, 3);
	        floatingSprites.add(label);
	        
	        sprite.setUserData(label);
	        
	        //move top a little for next item answer
	        yAnswer += sprite.getContentSize().height+6;
	    }
	    answeringIndex=0;//start to answer the first type of items
	    this.refreshAnswers();
	}
	public void ok(Object sender) {
	    boolean correct=true;
	    for (int i = 0; i < number_of_item_type; ++i) {
	        if (number_of_item_each_type[i] != answer_of_item_each_type[i]) {
	            correct=false;
	            break;
	        }
	    }
	    super.flashAnswerWithResult(correct,correct,null,null,2);
	}
	
	public void letterTouched(Object _sender) {
	    playSound("audio/sounds/click.wav");
	    CCNode sender=(CCNode)_sender;
	    String theKey=(String)sender.getUserData();
	    //the answer is less than 10
	    int number=Integer.parseInt(theKey);
	    answer_of_item_each_type[answeringIndex]=number;
	    this.refreshAnswers();
	}
	
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
	    pickedSprite=null;
        CCNode clicked=null;
        for (CCNode node : floatingSprites) {
            if (super.isNodeHit(node, p1)) {
                if (node.getTag() == kTagItem) {
                    clicked=node;
                    break;
                }
                else if (node.getTag() >= kTagAnswerBg) {
                    answeringIndex=node.getTag()-kTagAnswerBg;
                    this.refreshAnswers();
                    super.playSound("audio/sounds/prompt.wav");
                }
            }
        }
        if (clicked!=null) {
            clicked.setPosition(p1);
            pickedSprite=clicked;
            super.playSound("audio/sounds/line_end.wav");
        }
        return true;
	}
	
	public boolean ccTouchesMoved(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
		if (pickedSprite!=null)
            pickedSprite.setPosition(p1);
		return true;
	}
	
	public boolean ccTouchesEnded(MotionEvent event) {
	    pickedSprite=null;
	    return true;
	}
	
	
	private void refreshAnswers() {
	    for (CCNode node : floatingSprites) {
	        if (node.getTag() >= kTagAnswerBg) {
	            CCSprite sprite=(CCSprite)node;
	            int _answeringIdx=sprite.getTag() - kTagAnswerBg;
	            if (_answeringIdx == answeringIndex) {
	                sprite.setColor(ccColor3B.ccWHITE);
	            }
	            else {
	            	sprite.setColor(ccColor3B.ccGRAY);
	            }
	            CCLabel label=(CCLabel)sprite.getUserData();
	            if (answer_of_item_each_type[_answeringIdx] >= 0) {
	                label.setString(""+answer_of_item_each_type[_answeringIdx]);
	            }
	            else {
	                label.setString("?");
	            }
	        }
	    }
	}
}
