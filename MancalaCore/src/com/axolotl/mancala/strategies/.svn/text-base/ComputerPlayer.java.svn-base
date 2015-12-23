package com.axolotl.mancala.strategies;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.axolotl.mancala.Predicate;
import com.axolotl.mancala.game.Difficulty;
import com.axolotl.mancala.game.PlayerNumber;
import com.axolotl.mancala.model.Board;
import com.axolotl.mancala.model.Pit;
import com.axolotl.mancala.model.Store;


/**
 * Represents a computer player
 */
public abstract class ComputerPlayer {
	
	// The board reference
	private final Board mBoard;
	
	// The random instance used to choose when there are multiple options
	private final Random mRandom = new Random(new Date().getTime());
	
	// The player that this strategy player
	private final PlayerNumber mPlayer;
	
	// The scoring strategy
	private ScoringStrategy mScoringStrategy;
	
	// The difficulty level
	private Difficulty mDifficulty;
		
	/**
	 * Constructs a new instance of the computer player
	 * 
	 * @param board The associated board
	 * 
	 * @param player The player that this instance represents
	 * 
	 * @param level The difficulty level that this player should adopt
	 */
	protected ComputerPlayer(Board board, PlayerNumber player, Difficulty level) {
		mBoard = board;
		mPlayer = player;
		mDifficulty = level;
		mScoringStrategy = getScoringStrategy(level);
	}
	
	/**
	 * Gets the difficulty level associated with this player
	 * 
	 * @return The current difficulty level
	 */
	public Difficulty getDifficulty() {
		return mDifficulty;
	}
	
	/**
	 * Called when it is the computer's turn to make a move
	 * 
	 * @return The hollow to play
	 */
	public Pit play() {
		
		// Get a list of all available hollows still in play
		List<Pit> playablePits = mBoard.getPits(new Predicate<Pit>() {
			public boolean matches(Pit item) {
				return !(item instanceof Store) &&
						item.getPlayerNumber() == mPlayer &&
						item.getNumberOfMarbles() > 0;
			}
		});
		
		int maxScore = -1;
		
		List<Pit> bestMoves = new ArrayList<Pit>();
		
		// Keep track of the hollows with the highest score
		for (Pit move : playablePits) {
			
			int score = mScoringStrategy.getScoreForMove(move);
			
			if (score > maxScore) {
				
				// Clear out the hollows from the best moves list, and replace with the new
				// highest scoring move
				maxScore = score;
				
				bestMoves.clear();
				bestMoves.add(move);
				
			} else if (score == maxScore) {				
				bestMoves.add(move);				
			}
		}
		
		if (bestMoves.size() > 1) {		
		
			// Randomly pick a move from the list
			return bestMoves.get(mRandom.nextInt(bestMoves.size()));
			
		} else if (bestMoves.size() == 1) {
			
			// Only one move has the high score
			return bestMoves.get(0);
			
		} else {
			
			// All playable moves have equally poor scores, pick a random one
			return playablePits.get(mRandom.nextInt(playablePits.size()));				
		}
	}
	
	/**
	 * Returns the scoring strategy used for scoring each move.  The strategy should
	 * be based on the difficulty level supplied
	 * 
	 * @param level
	 * 		  The level of difficulty to influence the scoring strategy
	 * 
	 * @return The scoring strategy
	 */
	protected abstract ScoringStrategy getScoringStrategy(Difficulty level);
	
	/**
	 * A strategy used for scoring a move based on a difficulty level
	 */
	interface ScoringStrategy {
		
		/**
		 * Returns a score for the given move
		 * 
		 * @param pit
		 * 			The hollow to move
		 * 
		 * @return A value used to influence the decision on which hollow to play
		 */
		int getScoreForMove(Pit pit);
		
	}
}
