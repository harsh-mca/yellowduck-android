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


package name.w.yellowduck.activities.strategy.chinesecheckers.game.move;

import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Board;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.NoSuchNeighbourException;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Directions.Direction;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes.SlotType;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;

import java.util.List;
import java.util.ArrayList;

/**
 * This class checks and generates moves.
 */
public class MoveGenerator {

	/**
	 * private constructor for avoiding the instanciation of this helper class
	 */
	private MoveGenerator() {
	}

	/**
	 * calculates all valid moves for a given board and player. This method is
	 * designed especially for alpha-beta based algorithms, for which it's
	 * important to have better moves first.
	 * 
	 * @param b
	 *            The game Board
	 * @param p
	 *            The Player who have to play
	 * @return The list of all possible moves
	 */
	public static List<CkMove> possibleMovesJumpsFirst(Board b, Player p) {

		ArrayList<CkMove> possibleMoves = new ArrayList<CkMove>();

		// all the pawns of player p
		List<Integer> pawns = b.allPawns(p);

		//calculate the jumps
		for (int i = 0; i < pawns.size(); i++)
			addPossibleJumps(pawns.get(i), b, p, possibleMoves);

		//calculate the simple moves
		for (int i = 0; i < pawns.size(); i++)
			addPossibleSimpleMoves(pawns.get(i), b, p, possibleMoves);

		return possibleMoves;
	}

	/**
	 * Calculates all valid moves for a given board and player. It's slightly
	 * faster than the possiblMovesJumpsFirst method.
	 * 
	 * @param b
	 *            The game Board
	 * @param p
	 *            The Player who have to play
	 * @return The list of all valid moves for a given board and player
	 */
	public static List<CkMove> possibleMoves(Board b, Player p) {

		ArrayList<CkMove> possibleMoves = new ArrayList<CkMove>();

		// all the pawns of player p
		List<Integer> pawns = b.allPawns(p);

		for (int i = 0; i < pawns.size(); i++) {
			addPossibleJumps(pawns.get(i), b, p, possibleMoves);
			addPossibleSimpleMoves(pawns.get(i), b, p, possibleMoves);
		}

		return possibleMoves;
	}
	
	public static List<CkMove> possibleMoves(Board b, Player p, int pawn) {

		ArrayList<CkMove> possibleMoves = new ArrayList<CkMove>();

		addPossibleJumps(pawn, b, p, possibleMoves);
		addPossibleSimpleMoves(pawn, b, p, possibleMoves);

		return possibleMoves;
	}

	/**
	 * Adds all possible simple moves for a given pawn, player and board to a
	 * given List
	 * 
	 * @param pawn
	 *            The number of the pawn
	 * @param b
	 *            The game Board
	 * @param p
	 *            The Player
	 * @param possibleMoves
	 *            The list of passible moves
	 */
	private static void addPossibleSimpleMoves(Integer pawn, Board b, Player p,
			List<CkMove> possibleMoves) {
		for (Direction currentDirection : Direction.values()) {
			// browse all neighbours
			Integer currentNeighbour;
			try {
				currentNeighbour = b.getNeighbour(pawn, currentDirection);

				if (b.getSlotType(currentNeighbour) == SlotType.EMPTY) {
					// neighbour slot is empty
					CkMove m = new MoveImpl(pawn, currentNeighbour, p);

					//check for validity and if move has not already been calculated
					if (isTargetOfMoveValid(b, m)
							&& !moveIsContained(m, possibleMoves)) {
						possibleMoves.add(m);// add move
					}
				}
			} catch (NoSuchNeighbourException e) {
				continue;
			}
		}
	}

	/**
	 * adds all possible jumps for a given pawn, player and board to a given
	 * List
	 * 
	 * @param pawn
	 *            The number of the pawn
	 * @param b
	 *            The game Board
	 * @param p
	 *            The Player
	 * @param possibleMoves
	 *            The list of passible moves
	 */
	private static void addPossibleJumps(Integer pawn, Board b, Player p,
			List<CkMove> possibleMoves) {

		ArrayList<ArrayList<Integer>> destinationSlots = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> pawnWay = new ArrayList<Integer>();
		pawnWay.add(pawn);

		// calculate jumps by the possibleJumps method
		possibleJumps(b, pawnWay, destinationSlots, false);

		for (ArrayList<Integer> jumpWay : destinationSlots) {
			Integer moveTarget = jumpWay.get(jumpWay.size() - 1);

			if (!pawn.equals(moveTarget)) {
				CkMove m = new MoveImpl(pawn, moveTarget, p);

				for (Integer jumpSlot : jumpWay)
					if (!jumpSlot.equals(pawn) && !jumpSlot.equals(moveTarget))
						m.addJumPslot(jumpSlot);

				if (isTargetOfMoveValid(b, m)
						&& !moveIsContained(m, possibleMoves)) {
					possibleMoves.add(m);
				}
			}
		}
	}

	/**
	 * calculates recursively all the possible jumps for a pawn
	 * 
	 * @param b
	 *            The game Board
	 * @param way
	 *            the so far examined way
	 * @param destinationSlots
	 *            the result vector
	 */
	private static void possibleJumps(Board b, ArrayList<Integer> way,
			ArrayList<ArrayList<Integer>> destinationSlots, boolean debug) {

		destinationSlots.add(way);// add current way to result
		Integer pawn = way.get(way.size() - 1);// the last pawn of the way

		//browse all possible neighbour directions
		for (Direction currentDirection : Direction.values()) {
			try {
				Integer currentNeighbour = b.getNeighbour(pawn,
						currentDirection);

				//there's a pawn on the neighbour slot
				if (b.isOccupied(currentNeighbour)) {

					// the neighbour of the neighbour in the same direction:
					Integer secondNeighbour = b.getNeighbour(currentNeighbour,
							currentDirection);

					//check if this slot hasn't been already visited
					boolean notAlreadyVisited = true;
					for (ArrayList<Integer> w : destinationSlots) {
						Integer lastPawn = w.get(w.size() - 1);
						notAlreadyVisited = notAlreadyVisited
								&& !secondNeighbour.equals(lastPawn);
					}

					if (b.isEmpty(secondNeighbour) && notAlreadyVisited) {
						if (debug)
							System.out.println("jump OK:from, through, to :"
									+ way.get(0) + "," + currentNeighbour + ","
									+ secondNeighbour);

						ArrayList<Integer> newWay = new ArrayList<Integer>();
						for (Integer i : way)
							newWay.add(i);
						newWay.add(secondNeighbour);

						//recursively add multiple jumps
						possibleJumps(b, newWay, destinationSlots, debug);
					}

				}
			} catch (NoSuchNeighbourException e) {
				continue;
			}
		}

	}

	/**
	 * this private method checks if the target of a move is either the goal
	 * camp, the start camp of the player, or the middle field
	 * 
	 * @param b
	 *            The game Board
	 * @param m
	 *            The move
	 * @return true if the move can be play
	 */
	private static boolean isTargetOfMoveValid(Board b, CkMove m) {
		SlotType targetST = b.getCamp(m.getTarget());
		Player p = m.getPlayer();
		return (targetST == SlotType.EMPTY || targetST == p.getSlotType() || targetST == SlotTypes
				.getGoal(p.getSlotType()));
	}

	/**
	 * this method returns wether a move is valid or not, independently of the
	 * correctness of the moving player
	 * 
	 * @param m
	 *            The move to play
	 * @param b
	 *            The game Board
	 * @return true if the move is correct
	 */
	public static boolean checkMove(CkMove m, Board b) {
		return moveIsContained(m, possibleMoves(b, m.getPlayer()));
	}

	/**
	 * method which checks if a given move is contained in a given moves' list.
	 * Only sources and targets are compared : equivalent moves are considered
	 * equal
	 * 
	 * @param m
	 *            The move
	 * @param l
	 *            The list of CkMove
	 * @return true if th eMove m is in the list
	 */
	private static boolean moveIsContained(CkMove m, List<CkMove> l) {
		for (int i = 0; i < l.size(); i++)
			if (l.get(i).equals(m))
				return true;

		return false;
	}

}
