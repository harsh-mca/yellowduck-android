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

import name.w.yellowduck.activities.strategy.chinesecheckers.ai.ValuedMove;

/**
 * 
 * this class represents a container for a value of the transposition table : a
 * search depth and a move
 * 
 */
public class TranspositionValue {

	/**
	 * the searchdepth
	 */
	private int searchDepth;

	/**
	 * the move
	 */
	private ValuedMove move;

	/**
	 * basic constructor
	 * @param s a searchdepth
	 * @param m a valued move
	 */
	public TranspositionValue(int s, ValuedMove m) {
		searchDepth = s;
		move = m;
	}

	/**
	 * basic getter for the searchdepth
	 * @return the searchdepth
	 */
	public int getSearchDepth() {
		return searchDepth;
	}

	/**
	 * basic getter for the move
	 * @return the move
	 */
	public ValuedMove getMove() {
		return move;
	}
}
