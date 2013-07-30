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

/**
 * The exception class when a illegal move is applied.
 */
public class IllegalMoveException extends Exception {

	private static final long serialVersionUID = -3464447251266707892L;

	/**
	 * The illegal Move.
	 */
	private CkMove illegalMove;

	/**
	 * The default constructor
	 * 
	 * @param m
	 *            The illegal move
	 */
	public IllegalMoveException(CkMove m) {
		super();
		illegalMove = m;
	}

	/**
	 * Get the illegal Move
	 * 
	 * @return The illegal Move
	 */
	public CkMove getIllegalMove() {
		return illegalMove;
	}

}
