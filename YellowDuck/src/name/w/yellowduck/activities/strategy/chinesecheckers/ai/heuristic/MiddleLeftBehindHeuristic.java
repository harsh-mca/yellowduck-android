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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import name.w.yellowduck.activities.strategy.chinesecheckers.ai.ValuedSlot;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.ArrayBoard;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Board;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes.SlotType;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;

/**
 * This HeuristicFunction is the combination of MiddleBoard and LeftBehind
 * Heuristics
 */
public class MiddleLeftBehindHeuristic extends HeuristicFunction {

	/**
	 * Default constructor
	 */
	public MiddleLeftBehindHeuristic() {
		super(HeuristicFunction.HeuristicType.MIDDLELEFTBEHIND);
	}

	/**
	 * @see HeuristicFunction#value(Board, Player)
	 */
	public double value(Board b, Player player) {

		double dst, coeffDstToMiddle;
		double value = 0;

		SlotType playerST = player.getSlotType();

		List<Integer> allPawns = b.allPawns(player);

		// all pawn of the player are sort by distance to its camp
		List<ValuedSlot> allPawns2 = new ArrayList<ValuedSlot>();
		for (int i : allPawns) {
			ValuedSlot v = new ValuedSlot(i, player);
			allPawns2.add(v);
		}

		Collections.sort(allPawns2, ValuedSlot.getComparator());

		// bonus for three last pawns of the player
		int LeftBehind = 3 * allPawns2.get(0).getDistance() + 2
				* allPawns2.get(1).getDistance()
				+ allPawns2.get(2).getDistance();

		value += (LeftBehind * 0.2);

		for (int slot = 0; slot < 121; slot++) {
			SlotType currentST = b.getSlotType(slot);
			if (playerST == currentST) {
				// for all slots of the player, add distance to start pos
				dst = ArrayBoard.distanceToCamp(slot, playerST);
				// for all slots of the player, add coeffDistanceToMiddle
				coeffDstToMiddle = b.getDistanceToMiddle(slot) / 100.0;
				value += dst + coeffDstToMiddle;
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
