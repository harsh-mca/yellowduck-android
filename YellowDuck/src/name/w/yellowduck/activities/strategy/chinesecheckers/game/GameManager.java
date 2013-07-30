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

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.ArrayBoard;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Board;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes.SlotType;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.CkMove;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.IllegalMoveException;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.MoveGenerator;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.NoSuchPlayerException;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;

/**
 * this class implements the GameCore interface, and is the central game managing class, which handles the player's moves, updates the board
 */
public class GameManager extends Observable implements GameCore {

	/**
	 * minimal time after an AI move (so AI doesn't play too fast), in ms.
	 */
	private static final long MINIMUM_AI_MOVE_TIME = 500;

	/**
	 * the current players
	 */
	private List<Player> players;

	/**
	 * the current game board
	 */
	private Board gameBoard;

	/**
	 * the curent player (who has to play next)
	 */
	private Player currentPlayer;

	/**
	 * to check if a game is currently running or not
	 */
	private boolean gameIsRunning;

	/**
	 * to check if a game is over or not
	 */
	private boolean gameIsOver;

	/**
	 * to check if verbose mode is on or not
	 */
	private boolean verboseMode = false;

	/**
	 * to check if a game has been saved or not
	 */
	private boolean gameSaved = false;
	
	
	private Player winner;

	/**
	 * basic constructor
	 * @param verbose wether the debug info should be displayed on default output or not
	 */
	public GameManager(boolean verbose) {
		this.gameIsRunning = false;
		this.gameIsOver = false;
		this.players = new ArrayList<Player>();// empty players
		this.gameBoard = new ArrayBoard(players);// empty board
		this.verboseMode = verbose;
	}

	/**
	 * 
	 * default constructor, setting verbiose mode to false
	 */
	public GameManager() {
		this(false);
	}

	/**
	 * @see GameCore#startGame(GameParameters)
	 */
	public void startGame(GameParameters gp) {

		cancelGame();

		// setting players
		this.players = new ArrayList<Player>();
		for (Player p : gp.getPlayers()) {
			this.players.add(p);
		}

		//loaded game
		this.gameBoard = new ArrayBoard(players);

		this.currentPlayer = gp.getBeginningPlayer();

		// setting that game has begun
		this.gameIsRunning = true;
		this.gameIsOver = false;
		this.gameSaved = false;
	}

	/**
	 * @see GameCore#currentBoard()
	 */
	public Board currentBoard() {
		return gameBoard;
	}

	/**
	 * @see GameCore#currentPlayer()
	 */
	public Player currentPlayer() {
		return currentPlayer;

	}

	/**
	 * @see GameCore#playMove(CkMove)
	 */
	public boolean playMove(CkMove m) {

		if (gameIsRunning) {

			debugPrint("trying to apply move from " + m.getPlayer().getName()
					+ "(current player is:" + currentPlayer.getName() + ")");

			if (!currentPlayer.equals(m.getPlayer())) {// wrong player

				debugPrint("wrong player. move cancelled.");

			} else if (!MoveGenerator.checkMove(m, gameBoard)) {// illegal move

				debugPrint("bad move (checked by MoveValidator). move cancelled.");

			} else {
				try {
					this.gameBoard.applyMove(m);

					debugPrint("applied move successfully.");

					gameSaved = false;

					try {

						//check if there is a winner in the game

						this.winner = getPlayerBySlotType(gameBoard
								.getWinnerSlotType());

						debugPrint("GAME OVER, winner is " + winner.getName());

						this.gameIsRunning = false;
						this.gameIsOver = true;

					} catch (NoSuchPlayerException e) {

						// nobody has won
						this.changeCurrentPlayer();
						debugPrint("next player is now "
								+ currentPlayer.getName());

					}

					return true;

				} catch (IllegalMoveException e) {

					debugPrint("bad move (checked by Board!). move cancelled.");
				}
			}
		}

		return false;
	}

	/**
	 * @see GameCore#players()
	 */
	public List<Player> players() {
		return players;
	}

	/**
	 * @see GameCore#isGameRunning()
	 */
	public boolean isGameRunning() {
		return gameIsRunning;
	}

	/**
	 * returns the player corresponding to the given SlotType
	 * @param st the given sslotType
	 * @return the corresponding player
	 * @throws NoSuchPlayerException if no player corresponds
	 */
	public Player getPlayerBySlotType(SlotType st) throws NoSuchPlayerException {
		for (Player p : players)
			if (p.getSlotType() == st)
				return p;
		throw new NoSuchPlayerException(st);
	}

	/**
	 *  @see GameCore#getNextPlayer(Player)
	 */
	public Player getNextPlayer(Player p) throws NoSuchPlayerException {

		Player nextP = null;
		SlotType nextST = p.getSlotType();
		while (nextP == null) {
			nextST = SlotTypes.nextSlotType(nextST);
			for (Player plyr : players)
				if (nextST == plyr.getSlotType())
					nextP = plyr;
		}

		if (!players.contains(p) || nextP == null)
			throw new NoSuchPlayerException(p.getSlotType());
		else
			return nextP;
	}

	/**
	 * private method to determine and set the next playing player
	 */
	private void changeCurrentPlayer() {
		try {
			this.currentPlayer = getNextPlayer(this.currentPlayer);
		} catch (NoSuchPlayerException e) {
			;
		}
	}

	/**
	 * @see GameCore#cancelGame()
	 */
	public void cancelGame() {

		if (isGameRunning()) {
			gameIsRunning = false;
		}
		clearGame();

	}

	/**
	 * Delete all the players, history and statistics.
	 *
	 */
	private void clearGame() {
	}

	/**
	 * prints a given debug text on the output if verbose mode is on.
	 * @param s the string to display
	 */
	public void debugPrint(String s) {
		if (verboseMode)
			System.out.println("--- CORE: " + s);
	}

	/**
	 * to check if verbose is activated or not
	 * @return wether verbose mode is activated or not
	 */
	public boolean verboseMode() {
		return verboseMode;
	}

	/**
	 * @see GameCore#gameHasBeenSaved()
	 */
	public boolean gameHasBeenSaved() {
		return gameSaved;
	}

	/**
	 * @see GameCore#gameIsOver()
	 */
	public boolean gameIsOver() {
		return this.gameIsOver;
	}

	public Player getWinner() {
		return gameIsOver?winner:null;
	}

}
