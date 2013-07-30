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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class represents the different positions of the star's branches.
 */
public class SlotTypes {

	/**
	 * The enumeration of the different possible positions for a player.
	 */
	public static enum SlotType {
		NORTH, NORTHEAST, SOUTHEAST, SOUTH, SOUTHWEST, NORTHWEST, EMPTY, OUT
	}

	/**
	 * Check if a player is at the position.
	 * 
	 * @param st
	 *            The position
	 * @return true if the player is at the position st.
	 */
	public static boolean isPlayerPosition(SlotType st) {
		if (st == null)
			throw new NullPointerException("SlotType is null!");

		return st != SlotType.EMPTY && st != SlotType.OUT;
	}

	/**
	 * Get the position of the next player.
	 * 
	 * @param player
	 *            The SlotType of the player
	 * @return The next player's position
	 */
	public static SlotType nextSlotType(SlotType player) {
		switch (player) {
		case NORTH:
			return SlotType.NORTHEAST;
		case NORTHEAST:
			return SlotType.SOUTHEAST;
		case SOUTHEAST:
			return SlotType.SOUTH;
		case SOUTH:
			return SlotType.SOUTHWEST;
		case SOUTHWEST:
			return SlotType.NORTHWEST;
		case NORTHWEST:
			return SlotType.NORTH;
		}
		throw new IllegalArgumentException("player is not a player SlotType");
	}

	/**
	 * Get the player's goal
	 * 
	 * @param playerSlotType
	 *            The player's SlotType
	 * @return The goal's SlotType
	 */
	public static SlotType getGoal(SlotType playerSlotType) {
		switch (playerSlotType) {
		case NORTH:
			return SlotType.SOUTH;
		case NORTHEAST:
			return SlotType.SOUTHWEST;
		case SOUTHEAST:
			return SlotType.NORTHWEST;
		case SOUTH:
			return SlotType.NORTH;
		case SOUTHWEST:
			return SlotType.NORTHEAST;
		case NORTHWEST:
			return SlotType.SOUTHEAST;
		}
		return SlotType.EMPTY;
	}

	/**
	 * returns an iterator which iterates only on SlotTypes which are player
	 * positions
	 * 
	 * @return The iterator which iterates only on SlotTypes which are player
	 *          positions
	 * @deprecated : use SlotType.values() instead!
	 */
	public static Iterator<SlotType> iterator() {
		return new Iterator<SlotType>() {

			private SlotType currentSlotType = null;

			public boolean hasNext() {
				return currentSlotType != SlotType.NORTHWEST;
			}

			public SlotType next() {
				if (currentSlotType == SlotType.NORTHWEST
						|| currentSlotType == SlotType.OUT
						|| currentSlotType == SlotType.EMPTY)
					// end of iteration reached
					throw new NoSuchElementException();

				if (currentSlotType == null)// beginning of iteration
					currentSlotType = SlotType.NORTH;
				else
					switch (currentSlotType) {
					case NORTH:
						currentSlotType = SlotType.NORTHEAST;
					case NORTHEAST:
						currentSlotType = SlotType.SOUTHEAST;
					case SOUTHEAST:
						currentSlotType = SlotType.SOUTH;
					case SOUTH:
						currentSlotType = SlotType.SOUTHWEST;
					case SOUTHWEST:
						currentSlotType = SlotType.NORTHWEST;
					}

				return currentSlotType;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}
}
