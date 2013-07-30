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

import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Directions.Direction;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.CkMove;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.IllegalMoveException;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.NoSuchPlayerException;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes.SlotType;
import java.util.List;

/**
 * This interface represents the game Board
 */
public interface Board {

	/**
	 * applies a given move to the board
	 * @param m The CkMove
	 */
	void applyMove(CkMove m) throws IllegalMoveException;

	/**
	 * applies a given move to the board, uses for replay game
	 * @param m The CkMove
	 * @throws IllegalMoveException
	 */
	void backMove(CkMove m) throws IllegalMoveException;

	/**
	 * gives a copy of the board, as if the given move had been applied.
	 * this is used by the AI classes.
	 * @param m The move
	 * @return The result board
	 * @throws IllegalMoveException
	 */
	Board getResultBoard(CkMove m) throws IllegalMoveException;

	/**
	 * Gives a copy of the ArrayBoard
	 * This is used by GameHistory
	 * @return The converted board in SlotType[]
	 */
	SlotType[] getBoard();

	/**
	 * returns all the slots occupied by a given player
	 * @param p The plyare
	 * @return The list of player's pawns
	 */
	List<Integer> allPawns(Player p);

	/**
	 * returns a particular neighbour of a given slot
	 * @param slot The slot
	 * @param d The direction
	 * @return The neighbour of slot at the poisition d
	 */
	Integer getNeighbour(Integer slot, Direction d)
			throws NoSuchNeighbourException;

	/**
	 * returns the type of a slot on the board
	 * @param slot The number of the slot
	 * @return The SlotType of a slot
	 */
	SlotType getSlotType(Integer slot);

	/**
	 * returns wether a given slot of the board is occupied by a pawn or not
	 * @param slot The number of the slot
	 * @return true if the slot is occupied
	 */
	boolean isOccupied(Integer slot);

	/**
	 * returns wether a given slot is empty or not
	 * @param slot The number of the slot 
	 * @return true is the slot is empty
	 */
	boolean isEmpty(Integer slot);

	/**
	 * returns the winner of the game on the current board
	 * @return The winner of the game on the current board
	 * @throws a nosuchplayerException if no winner
	 */
	SlotType getWinnerSlotType() throws NoSuchPlayerException;

	/**
	 * returns a matrix coordinate corresponding to the given integer
	 * @param slot The number of the slot
	 * @return The matrix coordinate corresponding to the given integer
	 */
	Coordinate getCoordinate(Integer slot);

	/**
	 * returns a SlotType if the given slot is in a camp (a branch of the star)
	 * or EMPTY if it's the middle. else, returns OUT.
	 * 
	 * @param slot The number of the slot
	 * @return The SlotType if the given slot is in a camp
	 */
	SlotType getCamp(Integer slot);

	/**
	 * Get the distance to middle from a slot.
	 * @param slot The number of the slot
	 * @return The distance from the slot to the middle
	 */
	int getDistanceToMiddle(Integer slot);

}
