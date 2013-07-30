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

import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;

public class GuessNumberScene extends name.w.yellowduck.YDActLayerBase {

    private CCSprite chopper;
    private float xExit, yExit;
    
    
    private CCLabel promptLabel, answerLabel, hintLabel;
    private int min, max;
    private int target, answered;
    
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new GuessNumberScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
		super.onEnter();
	    mMaxLevel=4;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.shufflePlayBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
	
	    
	    //
	    chopper=spriteFromExpansionFile("image/activities/math/numeration/planegame/tuxhelico.png");
	    chopper.setScale(szWin.height/8/chopper.getContentSize().height);
	    chopper.setPosition(chopper.getContentSize().width*chopper.getScale()/2, chopper.getContentSize().height*chopper.getScale()/2);
	    super.addChild(chopper,1);
	    xExit=szWin.width-chopper.getContentSize().width*chopper.getScale()/2;
	    yExit=szWin.height/2;
	
	    promptLabel = CCLabel.makeLabel("10/10", super.sysFontName(), super.smallFontSize());
	    promptLabel.setColor(ccColor3B.ccBLACK);
	    super.addChild(promptLabel, 1);
	    
	    answerLabel = CCLabel.makeLabel("1000", super.sysFontName(), super.mediumFontSize());
	    answerLabel.setColor(ccColor3B.ccBLUE);
	    answerLabel.setPosition(szWin.width-answerLabel.getContentSize().width/2, szWin.height-topOverhead() - answerLabel.getContentSize().height/2);
	    super.addChild(answerLabel, 1);
	    
	    hintLabel = CCLabel.makeLabel(" ", super.sysFontName(), super.smallFontSize());
	    hintLabel.setColor(ccColor3B.ccRED);
	    hintLabel.setPosition(szWin.width/2, answerLabel.getPosition().y);
	    super.addChild(hintLabel, 1);
	
	    //the numerical keyboard
	    super.setupVirtualKeyboard("1234567890*", null);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
	
	    chopper.stopAllActions();
	    float xOrg=chopper.getContentSize().width*chopper.getScale()/2;
	    float yOrg=bottomOverhead()+chopper.getContentSize().height*chopper.getScale()*2;
	    chopper.setPosition(xOrg, yOrg);
	
	    min = 1; max = 10;
	    if (mLevel == 2)
	        max = 100;
	    else if (mLevel==3)
	        max=500;
	    else if (mLevel==4)
	        max=1000;
	    promptLabel.setString(String.format(localizedString("prompt_guess_number"), min, max));
	    promptLabel.setPosition(promptLabel.getContentSize().width/2+4, szWin.height-topOverhead()-promptLabel.getContentSize().height/2);
	    
	    target=randomBetween(min, max);
	    answered=0;
	    answerLabel.setString("?");
	    hintLabel.setString(" ");
	    hintLabel.setVisible(true);
	}
	
	public void ok(Object _sender) {
	    if (answered == target) {
	        hintLabel.setVisible(false);
	        chopper.stopAllActions();
	        CCMoveTo moveAction = CCMoveTo.action(1, CGPoint.ccp(xExit+chopper.getContentSize().width, yExit));
	        chopper.runAction(moveAction);
	        super.flashAnswerWithResult(true, true, null, null, 2);
	    }
	    else {
	        if (answered > max || answered < min)
	            hintLabel.setString(localizedString("prompt_guess_number_out_of_range"));
	        else {
	            /*
	             original implementation:
	             max_distance = max(self.max - self.solution, self.solution)
	             distance_x = self.target_x - abs(self.solution - number) * float(self.target_x - self.orig_x) / max_distance
	             distance_y = self.orig_y + float(((self.solution - number) * 170) / max_distance)
	            */
	            if (answered > target)
	                hintLabel.setString(localizedString("prompt_guess_number_too_high"));
	            else
	                hintLabel.setString(localizedString("prompt_guess_number_too_low"));
	            
	            int distance=target-answered;
	            int maxDistance=Math.abs(distance)+target;
	            float xPos=xExit-xExit * Math.abs(distance)/maxDistance;
	            float yPos=yExit - 1.0f*distance/maxDistance*szWin.height*0.4f;
	            chopper.stopAllActions();
	            CCMoveTo moveAction = CCMoveTo.action(2, CGPoint.ccp(xPos, yPos));
	            chopper.runAction(moveAction);
	        }
	        //reset answer
	        super.performSelector("reset", 1);
	    }
	}
	
	public void letterTouched(Object _sender) {
	    playSound("audio/sounds/click.wav");
	    CCNode sender=(CCNode)_sender;
	    String theKey=(String)sender.getUserData();
	    if ("*".equals(theKey))
	        this.ok(sender);
	    else {
	        answered=answered * 10 + Integer.parseInt(theKey);
	        answerLabel.setString(""+answered);
	    }
	}
	
	public void reset() {
	    answered = 0;
	    answerLabel.setString("?");
	}
}
