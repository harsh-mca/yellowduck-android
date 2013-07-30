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
import name.w.yellowduck.EllipseSprite;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4F;

public class GuessCountScene extends name.w.yellowduck.YDActLayerBase {
	private final int OP_PLUS             =0;
	private final int OP_MINUS            =1;
	private final int OP_MULT             =2;
	private final int OP_DIV              =3;

	private final int kTotalNumbers           =13;
	//max numbers used to guess
	private final int MAX_NUMBER              =5;

	private final int kTagPositionNumber1      =1000; // a number should be filled in this location
	private final int kTagPositionNumber2      =1001;
	private final int kTagPositionNumberAuto   =1002;
	private final int kTagPositionOp           =1003; // a operator should be filled in this location
	private final int kTagIntemediaResult      =1004; // a operator should be filled in this location

    private CCLabel sublevelLabel, resultLabel;
    private float keypadSize, keypadRoom;
    private float yTopNumber;
    private int filledAnswerIdxStarts, filledAnswerIdxEnds, answeringIdx;
    private boolean operationInvalid, ready;
    
    private int num_values[]=new int[kTotalNumbers];
    private int selected_values[]=new int[kTotalNumbers];
    private int answer;
    private int fontSize;

	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new GuessCountScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	

	public GuessCountScene() {
		super ();
        int i=0;
        for (i = 0; i < 10; ++i) {
            num_values[i]=i+1;
        }
        num_values[i++]=25;
        num_values[i++]=50;
        num_values[i++]=100;
	}

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=MAX_NUMBER-1;
	    mMaxSublevel=3;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(), kBgModeFit2Center);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);

	    fontSize=super.largeFontSize();
	    //sublevel label
	    sublevelLabel = CCLabel.makeLabel("4/4", super.sysFontName(),fontSize);
	    sublevelLabel.setPosition(sublevelLabel.getContentSize().width, szWin.height-topOverhead()-sublevelLabel.getContentSize().height/2);
	    super.addChild(sublevelLabel,1);

	    //four operators
	    keypadRoom=szWin.width/10; //includes the margin around the button
	    keypadSize=keypadRoom * 0.8f; //the size of button itself 
	    float yPos=szWin.height-topOverhead();
	    String opSvg[]={"plus", "minus", "by", "div"};
	    for (int i = 0; i < 4; ++i) {
	    	String img=String.format("image/activities/math/algebramenu/algebra_guesscount/%s.png", opSvg[i]);
	    	String imgSel=super.buttonize(img);
	    	CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(imgSel);
	        
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCSprite spriteSelected=CCSprite.sprite(texture);
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite,spriteSelected,this,"operatorTouched");
	        menuitem.setPosition((i+3)*keypadRoom + keypadRoom / 2, yPos - keypadRoom / 2);
	        menuitem.setTag(i);
	        menuitem.setScale(keypadSize/menuitem.getContentSize().width);
	        CCMenu menu = CCMenu.menu(menuitem);
	        menu.setPosition(0,0);
	        super.addChild(menu,1);
	    }
	    yTopNumber=yPos-keypadSize;
	    //sublevel label
	    resultLabel = CCLabel.makeLabel("1000", super.sysFontName(), fontSize);
	    resultLabel.setColor(ccColor3B.ccRED);
	    resultLabel.setPosition((szWin.width+keypadRoom*6)/2, yPos - keypadRoom / 2);
	    super.addChild(resultLabel,2);
	    
	    EllipseSprite circle=new EllipseSprite(resultLabel.getPosition(),resultLabel.getContentSize().width/2, resultLabel.getContentSize().width/2);
	    circle.setSolid(true);
	    circle.setLineWidth(4);
	    circle.setClr(new ccColor4F(1.0f, 1.0f, 1.0f, 0.8f));
	    super.addChild(circle,1);
	
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();

	    ready=true;
	    //random numbers
	    for (int i = 0; i < kTotalNumbers; ++i)
	        selected_values[i]=num_values[i];
	    randomIt(selected_values,kTotalNumbers);
	    
	    int operators[]=new int[MAX_NUMBER+1];
	    if (mLevel == 1) {
	        operators[0]=OP_PLUS;
	        operators[1]=OP_MINUS;
	    }
	    else if (mLevel == 2) {
	        operators[0]=OP_MULT;
	        operators[1]=OP_PLUS;
	        operators[2]=OP_MINUS;
	    }
	    else { //level 3 and 4
	        operators[0]=OP_DIV;
	        operators[1]=OP_PLUS;
	        operators[2]=OP_MINUS;
	        operators[3]=OP_PLUS;
	        operators[4]=OP_MINUS;
	    }
	    randomIt(operators,mLevel+1);
	
	    int result=0;
	    String stat="";
	    for (int i = 0; i < mLevel+1; ++i) {
	        int theNumber=selected_values[i];
	        if (i <= 0) {
	            result=theNumber;
	            stat+=""+theNumber;
	        }
	        else {
	            switch (operators[i-1]) {
	                case OP_PLUS:
	                    result += theNumber;
	                    stat+="+"+theNumber;
	                    break;
	                case OP_MINUS:
	                    if (result > theNumber) {
	                        result -= theNumber;
	                        stat+="-"+theNumber;
	                    }
	                    else {
	                        result += theNumber;
	                        stat+="+"+ theNumber;
	                    }
	                    break;
	                case OP_MULT:
	                    if (result * theNumber < 1000) {
	                        result *= theNumber;
	                        stat+="*"+theNumber;
	                    }
	                    else {
	                        if (result > theNumber) {
	                            result -= theNumber;
	                            stat+="-"+theNumber;
	                        }
	                        else {
	                            result += theNumber;
	                            stat+="+"+theNumber;
	                        }
	                        operators[i]=OP_MULT;
	                    }
	                    break;
	                case OP_DIV:
	                    if (result >= theNumber && (result % theNumber) == 0) {
	                        result /= theNumber;
	                        stat+="/"+theNumber;
	                    }
	                    else {
	                        if (result > theNumber) {
	                            result -= theNumber;
		                        stat+="-"+theNumber;
	                        }
	                        else {
	                            result += theNumber;
		                        stat+="+"+theNumber;
	                        }
	                        operators[i]=OP_DIV;
	                    }
	                    break;
	            }
	        }
	    }
	    if (stat != null) {
	    }
	    //NSLog(@"%@", stat);
	    answer=result;
	    randomIt(selected_values,mLevel+1);
	
	    //select numbers
	    float xMargin=(szWin.width - (mLevel+1)*keypadRoom)/2;
	    float yPos=yTopNumber;
	    for (int i = 0; i < mLevel+1; ++i) {
	        int theNumber=selected_values[i];
	        String img="image/activities/math/algebramenu/algebra_guesscount/"+theNumber+".png";
	        String imgSel=super.buttonize(img);
	        CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(imgSel);
	
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCSprite spriteSelected=CCSprite.sprite(texture);
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite,spriteSelected,this,"numTouched");
	        menuitem.setPosition(xMargin+i*keypadRoom + keypadRoom / 2, yPos - keypadRoom / 2);
	        menuitem.setTag(theNumber);
	        menuitem.setScale(keypadSize/menuitem.getContentSize().width);
	        CCMenu menu = CCMenu.menu(menuitem);
	        menu.setPosition(0,0);
	        menu.setTag(i);
	        menu.setUserData(Float.valueOf(menuitem.getPosition().x));//remember its original position
	        super.addChild(menu,1);
	
	        floatingSprites.add(menu);
	    }
	    //the equal sign, not clickable
	    float xPos=szWin.width - keypadRoom * 3;
	    //the buttion height is less than its width, here use keypadSize to save space to display up to four rows on iPhone
	    yPos = yTopNumber-keypadSize;
	    for (int i = 0; i < mLevel; ++i) {
	        CCSprite eql=spriteFromExpansionFile("image/activities/math/algebramenu/algebra_guesscount/equal.png");
	        eql.setScale(keypadSize/eql.getContentSize().width);
	        eql.setPosition(xPos, yPos-keypadSize/2);
	        super.addChild(eql,1);
	        floatingSprites.add(eql);
	        yPos -= keypadSize;
	    }
	    //contents which will be changed when user answers the questions
	    filledAnswerIdxStarts=floatingSprites.size();
	    answeringIdx=0;
	    //blank spaces
	    yPos = yTopNumber-keypadSize;
	    String blank="____";
	    
	    CCLabel intermediaResultLabel=null;
	    for (int i = 0; i < mLevel; ++i) {
	        xPos=szWin.width - keypadRoom * 6;
	        //the first number, only a space holder
	        CCLabel num1Label = CCLabel.makeLabel((i>0)?blank:" ", super.sysFontName(), fontSize);
	        num1Label.setPosition(xPos, yPos-keypadSize/2);
	        num1Label.setColor(ccColor3B.ccYELLOW);
	        num1Label.setTag((i>0)?kTagPositionNumberAuto:kTagPositionNumber1);
	        super.addChild(num1Label, 1);
	        floatingSprites.add(num1Label);
	        //link intermediaResultLabel together
	        if (intermediaResultLabel!=null)
	            intermediaResultLabel.setUserData(num1Label);
	        xPos += keypadRoom;
	
	        //the operator
	        CCLabel opLabel = CCLabel.makeLabel(" ", super.sysFontName(), fontSize);
	        opLabel.setPosition(xPos, yPos-keypadSize/2);
	        opLabel.setTag(kTagPositionOp);
	        super.addChild(opLabel,1);
	        floatingSprites.add(opLabel);
	        xPos += keypadRoom;
	        
	        //the second number
	        CCLabel num2Label = CCLabel.makeLabel(" ", super.sysFontName(), fontSize);
	        num2Label.setPosition(xPos, yPos-keypadSize/2);
	        num2Label.setTag(kTagPositionNumber2);
	        super.addChild(num2Label,1);
	        floatingSprites.add(num2Label);
	        xPos += keypadRoom;
	        
	        //equal sign were created above
	        
	        //
	        xPos += keypadRoom;
	        //the result
	        intermediaResultLabel = CCLabel.makeLabel(blank, super.sysFontName(), fontSize);
	        intermediaResultLabel.setColor(ccColor3B.ccYELLOW);
	        intermediaResultLabel.setPosition(xPos, yPos-keypadSize/2);
	        intermediaResultLabel.setTag(kTagIntemediaResult);
	        super.addChild(intermediaResultLabel,1);
	        floatingSprites.add(intermediaResultLabel);
	        
	        yPos -= keypadSize;
	    }
	    filledAnswerIdxEnds=floatingSprites.size();
	    
	    resultLabel.setString(""+answer);
	    sublevelLabel.setString(String.format("%d/%d", mSublevel+1, mMaxSublevel));
	}
	public void operatorTouched(Object _sender) {
	    if (!ready)
	        return;
	    CCNode sender=(CCNode)_sender;
	    if (sender.getUserData()==null && !operationInvalid) {
	        CCNode nodeWorkingOn=floatingSprites.get(filledAnswerIdxStarts+answeringIdx);
	        if (nodeWorkingOn.getTag() == kTagPositionOp) {
	            int opSign=sender.getTag();
	            //make a copy of this operator sprite
	            String opSvg[]={"plus", "minus", "by", "div"};
		    	String img=String.format("image/activities/math/algebramenu/algebra_guesscount/%s.png", opSvg[opSign]);
		    	String imgSel=super.buttonize(img);
		    	CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(imgSel);
		        
		        CCSprite sprite=spriteFromExpansionFile(img);
		        CCSprite spriteSelected=CCSprite.sprite(texture);
		        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite,spriteSelected,this,"operatorTouched");
		        menuitem.setPosition(nodeWorkingOn.getPosition());
		        menuitem.setTag(opSign);
		        menuitem.setScale(sender.getScale());
		        menuitem.setUserData("copied");
		        CCMenu menu = CCMenu.menu(menuitem);
		        menu.setPosition(0,0);
		        super.addChild(menu,1);
		        floatingSprites.add(menu);
	            	
	            nodeWorkingOn.setUserData(menuitem);
	            ++answeringIdx;
	            this.forward();
	            this.checkAnswer();
	        }
	    }
	    else {
	        int backup=answeringIdx;
	        this.backward();
	        CCNode nodePreWorkingOn=floatingSprites.get(filledAnswerIdxStarts+answeringIdx);
	        if (nodePreWorkingOn.getUserData() == sender) {
	            nodePreWorkingOn.setUserData(null);
	            //find the menu of this menu item
	            sender.getParent().removeFromParentAndCleanup(true);
	            floatingSprites.remove(sender.getParent());
	            this.checkAnswer();
	        }
	        else {
	            answeringIdx=backup;
	        }
	    }
	}
	
	public void numTouched(Object _sender) {
	    if (!ready)
	        return;
	    CCNode sender=(CCNode)_sender;
	    if (sender.getUserData()== null && !operationInvalid) {
	        CCNode nodeWorkingOn=floatingSprites.get(filledAnswerIdxStarts+answeringIdx);
	        if (nodeWorkingOn.getTag() == kTagPositionNumber1 || nodeWorkingOn.getTag() == kTagPositionNumber2) {
	            sender.setPosition(nodeWorkingOn.getPosition());
	            sender.setUserData("picked");
	
	            nodeWorkingOn.setUserData(sender);
	            ++answeringIdx;
	            this.forward();
	            this.checkAnswer();
	        }
	    }
	    else {
	        int backup=answeringIdx;
	        this.backward();
	        CCNode nodePreWorkingOn=floatingSprites.get(filledAnswerIdxStarts+answeringIdx);
	        if (nodePreWorkingOn.getUserData() == sender) {
	            //put it back
	            nodePreWorkingOn.setUserData(null);
	            sender.setUserData(null); //not moved
	            
	            Float xPos=(Float)sender.getParent().getUserData();
	            float yPos=yTopNumber - keypadRoom/2;
	            sender.setPosition(xPos, yPos);
	            this.checkAnswer();
	        }
	        else {
	            answeringIdx=backup;
	        }
	    }
	}

	//User picked a number or operator, moving forward
	private void forward (){
	    if (filledAnswerIdxStarts+answeringIdx >=filledAnswerIdxEnds)
	        return;
	    CCNode nodePreWorkingOn=floatingSprites.get(filledAnswerIdxStarts+answeringIdx);
	    while (nodePreWorkingOn.getTag()==kTagPositionNumberAuto || nodePreWorkingOn.getTag()==kTagIntemediaResult) {
	        ++answeringIdx;
	        if (filledAnswerIdxStarts+answeringIdx >=filledAnswerIdxEnds)
	            break;
	        nodePreWorkingOn=floatingSprites.get(filledAnswerIdxStarts+answeringIdx);
	    }
	}
	
	//User removed a number or operator, moving backward
	private void backward() {
	    if (answeringIdx <= 0)
	        return;
	    --answeringIdx;
	    CCNode nodePreWorkingOn=floatingSprites.get(filledAnswerIdxStarts+answeringIdx);
	    while (nodePreWorkingOn.getTag()==kTagPositionNumberAuto || nodePreWorkingOn.getTag()==kTagIntemediaResult) {
	        --answeringIdx;
	        if (answeringIdx <= 0)
	            break;
	        nodePreWorkingOn=floatingSprites.get(filledAnswerIdxStarts+answeringIdx);
	    }
	}
	private void checkAnswer() {
	    //reset all intermediate results
	    for (int i = filledAnswerIdxStarts; i<filledAnswerIdxEnds; ++i) {
	        CCNode node=floatingSprites.get(i);
	        if (node.getTag()==kTagIntemediaResult || node.getTag()==kTagPositionNumberAuto) {
	            CCLabel label=(CCLabel)node;
	            label.setString("____");
	            label.setColor(ccColor3B.ccYELLOW);
	        }
	    }
	    operationInvalid=false;
	    //check results
	    int result=0, operator=0;
	    for (int i = filledAnswerIdxStarts; i<filledAnswerIdxEnds; ++i) {
	        CCNode node=floatingSprites.get(i);
	        if (node.getTag()==kTagIntemediaResult) {
	            CCLabel label=(CCLabel)node;
	            if (operationInvalid) {
	                label.setString("x");
	                label.setColor(ccColor3B.ccRED);
	                break;
	            }
	            else {
	                label.setString(""+result);
	            }
	        }
	        else if (node.getTag()==kTagPositionNumberAuto) {
	            CCLabel label=(CCLabel)node;
	            label.setString(""+result);
	        }
	        else if (node.getTag() == kTagPositionNumber1) {
	            CCNode number=(CCNode)node.getUserData();
	            if (number == null)
	                break;
	            result=number.getTag();
	        }
	        else if (node.getTag() == kTagPositionNumber2) {
	            CCNode number=(CCNode)node.getUserData();
	            if (number == null)
	                break;
	            int num2=number.getTag();
	            switch (operator) {
	                case OP_PLUS:
	                    result += num2;
	                    break;
	                case OP_MINUS:
	                    if (result >= num2)
	                        result -= num2;
	                    else
	                        operationInvalid=true;
	                    break;
	                case OP_MULT:
	                    result *= num2;
	                    break;
	                case OP_DIV:
	                    if (result >= num2 && (result % num2) == 0)
	                        result /= num2;
	                    else
	                        operationInvalid=true;
	                    break;
	            }
	        }
	        else if (node.getTag() == kTagPositionOp) {
	            CCNode op=(CCNode)node.getUserData();
	            if (op == null)
	                break;
	            operator=op.getTag();
	        }
	    }
	    if (!operationInvalid) {
	        if (result == answer && filledAnswerIdxStarts+answeringIdx>=filledAnswerIdxEnds) {
	            ready=false;
	            super.flashAnswerWithResult(true,  true,  null,  null,  2);
	        }
	    }
	}
}
