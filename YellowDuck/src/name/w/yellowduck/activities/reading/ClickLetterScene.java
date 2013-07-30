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
import name.w.yellowduck.Schema;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
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

public class ClickLetterScene extends name.w.yellowduck.YDActLayerBase {
	private CCSprite backgroundSprite;
    private CCLabel sublevelLabel;
    private boolean upperCase;
    private int selections[]=new int[26];
    private String questions;
    private String answers;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ClickLetterScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public void onEnter() {
		super.onEnter();
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    upperCase=activeCategory.getSettings().equalsIgnoreCase("up");
	    super.shufflePlayBackgroundMusic();
	    backgroundSprite=super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionRepeatButton);

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
	    	java.util.ArrayList<String> lines=loadExpansionAssetFile("image/activities/reading/click_on_letter/default-en.desktop");
	        if (mMaxLevel <= 2) {
	            //test the max level from the letter list file
	            mMaxLevel=3;
	            for (;;) {
		            String lookingFor="["+mMaxLevel+"]";
		            boolean find=false;
		            for (String line : lines) {
		            	if (line.indexOf(lookingFor) >= 0) {
		            		find=true;
		            		break;
		            	}
		            }
		            if (find) {
		            	++mMaxLevel;
		            }
		            else {
		            	--mMaxLevel;
		            	break;
		            }
	            }	            
	        }
	        int start=0;
	        for (start = 0; start < lines.size(); ++start) {
	            String line=lines.get(start);
	            if (line.startsWith("#")) //comment line
	                continue;
	
	            if (line.equalsIgnoreCase("["+mLevel+"]")) {
	                break;
	            }
	        }
	        
	        /*
	         [1]
	         Questions=aeiouy
	         Answers=aeiouy
	         */     
	        if (start < lines.size()) {
	            String _questions=lines.get(start+1);
	            String _answers=lines.get(start+2);
	            
	            this.questions=_questions.split("=")[1];
	            this.answers=_answers.split("=")[1];
	            
	            if (upperCase) {
	                questions=questions.toUpperCase();
	                answers=answers.toUpperCase();
	            }
	            
	            mMaxSublevel=questions.length();
	            for (int i = 0; i < mMaxSublevel; ++i) {
	                selections[i]=i;
	            }
	            super.randomIt(selections,mMaxSublevel);
	        }
	    }
	    //display up to six letters behind the train
	    //184x160, 1024x666
	    float xLeft=184.0f/1024*backgroundSprite.getContentSize().width*backgroundSprite.getScaleX();
	    //track to the screen bottom
	    float yBottom=20.0f/666*backgroundSprite.getContentSize().height*backgroundSprite.getScaleY();
	    
	    float carriageHeight=158.0f/666*backgroundSprite.getContentSize().height*backgroundSprite.getScaleY();
	    float carriageHeightPixels=carriageHeight;
	    
	    String imgCarriage=super.renderSVG2Img("image/activities/reading/click_on_letter/carriage.svg", 0, (int)carriageHeightPixels);
	    String imgCarriagePressed=super.buttonize(imgCarriage);
	
	    CCTexture2D texture1=CCTextureCache.sharedTextureCache().addImageExternal(imgCarriage);
	    CCTexture2D texture2=CCTextureCache.sharedTextureCache().addImageExternal(imgCarriagePressed);
	    int upto=(int)((szWin.width - xLeft)/texture1.getWidth());
	    if (upto > answers.length())
	        upto = answers.length();
	    for (int i = 0; i < upto; ++i) {
	        CCSprite sprite=CCSprite.sprite(texture1);
	        CCSprite spriteSelected=CCSprite.sprite(texture2);
	        CCMenuItemSprite letter=CCMenuItemImage.item(sprite, spriteSelected, this, "letterTouched");
	        letter.setTag(i);
	        letter.setPosition(xLeft + letter.getContentSize().width * i + letter.getContentSize().width/2, yBottom + letter.getContentSize().height/2);
	        CCMenu menu = CCMenu.menu(letter);
	        menu.setPosition(0,0);
	        super.addChild(menu,1); //just above the background
	        floatingSprites.add(menu);
	        
	        //display the letter on the carriage
	        CCLabel titleLabel = CCLabel.makeLabel(this.answers.substring(i, i+1), sysFontName(), super.mediumFontSize());
	        titleLabel.setColor(ccColor3B.ccBLACK);
	        titleLabel.setPosition(letter.getPosition().x, letter.getPosition().y + letter.getContentSize().height/4);
	        super.addChild(titleLabel,2); //above the carriage
	        floatingSprites.add(titleLabel);
	        
	        letter.setUserData(titleLabel);
	    }
	    //display other letters on the cloud
	    String imgCloud=super.renderSVG2Img("image/activities/reading/click_on_letter/cloud.svg", (int)texture1.getWidth(), 0);
	    String imgCloudPressed=super.buttonize(imgCloud);
	    CCTexture2D texture3=CCTextureCache.sharedTextureCache().addImageExternal(imgCloud);
	    CCTexture2D texture4=CCTextureCache.sharedTextureCache().addImageExternal(imgCloudPressed);
	    int row=0, col=0;
	    for (int i = upto; i < answers.length(); ++i) {
	        CCSprite sprite=CCSprite.sprite(texture3);
	        CCSprite spriteSelected=CCSprite.sprite(texture4);
	        CCMenuItemSprite letter=CCMenuItemImage.item(sprite, spriteSelected, this, "letterTouched");
	        letter.setTag(i);
	        letter.setScale(0.9f);
	        letter.setPosition(xLeft + letter.getContentSize().width * col + letter.getContentSize().width/2, yBottom + carriageHeight + row * letter.getContentSize().height + letter.getContentSize().height/2);
	        CCMenu menu = CCMenu.menu(letter);
	        menu.setPosition(0,0);
	        super.addChild(menu,1); //just above the background
	        floatingSprites.add(menu);
	        
	        //display the letter on the carriage
	        CCLabel titleLabel = CCLabel.makeLabel(answers.substring(i, i+1), super.sysFontName(), super.mediumFontSize());
	        titleLabel.setColor(ccColor3B.ccBLACK);
	        titleLabel.setPosition(letter.getPosition().x, letter.getPosition().y);
	        super.addChild(titleLabel,2); //above the carriage
	        floatingSprites.add(titleLabel);
	        
	        letter.setUserData(titleLabel);
	        
	        ++col;
	        if (col >= upto) {
	            ++row;
	            col=0;
	        }
	    }
	    
	    sublevelLabel.setString(String.format("%d of %d", mSublevel+1, mMaxSublevel));
	    super.performSelector("announce", 1);
	}
	public void letterTouched(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    CCLabel labelLetter=(CCLabel)sender.getUserData();
	    if (labelLetter!=null) {
	        labelLetter.stopAllActions();
	        CCScaleTo scaleUp=CCScaleTo.action(Schema.ANIMATION_SPEED, 2);
	        CCScaleTo scaleDn=CCScaleTo.action(Schema.ANIMATION_SPEED, 1);
	        labelLetter.runAction(CCSequence.actions(scaleUp, scaleDn));
	    }
	    int _answer=answers.charAt(sender.getTag());
	    int _question=questions.charAt(selections[mSublevel]);
	    if (_answer == _question) {
	        super.flashAnswerWithResult(true, true, null, null, 2);
	    }
	    else {
	    	super.flashAnswerWithResult(false, false, null, null, 2);
	    }
	}
	
	public void announce(Object sender) {
	    super.playVoice("misc/click_on_letter.mp3");
	    super.performSelector("repeat", 1.5f);
	}
	
	public void repeat(Object sender) {
	    String _question=questions.toLowerCase();
	    int ch=_question.charAt(selections[mSublevel]);
	    String voiceFile=String.format("alphabet/U%04X.mp3", ch);
	    super.playVoice(voiceFile);
	}
}
