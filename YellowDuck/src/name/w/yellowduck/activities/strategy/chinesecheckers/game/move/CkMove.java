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


package name.w.yellowduck.activities.strategy.chinesecheckers.game.move;

import  name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;

import java.util.List;
import java.lang.Comparable;

/**
 * This interface represents a Move.
 */
public interface CkMove extends Comparable {

	/**
	 * Add an Integer to a Move.
	 * 
	 * @param j
	 *            The number of the move to add
	 */
	public void addJumPslot(Integer j);

	/**
	 * Add a List<Integer> to a move.
	 * 
	 * @param l
	 *            The List of moves' numbers
	 */
	public void addJumPslots(List<Integer> l);

	/**
	 * Get the player who do the Move.
	 * 
	 * @return The move's player
	 */
	public Player getPlayer();

	/**
	 * Get the source pawn of the move.
	 * 
	 * @return The Integer (the number) of the source pawn
	 */
	public Integer getSource();

	/**
	 * Get the target pawn of the move.
	 * 
	 * @return The Integer (the number) of the target pawn
	 */
	public Integer getTarget();

	/**
	 * Get a list of jumped slots in a Move
	 * 
	 * @return The List<Integer> of jumped slots in a Move
	 */
	public List<Integer> getJumpedSlots();

	/**
	 * Get the distance of a Move
	 * 
	 * @return The Integer which reresents the move's distance
	 */
	public Integer getDistanceMove();

	/**
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(Object other);
}
