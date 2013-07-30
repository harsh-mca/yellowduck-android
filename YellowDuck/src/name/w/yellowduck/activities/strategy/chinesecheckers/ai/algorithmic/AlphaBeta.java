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

import java.util.List;

import name.w.yellowduck.activities.strategy.chinesecheckers.ai.ValuedMove;
import name.w.yellowduck.activities.strategy.chinesecheckers.ai.heuristic.HeuristicFunction;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.GameCore;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Board;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.CkMove;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.MoveGenerator;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.NoSuchPlayerException;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;

/**
 * This class represents an AI player using the algorithm AlphaBeta
 *
 */
public class AlphaBeta extends AbstractAlphaBeta {
	private GameCore gameCore;
	/**
	 * The default search
	 */
	private static int defaultSearchDepth = 3;

	/**
	 * The default constructor to create a player with a AlphaBeta algorithm
	 * 
	 * @param gm
	 *            The GameCore uses in the game
	 * @param f
	 *            The evaluation functions for the player
	 * @param n
	 *            The player's name
	 * @param st
	 *            The player's Slottype
	 * @param pt
	 *            The player type
	 * @param c
	 *            The player's color
	 * @param searchDepth
	 *            The player's search depth
	 */
	public AlphaBeta(HeuristicFunction f, int searchDepth_, boolean useTT_) {
		super(f, searchDepth_, useTT_);
	}

	/**
	 * @see ai.algorithmic.AbstractAlphaBeta#getPossibleMoves(game.board.Board,
	 *      game.player.Player)
	 */
	protected List<CkMove> getPossibleMoves(Board b, Player p) {
		return MoveGenerator.possibleMovesJumpsFirst(b, p);
	}

	/**
	 * @see ai.algorithmic.AbstractAlphaBeta#updateBestMove(game.board.Board,
	 *      java.util.List, ai.ValuedMove)
	 */
	protected void updateBestMove(Board b, List<ValuedMove> bestMoves,
			ValuedMove move) {
		if (bestMoves.size() == 0)
			bestMoves.add(move);
		else {
			/*
			 * //determine the maximum value of current best moves double
			 * maxValue = 0; for (int i = 0; i < bestMoves.size(); i++){ double
			 * currentVal = bestMoves.get(i).getValue(); if(maxValue <
			 * currentVal) maxValue = currentVal; }
			 */
			double maxValue = bestMoves.get(0).getValue();

			double val = move.getValue();

			if (areEqual(val, maxValue))
				bestMoves.add(move);
			else if (isStrictlyGreaterThan(val, maxValue)) {
				bestMoves.clear();
				bestMoves.add(move);
			}

		}
	}

	/**
	 * @see ai.algorithmic.AbstractAlphaBeta#selectBestMove(java.util.List)
	 */
	protected ValuedMove selectBestMove(List<ValuedMove> bestMoves) {
		int randomInd = (int) (Math.random() * (double) (bestMoves.size()));
		return bestMoves.get(randomInd);
	}

	/**
	 * @see ai.algorithmic.AbstractAlphaBeta#evaluateEndPosition(game.board.Board,
	 *      game.player.Player, int)
	 */
	protected double evaluateEndPosition(Board b, Player p, int remainingDepth) {
		double bonusFactor = 1.0 + remainingDepth
				* bonusFactorForShortWinningMoves;
		return bonusFactor * this.evalFunction.value(b, p);
	}

	/**
	 * @see ai.algorithmic.AbstractAlphaBeta#areEqual(double, double)
	 */
	protected boolean areEqual(double v1, double v2) {
		return v1 == v2;
	}

	/**
	 * @see ai.algorithmic.AbstractAlphaBeta#isStrictlyGreaterThan(double,
	 *      double)
	 */
	protected boolean isStrictlyGreaterThan(double v1, double v2) {
		return v1 > v2;
	}

	/**
	 * @see ai.algorithmic.AbstractAlphaBeta#calculateNextPlayer(game.board.Board,
	 *      game.player.Player, game.player.Player, int)
	 */
	protected Player calculateNextPlayer(Board b, Player myPlayer,
			Player currentPlayer, int remainingSearchDepth) {
		try {
			return gameCore.getNextPlayer(currentPlayer);
		} catch (NoSuchPlayerException e) {
			return currentPlayer;
		}
	}

	/**
	 * @see ai.algorithmic.AbstractAlphaBeta#calculateRemainingSearchDepth(game.board.Board,
	 *      game.player.Player, game.player.Player, int)
	 */
	protected int calculateRemainingSearchDepth(Board b, Player myPlayer,
			Player currentPlayer, int remainingSearchDepth) {
		return remainingSearchDepth - 1;
	}

	public GameCore getGameCore() {
		return gameCore;
	}

	public void setGameCore(GameCore gameCore) {
		this.gameCore = gameCore;
	}

}
