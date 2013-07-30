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


package name.w.yellowduck.activities.color;

import name.w.yellowduck.Category;
import name.w.yellowduck.YDActLayerBase;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

import android.graphics.Bitmap;
import android.view.MotionEvent;

public class MosaicScene extends YDActLayerBase {
	private final int kPaletteRows        =4;
	private final int kPaletteCols        =8;

	private java.util.ArrayList<CCSprite> floatingLeftSprites, floatingRightSprites, floatingBottomSprites;
	private CCTexture2D palette; //4 rows, 8 cols, the last row is the blank cell
	int paletteWidth, paletteHeight;
    private int topPanelWidth, topPanelHeight;
    private int bottomPanelWidth; //height is the half size of top panel
    private int bottomPanelHeight;
    private CGPoint leftPanelCenter, rightPanelCenter, bottomPanelCenter;
    private CCSprite checked;
    
    private int _selection[]=new int[24];
    private int _rows, _cols;
    private int _pick;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new MosaicScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	public MosaicScene() {
		super();
		super.setColor(new ccColor3B(176, 224, 230));
		floatingLeftSprites=new java.util.ArrayList<CCSprite>();
		floatingRightSprites=new java.util.ArrayList<CCSprite>();
		floatingBottomSprites=new java.util.ArrayList<CCSprite>();
	}

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=6;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    this.setupOwnBackground();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionOk);
		
	    checked=spriteFromExpansionFile("image/activities/discovery/color/mosaic/button_checked.png");
	    checked.setVisible(false);
	    super.addChild(checked,2);
	
	    palette=textureFromExpansionFile("image/activities/discovery/color/mosaic/mosaic_palette.png");
	    paletteWidth=(int)(palette.getContentSize().width);
	    paletteHeight=(int)(palette.getContentSize().height);
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	private void setupOwnBackground() {
	    int topNavButtonSize = super.topOverhead();
	    int bottomNavButtonSize=super.bottomOverhead();
	    
	    int borderMargin=10;
	
	    topPanelHeight=(int)(szWin.height-topNavButtonSize-bottomNavButtonSize - borderMargin*4) * 2 / 3;
	    topPanelWidth=(int)(szWin.width - 3*borderMargin) / 2;
	    
	    Bitmap leftPanel=super.roundCornerRect(topPanelWidth, topPanelHeight, 0, new ccColor4B(0xff, 0xff, 0xff, 0xff));
	    CCSprite leftSprite=CCSprite.sprite(leftPanel, "left");
	    leftPanelCenter=CGPoint.ccp(borderMargin+leftSprite.getContentSize().width/2, szWin.height - topNavButtonSize - borderMargin - leftSprite.getContentSize().height/2);
	    leftSprite.setPosition(leftPanelCenter);
	    super.addChild(leftSprite,0);
	    
	    Bitmap rightPanel=super.roundCornerRect(topPanelWidth, topPanelHeight, 0, new ccColor4B(0xff, 0xff, 0xff, 0xff));
	    CCSprite rightSprite=CCSprite.sprite(rightPanel, "right");
	    rightPanelCenter=CGPoint.ccp(szWin.width - borderMargin - rightSprite.getContentSize().width/2, szWin.height - topNavButtonSize - borderMargin - rightSprite.getContentSize().height/2);
	    rightSprite.setPosition(rightPanelCenter);
	    super.addChild(rightSprite,0);
	    
	    bottomPanelWidth=(int)(szWin.width-borderMargin*2);
	    bottomPanelHeight=topPanelHeight/2;
	    Bitmap bottomPanel=super.roundCornerRect(bottomPanelWidth, bottomPanelHeight, 0, new ccColor4B(0xff, 0xff, 0xff, 0xff));
	    CCSprite bottomSprite=CCSprite.sprite(bottomPanel, "bottom");
	    bottomPanelCenter=CGPoint.ccp(szWin.width/2, bottomNavButtonSize + borderMargin*2 + bottomSprite.getContentSize().height/2);
	    bottomSprite.setPosition(bottomPanelCenter);
	    super.addChild(bottomSprite, 0);
	}
	protected void initGame(boolean firstTime, Object sender) {
	    super.initGame(firstTime, sender);
	    
	    for (CCNode node : floatingLeftSprites) {
	        node.removeFromParentAndCleanup(true);
	    }
	    for (CCNode node : floatingRightSprites) {
	        node.removeFromParentAndCleanup(true);
	    }
	    for (CCNode node : floatingBottomSprites) {
	        node.removeFromParentAndCleanup(true);
	    }
	    floatingLeftSprites.clear();
	    floatingRightSprites.clear();
	    floatingBottomSprites.clear();
	    
	    checked.setVisible(false);;
	    
	    int selUp2Row=0;
	    switch (mLevel) {
	        case 1:
	            _rows=2; _cols=4;
	            selUp2Row=1;
	            break;
	        case 2:
	            _rows=2; _cols=6;
	            selUp2Row=1;
	            break;
	        case 3:
	            _rows=3; _cols=6;
	            selUp2Row=2;
	            break;
	        case 4:
	            _rows=3; _cols=6;
	            selUp2Row=3;
	            break;
	        case 5:
	            _rows=4; _cols=6;
	            selUp2Row=3;
	            break;
	        case 6:
	            _rows=4; _cols=6;
	            selUp2Row=3;
	            break;
	    }
	    for (int i= 0; i < _rows * _cols; ++i) {
	        _selection[i]=super.nextInt(selUp2Row*kPaletteCols);
	    }
	    for (int i= 0; i < _rows; ++i) {
	        for (int j = 0; j < _cols; ++j) {
	            int sel=_selection[i*_cols+j];
	            int w=paletteWidth/kPaletteCols;
	            int h=paletteHeight/kPaletteRows;
	            int x=(sel%kPaletteCols) * w;
	            int y=(sel/kPaletteCols) * h;
	            CGRect rc=CGRect.make(x, y, w, h);
	            
	            int xRoom=topPanelWidth/_cols;
	            int yRoom=topPanelHeight/4; //up to 4 rows at the left panel
	            
	            CCSprite slotSprite=CCSprite.sprite(palette, rc);
	            slotSprite.setPosition(CGPoint.ccp(leftPanelCenter.x - topPanelWidth / 2 + j * xRoom + xRoom/2, leftPanelCenter.y + topPanelHeight / 2 - i * yRoom - yRoom/2));
	            slotSprite.setTag(sel);
	            slotSprite.setScale(yRoom/slotSprite.getContentSize().height*0.6f);
	            super.addChild(slotSprite, 1);
	            floatingLeftSprites.add(slotSprite);
	            
	            //corresponding blank cell at the right panel
	            rc=CGRect.make(0, (kPaletteRows-1) * h, w, h);
	            slotSprite=CCSprite.sprite(palette, rc);
	            slotSprite.setPosition(CGPoint.ccp(rightPanelCenter.x - topPanelWidth / 2 + j * xRoom + xRoom/2, rightPanelCenter.y + topPanelHeight / 2 - i * yRoom - yRoom/2));
	            slotSprite.setTag(-1);
	            slotSprite.setScale(yRoom/slotSprite.getContentSize().height*0.6f);
	            super.addChild(slotSprite,1);
	            floatingRightSprites.add(slotSprite);
	        }
	    }
	    //candidates
	    int intoRows=2; //up to two rows in the bottom panel
	    int colPerRow=(kPaletteCols*(kPaletteRows-1)/intoRows);
	    int xRoom=bottomPanelWidth/colPerRow; //total 24, into 2 rows, the last row in the palette is the blank cell
	    int yRoom=bottomPanelHeight/intoRows;
	    for (int i= 0; i < selUp2Row; ++i) {
	        for (int j = 0; j < kPaletteCols; ++j) {
	            int w=paletteWidth/kPaletteCols;
	            int h=paletteHeight/kPaletteRows;
	            int x=j * w;
	            int y=i * h;
	            CGRect rc=CGRect.make(x, y, w, h);
	            int sel=(i*kPaletteCols+j);
	            int row=sel/colPerRow;
	            int col=sel%colPerRow;
	            CCSprite slotSprite=CCSprite.sprite(palette, rc);
	            slotSprite.setPosition(bottomPanelCenter.x - bottomPanelWidth / 2 + col * xRoom + xRoom/2, bottomPanelCenter.y + bottomPanelHeight / 2 - row * yRoom - yRoom/2);
	            slotSprite.setTag(sel);
	            slotSprite.setScale(yRoom/slotSprite.getContentSize().height*0.6f);
	            super.addChild(slotSprite,1);
	            floatingBottomSprites.add(slotSprite);
	        }
	    }
	    _pick=-1;
	}
	
	public boolean ccTouchesBegan(MotionEvent event)
	{
	    CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
	
        for (CCSprite sprite : floatingBottomSprites) {
            if (super.isNodeHit(sprite, p1)) {
                _pick=sprite.getTag();
                checked.setPosition(sprite.getPosition());
                checked.setVisible(true);
                break;
            }
        }
        return true;
	}

	public boolean ccTouchesEnded(MotionEvent event) {
	    if (_pick < 0)
	        return true;
	    CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
        for (int i = 0; i < floatingRightSprites.size(); ++i) {
            CCSprite sprite=floatingRightSprites.get(i);;
            CGRect rc=CGRect.make(sprite.getPosition().x-sprite.getContentSize().width*sprite.getScale()/2,
                    sprite.getPosition().y-sprite.getContentSize().height*sprite.getScale()/2,
                    sprite.getContentSize().width*sprite.getScale(), sprite.getContentSize().height*sprite.getScale());
            if (rc.contains(p1.x, p1.y)) {
                int w=paletteWidth/kPaletteCols;
                int h=paletteHeight/kPaletteRows;
                int x=(_pick % kPaletteCols) * w;
                int y=(_pick / kPaletteCols) * h;
                
                CCSprite slotSprite=CCSprite.sprite(palette, CGRect.make(x, y, w, h));
                slotSprite.setPosition(sprite.getPosition());
                slotSprite.setScale(sprite.getScale());
                slotSprite.setTag(_pick);
                super.addChild(slotSprite,1);
                floatingRightSprites.remove(i);
                floatingRightSprites.add(i, slotSprite);
                
                sprite.removeFromParentAndCleanup(true);
                break;
            }
        }
        return true;
	}
	public void ok(Object sender) {
	    boolean  matched=true;
	    for (int i = 0; i < floatingLeftSprites.size(); ++i) {
	        CCSprite compare2=floatingLeftSprites.get(i);
	        CCSprite with=floatingRightSprites.get(i);
	        if (compare2.getTag() != with.getTag()) {
	            matched=false;
	            break;
	        }
	    }
	    super.flashAnswerWithResult(matched,matched,null,null,2);
	}
}
