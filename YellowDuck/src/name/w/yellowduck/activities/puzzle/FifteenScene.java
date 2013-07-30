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
import name.w.yellowduck.PolygonSprite;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor4F;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.view.MotionEvent;

public class FifteenScene extends name.w.yellowduck.YDActLayerBase {
    private CGRect rcGrid;
    int gridCellSize;
    private CCSprite board[]=new CCSprite[16]; //4x4

	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new FifteenScene();
	 
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
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
	
	
	    float gridSize=(szWin.height-topOverhead()-bottomOverhead()) * 0.8f;
	    float xMargin=(szWin.width - gridSize)/2;
	    float yMargin=(szWin.height-topOverhead()-bottomOverhead()-gridSize)/2;
	    rcGrid=CGRect.make(xMargin, bottomOverhead()+yMargin, gridSize, gridSize);
	    gridCellSize=(int)gridSize/4;
	    //the grid
	    PolygonSprite poly=new PolygonSprite(4);
	    poly.setClr(new ccColor4F(0, 0, 0, 1.0f));
	    poly.setSolid(false);
	    poly.setLineWidth(2);
	    poly.setVertix(0, rcGrid.origin);
	    poly.setVertix(1, CGPoint.ccp(rcGrid.origin.x+rcGrid.size.width, rcGrid.origin.y));
	    poly.setVertix(2, CGPoint.ccp(rcGrid.origin.x+rcGrid.size.width, rcGrid.origin.y+rcGrid.size.height));
	    poly.setVertix(3,  CGPoint.ccp(rcGrid.origin.x, rcGrid.origin.y+rcGrid.size.height));
	    super.addChild(poly, 1);
	    //H grid line
	    for (int y = 1; y <  4; ++y) {
	        CGPoint p1=CGPoint.ccp(rcGrid.origin.x, rcGrid.origin.y+y*gridCellSize);
	        CGPoint p2=CGPoint.ccp(rcGrid.origin.x+gridCellSize*4, rcGrid.origin.y+y*gridCellSize);
	
	        LineSprite line=new LineSprite(p1, p2);
	        super.addChild(line, 1);
	    }
	    //V grid lines
	    for (int x = 1; x <  4; ++x) {
	        CGPoint p1=CGPoint.ccp(rcGrid.origin.x+gridCellSize*x, rcGrid.origin.y);
	        CGPoint p2=CGPoint.ccp(rcGrid.origin.x+gridCellSize*x, rcGrid.origin.y+gridCellSize*4);
	        
	        LineSprite line=new LineSprite(p1,p2);
	        super.addChild(line, 1);
	    }

	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
	    
	    int target[]={13,14,15,0,9,10,11,12,5,6,7,8,1,2,3,4};
	    for (int i= 0; i < 16; ++i) {
	        if (target[i] == 0)
	            board[i]=null;
	        else {
	            board[i]=this.getSprite4Item(target[i]);
	            board[i].setTag(target[i]);
	        }
	    }
	    /* Select level difficulty */
	    switch(mLevel) {
	        case 1:
	            this.scramble(10);
	            break;
	        case 2:
	            this.scramble(50);
	            break;
	        case 3:
	        case 4:
	            this.scramble(100);
	            break;
	        case 5:
	            this.scramble(150);
	            break;
	        default:
	            this.scramble(256);
	            break;
	    }
	    for (int i = 0; i < 16; ++i) {
	        if (board[i] == null)
	            continue;
	        int x=i%4;
	        int y=i/4;
	        board[i].setPosition(rcGrid.origin.x + x * gridCellSize + gridCellSize/2, rcGrid.origin.y + y * gridCellSize + gridCellSize/2);
	        super.addChild(board[i], 1);
	        floatingSprites.add(board[i]);
	    }
	}
	
	private void scramble(int number_of_scrambles) {
	    int pos=0;//the empty slot
	    while (board[pos] != null)
	        ++pos;
	    for (int i = 0; i < number_of_scrambles; ++i) {
	        int xMovement=0, yMovement=0;
	        int dir=super.nextInt(4);
	        switch (dir) {
	            case 0:
	                xMovement=1;
	                break;
	            case 1:
	                xMovement=-1;
	                break;
	            case 2:
	                yMovement=1;
	                break;
	            case 3:
	                yMovement=-1;
	                break;
	        }
	        int x=pos%4;
	        int y=pos/4;
	        if (xMovement < 0) {
	            if (x <= 0)
	                xMovement=0;
	        }
	        else if (xMovement > 0) {
	            if (x >= 3)
	                xMovement=0;
	        }
	        if (yMovement < 0) {
	            if (y <= 0)
	                yMovement=0;
	        }
	        else if (yMovement > 0) {
	            if (y >= 3)
	                yMovement=0;
	        }
	        if (xMovement != 0 || yMovement != 0) {
	            x+=xMovement;
	            y+=yMovement;
	            
	            int pos2=y*4+x;
	            board[pos]=board[pos2];
	            board[pos2]=null;
	            pos=pos2;
	        }
	    }
	}
	
	private CCSprite getSprite4Item(int number){
		int size=gridCellSize-2;

	    android.graphics.Bitmap img=android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888);
	    android.graphics.Canvas canvas=new android.graphics.Canvas(img);
	    Paint paint=new Paint();
	    paint.setColor(getPieceColor(number));
	    canvas.drawRect(new Rect(0,0,size, size), paint);
        //test the font size, try to fit the grid
        String label=""+ number;
        int fontSize=super.mediumFontSize();
        paint.setTextSize(fontSize);
        float sz2=paint.measureText("15");
        fontSize=(int)(fontSize * size/sz2 * 0.5f);
        //android.util.Log.e("SK", ""+fontSize);
        paint.setTextSize(fontSize);
        
        FontMetrics fm = new FontMetrics();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLACK);
        paint.getFontMetrics(fm);
        canvas.drawText(label, size/2, size/2 + -(fm.ascent + fm.descent) / 2, paint);	        
		
        return CCSprite.sprite(img, ""+number);
	}
	
	private int getPieceColor(int piece) {
	
	    int x, y;
	    int r, g, b;
	    
	    y = piece / 4;
	    x = piece % 4;
	    
	    r = ((4 - x) * 255) / 4;
	    g = ((4 - y) * 255) / 4;
	    b = 128;
	    
	    return Color.argb(0xff, r, g, b);
	}

	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
        if (rcGrid.contains(p1.x,  p1.y)) {
            int x=(int)((p1.x - rcGrid.origin.x) / gridCellSize);
            int y=(int)((p1.y - rcGrid.origin.y) / gridCellSize);
            boolean moved=this.move(x, y, -1, 0);
            if (!moved) {
            	moved=this.move(x, y, 1, 0);
                if (!moved) {
                	moved=this.move(x, y, 0, -1);
                    if (!moved)
                    	moved=this.move(x, y, 0, 1);
                }
            }
            if (moved) {
                playSound("audio/sounds/drip.wav");
                this.checkAnswer();
            }
            else {
                playSound("audio/sounds/brick.wav");
            }
        }
        return true;
	}
	
	private void checkAnswer() {
	    int target[]={13,14,15,0,9,10,11,12,5,6,7,8,1,2,3,4};
	    for (int i = 0; i < 16; ++i) {
	        int value=(board[i]==null) ? 0 : board[i].getTag();
	        if (value != target[i])
	            return;
	    }
	    flashAnswerWithResult(true, true, null, null, 2);
	}
	private boolean move(int x, int y, int xMovement, int yMovement){
	    if (xMovement < 0) {
	        if (x <= 0)
	            xMovement=0;
	    }
	    else if (xMovement > 0) {
	        if (x >= 3)
	            xMovement=0;
	    }
	    if (yMovement < 0) {
	        if (y <= 0)
	            yMovement=0;
	    }
	    else if (yMovement > 0) {
	        if (y >= 3)
	            yMovement=0;
	    }
	    boolean moved=false;
	    if (xMovement != 0 || yMovement != 0) {
	        int orgPos=y*4+x;
	        
	        x+=xMovement;
	        y+=yMovement;
	        
	        int pos=y*4+x;
	        if (board[pos] == null) {
	            board[pos]=board[orgPos];
	            board[orgPos]=null;
	            board[pos].setPosition(rcGrid.origin.x + x * gridCellSize + gridCellSize/2, rcGrid.origin.y + y * gridCellSize + gridCellSize/2);
	            moved=true;
	        }
	    }
	    return moved;
	}
}
