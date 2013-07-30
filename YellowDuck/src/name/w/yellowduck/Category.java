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

import org.cocos2d.types.CGRect;

public class Category {
	private String name;
	private String type;
	private String icon;
	private String author;
	private String credit;
	private String clz;
	private String bg;
	private Category parent;
	private CGRect rect;
	private int tag;
	private Object userObj;
	private int difficulty;
	private String settings;
	private int seq;
	private String intr;
	
	private java.util.ArrayList<Category> subCategories;
	
	public Category() {
		super();
		this.subCategories=new java.util.ArrayList<Category>();
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	public String getClz() {
		return clz;
	}
	public void setClz(String clz) {
		this.clz = clz;
	}
	public String getBg() {
		return bg;
	}
	public void setBg(String bg) {
		this.bg = bg;
	}
	public Category getParent() {
		return parent;
	}
	public void setParent(Category parent) {
		this.parent = parent;
	}
	public CGRect getRect() {
		return rect;
	}
	public void setRect(CGRect rect) {
		this.rect = rect;
	}
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public Object getUserObj() {
		return userObj;
	}
	public void setUserObj(Object userObj) {
		this.userObj = userObj;
	}
	public int getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	public String getSettings() {
		return settings;
	}
	public void setSettings(String settings) {
		this.settings = settings;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getIntr() {
		return intr;
	}
	public void setIntr(String intr) {
		this.intr = intr;
	}

	public String getTitle() {
		return "title_"+this.name;
	}
	public String getDescription() {
		return "description_"+this.name;
	}
	public String getGoal() {
		return "goal_"+this.name;
	}
	public String getManual() {
		return "manual_"+this.name;
	}
	public java.util.ArrayList<Category> getSubCategories() {
		return this.subCategories;
	}
	public void addSubCategory(Category sub) {
		this.subCategories.add(sub);
	}
	
	public java.util.ArrayList<Category>createAncestorTree() {
		java.util.ArrayList<Category> ancestors=new java.util.ArrayList<Category>();
	    Category _parent=this.parent;
	    while (_parent != null) {
	        ancestors.add(0, _parent);
	        _parent=_parent.getParent();
	    }
	    return ancestors;
	}
	
	public boolean isActivity() {
		return this.subCategories.size()<=0;
	}
}
