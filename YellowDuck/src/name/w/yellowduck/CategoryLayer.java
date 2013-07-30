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

import java.lang.reflect.Method;

import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCTextureCache;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.transitions.CCFadeTRTransition;
import org.cocos2d.transitions.CCFadeTransition;
import org.cocos2d.transitions.CCMoveInBTransition;
import org.cocos2d.transitions.CCMoveInLTransition;
import org.cocos2d.transitions.CCMoveInTTransition;
import org.cocos2d.transitions.CCTransitionScene;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.MotionEvent;

public class CategoryLayer extends YDLayerBase {
    //The category that user currently selected
    private Category activeCategory;
    //The category that the user tries to select
    private Category selectedCategory;

    private int cols,xRoom;
	
    private final int kFreeActivities         =2;
    
	public static CCScene scene() {
	    CCScene scene = CCScene.node();
	    CCLayer layer = new CategoryLayer();
	    layer.setTag(8888);
	    scene.addChild(layer); 
	    return scene;
	}
    
	public void onEnter() {
		super.onEnter();
	    super.shufflePlayBackgroundMusic();
	    this.restart();
	    super.setIsTouchEnabled(true);
	}
	
	//Display contents of the current category
	private void restart() {
		super.removeAllChildren(true);
	    CCTextureCache.sharedTextureCache().removeAllTextures();
	    selectedCategory=null;
	
	    activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    if (activeCategory.isActivity()) {
	    	//reset
	    	YDConfiguration.sharedConfiguration().setActiveCategory(null);
	    	activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    }
	    boolean topCategory=YDConfiguration.sharedConfiguration().isRoot(activeCategory);
	
	    super.setupBackground("image/misc/background.jpg",kBgModeFit);
	    super.setupTitle(activeCategory);
	    
	    //description located at the screen bottom
		int fontSize = super.mediumFontSize();
	    String fontName=super.sysFontName();
		// Create the text
	    String totalActivities=String.format(super.localizedString("label_total_activities"), SysHelper.getTotalActivities());
		String description =YDConfiguration.sharedConfiguration().isRoot(activeCategory)?totalActivities:super.localizedString(activeCategory.getDescription());
		// Create the label with our text (text1) and the container we created
		CCLabel descriptionLabel = CCLabel.makeLabel(description,fontName,fontSize);
	    descriptionLabel.setPosition(CGPoint.ccp(szWin.width-descriptionLabel.getContentSize().width/2-10, 0-descriptionLabel.getContentSize().height));
		super.addChild(descriptionLabel, 0);
		CCMoveTo pushup = CCMoveTo.action(1, CGPoint.ccp(szWin.width-descriptionLabel.getContentSize().width/2-10, descriptionLabel.getContentSize().height));
	    descriptionLabel.runAction(pushup);
	
	    //exit button on the top-left
	    if (!topCategory) {
	        super.setupNavBar(activeCategory);
	    }
	    super.setupSideToolbar(activeCategory,kOptionConfig|(SysHelper.isFullVersion()?0:kOptionUpgrade));
	    
	    fontSize=super.smallFontSize()-4;
	    int iconSize=super.categoryIconSize();
	
	    int topMargin=super.topOverhead();//top navigation bar size
	    cols=(int)(szWin.width / (iconSize+iconSize/4)); //with margin
	    xRoom=(int)(szWin.width / cols);
	    
	    //CGSize szLabel=[@"W" sizeWithFont:[UIFont fontWithName:fontName size:fontSize]];
	
	    int xIdx=0, yMargin=0;//margin between rows
	    float yPos=szWin.height - topMargin - yMargin;
	    float cyLabel=0;
	    int seq=0;
	    for (Category category : activeCategory.getSubCategories()) {
	        if (category.isActivity()) {
	            category.setSeq(seq++);
	        }
	        else {
	            category.setSeq(-1);//subcategory with activities
	        }
	        //get the path name and file type from the file name
	//        NSLog(@"Rendering %@", [category icon]);
	        String img=super.renderSVG2Img(category.getIcon(), iconSize, iconSize);
	        String icon=super.iconize(img, category.isActivity()?category.getDifficulty():888);
	        CCTexture2D texture = CCTextureCache.sharedTextureCache().addImageExternal(icon);
	        CCSprite sprite=CCSprite.sprite(texture);
	        //sprite center location
	        float x0=xIdx*xRoom + xRoom/2, y0=yPos - iconSize/2;
	        //from top to bottom
	        sprite.setPosition(CGPoint.ccp(x0, y0));
	        super.addChild(sprite, Schema.zSubCategoryIcon);
	        category.setRect(CGRect.make(x0-sprite.getContentSize().width/2, y0-sprite.getContentSize().height/2, sprite.getContentSize().width, sprite.getContentSize().height));
	        category.setUserObj(sprite);
	
	        // Create the label with our text (text1) and the container we created
	        Bitmap bmp=super.createMultipleLineLabel(super.localizedString(category.getTitle()), fontName, fontSize, 
	        		xRoom*0.9f, 
	        		(!SysHelper.isFullVersion() && category.getSeq() >= kFreeActivities)?ccColor3B.ccRED:Schema.kCategoryActNameClr, 
	        		ccColor4B.ccc4(0, 0, 0, 0));
	        CCSprite nameLabel=CCSprite.sprite(bmp, category.getTitle());
	        nameLabel.setPosition(x0, y0 - iconSize/2 - nameLabel.getContentSize().height/2);
	        super.addChild(nameLabel,Schema.zSubCategoryIcon);
	        
	        if (nameLabel.getContentSize().height > cyLabel)
	        	cyLabel=nameLabel.getContentSize().height;
	        
	        if (++xIdx >= cols) {
	            xIdx=0;
	            
	            yPos -= iconSize+nameLabel.getContentSize().height+cyLabel+yMargin;
	        }
	    }
	    
	    //[super reportMemory:@"MainEntrance"];
	}
	public boolean ccTouchesBegan(MotionEvent event)
	{
	    CGPoint pt = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
	    Category sel=null;
        for (Category category : activeCategory.getSubCategories()) {
            if (category.getRect().contains(pt.x, pt.y)) {
                sel=category;
                break;
            }
        }
	    if (sel!=null) {
	        selectedCategory=sel;
	        CCSprite sprite=(CCSprite)selectedCategory.getUserObj();
	        sprite.setColor(ccColor3B.ccc3(0xe0,0xe0,0xe0));
	    }
	    return true;
	}
	
	public boolean ccTouchesEnded(MotionEvent event)
	{
	    CGPoint pt = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
	    Category sel=null;
        for (Category category : activeCategory.getSubCategories()) {
            if (category.getRect().contains(pt.x, pt.y)) {
                sel=category;
                break;
            }
        }
	    if (sel!=null) {
	        if (!sel.isActivity()) {
	            //navigate to a cagegory
	            this.nav2(sel);
	        }
	        else {
	            if (!SysHelper.isFullVersion() && sel.getSeq() >= kFreeActivities) {
	                this.prompt2upgrade();
	            }
	            else {
	                //activity, update active category
	                super.nav2(sel);
	                CCScene scene=null;
	                try {
	                	Class<?> clz=Class.forName("name.w.yellowduck.activities."+sel.getClz());
	                	Method m = clz.getDeclaredMethod("scene", new Class[0]);
	                    scene = (CCScene)m.invoke(null, new Object[0]);
	                }
	                catch (Throwable ignore) {
	                }
	                if (scene != null) {
	                    float speed=Schema.kSceneTransitionSpeed;
	                    CCTransitionScene transition=null;
	                    int _sel=super.nextInt(5);
	                    switch (_sel) {
	                        case 0:transition=CCFadeTransition.transition(speed, scene, ccColor3B.ccWHITE);break;
	                        case 1:transition=CCFadeTRTransition.transition(speed, scene);break;
	                        case 2:transition=CCMoveInBTransition.transition(speed, scene);break;
	                        case 3:transition=CCMoveInLTransition.transition(speed, scene);break;
	                        case 4:transition=CCMoveInTTransition.transition(speed, scene);break;
	                    }
	                    CCDirector.sharedDirector().replaceScene(transition);
	                }
	            }
	        }
	    }
	    else if (selectedCategory!=null) {
	        //restore color
	        CCSprite sprite=(CCSprite)selectedCategory.getUserObj();
	        sprite.setColor(ccColor3B.ccc3(0xff,0xff,0xff));
	        
	        selectedCategory=null;
	    }
		return true;
	}

	//@Override
	protected void nav2(Category category) {
	    super.nav2(category);
	    this.restart();
	}
	//Change background music options or purchase the full version
	public void toolbarBtnTouched(Object _sender) {
		CCNode sender=(CCNode)_sender;
	    if (sender == null)
	        return;
	    switch (sender.getTag()) {
	        case Schema.kSvgConfig:
	            CCDirector.sharedDirector().replaceScene(SettingsLayer.scene());
	            break;
	        case Schema.kSvgDollar:
	            this.prompt2upgrade();
	            break;
	    }
	}
	
	private void prompt2upgrade() {
		if (!SysHelper.isFullVersion()) {
    		CCDirector.sharedDirector().getActivity().runOnUiThread(new Runnable() {
    		    public void run() {
    				Activity activity=CCDirector.sharedDirector().getActivity();
    				if (activity != null && activity instanceof MainActivity) {
	    				MainActivity mainActivity=(MainActivity)activity;
	    				mainActivity.prompt2upgrade();
    				}
    		    }
    		});
		}
	}
	//unlock all activities
	public void justUpgraded2FullVersion() {
	    if (SysHelper.isFullVersion()) {
	        this.restart();
	    }
	}
}
