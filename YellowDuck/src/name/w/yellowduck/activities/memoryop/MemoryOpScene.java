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


package name.w.yellowduck.activities.memoryop;

import name.w.yellowduck.Category;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.types.ccColor3B;

public class MemoryOpScene extends name.w.yellowduck.activities.memory.MemorySceneBase {
    private String operations;
    
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new MemoryOpScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
		super.onEnter();
	    mMaxLevel=9;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    operations=activeCategory.getSettings();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(),kBgModeStretch);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);

	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    super.preInitMemoryGame();
	    
	    int totalNumbers=rows * cols / 2;
	    int ceiling=(mLevel+1) * (mLevel+1);
	    for (int i = 0; i < totalNumbers; ++i) {
	        int theNumber=ceiling-i;
	        CCLabel numberLabel = CCLabel.makeLabel("?", super.sysFontName(), super.mediumFontSize());
	        numberLabel.setColor(ccColor3B.ccBLACK);
	        //position to be determined
	        floatingSprites.add(numberLabel);
	
	        String entry=null;
	        //random select operation
	        int num1, num2;
	        int sel=super.nextInt(operations.length());
	        String selectedOp=operations.substring(sel, sel+1);
	        if ("+".equals(selectedOp)) {
	            num1=super.nextInt(theNumber);
	            num2=theNumber-num1;
	            entry=String.format("%d+%d", num2, num1);
	        }
	        else if ("-".equals(selectedOp)) {
	            num1=super.nextInt(theNumber);
	            num2=theNumber+num1;
	            entry=String.format("%d-%d", num2, num1);
	        }
	        else if ("*".equals(selectedOp)) {
	            int floor=mLevel+1;
	            num1=super.nextInt(floor) + 1;
	            num2=super.nextInt(floor) + 1;
	            theNumber=num1 * num2;
	            entry=String.format("%dx%d", num2, num1);
	        }
	        else if ("/".equals(selectedOp)) {
	            int floor=mLevel+1;
	            num1=super.nextInt(floor) + 1;
	            num2=(super.nextInt(floor) + 1)*num1;
	            theNumber=num2/num1;
	            entry=String.format("%d/%d", num2, num1);
	        }
	        numberLabel.setTag(theNumber);
	        numberLabel.setString(""+theNumber);
	
	        CCLabel wordLabel = CCLabel.makeLabel(entry, super.sysFontName(), super.mediumFontSize());
	        wordLabel.setColor(ccColor3B.ccBLACK);
	        wordLabel.setTag(kJump+theNumber);
	        //position to be determined
	        floatingSprites.add(wordLabel);
	    }
	    super.postInitMemoryGame();
	}
}
