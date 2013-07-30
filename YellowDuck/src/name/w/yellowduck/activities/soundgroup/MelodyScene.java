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


package name.w.yellowduck.activities.soundgroup;

import name.w.yellowduck.Category;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;


public class MelodyScene extends name.w.yellowduck.YDActLayerBase {
	private final int kInstrumentGuitar           =0;
	private final int kInstrumentXylofon          =1;
	
    private int instrument;
    private String instrumentName;
    
    private CCSprite cursorSprite;
    private CGPoint tonePositions[]=new CGPoint[4];
    
    private boolean busy, melodyPlayed;
    int totalNotes;
    private int notes[]=new int[20], tryingNotes[]=new int[20];
    private int tryingIdx, playingIdx;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new MelodyScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=9;

	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionRepeatButton);
	    
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}

	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    
	    instrument=super.nextInt(2);
	//    instrument = 0;
	    if (instrument == kInstrumentGuitar)
	        instrumentName="guitar";
	    else if (instrument == kInstrumentXylofon)
	        instrumentName="xylofon";
	
	    //background
	    String bg=String.format("image/activities/discovery/sound_group/melody/%s_background.jpg", instrumentName);
	    CCSprite bgSprite=super.setupBackground(bg, kBgModeFit2Center);
	    floatingSprites.add(bgSprite);
	    for (int i =1 ; i <= 4; ++i) {
	        String sound=String.format("image/activities/discovery/sound_group/melody/%s_son%d.png", instrumentName, i);
	        CCSprite sprite=spriteFromExpansionFile(sound);
	        CCMenuItemSprite cover=CCMenuItemImage.item(sprite, null, this, "toneTouched");
	        cover.setTag(i);
	        CCMenu menu=CCMenu.menu(cover);
	        menu.setPosition(0,0);
	        
	        if (instrument == kInstrumentGuitar) {
	            cover.setScaleY(bgSprite.getScale()*1.2f);
	            cover.setScaleX(bgSprite.getContentSize().width*bgSprite.getScale()/sprite.getContentSize().width);
	
	            CGPoint points[]={CGPoint.ccp(0, 170),CGPoint.ccp(0, 230),CGPoint.ccp(0, 290),CGPoint.ccp(0, 350)};
	            float yOffset=(points[i-1].y-260)*bgSprite.getScale();
	            
	            cover.setPosition(bgSprite.getPosition().x, bgSprite.getPosition().y-yOffset-sprite.getContentSize().height*sprite.getScale()/2);
	        }
	        else if (instrument == kInstrumentXylofon) {
	            cover.setScale(bgSprite.getScale());
	            //background svg size 800x520
	            CGPoint points[]={CGPoint.ccp(152+18, 101),CGPoint.ccp(284+18, 118),CGPoint.ccp(412+18, 140),CGPoint.ccp(546+18, 157)};
	            float xOffset=(points[i-1].x-400)*bgSprite.getScale();
	            float yOffset=(points[i-1].y-260)*bgSprite.getScale();
	            
	            cover.setPosition(bgSprite.getPosition().x+xOffset+sprite.getContentSize().width*sprite.getScale()/2, bgSprite.getPosition().y-yOffset-sprite.getContentSize().height*sprite.getScale()/2);
	            
	        }
	        super.addChild(menu, 1);
	        floatingSprites.add(menu);
	        tonePositions[i-1]=cover.getPosition();
	
	        cursorSprite=spriteFromExpansionFile("image/activities/discovery/sound_group/melody/"+instrumentName+"_cursor.png");
	        cursorSprite.setVisible(false);
	        cursorSprite.setPosition(bgSprite.getPosition());
	        super.addChild(cursorSprite, 2);
	        floatingSprites.add(cursorSprite);
	    }
	    totalNotes=mLevel+2;
	    for (int i = 0; i < totalNotes; ++i) {
	        notes[i]=super.nextInt(4)+1;
	    }
	    tryingIdx=playingIdx=0;
	    melodyPlayed=false;
	    
	    mLevelLabel.setString (""+mLevel);
	    super.performSelector("repeat", 1.0f);
	}
	//Override
	protected void beforeLevelChange(Object sender) {	    //stop playing
	    playingIdx=100;
	}
	public boolean repeatIf(Object sender) {
	    if (!busy) {
	        playingIdx=0;
	        melodyPlayed=false;
	        this.repeat(sender);
	    }
	    return true; //event consumed
	}
	//Replay the melody created
	public void repeat(Object _sender) {
	    if (super.isShuttingDown())
	        return;
	    if (playingIdx >= totalNotes) {
	        cursorSprite.setVisible(false);
	        busy=false;
	        tryingIdx=0;
	    }
	    else {
	        busy=true;
	        if (!melodyPlayed) {
	            melodyPlayed=true;
	            
	            String clip=String.format("image/activities/discovery/sound_group/melody/%s_melody.mp3", instrumentName);
	            super.playSound(clip);
	            super.performSelector("repeat", 0.7f);
	        }
	        else {
	            String clip=String.format("image/activities/discovery/sound_group/melody/%s_son%d.mp3", instrumentName,notes[playingIdx]);
	            super.playSound(clip);
	            
	            cursorSprite.setPosition(tonePositions[notes[playingIdx]-1]);
	            cursorSprite.setVisible(true);
	            CCDelayTime idleAction = CCDelayTime.action(0.8f);
	            CCCallFuncN actionHide = CCCallFuncN.action(this, "hideMe");
	            CCDelayTime idleAction2 = CCDelayTime.action(0.2f);
	            CCCallFuncN actionDone = CCCallFuncN.action(this, "repeat");
	            cursorSprite.runAction(CCSequence.actions(idleAction, actionHide, idleAction2, actionDone));
	
	            ++playingIdx;
	        }
	    }
	}
	
	public void hideMe(Object _sender){
		CCNode sender=(CCNode)_sender;
	    sender.setVisible(false);
	}
	
	public void toneTouched(Object _sender) {
	    if (busy)
	        return;
	    CCNode sender=(CCNode)_sender;
	    int tone=sender.getTag();
	    String clip=String.format("image/activities/discovery/sound_group/melody/%s_son%d.mp3", instrumentName,tone);
	    super.playSound(clip);
	    
	    cursorSprite.stopAllActions();
	    cursorSprite.setPosition(tonePositions[tone-1]);
	    cursorSprite.setVisible(true);
	    CCDelayTime idleAction = CCDelayTime.action(0.5f);
	    CCCallFuncN actionDone = CCCallFuncN.action(this, "hideMe");
	    cursorSprite.runAction(CCSequence.actions(idleAction, actionDone));
	    if (tryingIdx >= totalNotes) {
	        for (int i = 0; i < totalNotes; ++i) {
	            tryingNotes[i]=tryingNotes[i+1];
	        }
	        --tryingIdx;
	    }
	    tryingNotes[tryingIdx++]=tone;
	    super.performSelector("checkAnswer", 0.7f);
	}
	public void checkAnswer() {
	    if (tryingIdx == totalNotes) {
	        boolean matched=true;
	        for (int i = 0; i < totalNotes; ++i) {
	            if (tryingNotes[i] != notes[i]) {
	                matched=false;
	                break;
	            }
	        }
	        if (matched) {
	            super.flashAnswerWithResult(true, true, null, null, 1);
	        }
	    }
	}
}
