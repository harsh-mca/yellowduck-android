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


package name.w.yellowduck.activities.reading;

import name.w.yellowduck.Category;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.types.ccColor3B;

public class MemoryWorldNumberScene extends name.w.yellowduck.activities.memory.MemorySceneBase {
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new MemoryWorldNumberScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public void onEnter() {
		super.onEnter();
		mMaxLevel=6;
		
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
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
	    for (int i = 0; i < totalNumbers; ++i) {
	        CCLabel numberLabel = CCLabel.makeLabel(""+(i + 1), super.sysFontName(), super.mediumFontSize());
	        numberLabel.setColor(ccColor3B.ccBLACK);
	        numberLabel.setTag(i);
	        //position to be determined
	        floatingSprites.add(numberLabel);
	
	
	        String entry="number_" +(i + 1);
	        CCLabel worldLabel = CCLabel.makeLabel(localizedString(entry), super.sysFontName(),super.mediumFontSize());
	        worldLabel.setColor(ccColor3B.ccBLACK);
	        worldLabel.setTag(i);
	        //position to be determined
	        floatingSprites.add(worldLabel);
	    }
	    super.postInitMemoryGame();
	}
}
