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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.opengles.GL10;

import name.w.yellowduck.activities.misc.YDShape;

import org.cocos2d.layers.CCColorLayer;
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
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

public abstract class YDLayerBase extends CCColorLayer {	
	protected final int kBgModeTile			=0x01;	
	protected final int kBgModeStretch      =0x02; //stretch the image to cover the whole screen, the aspect ratio is kept
	protected final int kBgModeFit          =0x03; //Fit the image into whole screen, the aspect ratio is NOT kept
	protected final int kBgModeFit2Center   =0x04;

	protected final int kOptionLevelButtons             =0x01;
	protected final int kOptionRepeatButton             =0x02;
	protected final int kOptionArrows                   =0x04;
	protected final int kOptionOk                       =0x08;
	protected final int kOptionConfig                   =0x10;
	protected final int kOptionUpgrade                  =0x20;
	protected final int kOptionHelp                     =0x40;
	protected final int kOptionIntr                     =0x80;
	protected final int kOptionUpDnArrows               =0x0100;
	protected final int kOptionLfRtArrows               =0x0200;
	
	
	protected java.util.ArrayList<CCNode> floatingSprites;
	protected CGSize szWin;
	
	protected CCLabel mLevelLabel;
	
	private java.util.Random random;

	private static ZipResourceFile expansionFile=null;
	
	public YDLayerBase() {
		super(new ccColor4B(0xff,0xff,0xff,0xff));
	    floatingSprites=new java.util.ArrayList<CCNode>();
	    szWin=CCDirector.sharedDirector().winSize();
	    if (szWin.width < szWin.height) {
	        float t=szWin.width;
	        szWin.width=szWin.height;
	        szWin.height=t;
	    }

	    try {
	    	if (expansionFile==null) {
	    		int mainVersion=this.getVersion();
	    		int patchVersion=0;
	    		expansionFile = APKExpansionSupport.getAPKExpansionZipFile(YDConfiguration.context, mainVersion, patchVersion);
	    	}
	    }
	    catch (java.io.IOException e) {
	    	e.printStackTrace();
	    }
	}

	public void onExit() {
		floatingSprites.clear();
	    super.removeAllChildren(true);
		super.onExit();
	}
	
	protected int _screenUnit() {
		return YDConfiguration.sharedConfiguration().getScreenUnit();
	}
	
	//If the os is Android 4.x, the bottom will be taken by the system operation bar
	protected boolean isTablet() {
		return szWin.width >= 1024;// && szWin.height >= 768;
	}
	
	protected int preferredContentScale (boolean forImage) {
	    return (this.isTablet() && (!forImage || szWin.width>=2048))?2:1;
	}
	
	protected int buttonSize () {
		return (int)(szWin.height * 52 / 480);
	}
	
	//category icon size in pixels
	protected int categoryIconSize() {
		return (int)((szWin.height-this.topOverhead()) / 4);
	}

	protected int topOverhead() {
		return (int)(szWin.height * 60 / 480);
	}
	
	//should be bigger than buttonSize
	protected int bottomOverhead() {
		if (SysHelper.isFullVersion())
			return 0;
		int h=(int)(szWin.height * 60 / 480);
		if (h < YDConfiguration.sharedConfiguration().getAdHeight())
			h=YDConfiguration.sharedConfiguration().getAdHeight();
		return h;
	}
	
	protected CGSize popupWinSize () {
		return CGSize.make(660, 480);
	}
	
	protected String sysFontName() {
		return "normal";
	}

	protected int largeFontSize() {
		return (int)(szWin.width*24/800);
	}

	protected int mediumFontSize() {
		return (int)(szWin.width*18/800);
	}

	protected int smallFontSize() {
		return (int)(szWin.width*16/800);
	}
	
	protected String localizedString(String key) {
		return YDConfiguration.sharedConfiguration().getLocalizedString(key);
	}
		
	//Setup an image as scene background. The image may be tiled to full screen or scaled to fit the screen
	protected CCSprite setupBackground(String imgfile, int mode) {
	    float _width=szWin.width;
	    float _height=szWin.height;
	   
	    CCSprite background=null;
	    if (mode == kBgModeTile) {
	    	background=spriteFromExpansionFile(imgfile);//cannot be svg file
	        background.setTextureRect(0, 0, szWin.width, szWin.height, false);
	        background.getTexture().setTexParameters(GL10.GL_LINEAR, GL10.GL_LINEAR, GL10.GL_REPEAT, GL10.GL_REPEAT);
	        background.setPosition(CGPoint.ccp(szWin.width/2 , szWin.height/2));
	    }
	    else {
	    	if (imgfile.endsWith(".svg")) {
	    		imgfile=this.renderSVG2Img(imgfile, (int)szWin.width, (int)szWin.height);
	    		CCTexture2D texture = CCTextureCache.sharedTextureCache().addImageExternal(imgfile);
	    		background=CCSprite.sprite(texture);
	    	}
	    	else {
	    		background=spriteFromExpansionFile(imgfile);
	    	}
	        if (mode == kBgModeStretch) {
	            float scale=_width/background.getContentSize().width;
	            float scale2=_height/background.getContentSize().height;
	            if (scale2 > scale)
	                scale=scale2;
	            background.setScale(scale);
	        }
	        else if (mode == kBgModeFit) {
	            background.setScaleX(_width/background.getContentSize().width);
	            background.setScaleY(_height/background.getContentSize().height);
	        }
	        else if (mode == kBgModeFit2Center) {
	            float scale=_width/background.getContentSize().width;
	            float scale2=_height/background.getContentSize().height;
	            if (scale2 < scale)
	                scale=scale2;
	            background.setScale(scale);
	        }
	    }
	    background.setPosition(CGPoint.ccp(_width/2,_height/2));
	    super.addChild(background, 0);
	    
	    return background;
	}

	private String getCachedImageName(String srcImg, int width, int height) {
		int sum=0;
		for (int i = 0; i < srcImg.length(); ++i) {
			sum += srcImg.charAt(i);
		}
		int index=srcImg.lastIndexOf("/");
		String path=""+sum+"_"+width+"x"+height+"_"+((index >= 0) ? srcImg.substring(index+1) : srcImg);
		index=path.lastIndexOf(".");
		if (index > 0) {
			path=path.substring(0, index) + ".png";
		}
		java.io.File file=new java.io.File(YDConfiguration.context.getCacheDir() + java.io.File.separator + path);
		return file.getAbsolutePath();
	}
	
	protected String renderSVG2Img(String svgFile, int width, int height) {
		Log.e("harsh","renderSVG2Img function called  ");
	    String cachedFile=this.getCachedImageName(svgFile, width, height);
	    if (!new java.io.File(cachedFile).exists() && (expansionFile!=null)) {
	        try { //http://code.google.com/p/svg-android-2/
				// Get an input stream for a known file inside the expansion file ZIPs
	        	Log.e("harsh","	new java.io.File(cachedFile).exists() && (expansionFile!=null == true");
				InputStream fileStream = expansionFile.getInputStream(svgFile);
	        	
        	    SVG svg = SVGParser.getSVGFromInputStream(fileStream);
        	    //to workaround a bug in androidsvg, the output can not be smaller than the svg document size
        	    android.graphics.Picture picture=svg.getPicture();
        	    float svgWidth=picture.getWidth();
        	    float svgHeight=picture.getHeight();
        	    //either width or height musbe be specified
        	    float scaleX = width / svgWidth;
        	    float scaleY = height / svgHeight;
        	    if (scaleX <= 0)
        	        scaleX = scaleY;
        	    if (scaleY <= 0)
        	        scaleY = scaleX;
        	    if (scaleX <= 0)
        	        scaleX = 1.0f;
        	    if (scaleY <= 0)
        	        scaleY = 1.0f;
        	    
        	    float scale = (scaleX < scaleY) ? scaleX : scaleY;
        	    int imgWidth=(int)(svgWidth * scale);
        	    int imgHeight=(int)(svgHeight * scale);
	 		    android.graphics.Bitmap img=android.graphics.Bitmap.createBitmap(imgWidth, imgHeight, android.graphics.Bitmap.Config.ARGB_8888);
	 		    android.graphics.Canvas canvas=new android.graphics.Canvas(img);
	 		    canvas.drawARGB(0, 0, 0, 0);
	 		    canvas.drawPicture(picture, new android.graphics.Rect(0,0,imgWidth,imgHeight));

	 	       	java.io.FileOutputStream out = new java.io.FileOutputStream(cachedFile);
	 			img.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, out);
	 			out.close();
	 			img.recycle();

	 			fileStream.close();
	        }
	        catch (Throwable e) {
	        	android.util.Log.e("SVG", svgFile+":"+e.getMessage());
	        	Log.e("harsh","excetion raised while calling renderSVG2Img function");
	        	//e.printStackTrace();
	        	cachedFile=null;
	        }	        
	    }
	    return cachedFile;
	}
	
	protected String renderSkinSVG2Button(int btnId, int sz) {
	    String name=null;
	    switch (btnId) {
	        case Schema.kSvgButtonHome:
	            name="HOME";
	            break;
	        case Schema.kSvgButtonHELP:
	            name="HELP";
	            break;
	        case Schema.kSvgButtonLevelUp:
	            name="LEVEL_UP";
	            break;
	        case Schema.kSvgButtonLevelDn:
	            name="LEVEL_DOWN";
	            break;
	        case Schema.kSvgButtonDelimiter:
	            name="g14150";
	            break;
	        case Schema.kSvgButtonOk:
	            name="OK";
	            break;
	        case Schema.kSvgButtonRepeat:
	            name="REPEAT";
	            break;
	        case Schema.kDifficulty1: //26.066x25.346
	            name="DIFFICULTY1";
	            break;
	        case Schema.kDifficulty2: //30.089x27.694
	            name="DIFFICULTY2";
	            break;
	        case Schema.kDifficulty3: //30.420x24.383
	            name="DIFFICULTY3";
	            break;
	        case Schema.kDifficulty4: //25.745x26.988
	            name="DIFFICULTY4";
	            break;
	        case Schema.kDifficulty5: //27.745x26.252
	            name="DIFFICULTY5";
	            break;
	        case Schema.kDifficulty6: //29.615x27.352
	            name="DIFFICULTY6";
	            break;
	        case Schema.kSvgArrowUp:
	            name="UP";
	            break;
	        case Schema.kSvgArrowLeft:
	            name="PREVIOUS";
	            break;
	        case Schema.kSvgArrowRight:
	            name="NEXT";
	            break;
	        case Schema.kSvgArrowDown:
	            name="DOWN";
	            break;
	        case Schema.kSvgConfig:
	            name="CONFIG";
	            break;
	        case Schema.kSvgDollar:
	            name="DOLLAR";
	            break;
	        case Schema.kSvgButtonSound:
	            name="SOUND";
	            break;
	    }
	    if (name == null)
	        return null; //button does not exit
	    String path="image/skins/"+name+".svg";
	    return this.renderSVG2Img(path, sz, sz);
	}
	
	
	//Apply a mask layer on the image to make the button image looks like touched
	protected String buttonize(String assetImg) {
		String cached=this.getCachedImageName(assetImg, 0, 0);
		if (! new java.io.File(cached).exists()) {
			android.graphics.Bitmap img=null;
			if (assetImg.startsWith("/")) {
				img=android.graphics.BitmapFactory.decodeFile(assetImg, null);
			}
			else {
				img=this.decodeExpansionBitmap(assetImg);
			}
			int offset=2;
			int width=img.getWidth()+offset;
			int height=img.getHeight()+offset;
	    
			android.graphics.Bitmap withMask=android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
		    android.graphics.Canvas canvas=new android.graphics.Canvas(withMask);
		    canvas.drawARGB(0, 0, 0, 0);
		    canvas.drawBitmap(img, offset, offset, null);
	    
		    android.graphics.Bitmap mask=this.decodeExpansionBitmap("image/misc/buttonmask.png");
		    canvas.drawBitmap(mask, new android.graphics.Rect(0,0,mask.getWidth(),mask.getHeight()), new android.graphics.Rect(offset,offset,width,height), null);
			
		    try {
	 	       	java.io.FileOutputStream out = new java.io.FileOutputStream(cached);
	 	       	withMask.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, out);
	 	       	out.close();
		    }
		    catch(java.io.IOException e) {
		    	e.printStackTrace();
		    }
		}
		return cached;
	}
	protected Bitmap buttonize(Bitmap img) {
		int offset=2;
		int width=img.getWidth()+offset;
		int height=img.getHeight()+offset;
    
		android.graphics.Bitmap withMask=android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
	    android.graphics.Canvas canvas=new android.graphics.Canvas(withMask);
	    canvas.drawARGB(0, 0, 0, 0);
	    canvas.drawBitmap(img, offset, offset, null);
    
	    android.graphics.Bitmap mask=this.decodeExpansionBitmap("image/misc/buttonmask.png");
	    canvas.drawBitmap(mask, new android.graphics.Rect(0,0,mask.getWidth(),mask.getHeight()), new android.graphics.Rect(offset,offset,width,height), null);

	    return withMask;
	}
	
	protected String iconize(String imgFile, int difficulty) {
		String cached=this.getCachedImageName(imgFile, difficulty, difficulty);
		if (! new java.io.File(cached).exists()) {
			android.graphics.Bitmap img=null;
			if (imgFile.startsWith("/")) {
				img=android.graphics.BitmapFactory.decodeFile(imgFile, null);
			}
			else {
				img=this.decodeExpansionBitmap(imgFile);
			}
			
		    int margin=4;
		    //make the icon a square 
		    float max=img.getWidth();
		    if (img.getHeight() > max)
		        max=img.getHeight();
		    int canvasWidth=(int)(max+margin*2);
		    int canvasHeight=(int)(max+margin*2);
		    
			android.graphics.Bitmap withMask=android.graphics.Bitmap.createBitmap(canvasWidth, canvasHeight, android.graphics.Bitmap.Config.ARGB_8888);
		    android.graphics.Canvas canvas=new android.graphics.Canvas(withMask);
		    canvas.drawARGB(0, 0, 0, 0);
		    // @harsh there is no icon_base.png file in image/misc/ so replacing icon_base.png to Icon.png
		  // @harsh : placed one image as icon_base.png in image/misc/ folder just for  testing purpose 
		    android.graphics.Bitmap base=this.decodeExpansionBitmap("image/misc/icon_base.png");
		    canvas.drawBitmap(base, new android.graphics.Rect(0,0,base.getWidth(),base.getHeight()), new android.graphics.Rect(0,0, canvasWidth, canvasHeight), null);
		    int xMargin=(canvasWidth-img.getWidth())/2;
		    int yMargin=(canvasHeight-img.getHeight())/2;
		    canvas.drawBitmap(img, new android.graphics.Rect(0,0,img.getWidth(),img.getHeight()), new android.graphics.Rect(xMargin,yMargin, canvasWidth-xMargin*2, canvasHeight-yMargin*2), null);
		    
		    if (difficulty > 0) {
		        //draw difficulty level at the top left corner. if it is a category, draw the arrow key
		        int btnId=(difficulty < 100) ? Schema.kDifficulty1+difficulty-1:Schema.kSvgButtonDelimiter;
		        String strDiffImg=this.renderSkinSVG2Button(btnId, Schema.kDifficultyIndicatorSize_);
				android.graphics.Bitmap diff=android.graphics.BitmapFactory.decodeFile(strDiffImg, null);
				if (diff != null) {
					canvas.drawBitmap(diff, 1, 1, null);
				}
		    }
		    try {
	 	       	java.io.FileOutputStream out = new java.io.FileOutputStream(cached);
	 	       	withMask.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, out);
	 	       	out.close();
		    }
		    catch(java.io.IOException e) {
		    }
		}
	    return cached;
	}
	
	protected String resizeImage2(String assetImg, int width, int height) {
		String cached=this.getCachedImageName(assetImg, width, height);
		if (! new java.io.File(cached).exists()) {
			android.graphics.Bitmap img=this.decodeExpansionBitmap(assetImg);
			
		    float scale1=1.0f*width/img.getWidth();
		    float scale2=1.0f*height/img.getHeight();
		    if (scale1 <= 0)
		        scale1=scale2;
		    if (scale2 <= 0)
		        scale2=scale1;
		    float scale=(scale2 > scale1) ? scale1 : scale2;
		
		    int outWidth=(int)(img.getWidth() * scale);
		    int outHeight=(int)(img.getHeight() * scale);
		    
			android.graphics.Bitmap result=android.graphics.Bitmap.createBitmap(outWidth, outHeight, android.graphics.Bitmap.Config.ARGB_8888);
		    android.graphics.Canvas canvas=new android.graphics.Canvas(result);
		    canvas.drawBitmap(img, new android.graphics.Rect(0,0,img.getWidth(),img.getHeight()), new android.graphics.Rect(0,0,outWidth,outHeight), null);
			
		    try {
	 	       	java.io.FileOutputStream out = new java.io.FileOutputStream(cached);
	 	        result.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, out);
	 	       	out.close();
		    }
		    catch(java.io.IOException e) {
		    }
		}
		return cached;
	}
	
		
	protected void playBackgroundMusic(String path) {
		if (!SysHelper.isBgMusicEnabled() || (expansionFile==null))
			return;
		if (path == null) {
	        int sel=this.nextInt(2);
	        switch (sel) {
	          // @harsh : there is no BachJSBrandenburg.mp3 file in audio/music/background/ so replacing BachJSBrandenburg.mp3 with BachJSBrandenburgConcertNo2inFMajorBWV1047mvmt1.mp3
	         // @harsh : added new BachJSBrandenburg.mp3 file as temporary in audio/music/background/
	            case 0:path="audio/music/background/BachJSBrandenburg.mp3";break;
	            case 1:path="audio/music/background/LRLaPause.mp3";break;
	        }
		}
		try {
			AssetFileDescriptor afd = expansionFile.getAssetFileDescriptor(path);
			if (afd !=null) {
				YDSoundEngine.sharedEngine().playSound(afd, path, true);
				afd.close();
			}
		}
		catch (java.io.IOException e) {
		}
	}
	
	protected void stopBackgroundMusic() {
		YDSoundEngine.sharedEngine().pauseSound();
	}

	protected int playSound(String assetFile) {
		return this.playSoundLooped(assetFile, false);
	}

	protected int playSoundLooped(String assetFile, boolean loop) {
		int sndId=-1;
		try {
			if (expansionFile!=null) {
				AssetFileDescriptor afd = expansionFile.getAssetFileDescriptor(assetFile);
				if (afd != null) {
					sndId=YDSoundEngine.sharedEngine().playEffect(afd, assetFile, loop);
					afd.close();
				}
			}
		}
		catch (java.io.IOException e) {
		}
		return sndId;
	}
	
	protected int playVoice(String voice) {
	    String localization=YDConfiguration.sharedConfiguration().getLocale();
	    String path=String.format("audio/voices/%s/%s", localization, voice);
	    return this.playSoundLooped(path, false);
	}

	//Stop playing a sound or voice
	protected void stopSoundOrVoice(int handle) {
	    if (handle >= 0) {
	        YDSoundEngine.sharedEngine().stopEffect(YDConfiguration.context, handle);
	    }
	}

	protected void setupTitle(Category category) {
		String key=category.getTitle();
		this.setupTitle(YDConfiguration.sharedConfiguration().getLocalizedString(key));
	}
	
	protected void setupTitle(String msg) {
		if (msg == null || msg.length() <= 0)
			return;
		CCLabel title=CCLabel.makeLabel(msg, this.sysFontName(), this.largeFontSize());
		title.setColor(Schema.kTitleClr);
		title.setPosition(CGPoint.ccp(szWin.width/2, szWin.height-this.topOverhead()/2));
		super.addChild(title,1);
	}
	//Create an round corder rectangle image with filled color
	protected android.graphics.Bitmap roundCornerRect(int width, int height, int cornerSize, ccColor4B color) {
	    android.graphics.Bitmap img=android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
	    android.graphics.Canvas canvas=new android.graphics.Canvas(img);
	    canvas.drawARGB(0, 0, 0, 0);
		
	    int min=(width > height)?height:width;
	    if (cornerSize <= 0)
	        cornerSize=(int)(min*0.1f);
	    
		Paint paint=new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.rgb(color.r, color.g, color.b));
		paint.setAlpha(color.a);
		canvas.drawRoundRect(new android.graphics.RectF(0,0,width,height), cornerSize, cornerSize, paint);

	    return img;
	}
	
	protected Bitmap createMultipleLineLabel(String text, String fontName, int fontSize, float canvasWidth, ccColor3B clrTxt, ccColor4B clrBg) {
		int margin=(clrBg.a>0)?6:4;
		
		TextPaint textPaint = new TextPaint();
		textPaint.setTextSize(fontSize);
		textPaint.setTypeface(Typeface.create(fontName, Typeface.NORMAL));
		textPaint.setColor(Color.argb(0xff, clrTxt.r, clrTxt.g, clrTxt.b));
		if (canvasWidth <= 0)
			canvasWidth = textPaint.measureText(text);
		StaticLayout textLayout = new StaticLayout(text, textPaint, (int)canvasWidth, (text.indexOf("\n")>=0)?Alignment.ALIGN_NORMAL:Alignment.ALIGN_CENTER, 1, 0, false);
		int desiredHeight=textLayout.getHeight() + margin*2;
		int desiredWidth=textLayout.getWidth() + margin*2;

	    android.graphics.Bitmap img=android.graphics.Bitmap.createBitmap(desiredWidth, desiredHeight, android.graphics.Bitmap.Config.ARGB_8888);
	    android.graphics.Canvas canvas=new android.graphics.Canvas(img);
	    canvas.drawARGB(0, 0, 0, 0);
		
	    int min=(desiredWidth > desiredHeight)?desiredHeight:desiredWidth;
	    int cornerSize=(int)(min*0.1f);

	    if (clrBg.a > 0) {
			Paint paint=new Paint();
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.argb(clrBg.a, clrBg.r, clrBg.g, clrBg.b));
			canvas.drawRoundRect(new android.graphics.RectF(0,0,desiredWidth,desiredHeight), cornerSize, cornerSize, paint);
	    }
		canvas.translate(margin, margin);
		textLayout.draw(canvas);

	    return img;
	}
	
	public void __removeMe(Object sender) {
	    CCNode bg=(CCNode)(((CCNode)sender).getUserData());
	    ((CCNode)sender).removeFromParentAndCleanup(true);
	    if (bg!=null)
	        bg.removeFromParentAndCleanup(true);
	}
	
	protected void _reportMemory(String tag) {
	    MemoryInfo mi = new MemoryInfo();
	    ActivityManager activityManager = (ActivityManager) YDConfiguration.context.getSystemService(Activity.ACTIVITY_SERVICE);
	    activityManager.getMemoryInfo(mi);
	    long availableMegs = mi.availMem / 1048576L;
	    android.util.Log.e("Memory", "["+tag + "]available " + availableMegs + "MB");
	}
	
	protected void shufflePlayBackgroundMusic() {
	    this.stopBackgroundMusic();
	    this.playBackgroundMusic(null);
	}
	
	//Top buttions navigating between different categories(sections)
	protected void setupNavBar(Category activeCategory){
	    int    height=this.buttonSize();
	    int    heightContentSize=this.topOverhead();

	    java.util.ArrayList<Category> ancestors=activeCategory.createAncestorTree();
	    int generationGap=0, xPos=4;
	    float yPos=szWin.height - heightContentSize/2;
	    for (Category p : ancestors) {
	        if (generationGap <= 0) {
		        String img=this.renderSkinSVG2Button(Schema.kSvgButtonHome, height);
		        CCTexture2D texture1 = CCTextureCache.sharedTextureCache().addImageExternal(img);
		        CCSprite sprite=CCSprite.sprite(texture1);
		        CCTexture2D texture2 = CCTextureCache.sharedTextureCache().addImageExternal(this.buttonize(img));
		        CCSprite spriteSelected=CCSprite.sprite(texture2);
	            CCMenuItemSprite exit=CCMenuItemImage.item(sprite,spriteSelected,this,"back2");
	            exit.setTag(generationGap);
	            exit.setPosition(CGPoint.ccp(exit.getContentSize().width/2+xPos, yPos));
	            CCMenu menu = CCMenu.menu(exit);
	            menu.setPosition(CGPoint.ccp(0, 0));
	            super.addChild(menu, Schema.zMenuItem);

	            xPos += exit.getContentSize().width;
	        }
	        else {
	            //return to previous screen
	            xPos+=4;
	            String slash=this.renderSkinSVG2Button(Schema.kSvgButtonDelimiter,height);
		        CCTexture2D _texture = CCTextureCache.sharedTextureCache().addImageExternal(slash);
	            CCSprite separator=CCSprite.sprite(_texture);
	            separator.setScale(1.0f * height / separator.getContentSize().height / 3);
	            separator.setPosition(CGPoint.ccp(xPos+separator.getContentSize().width*separator.getScale()/2, yPos));
	            super.addChild(separator, Schema.zMenuItem);
	            xPos += separator.getContentSize().width*separator.getScale() + 4;
	            
	            String img=this.renderSVG2Img(p.getIcon(), height,height);
		        CCTexture2D texture1 = CCTextureCache.sharedTextureCache().addImageExternal(img);
		        CCSprite sprite=CCSprite.sprite(texture1);
		        CCTexture2D texture2 = CCTextureCache.sharedTextureCache().addImageExternal(this.buttonize(img));
		        CCSprite spriteSelected=CCSprite.sprite(texture2);
	            CCMenuItemSprite back=CCMenuItemImage.item(sprite,spriteSelected,this,"back2");
	            back.setTag(generationGap);
	            back.setPosition(CGPoint.ccp(back.getContentSize().width/2+xPos, yPos));
	            CCMenu menu = CCMenu.menu(back);
	            menu.setPosition(CGPoint.ccp(0, 0));
	            super.addChild(menu, Schema.zMenuItem);

	            xPos += back.getContentSize().width;
	        }
	        
	        ++generationGap;
	    }
	    ancestors.clear();
	}
	
	//help, game level buttons etc. located on the top-right of the screen
	protected void setupSideToolbar(Category activeCategory, int options) {
	    java.util.ArrayList<CCNode> collection=new java.util.ArrayList<CCNode>();

	    float yPos=szWin.height - this.topOverhead()/2;
	    float xPos=6, margin=6; //x margin between icons
	    	    
	    if ((options & kOptionIntr)!=0) {
	    	xPos = this.createButtonMenu(Schema.kSvgButtonSound, collection, xPos, yPos);
	    }
	    if ((options & kOptionHelp) != 0) {
	    	xPos = this.createButtonMenu(Schema.kSvgButtonHELP, collection, xPos, yPos);
	    }
	    if ((options & kOptionLevelButtons)!=0) {
	        xPos += margin * 2;
	    	
	    	xPos = this.createButtonMenu(Schema.kSvgButtonLevelDn, collection, xPos, yPos);
	        
	        String fontName=this.sysFontName();
	        int fontSize=this.mediumFontSize();
	        
	        String str="1";
	        mLevelLabel = CCLabel.makeLabel(str, fontName, fontSize);
	        mLevelLabel.setPosition(CGPoint.ccp(xPos + mLevelLabel.getContentSize().width/2, yPos)); //same horitonzal line as the above icon
	        mLevelLabel.setColor(ccColor3B.ccRED);
	        super.addChild(mLevelLabel,Schema.zMenuItem);
	        collection.add(mLevelLabel);
	        
	        xPos += mLevelLabel.getContentSize().width + margin;
	        
	        //increase level
	    	xPos = this.createButtonMenu(Schema.kSvgButtonLevelUp, collection, xPos, yPos);
	        xPos += margin * 2;
	    }
	    
	    if ((options & kOptionRepeatButton) != 0) {
	    	xPos = this.createButtonMenu(Schema.kSvgButtonRepeat, collection, xPos, yPos);
	    }
	    
	    if ((options & kOptionArrows) != 0) {
	        int arrows[]={Schema.kSvgArrowLeft, Schema.kSvgArrowUp, Schema.kSvgArrowDown, Schema.kSvgArrowRight};
	        for (int i=0; i < 4; ++i) {
		    	xPos = this.createButtonMenu(arrows[i], collection, xPos, yPos);
	        }
	    }
	    else if ((options & kOptionUpDnArrows) != 0) {
	        int arrows[]={Schema.kSvgArrowUp, Schema.kSvgArrowDown};
	        for (int i=0; i < 2; ++i) {
		    	xPos = this.createButtonMenu(arrows[i], collection, xPos, yPos);
	        }
	    }
	    else if ((options & kOptionLfRtArrows)!=0) {
	        int arrows[]={Schema.kSvgArrowLeft, Schema.kSvgArrowRight};
	        for (int i=0; i < 2; ++i) {
		    	xPos = this.createButtonMenu(arrows[i], collection, xPos, yPos);
	        }
	    }
	    if ((options & kOptionOk) != 0) {
	    	xPos = this.createButtonMenu(Schema.kSvgButtonOk, collection, xPos, yPos);
	    }
	    
	    if ((options & kOptionConfig) != 0) {
	    	xPos = this.createButtonMenu(Schema.kSvgConfig, collection, xPos, yPos);
	    }

	    if ((options & kOptionUpgrade)!=0) {
	    	xPos = this.createButtonMenu(Schema.kSvgDollar, collection, xPos, yPos);
	    }

	    //move all buttons to final position
	    float movement=szWin.width - xPos - 4;
	    for (CCNode node : collection) {
	        node.setPosition(CGPoint.ccp(node.getPosition().x+movement, node.getPosition().y));
	    }
	    collection.clear();
	}

	private float createButtonMenu(int btnId, java.util.ArrayList<CCNode>collect2, float xPos, float yPos) {
    	String img=this.renderSkinSVG2Button(btnId,this.buttonSize());
        CCTexture2D texture1 = CCTextureCache.sharedTextureCache().addImageExternal(img);	        
        CCSprite sprite=CCSprite.sprite(texture1);
        CCTexture2D texture2 = CCTextureCache.sharedTextureCache().addImageExternal(this.buttonize(img));	        
        CCSprite spriteSelected=CCSprite.sprite(texture2);
        CCMenuItemSprite menuitem=CCMenuItemImage.item(sprite, spriteSelected,this,"toolbarBtnTouched");
        menuitem.setPosition(CGPoint.ccp(0, 0));
        menuitem.setTag(btnId);
        CCMenu menu = CCMenu.menu(menuitem);
        menu.setPosition(CGPoint.ccp(xPos + menuitem.getContentSize().width/2, yPos));
        super.addChild(menu,Schema.zMenuItem);
        collect2.add(menu);
    	
        return xPos + menuitem.getContentSize().width + 4;
	}
	
	//User tap on one icon to navigate back to previous screen
	public void back2(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();

	    int steps=sender.getTag();
	    if (steps <= 0)
	        this.nav2(null);
	    else {
	    	java.util.ArrayList<Category> ancestors=activeCategory.createAncestorTree();
	        if (steps < ancestors.size())
	            this.nav2(ancestors.get(steps));
	        else
	            this.nav2(null);
	        ancestors.clear();
	    }
	}
	//Subclass is responsible for replacing the scene
	protected void nav2(Category category) {
	    YDConfiguration.sharedConfiguration().setActiveCategory(category);
	    if ((category == null) || !category.isActivity())
	        this.playSound("audio/sounds/bleep.wav");
	    else
	        this.playSound("audio/sounds/level.wav");
	}
	
	protected int nextInt(int max) {
		if (random == null)
			random=new java.util.Random();
		int value=random.nextInt(max);
		if (value < 0)
			value = 0-value;
		return value % max;
	}
	/*
	protected android.graphics.Bitmap decodeAssetBitmap(String path) {
	    android.graphics.Bitmap bitmap = null;
	    try {
			android.content.res.AssetManager assetManager = YDConfiguration.context.getAssets();
	    	java.io.InputStream istr = assetManager.open(path);
	        bitmap = android.graphics.BitmapFactory.decodeStream(istr);
	        istr.close();
	    }
	    catch (java.io.IOException e) {
	    	bitmap=null;
	    }

	    return bitmap;	
	}
	*/
	protected android.graphics.Bitmap decodeExpansionBitmap(String path) {
	    android.graphics.Bitmap bitmap = null;
    	//long start=android.os.SystemClock.elapsedRealtime();
	    try {
			// Get a ZipResourceFile representing a merger of both the main and patch files
			if (expansionFile!=null) {
				// Get an input stream for a known file inside the expansion file ZIPs
				InputStream fileStream = expansionFile.getInputStream(path);
		        bitmap = android.graphics.BitmapFactory.decodeStream(fileStream);
		        fileStream.close();
			}
	    }
	    catch (java.io.IOException e) {
	    	bitmap=null;
	    }
	    //long finished=android.os.SystemClock.elapsedRealtime();
	    //android.util.Log.e("APK", path+":"+(finished-start));
	    return bitmap;	
	}
	
	public CCSprite spriteFromExpansionFile(String pathToFileInsideZip) {
        Bitmap bitmap = this.decodeExpansionBitmap(pathToFileInsideZip);
		return (bitmap == null) ? null : CCSprite.sprite(bitmap, pathToFileInsideZip);
	}
	
	public CCTexture2D textureFromExpansionFile(String pathToFileInsideZip) {
        Bitmap bitmap = this.decodeExpansionBitmap(pathToFileInsideZip);
		return (bitmap == null) ? null : CCTextureCache.sharedTextureCache().addImage(bitmap, pathToFileInsideZip);
	}
	
	protected java.util.ArrayList<String> loadExpansionAssetFile(String path) {
		java.util.ArrayList<String> lines= new java.util.ArrayList<String>();
		try {
			if (expansionFile!=null) {
				// Get an input stream for a known file inside the expansion file ZIPs
				InputStream raw = expansionFile.getInputStream(path);
		        BufferedReader reader = new BufferedReader( new InputStreamReader (raw, "utf8"));
		        String         line = null;
		        while( ( line = reader.readLine() ) != null ) {
		        	lines.add(line);
		        }
		        raw.close();
			}
		}
		catch (Throwable ignore) {
		}
		return lines;
	}
	
	protected java.util.ArrayList<YDShape> createShapesFromConfiguration(String file) {
		java.util.ArrayList<YDShape> lines=new java.util.ArrayList<YDShape>();
		try {
			if (expansionFile!=null) {
				// Get an input stream for a known file inside the expansion file ZIPs
				InputStream raw = expansionFile.getInputStream(file);
				lines=YDConfiguration.sharedConfiguration().createShapesFromConfiguration(raw);
				raw.close();
			}
		}
		catch (Throwable ignore) {
		}
		return lines;
	}
	
	private int getVersion() {
	    int v = 0;
	    try {
	        v = YDConfiguration.context.getPackageManager().getPackageInfo(YDConfiguration.context.getPackageName(), 0).versionCode;
	    } catch (NameNotFoundException e) {
	        // Huh? Really?
	    	v=2;
	    }
	    return v;
	}	
}
