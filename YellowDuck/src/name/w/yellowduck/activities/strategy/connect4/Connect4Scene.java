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


package name.w.yellowduck.activities.strategy.connect4;

import name.w.yellowduck.Category;
import name.w.yellowduck.LineSprite;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
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
import org.cocos2d.types.ccColor4F;
import org.cocos2d.types.ccColor3B;

import android.view.MotionEvent;

public class Connect4Scene extends name.w.yellowduck.YDActLayerBase {
	private final int kTagTux         =100;
	
	private final int HUMAN               =0;
	private final int COMPUTER            =1;
	
	
	private int player[]=new int[2], level[]=new int[2], turn, num_of_players;
    private int width, height, num_to_connect;
    private int currentLevel;
    
    private int humanWin, computerWin;
    
    private C4 algorithm;
    private CGRect rcGrid;
    private float gridCellWidth, gridCellHeight;
    private boolean busy, firstTurn;
    
    private CCLabel labelComputerWin, labelHumanWin;	
	
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new Connect4Scene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=5;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    CCSprite bg=super.setupBackground(activeCategory.getBg(),kBgModeFit2Center);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionRepeatButton);
	
	    float w=bg.getContentSize().width*bg.getScaleX();
	    float h=bg.getContentSize().height*bg.getScaleY();
	    float x0=bg.getPosition().x-w/2;
	    float y0=bg.getPosition().y-h/2;
	
	    width=7; height=6;num_to_connect=4;
	    
	    gridCellWidth=(608.0f-190.0f)/800*w/(width-1);
	    gridCellHeight=(438.0f-86.0f)/520*h/(height-1);
	    rcGrid=CGRect.make(x0+156.0f/800*w, y0+52.0f/520*h, gridCellWidth*width, gridCellHeight*height);
	    
	    player[0] = HUMAN;
	    player[1] = COMPUTER;
	    algorithm=new C4();
	        
	    super.setIsTouchEnabled(true);
	    super.scheduleUpdate();
	    super.afterEnter();
	}
	
	public void onExit() {
	    algorithm.c4_reset();
	    
	    super.onExit();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
	
	    if (mLevel != currentLevel) {
	        humanWin=computerWin=0;
	        currentLevel=mLevel;
	    }
	    CCSprite human=this.displayPawn(-2, height/2-1, true, false);
	    CCSprite computer=this.displayPawn(-2, height/2+1,false, false);
	
	    labelComputerWin=CCLabel.makeLabel(""+computerWin, super.sysFontName(), super.mediumFontSize());
	    labelComputerWin.setPosition(computer.getPosition());
	    super.addChild(labelComputerWin,2);
	    floatingSprites.add(labelComputerWin);
	    
	    labelHumanWin=CCLabel.makeLabel(""+humanWin, super.sysFontName(), super.mediumFontSize());
	    labelHumanWin.setPosition(human.getPosition());
	    super.addChild(labelHumanWin, 2);
	    floatingSprites.add(labelHumanWin);
	    
	    //tap to let Tux to go first
	    String img="image/activities/strategy/connect4/tux-teacher.png";
	    String imgSel=super.buttonize(img);
	    CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(imgSel);
	    CCSprite sprite=spriteFromExpansionFile(img);
	    CCSprite spriteSelected=CCSprite.sprite(texture);
	    CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite,  spriteSelected, this, "yieldTurn");
	    CCMenu menu = CCMenu.menu(menuitem);
	    menuitem.setScale(rcGrid.origin.x/menuitem.getContentSize().width*0.5f);
	    menu.setPosition(rcGrid.origin.x/2-gridCellWidth/2, bottomOverhead() + menuitem.getContentSize().height*menuitem.getScale()/2+gridCellHeight/2);
	    menu.setTag(kTagTux);
	    super.addChild(menu, 2);
	    floatingSprites.add(menu);
	    firstTurn=true;
	    
	    turn=HUMAN;
	    level[COMPUTER] = (mLevel<=1)?1: mLevel*3; //C4_MAX_LEVEL is too slow
	    if (level[COMPUTER] > C4.C4_MAX_LEVEL)
	        level[COMPUTER]=C4.C4_MAX_LEVEL;
	    busy=false;
	    algorithm.c4_reset();
	    algorithm.c4_new_game(width, height, num_to_connect);
	}
	public void repeat(Object sender) {
	    this.initGame(false, sender);
	}
	public void update(float dt) {
	    if (!busy && algorithm.c4_is_game_in_progress() && player[turn]==COMPUTER) {
	        int row[]=new int[1], col[]=new int[1];
	        //-(BOOL)c4_auto_move:(int)player level:(int)level column:(int*)column row:(int*)row;
	        algorithm.c4_auto_move(turn, level[turn], col,row);
	        this.displayPawn(col[0], row[0], false, true);
	    }
	}
	public boolean ccTouchesEnded(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
        if (!busy && rcGrid.contains(p1.x, p1.y) && algorithm.c4_is_game_in_progress() && (player[turn]==HUMAN)) {
            CGPoint ptGrid=this.screen2Grid(p1);
            int col=(int)(ptGrid.x), row[]=new int[1];
            //-(BOOL)c4_make_move:(int)player column:(int)column row:(int*)row;
            if (algorithm.c4_make_move(turn, col, row)) {
                this.displayPawn(col, row[0], true, true);
            }
        }
        return true;
	}
	private void checkAnswer() {
	    boolean gameOver=false, adv2NextLevel=false, tied=false;
	
	    if (algorithm.c4_is_winner(HUMAN)) {
	        ++humanWin;
	        labelHumanWin.setString(""+humanWin);
	        adv2NextLevel=true;
	        gameOver=true;
	    }
	    else if (algorithm.c4_is_tie()) {
	        gameOver=true;
	        adv2NextLevel=true;
	        tied=true;
	    }
	    else if (algorithm.c4_is_winner(COMPUTER)) {
	        ++computerWin;
	        labelComputerWin.setString(""+computerWin);
	        gameOver=true;
	    }    
	    
	    if (gameOver) {
	        busy=true;
	        if (!tied) {
	            int coords[]=new int[4];
	            algorithm.c4_win_coords(coords);
	            CGPoint pt1=this.grid2Screen(CGPoint.ccp(coords[0], coords[1]));
	            CGPoint pt2=this.grid2Screen(CGPoint.ccp(coords[2], coords[3]));;
	            LineSprite sprite=new LineSprite(pt1, pt2);
	            sprite.setLineWidth(4);
	            sprite.setClr(new ccColor4F(1.0f, 1.0f, 1.0f, 1.0f));
	            super.addChild(sprite, 4);
	            floatingSprites.add(sprite);
	        }
	        super.flashAnswerWithResult(adv2NextLevel,adv2NextLevel,null,null,2);
	        algorithm.c4_end_game();
	    }
	}
	private CGPoint screen2Grid(CGPoint ptScreen) {
	    int x=(int)((ptScreen.x-rcGrid.origin.x) / gridCellWidth);
	    int y=(int)((ptScreen.y-rcGrid.origin.y) / gridCellHeight);
	    
	    return CGPoint.ccp(x, y);
	}
	private CGPoint grid2Screen(CGPoint ptGrid) {
	    int x=(int)(ptGrid.x * gridCellWidth + gridCellWidth/2+ rcGrid.origin.x);
	    int y=(int)(ptGrid.y * gridCellHeight + gridCellHeight/2+ rcGrid.origin.y);
	    
	    return CGPoint.ccp(x, y);
	}
	private CCSprite displayPawn(int col, int row, boolean human, boolean animated) {
	    CGPoint ptScreen=this.grid2Screen(CGPoint.ccp(col, row));
	    CCSprite sprite=spriteFromExpansionFile("image/activities/strategy/connect4/ball.png");
	    sprite.setScale(gridCellWidth/sprite.getContentSize().width *0.94f);
	    if (animated) {
	        sprite.setPosition(ptScreen.x, szWin.height);
	    }
	    else {
	        sprite.setPosition(ptScreen);
	    }
	    if (!human)
	        sprite.setColor(ccColor3B.ccGREEN);
	    super.addChild(sprite, 1);
	    floatingSprites.add(sprite);
	    
	    if (animated) {
	        busy=true;
	        float distance=sprite.getPosition().y-ptScreen.y;
	        CCMoveTo moveAction=CCMoveTo.action(distance/rcGrid.size.height*0.8f, ptScreen);
	        CCCallFuncN actionDone = CCCallFuncN.action(this, "moveActionDone");
	        sprite.runAction(CCSequence.actions(moveAction, actionDone));
	    }
	    return sprite;
	}
	public void moveActionDone(Object _sender) {
	    if (firstTurn) {
	        firstTurn=false;
	        int total=floatingSprites.size();
	        for (int i = total - 1; i >= 0; --i) {
	            CCNode node=floatingSprites.get(i);
	            if (node.getTag() == kTagTux) {
	                node.removeFromParentAndCleanup(true);
	                floatingSprites.remove(i);
	                break;
	            }
	        }
	    }
	    turn=1-turn;
	    busy=false;
	    this.checkAnswer();
	}
	
	public void yieldTurn(Object _sender) {
	    if (firstTurn) {
	    	CCNode sender=(CCNode)_sender;
	        sender.getParent().removeFromParentAndCleanup(true);
	        floatingSprites.remove(sender.getParent());
	        turn=1-turn;
	
	        firstTurn=false;
	    }
	}
}
