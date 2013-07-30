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

import org.cocos2d.types.CGPoint;

public class EighthNote extends Note {
	public EighthNote(int numId, int staff, boolean sharpNotation) {
		super(numId, staff, sharpNotation);
        super.setNoteType(8);
        super.setMillisecs(250);
        super.setBeatNums("+");
        
        super._setAppearance("eighthNote.png",(numId > 8 || numId < -6),CGPoint.ccp(25, 16));
	}
}
