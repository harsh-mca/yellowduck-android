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
import org.cocos2d.types.ccColor4B;
import org.cocos2d.types.ccColor4F;
import org.cocos2d.types.ccColor3B;

import android.graphics.Bitmap;
import android.view.MotionEvent;
	
public class LightsoffScene extends name.w.yellowduck.YDActLayerBase {

	private final int zClickableItems         =10;

	private final int kTagLight               =10;
	private final int kTagHints               =2;
	private final int kTagTux                 =3;
	private final int kTagTuxSleeping         =4;
	private final int kTagMsg                 =5;

	private final int kCanvasWidth            =1024;
	private final int kCanvasHeight           =666;
	private final int LightsPerRowCol         =5;
	
    private CCSprite sunSprite;
    private PolygonSprite skySprite;
    
    private boolean hintMode;
    
    private int lights[]=new int[LightsPerRowCol*LightsPerRowCol];
    
    private CGRect rcGrid;
    private float gridCellSize;
    private CGPoint ptSunset;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new LightsoffScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=57;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    CCSprite bg=super.setupBackground(activeCategory.getBg(), kBgModeFit);
	    bg.removeFromParentAndCleanup(true);
	    super.addChild(bg, 2);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);

	    
	    float xLeftTux=0;
	    //the tux, width 80
	    {
	        String name="image/activities/puzzle/lightsoff/tux.svg";
	        float width=76.0f/kCanvasWidth*szWin.width;
	        String img=super.renderSVG2Img(name, (int)width, 0);
	        String imgSel=super.buttonize(img);
	        CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(img);
	        CCTexture2D texture2=CCTextureCache.sharedTextureCache().addImageExternal(imgSel);
	        
	        CCSprite sprite=CCSprite.sprite(texture1);
	        CCSprite spriteSelected=CCSprite.sprite(texture2);
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite,  spriteSelected, this,"tuxTouched");
	        menuitem.setPosition(szWin.width-menuitem.getContentSize().width/2-2 , 80.0f/kCanvasHeight*szWin.height+menuitem.getContentSize().height/2);
	        CCMenu menu = CCMenu.menu(menuitem);
	        menu.setPosition(0,0);
	        menu.setTag(kTagTux);
	        super.addChild(menu, zClickableItems);
	        floatingSprites.add(menu);
	        
	        xLeftTux=menuitem.getPosition().x - menuitem.getContentSize().width/2;
	    }
	    
	    float yTopPrompt=148.0f/kCanvasHeight*szWin.height;
	    float xLeftPromp=314.0f/kCanvasWidth*szWin.width;
	    {
	        //background of message
	        String name="image/activities/puzzle/lightsoff/prompt.svg";
	        String img=super.renderSVG2Img(name, (int)(xLeftTux-xLeftPromp), 0);
	        CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(img);
	        CCSprite bgSprite=CCSprite.sprite(texture1);
	        bgSprite.setPosition(xLeftPromp+bgSprite.getContentSize().width/2, yTopPrompt-bgSprite.getContentSize().height/2);
	        bgSprite.setTag(kTagMsg);
	        super.addChild(bgSprite, 2);
	        floatingSprites.add(bgSprite);
	    }
	    //the prompt, height 84
	    xLeftPromp += 4 * preferredContentScale(true);
	    yTopPrompt -= 4 * preferredContentScale(true);
	    {
	        CCLabel prompt=CCLabel.makeLabel(localizedString("prompt_lightsoff_1"), super.sysFontName(), super.smallFontSize());
	        prompt.setPosition(xLeftPromp+prompt.getContentSize().width/2+6, yTopPrompt-prompt.getContentSize().height/2-6);
	        prompt.setColor(ccColor3B.ccBLACK);
	        prompt.setTag(kTagMsg);
	        super.addChild(prompt, 3);
	        floatingSprites.add(prompt);
	    }
	    {
	        CCLabel prompt=CCLabel.makeLabel(localizedString("prompt_lightsoff_2"), super.sysFontName(), super.smallFontSize());
	        prompt.setPosition(xLeftPromp+prompt.getContentSize().width/2+6, yTopPrompt-prompt.getContentSize().height*1.5f-6);
	        prompt.setColor(ccColor3B.ccBLACK);
	        prompt.setTag(kTagMsg);
	        super.addChild(prompt, 3);
	        floatingSprites.add(prompt);
	    }
	    float gridSize=(szWin.height-topOverhead()-yTopPrompt) * 0.8f;
	    float xMargin=(szWin.width - gridSize)/2;
	    float yMargin=(szWin.height-topOverhead()-yTopPrompt-gridSize)/2;
	    rcGrid=CGRect.make(xMargin, yTopPrompt+yMargin, gridSize, gridSize);
	    gridCellSize=gridSize/LightsPerRowCol;
	    {
	        //tux sleeping
	        String name="image/activities/puzzle/lightsoff/tuxsleep.svg";
	        float width=100.0f/kCanvasWidth*szWin.width;
	        String img=renderSVG2Img(name, (int)width, 0);
	        CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(img);
	        
	        CCSprite sprite=CCSprite.sprite(texture1);
	        sprite.setPosition((szWin.width+rcGrid.origin.x+rcGrid.size.width)/2, yTopPrompt+sprite.getContentSize().height/2);
	        sprite.setTag(kTagTuxSleeping);
	        sprite.setVisible(false);
	        super.addChild(sprite, zClickableItems-1);
	        floatingSprites.add(sprite);
	    }
	
	    
	    Bitmap imgGridBg=super.roundCornerRect((int)(rcGrid.size.width+2), (int)(rcGrid.size.height+2), 6*preferredContentScale(true), new ccColor4B(0xc0, 0xc0, 0xc0, 0xf0));
	    CCSprite sprite=CCSprite.sprite(imgGridBg, "gridbg");
	    sprite.setPosition(rcGrid.origin.x+rcGrid.size.width/2,rcGrid.origin.y+rcGrid.size.height/2);
	    super.addChild(sprite, zClickableItems-1);
	    //the sun
	    ptSunset=CGPoint.ccp(86.0f/kCanvasWidth*szWin.width,468.0f/kCanvasHeight*szWin.height);
	    {
	        String name="image/activities/puzzle/lightsoff/sun.svg";
	        float sunSize=110.0f/kCanvasWidth*szWin.width;
	        String img=super.renderSVG2Img(name, (int)sunSize, (int)sunSize);
	        CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(img);
	        
	        sunSprite=CCSprite.sprite(texture1);
	        sunSprite.setPosition(ptSunset);
	        super.addChild(sunSprite,1);//below the bg
	
	        ptSunset.y -= sunSprite.getContentSize().height/2;
	    }
	    //the sky
	    {
	        skySprite=new PolygonSprite(4);
	        skySprite.setSolid(true);
	        skySprite.setVertix(0, CGPoint.ccp(0, ptSunset.y-4));
	        skySprite.setVertix(1, CGPoint.ccp(szWin.width, ptSunset.y-4));
	        skySprite.setVertix(2, CGPoint.ccp(szWin.width, szWin.height));
	        skySprite.setVertix(3, CGPoint.ccp(0, szWin.height));
	        super.addChild(skySprite, 0);
	    }
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    	    
	    int total=floatingSprites.size();
	    for (int i =total-1; i >=0; --i) {
	        CCNode node=floatingSprites.get(i);
	        if (node.getTag() == kTagMsg || node.getTag()==kTagTux) {
	            node.setVisible(true);
	        }
	        else if (node.getTag() == kTagTuxSleeping) {
	            node.setVisible(false);
	        }
	        else {
	            node.removeFromParentAndCleanup(true);
	            floatingSprites.remove(i);
	        }
	    }
	    
	    this.loadData4CurLevel();
	    for (int i = 0; i < LightsPerRowCol*LightsPerRowCol; ++i) {
	        this.updateLightStatus(i, false);
	    }
	    //update sky color and sun position
	    this.solve_it();
	}
	
	private void updateLightStatus(int pos, boolean removePrevious) {
	    if (removePrevious) {
	        for (int i = 0; i < floatingSprites.size(); ++i) {
	            CCNode node=floatingSprites.get(i);
	            if (node.getTag() - kTagLight == pos) {
	                node.removeFromParentAndCleanup(true);
	                floatingSprites.remove(i);
	                break;
	            }
	        }
	    }
	    int x=pos%LightsPerRowCol;
	    int y=pos/LightsPerRowCol;
	    int lightSize=(int)(gridCellSize*0.8f);
	    
	    String name=(lights[pos]!=0)?"image/activities/puzzle/lightsoff/on.svg":"image/activities/puzzle/lightsoff/off.svg";
	    String img=renderSVG2Img(name, lightSize, lightSize);
	    CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(img);
	    CCSprite sprite=CCSprite.sprite(texture);
	    sprite.setPosition(rcGrid.origin.x+x*gridCellSize+gridCellSize/2, rcGrid.origin.y+y*gridCellSize+gridCellSize/2);
	    sprite.setTag(kTagLight+pos);
	    super.addChild(sprite, zClickableItems);
	    floatingSprites.add(sprite);
	}
	
	private void loadData4CurLevel() {
	    //loading configuration
	    String dataFilePath = "image/activities/puzzle/lightsoff/data.txt";
	    //split into lines
	    java.util.ArrayList<String> lines=loadExpansionAssetFile(dataFilePath);
	    //filter out comment lines
	    java.util.ArrayList<String> settingLines=new java.util.ArrayList<String>();
	    for (String line : lines) {
	        line=line.trim();
	        if (!line.startsWith("#"))
	            settingLines.add(line);
	    }
	    lines=null;
	    //merge into one line
	    String currnetLevelSettings="";
	    for (int i = (mLevel-1) * LightsPerRowCol; i < mLevel * LightsPerRowCol; ++i) {
	        currnetLevelSettings=currnetLevelSettings + settingLines.get(i);
	    }
	    settingLines.clear();
	    
	    currnetLevelSettings=currnetLevelSettings.replaceAll("\\[", "");
	    currnetLevelSettings=currnetLevelSettings.replaceAll("\\]", "");
	    String numbers[]=currnetLevelSettings.split(",");
	    
	    for (int i = 0; i < LightsPerRowCol*LightsPerRowCol; ++i)
	        lights[i]=Integer.parseInt(numbers[i]);
	    
	    currnetLevelSettings=null;
	    numbers=null;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// We check only the last line
	private boolean solution_found(int[] solution) {
	    for (int x=0; x <  LightsPerRowCol; ++x) {
	        if (solution[(LightsPerRowCol-1)*LightsPerRowCol+x] > 0)
	            return false;
	    }
	    return true;
	}
	
	private void chase_light(int items[], int clicks[]) {
	    for (int y=1; y <  LightsPerRowCol; ++y)
	        for (int x=0; x < LightsPerRowCol; ++x) {
	            if (items[(y-1)*LightsPerRowCol+x] > 0) {
	                this.solution_switch(items, clicks,y, x);
	            }
	        }
	}
	private int[]solve_one(int solution[], int clicks[]) {
	    boolean found = false;
	    for (int index=0; index<5; ++index) {
	        this.chase_light(solution, clicks);
	        if (this.solution_found(solution))
	            found=true;
	        if (!this.solution_wrap(solution, clicks))
	            break;
	    }
	    if (found)
	        return clicks;
	    return null;
	}
	private boolean is_solution_pattern(int s[], int a, int b, int c, int d, int e){
	    return (s[4*LightsPerRowCol+0] == a && s[4*LightsPerRowCol+1] == b &&
	        s[4*LightsPerRowCol+2] == c && s[4*LightsPerRowCol+3] == d && s[4*LightsPerRowCol+4] == e);
	}
	
	// Return False if the is no solution
	private boolean solution_wrap(int solution[], int clicks[]) {
	    if (this.is_solution_pattern(solution,1,0,0,0,1)) {
	        this.solution_switch(solution,clicks,0,0);
	        this.solution_switch(solution,clicks,0,1);
	    }
	    else if (this.is_solution_pattern(solution,0,1,0,1,0)) {
	        this.solution_switch(solution, clicks, 0, 0);
	        this.solution_switch(solution, clicks, 0, 3);
	    }
	    else if (this.is_solution_pattern(solution,1,1,1,0,0)) {
	        this.solution_switch(solution,clicks,0,1);
	    }
	    else if (this.is_solution_pattern(solution,0,0,1,1,1)) {
	        this.solution_switch(solution,clicks, 0, 3);
	    }
	    else if (this.is_solution_pattern(solution,1,0,1,1,0)) {
	        this.solution_switch(solution,clicks, 0, 4);
	    }
	    else if (this.is_solution_pattern(solution,0,1,1,0,1)) {
	        this.solution_switch(solution,clicks,0,0);
	    }
	    else if (this.is_solution_pattern(solution,1,1,0,1,1)) {
	        this.solution_switch(solution, clicks, 0, 2);
	    }
	    else {
	        return false;
	    }
	    return true;
	}
	
	private void solution_switch(int items[], int clicks[], int y, int x) {
	    items[y*LightsPerRowCol+x] = 1-items[y*LightsPerRowCol+x];
	    clicks[y*LightsPerRowCol+x]= 1-clicks[y*LightsPerRowCol+x];
	    if (y >= 1)
	        items[(y-1)*LightsPerRowCol+x] = 1-items[(y-1)*LightsPerRowCol+x];
	    if (y <= 3)
	        items[(y+1)*LightsPerRowCol+x] = 1-items[(y+1)*LightsPerRowCol+x];
	    if (x >= 1)
	        items[y*LightsPerRowCol+x-1] = 1- items[y*LightsPerRowCol+x-1];
	    if (x <= 3)
	        items[y*LightsPerRowCol+x+1] = 1- items[y*LightsPerRowCol+x+1];
	}
	private int solution_length(int clicks[]) {
	    int click = 0;
	    for (int i = 0; i < LightsPerRowCol*LightsPerRowCol; ++i)
	        if (clicks[i]>0)
	            ++click;
	    return click;
	}

	/*
	# Solving algorithm is the one described here:
	# http://www.haar.clara.co.uk/Lights/solving.html To begin, you turn
	# out all the lights on the top row, by pressing the buttons on the
	# second row that are directly underneath any lit buttons on the top
	# row. The top row will then have all it's lights off.  Repeat this
	# step for the second, third and fourth row. (i.e. chase the lights
	# all the way down to the bottom row). This may have solved the
	# puzzle already ( click here for an example of this ), but is more
	# likely that there will now be some lights left on in the bottom
	# row. If so, there are only 7 posible configurations. Depending on
	# which configuration you are left with, you need to press some
	# buttons in the top row. You can determine which buttons you need
	# to press from the following table.
	# Light on bottom row     Press on this on top row
	# 10001                   11000
	# 01010                   10010
	# 11100                   01000
	# 00111                   00010
	# 10110                   00001
	# 01101                   10000
	# 11011                   00100
	*/
	private void solve_it() {
	    int clicks[]=new int[LightsPerRowCol*LightsPerRowCol];
	    boolean initialized=false;
	    /*
	    # Our solving algorithm does not find the shortest solution. We
	    # don't really care but we'd like to keep the proposed solution
	    # stable (not propose a complete new solution when one light
	    # changes).  To achieve this (closely), we test here all the
	    # combination of the first line, trying to find the shortest
	    # solution.
	    */
	    for (int x=0; x < 64; ++x) {
	        int solution[]=new int[LightsPerRowCol*LightsPerRowCol];
	        for (int t=0; t<LightsPerRowCol*LightsPerRowCol; ++t)
	            solution[t]=lights[t];
	        int clicks2[]=new int[LightsPerRowCol*LightsPerRowCol];
	        for (int t=0; t<LightsPerRowCol*LightsPerRowCol; ++t)
	            clicks2[t]=0;
	        
	        if ((x & 1) != 0)
	            this.solution_switch(solution,clicks2,0,0);
	        if ((x & 2)!=0)
	            this.solution_switch(solution,clicks2,0,1);
	        if ((x & 4)!=0)
	            this.solution_switch(solution,clicks2,0,2);
	        if ((x & 8)!=0)
	            this.solution_switch(solution,clicks2,0,3);
	        if ((x & 16)!=0)
	            this.solution_switch(solution,clicks2,0,4);
	        int clicks3[] = this.solve_one(solution, clicks2);
	        if (!initialized && clicks3!=null) {
	            for (int t=0; t<LightsPerRowCol*LightsPerRowCol; ++t)
	                clicks[t]=clicks3[t];
	            initialized=true;
	        }
	        else if (clicks3!=null && solution_length(clicks3) < solution_length(clicks)){
	            for (int t=0; t<LightsPerRowCol*LightsPerRowCol; ++t)
	                clicks[t]=clicks3[t];
	        }
	    }
	    if (hintMode) {
	        this.removeHints();
	        this.showHints(clicks);
	    }
	    this.update_background(clicks);
	}
	private boolean is_done() {
	    for (int i = 0; i < LightsPerRowCol*LightsPerRowCol; ++i)
	        if (lights[i] > 0)
	            return false;
	    return true;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
        if (rcGrid.contains(p1.x, p1.y)) {
            int x=(int)((p1.x-rcGrid.origin.x)/gridCellSize);
            int y=(int)((p1.y-rcGrid.origin.y)/gridCellSize);
            
            this.lightTouched(x, y);
        }
        return true;
	}
	private void update_background(int clicks[]) {
	    int length = this.solution_length(clicks);
	    int c = length * 0xFF / 18;
	    skySprite.setClr(new ccColor4F(1.0f*0x33/0xff, 1.0f*0x11/0xff, 1.0f*c/0xff, 1.0f));
	
	    float rise=(szWin.height-ptSunset.y) * (1.0f *length/(LightsPerRowCol*2));
	    if (ptSunset.y+rise >= szWin.height)
	        rise=szWin.height-ptSunset.y;
	    sunSprite.setPosition(ptSunset.x, ptSunset.y+rise);
	}
	
	public void tuxTouched(Object _sender) {
	    hintMode=!hintMode;
	    if (hintMode)
	        this.solve_it();
	    else {
	        this.removeHints();
	    }
	}
	private void lightTouched(int x, int y) {
	    int pos=y*LightsPerRowCol+x;
	    lights[pos]=1-lights[pos];
	    this.updateLightStatus(pos, true);
	    if (x > 0) {
	        lights[pos-1]=1-lights[pos-1];
	        this.updateLightStatus(pos-1, true);
	    }
	    if (x < LightsPerRowCol - 1) {
	        lights[pos+1]=1-lights[pos+1];
	        this.updateLightStatus(pos+1, true);
	    }
	    if (y > 0) {
	        lights[pos-LightsPerRowCol]=1-lights[pos-LightsPerRowCol];
	        this.updateLightStatus(pos-LightsPerRowCol, true);
	    }
	    if (y < LightsPerRowCol - 1) {
	        lights[pos+LightsPerRowCol]=1-lights[pos+LightsPerRowCol];
	        this.updateLightStatus(pos+LightsPerRowCol, true);
	    }
	    if (this.is_done()) {
	        for (CCNode node : floatingSprites) {
	            if (node.getTag() == kTagMsg || node.getTag()==kTagTux) {
	                node.setVisible(false);
	            }
	            else if (node.getTag() == kTagTuxSleeping) {
	                node.setVisible(true);
	            }
	        }
	        flashAnswerWithResult(true, true, null, null, 2);
	    }
	    else {
	        this.solve_it();
	    }
	}
	private void removeHints() {
	    int total=floatingSprites.size();
	    for (int i = total - 1; i >= 0; --i) {
	        CCNode node=floatingSprites.get(i);
	        if (node.getTag() == kTagHints) {
	            node.removeFromParentAndCleanup(true);
	            floatingSprites.remove(i);
	        }
	    }
	}
	private void showHints(int clicks[]) {
	    float width=gridCellSize*0.86f;
	    float margin=(gridCellSize-width)/2;
	    for (int i = 0; i < LightsPerRowCol*LightsPerRowCol; ++i) {
	        if (clicks[i] > 0) {
	            int x=i%LightsPerRowCol;
	            int y=i/LightsPerRowCol;
	            float xPos=rcGrid.origin.x+x*gridCellSize;
	            float yPos=rcGrid.origin.y+y*gridCellSize;
	            
	            PolygonSprite sprite=new PolygonSprite(4);
	            sprite.setSolid(false);
	            sprite.setLineWidth(2);
	            sprite.setClr(new ccColor4F(0, 0, 1.0f, 0.9f));
	            sprite.setTag(kTagHints);
	            
	            sprite.setVertix(0, CGPoint.ccp(xPos+margin, yPos+margin));
	            sprite.setVertix(1, CGPoint.ccp(xPos+margin+width, yPos+margin));
	            sprite.setVertix(2, CGPoint.ccp(xPos+margin+width, yPos+margin+width));
	            sprite.setVertix(3, CGPoint.ccp(xPos+margin, yPos+margin+width));
	            super.addChild(sprite, zClickableItems+1);
	            floatingSprites.add(sprite);
	        }
	    }
	}
}
