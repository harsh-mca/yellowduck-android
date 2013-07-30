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


package name.w.yellowduck.activities.strategy.chinesecheckers.ai.tt;

import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Board;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes.SlotType;

/**
 * this class represents a key for a transposition table
 */
public class TranspositionKey {
	
	/**
	 * the related board
	 */
	private Board board;

	/**
	 * the player's SlotType
	 */
	private SlotType playerST;

	/**
	 * basic constructor
	 * @param b the board
	 * @param p the player's slotType
	 */
	public TranspositionKey(Board b, SlotType p) {
		this.board = b;
		this.playerST = p;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 **/
	public boolean equals(Object o) {
		if(!(o instanceof TranspositionKey))
			return false;
		else{
			TranspositionKey k = (TranspositionKey)o;
			return this.board.equals(k.board) && this.playerST == k.playerST;
		}

	}

	/**
	 * hashcode method written with technique of the book "Effective Java"
	 */
	public int hashCode() {
		int result = 11;
		result = result + this.board.hashCode();
		result = result * 23 + this.playerST.hashCode();

		return result;

	}
}
