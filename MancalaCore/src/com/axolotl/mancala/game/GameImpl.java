package com.axolotl.mancala.game;

import java.util.ArrayList;
import java.util.List;

import com.axolotl.mancala.MancalaException;
import com.axolotl.mancala.model.Board;
import com.axolotl.mancala.model.BoardImpl;
import com.axolotl.mancala.model.Marble;
import com.axolotl.mancala.model.MarbleColour;
import com.axolotl.mancala.model.Pit;
import com.axolotl.mancala.strategies.ComputerPlayer;
import com.axolotl.mancala.strategies.MancalaStrategy;

/**
 * The Game implementation
 */
public class GameImpl implements Game, GamePublisher {

	// The delimiter for state strings
	private final String STATE_DELIMITER = "\t";
	
	// The mBoard
	private final Board mBoard;
	
	// The list of listeners
	private final List<GameListener> mGameListeners;
	
	// The current mStrategy in use
	private MancalaStrategy mStrategy;
	
	// The current game state prior to a move being made
	private String mCurrentGameState;
	
	// The difficulty level
	private Difficulty mDifficulty;
	
	// The game mode
	private GameMode mPlayerMode;
	
	// The second player, if the player mode is set to single player
	private ComputerPlayer mPlayer2;
	
	/**
	 * Creates a new instance of the GameImpl class
	 */
	public GameImpl() {
		mBoard = new BoardImpl();
		mGameListeners = new ArrayList<GameListener>();
	}
	
	/**
	 * Gets the computer player, if the current game mode is set to single player
	 * @return The computer player
	 */
	public ComputerPlayer getPlayer2() {
		return mPlayer2;
	}
	
	/**
	 * Gets the current difficulty level for this game
	 * @returnState
	 */
	public Difficulty getDifficulty() {
		return mDifficulty;
	}
	
	/**
	 * Gets the current game mode
	 * @return
	 */
	public GameMode getGameMode() {
		return mPlayerMode;
	}
    /**
	 * Returns the board in play
	 * 
	 * @return The current board
	 */
	@Override
	public Board getBoard() {
		return mBoard;
	}
	
   /**
	 * Returns the current strategy in use
	 */
	@Override
	public MancalaStrategy getStrategy() {
		return mStrategy;
	}
	
	/**
	 * Sets the current strategy
	 */
	@Override 
	public void setStrategy(MancalaStrategy strategy) {
		this.mStrategy = strategy;
		strategy.setGamePublisher(this);
	}
	
	/**
	 * Creates a new game
	 * 
	 * @param playerMode The game mode
	 * 
	 * @param difficulty The difficulty level
	 */
	@Override
	public void newGame(GameMode playerMode, Difficulty difficulty) throws MancalaException {
		
		if (mStrategy == null)
			throw new MancalaException("No mStrategy currently set");
		
		mPlayerMode = playerMode;
		
		if (mPlayerMode == GameMode.OnePlayer) {
			mPlayer2 = mStrategy.createAIPlayer(PlayerNumber.Two, difficulty);
		} else {
			mPlayer2 = null;
		}
		
		mDifficulty = difficulty;
		mStrategy.setBoard(mBoard);
		mStrategy.reset();
		
		notifyNewGame();
	}

	/**
	 * Plays the current hollow in the context of the current player
	 */
	@Override
	public void makeMove(Pit currentPit) throws MancalaException {
		
		mStrategy.makeMove(currentPit);
		mCurrentGameState = getGameStringCore();
	}
	
	/**
	 * Adds a GameListener
	 * 
	 * @param listener The listener to add
	 */
	@Override
	public void addGameListener(GameListener listener) {
		if (!mGameListeners.contains(listener))
			mGameListeners.add(listener);
	}

	/**
	 * Removes the given listener
	 * hollow
	 * @param listener The listener to remove
	 */
	@Override
	public void removeGameListener(GameListener listener) {
		mGameListeners.remove(listener);		
	}
	
	/**
	 * Notifies all listeners when the board has changed
	 */
	@Override
	public void notifyBoardChanged(List<Pit> changedPits) {
		for (GameListener l : mGameListeners) {
			l.onBoardChanged(changedPits);
		}
	}
	
	/**
	 * Notifies all listeners when a game has been restored
	 */
	@Override
	public void notifyGameRestored() {

		for (GameListener l : mGameListeners) {
			l.onGameRestored();
		}	
	}
	
	/**
	 * Notifies all listeners that the player has changed
	 * @param newPlayer
	 */
	@Override
	public void notifyPlayerChanged(PlayerNumber newPlayer) {
		for (GameListener l : mGameListeners) {
			l.onPlayerChanged(newPlayer);
		}
	}
	
	/**
	 * Notifies all listeners that the game has completed
	 * @param score The final score
	 */
	@Override
	public void notifyGameComplete(FinalScore score) {		
		for (GameListener l : mGameListeners) {
			l.onGameComplete(score);
		}
	}

	/**
	 * Notifies all listeners that the current player has another turn
	 */
	@Override
	public void notifyPlayerHasAnotherTurn() {
		for (GameListener l : mGameListeners) {
			l.onPlayerHasAnotherTurn();
		}
	}

	/**
	 * Notifies all listeners that the given hollow has been captured
	 */
	@Override
	public void notifyCapture(Pit capturedPit) {
		for (GameListener l : mGameListeners) {
			l.onPitCaptured(capturedPit);
		}		
	}

	/**
	 * Notifies when a new game has started
	 */
	@Override
	public void notifyNewGame() {
		for (GameListener l : mGameListeners) {
			l.onNewGame();
		}
	}

	/**
	 * Gets the current state of the game, represented as a string
	 * 
	 * @return The string representation of the game
	 */
	public String getGameState() {
		
		if (mCurrentGameState == null)
			mCurrentGameState = getGameStringCore();
		
		return mCurrentGameState;
		
	}
	
	/**
	 * Restores the game back to the state given by the string
	 * 
	 * @param state The game state to restore
	 */
	public void restoreGameState(String state) {

		if (state == null || state.length() == 0)
			return;
		
		try {
			
			mStrategy.reset();
			List<Pit> pits = mBoard.getPits();
			
			String[] segments = state.split(STATE_DELIMITER);
			if (segments.length != pits.size() + 3) {
				return;
			}
			
			MarbleColour[] colours = MarbleColour.values();
			mPlayerMode = GameMode.valueOf(segments[0]);		
			mDifficulty = Difficulty.valueOf(segments[1]);
			PlayerNumber player = PlayerNumber.valueOf(segments[2]);
			mStrategy.setInitialPlayer(player);
			
			for (int i = 0; i < mBoard.getPits().size(); i++) {
				Pit p = pits.get(i);
				Integer marbleCount = Integer.valueOf(segments[i + 3]);
				p.removeAll();
				for (int marbleIndex = 0; marbleIndex < marbleCount; marbleIndex++) {
					p.add(new Marble(colours[marbleIndex % colours.length]));
				}
			}
			
			if (mPlayerMode == GameMode.OnePlayer) {
			    mPlayer2 = mStrategy.createAIPlayer(PlayerNumber.Two, mDifficulty);
			}
			
			notifyGameRestored();
		}
		catch (Exception ex) {
			System.err.print(ex.toString());
		}		
	}
	
	/**
	 * The main method to construct the game state
	 * @return
	 */
	private String getGameStringCore() {
		
		StringBuilder builder = new StringBuilder();
		
		// write the game mode
		builder.append(mPlayerMode.toString());
		builder.append(STATE_DELIMITER);
		
		// write the difficulty level
		builder.append(mDifficulty.toString());
		builder.append(STATE_DELIMITER);
		
		// write the current player
		builder.append(mStrategy.getCurrentPlayer().toString());
		builder.append(STATE_DELIMITER);
		
		// write the mBoard state
		for (Pit pit : mBoard.getPits()) {
			builder.append(pit.getNumberOfMarbles());
			builder.append(STATE_DELIMITER);
		}
		return builder.toString();
	}
}
