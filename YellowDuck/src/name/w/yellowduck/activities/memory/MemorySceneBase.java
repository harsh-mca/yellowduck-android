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


package name.w.yellowduck.activities.memory;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import android.view.MotionEvent;

public abstract class MemorySceneBase  extends name.w.yellowduck.YDActLayerBase {
	protected final int kJump 				  =8000;
	private final int kTagCardBack            =-1;
	private final int kTagCardEmpty           =-2;

	protected int rows, cols;
    protected int cardWidth, cardHeight;
    
    private CCNode visibleCard1, visibleCard2;//the card
    //set it to true when we are playing an audio clip
    private boolean busy;
	
    private String backCard, emptyCard;
    private CGRect rcDisplayArea;
    private boolean audioAttached;
    private int clipPlaying;
    
    public String getBackCard() {
		return backCard;
	}

	public void setBackCard(String backCard) {
		this.backCard = backCard;
	}

	public String getEmptyCard() {
		return emptyCard;
	}

	public void setEmptyCard(String emptyCard) {
		this.emptyCard = emptyCard;
	}

	public CGRect getRcDisplayArea() {
		return rcDisplayArea;
	}

	public void setRcDisplayArea(CGRect rcDisplayArea) {
		this.rcDisplayArea = rcDisplayArea;
	}

	public boolean isAudioAttached() {
		return audioAttached;
	}

	public void setAudioAttached(boolean audioAttached) {
		this.audioAttached = audioAttached;
	}
	
	public void onEnter() {
		super.onEnter();
		//default card image
    	backCard="image/activities/discovery/memory_group/memory/backcard.png";
    	emptyCard="image/activities/discovery/memory_group/memory/emptycard.png";
    	//full screen
	    rcDisplayArea=CGRect.make(0, bottomOverhead(), szWin.width, szWin.height-topOverhead()-bottomOverhead());
	    this.testCardSize();
	}

	protected void testCardSize() {
	    CCSprite empty_=spriteFromExpansionFile(this.emptyCard);
	    cardWidth=(int)empty_.getContentSize().width;
	    cardHeight=(int)empty_.getContentSize().height;
	    empty_.cleanup();
	    empty_=null;
	}
	
	protected void preInitMemoryGame() {
		//supports up to 9 levels
	    switch (mLevel) {
        case 1:cols=3; rows=2;break;
        case 2:cols=4; rows=2;break;
        case 3:cols=5; rows=2;break;
        case 4:cols=4; rows=3;break;
        case 5:cols=6; rows=3;break;
        case 6:cols=5; rows=4;break;
        case 7:cols=6; rows=4;break;
        case 8:cols=7; rows=4;break;
        case 9:cols=8; rows=4;break;
	    }
    }
    
	protected void postInitMemoryGame() {
	    //random selections
	    for (int i = 0; i < floatingSprites.size(); ++i) {
	        int first=super.nextInt(floatingSprites.size());
	        int second=super.nextInt(floatingSprites.size());
	        if (first != second) {
	        	CCNode firstCard=floatingSprites.get(first);
	        	CCNode secondCard=floatingSprites.get(second);
	        	floatingSprites.remove(first);
	        	floatingSprites.add(first, secondCard);
	        	floatingSprites.remove(second);
	        	floatingSprites.add(second, firstCard);
	        }
	    }
	    if (cardWidth <= 0 || cardHeight <= 0)
	    	this.testCardSize();
	    int margin=10;//top & left margins
	    int xRoom=(int)(rcDisplayArea.size.width - margin * 2) / cols;
	    int yRoom=(int)(rcDisplayArea.size.height - margin * 2) / rows;
	    float scale1=1.0f * xRoom / cardWidth;
	    float scale2=1.0f * yRoom / cardHeight;
	    float scale=(scale2 > scale1) ? scale1 : scale2;
	    if (scale > 1.5f)
	        scale = 1.5f;
	    scale *= 0.9f; //leave some space between cards
	    for (int y=0; y < rows; ++y) {
	        for (int x=0; x < cols; ++x) {
	            int location=y * cols + x;
	            CCNode sprite=floatingSprites.get(location);
	            if (sprite.getContentSize().width > cardWidth*scale || sprite.getContentSize().height > cardHeight*scale) {
	                scale1=cardWidth *scale / sprite.getContentSize().width;
	                scale2=cardHeight*scale / sprite.getContentSize().height;
	                sprite.setScale((scale2>scale1)?scale1:scale2);
	            }
	            sprite.setPosition(rcDisplayArea.origin.x + margin+xRoom * x + xRoom/2,  rcDisplayArea.origin.y + rcDisplayArea.size.height - margin - y * yRoom - yRoom / 2);
	            super.addChild(sprite,2);

	            //the cover
	            CCSprite back=spriteFromExpansionFile(this.backCard);
	            back.setScale(scale);
	            back.setPosition(sprite.getPosition());
	            back.setTag(kTagCardBack);
	            super.addChild(back,3);
	            floatingSprites.add(back);
	
	            //empty card
	            CCSprite empty=spriteFromExpansionFile(this.emptyCard);
	            empty.setScale(scale);
	            empty.setPosition(sprite.getPosition());
	            empty.setTag(kTagCardEmpty);
	            super.addChild(empty,1);
	            floatingSprites.add(empty);
	            
	            back.setUserData(empty); //associate with the card
	            empty.setUserData(sprite);//link them together
	        }
	    }
	    visibleCard1=visibleCard2=null;
	}
	
	public boolean ccTouchesEnded(MotionEvent event) {
	    if (busy)
	        return true;
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
	    
        CCNode clicked=null;
        for (CCNode card : floatingSprites) {
            if (card.getTag() == kTagCardBack) {
                if (super.isNodeHit(card, p1)) {
                    clicked=card;
                    break;
                }
            }
        }
        if (clicked!=null) {
            if (audioAttached) {
                super.stopSoundOrVoice(clipPlaying);
                
                CCSprite empty=(CCSprite)clicked.getUserData();
                CCNode card=(CCNode)empty.getUserData();
                clipPlaying=super.playSound((String)card.getUserData());
            }
            else {
            	super.playSound("audio/sounds/bleep.wav");
            }
            if (visibleCard1!=null && visibleCard2!=null) {
                //already selected two cards
                if (visibleCard1 == clicked) {
                    //hide card2
                    this.toggleCard(visibleCard2);
                    visibleCard2=null;
                }
                else if (visibleCard2 == clicked) {
                    this.toggleCard(visibleCard1);
                    visibleCard1=visibleCard2;
                    visibleCard2=null;
                }
                else {
                    this.toggleCard(visibleCard1);
                    this.toggleCard(visibleCard2);
                    visibleCard1=clicked;
                    visibleCard2=null;
                    this.toggleCard(clicked);
                }
            }
            else if (visibleCard1!=null) {
                if (visibleCard1 != clicked) {
                    visibleCard2=clicked;
                    this.toggleCard(clicked);
                    
                    CCSprite emptyCard1=(CCSprite)visibleCard1.getUserData();
                    CCSprite emptyCard2=(CCSprite)visibleCard2.getUserData();
                    
                    CCSprite card1=(CCSprite)emptyCard1.getUserData();
                    CCSprite card2=(CCSprite)emptyCard2.getUserData();
                    
                    if ((card1.getTag() == card2.getTag()) || (Math.abs(card1.getTag() - card2.getTag())==kJump)) {
                        //a match
                        this.matched();
                    }
                }
            }
            else {
                visibleCard1=clicked;
                this.toggleCard(clicked);
            }
        }
        return true;
    }
	
	private void toggleCard(CCNode cardBack) {
	    cardBack.setVisible(!cardBack.getVisible());
	}
	
	public void removeCard(Object sender) {
		CCSprite cardBack=(CCSprite)sender;
	    CCSprite empty=(CCSprite)cardBack.getUserData();
	    CCSprite card=(CCSprite)empty.getUserData();
	    card.removeFromParentAndCleanup(true);
	    cardBack.removeFromParentAndCleanup(true);
	    empty.removeFromParentAndCleanup(true);
	    
	    floatingSprites.remove(card);
	    floatingSprites.remove(cardBack);
	    floatingSprites.remove(empty);
	}
	
	private void matched() {
		if (!this.audioAttached)
			super.playSound("audio/sounds/flip.wav");
	    busy=true;
	    super.performSelector("removeCards", 1);
	}
	
	public void removeCards() {
	    this.removeCard(visibleCard1);
	    this.removeCard(visibleCard2);
	    visibleCard1=null; visibleCard2=null;
	    if (floatingSprites.size() <= 1) {
	        super.flashAnswerWithResult(true,  true,  null,  null,  2.0f);
	    }
	    busy=false;
	}
}
