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


package name.w.yellowduck.activities.strategy.chinesecheckers.ai.algorithmic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import name.w.yellowduck.activities.strategy.chinesecheckers.ai.ValuedSlot;
import name.w.yellowduck.activities.strategy.chinesecheckers.ai.heuristic.HeuristicFunction;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.GameCore;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Board;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes.SlotType;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.PlayerType;
import android.graphics.Color;

/**
 * This class represents an AI player using a special algorithm AlphaBeta. This
 * algorithm see only his possibles moves in the begin and end game.
 * 
 */
public class AlphaBetaBeginEnd extends AlphaBeta {
	
	/**
	 * The default search
	 */
	private static int defaultSearchDepth = 3;

	
	/**
	 * The default constructor to create a player with a AlphaBetaBeginEnd algorithm
	 * 
	 * @param g
	 *            The GameCore uses in the game
	 * @param f
	 *            The evaluation functions for the player
	 * @param n
	 *            The player's name
	 * @param st
	 *            The player's Slottype
	 * @param c
	 *            The player's color
	 * @param s
	 *            The player's search depth
	 */
	public AlphaBetaBeginEnd(HeuristicFunction f, int searchDepth_, boolean useTT_) {
		super(f, searchDepth_, useTT_);
	}
	
	/**
	 * @see ai.algorithmic.AlphaBeta#calculateNextPlayer(game.board.Board, game.player.Player, game.player.Player, int)
	 */
	protected Player calculateNextPlayer(Board b, Player myPlayer,
			Player currentPlayer, int remDepth) {
		if (this.canPlayerPlayAlone(b, myPlayer, remDepth))
			return myPlayer;
		else
			return super.calculateNextPlayer(b, myPlayer, currentPlayer,
					remDepth);
	}

	/**
	 * Check if the player can play alone
	 * @param b The board
	 * @param myPlayer The player
	 * @param remDepth The search depth
	 * @return true if the player can play alone
	 */
	private boolean canPlayerPlayAlone(Board b, Player myPlayer, int remDepth) {

		if (remDepth == 0)
			return false;

		List<Player> players = super.getGameCore().players();

		ArrayList<Integer> lastPawnOtherPlayers = new ArrayList<Integer>();
		ArrayList<Integer> firstPawnOtherPlayers = new ArrayList<Integer>();
		int lastPawnMyPlayer = 0;
		int firstPawnMyPlayer = 0;

		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);

			List<Integer> allPawns = b.allPawns(p);
			List<ValuedSlot> allValuedPawns = new ArrayList<ValuedSlot>();
			for (int j : allPawns)
				allValuedPawns.add(new ValuedSlot(j, p));

			Collections.sort(allValuedPawns, ValuedSlot.getComparator());

			int last = allValuedPawns.get(0).getDistance();
			int first = allValuedPawns.get(allValuedPawns.size() - 1)
					.getDistance();

			if (p.equals(myPlayer)) {
				lastPawnMyPlayer = last;
				firstPawnMyPlayer = first;
			} else {
				lastPawnOtherPlayers.add(last);
				firstPawnOtherPlayers.add(first);
			}

		}

		int totalLengthOfBoard = 16;

		boolean beginGame = false;
		boolean endGame = false;

		for (int i = 0; i < players.size() - 1; i++) {

			// beginGame = true if first pawn of enemies are
			// at least at a distance of <remainingDepth> of player's first pawn
			boolean beg = true;
			for (int j = 0; j < firstPawnOtherPlayers.size(); j++) {
				int freeDistance = totalLengthOfBoard
						- (firstPawnMyPlayer + firstPawnOtherPlayers.get(j));
				beg = beg && freeDistance >= remDepth / 2;
				if (!beg)
					break;
			}

			beginGame = beg;

			if (beginGame)
				break;

			// endGame = true if the last pawn is at a distance of
			// <remdepth> of the last pawn of the enemies
			boolean end = true;
			for (int j = 0; j < lastPawnOtherPlayers.size(); j++) {
				int freeDistance = (lastPawnMyPlayer
						+ lastPawnOtherPlayers.get(i) - totalLengthOfBoard) / 2;
				end = end && freeDistance >= remDepth / 2;
				if (!end)
					break;
			}

			endGame = end;

		}

		return beginGame || endGame;
	}

}
