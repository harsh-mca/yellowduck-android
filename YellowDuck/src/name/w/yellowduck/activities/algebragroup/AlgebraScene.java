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


package name.w.yellowduck.activities.algebragroup;

import name.w.yellowduck.Category;
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

import android.graphics.Bitmap;

public class AlgebraScene extends name.w.yellowduck.YDActLayerBase {
	private final int kTimeOut            =50; //seconds
	
	private final int OP_PLUS             =0;
	private final int OP_MINUS            =1;
	private final int OP_MULTIPLE         =2;
	private final int OP_DIV              =3;

    private CCLabel triesLabel, questionLabel, answerLabel;
    private int operation, result;
    private int entered, digits;

    private boolean ready;
    private float letterSize;
    private int questionsTotal, questionsAnswered;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new AlgebraScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=9;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    if ("+".equalsIgnoreCase(activeCategory.getSettings())) {
	    	operation=OP_PLUS;
	    }
	    else if ("-".equalsIgnoreCase(activeCategory.getSettings())) {
	    	operation=OP_MINUS;
	    } 
	    else if ("*".equalsIgnoreCase(activeCategory.getSettings())) {
	    	operation=OP_MULTIPLE;
	    } 
	    else if ("/".equalsIgnoreCase(activeCategory.getSettings())) {
	    	operation=OP_DIV;
	    } 
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);

	    //display the keyboard
	    letterSize=super.setupVirtualKeyboard("1234567890", null);
	    
	    triesLabel = CCLabel.makeLabel(String.format("%d/%d", questionsAnswered+1, questionsTotal), super.sysFontName(), super.mediumFontSize());
	    triesLabel.setColor(ccColor3B.ccBLUE);
	    triesLabel.setPosition(triesLabel.getContentSize().width, szWin.height-topOverhead()-triesLabel.getContentSize().height);
	    triesLabel.setVisible(false);
	    super.addChild(triesLabel,1);
	    
	
	    questionLabel = CCLabel.makeLabel("n+n=", super.sysFontName(), super.mediumFontSize());
	    questionLabel.setPosition(szWin.width*0.25f, bottomOverhead() + letterSize + questionLabel.getContentSize().height);
	    questionLabel.setVisible(false);
	    super.addChild(questionLabel,1);
	
	    answerLabel = CCLabel.makeLabel("??", super.sysFontName(), super.mediumFontSize());
	    answerLabel.setPosition(questionLabel.getPosition().x+questionLabel.getContentSize().width/2+answerLabel.getContentSize().width/2, questionLabel.getPosition().y);
	    answerLabel.setVisible(false);
	    super.addChild(answerLabel,1);
	
	    super.afterEnter();
	}
	
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	
	    questionsTotal=10;
	    questionsAnswered=0;
	    
	    ready=false;
	    triesLabel.setVisible(false);
	    questionLabel.setVisible(false);
	    answerLabel.setVisible(false);
	    
	    //ready menu item
	    int fontSize=super.largeFontSize();
	    String text=localizedString("label_am_ready");
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
	public void letterTouched(Object _sender) {
	    if (!ready)
	        return;
	    super.playSound("audio/sounds/click.wav");
	    CCNode sender=(CCNode)_sender;
	    String theLetter=(String)sender.getUserData();
	    entered=entered*10+Integer.parseInt(theLetter);
	    ++digits;
	
	    answerLabel.setString(""+entered);
	    //display the result for a while
	    super.performSelector("checkAnswer", 0.5f);
	}

	public void checkAnswer() {
	    if (entered == result) {
	        if (++questionsAnswered >= questionsTotal) {
	            ready=false;
	            for (CCNode node : floatingSprites) {
	                node.stopAllActions();
	            }
	            super.flashAnswerWithResult(true, true, null, null, 2);
	        }
	        else {
	            this.setupQuestion();
	        }
	    }
	    else {
	        if (result >= 10) {
	            if (digits >= 2) {
	                super.playSound("audio/sounds/brick.wav");
	                //erase the wrong answer
	                entered=digits=0;
	                this.displayHint();
	            }
	        }
	        else {
	        	super.playSound("audio/sounds/brick.wav");
	            //erase the wrong answer
	            entered=digits=0;
	            this.displayHint();
	        }
	    }
	}
	public void ready(Object _sender) {
	    if (ready)
	        return;
	    ready=true;
	    CCNode sender=(CCNode)_sender;
	    sender.getParent().removeFromParentAndCleanup(true);
	    floatingSprites.remove(sender.getParent());
	
	    //the balloon falling from top to bottom of the screen
	    CCSprite timer=super.spriteFromExpansionFile("image/activities/math/algebra_group/algebra/tuxballoon.png");
	    timer.setPosition(szWin.width * 3 / 4, szWin.height - timer.getContentSize().height/2);
	    super.addChild(timer,1);
	    floatingSprites.add(timer);
	    
	    CCMoveTo moveAction = CCMoveTo.action(kTimeOut-mLevel+1, CGPoint.ccp(timer.getPosition().x, timer.getContentSize().height/2));
	    CCCallFuncN moveDone = CCCallFuncN.action(this, "timeout");
	    timer.runAction(CCSequence.actions(moveAction, moveDone));
	
	    triesLabel.setVisible(true);
	    questionLabel.setVisible(true);
	    answerLabel.setVisible(true);
	    
	    this.setupQuestion();
	}
	public void timeout(Object _sender){
	    if (ready) {
	        ready=false;
	        super.flashAnswerWithResult(false, false, null, null, 1);
	        this.initGame(false,  null);
	    }
	}
	
	private int getOperand() {
	    return super.nextInt(10)+1;
	}
	
	private void setupQuestion() {
	    int first_operand, second_operand;
	    switch (operation) {
	        case OP_PLUS:
	            first_operand=this.getOperand();
	            second_operand=questionsAnswered+1;
	            result=first_operand+second_operand;
	            questionLabel.setString(String.format("%d + %d = ", first_operand, second_operand));
	            break;
	        case OP_MINUS:
	            first_operand=questionsAnswered+9;
	            second_operand=this.getOperand();
	            if (first_operand < second_operand) {
	                int t=first_operand;
	                first_operand=second_operand;
	                second_operand=t;
	            }
	            result=first_operand-second_operand;
	            questionLabel.setString(String.format("%d - %d = ", first_operand, second_operand));
	            break;
	        case OP_MULTIPLE:
	            first_operand=questionsAnswered+1;
	            second_operand=this.getOperand();
	            if (first_operand >= 10 && second_operand >= 10)
	                second_operand = super.randomBetween(2, 7);
	            result=first_operand*second_operand;
	            questionLabel.setString(String.format("%d x %d = ", first_operand, second_operand));
	            break;
	    }
	    this.displayHint();
	
	    //questionLabel.setPosition(questionLabel.getContentSize().width, questionLabel.getPosition().y);
	    answerLabel.setPosition(questionLabel.getPosition().x+questionLabel.getContentSize().width/2+answerLabel.getContentSize().width/2, questionLabel.getPosition().y);
	
	    triesLabel.setString(String.format("%d/%d", questionsAnswered+1, questionsTotal));
	    entered=digits=0;
	}
	
	private void displayHint() {
	    answerLabel.setString((result < 10)?"?":"??");
	}
}
