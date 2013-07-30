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

import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;

import java.util.List;

/**
 * This class represents the game parameters of a chinese checkers game (players and beginning player)
 */
public class GameParameters {

	protected List<Player> players;

	protected Player beginningPlayer;

	/**
	 * Create a GameParameters corresponding to a new game
	 * @param p The list of the players who ply
	 * @param beginner The Player who start to play
	 */
	public GameParameters(List<Player> p, Player beginner) {
		players = p;
		beginningPlayer = beginner;
	}

	/**
	 * Get the list of players of the game
	 * @return The List<Player>
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * Get the beginning player
	 * @return The player who have to start to play
	 */
	public Player getBeginningPlayer() {
		return beginningPlayer;
	}
}
