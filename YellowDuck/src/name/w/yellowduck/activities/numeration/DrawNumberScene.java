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


package name.w.yellowduck.activities.numeration;

import name.w.yellowduck.Category;
import name.w.yellowduck.EllipseSprite;
import name.w.yellowduck.LineSprite;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemLabel;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4F;

import android.graphics.Paint;


public class DrawNumberScene extends name.w.yellowduck.YDActLayerBase {
	private class DrawNumberData extends Object {
		private java.util.ArrayList<String> _coords;
		private int _level, _sublevel;
		private String picture1, picture2;
		
		private DrawNumberData(int level, int sublevel) {
			super();
			this._level=level;
			this._sublevel=sublevel;
			
			this._coords=new java.util.ArrayList<String>();
		}
		public int getLevel() {
			return this._level;
		}
		public int getSublevel() {
			return this._sublevel;
		}

		public String getPicture1() {
			return picture1;
		}
		public void setPicture1(String picture1) {
			this.picture1 = picture1;
		}
		public String getPicture2() {
			return picture2;
		}
		public void setPicture2(String picture2) {
			this.picture2 = picture2;
		}
		public void addCoord(String coord_) {
			_coords.add(coord_);
		}
		public java.util.ArrayList<String> getCoords() {
			return this._coords;
		}
	}
	
    private CCLabel promptLabel;
    private CCMenuItemLabel lastNumber;
    
    private java.util.ArrayList<DrawNumberData> data;
    private DrawNumberData currentLevelData;
    
    private int totalPoints, pointNext2Touch;
    private CGPoint ptLastTouched;

	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new DrawNumberScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	public void onEnter() {
		super.onEnter();
	    mMaxLevel=3;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
	    
	    promptLabel = CCLabel.makeLabel("10/10", super.sysFontName(), super.smallFontSize());
	    promptLabel.setColor(ccColor3B.ccBLACK);
	    promptLabel.setPosition(promptLabel.getContentSize().width/2+4, szWin.height-topOverhead()-promptLabel.getContentSize().height/2);
	    super.addChild(promptLabel,1);
	
	    //parse configuration file
	    String filename = "image/activities/math/numeration/drawnumber/activity.txt";
	    java.util.ArrayList<String> lines=loadExpansionAssetFile(filename);
	    data=new java.util.ArrayList<DrawNumberData>();
	    int _level=1, _sublevel=0;
	    for (String line : lines) {
	        String text=line.trim();
	        if (text.length() <= 0 || text.startsWith("#"))
	            continue;
	        if (text.equalsIgnoreCase("NEXT_LEVEL")) {
	            ++_level;
	            _sublevel=0;
	            continue;
	        }
	        DrawNumberData levelData=new DrawNumberData(_level, _sublevel);
	        data.add(levelData);
	        
	        String info[]=text.split(";");
	        //two pictures
	        String picture1=info[0].trim();
	        String picture2=info[1].trim();
	        levelData.setPicture1(picture1);
	        levelData.setPicture2(picture2);
	        //coordinations
	        for (int i = 2; i < info.length; ++i) {
	            String coord=info[i].trim();
	            if (coord.startsWith("[") && coord.endsWith("]")) {
	                levelData.addCoord(coord);
	            }
	        }
	        ++_sublevel;
	    }
	    mMaxLevel=_level;
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
	    
	    int _maxsublevel=0;
	    currentLevelData=null;
	    for (DrawNumberData levelData : data) {
	        if (levelData.getLevel() == mLevel) {
	            ++_maxsublevel;
	            if (levelData.getSublevel() == mSublevel) {
	                currentLevelData=levelData;
	            }
	        }
	    }
	    mMaxSublevel=_maxsublevel;
	    CCSprite bg=setupBackground("image/activities/math/numeration/"+currentLevelData.getPicture1(), kBgModeFit);
	    floatingSprites.add(bg);
	    
	    float imgWidth=bg.getContentSize().width;
	    float imgHeight=bg.getContentSize().height;
	    
	    String font=super.sysFontName();
	    int fontSize=super.mediumFontSize();
	    Paint paint=new Paint();
	    paint.setTextSize(fontSize);
	    float w=paint.measureText(" 99 ");
	    float h=paint.descent()-paint.ascent();
	    
	    float radius=(w > h) ? w : h;
	    radius = radius / 2 + 1;
	    
	    totalPoints=0; lastNumber=null;
	    java.util.ArrayList<String> numbers=currentLevelData.getCoords();
	    totalPoints=numbers.size();
	    int zOrder=1;
	    for (int i = totalPoints - 1; i >= 0; --i) {
	        String strCoord=numbers.get(i);
	        //[1,2] => 1,2
	        String bareCoord=strCoord.substring(1, strCoord.length()-1);
	        String xy[]=bareCoord.split(",");
	        int x=Integer.parseInt(xy[0]);
	        int y=Integer.parseInt(xy[1]);
	        float xPos=1.0f*x/imgWidth*szWin.width;
	        float yPos=1.0f*(imgHeight-y)/imgHeight*szWin.height;
	
	        CCMenuItemLabel label = CCMenuItemLabel.item(CCLabel.makeLabel(" "+(i+1) + " ", font, fontSize), this, "numberTouched");
	        label.setColor(ccColor3B.ccBLACK);
	        label.setTag(i+1);
	        CCMenu menu = CCMenu.menu(label);
	        menu.setPosition(xPos, yPos);
	        super.addChild(menu, zOrder+1);
	        floatingSprites.add(menu);
	        if (lastNumber == null)
	            lastNumber=label;
	
	        EllipseSprite sprite=new EllipseSprite(menu.getPosition(), radius, radius);
	        sprite.setClr(((i<=0)?new ccColor4F(0, 0, 1.0f, 1.0f):new ccColor4F(0, 1.0f, 0, 1.0f)));
	        super.addChild(sprite,zOrder);
	        floatingSprites.add(sprite);
	        
	        label.setUserData(sprite);
	
	        ++zOrder;
	    }
	    lastNumber.setVisible(false);
	    CCNode node=(CCNode)lastNumber.getUserData();
	    node.setVisible(false);
	    
	    pointNext2Touch=1;
	    
	    promptLabel.setString(String.format("%d/%d", mSublevel+1, mMaxSublevel));
	}
	
	public void numberTouched(Object _sender) {
		CCNode sender=(CCNode)_sender;

	    if (sender.getTag()==pointNext2Touch) {
	        playSound("audio/sounds/bleep.wav");
	        
	        CCNode node=(CCNode)sender.getUserData();
	        sender.getParent().removeFromParentAndCleanup(true);
	        floatingSprites.remove(sender.getParent());
	        node.removeFromParentAndCleanup(true);
	        floatingSprites.remove(sender);
	        
	        if (sender.getTag() == 1) {
	            lastNumber.setVisible(true);
	            CCNode _node=(CCNode)lastNumber.getUserData();
	            _node.setVisible(true);
	        }
	        
	        if (pointNext2Touch > 1) {
	            LineSprite sprite=new LineSprite(ptLastTouched, sender.getParent().getPosition());
	            sprite.setClr(new ccColor4F(0, 0, 0, 1.0f));
	            sprite.setLineWidth(2);
	            super.addChild(sprite, 2);
	            floatingSprites.add(sprite);
	        }
	        ptLastTouched=sender.getParent().getPosition();
	
	        if (++pointNext2Touch > totalPoints) {
	            if (!currentLevelData.getPicture1().equals(currentLevelData.getPicture2())) {
	            	super.clearFloatingSprites();
	                CCSprite bg=super.setupBackground("image/activities/math/numeration/" + currentLevelData.getPicture2(), kBgModeFit);
	                floatingSprites.add(bg);
	            }
	            super.flashAnswerWithResult(true, true, null, "image/bonus/flower_good.png", 2);
	        }
	    }
	}
}
