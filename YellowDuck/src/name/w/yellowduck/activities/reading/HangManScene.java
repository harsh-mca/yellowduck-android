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

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.ccColor3B;

public class HangManScene extends name.w.yellowduck.YDActLayerBase {
	private final int kTagLetterInAnswer      =101;
	
    private java.util.ArrayList<String> words;
    private CCLabel triesLabel;
    
    private float letterSize;
    
    private String answer;
    private int maxTries, triesLeft;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new HangManScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public void onEnter() {
		super.onEnter();
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);

	    //display the keyboard
	    letterSize=super.setupVirtualKeyboard("ZXCVBNM/ASDFGHJKL/QWERTYUIOP", floatingSprites) * 0.6f;
	    
	    words=new java.util.ArrayList<String>();
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    
	    for (int i = floatingSprites.size() - 1; i >= 0; --i) {
	        CCNode node=floatingSprites.get(i);
	        if (node.getTag() == kTagKeyboard) {
	        	if (node instanceof CCMenuItemSprite) {
		            CCMenuItemSprite letter=(CCMenuItemSprite)node;
		            letter.setColor(ccColor3B.ccWHITE);
	        	}
	        }
	        else {
	            node.removeFromParentAndCleanup(true);
	            floatingSprites.remove(i);
	        }
	    }
	    if (mSublevel <= 0) {
	        words.clear();
	        
	        //loading configuration
	        String fileName="image/activities/reading/wordsgame/default-" + YDConfiguration.sharedConfiguration().getLocale() + ".xml";
	        java.util.ArrayList<String> lines=loadExpansionAssetFile(fileName);
	        if (lines==null || lines.isEmpty()) {
	        	fileName="image/activities/reading/wordsgame/default-en.xml";
		        lines=loadExpansionAssetFile(fileName);
	        }
	        if (mMaxLevel <= 2) {
	            //test the max level from the word-list file
	            mMaxLevel=3;
	            for (;;) {
		            String lookingFor=String.format("<level value=\"%d\">", mMaxLevel);
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
	        int start=0, findLevel=mLevel;
	        //try to find: <level value="1">
	        while (words.size() <= 0) {
	            String lookingFor=String.format("<level value=\"%d\">", findLevel);
	            for (start = 0; start < lines.size(); ++start) {
	                String line=lines.get(start);
	                if (line.indexOf(lookingFor) >= 0) {
	                    break;
	                }
	            }
	            for (int i = start + 1; i < lines.size(); ++i) {
	                String word=lines.get(i);
	                if (word.indexOf("<") >= 0)//another level started
	                    break;
	                if (word.length() > 0)
	                    words.add(word);
	            }
	            --findLevel;
	        }
	        
	        mMaxSublevel=words.size()/30;
	        if (mMaxSublevel < 3)
	            mMaxSublevel=3;
	    }
	    //random selection one
	    answer=words.get(super.nextInt(words.size()));
	    answer=answer.toUpperCase();
	    
	    maxTries = 12;
	    triesLeft= maxTries;
	
	    float xPos=(szWin.width - answer.length() * letterSize)/2;
	    float yPos=szWin.height-szWin.height/6;
	
	    for (int i = 0; i < answer.length(); ++i) {
	        //the letter bg
	        CCSprite sprite=spriteFromExpansionFile("image/activities/reading/hangman/tile.png");
	        sprite.setScale(letterSize/sprite.getContentSize().width*0.9f);
	        sprite.setPosition(xPos + i * letterSize + sprite.getContentSize().width*sprite.getScale()/2, yPos);
	        super.addChild(sprite,1);
	        floatingSprites.add(sprite);
	
	        //the letter, still invisible
	        String theLetter=answer.substring(i, i+1);
	        CCLabel titleLabel = CCLabel.makeLabel(theLetter, super.sysFontName(), super.mediumFontSize());
	        titleLabel.setColor(ccColor3B.ccBLACK);
	        titleLabel.setPosition(sprite.getPosition());
	        titleLabel.setTag(kTagLetterInAnswer);
	        titleLabel.setUserData(theLetter);
	        titleLabel.setVisible(false);
	        if (titleLabel.getContentSize().width > letterSize*0.9f)
	            titleLabel.setScale(letterSize/titleLabel.getContentSize().width*0.9f);
	        super.addChild(titleLabel, 2);
	        floatingSprites.add(titleLabel);
	    }
	
	    triesLabel = CCLabel.makeLabel(String.format("%d/%d", triesLeft, maxTries), super.sysFontName(), super.mediumFontSize());
	    triesLabel.setPosition(szWin.width/2, yPos - letterSize * 2.5f);
	    super.addChild(triesLabel, 1);
	    floatingSprites.add(triesLabel);
	}

	public void letterTouched(Object _sender) {
	    if (triesLeft <= 0) //we are playing the check answer sound
	        return;
	    
	    super.playSound("audio/sounds/click.wav");
	    CCMenuItemSprite letter=(CCMenuItemSprite)_sender;
	    letter.setColor(ccColor3B.ccRED);
	    String theLetter=(String)(letter.getUserData());
	    theLetter=theLetter.toUpperCase();
	    
	    --triesLeft;
	    triesLabel.setString(String.format("%d/%d", triesLeft, maxTries));
	
	    for (CCNode node : floatingSprites) {
	        if (node.getTag() == kTagLetterInAnswer) {
	            String letterInAnswer=(String)node.getUserData();
	            if (theLetter.equalsIgnoreCase(letterInAnswer)) {
	                node.setVisible(true);
	                node.setTag(0);
	            }
	        }
	    }
	    int lettersLeft=0;
	    for (CCNode node : floatingSprites) {
	        if (node.getTag() == kTagLetterInAnswer) {
	            ++lettersLeft;
	        }
	    }
	    if (lettersLeft <= 0) {
	        super.flashAnswerWithResult(true,  true,  null,  null,  2);
	    }
	    else if (triesLeft <= 0) {
	        //reveal the answer
	        for (CCNode node : floatingSprites) {
	            if (node.getTag() == kTagLetterInAnswer) {
	                CCLabel label=(CCLabel)node;
	                label.setColor(ccColor3B.ccRED);
	                label.setVisible(true);
	            }
	        }
	        super.flashAnswerWithResult(false, false, null, null, 2);
	        super.performSelector("__initGame",  1);
	    }
	}

	public void __initGame() {
	    this.initGame(false,  null);
	}
}
