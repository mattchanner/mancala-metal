package com.axolotl.mancala.game;

import java.io.Serializable;

/**
 * Represents details of the players final score
 */
public class PlayerScore implements Serializable {

    // Serial version number
	private static final long serialVersionUID = 1L;
	
	// The number of remaining marbles in open play on this player's side
	private final int numberRemaining;
	
	// The number of marbles in this player's store
	private final int numberInStore;
	
	/**
	 * Creates a new instance of the player score
	 * 
	 * @param numberRemaining
	 * 		   The number of marbles remaining in open play
	 * 
	 * @param numberInStore
	 * 		   The number of marbles in the players store
	 */
	public PlayerScore(int numberRemaining, int numberInStore) {
		this.numberRemaining = numberRemaining;
		this.numberInStore = numberInStore;
	}
	
	/**
	 * Gets the number of marbles remaining in open play
	 * 
	 * @return The number of remaining marbles in open play
	 */
	public int getNumberRemaining() {
		return numberRemaining;
	}
	
	/**
	 * Gets the number of marbles in the player's store
	 * 
	 * @return The number of marbles in the store
	 */
	public int getNumberInStore() {
		return numberInStore;
	}
	
	/**
	 * Returns the total score as the sum of the marbles in the store, and the marbles in open play
	 * @return
	 */
	public int getTotalScore() {
		return numberInStore + numberRemaining;
	}
}
