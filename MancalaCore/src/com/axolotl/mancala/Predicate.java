package com.axolotl.mancala;

/**
 * A generic predicate for matching items
 *
 * @param <T> The generic type
 */
public abstract class Predicate<T> {
	
	/**
	 * The main method to match an item against a predicate
	 * 
	 * @param item 
	 * 		   The item to match on
	 * 
	 * @return
	 * 		   True if the item matches, false otherwise
	 */
	public abstract boolean matches(T item);
}
