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
import name.w.yellowduck.EllipseSprite;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4F;

import android.graphics.Bitmap;
import android.view.MotionEvent;

public class PhotoHunterScene extends name.w.yellowduck.YDActLayerBase {

	private final int kSensitiveGridSize          =10;

    private CGRect rcLeftImage;
    float imgWidth, imgHeight;
    private int pixelData1[], pixelData2[];
    
    private CCLabel sublevelLabel;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new PhotoHunterScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=3;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
	
	    //sublevel
	    sublevelLabel = CCLabel.makeLabel("10/10", super.sysFontName(), super.mediumFontSize());
	    sublevelLabel.setColor(ccColor3B.ccBLACK);
	    sublevelLabel.setPosition(szWin.width-sublevelLabel.getContentSize().width/2-4, bottomOverhead()+sublevelLabel.getContentSize().height/2);
	    super.addChild(sublevelLabel,10);
	    
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
	    
	    if (mLevel == 1)
	        mMaxSublevel=7;
	    else if (mLevel == 2)
	        mMaxSublevel=1;
	    else if (mLevel==3)
	        mMaxSublevel=5;
	    
	    if (mLevel <= 1 && mSublevel <= 0) {
	        CCLabel prompt = CCLabel.makeLabel(localizedString("prompt_photo_hunter"), super.sysFontName(), super.smallFontSize());
	        prompt.setColor(ccColor3B.ccBLACK);
	        prompt.setPosition(szWin.width/2, szWin.height-topOverhead()/2);
	        super.addChild(prompt, 1);
	        floatingSprites.add(prompt);
	    }
	    {
	        //left image
	        String url=String.format("image/activities/puzzle/photohunter/board%d_%da.png", mLevel, mSublevel);
	        CCSprite sprite=spriteFromExpansionFile(url);
	        sprite.setScale(szWin.width/2 * 0.8f / sprite.getContentSize().width);
	        sprite.setPosition(szWin.width/4, szWin.height/2);
	        super.addChild(sprite, 1);
	        floatingSprites.add(sprite);
	        
	        rcLeftImage=CGRect.make(sprite.getPosition().x-sprite.getContentSize().width*sprite.getScale()/2,
	                               sprite.getPosition().y-sprite.getContentSize().height*sprite.getScale()/2,
	                               sprite.getContentSize().width*sprite.getScale(), sprite.getContentSize().height*sprite.getScale());
	    }
	    {
	        //right image
	        String url=String.format("image/activities/puzzle/photohunter/board%d_%db.png", mLevel, mSublevel);
	        CCSprite sprite=spriteFromExpansionFile(url);
	        sprite.setScale(szWin.width/2 * 0.8f / sprite.getContentSize().width);
	        sprite.setPosition(szWin.width*0.75f, szWin.height/2);
	        super.addChild(sprite, 1);
	        floatingSprites.add(sprite);
	    }
	    this.loadPixelData();
	    
	    sublevelLabel.setString(String.format("%d/%d", mSublevel+1, mMaxSublevel));
	}
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
        if (p1.x >= szWin.width/2)
            p1.x -= szWin.width/2;
        if (rcLeftImage.contains(p1.x, p1.y)) {
            this.findDifferenceAt(p1);
        }
	    return true;
	}
	private void findDifferenceAt(CGPoint pt) {
	    if (pixelData1 == null || pixelData2 == null) //out of memory
	        return;
	    
	    int xClicked=(int)((pt.x-rcLeftImage.origin.x)/rcLeftImage.size.width * imgWidth);
	    int yClicked=(int)((pt.y-rcLeftImage.origin.y)/rcLeftImage.size.height * imgHeight);
	    yClicked=(int)(imgHeight-yClicked);
	    
	    int areaSize=kSensitiveGridSize;
	    int x0=xClicked-areaSize;
	    if (x0 < 0)
	        x0=0;
	    int y0=yClicked-areaSize;
	    if (y0 < 0)
	        y0 = 0;
	    int x1=xClicked+areaSize;
	    if (x1 > imgWidth)
	        x1=(int)(imgWidth);
	    int y1=yClicked+areaSize;
	    if (y1 > imgHeight)
	        y1=(int)(imgHeight);
	    int differnce=0;
	    for (int x=x0; x<x1; ++x) {
	        for (int y=y0; y<y1; ++y) {
	            int offset=(int)(y*imgWidth+x);
	            if (pixelData1[offset] != pixelData2[offset]) {
	                ++differnce;
	            }
	        }
	    }
	    if (differnce > 0 && differnce > (x1-x0)*(y1-y0)/8) {
	        //draw two circles
	        EllipseSprite sprite=new EllipseSprite(pt, kSensitiveGridSize, kSensitiveGridSize);
	        sprite.setSolid(false);
	        sprite.setLineWidth(2);
	        sprite.setClr(new ccColor4F(1.0f, 0, 0, 0.7f));
	        super.addChild(sprite,2);
	        floatingSprites.add(sprite);
	        
	        sprite=new EllipseSprite(CGPoint.ccp(pt.x+szWin.width/2, pt.y), kSensitiveGridSize, kSensitiveGridSize);
	        sprite.setSolid(false);
	        sprite.setLineWidth(2);
	        sprite.setClr(new ccColor4F(1.0f, 0, 0, 0.7f));
	        super.addChild(sprite,2);
	        floatingSprites.add(sprite);	        
	        flashAnswerWithResult(true, true, null, null, 2);
	    }
	}
	
	private void loadPixelData() {
		String left=String.format("image/activities/puzzle/photohunter/board%d_%da.png", mLevel, mSublevel);
		String right=String.format("image/activities/puzzle/photohunter/board%d_%db.png", mLevel, mSublevel);
		Bitmap imgLeft=super.decodeExpansionBitmap(left);
		Bitmap imgRight=super.decodeExpansionBitmap(right);
	    
	    imgWidth=imgLeft.getWidth();
	    imgHeight=imgLeft.getHeight();
	    
	    pixelData1=this.getRawDataFromImage(imgLeft);
	    pixelData2=this.getRawDataFromImage(imgRight);
	}
	
	private int[] getRawDataFromImage(Bitmap myBitmap) {
		int[] pixels = new int[myBitmap.getHeight()*myBitmap.getWidth()];
		myBitmap.getPixels(pixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
		return pixels;
	}
}
