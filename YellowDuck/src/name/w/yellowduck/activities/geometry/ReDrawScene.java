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

import name.w.yellowduck.Category;
import name.w.yellowduck.EllipseSprite;
import name.w.yellowduck.LineSprite;
import name.w.yellowduck.PolygonSprite;
import name.w.yellowduck.PrimSprite;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4F;

import android.graphics.Paint;

public class ReDrawScene extends DrawSceneBase {
    private boolean symmetrical;
    //the control group
    private java.util.ArrayList<CCNode> ctrGroup;
    
    private int xLeftGrid; //the most left x position of the left part grid
    private int yTopGrid; //the top position of the grid (both left & right)
    private int gridCellSize; //grid size, grid has same size in width & height
    private int xTotalGrids; //how many grid cells horizontally. 14
    private int yTotalGrids; //how many grid cells vertically. calculated based on the screen size
    
    private float xRightGrid; //the most left x position of the right part grid
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ReDrawScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() { 
		super.onEnter();
		mMaxLevel=4;
	
		Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
		symmetrical=(activeCategory.getSettings()!=null) && "symmetrical".equalsIgnoreCase(activeCategory.getSettings());
		//change a background music
		super.shufflePlayBackgroundMusic();
		super.setupTitle(activeCategory);
		super.setupNavBar(activeCategory);
		super.setupSideToolbar(activeCategory,kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionOk);
	
	    int tools[]={kTooltipSelect, kTooltipLine, kTooltipRectangle, kTooltipRectangleFilled,
	        kTooltipCircle, kTooltipCircleFilled, kTooltipFill, kTooltipDel, kTooltipClr};
	    super.setupTooltips(tools, tools.length);
	    super.setSimpleEditMode(true);
	    super.setHalfTransparent(true);
	    
	    int margin=4*preferredContentScale(true);
	    float x0=tooltipsCanvasWidth+margin;
	    float y0=bottomOverhead();
	    //half size
	    float cx=(szWin.width-margin*2-tooltipsCanvasWidth)/2;
	    float cy=szWin.height-topOverhead()-bottomOverhead();
	    CGRect rc=CGRect.make(x0, y0, cx, cy);
	
	    //background
	    PolygonSprite bgSprite=new PolygonSprite(4);
	    bgSprite.setClr(new ccColor4F(1.0f, 1.0f, 1.0f, 1.0f));
	    bgSprite.setVertix(0, rc.origin);
	    bgSprite.setVertix(1, CGPoint.ccp(x0+cx*2, y0));
	    bgSprite.setVertix(2, CGPoint.ccp(x0+cx*2, y0+cy));
	    bgSprite.setVertix(3, CGPoint.ccp(x0, y0+cy));
	    super.addChild(bgSprite,1);
	    	    
	    int    height=buttonSize();
	    String icon=renderSVG2Img(activeCategory.getIcon(), height, height);
	    CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(icon);
	    CCSprite iconSprite=CCSprite.sprite(texture);
	    iconSprite.setPosition(szWin.width/2, szWin.height-super.topOverhead()/2);
	    super.addChild(iconSprite,1);
	    
	    xTotalGrids=10;
	    //grid
	    int fontSize=6*preferredContentScale(false);
	    Paint paint=new Paint();
	    paint.setTextSize(fontSize);
	    float labelSizeWidth=paint.measureText("23")+2;
	    float labelSizeHeight=paint.descent()-paint.ascent();
	    xLeftGrid=(int)(x0+labelSizeWidth);
	    yTopGrid=(int)(y0+cy-labelSizeHeight);
	    gridCellSize=(int)((cx-labelSizeWidth - margin) / xTotalGrids);
	    yTotalGrids =(int)((cy-labelSizeHeight) / gridCellSize);
	
	    super.setAlignment(gridCellSize);
	    
	    ccColor4F clr1=new ccColor4F(0, 0, 1.0f, 1.0f), clr2=new ccColor4F(1.0f*0x80/0xff, 1.0f*0x80/0xff, 0, 1.0f);
	    //H grid labels
	    for (int i = 0; i < xTotalGrids; ++i) {
	        CCLabel titleLabel = CCLabel.makeLabel(""+i, super.sysFontName(), fontSize);
	        titleLabel.setColor(ccColor3B.ccBLACK);
	        if (symmetrical) {
	            titleLabel.setPosition(xLeftGrid+gridCellSize*xTotalGrids-i*gridCellSize, yTopGrid+labelSizeHeight/2);
	        }
	        else {
	            titleLabel.setPosition(xLeftGrid+i*gridCellSize, yTopGrid+labelSizeHeight/2);
	        }
	        super.addChild(titleLabel,2);
	    }
	    //H grids
	    for (int i = 0; i <= yTotalGrids; ++i) {
	        LineSprite _sprite=new LineSprite(CGPoint.ccp(xLeftGrid, yTopGrid-i*gridCellSize), CGPoint.ccp(xLeftGrid+gridCellSize*xTotalGrids, yTopGrid-i*gridCellSize));
	        _sprite.setClr(((i&1)==0)?clr1:clr2);
	        super.addChild(_sprite, 2);
	    }
	    
	    //V grid labels
	    for (int i = 0; i < yTotalGrids; ++i) {
	        CCLabel titleLabel = CCLabel.makeLabel(""+i, super.sysFontName(), fontSize);
	        titleLabel.setColor(ccColor3B.ccBLACK);
	        titleLabel.setPosition(xLeftGrid-labelSizeWidth/2, yTopGrid-i*gridCellSize);
	        super.addChild(titleLabel,2);
	    }
	    //V grids
	    for (int i = 0; i <= xTotalGrids; ++i) {
	        LineSprite _sprite=new LineSprite(CGPoint.ccp(xLeftGrid+i*gridCellSize, yTopGrid), CGPoint.ccp(xLeftGrid+gridCellSize*i, yTopGrid-yTotalGrids*gridCellSize));
	        _sprite.setClr(((i&1)==0)?clr1:clr2);
	        super.addChild(_sprite,2);
	    }
	    rc=CGRect.make(xLeftGrid, yTopGrid-yTotalGrids*gridCellSize, xTotalGrids*gridCellSize, yTotalGrids*gridCellSize);
	    super.setupWorkingArea(rc);
	    
	    //the right part, H grid labels
	    xRightGrid=(int)(xLeftGrid + gridCellSize*xTotalGrids + margin);
	    for (int i = 0; i < xTotalGrids; ++i) {
	        CCLabel titleLabel = CCLabel.makeLabel(""+i, super.sysFontName(), fontSize);
	        titleLabel.setColor(ccColor3B.ccBLACK);
	        titleLabel.setPosition(xRightGrid+i*gridCellSize, yTopGrid+labelSizeHeight/2);
	        super.addChild(titleLabel,2);
	    }
	    //H grids
	    for (int i = 0; i <= yTotalGrids; ++i) {
	        LineSprite _sprite=new LineSprite(CGPoint.ccp(xRightGrid, yTopGrid-i*gridCellSize), CGPoint.ccp(xRightGrid+gridCellSize*xTotalGrids, yTopGrid-i*gridCellSize));
	        _sprite.setClr(((i&1)==0)?clr1:clr2);
	        super.addChild(_sprite, 2);
	    }
	    
	    //V grid labels
	    for (int i = 0; i < yTotalGrids; ++i) {
	        CCLabel titleLabel = CCLabel.makeLabel(""+i, super.sysFontName(), fontSize);
	        titleLabel.setColor(ccColor3B.ccBLACK);
	        titleLabel.setPosition(xRightGrid +gridCellSize * xTotalGrids + labelSizeWidth/2, yTopGrid-i*gridCellSize);
	        super.addChild(titleLabel,2);
	    }
	    //V grids
	    for (int i = 0; i <= xTotalGrids; ++i) {
	        LineSprite _sprite=new LineSprite(CGPoint.ccp(xRightGrid+i*gridCellSize, yTopGrid), CGPoint.ccp(xRightGrid+gridCellSize*i, yTopGrid-yTotalGrids*gridCellSize));
	        _sprite.setClr(((i&1)==0)?clr1:clr2);
	        super.addChild(_sprite,2);
	    }
	    ctrGroup=new java.util.ArrayList<CCNode>();
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    
	    for (CCNode node : ctrGroup) {
	        node.removeFromParentAndCleanup(true);
	    }
	    ctrGroup.clear();
	    
	    //the list of items
	    String shapesDefinition=""; //in the format of shape type,shape detail(1:filled),location x1, location y1, location x2, location y2 width,height,color index
	    if (symmetrical) {
	        switch (mLevel) {
	            case 1:
	                shapesDefinition="shape:667,filled:1,x1:0,y1:6,w:2,h:2,clr:4|shape:667,filled:1,x1:2,y1:4,w:2,h:2,clr:7|shape:667,filled:1,x1:2,y1:8,w:2,h:2,clr:4";
	                break;
	            case 2:
	                shapesDefinition="shape:667,filled:1,x1:0,y1:5,w:4,h:2,clr:1|shape:666,x1:4,y1:1,x2:4,y2:7,clr:1|shape:666,x1:4,y1:1,x2:8,y2:3,clr:1|shape:666,x1:8,y1:5,x2:4,y2:7,clr:1|shape:666,x1:8,y1:3,x2:8,y2:5,clr:4";
	                break;
	            case 3:
	                shapesDefinition="shape:667,filled:1,x1:0,y1:7,w:2,h:6,clr:7|shape:668,filled:1,x1:5,y1:4,w:3,h:4,clr:4|shape:668,filled:0,x1:7,y1:4,w:3,h:2,clr:7";
	                break;
	            case 4:
	                shapesDefinition="shape:667,filled:1,x1:2,y1:5,w:5,h:2,clr:4|shape:667,filled:1,x1:6,y1:7,w:1,h:2,clr:4|shape:667,filled:1,x1:2,y1:7,w:1,h:2,clr:1|shape:667,filled:1,x1:1,y1:3,w:1,h:1,clr:1|shape:667,filled:1,x1:7,y1:3,w:3,h:2,clr:7";
	                break;
	        }
	    }
	    else {
	        switch (mLevel) {
	            case 1:
	                shapesDefinition="shape:667,filled:1,x1:0,y1:4,w:10,h:2,clr:1|shape:667,filled:1,x1:0,y1:8,w:10,h:2,clr:1";
	                break;
	            case 2:
	                shapesDefinition="shape:667,filled:0,x1:1,y1:8,w:8,h:7,clr:4|shape:667,filled:1,x1:3,y1:5,w:4,h:3,clr:4|shape:668,filled:1,x1:7,y1:6,w:1,h:1,clr:4";
	                break;
	            case 3:
	                shapesDefinition="shape:668,filled:1,x1:3,y1:3,w:2,h:2,clr:1|shape:666,x1:3,y1:5,x2:3,y2:9,clr:4|shape:668,filled:1,x1:7,y1:3,w:1,h:2,clr:7|shape:666,x1:7,y1:5,x2:7,y2:7,clr:4";
	                break;
	            case 4:
	                shapesDefinition="shape:667,filled:1,x1:2,y1:5,w:5,h:2,clr:4|shape:667,filled:1,x1:6,y1:7,w:1,h:2,clr:4|shape:667,filled:1,x1:2,y1:7,w:1,h:2,clr:1|shape:667,filled:1,x1:1,y1:3,w:1,h:1,clr:1|shape:667,filled:1,x1:7,y1:3,w:3,h:2,clr:7";
	                break;
	        }
	    }
	    String shapes[]=shapesDefinition.split("\\|");
	    for (int i = 0; i < shapes.length; ++i) {
	    	String oneShape=shapes[i];
	    	String shapeParams[]=oneShape.split(",");
	        int type=0,solid=0,x1=0,y1=0,x2=0,y2=0,cx=0,cy=0,clrIdx=0;
	        for (int j = 0; j < shapeParams.length; ++j) {
	        	String nv=shapeParams[j];
	            String nameValue[]=nv.split(":");
	            String name=nameValue[0];
	            String value=nameValue[1];
	            
	            if (name.equalsIgnoreCase("shape")) {
	                type=Integer.parseInt(value);
	            }
	            else if (name.equalsIgnoreCase("filled")) {
	                solid=Integer.parseInt(value);
	            }
	            else if (name.equalsIgnoreCase("x1")) {
	                x1=Integer.parseInt(value);
	            }
	            else if (name.equalsIgnoreCase("y1")) {
	                y1=Integer.parseInt(value);
	            }
	            else if (name.equalsIgnoreCase("x2")) {
	                x2=Integer.parseInt(value);
	            }
	            else if (name.equalsIgnoreCase("y2")) {
	                y2=Integer.parseInt(value);
	            }
	            else if (name.equalsIgnoreCase("w")) {
	                cx=Integer.parseInt(value);
	            }
	            else if (name.equalsIgnoreCase("h")) {
	                cy=Integer.parseInt(value);
	            }
	            else if (name.equalsIgnoreCase("clr")) {
	                clrIdx=Integer.parseInt(value);
	            }
	        }
	        CGPoint p1=this.grid2position(CGPoint.ccp(x1, y1), false);
	        CGPoint p2=this.grid2position(CGPoint.ccp(x2, y2), false);
	        cx *= gridCellSize;
	        if (cx <= 0)
	            cx = xTotalGrids * gridCellSize;
	        cy *= gridCellSize;
	        if (cy <= 0)
	            cy=gridCellSize;
	        switch (type) {
	            //macros defined in PrimSprite.h
	            case PrimSprite.kTypePrimLine:
	            {
	                LineSprite line=new LineSprite(p1,p2);
	                line.setLineWidth(kLineWidth);
	                line.setClr(getColor(clrIdx));
	                super.addChild(line,2);
	                ctrGroup.add(line);
	            }
	                break;
	            case PrimSprite.kTypePrimPloygon:
	            {
	                PolygonSprite polygon=new PolygonSprite(4);
	                polygon.setVertix(0,p1);
	                polygon.setVertix(1,CGPoint.ccp(p1.x+cx, p1.y));
	                polygon.setVertix(2,CGPoint.ccp(p1.x+cx, p1.y+cy));
	                polygon.setVertix(3,CGPoint.ccp(p1.x, p1.y+cy));
	                polygon.setSolid(solid>0);
	                polygon.setLineWidth(kLineWidth);
	                polygon.setClr(getColor(clrIdx));
	                super.addChild(polygon,2);
	                ctrGroup.add(polygon);
	            }
	                break;
	            case PrimSprite.kTypePrimEllipse:
	            {
	                EllipseSprite ellipse=new EllipseSprite(p1, cx, cy);
	                ellipse.setSolid(solid>0);
	                ellipse.setClr(getColor(clrIdx));
	                ellipse.setLineWidth(kLineWidth);
	                super.addChild(ellipse,2);
	                ctrGroup.add(ellipse);
	            }
	                break;
	        }
	    }
	}
	
	//grid coordination to screen position
	private CGPoint grid2position(CGPoint ptGrid, boolean left) {
	    float x=0, y=0;
	    if (left) {
	        if (symmetrical)
	            x=xLeftGrid + (xTotalGrids-ptGrid.x) * gridCellSize;
	        else
	            x=xLeftGrid + ptGrid.x * gridCellSize;
	    }
	    else {
	        x=xRightGrid + ptGrid.x * gridCellSize;
	    }
	    y=yTopGrid - ptGrid.y * gridCellSize;
	    return CGPoint.ccp(x, y);
	}
	private int size2grid(float sz){
	    return (int)sz/gridCellSize;
	}
	
	//Screen position to grid coordination
	private CGPoint position2grid(CGPoint ptPosition, boolean left) {
	    int x=0, y=0;
	    if (left) {
	        x=(int)(ptPosition.x-xLeftGrid)/gridCellSize;
	        if (symmetrical)
	            x=xTotalGrids-x;
	    }
	    else {
	        x=(int)(ptPosition.x-xRightGrid)/gridCellSize;
	    }
	    y=(int)(yTopGrid - ptPosition.y) / gridCellSize;
	    return CGPoint.ccp(x, y);
	}

	public void ok(Object _sender) {
	    super.normalize();
	    
	    for (CCNode node : ctrGroup) {
	    	PrimSprite prim=(PrimSprite)node;
	    	prim.setSelected(false);
	    }
	    for (CCNode node : floatingSprites) {
	    	PrimSprite prim=(PrimSprite)node;
	    	prim.setSelected(false);
	    }
	    boolean allMatched=(floatingSprites.size()==ctrGroup.size()), find=false;
	    for (CCNode node1 : floatingSprites) {
	    	PrimSprite userObject=(PrimSprite)node1;
	        find=false;
	        for (CCNode node2 : ctrGroup) {
	        	PrimSprite node=(PrimSprite)node2;
	//            NSLog("%d-%d", [node type] , [userObject type]);
	            if (!node.isSelected() && node.getType() == userObject.getType()) {
	                boolean result=false;
	                if (node.isLine())
	                    result=this.sameLine((LineSprite)userObject,(LineSprite)node);
	                else if (node.isPolygon())
	                    result=this.samePloygon((PolygonSprite)userObject, (PolygonSprite)node);
	                else if (node.isEllipse())
	                    result=this.sameEllipse((EllipseSprite)userObject, (EllipseSprite)node);
	                if (result) {
	                    node.setSelected(true);
	                    userObject.setSelected(true);
	                    find=true;
	                    break;
	                }
	            }
	        }
	        if (!find) {
	            //flag error
	            allMatched=false;
	        }
	    }
	    if (!allMatched) {
	        for (CCNode node1 : ctrGroup) {
	        	PrimSprite node=(PrimSprite)node1;
	            if (!node.isSelected()) {
	                CGRect rc=node.enclosedArea();
	                CGPoint center=CGPoint.ccp(rc.origin.x+rc.size.width/2, rc.origin.y+rc.size.height/2);
	                super.flashWrongAnswer(center,2);
	            }
	        }
	        for (CCNode node2 : floatingSprites) {
	        	PrimSprite node=(PrimSprite)node2;
	            if (!node.isSelected()) {
	                CGRect rc=node.enclosedArea();
	                CGPoint center=CGPoint.ccp(rc.origin.x+rc.size.width/2, rc.origin.y+rc.size.height/2);
	                super.flashWrongAnswer(center,2);
	            }
	        }
	    }
	    super.flashAnswerWithResult(allMatched,allMatched,null,null,1);
	}
	
	private boolean sameLine(LineSprite leftObj, LineSprite rightObj) {
	    if (!sameColor(leftObj.getClr(), rightObj.getClr()))
	        return false;
	          
	    CGPoint p11=leftObj.getP1();
	    CGPoint p12=leftObj.getP2();
	    p11=this.position2grid(p11, true);
	    p12=this.position2grid(p12,true);
	
	    CGPoint p21=rightObj.getP1();
	    CGPoint p22=rightObj.getP2();
	    p21=this.position2grid(p21, false);
	    p22=this.position2grid(p22, false);
	
	    return (this.sameGridPosition(p11,p21) && this.sameGridPosition(p12,p22)) ||
	                (this.sameGridPosition(p11,p22) && this.sameGridPosition(p12, p21));
	}
	
	private boolean samePloygon(PolygonSprite leftObj, PolygonSprite rightObj) {
	    if (!sameColor(leftObj.getClr(), rightObj.getClr()))
	        return false;
	    CGRect rc1=leftObj.enclosedArea();
	    float w1=Math.abs(rc1.size.width);
	    float h1=Math.abs(rc1.size.height);
	    CGPoint pt1=this.position2grid(rc1.origin, true);
	    if (symmetrical) {
	        pt1=this.position2grid(CGPoint.ccp(rc1.origin.x+rc1.size.width, rc1.origin.y), true);
	    }
	    w1=this.size2grid(w1);
	    h1=this.size2grid(h1);
	
	    CGRect rc2=rightObj.enclosedArea();
	    float w2=Math.abs(rc2.size.width);
	    float h2=Math.abs(rc2.size.height);
	    CGPoint pt2=this.position2grid(rc2.origin, false);
	    w2=this.size2grid(w2);
	    h2=this.size2grid(h2);
	
	    return w1==w2 && h1==h2 && this.sameGridPosition(pt1, pt2);
	}
	
	private boolean sameEllipse(EllipseSprite leftObj, EllipseSprite rightObj) {
	    if (!sameColor(leftObj.getClr(), rightObj.getClr()))
	        return false;
	    CGPoint center1=leftObj.getCenter();
	    float rx1=leftObj.getRx();
	    float ry1=leftObj.getRy();
	    center1=this.position2grid(center1, true);
	    rx1=this.size2grid(rx1);
	    ry1=this.size2grid(ry1);
	
	    CGPoint center2=rightObj.getCenter();
	    float rx2=rightObj.getRx();
	    float ry2=rightObj.getRy();
	    center2=this.position2grid(center2,false);
	    rx2=this.size2grid(rx2);
	    ry2=this.size2grid(ry2);
	    
	    return rx1 == rx2 && ry1==ry2 && sameGridPosition(center1,center2);
	}
	
	private boolean sameGridPosition(CGPoint p1, CGPoint p2) {
	    return p1.x == p2.x && p1.y == p2.y;
	}
	
	private boolean sameColor(ccColor4F clr1, ccColor4F clr2) {
	    float diff1=Math.abs(clr1.r-clr2.r);
	    float diff2=Math.abs(clr1.g-clr2.g);
	    float diff3=Math.abs(clr1.b-clr2.b);
	    
	    return diff1 < 0.1f && diff2 < 0.2f && diff3 < 0.2f;
	}
}
