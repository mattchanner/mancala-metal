package com.axolotl.mancala.game;

import com.axolotl.mancala.MancalaException;
import com.axolotl.mancala.model.Board;
import com.axolotl.mancala.model.Pit;
import com.axolotl.mancala.strategies.ComputerPlayer;
import com.axolotl.mancala.strategies.MancalaStrategy;

/**
 * Interface representing the behaviour of the mancala game
 */
public interface Game {
	
	/**
	 * Gets the current state of the game, represented as a string
	 * 
	 * @return The string representation of the game
	 */
	String getGameState();
	
	/**
	 * Gets the current game mode
	 * @return
	 */
	GameMode getGameMode();

	/**
	 * Gets the current difficulty level for this game
	 * @return
	 */
	Difficulty getDifficulty();
	
	/**
	 * Restores the game back to the state given by the string
	 * 
	 * @param state The game state to restore
	 */
	void restoreGameState(String state);
	
	/**
	 * Adds a GameListener
	 * 
	 * @param listener The listener to add
	 */
	void addGameListener(GameListener listener);
	
	/**
	 * Removes a GameListener
	 * 
	 * @param listener The listener to remove
	 */
	void removeGameListener(GameListener listener);
	
	/**
	 * Returns the board in play
	 * 
	 * @return The current board
	 */
	Board getBoard();
	
	/**
	 * Sets the current strategy
	 * 
	 * @param strategy The current strategy to use 
	 */
	void setStrategy(MancalaStrategy strategy);
	
	/**
	 * Returns the current strategy in use
	 */
	MancalaStrategy getStrategy();
	
	/**
	 * Gets the computer player, if the current game mode is set to single player
	 * @return The computer player
	 */
	ComputerPlayer getPlayer2();
	
	/**
	 * Creates a new game
	 * 
	 * @param playerMode The game mode
	 * 
	 * @param difficulty The difficulty level
	 */
	void newGame(GameMode playerMode, Difficulty difficulty) throws MancalaException;
	
	/**
	 * Called to perform a move on the current hollow
	 * 
	 * @param currentPit The current hollow to play
	 * @throws MancalaException 
	 */
	void makeMove(Pit currentPit) throws MancalaException;
}
