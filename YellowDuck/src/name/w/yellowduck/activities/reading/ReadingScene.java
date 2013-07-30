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


package name.w.yellowduck.activities.reading;

import name.w.yellowduck.Category;
import name.w.yellowduck.YDConfiguration;
import name.w.yellowduck.activities.misc.YDShape;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

import android.graphics.Bitmap;

public class ReadingScene extends name.w.yellowduck.YDActLayerBase {
    private CCSprite backgroundSprite;
    private CCLabel sublevelLabel;
    private java.util.ArrayList<YDShape> shapes;

	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ReadingScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
		super.onEnter();
	    mMaxLevel=2;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    backgroundSprite=super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);

	    sublevelLabel = CCLabel.makeLabel("XX of X", super.sysFontName(), super.mediumFontSize());
	    sublevelLabel.setColor(ccColor3B.ccBLACK);
	    sublevelLabel.setPosition(sublevelLabel.getContentSize().width/2, szWin.height-topOverhead() - sublevelLabel.getContentSize().height);
	    super.addChild(sublevelLabel,1);
	
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    
	    if (mSublevel <= 0) {
	        //loading configuration
	        String configFile=String.format("image/activities/reading/imageid/board%d.xml", mLevel);
	        shapes=createShapesFromConfiguration(configFile);
	        mMaxSublevel=shapes.size();
	    }
	    //the picture
	    YDShape shape=shapes.get(mSublevel);
	    CCSprite sprite=spriteFromExpansionFile("image/activities/reading/" + shape.getResource());
	    sprite.setPosition(673.0f/1024 * backgroundSprite.getContentSize().width * backgroundSprite.getScaleX() , 326.f/666*backgroundSprite.getContentSize().height*backgroundSprite.getScaleY());
	    sprite.setScale(preferredContentScale(true));
	    super.addChild(sprite,1);
	    floatingSprites.add(sprite);
	    
	    //the answers
	    String answers[]=shape.getExtra().split("/");
	    int selections[]=new int[20];
	    for (int i = 0; i < answers.length; ++i)
	        selections[i]=i;
	    super.randomIt(selections, answers.length);
	    //area to display the answers
	    float ceiling=560.0f/666*szWin.height;
	    float floor=93.0f/666*szWin.height;
	    float yRoom=(ceiling-floor) / answers.length;
	    float yPos=ceiling-yRoom/2;
	    float xPos=147.0f/1024*backgroundSprite.getContentSize().width * backgroundSprite.getScaleX();
	
	    Bitmap bg=null, bgSel=null;
	    for (int i = 0; i < answers.length; ++i) {
	        String entry="name_" + answers[selections[i]];
	        String name=localizedString(entry);
	        	
	        CCLabel label = CCLabel.makeLabel(name, super.sysFontName(),super.mediumFontSize());
	        label.setPosition(xPos, yPos);
	        super.addChild(label,2);
	        floatingSprites.add(label);
	        
	        if (bg == null) {
	            bg=super.roundCornerRect((int)(xPos*2*0.8f), (int)(label.getContentSize().height*1.6f),  0, new ccColor4B(0x93, 0x70, 0xdb, 0xff));
	            bgSel=super.buttonize(bg);
	        }
	        CCSprite sprite_=CCSprite.sprite(bg, "wbg");
	        CCSprite spriteSelected_=CCSprite.sprite(bgSel, "wbgsel");
	        CCMenuItemSprite letter=CCMenuItemImage.item(sprite_, spriteSelected_, this, "answered");
	        letter.setTag((selections[i]==0)?100:0);//100: correct
	        letter.setPosition(0,0);
	        CCMenu menu = CCMenu.menu(letter);
	        menu.setPosition(label.getPosition());
	        super.addChild(menu,1); //just above the background
	        floatingSprites.add(menu);
	
	        yPos -= yRoom;
	    }
	    
	    sublevelLabel.setString(String.format("%d of %d", mSublevel+1, mMaxSublevel));
	}
	
	public void answered(Object _sender) {
		CCNode sender=(CCNode)_sender;
		int tag=sender.getTag();
	    super.flashAnswerWithResult((tag==100),(tag==100), null, null, 2);
	}
}
