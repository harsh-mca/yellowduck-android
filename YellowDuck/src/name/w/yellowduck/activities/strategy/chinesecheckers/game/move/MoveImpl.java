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

import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.ArrayBoard;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the interface Move.
 */
public class MoveImpl implements CkMove {

	/**
	 * The move's player
	 */
	private Player player;

	/**
	 * The move's source
	 */
	private Integer source;

	/**
	 * The move's target
	 */
	private Integer target;

	/**
	 * The list of jumped slots in the move
	 */
	private List<Integer> jumpedSlots;

	/**
	 * The move's distance
	 */
	private Integer distanceMove;

	/**
	 * The default constructor to create a move.
	 * 
	 * @param src
	 *            The move's source
	 * @param tgt
	 *            The move's target
	 * @param p
	 *            The move's player
	 * @throws IllegalArgumentException
	 */
	public MoveImpl(Integer src, Integer tgt, Player p)
			throws IllegalArgumentException {
		if (src == tgt)
			throw new IllegalArgumentException("source = target!");

		this.source = src;
		this.target = tgt;
		this.player = p;
		this.jumpedSlots = new ArrayList<Integer>();
		this.distanceMove = Integer.MAX_VALUE;

	}

	/**
	 * @see Move#addJumPslot(Integer)
	 */
	public void addJumPslot(Integer j) {
		jumpedSlots.add(j);
	}

	/**
	 * @see Move#addJumPslots(List)
	 */
	public void addJumPslots(List<Integer> l) {
		for (Integer j : l)
			jumpedSlots.add(j);
	}

	/**
	 * @see Move#getPlayer()
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @see Move#getSource()
	 */
	public Integer getSource() {
		return source;
	}

	/**
	 * @see Move#getTarget()
	 */
	public Integer getTarget() {
		return target;
	}

	/**
	 * @see Move#getJumpedSlots()
	 */
	public List<Integer> getJumpedSlots() {
		return jumpedSlots;
	}

	/**
	 * @see Move#getDistanceMove()
	 */
	public Integer getDistanceMove() {
		if (distanceMove == Integer.MAX_VALUE) {
			int tgt = ArrayBoard.distanceToCamp(target, player.getSlotType());
			int src = ArrayBoard.distanceToCamp(source, player.getSlotType());
			this.distanceMove = tgt - src;
		}

		return distanceMove;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {

		if (!(o instanceof CkMove))
			return false;
		else {
			CkMove m = (CkMove) o;
			return player.equals(m.getPlayer()) && source.equals(m.getSource())
					&& target.equals(m.getTarget());
		}
	}

	/**
	 * hashcode method written with technique of the book "Effective Java"
	 */
	public int hashCode() {

		int result = 17;
		result = source + 37 * result;
		result = target + 37 * result;

		return result;
	}

	public String toString() {

		String s = "Move from " + source + " to " + target + " by "
				+ player.getName();

		if (jumpedSlots.size() > 0) {
			s += " with jumps over : ";
			for (Integer i : jumpedSlots)
				s += i + " - ";
		}
		return s;
	}

	/**
	 * @see Move#compareTo(Object)
	 */
	public int compareTo(Object other) {

		if (other instanceof CkMove) {
			int otherValue = ((CkMove) other).getDistanceMove();

			int myValue = this.getDistanceMove();

			if (otherValue == myValue)
				return 0;
			else if (otherValue > myValue)
				return -1;
			else
				return 1;
		} else
			throw new IllegalArgumentException("not a move");
	}
}
