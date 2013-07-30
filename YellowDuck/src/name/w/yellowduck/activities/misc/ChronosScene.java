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

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

public class ChronosScene extends ShapeGameBase {
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new ChronosScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}
	
	
	public void onEnter() {
	    super.onEnter();
	    mMaxLevel=4;
	    super.afterEnter();
	}
	
	protected void initGame(boolean firstTime, Object sender) {
		super.initGame(firstTime, sender);


	    java.util.ArrayList<String> items=new java.util.ArrayList<String>();
	    String bg=null, title=null;
	    int totalHotspots=0;
	    CGPoint hotspots[]=new CGPoint[10];
	    switch (mLevel) {
	        case 1:
	        {
	            int sel=super.nextInt(3);
	            if (sel == 0) {
	                bg="image/activities/discovery/miscelaneous/chronos/bg.jpg";
	                title="title_moonwalker";
	                items.add("1.png"); //0
	                items.add("audio:1.mp3");
	
	                items.add("2.png"); //1
	                items.add("audio:2.mp3");
	                
	                items.add("3.png"); //2
	                items.add("audio:3.mp3");
	
	                items.add("4.png"); //3
	                items.add("audio:2.mp3");
	                totalHotspots=4;
	            }
	            else if (sel == 1) {
	                bg=null;
	                title="title_seasons";
	                items.add("spring.png"); //0
	                items.add("label:season_spring");
	                
	                items.add("summer.png"); //1
	                items.add("label:season_summer");
	                
	                items.add("autumn.png"); //2
	                items.add("label:season_autumn");
	                
	                items.add("winter.png"); //3
	                items.add("label:season_winter");
	                totalHotspots=4;
	            }
	            else if (sel == 2) {
	                bg=null;
	                title="title_tux_gardening";
	                items.add("garden1.png"); //0
	                items.add("xx");
	                
	                items.add("garden2.png"); //1
	                items.add("xx");
	                
	                items.add("garden3.png"); //2
	                items.add("xx");
	                
	                items.add("garden4.png"); //3
	                items.add("xx");
	                totalHotspots=4;
	            }
	        }
	            break;
	        case 2:
	        {
	            bg=null;
	            title="title_tux_n_tree";
	            items.add("chronos-tuxtree1.png"); //0
	            items.add("xxx:1.mp3");
	            
	            items.add("chronos-tuxtree2.png"); //1
	            items.add("xxx:2.mp3");
	            
	            items.add("chronos-tuxtree3.png"); //2
	            items.add("xxx:3.mp3");
	            
	            items.add("chronos-tuxtree4.png"); //3
	            items.add("xxx:2.mp3");
	            totalHotspots=4;
	        }
	            break;
	        case 3:
	        {
	            int sel=super.nextInt(2);
	            if (sel == 0) {
	                bg=null;
	                title="title_date_invented";
	                items.add("fardier.png"); //0
	                items.add("label:date_fardier");
	                
	                items.add("st_rocket.png"); //1
	                items.add("label:date_rocket");
	                
	                totalHotspots=2;
	            }
	            else if (sel == 1) {
	                bg=null;
	                title="title_transportation"; //Transportation
	                items.add("mongolfiere.png"); //0
	                items.add("label:date_hot_air_ballon");//1783 Montgolfier brothers' hot air balloon
	
	                items.add("celerifere.png"); //1
	                items.add("label:date_celerifere"); //1791 Comte de Sivrac's Celerifere
	                
	                items.add("Eole.png"); //2
	                items.add("label:date_eole"); //??1880 Clement Ader's Eole
	                
	                items.add("helico_cornu.png"); //3
	                items.add("label:date_helicopter"); //1906 Paul Cornu First helicopter flight
	
	                
	                totalHotspots=4;
	            }
	        }
	            break;
	        case 4:
	        {
	            int sel=super.nextInt(4);
	            if (sel == 0) {
	                bg=null;
	                title="title_aviation"; //Aviation
	                items.add("Eole.png"); //1880 Clement Ader's Eole
	                items.add("label:date_eole");
	                
	                items.add("wright_flyer.png"); //1903 The Wright brothers' Flyer III
	                items.add("label:date_wright_flyer");
	
	                items.add("bleriot.png"); //1909 Louis Bleriot crosses the English Channel
	                items.add("label:date_bleriot");
	
	                
	                totalHotspots=3;
	            }
	            else if (sel == 1) {
	                bg=null;
	                title="title_aviation"; //Aviation
	                items.add("bell_X1.png"); //1947 Chuck Yeager breaks the sound-barrier
	                items.add("label:date_bell");
	                
	                items.add("lindbergh.png"); //1927 Charles Lindbergh crosses the Atlantic Ocean
	                items.add("label:date_rocket");
	                
	                items.add("rafale.png"); //1934 Hélène Boucher's speed record of 444km/h
	                items.add("label:date_rafale");
	                
	                
	                totalHotspots=3;
	            }
	            else if (sel == 2) {
	                bg=null;
	                title="title_cars"; //The car
	                items.add("bolle1878.png"); //1878 Léon Bollé's "La Mancelle"
	                items.add("label:date_bolle");
	                
	                items.add("fardier.png"); //1769 Cugnot's fardier
	                items.add("label:date_fardier");
	                
	                items.add("benz1885.png"); //1885 The first petrol car by Benz
	                items.add("label:date_benz");
	                
	                
	                totalHotspots=3;
	            }
	            else if (sel == 3) {
	                bg=null;
	                title="title_cars"; //Cars
	                items.add("renault1899.png"); //899 Renault "voiturette"
	                items.add("label:date_renault");
	                
	                items.add("lancia1923.png"); //1923 Lancia Lambda
	                items.add("label:date_lancia");
	                
	                items.add("1955ds19.png"); //1955 Citroën DS 19
	                items.add("label:date_ds");
	                
	                
	                totalHotspots=3;
	            }
	        }
	            break;
	    }
	    super.preGameInit(bg);
	    
	    float canvasWidth=szWin.width-separator;
	    float canvasHeight=szWin.height-super.topOverhead() - super.bottomOverhead();
	    float slotWidth=0, slotHeight=0;
	    if (totalHotspots == 2) {
	        hotspots[0]=CGPoint.ccp(0.5f, 0.75f);
	        hotspots[1]=CGPoint.ccp(0.5f, 0.25f);
	        slotWidth=canvasWidth;
	        slotHeight=(canvasHeight)/2;
	    }
	    else if (totalHotspots == 3) {
	        hotspots[0]=CGPoint.ccp(0.25f, 0.5f);
	        hotspots[1]=CGPoint.ccp(0.75f, 0.75f);
	        hotspots[2]=CGPoint.ccp(0.75f, 0.25f);
	        slotWidth=canvasWidth/2;
	        slotHeight=canvasHeight/2;
	    }
	    else if (totalHotspots == 4) {
	        hotspots[0]=CGPoint.ccp(0.25f, 0.75f);
	        hotspots[1]=CGPoint.ccp(0.75f, 0.75f);
	        hotspots[2]=CGPoint.ccp(0.25f, 0.25f);
	        hotspots[3]=CGPoint.ccp(0.75f, 0.25f);
	        slotWidth=canvasWidth/2;
	        slotHeight=(canvasHeight)/2;
	    }
	    slotWidth *= 0.9f;
	    slotHeight *= 0.9f;
	    for (int i = 0; i < totalHotspots; ++i) {
	        CGPoint center=CGPoint.ccp(separator+canvasWidth*hotspots[i].x, super.bottomOverhead() + canvasHeight*hotspots[i].y);
	        hotspots[i]=center;
	    }
	    for (int i = 0; i < items.size(); i+=2) {
	        YDShape stock=new YDShape("image/activities/discovery/miscelaneous/chronos/"+items.get(i),YDShape.kShapeStock);
	        stock.setPosition(hotspots[i/2]);
	        stock.setFit2(CGSize.make(slotWidth, slotHeight));
	        super.addShape(stock);
	        
	        String label=items.get(i+1);
	        boolean labeled=false;
	        if (label.startsWith("label:")) {
	            YDShape labelShape=new YDShape(super.localizedString(label.substring(6)), YDShape.kShapeLabelText);
	            labelShape.setPosition(CGPoint.ccp(hotspots[i/2].x, hotspots[i/2].y+20));
	            super.addShape(labelShape);
	            labeled=true;
	        }
	        /*
	        else if ([label hasPrefix:@"audio:")) {
	            [stock setSound:[NSString stringWithFormat:@"Assets/image/activities/discovery/miscelaneous/chronos/%@", [label substringFromIndex:6]]];
	        }
	        */
	        if (!labeled) {
	            //default label
	            YDShape labelShape=new YDShape(""+(i/2+1), YDShape.kShapeLabelText);
	            labelShape.setPosition(CGPoint.ccp(hotspots[i/2].x, hotspots[i/2].y+20));
	            super.addShape(labelShape);
	        }
	    }
	    super.postGameInit(true, false);
	    items.clear();
	    
	    this.drawTitle(super.localizedString(title));
	}
	
	private void drawTitle(String theTitle) {
	    if (theTitle == null)
	        return;
	    
	    int fontSize=super.mediumFontSize();
	    String font=super.sysFontName();
	
	    CCLabel titleLabel = CCLabel.makeLabel(theTitle, font, fontSize);
	    titleLabel.setColor(ccColor3B.ccBLACK);
	    titleLabel.setPosition(szWin.width/2, super.bottomOverhead()+titleLabel.getContentSize().height/2);
	    super.addChild(titleLabel,1);
	    floatingSprites.add(titleLabel);
	    //the title is too long
	    if (titleLabel.getContentSize().width > (szWin.width-separator)*0.8f) {
	        titleLabel.setScale((szWin.width-separator)*0.8f/titleLabel.getContentSize().width);
	    }
	}
}
