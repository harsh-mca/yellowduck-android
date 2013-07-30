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


package name.w.yellowduck.activities.strategy.chinesecheckers.game.board;

/**
 * This class represents the coordinates for a pawn.
 */
public class Coordinate {
	private int row, line;

	/**
	 * The default constructor to create a coordinate.
	 * 
	 * @param r
	 *            The row
	 * @param l
	 *            The line
	 */
	public Coordinate(int r, int l) {

		this.row = r;
		this.line = l;
	}

	/**
	 * Gets the row of a coordinate.
	 * 
	 * @return The row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Gets the line of a coordinate.
	 * 
	 * @return the line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Coordinate c) {
		return row == c.row && line == c.line;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(row:" + row + ",line:" + line + ")";
	}
}
