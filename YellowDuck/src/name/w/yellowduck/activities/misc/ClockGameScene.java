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

import name.w.yellowduck.Category;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;

import android.view.MotionEvent;

public class ClockGameScene extends name.w.yellowduck.YDActLayerBase {
	private final int kTagBg                      =0;
	private final int kTagClock                   =1;

    private ClockSprite clockSprite;
    private CCLabel currentTimeLabel;
    
    private CGPoint clockCenter;
    private float clockRadius;
    
    private int hour, minute, second;
    private boolean  secondHand;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ClockGameScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
		super.onEnter();
	    mMaxLevel=5;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionOk);

	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}

	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    currentTimeLabel=null;
	
	    CCSprite bgSprite=null;
	    //current time
	    int _hour=0, _minute=0, _second=0;
	    int _whichBg=0;
	    boolean _timeRule=false, _detailTimeRule=false;
	    switch (mLevel) {
	        case 1:
	            _whichBg=0;
	            _timeRule=true;
	            _detailTimeRule=true;
	            //preset time
	            secondHand=false;
	            _hour=super.nextInt(12);
	            _minute=super.nextInt(60)/5*5;
	            _second=0;
	            //target time
	            hour=super.nextInt(12);
	            minute=super.nextInt(60)/5*5;
	            second=0;
	            break;
	        case 2:
	            _whichBg=0;
	            _timeRule=true;
	            _detailTimeRule=true;
	            secondHand=true;
	            _hour=super.nextInt(12);
	            _minute=super.nextInt(60)/5*5;
	            _second=super.nextInt(60);
	            //target time
	            hour=super.nextInt(12);
	            minute=super.nextInt(60)/5*5;
	            second=super.nextInt(60);
	            break;
	        case 3:
	            _whichBg=1;
	            _timeRule=true;
	            _detailTimeRule=true;
	            secondHand=true;
	            _hour=super.nextInt(12);
	            _minute=super.nextInt(60)/5*5;
	            _second=super.nextInt(60);
	            //target time
	            hour=super.nextInt(12);
	            minute=super.nextInt(60)/5*5;
	            second=super.nextInt(60);
	            break;
	        case 4:
	            _whichBg=1;
	            _timeRule=true;
	            _detailTimeRule=false;
	            secondHand=true;
	            _hour=super.nextInt(12);
	            _minute=super.nextInt(60)/5*5;
	            _second=super.nextInt(60);
	            //target time
	            hour=super.nextInt(12);
	            minute=super.nextInt(60)/5*5;
	            second=super.nextInt(60);
	            break;
	        case 5:
	            _whichBg=1;
	            _timeRule=false;
	            _detailTimeRule=false;
	            secondHand=true;
	            _hour=super.nextInt(12);
	            _minute=super.nextInt(60)/5*5;
	            _second=super.nextInt(60);
	            //target time
	            hour=super.nextInt(12);
	            minute=super.nextInt(60)/5*5;
	            second=super.nextInt(60);
	            break;
	    }
	    bgSprite=this.setupClock(_whichBg,_timeRule,_detailTimeRule);
	    clockSprite.setTimeRule(_timeRule);
	    clockSprite.setDetailedTimeRule(_detailTimeRule);
	    clockSprite.setTimeHour(_hour, _minute, _second);
	    clockSprite.setSecondHand(secondHand);
	    this.displayCurrentTime();
	    //target time
	    int _hour_=hour;
	    if (_hour_ == 0)
	        _hour_=12;
	    String font=super.sysFontName();
	    String time= secondHand ? String.format("%02d:%02d:%02d", _hour_, minute, second) : String.format("%02d:%02d", _hour_, minute);
	    CCLabel labelTime = CCLabel.makeLabel(time, font, super.mediumFontSize());
	    labelTime.setColor(ccColor3B.ccBLACK);
	    float bgWidth=bgSprite.getContentSize().width*bgSprite.getScale();
	    float bgHeight=bgSprite.getContentSize().height*bgSprite.getScale();
	    //the left-bottom coordination of the background
	    float x0=bgSprite.getPosition().x-bgWidth/2;
	    float y0=bgSprite.getPosition().y-bgHeight/2;
	    labelTime.setPosition(x0+128.0f/800*bgWidth, y0+44.0f/521*bgHeight);
	    super.addChild(labelTime, 2);
	    floatingSprites.add(labelTime);
	
	    CCLabel labelPrompt = CCLabel.makeLabel(super.localizedString("label_set_watch"), font, super.smallFontSize());
	    labelPrompt.setColor(ccColor3B.ccBLACK);
	    labelPrompt.setPosition(labelTime.getPosition().x, y0+88.0f/521*bgHeight);
	    super.addChild(labelPrompt,2);
	    floatingSprites.add(labelPrompt);
	}
	public void ok(Object sender) {
	    boolean done=false;
	    if (hour == clockSprite.getHour() && minute == clockSprite.getMinute() && second==clockSprite.getSecond()) {
	        done=true;
	    }
	    super.flashAnswerWithResult(done, done, null, null, 2);
	}
	private String bgResource(int which) {
	    return String.format("image/activities/discovery/miscelaneous/clockgame/clockgame-bg%d.png", which);
	}
	
	private CCSprite setupClock(int which, boolean timeRule, boolean detailTimeRule) {
	    float width=szWin.width;
	    float height=szWin.height-super.topOverhead()-super.bottomOverhead();
	
	    CCSprite bgSprite=spriteFromExpansionFile(this.bgResource(which));
	    float scale1=width / bgSprite.getContentSize().width;
	    float scale2=height/ bgSprite.getContentSize().height;
	    float scale=(scale2 > scale1) ? scale1 : scale2;
	    bgSprite.setScale(scale);
	    bgSprite.setPosition(szWin.width/2, super.bottomOverhead()+bgSprite.getContentSize().height*scale/2);
	    bgSprite.setTag(kTagBg);
	    super.addChild(bgSprite,1);
	    floatingSprites.add(bgSprite);
	
	    clockCenter=CGPoint.ccp(bgSprite.getPosition().x - bgSprite.getContentSize().width*scale/2 + bgSprite.getContentSize().width*scale*415/801,
	                               bgSprite.getPosition().y - bgSprite.getContentSize().height*scale/2 + bgSprite.getContentSize().height*scale*(521-250)/521);
	    clockRadius=bgSprite.getContentSize().width*scale*174/800;
	    clockSprite=new ClockSprite(clockCenter, clockRadius);
	    clockSprite.setTag(kTagClock);
	    super.addChild(clockSprite,3);
	    floatingSprites.add(clockSprite);
	    
	    if (timeRule) {
	        for (int i = 0; i < 360; i+= 30) {
	            CGPoint pt1=CGPoint.ccp(clockRadius * (ClockSprite.kRulePosition-0.1f), 0);
	            pt1=super.rotatePoint(pt1, i);
	            pt1.x += clockCenter.x; pt1.y += clockCenter.y;
	            
	            int _hour = (i - 90) / (- 30);
	            if (_hour < 0)
	                _hour += 12;
	            _hour %= 12;
	            if (_hour == 0)
	                _hour=12;
	            
	            CCLabel ruleLabel = CCLabel.makeLabel(""+_hour, super.sysFontName(), 10);
	            ruleLabel.setColor(ccColor3B.ccBLUE);
	            ruleLabel.setPosition(pt1);
	            super.addChild(ruleLabel, 2);
	            floatingSprites.add(ruleLabel);
	        }
	    }
	    
	    if (detailTimeRule) {
	        for (int i = 0; i < 360; i+= 6) {
	            CGPoint pt1=CGPoint.ccp(clockRadius * 0.96f, 0);
	            pt1=super.rotatePoint(pt1, i);
	            pt1.x += clockCenter.x; pt1.y += clockCenter.y;
	
	            int _second =(i - 90) / (-6);
	            if (_second < 0)
	                _second += 60;
	            _second %= 60;
	            if (_second == 0)
	                _second=60;
	            boolean bigFont=((i/6)%5)==0;
	            CCLabel ruleLabel = CCLabel.makeLabel(""+ _second, super.sysFontName(), bigFont?8:6);
	            ruleLabel.setColor(ccColor3B.ccRED);
	            ruleLabel.setPosition(pt1);
	            super.addChild(ruleLabel,2);
	            floatingSprites.add(ruleLabel);
	        }
	    }
	
	    return bgSprite;
	}
	
	private void displayCurrentTime(){
	    int _hour=clockSprite.getHour();
	    int _minute=clockSprite.getMinute();
	    int _second=clockSprite.getSecond();
	    if (_hour == 0)
	        _hour=12;
	    
	    String time= secondHand ? String.format("%02d:%02d:%02d", _hour, _minute, _second) : String.format("%02d:%02d", _hour, _minute);
	    if (currentTimeLabel == null) {
	        currentTimeLabel = CCLabel.makeLabel(time, super.sysFontName(),super.smallFontSize());
	        currentTimeLabel.setColor(ccColor3B.ccBLACK);
	        currentTimeLabel.setPosition(clockCenter.x, clockCenter.y + clockRadius * 0.5f);
	        super.addChild(currentTimeLabel, 2);
	        floatingSprites.add(currentTimeLabel);
	    }
	    currentTimeLabel.setString(time);
	}
	
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
        clockSprite.graspHand(p1);
        return true;
	}	

	public boolean ccTouchesMoved(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
        clockSprite.moveHand(p1);
        this.displayCurrentTime();
        return true;
	}	
	
	public boolean ccTouchesEnded(MotionEvent event) {
		CGPoint p1 = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));	
        clockSprite.moveHand(p1);
        this.displayCurrentTime();
        return true;
	}		
}
