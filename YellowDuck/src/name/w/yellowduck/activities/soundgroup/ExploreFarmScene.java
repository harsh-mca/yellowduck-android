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


public class ExploreFarmScene extends name.w.yellowduck.YDActLayerBase {
	private final int kTagRibbon          =10;
	
	private int totalAnimals;
	private java.util.ArrayList<YDShape> animals;
	private java.util.ArrayList<CCNode> clickableItems;
	private CCLabel descriptionLabel;
    
    private int selections[]=new int[20];
    private int trying, wins;
    private int soundPlaying;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ExploreFarmScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	// on "init" you need to initialize your instance
	public ExploreFarmScene() {
		super();
		animals=createShapesFromConfiguration("image/activities/discovery/sound_group/explore_farm_animals/board1_0.xml.in");
		this.totalAnimals=animals.size();
	    clickableItems=new java.util.ArrayList<CCNode>();
	}

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=3;

	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
	    
	//svg bg:  800x520 
	    CCSprite backgroundSprite=super.setupBackground(activeCategory.getBg(), kBgModeFit2Center);
	    
	    float w=backgroundSprite.getContentSize().width * backgroundSprite.getScale();
	    float h=backgroundSprite.getContentSize().height * backgroundSprite.getScale();
	    float x0=backgroundSprite.getPosition().x - w/2;
	    float y0=backgroundSprite.getPosition().y + h/2;

	    for (int i = 0; i < animals.size(); ++i) {
	        YDShape shape=animals.get(i);
	        
	        String img="image/activities/discovery/sound_group/explore_farm_animals/questionpic.png";
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(super.buttonize(img));
	        CCSprite spriteDn=CCSprite.sprite(texture);
	        CCMenuItemSprite cover=CCMenuItemImage.item(sprite, spriteDn, this, "exploreAnimal");
	        cover.setTag(i);
	        cover.setPosition(x0+shape.getPosition().x/800*w, y0-shape.getPosition().y/520*h);
	        cover.setScale(super.preferredContentScale(true));
	        CCMenu menu=CCMenu.menu(cover);
	        menu.setPosition(0,0);
	        super.addChild(menu,2);
	        
	        clickableItems.add(cover);
	    }
	    super.afterEnter();
	}
	
	public void onExit() {
	    super.stopSoundOrVoice(soundPlaying);
	    soundPlaying=0;
	    
	    super.onExit();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    
	    for (CCNode node : clickableItems) {
	    	CCMenuItemSprite sprite=(CCMenuItemSprite)node;
	    	sprite.setColor(ccColor3B.ccWHITE);
	    }

	    super.stopSoundOrVoice(soundPlaying);
	    soundPlaying=0;
	    
	    if (mLevel == 1) {
	        for (int i = 0; i < totalAnimals; ++i)
	            selections[i]=0;
	        Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	        //setup title at the bottom of the screen
	        this.drawTitle(super.localizedString(activeCategory.getTitle()));
	    }
	    else if (mLevel == 2 || mLevel == 3) {
	        for (int i = 0; i < totalAnimals; ++i)
	            selections[i]=i;
	        super.randomIt(selections,totalAnimals);
	        trying=wins=0;
	
	        String font=super.sysFontName();
	        int fontSize=super.smallFontSize();
	        String prompt=null;
	        if (mLevel==2)
	            prompt=super.localizedString("label_find_animal_sound");
	        else if (mLevel == 3)
	            prompt=super.localizedString("label_find_animal_description");
	        CCLabel promptLabel = CCLabel.makeLabel(prompt, font, fontSize);
	        promptLabel.setColor(ccColor3B.ccBLACK);
	        float yPos=szWin.height - super.topOverhead() - promptLabel.getContentSize().height;
	        promptLabel.setPosition(promptLabel.getContentSize().width/2+4, yPos);
	        super.addChild(promptLabel,2);
	        floatingSprites.add(promptLabel);
	        if (mLevel == 2) {
	            //the play button
	        	String img="image/activities/discovery/miscelaneous/explore/playbutton.png";
	            CCSprite sprite=spriteFromExpansionFile(img);
	            CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(super.buttonize(img));
	            CCSprite spriteDn=CCSprite.sprite(texture);
	            CCMenuItemSprite cover=CCMenuItemImage.item(sprite,spriteDn,this,"playAnimalSound");
	            cover.setTag(selections[trying]);
	            cover.setPosition(promptLabel.getPosition().x+promptLabel.getContentSize().width/2+4+sprite.getContentSize().width/2, yPos);
	            CCMenu menu=CCMenu.menu(cover);
	            menu.setPosition(0,0);
	            super.addChild(menu,2);
	            floatingSprites.add(menu);
	            yPos -= sprite.getContentSize().height/2;
	
	            this.prompt2explore();
	        }
	        else if (mLevel == 3) {
	            //the animal description
	            descriptionLabel = CCLabel.makeLabel(" ", font, fontSize);
	            descriptionLabel.setColor(ccColor3B.ccBLACK);
	            descriptionLabel.setPosition(descriptionLabel.getContentSize().width/2+4, yPos-descriptionLabel.getContentSize().height);
	            super.addChild(descriptionLabel,2);
	            floatingSprites.add(descriptionLabel);
	            yPos -= descriptionLabel.getContentSize().height*2;
	            
	            this.prompt2explore();
	        }
	        for (int i = 0; i < totalAnimals; ++i) {
	            CCSprite ribbon=spriteFromExpansionFile("image/activities/discovery/miscelaneous/explore/ribbon.png");
	            ribbon.setScale(super.preferredContentScale(true));
	            float h=ribbon.getContentSize().height*ribbon.getScale();
	            float w=ribbon.getContentSize().width*ribbon.getScale();
	            ribbon.setPosition((i+1) * w, yPos - h/2);
	            ribbon.setTag(kTagRibbon+i);
	            ribbon.setScale(ribbon.getScale()*0.8f);
	            ribbon.setColor(ccColor3B.ccGRAY);
	            super.addChild(ribbon,2);
	
	            floatingSprites.add(ribbon);
	        }
	    }
	}
	public void playAnimalSound(Object _sender){
	    String sound=this.animalSound(selections[trying]);
	    super.stopSoundOrVoice(soundPlaying);
	    soundPlaying=super.playSound(sound);
	}
	
	public void prompt2explore() {
	    if (trying < totalAnimals) {
	        if (mLevel == 2) {
	            this.playAnimalSound(null);
	        }
	        else if (mLevel == 3) {
	            descriptionLabel.setString(this.animalShortDescription(selections[trying]));
	            descriptionLabel.setPosition(descriptionLabel.getContentSize().width/2+4, descriptionLabel.getPosition().y);
	        }
	    }
	}
	public void exploreAnimal(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    super.stopSoundOrVoice(soundPlaying);
	    soundPlaying=0;
	    if (mLevel == 1) {
	        CCMenuItemSprite menuitem=(CCMenuItemSprite)_sender;
	        menuitem.setColor(ccColor3B.ccGRAY);
	        selections[sender.getTag()]=1;
	        
	        String animal=this.animalName(sender.getTag());
	        String img=this.animalImg(sender.getTag());
	        String titleEntry="title_"+animal;
	        String title=super.localizedString(titleEntry);
	        String textEntry="text_"+animal;
	        String text =super.localizedString(textEntry);
	        this.popupDetailsWithTitle(title, text, img);
	        soundPlaying=super.playSound(this.animalSound(sender.getTag()));
	    }
	    else {
	        if (sender.getTag() == selections[trying]) {
	            //correct
	            trying = (trying + 1) % totalAnimals;
	            if (++wins > totalAnimals) {
	                wins=totalAnimals;
	            }
	            for (CCNode node : floatingSprites) {
	                if (node.getTag() >= kTagRibbon && wins > node.getTag()-kTagRibbon) {
	                	CCSprite sprite=(CCSprite)node;
	                    sprite.setColor(ccColor3B.ccWHITE);
	                }
	            }
	            super.flashAnswerWithResult(true, (wins>=totalAnimals), null, null, 1);
	            if (wins < totalAnimals) {
	            	super.performSelector("prompt2explore",2.0f);//the voice is about 2 seconds long
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
		
		CCTextureCache.sharedTextureCache().removeTexture("animal_desc");//remove previous one if exists
		CCSprite flashContent=CCSprite.sprite(img, "animal_desc");

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
	private String animalName(int idx) {
	    YDShape shape=animals.get(idx);
	    return shape.getName();
	}
	private String animalShortDescription(int idx) {
	    String entry="short_"+this.animalName(idx);
	    return super.localizedString(entry);
	}
	private String animalImg(int idx) {
	    YDShape shape=animals.get(idx);
	    String img=shape.getResource();
	    return "image/activities/discovery/sound_group/explore_farm_animals/"+img;
	}
	private String animalSound(int idx) {
	    YDShape shape=animals.get(idx);
	    String sound=shape.getSound();
	    return "image/activities/discovery/sound_group/explore_farm_animals/"+sound;
	}
	
	protected void onFocus(int focusStatus, int event) {
	    if (focusStatus==kFocusStatusRestore && event == kPopupEventDetails && mLevel==1) {
	        super.stopSoundOrVoice(soundPlaying);
	        soundPlaying=0;
	        
	        boolean allExplored=true;
	        for (int i = 0; i < totalAnimals; ++i) {
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
