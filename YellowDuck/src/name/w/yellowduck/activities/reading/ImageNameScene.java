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

import name.w.yellowduck.YDConfiguration;
import name.w.yellowduck.activities.misc.YDShape;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public class ImageNameScene extends name.w.yellowduck.activities.misc.ShapeGameBase {

	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ImageNameScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=7;
	    super.afterEnter();
	}

	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);

	    String config=String.format("image/activities/reading/imagename/board%d_0.xml.in", mLevel);
	    java.util.ArrayList<YDShape> shapes=createShapesFromConfiguration(config);
	    
	    super.preGameInit(null);
	    //fix the shape position
	    int totalShapes=0;
	    for (YDShape shape : shapes) {
	        if (shape.isStock()) {
	            ++totalShapes;
	        }
	    }
	    int totalRows=2;
	    int totalCols=totalShapes/totalRows;
	    int rowHeight=(int)((szWin.height - topOverhead() - bottomOverhead()) / totalRows);
	    int colWidth=(int)((szWin.width - separator)/totalCols);
	    
	    int row=0, col=0;
	    int lastRow=row, lastCol=col;
	    //auto layout shapes
	    for (YDShape shape : shapes) {
	        if (shape.isStock()) {
	            float xPos=separator + col * colWidth + colWidth / 2;
	            float yPos=bottomOverhead() + row * rowHeight + rowHeight / 2;
	            shape.setPosition(CGPoint.ccp(xPos, yPos));
	            shape.setFit2(CGSize.make(colWidth*0.8f, rowHeight*0.8f));
	     
	            shape.setResource("image/activities/reading/" + shape.getResource());
	     
	            super.addShape(shape);
	            
	            lastRow=row; lastCol=col;
	            if (++col >= totalCols) {
	                col=0;
	                ++row;
	            }
	        }
	        else if (shape.getType() == YDShape.kShapeLabelText) {
	            String label=localizedString(shape.getName());
	            if (label!=null && label.length() > 0) {
	                shape.setResource(label);
	
	                float xPos=separator + lastCol * colWidth + colWidth / 2;
	                float yPos=bottomOverhead() + lastRow * rowHeight + smallFontSize()/2;
	                shape.setPosition(CGPoint.ccp(xPos, yPos));
	                super.addShape(shape);
	            }
	        }
	    }
	    super.postGameInit(true,  false);
	}
}
