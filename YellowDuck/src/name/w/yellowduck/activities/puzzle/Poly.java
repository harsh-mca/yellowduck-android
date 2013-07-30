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


package name.w.yellowduck.activities.puzzle;
//Tangram outline element
public class Poly extends Object {
	public static final int kTangramOutlineNormal      =5;
	public static final int kTangramOutlineBack        =6;
	public static final int kTangramOutlineOn          =7;
	
	private int firstPoint;
	private int pointNbr;
	private int type;

	public Poly(int fp, int pn) {
		super();
		this.firstPoint=fp;
		this.pointNbr=pn;
		this.type=kTangramOutlineNormal;
	}
	public Poly(int fp, int pn, int ty) {
		super();
		this.firstPoint=fp;
		this.pointNbr=pn;
		this.type=ty;
	}
	
	public int getFirstPoint() {
		return firstPoint;
	}
	public void setFirstPoint(int firstPoint) {
		this.firstPoint = firstPoint;
	}
	public int getPointNbr() {
		return pointNbr;
	}
	public void setPointNbr(int pointNbr) {
		this.pointNbr = pointNbr;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	
}
