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


package name.w.yellowduck.activities.puzzle;

import name.w.yellowduck.Category;
import name.w.yellowduck.PolygonSprite;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor4F;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.view.MotionEvent;

public class SudokuScene extends name.w.yellowduck.YDActLayerBase {
	private final String kObjectFilled            ="filled";
	private final String kObjectFixed             ="fixed";
	private final String kObjectStock             ="stock";
	
	private final int kMaxSudokuSize      =9;
	
    private CCLabel sublevelLabel;
    private CCSprite picked;
    
    private float separator;
    private CGRect rcGrid;
    private float gridCellSize;
    private float itemScale2;
    private boolean usePictures;
    private int sudokuSize, subSudokuSize;
    private CCSprite sudoku[][]=new CCSprite[kMaxSudokuSize][kMaxSudokuSize];
    private int selections[]=new int[10];
		
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new SudokuScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=9;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(), kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionRepeatButton);
	
	    //sublevel label
	    sublevelLabel = CCLabel.makeLabel("65/65", sysFontName(),super.mediumFontSize());
	    sublevelLabel.setPosition(szWin.width-sublevelLabel.getContentSize().width/2-4, bottomOverhead()+sublevelLabel.getContentSize().height/2);
	    super.addChild(sublevelLabel,1);
	    
	    separator=szWin.width/8;
	    float w=szWin.width-separator;
	    float h=szWin.height-topOverhead()-bottomOverhead();
	    float sz=w;
	    if (h < sz)
	        sz=h;
	    float xMargin=(w-sz)/2;
	    float yMargin=(h-sz)/2;
	    rcGrid=CGRect.make(separator+xMargin, bottomOverhead()+yMargin, sz, sz);
	    //the grid
	    PolygonSprite poly=new PolygonSprite(4);
	    poly.setClr(new ccColor4F(0, 0, 0, 1.0f));
	    poly.setSolid(false);
	    poly.setLineWidth(4);
	    poly.setVertix(0, rcGrid.origin);
	    poly.setVertix(1, CGPoint.ccp(rcGrid.origin.x+rcGrid.size.width, rcGrid.origin.y));
	    poly.setVertix(2, CGPoint.ccp(rcGrid.origin.x+rcGrid.size.width, rcGrid.origin.y+rcGrid.size.height));
	    poly.setVertix(3,  CGPoint.ccp(rcGrid.origin.x, rcGrid.origin.y+rcGrid.size.height));
	    super.addChild(poly, 1);
	    
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();

		java.util.ArrayList<String> lines=this.loadSudokuData4CurLevel();
	    gridCellSize=rcGrid.size.width/sudokuSize;
	    itemScale2=gridCellSize*0.68f;
	    
	    for (int i = 0; i < 10; ++i)
	        selections[i]=i;
	    randomIt(selections, 10);
	    for (int i = 0; i < sudokuSize; ++i)
	        for (int j = 0; j < sudokuSize; ++j) {
	            sudoku[i][j]=null;
	        }
	    
	    usePictures=false;
	    for (int x = 0; x < lines.size(); ++x) {
	        String lineStr=lines.get(x);
	        //       ['.','C','B'], ==>'.','C','B'
	        int find1=lineStr.indexOf("[");
	        int find2=lineStr.indexOf("]");
	        lineStr=lineStr.substring(find1+1, find2);
	        
	        String cells[]=lineStr.split(",");
	        for (int y = 0; y < cells.length; ++y) {
	            float xOffset=x * gridCellSize + gridCellSize/2;
	            float yOffset=y * gridCellSize + gridCellSize/2;
	            String cell=cells[y]; //'A'
	            int item=cell.charAt(1); //get the A
	            int itemId=-1;
	            
	            ccColor4F clr=new ccColor4F(0,0,0,0);
	            if (item == '.') {
	                //blank
	                clr=new ccColor4F(1.0f*0x33/0xff, 1.0f*0xcc/0xff, 1.0f, 0.6f);
	            }
	            else if (item >= 'A' && item <= 'Z') {
	                usePictures=true;//use picture
	                clr=new ccColor4F(0, 0, 1.0f, 0.6f);
	                
	                itemId=item-'A';
	            }
	            else  {
	                clr=new ccColor4F(0, 0, 1.0f, 0.6f);
	                //user number;
	                itemId=item-'1';
	            }
	            if (itemId >= 0) {
	                CCSprite sprite=getSprite4Item(itemId);
	                sprite.setPosition(rcGrid.origin.x+xOffset, rcGrid.origin.y+yOffset);
	                sprite.setScale(itemScale2/sprite.getContentSize().width);
	                sprite.setTag(itemId);
	                sprite.setUserData(kObjectFixed);
	                super.addChild(sprite, 2);
	                floatingSprites.add(sprite);
	
	                sudoku[y][x]=sprite;
	            }
	            //cell background
	            int extra=1;
	            float x0=rcGrid.origin.x+x*gridCellSize;
	            float y0= rcGrid.origin.y+y*gridCellSize;
	            PolygonSprite poly=new PolygonSprite(4);
	            poly.setClr(clr);
	            poly.setVertix(0, CGPoint.ccp(x0+extra, y0+extra));
	            poly.setVertix(1, CGPoint.ccp(x0+gridCellSize-2*extra, y0+extra));
	            poly.setVertix(2, CGPoint.ccp(x0+gridCellSize-2*extra, y0+gridCellSize-2*extra));
	            poly.setVertix(3, CGPoint.ccp(x0+extra, y0+gridCellSize-2*extra));
	            super.addChild(poly,1);
	            floatingSprites.add(poly);
	        }
	    }
	    //items that user can pick to fill the sudoku
	    float thumbnailSize=separator;
	    float yAvailable=szWin.height-topOverhead()-bottomOverhead();
	    if (thumbnailSize > yAvailable/sudokuSize)
	        thumbnailSize=yAvailable/sudokuSize;
	    float yMargin=(yAvailable - sudokuSize * thumbnailSize)/2;
	    for (int i = 0; i < sudokuSize; ++i) {
	        CCSprite sprite=getSprite4Item(i);
	        sprite.setPosition(thumbnailSize/2, bottomOverhead()+yMargin+i*thumbnailSize+thumbnailSize/2);
	        sprite.setScale(thumbnailSize*0.68f/sprite.getContentSize().width);
	        sprite.setTag(i);
	        sprite.setUserData(kObjectStock);
	        super.addChild(sprite, 2);
	        floatingSprites.add(sprite);
	    }
	    sublevelLabel.setString(String.format("%d/%d", mSublevel+1, mMaxSublevel));
	    
	    if (subSudokuSize < sudokuSize) {
	        int groups=sudokuSize/subSudokuSize;
	        for (int yg=0; yg < groups; ++yg) {
	            for (int xg=0; xg < groups; ++xg) {
	                //the lower left coordination of this group
	                float xGrid=rcGrid.origin.x+xg * subSudokuSize * gridCellSize;
	                float yGrid=rcGrid.origin.y+yg * subSudokuSize * gridCellSize;
	                
	                PolygonSprite poly=new PolygonSprite(4);
	                poly.setClr(new ccColor4F(1.0f, 0, 0, 1.0f));
	                poly.setSolid(false);
	                poly.setLineWidth(2);
	                poly.setVertix(0, CGPoint.ccp(xGrid, yGrid));
	                poly.setVertix(1, CGPoint.ccp(xGrid+subSudokuSize*gridCellSize, yGrid));
	                poly.setVertix(2, CGPoint.ccp(xGrid+subSudokuSize*gridCellSize, yGrid+subSudokuSize*gridCellSize));
	                poly.setVertix(3, CGPoint.ccp(xGrid, yGrid+subSudokuSize*gridCellSize));
	                super.addChild(poly, 1);
	                floatingSprites.add(poly);
	            }
	        }
	    }
	}
	
	private CCSprite getSprite4Item(int ident){
	    CCSprite sprite=null;
	    if (usePictures)
	        sprite=spriteFromExpansionFile(getPicPath(selections[ident]));
	    else {
	        int clr=0;
	        switch (ident) {
	            case 1:clr=Color.rgb(0x80, 0x80, 00);break;
	            case 2:clr=Color.rgb(0x80, 0x00, 0x80);break;
	            case 3:clr=Color.rgb(0xff, 0xa5, 00);break;
	            case 4:clr=Color.rgb(0xff, 0x00, 0xff);break;
	            case 5:clr=Color.rgb(0xc0, 0xc0, 0xc0);break;
	            case 6:clr=Color.rgb(0x00, 0xff, 0xff);break;
	            case 7:clr=Color.rgb(0x0, 0x0, 0xff);break;
	            case 8:clr=Color.rgb(0x0, 0xff, 0x00);break;
	            case 0:
	            default:
	                clr=Color.rgb(0xff, 0x0, 0);break;
	        }
	        int sz=(int)gridCellSize;
		    android.graphics.Bitmap img=android.graphics.Bitmap.createBitmap(sz, sz, android.graphics.Bitmap.Config.ARGB_8888);
		    android.graphics.Canvas canvas=new android.graphics.Canvas(img);
		    canvas.drawARGB(0, 0, 0, 0);
		    Paint paint=new Paint();
		    paint.setColor(clr);
		    canvas.drawCircle(sz/2, sz/2, sz/2, paint);
		    
		    paint.setColor(Color.WHITE);
	        //test the font size, try to fit the grid
	        String label=""+ (ident+1);
	        int fontSize=super.mediumFontSize();
	        paint.setTextSize(fontSize);
	        float sz2=paint.measureText(label);
	        fontSize=(int)(fontSize * gridCellSize/sz2 * 0.5f);
	        //android.util.Log.e("SK", ""+fontSize);
	        paint.setTextSize(fontSize);
	        
	        FontMetrics fm = new FontMetrics();
	        paint.setTextAlign(Paint.Align.CENTER);
	        paint.getFontMetrics(fm);
	        canvas.drawText(label, gridCellSize/2, gridCellSize/2 + -(fm.ascent + fm.descent) / 2, paint);	        

	        //done
	        sprite=CCSprite.sprite(img, ""+ident);
	    }
	    return sprite;
	}
	
	private java.util.ArrayList<String> loadSudokuData4CurLevel() {
	    //loading configuration
	    String dataFilePath = "image/activities/puzzle/sudoku/data.txt";
	    //split into lines
	    java.util.ArrayList<String> lines=loadExpansionAssetFile(dataFilePath);

	    String signature="Level " + mLevel;
	    int startLine=0; String lineStr=null;
	    for (startLine=0;;++startLine) {
	        lineStr=lines.get(startLine);
	        if (lineStr.indexOf(signature)>=0)
	            break;
	    }
	    int endLine=startLine;
	    signature="Level " + (mLevel+1);
	    for (endLine=startLine;;++endLine) {
	        if (endLine >= lines.size())
	            break;
	        lineStr=lines.get(endLine);
	        if (lineStr.indexOf(signature)>=0)
	            break;
	    }
	    //remove settings for other levels;
	    for (int i = lines.size() - 1; i >= endLine; --i) {
	        lines.remove(i);
	    }
	    for (int i = startLine-1; i >= 0; --i) {
	        lines.remove(i);
	    }
	    //test max sublevels
	    mMaxSublevel=1;
	    for (int i = 0; i < lines.size(); ++i) {
	        lineStr=lines.get(i);
	        if (lineStr.indexOf("#NEXT") >= 0)
	            ++mMaxSublevel;
	    }
	    //find sub level data
	    int level=0;
	    startLine=0;
	    while (level < mSublevel) {
	        lineStr=lines.get(startLine++);
	        if (lineStr.indexOf("#NEXT") >= 0)
	            ++level;
	    }
	    for (endLine=startLine;endLine < lines.size(); ++endLine) {
	        lineStr=lines.get(endLine);
	        if (lineStr.indexOf("#NEXT") >= 0)
	            break;
	    }
	    //remove data for other sublevel
	    for (int i = lines.size() - 1; i >= endLine; --i) {
	        lines.remove(i);
	    }
	    for (int i = startLine-1; i >= 0; --i) {
	        lines.remove(i);
	    }
	    if (mSublevel <= 0) {
	        String strLevelSetting=lines.get(0);
	        lines.remove(0);
	        sudokuSize=subSudokuSize=lines.size();
	        //test sub sudoku settings
	        int find1=strLevelSetting.indexOf("[");
	        int find2=strLevelSetting.indexOf("]");
	        if (find1 >= 0 && find2 > find1) {
	            String str=strLevelSetting.substring(find1+1,  find2);
	            String ary[]=str.split("x");
	            subSudokuSize=Integer.parseInt(ary[0]);
	        }
	    }
	    return lines;
	}
	private String getPicPath(int idx) {
	    String pictures[]={"sudoku/rectangle.png",
	        "sudoku/rectangle_grey.png",
	        "sudoku/circle.png",
	        "sudoku/circle_grey.png",
	        "sudoku/rhombus.png",
	        "sudoku/rhombus_grey.png",
	        "sudoku/triangle.png",
	        "sudoku/triangle_grey.png",
	        "sudoku/star.png",
	        "sudoku/star_grey.png"};
	
	    return "image/activities/puzzle/" + pictures[idx];
	}
	public boolean ccTouchesBegan(MotionEvent event) {
	    picked=null;
	
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
        if (p1.x < separator) {
            int total=floatingSprites.size();
            for (int i =0; i < total; ++i) {
                CCNode sprite=floatingSprites.get(i);
                if (kObjectStock.equals(sprite.getUserData())) {
                	if (super.isNodeHit(sprite, p1)) {
                        //duplicate this item
                        picked=this.getSprite4Item(sprite.getTag());
                        picked.setScale(itemScale2/picked.getContentSize().width);
                        picked.setPosition(p1);
                        picked.setTag(sprite.getTag());
                        
                        super.addChild(picked,2);
                        floatingSprites.add(picked);
                    }
                }
            }
        }
        else if (rcGrid.contains(p1.x, p1.y)) {
            int xPos=(int)((p1.x-rcGrid.origin.x)/gridCellSize);
            int yPos=(int)((p1.y-rcGrid.origin.y)/gridCellSize);
            CCSprite existing=sudoku[yPos][xPos];
            if (existing != null && kObjectFilled.equals(existing.getUserData())) {
                sudoku[yPos][xPos]=null;
                picked=existing;
                picked.setPosition(p1);
                picked.setUserData(null);
            }
        }
        return true;
	}
	
	public boolean ccTouchesMoved(MotionEvent event) {
		if (picked != null) {
			CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
			picked.setPosition(p1);
		}
		return true;
	}

	public boolean ccTouchesEnded(MotionEvent event) {
		if (picked==null)
			return true;
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
	        
        boolean aborted=true, finished[]=new boolean[1];
        finished[0]=false;
        if (rcGrid.contains(p1.x, p1.y)) {
            int xPos=(int)((p1.x-rcGrid.origin.x)/gridCellSize);
            int yPos=(int)((p1.y-rcGrid.origin.y)/gridCellSize);
            CCSprite existing=sudoku[yPos][xPos];
            if (existing != null && kObjectFilled.equals(existing.getUserData())) {
                existing.removeFromParentAndCleanup(true);
                floatingSprites.remove(existing);
                sudoku[yPos][xPos]=null;
            }
            if (sudoku[yPos][xPos] == null) {
                //filled
                sudoku[yPos][xPos]=picked;
                if (this.isSudokuValid(finished)) {
                    picked.setUserData(kObjectFilled);
                    picked.setPosition(rcGrid.origin.x + xPos * gridCellSize + gridCellSize/2, rcGrid.origin.y + yPos * gridCellSize + gridCellSize/2);
                    aborted=false;
                }
                else {
                    //reset to empty
                    sudoku[yPos][xPos]=null;
                }
            }
        }
        if (aborted) {
            playSound("audio/sounds/brick.wav");
            //discard it
            CGPoint pt=CGPoint.ccp(0-separator, 0);
            int total=floatingSprites.size();
            for (int i =0; i < total; ++i) {
                CCNode sprite=floatingSprites.get(i);
                if (kObjectStock.equals(sprite.getUserData())) {
                    if (sprite.getTag()==picked.getTag()) {
                        pt=sprite.getPosition();
                        break;
                    }
                }
            }
            CCMoveTo moveAction=CCMoveTo.action(0.5f, pt);
            CCCallFuncN doneAction = CCCallFuncN.action(this, "abort");
            picked.runAction(CCSequence.actions(moveAction, doneAction));
        }
        else {
            playSound("audio/sounds/drip.wav");
            if (finished[0])
                super.flashAnswerWithResult(true, true, null, null, 2);
        }
	
	    picked=null;
	    
	    return true;
	}

	public void abort(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    sender.removeFromParentAndCleanup(true);
	    floatingSprites.remove(sender);
	}
	public void repeat(Object _sender){
	    this.initGame(false, _sender);
	}
	private boolean isSudokuValid(boolean status[]) {
	    boolean valid=true, finished=true;
	    CCNode conflictWith=null;
	    for (int y=0; y < sudokuSize && valid; ++y) {
	        for (int x=0; x < sudokuSize && valid; ++x) {
	            CCNode thisCell=sudoku[y][x];
	            if (thisCell!=null) {
	                for (int jump=x+1; jump < sudokuSize; ++jump) {
	                    CCNode anotherCell=sudoku[y][jump];
	                    if (anotherCell!=null && (anotherCell.getTag()==thisCell.getTag())) {
	                        valid=false;
	                        if (anotherCell.getUserData() != null)
	                            conflictWith=anotherCell;
	                        else if (thisCell.getUserData()!=null)
	                            conflictWith=thisCell;
	                        break;
	                    }
	                }
	            }
	            else {
	                finished=false;
	            }
	        }
	    }
	    //check vertical items
	    for (int x=0; x < sudokuSize && valid; ++x) {
	        for (int y=0; y < sudokuSize && valid; ++y) {
	            CCNode thisCell=sudoku[y][x];
	            if (thisCell!=null) {
	                for (int jump=y+1; jump < sudokuSize; ++jump) {
	                    CCNode anotherCell=sudoku[jump][x];
	                    if (anotherCell!=null && (anotherCell.getTag()==thisCell.getTag())) {
	                        valid=false;
	                        if (anotherCell.getUserData()!=null)
	                            conflictWith=anotherCell;
	                        else if (thisCell.getUserData()!=null)
	                            conflictWith=thisCell;
	                        break;
	                    }
	                }
	            }
	            else {
	                finished=false;
	            }
	        }
	    }
	    int groups=sudokuSize/subSudokuSize;
	    if (groups > 1 && valid) {
	        //check region
	        for (int yg=0; yg < groups && valid; ++yg) {
	            for (int xg=0; xg < groups && valid; ++xg) {
	                //the lower left coordination of this groupo
	                int xGrid=xg * subSudokuSize;
	                int yGrid=yg * subSudokuSize;
	                //check horizontal items
	                CCNode items[]=new CCNode[kMaxSudokuSize*kMaxSudokuSize];
	                int filling=0;
	                //serialize all items in this region
	                for (int y=0; y < subSudokuSize; ++y) {
	                    for (int x=0; x < subSudokuSize; ++x) {
	                        CCNode thisCell=sudoku[yGrid+y][xGrid+x];
	                        items[filling++]=thisCell;
	                    }
	                }
	                for (int i = 0; i < subSudokuSize*subSudokuSize && valid; ++i) {
	                    CCNode thisCell=items[i];
	                    if (thisCell!=null) {
	                        for (int j = i+1; j < subSudokuSize*subSudokuSize; ++j) {
	                            CCNode anotherCell=items[j];
	                            if (anotherCell!=null && anotherCell.getTag()==thisCell.getTag()) {
	                                valid=false;
	                                if (anotherCell.getUserData()!=null)
	                                    conflictWith=anotherCell;
	                                else if (thisCell.getUserData()!=null)
	                                    conflictWith=thisCell;
	                                break;
	                            }
	                        }
	                    }
	                    else {
	                        finished=false;
	                    }
	                }
	            }
	        }
	    }
	    if (!valid)
	        finished=false;
	    
	    if (status!=null)
	        status[0]=finished;
	    
	    if (conflictWith!=null) {
	        float scale=conflictWith.getScale();
	        conflictWith.runAction(CCSequence.actions(CCScaleTo.action(0.2f, scale*1.6f), CCScaleTo.action(0.2f, scale)));
	    }
	    
	    return valid;
	}
}
