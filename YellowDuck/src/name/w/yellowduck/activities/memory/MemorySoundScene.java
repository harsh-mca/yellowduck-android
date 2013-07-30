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
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor4B;

import android.graphics.Bitmap;

public class MemorySoundScene  extends name.w.yellowduck.activities.memory.MemorySceneBase {
    private java.util.ArrayList<String> clips;
    
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new MemorySoundScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	// on "init" you need to initialize your instance
	public MemorySoundScene() {
		super ();
        clips=new java.util.ArrayList<String>();
        clips.add("image/activities/discovery/memory_group/memory/LRApplauses_1_LA_cut.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRBark_1_LA_cut.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRBark_3_LA_cut.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRBuddhist_gong_05_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRDoor_Open_2_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRFactory_noise_01_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRFactory_noise_02_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRFactory_noise_03_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRFactory_noise_04_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRFactory_noise_05_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRFactory_noise_06_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRFireballs_01_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRFrogsInPondDuringStormLACut.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRHeart_beat_01_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRHits_01_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRLaPause_short.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRObject_falling_01_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRObject_falling_02_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRRain_in_garden_01_LA_cut.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRRing_01_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRStartAndStopCarEngine1LACut.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRTrain_slowing_down_01_LA_cut.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRWeird_1_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRWeird_2_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRWeird_3_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRWeird_4_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRWeird_5_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/LRWeird_6_LA.mp3");
        clips.add("image/activities/discovery/memory_group/memory/guitar_melody.mp3");
//        clips.add("image/activities/discovery/memory_group/memory/guitar_son1.mp3"); //too soft
        clips.add("image/activities/discovery/memory_group/memory/guitar_son2.mp3");
        clips.add("image/activities/discovery/memory_group/memory/guitar_son3.mp3");
        clips.add("image/activities/discovery/memory_group/memory/guitar_son4.mp3");
        clips.add("image/activities/discovery/memory_group/memory/plick.mp3");
        clips.add("image/activities/discovery/memory_group/memory/tachos_melody.mp3");
        clips.add("image/activities/discovery/memory_group/memory/tachos_son1.mp3");
        clips.add("image/activities/discovery/memory_group/memory/tachos_son2.mp3");
        clips.add("image/activities/discovery/memory_group/memory/tachos_son3.mp3");
        clips.add("image/activities/discovery/memory_group/memory/tachos_son4.mp3");
        clips.add("image/activities/discovery/memory_group/memory/tick.mp3");
        clips.add("image/activities/discovery/memory_group/memory/tri.mp3");
	}
	
	public void onEnter() {
		super.onEnter();
	    super.setBackCard("image/activities/discovery/memory_group/memory/Tux_mute.png");
	    super.setEmptyCard("image/activities/discovery/memory_group/memory/Tux_play.png");
	    super.setAudioAttached(true);
	    float xMargin=szWin.width * 0.8f / 3;//left & right margins
	    float yMargin=2; //margin below the page title
	    CGRect rcWorkingArea=CGRect.make(xMargin, szWin.height/2, szWin.width-xMargin*2, szWin.height/2-topOverhead()-yMargin);
	    super.setRcDisplayArea(rcWorkingArea);
	    super.testCardSize();
		
	    mMaxLevel=9;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(),kBgModeStretch);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);

	    //background rect
	    Bitmap bg=super.roundCornerRect((int)rcWorkingArea.size.width, (int)rcWorkingArea.size.height, 0, new ccColor4B(0xff,0xff,0xff,120));
	    CCSprite sprite=CCSprite.sprite(bg, "ma_bg");
	    sprite.setTag(-2);
	    sprite.setPosition(rcWorkingArea.origin.x + rcWorkingArea.size.width/2, rcWorkingArea.origin.y + rcWorkingArea.size.height/2);
	    super.addChild(sprite,0);
	    
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    super.preInitMemoryGame();
	    
	    //random audio clips
	    for (int i = 0; i < clips.size(); ++i) {
	        int first=super.nextInt(clips.size());
	        int second=super.nextInt(clips.size());
	        if (first != second) {
	            String firstCard=clips.get(first);
	            String secondCard=clips.get(second);
	            clips.remove(first);
	            clips.add(first, secondCard);
	            clips.remove(second);
	            clips.add(second, firstCard);
	        }
	    }
	    for (int i = 0; i < rows * cols / 2; ++i) {
	        //add one card
	        CCLabel label=CCLabel.makeLabel(" ", super.sysFontName(),super.smallFontSize());
	        label.setTag(i);
	        label.setUserData(clips.get(i));
	        floatingSprites.add(label);
	        
	        //add its twin
	        CCLabel twin=CCLabel.makeLabel(" ", super.sysFontName(),super.smallFontSize());
	        twin.setTag(i);
	        twin.setUserData(clips.get(i));
	        floatingSprites.add(twin);
	    }
	    super.postInitMemoryGame();
	}
}
