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


package name.w.yellowduck.activities.memory;

import name.w.yellowduck.Category;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCSprite;

public class MemoryScene  extends MemorySceneBase {
    private java.util.ArrayList<String> cards;
    
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new MemoryScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public MemoryScene() {
		super();
        cards=new java.util.ArrayList<String>();
        cards.add("image/activities/discovery/memory_group/memory/01_cat.png");
        cards.add("image/activities/discovery/memory_group/memory/02_pig.png");
        cards.add("image/activities/discovery/memory_group/memory/03_bear.png");
        cards.add("image/activities/discovery/memory_group/memory/04_hippopotamus.png");
        cards.add("image/activities/discovery/memory_group/memory/05_penguin.png");
        cards.add("image/activities/discovery/memory_group/memory/06_cow.png");
        cards.add("image/activities/discovery/memory_group/memory/07_sheep.png");
        cards.add("image/activities/discovery/memory_group/memory/08_turtle.png");
        cards.add("image/activities/discovery/memory_group/memory/09_panda.png");
        cards.add("image/activities/discovery/memory_group/memory/10_chicken.png");
        cards.add("image/activities/discovery/memory_group/memory/11_redbird.png");
        cards.add("image/activities/discovery/memory_group/memory/12_wolf.png");
        cards.add("image/activities/discovery/memory_group/memory/13_monkey.png");
        cards.add("image/activities/discovery/memory_group/memory/14_fox.png");
        cards.add("image/activities/discovery/memory_group/memory/15_bluebirds.png");
        cards.add("image/activities/discovery/memory_group/memory/16_elephant.png");
        cards.add("image/activities/discovery/memory_group/memory/17_lion.png");
        cards.add("image/activities/discovery/memory_group/memory/18_gnu.png");
        cards.add("image/activities/discovery/memory_group/memory/19_bluebaby.png");
        cards.add("image/activities/discovery/memory_group/memory/20_greenbaby.png");
        cards.add("image/activities/discovery/memory_group/memory/21_frog.png");
	}

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=9;
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
	    //random cards
	    for (int i = 0; i < cards.size(); ++i) {
	        int first=super.nextInt(cards.size());
	        int second=super.nextInt(cards.size());
	        if (first != second) {
	            String firstCard=cards.get(first);
	            String secondCard=cards.get(second);
	            cards.remove(first);
	            cards.add(first, secondCard);
	            cards.remove(second);
	            cards.add(second, firstCard);
	        }
	    }
	    
	    for (int i = 0; i < rows * cols / 2; ++i) {
	        String card=cards.get(i);
	        //add one card
	        CCSprite sprite=spriteFromExpansionFile(card);
	        sprite.setTag(i);
	        floatingSprites.add(sprite);
	        
	        //add its twin
	        CCSprite twin=spriteFromExpansionFile(card);
	        twin.setTag(i);
	        floatingSprites.add(twin);
	    }
	    super.postInitMemoryGame();
	}
}
