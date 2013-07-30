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


package name.w.yellowduck.activities.strategy.chinesecheckers;

import name.w.yellowduck.Category;
import name.w.yellowduck.EllipseSprite;
import name.w.yellowduck.LineSprite;
import name.w.yellowduck.YDConfiguration;
import name.w.yellowduck.activities.strategy.chinesecheckers.ai.AIMove;
import name.w.yellowduck.activities.strategy.chinesecheckers.ai.AIPlayer;
import name.w.yellowduck.activities.strategy.chinesecheckers.ai.algorithmic.AlphaBeta;
import name.w.yellowduck.activities.strategy.chinesecheckers.ai.heuristic.MiddleLeftBehindHeuristic;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.GameManager;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.GameParameters;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.ArrayBoard;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Board;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Coordinate;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes.SlotType;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.CkMove;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.MoveGenerator;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.MoveImpl;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.HumanPlayer;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.PlayerType;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemLabel;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.types.ccColor4F;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.MotionEvent;

public class ChineseCheckersScene extends name.w.yellowduck.YDActLayerBase {
	private final int kTagPawn            =100;
	private final int kTagMovementTrace   =999;
	private final int zPawn               =4;
	
    private CGRect rcBoard;
    private float slotWidth, slotHeight;
    private float pawnDiameter;
    private float xOffset, yOffset;
    private int numberOfPlayers; //human  players
    
    private GameManager gameManager;
    
    private CCSprite picked;
    private float delta;
    
    private CCLabel prompt;
    private CCSprite highlight1, highlight2;
	
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ChineseCheckersScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=4;
        this.gameManager=new GameManager();
	    
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground("image/activities/strategy/chinesecheckers/bg.jpg",kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionRepeatButton);
		    //background board size
	    CCSprite bg=super.setupBackground(activeCategory.getBg(), kBgModeFit2Center);
	    float w=bg.getContentSize().width*bg.getScale();
	    float h=bg.getContentSize().height*bg.getScale();
	    rcBoard=CGRect.make(bg.getPosition().x-w/2, bg.getPosition().y-h/2, w, h);
	    xOffset=60.0f/1024*w;
	    yOffset=25.0f/1157*h;
	    slotHeight=(rcBoard.size.height-yOffset*2)/17;
	    slotWidth =(rcBoard.size.width-xOffset*2)/25;
	    //all empty slots
	    pawnDiameter=slotWidth;
	    if (slotHeight > pawnDiameter)
	        pawnDiameter=slotHeight;
	    for (int i = 0; i < 121; i++) {
	        EllipseSprite sprite=new EllipseSprite(this.pawnScreenLocation(i), pawnDiameter/2, pawnDiameter/2);
	        sprite.setClr(new ccColor4F(1.0f, 1.0f, 1.0f, 1.0f));
	        super.addChild(sprite, 1);
	    }
	    
	    prompt=CCLabel.makeLabel("turn", super.sysFontName(), super.smallFontSize());
	    super.addChild(prompt,1);
	    
	    highlight1=spriteFromExpansionFile("image/misc/selectionmask.png");
	    highlight1.setScale(1.0f*pawnDiameter/highlight1.getContentSize().width);
	    highlight1.setColor(ccColor3B.ccc3(0x90, 0xee, 0x90));
	    highlight1.setVisible(false);
	    super.addChild(highlight1, zPawn-1);
	    
	    highlight2=spriteFromExpansionFile("image/misc/selectionmask.png");
	    highlight2.setScale(1.0f*pawnDiameter/highlight2.getContentSize().width);
	    highlight2.setColor(ccColor3B.ccc3(0x90, 0xee, 0x90));
	    highlight2.setVisible(false);
	    super.addChild(highlight2, zPawn-1);

	    super.setIsTouchEnabled(true);
	    super.scheduleUpdate();
	    super.afterEnter();
	}
	public void onExit() {
		gameManager.cancelGame();
	    super.onExit();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
		
	    highlight1.setVisible(false);
	    highlight2.setVisible(false);
	    
	    gameManager.cancelGame();
	    if (numberOfPlayers<=0) {
	        String fontName=super.sysFontName();
	        int fontSize=super.mediumFontSize();
	        CCMenuItemLabel label1 = CCMenuItemLabel.item(CCLabel.makeLabel(localizedString("1_player"),fontName,fontSize), this, "specifyNumberOfPlayer");
	        CCMenuItemLabel label2 = CCMenuItemLabel.item(CCLabel.makeLabel(localizedString("2_player"),fontName,fontSize), this, "specifyNumberOfPlayer");
	        CCMenuItemLabel label3 = CCMenuItemLabel.item(CCLabel.makeLabel(localizedString("3_player"),fontName,fontSize), this, "specifyNumberOfPlayer");
	        CCMenuItemLabel label4 = CCMenuItemLabel.item(CCLabel.makeLabel(localizedString("4_player"),fontName,fontSize), this, "specifyNumberOfPlayer");
	        label1.setTag(1); label2.setTag(2);label3.setTag(3);label4.setTag(4);
	        label1.setColor(ccColor3B.ccBLUE);
	        label2.setColor(ccColor3B.ccBLUE);
	        label3.setColor(ccColor3B.ccBLUE);
	        label4.setColor(ccColor3B.ccBLUE);
	
	        CCMenu menu = CCMenu.menu(label1, label2,label3,label4);
	        menu.setPosition(szWin.width/2, szWin.height/2);
	        menu.alignItemsVertically();
	        super.addChild(menu, 5);
	        floatingSprites.add(menu);
	        //background
	        int w=(int)(label1.getContentSize().width*1.5f);
	        int h=(int)(label1.getContentSize().height*4*1.5f);
	        Bitmap imgGridBg=super.roundCornerRect(w+2,h+2,6*preferredContentScale(false),new ccColor4B(0xc0, 0xc0, 0xc0, 0xf0));
	        CCSprite sprite=CCSprite.sprite(imgGridBg, "menubg");
	        sprite.setPosition(menu.getPosition());
	        super.addChild(sprite, 4);
	        floatingSprites.add(sprite);
	
	        menu.setUserData(sprite);
	    }
	    else {
	        this.initPlayers();
	    }
	}
	private void initPlayers() {
	    SlotType sts[]={SlotType.SOUTH, SlotType.NORTH, SlotType.NORTHEAST, SlotType.SOUTHWEST};
	    int clrs[]={Color.RED, Color.BLUE, Color.GREEN, Color.CYAN};
	    java.util.ArrayList<Player> players=new java.util.ArrayList<Player> ();
	    for (int i = 1; i <= numberOfPlayers; ++i) {
	        String name=String.format(localizedString("cc_player_name"), i);
	        HumanPlayer player=new HumanPlayer(name, sts[i-1], clrs[i-1]);
	        players.add(player);
	    }
	    if ((numberOfPlayers & 1) == 1) {
	        AIPlayer player2=new AIPlayer(localizedString("cc_ai_name"), sts[numberOfPlayers], PlayerType.ALPHABETA, clrs[numberOfPlayers]);
	        players.add(player2);
	    }
	    
	    GameParameters params=new GameParameters(players, players.get(0));
	    gameManager.startGame(params);
	    
	    this.setupBoard();
	    
	    //prompt to start
	    CCSprite arrow=spriteFromExpansionFile("image/activities/strategy/chinesecheckers/arrow.png");
	    arrow.setScale(slotHeight*3/arrow.getContentSize().height);
	    arrow.setPosition(this.pawnScreenLocation(104));
	    super.addChild(arrow, zPawn+2);
	    CCDelayTime idleAction = CCDelayTime.action(1.5f);
	    CCScaleTo scaleAction = CCScaleTo.action(0.5f, 0.2f);
	    CCCallFuncN actionDone = CCCallFuncN.action(this, "removeMe");
	    arrow.runAction(CCSequence.actions(idleAction, scaleAction, actionDone));
	    
	    this.updatePrompt();
	}
	public void update(float dt) {
	    if (!gameManager.isGameRunning())
	        return;
	    delta += dt;
	    if (delta < 1.0f)
	        return;
	    Player currentPlayer=gameManager.currentPlayer();
	    if (currentPlayer.isAI()) {
	        MiddleLeftBehindHeuristic f=new MiddleLeftBehindHeuristic();
	        int depth=(mLevel > 3)?3:mLevel;
	
	        //AlphaBetaBeginEnd does not work well when the search depth is high
	        //AlphaBetaBeginEnd *algorithm=[[AlphaBetaBeginEnd alloc] initWith:f searchDepth:depth userTT:YES];
	        AlphaBeta algorithm=new AlphaBeta(f, depth, true);
	        
	        algorithm.setGameCore(gameManager);
	        AIMove move=algorithm.calculateMove(gameManager.currentBoard(), currentPlayer);
	        
	        CCSprite _picked=null;
	        for (CCNode sprite : floatingSprites) {
	            if (sprite.getTag()==kTagPawn+move.getSource()) {
	                _picked=(CCSprite)sprite;
	                break;
	            }
	        }
	        highlight1.setPosition(pawnScreenLocation(move.getSource()));
	        highlight1.setVisible(true);
	        highlight2.setPosition(pawnScreenLocation(move.getTarget()));
	        highlight2.setVisible(true);
	        this.play(_picked, move.getSource(), move.getTarget());
	    }
	}
	public boolean ccTouchesBegan(MotionEvent event) {
	    if (!gameManager.isGameRunning())
	        return true;
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
	    if (rcBoard.contains(p1.x, p1.y)) {
	        int slot = this.screenLocation2Pawn(p1);
	        Player player=null;
	        try {
		        SlotType playingST = gameManager.currentBoard().getSlotType(slot);
	            player=gameManager.getPlayerBySlotType(playingST);
	        }
	        catch (Throwable e) {
	            player=null;
	        }
	        if (player!=null && !player.isAI() && gameManager.currentPlayer().equals(player)) {
	            for (CCNode sprite : floatingSprites) {
	                if (sprite.getTag()==kTagPawn+slot) {
	                    picked=(CCSprite)sprite;
	                    break;
	                }
	            }
	            highlight1.setPosition(picked.getPosition());
	            highlight1.setVisible(true);
	            highlight2.setVisible(false);
	        }
	        else if (picked!=null) {
	            int dst = this.screenLocation2Pawn(p1);
	            int src=picked.getTag()-kTagPawn;
	
	            if (this.play(picked, src, dst)) {
	                picked=null;
	
	                highlight2.setPosition(this.pawnScreenLocation(dst));
	                highlight2.setVisible(true);
	            }
	        }
	        else {
	            playSound("audio/sounds/brick.wav");
	        }
	    }
	    return true;
	}
	
	
	 //* Paints the board with a Graphics2D.
	 //* @param g the Graphics2D to paint
	private void setupBoard() {
	    Board b=gameManager.currentBoard();
	
	    for (int i = 0; i < 121; i++) {
	        SlotType st = b.getSlotType(i);
	        if (st != SlotType.EMPTY) {
	        	Player player=null;
	        	try {
	        		player=gameManager.getPlayerBySlotType(st);
	        	}
	        	catch (Throwable ignore) {
	        	}
	        	if (player != null) {
		            CCSprite sprite=this.getPawn(i, player.getColor());
		            sprite.setTag(kTagPawn+i);
		            super.addChild(sprite, zPawn);
		            floatingSprites.add(sprite);
	        	}
	        }
	    }
	}
	
	private CCSprite getPawn(int pawn, int c) {
		int r=(c >> 16) & 0xff;
		int g=(c >> 8) & 0xff;
		int b=(c & 0xff);
	    
	    CCSprite sprite=spriteFromExpansionFile("image/activities/strategy/chinesecheckers/ball.png");
	    sprite.setScaleX(pawnDiameter/sprite.getContentSize().width);
	    sprite.setScaleY(pawnDiameter/sprite.getContentSize().height);
	    sprite.setColor(ccColor3B.ccc3(r, g, b));
	    sprite.setPosition(this.pawnScreenLocation(pawn));
	
	    return sprite;
	}
	private CGPoint pawnScreenLocation(int pawn) {
	    Coordinate coord = gameManager.currentBoard().getCoordinate(pawn);
	    float x = rcBoard.origin.x+coord.getRow()*slotWidth + slotWidth/2+xOffset;
	    float y = rcBoard.origin.y+(16-coord.getLine())*slotHeight + slotHeight/2+ yOffset;
	    
	    return CGPoint.ccp(x, y);
	}
	private int screenLocation2Pawn(CGPoint p1) {
	    int x=(int)((p1.x-rcBoard.origin.x-xOffset)/slotWidth);
	    int y=(int)((p1.y-rcBoard.origin.y-yOffset)/slotHeight);
	    int slot = ArrayBoard.getIntegerSlot(x, 16-y);
	    
	    return slot;
	}
	private boolean play(CCSprite thePicked, int src, int dst) {
	    if (!gameManager.isGameRunning())
	        return false;
	    
	    CkMove detailed=null;
	    if (dst >= 0) {
	        SlotType playingST = gameManager.currentBoard().getSlotType(src);
	        Player player=null;
	        try {
	        	player=gameManager.getPlayerBySlotType(playingST);
	        }
	        catch (Throwable ignore) {
	        }
	        if (gameManager.currentPlayer().equals(player)) {
	            CkMove m = new MoveImpl(src, dst, player);
	            java.util.List<CkMove> possibleMoves = MoveGenerator.possibleMoves(gameManager.currentBoard(), player, src);
	            for (CkMove mov : possibleMoves) {
	                if (mov.equals(m)) {
	                    detailed=mov;
	                    break;
	                }
	            }
	        }
	    }
	    boolean ret= (detailed!=null)?gameManager.playMove(detailed):false;
	    boolean moved=false;
	    if (ret) {
	        delta=0;
	        //good to move
	        super.playSound("audio/sounds/drip.wav");
	        thePicked.setPosition(pawnScreenLocation(dst));
	        thePicked.setTag(kTagPawn+dst);
	        //display the movement, first remove any previous movement
	        for (int i = floatingSprites.size() - 1; i >= 0; --i) {
	            CCNode one=floatingSprites.get(i);
	            if (one.getTag()==kTagMovementTrace) {
	                one.removeFromParentAndCleanup(true);
	                floatingSprites.remove(i);
	            }
	        }
	        //draw the lines
	        java.util.ArrayList<Integer> movements=new java.util.ArrayList<Integer>();
	        movements.addAll(detailed.getJumpedSlots());
	        
	        movements.add(Integer.valueOf(dst));
	        CGPoint ptLast=this.pawnScreenLocation(src);
	        for (Integer number : movements) {
	            int slot=number.intValue();
	            CGPoint pt=this.pawnScreenLocation(slot);
	            LineSprite line=new LineSprite(ptLast, pt);
	            line.setLineWidth(2);
	            line.setClr(new ccColor4F(0, 0, 0, 1.0f));
	            line.setTag(kTagMovementTrace);
	            super.addChild(line, zPawn+1);
	            floatingSprites.add(line);
	            
	            ptLast=pt;
	        }
	        
	        this.updatePrompt();
	        
	        if (gameManager.gameIsOver()) {
	            Player winner=gameManager.getWinner();
	            boolean humanWin=winner.isHuman();
	            super.flashAnswerWithResult(humanWin, humanWin, null, null,2);
	        }
	        moved=true;
	    }
	    else {
	        //put it back
	        playSound("audio/sounds/brick.wav");
	        moved=false;
	    }
	    return moved;
	}
	private void updatePrompt() {
	    Player currentPlayer=gameManager.currentPlayer();
	    String str="=>"+currentPlayer.getName();
	    prompt.setString(str);
	    prompt.setPosition(prompt.getContentSize().width/2+4, szWin.height-topOverhead()-prompt.getContentSize().height*4);
	    int clr=currentPlayer.getColor();
	    int r=(clr>>16)&0xff;
	    int g=(clr>>8)&0xff;
	    int b=(clr&0xff);
	    prompt.setColor(ccColor3B.ccc3(r, g, b));
	}
	public void removeMe(Object _sender) {
		CCNode sender=(CCNode)_sender;
		sender.removeFromParentAndCleanup(true);
	}
	//restart the game
	public void repeat(Object sender) {
	    this.initGame(false, sender);
	}
	public void specifyNumberOfPlayer(Object _sender) {
		CCNode sender=(CCNode)_sender;
	
	    numberOfPlayers=sender.getTag();
	    
	    CCSprite bg=(CCSprite)sender.getParent().getUserData();
	    sender.getParent().removeFromParentAndCleanup(true);
	    floatingSprites.remove(sender.getParent());
	    bg.removeFromParentAndCleanup(true);
	    floatingSprites.remove(bg);
	
	    this.initPlayers();
	}
}
