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
import name.w.yellowduck.LineSprite;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4F;

import android.view.MotionEvent;

public class CraneScene extends name.w.yellowduck.YDActLayerBase {
	private final int kTagArrowLeft           =10;
	private final int kTagArrowRight          =11;
	private final int kTagArrowUp             =12;
	private final int kTagArrowDown           =13;
	
	private final int kTagMovingItem          =100;

	private final int kGridCols           =6;
	private final int kGridRows           =5;
	
    private CGRect rcLeftCanvas, rcRightCanvas;
    
    private int targetItems[]=new int[kGridRows*kGridCols];
    private int movingItems[]=new int[kGridRows*kGridCols];
    private int movingItemIdx;
    
    private float yHookTop;
    private CCSprite spriteHook;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new CraneScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=6;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(), kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionOk);
	
	    rcLeftCanvas=CGRect.make(54.0f/1024*szWin.width, 158.0f/666*szWin.height, 400.0f/1024*szWin.width, 332.0f/666*szWin.height);
	    rcRightCanvas=CGRect.make(592.0f/1024*szWin.width, 275.0f/666*szWin.height, 400.0f/1024*szWin.width, 323.0f/666*szWin.height);
	    
	    yHookTop=530.f/666*szWin.height;
	    spriteHook=spriteFromExpansionFile("image/misc/layermaskwhite.png");
	    spriteHook.setScaleX(2.0f/spriteHook.getContentSize().width);
	    spriteHook.setScaleY(1);//to be adjusted
	    spriteHook.setColor(ccColor3B.ccBLACK);
	    super.addChild(spriteHook,1);
	
	    float arrowScale=preferredContentScale(true);
	    float arrowYPos=70.0f/666*szWin.height;
	    {
	        //left button
	    	String img="image/activities/puzzle/crane/arrow_left.png";
	    	String selected=buttonize(img);
	    	CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(selected);
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCSprite spriteSelected=CCSprite.sprite(texture);
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected,this,"arrowTouched");
	        menuitem.setScale(arrowScale);
	        menuitem.setTag(kTagArrowLeft);
	        CCMenu menu = CCMenu.menu(menuitem);
	        menu.setPosition(139.0f/1024*szWin.width, arrowYPos);
	        super.addChild(menu,2);
	    }
	    
	    {
	        //right button
	    	String img="image/activities/puzzle/crane/arrow_right.png";
	    	String selected=buttonize(img);
	    	CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(selected);
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCSprite spriteSelected=CCSprite.sprite(texture);
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected,this,"arrowTouched");
	        menuitem.setScale(arrowScale);
	        menuitem.setTag(kTagArrowRight);
	        CCMenu menu = CCMenu.menu(menuitem);
	        menu.setPosition(207.0f/1024*szWin.width, arrowYPos);
	        super.addChild(menu,2);
	    }
	    arrowYPos += preferredContentScale(true);
	    {
	        //up
	    	String img="image/activities/puzzle/crane/arrow_up.png";
	    	String selected=buttonize(img);
	    	CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(selected);
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCSprite spriteSelected=CCSprite.sprite(texture);
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected,this,"arrowTouched");
	        menuitem.setScale(arrowScale);
	        menuitem.setTag(kTagArrowUp);
	        CCMenu menu = CCMenu.menu(menuitem);
	        menu.setPosition(306.0f/1024*szWin.width, arrowYPos);
	        super.addChild(menu,2);
	    }
	    {
	        //down
	    	String img="image/activities/puzzle/crane/arrow_down.png";
	    	String selected=buttonize(img);
	    	CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(selected);
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCSprite spriteSelected=CCSprite.sprite(texture);
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected,this,"arrowTouched");
	        menuitem.setScale(arrowScale);
	        menuitem.setTag(kTagArrowDown);
	        CCMenu menu = CCMenu.menu(menuitem);
	        menu.setPosition(368.0f/1024*szWin.width, arrowYPos);
	        super.addChild(menu,2);
	    }
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
	    
	    spriteHook.stopAllActions();
	    
	    if (mLevel < 5) {
	        float gridCellWidth=rcLeftCanvas.size.width/kGridCols;
	        float gridCellHeight=rcLeftCanvas.size.height/kGridRows;
	        //display grid
	        for (int j=0;j<kGridRows-1;++j) {
	            float x0=rcLeftCanvas.origin.x;
	            float y0=rcLeftCanvas.origin.y + (j + 1) * gridCellHeight;
	            float x1=rcLeftCanvas.origin.x + rcLeftCanvas.size.width;
	            float y1=y0;
	            
	            LineSprite sprite=new LineSprite(CGPoint.ccp(x0,y0),CGPoint.ccp(x1, y1));
	            sprite.setClr(new ccColor4F(0, 0, 0, 1.0f));
	            sprite.setLineWidth(2);
	            super.addChild(sprite,1);
	            floatingSprites.add(sprite);
	        }
	        for (int i = 0; i < kGridCols-1; ++i) {
	            float x0=rcLeftCanvas.origin.x+(i+1)*gridCellWidth;
	            float y0=rcLeftCanvas.origin.y;
	            float x1=x0;
	            float y1=rcLeftCanvas.origin.y+rcLeftCanvas.size.height;

	            LineSprite sprite=new LineSprite(CGPoint.ccp(x0,y0),CGPoint.ccp(x1, y1));
	            sprite.setClr(new ccColor4F(0, 0, 0, 1.0f));
	            sprite.setLineWidth(2);
	            super.addChild(sprite,1);
	            floatingSprites.add(sprite);
	        }
	        //right part
	        gridCellWidth=rcRightCanvas.size.width/kGridCols;
	        gridCellHeight=rcRightCanvas.size.height/kGridRows;
	        //display grid
	        for (int j=0;j<kGridRows-1;++j) {
	            float x0=rcRightCanvas.origin.x;
	            float y0=rcRightCanvas.origin.y + (j + 1) * gridCellHeight;
	            float x1=rcRightCanvas.origin.x + rcRightCanvas.size.width;
	            float y1=y0;
	            
	            LineSprite sprite=new LineSprite(CGPoint.ccp(x0,y0),CGPoint.ccp(x1, y1));
	            sprite.setClr(new ccColor4F(0, 0, 0, 1.0f));
	            sprite.setLineWidth(2);
	            super.addChild(sprite,1);
	            floatingSprites.add(sprite);
	        }
	        for (int i = 0; i < kGridCols-1; ++i) {
	            float x0=rcRightCanvas.origin.x+(i+1)*gridCellWidth;
	            float y0=rcRightCanvas.origin.y;
	            float x1=x0;
	            float y1=rcRightCanvas.origin.y+rcRightCanvas.size.height;
	            
	            LineSprite sprite=new LineSprite(CGPoint.ccp(x0,y0),CGPoint.ccp(x1, y1));
	            sprite.setClr(new ccColor4F(0, 0, 0, 1.0f));
	            sprite.setLineWidth(2);
	            super.addChild(sprite,1);
	            floatingSprites.add(sprite);
	        }
	    }
	    String pictures[]={"image/activities/puzzle/crane/water_spot1.png",
	                            "image/activities/puzzle/crane/water_spot2.png",
	                            "image/activities/puzzle/crane/water_drop1.png",
	                            "image/activities/puzzle/crane/water_drop2.png",
	                            "image/activities/puzzle/crane/tux.png",
	                            "image/activities/puzzle/crane/triangle1.png",
	                            "image/activities/puzzle/crane/triangle2.png",
	                            "image/activities/puzzle/crane/rectangle1.png",
	                            "image/activities/puzzle/crane/rectangle2.png",
	                            "image/activities/puzzle/crane/square1.png",
	                            "image/activities/puzzle/crane/square2.png",
	                            "image/activities/puzzle/crane/bulb.png",
	                            "image/activities/puzzle/crane/letter-a.png",
	                            "image/activities/puzzle/crane/letter-b.png"};
	    int totalElements=mLevel*2+2;
	    int selection[]=new int[14]; //total 14 pictures
	    for (int i = 0;i < 14;++i)
	        selection[i]=i;
	    super.randomIt(selection,14);
	    
	    for (int i = 0; i < kGridRows*kGridCols; ++i) {
	        if (i < totalElements) {
	            targetItems[i]=selection[i];
	            movingItems[i]=selection[i];
	        }
	        else {
	            targetItems[i]=-1;
	            movingItems[i]=-1;
	        }
	    }
	    randomIt(targetItems,kGridRows*kGridCols);
	    randomIt(movingItems,kGridRows*kGridCols);
	    
	    float gridSize=rcRightCanvas.size.width/kGridRows;
	    for (int i = 0; i < kGridRows*kGridCols; ++i) {
	        if (targetItems[i] >= 0) {
	            CCSprite sprite=spriteFromExpansionFile(pictures[targetItems[i]]);
	            sprite.setPosition(seq2Position(i,rcRightCanvas));
	            sprite.setScale(gridSize/sprite.getContentSize().width*0.6f);
	            super.addChild(sprite,4);
	            floatingSprites.add(sprite);
	        }
	    }
	    movingItemIdx=-1;
	    for (int i = 0; i < kGridRows*kGridCols; ++i) {
	        if (movingItems[i] >= 0) {
	            CCSprite sprite=spriteFromExpansionFile(pictures[movingItems[i]]);
	            sprite.setPosition(seq2Position(i,rcLeftCanvas));
	            sprite.setScale(gridSize/sprite.getContentSize().width*0.6f);
	            sprite.setTag(kTagMovingItem+i);
	            super.addChild(sprite,4);
	            floatingSprites.add(sprite);
	        	
	            if (movingItemIdx < 0)
	                movingItemIdx=i;
	        }
	    }
	    this.moveHook2(movingItemIdx);
	    //place the crane on this item
	}
	private CGPoint seq2Position(int seq, CGRect rc) {
	    int row=seq / kGridCols;
	    int col=seq % kGridCols;
	    float gridCellWidth=rc.size.width/kGridCols;
	    float gridCellHeight=rc.size.height/kGridRows;
	    
	    return CGPoint.ccp(rc.origin.x+col*gridCellWidth+gridCellWidth/2, rc.origin.y+row*gridCellHeight+gridCellHeight/2);
	}
	private int position2seq(CGPoint pt, CGRect rc) {
	    float xOffset=(pt.x-rc.origin.x);
	    float yOffset=(pt.y-rc.origin.y);
	
	    float gridCellWidth=rc.size.width/kGridCols;
	    float gridCellHeight=rc.size.height/kGridRows;
	  
	    int x=(int)(xOffset/gridCellWidth);
	    int y=(int)(yOffset/gridCellHeight);
	    
	    return y*kGridCols+x;
	}
	
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
        if (rcLeftCanvas.contains(p1.x,  p1.y)) {
            int seq=position2seq(p1, rcLeftCanvas);
            if (movingItems[seq] >= 0) {
                movingItemIdx=seq;
                moveHook2(movingItemIdx);
            }
        }
	    return true;
	}
	
	private void moveHook2(int itemSeq) {
	    CGPoint pt1=seq2Position(itemSeq, rcLeftCanvas);
	    CGPoint pt2=CGPoint.ccp(pt1.x, yHookTop);
	    spriteHook.setScaleY((pt2.y-pt1.y)/spriteHook.getContentSize().height);
	    spriteHook.setPosition(pt1.x, (pt1.y+pt2.y)/2);
	}
	
	public void arrowTouched(Object _sender){
		CCNode sender=(CCNode)_sender;

	    int xMovement=0, yMovement=0;
	    if (sender.getTag()==kTagArrowDown) {
	        yMovement=-1;
	    }
	    else if (sender.getTag()==kTagArrowUp) {
	        yMovement=1;
	    }
	    else if (sender.getTag()==kTagArrowLeft) {
	        xMovement=-1;
	    }
	    else if (sender.getTag()==kTagArrowRight) {
	        xMovement=1;
	    }
	    int row=movingItemIdx/kGridCols;
	    int col=movingItemIdx%kGridCols;
	    row += yMovement;
	    col += xMovement;
	    if (col < 0)
	        col=0;
	    else if (col >= kGridCols) {
	        col=kGridCols-1;
	    }
	    if (row < 0)
	        row=0;
	    else if (row >= kGridRows) {
	        col=kGridRows-1;
	    }
	    int move2pos=row*kGridCols+col;
	    if (move2pos != movingItemIdx && movingItems[move2pos] < 0) {
	        int previousPos=movingItemIdx;
	        
	        int item=movingItems[movingItemIdx];
	        movingItems[movingItemIdx]=-1;
	        movingItemIdx=move2pos;
	        movingItems[movingItemIdx]=item;
	        
	        float movingSpeed=0.5f;//seconds
	        for (CCNode node : floatingSprites) {
	            if (node.getTag()==kTagMovingItem+previousPos) {
	                node.setTag(kTagMovingItem+move2pos);
	                CCMoveTo moveAction = CCMoveTo.action(movingSpeed, seq2Position(move2pos,  rcLeftCanvas));
	                node.runAction(moveAction);
	            }
	        }
	        //move the crane
	        CGPoint pt1=seq2Position(move2pos, rcLeftCanvas);
	        CGPoint pt2=CGPoint.ccp(pt1.x, yHookTop);
	        CCMoveTo moveAction = CCMoveTo.action(movingSpeed, CGPoint.ccp(pt1.x, (pt1.y+pt2.y)/2));
	        spriteHook.runAction(moveAction);
	        CCScaleTo scaleAction=CCScaleTo.action(movingSpeed, spriteHook.getScaleX(), (pt2.y-pt1.y)/spriteHook.getContentSize().height);
	        spriteHook.runAction(scaleAction);
	        
	        playSound("audio/sounds/drip.wav");
	    }
	    else {
	        playSound("audio/sounds/brick.wav");
	    }
	}
	public void ok(Object _sender) {
	    boolean same=true;
	    for (int i = 0; i < kGridRows*kGridCols; ++i) {
	        if (targetItems[i] != movingItems[i]) {
	            same=false;
	            break;
	        }
	    }
	    flashAnswerWithResult(same, same, null, null, 2);
	}
}
