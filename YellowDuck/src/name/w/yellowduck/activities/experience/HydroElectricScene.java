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


package name.w.yellowduck.activities.experience;

import name.w.yellowduck.Category;
import name.w.yellowduck.YDConfiguration;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor3B;

import android.view.MotionEvent;

public class HydroElectricScene extends name.w.yellowduck.YDActLayerBase {
	private final int zItemAboveBg        =5;

	private final int kTagWindMill        =6;
	private final int kTagWaterPower      =10;
	private final int kTagWinPower        =11;
	private final int kTagSolarPower      =12;
	private final int kTagOutputPower     =13; //the power that customers can use

//clickable items
	private final int kTuxHome            =100;
	private final int kCloud2             =101;
	private final int kSolarPanel         =102;


	private final int kPowerTuxHome           =100;
	private final int kPowerSwing             =400;
	private final int kPowerBuilding          =800;

	private final int kPowerWaterOutput       =1000;
	private final int kPowerWindOutput        =600;
	private final int kPowerSolarOutput       =400;

	private final int kTimeSolarPower         =40; //seconds to turn off solar power, this is also defines the speed of the sun movement
	private final int kTimeWindPower          =50; //seconds to turn off wind power
	private final int kTimeWaterPower         =60; //seconds to turn off water power
	private final int kTimeCloudMovement      =8;

	
	private final int kStepIdle                   =0;

	private final int kStep2ClickSun              =1;
	private final int kStepWaitingRain            =2;
	private final int kStep2ClickTurbine          =3; //water power starting point
	private final int kStep2ClickCloud            =4; //wind power
	private final int kStep2ClickPanel            =5; //solar power
	private final int kStepRainning               =6;
	private final int kStepRainStopped            =7;
	private final int kStep2ClickTransformer      =8;
	private final int kStep2ClickDnTransformer    =9;
	private final int kStepPowerReady             =10;
	
    private CCSprite sun, lightsoff, lightson, cloud, drops, river, cloud1, cloud2, solarpanel, buildingon, swingon;
    private CCLabel labelWaterPower, labelWindPower, labelSolarPower, labelOutputPower;
    private CCLabel labelTuxHomeRequired, labelSwingRequired, labelBuildingRequired;
    private CGPoint ptSunRises;
    
    private int stepRain, stepWaterPower, stepWindPower, stepSolarPower;
    private boolean solarAvailable;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new HydroElectricScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=3;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.shufflePlayBackgroundMusic();
	    CCSprite spriteBg=super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    spriteBg.removeFromParentAndCleanup(true);
	    super.addChild(spriteBg, 2);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons);
	
	    //the sky
	    CCSprite spriteSky=spriteFromExpansionFile("image/activities/experience/hydroelectric/sky.png");
	    spriteSky.setScaleX(spriteBg.getScaleX());
	    spriteSky.setScaleY(spriteBg.getScaleY());
	    spriteSky.setPosition(szWin.width/2, szWin.height-spriteSky.getContentSize().height*spriteSky.getScaleY()/2);
	    super.addChild(spriteSky, 0);
	    //the sun
	    sun=spriteFromExpansionFile("image/activities/experience/hydroelectric/sun.png");
	    sun.setScale(60.0f/525*szWin.width/sun.getContentSize().width);
	    sun.setPosition(87.0f/1024*szWin.width, 466.0f/666*szWin.height-sun.getContentSize().height*sun.getScale()*0.25f);
	    super.addChild(sun, 1);
	    ptSunRises=sun.getPosition();
	    
	    //main building lights on
	    buildingon=super.setupBackground("image/activities/experience/hydroelectric/building_on.png", kBgModeFit);
	    buildingon.removeFromParentAndCleanup(true);
	    super.addChild(buildingon, 3);
	    buildingon.setVisible(false);
	    //swing building lights on
	    swingon=super.setupBackground("image/activities/experience/hydroelectric/swing_on.png", kBgModeFit);
	    swingon.removeFromParentAndCleanup(true);
	    super.addChild(swingon, 3);
	    swingon.setVisible(false);
	    //tux home
	    lightsoff=spriteFromExpansionFile("image/activities/experience/hydroelectric/lightoff.png");
	    lightsoff.setScale(116.0f/1024*szWin.width/lightsoff.getContentSize().width);
	    lightsoff.setPosition(842.0f/1024*szWin.width + lightsoff.getContentSize().width*lightsoff.getScale()/2, 280.0f/666*szWin.height);
	    lightsoff.setVisible(false);
	    super.addChild(lightsoff, zItemAboveBg);
	
	    lightson=spriteFromExpansionFile("image/activities/experience/hydroelectric/lighton.png");
	    lightson.setScale(lightsoff.getScale());
	    lightson.setPosition(lightsoff.getPosition());
	    lightson.setVisible(false);
	    super.addChild(lightson, zItemAboveBg);
	
	    //labels
	    labelWaterPower=this.setupPowerLabel(451, 387, 0);
	    labelWindPower =this.setupPowerLabel(820, 546, 0);
	    labelSolarPower =this.setupPowerLabel(920, 394, 0);
	    //on the step down transformer
	    labelOutputPower =this.setupPowerLabel(703, 359, 0);
	    //required power
	    labelTuxHomeRequired=this.setupPowerLabel(826,250,kPowerTuxHome);
	    labelSwingRequired=this.setupPowerLabel(460, 265, kPowerSwing);
	    labelBuildingRequired=this.setupPowerLabel(660, 270, kPowerBuilding);
	    
	    super.setIsTouchEnabled(true);
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
		super.clearFloatingSprites();
	    
	    sun.stopAllActions();
	    sun.setPosition(ptSunRises);
	    cloud=drops=river=cloud1=cloud2=solarpanel=null;
	    solarAvailable=false;
	    lightsoff.setVisible(false);
	    lightson.setVisible(false);
	    buildingon.setVisible(false);
	    swingon.setVisible(false);
	    
	    labelWindPower.setVisible(false);
	    labelSolarPower.setVisible(false);
	    labelSwingRequired.setVisible(false);
	    labelBuildingRequired.setVisible(false);
	    if (mLevel >= 2) {
	        labelWindPower.setVisible(true);
	        labelSwingRequired.setVisible(true);
	    }
	    if (mLevel >= 3) {
	        labelSolarPower.setVisible(true);
	        labelBuildingRequired.setVisible(true);
	    }
	
	    if (mLevel >= 2) {
	        CCSprite more=super.setupBackground("image/activities/experience/hydroelectric/wind_power.png", kBgModeFit);
	        more.removeFromParentAndCleanup(true);
	        super.addChild(more,  3);
	        floatingSprites.add(more);
	        
	        cloud1=spriteFromExpansionFile("image/activities/experience/hydroelectric/cloud-1.png");
	        cloud1.setScale(sun.getContentSize().width * sun.getScaleX() / cloud1.getContentSize().width);
	        cloud1.setPosition(780.0f/1024*szWin.width, 590.0f/666*szWin.height);
	        super.addChild(cloud1,3);
	        floatingSprites.add(cloud1);
	        
	        cloud2=spriteFromExpansionFile("image/activities/experience/hydroelectric/cloud-2.png");
	        cloud2.setScale(cloud1.getScale());
	        cloud2.setPosition(cloud1.getPosition());
	        cloud2.setVisible(false);
	        super.addChild(cloud2, 3);
	        floatingSprites.add(cloud2);
	
	        //wind mill
	        {
	            CCSprite blades=spriteFromExpansionFile("image/activities/experience/hydroelectric/blades.png");
	            blades.setScale(30.0f*preferredContentScale(true) / blades.getContentSize().width);
	            blades.setPosition(870.0f/1024*szWin.width,550.0f/666*szWin.height);
	            blades.setTag(kTagWindMill);
	            super.addChild(blades,3);
	            floatingSprites.add(blades);
	        }
	        {
	            CCSprite blades=spriteFromExpansionFile("image/activities/experience/hydroelectric/blades.png");
	            blades.setScale(28.0f*preferredContentScale(true) / blades.getContentSize().width);
	            blades.setPosition(918.0f/1024*szWin.width,532.0f/666*szWin.height);
	            blades.setTag(kTagWindMill);
	            super.addChild(blades,3);
	            floatingSprites.add(blades);
	        }
	        {
	            CCSprite blades=spriteFromExpansionFile("image/activities/experience/hydroelectric/blades.png");
	            blades.setScale(26.0f*preferredContentScale(true) / blades.getContentSize().width);
	            blades.setPosition(966.0f/1024*szWin.width,524.0f/666*szWin.height);
	            blades.setTag(kTagWindMill);
	            super.addChild(blades,3);
	            floatingSprites.add(blades);
	        }
	
	        stepWindPower=kStep2ClickCloud;
	    }
	    if (mLevel >= 3) {
	        CCSprite more=setupBackground("image/activities/experience/hydroelectric/solar_power.png", kBgModeFit);
	        more.removeFromParentAndCleanup(true);
	        super.addChild(more, 3);
	        floatingSprites.add(more);
	        
	        solarpanel=spriteFromExpansionFile("image/activities/experience/hydroelectric/solar_panel.png");
	        solarpanel.setScale(46.0f/800*szWin.width/solarpanel.getContentSize().width);
	        solarpanel.setPosition(656.0f/800*szWin.width, 340.0f/525*szWin.height);
	        super.addChild(solarpanel,3);
	        floatingSprites.add(solarpanel);
	
	        stepSolarPower=kStep2ClickPanel;
	    }
	    //the tux heading home
	    CCSprite tux=spriteFromExpansionFile("image/activities/experience/hydroelectric/boat_sailing.png");
	    tux.setScale(70.0f/666*szWin.height/tux.getContentSize().height);
	    tux.setPosition(0, tux.getContentSize().height*tux.getScale()/2+15.0f/666*szWin.height);
	    super.addChild(tux,zItemAboveBg);
	    floatingSprites.add(tux);
	
	    CCMoveTo moveAction=CCMoveTo.action(8, CGPoint.ccp(930.f/1024*szWin.width, tux.getPosition().y));
	    CCCallFuncN doneAction = CCCallFuncN.action(this, "arrivedHome");
	    tux.runAction(CCSequence.actions(moveAction, doneAction));
	    super.playSound("audio/sounds/Harbor1.wav");
	    
	    stepRain=kStep2ClickSun;
	    stepWaterPower=kStepWaitingRain;
	    stepWindPower=kStep2ClickCloud;
	    stepSolarPower=kStep2ClickPanel;
	    
	    this.updatePowerLabels();
	}
	
	public boolean ccTouchesEnded(MotionEvent event) {
		CGPoint pt = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
        int clickedAt=this.getClickedItem(pt);
        String msg=null; boolean transformerOn=false;
        if (clickedAt >= 0) {
            super.playSound("audio/sounds/bleep.wav");
            switch (clickedAt) {
                case 0: //turbine
                    msg=localizedString("hydroelectric_turbine");
                    flashMsg(msg,4.0f);
                    if (stepWaterPower == kStep2ClickTurbine) {
                        this.setupPowerIndicator(412,320,kTagWaterPower);
                        stepWaterPower=kStep2ClickTransformer;
                    }
                    break;
                case 1: //transform
                    msg=localizedString("hydroelectric_transformer");
                    flashMsg(msg,4.0f);
                    if (stepWaterPower == kStep2ClickTransformer) {
                        this.setupPowerIndicator(614, 384, kTagWaterPower);
                        stepWaterPower=kStep2ClickDnTransformer;
                    }
                    break;
                case 2: //step down tranform
                    msg=localizedString("hydroelectric_dntransformer");
                    flashMsg(msg,4.0f);
                    transformerOn=false;
                    if (stepWaterPower == kStep2ClickDnTransformer) {
                        stepWaterPower=kStepPowerReady;
                        transformerOn=true;
                    }
                    else if (mLevel >= 2 && stepWindPower == kStep2ClickDnTransformer) {
                        stepWindPower=kStepPowerReady;
                        transformerOn=true;
                    }
                    else if (mLevel >= 3 && stepSolarPower == kStep2ClickDnTransformer) {
                        stepSolarPower=kStepPowerReady;
                        transformerOn=true;
                    }
                    if (transformerOn) {
                        this.setupPowerIndicator(690, 318, kTagOutputPower);
                        this.setupPowerIndicator(784, 306, kTagOutputPower);
                    }
                    break;
                case kTuxHome:
                case 7:
                    if (stepWaterPower == kStepPowerReady || stepWindPower == kStepPowerReady || stepSolarPower == kStepPowerReady) {
                        this.turnTuxHomeLightsOn(null);
                    }
                    break;
                case 5://building
                    if (mLevel >= 3) {
                        if (stepWaterPower == kStepPowerReady || stepWindPower == kStepPowerReady || stepSolarPower == kStepPowerReady) {
                            this.turnBuildingLightsOn(null);
                        }
                    }
                    break;
                case 6://swing building
                    if (mLevel >= 2) {
                        if (stepWaterPower == kStepPowerReady || stepWindPower == kStepPowerReady || stepSolarPower == kStepPowerReady) {
                            this.turnSwingLightsOn(null);
                        }
                    }
                    break;
                case kCloud2:
                    if (mLevel >= 2) {
                        if (stepWindPower == kStep2ClickCloud) {
                            cloud2.setVisible(true);
                            cloud1.setVisible(false);
                            stepWindPower=kStep2ClickTurbine;
                        }
                    }
                    break;
                case 8:
                    if (mLevel >= 2) {
                        msg=localizedString("hydroelectric_windturbine");
                        flashMsg(msg,4.0f);
                        if (stepWindPower == kStep2ClickTurbine) {
                        	CCDelayTime idleAction = CCDelayTime.action(kTimeWindPower);
                            CCCallFuncN actionDone = CCCallFuncN.action(this, "turnoffWindPower");
                            cloud2.runAction(CCSequence.actions(idleAction, actionDone));
                            
                            for (CCNode node : floatingSprites) {
                                if (node.getTag()==kTagWindMill) {
                                	CCRotateBy rotateAction=CCRotateBy.action(1, 60);
                                    node.runAction(CCRepeatForever.action(rotateAction));
                                }
                            }
                            this.setupPowerIndicator(892, 497, kTagWinPower);
                            stepWindPower=kStep2ClickTransformer;
                        }
                    }
                    break;
                    case 3://transformer for wind power
                    if (mLevel >= 2) {
                        msg=localizedString("hydroelectric_transformer");
                        flashMsg(msg,4.0f);
                        if (stepWindPower == kStep2ClickTransformer) {
                            this.setupPowerIndicator(784, 446, kTagWinPower);
                            stepWindPower=kStep2ClickDnTransformer;
                        }
                    }
                    break;
                    case kSolarPanel:
                        if (mLevel >= 3) {
                            msg=localizedString("hydroelectric_solarpanel");
                            flashMsg(msg,4.0f);
                            if (stepSolarPower == kStep2ClickPanel && solarAvailable) {
                                solarpanel.setColor(ccColor3B.ccRED);
                                this.setupPowerIndicator(840, 392, kTagSolarPower);
                                stepSolarPower=kStep2ClickTransformer;
                            }
                        }
                    break;
                    case 4://solar power transformer
                    if (mLevel >= 3) {
                        msg=localizedString("hydroelectric_transformer");
                        flashMsg(msg,4.0f);
                        if (stepSolarPower == kStep2ClickTransformer) {
                            this.setupPowerIndicator(826, 354, kTagSolarPower);
                            stepSolarPower=kStep2ClickDnTransformer;
                        }
                    }
                    break;
            }
        }
        else {
            if (stepRain == kStep2ClickSun || stepRain == kStepRainStopped) {
                if (this.hit(sun, pt)) {
                    stepRain=kStepIdle;
                    super.playSound("audio/sounds/bleep.wav");
                    this.makeSunRises();
                }
            }
            else if (stepRain == kStep2ClickCloud) {
                if (this.hit(cloud, pt)) {
                    super.playSound("audio/sounds/Water5.wav");
                    this.startRaining();
                }
            }
        }//not cliked on fixed items
	    
	    this.updatePowerLabels();
	    return true;
	}
	
	private void makeSunRises() {
		CCMoveTo moveAction=CCMoveTo.action(kTimeSolarPower*0.2f, CGPoint.ccp(sun.getPosition().x, szWin.height-sun.getContentSize().height*sun.getScaleY()/2));
		CCCallFuncN doneAction = CCCallFuncN.action(this,"noon");
	    sun.runAction(CCSequence.actions(moveAction, doneAction));
	
	    solarAvailable=true;
	}
	private void startRaining() {
	    
	    if (drops == null) {
	        drops=spriteFromExpansionFile("image/activities/experience/hydroelectric/drops.png");
	        drops.setScale(cloud.getContentSize().width * cloud.getScaleX() / drops.getContentSize().width);
	        drops.setPosition(cloud.getPosition().x, cloud.getPosition().y - cloud.getContentSize().height*cloud.getScaleY()/2-drops.getContentSize().height*drops.getScaleY()/2);
	        super.addChild(drops,zItemAboveBg);
	        floatingSprites.add(drops);
	    }
	    
	    //the river
	    if (river == null) {
	        river=super.setupBackground("image/activities/experience/hydroelectric/river.png", kBgModeFit);
	        river.removeFromParentAndCleanup(true);
	        super.addChild(river, 3);
	        floatingSprites.add(river);
	    }
	    if (river!=null) {
	    	CCDelayTime idleAction = CCDelayTime.action(kTimeWaterPower);
	    	CCCallFuncN actionDone = CCCallFuncN.action(this,"turnoffWaterPower");
	        river.runAction(CCSequence.actions(idleAction, actionDone));
	    }
	    
	    stepRain=kStepRainning;
	    //NSString *msg=localizedString("watercycle_rain", nil);
	    //flashMsg(msg,6.0f);
	
	    CCDelayTime idleAction = CCDelayTime.action(6.0f);
	    CCCallFuncN actionDone = CCCallFuncN.action(this,"stopRainning");
	    drops.runAction(CCSequence.actions(idleAction, actionDone));
	    
	    
	    if (stepWaterPower==kStepWaitingRain)
	        stepWaterPower=kStep2ClickTurbine;
	}
	
	public void noon(Object _sender) {
	    //the vapor
	    CCSprite vapor=spriteFromExpansionFile("image/activities/experience/hydroelectric/vapor.png");
	    vapor.setScale(sun.getContentSize().width * sun.getScaleX() / vapor.getContentSize().width);//same width as the sun
	    vapor.setPosition(ptSunRises);
	    super.addChild(vapor,zItemAboveBg);
	    floatingSprites.add(vapor);
	
	    CCMoveTo moveAction=CCMoveTo.action(3.0f, CGPoint.ccp(vapor.getPosition().x, szWin.height-vapor.getContentSize().height*vapor.getScaleY()/2));
	    CCCallFuncN doneAction = CCCallFuncN.action(this,"cloudBorn");
	    vapor.runAction(CCSequence.actions(moveAction, doneAction));
	    //NSString *msg=localizedString("watercycle_heat", nil);
	    //flashMsg(msg,6.0f);
	}
	public void sunset(Object _sender) {
	    solarAvailable=false;
	    this.turnoffSolarPower(null);
	}
	
	public void cloudBorn(Object _sender) {
		CCNode node =(CCNode)_sender;
	    //remove vapor
	    node.removeFromParentAndCleanup(true);
	
	    cloud=spriteFromExpansionFile("image/activities/experience/hydroelectric/cloud.png");
	    cloud.setScale(sun.getContentSize().width * sun.getScaleX() / cloud.getContentSize().width);
	    cloud.setPosition(ptSunRises.x, szWin.height-cloud.getContentSize().height*cloud.getScale()/2);
	    super.addChild(cloud,zItemAboveBg);
	    floatingSprites.add(cloud);
	    //move to middle
	    CCMoveTo moveAction=CCMoveTo.action(kTimeCloudMovement,CGPoint.ccp(450.0f/1024*szWin.width, cloud.getPosition().y));
	    CCCallFuncN doneAction = CCCallFuncN.action(this,"beforeRaining");
	    cloud.runAction(CCSequence.actions(moveAction, doneAction));
	}
	public void beforeRaining(Object _sender) {
	    //sunset
	    CCMoveTo moveAction=CCMoveTo.action(kTimeSolarPower*0.8f, ptSunRises);
	    CCCallFuncN doneAction = CCCallFuncN.action(this,"sunset");
	    sun.runAction(CCSequence.actions(moveAction, doneAction));
	    
	    stepRain=kStep2ClickCloud;
	}
	public void stopRainning(Object _sender) {
	    if (cloud!=null) {
	        cloud.removeFromParentAndCleanup(true);
	        floatingSprites.remove(cloud);
	        cloud=null;
	    }
	    if (drops!=null) {
	        drops.removeFromParentAndCleanup(true);
	        floatingSprites.remove(drops);
	        drops=null;
	    }
	    stepRain=kStepRainStopped;
	    if (mLevel <= 1)
	        this.prompt2click(369, 363);
	}
	
	public void turnTuxHomeLightsOff(Object _sender) {
	    lightsoff.setVisible(true);
	    lightson.setVisible(false);
	}
	
	public void turnTuxHomeLightsOn(Object _sender) {
	    if (this.tryTurnOn(kPowerTuxHome)) {
	        lightsoff.setVisible(false);
	        lightson.setVisible(true);
	        
	        this.checkAnswer();
	    }
	}
	public void turnBuildingLightsOn(Object _sender) {
	    if (this.tryTurnOn(kPowerBuilding)) {
	        buildingon.setVisible(true);
	        this.checkAnswer();
	    }
	}
	
	public void turnBuildingLightsOff(Object _sender) {
	    buildingon.setVisible(false);
	}
	
	public void turnSwingLightsOn(Object _sender) {
	    if (this.tryTurnOn(kPowerSwing)) {
	        swingon.setVisible(true);
	        this.checkAnswer();
	    }
	}
	public void turnSwingLightsOff(Object _sender) {
	    swingon.setVisible(false);
	}
	
	private void checkAnswer(){
	    boolean gamewon=false;
	    if (mLevel == 1 && lightson.getVisible())
	        gamewon=true;
	    else if (mLevel == 2) {
	        gamewon=lightson.getVisible() && swingon.getVisible();
	    }
	    else if (mLevel==3) {
	        gamewon=lightson.getVisible() && swingon.getVisible() && buildingon.getVisible();
	    }
	    if (gamewon)
	        flashAnswerWithResult(true, true, null, null, 2);
	}
	private boolean tryTurnOn(int requirement) {
	    int outputpower=this.getTotalOutputPower();
	    int requiredpower=this.getTotalRequiredPower();
	    if (requiredpower+requirement <= outputpower) {
	        return true;
	    }
	    else {
	        this.turnoffOutputPower();
	        String msg=localizedString("hydroelectric_notenoughpower");
	        flashMsg(msg,3.0f);
	
	        this.updatePowerLabels();
	    }
	    return false;
	}
	public void turnoffWaterPower(Object _sender) {
	    this.removePowerIndicator(kTagWaterPower);
	    stepWaterPower=kStepWaitingRain;
	    this.tryTurnOn(0);
	    this.updatePowerLabels();
	}
	
	public void turnoffWindPower(Object _sender){
	    if (cloud1!=null && cloud2!=null) {
	        cloud2.setVisible(false);
	        cloud1.setVisible(true);
	    }
	    for (CCNode node : floatingSprites) {
	        if (node.getTag()==kTagWindMill) {
	            node.stopAllActions();
	        }
	    }
	    this.removePowerIndicator(kTagWinPower);
	    stepWindPower=kStep2ClickCloud;
	    this.tryTurnOn(0);
	    this.updatePowerLabels();
	}
	public void turnoffSolarPower(Object _sender) {
	    if (solarpanel!=null) {
	        solarpanel.setColor(ccColor3B.ccWHITE);
	    }
	    this.removePowerIndicator(kTagSolarPower);
	    stepSolarPower=kStep2ClickPanel;
	    this.tryTurnOn(0);
	    this.updatePowerLabels();
	}
	private void turnoffOutputPower() {
	    lightsoff.setVisible(true);
	    lightson.setVisible(false);
	    buildingon.setVisible(false);
	    swingon.setVisible(false);
	    this.removePowerIndicator(kTagOutputPower);
	
	    //step back
	    if (stepWaterPower == kStepPowerReady)
	        stepWaterPower=kStep2ClickDnTransformer;
	    if (stepWindPower == kStepPowerReady)
	        stepWindPower=kStep2ClickDnTransformer;
	    if (stepSolarPower == kStepPowerReady)
	        stepSolarPower=kStep2ClickDnTransformer;
	}
	private void removePowerIndicator(int tag) {
	    int total=floatingSprites.size();
	    for (int i = total - 1; i >= 0; --i) {
	        CCNode node=floatingSprites.get(i);
	        if (node.getTag() == tag) {
	            floatingSprites.remove(i);
	            node.removeFromParentAndCleanup(true);
	        }
	    }
	}
	
	public void arrivedHome(Object _sender) {
		CCNode node=(CCNode)_sender;
	
	    node.removeFromParentAndCleanup(true);
	    floatingSprites.remove(node);
	    
	    CCSprite boat=spriteFromExpansionFile("image/activities/experience/hydroelectric/boat.png");
	    boat.setScale(node.getScale());
	    boat.setPosition(node.getPosition());
	    super.addChild(boat,zItemAboveBg);
	    floatingSprites.add(boat);
	
	    //home lights off
	    lightsoff.setVisible(true);
	    super.playSound("audio/sounds/Harbor3.wav");
	    
	    if (mLevel == 1) {
	        this.prompt2clickAt(sun.getPosition());
	    }
	    else if (mLevel == 2) {
	        this.prompt2clickAt(cloud2.getPosition());
	    }
	    else if (mLevel == 3) {
	        this.prompt2clickAt(solarpanel.getPosition());
	    }
	}
	public void removeMe(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    sender.removeFromParentAndCleanup(true);
	    floatingSprites.remove(sender);
	}
	
	private void setupPowerIndicator(float x, float y, int tag) {
	    CCSprite sprite=spriteFromExpansionFile("image/activities/experience/hydroelectric/shock.png");
	    sprite.setPosition(1.0f*x/1024*szWin.width, 1.0f*y/666*szWin.height);
	    sprite.setTag(tag);
	    super.addChild(sprite,zItemAboveBg);
	    floatingSprites.add(sprite);
	}
	private int getClickedItem(CGPoint pt) {
	    float positions[]={369,363,66,66, //0:tubine
	                       458,356,66,56, //1:transformer
	                       760,360,66,66,//2:step down transformer
	                       824,518,40,40,//3:transformer for wind power
	                       886,390,46,38,//4:transformer for wind power
	                        579,307,94,82,//5:building
	                        520,254,86,66,//6:swing
	                        825,273,35,35, //7:also tux home
	                        913,509,104,82 //8:wind turbine
	                        };
	    for (int i = 0; i < positions.length; i+=4) {
	        float x=1.0f * positions[i] / 1024 * szWin.width;
	        float y=1.0f * positions[i+1] /666 * szWin.height;
	        float w=1.0f * positions[i+2]/1024 * szWin.width;
	        float h=1.0f * positions[i+3]/ 666 * szWin.height;
	        
	        CGRect rc=CGRect.make(x-w/2,y-h/2, w, h);
	        if (rc.contains(pt.x, pt.y)) {
	            return i/4;
	        }
	    }
	    if (this.hit(lightson, pt))
	        return kTuxHome;
	    else if (this.hit(cloud2, pt))
	        return kCloud2;
	    else if (this.hit(solarpanel,pt))
	        return kSolarPanel;
	
	    return -1;
	}
	private boolean hit(CCNode sprite, CGPoint pt) {
	    if (sprite == null)
	        return false;
	    return super.isNodeHit(sprite, pt);
	}

	private int getTotalOutputPower() {
	    boolean oneReady=(stepWaterPower == kStepPowerReady || stepWindPower == kStepPowerReady || stepSolarPower == kStepPowerReady);
	    int transformerOutput=0;
	    if (oneReady) {
	        if (stepWaterPower == kStep2ClickDnTransformer || stepWaterPower == kStepPowerReady)
	            transformerOutput += kPowerWaterOutput;
	        if (stepWindPower == kStep2ClickDnTransformer || stepWindPower == kStepPowerReady)
	            transformerOutput += kPowerWindOutput;
	        if (stepSolarPower == kStep2ClickDnTransformer || stepSolarPower == kStepPowerReady)
	            transformerOutput += kPowerSolarOutput;
	    }
	    return transformerOutput;
	}
	private int getTotalRequiredPower() {
	    int power=0;
	    if (lightson.getVisible())
	        power += kPowerTuxHome;
	    if (swingon.getVisible())
	        power += kPowerSwing;
	    if (buildingon.getVisible())
	        power += kPowerBuilding;
	    return power;
	}
	
	private CCLabel setupPowerLabel(float x0, float y0, int p) {
	    float x=1.0f * x0 / 1024 * szWin.width;
	    float y=1.0f * y0 / 666  * szWin.height;
	    
	    CCLabel label=CCLabel.makeLabel(String.format("%dW", p), super.sysFontName(), 8*preferredContentScale(true));
	    label.setColor((p>0)?ccColor3B.ccRED:ccColor3B.ccWHITE);
	    label.setPosition(x, y);
	    super.addChild(label,zItemAboveBg);
	
	    return label;
	}
	
	private void updatePowerLabels() {
	    this.updateLabel(labelWaterPower, (stepWaterPower > kStep2ClickTransformer)?kPowerWaterOutput:0);
	    if (mLevel >= 2) {
	        this.updateLabel(labelWindPower, (stepWindPower > kStep2ClickTransformer)?kPowerWindOutput:0);
	    }
	    if (mLevel >= 3) {
	        this.updateLabel(labelSolarPower, (stepSolarPower > kStep2ClickTransformer)?kPowerSolarOutput:0);
	    }
	    int totaloutput=this.getTotalOutputPower();
	    labelOutputPower.setString(String.format("%dW", totaloutput));
	}
	private void updateLabel(CCLabel theLabel, int power) {
	    theLabel.setString(String.format("%dW", power));
	}
	
	private void prompt2clickAt(CGPoint pt) {
	    //flash the start
	    CCSprite sparkle=spriteFromExpansionFile("image/misc/star.png");
	    sparkle.setPosition(pt);
	    sparkle.setScale(0.2f);
	    super.addChild(sparkle, 7);
	    floatingSprites.add(sparkle);
	    CCScaleTo scaleUpAction=CCScaleTo.action(2, 2);
	    CCScaleTo scaleDownAction=CCScaleTo.action(1, 0.2f);
	    CCCallFuncN actionDone = CCCallFuncN.action(this, "removeMe");
	    sparkle.runAction(CCSequence.actions(scaleUpAction, scaleDownAction, actionDone));
	}
	
	private void prompt2click(int x, int y) {
	    float x0=1.0f * x / 1024 * szWin.width;
	    float y0=1.0f * y / 666 * szWin.height;
	    
	    this.prompt2clickAt(CGPoint.ccp(x0, y0));
	}
	
}
