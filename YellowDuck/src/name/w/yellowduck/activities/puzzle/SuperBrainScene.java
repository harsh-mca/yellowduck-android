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


package name.w.yellowduck.activities.puzzle;

import name.w.yellowduck.Category;
import name.w.yellowduck.EllipseSprite;
import name.w.yellowduck.LineSprite;
import name.w.yellowduck.PrimSprite;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor4F;

import android.view.MotionEvent;

public class SuperBrainScene extends name.w.yellowduck.YDActLayerBase {
	private final int LEVEL_MAX_FOR_HELP	=4;
	private final int kTagSolution        =1000;
	private final int kTagPreSolution     =10;
	private final int kTagHint            =11;

	private final int MAX_PIECES	=10;

    private int number_of_piece, number_of_color;
    private int pieces[]=new int[MAX_PIECES];
    private int answering[]=new int[MAX_PIECES];
    private int answeringGroup;
    
    private float radius, yRoom;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new SuperBrainScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=6; mMaxSublevel=6;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(), kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionOk);
	

	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
	    
	    if(mLevel < LEVEL_MAX_FOR_HELP) {
	        number_of_piece = mLevel + 2;
	        number_of_color = mLevel + 4;
	    }
	    else {
	        number_of_piece = mLevel - LEVEL_MAX_FOR_HELP + 3;
	        number_of_color = mLevel - LEVEL_MAX_FOR_HELP + 5;
	    }
	    radius=10*preferredContentScale(false);
	    for (int i = 0; i < number_of_color; ++i) {
	        pieces[i]=i;
	
	        EllipseSprite sprite=new EllipseSprite(CGPoint.ccp(radius * 3, szWin.height-topOverhead() - ((i+1) * (radius+2)) * 2 - radius), radius, radius);
	        sprite.setSolid(true);
	        sprite.setClr(getClr(i));
	        super.addChild(sprite, 1);
	        floatingSprites.add(sprite);
	    }
	    //the first number_of_piece colors will be acted as answers
	    randomIt(pieces, number_of_color);
	    
	    //solutions
	    answeringGroup=0;
	    for (int i = 0; i < number_of_piece; ++i) {
	        answering[i]=0;
	    }
	    this.tryAgain();
	}
	private ccColor4F getClr(int idx) {
	     int  colors[] =
	                 {
	                 0x0000FFC0,
	                 0x00FF00C0,
	                 0xFF0000C0,
	                 0x00FFFFC0,
	                 0xFF00FFC0,
	                 0xFFFF00C0,
	                 0x00007FC0,
	                 0x007F00C0,
	                 0x7F0000C0,
	                 0x7F007FC0,
	                 };
	    return new ccColor4F(1.0f*((colors[idx]>>24)& 0xff)/0xff, 1.0f*((colors[idx]>>16)& 0xff)/0xff, 1.0f*((colors[idx]>>8)& 0xff)/0xff, 1.0f*(colors[idx]& 0xff)/0xff);
	}
	
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
        for (CCNode node : floatingSprites) {
            if (node.getTag() >= kTagSolution) {
                EllipseSprite sprite=(EllipseSprite)node;
                if (sprite.hit(p1)) {
                    int index=node.getTag()-kTagSolution;
                    answering[index]=(answering[index]+1)%number_of_color;
                    sprite.setClr(getClr(answering[index]));
                }
            }
        }
	    return true;
	}
	
	public void ok(Object _sender) {
	    boolean success=true;
	    int total=floatingSprites.size();
	    
	    int processed[]=new int[MAX_PIECES];
	    for (int i = 0; i < number_of_piece; ++i) {
	        //both color and position are correct
	        processed[i]=(pieces[i] == answering[i])?2:0;
	    }
	    //if two colors are same in user's answer, delete one
	    for (int i = 0; i < number_of_piece - 1; ++i) {
	        for (int j = i+1; j < number_of_piece; ++j) {
	            if (answering[j] == answering[i]) {
	                if (processed[j] == 2) {
	                    answering[i]=-1;
	                }
	                else {
	                    answering[j]=-1;
	                }
	            }
	        }
	    }
	    for (int p=0; p < total; ++p) {
	        CCNode node=floatingSprites.get(p);
	        if (node.getTag() >= kTagSolution) {
	            EllipseSprite ellipse=(EllipseSprite)node;
	            int index=ellipse.getTag()-kTagSolution;
	            ellipse.setTag(kTagPreSolution);
	
	            ccColor4F clr=new ccColor4F(0, 0, 0, 0); //transparent
	            if (pieces[index]==answering[index]) {
	                //position is correct, color is correct
	                clr=new ccColor4F(0, 0, 0, 1.0f);//black
	                processed[index]=2;
	            }
	            else {
	                success=false;
	                boolean clrExists=false;
	                for (int j = 0; j <number_of_piece; ++j) {
	                    if (pieces[j]==answering[index]) {
	                        clrExists=true;
	                        break;
	                    }
	                }
	                if (clrExists)
	                    clr=new ccColor4F(1.0f, 1.0f, 1.0f, 1.0f);//black
	                answering[index]=0;//reset this answer
	            }
	            if (clr.a > 0) {
	                LineSprite sprite=new LineSprite(CGPoint.ccp(ellipse.getCenter().x-radius, ellipse.getCenter().y-radius-2), CGPoint.ccp(ellipse.getCenter().x+radius, ellipse.getCenter().y-radius-2));
	                sprite.setClr(clr);
	                sprite.setLineWidth(2);
	                sprite.setTag(kTagHint);
	                super.addChild(sprite,1);
	                floatingSprites.add(sprite);
	            }
	        }
	    }
	    ++answeringGroup;
	    if (success) {
	        flashAnswerWithResult(true, true, null, null, 2);
	    }
	    else {
	        this.tryAgain();
	    }
	}
	private void tryAgain() {
	    radius = 16.0f * preferredContentScale(false);//this must be big engough so that user can tap on it
	    yRoom=radius * 2 + 4;
	    float yPos=bottomOverhead() + radius + answeringGroup * yRoom + 10;
	
	    for (int i = 0; i < number_of_piece; ++i) {
	        ccColor4F clr=getClr(answering[i]);
	        float xPos=szWin.width - (number_of_piece-i) * (radius+2) * 2;
	        EllipseSprite sprite=new EllipseSprite(CGPoint.ccp(xPos, yPos),radius,radius);
	        sprite.setSolid(true);
	        sprite.setClr(clr);
	        sprite.setTag(kTagSolution+i);
	        super.addChild(sprite ,1);
	        floatingSprites.add(sprite);
	    }
	    //scroll down
	    if (yPos + radius > szWin.height-topOverhead()) {
	        int total=floatingSprites.size();
	        for (int i = total - 1; i >= 0; --i) {
	            CCNode node=floatingSprites.get(i);
	            if (node.getTag()==kTagPreSolution || node.getTag() >= kTagSolution || node.getTag()==kTagHint) {
	                PrimSprite sprite=(PrimSprite)node;
	                CGRect rc=sprite.enclosedArea();
	                if (rc.origin.y < 0) {
	                    sprite.removeFromParentAndCleanup(true);
	                    floatingSprites.remove(i);
	                }
	                else {
	                    sprite.moveWithOffsetX(0, -yRoom);
	                }
	            }
	        }
	        --answeringGroup;
	    }
	}
}
