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
import name.w.yellowduck.PrimSprite;
import name.w.yellowduck.Schema;
import name.w.yellowduck.SysHelper;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
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

public class TangramScene extends name.w.yellowduck.YDActLayerBase {
	private final int kTagOutlinePolygon      =10;
	private final int kTagPuzzleShapes        =11;
	private final int kTagResolutionShapes    =12;

    private java.util.ArrayList<TangramShape> puzzleShapes, resolutionShapes;
    private CGRect rcLeft, rcRight;
    private float precision;

    //UIRotationGestureRecognizer *rotationGuesture;
    
    private CCLabel promptLabel;
    private CGPoint ptLastTouched;
    int showShapeIdx, workingOnShapeIdx;
	

	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new TangramScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=1;
	    puzzleShapes=new java.util.ArrayList<TangramShape>();
	    resolutionShapes=new java.util.ArrayList<TangramShape>();	    
	    
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    CCSprite bgSprite=super.setupBackground(activeCategory.getBg(), kBgModeFit);
	    bgSprite.removeFromParentAndCleanup(true);
	    super.addChild(bgSprite, 1);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionOk);
	    	    
	    //rotationGuesture = [[UIRotationGestureRecognizer alloc]
	    //                                     initWithTarget:self action:@selector(handleRotation:)];
	    //[rotationGuesture setDelegate:self];
	    //[[[CCDirector sharedDirector] view] addGestureRecognizer:rotationGuesture];
	
	    mMaxSublevel=0;
	    mSublevel=SysHelper.getIntProfileValue("Tangram");
	    rcLeft=CGRect.make(43.0f/1024*szWin.width, 330.0f/666*szWin.height, 296.0f/1024*szWin.width, 280.0f/666*szWin.height);
	    rcRight=CGRect.make(420.0f/1024*szWin.width, 72.0f/666*szWin.height, 550.0f/1024*szWin.width, 536.0f/666*szWin.height);
	    //navigation keys
	    {
	        String imgLeft=renderSkinSVG2Button(Schema.kSvgArrowLeft, buttonSize());
	        String selectedLeft=buttonize(imgLeft);
	        CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(imgLeft);
	        CCTexture2D texture2=CCTextureCache.sharedTextureCache().addImageExternal(selectedLeft);
	        
	        CCSprite spriteLeft=CCSprite.sprite(texture1);
	        CCSprite spriteSelectedLeft=CCSprite.sprite(texture2);
	        CCMenuItemSprite menuitemLeft=CCMenuItemImage.item(spriteLeft, spriteSelectedLeft, this, "preBtnTouched");
	        CCMenu menuLeft = CCMenu.menu(menuitemLeft);
	        menuLeft.setPosition(rcLeft.origin.x+menuitemLeft.getContentSize().width/2, rcLeft.origin.y-menuitemLeft.getContentSize().height/2-2);
	        super.addChild(menuLeft, 2);
	        
	        promptLabel = CCLabel.makeLabel("1/245", super.sysFontName(), super.smallFontSize());
	        promptLabel.setColor(ccColor3B.ccBLACK);
	        promptLabel.setPosition(rcLeft.origin.x+rcLeft.size.width/2, rcLeft.origin.y-menuitemLeft.getContentSize().height/2-2);
	        super.addChild(promptLabel, 2);
	    }
	    {
	        String imgRight=renderSkinSVG2Button(Schema.kSvgArrowRight, buttonSize());
	        String selectedRight=buttonize(imgRight);
	        CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(imgRight);
	        CCTexture2D texture2=CCTextureCache.sharedTextureCache().addImageExternal(selectedRight);
	        
	        CCSprite spriteLeft=CCSprite.sprite(texture1);
	        CCSprite spriteSelectedLeft=CCSprite.sprite(texture2);
	        CCMenuItemSprite menuitemRight=CCMenuItemImage.item(spriteLeft, spriteSelectedLeft, this, "nxtBtnTouched");
	        CCMenu menuLeft = CCMenu.menu(menuitemRight);
	        menuLeft.setPosition(rcLeft.origin.x+rcLeft.size.width-menuitemRight.getContentSize().width/2, rcLeft.origin.y-menuitemRight.getContentSize().height/2-2);
	        super.addChild(menuLeft, 2);
	    }
	    float yRowPos=0, yRowMargin=6*preferredContentScale(true);
	    {
	        //show button
	    	String img="image/activities/puzzle/tangram/gtans_show.png";
	        String selected=buttonize(img);
	        CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(selected);
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCSprite spriteSelected=CCSprite.sprite(texture1);
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected, this, "showHint");
	        menuitem.setScale(preferredContentScale(true));
	        CCMenu menu = CCMenu.menu(menuitem);
	        yRowPos=rcLeft.origin.y-(menuitem.getContentSize().height*menuitem.getScale()+yRowMargin)*2;
	        menu.setPosition(rcLeft.origin.x+menuitem.getContentSize().width*menu.getScale()/2, yRowPos);
	        super.addChild(menu, 2);
	    }
	    {
	        //rotate left
	    	String img="image/activities/puzzle/tangram/gtans_rotate-left.png";
	        String selected=buttonize(img);
	        CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(selected);
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCSprite spriteSelected=CCSprite.sprite(texture1);
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected, this, "rotateLeft");
	        menuitem.setScale(preferredContentScale(true));
	        CCMenu menu = CCMenu.menu(menuitem);
	        menu.setPosition(rcLeft.origin.x+rcLeft.size.width/2, yRowPos);
	        super.addChild(menu, 2);
	    }
	    {
	        //rotate right
	    	String img="image/activities/puzzle/tangram/gtans_rotate.png";
	        String selected=buttonize(img);
	        CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(selected);
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCSprite spriteSelected=CCSprite.sprite(texture1);
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected, this, "rotateRight");
	        menuitem.setScale(preferredContentScale(true));
	        CCMenu menu = CCMenu.menu(menuitem);
	        menu.setPosition(rcLeft.origin.x+rcLeft.size.width-menuitem.getContentSize().width*menu.getScale()/2, yRowPos);
	        super.addChild(menu, 2);
	    }
	    //outline button
	    {
	    	String img="image/activities/puzzle/tangram/gtans_outline.png";
	        String selected=buttonize(img);
	        CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(selected);
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCSprite spriteSelected=CCSprite.sprite(texture1);
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected, this, "displayOutlines");
	        menuitem.setScale(preferredContentScale(true));
	        CCMenu menu = CCMenu.menu(menuitem);
	        yRowPos=rcLeft.origin.y-(menuitem.getContentSize().height*menuitem.getScale()+yRowMargin)*3;
	        menu.setPosition(rcLeft.origin.x+menuitem.getContentSize().width/2, yRowPos);
	        super.addChild(menu, 2);
	    }
	    {
	        //rotate left x 2
	    	String img="image/activities/puzzle/tangram/gtans_2x-rotate-left.png";
	        String selected=buttonize(img);
	        CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(selected);
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCSprite spriteSelected=CCSprite.sprite(texture1);
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected, this, "rotateLeft");
	        menuitem.setScale(preferredContentScale(true));
	        menuitem.setTag(2);
	        CCMenu menu = CCMenu.menu(menuitem);
	        yRowPos=rcLeft.origin.y-(menuitem.getContentSize().height*menuitem.getScale()+yRowMargin)*3;
	        menu.setPosition(rcLeft.origin.x+rcLeft.size.width/2, yRowPos);
	        super.addChild(menu, 2);
	    }
	    {
	        //rotate right x 2
	    	String img="image/activities/puzzle/tangram/gtans_2x-rotate.png";
	        String selected=buttonize(img);
	        CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(selected);
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCSprite spriteSelected=CCSprite.sprite(texture1);
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected, this, "rotateRight");
	        menuitem.setScale(preferredContentScale(true));
	        menuitem.setTag(2);
	        CCMenu menu = CCMenu.menu(menuitem);
	        menu.setPosition(rcLeft.origin.x+rcLeft.size.width-menuitem.getContentSize().width*menu.getScale()/2, yRowPos);
	        super.addChild(menu, 2);
	    }
	    {
	        //flip
	    	String img="image/activities/puzzle/tangram/tool-flip.png";
	        String selected=buttonize(img);
	        CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(selected);
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCSprite spriteSelected=CCSprite.sprite(texture1);
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected, this, "flip");
	        menuitem.setScale(preferredContentScale(true));
	        CCMenu menu = CCMenu.menu(menuitem);
	        menu.setPosition(rcLeft.origin.x+rcLeft.size.width/2, yRowPos-menuitem.getContentSize().height*menuitem.getScale()-yRowMargin);
	        super.addChild(menu, 2);
	    }
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	/*
	-(void)onExit {
	    [[[CCDirector sharedDirector] view] removeGestureRecognizer:rotationGuesture];
	    [rotationGuesture release];
	    
	    [super onExit];
	}
	*/
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
	    
	    puzzleShapes.clear();
	    resolutionShapes.clear();
	    
	    float scaleLeft=rcLeft.size.width/10;
	    java.util.ArrayList<TangramShape> shapes=createShapesFromConfigurationFile(mSublevel);
	    if (shapes.size()>0) {
	        for (TangramShape oneShape : shapes) {
	            oneShape.setShapeScale(scaleLeft);
	            oneShape.setClr(new ccColor4F(0, 0, 0, 1.0f));
	            CGPoint center=oneShape.getShapePosition();
	            oneShape.setShapePosition(CGPoint.ccp(rcLeft.origin.x+center.x*scaleLeft, rcLeft.origin.y + rcLeft.size.height - center.y*scaleLeft));
	            oneShape.initPolygon();
	            
	            oneShape.setTag(kTagPuzzleShapes);
	            super.addChild(oneShape,2);
	            floatingSprites.add(oneShape);
	            puzzleShapes.add(oneShape);
	        }
	        this.centeralizeShapes(shapes, rcLeft);
	    }
	    //initial shapes that user can move
	    float scaleRight=(int)(rcRight.size.width/8);
	    precision=0.1f * scaleRight;
	    
	    int HT=65536/8;
	    float values[]={
	        //type, flipped, x, y, rotation
	        0,0,0.8f,0.8f,0,
	        0,0,3.3f,0.8f,0,
	        1,0,1.5f,1.5f,HT*4,
	        2,0,6,0.6f,0,
	        3,0,6,1.6f,HT*2,
	        4,0,3.6f,1.8f,HT*7,
	        4,0,4.1f,1.3f,HT*1};
	    for (int i = 0; i < 7; ++i) {
	        int idx=i * 5;
	        int shapeType=(int)values[idx++];
	        int flipped=(int)values[idx++];
	        float x=values[idx++];
	        float y=values[idx++];
	        int rotation=(int)values[idx++];
	        TangramShape shape=new TangramShape(shapeType, CGPoint.ccp(x,y),normalizeAngle(rotation), 1.0f, flipped>0);
	        shape.setTag(kTagResolutionShapes);
	        shape.setClr(this.getNormalShapeClr());
	        shape.setShapeScale(scaleRight);
	        CGPoint center=shape.getShapePosition();
	        shape.setShapePosition(CGPoint.ccp(rcRight.origin.x+center.x*scaleRight, rcRight.origin.y + rcRight.size.height - center.y*scaleRight));
	        shape.initPolygon();
	        
	        this.addChild(shape, 0);
	        floatingSprites.add (shape);
	        resolutionShapes.add(shape);
	    }
	    //the outlines
	    java.util.ArrayList<PolygonSprite> outlineShapes=this.createOutlines(puzzleShapes);
	    for (PolygonSprite oneShape : outlineShapes) {
	        for (int i = 0; i < oneShape.getNumOfVertices(); ++i) {
	            //adjust it size to match the right shape size
	            CGPoint pt=oneShape.getVertix(i);
	            pt.x *= scaleRight/scaleLeft;
	            pt.y *= scaleRight/scaleLeft;
	            oneShape.setVertix(i, pt);
	        }
	        oneShape.setVisible(false);
	        oneShape.setTag(kTagOutlinePolygon);
	        super.addChild(oneShape, 2);
	        floatingSprites.add(oneShape);
	        
	    }
	    this.centeralizeShapes(outlineShapes, rcRight);
	    
	    //not selected
	    showShapeIdx=workingOnShapeIdx=-1;
	    promptLabel.setString(String.format("%d/%d", mSublevel+1, mMaxSublevel));
	}
	private void centeralizeShapes(java.util.ArrayList<?>shapes, CGRect rc) {
	    float xMin=0, xMax=0, yMin=0, yMax=0;
	    int vertices=0;
	    for (int j = 0; j < shapes.size(); ++j) {
	    	PolygonSprite oneShape =(PolygonSprite)shapes.get(j);
	        for (int i = 0; i < oneShape.getNumOfVertices(); ++i) {
	            CGPoint pt=oneShape.getVertix(i);
	            if (vertices <= 0) {
	                xMin=xMax=pt.x;
	                yMin=yMax=pt.y;
	            }
	            else {
	                if (pt.x < xMin)
	                    xMin=pt.x;
	                else if (pt.x > xMax)
	                    xMax=pt.x;
	                if (pt.y < yMin)
	                    yMin=pt.y;
	                else if (pt.y > yMax)
	                    yMax=pt.y;
	            }
	            ++vertices;
	        }
	    }
	    //centeralize it
	    float xMargin=(rc.size.width -(xMax - xMin))/2;
	    float yMargin=(rc.size.height-(yMax - yMin))/2;
	    float xMove=xMargin-(xMin-rc.origin.x);
	    float yMove=yMargin-(yMin-rc.origin.y);
	    for (int j = 0; j < shapes.size(); ++j) {
	    	PolygonSprite oneShape =(PolygonSprite)shapes.get(j);
	        oneShape.moveWithOffsetX(xMove, yMove);
	    }
	}
	
	private ccColor4F getNormalShapeClr() {
	    return new ccColor4F(72.0f/255, 61.0f/255, 139.0f/255, 1.0f);
	}
	
	private ccColor4F getSelectedShapeClr() {
	    return new ccColor4F(123.0f/255, 104.0f/255, 238.0f/255, 1.0f);
	}
	
	private java.util.ArrayList<PolygonSprite> createOutlines(java.util.ArrayList<TangramShape> tangramShapes) {
	    TangramOutlineAlgorithm algorithm=new TangramOutlineAlgorithm();
	    java.util.ArrayList<TangramOutline> outlines=algorithm.createOutline(tangramShapes);
	
	    java.util.ArrayList<PolygonSprite> outlineSprites=new java.util.ArrayList<PolygonSprite>();
	    for (TangramOutline one : outlines) {
	        PolygonSprite sprite = new PolygonSprite(one.getNpoints());
	        sprite.setSolid(false);
	        sprite.setLineWidth(2);
	        sprite.setClr(new ccColor4F(1.0f, 0, 0, 1.0f));
	        sprite.setTag(kTagOutlinePolygon);
	        float xpoints[]=one.getXpoints();
	        float ypoints[]=one.getYpoints();
	        for (int i = 0; i < sprite.getNumOfVertices(); ++i) {
	            sprite.setVertix(i, CGPoint.ccp(xpoints[i], ypoints[i]));
	        }
	        outlineSprites.add(sprite);
	    }
	    
	    return outlineSprites;
	}
	
	public void displayOutlines(Object sender) {
	    for (int i = floatingSprites.size() - 1; i >= 0; --i) {
	        CCNode node=floatingSprites.get(i);
	        if (node.getTag() == kTagOutlinePolygon) {
	            node.setVisible(true);
	        }
	    }
	}

	/*
	 file format:
	 
	 gTans v1.0 83
	 1.000000e+00 1.000000e+00 1024
	 p 3 0 1.438904e+00 7.119497e+00 57344
	 p 2 0 1.938904e+00 6.119497e+00 0
	 p 4 0 2.105571e+00 7.286164e+00 32768
	 p 0 0 2.853117e+00 5.148092e+00 57344
	 p 0 0 4.933998e+00 6.952830e+00 16384
	 p 1 0 3.795926e+00 6.090901e+00 49152
	 p 4 0 3.146011e+00 6.855199e+00 24576
	 
	 
	 1.000000e+00 1.000000e+00 1024
	 p 0 0 2.716981e+00 6.749326e+00 32768
	 p 3 1 2.883648e+00 5.415993e+00 24576
	 p 0 0 4.050314e+00 5.082659e+00 0
	 p 4 0 5.050314e+00 5.082659e+00 32768
	 p 2 0 4.883648e+00 5.915993e+00 0
	 p 4 0 4.716981e+00 6.749326e+00 0
	 p 1 0 5.383648e+00 7.082659e+00 57344
	*/
	private java.util.ArrayList<TangramShape> createShapesFromConfigurationFile(int puzzleIdx) {
	    String filename = "image/activities/puzzle/tangram/default.figures";
	    java.util.ArrayList<String> lines=loadExpansionAssetFile(filename);
	    
	    if (mMaxSublevel <= 1) {
	        String firstLine=lines.get(0);
	        String info[]=firstLine.split(" ");
	        String strTotal=info[info.length-1];
	        mMaxSublevel=Integer.parseInt(strTotal);
	    }
	    
	    int totalshapes=0;
	    java.util.ArrayList<TangramShape> shapes=new java.util.ArrayList<TangramShape>();
	    for (int i = 0; i< 8; ++i) { //7 shapes plus one setting line
	        int idx=puzzleIdx*9+i+1;
	        if (idx >= lines.size())
	            break;
	        
	        String line=lines.get(idx).trim();
	        if (line.length() <= 0)
	            continue;
	
	        if (line.startsWith("p ")) {
	            String params[]=line.split(" ");
	            int shapeType=Integer.parseInt(params[1]);
	            int flipped=Integer.parseInt(params[2]);
	            float x=Float.parseFloat(params[3]);
	            float y=Float.parseFloat(params[4]);
	            int rotation=Integer.parseInt(params[5]);
	            
	            TangramShape oneShape=new TangramShape(shapeType, CGPoint.ccp(x,y), normalizeAngle(rotation), 1.0f, flipped>0);
	            shapes.add(oneShape);
	            
	            ++totalshapes;
	        }
	        else {
	        }
	    }
	    if (totalshapes != 7) {
	        //damaged configuration file
	        shapes.clear();
	    }
	    return shapes;
	}
	
	private int normalizeAngle(int  angle) {
	    long value=angle * 360;
	    return  (int)(value / 65536);
	}
	public void preBtnTouched(Object _sender) {
	    mSublevel=(mSublevel - 1 + mMaxSublevel) % mMaxSublevel;
	    this.initGame(false, null);
	}
	public void nxtBtnTouched(Object sender) {
	    mSublevel=(mSublevel + 1) % mMaxSublevel;
	    this.initGame(false, null);
	}

	public void showHint(Object _sender){
	    if (showShapeIdx >= 0) {
	        PrimSprite previous=puzzleShapes.get(showShapeIdx);
	        previous.setClr(new ccColor4F(0, 0, 0, 1.0f));
	    }
	    if (showShapeIdx < 0)
	        showShapeIdx=0;
	    else {
	        showShapeIdx=(showShapeIdx+1)%puzzleShapes.size();
	    }
	    PrimSprite show=puzzleShapes.get(showShapeIdx);
	    show.setClr(new ccColor4F(1.0f*0x80/0xff, 1.0f*0x80/0xff, 1.0f*0x80/0xff, 1.0f));
	}
	public void rotateLeft(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    float angle=(sender.getTag()<=1)?360.0f/32:45;
	    this.rotate(angle);
	}

	public void rotateRight(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    float angle=(sender.getTag()<=1)?360.0f/32:45;
	    this.rotate(0-angle);
	}

	private void rotate(float angle) {
	    if (workingOnShapeIdx < 0)
	        return;
	    TangramShape selected=resolutionShapes.get(workingOnShapeIdx);
	    float rotate=selected.getShapeRotation();
	    rotate += angle;
	    if (rotate < 0)
	        rotate += 360;
	    else if (rotate >= 360)
	        rotate-=360;
	    selected.setShapeRotation(rotate);
	    selected.initPolygon();
	}
	public void flip(Object _sender){
	    if (workingOnShapeIdx < 0)
	        return;
	    TangramShape selected=resolutionShapes.get(workingOnShapeIdx);
	    boolean flipped=selected.isFlipped();
	    selected.setFlipped(!flipped);
	    selected.initPolygon();
	}
	
	private CGPoint clip2workingArea(CGPoint pt) {
	    float cx=pt.x-rcRight.origin.x;
	    if (cx < 0)
	        cx=0;
	    else if (cx > rcRight.size.width)
	        cx=rcRight.size.width;
	    float cy=pt.y-rcRight.origin.y;
	    if (cy < 0)
	        cy=0;
	    else if (cy > rcRight.size.height)
	        cy=rcRight.size.height;
	    
	    return CGPoint.ccp(rcRight.origin.x+cx, rcRight.origin.y+cy);
	}
	private CGSize clipMovement(PrimSprite element, float xOffset, float yOffset) {
	    CGRect rc=element.enclosedArea();
	    
	    if (xOffset < 0) {
	        float distance=rcRight.origin.x-rc.origin.x;
	        if (xOffset < distance)
	            xOffset=distance;
	    }
	    else if (xOffset > 0) {
	        float distance=rcRight.origin.x+rcRight.size.width-(rc.origin.x+rc.size.width);
	        if (xOffset > distance)
	            xOffset=distance;
	    }
	    if (yOffset < 0) {
	        float distance=rcRight.origin.y-rc.origin.y;
	        if (yOffset < distance)
	            yOffset=distance;
	    }
	    else if (yOffset > 0) {
	        float distance=rcRight.origin.y+rcRight.size.height-(rc.origin.y+rc.size.height);
	        if (yOffset > distance)
	            yOffset=distance;
	    }
	    return CGSize.make(xOffset, yOffset);
	}
	
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
	    ptLastTouched=CGPoint.ccp(0, 0);
	    if (rcRight.contains(p1.x, p1.y)) {
            for (int i = 0; i < resolutionShapes.size(); ++i) {
                PrimSprite sprite=resolutionShapes.get(i);
                if (sprite.hit(p1)) {
                    if (workingOnShapeIdx>=0) {
                        PrimSprite previous=resolutionShapes.get(workingOnShapeIdx);
                        previous.setClr(getNormalShapeClr());
                    }
                    workingOnShapeIdx=i;
                    PrimSprite selected=resolutionShapes.get(workingOnShapeIdx);
                    selected.setClr(getSelectedShapeClr());
                    
                    ptLastTouched=p1;
                    break;
                }
            }
	    }
		return true;
	}
	public boolean ccTouchesMoved(MotionEvent event) {
	    if (workingOnShapeIdx < 0 || ptLastTouched.x <= 0)
	        return true;
	    CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));

        float xMovement=p1.x-ptLastTouched.x;
        float yMovement=p1.y-ptLastTouched.y;
        TangramShape selected=resolutionShapes.get(workingOnShapeIdx);
        
        CGSize szMovement=this.clipMovement(selected,xMovement,yMovement);
        CGPoint center=selected.getShapePosition();
        center.x += szMovement.width;
        center.y += szMovement.height;
        selected.setShapePosition(center);
        selected.initPolygon();
        
        ptLastTouched=p1;
	    return true;
	}
	
	public boolean ccTouchesEnded(MotionEvent event) {
	    if (ptLastTouched.x > 0)
	        return true;
	    CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
	
        if (rcRight.contains(p1.x,  p1.y)) {
            this.ok(null);
        }
        return true;
	}
	
	/*
	- (void)handleRotation:(UIRotationGestureRecognizer *)recognizer {
	    float rotation = CC_RADIANS_TO_DEGREES([recognizer rotation]);
	    [self rotate:0-rotation];
	}
	*/
	public void ok(Object _sender) {
		java.util.ArrayList<PolygonSprite> puzzleOutline= new java.util.ArrayList<PolygonSprite>(); 
	    for (int i = floatingSprites.size() - 1; i >= 0; --i) {
	        CCNode node=floatingSprites.get(i);
	        if (node.getTag() == kTagOutlinePolygon) {
	            puzzleOutline.add((PolygonSprite)node);
	        }
	    }
	    java.util.ArrayList<PolygonSprite> resolutionOutline=this.createOutlines(resolutionShapes);
	    this.centeralizeShapes(resolutionOutline, rcRight);
	    
	    boolean failed=false;
	    //Test of polygons have points at the same location
	    for (PolygonSprite sprite : puzzleOutline) {
	        int sides=sprite.getNumOfVertices();
	        for (int i = 0; i < sides; ++i) {
	            CGPoint pt=sprite.getVertix(i);
	            boolean matched=false; float minDistance=1024;
	            for (PolygonSprite target : resolutionOutline) {
	                int sides2=target.getNumOfVertices();
	                for (int j = 0; j < sides2; ++j) {
	                    CGPoint pt2=target.getVertix(j);
	                    float distance=distanceFrom(pt,pt2);
	                    if (distance < minDistance)
	                        minDistance=distance;
	                    if (distance < precision) {
	                        matched=true;
	                        break;
	                    }
	                }
	                if (matched)
	                    break;
	            }
	            //NSLog(@"Min distance=%f", minDistance);
	            if (!matched) {
	                failed=true;
	                break;
	            }
	        }
	        if (failed)
	            break;
	    }
	
	    if (!failed) {
	        SysHelper.setIntProfileValue("Tangram", (mSublevel+1)%mMaxSublevel);
	        flashAnswerWithResult(true, true, null, null, 2);
	    }
	    else if (_sender!=null) {
	        flashAnswerWithResult(false, false, null, null, 2);
	    }
	}
}
