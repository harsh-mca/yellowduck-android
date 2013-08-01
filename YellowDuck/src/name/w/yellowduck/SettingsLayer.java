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


package name.w.yellowduck;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemLabel;
import org.cocos2d.menus.CCMenuItemToggle;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.nodes.CCDirector;

public class SettingsLayer extends YDLayerBase {
    private CCSprite spriteChecked;
    private String locale;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new SettingsLayer();
	    scene.addChild(layer); 
	    return scene;
	}

	public void onEnter() {
		super.onEnter();
	    // @harsh : changed background2.jpg tobackground2.png
		
		// ask director for the window size
	    super.setupBackground("image/misc/background2.png", kBgModeFit);
	    super.stopBackgroundMusic();
	    super.playBackgroundMusic("audio/music/intro.mp3");
	    super.setupSideToolbar(null, kOptionOk);
	    
	    
	    float yPos=szWin.height*0.75f;
	    float yGroupMargin=10;
	    
	    ccColor3B clrText=ccColor3B.ccBLACK;
        String fontName=super.sysFontName();
        int fontSize=super.mediumFontSize();
	    {
	        //background music settings
	        String msg=localizedString("label_bg_music");
	        CCLabel labelBgMusic = CCLabel.makeLabel(msg, fontName, fontSize);
	        labelBgMusic.setColor(clrText);
	        labelBgMusic.setPosition(szWin.width/2-labelBgMusic.getContentSize().width/2, yPos);
	        super.addChild(labelBgMusic,1);
	        yPos -= labelBgMusic.getContentSize().height + 4;
	        
	        //its options: on / off
	
	        CCMenuItemLabel labelOn = CCMenuItemLabel.item(CCLabel.makeLabel(localizedString("label_on"), fontName, fontSize), this, "toggleMusicStatus");
	        labelOn.setColor(clrText);
	        CCMenuItemLabel labelOff = CCMenuItemLabel.item(CCLabel.makeLabel(localizedString("label_off"), fontName, fontSize), this, "toggleMusicStatus");
	        labelOff.setColor(clrText);
	        CCMenuItemToggle toggleItems = CCMenuItemToggle.item(this,  "toggleMusicStatus", labelOn, labelOff);
	        toggleItems.setSelectedIndex(SysHelper.isBgMusicEnabled()?0:1);
	        CCMenu menu = CCMenu.menu(toggleItems);
	        menu.setPosition(szWin.width/2+labelOn.getContentSize().width/2, yPos);
	        menu.alignItemsHorizontally(100);
	        super.addChild(menu,1);
	        
	        yPos -= labelOn.getContentSize().height;
	    }
	    yPos -= yGroupMargin;
	    //language setting
	    {
	        locale=YDConfiguration.sharedConfiguration().getLocale();
	        
	        String msg=localizedString("label_language");
	        CCLabel labelLanguage = CCLabel.makeLabel(msg,fontName, fontSize);
	        labelLanguage.setColor(clrText);
	        labelLanguage.setPosition(szWin.width/2-labelLanguage.getContentSize().width/2, yPos);
	        super.addChild(labelLanguage,1);
	        yPos -= labelLanguage.getContentSize().height + 4;
	        	        //English
	        String languages[]={"English", "中文"};
	        String locales[]={"en", "zh_CN"};
	        for (int i = 0; i < languages.length; ++i) {
	            CCMenuItemLabel label = CCMenuItemLabel.item(CCLabel.makeLabel(languages[i],fontName,fontSize),this, "localeSelected");
	            label.setColor(clrText);
	            label.setUserData(locales[i]);
	            CCMenu menu = CCMenu.menu(label);
	            menu.setPosition(szWin.width/2+label.getContentSize().width/2, yPos);
	            menu.alignItemsHorizontally(100);
	            super.addChild(menu,1);
	            if (locale.equalsIgnoreCase(locales[i])) {
	            	// @harsh : there is no ok.png file in misc folder so i repalced the "ok.png" to "star.png"
	            	// @harsh : placed one image as ok.png in image/misc/ folder   just  for testing purpose
	                spriteChecked=spriteFromExpansionFile("image/misc/ok.png");
	                spriteChecked.setScale(label.getContentSize().height/spriteChecked.getContentSize().height);
	                spriteChecked.setPosition(szWin.width/2-spriteChecked.getContentSize().width*spriteChecked.getScale()/2, yPos);
	                super.addChild(spriteChecked);
	            }
	            yPos -= label.getContentSize().height;
	        }
	    }
	
	    yPos -= yGroupMargin*2;
	    float xLeft=0;
	    {
	        CCLabel label=CCLabel.makeLabel(localizedString("label_difficulty_level"), fontName, super.smallFontSize());
	        label.setColor(clrText);
	        label.setPosition(szWin.width/2, yPos);
	        super.addChild(label);
	        yPos -= label.getContentSize().height;
	        xLeft = label.getPosition().x-label.getContentSize().width/2+label.getContentSize().width/4;
	    }
	    {
	        CCLabel label=CCLabel.makeLabel(localizedString("label_difficulty_group1"), fontName, super.smallFontSize());
	        float cyRoom=label.getContentSize().height;
	        float xPos=xLeft;
	        for (int i= 0; i < 3; ++i) {
	            String diff=super.renderSkinSVG2Button(i+Schema.kDifficulty1,Schema.kDifficultyIndicatorSize_);
	            if (diff!=null) {
	            	CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(diff);
	                CCSprite sprite=CCSprite.sprite(texture);
	                if (sprite.getContentSize().height > cyRoom)
	                    cyRoom=sprite.getContentSize().height;
	                sprite.setPosition(xPos+sprite.getContentSize().width/2, yPos-cyRoom/2);
	                super.addChild(sprite, 1);
	                xPos += sprite.getContentSize().width;
	            }
	        }
	        label.setColor(clrText);
	        label.setPosition(xPos+label.getContentSize().width/2, yPos-cyRoom/2);
	        super.addChild(label);
	        yPos -= cyRoom;
	    }
	    {
	        CCLabel label=CCLabel.makeLabel(localizedString("label_difficulty_group2"), fontName, super.smallFontSize());
	        float cyRoom=label.getContentSize().height;
	        float xPos=xLeft;
	        for (int i= 0; i < 3; ++i) {
	            String diff=super.renderSkinSVG2Button(i+Schema.kDifficulty4,Schema.kDifficultyIndicatorSize_);
	            if (diff!=null) {
	            	CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(diff);
	                CCSprite sprite=CCSprite.sprite(texture);
	                if (sprite.getContentSize().height > cyRoom)
	                    cyRoom=sprite.getContentSize().height;
	                sprite.setPosition(xPos+sprite.getContentSize().width/2, yPos-cyRoom/2);
	                super.addChild(sprite, 1);
	                xPos += sprite.getContentSize().width;
	            }
	        }
	        label.setColor(clrText);
	        label.setPosition(xPos+label.getContentSize().width/2, yPos-cyRoom/2);
	        super.addChild(label);
	        yPos -= label.getContentSize().height;
	    }
	    
	    String copyright=localizedString("label_copyright");
	    CCLabel labelCopyright = CCLabel.makeLabel(copyright, fontName, super.smallFontSize());
	    labelCopyright.setColor(clrText);
	    labelCopyright.setPosition (labelCopyright.getContentSize().width/2, bottomOverhead()+labelCopyright.getContentSize().height/2);
	    super.addChild(labelCopyright,1);
	}
	
	public void toolbarBtnTouched(Object _sender) {
	    if (_sender == null)
	        return;
	    CCNode sender=(CCNode)_sender;
	    if (sender.getTag() == Schema.kSvgButtonOk) {
        	this.ok(sender);
	    }
	}
	
	public void ok(Object sender) {
	    YDConfiguration.sharedConfiguration().setLocale(locale);
	    CCDirector.sharedDirector().replaceScene(CategoryLayer.scene());
	}
	
	public void toggleMusicStatus(Object _sender) {
	    CCMenuItemToggle toggleItem = (CCMenuItemToggle)_sender;
	    if (toggleItem.selectedIndex() == 0) {//on
	        SysHelper.setBgMusicEnabled(true);
	      //[super refreshAudioSettings];
	        super.playBackgroundMusic("audio/music/intro.mp3");
	    }
	    else {//off
	        SysHelper.setBgMusicEnabled(false);
	        //[super refreshAudioSettings];
	        super.stopBackgroundMusic();
	    }
	}
	public void localeSelected(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    locale=(String)sender.getUserData();
	    spriteChecked.setPosition(szWin.width/2-spriteChecked.getContentSize().width*spriteChecked.getScale()/2, sender.getParent().getPosition().y);
	}
}
