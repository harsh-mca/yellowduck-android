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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import name.w.yellowduck.activities.strategy.chinesecheckers.ai.ValuedMove;

/**
 * this class provides nodes for the representation of a search tree.
 */
public class SearchNode extends Object {

	/**
	 * the text to be displayed on this node in the JFrame view
	 */
	private String nodeDescription = "";

	/**
	 * the move contained by this node
	 */
	private ValuedMove move;

	/**
	 * the parent node of the node
	 */
	private SearchNode parent;

	/**
	 * the List of children
	 */
	private List<SearchNode> children = new ArrayList<SearchNode>();

	/**
	 * wether the AI chose this move
	 */
	private boolean choosen = false;

	/**
	 * wether this move was part of the best moves
	 */
	private boolean best = false;

	/**
	 * node without parent (root)	
	 */
	public SearchNode() {
		this.nodeDescription = "root";
	}

	/**
	 * A constructor to build an intermediate node with MAX/MIN info
	 * @param parent the parent
	 * @param isMax wether this node is max or min
	 * @param nbPossibleMoves the number of possible moves
	 */
	public SearchNode(SearchNode parent, boolean isMax, int nbPossibleMoves){
		this(parent, (isMax?"MAX":"MIN") + " - " + nbPossibleMoves + " possible moves");
	}
	
	/**
	 * Basic constructor, with only a parent
	 * @param parent The parent 
	 */
	public SearchNode(SearchNode parent){
		if(parent != null)
			parent.addChild(this);
	}
	
	/**
	 * node with a description only (max/min or leaf node) 
	 * @param parent the parent
	 * @param s the string to be displayed
	 */
	public SearchNode(SearchNode parent, String s) {
		this(parent);
		nodeDescription = s;
	}

	/**
	 * setter for the move
	 * @param m the move to set
	 */
	public void setMove(ValuedMove m) {
		move = m;
		if (m != null)
			nodeDescription = m.toString();
	}

	/**
	 * adds a child to the node
	 * @param n the child to add
	 */
	public void addChild(SearchNode n) {
			children.add(n);
			n.parent = this;
	}

	/**
	 * @see javax.swing.tree.SearchNode#children()
	 */
	public Enumeration children() {
		return new Enumeration() {
			private int index = 0;

			public boolean hasMoreElements() {
				return children.size() > index;
			}

			public Object nextElement() {
				if (hasMoreElements())
					return children.get(index++);
				return null;
			}
		};
	}

	/**
	 * @see javax.swing.tree.SearchNode#getAllowsChildren()
	 */
	public boolean getAllowsChildren() {
		return true;
	}

	/**
	 * @see javax.swing.tree.SearchNode#getChildAt(int)
	 */
	public SearchNode getChildAt(int arg0) {
		try {
			return children.get(arg0);
		} catch (ArrayIndexOutOfBoundsException ex) {
			return null;
		}
	}

	/**
	 * @see javax.swing.tree.SearchNode#getChildCount()
	 */
	public int getChildCount() {
		return children.size();
	}

	/**
	 * @see javax.swing.tree.SearchNode#getIndex(javax.swing.tree.SearchNode)
	 */
	public int getIndex(SearchNode node) {
		return children.indexOf(node);
	}

	/**
	 * @see javax.swing.tree.SearchNode#getParent()
	 */
	public SearchNode getParent() {
		return parent;
	}

	/**
	 * @see javax.swing.tree.SearchNode#isLeaf()
	 */
	public boolean isLeaf() {
		return getChildCount() == 0;
	}

	/**
	 * returns an array containing all node's children
	 * @return the array of children
	 */
 	public SearchNode[] getChildren() {
 		
		SearchNode[] c = new SearchNode[getChildCount()];
		for (int i = 0; i < getChildCount(); i++)
			c[i] = children.get(i);
		return c;
	}
	
 	/**
 	 * sets this node as choosen by the AI
 	 */
	public void setChoosen() {
		if (!choosen) {
			nodeDescription = "[CHOOSEN] " + nodeDescription;
			choosen = true;
		}
	}

	/**
	 * sets this node as one of the bests
	 */
	public void setBest() {
		if (!best) {
			nodeDescription = "BEST MOVE " + nodeDescription;
			best = true;
		}
	}

	/**
	 * getter for the move
	 * @return the node's move
	 */
	public ValuedMove getMove() {
		return move;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return nodeDescription;
	}

}
