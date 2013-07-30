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

import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Board;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;

/**
 * This class handles Heuristic Function
 */
public abstract class HeuristicFunction {

	/**
	 * Enum of HeuristicType
	 */
	public enum HeuristicType {
		DISTANCE, ADVANCING, BLOCKING, ISOLATED, MIDDLE, LEFTBEHIND, MIDDLEISOLATED, MIDDLELEFTBEHIND
	}

	/**
	 * Type of the Heuristic
	 */
	protected HeuristicType type;

	/**
	 * This method return the Heuristic function of one Heuristic Type
	 * 
	 * @param h 
	 *            HeuristicType
	 */
	/*
	public static HeuristicFunction getHeuristicFunction(HeuristicType h) {
		switch (h) {
		case BLOCKING:
			return new BlockingHeuristic();
		case ADVANCING:
			return new AdvancingHeuristic();
		case DISTANCE:
			return new SimpleDistanceHeuristic();
		case ISOLATED:
			return new IsolatedPawnHeuristic();
		case MIDDLE:
			return new MiddleBoardHeuristic();
		case MIDDLEISOLATED:
			return new MiddleIsolatedHeuristic();
		case LEFTBEHIND:
			return new LeftBehindHeuristic();
		case MIDDLELEFTBEHIND:
			return new MiddleLeftBehindHeuristic();
		default:
			return new SimpleDistanceHeuristic();
		}
	}
	*/
	/**
	 * This method is used by the GUI. It returns an HeuristicFunction from a
	 * String
	 * 
	 * @param s
	 *            The string selected by the GUI
	 */
	/*
	public static HeuristicFunction getHeuristicFunction(String s) {
		if (s.equals("advancing heuristic") || s.equals("ADVANCING"))
			return new AdvancingHeuristic();
		else if (s.equals("blocking heuristic") || s.equals("BLOCKING"))
			return new BlockingHeuristic();
		else if (s.equals("simple distance heuristic") || s.equals("DISTANCE"))
			return new SimpleDistanceHeuristic();
		else if (s.equals("isolated pawn heuristic") || s.equals("ISOLATED"))
			return new IsolatedPawnHeuristic();
		else if (s.equals("middle distance heuristic") || s.equals("MIDDLE"))
			return new MiddleBoardHeuristic();
		else if (s.equals("middle and isolated heuristic")
				|| s.equals("MIDDLEISOLATED"))
			return new MiddleIsolatedHeuristic();
		else if (s.equals("left behind heuristic") || s.equals("LEFTBEHIND"))
			return new LeftBehindHeuristic();
		else if (s.equals("middle and left behind heuristic")
				|| s.equals("MIDDLELEFTBEHIND"))
			return new MiddleLeftBehindHeuristic();
		else
			return new SimpleDistanceHeuristic();
	}
	*/
	/**
	 * This method return a string from an HeuristicType
	 * 
	 * @param h
	 *            HeuristicType
	 */
	public static String getString(HeuristicType h) {
		switch (h) {
		case BLOCKING:
			return "blocking heuristic";
		case ADVANCING:
			return "advancing heuristic";
		case DISTANCE:
			return "simple distance heuristic";
		case ISOLATED:
			return "isolated pawn heuristic";
		case MIDDLE:
			return "middle distance heuristic";
		case MIDDLEISOLATED:
			return "middle and isolated heuristic";
		case LEFTBEHIND:
			return "left behind heuristic";
		case MIDDLELEFTBEHIND:
			return "middle and left behind heuristic";
		default:
			return h.toString();
		}
	}

	/**
	 * Default constructor
	 * 
	 * @param h
	 * 				Heuristic function
	 */
	public HeuristicFunction(HeuristicType h) {
		this.type = h;
	}

	/**
	 * This method return the value from the Heuristic Function
	 * 
	 * @param b
	 *            Board to analyse
	 * @param p
	 *            Player wich play
	 * @return value of heuristic function
	 */
	public abstract double value(Board b, Player p);

	/**
	 * This method return the type of the Heuristic Function
	 */
	public HeuristicType getHeuristicType() {
		return type;
	}

}
