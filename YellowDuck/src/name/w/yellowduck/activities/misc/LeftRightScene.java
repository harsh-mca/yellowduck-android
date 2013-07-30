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
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemLabel;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.ccColor3B;

public class LeftRightScene extends name.w.yellowduck.YDActLayerBase {
	private final int kSublevelsPerLevel      =8;
	private final int kLeft                   =0;
	private final int kRight                  =1;
	
    private CCLabel infoLabel;
    private CCSprite   handSprite;
    
    private int selections[]=new int[10];
    private int leftright;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new LeftRightScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=4;
	    mMaxSublevel=kSublevelsPerLevel;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
	
	
	    String fontName=super.sysFontName();
	    int fontSize=super.mediumFontSize();
	    CCMenuItemLabel label1 = CCMenuItemLabel.item(CCLabel.makeLabel(super.localizedString("label_left"), fontName, fontSize), this, "left");
	    CCMenuItemLabel label2 = CCMenuItemLabel.item(CCLabel.makeLabel(super.localizedString("label_right"), fontName, fontSize), this, "right");
	    CCMenu menu = CCMenu.menu(label1, label2);
	    menu.setPosition(szWin.width/2, szWin.height / 4);
	    menu.alignItemsHorizontally(100);
	    super.addChild(menu,1);
	    
	    infoLabel = CCLabel.makeLabel("1 of 6",fontName,fontSize);
	    infoLabel.setPosition(infoLabel.getContentSize().width, szWin.height-super.topOverhead() - infoLabel.getContentSize().height);
	    infoLabel.setColor(ccColor3B.ccBLACK);
	    super.addChild(infoLabel,1);
	    
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    if (handSprite!=null) {
	        handSprite.removeFromParentAndCleanup(true);
	    }
	    infoLabel.setString(String.format("%d of %d", mSublevel+1, kSublevelsPerLevel));
	
	    String hands[]=new String[]{"main_droite_dessus_0.png","main_droite_paume_0.png",
	        "main_gauche_dessus_0.png","main_gauche_paume_0.png",
	        "main_droite_dessus_90.png","main_droite_paume_90.png",
	        "main_gauche_dessus_90.png","main_gauche_paume_90.png",
	        "main_droite_dessus_180.png","main_droite_paume_180.png",
	        "main_gauche_dessus_180.png","main_gauche_paume_180.png",
	        "main_droite_dessus_270.png","main_droite_paume_270.png",
	        "main_gauche_dessus_270.png","main_gauche_paume_270.png",
	        "poing_droit_dessus_0.png", "poing_droit_paume_0.png",
	        "poing_gauche_dessus_0.png", "poing_gauche_paume_0.png",
	        "poing_droit_dessus_90.png", "poing_droit_paume_90.png",
	        "poing_gauche_dessus_90.png", "poing_gauche_paume_90.png",
	        "poing_droit_dessus_180.png", "poing_droit_paume_180.png",
	        "poing_gauche_dessus_180.png", "poing_gauche_paume_180.png",
	        "poing_droit_dessus_270.png", "poing_droit_paume_270.png",
	        "poing_gauche_dessus_270.png", "poing_gauche_paume_270.png"};
	    if (mSublevel <= 0) {
	        for (int i = 0; i < kSublevelsPerLevel; ++i)
	            selections[i]=i;
	        super.randomIt(selections,kSublevelsPerLevel);
	    }
	    
	    String selected="image/activities/discovery/miscelaneous/leftright/"+hands[(mLevel-1) * kSublevelsPerLevel + selections[mSublevel]];
	    handSprite=spriteFromExpansionFile(selected);
	    handSprite.setScale(super.preferredContentScale(true));
	    handSprite.setPosition(szWin.width/2, szWin.height/2*1.2f);
	    super.addChild(handSprite,2);
	
	    if (selected.indexOf("droit") >= 0) {
	        leftright=kRight;
	    }
	    else {
	        leftright=kLeft;
	    }
	}
	public void left(Object sender) {
	    this.ok(kLeft);
	}
	public void right(Object sender) {
	    this.ok(kRight);
	}
	
	private void ok(int answer) {
	    boolean matched=(answer == leftright);
	    super.flashAnswerWithResult(matched, matched, null, null, 2);
	}
}
