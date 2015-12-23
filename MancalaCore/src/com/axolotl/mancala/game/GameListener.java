package com.axolotl.mancala.game;

import java.util.List;

import com.axolotl.mancala.model.Pit;

/**
 * The listener interface for all changes in the game

 */
public interface GameListener {

	/**
	 * Called when 
	 */
	void onNewGame();
	
	/**
	 * Called when a game has been restored
	 */
	void onGameRestored();
	
	/**
	 * Called when the board has changed
	 * 
	 * @param changedPits The changed hollows
	 */
	void onBoardChanged(List<Pit> changedPits);
	
	/**
	 * Called when the current player changes
	 * 
	 * @param newPlayer The new player
	 */
	void onPlayerChanged(PlayerNumber newPlayer);
	
	/**
	 * Called when the game completes
	 * 
	 * @param score The final score
	 */
	void onGameComplete(FinalScore score);
	
	/**
	 * Called when the current player has another turn
	 */
	void onPlayerHasAnotherTurn();
	
	/**
	 * Called when the given hollow has been captured by the current player
	 * 
	 * @param capturedPit The captured hollow
	 */
	void onPitCaptured(Pit capturedPit);
}