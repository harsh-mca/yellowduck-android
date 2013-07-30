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
import name.w.yellowduck.YDLayerBase;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor3B;

import android.util.FloatMath;

public class Staff extends Object {
	public static final int NOTATION_WHITE      =0;
	public static final int NOTATION_SHARP      =1;
	public static final int NOTATION_FLAT       =2;

	public static final int kInitialNoteX       =30;
	public static final int kNoteSpaceX         =30;
	
	private String stffName;
	
	private java.util.ArrayList<KeyNotation> notations;
	private java.util.Hashtable<String, ccColor3B> colorSchema;
	private java.util.ArrayList<Note> notesDrawn;
    
    private int staffType;
    private float currentNoteXCoordinate;
    private float initialNoteX;
    private float noteSpacingX;
    private int  currentLineNum;
    
    private float zoom;

    protected CGRect rect;
    //#vertical distance between lines in staff
    protected int staffLineSpacing;
    //thickness of staff lines
    protected float staffLineThickness;
    //where the staff begins
    protected float yTopStaff, yBottomStaff;
    
    //# number of staves to draw (1,2, or 3)
    protected int numStaves;
    //vertical distance between musical staves
    protected int verticalDistanceBetweenStaves;
    
    protected YDLayerBase parent;
    protected java.util.ArrayList<CCNode> floatingSprites;
	
	
	public String getStffName() {
		return stffName;
	}

	public void setStffName(String stffName) {
		this.stffName = stffName;
	}
	
	public float yTopStaff() {
		return this.yTopStaff;
	}
	public float yBottomStaff() {
		return this.yBottomStaff;
	}

	public Staff (int type_, CGRect rc_, int staves_) {
		super();
        staffType=type_;
        rect=rc_;
        numStaves=staves_;
        //If you modify its value, please also modify Note.draw method
        staffLineSpacing=13;
        staffLineThickness = 2.0f;
        verticalDistanceBetweenStaves = 95;
        
        initialNoteX=rc_.origin.x + kInitialNoteX;
        currentNoteXCoordinate=initialNoteX; //starting X position of first note
        noteSpacingX = kNoteSpaceX;// #distance between each note when appended to staff
        currentLineNum=1;
        
        /*
         WHITE_KEY_NOTATION_US = {1:'C', 2:'D', 3:'E', 4:'F', 5:'G', 6:'A', 7:'B', 8:'C'}
         SHARP_NOTATION_US = {-1:'C#', -2:'D#', -3:'F#', -4:'G#', -5:'A#'}
         FLAT_NOTATION_US = {-1:'Db', -2:'Eb', -3:'Gb', -4:'Ab', -5:'Bb'}
         */
        YDConfiguration ydConfig=YDConfiguration.sharedConfiguration();
        notations=new java.util.ArrayList<KeyNotation>();
        notations.add(new KeyNotation(NOTATION_WHITE,1,ydConfig.getLocalizedString("note_white_1")));
        notations.add(new KeyNotation(NOTATION_WHITE,2,ydConfig.getLocalizedString("note_white_2")));
        notations.add(new KeyNotation(NOTATION_WHITE,3,ydConfig.getLocalizedString("note_white_3")));
        notations.add(new KeyNotation(NOTATION_WHITE,4,ydConfig.getLocalizedString("note_white_4")));
        notations.add(new KeyNotation(NOTATION_WHITE,5,ydConfig.getLocalizedString("note_white_5")));
        notations.add(new KeyNotation(NOTATION_WHITE,6,ydConfig.getLocalizedString("note_white_6")));
        notations.add(new KeyNotation(NOTATION_WHITE,7,ydConfig.getLocalizedString("note_white_7")));
        notations.add(new KeyNotation(NOTATION_WHITE,8,ydConfig.getLocalizedString("note_white_8")));
        
        notations.add(new KeyNotation(NOTATION_SHARP,-1,ydConfig.getLocalizedString("note_sharp_-1")));
        notations.add(new KeyNotation(NOTATION_SHARP,-2,ydConfig.getLocalizedString("note_sharp_-2")));
        notations.add(new KeyNotation(NOTATION_SHARP,-3,ydConfig.getLocalizedString("note_sharp_-3")));
        notations.add(new KeyNotation(NOTATION_SHARP,-4,ydConfig.getLocalizedString("note_sharp_-4")));
        notations.add(new KeyNotation(NOTATION_SHARP,-5,ydConfig.getLocalizedString("note_sharp_-5")));
        
        notations.add(new KeyNotation(NOTATION_FLAT,-1,ydConfig.getLocalizedString("note_flat_-1")));
        notations.add(new KeyNotation(NOTATION_FLAT,-2,ydConfig.getLocalizedString("note_flat_-2")));
        notations.add(new KeyNotation(NOTATION_FLAT,-3,ydConfig.getLocalizedString("note_flat_-3")));
        notations.add(new KeyNotation(NOTATION_FLAT,-4,ydConfig.getLocalizedString("note_flat_-4")));
        notations.add(new KeyNotation(NOTATION_FLAT,-5,ydConfig.getLocalizedString("note_flat_-5")));
        
        colorSchema=new java.util.Hashtable<String, ccColor3B>();
        colorSchema.put("1", new ccColor3B(0xff, 0,0));
        colorSchema.put("-1", new ccColor3B(0xff, 0x63, 0x47));
        colorSchema.put("2", new ccColor3B(0xff,0x7f,0x00));
        colorSchema.put("-2", new ccColor3B(0xff,0xd7,0x00));
        colorSchema.put("3", new ccColor3B(0xff,0xff,0x00));
        colorSchema.put("4", new ccColor3B(0x32,0xcd,0x32));
        colorSchema.put("-3", new ccColor3B(0x20,0xb2,0xaa));
        colorSchema.put("5", new ccColor3B(0x64,0x95,0xed));
        colorSchema.put("-4", new ccColor3B(0x8a,0x2b,0xe2));
        colorSchema.put("6", new ccColor3B(0xd0,0x20,0x90));
        colorSchema.put("-5", new ccColor3B(0xff,0x00,0xff));
        colorSchema.put("7", new ccColor3B(0xff,0x14,0x93));
        colorSchema.put("-6", new ccColor3B(0xff,0x63,0x47));
        colorSchema.put("8", new ccColor3B(0xff,0x00,0x00));
        colorSchema.put("9", new ccColor3B(0xff,0x7f,0x00));
        colorSchema.put("10", new ccColor3B(0xff,0xff,0x00));
        colorSchema.put("11", new ccColor3B(0x32,0xcd,0x32));
	}
	public void beginDraw(YDLayerBase _parent, java.util.ArrayList<CCNode>_floatingSprites,float zm) {
	    parent=_parent;
	    floatingSprites=_floatingSprites;
	    zoom=zm;
	
	    initialNoteX=rect.origin.x + kInitialNoteX * zoom;
	    currentNoteXCoordinate=initialNoteX;
	    
	    currentLineNum = 1;
	    
	    notesDrawn=new java.util.ArrayList<Note>();
	}
	public void endDraw() {
	    parent=null;
	    floatingSprites=null;
	    
	    for (Note note : notesDrawn) {
	        note.setUserObject(null);
	    }
	    notesDrawn=null;
	}

	public void drawStaff(boolean withEndBars) {    
	    yTopStaff=rect.origin.y - (rect.size.height - numStaves*verticalDistanceBetweenStaves*zoom)/2;
	    
	    float yTop=yTopStaff;
	    for (int i = 0; i < numStaves; ++i) {
	        this._drawLinesAtCenterX(rect.origin.x+rect.size.width/2,yTop,rect.size.width);
	        yTop -= verticalDistanceBetweenStaves*zoom;
	    }
	    yBottomStaff=yTop;
	    
	    //two lines at end of the last staff
	    if (withEndBars)
	        this._drawEndBars();
	    
	    this._drawClefs();
	}
	/*
	 int h = 65;
	 int w = kInitialNoteX;
	 float y = yTopStaff;
	 for (int i = 0; i < numStaves; ++i) {
	 CCSprite *sprite=[CCSprite spriteWithFile:@"Assets/image/activities/discovery/sound_group/piano_composition/trebleClef.png"];
	 sprite.scaleX=w/sprite.contentSize.width;
	 sprite.scaleY=h/sprite.contentSize.height;
	 sprite.position=CGPointMake(rect.origin.x+3 + w/2, y+2- h/2 );
	 [parent addChild:sprite z:zStaff];
	 [floatingSprites addObject:sprite];
	 
	 y -= verticalDistanceBetweenStaves;
	 }
	*/ 
	private void _drawClefs() {
	    int h = 40;
	    int w = kInitialNoteX;
	    int xShift=0, yShift=0;
	    String clef=null;
	    if (staffType == Note.kStaffTreble) {
	        h = 65;
	        xShift=3; yShift=2;
	        clef="image/activities/discovery/sound_group/piano_composition/trebleClef.png";
	    }
	    else if (staffType == Note.kStaffBass) {
	        h=40;
	        xShift=6; yShift=-1;
	        clef="image/activities/discovery/sound_group/piano_composition/bassClef.png";
	    }
	    w *= zoom;
	    h *= zoom;
	    xShift *= zoom;
	    yShift *= zoom;
	    float y = yTopStaff;
	    if (numStaves >= 1) {
	        CCSprite sprite=parent.spriteFromExpansionFile(clef);
	        sprite.setScaleX(w/sprite.getContentSize().width);
	        sprite.setScaleY(h/sprite.getContentSize().height);
	        sprite.setPosition(rect.origin.x+xShift + w/2, y+yShift- h/2 );
	        parent.addChild(sprite,Note.zStaff);
	        floatingSprites.add(sprite);
	    }
	}
	
	//draw the scale on the staff, always draw with color
	public java.util.ArrayList<Note> drawScale(String scaleName) {
		java.util.ArrayList<Note> notes=new java.util.ArrayList<Note>();
	    if (scaleName.equalsIgnoreCase("C Major")) {
	        int numIds[] = {1, 2, 3, 4, 5, 6, 7, 8};
	        // good luck with the rest of the scales...all them exceed the C octave,
	        // so you'll need to add in a feature to go beyond just 8 notes ;-)
	        for (int i = 0; i < numIds.length; ++i) {
	            Note note = new QuarterNote(numIds[i],staffType,true);
	            this.drawNote(note, true);
	            
	            notes.add(note);
	        }
	    }
	    return notes;
	}
	
	//needs to be override
	public int translatePosition(int numId) {
	    int translated=0;
	    if (staffType == Note.kStaffTreble) {
	        //    self.positionDict = {1:26, 2:22, 3:16, 4:9, 5:3, 6:-4, 7:-10, 8:-17, 9:-23, 10:-29,11:-35}
	        int dict[]={0, 26, 22, 16, 9, 3, -4, -10, -17, -23, -29, -35};
	        translated=dict[numId];
	    }
	    else if (staffType == Note.kStaffBass) {
	        //    self.positionDict = {1:-4, 2:-11, 3:-17, 4:-24, 5:-30, 6:-36, 7:-42, 8:-48, 9:-52, 10:-58, 11:-64}
	        int dict[]={0, -4, -11, -17, -24, -30, -36, -42, -48, -52, -58, -64};
	        translated=dict[numId];
	    }
	    return translated;
	}
	private void _drawLinesAtCenterX(float x, float y, float width){
	    for (int i = 0; i < 5; ++i) {
	        CCSprite one=parent.spriteFromExpansionFile("image/activities/discovery/sound_group/piano_composition/line.png");
	        one.setScaleX(width / one.getContentSize().width);
	        one.setScaleY(staffLineThickness/one.getContentSize().height);
	        one.setPosition(x, y-one.getContentSize().height*one.getScaleY()/2);
	        parent.addChild(one, Note.zStaff);
	        floatingSprites.add(one);
	        
	        y -= staffLineSpacing*zoom;
	    }
	}
	
	//draw the vertical end bars on each line, two for line 3
	private void _drawEndBars() {
	    float y = yTopStaff;
	    String strLine="image/activities/discovery/sound_group/piano_composition/line.png";
	    for (int num = 0; num < numStaves - 1; ++num) {
	        CCSprite one=parent.spriteFromExpansionFile(strLine);
	        one.setScaleX(staffLineSpacing*4*zoom / one.getContentSize().width);
	        one.setScaleY(3.0f/one.getContentSize().height);
	        one.setRotation(90);
	        one.setPosition(rect.origin.x+rect.size.width - 3.0f/2, y-staffLineSpacing*2*zoom);
	        parent.addChild(one, Note.zStaff);
	        floatingSprites.add(one);
	        
	        y-=verticalDistanceBetweenStaves*zoom;
	    }
	     //doublebar
	    
	    CCSprite left=parent.spriteFromExpansionFile(strLine);
	    left.setScaleX(staffLineSpacing*4*zoom / left.getContentSize().width);
	    left.setScaleY(3.0f/left.getContentSize().height);
	    left.setRotation(90);
	    left.setPosition(rect.origin.x+rect.size.width - 3.0f/2 - 7, y-staffLineSpacing*2*zoom);
	    parent.addChild(left, Note.zStaff);
	    floatingSprites.add(left);
	    
	     //final barline, dark
	    CCSprite thick=parent.spriteFromExpansionFile(strLine);
	    thick.setScaleX(staffLineSpacing*4*zoom / thick.getContentSize().width);
	    thick.setScaleY(4.0f/thick.getContentSize().height);
	    thick.setRotation(90);
	    thick.setPosition(rect.origin.x+rect.size.width - 4.0f/2, y-staffLineSpacing*2*zoom);
	    parent.addChild(thick, Note.zStaff);
	    floatingSprites.add(thick);
	}
	/*
	 determine the correct x & y coordinate for the next note, and writes
	 this note as an image to the staff. An alert is triggered if no more
	 room is left on the staff. Also color-codes the note if self.colorCodeNotes == True
	*/
	public CGPoint drawNote(Note note, boolean withColor) {
	    float x = this.getNoteXCoordinate(note);//returns next x coordinate for note,
	    if (x < 0)
	        return CGPoint.ccp(0, 0);
	    float y = this.getNoteYCoordinate(note);// #returns the y coordinate based on note name
	    //?? currentLineNum = [self getLineNum:y];/// #updates self.lineNum
	 
	    ccColor3B clr=withColor ? this.getColorFromID(note.getNumId()) : ccColor3B.ccBLACK;
	    note.draw(parent,x,y,clr,zoom,floatingSprites);
	    
	    if (notesDrawn.size() >= 1) {
	        Note pre=notesDrawn.get(notesDrawn.size()-1);
	        //#if previous note and current note are eighth notes, draw duple
	        if ((pre.getNoteType() == 8) && !pre.isTupleBound() && note.getNoteType() == 8)
	            this.drawTupleEighth(pre,note);
	    }
	    
	    notesDrawn.add(note);
	    return CGPoint.ccp(x, y);
	}
	private void drawTupleEighth(Note note1, Note note2) {
	    //# don't draw the duple if it's going to be crazy long (from one line to the next for example)
	    if (Math.abs(note1.getPosition().x - note2.getPosition().x) > 40*zoom)
	        return;
	     boolean note1Rotated=note1.getNumId() > 8 || note1.getNumId() < -6;
	     boolean note2Rotated=note2.getNumId() > 8 || note2.getNumId() < -6;
	     if ((!note1Rotated && !note2Rotated) || (note1Rotated && note2Rotated)) {
	         CCSprite firstFlag=(CCSprite)note1.getUserObject();
	         CCSprite secondFlag=(CCSprite)note2.getUserObject();

	         firstFlag.setVisible(false);
	         secondFlag.setVisible(false);
	         
	         float x1=note1.getPosition().x;
	         float x2=note2.getPosition().x;
	         
	         float y1, y2;
	         if (note1Rotated) {
	             y1=firstFlag.getPosition().y - firstFlag.getContentSize().height*firstFlag.getScaleY()/2;
	         }
	         else {
	             y1=firstFlag.getPosition().y + firstFlag.getContentSize().height*firstFlag.getScaleY()/2;
	         }
	         if (note2Rotated) {
	             y2=secondFlag.getPosition().y - secondFlag.getContentSize().height*secondFlag.getScaleY()/2;
	         }
	         else {
	             y2=secondFlag.getPosition().y + secondFlag.getContentSize().height*secondFlag.getScaleY()/2;
	         }
	         CCSprite beam=parent.spriteFromExpansionFile("image/activities/discovery/sound_group/piano_composition/beam.png");
	         beam.setPosition((x1+x2)/2, (y1+y2)/2);
	         beam.setScaleY(2.0f/beam.getContentSize().height);
	         float xDist = (x2 - x1);
	         float yDist = (y2 - y1);
	         beam.setScaleX((FloatMath.sqrt((xDist * xDist) + (yDist * yDist)))/beam.getContentSize().width);
	         float angle = 180.0f*((float)Math.atan2(y1 - y2, x1 - x2))/3.14f;
	         angle = -1*(angle);
	         beam.setRotation(angle);
	         beam.setTag(Note.kTagNote);
	         parent.addChild(beam,Note.zNote);
	         floatingSprites.add(beam);
	
	         note1.setTupleBound(true);
	         note2.setTupleBound(true);
	     }
	     
	    /*
	     //rotate if > 8 or < -6
	    float x1, y1, x2, y2;
	    if ((note1.numId > 8 && note2.numId > 8) || (note1.numId <= 8 && note2.numId <= 8) || (note1.numId > 8 && note2.numId < -5)) {
	         if (note2.numId <= 8 && note2.numId >= -5) {
	             x2 = note2.position.x + 7;
	             y2 = note2.position.y - 34;
	         }
	         if (note1.numId <= 8) {
	             x1 = note1.position.x + 7;
	             y1 = note1.position.y - 34;
	         }
	         if (note2.numId > 8 || note2.numId < -5) {
	             x2 = note2.position.x - 7;
	             y2 = note2.position.y + 34;
	         }
	        if (note1.numId > 8) {
	            x1 = note1.position.x - 7;
	            y1 = note1.position.y + 34;
	        }
	    }
	    else {
	        x2 = note2.position.x + 7;
	        y2 = note2.position.y - 34;
	        x1 = note1.position.x + 7;
	        y1 = note1.position.y - 34;
	        if (note2.numId > 8 || note2.numId < -5) {
	             //goocanvas.Item.rotate(note2.noteHead, 180, note2.x, note2.y)
	        }
	        if (note1.numId > 8) {
	             //goocanvas.Item.rotate(note1.noteHead, 180, note1.x, note1.y)
	        }
	    }
	
	     note1.tupleBar = goocanvas.polyline_new_line(note1.rootitem,
	                                                  x1, y1, x2, y2,
	                                                  stroke_color_rgba=0x121212D0, line_width=4)
	
	     note1.tupleBound = YES;
	     note2.tupleBound = YES;
	     */
	}
	//given the Ycoordinate, returns the correct lineNum (1,2,etc.)
	//-(int) ____getLineNum: (float)Ycoordinate {
	  //  return (int)(((Ycoordinate - yTopStaff + 20) / verticalDistanceBetweenStaves)) + 1;
	//}
	/*
	 determines the x coordinate of the next note to be written to the
	 staff, with consideration for the maximum staff line length.
	 Increments self.currentLineNumand sets self.currentNoteXCoordinate
	*/
	private float getNoteXCoordinate(Note note) {
	    currentNoteXCoordinate += noteSpacingX*zoom;
	
	    float endX=rect.origin.x+rect.size.width;
	    if ((currentNoteXCoordinate >= endX - 15) || (note.getNoteType() == 8 && currentNoteXCoordinate >= endX -25)) {
	        if (currentLineNum >= 3)
	            return -1; //no more space
	        else {
	            if (note.getNumId() < 0)
	                currentNoteXCoordinate = initialNoteX * zoom + 20;
	            else
	                currentNoteXCoordinate = initialNoteX * zoom + 20;
	        }
	        ++currentLineNum;
	    }
	    return currentNoteXCoordinate;
	}
	/*
	 return a note's vertical coordinate based on the note's name. This is
	 unique to each type of clef (different for bass and treble)
	*/ 
	private float getNoteYCoordinate(Note note) {
	    float yoffset = (currentLineNum - 1) * verticalDistanceBetweenStaves * zoom;
	    
	    int numId=0;
	    if (note.getNumId() < 0 && note.isSharpNotation()) {
	        switch (note.getNumId()) {
	            case -1:
	                numId=1;
	                break;
	            case -2:
	                numId=2;
	                break;
	            case -3:
	                numId=4;
	                break;
	            case -4:
	                numId=5;
	                break;
	            case -5:
	                numId=6;
	                break;
	            case -6:
	                numId=7;
	                break;
	        }
	        //numID = {-1:1, -2:2, -3:4, -4:5, -5:6, -6:8}[note.numID]
	    }
	    else if (note.getNumId() < 0) {
	        switch (note.getNumId()) {
	            case -1:
	                numId=2;
	                break;
	            case -2:
	                numId=3;
	                break;
	            case -3:
	                numId=4;
	                break;
	            case -4:
	                numId=6;
	                break;
	            case -5:
	                numId=7;
	                break;
	            case -6:
	                numId=9;
	                break;
	        }
	//        numID = {-1:2, -2:3, -3:5, -4:6, -5:7, -6:9}[note.numID]
	    }
	    else {
	        numId = note.getNumId();
	    }
	    return yTopStaff -(this.translatePosition(numId) + yoffset + 36+1) * zoom;
	}
	/*
	 get the name of the key that corresponds to the numID given
	 optionally set sharpNotation = True for sharp notation, or
	 sharpNotation = False for flat notation
	 >>> getKeyNameFromID(1)
	 C
	 >>> getKeyNameFromID(-3, sharpNotation=True)
	 F#
	 >>> getKeyNameFromID(-5, sharpNotation=False)
	 Bb
	 '''
	 */
	public String getKeyNameFromID(int numID, boolean sharpNotation) {
	    int sys=NOTATION_WHITE;
	    if (numID > 0) {
	        sys=NOTATION_WHITE;
	    }
	    else if (sharpNotation)
	        sys=NOTATION_SHARP;
	    else {
	        sys=NOTATION_FLAT;
	    }
	    
	    for (KeyNotation note : notations) {
	        if (note.getSys() == sys && note.getIdentifier() == numID) {
	            return note.getKeyName();
	        }
	    }
	    return null;
	}
	
	public ccColor3B getColorFromID(int numId) {
		return this.colorSchema.get(""+numId);
	}
	
	
	/*
	 returns the numID of the note that corresponds to the keyName
	 
	 >>> getIDFromKeyName('C')
	 1
	 >>> getIDFromKeyName('D#')
	 -2
	 >>> getIDFromKeyName('Eb')
	 -2
	 */
	/*
	private int _getIDFromKeyName(String keyName) {
	    for (KeyNotation note : notations) {
	        if (note.getSys() == NOTATION_WHITE) {
	            if (keyName.equalsIgnoreCase(note.getKeyName()))
	                return note.getIdentifier();
	        }
	    }
	    for (KeyNotation note : notations) {
	        if (note.getSys() == NOTATION_SHARP) {
	            if (keyName.equalsIgnoreCase(note.getKeyName()))
	                return note.getIdentifier();
	        }
	    }
	    for (KeyNotation note : notations) {
	        if (note.getSys() == NOTATION_FLAT) {
	            if (keyName.equalsIgnoreCase(note.getKeyName()))
	                return note.getIdentifier();
	        }
	    }
	    return 0;
	}
	*/
}
