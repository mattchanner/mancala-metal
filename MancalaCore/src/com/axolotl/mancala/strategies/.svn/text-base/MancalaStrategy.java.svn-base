package com.axolotl.mancala.strategies;

import com.axolotl.mancala.MancalaException;
import com.axolotl.mancala.game.Difficulty;
import com.axolotl.mancala.game.GamePublisher;
import com.axolotl.mancala.game.PlayerNumber;
import com.axolotl.mancala.model.Board;
import com.axolotl.mancala.model.Pit;

/**
 * A game strategy for playing mancala
 */
public interface MancalaStrategy {

	/**
	 * Gets the name of the strategy
	 * @return The strategy name
	 */
	public String getStrategyName();
	
	/**
	 * Gets a description of the strategy
	 * @return The strategies description
	 */
	public String getStrategyDescription();
	
	/**
	 * Sets the initial player
	 * 
	 * @param playerToGoFirst The player to start
	 */
	public void setInitialPlayer(PlayerNumber playerToGoFirst);
	
	/**
	 * Returns the current player
	 * 
	 * @return The current Player
	 */
	public PlayerNumber getCurrentPlayer();
	
	/**
	 * Called when one of the opponents is the computer.  The strategy is
	 * responsible for the construction of a player that has knowledge about the
	 * type of mancala game associated to the strategy
	 * 
	 * @param player The player number 
	 * 
	 * @param level The difficulty level
	 * 
	 * @return A player
	 */
	public ComputerPlayer createAIPlayer(PlayerNumber player, Difficulty level);
	
	/**
	 * Sets the board reference on the current strategy
	 * @param board
	 */
	void setBoard(Board board);
	
	/**
	 * Sets the event publisher for the game
	 * 
	 * @param publisher 
	 * 		   The game publisher
	 */
	void setGamePublisher(GamePublisher publisher);
	
	/**
	 * Resets the board back to an initial state
	 */
	void reset();
	
	/**
	 * Plays the current hollow, returning the result of the move
	 * 
	 * @param pit
	 * 		   The hollow to play
	 * 
	 * @throws MancalaException
	 */
	void makeMove(Pit pit) throws MancalaException;
}
