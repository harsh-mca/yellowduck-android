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
import name.w.yellowduck.EllipseSprite;
import name.w.yellowduck.PolygonSprite;
import name.w.yellowduck.YDActLayerBase;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.CCProgressTimer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemLabel;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor4F;

public class ColorMixingLightScene extends YDActLayerBase {
    private CCSprite torchRed, torchGreen, torchBlue;
    private CCProgressTimer redBar, blueBar, greenBar;
    private PolygonSprite target;
    private EllipseSprite ellipse;
    
    
    int r,g,b;//required color
    int rSet, gSet, bSet; //current color set
    int increment;
	
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ColorMixingLightScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	public void onEnter() { 
		super.onEnter();
		mMaxLevel=4;
	
		Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
		//change a background music
		super.shufflePlayBackgroundMusic();
		super.setupBackground(activeCategory.getBg(), kBgModeStretch);
		super.setupTitle(activeCategory);
		super.setupNavBar(activeCategory);
		super.setupSideToolbar(activeCategory,kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionOk);
		
		float rx=40, ry=rx*szWin.height/szWin.width;
		float space2flashligh=szWin.height/2- super.bottomOverhead() -ry;
		
		//for the +, - buttons
		String fontName=super.sysFontName();
		int fontSize=32;
		
		String signAdd="[+]", signMinus="[-]";
		//below
		torchGreen=spriteFromExpansionFile("image/activities/discovery/color/clr_mix_light/torch_green.png");
		float scale=space2flashligh/torchGreen.getContentSize().height;
		torchGreen.setScale(scale);
		torchGreen.setPosition(szWin.width/2, szWin.height/2-torchGreen.getContentSize().height*scale/2-ry);
		super.addChild(torchGreen,1);
		//buttons for adjust its value
		float radius=torchGreen.getContentSize().height *scale / 16;
		CGPoint ptUp=CGPoint.make(torchGreen.getPosition().x, torchGreen.getPosition().y+radius);
		
		CCMenuItemLabel label1 = CCMenuItemLabel.item(CCLabel.makeLabel(signAdd, fontName, fontSize), this, "incGreen");
		CCMenu menu1 = CCMenu.menu(label1);
		menu1.setPosition(ptUp);
		super.addChild(menu1,2);
		
		radius=torchGreen.getContentSize().height * scale * 0.4f;
		CGPoint ptDn=CGPoint.ccp(torchGreen.getPosition().x, torchGreen.getPosition().y-radius);
		
		CCMenuItemLabel label2 = CCMenuItemLabel.item(CCLabel.makeLabel(signMinus, fontName, fontSize), this, "decGreen");
		CCMenu menu2 = CCMenu.menu(label2);
		menu2.setPosition(ptDn);
		super.addChild(menu2,2);
		
		CCTexture2D bar=textureFromExpansionFile("image/misc/fullbar.png");
		greenBar= CCProgressTimer.progress(bar);
		greenBar.setType(CCProgressTimer.kCCProgressTimerTypeHorizontalBarLR);
		greenBar.setPercentage(0);
		greenBar.setRotation(-90);
		greenBar.setScale((super.distanceFrom(ptDn,ptUp) - label1.getContentSize().width/2 - label2.getContentSize().width/2) /bar.getWidth());
		greenBar.setPosition((ptUp.x+ptDn.x)/2, (ptUp.y+ptDn.y)/2);
		super.addChild(greenBar,3);
		
		CCSprite bg=spriteFromExpansionFile("image/misc/fullbarborder.png");
		bg.setPosition(greenBar.getPosition());
		bg.setScale(greenBar.getScale());
		bg.setRotation(greenBar.getRotation());
		super.addChild(bg,2);
		
		
		//left red torch
		torchRed=spriteFromExpansionFile("image/activities/discovery/color/clr_mix_light/torch_red.png");
		torchRed.setScale(scale);
		torchRed.setPosition(szWin.width/2-torchRed.getContentSize().width*scale/2-rx, szWin.height/2+torchRed.getContentSize().height*scale/2-ry);
		super.addChild(torchRed,1);
		//buttons for adjust its value
		radius=torchRed.getContentSize().width *scale / 10;
		ptUp=super.rotatePoint(CGPoint.ccp(radius, 0), -18);
		ptUp.x += torchRed.getPosition().x;
		ptUp.y += torchRed.getPosition().y;
		
		label1 = CCMenuItemLabel.item(CCLabel.makeLabel(signAdd, fontName, fontSize), this, "incRed");
		menu1 = CCMenu.menu(label1);
		menu1.setPosition(ptUp);
		super.addChild(menu1,2);
		
		radius=torchRed.getContentSize().width * 1.5f *scale / 4;
		ptDn=super.rotatePoint(CGPoint.ccp(0-radius, 0), -18);
		ptDn.x += torchRed.getPosition().x;
		ptDn.y += torchRed.getPosition().y;
		
		label2 = CCMenuItemLabel.item(CCLabel.makeLabel(signMinus,fontName,fontSize), this,"decRed");
		menu2 = CCMenu.menu(label2);
		menu2.setPosition(ptDn);
		super.addChild(menu2,2);
		
		redBar= CCProgressTimer.progress(bar);
		redBar.setType(CCProgressTimer.kCCProgressTimerTypeHorizontalBarLR);
		redBar.setPercentage(0);
		redBar.setRotation(18);
		redBar.setScale((super.distanceFrom(ptDn,ptUp) - label1.getContentSize().width/2 - label2.getContentSize().width/2) /bar.getWidth());
		redBar.setPosition((ptUp.x+ptDn.x)/2, (ptUp.y+ptDn.y)/2);
		super.addChild(redBar,3);
		bg=spriteFromExpansionFile("image/misc/fullbarborder.png");
		bg.setPosition(redBar.getPosition());
		bg.setScale(redBar.getScale());
		bg.setRotation(redBar.getRotation());
		super.addChild(bg,2);
		
		//right
		torchBlue=spriteFromExpansionFile("image/activities/discovery/color/clr_mix_light/torch_blue.png");
		torchBlue.setScale(scale);
		torchBlue.setPosition(szWin.width/2+torchBlue.getContentSize().width*scale/2+rx, szWin.height/2+torchBlue.getContentSize().height*scale/2-ry);
		super.addChild(torchBlue,1);
		radius=torchBlue.getContentSize().width *scale / 10;
		ptUp=super.rotatePoint(CGPoint.ccp(-radius, 0), 18);
		ptUp.x += torchBlue.getPosition().x;
		ptUp.y += torchBlue.getPosition().y;
		
		label1 = CCMenuItemLabel.item(CCLabel.makeLabel(signAdd,fontName,fontSize), this,"incBlue");
		menu1 = CCMenu.menu(label1);
		menu1.setPosition(ptUp);
		super.addChild(menu1,2);
		
		radius=torchBlue.getContentSize().width * 1.5f *scale / 4;
		ptDn=super.rotatePoint(CGPoint.ccp(radius, 0),18);
		ptDn.x += torchBlue.getPosition().x;
		ptDn.y += torchBlue.getPosition().y;
		
		label2 = CCMenuItemLabel.item(CCLabel.makeLabel(signMinus,fontName,fontSize), this, "decBlue");
		menu2 = CCMenu.menu(label2);
		menu2.setPosition(ptDn);
		super.addChild(menu2,2);
		
		blueBar= CCProgressTimer.progress(bar);
		blueBar.setType(CCProgressTimer.kCCProgressTimerTypeHorizontalBarLR);
		blueBar.setPercentage(0); // (0 - 100)
		blueBar.setRotation(180-18);
		blueBar.setScale((super.distanceFrom(ptDn,ptUp) - label1.getContentSize().width/2 - label2.getContentSize().width/2) /bar.getWidth());
		blueBar.setPosition((ptUp.x+ptDn.x)/2, (ptUp.y+ptDn.y)/2);
		super.addChild(blueBar,3);
		bg=spriteFromExpansionFile("image/misc/fullbarborder.png");
		bg.setPosition(blueBar.getPosition());
		bg.setScale(blueBar.getScale());
		bg.setRotation(blueBar.getRotation());
		super.addChild(bg,2);
		
		//center
		ellipse=new EllipseSprite(CGPoint.ccp(szWin.width/2, szWin.height/2), rx, ry);
		ellipse.setClr(new ccColor4F(0, 0, 0, 1.0f));
		super.addChild(ellipse,2);

		//the result
		target=new PolygonSprite(4);
		float height=ry*2;
		//center between the ellipse and the top title
		float yMargin=(szWin.height/2-super.topOverhead()-ry)/2;
		float yBottom=szWin.height/2+yMargin;
		if (yBottom + height > szWin.height - super.topOverhead()) {
		    yBottom =szWin.height - super.topOverhead() - height;
		}
		
		target.setVertix(0, CGPoint.ccp(szWin.width/2-rx, yBottom));
		target.setVertix(1, CGPoint.ccp(szWin.width/2+rx, yBottom));
		target.setVertix(2, CGPoint.ccp(szWin.width/2+rx, yBottom+height));
		target.setVertix(3, CGPoint.ccp(szWin.width/2-rx, yBottom+height));
		target.setClr(new ccColor4F(1.0f*r/255, 1.0f*g/255, 1.0f*b/255, 1.0f));
		super.addChild(target, 1);
		
		super.setIsTouchEnabled(true);
		super.afterEnter();
	}
	
	//Random color to be matched
	private void color2BeMatched() {
		increment = 255 / (mLevel * 2 + 1);
		int max=255/increment;
		r=super.nextInt(max)*increment;
		g=super.nextInt(max)*increment;
		b=super.nextInt(max)*increment;
	}
	
	//set the color to be matched
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		this.color2BeMatched();
		target.setClr(new ccColor4F(1.0f*r/255, 1.0f*g/255, 1.0f*b/255, 1.0f));
		rSet=gSet=bSet=0;
		ellipse.setClr(new ccColor4F(1.0f*rSet/255, 1.0f*gSet/255, 1.0f*bSet/255, 1.0f));
		redBar.setPercentage(0);
		greenBar.setPercentage(0);
		blueBar.setPercentage(0);
	}
	public void incRed(Object sender) {
		rSet += increment;
		if (rSet > 255)
		    rSet=255;
		redBar.setPercentage(100.0f * rSet / 255);
		ellipse.setClr(new ccColor4F(1.0f*rSet/255, 1.0f*gSet/255, 1.0f*bSet/255, 1.0f));
	}
	public void decRed(Object sender) {
		rSet -= increment;
		if (rSet < 0)
		    rSet=0;
		redBar.setPercentage(100.0f * rSet / 255);
		ellipse.setClr(new ccColor4F(1.0f*rSet/255, 1.0f*gSet/255, 1.0f*bSet/255, 1.0f));
	}
	public void incGreen(Object sender) {
		gSet += increment;
		if (gSet > 255)
		    gSet=255;
		greenBar.setPercentage(100.0f * gSet / 255);
		ellipse.setClr(new ccColor4F(1.0f*rSet/255, 1.0f*gSet/255, 1.0f*bSet/255, 1.0f));
	}
	public void decGreen(Object sender) {
		gSet -= increment;
		if (gSet < 0)
		    gSet=0;
		greenBar.setPercentage(100.0f * gSet / 255);
		ellipse.setClr(new ccColor4F(1.0f*rSet/255, 1.0f*gSet/255, 1.0f*bSet/255, 1.0f));
	}
	public void incBlue(Object sender) {
		bSet += increment;
		if (bSet > 255)
		    bSet=255;
		blueBar.setPercentage(100.0f * bSet / 255);
		ellipse.setClr(new ccColor4F(1.0f*rSet/255, 1.0f*gSet/255, 1.0f*bSet/255, 1.0f));
	}
	public void decBlue(Object sender) {
		bSet -= increment;
		if (bSet < 0)
		    bSet=0;
		blueBar.setPercentage(100.0f * bSet / 255);
		ellipse.setClr(new ccColor4F(1.0f*rSet/255, 1.0f*gSet/255, 1.0f*bSet/255, 1.0f));
	}
	
	public void ok(Object sender) {
		if (rSet == r && gSet == g && bSet ==b) {
		    super.flashAnswerWithResult(true,true, null, null, 2);
		}
		else {
		    super.flashAnswerWithResult(false, false, null, null, 2);
		    java.util.ArrayList<String> msgs=new java.util.ArrayList<String>();
		    if (rSet < r) {
		        msgs.add(String.format(super.localizedString("msg_not_enough"), super.localizedString("clr_red")));
		    }
		    else if (rSet > r) {
		        msgs.add(String.format(super.localizedString("msg_too_much"), super.localizedString("clr_red")));
		    }
		    if (gSet < g) {
		        msgs.add(String.format(super.localizedString("msg_not_enough"), super.localizedString("clr_green")));
		    }
		    else if (gSet > g) {
		        msgs.add(String.format(super.localizedString("msg_too_much"), super.localizedString("clr_green")));
		    }
		    if (bSet < b) {
		        msgs.add(String.format(super.localizedString("msg_not_enough"), super.localizedString("clr_blue")));
		    }
		    else if (bSet > b) {
		        msgs.add(String.format(super.localizedString("msg_too_much"), super.localizedString("clr_blue")));
		    }
		    StringBuffer sb=new StringBuffer();
		    boolean first=true;
		    for (String sentence : msgs) {
		        if (first) {
		        	first=false;
		        }
		        else {
		        	sb.append("\n");
		        }
	        	sb.append(sentence);
		    }
		    super.flashMsg(sb.toString(), 2);
		}
	}
}
