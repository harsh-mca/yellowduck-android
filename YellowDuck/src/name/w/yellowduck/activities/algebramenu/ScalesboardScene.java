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


package name.w.yellowduck.activities.algebramenu;

import name.w.yellowduck.Category;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;

import android.util.FloatMath;
import android.view.MotionEvent;

public class ScalesboardScene extends name.w.yellowduck.YDActLayerBase {
	private final int kModeCount          =0;
	private final int kModeWeight         =1;

	private final int kTagItemsLeft        =8000;
	private final int  kTagItemsRight       =8001;

	private final int kTagItemWeightObject =9000;
	private final int kTagMass             =9001;
	
    private int mode;
    private CCSprite bras, leftPlate, rightPlate;
    private CCLabel promptLabel, sublevelLabel, signLabel;
    
    private float yBalance, yScaleBaseBottom;
    private CGPoint ptOriginal;
    private float yMassOriginal;
    private CCNode clicked;
    
    private int objectWeight, totalMassWeight;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ScalesboardScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=5;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    if ("weight".equalsIgnoreCase(activeCategory.getSettings())) {
	        mode=kModeWeight;
	    }
	    else {
	        mode=kModeCount;
	    }
	    if (mode==kModeCount) {
	        mMaxLevel=4;
	        mMaxSublevel=5;
	    }
	    else {
	        mMaxLevel=5;
	        mMaxSublevel=3;
	    }
	    //change a background music
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(), kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
	    
	    //scale base
	    CCSprite base=spriteFromExpansionFile("image/activities/math/algebramenu/scalesboard/balance.png");
	    base.setScale(preferredContentScale(true));
	    base.setPosition(szWin.width/2, szWin.height/2);
	    super.addChild(base,2);
	    yScaleBaseBottom=base.getPosition().y-base.getContentSize().height*base.getScaleY()/2;
	    //the >,<or= sign
	    signLabel = CCLabel.makeLabel("<", super.sysFontName(), super.smallFontSize());
	    signLabel.setPosition(szWin.width/2, base.getPosition().y+base.getContentSize().height*base.getScale()*0.1f);
	    super.addChild(signLabel,3);
	    
	    //bras
	    bras=spriteFromExpansionFile("image/activities/math/algebramenu/scalesboard/bras.png");
	    bras.setScale(preferredContentScale(true));
	    bras.setPosition(szWin.width/2, base.getPosition().y+base.getContentSize().height*base.getScale()/2-bras.getContentSize().height*bras.getScale()/2+base.getContentSize().height*base.getScale()*0.076f);
	    super.addChild(bras,1);    
	    
	    yBalance=bras.getPosition().y-bras.getContentSize().height*bras.getScale()/2;
	    //left plate
	    leftPlate=spriteFromExpansionFile("image/activities/math/algebramenu/scalesboard/plateau.png");
	    leftPlate.setScale(preferredContentScale(true));
	    leftPlate.setPosition(bras.getPosition().x-bras.getContentSize().width*bras.getScale()/2+1, yBalance);
	    super.addChild(leftPlate,1);
	    //right plate
	    rightPlate=spriteFromExpansionFile("image/activities/math/algebramenu/scalesboard/plateau.png");
	    rightPlate.setScale(preferredContentScale(true));
	    rightPlate.setPosition(bras.getPosition().x+bras.getContentSize().width*bras.getScale()/2-1, yBalance);
	    super.addChild(rightPlate,1);
	    
	    //display the current sublelvel
	    sublevelLabel = CCLabel.makeLabel("10/10", super.sysFontName(), super.smallFontSize());
	    sublevelLabel.setColor(ccColor3B.ccBLACK);
	    sublevelLabel.setPosition(sublevelLabel.getContentSize().width/2+4, szWin.height - topOverhead() - sublevelLabel.getContentSize().height/2);
	    super.addChild(sublevelLabel,2);
	
	    //prompt: you can add weight to both sides of the scale
	    promptLabel = CCLabel.makeLabel(localizedString("prompt_scaleboard"), super.sysFontName(), super.smallFontSize());
	    promptLabel.setColor(ccColor3B.ccBLACK);
	    promptLabel.setVisible(false);
	    promptLabel.setPosition(szWin.width/2, szWin.height - topOverhead() - promptLabel.getContentSize().height/2);
	    super.addChild(promptLabel,2);
	    
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
		
	    boolean showWeight=(mLevel==1) || (mLevel==3);
	    //if (mode == kModeWeight) {
	    //    showWeight=(mLevel<3);
	    //}
	    int factor=(mode == kModeCount) ? 1:  ((mLevel<5)?100:200);
	    int default_list_weight[] =new int[]{ 1*factor, 2*factor, 2*factor, 5*factor, 5*factor, 10*factor, 10*factor};
	    int list_weight[]=new int[10];
	    int tmp[]=new int[5];
	    
	    int totalMasse=0;
	    switch(mLevel)
	    {
	        case 1:
	        case 2:
	            objectWeight =randomBetween(5,20)*factor;
	            totalMasse=7;
	            for(int i=0; i<totalMasse; i++)
	                list_weight[i] = default_list_weight[i];
	            
	            promptLabel.setVisible(false);
	            break;
	        case 3:
	        case 4:
	        case 5:
	            totalMasse=5;
	            while(true)
	            {
	                for(int i=0; i< totalMasse; i++) {
	                    tmp[i] = default_list_weight[randomBetween(0,6)];
	                }
	                
	                int left=0, right=0;
	                for(int i=0; i<totalMasse; i++) {
	                    if (super.nextInt(2)==0)
	                        left += tmp[i];
	                    else
	                        right += tmp[i];
	                }
	                objectWeight = left-right;
	                if (objectWeight < 0)
	                    objectWeight=0-objectWeight;
	                if(!this.testAddition(objectWeight,tmp,totalMasse))
	                    break;
	            }
	            for(int i=0; i<totalMasse; i++)
	                list_weight[i] = tmp[i];
	            //sort it
	            boolean more=true;
	            while (more) {
	                more=false;
	                for (int i = 0; i < totalMasse-1; ++i) {
	                    if (list_weight[i] > list_weight[i+1]) {
	                        more=true;
	                        int t=list_weight[i];
	                        list_weight[i]=list_weight[i+1];
	                        list_weight[i+1]=t;
	                    }
	                }
	            }
	            promptLabel.setVisible(true);
	            break;
	    }
	    String items[]={"chocolate_cake.png", "flowerpot.png", "glass.png", "orange.png", "pear.png", "suggar_box.png"};
	    int sel=super.nextInt(items.length);
	    CCSprite itemSprite=spriteFromExpansionFile("image/activities/math/algebramenu/scalesboard/" + items[sel]);
	    itemSprite.setScale(preferredContentScale(true));
	    itemSprite.setPosition(rightPlate.getPosition().x, rightPlate.getPosition().y+rightPlate.getContentSize().height*rightPlate.getScale()/2+itemSprite.getContentSize().height*itemSprite.getScale()/2);
	    itemSprite.setTag(kTagItemWeightObject);
	    super.addChild(itemSprite,2);
	    floatingSprites.add(itemSprite);
	    
	    if (showWeight) {
	        CCLabel label = CCLabel.makeLabel(this.toUnit(objectWeight), super.sysFontName(), super.smallFontSize());
	        label.setColor(ccColor3B.ccBLACK);
	        label.setPosition(itemSprite.getPosition());
	        super.addChild(label,3);
	        floatingSprites.add(label);
	        
	        itemSprite.setUserData(label);
	        if (mode==kModeWeight) {
	            label.setScale(0.6f);
	        }
	    }
	
	    float xMargin=0, xRoom=0;
	    totalMassWeight=0;
	    for (int i = 0; i < totalMasse; ++i) {
	        totalMassWeight+=list_weight[i];
	        CCSprite mass=spriteFromExpansionFile("image/activities/math/algebramenu/scalesboard/masse.png");
	        mass.setScale(preferredContentScale(true));
	        if (xMargin <= 0) {
	            xMargin=(szWin.width - (mass.getContentSize().width * mass.getScale() + mass.getContentSize().width/2) * totalMasse) / 2;
	            xRoom=(szWin.width - xMargin * 2) / totalMasse;
	        }
	        mass.setPosition(xMargin + xRoom * i + xRoom/2, (bottomOverhead() + yScaleBaseBottom)/2);
	        mass.setTag(kTagMass);
	        super.addChild(mass,2);
	        floatingSprites.add(mass);
	        yMassOriginal=mass.getPosition().y;
	
	        CCLabel label = CCLabel.makeLabel(toUnit(list_weight[i]), super.sysFontName(), super.smallFontSize());
	        label.setColor(ccColor3B.ccBLACK);
	        label.setPosition(mass.getPosition());
	        label.setTag(list_weight[i]);
	        label.setUserData(Float.valueOf(label.getPosition().x));//remember its original position
	        super.addChild(label,3);
	        floatingSprites.add(label);
	
	        mass.setUserData(label);
	        if (mode==kModeWeight) {
	            label.setScale(0.6f);
	        }
	        
	    }
	    sublevelLabel.setString(String.format("%d/%d", mSublevel+1, mMaxSublevel));
	    
	    this.skewTheScale();
	}
	// test if adding elements in table can produce total
	private boolean testAddition(int total, int[] table, int len) {
	    if(total == 0)
	        return true;
	    for(int i=0; i<len; i++) {
	        if(table[i] <= total && table[i] != 0) {
	            int cur = table[i];
	            table[i] = 0;
	            boolean result = this.testAddition(total-cur, table, len);
	            table[i] = cur;
	            if(result)
	                return true;
	        }
	    }
	    return false;
	}
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
        clicked=null;
        for (CCNode item : floatingSprites) {
            if (item.getTag()==kTagItemsLeft || item.getTag()==kTagItemsRight || item.getTag()==kTagMass) {
            	if (super.isNodeHit(item, p1)) {
                    clicked=item;
                    break;
                }
            }
        }
        if (clicked!=null) {
            //restore to its original position if the operation ends up as invalid
            ptOriginal=clicked.getPosition();
        }
        return true;
	}
	public boolean ccTouchesMoved(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
        if (clicked!=null) {
            clicked.setPosition(p1);
            CCNode attached=(CCNode)clicked.getUserData();
            if (attached!=null)
                attached.setPosition(p1);
        }
        return true;
	}
	
	public boolean ccTouchesEnded(MotionEvent event) {
	    if (clicked==null)
	        return true;
	    super.playSound("audio/sounds/eraser1.wav");    
	    boolean restore=true;
	    if (clicked.getPosition().y <= bras.getPosition().y) {
	        //put it back
	        if (clicked.getTag()==kTagItemsLeft || clicked.getTag()==kTagItemsRight) {
	            clicked.setTag(kTagMass);
	            CCNode attachedLabel=(CCNode)clicked.getUserData();
	            Float pos=(Float)attachedLabel.getUserData();
	            clicked.setPosition(pos.floatValue(), yMassOriginal);
	            restore=false;
	        }
	    }
	    else if (clicked.getPosition().x < bras.getPosition().x) {
	        //put it on the left plate
	        clicked.setTag(kTagItemsLeft);
	        restore=false;
	    }
	    else if (mLevel > 2) {
	        //put it on the right
	        clicked.setTag(kTagItemsRight);
	        restore=false;
	    }
	    if (restore) {
	        clicked.setPosition(ptOriginal);
	        CCNode attached=(CCNode)clicked.getUserData();
	        if (attached!=null)
	            attached.setPosition(ptOriginal);
	    }
	    else {
	        CCNode attached=(CCNode)clicked.getUserData();
	        if (attached!=null)
	            attached.setPosition(clicked.getPosition());
	    }
	    clicked=null;
	    this.skewTheScale();
	    
	    return true;
	}
	
	private String toUnit(int weight) {
	    if (mode == kModeCount)
	        return ""+weight;
	    if (weight >= 1000) {
	        return String.format("%.1fkg", 1.0f*weight/1000);
	    }
	    return ""+weight+"g";
	}
	
	private void skewTheScale() {
	    int leftWeight=0, rightWeight=0;
	    for (CCNode node : floatingSprites) {
	        if (node.getTag() == kTagItemsLeft) {
	            CCNode obj=(CCNode)node.getUserData();
	            leftWeight += obj.getTag();
	        }
	        else if (node.getTag() == kTagItemsRight) {
	            CCNode obj=(CCNode)node.getUserData();
	            rightWeight += obj.getTag();
	        }
	        else if (node.getTag()== kTagItemWeightObject) {
	            rightWeight += objectWeight;
	        }
	    }
	    float maxAngle=10.0f;
	    float angle=maxAngle * (rightWeight-leftWeight) / totalMassWeight;
	    if (angle > maxAngle)
	        angle=maxAngle;
	    else if (angle < 0-maxAngle)
	        angle=0-maxAngle;
	    bras.setRotation(angle);
	    
	    float adjustment=bras.getContentSize().width*bras.getScale()/2*FloatMath.sin(angle*3.14f/180.0f);
	    rightPlate.setPosition(rightPlate.getPosition().x, yBalance-adjustment);
	    leftPlate.setPosition(leftPlate.getPosition().x, yBalance+adjustment);
	    int leftIdx=0, rightIdx=0;
	    float leftY=leftPlate.getPosition().y+leftPlate.getContentSize().height*leftPlate.getScale()/2;
	    float rightY=rightPlate.getPosition().y+rightPlate.getContentSize().height*rightPlate.getScale()/2;
	    for (CCNode node : floatingSprites) {
	        if (node.getTag() == kTagItemsLeft) {
	            float x0= leftPlate.getPosition().x-leftPlate.getContentSize().width*leftPlate.getScale()/2+leftIdx * node.getContentSize().width*node.getScale()+node.getContentSize().width*node.getScale()/2;
	            node.setPosition(x0,  leftY+node.getContentSize().height*node.getScale()/2);
	            CCNode obj=(CCNode)node.getUserData();
	            if (obj!=null)
	                obj.setPosition(node.getPosition());
	            if (++leftIdx >= 5) {
	                leftIdx=0;
	                leftY+=node.getContentSize().height*node.getScale();
	            }
	        }
	        else if (node.getTag() == kTagItemsRight) {
	            float x0= rightPlate.getPosition().x-rightPlate.getContentSize().width*rightPlate.getScale()/2+rightIdx * node.getContentSize().width*node.getScale()+node.getContentSize().width*node.getScale()/2;
	            node.setPosition(x0,  rightY+node.getContentSize().height*node.getScale()/2);
	            CCNode obj=(CCNode)node.getUserData();
	            if (obj!=null)
	                obj.setPosition(node.getPosition());
	            if (++rightIdx >= 5) {
	                rightIdx=0;
	                rightY+=node.getContentSize().height*node.getScale();
	            }
	        }
	        else if (node.getTag()== kTagItemWeightObject) {
	            node.setPosition(node.getPosition().x,  rightPlate.getPosition().y+rightPlate.getContentSize().height*rightPlate.getScale()/2+node.getContentSize().height*node.getScale()/2);
	            CCNode obj=(CCNode)node.getUserData();
	            if (obj!=null)
	                obj.setPosition(node.getPosition());
	        }
	    }
	    if (leftWeight == rightWeight) {
	        signLabel.setString("=");
	        super.flashAnswerWithResult(true, true, null, null, 2);
	    }
	    else if (leftWeight > rightWeight) {
	        signLabel.setString(">");
	    }
	    else {
	        signLabel.setString("<");
	    }
	}
}
