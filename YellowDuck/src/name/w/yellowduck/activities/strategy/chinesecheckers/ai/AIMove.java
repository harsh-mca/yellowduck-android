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
 * This class represents the move for AI players.
 */
public class AIMove implements CkMove {

	/**
	 * The number of visited nodes.
	 */
	private int nbVisitedNodes;
	
	/**
	 * The time to calculate a move.
	 */
	private long time;
	
	/**
	 * The delegation of a move.
	 */
	private CkMove standardMove;
	
	/**
	 * The default constructor to create a AI move.
	 * @param m The move to play
	 * @param nbNodes The number of visited nodes
	 * @param t The time to calculate a move
	 */
	public AIMove(CkMove m, int nbNodes, long t){
		this.standardMove = m;
		this.nbVisitedNodes = nbNodes;
		this.time = t;;
	}
	
	/**
	 * Gets the number of visited nodes.
	 * @return The number of visited nodes
	 */
	public int getNbVisitedNodes(){
		return nbVisitedNodes;
	}
	
	/**
	 * Gets the time to calculate a move.
	 * @return The time 
	 */
	public long getTime(){
		return time;
	}

	/**
	 * @see CkMove#addJumPslot(Integer)
	 */
	public void addJumPslot(Integer j) {
		standardMove.addJumPslot(j);
	}

	/**
	 * @see CkMove#addJumPslots(List)
	 */
	public void addJumPslots(List<Integer> l) {
		standardMove.addJumPslots(l);		
	}

	/**
	 * @see CkMove#getJumpedSlots()
	 */
	public List<Integer> getJumpedSlots() {
		return standardMove.getJumpedSlots();
	}

	/**
	 * @see CkMove#getPlayer()
	 */
	public Player getPlayer() {
		return standardMove.getPlayer();
	}

	/**
	 * @see CkMove#getSource()
	 */
	public Integer getSource() {
		return standardMove.getSource();
	}

	/**
	 * @see CkMove#getTarget()
	 */
	public Integer getTarget() {
		return standardMove.getTarget();
	}

	/**
	 * @see CkMove#getDistanceMove()
	 */
	public Integer getDistanceMove() {
		return standardMove.getDistanceMove();
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		return standardMove.hashCode();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return standardMove.toString() + "("+nbVisitedNodes +"visited nodes in "+time+"ms.)";
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		return standardMove.equals(o);
	}

	/**
	 * @see game.move.CkMove#compareTo(java.lang.Object)
	 */
	public int compareTo(Object other) {
		return standardMove.compareTo(other);
	}
	
}
