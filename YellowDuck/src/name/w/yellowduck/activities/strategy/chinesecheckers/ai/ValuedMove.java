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

import java.util.List;

import name.w.yellowduck.activities.strategy.chinesecheckers.game.move.CkMove;
import name.w.yellowduck.activities.strategy.chinesecheckers.game.player.Player;

/**
 * This class represents valued move used for the algorithmic functions.
 */
public class ValuedMove implements Comparable, CkMove{

	/**
	 * The associated value move
	 */
	private double value;
	
	/**
	 * The move
	 */
	private CkMove standardMove;
	
	/**
	 * The default constructor to add a value to a move.
	 * @param m The move
	 * @param v The given value
	 */
	public ValuedMove(CkMove m, double v){
		this.standardMove = m;
		this.value = v;	
	}
	
	/**
	 * Get the value of a move.
	 * @return The value
	 */
	public double getValue(){
		return value;
	}
	
	/**
	 * Compare two valued moves.
	 * @param m The valued move to compare to
	 * @return The 
	 */
	public int compareTo(ValuedMove m){
		return -Double.compare(value, m.value);
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object m) {
		if(m instanceof ValuedMove)
			return -Double.compare(value, ((ValuedMove)m).value);
		return -1;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(!(o instanceof ValuedMove))
				return false;
		return standardMove.equals(o) && value == ((ValuedMove)o).value;
	}
	
	/**
	 * hashcode method written with technique of the book "Effective Java"
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		
		long valueHash = Double.doubleToLongBits(value);
		
		return standardMove.hashCode() * 17 + (int)(valueHash^(valueHash>>>32));
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return standardMove.toString()+" - value: "+value;
	}
	
	/**
	 * @see game.move.CkMove#addJumPslot(java.lang.Integer)
	 */
	public void addJumPslot(Integer j) {
		standardMove.addJumPslot(j);
	}

	/**
	 * @see game.move.CkMove#addJumPslots(java.util.List)
	 */
	public void addJumPslots(List<Integer> l) {
		standardMove.addJumPslots(l);		
	}

	/**
	 * @see game.move.CkMove#getJumpedSlots()
	 */
	public List<Integer> getJumpedSlots() {
		return standardMove.getJumpedSlots();
	}

	/**
	 * @see game.move.CkMove#getPlayer()
	 */
	public Player getPlayer() {
		return standardMove.getPlayer();
	}

	/**
	 * @see game.move.CkMove#getSource()
	 */
	public Integer getSource() {
		return standardMove.getSource();
	}

	/**
	 * @see game.move.CkMove#getTarget()
	 */
	public Integer getTarget() {
		return standardMove.getTarget();
	}

	/**
	 * @see game.move.CkMove#getDistanceMove()
	 */
	public Integer getDistanceMove() {
		return standardMove.getDistanceMove();
	}
}
