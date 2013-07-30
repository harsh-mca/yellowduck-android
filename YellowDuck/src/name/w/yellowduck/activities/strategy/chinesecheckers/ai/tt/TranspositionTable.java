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


package name.w.yellowduck.activities.strategy.chinesecheckers.ai.tt;

import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.Board;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes.SlotType;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.CkMove;

import java.util.HashMap;

import name.w.yellowduck.activities.strategy.chinesecheckers.ai.ValuedMove;

/**
 * this class represents a transposition table
 */
public class TranspositionTable {

	/**
	 * the hashmap representing the table
	 */
	public HashMap<Integer, TranspositionValue> table;

	/**
	 * basic constructor, only initializes the hashmap
	 */
	public TranspositionTable() {
		table = new HashMap<Integer, TranspositionValue>();
	}

	/**
	 * to get the size of theb table
	 * 
	 * @return the size of theb table
	 */
	public int getNbElements() {
		return table.size();
	}

	/**
	 * returns wether a value is contained in the table, for a given board and
	 * player, and the searchdepth is higher than the given searchdepth
	 * 
	 * @param b
	 *            the given board
	 * @param p
	 *            the given player
	 * @param s
	 *            the given searchdepth
	 * @return wether there is a better value contained, or not
	 */
	public boolean betterValueIsContained(Board b, SlotType p, int s) {
		if (!valueIsContained(b, p))
			return false;
		return table.get(new TranspositionKey(b, p).hashCode())
				.getSearchDepth() >= s;
	}

	/**
	 * returns wether a value is contained in the table for a given board and
	 * player
	 * 
	 * @param b
	 *            the given board
	 * @param p
	 *            the given player
	 * @return wether a value is contained or not
	 */
	private boolean valueIsContained(Board b, SlotType p) {
		Integer k = new TranspositionKey(b, p).hashCode();
		return table.containsKey(k);
	}

	/**
	 * replaces a value in the table by a new value
	 * 
	 * @param b
	 *            the given board
	 * @param p
	 *            the given player
	 * @param s
	 *            the given searchdepth
	 * @param value
	 *            the given value
	 * @param m
	 *            the given move
	 */
	public void replaceValue(Board b, SlotType p, int s, double value, CkMove m) {
		ValuedMove vm = new ValuedMove(m, value);
		replaceValue(b, p, s, vm);
	}

	/**
	 * replaces a value in the table by a new value
	 * 
	 * @param b
	 *            a board
	 * @param p
	 *            a player
	 * @param s
	 *            a searchdepth
	 * @param m
	 *            a valued move
	 */
	public void replaceValue(Board b, SlotType p, int s, ValuedMove m) {
		TranspositionKey key = new TranspositionKey(b, p);
		table.remove(key.hashCode());
		table.put(key.hashCode(), new TranspositionValue(s, m));
	}

	/**
	 * to get the move contained in the table for given board and player
	 * 
	 * @param b
	 *            a board
	 * @param p
	 *            a player
	 * @return the corresponding move
	 */
	public ValuedMove getMove(Board b, SlotType p) {
		if (!valueIsContained(b, p))
			return null;
		Integer k = new TranspositionKey(b, p).hashCode();
		if (table.containsKey(k))
			;
		return table.get(k).getMove();
	}

	/**
	 * to get the value contained in the table for given board and player
	 * 
	 * @param b
	 *            a board
	 * @param p
	 *            a player
	 * @return the corresponding value
	 */
	public double getValue(Board b, SlotType p) {
		if (!valueIsContained(b, p))
			return -1;
		return getMove(b, p).getValue();
	}

}
