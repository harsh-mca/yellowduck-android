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
 * This class represents the different neighbours of a pawn.
 */
public class Directions {

	/**
	 * The enumeration of the neighbours.
	 */
	public static enum Direction {
		WEST, NORTHWEST, NORTHEAST, EAST, SOUTHEAST, SOUTHWEST;

	}

	/**
	 * Gives the next direction for a pawn.
	 * 
	 * @param d
	 *            The direction
	 * @return The next direction
	 */
	public static Direction nextDirection(Direction d) {
		switch (d) {
		case WEST:
			return Direction.NORTHWEST;
		case NORTHWEST:
			return Direction.NORTHEAST;
		case NORTHEAST:
			return Direction.EAST;
		case EAST:
			return Direction.SOUTHEAST;
		case SOUTHEAST:
			return Direction.SOUTHWEST;
		case SOUTHWEST:
			return Direction.WEST;
		}
		throw new IllegalArgumentException();
	}

}
