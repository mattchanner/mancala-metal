package com.axolotl.mancala.model;

import com.axolotl.mancala.game.PlayerNumber;

/**
 * A specialised type of pit used to store the end marbles
 */
public class Store extends Pit {

	/**
	 * Constructs a new instance of the Store class
	 * @param player The player associated with this store
	 */
	public Store(PlayerNumber player) {
		super(player);
	}
}
