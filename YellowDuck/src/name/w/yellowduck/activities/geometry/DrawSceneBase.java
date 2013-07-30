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


package name.w.yellowduck.activities.geometry;

import name.w.yellowduck.EllipseSprite;
import name.w.yellowduck.LineSprite;
import name.w.yellowduck.PolygonSprite;
import name.w.yellowduck.PrimSprite;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
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
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4F;

import android.view.MotionEvent;

public abstract class DrawSceneBase extends name.w.yellowduck.YDActLayerBase {
	protected final int kTagTooltip             =10;
	protected final int kTagTooltipClr          =50;
	protected final int kLineWidth              =8;

	protected final int kTooltipSelect          =1;
	protected final int kTooltipLine            =2;
	protected final int kTooltipRectangle       =3;
	protected final int kTooltipRectangleFilled =4;
	protected final int kTooltipCircle          =5;
	protected final int kTooltipCircleFilled    =6;
	protected final int kTooltipFill            =7;
	protected final int kTooltipDel             =8;
	protected final int kTooltipUp              =9;
	protected final int kTooltipDown            =10;
	protected final int kTooltipClr             =11;
	
	private final int kZorder             =2;
	private final int kArea2Small         =10;


	protected float tooltipsCanvasWidth;
    
    private CCSprite spriteClrSelection;
    private ccColor4F currentClr;
    
    CCMenuItemSprite selectedTooltipMenuitem;
    private int selectedTooltip;
    
    private CGRect rcWorkingArea;
    private CGPoint ptInitTouched, ptLastTouched;
    
    private int zOrder;
    private PrimSprite primSprite;
    private SelectionHandleSprite selectionHandle;
    private int mappingIdx0, mappingIdx1;
	
    private boolean simpleEditMode;
    private boolean halfTransparent;
    private int alignment;

    public DrawSceneBase() {
    	super();
    	this.alignment=1;
    	super.setColor(ccColor3B.ccGRAY);
    }
    
    public boolean isSimpleEditMode() {
		return simpleEditMode;
	}

	public void setSimpleEditMode(boolean simpleEditMode) {
		this.simpleEditMode = simpleEditMode;
	}

	public boolean isHalfTransparent() {
		return halfTransparent;
	}

	public void setHalfTransparent(boolean halfTransparent) {
		this.halfTransparent = halfTransparent;
	}

	public int getAlignment() {
		return alignment;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}

	protected void setupTooltips(int tooltips[], int total) {
	    tooltipsCanvasWidth=szWin.width/6;
	    //tooltips background
	    CCSprite bgSprite=spriteFromExpansionFile("image/activities/math/geometry/draw/tool-selector.png");
	    bgSprite.setScaleX(tooltipsCanvasWidth/bgSprite.getContentSize().width);
	    bgSprite.setScaleY((szWin.height-topOverhead())/bgSprite.getContentSize().height);
	    bgSprite.setPosition(tooltipsCanvasWidth/2, szWin.height-topOverhead()-bgSprite.getContentSize().height*bgSprite.getScaleY()/2);
	    super.addChild(bgSprite,1);
	    
	    selectedTooltipMenuitem=null;
	    
	    float margin=4;
	    float tooltipSize=(szWin.height - topOverhead())/9;//(tooltipsCanvasWidth-margin * 4)/2;
	    if (tooltipSize > tooltipsCanvasWidth/2)
	    	tooltipSize=tooltipsCanvasWidth/2;
	    float yTop=szWin.height-topOverhead() - margin;
	    
	    //in the same order as kTooltipXXX
	    String tools[]={"tool-select_on.png", "tool-line_on.png", "tool-rectangle_on.png", "tool-filledrectangle_on.png",
	        "tool-circle_on.png", "tool-filledcircle_on.png",
	        "tool-fill_on.png", "tool-del_on.png", "tool-up_on.png", "tool-down_on.png"};
	    
	    boolean drawPalette=false;
	    float yPos=0;
	    for (int i = 0; i < total; ++i) {
	        if (tooltips[i] <= 0)
	            continue;
	        if (tooltips[i]==kTooltipClr) {
	            drawPalette=true;
	        }
	        else {
	            String str=tools[tooltips[i]-1];
	
	            String img="image/activities/math/geometry/draw/" + str;
	            String imgSel=super.buttonize(img);
	            CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(imgSel);
	            CCSprite sprite=spriteFromExpansionFile(img);
	            CCSprite spriteSelected=CCSprite.sprite(texture);
	            CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite,spriteSelected,this,"tooltipTouched");
	            menuitem.setScale(tooltipSize/sprite.getContentSize().width*0.92f);
	            menuitem.setTag(kTagTooltip+tooltips[i]);
	            CCMenu menu = CCMenu.menu(menuitem);
	            float xPos=tooltipsCanvasWidth * (((i&1)==0)?0.22f:0.7f);
	            yPos=yTop - (i/2) * tooltipSize -tooltipSize/2;
	            menu.setPosition(xPos, yPos);
	            super.addChild(menu,2);
	            
	            if (selectedTooltipMenuitem == null) {
	                selectedTooltipMenuitem=menuitem;
	                selectedTooltip=tooltips[i];
	            }
	            
	            if (selectedTooltip==tooltips[i]) {
	                sprite.setColor(ccColor3B.ccWHITE);
	            }
	            else {
	            	sprite.setColor(ccColor3B.ccGRAY);
	            }
	        }
	    }
	    if (drawPalette) {
	        yTop=yPos-tooltipSize/2;
	        
	        float sz=tooltipSize*0.8f;
	        spriteClrSelection=spriteFromExpansionFile("image/misc/selectionmask.png");
	        spriteClrSelection.setScale((sz+2)/spriteClrSelection.getContentSize().width);
	        super.addChild(spriteClrSelection,2);
	
	        for (int i = 0; i < 8; ++i) {
	            float xPos=tooltipsCanvasWidth * (((i&1)==0)?0.22f:0.7f);
	            yPos=yTop - (i/2) * (sz+2) - sz/2;
	            //this png image must be white
	            CCSprite sprite=spriteFromExpansionFile("image/misc/layermaskwhite.png");
	            CCSprite spriteSel=spriteFromExpansionFile("image/misc/layermaskwhite.png");
	            ccColor4F clr=this.getColor(i);
	            sprite.setColor(new ccColor3B((int)(clr.r*0xff), (int)(clr.g*0xff), (int)(clr.b*0xff)));
	            CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSel, this, "tooltipClrTouched");
	            menuitem.setScaleX(sz/sprite.getContentSize().width*0.8f);
	            menuitem.setScaleY(sz/sprite.getContentSize().height*0.8f);
	            menuitem.setTag(kTagTooltipClr+i);
	            CCMenu menu = CCMenu.menu(menuitem);
	            menu.setPosition(xPos, yPos);
	            super.addChild(menu, 3);
	            
	            if (i <= 0) {
	                currentClr=clr;
	                spriteClrSelection.setPosition(menu.getPosition());
	            }
	        }
	    }
	    
	    zOrder=kZorder;
	    if (selectionHandle!=null) {
	        selectionHandle.removeFromParentAndCleanup(true);
	    }
	    selectionHandle=null;
	}
	public void tooltipTouched(Object _sender) {
		CCNode sender=(CCNode)_sender;
		
	    selectedTooltipMenuitem.setColor(ccColor3B.ccGRAY);
	    selectedTooltipMenuitem=(CCMenuItemSprite)_sender;
	    selectedTooltipMenuitem.setColor(ccColor3B.ccWHITE);
	    selectedTooltip=sender.getTag()-kTagTooltip;
	    
	    super.playSound("audio/sounds/bleep.wav");
	    
	    if (selectedTooltip != kTooltipSelect && selectionHandle!=null) {
	        selectionHandle.removeFromParentAndCleanup(true);
	        selectionHandle=null;
	    }
	    if (!simpleEditMode) {
	        String key="drawing_tool_" + selectedTooltip;
	        String tips=localizedString(key);
	
	        CCLabel label=CCLabel.makeLabel(tips, super.sysFontName(), super.smallFontSize()-2);
	        label.setPosition(sender.getParent().getPosition());
	        label.setColor(ccColor3B.ccBLUE);
	        super.addChild(label, 4);
	        
	        CCMoveTo moveAction=CCMoveTo.action(1.5f, CGPoint.ccp(0-label.getContentSize().width, label.getPosition().y+label.getContentSize().height*3));
	        CCCallFuncN doneAction = CCCallFuncN.action(this, "removeMe");
	        label.runAction(CCSequence.actions(moveAction, doneAction));
	    }
	}
	public void tooltipClrTouched(Object _sender){
		CCNode sender=(CCNode)_sender;
		
	    currentClr=this.getColor(sender.getTag()-kTagTooltipClr);
	    spriteClrSelection.setPosition(sender.getParent().getPosition());
	    super.playSound("audio/sounds/drip.wav");
	}
	protected ccColor4F getColor(int idx) {
	    //0:black, 1:red, 2:yellow, 3:olive, 4:green, 5:purple, 6:silver, 7:blue, 8:transparent
	    ccColor4F clrs[]={new ccColor4F(0, 0, 0, 1.0f), new ccColor4F(1.0f, 0, 0, 1.0f), new ccColor4F(1.0f, 1.0f, 0, 1.0f),new ccColor4F(0.5f, 0.5f, 0, 1.0f),
	    		new ccColor4F(0, 1.0f, 0, 1.0f), new ccColor4F(0.5f, 0, 0.5f, 1.0f), new ccColor4F(0.75f, 0.75f, 0.75f, 1.0f), new ccColor4F(0, 0, 1.0f, 1.0f), new ccColor4F(0, 0, 0, 0)};
	    
	    ccColor4F clr=clrs[idx];
	    if (halfTransparent) {
	        clr.a=0.6f;
	    }
	    return clr;
	}
	protected void setupWorkingArea(CGRect rc) {
	    rcWorkingArea=rc;
	}
	private CGPoint clip2workingArea(CGPoint pt, boolean align) {
	    float cx=pt.x-rcWorkingArea.origin.x;
	    if (cx < 0)
	        cx=0;
	    else if (cx > rcWorkingArea.size.width)
	        cx=rcWorkingArea.size.width;
	    float cy=pt.y-rcWorkingArea.origin.y;
	    if (cy < 0)
	        cy=0;
	    else if (cy > rcWorkingArea.size.height)
	        cy=rcWorkingArea.size.height;
	    if (align) {
	        cx=(int)cx/alignment*alignment;
	        cy=((int)cy+alignment/2)/alignment*alignment;
	    }
	    return CGPoint.ccp(rcWorkingArea.origin.x+cx, rcWorkingArea.origin.y+cy);
	}
	private CGSize clipMovement(PrimSprite element, float xOffset, float yOffset){
	    CGRect rc=element.enclosedArea();
	
	    if (xOffset < 0) {
	        float distance=rcWorkingArea.origin.x-rc.origin.x;
	        if (xOffset < distance)
	            xOffset=distance;
	    }
	    else if (xOffset > 0) {
	        float distance=rcWorkingArea.origin.x+rcWorkingArea.size.width-(rc.origin.x+rc.size.width);
	        if (xOffset > distance)
	            xOffset=distance;
	    }
	    if (yOffset < 0) {
	        float distance=rcWorkingArea.origin.y-rc.origin.y;
	        if (yOffset < distance)
	            yOffset=distance;
	    }
	    else if (yOffset > 0) {
	        float distance=rcWorkingArea.origin.y+rcWorkingArea.size.height-(rc.origin.y+rc.size.height);
	        if (yOffset > distance)
	            yOffset=distance;
	    }
	    return CGSize.make(xOffset, yOffset);
	}
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint pt = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
		        
        CGPoint raw=this.clip2workingArea(pt, false);
		CGPoint p1 =this.clip2workingArea(pt, true);

        switch (selectedTooltip) {
            case kTooltipSelect:
                if (simpleEditMode) {
                    primSprite=null;
                    for (int i = floatingSprites.size() - 1; i >= 0; --i) {
                        PrimSprite sprite=(PrimSprite)floatingSprites.get(i);
                        if (sprite.hit(raw)) {
                            primSprite=sprite;
                            break;
                        }
                    }
                }
                else {
                    if (selectionHandle!=null) {
                        if (!selectionHandle.hit(raw)) {
                            selectionHandle.removeFromParentAndCleanup(true);
                            selectionHandle=null;
                        }
                    }
                    if (selectionHandle==null) {
                        for (int i = floatingSprites.size() - 1; i >= 0; --i) {
                            PrimSprite sprite=(PrimSprite)floatingSprites.get(i);
                            if (sprite.hit(raw)) {
                                primSprite=sprite;
                                
                                CGRect rc=sprite.enclosedArea();
                                	    p1=rc.origin;
                                CGPoint p2=CGPoint.ccp(p1.x+rc.size.width, p1.y);
                                CGPoint p3=CGPoint.ccp(p1.x+rc.size.width, p1.y+rc.size.height);
                                CGPoint p4=CGPoint.ccp(p1.x, p1.y+rc.size.height);
                                
                                selectionHandle=new SelectionHandleSprite(p1, p2, p3, p4);
                                selectionHandle.setWorkingArea(rcWorkingArea);
                                selectionHandle.setHandleSize(buttonSize()/2);
                                super.addChild(selectionHandle,100);
                                
                                //needs to determine how the rect is constructed based on the line start and end point
                                if (sprite.isLine()) {
                                    LineSprite lineSprite=(LineSprite)sprite;
                                    CGPoint points[]=new CGPoint[4];
                                    points[0]=p1; points[1]=p2;points[2]=p3;points[3]=p4;
                                    CGPoint p1Line=lineSprite.getP1();
                                    for (int j = 0; j < 4; ++j) {
                                        if (points[j].x==p1Line.x && points[j].y == p1Line.y) {
                                            mappingIdx0=j;
                                            break;
                                        }
                                    }
                                    mappingIdx1 = (mappingIdx0+2+4)%4;
                                }
                                break;
                            }
                        }
                    }
                    if (selectionHandle!=null)
                        selectionHandle.hit(raw);
                }
                break;
            default:
                primSprite=null;
                break;
        }
        ptInitTouched=ptLastTouched=p1;
        
        return true;
	}
	public boolean ccTouchesMoved(MotionEvent event) {
		CGPoint pt = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
		CGPoint p1 = this.clip2workingArea(pt, true);
        
        boolean newBorn=false;
        
        switch (selectedTooltip) {
            case kTooltipSelect:
                if (simpleEditMode) {
                    if (primSprite!=null) {
                        //This will move out of working area
                        float xOffset=p1.x-ptLastTouched.x;
                        float yOffset=p1.y-ptLastTouched.y;
                        CGSize movement=this.clipMovement(primSprite,xOffset,yOffset);
                        primSprite.moveWithOffsetX(movement.width,movement.height);
                    }
                }
                else if (selectionHandle!=null) {
                    selectionHandle.move(p1.x-ptLastTouched.x,p1.y-ptLastTouched.y);
                    switch (primSprite.getType()) {
                        case PrimSprite.kTypePrimLine:
                        {
                            LineSprite sprite=(LineSprite)primSprite;
                            CGPoint pts[]=new CGPoint[4];
                            for (int i = 0; i < 4; ++i) {
                                pts[i]=selectionHandle.getPoint(i);
                            }
                            sprite.setP1(pts[mappingIdx0]);
                            sprite.setP2(pts[mappingIdx1]);
                        }
                            break;
                        case PrimSprite.kTypePrimPloygon:
                        {
                            PolygonSprite sprite=(PolygonSprite)primSprite;
                            for (int i = 0; i < 4; ++i) {
                                sprite.setVertix(i, selectionHandle.getPoint(i));
                            }
                        }
                            break;
                        case PrimSprite.kTypePrimEllipse:
                        {
                            EllipseSprite sprite=(EllipseSprite)primSprite;
                            CGPoint pts[]=new CGPoint[4];
                            for (int i = 0; i < 4; ++i) {
                                pts[i]=selectionHandle.getPoint(i);
                            }
                            sprite.setCenter(CGPoint.ccp((pts[0].x+pts[1].x)/2, (pts[1].y+pts[2].y)/2));
                            sprite.setRx(Math.abs(pts[0].x - pts[1].x)/2);
                            sprite.setRy(Math.abs(pts[1].y - pts[2].y)/2);
                        }
                            break;
                    }
                    
                }
                break;
            case kTooltipLine:
                if (primSprite == null) {
                    LineSprite sprite=new LineSprite(ptLastTouched, p1);
                    sprite.setLineWidth(kLineWidth);
                    sprite.setClr(currentClr);
                    
                    primSprite=sprite;
                    newBorn=true;
                }
                if (primSprite.isLine()) {
                    LineSprite sprite=(LineSprite)primSprite;
                    sprite.setP2(p1);
                }
                break;
            case kTooltipRectangle:
            case kTooltipRectangleFilled:
                if (primSprite == null) {
                    PolygonSprite sprite=new PolygonSprite(4);
                    sprite.setLineWidth(kLineWidth);
                    sprite.setClr(currentClr);
                    sprite.setSolid((selectedTooltip==kTooltipRectangleFilled));
                    
                    primSprite=sprite;
                    newBorn=true;
                }
                if (primSprite.isPolygon()) {
                    PolygonSprite sprite=(PolygonSprite)primSprite;
                    sprite.setVertix(0,ptInitTouched);
                    sprite.setVertix(1, CGPoint.ccp(ptInitTouched.x, p1.y));
                    sprite.setVertix(2, p1);
                    sprite.setVertix(3, CGPoint.ccp(p1.x, ptInitTouched.y));
                }
                break;
            case kTooltipCircle:
            case kTooltipCircleFilled:
                if (primSprite == null) {
                    EllipseSprite sprite=new EllipseSprite(CGPoint.ccp(0,  0), 100, 100);
                    sprite.setLineWidth(kLineWidth);
                    sprite.setClr(currentClr);
                    sprite.setSolid((selectedTooltip==kTooltipCircleFilled));

                    primSprite=sprite;
                    newBorn=true;
                }
                if (primSprite.isEllipse()) {
                    EllipseSprite sprite=(EllipseSprite)primSprite;
                    sprite.setCenter(CGPoint.ccp(ptInitTouched.x+(p1.x-ptInitTouched.x)/2, ptInitTouched.y+(p1.y-ptInitTouched.y)/2));
                    sprite.setRx(Math.abs(p1.x-ptInitTouched.x)/2);
                    sprite.setRy(Math.abs(p1.y-ptInitTouched.y)/2);
                }
                break;
                
        }
        if (newBorn) {
            super.addChild(primSprite,zOrder++);
            floatingSprites.add(primSprite);
            this.normalize();
            
            playSound("audio/sounds/grow.wav");
        }
        ptLastTouched=p1;
        
        return true;
	}
	
	public boolean ccTouchesEnded(MotionEvent event) {
		CGPoint pt = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
        
        CGPoint raw=this.clip2workingArea(pt, false);
		CGPoint p1 =this.clip2workingArea(pt, true);
        
        float distance=distanceFrom(p1, ptInitTouched);
        if (distance < kArea2Small) {
            CCSprite sparkle=spriteFromExpansionFile("image/misc/star.png");
            sparkle.setPosition(raw);
            sparkle.setScale(2.0f);
            super.addChild(sparkle,100);
            CCScaleTo scaleDownAction=CCScaleTo.action(0.4f, 2.0f);
            CCCallFuncN actionDone = CCCallFuncN.action(this, "removeMe");
            sparkle.runAction(CCSequence.actions(scaleDownAction, actionDone));
        }
        switch (selectedTooltip) {
            case kTooltipDel:
                for (int i = floatingSprites.size() - 1; i >= 0; --i) {
                    PrimSprite sprite=(PrimSprite)floatingSprites.get(i);
                    if (sprite.hit(raw)) {
                        sprite.removeFromParentAndCleanup(true);
                        floatingSprites.remove(i);
                        
                        super.playSound("audio/sounds/eraser1.wav");
                        break;
                    }
                }
                this.sortSpritesOnZOrder();
                break;
            case kTooltipFill:
                for (int i = floatingSprites.size() - 1; i >= 0; --i) {
                    PrimSprite sprite=(PrimSprite)floatingSprites.get(i);
                    if (sprite.hit(raw)) {
                        sprite.setClr(currentClr);
                        super.playSound("audio/sounds/paint1.wav");
                        break;
                    }
                }
                break;
            case kTooltipUp:
            case kTooltipDown:
                for (int i = floatingSprites.size() - 1; i >= 0; --i) {
                    PrimSprite sprite=(PrimSprite)floatingSprites.get(i);
                    if (sprite.hit(raw)) {
                        if (selectedTooltip==kTooltipUp) {
                        	sprite.removeFromParentAndCleanup(true);
                        	super.addChild(sprite, sprite.getZOrder()+1);
                            floatingSprites.remove(i);
                            floatingSprites.add(sprite);
                        }
                        else {
                        	sprite.removeFromParentAndCleanup(true);
                        	super.addChild(sprite, sprite.getZOrder()-1);
                        	
                        	floatingSprites.remove(i);
                            floatingSprites.add(0, sprite);
                        }
                        super.playSound("audio/sounds/line_end.wav");
                        break;
                    }
                }
                this.sortSpritesOnZOrder();
                break;
            case kTooltipLine:
            case kTooltipRectangle:
            case kTooltipCircle:
            case kTooltipSelect:
                //delete it if it is too small
                if (primSprite!=null) {
                    super.playSound("audio/sounds/line_end.wav");
                    this.normalize();
                }
                break;
        }
        
        return true;
	}

	private void sortSpritesOnZOrder() {
	    int total=floatingSprites.size();
	    boolean more=total > 1;
	    while (more) {
	        more=false;
	        for (int i = 0; i < total-1; ++i) {
	            CCNode first=floatingSprites.get(i);
	            CCNode second=floatingSprites.get(i+1);
	            if (first.getZOrder() > second.getZOrder()) {
	                more=true;
	                floatingSprites.remove(i);
	                floatingSprites.add(i+1, first);
	            }
	        }
	    }
	    zOrder=kZorder;
	    for (CCNode node : floatingSprites) {
	    	node.removeFromParentAndCleanup(true);
	    	super.addChild(node, zOrder++);
	    }
	}
	
	//Returns how many tiny objects are removed.
	protected int normalize() {
	    int total=floatingSprites.size();
	    int removed=0;
	    for (int i = total - 1; i >=0; --i) {
	        PrimSprite sprite=(PrimSprite)floatingSprites.get(i);
	        CGRect rc=sprite.enclosedArea();
	        if (sprite.isLine()) {
	            if (rc.size.width == 0)
	                rc.size.width=1;
	            if (rc.size.height==0)
	                rc.size.height=1;
	        }
	        if (Math.abs(rc.size.width * rc.size.height) < kArea2Small) {
	            if (primSprite == sprite) {
	                primSprite=null;
	                if (selectionHandle!=null) {
	                    selectionHandle.removeFromParentAndCleanup(true);
	                    selectionHandle=null;
	                }
	            }
	            //following operations will release this element.
	            sprite.removeFromParentAndCleanup(true);
	            floatingSprites.remove(i);
	            ++removed;
	        }
	    }
	    return removed;
	}
	
	public void removeMe(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    sender.removeFromParentAndCleanup(true);
	}
}
