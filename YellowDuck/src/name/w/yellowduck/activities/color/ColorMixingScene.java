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

public class ColorMixingScene extends YDActLayerBase {
    private CCSprite torchC, torchM, torchY;
    private CCProgressTimer cBar, mBar, yBar;
    private PolygonSprite target;
    private EllipseSprite ellipse;
    
    
    int c,m,y;//required color
    int cSet, mSet, ySet; //current color set
    int increment;
	
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ColorMixingScene();
	 
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
		torchM=spriteFromExpansionFile("image/activities/discovery/color/clr_mix_light/magenta_tube.png");
		float scale=space2flashligh/torchM.getContentSize().height;
		torchM.setScale(scale);
		torchM.setPosition(szWin.width/2, szWin.height/2-torchM.getContentSize().height*scale/2-ry-8);
		super.addChild(torchM,1);
		//buttons for adjust its value
		float radius=torchM.getContentSize().height *scale / 16;
		CGPoint ptUp=CGPoint.make(torchM.getPosition().x, torchM.getPosition().y+radius);
		
		CCMenuItemLabel label1 = CCMenuItemLabel.item(CCLabel.makeLabel(signAdd, fontName, fontSize), this, "incM");
		CCMenu menu1 = CCMenu.menu(label1);
		menu1.setPosition(ptUp);
		super.addChild(menu1,2);
		
		radius=torchM.getContentSize().height * scale * 0.4f;
		CGPoint ptDn=CGPoint.ccp(torchM.getPosition().x, torchM.getPosition().y-radius);
		
		CCMenuItemLabel label2 = CCMenuItemLabel.item(CCLabel.makeLabel(signMinus, fontName, fontSize), this, "decM");
		CCMenu menu2 = CCMenu.menu(label2);
		menu2.setPosition(ptDn);
		super.addChild(menu2,2);
		
		CCTexture2D bar=textureFromExpansionFile("image/misc/fullbar.png");
		mBar= CCProgressTimer.progress(bar);
		mBar.setType(CCProgressTimer.kCCProgressTimerTypeHorizontalBarLR);
		mBar.setPercentage(0);
		mBar.setRotation(-90);
		mBar.setScale((super.distanceFrom(ptDn,ptUp) - label1.getContentSize().width/2 - label2.getContentSize().width/2) /bar.getWidth());
		mBar.setPosition((ptUp.x+ptDn.x)/2, (ptUp.y+ptDn.y)/2);
		super.addChild(mBar,3);
		
		CCSprite bg=spriteFromExpansionFile("image/misc/fullbarborder.png");
		bg.setPosition(mBar.getPosition());
		bg.setScale(mBar.getScale());
		bg.setRotation(mBar.getRotation());
		super.addChild(bg,2);
		
		
		//left red torch
		torchC=spriteFromExpansionFile("image/activities/discovery/color/clr_mix_light/cyan_tube.png");
		torchC.setScale(scale);
		torchC.setPosition(szWin.width/2-torchC.getContentSize().width*scale/2-rx, szWin.height/2+torchC.getContentSize().height*scale/2-ry);
		super.addChild(torchC,1);
		//buttons for adjust its value
		radius=torchC.getContentSize().width *scale / 10;
		ptUp=super.rotatePoint(CGPoint.ccp(radius, 0), -18);
		ptUp.x += torchC.getPosition().x;
		ptUp.y += torchC.getPosition().y;
		
		label1 = CCMenuItemLabel.item(CCLabel.makeLabel(signAdd, fontName, fontSize), this, "incC");
		menu1 = CCMenu.menu(label1);
		menu1.setPosition(ptUp);
		super.addChild(menu1,2);
		
		radius=torchC.getContentSize().width * 1.5f *scale / 4;
		ptDn=super.rotatePoint(CGPoint.ccp(0-radius, 0), -18);
		ptDn.x += torchC.getPosition().x;
		ptDn.y += torchC.getPosition().y;
		
		label2 = CCMenuItemLabel.item(CCLabel.makeLabel(signMinus,fontName,fontSize), this,"decC");
		menu2 = CCMenu.menu(label2);
		menu2.setPosition(ptDn);
		super.addChild(menu2,2);
		
		cBar= CCProgressTimer.progress(bar);
		cBar.setType(CCProgressTimer.kCCProgressTimerTypeHorizontalBarLR);
		cBar.setPercentage(0);
		cBar.setRotation(18);
		cBar.setScale((super.distanceFrom(ptDn,ptUp) - label1.getContentSize().width/2 - label2.getContentSize().width/2) /bar.getWidth());
		cBar.setPosition((ptUp.x+ptDn.x)/2, (ptUp.y+ptDn.y)/2);
		super.addChild(cBar,3);
		bg=spriteFromExpansionFile("image/misc/fullbarborder.png");
		bg.setPosition(cBar.getPosition());
		bg.setScale(cBar.getScale());
		bg.setRotation(cBar.getRotation());
		super.addChild(bg,2);
		
		//right
		torchY=spriteFromExpansionFile("image/activities/discovery/color/clr_mix_light/yellow_tube.png");
		torchY.setScale(scale);
		torchY.setPosition(szWin.width/2+torchY.getContentSize().width*scale/2+rx, szWin.height/2+torchY.getContentSize().height*scale/2-ry);
		super.addChild(torchY,1);
		radius=torchY.getContentSize().width *scale / 10;
		ptUp=super.rotatePoint(CGPoint.ccp(-radius, 0), 18);
		ptUp.x += torchY.getPosition().x;
		ptUp.y += torchY.getPosition().y;
		
		label1 = CCMenuItemLabel.item(CCLabel.makeLabel(signAdd,fontName,fontSize), this,"incY");
		menu1 = CCMenu.menu(label1);
		menu1.setPosition(ptUp);
		super.addChild(menu1,2);
		
		radius=torchY.getContentSize().width * 1.5f *scale / 4;
		ptDn=super.rotatePoint(CGPoint.ccp(radius, 0),18);
		ptDn.x += torchY.getPosition().x;
		ptDn.y += torchY.getPosition().y;
		
		label2 = CCMenuItemLabel.item(CCLabel.makeLabel(signMinus,fontName,fontSize), this, "decY");
		menu2 = CCMenu.menu(label2);
		menu2.setPosition(ptDn);
		super.addChild(menu2,2);
		
		yBar= CCProgressTimer.progress(bar);
		yBar.setType(CCProgressTimer.kCCProgressTimerTypeHorizontalBarLR);
		yBar.setPercentage(0); // (0 - 100)
		yBar.setRotation(180-18);
		yBar.setScale((super.distanceFrom(ptDn,ptUp) - label1.getContentSize().width/2 - label2.getContentSize().width/2) /bar.getWidth());
		yBar.setPosition((ptUp.x+ptDn.x)/2, (ptUp.y+ptDn.y)/2);
		super.addChild(yBar,3);
		bg=spriteFromExpansionFile("image/misc/fullbarborder.png");
		bg.setPosition(yBar.getPosition());
		bg.setScale(yBar.getScale());
		bg.setRotation(yBar.getRotation());
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
		target.setClr(this.CMYKtoRGB(c, m, y));
		super.addChild(target, 1);
		
		super.setIsTouchEnabled(true);
		super.afterEnter();
	}
	
	//Random color to be matched
	private void color2BeMatched() {
		increment = 255 / (mLevel * 2 + 1);
		int max=255/increment;
		c=super.nextInt(max)*increment;
		m=super.nextInt(max)*increment;
		y=super.nextInt(max)*increment;
	}
	
	//set the color to be matched
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		this.color2BeMatched();
		target.setClr(this.CMYKtoRGB(c, m, y));
		cSet=mSet=ySet=0;
		ellipse.setClr(this.CMYKtoRGB(cSet, mSet, ySet));
		cBar.setPercentage(0);
		mBar.setPercentage(0);
		yBar.setPercentage(0);
	}
	public void incC(Object sender) {
		cSet += increment;
		if (cSet > 255)
			cSet=255;
		cBar.setPercentage(100.0f * cSet / 255);
		ellipse.setClr(this.CMYKtoRGB(cSet, mSet, ySet));
	}
	public void decC(Object sender) {
		cSet -= increment;
		if (cSet < 0)
			cSet=0;
		cBar.setPercentage(100.0f * cSet / 255);
		ellipse.setClr(this.CMYKtoRGB(cSet, mSet, ySet));
	}
	public void incM(Object sender) {
		mSet += increment;
		if (mSet > 255)
		    mSet=255;
		mBar.setPercentage(100.0f * mSet / 255);
		ellipse.setClr(this.CMYKtoRGB(cSet, mSet, ySet));
	}
	public void decM(Object sender) {
		mSet -= increment;
		if (mSet < 0)
		    mSet=0;
		mBar.setPercentage(100.0f * mSet / 255);
		ellipse.setClr(this.CMYKtoRGB(cSet, mSet, ySet));
	}
	public void incY(Object sender) {
		ySet += increment;
		if (ySet > 255)
		    ySet=255;
		yBar.setPercentage(100.0f * ySet / 255);
		ellipse.setClr(this.CMYKtoRGB(cSet, mSet, ySet));
	}
	public void decY(Object sender) {
		ySet -= increment;
		if (ySet < 0)
		    ySet=0;
		yBar.setPercentage(100.0f * ySet / 255);
		ellipse.setClr(this.CMYKtoRGB(cSet, mSet, ySet));
	}
	
	public void ok(Object sender) {
		if (cSet == c && mSet == m && ySet ==y) {
		    super.flashAnswerWithResult(true,true, null, null, 2);
		}
		else {
		    super.flashAnswerWithResult(false, false, null, null, 2);
		    java.util.ArrayList<String> msgs=new java.util.ArrayList<String>();
		    if (cSet < c) {
		        msgs.add(String.format(super.localizedString("msg_not_enough"), super.localizedString("clr_cyan")));
		    }
		    else if (cSet > c) {
		        msgs.add(String.format(super.localizedString("msg_too_much"), super.localizedString("clr_cyan")));
		    }
		    if (mSet < m) {
		        msgs.add(String.format(super.localizedString("msg_not_enough"), super.localizedString("clr_magenta")));
		    }
		    else if (mSet > m) {
		        msgs.add(String.format(super.localizedString("msg_too_much"), super.localizedString("clr_magenta")));
		    }
		    if (ySet < y) {
		        msgs.add(String.format(super.localizedString("msg_not_enough"), super.localizedString("clr_yellow")));
		    }
		    else if (ySet > y) {
		        msgs.add(String.format(super.localizedString("msg_too_much"), super.localizedString("clr_yellow")));
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
	private ccColor4F CMYKtoRGB(int _c, int _m, int _y) {
	    float color[]=new float[4];
	    color[0]=1.0f * _c / 255;
	    color[1]=1.0f * _m / 255;
	    color[2]=1.0f * _y / 255;
	    color[3]=0;
	    
	    float rgbColor[]=new float[4];
		rgbColor[0] = 1.0f - (color[0] * (1.0f - color[3]) + color[3]);
		rgbColor[1] = 1.0f - (color[1] * (1.0f - color[3]) + color[3]);
		rgbColor[2] = 1.0f - (color[2] * (1.0f - color[3]) + color[3]);
		rgbColor[3] = 1.0f;
	    
	    return new ccColor4F(rgbColor[0], rgbColor[1], rgbColor[2], rgbColor[3]);
	}
	
}
