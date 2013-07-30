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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor3B;

import android.graphics.Bitmap;
import android.util.FloatMath;

public abstract class YDActLayerBase extends YDLayerBase {
	protected final int zPopupBackground                =1000;
	protected final int zPopupContents                  =1001;

	protected final int kFocusStatusLose                =0;
	protected final int kFocusStatusRestore             =1;

	protected final int kPopupEventHelp                 =0;
	protected final int kPopupEventDetails              =1;

	protected final int kTagKeyboard					=1111;

	private final int kTagFlashMsg            =-123;
	private final int kTagPerformSelector     =-124;
	
	protected int mLevel, mMaxLevel;
	    //sublevel starts from zero(0)
	protected int mSublevel, mMaxSublevel;
	protected java.util.ArrayList<CCNode> floatingSprites;
	private java.util.ArrayList<CCNode> popupCtrls;
	private java.util.ArrayList<CCNode> flashSprites;
	private int popupEvent;
	private int intrPlaying;
	private boolean shuttingDown;
	
	private boolean advanced2nextLevel;
	
	public YDActLayerBase() {
		super();
        floatingSprites=new java.util.ArrayList<CCNode>();
        flashSprites=new java.util.ArrayList<CCNode>();
	}
	public void onEnter() {
		super.onEnter();
	    mLevel=1; mMaxLevel=2;
	    intrPlaying=-1;
	    //by default, no sublevel
	    mSublevel=mMaxSublevel=0;
	    shuttingDown=false;
	    popupCtrls=new java.util.ArrayList<CCNode>();
	}
	
	public void afterEnter() {
		this.performSelector("____", Schema.kSceneTransitionSpeed);
	}
	
	public void onExit() {
	    if (intrPlaying >= 0) {
	        super.stopSoundOrVoice(intrPlaying);
	        intrPlaying=-1;
	    }
	    popupCtrls.clear();
	    mLevelLabel=null;
	    shuttingDown=true;
	    floatingSprites.clear();
	    
	    super.onExit();
	}
	
	protected void playIntr(Category activeCategory) {
	    if (!activeCategory.isActivity())
	        return;
	    if (intrPlaying > 0) {
	        this.stopSoundOrVoice(intrPlaying);
	        intrPlaying=-1;
	    }
	    String name=activeCategory.getIntr();
	    if (name == null || name.length() <= 0)
	        name=activeCategory.getName();
	    String file="intro/"+name+".mp3";
	    intrPlaying=super.playVoice(file);
	}
	
	//Title is hidden on all activity scenes
	@Override
	protected void setupTitle(Category category) {
	}
	
	protected void popupHelp(Category activeCategory) {
	    String content="";
	    String _goal=super.localizedString(activeCategory.getGoal());
	    if (_goal!=null && !_goal.equalsIgnoreCase(activeCategory.getGoal()) && _goal.length() > 0) {
	        content="*"+_goal+"\n";
	    }
	    String _manual=super.localizedString(activeCategory.getManual());
	    if (_manual!=null && !_manual.equalsIgnoreCase(activeCategory.getManual()) && _manual.length() > 0) {
	        content += _manual;
	    }
	    if (content.length() <= 0) {
	        //no goal, no manual, try description
	        String _description=super.localizedString(activeCategory.getDescription());
	        if (_description!=null && !_description.equalsIgnoreCase(activeCategory.getDescription())) {
	            content=_description;
	        }
	    }
	    if (content.length() <= 0)
	        return;
	    java.util.ArrayList<CCNode> ctrls=new java.util.ArrayList<CCNode>();
	    	    
	    Bitmap bmp=super.createMultipleLineLabel(content, super.sysFontName(), super.mediumFontSize(), szWin.height, Schema.kPopUpFontClr, Schema.kPopUpBgClr);
	    CCSprite popup=CCSprite.sprite(bmp, "popup_help");
	    popup.setPosition(szWin.width/2, szWin.height/2);
	    super.addChild(popup, zPopupContents);
	    ctrls.add(popup);
	    this.pinPopupContents2Background(ctrls, kBgModeStretch, kPopupEventHelp);
	    
	    ctrls.clear();
	}
	//
	// Display a background image to cover the full screen. The background image is pulled down when user taps anywhere on the screen.
	// Some labels, images may be displayed above this background image.
	//
	protected void pinPopupContents2Background(java.util.ArrayList<CCNode>contents, int mode, int event){
	    popupEvent=event;
	    CCSprite sprite = null;
	    if (mode == kBgModeTile) {
	    	sprite = spriteFromExpansionFile("image/misc/layermaskwhite.png");
	    	mode=kBgModeStretch;
	    	
	    	//tile mode is not supported by Android Cocos2d
	    	/*
		    sprite=spriteFromExpansionFile("image/misc/blgr.png");

		    sprite.setTextureRect(0, 0, szWin.width, szWin.height, false);
		    sprite.getTexture().setTexParameters(GL10.GL_LINEAR, GL10.GL_LINEAR, GL10.GL_REPEAT, GL10.GL_REPEAT);
		    sprite.setPosition(CGPoint.ccp(szWin.width/2 , szWin.height/2));
		    */
	    }
	    else if (mode == kBgModeStretch) {
	        sprite = spriteFromExpansionFile("image/misc/layermask.png");
	    }
	    CCMenuItemSprite cover=CCMenuItemImage.item(sprite, null, this, "_helpOk_");
	    if (mode == kBgModeStretch) {
	        float scale1=szWin.width/cover.getContentSize().width;
	        float scale2=szWin.height/cover.getContentSize().height;
	        float scale=(scale2 > scale1)?scale2:scale1;
	        cover.setScale(scale);
	    }
	    cover.setPosition(szWin.width/2,szWin.height/2);
	    CCMenu popupMaskLayer = CCMenu.menu(cover);
	    popupMaskLayer.setPosition(0,0);
	    super.addChild(popupMaskLayer,zPopupBackground);
	    this.onFocus(kFocusStatusLose, popupEvent);
	    popupCtrls.add(popupMaskLayer);
	    
	    for (CCNode node : contents) {
	        popupCtrls.add(node);
	    }
	}
	
	//Override to implement corresponding functionalities
	public void toolbarBtnTouched(Object _sender) {
	    if (_sender == null)
	        return;
	    CCNode sender=(CCNode)_sender;
	    switch (sender.getTag()) {
	        case Schema.kSvgButtonHELP:
	            super.playVoice("misc/help.mp3");
	            this.popupHelp(YDConfiguration.sharedConfiguration().getActiveCategory());
	            break;
	        case Schema.kSvgButtonSound:
	            this.playIntr(YDConfiguration.sharedConfiguration().getActiveCategory());
	            break;
	        case Schema.kSvgButtonRepeat:
	        	if (!this.repeatIf(sender))
	        		this.repeat(sender);
	            break;
	        case Schema.kSvgButtonLevelDn:
	            this.beforeLevelChange(sender);
	            this.preLevel(sender);
	            break;
	        case Schema.kSvgButtonLevelUp:
	            this.beforeLevelChange(sender);
	            this.nxtLevel(sender);
	            break;
	        case Schema.kSvgButtonOk:
	        	this.ok(sender);
	        	break;
	    }
	}
	//methods response to touch event are all defined as public void name(Object sender)
	public void ok(Object _sender){
	}
	
	//update level label, release unused resources
	protected void initGame(boolean firstTime, Object sender) {
		if (mLevelLabel != null)
			mLevelLabel.setString(""+mLevel);
	}
	
	//@return true if the event is consumed
	public boolean repeatIf(Object sender) {
		return false;
	}
	
	//@return true if the event is consumed
	public void repeat(Object sender) {
	}
	
	protected void beforeLevelChange(Object sender) {
	}
	
	protected boolean isShuttingDown() {
	    return shuttingDown;
	}
	
	private void preLevel(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    int _current=mLevel;
	    advanced2nextLevel=false;
	    if (mMaxSublevel <= 0 || (--mSublevel < 0) || (sender!=null && sender.getTag()==Schema.kSvgButtonLevelDn)) {
	        mSublevel=0;
	        if (--mLevel < 1) {
	            super.playSound("audio/sounds/ding.wav");
	            mLevel=mMaxLevel;
	        }
	    }
	    //do not play the voice if only the sublevel changed
	    if (_current != mLevel) {
	        super.playVoice("misc/level.mp3");
	        advanced2nextLevel=true;
	    }
	    this.performSelector("__newLevel", 0.8f);
	}
	public void nxtLevel(Object _sender) {
	    if (shuttingDown)
	        return;
		CCNode sender=(CCNode)_sender;
	    int _current=mLevel;
	    advanced2nextLevel=false;
	    if (mMaxSublevel <= 0 || (++mSublevel >= mMaxSublevel) || (sender!=null && sender.getTag()==Schema.kSvgButtonLevelUp)) {
	        mSublevel=0;
	        if (++mLevel > mMaxLevel) {
	            mLevel=1;
	            super.playSound("audio/sounds/ding.wav");
	        }
	    }
	    if (_current != mLevel) {
	        super.playVoice("misc/level.mp3");
	        advanced2nextLevel=true;	        
	    }
	    this.performSelector("__newLevel", 0.8f);
	}
	
	//Jump to the new level
	public void __newLevel() {
		if (shuttingDown)
			return;
		if (advanced2nextLevel) {
	        String voice=(mLevel <= 9) ? String.format("alphabet/U%04X.mp3", 0x30+mLevel) : String.format("alphabet/%d.mp3", mLevel);
	        super.playVoice(voice);
	        
	        advanced2nextLevel=false;
	        android.os.SystemClock.sleep(800);
		}
        this.initGame(false, null);
	}
	
	//Remove the full-screen background image and contents above it
	public void _helpOk_(Object _sender) {
	    for (CCNode node : popupCtrls) {
	        node.removeFromParentAndCleanup(true);
	    }
	    popupCtrls.clear();
	    this.onFocus(kFocusStatusRestore, popupEvent);
	}
	
	//Display a sprite at the middle of screen, after given seconds remove it
	private void flashSprite(String img, float seconds, Object userObj, boolean fullScreen){
	    CCSprite sprite=spriteFromExpansionFile(img);
	    sprite.setPosition(szWin.width/2, szWin.height/2);
	    sprite.setScale(preferredContentScale(true));
	    sprite.setUserData(userObj);
	    
	    super.addChild(sprite, 100);
	    this.flash(sprite, seconds, fullScreen);
	}

	//display error message on screen for given seconds
	protected void flashMsg(String msg, float seconds) {
		this.flashMsg(msg, seconds, szWin.width * 0.3f);
	}
	protected void flashMsg(String msg, float seconds, float width) {
	    //If previous flash message is still on the screen, hide it
	    int total=flashSprites.size();
	    for (int i = total - 1; i >= 0; --i) {
	        CCNode node=flashSprites.get(i);
	        if (node.getTag()==kTagFlashMsg) {
	            node.stopAllActions();
	            this.__removeMe(node);
	        }
	    }
	    Bitmap bmp=super.createMultipleLineLabel(msg, super.sysFontName(), super.mediumFontSize(), width, Schema.kPopUpFontClr, Schema.kPopUpBgClr);
	    CCSprite sprite=CCSprite.sprite(bmp, msg);
	    sprite.setPosition(8+sprite.getContentSize().width/2, szWin.height/2);
	    sprite.setTag(kTagFlashMsg);
	    super.addChild(sprite, 100);
	    this.flash(sprite, seconds, false);
	}
	
	//Display a wrong or correct answer indicator
	protected void flashWrongAnswer(CGPoint pt, float seconds) {
	    CCLabel titleLabel = CCLabel.makeLabel("X", super.sysFontName(), super.mediumFontSize());
	    titleLabel.setColor(ccColor3B.ccRED);
	    titleLabel.setPosition(pt);
	    super.addChild(titleLabel, 5);
	    this.flash(titleLabel, seconds, false);
	}
	
	 //Flash a picture and play a voice after the user answered a question.
	protected void flashAnswerWithResult(boolean correct, boolean toNxt, String voice, String pic, float seconds) {
	    //setup a dummy menu to capture all inputs
	    CCSprite sprite=spriteFromExpansionFile("image/misc/transparent.png");
	    CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, null, this, "__ignore");
	    menuitem.setScaleX(szWin.width/menuitem.getContentSize().width);
	    menuitem.setScaleY(szWin.height/menuitem.getContentSize().height);
	    CCMenu  dummyMenu = CCMenu.menu(menuitem);
	    dummyMenu.setPosition(szWin.width/2, szWin.height/2);
	    super.addChild(dummyMenu,99);
	    //shall we file the onfocus event?
	
	    if (correct) {
	        //random select one
	        if (voice == null) {
	            int sel=super.nextInt(6);
	            switch (sel) {
	                case 0:
	                    voice="awesome.mp3";
	                    break;
	                case 1:
	                    voice="congratulation.mp3";
	                    break;
	                case 2:
	                    voice="fantastic.mp3";
	                    break;
	                case 3:
	                    voice="great.mp3";
	                    break;
	                case 4:
	                    voice="super.mp3";
	                    break;
	                case 5:
	                    voice="waytogo.mp3";
	                    break;
	            }
	        }
	        if (!voice.startsWith("misc")) {
	            voice="misc/"+voice;
	        }
	        super.playVoice(voice);
	        if (pic == null) {
	            int sel=super.nextInt(3);
	            switch (sel) {
	                case 0:
	                    pic="flower_good.png";
	                    break;
	                case 1:
	                    pic="lion_good.png";
	                    break;
	                case 2:
	                    pic="smiley_good.png";
	                    break;
	            }
	        }
	        if (!pic.startsWith("image/")) {
	            pic="image/bonus/"+pic;
	        }
	        //make sure the dummy menu is removed before advance to next level
	        this.flashSprite(pic, seconds-0.1f, dummyMenu, true);
	        if (toNxt) {
	        	this.performSelector("nxtLevel", seconds);
	        }
	    }
	    else {
	        super.playVoice("misc/check_answer.mp3");
	        if (pic == null) {
	            int sel=super.nextInt(3);
	            switch (sel) {
	                case 0:
	                    pic="flower_bad.png";
	                    break;
	                case 1:
	                    pic="lion_bad.png";
	                    break;
	                case 2:
	                    pic="smiley_bad.png";
	                    break;
	            }
	        }
	        if (!pic.startsWith("image/")) {
	            pic="image/bonus/" +  pic;
	        }
	        this.flashSprite(pic, seconds, dummyMenu, true);
	    }
	}
	//the method (selector) must be public
	protected void performSelector(String selector, float delay) {
		CCNode node=super.getChildByTag(kTagPerformSelector);
		if (node == null) {
	        //child class should implement this method
	    	CCSprite dummy=spriteFromExpansionFile("image/misc/transparent.png");
	    	dummy.setPosition(szWin.width/2, szWin.height/2);
	    	dummy.setTag(kTagPerformSelector);
	    	super.addChild(dummy);
	    	floatingSprites.add(dummy);

	    	node=dummy;
		}
		else {
			node.stopAllActions();
		}
		node.setUserData(selector);
    	CCDelayTime idleAction = CCDelayTime.action(delay);
    	CCCallFuncN doneAction = CCCallFuncN.action(this, "__delayAction");
    	node.runAction(CCSequence.actions(idleAction, doneAction));
	}
	
	//Display a coco2d node for given time period
	private void flash(CCNode node, float seconds, boolean fullscreen){
	    flashSprites.add(node);
    	CCDelayTime idleAction = CCDelayTime.action(seconds);
    	CCCallFuncN doneAction = CCCallFuncN.action(this, "__removeMe");
        node.runAction(CCSequence.actions(idleAction, doneAction));
	}
	
	public void __removeMe(Object _sender) {
		CCNode sender=(CCNode)_sender;
		CCNode additional=null;
		if (sender.getUserData()!=null && (sender.getUserData() instanceof CCNode)) {
			additional=(CCNode)sender.getUserData();
		}
	    sender.setUserData(null);
	    sender.removeFromParentAndCleanup(true);
	    flashSprites.remove(sender);
	    if (additional!=null) {
	        additional.removeFromParentAndCleanup(true);
	    }
	}

	public void __delayAction(Object _sender) {
		if (shuttingDown)
			return;
		CCNode sender=(CCNode)_sender;
		String selector=(String)sender.getUserData();
		this.__removeMe(_sender);
		floatingSprites.remove(sender);
		
		if ("____".equalsIgnoreCase(selector)) {
			this.initGame(true, null);
		}
		else {
			boolean called=false;
	        try {
	            Method invocation = this.getClass().getMethod(selector);
	            invocation.invoke(this, new Object[0]);//no parameters
	            called=true;
			}
	        catch (NoSuchMethodException e) {
			}
	        catch (NullPointerException e) {
			}
	        catch (IllegalAccessException e) {
			}
	        catch (IllegalArgumentException e) {
			}
	        catch (InvocationTargetException e) {
	        	e.getCause().printStackTrace();
			}
	        if (!called) {//try method with one parameter(Object)
	            try {
	            	Class<?> paramtype[]=new Class[1];
	            	paramtype[0]=Object.class;
	                Method invocation = this.getClass().getMethod(selector, paramtype);
	                invocation.invoke(this, new Object[]{null});//no parameters
	                called=true;
	    		} 
		        catch (NoSuchMethodException e) {
				}
		        catch (NullPointerException e) {
				}
		        catch (IllegalAccessException e) {
				}
		        catch (IllegalArgumentException e) {
				}
		        catch (InvocationTargetException e) {
		        	e.getCause().printStackTrace();
				}
	        }
		}
	}

	public void __ignore(Object _sender){    
	}
	
	//User tapped on the category icon on the top navigation tool.
	protected void nav2(Category category) {
	    super.nav2(category);
	    if (category == null ||  !category.isActivity())
	        CCDirector.sharedDirector().replaceScene(CategoryLayer.scene());
	}
	protected int distanceFrom(CGPoint p1, CGPoint p2) {
	    float xDist = (p2.x - p1.x);
	    float yDist = (p2.y - p1.y);
	    return (int)(FloatMath.sqrt((xDist * xDist) + (yDist * yDist)));
	}
	
	protected CGPoint rotatePoint(CGPoint pt, float angle) {
	    float PI = 3.14159265f;
	    float rad_angle = angle*PI/180.0f;
	    
	    float x = pt.x*FloatMath.cos(rad_angle) - pt.y*FloatMath.sin(rad_angle);
	    float y = pt.y*FloatMath.cos(rad_angle) + pt.x*FloatMath.sin(rad_angle);
	    
	    return CGPoint.ccp(x, y);
	}
	protected void randomIt(int[] data, int total) {
	    for (int i = 0; i < total; ++i) {
	        int first=super.nextInt(total);
	        int second=super.nextInt(total);
	        if (first != second) {
	            int tmp=data[first];
	            data[first]=data[second];
	            data[second]=tmp;
	        }
	    }
	}
	protected int randomBetween(int x0, int x1) {
	    return x0 + (super.nextInt(x1 - x0 + 1));
	}
	
	protected void onFocus(int focusStatus, int event) {
	}
	
	protected boolean isNodeHit(CCNode node, CGPoint pt) {
        float w=node.getContentSize().width * node.getScaleX();
        float h=node.getContentSize().height * node.getScaleY();
        CGRect rc=CGRect.make(node.getPosition().x-w/2, node.getPosition().y-h/2, w, h);
        return rc.contains(pt.x,  pt.y);
	}
	
	protected void clearFloatingSprites() {
		for (CCNode node : floatingSprites) {
			node.removeFromParentAndCleanup(true);
		}
		floatingSprites.clear();
	}
	
	protected float setupVirtualKeyboard(String keyboardKeys, java.util.ArrayList<CCNode> collected) {
	    String allKeys[]= keyboardKeys.split("/");
	    int maxKeysLine=0;
	    for (int i = 0; i < allKeys.length; ++i) {
	        if (allKeys[i].length()  > maxKeysLine)
	            maxKeysLine=allKeys[i].length();
	    }
	    float keyWidth=szWin.width / (maxKeysLine + 3);
	    float yPos=bottomOverhead()+2;
	    String imgKeyBg="image/activities/reading/hangman/tile.png";
	    String imgKeyPressed=buttonize(imgKeyBg);
	    CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(imgKeyPressed);
	    for (int m = 0; m < allKeys.length; ++m) {
	    	String keys=allKeys[m];
	        float margin=(szWin.width - keys.length() * keyWidth)/2;
	        for (int i = 0; i < keys.length(); ++i) {
	            String theKey=keys.substring(i, i+1);
	
	            CCSprite sprite=spriteFromExpansionFile(imgKeyBg);
	            CCSprite spriteSelected=CCSprite.sprite(texture);
	            CCMenuItemSprite letter=CCMenuItemImage.item(sprite, spriteSelected, this, "letterTouched");
	            letter.setPosition(margin + i * keyWidth + keyWidth/2, yPos + keyWidth / 2);
	            letter.setScale(keyWidth / sprite.getContentSize().width * 0.9f);
	            letter.setUserData(theKey);
	            letter.setTag(kTagKeyboard);
	            CCMenu menu = CCMenu.menu(letter);
	            menu.setPosition(0,0);
	            super.addChild(menu,1); //just above the background
	            if (collected!=null)
	            	collected.add(letter);
	            if ("*".equalsIgnoreCase(theKey))
	            	theKey=super.localizedString("btn_ok");
	            CCLabel titleLabel = CCLabel.makeLabel(theKey, super.sysFontName(), super.mediumFontSize());
	            titleLabel.setColor(ccColor3B.ccBLACK);
	            titleLabel.setPosition(letter.getPosition());
	            titleLabel.setTag(kTagKeyboard);
	            if (titleLabel.getContentSize().width > keyWidth)
	                titleLabel.setScale(keyWidth/titleLabel.getContentSize().width*0.9f);
	            super.addChild(titleLabel,2);
	            if (collected!=null)
	            	collected.add(titleLabel);
	            
	        }
	        yPos += keyWidth;
	    }
	    return keyWidth;
	}
}
