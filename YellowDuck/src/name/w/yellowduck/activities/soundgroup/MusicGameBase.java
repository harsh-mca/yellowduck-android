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

public abstract class MusicGameBase extends name.w.yellowduck.YDActLayerBase {
    private int availableNavButtons;
    
    protected Staff staff;
	
    public MusicGameBase () {
    	super();
        availableNavButtons=kOptionIntr|kOptionLevelButtons|kOptionRepeatButton|kOptionOk;
    }
    
    protected void setAvailableNavButtons(int buttons) {
    	this.availableNavButtons=buttons;
    }
	public void onEnter() {
		super.onEnter();
	    mMaxLevel=3;

	    Category activeCategory=YDConfiguration.sharedConfiguration().getActiveCategory();
	    super.stopBackgroundMusic(); //no background music
	    super.setupTitle(activeCategory);
	    super.setupBackground("image/activities/discovery/sound_group/piano_composition/bg.png",kBgModeTile);
	    super.setupNavBar(activeCategory);
	    super.setupSideToolbar(activeCategory, availableNavButtons);
	    super.setIsTouchEnabled(true);
	}    
}
