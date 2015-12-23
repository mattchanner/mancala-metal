package com.axolotl.mancala;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.axolotl.mancala.game.Difficulty;
import com.axolotl.mancala.game.FinalScore;
import com.axolotl.mancala.game.Game;
import com.axolotl.mancala.game.GameImpl;
import com.axolotl.mancala.game.GameListener;
import com.axolotl.mancala.game.GameMode;
import com.axolotl.mancala.game.PlayerNumber;
import com.axolotl.mancala.model.Pit;
import com.axolotl.mancala.strategies.EgyptianStrategy;
import com.axolotl.mancala.ui.FinalScoreDialog;
import com.axolotl.mancala.ui.MancalaBoard;
import com.axolotl.mancala.ui.OnAnimationCompleteListener;

/**
 * The main game activity
 */
public class MancalaActivity extends Activity implements GameListener, OnAnimationCompleteListener {
	
	// the amount of time in milliseconds to show a message
	private static final int MESSAGE_DELAY = 1000;

	// the amount of time in milliseconds to show a message
	private static final int COMPUTER_DELAY = 1000;
	
	// The time to highlight the changed hollows for
	private static final int HIGHLIGHT_DELAY = 1000;
	
	private static final Random mRandom = new Random(new Date().getTime());
	
	// The log tag
	private static final String TAG = "MancalaActivity";
	
	// The key for the game state
	private static final String GAME_STATE_KEY = "GAME_STATE";
	
	// The mancala board
	private MancalaBoard mMancalaBoard;
	
	// The message area
	private TextView mMessageBoard;
	
	// The player one indicator
	private TextView mPlayerOneInfo;
	
	// The player two indicator
	private TextView mPlayerTwoInfo;
	
	// The current game
	private Game mMancalaGame;
	
	// Handler for timer tasks
	private Handler mHandler = new Handler();
	
	// The message to show to the user once the play animation has completed
	private String mMessageToShowAfterAnimation;
	
	private Queue<List<Pit>> mNotifications;
	
	// Game completed indicator
	private boolean mGameCompleted;
	
	private FinalScore mFinalScore;
	
    /** 
     * Called when the activity is first created.
     * 
     * @param savedInstanceState The instance state
     *  
     **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "onCreate called");
        
        // Inflate the main layoutmGameCompleted
        setContentView(R.layout.main);
        
        // Initialise the main game
        mMancalaGame = new GameImpl();
        
        // Set the default strategy
        // TODO: Choose from an option of strategies
        mMancalaGame.setStrategy(new EgyptianStrategy(mMancalaGame.getBoard()));  
        
        // Find view components now that the view is inflated
        mMancalaBoard = (MancalaBoard)findViewById(R.id.mancala_board);
        mMessageBoard = (TextView)findViewById(R.id.message_view);
        mPlayerOneInfo = (TextView)findViewById(R.id.player_1_view);
        mPlayerTwoInfo = (TextView)findViewById(R.id.player_2_view);
        
        // Add a listener to the game core - note this is done after the initial call to newGame
        mMancalaGame.addGameListener(this);
        
        mMancalaBoard.setOnAnimationCompleteListener(this);
        
        mNotifications = new LinkedList<List<Pit>>();
        
        String gameState = null;
        
        if (savedInstanceState != null && savedInstanceState.containsKey(GAME_STATE_KEY)) {
        	gameState = savedInstanceState.getString(GAME_STATE_KEY);
        }
        
		if (gameState != null && gameState.length() > 0) {
		
			Log.d(TAG, "onCreate -> Restoring state");
			mMancalaGame.restoreGameState(gameState);
			
		} else {
		    
			Log.d(TAG, "onCreate -> No existign state");
			int gameModeIndex = getIntent().getIntExtra("GAMEMODE", 0);
			int difficultyIndex = getIntent().getIntExtra("DIFFICULTY", 0);	        
	        
	        Difficulty difficulty = Difficulty.values()[difficultyIndex];
	        GameMode mode = gameModeIndex == 0 ? GameMode.OnePlayer : GameMode.TwoPlayer;
	    
	        // Set up a new game - this will initialise the board based on the current strategy
	        mMancalaGame.newGame(mode, difficulty);
		}
    }
    
	/**
	 * Handles the animation complete notification
	 */
    public void onAnimationComplete() {
    	
    	setPlayerDetails();
    	
    	if (mGameCompleted) {
			
    		displayMessage("");
			
			mMancalaGame.getStrategy().reset();
			
			FinalScoreDialog dialog = new FinalScoreDialog(this, mFinalScore, mMancalaGame.getGameMode());
			dialog.show();
    	} else {
    	
			if (mMancalaGame.getGameMode() == GameMode.OnePlayer && mMancalaGame.getStrategy().getCurrentPlayer() == PlayerNumber.Two) {
					
				displayMessage(getResources().getString(R.string.thinking_text));
				
				// Make the computer take a bit of time before performing the move
				mHandler.removeCallbacks(mComputerMove);
	            mHandler.postDelayed(mComputerMove, COMPUTER_DELAY); 
	            
			} else {
				displayMessage("");
			}
			
			if (mMessageToShowAfterAnimation != null && mMessageToShowAfterAnimation.length() > 0) {
				displayMessage(mMessageToShowAfterAnimation);
			}
    	}
	}
    
	/**
	 * Raised when an animation has begun
	 */
    public void onAnimationStart() {
    	mMessageBoard.setText("");
    }
	
	/**
	 * Sets the difficulty level of the computer player
	 * 
	 * @param difficulty The new level to apply
	 */
	public void setNewGame(GameMode mode, Difficulty difficulty) {
		
		Log.d(TAG, "setDifficulty: " + difficulty.toString());
		
		mMancalaGame.newGame(mode, difficulty);		
	}

	/**
	 * Called when a game has been restored
	 */
	public void onGameRestored() {
		
		Log.d(TAG, "onGameRestored");
		
		mMancalaBoard.setGame(mMancalaGame);
		mMancalaGame.getStrategy().setBoard(mMancalaGame.getBoard());
		
		// Set the current player information
		onAnimationComplete();
	}
	
    /**
     * Handles the board changed event
     * 
     * @param board The current board reference
     */
	public void onBoardChanged(List<Pit> changedPits) {
		
		mNotifications.add(changedPits);
		
		mMancalaBoard.highlightPits(mNotifications.remove(), HIGHLIGHT_DELAY);
	}

	/**
	 * Handles the new game event
	 */
	public void onNewGame() {
		
		// Give the board a reference to the current game
        mMancalaBoard.setGame(mMancalaGame);

        mGameCompleted = false;
        mFinalScore = null;
        
        // Flip a coin to see who goes first
        PlayerNumber playerToGoFirst = mRandom.nextInt(10) % 2 == 0 ? PlayerNumber.One : PlayerNumber.Two;
        
        mMancalaGame.getStrategy().setInitialPlayer(playerToGoFirst);
        
        if (mMancalaGame.getGameMode() == GameMode.TwoPlayer) {
            mPlayerTwoInfo.setTextColor(getResources().getColor(R.color.player_2_text_colour));
        } else {
            mPlayerTwoInfo.setTextColor(getResources().getColor(R.color.computer_text_colour));
        }
        
        // Set the current player information
        setPlayerDetails();
        
        if (playerToGoFirst == PlayerNumber.Two) {
        	onPlayerChanged(playerToGoFirst);
        	onAnimationComplete();
        }
	}
	
	/**
	 * Handles the game complete message
	 * 
	 * @param score The results of the game
	 */
	public void onGameComplete(FinalScore score) {
		
		if (mMancalaBoard.isAnimating()) {
			mGameCompleted = true;
			mFinalScore = score;
		} else {
			
			displayMessage("");
			
			mMancalaGame.getStrategy().reset();
			
			FinalScoreDialog dialog = new FinalScoreDialog(this, score, mMancalaGame.getGameMode());
			dialog.show();
		}
	}

	/**
	 * Displays a capture message to the user
	 */
	public void onPitCaptured(Pit capturedPit) {
		displayMessage(getResources().getString(R.string.capture_text));
	}

	/**
	 * Handles the player changed message
	 */
	public void onPlayerChanged(PlayerNumber newPlayer) {			
		mHandler.removeCallbacks(mClearTextDelayed);
        mHandler.postDelayed(mClearTextDelayed, MESSAGE_DELAY);
	}

	/**
	 * Handles the game message that the current player has another go
	 */
	public void onPlayerHasAnotherTurn() {
		
		mMessageToShowAfterAnimation = getResources().getString(R.string.free_go_text);	
	}
	
	/**
	 * Handles the pausing of this activity
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		Log.d(TAG, "onSaveInstanceState Called");
		
		String gameState = mMancalaGame.getGameState();
		outState.putString(GAME_STATE_KEY, gameState);
	}
	
	/**
	 * Callback method to clear the display text after a period of time
	 */
	private Runnable mClearTextDelayed = new Runnable() {
		public void run() {
			displayMessage("");
		}
	};
	
	/**
	 * Callback method to cause the computer to perform his next move
	 */
	private Runnable mComputerMove = new Runnable() {
	   public void run() {
		   mMancalaGame.makeMove(mMancalaGame.getPlayer2().play());
	   }
	};
		
	/**
	 * Displays a message to the user
	 * 
	 * @param message
	 * 		  The message to display
	 */
	private void displayMessage(String message) {
				
		if (mMancalaBoard.isAnimating()) {
			mMessageToShowAfterAnimation = message;
		} else {
			mMessageBoard.setText(message);	
	        mHandler.postDelayed(mClearTextDelayed, MESSAGE_DELAY);
		}
	}
	
	/**
	 * Sets the view to display the current player
	 */
	private void setPlayerDetails() {
		
		mMancalaBoard.setCurrentPlayer(mMancalaGame.getStrategy().getCurrentPlayer());
		if (mMancalaGame.getStrategy().getCurrentPlayer() == PlayerNumber.One) {
			mPlayerOneInfo.setText(getResources().getString(R.string.player_1_turn));
			mPlayerTwoInfo.setText("");
		} else {
			if (mMancalaGame.getGameMode() == GameMode.OnePlayer) {
				mPlayerTwoInfo.setText(getResources().getString(R.string.computers_turn_text));
			} else {
				mPlayerTwoInfo.setText(getResources().getString(R.string.player2_turn_text));
			}
			mPlayerOneInfo.setText("");
		}
	}
}