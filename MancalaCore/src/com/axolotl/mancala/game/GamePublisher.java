package com.axolotl.mancala.game;

import java.util.List;

import com.axolotl.mancala.model.Pit;

/**
 * The game event publisher
 */
public interface GamePublisher {
	
	/**
	 * Sends a notification that a game has been restored
	 */
	void notifyGameRestored();
	
	/**
	 * Notification that a new game has commenced
	 */
	void notifyNewGame();
	
	/**
	 * Notifies all listeners when the board has changed
	 * 
	 * @param changedPits The modified hollows
	 */
	void notifyBoardChanged(List<Pit> changedPits);
	
	/**
	 * Notifies all listeners that the current player has another turn
	 */
	void notifyPlayerHasAnotherTurn();
	
	/**
	 * Notifies all listeners that the given hollow has been captured by the player
	 * @param capturedPit The captured hollow
	 */
	void notifyCapture(Pit capturedPit);
	
	/**
	 * Notifies all listeners that the player has changed
	 * @param newPlayer The new player
	 */
	void notifyPlayerChanged(PlayerNumber newPlayer);
	
	/**
	 * Notifies all listeners that the game has completed
	 * 
	 * @param score The final score
	 */
	void notifyGameComplete(FinalScore score);
}
