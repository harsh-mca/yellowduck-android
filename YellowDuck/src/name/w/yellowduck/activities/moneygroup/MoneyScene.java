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


package name.w.yellowduck.activities.moneygroup;

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
import org.cocos2d.types.ccColor3B;

public class MoneyScene extends name.w.yellowduck.YDActLayerBase {

	private final int WITHOUT_CENTS                =0;
	private final int BACK_WITHOUT_CENTS           =1;
	private final int BACK_WITH_CENTS              =2;
	private final int WITH_CENTS                   =3;


	private final int MONEY_EURO_COIN_1C   =0;
	private final int MONEY_EURO_COIN_2C   =1;
	private final int MONEY_EURO_COIN_5C   =2;
	private final int MONEY_EURO_COIN_10C  =3;
	private final int MONEY_EURO_COIN_20C  =4;
	private final int MONEY_EURO_COIN_50C  =5;
	private final int MONEY_EURO_COIN_1E   =6;
	private final int  MONEY_EURO_COIN_2E  =7;
	private final int MONEY_EURO_PAPER_5E  =8;
	private final int MONEY_EURO_PAPER_10E =9;
	private final int MONEY_EURO_PAPER_20E =10;
	private final int MONEY_EURO_PAPER_50E =11;


	private final int PRICE_CHEAP             =3;
	private final int PRICE_MEDIUM            =8;
	private final int PRICE_EXPENSIVE         =15;
	
	private class Money extends Object {
		private String img;
		private float denomination;
		private Money(float value, String img_) {
			super();
			this.denomination=value;
			this.img="image/activities/math/numeration/money_group/money/"+img_+".png";
		}
		public String getImg() {
			return img;
		}
		public float getDenomination() {
			return denomination;
		}
	}
	private class Product extends Object {
		private float price;
		private String img;
		private Product(float price_, String img_) {
			super();
			this.price=price_;
			this.img="image/activities/math/numeration/money_group/money/"+img_+".png";
		}
		public float getPrice() {
			return price;
		}
		public String getImg() {
			return img;
		}
	}
	private int mode;
	private java.util.ArrayList<Money> moneyList, moneyHave;
	private java.util.ArrayList<Product> productList;
	
    private float paid, price_target;
    
    private float moneySize;
    private float yMoney;
    private boolean correctAmtPaid;


	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new MoneyScene();
	 
	    scene.addChild(layer); 
	    return scene;
	}

	public MoneyScene() {
		super();
        moneyHave=new java.util.ArrayList<Money>();
        
        moneyList=new java.util.ArrayList<Money>();
        //must be setup in the order defined by the macros MONEY_EURO_XXX
        moneyList.add(new Money(0.01f ,"c1c"));
        moneyList.add(new Money(0.02f ,"c2c"));
        moneyList.add(new Money(0.05f ,"c5c"));
        moneyList.add(new Money(0.10f ,"c10c"));
        moneyList.add(new Money(0.20f ,"c20c"));
        moneyList.add(new Money(0.50f ,"c50c"));
        moneyList.add(new Money(1 ,"c1e"));
        moneyList.add(new Money(2 ,"c2e"));
        moneyList.add(new Money(5 ,"n5e"));
        moneyList.add(new Money(10 ,"n10e"));
        moneyList.add(new Money(20 ,"n20e"));
        moneyList.add(new Money(50 ,"n50e"));
        
        productList=new java.util.ArrayList<Product>();
        //price below 5
        productList.add(new Product(PRICE_CHEAP ,"apple"));
        productList.add(new Product(PRICE_CHEAP ,"orange"));
        productList.add(new Product(PRICE_CHEAP ,"banane"));
        productList.add(new Product(PRICE_CHEAP ,"pamplemousse"));
        productList.add(new Product(PRICE_CHEAP ,"carot"));
        productList.add(new Product(PRICE_CHEAP ,"cerise"));
        productList.add(new Product(PRICE_CHEAP ,"cake"));
        //below 10
        productList.add(new Product(PRICE_MEDIUM ,"umbrella"));
        productList.add(new Product(PRICE_MEDIUM ,"pencil"));
        productList.add(new Product(PRICE_MEDIUM ,"bottle"));
        productList.add(new Product(PRICE_MEDIUM ,"light"));
        productList.add(new Product(PRICE_MEDIUM ,"eggpot"));
        //above 10
        productList.add(new Product(PRICE_EXPENSIVE ,"lamp"));
        productList.add(new Product(PRICE_EXPENSIVE ,"football"));
        productList.add(new Product(PRICE_EXPENSIVE ,"bicycle"));
        productList.add(new Product(PRICE_EXPENSIVE ,"crown"));
	}

	
	public void onEnter() {
		super.onEnter();
	    mMaxLevel=9;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    mode=Integer.parseInt(activeCategory.getSettings());
	    if (mode == WITH_CENTS || mode == BACK_WITH_CENTS)
	        mMaxLevel=5;
	    super.shufflePlayBackgroundMusic();
	    super.setupBackground(activeCategory.getBg(),kBgModeFit);
	    super.setupTitle(activeCategory);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, kOptionIntr|kOptionHelp|kOptionLevelButtons|kOptionOk);
	    
	    super.afterEnter();
	}
	
	private void _moneyHaveAdd(int idx) {
	    moneyHave.add(moneyList.get(idx));
	}
	
	protected void initGame(boolean firstTime, Object sender) {
	    super.initGame(firstTime, sender);
	    super.clearFloatingSprites();
	    
	    paid = 0;
	    int		   min_price = 0, max_price = 0;
	    int		   number_of_item = 0;
	    
	    moneyHave.clear();
	    /* Select level difficulty */
	    switch(mode)
	    {
	        case WITHOUT_CENTS:
	            switch(mLevel)
	        {
	            case 1:
	                number_of_item = 1;
	                min_price      = 3;
	                max_price      = 10;
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 2:
	                number_of_item = 1;
	                min_price      = 10;
	                max_price      = 20;
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 3:
	                number_of_item = 2;
	                min_price      = 20;
	                max_price      = 30;
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 4:
	                number_of_item = 2;
	                min_price      = 30;
	                max_price      = 40;
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 5:
	                number_of_item = 3;
	                min_price      = 40;
	                max_price      = 50;
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_20E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 6:
	                number_of_item = 3;
	                min_price      = 50;
	                max_price      = 60;
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_20E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_20E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 7:
	                number_of_item = 4;
	                min_price      = 60;
	                max_price      = 70;
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_50E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_20E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 8:
	                number_of_item = 4;
	                min_price      = 70;
	                max_price      = 80;
	                this._moneyHaveAdd(MONEY_EURO_PAPER_50E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_20E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 9:
	                number_of_item = 4;
	                min_price      = 50;
	                max_price      = 100;
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_50E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_20E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                break;
	        }
	            break;
	        case WITH_CENTS:
	            switch(mLevel)
	        {
	            case 1:
	                number_of_item = 1;
	                min_price      = 1;
	                max_price      = 3;
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_5C);;
	                this._moneyHaveAdd(MONEY_EURO_COIN_2C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_50C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_10C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                break;
	            case 2:
	                number_of_item = 1;
	                min_price      = 1;
	                max_price      = 3;
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_5C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_50C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_10C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                break;
	            case 3:
	                number_of_item = 2;
	                min_price      = 1;
	                max_price      = 3;
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_5C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_50C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_10C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                break;
	            case 4:
	                number_of_item = 3;
	                min_price      = 1;
	                max_price      = 3;
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_5C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_50C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_10C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                break;
	            case 5:
	                number_of_item = 4;
	                min_price      = 1;
	                max_price      = 4;
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_5C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_50C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_10C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                break;
	        }
	            break;
	        case BACK_WITHOUT_CENTS:
	            switch(mLevel)
	        {
	            case 1:
	                number_of_item = 1;
	                min_price      = 3;
	                max_price      = 9;
	                paid		 = 10;
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 2:
	                number_of_item = 1;
	                min_price      = 1;
	                max_price      = 19;
	                paid		 = 20;
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 3:
	                number_of_item = 2;
	                min_price      = 2;
	                max_price      = 29;
	                paid		 = 30;
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 4:
	                number_of_item = 2;
	                min_price      = 2;
	                max_price      = 39;
	                paid		 = 40;
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 5:
	                number_of_item = 3;
	                min_price      = 3;
	                max_price      = 49;
	                paid		 = 50;
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_20E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 6:
	                number_of_item = 3;
	                min_price      = 3;
	                max_price      = 60;
	                paid		 = 100;
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_50E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_20E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_20E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 7:
	                number_of_item = 4;
	                min_price      = 4;
	                max_price      = 70;
	                paid		 = 100;
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_50E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_20E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 8:
	                number_of_item = 4;
	                min_price      = 4;
	                max_price      = 80;
	                paid		 = 100;
	                this._moneyHaveAdd(MONEY_EURO_PAPER_50E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_20E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                break;
	            case 9:
	                number_of_item = 4;
	                min_price      = 4;
	                max_price      = 99;
	                paid		 = 100;
	                this._moneyHaveAdd(MONEY_EURO_PAPER_10E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_50E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_20E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                this._moneyHaveAdd(MONEY_EURO_PAPER_5E);
	                break;
	        }
	            break;
	        case BACK_WITH_CENTS:
	            switch(mLevel)
	        {
	            case 1:
	                number_of_item = 1;
	                min_price      = 1;
	                max_price      = 3;
	                paid		 = 5;
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_5C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_50C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_10C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                break;
	            case 2:
	                number_of_item = 1;
	                min_price      = 1;
	                max_price      = 3;
	                paid		 = 5;
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_5C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_50C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_10C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                break;
	            case 3:
	                number_of_item = 2;
	                min_price      = 1;
	                max_price      = 3;
	                paid		 = 5;
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_5C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_50C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_10C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                break;
	            case 4:
	                number_of_item = 3;
	                min_price      = 1;
	                max_price      = 3;
	                paid		 = 5;
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_5C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_50C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_10C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                break;
	            case 5:
	                number_of_item = 4;
	                min_price      = 1;
	                max_price      = 4;
	                paid           = 5;
	                this._moneyHaveAdd(MONEY_EURO_COIN_2E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1E);
	                this._moneyHaveAdd(MONEY_EURO_COIN_5C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_2C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_50C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_20C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_10C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                this._moneyHaveAdd(MONEY_EURO_COIN_1C);
	                break;
	        }
	            break;
	    }
	    //sort on value desc
	    boolean  more=true;
	    while (more) {
	        more=false;
	        for (int i = 0; i < moneyHave.size() - 1; ++i) {
	            Money first=moneyHave.get(i);
	            Money second=moneyHave.get(i+1);
	            if (first.getDenomination() < second.getDenomination()) {
	                more=true;
	                moneyHave.remove(i);
	                moneyHave.add(i+1, first);
	            }
	        }
	    }
	    //164 vs 520
	    float itemSize=164.0f/520 * szWin.height * 0.9f;
	    float xMargin=(szWin.width - itemSize*number_of_item)/2;
	    
	    /* Display what to buy */
	    price_target = 0;
	    for(int i=1; i<=number_of_item; i++) {
	        //must be integer, cents will be added later if required
	        float object_price = randomBetween(min_price,max_price)  / number_of_item;
	        java.util.ArrayList<Product> selected=new java.util.ArrayList<Product>();
	        for (Product product : productList) {
	            if (object_price < 5 && product.getPrice() == PRICE_CHEAP) {
	                selected.add(product);
	            }
	            else if (object_price >= 5 && object_price < 10 && product.getPrice() == PRICE_MEDIUM) {
	                selected.add(product);
	            }
	            else if (object_price >= 10 && product.getPrice() == PRICE_EXPENSIVE) {
	                selected.add(product);
	            }
	        }
	        float xPos=xMargin + i * itemSize - itemSize/2;
	        Product selectedProduct=selected.get(super.nextInt(selected.size()));
	        CCSprite sprite=spriteFromExpansionFile(selectedProduct.getImg());
	        float max=sprite.getContentSize().width;
	        if (sprite.getContentSize().height > max)
	            max=sprite.getContentSize().height;
	        sprite.setScale(itemSize/max * 0.8f);
	        sprite.setPosition(xPos, 252.0f/520*szWin.height);
	        super.addChild(sprite,1);
	        floatingSprites.add(sprite);
	        
	        if( (mode == WITH_CENTS) ||  (mode == BACK_WITH_CENTS) ) {
	            /* Set here the way to display money. Change only the money sign, and it's place, always keep %.2f, it will be replaced by 0,34 if decimal is ',' in your locale */
	            /* Add random cents */
	            if(mLevel == 1) {
	                object_price += 1.0f * randomBetween(1, 9)/10.0f;
	            }
	            else {
	                object_price += 1.0f * randomBetween(1,99)/100.0f;
	            }
	        }
	        price_target += object_price;
	        
	        String strAmount=null;
	        switch (mode) {
	            case WITHOUT_CENTS:
	            case BACK_WITHOUT_CENTS:
	                strAmount="$"+(int)object_price;
	                break;
	            case WITH_CENTS:
	            case BACK_WITH_CENTS:
	                strAmount=String.format("%.2f",  object_price);
	                break;
	            default:
	                break;
	        }
	        if (strAmount!=null) {
	            CCLabel amount = CCLabel.makeLabel(strAmount, super.sysFontName(),super.smallFontSize());
	            amount.setPosition(xPos, 336.0f/520*szWin.height);
	            amount.setColor(ccColor3B.ccYELLOW);
	            super.addChild(amount, 1);
	            floatingSprites.add(amount);
	        }
	        if (paid > 0) {
	            //tux_graduate
	            CCSprite _sprite=spriteFromExpansionFile("image/activities/math/numeration/money_group/money/tux_graduate.png");
	            _sprite.setPosition(_sprite.getContentSize().width/2+2,szWin.height/2);
	            super.addChild(_sprite,1);
	            floatingSprites.add(_sprite);
	            
	            //the amount tux paid
	            float total=paid, yOffset=0, xOffset=0;
	            while (total > 0) {
	                String str=null;
	                float deductible=0;
	                for (Money money : moneyList) {
	                    if (money.getDenomination() == total) {
	                        str=money.getImg();
	                        deductible=total;
	                        break;
	                    }
	                    else if (money.denomination < total && money.denomination > deductible) {
	                        str=money.getImg();
	                        deductible=money.getDenomination();
	                    }
	                }
	                if (str!=null) {
	                    CCSprite spriteMoney=spriteFromExpansionFile(str);
	                    spriteMoney.setPosition(_sprite.getPosition().x + xOffset, _sprite.getPosition().y-_sprite.getContentSize().height/6 - yOffset);
	                    spriteMoney.setScale(_sprite.getContentSize().width/spriteMoney.getContentSize().width);
	                    super.addChild(spriteMoney,2);
	                    floatingSprites.add(spriteMoney);
	                    
	                    yOffset += spriteMoney.getContentSize().height*spriteMoney.getScale()/2;
	                    xOffset += 2;
	                }
	                total -= deductible;
	                //It is possible that Tux bought $23 items but paid with two $50 bills
	            }
	            String amt=(mode==BACK_WITHOUT_CENTS)?String.format("%.2f", paid) : String.format("%d", (int)paid);
	            String prompt=String.format(localizedString("prompt_money_back"), amt);
	
	            CCLabel promptLabel = CCLabel.makeLabel(prompt, super.sysFontName(), 8*preferredContentScale(false));
	            promptLabel.setPosition(szWin.width/2, 192.0f/520*szWin.height);
	            super.addChild(promptLabel,2);
	            floatingSprites.add(promptLabel);
	        }
	    }
	    
	    if (paid>0)  {
	        // Calc the money back instead of the objects price
	        price_target = paid - price_target;
	    }
	
	    //Money in my wallet
	    yMoney=86.0f/520*szWin.height;
	    moneySize=90.0f/520*szWin.height*0.9f;
	    float maxWidth=szWin.width/moneyHave.size();
	    if (moneySize > maxWidth)
	        moneySize=maxWidth;
	    xMargin=(szWin.width - moneySize * moneyHave.size())/2;
	    float scale=1.0f;//Apply the same scale to all bills
	    java.util.ArrayList<CCNode> collected=new java.util.ArrayList<CCNode>();
	    for (int i = 0; i < moneyHave.size(); ++i) {
	        Money money=moneyHave.get(i);
	        String img=money.getImg();
	        String imgSel=buttonize(img);
	        CCTexture2D texture=CCTextureCache.sharedTextureCache().addImageExternal(imgSel);
	        
	        CCSprite sprite=spriteFromExpansionFile(img);
	        CCSprite spriteSelected=CCSprite.sprite(texture);
	        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite,spriteSelected,this,"moneyTouched");
	        menuitem.setPosition(0,0);
	        CCMenu menu = CCMenu.menu(menuitem);
	        menu.setPosition(xMargin+i*moneySize + moneySize / 2, yMoney);;
	        menu.setTag((int)(money.getDenomination()*100));
	        super.addChild(menu,1);
	        menuitem.setUserData(Float.valueOf(menu.getPosition().x));//remember its original position
	
	        floatingSprites.add(menu);
	        
	        collected.add(menuitem);
	        
	        float max=sprite.getContentSize().width;
	        if (sprite.getContentSize().height > max)
	            max=sprite.getContentSize().height;
	        float _scale=moneySize/max * 0.8f;
	        if (_scale < scale)
	            scale=_scale;
	    }
	    for (CCNode item : collected) {
	        item.setScale(scale);
	    }
	    collected.clear();
	    
	    correctAmtPaid=false;
	}
	
	public void moneyTouched(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    if (sender.getParent().getUserData() == null) {
	        sender.getParent().setUserData("paid");
	    }
	    else {
	        sender.getParent().setUserData(null);
	        sender.getParent().setPosition((Float)sender.getUserData(), yMoney);
	    }
	
	    int idx=0;
	    int amount=0;
	    for (CCNode node : floatingSprites) {
	        if (node.getUserData()!=null && "paid".equals(node.getUserData())) {
	            node.setPosition(122.f/800*szWin.width+idx*moneySize, 441.0f/520*szWin.height);
	            amount += node.getTag();
	            ++idx;
	        }
	    }
	    int _target=(int)(price_target*100);
	    if (amount == _target) {
	        correctAmtPaid=true;
	    }
	    else {
	        correctAmtPaid=false;
	    }
	}
	
	public void ok(Object sender) {
	    super.flashAnswerWithResult(correctAmtPaid, correctAmtPaid, null, null, 2);
	}
}
