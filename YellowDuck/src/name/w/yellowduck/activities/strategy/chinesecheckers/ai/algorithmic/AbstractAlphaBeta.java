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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import name.w.yellowduck.activities.strategy.chinesecheckers.ai.AIMove;
import name.w.yellowduck.activities.strategy.chinesecheckers.ai.ValuedMove;
import name.w.yellowduck.activities.strategy.chinesecheckers.ai.heuristic.HeuristicFunction;
import name.w.yellowduck.activities.strategy.chinesecheckers.ai.tree.SearchNode;
import name.w.yellowduck.activities.strategy.chinesecheckers.ai.tree.SearchTree;
import name.w.yellowduck.activities.strategy.chinesecheckers.ai.tt.TranspositionTable;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.GameCore;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Board;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes.SlotType;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.CkMove;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.IllegalMoveException;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.MoveComparator;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.NoSuchPlayerException;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.PlayerType;
import android.graphics.Color;

/**
 * This class is the super class for all implementations of AlphaBeta
 */
public abstract class AbstractAlphaBeta {

	/**
	 * The bonus for a node which wins with a small search Depth
	 */
	protected static final double bonusFactorForShortWinningMoves = .2;

	/**
	 * The default search depth
	 */
	protected static int defaultSearchDepth = 1;

	/**
	 * The number of visited nodes
	 */
	protected int visitedNodes = 0;

	protected boolean useTT = false, calculateSearchTree=false;

	protected TranspositionTable transpositionTable;

	protected int[] saved = new int[100];

	private int searchDepth;
	
	protected HeuristicFunction evalFunction;
	protected SearchTree tree;
	/**
	 * The constructor to create an AlphaBeta player
	 * 
	 * @param gm
	 *            The GameCore
	 * @param f
	 *            the evaluation function for the algorithm
	 * @param n
	 *            The player's name
	 * @param st
	 *            The player's slotType
	 * @param pt
	 *            The player type
	 * @param c
	 *            The player's color
	 * @param searchDepth
	 *            The search depth
	 * @param useTT
	 *            true if useTT else false
	 */
	public AbstractAlphaBeta(HeuristicFunction f, int searchDepth_, boolean useTT_) {
		super();
        evalFunction=f;
        searchDepth = searchDepth_;
        
		useTT = useTT_;
		if (useTT) {
			transpositionTable =  new TranspositionTable();
			for (int i = 0; i < 100; i++)
				saved[i] = 0;
		}
        calculateSearchTree=true;
	}

	/**
	 * @see ai.AIPlayer#calculateMove(game.board.Board, game.player.Player)
	 */
	public AIMove calculateMove(Board b, Player myPlayer) {

		visitedNodes = 0;
		long begTime = Calendar.getInstance().getTimeInMillis();

		long endTime = begTime;

		ValuedMove bestMove = null;

		// a better value has already been calculated for this configuration!
		// let's take it.
		if (useTT
				&& transpositionTable.betterValueIsContained(b, myPlayer
						.getSlotType(), searchDepth)) {
			saved[searchDepth]++;
			bestMove = transpositionTable.getMove(b, myPlayer.getSlotType());
		} else {

			List<CkMove> possiblesMoves = getPossibleMoves(b, myPlayer);

			Collections.sort(possiblesMoves, Collections.reverseOrder());

			SearchNode root = calculateSearchTree ? new SearchNode() : null;
			this.tree = calculateSearchTree ? new SearchTree(root,
					"Search Tree for " + myPlayer.getName(), visitedNodes, 0)
					: null;
			SearchNode max = calculateSearchTree ? new SearchNode(root, true,
					possiblesMoves.size()) : null;

			// the remaining depth and next player for the next recursion level
			int newRemainingDepth = calculateRemainingSearchDepth(b, myPlayer,
					myPlayer, searchDepth);
			Player nextPlayertoCalculate = calculateNextPlayer(b, myPlayer,
					myPlayer, searchDepth);

			double alpha = Double.NEGATIVE_INFINITY;
			double beta = Double.POSITIVE_INFINITY;

			List<ValuedMove> bestMoves = new ArrayList<ValuedMove>();

			for (CkMove m : possiblesMoves) {
				visitedNodes++;
				double value;
				try {

					SearchNode child = calculateSearchTree ? new SearchNode(max)
							: null;

					value = alphaBetaValue(b.getResultBoard(m), myPlayer,
							nextPlayertoCalculate, newRemainingDepth,
							alpha - 1, beta, child);

					alpha = Math.max(alpha, value);

					ValuedMove valuedMove = new ValuedMove(m, value);

					if (calculateSearchTree)
						child.setMove(valuedMove);

					updateBestMove(b, bestMoves, valuedMove);

				} catch (IllegalMoveException e) {
					continue;
				}
			}
			bestMove = selectBestMove(bestMoves);

			if (useTT)
				transpositionTable.replaceValue(b, myPlayer.getSlotType(),
						searchDepth, bestMove);

			if (calculateSearchTree) {
				for (SearchNode n : max.getChildren()) {
					if (bestMoves.contains(n.getMove()))
						n.setBest();
					if (n.getMove().equals(bestMove))
						n.setChoosen();
				}

				endTime = Calendar.getInstance().getTimeInMillis();

				this.tree = new SearchTree(root, "Search Tree for "
						+ myPlayer.getName(), visitedNodes, endTime - begTime);
			}

		}

		endTime = Calendar.getInstance().getTimeInMillis();

		return new AIMove(bestMove, visitedNodes, endTime - begTime);
	}

	/**
	 * Gets the value of a node
	 * 
	 * @param b
	 *            The game board
	 * @param myPlayer
	 *            The player
	 * @param currentPlayer
	 *            The current player
	 * @param remainingDepth
	 *            The search depth
	 * @param alpha
	 *            The value of Alpha
	 * @param beta
	 *            the value of Beta
	 * @param currentNode
	 *            the node search
	 * @return The node value
	 */
	private double alphaBetaValue(Board b, Player myPlayer,
			Player currentPlayer, int remainingDepth, double alpha,
			double beta, SearchNode currentNode) {

		// a better value has already been calculated for this configuration!
		// let's take it.
		if (useTT
				&& transpositionTable.betterValueIsContained(b, currentPlayer
						.getSlotType(), remainingDepth)) {
			saved[remainingDepth]++;
			return transpositionTable.getValue(b, currentPlayer.getSlotType());
		}

		try {
			if (remainingDepth <= 0
					|| SlotTypes.isPlayerPosition(b.getWinnerSlotType())) {

				if (calculateSearchTree && currentNode != null) {
					String s;
					if (remainingDepth == 0)
						s = "SEARCH DEPTH REACHED";
					else
						s = "WINNER : ";
								//+ gameCore.getPlayerBySlotType(b
									//	.getWinnerSlotType());
					currentNode.addChild(new SearchNode(currentNode, s));
				}

				return evaluateEndPosition(b, myPlayer, remainingDepth);
			}
		} catch (NoSuchPlayerException e) {
			;// do nothing special, nobody has won
		}

		// the remaining depth and next player for the next recursion level
		int newRemainingDepth = calculateRemainingSearchDepth(b, myPlayer,
				currentPlayer, remainingDepth);
		Player nextPlayertoCalculate = calculateNextPlayer(b, myPlayer,
				currentPlayer, remainingDepth);

		boolean max = myPlayer.equals(currentPlayer);

		List<CkMove> possiblesMoves = getPossibleMoves(b, currentPlayer);

		SearchNode minMax = calculateSearchTree ? new SearchNode(currentNode,
				max, possiblesMoves.size()) : null;

		ValuedMove bestMove = null;

		if (max)
			Collections.sort(possiblesMoves, Collections.reverseOrder());
		else
			Collections.sort(possiblesMoves, new MoveComparator());

		for (CkMove m : possiblesMoves) {
			visitedNodes++;
			double newValue;
			if (max)
				newValue = alpha;
			else
				newValue = beta;

			try {

				SearchNode child = calculateSearchTree ? new SearchNode(minMax)
						: null;

				newValue = alphaBetaValue(b.getResultBoard(m), myPlayer,
						nextPlayertoCalculate, newRemainingDepth, alpha, beta,
						child);

				if (calculateSearchTree)
					child.setMove(new ValuedMove(m, newValue));

			} catch (IllegalMoveException e) {
				continue;
			}
			if (max) {
				if (isStrictlyGreaterThan(newValue, alpha)) {
					alpha = newValue;
					bestMove = new ValuedMove(m, newValue);
				}
				if (isGreaterOrEqualsThan(alpha, beta))// CUT
					return alpha;
			} else {
				if (isStrictlyGreaterThan(beta, newValue)) {
					beta = newValue;
					bestMove = new ValuedMove(m, newValue);
				}
				if (isGreaterOrEqualsThan(alpha, beta))// CUT
					return beta;
			}
		}

		// put the fresh calculated value into the transposition table
		if (useTT && bestMove != null)
			transpositionTable.replaceValue(b, currentPlayer.getSlotType(),
					remainingDepth, bestMove);

		return max ? alpha : beta;

	}

	/**
	 * Get the next player
	 * 
	 * @param b
	 *            The game board
	 * @param myPlayer
	 *            The player
	 * @param currentPlayer
	 *            The current player
	 * @param remainingSearchDepth
	 *            The search depth
	 * @return the next player
	 */
	protected abstract Player calculateNextPlayer(Board b, Player myPlayer,
			Player currentPlayer, int remainingSearchDepth);

	/**
	 * Get the next search depth
	 * 
	 * @param b
	 *            The game board
	 * @param myPlayer
	 *            The player
	 * @param myPlayer2
	 *            The player 2
	 * @param remainingSearchDepth
	 *            The search depth
	 * @return the next seach Depth
	 */
	protected abstract int calculateRemainingSearchDepth(Board b,
			Player myPlayer, Player myPlayer2, int remainingSearchDepth);

	/**
	 * Get the list of possible moves for player p with the board b
	 * 
	 * @param b
	 *            The game board
	 * @param p
	 *            The player
	 * @return list of possible moves for player p
	 */
	protected abstract List<CkMove> getPossibleMoves(Board b, Player p);

	/**
	 * Permit to update the list of bestMove
	 * 
	 * @param b
	 *            The board
	 * @param bestMoves
	 *            initial list of BestMove
	 * @param move
	 *            The valued move to test
	 */
	protected abstract void updateBestMove(Board b, List<ValuedMove> bestMoves,
			ValuedMove move);

	/**
	 * Select a move from a list of bestMoves
	 * 
	 * @param bestMoves
	 *            The list of bestMoves
	 * @return the valued move
	 */
	protected abstract ValuedMove selectBestMove(List<ValuedMove> bestMoves);

	/**
	 * Evaluate the end position
	 * 
	 * @param b
	 *            The board
	 * @param p
	 *            The player
	 * @param remainingDepth
	 *            The search depth
	 * @return a value for the end position
	 */
	protected abstract double evaluateEndPosition(Board b, Player p,
			int remainingDepth);

	/**
	 * Check if two double are equal
	 * 
	 * @param v1
	 *            first double
	 * @param v2
	 *            second double
	 * @return check if v1 and v2 are egals
	 */
	protected abstract boolean areEqual(double v1, double v2);

	/**
	 * Check if the first double is strictly greater than the second
	 * 
	 * @param v1
	 *            first double
	 * @param v2
	 *            second double
	 * @return check is strictly first double is greater than the second
	 */
	protected abstract boolean isStrictlyGreaterThan(double v1, double v2);

	/**
	 * Check if the first double is greater than the second
	 * 
	 * @param v1
	 *            first double
	 * @param v2
	 *            second double
	 * @return check is first double is greater than the second
	 */
	protected boolean isGreaterOrEqualsThan(double v1, double v2) {
		return areEqual(v1, v2) || isStrictlyGreaterThan(v1, v2);
	}

}
