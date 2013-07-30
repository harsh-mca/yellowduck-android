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


package name.w.yellowduck;

import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

public class Schema extends Object {
	public static final int kDifficultyIndicatorSize_   =22; //result image height

	public static final int kSvgButtonHome              =0;
	public static final int kSvgButtonHELP              =1;
	public static final int kSvgButtonLevelUp           =2;
	public static final int kSvgButtonLevelDn           =3;
	public static final int kSvgButtonDelimiter         =4;
	public static final int kSvgButtonOk                =5;
	public static final int kSvgButtonRepeat            =6;
	public static final int kDifficulty1                =7;
	public static final int kDifficulty2                =8;
	public static final int kDifficulty3                =9;
	public static final int kDifficulty4                =10;
	public static final int kDifficulty5                =11;
	public static final int kDifficulty6                =12;
	public static final int kSvgArrowUp                 =13;
	public static final int kSvgArrowLeft               =14;
	public static final int kSvgArrowRight              =15;
	public static final int kSvgArrowDown               =16;
	public static final int kSvgConfig                  =17;
	public static final int kSvgDollar                  =18;
	public static final int kSvgButtonSound             =19;
	public static final int kSvgTotalButtons_           =20;


	public static final float ANIMATION_SPEED           =0.2f; //general ccmoveto speed
	

//z-orders
	public static final int zMenuItem                   =20; //top most
	public static final int zSubCategoryIcon            =1;  //above the background image

//misc sizes
	public static final float kCategoryIconSize          =1.5f; //category icon size in screen units

//font colors
	public static final ccColor3B kTitleClr                   =ccColor3B.ccc3(0,0,0xff); //page title color
	public static final ccColor3B kCategoryActNameClr         =ccColor3B.ccc3(0x00,0x00,0xF0);
	public static final ccColor3B kDescriptionClr             =ccColor3B.ccc3(0,0,0xff); //description, goal, manual etc, located at the bottom of the screen
	public static final ccColor3B kPopUpFontClr               =ccColor3B.ccc3(0xff,0xff,0xff);
	public static final ccColor4B kPopUpBgClr                 =ccColor4B.ccc4(244,180,14,255);
	public static final int kPopUpBgExtra               =10; //extra space so that the popup message will not exceed the background round corner rectangle
	
	public static final float kSceneTransitionSpeed		=1.0f;
}
