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

import name.w.yellowduck.LineSprite;
import name.w.yellowduck.YDLayerBase;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4F;

public class Note extends Object {
	public static final int kStaffTreble        =0;
	public static final int kStaffBass          =1;

	//background has z-order 0
	public static final int zStaff              =1;
	public static final int zNote               =2;
	public static final int zFocus              =3;

	public static final int kTagNote            =100;
	
    private String imgFile;
    private boolean rotation;
    private CGPoint anchorPoint;

    private int numId;
    private int staffType;
    private boolean sharpNotation;
    private float millisecs;
    private String beatNums;
    private boolean tupleBound;

    private CGPoint position;//note head position
    private ccColor3B color;

    //the note type you're currently using to write to the musical staff, could be 4 (quarter), 8 (eighth), 2 (half) or 1 (whole)
    private int noteType;

    private Object userObject;
    
    public Note(int _numId, int _staff, boolean _sharpNotation) {
    	super();
    	this.numId=_numId;
    	this.staffType=_staff;
    	this.sharpNotation=_sharpNotation;
    }
    
    public int getNumId() {
		return numId;
	}
	public void setNumId(int numId) {
		this.numId = numId;
	}
	public float getMillisecs() {
		return millisecs;
	}
	public void setMillisecs(float millisecs) {
		this.millisecs = millisecs;
	}
	public String getBeatNums() {
		return beatNums;
	}
	public void setBeatNums(String beatNums) {
		this.beatNums = beatNums;
	}
	public boolean isTupleBound() {
		return tupleBound;
	}
	public void setTupleBound(boolean tupleBound) {
		this.tupleBound = tupleBound;
	}
	public CGPoint getPosition() {
		return position;
	}
	public void setPosition(CGPoint position) {
		this.position = position;
	}
	public int getNoteType() {
		return noteType;
	}
	public void setNoteType(int noteType) {
		this.noteType = noteType;
	}
	public Object getUserObject() {
		return userObject;
	}
	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}
	public int getStaffType() {
		return staffType;
	}
	public void setStaffType(int type) {
		this.staffType = type;
	}
	public ccColor3B getColor() {
		return color;
	}
	public void setColor(ccColor3B color) {
		this.color = color;
	}
	public boolean isSharpNotation() {
		return sharpNotation;
	}
	public void setSharpNotation(boolean b) {
		this.sharpNotation = b;
	}

	private CCNode _drawMidLine(CCLayer parent, float x, float y, float sz) {
	    if ((staffType == kStaffTreble && (numId == 1 || (numId == -1 && sharpNotation))) ||
	        (staffType == kStaffBass && (numId == 1 || numId == 8))) {
	        LineSprite sprite=new LineSprite(CGPoint.ccp(x-sz/2,y),CGPoint.ccp(x+sz/2, y));
	        sprite.setClr(new ccColor4F(1.0f*0x12/255, 1.0f*0x12/255, 1.0f*0x12/255, 1.0f*0xd0/255));
	        sprite.setTag(kTagNote);
	        parent.addChild(sprite,zNote+1);
	
	        return sprite;
	    }
	    return null;
	}
	
	/*
	 draws a flat or a sharp sign in front of the note if needed
	 width and height specifications needed because these images
	 need to be so small that scaling any larger image to the correct
	 size makes them extremely blury.
	*/ 
	private CCNode _drawAlteration(YDLayerBase parent, float x, float y, float sz) {
	    CCSprite sprite=null;
	    boolean flat=false;
	    if (numId < 0) {
	        if (sharpNotation) {
	            sprite=parent.spriteFromExpansionFile("image/activities/discovery/sound_group/piano_composition/blacksharp.png");
	        }
	        else {
	            sprite=parent.spriteFromExpansionFile("image/activities/discovery/sound_group/piano_composition/blackflat.png");
	            flat=true;
	        }
	    }
	    if (sprite!=null) {
	        sprite.setScale(sz/sprite.getContentSize().width);
	        sprite.setPosition(x-sprite.getContentSize().width*sprite.getScaleX()/2, flat?y+sprite.getContentSize().height*sprite.getScaleY()/4:y);
	        sprite.setTag(kTagNote);
	        parent.addChild(sprite,zNote);
	    }
	    return sprite;
	}
	public void draw(YDLayerBase parent, float x, float y, ccColor3B clr, float zoom, java.util.ArrayList<CCNode> floatSprites) {
	    this.color=clr;
	    
	    String img="image/activities/discovery/sound_group/piano_composition/" + imgFile;
	    CCSprite noteHead=parent.spriteFromExpansionFile(img);
	    noteHead.setScale(13.0f*zoom / (anchorPoint.y * 2));//staffLineSpacing=13;
	    CCSprite noteHeadDn=parent.spriteFromExpansionFile(img);
	    noteHeadDn.setScale(noteHead.getScale());
	    CCMenuItemSprite cover=CCMenuItemImage.item(noteHead,  noteHeadDn, parent,"noteTouched");
	    cover.setUserData(this);
	    cover.setColor(clr);
	    float toBottom=anchorPoint.y*cover.getScaleY();
	    if (rotation) {
	        cover.setRotation(180);
	        float toTop=toBottom;
	        cover.setPosition(x, y+toTop-cover.getContentSize().height*cover.getScaleY()/2);
	    }
	    else {
	        cover.setPosition(x, y-toBottom+cover.getContentSize().height*cover.getScaleY()/2);
	    }
	    CCMenu menu=CCMenu.menu(cover);
	    menu.setTag(kTagNote);
	    menu.setPosition(0,0);
	
	    parent.addChild(menu, zNote);
	    floatSprites.add(menu);
	    
	    if (noteType == 8) {
	        String flag=rotation?"eighthNoteRFlag.png":"eighthNoteFlag.png";
	        CCSprite flagSprite=parent.spriteFromExpansionFile("image/activities/discovery/sound_group/piano_composition/"+flag);
	        flagSprite.setScaleX(cover.getScaleX());
	        flagSprite.setScaleY(cover.getScaleY());
	        flagSprite.setPosition(cover.getPosition());
	        flagSprite.setColor(cover.getColor());
	        flagSprite.setTag(kTagNote);
	        parent.addChild(flagSprite,zNote+1);
	        floatSprites.add(flagSprite);
	
	        this.setUserObject(flagSprite);
	    }
	    this.position=CGPoint.ccp(x,y);//the notehead position
	    
	    float noteWidth=cover.getContentSize().width*cover.getScaleX();
	    float noteheadWidth=noteWidth;
	    if (noteType == 8)
	        noteheadWidth=noteheadWidth*0.5f;
	    CCNode attention=this._drawAlteration(parent,x-noteWidth/2,y,noteheadWidth*0.45f);
	    if (attention!=null)
	        floatSprites.add(attention);
	    //the center of the notehead
	    float w=cover.getContentSize().width * cover.getScaleX();
	    float x0=cover.getPosition().x-w/2;
	    float offset=(1.0f*anchorPoint.x/noteHead.getContentSize().width)*w;
	    float xCenter=x0+offset;
	    if (rotation) {
	        float x1=cover.getPosition().x-w/2;
	        xCenter=x1-offset;
	    }
	    CCNode midLine=this._drawMidLine(parent, xCenter, y, noteheadWidth);
	    if (midLine!=null)
	        floatSprites.add(midLine);
	}
	/*
	 uses the note's raw name to find the pitch directory associate to it.
	 Since only sharp pitches are stored, method finds the enharmonic name
	 to flat notes using the circle of fifths dictionary
	 */
	public String getPitchDir() {
	    String folder=null;
	    if (staffType == kStaffTreble) {
	        folder="treble_pitches";
	    }
	    else if (staffType==kStaffBass){
	        folder="bass_pitches";
	    }
	    return String.format("piano_composition/%s/%d/%d.wav", folder, noteType, numId);
	}
	protected void _setAppearance(String imgFile_, boolean rotation_, CGPoint anchorPoint_) {
	    imgFile=imgFile_;
	    rotation=rotation_;
	    anchorPoint=anchorPoint_;
	}
}
