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


package name.w.yellowduck.activities.maze;

import name.w.yellowduck.Category;
import name.w.yellowduck.Schema;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;

import android.view.MotionEvent;

public class MazeScene extends name.w.yellowduck.YDActLayerBase {
	private final int NORTH       =1;
	private final int WEST        =2;
	private final int SOUTH       =4;
	private final int EAST        =8;
	private final int SET         =16;
	//private final int BAD         =32;
	//private final int WON         =64;
	private final int MAX_WIDTH   =37; //width
	private final int MAX_HEIGHT  =20; //height


	private final int kFlashTimeSeconds       =2;
	
    private int width;
    private int height;
    private int cellsize;
    //private int buffer;
    private int board_border_x, board_border_y;
    private int begin, end;
    
    //from top to down (south: y+1, east: x+1), Maze[x][y];
    private int Maze[][]=new int[MAX_WIDTH][MAX_HEIGHT];
    //private int position[][]=new int[MAX_WIDTH*MAX_HEIGHT][2];
    
    //private int flashes;
    private boolean  invisible;
    private CCSprite tuxSprite, exitSprite;
    private boolean exited;
	
    
    private int pos[]=new int[5];
    
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new MazeScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	public MazeScene() {
		super();
		super.setColor(new ccColor3B(0xff, 0xff, 0xff));
	}

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=20;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    invisible=activeCategory.getSettings()!=null && activeCategory.getSettings().equalsIgnoreCase("invisible");
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(),kBgModeTile);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|(invisible?kOptionRepeatButton:0)|kOptionArrows);
	
	    tuxSprite=spriteFromExpansionFile("image/activities/discovery/maze/2d/tux_top_south.png");
	    tuxSprite.setVisible(false);
	    super.addChild(tuxSprite,5);
	    
	    //the exit door
	    exitSprite=spriteFromExpansionFile("image/activities/discovery/maze/2d/flag.png");
	    exitSprite.setVisible(false);
	    super.addChild(exitSprite,3);
	
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	//Override
	public void toolbarBtnTouched(Object _sender) {
	    super.toolbarBtnTouched(_sender);
	    CCNode sender=(CCNode)_sender;
	    switch (sender.getTag()) {
	        case Schema.kSvgButtonHELP:
	            break;
	        case Schema.kSvgButtonLevelDn:
	            break;
	        case Schema.kSvgButtonLevelUp:
	            break;
	        case Schema.kSvgButtonRepeat:
	            this.flashMazeOnce();
	            break;
	        case Schema.kSvgArrowUp:
	            this.move(0,-1);
	            break;
	        case Schema.kSvgArrowLeft:
	            this.move(-1,0);
	            break;
	        case Schema.kSvgArrowRight:
	            this.move(1,0);
	            break;
	        case Schema.kSvgArrowDown:
	            this.move(0,1);
	            break;
	    }
	}
	
	protected void initGame(boolean firstTime, Object sender) {
	    super.initGame(firstTime, sender);
	    
	    this.initMaze();
	    //when user tap the repeat very quickly, we only perform the action only
	    super.clearFloatingSprites();
	    CCTexture2D wood=super.textureFromExpansionFile("image/activities/discovery/maze/2d/wood.png");
	    for (int j = 0; j < height; ++j) {
	        for (int i = 0; i < width; ++i) {
	            CGPoint pt=this.maze2ContentCoord(i,j);
	            if ((Maze[i][j] & NORTH)!=0) {
	                //top-left
	                int x=(int)(pt.x);
	                int y=(int)(pt.y + cellsize/2);
	
	                CCSprite sprite = CCSprite.sprite(wood);
	                sprite.setScale(1.0f * cellsize / sprite.getContentSize().width);
	                sprite.setPosition(x, y);
	                super.addChild(sprite,1);
	                floatingSprites.add(sprite);;
	            }
	            if ((Maze[i][j] & SOUTH)!=0) {
	                //bottom-left
	                int x=(int)(pt.x);
	                int y=(int)(pt.y - cellsize/2);

	                CCSprite sprite = CCSprite.sprite(wood);
	                sprite.setScale(1.0f * cellsize / sprite.getContentSize().width);
	                sprite.setPosition(x, y);
	                super.addChild(sprite,1);
	                floatingSprites.add(sprite);;
	            }
	            if ((Maze[i][j] & WEST)!=0) {
	                //top left
	                int x=(int)(pt.x - cellsize/2);
	                int y=(int)(pt.y);

	                CCSprite sprite = CCSprite.sprite(wood);
	                sprite.setScale(1.0f * cellsize / sprite.getContentSize().width);
	                sprite.setRotation(90);
	                sprite.setPosition(x, y);
	                super.addChild(sprite,1);
	                floatingSprites.add(sprite);;
	            }
	            if ((Maze[i][j] & EAST)!=0) {
	                //top right
	                int x =(int)(pt.x+ cellsize/2);
	                int y =(int)(pt.y);
	
	                CCSprite sprite = CCSprite.sprite(wood);
	                sprite.setScale(1.0f * cellsize / sprite.getContentSize().width);
	                sprite.setRotation(90);
	                sprite.setPosition(x, y);
	                super.addChild(sprite,1);
	                floatingSprites.add(sprite);;
	            }
	        }
	    }
	    
	    float scale1=(cellsize-4)/tuxSprite.getContentSize().width;
	    float scale2=(cellsize-4)/tuxSprite.getContentSize().height;
	    float scale=(scale1>scale2)?scale2:scale1;
	    tuxSprite.setScale(scale);
	    tuxSprite.setRotation(-90); //towards to east
	    tuxSprite.setPosition(this.maze2ContentCoord(0,begin));
	    tuxSprite.setVisible(true);
	    
	    
	    scale1=(cellsize-4)/exitSprite.getContentSize().width;
	    scale2=(cellsize-4)/exitSprite.getContentSize().height;
	    scale=(scale1>scale2)?scale2:scale1;
	    exitSprite.setScale(scale);
	    exitSprite.setPosition(this.maze2ContentCoord(width-1,end));
	    exitSprite.setVisible(true);
	    
	    super.playSound("audio/sounds/prompt.wav");
	    if (invisible) {
	        this.flashMazeOnce();
	    }
	    
	    exited=false;
	}
	private void initMaze() {
	    this.setlevelproperties();
	    for (int x=0; x<width; x++) {
	        for (int y=0; y <height; y++) {
	            Maze[x][y]=15;
	        }
	    }
	    this.generateMaze(super.nextInt(width), super.nextInt(height));
	    //remove set
	    for (int x=0; x< width;x++)
	    {
	        for (int y=0; y < height; y++)
	        {
	            Maze[x][y]&=~SET;
	        }
	    }
	    
	    begin=super.nextInt(height);
	    end=super.nextInt(height);
	}
	
	private void setlevelproperties() {
	    switch (mLevel) {
	        case 1:
	    {
	        width=4;
	        height=4;
	    }
	            break;
	        case 2:
	    {
	        width=5;
	        height=4;
	    }
	            break;
	        case 3:
	    {
	        width=5;
	        height=5;
	    }
	            break;
	        case 4:
	    {
	        width=6;
	        height=5;
	    }
	            break;
	        case 5:
	    {
	        width=6;
	        height=6;
	    }
	            break;
	        case 6:
	    {
	        width=6;
	        height=7;
	    }
	            break;
	        case 7:
	    {
	        width=7;
	        height=7;
	    }
	            break;
	        case 8:
	    {
	        width=8;
	        height=7;
	    }
	            break;
	        case 9:
	    {
	        width=8;
	        height=8;
	    }
	            break;
	        case 10:
	    {
	        width=9;
	        height=8;
	    }
	            break;
	        case 11:
	    {
	        width=9;
	        height=9;
	    }
	            break;
	        case 12:
	    {
	        width=10;
	        height=9;
	    }
	            break;
	        case 13:
	    {
	        width=10;
	        height=10;
	    }
	            break;
	        case 14:
	    {
	        width=16;
	        height=8;
	    }
	            break;
	        case 15:
	    {
	        width=14;
	        height=14;
	    }
	            break;
	        case 16:
	    {
	        width=16;
	        height=15;
	    }
	            break;
	        case 17:
	    {
	        width=17;
	        height=16;
	    }
	            break;
	        case 18:
	    {
	        width=18;
	        height=17;
	    }
	            break;
	        case 19:
	    {
	        width=19;
	        height=18;
	    }
	            break;
	        case 20:
	    {
	        width=19;
	        height=19;
	    }
	            break;
	    }//swith
	    
	    
	    int BASE_X2=(int)(szWin.width);
	    int BASE_Y2=(int)(szWin.height - super.topOverhead() - super.bottomOverhead()-6);
	    
	    int sz1=BASE_X2/width;
	    int sz2=BASE_Y2/height;
	    cellsize=(sz1 > sz2) ? sz2 : sz1;
	    cellsize=cellsize/2*2;
	    board_border_x=(int) (BASE_X2-width*cellsize)/2;
	    board_border_y=(int) (BASE_Y2-height*cellsize)/2;
	}
	
	private void generateMaze(int x, int y) {
	    int po;
	    Maze[x][y]= Maze[x][y] + SET;
	    po = this.isPossible(x,y);
	    while (pos[po]>0)
	    {
	        int nr = pos[po];
	        int ran, in;
	        in=super.nextInt(nr)+1;
	        //printf("random: %d en %d mogelijkheden\n", in, *po);
	        ran=pos[po + in];
	        if (nr>=1)
	            switch (ran)
	        {
	            case EAST:
	                Maze[x][y]&=~EAST;
	                Maze[x+1][y]&=~WEST;
	                this.generateMaze(x+1,y);
	                break;
	            case SOUTH:
	                Maze[x][y]&=~SOUTH;
	                Maze[x][y+1]&=~NORTH;
	                this.generateMaze(x, y+1);
	                break;
	            case WEST:
	                Maze[x][y]&=~WEST;
	                Maze[x-1][y]&=~EAST;
	                this.generateMaze(x-1, y);
	                break;
	            case NORTH:
	                Maze[x][y]&=~NORTH;
	                Maze[x][y-1]&=~SOUTH;
	                this.generateMaze(x, y-1);
	                break;
	                
	        }
	        po=this.isPossible(x,y);
	    }
	    
	}
	private int check(int x, int y) {
	    if ((Maze[x][y]&SET) != 0)
	        return 1;
	    else 
	    	return 0;
	}
	
	private int isPossible(int x, int y){
	    int wall=Maze[x][y];
	    wall&=~SET;
	    pos[0]=0;
	    if(x==0)
	    {
	        wall&=~WEST;
	    }
	    if (y==0)
	    {
	        wall&=~NORTH;
	    }
	    if(x==(width-1))
	    {
	        wall&=~EAST;
	    }
	    if (y==(height-1))
	    {
	        wall&=~SOUTH;
	    }
	    if ((wall&EAST)!=0)
	    {
	        if(this.check(x+1,y)==0)
	        {
	            pos[0]=pos[0]+1;
	            pos[(pos[0])]=EAST;
	        }
	    }
	    if ((wall&SOUTH) != 0)
	    {
	        if (this.check(x,y+1)==0)
	        {
	            pos[0]=pos[0]+1;
	            pos[(pos[0])]=SOUTH;
	        }
	    }
	    if ((wall&WEST)!=0)
	    {
	        if (this.check(x-1, y)==0)
	        {
	            pos[0]=pos[0]+1;
	            pos[(pos[0])]=WEST;
	        }
	    }
	    if ((wall&NORTH)!=0)
	    {
	        if (this.check(x, y-1)==0)
	        {
	            pos[0]=pos[0]+1;
	            pos[(pos[0])]=NORTH;
	        }
	    }
	    return 0;//&pos[0];
	}
	//The center of the square
	private CGPoint maze2ContentCoord(int x, int y) {
	    int x0=x*cellsize+board_border_x;
	    int y0=(int)(szWin.height - super.topOverhead() - (y*cellsize+board_border_y));
	
	    return CGPoint.ccp(x0+cellsize/2, y0-cellsize/2);
	}
	private CGPoint content2MazeCoord(int x, int y) {
	    int x0=(x-board_border_x)/cellsize;
	    int y0=((int)szWin.height-y-super.topOverhead()-board_border_y)/cellsize;
	    
	    return CGPoint.ccp(x0, y0);
	}
	
	public boolean ccTouchesBegan(MotionEvent event) {
		return true;
	}
	public boolean ccTouchesEnded(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
        CGPoint p2=this.content2MazeCoord((int)(p1.x), (int)(p1.y));
        //tap inside the maze
        if (p2.x >= 0 && p2.x < width && p2.y >= 0 && p2.y < height) {
            CGPoint p0=this.content2MazeCoord((int)(tuxSprite.getPosition().x), (int)(tuxSprite.getPosition().y));
            
            float xDiff=p1.x - tuxSprite.getPosition().x;
            if (xDiff < 0)
                xDiff = 0-xDiff;
            float yDiff=p1.y - tuxSprite.getPosition().y;
            if (yDiff < 0)
                yDiff = 0-yDiff;
            
            int xSteps=0, ySteps=0;
            if (xDiff > yDiff) {
                xSteps=(int)(p2.x-p0.x);
            }
            else if (xDiff < yDiff) {
                ySteps=(int)(p2.y-p0.y);
            }
            this.move(xSteps,ySteps);
        }
        return true;
	}
	
	public void missionAccomplished(Object _sender) {
	    super.flashAnswerWithResult(true, true, null, null, 1);
	}
	private void flashMazeOnce() {
	    for (CCNode node : floatingSprites) {
	        node.setVisible(true);
	    }
	    super.performSelector("hideMaze", kFlashTimeSeconds);
	}
	
	public void hideMaze(Object _sender) {
        for (CCNode node : floatingSprites) {
            node.setVisible(false);
        }
	}
	private void move(int xSteps, int ySteps) {
	    if ((xSteps == 0 && ySteps == 0) || exited)
	        return;
	    
	    CGPoint ptTux=this.content2MazeCoord((int)(tuxSprite.getPosition().x), (int)(tuxSprite.getPosition().y));
	    int x0=(int)(ptTux.x);
	    int y0=(int)(ptTux.y);
	    
	    int x1=x0, y1=y0;
	    
	    int rotation=0;
	    if (xSteps == 0) {
	        if (ySteps > 0) {
	            //move to south ---south
	            for (int i = 0; i <ySteps; ++i) {
	                if ((Maze[x1][y1] & SOUTH) == 0) {
	                    ++y1;
	                    if (y1 >= height - 1)
	                        break;
	                }
	                else
	                    break;
	            }
	            rotation=0;
	        }
	        else {
	            //move to north
	            for (int i = 0; i < 0-ySteps; ++i) {
	                if ((Maze[x1][y1] & NORTH) == 0) {
	                    --y1;
	                    if (y1 <= 0)
	                        break;
	                }
	                else
	                    break;
	            }
	            rotation=180;
	        }
	    }
	    else {
	        if (xSteps > 0) {
	            //move to right
	            for (int i = 0; i < xSteps; ++i) {
	                if ((Maze[x1][y1] & EAST) == 0) {
	                    ++x1;
	                    if (x1 >= width - 1)
	                        break;
	                }
	                else
	                    break;
	            }
	            rotation=-90;
	        }
	        else {
	            //move to left
	            for (int i = 0; i < 0-xSteps; ++i ) {
	                if ((Maze[x1][y1] & WEST) == 0) {
	                    --x1;
	                    if (x1 <= 0)
	                        break;
	                }
	                else
	                    break;
	            }
	            rotation=90;
	        }
	    }
	    //check the final position
	    if (x1 != x0 || y1 != y0) {
	        CGPoint dst=this.maze2ContentCoord(x1,y1);
	        CCMoveTo move = CCMoveTo.action(0.5f, dst);
	        tuxSprite.setRotation(rotation);
	        super.playSound("audio/sounds/prompt.wav");
	        if (x1 == width-1 && y1 == end) {
	            exited=true;
	            CCCallFuncN doneAction = CCCallFuncN.action(this, "missionAccomplished");
	            tuxSprite.runAction(CCSequence.actions(move, doneAction));
	        }
	        else {
	            tuxSprite.runAction(move);
	        }
	    }
	    else {
	        //cannot move
	        super.playSound("audio/sounds/brick.wav");
	    }
	}
}
