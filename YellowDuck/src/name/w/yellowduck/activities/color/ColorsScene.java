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
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

public class ColorsScene extends name.w.yellowduck.YDActLayerBase {
	private final int kTagDuck        =100;
	private final int kTotalDucks	  =10;

	private java.util.ArrayList<String> ducks;
    private int numOfDucks[]=new int[3];
    private int selectionIdx[]=new int[kTotalDucks];
    private int targetDuckSelectionIdx;
    
    float duckSize;
    CCLabel colorLabel;
    int selectedDuckIdx;
    CCSprite mask;

	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ColorsScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public ColorsScene() {
		super();
		ducks=new java.util.ArrayList<String> ();
        //Ducks
        ducks.add("yellow");
        ducks.add("black");
        ducks.add("green");
        ducks.add("red");
        ducks.add("white");
        ducks.add("blue");
        ducks.add("grey");
        ducks.add("orange");
        ducks.add("purple");
        ducks.add("brown");

        //level one has six ducks
        numOfDucks[1]=6;
        numOfDucks[2]=10;
	}
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=2;
	    //change a background music
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(),kBgModeStretch);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionRepeatButton|kOptionOk);
	
	    //duck size is determined by the screen width
	    duckSize=this.getDuckSize(szWin);
	    //A sprite indicating a duck is selected
	    mask=spriteFromExpansionFile("image/misc/selectionmask.png");
	    mask.setScale(1.0f * duckSize/mask.getContentSize().width);
	    mask.setVisible(false);
	    super.addChild(mask, 1);
	    
	    //Prompt message: find the red duck
	    colorLabel = CCLabel.makeLabel("X", super.sysFontName(), super.mediumFontSize());
	    colorLabel.setColor(ccColor3B.ccWHITE);
	    colorLabel.setPosition(szWin.width/2-8, szWin.height-super.topOverhead()/2);
	    super.addChild(colorLabel, 0);
	    
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	        
	    for (int i = 0; i < kTotalDucks; ++i)
	        selectionIdx[i]=i;
	    //random ducks
	    super.randomIt(selectionIdx,numOfDucks[mLevel]);
	    //display ducks
	    float xLeft=(szWin.width - duckSize * 4)/2;
	    float xPos=xLeft;
	    float yPos=442.0f/640*szWin.height;
	    for (int i = 0; i < numOfDucks[mLevel]; ++i) {
	        int duckIdx=selectionIdx[i];
	        String duckClr=ducks.get(duckIdx);

	        String img=String.format("image/activities/discovery/color/colors/%s_duck.png", duckClr);
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCTexture2D texture2 = CCTextureCache.sharedTextureCache().addImageExternal(this.buttonize(img));
	        CCSprite spriteSelected=CCSprite.sprite(texture2);
	        	        
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected, this, "duckTouched");
	        menuitem.setPosition(xPos + duckSize /2 , yPos + duckSize/2);
	        menuitem.setTag(kTagDuck+duckIdx);
	        menuitem.setUserData(duckClr);
	        menuitem.setScale(1.0f * duckSize/menuitem.getContentSize().width);
	        CCMenu menu = CCMenu.menu(menuitem);
	        menu.setPosition(0,0);
	        super.addChild(menu,3);
	        floatingSprites.add(menu);
	
	        xPos += duckSize;
	        if ((i % 4) == 3) {
	            xPos = xLeft;
	            yPos -= duckSize;
	        }
	    }
	    
	    super.randomIt(selectionIdx,numOfDucks[mLevel]);    
	    targetDuckSelectionIdx=0;
	    this.prompt2find();
	}
	//Prompt to find next duck. 04/06/2013:improved to find all ducks one by one in each level
	public void prompt2find() {
	    selectedDuckIdx=100;
	    mask.setVisible(false);
	    
	    String duckClr=ducks.get(selectionIdx[targetDuckSelectionIdx]);
	    String entry="clr_"+duckClr;
	    colorLabel.setString(String.format(super.localizedString("msg_find_duck"),super.localizedString(entry)));
	
	    this.announce(duckClr);
	}
	private void announce(String duckClr) {
	    String voice=String.format("colors/%s.mp3", duckClr);
	    super.playVoice(voice);
	}
	//Override
	public void ok(Object _sender) {
	    if (selectedDuckIdx==selectionIdx[targetDuckSelectionIdx]) {
	        super.flashAnswerWithResult(true, (++targetDuckSelectionIdx>=numOfDucks[mLevel]), null, null, 2);
	        if (targetDuckSelectionIdx<numOfDucks[mLevel]) {
	            super.performSelector("prompt2find", 2);
	        }
	    }
	    else {
	        super.flashAnswerWithResult(false, false, null, null, 2);
	    }
	}

	//Move the selection mask to the behind of selected duck
	public void duckTouched(Object _sender){
		CCNode sender=(CCNode)_sender;
	    String duckClr=(String)sender.getUserData();
	    this.announce(duckClr);
	    
	    selectedDuckIdx=sender.getTag()-kTagDuck;
	
	    mask.setPosition(sender.getPosition());
	    mask.setVisible(true);
	}

	//Override
	public void repeat(Object sender) {
	    this.announce(ducks.get(selectionIdx[targetDuckSelectionIdx]));
	}

	private float getDuckSize(CGSize screen) {
	    return 420.0f/640*screen.height / 3 * 0.95f;
	}
}
