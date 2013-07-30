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


package name.w.yellowduck.activities.strategy.chinesecheckers.ai.tree;

/**
 * This class represents an AI search tree
 */
public class SearchTree {

	/**
	 * name of the tree
	 */
	private String name="";
	
	/**
	 * root of the tree
	 */
	private SearchNode root;
	
	/**
	 * the number of visited nodes by the AI
	 */
	private long nbVisitednodes;
	
	/**
	 * the computation time of the AI 
	 */
	private long time = 0;
	
	/**
	 * Basic constructor
	 * @param rt the root of the tree
	 * @param n the title of the tree
	 * @param nodes the number of visited nodes
	 * @param t the computation time
	 */
	public SearchTree(SearchNode rt,String n, long nodes, long t){
		root = rt;
		name = n;
		nbVisitednodes = nodes;
		this.time = t;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "";
	}

}
