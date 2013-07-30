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

import name.w.yellowduck.YDConfiguration;
import name.w.yellowduck.YDSoundEngine;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCDelayTime;
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
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

import android.graphics.Bitmap;

public class PlayRhythmScene extends MusicGameBase {
	private final int kTagCorrectOrWrong          =98;
	private final int kNotReady                   =1000;

	private java.util.ArrayList<Note> notes;
    private int noteTypes[]=new int[10];
    private int totalNotes;
    private int tickingAudioHandle;
    
    private int staffType;
    
    private int playingIdx, tryingIdx;
    private int score;
    private boolean busy, userPlaying, autoPlay;
    private  long playingNoteAt, nextNoteWillStartAt;
    
    private CCSprite noteFocusSprite;
    private CCSprite movingLine;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new PlayRhythmScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public void onEnter() {
	    notes=new java.util.ArrayList<Note>();
	    super.setAvailableNavButtons(kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionRepeatButton);
	    super.onEnter();
	    mMaxLevel=12;
	    super.afterEnter();
	}


	public void onExit() {
		super.stopSoundOrVoice(tickingAudioHandle);
	    super.onExit();
	}
	
	//Override
	protected void beforeLevelChange(Object sender) {
	    //stop playing
	    playingIdx=100;
	}
	
	public void tryAgain(Object _sender) {
	    if (busy)
	        return;
	    for (int i = floatingSprites.size() - 1; i >= 0; --i) {
	        CCNode node=floatingSprites.get(i);
	        if (node.getTag() == kTagCorrectOrWrong) {
	            node.removeFromParentAndCleanup(true);
	            floatingSprites.remove(i);
	        }
	    }
	    tryingIdx=0;
	    this.repeat(null);
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    
        staff=null;

	    notes.clear();
	    super.stopSoundOrVoice(tickingAudioHandle);
	    
	    this.generateInitialRhythmOptions();
	    staffType = Note.kStaffTreble;
	    float zoom=super.preferredContentScale(false);
	
	    float preferredWidth=Staff.kInitialNoteX*zoom + 5 * Staff.kNoteSpaceX*zoom;
	    float xMargin=(szWin.width - preferredWidth)/2;
	    staff=new Staff(staffType, CGRect.make(xMargin, szWin.height-super.topOverhead(), preferredWidth, (szWin.height-super.topOverhead() - super.bottomOverhead())), 1);
	    staff.beginDraw(this, floatingSprites,zoom);
	    staff.drawStaff(true);
	    for (Note note : notes) {
	        staff.drawNote(note, true);
	    }
	    staff.endDraw();
	    //prompt message
	    autoPlay=(mLevel & 1) == 1;
	    //place at the center between the staff top and screen top
	    float yPos=(szWin.height - super.topOverhead() + staff.yTopStaff() + 26)/2;
	    String msg=(autoPlay)?"msg_rhythm_play" : "msg_rhythm_no_play";
	    CCLabel prompt = CCLabel.makeLabel(super.localizedString(msg), super.sysFontName(), smallFontSize());
	    prompt.setColor(ccColor3B.ccBLACK);
	    prompt.setPosition(szWin.width/2, yPos+prompt.getContentSize().height);
	    super.addChild(prompt,1);
	    floatingSprites.add(prompt);
	    
	    //beat counters
	    float yTopStaff=staff.yTopStaff();
	    yPos=yTopStaff + 26;
	    int fontSize=super.smallFontSize();
	    String fontName=super.sysFontName();
	    
	    Bitmap bgCounter=super.roundCornerRect((int)preferredWidth, fontSize*2, 0, new ccColor4B(0xff, 0xff, 0xff, 0xff));
	    CCSprite bgCounterSprite=CCSprite.sprite(bgCounter, "counterbg");
	    bgCounterSprite.setPosition(szWin.width/2, yPos);
	    super.addChild(bgCounterSprite,1);
	    floatingSprites.add(bgCounterSprite);
	    for (Note note : notes) {
	        String beatNums=note.getBeatNums();
	        CCLabel beat = CCLabel.makeLabel(beatNums.substring(0, 1),fontName,super.smallFontSize());
	        beat.setColor(ccColor3B.ccBLACK);
	        beat.setPosition(note.getPosition().x, yPos);
	        super.addChild(beat,2);
	        floatingSprites.add(beat);
	        
	        int steps=beatNums.length();
	        float stepLength=1.0f *Staff.kNoteSpaceX*zoom/steps;
	        for (int i = 0; i < steps - 1; ++i) {
	            float x=note.getPosition().x + (i+1)*stepLength;
	            String str=beatNums.substring(i+1, i+2);
	
	            CCLabel _beat = CCLabel.makeLabel(str, fontName, super.smallFontSize()/2);
	            _beat.setColor(ccColor3B.ccBLACK);
	            _beat.setPosition(x, yPos);
	            super.addChild(_beat, 2);
	            floatingSprites.add(_beat);
	        }
	    }
	    
	    CCLabel counter = CCLabel.makeLabel(super.localizedString("label_beat_count"), fontName, fontSize);
	    counter.setColor(ccColor3B.ccBLACK);
	    counter.setPosition (bgCounterSprite.getPosition().x - bgCounterSprite.getContentSize().width/2  - counter.getContentSize().width/2, yPos);
	    super.addChild(counter,1);
	    floatingSprites.add(counter);
	
	    //the drum
	    yPos=staff.yBottomStaff();
	    String img="image/activities/discovery/sound_group/play_rhythm/drumhead.png";
	    CCSprite spriteDrum=spriteFromExpansionFile(img);
	    CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(super.buttonize(img));
	    CCSprite spriteDrumSelected=CCSprite.sprite(texture);
	    CCMenuItemSprite menuItemDrum=CCMenuItemImage.item(spriteDrum,spriteDrumSelected,this,"beatDrum");
	//    menuItemDrum.scale=[super pixelToContentSize:[super buttonSizePixels]] / menuItemDrum.contentSize.width;
	    menuItemDrum.setPosition(szWin.width/2, yPos - menuItemDrum.getContentSize().height);
	    menuItemDrum.setScale(super.preferredContentScale(true));
	    CCMenu menuDrum = CCMenu.menu(menuItemDrum);
	    menuDrum.setPosition(0,0);
	    super.addChild(menuDrum,3);
	    floatingSprites.add(menuDrum);
	    
	    yPos=menuItemDrum.getPosition().y;
	    //left : metronome
	    if (!autoPlay) {
	    	img="image/activities/discovery/sound_group/play_rhythm/metronome.png";
	    	
		    CCSprite spriteMetronome=spriteFromExpansionFile(img);
		    CCTexture2D texture2=CCTextureCache.sharedTextureCache().addImageExternal(super.buttonize(img));
		    CCSprite spriteMetronomeSelected=CCSprite.sprite(texture2);
		    CCMenuItemSprite menuItemMetronome=CCMenuItemImage.item(spriteMetronome,spriteMetronomeSelected,this,"beatMetronome");
		//    menuItemDrum.scale=[super pixelToContentSize:[super buttonSizePixels]] / menuItemDrum.contentSize.width;
		    menuItemMetronome.setPosition((menuItemDrum.getPosition().x - menuItemDrum.getContentSize().width/2)/2, yPos);
		    CCMenu menuMetronome = CCMenu.menu(menuItemMetronome);
		    menuMetronome.setPosition(0,0);
		    super.addChild(menuMetronome,3);
		    floatingSprites.add(menuMetronome);
	    }
	    
	    float xRightDrum=(menuItemDrum.getPosition().x + menuItemDrum.getContentSize().width/2 + szWin.width)/2;
	    //the delete button
	    String img2="image/activities/discovery/sound_group/piano_composition/edit-clear.png";
	    CCSprite sprite=spriteFromExpansionFile(img2);
	    CCTexture2D texture2=CCTextureCache.sharedTextureCache().addImageExternal(super.buttonize(img2));
	    CCSprite spriteSelected=CCSprite.sprite(texture2);
	    CCMenuItemSprite menuItem=CCMenuItemImage.item(sprite,spriteSelected,this, "tryAgain");
	    menuItem.setScale(super.buttonSize() / menuItem.getContentSize().width);
	    menuItem.setPosition((szWin.width + xRightDrum)/2 + menuItem.getContentSize().width*menuItem.getScale()/2, super.bottomOverhead() + menuItem.getContentSize().height *menuItem.getScale() * 2);
	    CCMenu menu = CCMenu.menu(menuItem);
	    menu.setPosition(0,0);
	    super.addChild(menu,3);
	    floatingSprites.add(menu);
	    //the button background
	    float margin=4;
	    int width=(int)(menuItem.getContentSize().width*menuItem.getScale()+margin);
	    int height=(int)(menuItem.getContentSize().height*menuItem.getScale()+margin);
	    Bitmap bg=super.roundCornerRect(width,height,0,new ccColor4B(0xff, 0x45, 0, 0xff));
	    CCSprite popupBgSprite=CCSprite.sprite(bg, "ctx_button_bg");
	    popupBgSprite.setPosition(menuItem.getPosition());
	    super.addChild(popupBgSprite,1);
	    floatingSprites.add(popupBgSprite);

	    
	    noteFocusSprite=spriteFromExpansionFile("image/activities/discovery/sound_group/piano_composition/note_highlight.png");
	    noteFocusSprite.setVisible(false);
	    super.addChild(noteFocusSprite,Note.zFocus);
	    floatingSprites.add(noteFocusSprite);
	    
	    //moving line
	    movingLine=spriteFromExpansionFile("image/activities/discovery/sound_group/piano_composition/line.png");
	    movingLine.setScaleX(1.0f * (staff.yTopStaff() - staff.yBottomStaff()) / movingLine.getContentSize().width);
	    movingLine.setVisible(false);
	    movingLine.setRotation(90);
	    movingLine.setPosition(0,(staff.yTopStaff() + staff.yBottomStaff())/2);
	    super.addChild(movingLine, Note.zFocus);
	    floatingSprites.add(movingLine);
	
	    userPlaying=false;busy=false;
	    tryingIdx=playingIdx=0;
	    if (autoPlay) {//[1, 3, 5, 7, 9, 11]:
	        tryingIdx=kNotReady;
	        super.performSelector("repeat", 1.0f);
	    }
	}
	
	public boolean repeatIf(Object sender) {
	    if (!busy && !userPlaying && autoPlay) {
	        playingIdx=0;
	        this.repeat(sender);
	    }
	    return true;
	}
	
	//Replay the melody created
	public void repeat(Object sender) {
	    if (super.isShuttingDown())
	        return;
	    
	    if (playingIdx >= totalNotes) {
	        noteFocusSprite.setVisible(false);
	        busy=false;
	        if (tryingIdx == kNotReady)
	            tryingIdx=0;
	    }
	    else {
	        busy=true;
	        Note note=notes.get(playingIdx);
	        //playing note one by one
	        if (!userPlaying || autoPlay) {
	            this.playNote(note);
	        }
	        if (userPlaying && autoPlay) {
	            movingLine.stopAllActions();
	            
	            Note first=notes.get(0);
	            Note second=notes.get(1);
	            float distance=second.getPosition().x - first.getPosition().x;
	            
	            movingLine.setPosition(note.getPosition().x, movingLine.getPosition().y);
	            movingLine.setVisible(true);
	
	            CCMoveTo moveAction = CCMoveTo.action(1.0f*note.getMillisecs()/1000,CGPoint.ccp(note.getPosition().x + distance , movingLine.getPosition().y));
	            CCCallFuncN moveDone = CCCallFuncN.action(this, "hideMe");
	            movingLine.runAction(CCSequence.actions(moveAction, moveDone));
	        }
	        
	        playingNoteAt=this.getTickCount();
	        nextNoteWillStartAt=playingNoteAt+(long)note.getMillisecs();
	        super.performSelector("repeat", 1.0f*note.getMillisecs()/1000);
	        ++playingIdx;
	    }
	}
	public void noteTouched(Object sender) {
	}
	public void noteNameTouched(Object sender) {
	}
	
	public void hideMe(Object sender) {
		CCNode node=(CCNode)sender;
		node.setVisible(false);
	}

	public void beatDrum(Object sender) {
	    if ((busy && tryingIdx <= 0) || (tryingIdx >= notes.size()))
	        return;
	    boolean match=false;
	    if (tryingIdx <= 0) {
	        userPlaying=true;
	
	        playingIdx=0;
	        this.repeat(null);
	        match=true;
	        score=0;
	    }
	    else {
	        long timestamp=this.getTickCount();
	        float diff=0;
	        if (tryingIdx == playingIdx - 1) {
	            diff=timestamp-playingNoteAt;
	        }
	        else if (tryingIdx == playingIdx) {
	            diff=nextNoteWillStartAt-timestamp;
	        }
	        else {
	            diff=10000;
	        }
	        if (diff < 0)
	            diff=0-diff;
	//        NSLog(@"diff=%f, line %f", diff, movingLine.position.x);
	        if (diff < 300)
	            match=true;
	    }
	    if (!autoPlay) {
	        Note note=notes.get(tryingIdx);
	        this.playNote(note);
	    }
	
	    String sign=match?"✔":"✗";
	    Note note=notes.get(tryingIdx);
	    CCLabel label = CCLabel.makeLabel(sign, super.sysFontName(), 16);
	    label.setColor(new ccColor3B(0, 0x80, 0x80));
	    label.setTag(kTagCorrectOrWrong);
	    label.setPosition(note.getPosition());
	    super.addChild(label, 5);
	    floatingSprites.add(label);
	
	    if (match)
	        ++score;
	    if (++tryingIdx >= notes.size()) {
	        if (score >= totalNotes) {
	            flashAnswerWithResult(true, true, null, "note_good.png",2);
	        }
	        else {
	            super.flashMsg(localizedString("msg_rhythm_try_again"), 2);
	        }
	    }
	}
	
	public void beatMetronome(Object sender) {
	    if (busy)
	        return;
	    if (tickingAudioHandle>0) {
	        super.stopSoundOrVoice(tickingAudioHandle);
	        tickingAudioHandle=0;
	    }
	    else {
	        tickingAudioHandle=super.playSoundLooped("image/activities/discovery/sound_group/play_rhythm/click.wav", true);
	    }
	}

	private void generateInitialRhythmOptions() {
	    int sel;
	    switch (mLevel) {
	        case 1:
	        case 2:
	            //options = [[4, 4, 4], [2, 2, 2], [1, 1] ]
	            mMaxSublevel=3;
	            sel=mSublevel;
	            if (sel == 0) {
	                noteTypes[0]=noteTypes[1]=noteTypes[2]=4;
	                totalNotes=3;
	            }
	            else if (sel == 1) {
	                noteTypes[0]=noteTypes[1]=noteTypes[2]=2;
	                totalNotes=3;
	            }
	            else {
	                noteTypes[0]=noteTypes[1]=1;
	                totalNotes=2;
	            }
	            break;
	        case 3:
	        case 4:
	            //options = [ [4, 2], [2, 4], [1, 4], [1, 2], [4, 1]]
	            mMaxSublevel=5;
	            sel=mSublevel;
	            
	            totalNotes=2;
	            if (sel == 0) {
	                noteTypes[0]=4; noteTypes[1]=2;
	            }
	            else if (sel == 1) {
	                noteTypes[0]=2; noteTypes[1]=4;
	            }
	            else if (sel == 2) {
	                noteTypes[0]=1; noteTypes[1]=4;
	            }
	            else if (sel == 3) {
	                noteTypes[0]=1; noteTypes[1]=2;
	            }
	            else  {
	                noteTypes[0]=4; noteTypes[1]=1;
	            }
	            break;
	        case 5:
	        case 6:
	//            options = [ [4, 2, 4], [4, 4, 4], [2, 4, 2], [4, 4, 4], [4, 2, 4], [2, 4, 2]]
	            mMaxSublevel=6;
	            sel=mSublevel;
	            
	            totalNotes=3;
	            if (sel == 0) {
	                noteTypes[0]=4; noteTypes[1]=2; noteTypes[2]=4;
	            }
	            else if (sel == 1) {
	                noteTypes[0]=4; noteTypes[1]=4; noteTypes[2]=4;
	            }
	            else if (sel == 2) {
	                noteTypes[0]=2; noteTypes[1]=4; noteTypes[2]=2;
	            }
	            else if (sel == 3) {
	                noteTypes[0]=4; noteTypes[1]=4; noteTypes[2]=4;
	            }
	            else if (sel == 4) {
	                noteTypes[0]=4; noteTypes[1]=2; noteTypes[2]=4;
	            }
	            else {
	                noteTypes[0]=2; noteTypes[1]=4; noteTypes[2]=2;
	            }
	            break;
	        case 7:
	        case 8:
	//            options = [ [4, 2, 4, 2], [2, 4, 4, 4], [4, 2, 2, 4], [4, 2, 4], [2, 2, 2] ]
	            mMaxLevel=5;
	            sel=mSublevel;
	
	            if (sel == 0) {
	                totalNotes=4;
	                noteTypes[0]=4; noteTypes[1]=2; noteTypes[2]=4;noteTypes[3]=2;
	            }
	            else if (sel == 1) {
	                totalNotes=4;
	                noteTypes[0]=2; noteTypes[1]=4; noteTypes[2]=4;noteTypes[3]=4;
	            }
	            else if (sel == 2) {
	                totalNotes=4;
	                noteTypes[0]=4; noteTypes[1]=2; noteTypes[2]=2;noteTypes[3]=4;
	            }
	            else if (sel == 3) {
	                totalNotes=3;
	                noteTypes[0]=4; noteTypes[1]=2; noteTypes[2]=4;
	            }
	            else {
	                totalNotes=3;
	                noteTypes[0]=2; noteTypes[1]=2; noteTypes[2]=2;
	            }
	            break;
	        case 9:
	        case 10:
	//            options = [ [8, 8, 8, 8], [4, 4, 4, 4], [2, 2, 2, 2], [4, 4, 4, 4] ]
	            mMaxLevel=4;
	            sel=mSublevel;
	            
	            totalNotes=4;
	            if (sel == 0) {
	                noteTypes[0]=noteTypes[1]=noteTypes[2]=noteTypes[3]=8;
	            }
	            else if (sel == 1) {
	                noteTypes[0]=noteTypes[1]=noteTypes[2]=noteTypes[3]=4;
	            }
	            else if (sel == 2) {
	                noteTypes[0]=noteTypes[1]=noteTypes[2]=noteTypes[3]=2;
	            }
	            else {
	                noteTypes[0]=noteTypes[1]=noteTypes[2]=noteTypes[3]=4;
	            }
	            break;
	        case 11:
	        case 12:
	//            options = [ [4, 8, 8, 4], [8, 8, 4, 4], [4, 4, 8, 4], [4, 8, 4, 8]]
	            mMaxLevel=4;
	            sel=mSublevel;
	            
	            totalNotes=4;
	            if (sel == 0) {
	                noteTypes[0]=4;noteTypes[1]=8;noteTypes[2]=8;noteTypes[3]=4;
	            }
	            else if (sel == 1) {
	                noteTypes[0]=8;noteTypes[1]=8;noteTypes[2]=4;noteTypes[3]=4;
	            }
	            else if (sel == 2) {
	                noteTypes[0]=4;noteTypes[1]=4;noteTypes[2]=8;noteTypes[3]=4;
	            }
	            else {
	                noteTypes[0]=4;noteTypes[1]=8;noteTypes[2]=4;noteTypes[3]=8;
	            }
	            break;
	    }
	    for (int i = 0; i < totalNotes; ++i) {
	        Note note=null;
	        if (noteTypes[i] == 8) {
	            note=new EighthNote(1, Note.kStaffTreble, true);
	        }
	        else if (noteTypes[i] == 4) {
	            note=new QuarterNote(1, Note.kStaffTreble, true);
	        }
	        else if (noteTypes[i] == 2) {
	            note=new HalfNote(1, Note.kStaffTreble, true);
	        }
	        else {
	            note=new WholeNote(1, Note.kStaffTreble, true);
	        }
	        notes.add(note);
	    }
	}
	private void playNote(Note note) {
	    String clip="image/activities/discovery/sound_group/"+note.getPitchDir();
	    super.playSound(clip);
	    
	    noteFocusSprite.stopAllActions();
	    noteFocusSprite.setPosition(note.getPosition());
	    noteFocusSprite.setVisible(true);
	    CCDelayTime idleAction = CCDelayTime.action(1.0f*note.getMillisecs()/1000);
	    CCCallFuncN actionDone = CCCallFuncN.action(this, "hideMe");
	    noteFocusSprite.runAction(CCSequence.actions(idleAction, actionDone));
	}
	private long getTickCount() {
		return android.os.SystemClock.currentThreadTimeMillis();
	}
}
