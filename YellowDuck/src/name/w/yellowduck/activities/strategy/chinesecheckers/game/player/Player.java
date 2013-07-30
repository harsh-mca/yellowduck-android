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


package name.w.yellowduck.activities.strategy.chinesecheckers.game.player;

import name.w.yellowduck.activities.strategy.chinesecheckers.game.board.SlotTypes.SlotType;
import android.graphics.Color;

/**
 * This class represents a player.
 */
public abstract class Player extends Object {

	/**
	 * the player's color
	 */
	protected int color;

	/**
	 * player's position on the board
	 */
	protected SlotType position;

	/**
	 * the type of the player
	 */
	protected PlayerType type;

	/**
	 * the name of the player
	 */
	protected String name;

	/**
	 * The default constructor to create a player
	 * 
	 * @param gm
	 *            The GameCore uses in the game
	 * @param n
	 *            The player's name
	 * @param st
	 *            The player's SlotType
	 * @param pt
	 *            The player-s PlayerType
	 * @param c
	 *            The player's color
	 */
	public Player(String n, SlotType st, PlayerType pt, int c) {
		this.color = c;
		this.position = st;
		this.name = n;
		this.type = pt;
	}

	/**
	 * Get the description of a player
	 * 
	 * @return The String with the player's name and the player's color
	 */
	public String getDescription() {
		return name + " (" + color + ")";

	}

	/**
	 * Get the player's name
	 * 
	 * @return The String with the player's name
	 */
	public String getName() {
		return name;

	}

	/**
	 * Get the player's color
	 * 
	 * @return The String with the player's color
	 */
	public int getColor() {
		return color;

	}

	/**
	 * Get the player's type
	 * 
	 * @return The String with the player's type
	 */
	public PlayerType getType() {
		return type;
	}

	/**
	 * Get the player's SlotType
	 * 
	 * @return The String with the player's SlotType
	 */
	public SlotType getSlotType() {
		return position;
	}


	/**
	 * @see #toString()
	 */
	public String toString() {
		return getName();
	}
	public boolean isAI() {
	    return type != PlayerType.HUMAN;
	}

	public boolean isHuman() {
	    return type == PlayerType.HUMAN;
	}

	public boolean equals(Object _another) {
	    if (!(_another instanceof Player))
	        return false;
	    Player another=(Player)_another;
	    return position==another.getSlotType();
	}
	
}
