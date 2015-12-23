package com.axolotl.mancala.model;

import java.util.List;

import com.axolotl.mancala.Predicate;
import com.axolotl.mancala.game.PlayerNumber;
import com.axolotl.mancala.game.PlayerScore;

/**
 * Represents the mancala board that the game is played on
 */
public interface Board {
	
	/**
	 * Returns a list of all the hollows on the current board
	 * 
	 * @return Each hollow, including both the playing area, and the player stores
	 */
	List<Pit> getPits();
	
	/**
	 * Gets the store associated with the given player
	 * @param player The player to query by
	 * @return The associated store
	 */
	Store getPlayersStore(PlayerNumber player);
	
	/**
	 * Returns the playable pits for the given player
	 * 
	 * @param player The player
	 * @return The pits associated to this player
	 */
	List<Pit> getPlayersPits(PlayerNumber player);
	
	/**
	 * Returns an enumeration of all hollows matching a given predicate
	 * 
	 * @param predicate
	 * 		   The predicate used to match each hollow with
	 * 
	 * @return
	 * 		   All matching hollows
	 */
	List<Pit> getPits(Predicate<Pit> predicate);
	
	/**
	 * Returns the hollow which is adjacent to the one given, assuming an anti-clockwise rotation of the board
	 * 
	 * @param pit The hollow to query
	 * 
	 * @return The adjacent hollow
	 */
	Pit getAdjacentPit(Pit pit);
	
	/**
	 * Returns the first hollow which matches the given predicate, or null if none found
	 * 
	 * @param predicate
	 * 		   The predicate to apply
	 * 
	 * @return
	 * 		   The first hollow matching the given predicate, or null if none found
	 */
	Pit first(Predicate<Pit> predicate);

	/**
	 * Returns the number of hollows currently defined for this board.
	 * 
	 * This method assumes that the board has been initialized by calling initialiseBoard
	 * first.
	 * 
	 * @return The number of hollows currently defined for this board
	 */
	int getNumberOfHollowsPerPlayer();

	/**
	 * Returns the hollows that follow on from the current pit.  This will include
	 * the store associated to the current player (if found), but not the opposing players store.
	 * 
	 * If needed, the method will wrap around
	 * 
	 * @param currentPit
	 * 		   The current pit to navigate from
	 * 
	 * @param currentPlayer
	 * 		   The current player, used to ensure only the player's store is included
	 * 
	 * @param numberOfPits
	 * 		  The number of pit to include
	 * 
	 * @return The list of pits
	 */
	abstract List<Pit> getNextPits(Pit currentPit,
		PlayerNumber currentPlayer, 
		int numberOfPits);

	/**
	 * Returns the score for the given player
	 * 
	 * @param player
	 * 		   The player to determine the current score for
	 * 
	 * @return
	 * 		   The score for the given player
	 */
	abstract PlayerScore getScoreForPlayer(PlayerNumber player);

	/**
	 * Initializes the board by adding numberOfMarblesPerHole marbles to each pit on the board
	 * 
	 * @param numberOfHollowsPerPlayer
	 * 		   The number of hollows on each side of the board
	 * 
	 * @param numberOfMarblesPerPit
	 * 		   The number of marbles to add to each of the pits
	 */
	abstract void initialiseBoard(int numberOfHollowsPerPlayer, int numberOfMarblesPerPit);

	/**
	 * Clears the contents of the board
	 */
	abstract void clear();
}