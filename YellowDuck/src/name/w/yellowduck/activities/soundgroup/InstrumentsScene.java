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


package name.w.yellowduck.activities.soundgroup;

import name.w.yellowduck.Category;
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


public class InstrumentsScene extends name.w.yellowduck.YDActLayerBase {
	
	private final int kMaxInstrumentsPerLevel=10;
	private class Instrument extends Object {
		
		private int level;
		private String name;
		private String svgImg;
		private String music;
		private CCMenu menu;
		
		private Instrument(String name_, String svg_, String music_, int level_) {
			this.name=name_;
			this.svgImg="image/activities/discovery/sound_group/instruments/"+svg_;
			this.music="image/activities/discovery/sound_group/"+music_;
			this.level=level_;
		}
		public int getLevel() {
			return level;
		}
		public String getName() {
			return name;
		}
		public String getSvgImg() {
			return svgImg;
		}
		public String getMusic() {
			return music;
		}
		public CCMenu getMenu() {
			return menu;
		}
		public void setMenu(CCMenu m) {
			this.menu=m;
		}
	}

	private java.util.ArrayList<Instrument> instruments;
	private int selectionIdx[]=new int[kMaxInstrumentsPerLevel]; //max instruments which will appear in a level
    private int targetInstrumentIdx;

    private int instrumentSize;
    private CCLabel promptLabel;
    private int answerCorrect;
    private CCSprite mask;
    
    private int musicPlaying;
	
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new InstrumentsScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public InstrumentsScene() {
		super ();
        instruments=new java.util.ArrayList<Instrument>();
//1
        instruments.add(new Instrument("clarinet" ,"clarinet.png" ,"instruments/clarinet.mp3" ,1));
        instruments.add(new Instrument("flute_traversiere" ,"flute_traversiere.png" ,"instruments/flute_traversiere.mp3" ,1));
        instruments.add(new Instrument("guitar" ,"guitar.png" ,"instruments/guitar.mp3" ,1));
        instruments.add(new Instrument("harp" ,"harp.png" ,"instruments/harp.mp3" ,1));
        instruments.add(new Instrument("piano" ,"piano.png" ,"instruments/piano.mp3" ,2));
        instruments.add(new Instrument("saxophone" ,"saxophone.png" ,"instruments/saxophone.mp3" ,2));
        instruments.add(new Instrument("trombone" ,"trombone.png" ,"instruments/trombone.mp3" ,2));
        instruments.add(new Instrument("trumpet" ,"trumpet.png" ,"instruments/trumpet.mp3" ,2));
        instruments.add(new Instrument("violin" ,"violin.png" ,"instruments/violin.mp3" ,2));
        instruments.add(new Instrument("clarinet" ,"clarinet.png" ,"instruments/clarinet.mp3" ,3));
        instruments.add(new Instrument("flute_traversiere" ,"flute_traversiere.png" ,"instruments/flute_traversiere.mp3" ,3));
        instruments.add(new Instrument("guitar" ,"guitar.png" ,"instruments/guitar.mp3" ,3));
        instruments.add(new Instrument("harp" ,"harp.png" ,"instruments/harp.mp3" ,3));
        instruments.add(new Instrument("piano" ,"piano.png" ,"instruments/piano.mp3" ,3));
        instruments.add(new Instrument("saxophone" ,"saxophone.png" ,"instruments/saxophone.mp3" ,3));
        instruments.add(new Instrument("trombone" ,"trombone.png" ,"instruments/trombone.mp3" ,3));
        instruments.add(new Instrument("trumpet" ,"trumpet.png" ,"instruments/trumpet.mp3" ,3));
        instruments.add(new Instrument("violin" ,"violin.png" ,"instruments/violin.mp3" ,4));
        instruments.add(new Instrument("flute_traversiere" ,"flute_traversiere.png" ,"instruments/flute_traversiere.mp3" ,4));
        instruments.add(new Instrument("guitar" ,"guitar.png" ,"instruments/guitar.mp3" ,4));
        instruments.add(new Instrument("harp" ,"harp.png" ,"instruments/harp.mp3" ,4));
        instruments.add(new Instrument("piano" ,"piano.png" ,"instruments/piano.mp3" ,4));
        instruments.add(new Instrument("saxophone" ,"saxophone.png" ,"instruments/saxophone.mp3" ,4));
        instruments.add(new Instrument("trombone" ,"trombone.png" ,"instruments/trombone.mp3" ,4));
        instruments.add(new Instrument("trumpet" ,"trumpet.png" ,"instruments/trumpet.mp3" ,4));
        instruments.add(new Instrument("drum_kit" ,"drum_kit.png" ,"instruments/drum_kit.mp3" ,5));
        instruments.add(new Instrument("accordion" ,"accordion.png" ,"instruments/accordion.mp3" ,5));
        instruments.add(new Instrument("banjo" ,"banjo.png" ,"instruments/banjo.mp3" ,5));
        instruments.add(new Instrument("bongo" ,"bongo.png" ,"instruments/bongo.mp3" ,5));
        instruments.add(new Instrument("electric_guitar" ,"electric_guitar.png" ,"instruments/electric_guitar.mp3" ,5));
        instruments.add(new Instrument("castanets" ,"castanets.png" ,"instruments/castanets.mp3" ,5));
        instruments.add(new Instrument("drum_kit" ,"drum_kit.png" ,"instruments/drum_kit.mp3" ,6));
        instruments.add(new Instrument("accordion" ,"accordion.png" ,"instruments/accordion.mp3" ,6));
        instruments.add(new Instrument("banjo" ,"banjo.png" ,"instruments/banjo.mp3" ,6));
        instruments.add(new Instrument("cymbal" ,"cymbal.png" ,"instruments/cymbal.mp3" ,6));
        instruments.add(new Instrument("cello" ,"cello.png" ,"instruments/cello.mp3" ,6));
        instruments.add(new Instrument("bongo" ,"bongo.png" ,"instruments/bongo.mp3" ,7));
        instruments.add(new Instrument("electric_guitar" ,"electric_guitar.png" ,"instruments/electric_guitar.mp3" ,7));
        instruments.add(new Instrument("harmonica" ,"harmonica.png" ,"instruments/harmonica.mp3" ,7));
        instruments.add(new Instrument("horn" ,"horn.png" ,"instruments/horn.mp3" ,7));
        instruments.add(new Instrument("maracas" ,"maracas.png" ,"instruments/maracas.mp3" ,7));
        instruments.add(new Instrument("organ" ,"organ.png" ,"instruments/organ.mp3" ,7));
        instruments.add(new Instrument("snare_drum" ,"snare_drum.png" ,"instruments/snare_drum.mp3" ,8));
        instruments.add(new Instrument("timpani" ,"timpani.png" ,"instruments/timpani.mp3" ,8));
        instruments.add(new Instrument("triangle" ,"triangle.png" ,"instruments/triangle.mp3" ,8));
        instruments.add(new Instrument("horn" ,"horn.png" ,"instruments/horn.mp3" ,8));
        instruments.add(new Instrument("maracas" ,"maracas.png" ,"instruments/maracas.mp3" ,8));
        instruments.add(new Instrument("organ" ,"organ.png" ,"instruments/organ.mp3" ,8));
        instruments.add(new Instrument("snare_drum" ,"snare_drum.png" ,"instruments/snare_drum.mp3" ,9));
        instruments.add(new Instrument("timpani" ,"timpani.png" ,"instruments/timpani.mp3" ,9));
        instruments.add(new Instrument("triangle" ,"triangle.png" ,"instruments/triangle.mp3" ,9));
        instruments.add(new Instrument("tambourine" ,"tambourine.png" ,"instruments/tambourine.mp3" ,9));
        instruments.add(new Instrument("tuba" ,"tuba.png" ,"instruments/tuba.mp3" ,9));
	}

	public void onEnter() {
		super.onEnter();
	    mMaxLevel=9;
	    instrumentSize=(int)szWin.width / 6;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.stopBackgroundMusic();
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionRepeatButton|kOptionOk);
	    super.setupBackground(activeCategory.getBg(), kBgModeFit);
	    	    
	    promptLabel = CCLabel.makeLabel("X", super.sysFontName(), super.mediumFontSize());
	    promptLabel.setPosition(szWin.width/2, szWin.height-promptLabel.getContentSize().height);
	    super.addChild(promptLabel,1);
	    
	    mask=spriteFromExpansionFile("image/misc/selectionmask.png");
	    mask.setScale(1.0f * instrumentSize/mask.getContentSize().width);
	    mask.setVisible(false);
	    super.addChild(mask, 2);
	    
	    super.afterEnter();
	}
	
	public void onExit() {
	    super.stopSoundOrVoice(musicPlaying);
	    super.onExit();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);
	    
	    super.stopSoundOrVoice(musicPlaying);
	    musicPlaying=0;
	    
	    int instrumentsThisLevel=0, startIdx=-1;
	    for (int i=0; i < instruments.size(); ++i) {
	        Instrument one=instruments.get(i);
	        if (one.getMenu()!=null) {
	            one.getMenu().removeFromParentAndCleanup(true);
	            one.setMenu(null);
	        }
	        if (one.getLevel() == mLevel) {
	            if (startIdx < 0)
	                startIdx=i;
	            ++instrumentsThisLevel;
	        }
	    }
	    
	    for (int i = 0; i < instrumentsThisLevel; ++i)
	        selectionIdx[i]=startIdx+i;
	    //random instruments
	    super.randomIt(selectionIdx,instrumentsThisLevel);
	
	    float xPos=instrumentSize;
	    float yPos=szWin.height - instrumentSize;
	    for (int i = 0; i < instrumentsThisLevel; ++i) {
	        int idx=selectionIdx[i];
	        Instrument instrument=instruments.get(idx);
	        
	        String img=instrument.getSvgImg();
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(super.buttonize(img));
	        CCSprite spriteSelected=CCSprite.sprite(texture);
	        CCMenuItemSprite repeat=CCMenuItemImage.item(sprite, spriteSelected, this, "instrumentTouched");
	        repeat.setPosition(xPos + instrumentSize /2 , yPos - instrumentSize/2);
	        repeat.setTag(idx);
	        float scale1=instrumentSize/repeat.getContentSize().width;
	        float scale2=instrumentSize/repeat.getContentSize().height;
	        repeat.setScale((scale2 > scale1) ? scale1 : scale2);
	        CCMenu menu = CCMenu.menu(repeat);
	        menu.setPosition(0,0);
	        super.addChild(menu,3);
	        
	        instrument.setMenu(menu);
	
	        xPos += instrumentSize;
	        if ((i % 4) == 3) {
	            xPos = instrumentSize;
	            yPos -= instrumentSize;
	        }
	    }
	    
	    targetInstrumentIdx=selectionIdx[super.nextInt(instrumentsThisLevel)];
	    Instrument instrument=instruments.get(targetInstrumentIdx);
	    String entry="name_" + instrument.getName();
	    promptLabel.setString(String.format(localizedString("msg_find_instrument"), localizedString(entry)));
	    //delay to playback the music after the "fantastic" is played
	    super.performSelector("repeat", 2);
	    
	    mask.setVisible(false);
	    answerCorrect=100;
	}
	private void announce(Instrument instrument) {
	    if (instrument!=null && !super.isShuttingDown()) {
	        super.stopSoundOrVoice(musicPlaying);
	        musicPlaying=super.playSound(instrument.getMusic());
	    }
	}
	
	public void ok(Object _sender) {
	    if (answerCorrect <= 1) {
	        if (answerCorrect == 1) {
	            stopSoundOrVoice(musicPlaying);
	            musicPlaying=0;
	        }
	        flashAnswerWithResult((answerCorrect==1), (answerCorrect==1),null,null,2);
	    }
	    else {
	        //no instrument is selected yet
	        super.playVoice("misc/check_answer.mp3");
	    }
	}
	
	public void instrumentTouched(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    int instrumentIdx=sender.getTag();
	    Instrument instrument=instruments.get(instrumentIdx);
	    this.announce(instrument);
	
	    mask.setPosition(sender.getPosition());
	    mask.setVisible(true);
	    answerCorrect=(instrumentIdx == targetInstrumentIdx)?1:0;
	}
	
	//Override
	public void repeat(Object _sender) {
	    this.announce(instruments.get(targetInstrumentIdx));
	}
}
