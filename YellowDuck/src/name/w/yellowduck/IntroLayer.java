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

import org.cocos2d.actions.CCProgressTimer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;

public class IntroLayer extends YDLayerBase{
	private java.util.ArrayList<Category> flattedCategories;
	private java.util.ArrayList<Category> activities;
	
    private CCProgressTimer mProgress;
    
    private int idxCategory, idxActivity, idxButton;
    private int finishedThumbnails;
    private int totalThumbnails;
	private boolean launched, finished;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new IntroLayer();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	public void onEnter() {
		super.onEnter();
		//setup a simple background
		CCSprite spriteBg=CCSprite.sprite("background1.jpg");
		spriteBg.setScaleX(szWin.width/spriteBg.getContentSize().width);
		spriteBg.setScaleY(szWin.height/spriteBg.getContentSize().height);
		spriteBg.setPosition(szWin.width/2, szWin.height/2);
		super.addChild(spriteBg, 0);
	    super.scheduleUpdate();
	}
	public void update(float dt) {
		if (!launched) {
			CCSprite spriteBg=super.spriteFromExpansionFile("image/misc/background1.jpg");
			if (spriteBg == null) {
				CCLabel labelError=CCLabel.makeLabel(YDConfiguration.context.getString(R.string.msg_installation_err), super.sysFontName(), super.largeFontSize());
				labelError.setColor(ccColor3B.ccRED);
				labelError.setPosition(szWin.width/2, szWin.height/2);
				super.addChild(labelError, 1);
				finished=true;
			}
			else {
			    super.playBackgroundMusic("audio/music/intro.mp3");
			    this.setupInitializationProgress();
		
			    flattedCategories=new java.util.ArrayList<Category>();
			    activities=new java.util.ArrayList<Category>();
			    
			    for (Category one : YDConfiguration.sharedConfiguration().getCategories()) {
			        this.flatAllCategories(one);
			    }
			    SysHelper.setTotalActivities(activities.size());
		
			    String description=String.format(super.localizedString("label_total_activities"), activities.size());
				CCLabel info = CCLabel.makeLabel(description,super.sysFontName(), super.smallFontSize());
			    info.setPosition(CGPoint.ccp(szWin.width-info.getContentSize().width/2-10, super.bottomOverhead() +info.getContentSize().height/2));
				super.addChild(info,1);

			    //button: home, help, level up, level down, delimiter, ok
			    totalThumbnails=flattedCategories.size()+activities.size()+Schema.kSvgTotalButtons_;
			    finishedThumbnails=0;
			    idxCategory=idxActivity=idxButton=0;
			    
				finished=false;
			}
			launched=true;
		}
		else if (!finished) {
		    //still generating thumbnails for the category
		    if (idxCategory < flattedCategories.size()) {
		        Category one=flattedCategories.get(idxCategory);
		        //NSLog(@"Processing %@ ", [one icon]);
	
	            super.renderSVG2Img(one.getIcon(), super.categoryIconSize(),super.categoryIconSize());
	            //a button image which may be displayed on the top navigation bar
	            super.renderSVG2Img(one.getIcon(),super.buttonSize(),super.buttonSize());
	
		        ++idxCategory;
		    }
		    else if (idxActivity < activities.size()) {
		        Category one=activities.get(idxActivity);
		        String bgImgFile=one.getBg();
		        if (bgImgFile.endsWith(".svg") && !bgImgFile.endsWith("xxx.svg")) {
	                super.renderSVG2Img(bgImgFile, (int)szWin.width, (int)szWin.height);
		        }
		        ++idxActivity;
		    }
		    else {
		        int height=super.buttonSize();
		        if (idxButton < Schema.kSvgTotalButtons_) {
		            if (idxButton >= Schema.kDifficulty1 && idxButton <= Schema.kDifficulty6) {
		                height=Schema.kDifficultyIndicatorSize_;
		            }
	                super.renderSkinSVG2Button(idxButton, height);
	                if (idxButton == Schema.kSvgButtonOk)
	                    super.renderSkinSVG2Button(idxButton,super.buttonSize()*2);
	                else if (idxButton == Schema.kSvgButtonDelimiter) {
	                    super.renderSkinSVG2Button(idxButton,Schema.kDifficultyIndicatorSize_);
	                }
		        }
		        else {
		        	finished=true;
		            flattedCategories.clear();
		            activities.clear();
		            this.startNow();
		        }
		        ++idxButton;
		    }
	
		    ++finishedThumbnails;
		    mProgress.setPercentage(finishedThumbnails*100/totalThumbnails);
		}
	}

	private void startNow() {
		//The CategoryLayer will all textures in its restart method. This will cause any transition not work properly.
		CCDirector.sharedDirector().replaceScene(CategoryLayer.scene());
	}

	private void flatAllCategories(Category  parent) {
	    for (Category one : parent.getSubCategories()) {
	        flattedCategories.add(one);
	        this.flatAllCategories(one);
	    }
	    if (parent.isActivity()) {
	        activities.add(parent);
	    }
	}

	//Display the initialization progress bar
	private void setupInitializationProgress() {
	    CCSprite mBgProgress=spriteFromExpansionFile("image/misc/fullbarborder.png");
	    float scale=szWin.width * 0.8f / mBgProgress.getContentSize().width;
	    mBgProgress.setPosition(CGPoint.ccp(szWin.width/2, szWin.height/2));
	    mBgProgress.setScale(scale);
	    super.addChild(mBgProgress,1);
	    
        CCTexture2D texture = textureFromExpansionFile("image/misc/fullbar.png");
	    mProgress= CCProgressTimer.progress(texture);
	    mProgress.setType(CCProgressTimer.kCCProgressTimerTypeHorizontalBarLR);
	    //mProgress.setmidpoint = ccp(0,0); // starts from left
	    //mProgress.barChangeRate = ccp(1,0); // grow only in the "x"-horizontal direction
	    mProgress.setPercentage(0);
	    mProgress.setPosition(CGPoint.ccp(szWin.width/2, szWin.height/2));
	    mProgress.setScale(scale);
	    super.addChild(mProgress,2);
	    
	    String fontName=super.sysFontName();
	    int fontSize=smallFontSize();
		CCLabel mMsgInitializing = CCLabel.makeLabel(super.localizedString("msg_initializing"),fontName,fontSize);
	    mMsgInitializing.setPosition(CGPoint.ccp(szWin.width/2, szWin.height/2+mBgProgress.getContentSize().height+mMsgInitializing.getContentSize().height*1.2f));
	    
		super.addChild(mMsgInitializing,1);
	}
}
