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


package name.w.yellowduck.activities.strategy.chinesecheckers.ai;

import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes.SlotType;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.PlayerType;


/**
 * This class represents an AI player.
 */
public class AIPlayer extends name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player {
	public AIPlayer (String n, SlotType st, PlayerType pt, int c) {
		super (n, st, pt, c);
	}
}
