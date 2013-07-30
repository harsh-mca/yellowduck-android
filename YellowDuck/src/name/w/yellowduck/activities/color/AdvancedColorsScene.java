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
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

import android.graphics.Bitmap;

public class AdvancedColorsScene extends YDActLayerBase {
	private final int kTagButterfly       =100;
	private final int kTotalButterflies         =70;
	private final int kButterfliesPerLevel       =8;
	
	private java.util.ArrayList<String> butterflies;
	private int selectionIdx[]=new int[kTotalButterflies];
    private int targetButterflyIdx, selectedButterflyIdx;

    private int butterflySize;
    private CCLabel colorLabel;

    private CCSprite mask;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new AdvancedColorsScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public AdvancedColorsScene() {
		super();
        butterflies=new java.util.ArrayList<String>();
//1
        butterflies.add("coral");
        butterflies.add("claret");
        butterflies.add("sienna");
        butterflies.add("corn");
        butterflies.add("cobalt");
        butterflies.add("cyan");
        butterflies.add("chestnut");
        butterflies.add("almond");
  //2
        butterflies.add("ruby");
  //3
        butterflies.add("sapphire");
        butterflies.add("sage");
        butterflies.add("salmon");
        butterflies.add("sepia");
        butterflies.add("sulphur");//sulfer
        butterflies.add("tea");
        butterflies.add("lime");
        butterflies.add("turquoise");
 //4
        butterflies.add("absinthe");
        butterflies.add("mahogany");
        butterflies.add("aquamarine");
        butterflies.add("alabaster");
        butterflies.add("amber");
        butterflies.add("amethyst");
        butterflies.add("anise");
        butterflies.add("vermilion");
//5
        butterflies.add("ceruse");
        butterflies.add("fawn");
        butterflies.add("chartreuse");
        butterflies.add("emerald");
        butterflies.add("aubergine");
        butterflies.add("fuchsia");
        butterflies.add("glaucous");
//6
        butterflies.add("auburn");
        butterflies.add("azure");
        butterflies.add("greyish-brown"); //grayish brown
        butterflies.add("bistre");
        butterflies.add("crimson");
        butterflies.add("celadon");
        butterflies.add("cerulean");
//7
        butterflies.add("dove");
        butterflies.add("garnet");
        butterflies.add("indigo");
        butterflies.add("ivory");
        butterflies.add("jade");
        butterflies.add("lavender");
        butterflies.add("lichen");
        butterflies.add("wine");
//8
        butterflies.add("lilac");
        butterflies.add("magenta");
        butterflies.add("malachite");
        butterflies.add("navy");
        butterflies.add("larch");
        butterflies.add("mimosa");
        butterflies.add("ochre");
        butterflies.add("olive");
//9
        butterflies.add("opaline");
        butterflies.add("ultramarine");
        butterflies.add("mauve");
        butterflies.add("greyish_blue"); //gray blue
        butterflies.add("pistachio");
        butterflies.add("platinum");
        butterflies.add("purple");
//10
        butterflies.add("plum");
        butterflies.add("prussian_blue");
        butterflies.add("rust");
        butterflies.add("saffron");
        butterflies.add("vanilla");
        butterflies.add("veronese");
        butterflies.add("verdigris");
        butterflies.add("dark_purple");
	}

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=(butterflies.size() + kButterfliesPerLevel - 1) / kButterfliesPerLevel;
	
	    //change a background music
	    super.shufflePlayBackgroundMusic();
	    butterflySize=(int)(szWin.width / 8);
	    
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(), kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionOk);
	    
	    colorLabel = CCLabel.makeLabel("X", super.sysFontName(), super.mediumFontSize());
	    colorLabel.setColor(ccColor3B.ccBLACK);
	    colorLabel.setPosition(szWin.width/2, szWin.height-super.topOverhead()-colorLabel.getContentSize().height/2);
	    super.addChild(colorLabel,0);
	    
	    //butterfly area background
	    int xPos=butterflySize*2;
	    int yPos=(int)(szWin.height - butterflySize*2);
	    
	    Bitmap bg=super.roundCornerRect(butterflySize*4+10,butterflySize*2+8,0,new ccColor4B(244,180,14,80));
	    CCTexture2D texture=CCTextureCache.sharedTextureCache().addImage(bg, "butterfly_bg");
	    CCSprite sprite=CCSprite.sprite(texture);
	    sprite.setPosition(xPos + butterflySize * 2, yPos);
	    super.addChild(sprite,1);
	    
	    
	    mask=spriteFromExpansionFile("image/misc/selectionmask.png");
	    mask.setScale(1.0f * butterflySize/mask.getContentSize().width);
	    mask.setVisible(false);
	    super.addChild(mask, 2);
	    
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    
	    int start=(mLevel - 1) * kButterfliesPerLevel; //include
	    int end=start + kButterfliesPerLevel - 1; //include
	    if (end >= butterflies.size()) {
	        end=butterflies.size()-1;
	        start=end-kButterfliesPerLevel+1;
	    }
	    for (int i = 0; i < kTotalButterflies; ++i)
	        selectionIdx[i]=i;
	    //random butterflies
	    int levelSelected[]=new int[kButterfliesPerLevel];
	    for (int i = 0; i < kButterfliesPerLevel; ++i) {
	    	levelSelected[i]=selectionIdx[i+start];
	    }
	    super.randomIt(levelSelected, kButterfliesPerLevel);
	    for (int i = 0; i < kButterfliesPerLevel; ++i) {
	    	selectionIdx[i+start]=levelSelected[i];
	    }
	
	    int xPos=butterflySize*2;
	    int yPos=(int)(szWin.height - butterflySize*2);
	    for (int i = 0; i < kButterfliesPerLevel; ++i) {
	        int butterflyIdx=selectionIdx[start+i];
	        String butterflyClr=butterflies.get(butterflyIdx);
	
	        String img=String.format("image/activities/discovery/color/advanced_colors/%s_butterfly.png", butterflyClr);
	        String imgSelected=super.buttonize(img);
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(imgSelected);
	        CCSprite spriteSelected=CCSprite.sprite(texture);
	        
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected, this, "butterflyTouched");
	        menuitem.setPosition(xPos + butterflySize /2 , yPos + butterflySize/2);
	        menuitem.setTag(kTagButterfly+butterflyIdx);
	        menuitem.setUserData(butterflyClr);
	        float scale1=butterflySize/menuitem.getContentSize().width;
	        float scale2=butterflySize/menuitem.getContentSize().height;
	        menuitem.setScale((scale2 > scale1) ? scale1 : scale2);
	
	        CCMenu menu = CCMenu.menu(menuitem);
	        menu.setPosition(0,0);
	        super.addChild(menu, 3);
	        floatingSprites.add(menu);
	        
	        xPos += butterflySize;
	        if ((i % 4) == 3) {
	            xPos = butterflySize*2;
	            yPos -= butterflySize;
	        }
	    }
	
	    targetButterflyIdx=selectionIdx[start+super.nextInt(kButterfliesPerLevel)];
	    this.prompt2find();
	}
	
	private void prompt2find() {
	    selectedButterflyIdx=1000;
	    mask.setVisible(false);
	    
	    String butterflyClr=butterflies.get(targetButterflyIdx);
	    String entry="clr_"+butterflyClr;
	    colorLabel.setString(String.format(super.localizedString("msg_find_butterfly"), super.localizedString(entry)));
	}
	
	public void ok(Object sender) {
	    if (selectedButterflyIdx == targetButterflyIdx) {
	        super.flashAnswerWithResult(true, true, null, null, 2);
	    }
	    else {
	        super.flashAnswerWithResult(false, false, null, null, 2);
	    }
	}

	public void butterflyTouched(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    selectedButterflyIdx=sender.getTag()-kTagButterfly;
	
	    mask.setPosition(sender.getPosition());
	    mask.setVisible(true);
	
	    //display the color name for a while
	    String butterflyClr=(String)sender.getUserData();
	    String entry="clr_"+butterflyClr;
	    super.flashMsg(super.localizedString(entry), 1.0f, butterflySize*1.5f);
	}
}
