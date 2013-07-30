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
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

import android.graphics.Bitmap;

public class PlayPianoScene extends MusicGameBase {
	private final int kTagCorrectOrWrong          =98; //the note tag is 100

	private java.util.ArrayList<Note> notes;
	private int noteIds[]=new int[10];//up to four notes
    private int totalNotes;
    
    private int staffType;
    
    private int score;
    private int playingIdx, tryingIdx;
    private boolean busy;
    
    private CCSprite noteFocusSprite;
	
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new PlayPianoScene();
	 
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
	    score=0;
	    this.repeat(null);
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    staff=null;
	    
	    notes.clear();
	    
	    this.generateMelody();
	    staffType = (mLevel <= 6) ? Note.kStaffTreble : Note.kStaffBass;
	    float zoom=super.preferredContentScale(false);
	
	    float preferredWidth=Staff.kInitialNoteX*zoom + 5 * Staff.kNoteSpaceX*zoom;
	    float xMargin=(szWin.width - preferredWidth)/2;
	    staff=new Staff(staffType, CGRect.make(xMargin, szWin.height-super.topOverhead(), preferredWidth, (szWin.height-super.topOverhead() - super.bottomOverhead())*0.6f), 1);
	    staff.beginDraw(this, floatingSprites, zoom);
	    staff.drawStaff(true);
	    for (int i = 0; i < totalNotes; ++i) {
	        Note note=new QuarterNote(noteIds[i], staffType, true);
	        staff.drawNote(note, (mLevel < 12));
	        notes.add(note);
	    }
	    staff.endDraw();
	    
	    float yTopStaff=staff.yTopStaff();
	    String msg=super.localizedString("msg_play_piano");
	    CCLabel prompt = CCLabel.makeLabel(msg, super.sysFontName(), super.smallFontSize());
	    prompt.setColor(ccColor3B.ccBLACK);
	    prompt.setPosition(2+prompt.getContentSize().width/2, yTopStaff + (szWin.height - super.topOverhead() - yTopStaff)/ 2);
	    super.addChild(prompt,1);
	    floatingSprites.add(prompt);
	
	    float yPos=staff.yBottomStaff();
	    float yRoom=yPos - super.bottomOverhead() - 4;
	    CCSprite pianoSprite=spriteFromExpansionFile("image/activities/discovery/sound_group/piano_composition/keyboard.png");
	    pianoSprite.setScaleY(yRoom / pianoSprite.getContentSize().height*0.8f);
	    pianoSprite.setScaleX(szWin.width * 2 /3 / pianoSprite.getContentSize().width);
	    pianoSprite.setPosition(szWin.width/2, yPos - pianoSprite.getContentSize().height*pianoSprite.getScaleY()/2);
	    super.addChild(pianoSprite,1);
	    floatingSprites.add (pianoSprite);
	    
	    
	    float pianoKeyWidth=pianoSprite.getContentSize().width * pianoSprite.getScaleX() / 8;
	    float xLeftPiano=pianoSprite.getPosition().x - pianoSprite.getContentSize().width * pianoSprite.getScaleX() / 2;
	    float yBottomPiano=pianoSprite.getPosition().y - pianoSprite.getContentSize().height * pianoSprite.getScaleY() / 2 + 4;
	    boolean colorButtons=true;
	    for (int i = 1; i <= 8; ++i) {
	        Note toDisplay=new QuarterNote(i, staffType, true);
	        if (colorButtons) {
	            toDisplay.setColor(staff.getColorFromID(i));
	        }
	        else {
	            toDisplay.setColor(ccColor3B.ccWHITE);
	        }
	        this.drawNoteName(toDisplay, xLeftPiano+pianoKeyWidth*i-pianoKeyWidth/2,yBottomPiano,pianoKeyWidth);
	    }
	    if ((mLevel >= 4 && mLevel <= 6) || (mLevel >= 10 && mLevel <= 12)) {
	        //sharp
	        for (int i = -1; i >= -5; --i) {
	            Note toDisplay=new QuarterNote(i, staffType, true);
	            if (colorButtons) {
		            toDisplay.setColor(staff.getColorFromID(i));
	            }
	            else {
	                toDisplay.setColor(ccColor3B.ccWHITE);
	            }
	            float xPos=xLeftPiano+pianoKeyWidth*(0-i);
	            if (i <= -3) {
	                xPos += pianoKeyWidth;
	            }
	            this.drawNoteName(toDisplay,xPos,yBottomPiano + pianoKeyWidth,pianoKeyWidth*0.8f); // a little small
	        }
	    }
	    
	    float xRightPiano=pianoSprite.getPosition().x + pianoSprite.getContentSize().width*pianoSprite.getScaleX()/2;
	    
	    //the delete button
	    String img="image/activities/discovery/sound_group/piano_composition/edit-clear.png";
	    CCSprite sprite=spriteFromExpansionFile(img);
	    CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(super.buttonize(img));
	    CCSprite spriteSelected=CCSprite.sprite(texture);
	    CCMenuItemSprite menuItem=CCMenuItemImage.item(sprite,spriteSelected,this, "tryAgain");
	    menuItem.setScale(super.buttonSize() / menuItem.getContentSize().width);
	    menuItem.setPosition((szWin.width + xRightPiano)/2 + menuItem.getContentSize().width*menuItem.getScale()/2, super.bottomOverhead() + menuItem.getContentSize().height *menuItem.getScale() * 2);
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
	    
	    tryingIdx=playingIdx=0;
	    score=0;
	    super.performSelector("repeat", 1.0f);
	}
	private CGSize drawNoteName(Note note, float x, float y, float fit2) {
	    String name=staff.getKeyNameFromID(note.getNumId(), note.isSharpNotation());
	    CCLabel nameLabel = CCLabel.makeLabel(name, super.sysFontName(), (note.getNumId() < 0) ?super.smallFontSize(): super.mediumFontSize());
	    nameLabel.setColor(ccColor3B.ccBLACK);
	    if (nameLabel.getContentSize().width > fit2)
	        nameLabel.setScale(fit2/nameLabel.getContentSize().width);
	    nameLabel.setPosition(x,y+nameLabel.getContentSize().height * nameLabel.getScale()/2);
	    super.addChild(nameLabel,3);
	    floatingSprites.add(nameLabel);
	
	    int width=(int)(fit2*0.8f);
	    int height=(int)(nameLabel.getContentSize().height*nameLabel.getScale()*1.2f);
	    if (note.getNumId() < 0)
	        height = (int)(width * 1.3f);
	    String bgImg="image/activities/discovery/sound_group/piano_composition/notenamebg.png";
	    String bgImgSelected="image/activities/discovery/sound_group/piano_composition/notenamebgselected.png";
	    CCMenuItemSprite menuItem=CCMenuItemImage.item(super.spriteFromExpansionFile(bgImg),
	    		super.spriteFromExpansionFile(bgImgSelected),this,"noteNameTouched");
	    menuItem.setTag(note.getNumId());
	    menuItem.setScaleX(width / menuItem.getContentSize().width);
	    menuItem.setScaleY(height / menuItem.getContentSize().height);
	    menuItem.setColor(note.getColor());
	    menuItem.setPosition(x,y+menuItem.getContentSize().height*menuItem.getScaleY()/2);
	    CCMenu menu = CCMenu.menu(menuItem);
	    menu.setPosition(0,0);
	
	    super.addChild(menu, 1);
	    floatingSprites.add(menu);
	    
	    //relocate the label
	    nameLabel.setPosition(menuItem.getPosition());
	
	    return CGSize.make(width, height);
	}
	public boolean repeatIf(Object sender) {
	    if (!busy) {
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
	    }
	    else {
	        busy=true;
	        Note note=notes.get(playingIdx);
	        String clip="image/activities/discovery/sound_group/"+note.getPitchDir();
	        super.playSound(clip);
	
	        noteFocusSprite.setPosition(note.getPosition());
	        noteFocusSprite.setVisible(true);
	        
	        CCDelayTime idleAction = CCDelayTime.action(note.getMillisecs()/1000);
	        CCCallFuncN actionDone = CCCallFuncN.action(this, "repeat");
	        noteFocusSprite.runAction(CCSequence.actions(idleAction, actionDone));
	        
	        ++playingIdx;
	    }
	}
	
	public void noteTouched(Object sender) {
	}
	
	public void noteNameTouched(Object _sender) {
	    if (busy || tryingIdx >= totalNotes)
	        return;
	    CCNode sender=(CCNode)_sender;
	    Note sample=notes.get(notes.size()-1); //there is only one
	    Note toPlay = new QuarterNote(sender.getTag(), sample.getStaffType(), sample.isSharpNotation());
	    String clip="image/activities/discovery/sound_group/"+ toPlay.getPitchDir();
	    super.playSound(clip);
	    
	   String sign=null;
	    if (sender.getTag() == noteIds[tryingIdx]) {
	        ++score;
	        sign="✔";
	    }
	    else {
	        //wrong
	        --score;
	        sign="✗";
	    }
	    Note note=notes.get(tryingIdx);
	    CCLabel label = CCLabel.makeLabel(sign, super.sysFontName(), 16);
	    label.setColor(new ccColor3B(0, 0x80, 0x80));
	    label.setTag(kTagCorrectOrWrong);
	    label.setPosition(note.getPosition());
	    super.addChild(label,5);
	    floatingSprites.add(label);
	    
	    if (++tryingIdx >= totalNotes) {
	        if (score >= totalNotes) {
	            super.flashAnswerWithResult(true, true, null, "note_good.png",2);
	        }
	        else {
	            super.flashAnswerWithResult(false, false, null, "note_bad.png",2);
	            super.performSelector("tryAgain", 2);
	        }
	    }
	}
	
	private void generateMelody() {
	    int options[]=new int[20];
	    int notenum = 3;
	    int tail;
	    for (tail = 0; tail < notenum; ++tail)
	        options[tail]=tail+1;
	    if ((mLevel >= 2 && mLevel <= 6) || (mLevel >= 8 && mLevel <= 12)) {
	        notenum=3;
	        options[tail++]=4;
	        options[tail++]=5;
	        options[tail++]=6;
	    }
	    if ((mLevel >= 3 && mLevel <= 6) || (mLevel >= 9 && mLevel <= 12)) {
	        notenum=4;
	        options[tail++]=7;
	        options[tail++]=8;
	    }
	    if ((mLevel >= 4 && mLevel <= 6) || (mLevel >= 10 && mLevel <= 12)) {
	        options[tail++]=-1;
	        options[tail++]=-2;
	    }
	    if ((mLevel >= 5 && mLevel <= 6) || (mLevel >= 11 && mLevel <= 12)) {
	        options[tail++]=-3;
	        options[tail++]=-4;
	        options[tail++]=-5;
	    }
	    totalNotes=notenum;
	    for (int i = 0; i < totalNotes; ++i) {
	        int sel=super.nextInt(tail);
	        noteIds[i]=options[sel];
	    }
	}
}
