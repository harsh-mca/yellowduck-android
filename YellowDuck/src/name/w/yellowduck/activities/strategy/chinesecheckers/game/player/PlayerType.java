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


package name.w.yellowduck.activities.strategy.chinesecheckers.game.player;

/**
 * This is an enumeration of the player's type
 */
public enum PlayerType {
	HUMAN, ALPHABETA, SCOUT, MINIMAX, RANDOM, NAIVE, 
	ALPHABETA_LASTPAWN, ALPHABETA_TT, MINIMAX_TT, 
	ALPHABETA_CUT, ALPHABETA_BEGINEND
}
