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
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.ccColor3B;

public class MissingLetterScene extends name.w.yellowduck.YDActLayerBase {
    private CCSprite backgroundSprite;
    private CCLabel sublevelLabel, missingLabel;
    private java.util.ArrayList<YDShape> shapes;
    private String word;
    
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new MissingLetterScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
		super.onEnter();
	    mMaxLevel=5;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    backgroundSprite=super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);

	    sublevelLabel = CCLabel.makeLabel("XX of X", super.sysFontName(), super.mediumFontSize());
	    sublevelLabel.setColor(ccColor3B.ccBLACK);
	    sublevelLabel.setPosition(sublevelLabel.getContentSize().width/2+10, szWin.height-topOverhead() - sublevelLabel.getContentSize().height);
	    super.addChild(sublevelLabel,1);
	
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	
	    if (mSublevel <= 0) {
	        //loading configuration
	        String configFile=String.format("image/activities/reading/missing_letter/board%d.xml", mLevel);
	        shapes=createShapesFromConfiguration(configFile);
	        mMaxSublevel=shapes.size();
	    }
	    //the picture
	    YDShape shape=shapes.get(mSublevel);
	    CCSprite sprite=spriteFromExpansionFile("image/activities/reading/" + shape.getResource());
	    sprite.setPosition(502.0f/800 * backgroundSprite.getContentSize().width * backgroundSprite.getScaleX() , 243.f/520*backgroundSprite.getContentSize().height*backgroundSprite.getScaleY());
	    sprite.setScale(preferredContentScale(true));
	    super.addChild(sprite,1);
	    floatingSprites.add(sprite);
	    
	    //the answers
	    String answers[]=shape.getExtra().split("/");
	    this.word=answers[0];
	    int selections[]=new int[answers.length];
	    for (int i = 0; i < answers.length; ++i)
	        selections[i]=i;
	    int partial[]=new int[selections.length-2];
	    for (int i = 0; i < partial.length; ++i)
	    	partial[i]=selections[i+2];
	    super.randomIt(partial, partial.length);
	    for (int i = 0; i < partial.length; ++i)
	    	selections[i+2]=partial[i];
	    
	    float xPos=117.0f/800*backgroundSprite.getContentSize().width * backgroundSprite.getScaleX();
	    int fontSize=super.mediumFontSize();
	    
	    float keySize=(szWin.height - topOverhead() * 2 - bottomOverhead()) / answers.length;
	    if (keySize > 128)
	        keySize=128;
	    if (keySize > xPos * 2)
	        keySize = xPos * 2;
	    float yMargin=(szWin.height - topOverhead() * 2 - bottomOverhead() - (answers.length - 2) * keySize)/2;
	    float yPos=szWin.height - topOverhead() * 2-yMargin;
	    
	    String imgKeyBg="image/activities/reading/hangman/tile.png";
	    String imgKeyPressed=super.buttonize(imgKeyBg);
	    CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(imgKeyPressed);
	    for (int i = 2; i < answers.length; ++i) {
	        CCSprite sprite_=spriteFromExpansionFile(imgKeyBg);
	        CCSprite spriteSelected_=CCSprite.sprite(texture);
	        CCMenuItemSprite letter=CCMenuItemImage.item(sprite_, spriteSelected_, this, "answered");
	        letter.setScale(keySize * 0.9f/letter.getContentSize().width);
	        letter.setPosition(xPos, yPos-letter.getContentSize().height*letter.getScale()/2);
	        letter.setTag((selections[i]==2)?100:0);//100: correct;
	        CCMenu menu = CCMenu.menu(letter);
	        menu.setPosition(0,0);
	        super.addChild(menu, 1);
	        floatingSprites.add(menu);
	
	        CCLabel titleLabel = CCLabel.makeLabel(answers[selections[i]], super.sysFontName(),fontSize);
	        titleLabel.setColor(ccColor3B.ccBLACK);
	        titleLabel.setPosition(letter.getPosition());
	        if (titleLabel.getContentSize().width > keySize)
	            titleLabel.setScale(keySize/titleLabel.getContentSize().width*0.9f);
	        super.addChild(titleLabel,2);
	        floatingSprites.add(titleLabel);
	
	        yPos -= keySize + 4;
	    }
	    
	    String missing=answers[1]; //496x76
	    missingLabel = CCLabel.makeLabel(missing, super.sysFontName(), super.mediumFontSize());
	    missingLabel.setPosition(496.0f/800*backgroundSprite.getContentSize().width*backgroundSprite.getScaleX(), 76.0f/520*backgroundSprite.getContentSize().height*backgroundSprite.getScaleY());
	    super.addChild(missingLabel,1);
	    floatingSprites.add(missingLabel);
	    
	    
	    sublevelLabel.setString(String.format("%d of %d", mSublevel+1, mMaxSublevel));
	}
	
	public void answered(Object _sender) {
		CCNode sender=(CCNode)_sender;
		int tag=sender.getTag();
	    if (tag==100) {
	        missingLabel.setString(word);
	    }
	    super.flashAnswerWithResult((tag==100),(tag==100), null, null, 2);
	}
}
