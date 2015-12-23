package com.axolotl.mancala.game;

import java.io.Serializable;

/**
 * Represents the final score of the game
 */
public class FinalScore implements Serializable {

	// Serialization version number
	private static final long serialVersionUID = 1L;
	
	// The game result
	private final GameResult result;
	
	// The score for player one
	private final PlayerScore player1Score;
	
	// The score for player two
	private final PlayerScore player2Score;
	
	/**
	 * Creates a new instance of the final score
	 * 
	 * @param player1Score The score for player 1 
	 * @param player2Score The score for player 2
	 */
	public FinalScore(PlayerScore player1Score, PlayerScore player2Score) {
		
		this.player1Score = player1Score;
		this.player2Score = player2Score;
		
		if (player1Score.getTotalScore() == player2Score.getTotalScore())
			result = GameResult.Draw;
		else if (player1Score.getTotalScore() > player2Score.getTotalScore())
			result = GameResult.Player1Wins;
		else
			result = GameResult.Player2Wins;
	}
	
	/**
	 * Gets the result of the game
	 * 
	 * @return The game result
	 */
	public GameResult getResult() {
		return result;
	}
	
	/**
	 * Returns the score for the requested player number
	 * @param player The player number
	 * @return The score for the requested player
	 */
	public PlayerScore getScore(PlayerNumber player) {
	    return player == PlayerNumber.One ? player1Score : player2Score;
	}
}
