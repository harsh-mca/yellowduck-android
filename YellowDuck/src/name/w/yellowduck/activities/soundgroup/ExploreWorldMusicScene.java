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


package name.w.yellowduck.activities.soundgroup;

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
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

public class ExploreWorldMusicScene extends name.w.yellowduck.YDActLayerBase {
	private final int kTagRibbon      =10;
	private java.util.ArrayList<YDShape> musics;
    private int totalMusics;
    private float yMapBottom;
    private java.util.ArrayList<CCNode> clickableItems;
    
    int selections[]=new int[20];
    int trying, wins;
    
    int musicPlaying;
    private CCLabel descriptionLabel;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ExploreWorldMusicScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public ExploreWorldMusicScene() {
		super();
		musics=createShapesFromConfiguration("image/activities/discovery/sound_group/explore_world_music/board1_0.xml.in");
		this.totalMusics=musics.size();
	    clickableItems=new java.util.ArrayList<CCNode>();
	}

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=3;
	
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.stopBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
	//svg bg:  770x413
	    CCSprite backgroundSprite=super.setupBackground(activeCategory.getBg(), kBgModeFit2Center);
	    
	    float w=backgroundSprite.getContentSize().width * backgroundSprite.getScale();
	    float h=backgroundSprite.getContentSize().height * backgroundSprite.getScale();
	    float x0=backgroundSprite.getPosition().x - w/2;
	    float y0=backgroundSprite.getPosition().y + h/2;
	    yMapBottom=backgroundSprite.getPosition().y-h/2;
	    int index=0;
	    for (YDShape shape : musics) {
	        String img="image/activities/discovery/sound_group/explore_world_music/suitcase.png";
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(super.buttonize(img));
	        CCSprite spriteDn=CCSprite.sprite(texture);
	        CCMenuItemSprite cover=CCMenuItemImage.item(sprite, spriteDn, this, "exploreMusic");
	        cover.setTag(index);
	        cover.setPosition(x0+shape.getPosition().x/770*w, y0-(shape.getPosition().y-10)/413*h);
	        cover.setScale(super.preferredContentScale(true));
	        CCMenu menu=CCMenu.menu(cover);
	        menu.setPosition(0,0);
	        super.addChild(menu,2);
	        
	        clickableItems.add(cover);
	    	
	        ++index;
	    }
	    super.afterEnter();
	}
	public void onExit() {
	    super.stopSoundOrVoice(musicPlaying);
	    super.onExit();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    
	    for (CCNode node : clickableItems) {
	    	CCMenuItemSprite sprite=(CCMenuItemSprite)node;
	    	sprite.setColor(ccColor3B.ccWHITE);
	    }

	    super.stopSoundOrVoice(musicPlaying);
	    musicPlaying=0;
	    
	    if (mLevel == 1) {
	        for (int i = 0; i < musics.size(); ++i)
	            selections[i]=0;
	        Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	        //setup title at the bottom of the screen
	        this.drawTitle(localizedString(activeCategory.getTitle()));
	    }
	    else if (mLevel == 2 || mLevel == 3) {
	        for (int i = 0; i < musics.size(); ++i)
	            selections[i]=i;
	        super.randomIt(selections, musics.size());
	        trying=wins=0;
	
	        for (int i = 0; i < musics.size(); ++i) {
	            CCSprite ribbon=spriteFromExpansionFile("image/activities/discovery/miscelaneous/explore/ribbon.png");
	            ribbon.setScale(super.preferredContentScale(true));
	            float h=ribbon.getContentSize().height*ribbon.getScale();
	            float w=ribbon.getContentSize().width*ribbon.getScale();
	            ribbon.setPosition((i+1) * w, yMapBottom+h/2);
	            ribbon.setTag(kTagRibbon+i);
	            ribbon.setScale(ribbon.getScale()*0.8f);
	            ribbon.setColor(ccColor3B.ccGRAY);
	            super.addChild(ribbon,2);
	
	            floatingSprites.add(ribbon);
	        }
	        
	        String font=super.sysFontName();
	        int fontSize=super.smallFontSize();
	        String prompt=null;
	        if (mLevel==2)
	            prompt=super.localizedString("label_find_music");
	        else if (mLevel == 3)
	            prompt=super.localizedString("label_find_music_tex");
	        
	        CCLabel promptLabel = CCLabel.makeLabel(prompt, font, fontSize);
	        promptLabel.setColor(ccColor3B.ccBLACK);
	        float yPos=szWin.height - super.topOverhead() - promptLabel.getContentSize().height;
	        promptLabel.setPosition(promptLabel.getContentSize().width/2+4, yPos);
	        super.addChild(promptLabel,2);
	        floatingSprites.add(promptLabel);
	        if (mLevel == 2) {
	        	String img="image/activities/discovery/miscelaneous/explore/playbutton.png";
	            CCSprite sprite=spriteFromExpansionFile(img);
	            CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(super.buttonize(img));
	            CCSprite spriteDn=CCSprite.sprite(texture);
	            CCMenuItemSprite cover=CCMenuItemImage.item(sprite,spriteDn,this,"playMusicSound");
	            cover.setTag(selections[trying]);
	            cover.setPosition(2+sprite.getContentSize().width/2,yPos-promptLabel.getContentSize().height/2-sprite.getContentSize().height/2-4);
	            CCMenu menu=CCMenu.menu(cover);
	            menu.setPosition(0,0);
	            super.addChild(menu,2);
	            floatingSprites.add(menu);
	            yPos -= sprite.getContentSize().height/2;
	
	            this.prompt2explore();
	        }
	        else if (mLevel == 3) {
	            //the Music description
	            descriptionLabel = CCLabel.makeLabel(" ", font, fontSize);
	            descriptionLabel.setColor(ccColor3B.ccBLACK);
	            descriptionLabel.setPosition(descriptionLabel.getContentSize().width/2+4, yPos-descriptionLabel.getContentSize().height);
	            super.addChild(descriptionLabel, 2);
	            floatingSprites.add(descriptionLabel);
	            
	            this.prompt2explore();
	        }
	    }
	}
	public void playMusicSound(Object sender) {
	    super.stopSoundOrVoice(musicPlaying);
	    String sound=this.musicSound(selections[trying]);
	    musicPlaying=super.playSound(sound);
	}
	private void prompt2explore() {
	    if (trying < totalMusics) {
	        if (mLevel == 2) {
	            this.playMusicSound(null);
	        }
	        else if (mLevel == 3) {
	            descriptionLabel.setString(musicShortDescription(selections[trying]));
	            descriptionLabel.setPosition(descriptionLabel.getContentSize().width/2+4, descriptionLabel.getPosition().y);
	        }
	    }
	}
	public void exploreMusic(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    if (musicPlaying>0) {
	        super.stopSoundOrVoice(musicPlaying);
	        musicPlaying=0;
	    }
	    if (mLevel == 1) {
	        CCMenuItemSprite menuitem=(CCMenuItemSprite)_sender;
	        menuitem.setColor(ccColor3B.ccGRAY);
	        selections[sender.getTag()]=1;
	        
	        String Music=this.musicName(sender.getTag());
	        String img=this.musicImg(sender.getTag());
	        String titleEntry="title_music_"+Music;
	        String title=localizedString(titleEntry);
	        String textEntry="text_music_"+Music;
	        String text =localizedString(textEntry);
	        this.popupDetailsWithTitle(title, text, img);
	        musicPlaying=super.playSound(musicSound(sender.getTag()));
	    }
	    else {
	        if (sender.getTag() == selections[trying]) {
	            //correct
	            trying = (trying + 1) % totalMusics;
	            if (++wins > totalMusics) {
	                wins=totalMusics;
	            }
	            for (CCNode node : floatingSprites) {
	                if (node.getTag() >= kTagRibbon && wins > node.getTag()-kTagRibbon) {
	                	CCSprite sprite=(CCSprite)node;
	                    sprite.setColor(ccColor3B.ccWHITE);
	                }
	            }
	            super.flashAnswerWithResult(true, (wins>=totalMusics), null, null, 1);
	            if (wins < totalMusics) {
	            	super.performSelector("prompt2explore", 2.0f);
	            }
	        }
	        else {
	        	super.flashAnswerWithResult(false, false, null, null, 1);
	        }
	    }
	}
	private void drawTitle(String theTitle) {
	    int fontSize=super.mediumFontSize();
	    String font=super.sysFontName();
	    
	    CCLabel titleLabel = CCLabel.makeLabel(theTitle, font, fontSize);
	    titleLabel.setColor(ccColor3B.ccBLACK);
	    titleLabel.setPosition(szWin.width/2, szWin.height-titleLabel.getContentSize().height/2);
	    super.addChild(titleLabel,1);
	    floatingSprites.add(titleLabel);
	}
	
	private void popupDetailsWithTitle(String title, String text, String contentImage) {
		java.util.ArrayList<CCNode> ctrls=new java.util.ArrayList<CCNode>();
	    
	    String font=super.sysFontName();
	    int fontSize=super.mediumFontSize();
	    CCLabel titleLabel = CCLabel.makeLabel(title, font, fontSize);
	    titleLabel.setColor(ccColor3B.ccBLACK);
	    titleLabel.setPosition(szWin.width/2, szWin.height - titleLabel.getContentSize().height/2);
	    super.addChild(titleLabel, zPopupContents);
	    ctrls.add(titleLabel);
	    
	    CCSprite imgSprite=spriteFromExpansionFile(contentImage);
	    float xPos=szWin.width - imgSprite.getContentSize().width/2 - 10;
	    float yPos=(szWin.height -  titleLabel.getContentSize().height)/2;
	    imgSprite.setPosition(xPos, yPos);
	    super.addChild(imgSprite,zPopupContents);
	    ctrls.add(imgSprite);
	
	    fontSize=super.smallFontSize();
	    CGSize max=CGSize.make(szWin.width - imgSprite.getContentSize().width - 20, szWin.height);
	    
	    android.graphics.Bitmap img=super.createMultipleLineLabel(text, super.sysFontName(),fontSize, (int)(max.width), ccColor3B.ccBLACK, ccColor4B.ccc4(0, 0, 0, 0));
		CCTextureCache.sharedTextureCache().removeTexture("music_desc");//remove previous one if exists
		CCSprite flashContent=CCSprite.sprite(img, "music_desc");

	    flashContent.setPosition((szWin.width-imgSprite.getContentSize().width)/2, szWin.height/2);
	    super.addChild(flashContent,zPopupContents+1);
	    ctrls.add(flashContent);

	    CCSprite decoration=spriteFromExpansionFile("image/activities/discovery/miscelaneous/explore/border.png");
	    decoration.setPosition(decoration.getContentSize().width/2, szWin.height-decoration.getContentSize().height/2);
	    super.addChild(decoration,zPopupContents);
	    ctrls.add(decoration);
	    
	    super.pinPopupContents2Background(ctrls,kBgModeTile,kPopupEventDetails);
	    ctrls.clear();
	}
	
	private String musicName(int idx) {
	    YDShape shape=musics.get(idx);
	    return shape.getName();
	}
	private String musicShortDescription(int idx) {
	    String entry="short_music_" + musicName(idx);
	    return localizedString(entry);
	}
	
	private String musicImg(int idx) {
	    YDShape shape=musics.get(idx);
	    String img=shape.getResource();
	    return "image/activities/discovery/sound_group/explore_world_music/" + img;
	}
	private String musicSound(int idx) {
	    YDShape shape=musics.get(idx);
	    String sound=shape.getSound();
	    return "image/activities/discovery/sound_group/explore_world_music/" + sound;
	}
	
	protected void onFocus(int focusStatus, int event) {
	    if (focusStatus==kFocusStatusRestore && event == kPopupEventDetails && mLevel==1) {
	        super.stopSoundOrVoice(musicPlaying);
	        musicPlaying=0;
	        
	        boolean allExplored=true;
	        for (int i = 0; i < totalMusics; ++i) {
	            if (selections[i]==0) {
	                allExplored=false;
	                break;
	            }
	        }
	        if (allExplored) {
	            super.flashAnswerWithResult(true, true, null, null, 1);
	        }
	    }
	}
}
