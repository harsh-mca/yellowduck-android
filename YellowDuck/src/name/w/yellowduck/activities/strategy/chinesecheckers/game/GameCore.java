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


package name.w.yellowduck.activities.strategy.chinesecheckers.game;

import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Board;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes.SlotType;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.CkMove;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.NoSuchPlayerException;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;

import java.util.List;
import java.util.Observer;

/**
 * 
 * this interface represents object that will be able to manage the game
 *
 */
public interface GameCore {

	/**
	 * applies a given move to the current board
	 * @param m the move to be applied
	 * @return wether the move has been successfully applied or not
	 */
	boolean playMove(CkMove m);
	
	/**
	 * gives a copy of the current game board
	 * @return the copy of the board
	 */
	Board currentBoard();
	
	/**
	 * gives the player who has to play now
	 * @return the current player
	 */
	Player currentPlayer();
	
	/**
	 * gives the player who has to play after the given player
	 * @return the next player
	 */
	Player getNextPlayer(Player p) throws NoSuchPlayerException;
		
	/**
	 * returns the player corresponding to the given SlotType
	 * @param st the given sslotType
	 * @return the corresponding player
	 * @throws NoSuchPlayerException if no player corresponds
	 */
	Player getPlayerBySlotType(SlotType st) throws NoSuchPlayerException;
	
	/**
	 * starts a game with the given parameters
	 * @param gp the game parameters to start the game with
	 */
	void startGame (GameParameters gp);

	/**
	 * gives the list of current players
	 * @return the list of current players
	 */
	List<Player> players();
	
	/**
	 * says wether a game is currently running or not
	 * @return wether a game is running or not
	 */
	boolean isGameRunning();
	
	/**
	 * cancels (stops) current game
	 */
	void cancelGame();
	
	/**
	 * to check if verbose (debug) mode is activated
	 * @return wether it is activated or not
	 */
	boolean verboseMode();

	/**
	 * to check if the current game (if any) has been saved.
	 * @return wether the current game (if any) has been saved
	 */
	boolean gameHasBeenSaved();
		
	/**
	 * to check if a game is over
	 * @return wether a game is over or not
	 */
	boolean gameIsOver();

	
}
