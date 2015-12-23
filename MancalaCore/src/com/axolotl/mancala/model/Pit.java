package com.axolotl.mancala.model;

import java.util.ArrayList;
import java.util.List;

import com.axolotl.mancala.game.PlayerNumber;

/**
 * Represents a hollow in the board containing mMarbles
 */
public class Pit {
	
	// The list of marbles for this hollow
	private final List<Marble> mMarbles;
	
	// The associated player
	private final PlayerNumber mPlayer;
	
	// Keeps track of the previous marble count
	private int mPreviousMarbleCount;
	
	/**
	 * Creates a new instance of the Pit class
	 */
	public Pit(PlayerNumber player) {
		mMarbles = new ArrayList<Marble>();
		this.mPlayer = player;
	}
	
	/**
	 * Returns an array of mMarbles in this pit
	 * @return
	 */
	public Marble[] getMarbles() {
		return mMarbles.toArray(new Marble[0]);
	}
	
	/**
	 * Returns the number of mMarbles currently in this pit
	 * 
	 * @return The number of mMarbles in this pit
	 */
	public int getNumberOfMarbles() {
		return mMarbles.size();
	}
	
	/**
	 * Returns the previous number of marbles in the pit prior to the
	 * last add	
	 * @return
	 */
	public int getPreviousMarbleCount() {
		return mPreviousMarbleCount;
	}
	
	/**
	 * Hack to force the pit to use a previous count when multiple operations
	 * occur on a pit, causing the last count to be displayed
	 * @param previousCountToUse
	 */
	public void forcePreviousMarbleCount(int previousCountToUse) {
		mPreviousMarbleCount = previousCountToUse;
	}
	
	/**
	 * Gets the mPlayer that this pit is associated with
	 * @return The mPlayer
	 */
	public PlayerNumber getPlayerNumber() {
		return mPlayer;		
	}
	
	/**
	 * Removes the mMarbles from this pit
	 * 
	 * @return The array of mMarbles that were in this pit
	 */
	public Marble[] removeAll() {
		Marble[] marbleArray = getMarbles();
		mPreviousMarbleCount = mMarbles.size();
		mMarbles.clear();
		return marbleArray;
	}
	
	/**
	 * Adds the mMarbles to this pit
	 * 
	 * @param items
	 *         The mMarbles to add
	 */
	public void addAll(Iterable<Marble> items) {
	    
	    if (items == null)
	        return;
	    
	    mPreviousMarbleCount = mMarbles.size();
	    
		for (Marble m : items) 
			mMarbles.add(m);
	}
	
	/**
	 * Adds the mMarbles to this pit
	 * 
	 * @param items
	 *         The mMarbles to add
	 */
	public void addAll(Marble[] items) {
	    
	    if (items == null)
	        return;
	    
	    mPreviousMarbleCount = mMarbles.size();
	    
		for (Marble m : items) 
			mMarbles.add(m);
	}
	
	/**
	 * Adds a marble to this pit
	 * 
	 * @param marble
	 *         The marble to add
	 */
	public void add(Marble marble) {
	    
	    if (marble == null)
	        return;
	    
	    mPreviousMarbleCount = mMarbles.size();
	    
		mMarbles.add(marble);
	}	
}