package com.axolotl.mancala;

import java.util.ArrayList;
import java.util.List;

/**
 * Pseudo linq extensions for iterable items
 */
public class JLinq {
	
	/**
	 * Selects the first item in the given iterable that matches the given predicate
	 * 
	 * @param <T> The type of item being tested	 
	 * @param items The items to iterate over
	 * @param predicate The predicate to apply to each item 
	 * @return The first matching item, or null if none fulfill the predicate
	 */
	public static <T> T first(Iterable<T> items, Predicate<T> predicate) {
		for (T item : items) {
			if (predicate.matches(item))
				return item;
		}
		return null;
	}
	
	/**
	 * Returns a list of all matching elements in the given iterable based on matching
	 * the given predicate
	 * 
	 * @param <T> The type of item being tested 
	 * @param items The items to iterate over 
	 * @param predicate The predicate to apply to each item 
	 * @return The matching items, or null if none fulfill the predicate
	 */
	public static <T> List<T> where(Iterable<T> items, Predicate<T> predicate) {
		List<T> matches = new ArrayList<T>();
		for (T item : items) {
			if (predicate.matches(item))
				matches.add(item);
		}
		return matches;
	}
}
