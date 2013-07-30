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


package name.w.yellowduck.activities.strategy.chinesecheckers.ai;

import java.util.Comparator;

import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.ArrayBoard;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;

/**
 * This class represents valued slot used for heuristics functions. It associated a slot with a distance.
 */
public class ValuedSlot implements Comparable{
	
	/**
	 * The associated slot
	 */
	private int slot;
	
	/**
	 * The associated distance
	 */
	private int distance;

	
	/**
	 * to get a comparator for ValuedSlot objects
	 * @return a comparator for ValuedSlots
	 */
	public static Comparator<ValuedSlot> getComparator(){
		return new Comparator<ValuedSlot>(){

			public int compare(ValuedSlot s1, ValuedSlot s2) {
				return s1.compareTo(s2);
			}
			
		};
	}
	
	/**
	 * Default Constructor
	 */
	public ValuedSlot(int slot, Player p) {
		this.slot = slot;
		this.distance = ArrayBoard.distanceToCamp(slot, p.getSlotType());
	}

	/**
	 * to get the slot of ValuedSlot
	 * @return slot of ValuedSlot
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * to get the value (the distance) of ValuedSlot
	 * @return distance of ValuedSlot
	 */
	public int getDistance() {
		return distance;
	}

	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object other) {
		int otherDist = ((ValuedSlot) other).getDistance();

		int myDist = this.distance;

		if (otherDist > myDist)
			return -1;
		else if (otherDist == myDist)
			return 0;
		else
			return 1;
	}
	

}
