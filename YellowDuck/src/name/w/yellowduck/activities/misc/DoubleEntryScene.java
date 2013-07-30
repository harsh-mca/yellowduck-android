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


package name.w.yellowduck.activities.misc;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.types.CGPoint;

public class DoubleEntryScene extends ShapeGameBase {
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new DoubleEntryScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=3;
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.preGameInit("image/activities/discovery/miscelaneous/doubleentry/doubleentry-bg.png");
	    java.util.ArrayList<String> items=new java.util.ArrayList<String>();
	    java.util.ArrayList<String> labels=new java.util.ArrayList<String>();
	    java.util.ArrayList<String> voices=new java.util.ArrayList<String>();
	    switch (mLevel) {
	        case 1:
	            labels.add("[0,1]image/activities/discovery/miscelaneous/doubleentry/d-entry_pomme_1.png");
	            labels.add("[0,2]image/activities/discovery/miscelaneous/doubleentry/d-entry_galette_1.png");
	            labels.add("[0,3]image/activities/discovery/miscelaneous/doubleentry/d-entry_banane_1.png");
	            labels.add("[1,0]1");
	            labels.add("[2,0]2");
	            labels.add("[3,0]3");
	            
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_pomme_1.png");
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_pomme_2.png");
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_pomme_3.png");
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_galette_1.png");
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_galette_2.png");
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_galette_3.png");
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_banane_1.png");
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_banane_2.png");
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_banane_3.png");
	            break;
	        case 2:
	            labels.add("[0,1]image/activities/discovery/miscelaneous/doubleentry/circle-r2-g0.png");
	            labels.add("[0,2]image/activities/discovery/miscelaneous/doubleentry/circle-r3-g0.png");
	            labels.add("[0,3]image/activities/discovery/miscelaneous/doubleentry/circle-r4-g0.png");
	            labels.add("[1,0]image/activities/discovery/miscelaneous/doubleentry/circle-r0-g2.png");
	            labels.add("[2,0]image/activities/discovery/miscelaneous/doubleentry/circle-r0-g3.png");
	            labels.add("[3,0]image/activities/discovery/miscelaneous/doubleentry/circle-r0-g4.png");
	            
	            items.add("image/activities/discovery/miscelaneous/doubleentry/circle-r2-g2.png");
	            items.add("image/activities/discovery/miscelaneous/doubleentry/circle-r2-g3.png");
	            items.add("image/activities/discovery/miscelaneous/doubleentry/circle-r2-g4.png");
	            
	            items.add("image/activities/discovery/miscelaneous/doubleentry/circle-r3-g2.png");
	            items.add("image/activities/discovery/miscelaneous/doubleentry/circle-r3-g3.png");
	            items.add("image/activities/discovery/miscelaneous/doubleentry/circle-r3-g4.png");
	            
	            items.add("image/activities/discovery/miscelaneous/doubleentry/circle-r4-g2.png");
	            items.add("image/activities/discovery/miscelaneous/doubleentry/circle-r4-g3.png");
	            items.add("image/activities/discovery/miscelaneous/doubleentry/circle-r4-g4.png");
	            break;
	        case 3:
	            labels.add("[0,1]A");
	            labels.add("[0,2]B");
	            labels.add("[0,3]C");
	            labels.add("[1,0]1");
	            labels.add("[2,0]2");
	            labels.add("[3,0]3");
	            
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_banane_1.png");//a1
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_banane_2.png");//a2
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_banane_3.png");//a3
	            
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_pomme_1.png");//b1
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_pomme_2.png");//b2
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_pomme_3.png");//b3
	            
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_galette_1.png");//c1
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_galette_2.png");//c2
	            items.add("image/activities/discovery/miscelaneous/doubleentry/d-entry_galette_3.png");//c3
	            
	            voices.add("alphabet/U0061.mp3");
	            voices.add("alphabet/U0031.mp3");
	            voices.add("alphabet/U0061.mp3");
	            voices.add("alphabet/U0032.mp3");
	            voices.add("alphabet/U0061.mp3");
	            voices.add("alphabet/U0033.mp3");
	
	            voices.add("alphabet/U0062.mp3");
	            voices.add("alphabet/U0031.mp3");
	            voices.add("alphabet/U0062.mp3");
	            voices.add("alphabet/U0032.mp3");
	            voices.add("alphabet/U0062.mp3");
	            voices.add("alphabet/U0033.mp3");
	            
	            voices.add("alphabet/U0063.mp3");
	            voices.add("alphabet/U0031.mp3");
	            voices.add("alphabet/U0063.mp3");
	            voices.add("alphabet/U0032.mp3");
	            voices.add("alphabet/U0063.mp3");
	            voices.add("alphabet/U0033.mp3");
	            
	            break;
	    }
	    for (int i = 0; i < items.size(); ++i) {
	        int row=i/3, col=i%3;
	        CGPoint center=this.cellPositionAt(row+1, col+1);
	        
	        YDShape shape=new YDShape(items.get(i), YDShape.kShapeStock);
	        shape.setPosition(center);
	        if (voices.size() > 0) {
	            shape.setVoice(voices.get(i*2));
	            shape.setVoice2(voices.get(i*2+1));
	        }
	        super.addShape(shape);
	    }
	    
	    for (int i = 0; i < labels.size(); ++i) {
	    	String components[]=labels.get(i).split("]");
	        String coord=components[0].substring(1);
	        String resource=components[1];
	        String xy[]=coord.split(",");
	        int x=Integer.parseInt(xy[0]);
	        int y=Integer.parseInt(xy[1]);
	
	        if (resource.startsWith("image")) {
	            YDShape label=new YDShape(resource, YDShape.kShapeLabelImage);
	            label.setPosition(this.cellPositionAt(y, x));
	            super.addShape(label);
	        }
	        else {
	            YDShape label=new YDShape(resource, YDShape.kShapeLabelText);
	            label.setPosition(this.cellPositionAt(y, x));
	            super.addShape(label);
	        }
	    }
	    super.postGameInit(true, false);
	}
	
	private CGPoint cellPositionAt(int y, int x) {
	    float w=backgroundSprite.getContentSize().width * backgroundSprite.getScale();
	    float h=backgroundSprite.getContentSize().height * backgroundSprite.getScale();
	    float cellWidth=w/7, cellHeight=h/5;
	    
	    return CGPoint.ccp(backgroundSprite.getPosition().x-w/2+x*cellWidth+cellWidth/2, backgroundSprite.getPosition().y+h/2-y*cellHeight-cellHeight/2);
	}
}
