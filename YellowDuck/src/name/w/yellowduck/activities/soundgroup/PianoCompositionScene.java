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

public class PianoCompositionScene extends MusicGameBase {
	private final int kMaxNotes           =40;
    private int noteIds[]=new int[kMaxNotes];
    private int totalNotes;
    
    private float zoom;
    private int staffType, noteType;
    private boolean sharpNotation;
    private int notesPerStave;
    
    
    private CCSprite mask;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new PianoCompositionScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	public void onEnter() {
	    super.setAvailableNavButtons(kOptionIntr|kOptionHelp);
	    super.onEnter();
	    staffType=Note.kStaffTreble;
	    noteType=4;
	    sharpNotation=true;
	    super.afterEnter();
	}

	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    staff=null;
	    totalNotes=0;
	    
	    float buttonHeight=super.buttonSize();
	    float xPos=4, yPos=szWin.height-topOverhead()-4-buttonHeight/2;
	    //staff type switch
	    String icon=null;
	    if (staffType == Note.kStaffTreble) {
	        icon="trebbleclef_button.png";
	    }
	    else if (staffType == Note.kStaffBass) {
	        icon="bassclef_button.png";
	    }
	    if (icon != null) {
	    	String img="image/activities/discovery/sound_group/piano_composition/"+icon;
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(super.buttonize(img));
	        CCSprite spriteSelected=CCSprite.sprite(texture);
	        CCMenuItemSprite menuItem=CCMenuItemImage.item(sprite,spriteSelected,this,"toggleStaffType");
	        menuItem.setScale(buttonHeight / menuItem.getContentSize().height);
	        menuItem.setPosition(xPos + menuItem.getContentSize().width*menuItem.getScale()/2, yPos);
	        CCMenu menu = CCMenu.menu(menuItem);
	        menu.setPosition(0,0);
	        super.addChild(menu,3);
	        floatingSprites.add(menu);
	        
	        xPos += menuItem.getContentSize().width*menuItem.getScale();
	    }
	    //note type selection
	    mask=spriteFromExpansionFile("image/misc/selectionmask.png");
	    mask.setVisible(false);
	    mask.setColor(ccColor3B.ccBLUE);
	    super.addChild(mask, 2);
	    floatingSprites.add(mask);
	    
	    xPos += 16;
	    int noteTypes[]={8,4,2,1};
	    float cxRoom=0;
	    for (int i = 0; i < 4; ++i) {
	        String str=null;
	        if (noteTypes[i] == 8) {
	            str="eighthNoteFlag.png";
	        }
	        else if (noteTypes[i] == 4) {
	            str="quarterNote.png";
	        }
	        else if (noteTypes[i] == 2) {
	            str="halfNote.png";
	        }
	        else {
	            str="wholeNote.png";
	        }
	        String img="image/activities/discovery/sound_group/piano_composition/"+str;
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(super.buttonize(img));
	        CCSprite spriteSelected=CCSprite.sprite(texture);
	        CCMenuItemSprite menuItem=CCMenuItemImage.item(sprite, spriteSelected, this, "changeNoteType");
	        menuItem.setScale(buttonHeight / menuItem.getContentSize().height);
	        menuItem.setPosition(xPos + menuItem.getContentSize().width*menuItem.getScale()/2, yPos);
	        menuItem.setTag(noteTypes[i]);
	        CCMenu menu = CCMenu.menu(menuItem);
	        menu.setPosition(0,0);
	        super.addChild(menu,3);
	        floatingSprites.add(menu);
	        
	        if (noteType == noteTypes[i]) {
	            mask.setPosition(menuItem.getPosition());
	            mask.setVisible(true);
	            mask.setScaleX((menuItem.getContentSize().width * menuItem.getScaleX() +4) / mask.getContentSize().width);
	            mask.setScaleY((menuItem.getContentSize().height * menuItem.getScaleY() +2)/ mask.getContentSize().height);
	        }
	        
	        if (cxRoom <= 0)
	            cxRoom=menuItem.getContentSize().width*menuItem.getScale() + 4;
	
	        xPos += cxRoom;
	    }
	    
	    //sharp or flat
	    xPos += 10;
	    icon=null;
	    if (sharpNotation) {
	        icon="blacksharp.png";
	    }
	    else {
	        icon="blackflat.png";
	    }
	    if (icon != null) {
	    	String img="image/activities/discovery/sound_group/piano_composition/" + icon;
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(super.buttonize(img));
	        CCSprite spriteSelected=CCSprite.sprite(texture);
	        CCMenuItemSprite menuItem=CCMenuItemImage.item(sprite,spriteSelected,this,"toggleAccidentalStyle");
	        menuItem.setScale(buttonHeight / menuItem.getContentSize().height);
	        menuItem.setPosition(xPos + menuItem.getContentSize().width*menuItem.getScale()/2, yPos);
	        CCMenu menu = CCMenu.menu(menuItem);
	        menu.setPosition(0,0);
	        super.addChild(menu,3);
	        floatingSprites.add(menu);
	        
	        xPos += menuItem.getContentSize().width*menuItem.getScale();
	    }
	
	    //The staff
	    float pianoHeight=(szWin.height - topOverhead() - bottomOverhead()/4) / 2;
	    zoom=preferredContentScale(false);
	    staff=new Staff(staffType, CGRect.make(4, yPos - buttonHeight, szWin.width-8, yPos - buttonHeight - pianoHeight), 1);
	    notesPerStave=(int)((szWin.width - Staff.kInitialNoteX*zoom) / (Staff.kNoteSpaceX*zoom)) - 1;
	    
	    staff.beginDraw(this, floatingSprites, zoom);
	    staff.drawStaff(true);
	    for (int i = 0; i < totalNotes; ++i) {
	        Note note=this.createNote(noteIds[i]);
	        staff.drawNote(note, true);
	    }
	    staff.endDraw();
	
	    //the piano
	    CCSprite pianoSprite=spriteFromExpansionFile("image/activities/discovery/sound_group/piano_composition/keyboard.png");
	    pianoSprite.setScaleY(pianoHeight / pianoSprite.getContentSize().height*0.8f);
	    pianoSprite.setScaleX(szWin.width * 2 /3 / pianoSprite.getContentSize().width);
	    pianoSprite.setPosition(szWin.width/2, bottomOverhead() + pianoHeight*0.5f);
	    super.addChild(pianoSprite,1);
	    floatingSprites.add(pianoSprite);
	    
	    float pianoKeyWidth=pianoSprite.getContentSize().width * pianoSprite.getScaleX() / 8;
	    float xLeftPiano=pianoSprite.getPosition().x - pianoSprite.getContentSize().width * pianoSprite.getScaleX() / 2;
	    float yBottomPiano=pianoSprite.getPosition().y - pianoSprite.getContentSize().height * pianoSprite.getScaleY() / 2 + 4;
	    boolean colorButtons=true;
	    for (int i = 1; i <= 8; ++i) {
	        Note toDisplay=new QuarterNote(i, staffType, sharpNotation);
	        if (colorButtons) {
	            toDisplay.setColor(staff.getColorFromID(toDisplay.getNumId()));
	        }
	        else {
	            toDisplay.setColor(ccColor3B.ccWHITE);
	        }
	        this.drawNoteName(toDisplay,xLeftPiano+pianoKeyWidth*i-pianoKeyWidth/2,yBottomPiano,pianoKeyWidth);
	    }
	    //sharp
	    for (int i = -1; i >= -5; --i) {
	        Note toDisplay=new QuarterNote(i, staffType, sharpNotation);
	        if (colorButtons) {
	            toDisplay.setColor(staff.getColorFromID(toDisplay.getNumId()));
	        }
	        else {
	            toDisplay.setColor(ccColor3B.ccWHITE);
	        }
	        float _xPos=xLeftPiano+pianoKeyWidth*(0-i);
	        if (i <= -3) {
	        	_xPos += pianoKeyWidth;
	        }
	        this.drawNoteName(toDisplay,_xPos,yBottomPiano + pianoKeyWidth,pianoKeyWidth*0.8f); // a little small
	    }
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
	
	//Replay the melody created
	public void repeat(Object sender){
	}
	
	public void noteTouched(Object sender) {
	}
	
	public void noteNameTouched(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    int noteId=sender.getTag();
	    
	    if (totalNotes >= kMaxNotes) {
	        for (int i = 0; i < totalNotes - 1; ++i)
	            noteIds[i]=noteIds[i+1];
	        --totalNotes;
	    }
	    noteIds[totalNotes++]=noteId;;
	    //play it
	    Note toPlay=this.createNote(noteId);
	    this.playNote(toPlay);
	    
	    //redraw staff
	    for (int i = floatingSprites.size() - 1; i >= 0; --i) {
	        CCNode note=floatingSprites.get(i);
	        if (note.getTag() == Note.kTagNote) {
	            note.removeFromParentAndCleanup(true);
	        }
	    }
	    staff.beginDraw(this, floatingSprites, zoom);
	    int from=totalNotes - notesPerStave;
	    if (from < 0)
	        from = 0;
	    for (int i = from; i < totalNotes; ++i) {
	        Note one=this.createNote(noteIds[i]);
	        staff.drawNote(one, true);
	    }
	    staff.endDraw();
	}
	
	public void toggleStaffType(Object sender) {
	    if (staffType == Note.kStaffBass)
	        staffType=Note.kStaffTreble;
	    else if (staffType == Note.kStaffTreble)
	        staffType=Note.kStaffBass;
	    this.initGame(false,  null);
	}
	public void changeNoteType(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    noteType=sender.getTag();
	    this.initGame(false,  null);
	}
	public void toggleAccidentalStyle(Object sender) {
	    sharpNotation = !sharpNotation;
	    this.initGame(false,  null);
	}
	private Note createNote(int noteId) {
	    Note toPlay=null;
	    switch (noteType) {
	        case 8:
	            toPlay=new EighthNote(noteId, staffType, sharpNotation);
	            break;
	        case 4:
	            toPlay=new QuarterNote(noteId, staffType, sharpNotation);
	            break;
	        case 2:
	            toPlay=new HalfNote(noteId, staffType, sharpNotation);
	            break;
	        case 1:
	            toPlay=new WholeNote(noteId, staffType, sharpNotation);
	            break;
	    }
	    return toPlay;
	}
	private void playNote(Note note) {
	    String clip="image/activities/discovery/sound_group/" +note.getPitchDir();
	    super.playSound(clip);
	}
}
