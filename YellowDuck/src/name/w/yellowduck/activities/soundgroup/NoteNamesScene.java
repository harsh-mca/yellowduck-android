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

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCDelayTime;
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
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

public class NoteNamesScene extends MusicGameBase {
	private java.util.ArrayList<Note> notes;
    
    private boolean  busy;
    private int totalNotes;

    private int playingIdx;    
    private int pitchPossibilities[]=new int[20], totalPitchPossibilities;
    private int selectedNoteId;
    private float noteNameWidth, noteNameHeight;

    private CCSprite noteFocusSprite;
    private CCSprite mask; //user selection indicator
    private boolean  pitchSoundEnabled;

	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new NoteNamesScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	public void onEnter() {
	    notes=new java.util.ArrayList<Note>();
	    super.setAvailableNavButtons(kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionRepeatButton|kOptionOk);
	    super.onEnter();
	    mMaxLevel=20;
	    super.afterEnter();
	}
	
	//Override
	protected void beforeLevelChange(Object sender) {
	    //stop playing
	    playingIdx=100;
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    staff=null;

		notes.clear();
	    mask=null;
	    
	    String msg=null;
	    float zoom=super.preferredContentScale(false), yTopStaff=0;
	    int staffType=(mLevel < 11) ? Note.kStaffTreble : Note.kStaffBass;
	    boolean sharpNotation=false;
	    selectedNoteId=1000;
	    switch (mLevel) {
	        case 1:
	        case 11:
	        {
	            float preferredWidth=Staff.kInitialNoteX*zoom + 9 * Staff.kNoteSpaceX*zoom;
	            float xMargin=(szWin.width - preferredWidth)/2;
	            staff=new Staff(staffType,CGRect.make(xMargin, szWin.height-super.topOverhead()-super.mediumFontSize()*2, preferredWidth, szWin.height-super.topOverhead() - bottomOverhead()),1);
	            staff.beginDraw(this, floatingSprites, zoom);
	            staff.drawStaff(false);
	            java.util.ArrayList<Note> notesCreated=staff.drawScale("C Major");
	            notes.addAll(notesCreated);
	            staff.endDraw();
	            
	            yTopStaff=staff.yTopStaff();
	            float yPos=staff.yBottomStaff() - 15;//staff line spacing is 13
	            for (Note one : notes) {
	                this.drawNoteName(one, one.getPosition().x, yPos, zoom, Staff.kNoteSpaceX*0.85f*zoom);
	            }
	            msg=(mLevel==1)?"msg_treble_basic":"msg_bass_basic";
	            pitchSoundEnabled=true;
	        }
	            break;
	        case 2:
	        case 3:
	        case 4:
	        case 12:
	        case 13:
	        case 14:
	            msg="msg_note_name_pitch";
	            break;
	        case 5:
	        case 6:
	        case 7:
	        case 15:
	        case 16:
	        case 17:
	            msg="msg_sharp_notes";
	            sharpNotation=true;
	            break;
	        case 8:
	        case 9:
	        case 10:
	        case 18:
	        case 19:
	        case 20:
	            msg="msg_flat_notes";
	            break;
	    }
	    if (mLevel != 1 && mLevel != 11) {
	        boolean colorButtons = (mLevel == 2 || mLevel==5 || mLevel==8 || mLevel==12 || mLevel== 15 || mLevel==18);
	        //Press A,B to play corresponding pitch
	        pitchSoundEnabled = !(mLevel == 4 || mLevel==7 || mLevel==10 || mLevel==14 || mLevel== 17 || mLevel==20);
	
	        totalPitchPossibilities=8;
	        for (int i = 0; i < totalPitchPossibilities; ++i)
	            pitchPossibilities[i]=i+1;
	        //[5, 6, 7, 8, 9, 10, 15, 16, 17, 18, 19, 20]
	        if ((mLevel >=5 && mLevel <= 10) || (mLevel >=15 && mLevel <= 20)) {
	            for (int i = 0; i < 5; ++i)
	                pitchPossibilities[totalPitchPossibilities++]=0 - (i + 1);
	        }
	
	        int idx=super.nextInt(totalPitchPossibilities);
	        Note note = new QuarterNote(pitchPossibilities[idx], staffType, sharpNotation);
	        notes.add(note);
	        
	        float preferredWidth=Staff.kInitialNoteX*zoom + 2*Staff.kNoteSpaceX*zoom;
	        float xMargin=szWin.width/10;
	        staff=new Staff(staffType, CGRect.make(xMargin, szWin.height-topOverhead()-super.mediumFontSize()*2, preferredWidth, szWin.height-topOverhead() - bottomOverhead()),1);
	        staff.beginDraw(this, floatingSprites, zoom);
	        staff.drawStaff(true);
	        staff.drawNote(note, colorButtons);
	        staff.endDraw();
	        
	        yTopStaff=staff.yTopStaff();
	        
	        float x0=xMargin + preferredWidth*2f;
	        float xPos=x0;
	        float yPos=staff.yTopStaff();
	        
	        float maxNameWidth=(szWin.width - x0) / 7;
	
	        for (int i = 0; i < totalPitchPossibilities; ++i) {
	            //do not draw another C-button, since this is ambiguous, because
	            //we have a c' and a c"
	            if (pitchPossibilities[i] != 8) {
	                Note toDisplay = new QuarterNote(pitchPossibilities[i], note.getStaffType(), note.isSharpNotation());
	                if (colorButtons) {
	                    toDisplay.setColor(staff.getColorFromID(toDisplay.getNumId()));
	                }
	                else {
	                    toDisplay.setColor(ccColor3B.ccWHITE);
	                }
	                this.drawNoteName(toDisplay, xPos, yPos,zoom, maxNameWidth*0.8f);
	
	                xPos += maxNameWidth;
	            }
	            if (i == (totalPitchPossibilities/2-1)) {
	                xPos = x0;
	                yPos = staff.yBottomStaff() + (staff.yTopStaff() - staff.yBottomStaff())/2;
	            }
	        }
	        
	        mask=spriteFromExpansionFile("image/misc/selectionmask.png");
	        mask.setVisible(false);
	        super.addChild(mask,2);
	        floatingSprites.add(mask);
	    }
	    //multiple lines label
	    android.graphics.Bitmap img=super.createMultipleLineLabel(super.localizedString(msg), super.sysFontName(), super.smallFontSize(), (int)(szWin.width*0.7f), ccColor3B.ccBLACK, ccColor4B.ccc4(0, 0, 0, 0));
		CCTextureCache.sharedTextureCache().removeTexture("msg_bg");//remove previous one if exists
		CCSprite flashContent=CCSprite.sprite(img, "msg_bg");
		float yPos=0;
		if (mLevel == 11) {
			//align to top
			yPos=szWin.height-super.topOverhead()-flashContent.getContentSize().height/ 2;
		}
		else {
			//center between the staff and top bar
			yPos=yTopStaff + (szWin.height - topOverhead() - yTopStaff)/ 2;
		}
		flashContent.setPosition(szWin.width/2, yPos);
	    super.addChild(flashContent,1);
	    floatingSprites.add(flashContent);
	
	    noteFocusSprite=super.spriteFromExpansionFile("image/activities/discovery/sound_group/piano_composition/note_highlight.png");
	    noteFocusSprite.setVisible(false);
	    super.addChild(noteFocusSprite, Note.zFocus);
	    floatingSprites.add(noteFocusSprite);
	    
	    playingIdx=0;
	    super.performSelector("repeat",  1);
	}
	private CGSize drawNoteName(Note note, float x, float y, float zoom, float maxWidth) {
	    String name=staff.getKeyNameFromID(note.getNumId(), note.isSharpNotation());
	    CCLabel nameLabel = CCLabel.makeLabel(name, super.sysFontName(), (note.getNumId() < 0) ?super.smallFontSize(): super.mediumFontSize());
	    nameLabel.setColor(ccColor3B.ccBLACK);
	    nameLabel.setPosition(x,y);
	    super.addChild(nameLabel,3);
	    floatingSprites.add(nameLabel);
	
	    int width=(int)((maxWidth > 0)?maxWidth:Staff.kNoteSpaceX*0.85f*zoom);
	    int height=(int)(nameLabel.getContentSize().height * 1.2f);
	    
	    String bgImg="image/activities/discovery/sound_group/piano_composition/notenamebg.png";
	    String bgImgSelected="image/activities/discovery/sound_group/piano_composition/notenamebgselected.png";
	    CCMenuItemSprite menuItem=CCMenuItemImage.item(super.spriteFromExpansionFile(bgImg),
	    												super.spriteFromExpansionFile(bgImgSelected),this,"noteNameTouched");
	    menuItem.setTag(note.getNumId());
	    menuItem.setScaleX(width / menuItem.getContentSize().width);
	    menuItem.setScaleY(height / menuItem.getContentSize().height);
	    menuItem.setColor(note.getColor());
	    menuItem.setPosition(x,y);
	    CCMenu menu = CCMenu.menu(menuItem);
	    menu.setPosition(0,0);
	
	    super.addChild(menu, 1);
	    floatingSprites.add(menu);
	    
	    noteNameWidth=width; noteNameHeight=height;

	    return CGSize.make(width, height);
	}
	
	public boolean repeatIf(Object sender) {
	    if (!busy) {
	        playingIdx=0;
	        this.repeat(null);
	    }
	    return true;
	}
	
	//Replay the melody created
	public void repeat(Object _sender) {
	    if (super.isShuttingDown())
	        return;
	    
	    totalNotes=notes.size();
	    if (playingIdx >= totalNotes) {
	        noteFocusSprite.setVisible(false);
	        busy=false;
	    }
	    else {
	        busy=true;
	        Note note=notes.get(playingIdx);
	        String clip="image/activities/discovery/sound_group/" +note.getPitchDir();
	        super.playSound(clip);
	
	        noteFocusSprite.setPosition(note.getPosition());
	        noteFocusSprite.setVisible(true);
	        
	        CCDelayTime idleAction = CCDelayTime.action(1.0f *note.getMillisecs()/1000);
	        CCCallFuncN actionDone = CCCallFuncN.action(this, "repeat");
	        noteFocusSprite.runAction(CCSequence.actions(idleAction, actionDone));
	        
	        ++playingIdx;
	    }
	}
	
	public void noteTouched(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    Note note=(Note)sender.getUserData();
	    if (!busy) {
	        String clip="image/activities/discovery/sound_group/" + note.getPitchDir();
	        super.playSound(clip);
	        
	        noteFocusSprite.setPosition(note.getPosition());
	        noteFocusSprite.setVisible(true);
	        
	        CCDelayTime idleAction = CCDelayTime.action(1.0f*note.getMillisecs()/1000);
	        CCCallFuncN actionDone = CCCallFuncN.action(this, "hideMe");
	        noteFocusSprite.runAction(CCSequence.actions(idleAction, actionDone));
	    }
	}
	public void noteNameTouched(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    if (busy)
	        return;
	    if (mLevel != 1 && mLevel != 11) {
	        mask.setScaleX(noteNameWidth/mask.getContentSize().width*0.9f);
	        mask.setScaleY(noteNameHeight/mask.getContentSize().height *0.9f);
	        mask.setPosition(sender.getPosition());
	        mask.setVisible(true);
	
	        selectedNoteId=sender.getTag();
	    }
	    if (pitchSoundEnabled) {
	        Note sample=notes.get(notes.size()-1); //there is only one
	        Note toPlay = new QuarterNote(sender.getTag(), sample.getStaffType(), sample.isSharpNotation());
	        String clip="image/activities/discovery/sound_group/" + toPlay.getPitchDir();
	        super.playSound(clip);
	    }
	}
	
	public void hideMe(Object _sender) {
		CCNode sender=(CCNode)_sender;
		sender.setVisible(false);
	}

	public void ok(Object _sender) {
	    if (mLevel != 1 && mLevel != 11) {
	        if (selectedNoteId >= 100) {
	            //no note is selected yet
	            super.playVoice("misc/check_answer.mp3");
	        }
	        else {
	            Note target=notes.get(notes.size()-1);
	            boolean answerCorrect=(selectedNoteId == target.getNumId()) || (target.getNumId()==8 && selectedNoteId==1);
	            if (answerCorrect)
	                super.flashAnswerWithResult(answerCorrect, answerCorrect, null, "note_good.png", 2);
	            else {
	                super.flashAnswerWithResult(answerCorrect, answerCorrect, null, "note_bad.png", 2);
	            }
	        }
	    }
	}
}
