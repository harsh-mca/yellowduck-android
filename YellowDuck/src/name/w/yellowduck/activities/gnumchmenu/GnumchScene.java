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


package name.w.yellowduck.activities.gnumchmenu;

import name.w.yellowduck.Category;
import name.w.yellowduck.Schema;
import name.w.yellowduck.YDConfiguration;

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

import android.view.MotionEvent;

public class GnumchScene extends name.w.yellowduck.YDActLayerBase {
	private final int kModeEquality           =0;
	private final int kModeFactor             =1;
	private final int kModeInEquality         =2;
	private final int kModeMultiples          =3;
	private final int kModePrimes             =4;

	private final int kTagEdible             =-8888;
	
    private int mode;
    private int topMargin, leftMargin, rows, cols, cellWidth, cellHeight;
    private CCLabel promptLabel;
    private CCSprite spriteMuncher;
	

	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new GnumchScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
		super.onEnter();
	    mMaxLevel=5;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    if ("equality".equals(activeCategory.getSettings())) {
	        mode=kModeEquality;
	    }
	    else if ("factors".equals(activeCategory.getSettings())) {
	        mode=kModeFactor;
	    }
	    else if ("inequality".equals(activeCategory.getSettings())) {
	        mode=kModeInEquality;
	    }
	    else if ("multiples".equals(activeCategory.getSettings())) {
	        mode=kModeMultiples;
	    }
	    else if ("primes".equals(activeCategory.getSettings())) {
	        mode=kModePrimes;
	    }

	    super.shufflePlayBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionArrows);

	    rows=cols=6;
	    topMargin=buttonSize();
	    leftMargin=10;
	    cellWidth=((int)szWin.width - leftMargin*2) / cols;
	    cellHeight=((int)szWin.height-topOverhead() - topMargin - bottomOverhead())/rows;
	    float gridWidth=cellWidth * cols;
	    //draw the grid
	    String wood="image/activities/discovery/maze/2d/wood.png";
	    for (int j = 0; j <= rows; ++j) {
	        CCSprite sprite = spriteFromExpansionFile(wood);
	        sprite.setScaleX(1.0f * gridWidth / sprite.getContentSize().width);
	        sprite.setScaleY(2.0f/sprite.getContentSize().height);
	        sprite.setPosition(leftMargin+gridWidth/2, szWin.height - topOverhead() - topMargin - j*cellHeight);
	        super.addChild(sprite,1);
	    }
	    for (int j = 0; j <= cols; ++j) {
	        CCSprite sprite = spriteFromExpansionFile(wood);
	        sprite.setScaleX(2.0f/sprite.getContentSize().width);
	        sprite.setScaleY(1.0f * cellHeight * rows / sprite.getContentSize().height);
	        sprite.setPosition(j * cellWidth+leftMargin,  (szWin.height - topOverhead() - topMargin + bottomOverhead())/2);
	        super.addChild(sprite,1);
	    }
	    //the prompt label
	    promptLabel = CCLabel.makeLabel("X", super.sysFontName(), super.mediumFontSize());
	    promptLabel.setColor(ccColor3B.ccBLACK);
	    promptLabel.setPosition(szWin.width/2, szWin.height-topOverhead()-promptLabel.getContentSize().height/2);
	    super.addChild(promptLabel,0);
	    //muncher
	    spriteMuncher=spriteFromExpansionFile("image/activities/math/gnumchmenu/muncher.png");
	    spriteMuncher.setPosition(this.grid2ContentCoord(0,0));
	    super.addChild(spriteMuncher,2);
	    
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	//Override
	public void toolbarBtnTouched(Object _sender) {
	    super.toolbarBtnTouched(_sender);
	    CCNode sender=(CCNode)_sender;
	    switch (sender.getTag()) {
	        case Schema.kSvgButtonHELP:
	            break;
	        case Schema.kSvgButtonLevelDn:
	            break;
	        case Schema.kSvgButtonLevelUp:
	            break;
	        case Schema.kSvgArrowUp:
	            this.move(0,-1);
	            break;
	        case Schema.kSvgArrowLeft:
	            this.move(-1,0);
	            break;
	        case Schema.kSvgArrowRight:
	            this.move(1,0);
	            break;
	        case Schema.kSvgArrowDown:
	            this.move(0, 1);
	            break;
	    }
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    
	    int max=mLevel+5;
	    int num1, num2;
	    switch (mode) {
	        case kModeEquality:
	            promptLabel.setString(String.format(localizedString("prompt_equal_to"), max));
	            for (int j = 0; j < rows; ++j)
	                for (int i = 0; i < cols; ++i) {
	                    int tag=0;
	                    if (super.nextInt(2)==0) {
	                        //correct
	                        num1=randomBetween(1,max-1);
	                        num2=max-num1;
	                        tag=kTagEdible;
	                    }
	                    else {
	                        //wrong
	                        int wrong=randomBetween(max/2,max*2);
	                        if (wrong == max)
	                            ++wrong;
	                        num1=randomBetween(1,wrong-1);
	                        num2=wrong-num1;
	                        tag=wrong;
	                    }
	                    CCLabel label = CCLabel.makeLabel(String.format("%d+%d", num1, num2), super.sysFontName(), super.smallFontSize());
	                    label.setPosition(this.grid2ContentCoord(i,j));
	                    label.setColor(ccColor3B.ccBLACK);
	                    label.setTag(tag);
	                    label.setUserData(String.format("%dx%d", i, j));//the coordination
	                    super.addChild(label,10); //top most
	                    floatingSprites.add(label);
	                }
	            break;
	        case kModeInEquality:
	            promptLabel.setString(String.format(localizedString("prompt_not_equal_to"), max));
	            for (int j = 0; j < rows; ++j)
	                for (int i = 0; i < cols; ++i) {
	                    int tag=0;
	                    if (super.nextInt(2)==0) {
	                        //correct
	                        num1=randomBetween(1,max-1);
	                        num2=max-num1;
	                        tag=max;
	                    }
	                    else {
	                        //wrong
	                        int wrong=randomBetween(max/2,max*2);
	                        if (wrong == max)
	                            ++wrong;
	                        num1=randomBetween(1,wrong-1);
	                        num2=wrong-num1;
	                        tag=kTagEdible;
	                    }
	                    CCLabel label = CCLabel.makeLabel(String.format("%d+%d", num1, num2), super.sysFontName(), super.smallFontSize());
	                    label.setPosition(grid2ContentCoord(i,j));
	                    label.setColor(ccColor3B.ccBLACK);
	                    label.setTag(tag);
	                    label.setUserData(String.format("%dx%d", i, j));//the coordination
	                    super.addChild(label,10); //top most
	                    floatingSprites.add(label);
	                }
	            break;
	        case kModeMultiples:
	            max=mLevel+1;
	            promptLabel.setString(String.format(localizedString("prompt_multiple_of"), max));
	            for (int j = 0; j < rows; ++j)
	                for (int i = 0; i < cols; ++i) {
	                    int value=randomBetween(1, rows*cols/2)*max;
	                    int tag=0;
	                    if (super.nextInt(2)==0) {
	                        //correct
	                        tag=kTagEdible;
	                    }
	                    else {
	                        value += randomBetween(1,max-1);
	                        tag=value;
	                    }
	                    CCLabel label = CCLabel.makeLabel(""+value, super.sysFontName(), super.smallFontSize());
	                    label.setPosition(grid2ContentCoord(i,j));
	                    label.setColor(ccColor3B.ccBLACK);
	                    label.setTag(tag);
	                    label.setUserData(String.format("%dx%d", i, j));
	                    super.addChild(label,10);
	                    floatingSprites.add(label);
	                }
	            break;
	        case kModeFactor:
	            max=(mLevel+1)*2;
	            promptLabel.setString(String.format(localizedString("prompt_factor_of"), max));
	            for (int j = 0; j < rows; ++j)
	                for (int i = 0; i < cols; ++i) {
	                    int value=randomBetween(1, max);
	                    int tag=0;
	                    if ((max % value) == 0) {
	                        //correct
	                        tag=kTagEdible;
	                    }
	                    else {
	                        tag=value;
	                    }
	                    CCLabel label = CCLabel.makeLabel(""+value, super.sysFontName(), super.smallFontSize());
	                    label.setPosition(grid2ContentCoord(i,j));
	                    label.setColor(ccColor3B.ccBLACK);
	                    label.setTag(tag);
	                    label.setUserData(String.format("%dx%d", i, j));
	                    super.addChild(label,10);
	                    floatingSprites.add(label);
	                }
	            break;
	        case kModePrimes:
	            max=mLevel*10;
	            promptLabel.setString(String.format(localizedString("prompt_prime_of"), max));
	            for (int j = 0; j < rows; ++j)
	                for (int i = 0; i < cols; ++i) {
	                    int value=randomBetween(1,max);
	                    boolean prime=(value > 1);
	                    for (int n = 2; n < value; ++n) {
	                        if ((value % n) == 0) {
	                            prime=false;
	                            break;
	                        }
	                    }
	                    int tag=prime?kTagEdible:value;
	                    CCLabel label = CCLabel.makeLabel(""+value, super.sysFontName(), super.smallFontSize());
	                    label.setPosition(grid2ContentCoord(i,j));
	                    label.setColor(ccColor3B.ccBLACK);
	                    label.setTag(tag);
	                    label.setUserData(String.format("%dx%d", i, j));
	                    super.addChild(label,10);
	                    floatingSprites.add(label);
	                }
	            break;
	    }
	    spriteMuncher.setPosition(grid2ContentCoord(0,0));
	}
	//The center of the square
	private CGPoint grid2ContentCoord(int x, int y) {
	    float x0=leftMargin+x*cellWidth;
	    float y0=szWin.height - topOverhead() - topMargin - y*cellHeight;
	
	    return CGPoint.ccp(x0+cellWidth/2, y0-cellHeight/2);
	}
	private CGPoint content2GridCoord(int x, int y) {
	    int x0=(x-leftMargin)/cellWidth;
	    int y0=((int)szWin.height-y-topOverhead() - topMargin)/cellHeight;
	    
	    return CGPoint.ccp(x0, y0);
	}
	
	public boolean ccTouchesEnded(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
	
        CGPoint p2=this.content2GridCoord((int)p1.x,(int)p1.y);
        //tap inside the grid
        if (p2.x >= 0 && p2.x < cols && p2.y >= 0 && p2.y < rows) {
            CGPoint p0=this.content2GridCoord((int)spriteMuncher.getPosition().x,(int)spriteMuncher.getPosition().y);
            int xSteps=(int)(p2.x-p0.x), ySteps=(int)(p2.y-p0.y);
            this.move(xSteps,ySteps);
        }
        return true;
	}
	private void move(int xSteps, int ySteps) {
	    CGPoint ptTux=content2GridCoord((int)spriteMuncher.getPosition().x, (int)spriteMuncher.getPosition().y);
	    int x0=(int)ptTux.x;
	    int y0=(int)ptTux.y;
	    
	    int x1=x0, y1=y0;
	    x1 += xSteps; y1 += ySteps;
	    if (x1 < 0)
	        x1=0;
	    else if (x1 >= cols)
	        x1=cols-1;
	    if (y1 < 0)
	        y1=0;
	    else if (y1 >= rows)
	        y1=rows-1;
	    
	    if (x1 != x0 || y1 != y0) {
	        CGPoint dst=grid2ContentCoord(x1, y1);
	        CCMoveTo move = CCMoveTo.action(0.5f, dst);
	        super.playSound("audio/sounds/smudge.wav");
	        spriteMuncher.runAction(move);
	    }
	    else {
	        //eat
	        String flag=String.format("%dx%d", x1, y1);
	        CCLabel find=null;
	        int answers=0;
	        for (CCNode node : floatingSprites) {
	            if (node.getTag() == kTagEdible) {
	                ++answers;
	            }
	            if (node.getUserData() != null && node.getUserData().equals(flag)) {
	                find=(CCLabel)node;
	            }
	        }
	        if (find!=null) {
	            if (find.getTag()==kTagEdible) {
	                super.playSound("audio/sounds/eat.wav");
	                find.removeFromParentAndCleanup(true);
	                floatingSprites.remove(find);
	                spriteMuncher.runAction(CCSequence.actions(CCScaleTo.action(.2f, 1.6f), CCScaleTo.action(.2f,1.0f)));
	                if (--answers <= 0)
	                    super.flashAnswerWithResult(true,  true,  null,  null,  2);
	            }
	            else {
	                String msg=null;
	                switch (mode) {
	                    case kModeEquality:
	                    case kModeInEquality:
	                        msg=String.format(localizedString("prompt_gnumch_wrong"), find.getString(), find.getTag());
	                        break;
	                    case kModeMultiples:
	                    case kModeFactor:
	                        msg=localizedString("prompt_gnumch_wrong_short");
	                        break;
	                    case kModePrimes:
	                        msg=String.format(localizedString("prompt_gnumch_wrong_prime"), find.getTag());
	                        break;
	                }
	                if (msg!=null) {
	                    super.playSound("audio/sounds/brick.wav");
	                    super.flashMsg(msg,2);
	                }
	            }
	        }
	    }
	}
}
