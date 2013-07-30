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

import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public class BabyshapesScene extends ShapeGameBase {
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new BabyshapesScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=8;
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);

    
	    java.util.ArrayList<String> items=new java.util.ArrayList<String>();
	    String bg=null;
	    
	    switch (mLevel) {
	        case 1:
	            items.add("food/marmelade.png");
	            items.add("T_marmelade.png");
	            
	            items.add("food/orange.png");
	            items.add("T_orange.png");
	            
	            items.add("food/butter.png");
	            items.add("T_butter.png");
	            
	            items.add("food/chocolate.png");
	            items.add("T_chocolate.png");
	            
	            items.add("food/cookie.png");
	            items.add("T_cookie.png");
	            
	            items.add("food/baby_bottle.png");
	            items.add("T_baby_bottle.png");
	            break;
	        case 2:
	            items.add("food/banana.png");
	            items.add("T_banana.png");
	            
	            items.add("food/suggar_box.png");
	            items.add("T_suggar_box.png");
	            
	            items.add("food/french_croissant.png");
	            items.add("T_french_croissant.png");
	            
	            items.add("food/bread_slice.png");
	            items.add("T_bread_slice.png");
	            
	            items.add("food/baby_bottle.png");
	            items.add("T_baby_bottle.png");
	            
	            items.add("food/yahourt.png");
	            items.add("T_yahourt.png");
	            break;
	        case 3:
	            items.add("food/milk_shake.png");
	            items.add("T_milk_shake.png");
	            
	            items.add("food/pear.png");
	            items.add("T_pear.png");
	            
	            items.add("food/grapefruit.png");
	            items.add("T_grapefruit.png");
	            
	            items.add("food/yahourt.png");
	            items.add("T_yahourt.png");
	            
	            items.add("food/milk_cup.png");
	            items.add("T_milk_cup.png");
	            
	            items.add("food/chocolate_cake.png");
	            items.add("T_chocolate_cake.png");
	            break;
	        case 4:
	            items.add("food/round_cookie.png");
	            items.add("T_round_cookie.png");
	            
	            items.add("food/pear.png");
	            items.add("T_pear.png");
	            
	            items.add("food/banana.png");
	            items.add("T_banana.png");
	            
	            items.add("food/yahourt.png");
	            items.add("T_yahourt.png");
	            
	            items.add("food/milk_cup.png");
	            items.add("T_milk_cup.png");
	            
	            items.add("food/baby_bottle.png");
	            items.add("T_baby_bottle.png");
	            break;
	    }
	    if (mLevel < 5) {
	        super.preGameInit((bg!=null)?String.format("image/activities/discovery/miscelaneous/babyshapes/%s", bg):null);
	        
	        int rows=2;
	        float rightItemWidth=(szWin.width - separator) / (items.size() / 2 / rows);
	        float rightItemHeight=(szWin.height - super.topOverhead() - super.bottomOverhead()) / 4;
	        int _row=0, _col=0;
	        for (int i = 1; i < items.size(); i+= 2) {
	            float xPos=separator + rightItemWidth * _col;
	            float yPos=szWin.height - rightItemHeight * (_row+1);
	            
	            YDShape shape=new YDShape("image/activities/discovery/miscelaneous/babyshapes/"+items.get(i), YDShape.kShapeLabelImage);
	            shape.setPosition(CGPoint.ccp(xPos + rightItemWidth/2, yPos - rightItemHeight/2));
	            shape.setFit2(CGSize.make(rightItemWidth*0.9f, rightItemHeight*0.9f));
	            super.addShape(shape);
	            
	            YDShape stock=new YDShape("image/activities/discovery/miscelaneous/babyshapes/"+items.get(i-1), YDShape.kShapeStock);
	            stock.setPosition(shape.getPosition());
	            stock.setFit2(CGSize.make(rightItemWidth*0.9f, rightItemHeight*0.9f));
	            super.addShape(stock);
	
	            if (++_col >= 3) {
	                _col=0;
	                _row += 2;
	            }
	        }
	        super.postGameInit(false, false);
	    }
	    else  {
	        int sublevel=0;
	        if (mLevel == 8)
	            sublevel=super.nextInt(5);
	        String config=String.format("image/activities/discovery/miscelaneous/babyshapes/board%d_%d.xml.in", mLevel, sublevel);
	        java.util.ArrayList<YDShape> shapes=createShapesFromConfiguration(config);
	        
	        //find the background specified by shape name
	        YDShape bgShape=null;
	        for (YDShape shape : shapes) {
	            if (shape.getType() == YDShape.kShapeBackground) {
	                if (shape.getName() != null  && "background".equalsIgnoreCase(shape.getName())) {
	                    bgShape=shape;
	                    break;
	                }
	                else {
	                    bgShape=shape;
	                }
	            }
	        }
	        String _bg="image/activities/discovery/miscelaneous/" + bgShape.getResource();
	        super.preGameInit(_bg);
	        //fix the shape position
	        for (YDShape shape : shapes) {
	            if (shape == bgShape || (shape.getType() == YDShape.kShapeLabelText))
	                continue;
	            float xOffset=(shape.getPosition().x-bgShape.getPosition().x)*backgroundSprite.getScale();
	            float yOffset=(shape.getPosition().y-bgShape.getPosition().y)*backgroundSprite.getScale();
	            shape.setPosition(CGPoint.make(backgroundSprite.getPosition().x+xOffset, backgroundSprite.getPosition().y-yOffset));
	            
	            shape.setResource("image/activities/discovery/miscelaneous/"+shape.getResource());
	            super.addShape(shape);
	        }
	        shapes.clear();
	        
	        super.postGameInit(true,  false);
	        if (mLevel==6)
	            backgroundSprite.setVisible(false);
	    }
	    items.clear();
	}
}
