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


import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;

public class MainActivity extends Activity {
	protected CCGLSurfaceView _glSurfaceView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        YDConfiguration.context=this.getApplicationContext();
        
		android.util.DisplayMetrics dm = new android.util.DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
	    YDConfiguration.sharedConfiguration().setScreenUnit(dm.densityDpi);
        
        LinearLayout gameLayout=(LinearLayout)this.findViewById(R.id.gameViewLayout);
        _glSurfaceView = new CCGLSurfaceView(this);
        gameLayout.addView(_glSurfaceView);    
        
        
        YDConfiguration.sharedConfiguration().setAdWidth(AdSize.BANNER.getWidth());
        YDConfiguration.sharedConfiguration().setAdHeight(AdSize.BANNER.getHeight());
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	
    	CCDirector.sharedDirector().attachInView(_glSurfaceView);
    	CCDirector.sharedDirector().setLandscape(true);
        CCDirector.sharedDirector().setDisplayFPS(false);
        CCDirector.sharedDirector().setAnimationInterval(1.0f / 30.0f);

        CCScene scene = IntroLayer.scene();
        CCDirector.sharedDirector().runWithScene(scene);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        CCDirector.sharedDirector().pause();
    }
     
    @Override
    public void onResume()
    {
        super.onResume();
        CCDirector.sharedDirector().resume();
    }
     
    @Override
    public void onStop()
    {
        super.onStop();
        CCDirector.sharedDirector().end();
    }
}
