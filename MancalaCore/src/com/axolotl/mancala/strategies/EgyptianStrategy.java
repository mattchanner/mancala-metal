package com.axolotl.mancala.strategies;

import java.util.List;

import com.axolotl.mancala.MancalaException;
import com.axolotl.mancala.game.Difficulty;
import com.axolotl.mancala.game.FinalScore;
import com.axolotl.mancala.game.GamePublisher;
import com.axolotl.mancala.game.PlayerNumber;
import com.axolotl.mancala.model.Board;
import com.axolotl.mancala.model.Marble;
import com.axolotl.mancala.model.Pit;
import com.axolotl.mancala.model.Store;

/**
 * A mancala strategy using the egyptian rules
 */
public class EgyptianStrategy implements MancalaStrategy {

	// The number of hollows for each player
	private static final int NUM_HOLLOWS_PER_PLAYER = 6;

	// The initial number of marbles per hollow
	private static final int NUM_MARBLES_PER_HOLLOW = 4;

	// The game board
	private Board mBoard;

	// The same publisher
	private GamePublisher mEventSink;

	// The current player
	private PlayerNumber mCurrentPlayer = PlayerNumber.One;
	
	/**
	 * Constructs a new instance of the board
	 * 
	 * @param board The board reference
	 */
	public EgyptianStrategy(Board board) {
		mBoard = board;
	}
	
	/**
	 * Sets the first player for the game
	 * 
	 * @param playerToGoFirst The player to start with
	 */
	@Override
	public void setInitialPlayer(PlayerNumber playerToGoFirst) {
		mCurrentPlayer = playerToGoFirst;		
	}

	/**
	 * Returns an AI player
	 */
	@Override
	public ComputerPlayer createAIPlayer(PlayerNumber player, Difficulty level) {

		return new EgyptianPlayer(mBoard, player, level);

	}

	/**
	 * Gets the name of the strategy
	 * 
	 * @return The strategy name
	 */
	@Override
	public String getStrategyName() {
		return "Egyptian Rules";
	}

	/**
	 * Gets a description of the strategy
	 * 
	 * @return The strategies description
	 */
	@Override
	public String getStrategyDescription() {
		return "If a player drops the last stone from his hand into his mancala, he gets to move again. "
			+ "If a player drops the last stone into one of the empty bowls on his side of the board, he "
			+ "takes that stone, plus all the stones in the opponent's bowl directly across from his bowl "
			+ "and places them in his mancala. The game ends when one player no longer has stones in his "
			+ "small bowls. The other player (who still has stones on his side) places all remaining stones "
			+ "into his own mancala (it is not necessarily an advantage to be the first player to empty the six "
			+ "bowls).";
	}

	/**
	 * Returns the active player
	 * 
	 * @return The current Player
	 */
	@Override
	public PlayerNumber getCurrentPlayer() {
		return mCurrentPlayer;
	}

	/**
	 * Sets the game publisher
	 */
	@Override
	public void setGamePublisher(GamePublisher eventSink) {
		this.mEventSink = eventSink;
	}

	/**
	 * Resets the board back to an initial state for a new game
	 */
	@Override
	public void reset() {

		mBoard.clear();

		try {

			mBoard.initialiseBoard(NUM_HOLLOWS_PER_PLAYER, NUM_MARBLES_PER_HOLLOW);

		} catch (MancalaException e) {

			// Should never happen as valid arguments have been supplied
			e.printStackTrace();
		}
	}

	/**
	 * Sets the board reference
	 * 
	 * @param board The board reference
	 */
	@Override
	public void setBoard(Board board) {
		this.mBoard = board;
	}

	/**
	 * Plays the current hollow
	 */
	@Override
	public void makeMove(Pit currentPit) throws MancalaException {

		if (currentPit == null) {
			throw new MancalaException("currentHollow cannot be null");
		}

		if (currentPit.getNumberOfMarbles() == 0) {
			throw new MancalaException("currentHollow does not contain any marbles");
		}

		// Pop out all the marbles from the current hollow
		Marble[] marbles = currentPit.removeAll();

		// Get the hollows that follow the current one
		List<Pit> placements = mBoard.getNextPits(currentPit, mCurrentPlayer, marbles.length);

		// Add a single marble to each of the hollows
		int index = 0;
		
		for (Pit h : placements) {
			h.add(marbles[index++]);
		}
		
		mEventSink.notifyBoardChanged(placements);
		
		if (hasGameCompleted()) {

			FinalScore score = new FinalScore(mBoard.getScoreForPlayer(PlayerNumber.One), 
											  mBoard.getScoreForPlayer(PlayerNumber.Two));

			mEventSink.notifyGameComplete(score);			

		} else {

			Pit lastPitUsed = placements.get(placements.size() - 1);

			// If the final marble was added to a store, the player gets another go
			if (lastPitUsed instanceof Store) {
				
				mEventSink.notifyPlayerHasAnotherTurn();

			} else {

				boolean hasCompleted = false;

				// Check last hollow to see if it can "capture" adjacent marbles
				// (as long as the marble is the only one in the hollow)
				if (lastPitUsed.getNumberOfMarbles() == 1) {

					Pit adjacentPit = mBoard.getAdjacentPit(lastPitUsed);

					if (adjacentPit.getPlayerNumber() != mCurrentPlayer &&
						adjacentPit.getNumberOfMarbles() > 0) {

						Store playerStore = mBoard.getPlayersStore(currentPit.getPlayerNumber());

						// Create a temporary pit to add the captured marbles to
						// this prevents the previous count on the end store
						// from losing track
						Pit p = new Pit(PlayerNumber.One);
						
						// Place the last hollows marble in the player's store
						p.addAll(lastPitUsed.removeAll());
						
						// Grab the adjacent player's marbles and add those too
						p.addAll(adjacentPit.removeAll());
						
						// Add marbles to the pit in one go
						playerStore.addAll(p.removeAll());

						// Notify listeners about the capturing of the hollow
						mEventSink.notifyCapture(adjacentPit);
						
						placements.add(lastPitUsed);
						placements.add(adjacentPit);
						placements.add(playerStore);
						
						mEventSink.notifyBoardChanged(placements);
						
						// As another move has just occurred, re-check for the
						// end of the game
						if (hasGameCompleted()) {

							FinalScore score = new FinalScore(mBoard.getScoreForPlayer(PlayerNumber.One), 
															  mBoard.getScoreForPlayer(PlayerNumber.Two));

							mEventSink.notifyGameComplete(score);

							hasCompleted = true;
						}
					}
				}
				
				// If the game has not yet ended, swap players and notify
				// listeners
				if (!hasCompleted) {

					// Switch players and indicate this as the result of the
					// method
					mCurrentPlayer = mCurrentPlayer == PlayerNumber.One ? PlayerNumber.Two
							: PlayerNumber.One;

					mEventSink.notifyPlayerChanged(mCurrentPlayer);
				}
			}
		}
	}

	/**
	 * Checks the current state of the board to determine whether the game has
	 * completed.
	 * 
	 * For this strategy, this means that there are no available marbles in any
	 * hollow for either player
	 * 
	 * @return True if one player has no more marbles left
	 */
	private boolean hasGameCompleted() {

		return mBoard.getScoreForPlayer(PlayerNumber.One).getNumberRemaining() == 0
				|| mBoard.getScoreForPlayer(PlayerNumber.Two).getNumberRemaining() == 0;
	}

	/**
	 * A private implementation of the ComputerPlayer interface for this
	 * strategy
	 */
	private class EgyptianPlayer extends ComputerPlayer {

		/**
		 * Constructs a new instance of the computer player
		 * 
		 * @param board The associated board
		 * 
		 * @param player The player that this instance represents
		 * 
		 * @param level The difficulty level that this player should adopt
		 */
		public EgyptianPlayer(Board board, PlayerNumber player, Difficulty level) {
			super(board, player, level);
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
		@Override
		protected ScoringStrategy getScoringStrategy(Difficulty level) {
			switch (level) {
			case Medium:
				return new MediumScoringStrategy();
			case Hard:				
				return new DifficultScoringStrategy();
			case Easy:				
			default:
				return new EasyScoringStrategy();
			}
		}
		
		/**
		 * A scoring strategy for easy games.  This implementation weights all
		 * playable moves equally, so a random choice will be made by the computer
		 * player as to which hollow is played.
		 */
		class EasyScoringStrategy implements ScoringStrategy {

			/**
			 * This implementation simply weights all moves as equal
			 */
			@Override
			public int getScoreForMove(Pit pit) {
				return 1;
			}			
		}
		
		/**
		 * A scoring strategy for medium level difficulty. This implementation weights all moves
		 * with at least one point, meaning that it is possible that any move could be played.
		 * Moves that ad a marble to the players store will be given higher weighting, but no
		 * consideration will be made for capturing the opponents marbles, or for repeat
		 * plays
		 */
		class MediumScoringStrategy implements ScoringStrategy {
			
			@Override
			public int getScoreForMove(Pit pit) {				
				int score = 1;
				
				List<Pit> placements = mBoard.getNextPits(pit, mCurrentPlayer, pit.getNumberOfMarbles());

				// If the store is in the list of moves, add 1 to the score
				if (placements.contains(mBoard.getPlayersStore(mCurrentPlayer))) {
					score += 1;
				}

				
				return score;
			}			
		}
		
		/**
		 * This method calculates a weighted score for playing the marbles in the given hollow.
		 * 
		 * The move is given a value of 1 if a marble ends up in the player's store.
		 * 
		 * If the final marble ends up in the store, an extra 5 points are added as the
		 * player gets another go.
		 * 
		 * If the final marble ends up in an empty hollow, the adjacent hollows marble count
		 * is added to the store (assuming that the empty hollow is one of the player's own hollows).
		 */
		class DifficultScoringStrategy implements ScoringStrategy {
			
			@Override
			public int getScoreForMove(Pit pit) {
				int score = 0;
				int marbleCount = pit.getNumberOfMarbles();
				
				List<Pit> placements = mBoard.getNextPits(pit, mCurrentPlayer, marbleCount);

				// If the store is in the list of moves, add 1 to the score
				if (placements.contains(mBoard.getPlayersStore(mCurrentPlayer))) {
					score += 1;
				}

				// Determine the last hollow in the list
				Pit lastPit = placements.get(placements.size() - 1);

				if (lastPit instanceof Store) {

					// Get another go if the last marble lands in the store
					score += 5;
					
				} else {

					// Test for a capture, and if there is one, add the number of captured marbles
					if (lastPit.getPlayerNumber() == mCurrentPlayer && lastPit.getNumberOfMarbles() == 0) {
					    
					    score += lastPit.getNumberOfMarbles();
						score += mBoard.getAdjacentPit(lastPit).getNumberOfMarbles();
						
					}
				}

				return score;
			}			
		}
	}
}
