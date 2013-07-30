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


package name.w.yellowduck.activities.strategy.chinesecheckers.ai.heuristic;

import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.ArrayBoard;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Board;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes.SlotType;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;

/**
 * HeuristicFunction which return the sum of distance from player's pawn to his
 * start camp and distance from other pawn to their goal camp.
 */
public class SimpleDistanceHeuristic extends HeuristicFunction {

	/**
	 * Default Constructor
	 */
	public SimpleDistanceHeuristic() {
		super(HeuristicFunction.HeuristicType.DISTANCE);
	}

	/**
	 * @see HeuristicFunction#value(Board, Player)
	 */
	public double value(Board b, Player player) {

		SlotType playerST = player.getSlotType();

		double value = 0;

		for (int slot = 0; slot < 121; slot++) {
			SlotType currentST = b.getSlotType(slot);
			if (playerST == currentST) {
				// for all slots of the player, add distance to start pos
				value += ArrayBoard.distanceToCamp(slot, playerST);
			} else if (!currentST.equals(playerST)
					&& SlotTypes.isPlayerPosition(currentST)) {
				// for all slots of other players, add distance to goal pos
				value += ArrayBoard.distanceToCamp(slot, SlotTypes
						.getGoal(currentST));
			}
		}

		return value;
	}

}
