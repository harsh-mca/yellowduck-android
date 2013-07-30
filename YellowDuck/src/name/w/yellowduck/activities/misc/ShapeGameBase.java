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


package name.w.yellowduck.activities.misc;

import name.w.yellowduck.Category;
import name.w.yellowduck.Schema;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import android.graphics.Bitmap;
import android.view.MotionEvent;

public abstract class ShapeGameBase extends name.w.yellowduck.YDActLayerBase {
	private java.util.ArrayList<YDShape> shapes;
	private CCNode pickedSprite;
	
    protected float separator;
    protected CCSprite backgroundSprite;
    
    protected boolean multiplePages;
    protected float yRoomThumbnail;
    protected int currentPage, pageSize, totalPages;
    protected CCMenu pageUpMenu, pageDnMenu;
    
    private float shapeScale;
    private String secondVoice;

    public ShapeGameBase() {
    	super();
    	super.setColor(ccColor3B.ccWHITE);
        shapes=new java.util.ArrayList<YDShape>();
        
        separator=szWin.width / 8;
        //page up & page down buttons
        float buttonsWidth=super.buttonSize()*2;
        if (separator < buttonsWidth) {
            separator = buttonsWidth;
        }
        this.shapeScale=0;
    }

    public float getShapeScale() {
		return shapeScale;
	}
	public void setShapeScale(float shapeScale) {
		this.shapeScale = shapeScale;
	}

	public void onEnter() {
		super.onEnter();
		
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionOk);

	    super.setIsTouchEnabled(true);
		
	    Bitmap apart=super.roundCornerRect(4, (int)(szWin.height - super.topOverhead()-super.bottomOverhead()), 0, Schema.kPopUpBgClr);
	    CCSprite apartSprite=CCSprite.sprite(apart, "__");
	    apartSprite.setPosition(separator+2, super.bottomOverhead()+apartSprite.getContentSize().height/2);
	    super.addChild(apartSprite,0);
	}
	
	protected void addShape(YDShape shape) {
	    shapes.add(shape);
	}
	protected void preGameInit(String bg) {
	    super.clearFloatingSprites();
	    
	    shapes.clear();
	    backgroundSprite=null;
	    this._drawBackground(bg);
	}
	protected void postGameInit(boolean drawHotspots, boolean bAutoSmall) {
	    String font=super.sysFontName();
	    
	    java.util.ArrayList<YDShape> stockImages=new java.util.ArrayList<YDShape>();
	    for (YDShape shape : shapes) {
	        if (shape.getType() == YDShape.kShapeLabelText) {
	            //make sure the label is what we wanted
	            if (shape.getResource()!=null && !shape.getResource().startsWith("/(null)")) {
	                CCLabel labelMark = CCLabel.makeLabel(shape.getResource(), font, super.smallFontSize());
	                labelMark.setColor(ccColor3B.ccBLUE);
	                labelMark.setPosition(shape.getPosition());
	                super.addChild(labelMark, 2);
	                floatingSprites.add(labelMark);
	                //adjust its size if it is too wide
	                float x1=labelMark.getPosition().x-labelMark.getContentSize().width/2;
	                float x2=labelMark.getPosition().x+labelMark.getContentSize().width/2;
	                if (x1 <= separator || x2 >= szWin.width) {
	                    float cx1=labelMark.getPosition().x-separator;
	                    float cx2=szWin.width-labelMark.getPosition().x;
	                    float cx=2*((cx1>cx2)?cx2:cx1);
	                    //at most 1/3 of the right part of the screen
	                    labelMark.setScale(cx / labelMark.getContentSize().width * 0.85f);
	                }
	            }
	        }
	        else if (shape.getType() == YDShape.kShapeLabelImage) {
	            CCSprite sprite=spriteFromExpansionFile(shape.getResource());
	            sprite.setPosition(shape.getPosition());
	            CGSize fit2=shape.getFit2();
	            if (fit2 != null && fit2.width > 0 && fit2.height > 0) {
	                float scale1=fit2.width / sprite.getContentSize().width;
	                float scale2=fit2.height / sprite.getContentSize().height;
	                float scale=(scale2 > scale1) ? scale1 : scale2;
	                sprite.setScale(scale);
	            }
	            
	            super.addChild(sprite,1);
	            floatingSprites.add(sprite);
	        }
	        else if (shape.isStock()) {
	            stockImages.add(shape);
	        }
	    }
	    if (drawHotspots)
	        this._drawHotspots();
	    
	    //left items
	    int total=stockImages.size();
	    float yPos=szWin.height - super.topOverhead();
	    yRoomThumbnail=(yPos - super.bottomOverhead() - super.buttonSize()) / total;
	
	//    Apple says that the avg finger tap is 44x44 (from WWDC). All table rows are recommended to be at least that height. 
	//    It is common for icons to appear 32x32, but have padding to make the touchable area 44x44
	
	    if (yRoomThumbnail < 44)
	        yRoomThumbnail = 44;
	    int selections[]=new int[total];
	    for (int i = 0; i < total; ++i)
	        selections[i]=i;
	    super.randomIt(selections,total);
	    multiplePages=false;
	    pageSize=0;
	    float yBottom=super.bottomOverhead() + super.buttonSize();
	    for (int i = 0; i < total; ++i) {
	        YDShape shape=stockImages.get(selections[i]);
	        CCSprite sprite=spriteFromExpansionFile(shape.getResource());
	        sprite.setTag(selections[i]);
	        float scale1=separator/sprite.getContentSize().width;
	        float scale2=yRoomThumbnail/sprite.getContentSize().height;
	        float scale=(scale2 > scale1) ? scale1 : scale2;
	        sprite.setScale(scale*0.9f);
	        sprite.setPosition(separator / 2, yPos - yRoomThumbnail/2);
	        sprite.setUserData(shape);
	        super.addChild(sprite,3);
	        shape.setScale(sprite.getScale());
	        floatingSprites.add(sprite);
	        //too small to tap on, auto place it
	        float areaPixels=sprite.getContentSize().width * sprite.getContentSize().height;
	        if (backgroundSprite!=null)
	            areaPixels *= backgroundSprite.getScale() * backgroundSprite.getScale();
	        if (bAutoSmall && areaPixels < super.buttonSize() * super.buttonSize()) {
	            sprite.setPosition(shape.getPosition());
	            sprite.setScale(backgroundSprite.getScale());
	            shape.setPinned(true);
	            //NSLog(@"Voice not required: %@", [shape voice]);
	        }
	        else {
	            float bottom=sprite.getPosition().y - sprite.getContentSize().height*sprite.getScale()/2;
	            if (bottom < yBottom) {
	                if ((i == total - 1) && (bottom >= super.bottomOverhead())) {//this is the last one
	                    //ok to place the last one using the button place
	                }
	                else {
	                    sprite.setVisible(false);
	                    multiplePages=true;
	                }
	            }
	            else {
	                ++pageSize;
	            }
	            yPos -= yRoomThumbnail;
	        }
	    }
	    stockImages.clear();
	    
	    if (multiplePages) {
	        currentPage=0;
	        //previous page
	        String pageUp=super.renderSkinSVG2Button(Schema.kSvgArrowLeft,super.buttonSize());
	        CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(super.buttonize(pageUp));
	        CCSprite pageUpSprite=spriteFromExpansionFile(pageUp);
	        CCSprite pageUpPressedSprite=CCSprite.sprite(texture1);
	        CCMenuItemSprite pageUpMenuItem=CCMenuItemImage.item(pageUpSprite,pageUpPressedSprite,this,"pageUp");
	        pageUpMenuItem.setPosition(separator/4, super.bottomOverhead() + pageUpMenuItem.getContentSize().height/2);
	        pageUpMenu=CCMenu.menu(pageUpMenuItem);
	        pageUpMenu.setPosition(0,0);
	        pageUpMenu.setVisible(false); //we at the first page
	        super.addChild(pageUpMenu,2);
	        floatingSprites.add(pageUpMenu);
	
	        //next page
	        String pageDn=super.renderSkinSVG2Button(Schema.kSvgArrowRight,super.buttonSize());
	        CCTexture2D texture2=CCTextureCache.sharedTextureCache().addImageExternal(super.buttonize(pageDn));
	        CCSprite pageDnSprite=spriteFromExpansionFile(pageDn);
	        CCSprite pageDnPressedSprite=CCSprite.sprite(texture2);
	        CCMenuItemSprite pageDnMenuItem=CCMenuItemImage.item(pageDnSprite,pageDnPressedSprite,this,"pageDown");
	        pageDnMenuItem.setPosition(separator*3/4, super.bottomOverhead() + pageDnMenuItem.getContentSize().height/2);
	        pageDnMenu=CCMenu.menu(pageDnMenuItem);
	        pageDnMenu.setPosition(0,0);
	        super.addChild(pageDnMenu,2);
	        floatingSprites.add(pageDnMenu);
	    }
	}
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
		
		pickedSprite=null;
        CCNode clicked=null;
        for (CCNode card : floatingSprites) {
            YDShape shape=(YDShape)card.getUserData();
            if (shape!=null && shape.isStock() && !shape.isPinned() && card.getVisible()) {
                if (super.isNodeHit(card, p1)) {
                    clicked=card;
                    if (shape.getSound()!=null)
                        super.playSound(shape.getSound());
                    if (shape.getVoice() != null)
                        super.playVoice(shape.getVoice());
                    if (shape.getVoice2()!=null) {
                    	this.secondVoice=shape.getVoice2();
                        super.performSelector("playSecondVoice", 0.6f);// withObject:[shape voice2] afterDelay:0.6f];
                    }
                    break;
                }
            }
        }
        if (clicked!=null) {
            clicked.setPosition(p1);
            pickedSprite=clicked;
        }
        return true;
	}
	public boolean ccTouchesMoved(MotionEvent event) {
		if (pickedSprite != null) {
			CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
            pickedSprite.setPosition(p1);
            
            if (backgroundSprite!=null && pickedSprite.getPosition().x > separator)
                pickedSprite.setScale(backgroundSprite.getScale());
            else if (this.shapeScale > 0)
                pickedSprite.setScale(shapeScale);
	    }
		return true;
	}
	public boolean ccTouchesEnded(MotionEvent event) {
		if (pickedSprite != null) {
			CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
            if (p1.x < separator) {
                YDShape shape=(YDShape)pickedSprite.getUserData();
                pickedSprite.setScale(shape.getScale());
                pickedSprite.setPosition(separator/2, p1.y);
            }
            else {
                float shortestDistance=-1;
                CGPoint shortest2=CGPoint.ccp(0,0);
                CGSize fit2=CGSize.make(0, 0);
                for (CCNode slot : floatingSprites) {
                    YDShape shape=(YDShape)slot.getUserData();
                    if (shape!=null && shape.isStock()) {
                        float distance=super.distanceFrom(shape.getPosition(), p1);
                        if (shortestDistance < 0 || distance < shortestDistance) {
                            shortestDistance=distance;
                            shortest2=shape.getPosition();
                            fit2=shape.getFit2();
                        }
                    }
                }
                if (fit2==null) {
                	fit2=CGSize.make(0, 0);
                }
                if (shortestDistance >= 0) {
                    float overallsize=pickedSprite.getContentSize().height*pickedSprite.getScale();
                    if (pickedSprite.getContentSize().width*pickedSprite.getScale() > overallsize)
                        overallsize=pickedSprite.getContentSize().width*pickedSprite.getScale();
                    if (fit2.width > overallsize)
                        overallsize=fit2.width;
                    if (shortestDistance < overallsize*3/4) {
                        pickedSprite.setPosition(shortest2);
                        if (backgroundSprite!=null)
                            pickedSprite.setScale(backgroundSprite.getScale());
                        else if (fit2.width > 0 && fit2.height > 0) {
                            float scale1=fit2.width / pickedSprite.getContentSize().width;
                            float scale2=fit2.height / pickedSprite.getContentSize().height;
                            float scale=(scale2 > scale1) ? scale1 : scale2;
                            pickedSprite.setScale(scale);
                        }
                    }
                }
            }
            super.playSound("audio/sounds/line_end.wav");
        }
        this.relayoutThumbnails();
        pickedSprite=null;
        
        return true;
	}

	public void ok(Object _sender) {
	    boolean matched=true;
	    for (CCNode card : floatingSprites) {
	        YDShape shape=(YDShape)card.getUserData();
	        if (shape!=null && shape.isStock()) {
	            if (super.distanceFrom(shape.getPosition(), card.getPosition()) > 2) {
	                matched=false;
	                //if moved
	                if (card.getPosition().x > separator)
	                    super.flashWrongAnswer(card.getPosition(),2);
	            }
	        }
	    }
	    super.flashAnswerWithResult(matched,  matched, null, null, matched?2:1);
	}
	
	private void _drawBackground(String background){
	    if (background == null)
	        return;
	    
	    backgroundSprite=spriteFromExpansionFile(background);
	    float xRoom=szWin.width - separator;
	    float yRoom=szWin.height-super.topOverhead() - super.bottomOverhead();
	    float scale1=xRoom/backgroundSprite.getContentSize().width;
	    float scale2=yRoom/backgroundSprite.getContentSize().height;
	    float scale=(scale2 > scale1) ? scale1 : scale2;
	    backgroundSprite.setTag(0);
	    backgroundSprite.setScale(scale*0.9f);
	    backgroundSprite.setPosition(separator+xRoom/2, super.bottomOverhead() + yRoom / 2);
	    super.addChild(backgroundSprite,1);
	    
	    floatingSprites.add(backgroundSprite);
	}
	
	private void _drawHotspots(){
	    String font=super.sysFontName();
	    for (YDShape shape : shapes) {
	        if (shape.isStock()) {
	            CCLabel mark = CCLabel.makeLabel("o", font,super.smallFontSize());
	            mark.setColor(ccColor3B.ccRED);
	            mark.setPosition(shape.getPosition());
	            super.addChild(mark,2);
	            floatingSprites.add(mark);
	        }
	    }
	}
	public void playSecondVoice() {
		if (this.secondVoice!=null) {
			super.playVoice(this.secondVoice);
			this.secondVoice=null;
		}
	}
	private void relayoutThumbnails() {
	    if (!multiplePages)
	        return;
	    
	    float yPos=szWin.height - super.topOverhead();
	
	    java.util.ArrayList<CCNode> thumbnailSprites=new java.util.ArrayList<CCNode>();
	    for (CCNode node : floatingSprites) {
	        YDShape shape=(YDShape)node.getUserData();
	        if (shape!=null && shape.isStock() && node.getPosition().x <= separator) {
	            node.setVisible(false);
	            thumbnailSprites.add(node);
	        }
	    }
	    totalPages=(thumbnailSprites.size() + pageSize - 1) / pageSize;
	    if (currentPage >= totalPages)
	        currentPage=totalPages-1;
	    else if (currentPage < 0)
	        currentPage=0;
	    if (thumbnailSprites.size() > 0) {
	        int start=currentPage * pageSize;
	        int end=start + pageSize;
	        if (end > thumbnailSprites.size())
	            end=thumbnailSprites.size();
	        for (int i = start; i < end; ++i) {
	            CCNode one=thumbnailSprites.get(i);
	            one.setPosition(separator/2, yPos - yRoomThumbnail/2);
	            one.setVisible(true);
	
	            yPos -= yRoomThumbnail;
	        }
	    }
	    pageUpMenu.setVisible((currentPage > 0));
	    pageDnMenu.setVisible((currentPage < totalPages-1));
	    
	    thumbnailSprites.clear();
	}
	
	public void pageUp(Object sender) {
	    --currentPage;
	    this.relayoutThumbnails();
	        
	}
	public void pageDown(Object sender) {
	    ++currentPage;
	    this.relayoutThumbnails();
	}
}
