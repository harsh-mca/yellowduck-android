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

import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public class YDShape extends Object {
	public static final int kShapeLabelText                 =0;
	public static final int kShapeLabelImage                =1;
	public static final int kShapeStock                     =2;
	public static final int kShapeBackground                =3;
	
	private String resource;
	private String name;
	private String sound;
	private String voice;
	private String voice2;
	private String extra;
	private int type;
	private CGPoint position;
	private float scale;
	private CGSize fit2;
	private boolean pinned;
	
	public YDShape(String _recource, int _type) {
		super();
		this.resource=_recource;
		this.type=_type;
	}
	
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSound() {
		return sound;
	}
	public void setSound(String sound) {
		this.sound = sound;
	}
	public String getVoice() {
		return voice;
	}
	public void setVoice(String voice) {
		this.voice = voice;
	}
	public String getVoice2() {
		return voice2;
	}
	public void setVoice2(String voice2) {
		this.voice2 = voice2;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public CGPoint getPosition() {
		return position;
	}
	public void setPosition(CGPoint position) {
		this.position = position;
	}
	public float getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
	public CGSize getFit2() {
		return fit2;
	}
	public void setFit2(CGSize fit2) {
		this.fit2 = fit2;
	}
	public boolean isPinned() {
		return pinned;
	}
	public void setPinned(boolean pinned) {
		this.pinned = pinned;
	}

	public boolean isStock() {
	    return type == kShapeStock;
	}
	public boolean isBackground() {
	    return type == kShapeBackground;
	}
	public boolean isLabelText() {
	    return type == kShapeLabelText;
	}
}
