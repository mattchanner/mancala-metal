package com.axolotl.mancala.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.axolotl.mancala.MancalaActivity;
import com.axolotl.mancala.R;
import com.axolotl.mancala.game.Difficulty;
import com.axolotl.mancala.game.FinalScore;
import com.axolotl.mancala.game.GameMode;
import com.axolotl.mancala.game.PlayerNumber;
import com.axolotl.mancala.game.PlayerScore;

/**
 * A dialog displaying the result of the game
 *
 */
public class FinalScoreDialog extends Dialog {

	// The final score
	private final FinalScore mScore;
	
	// The parent activity
	private final MancalaActivity mActivity;
	
	// The result text
	private TextView mResultText;
	
	// The title text for player 2
	private TextView mPlayer2TitleText;
	
	// Player 1 store text
	private TextView mPlayer1StoreText;
	
	// Player 2 store text
	private TextView mPlayer2StoreText;
	
	// Player 1 remaining
	private TextView mPlayer1RemainingText;
	
	// Player 2 remaining
	private TextView mPlayer2RemainingText;
	
	// Player 1 score
	private TextView mPlayer1ScoreText;
	
	// Player 2 score
	private TextView mPlayer2ScoreText;
	
	// New game button
	private Button mNewSinglePlayerGameButton;	
	
	// New game button
	private Button mNewTwoPlayerGameButton;	
	
	// The current game mode
	private GameMode mGameMode;
	
	/**
	 * Constructs a new instance of the final score dialog
	 * 
	 * @param context The parent activity
	 * 
	 * @param score The score to display
	 */
	public FinalScoreDialog(MancalaActivity context, FinalScore score, GameMode mode) {
		super(context);
		
		mActivity = context;
		mScore = score;
		mGameMode = mode;
		
		setContentView(R.layout.finalscoredialog);
		findViews();
		bind();
	}
	
	/**
	 * Gets a reference to each of the view components
	 */
	private void findViews() {
		
		mPlayer2TitleText = (TextView)findViewById(R.id.final_score_player_2_text);
		mResultText = (TextView)findViewById(R.id.final_score_result);
		mNewSinglePlayerGameButton = (Button)findViewById(R.id.final_score_new_single_player_game);
		mNewTwoPlayerGameButton = (Button)findViewById(R.id.final_score_new_two_player_game);
		
		mPlayer1RemainingText = (TextView)findViewById(R.id.final_score_player_1_remaining);
		mPlayer2RemainingText = (TextView)findViewById(R.id.final_score_player_2_remaining);
		
		mPlayer1StoreText = (TextView)findViewById(R.id.final_score_player_1_store);
		mPlayer2StoreText = (TextView)findViewById(R.id.final_score_player_2_store);
		
		mPlayer1ScoreText = (TextView)findViewById(R.id.final_score_player_1_score);
		mPlayer2ScoreText = (TextView)findViewById(R.id.final_score_player_2_score);
		
		int player2Colour = -1;
		if (mGameMode == GameMode.TwoPlayer) {
		    player2Colour = getContext().getResources().getColor(R.color.player_2_text_colour);
		} else {
		    player2Colour = getContext().getResources().getColor(R.color.computer_text_colour);
		}
		
		mPlayer2RemainingText.setTextColor(player2Colour);
		mPlayer2StoreText.setTextColor(player2Colour);
		mPlayer2ScoreText.setTextColor(player2Colour);
		mPlayer2TitleText.setTextColor(player2Colour);
	}
	/**
	 * Binds the final score result to the viewDifficultySelectorDialog
	 */
	private void bind() {
		
		setTitle(getContext().getResources().getString(R.string.final_score_dialog_title));
		
		if (mGameMode == GameMode.OnePlayer) {
			mPlayer2TitleText.setText(getContext().getResources().getString(R.string.player_2_text_computer));
		} else {
			mPlayer2TitleText.setText(getContext().getResources().getString(R.string.player_2_text_human));
		}
		
		String message = "";
		switch (mScore.getResult()) {
		
		case Player1Wins:
			if (mGameMode == GameMode.OnePlayer) {
				message = getContext().getResources().getString(R.string.you_win_text);
			} else {
				message = getContext().getResources().getString(R.string.player1_wins_text);
			}
			break;
		
		case Player2Wins:
			if (mGameMode == GameMode.OnePlayer) {
				message = getContext().getResources().getString(R.string.computer_wins_text);
			} else {
				message = getContext().getResources().getString(R.string.player2_wins_text);
			}
			break;
		
		case Draw:
			message = getContext().getResources().getString(R.string.its_a_draw_text);
			break;
		}
		
		bindScore(mScore.getScore(PlayerNumber.One), mPlayer1StoreText, mPlayer1RemainingText, mPlayer1ScoreText);
		bindScore(mScore.getScore(PlayerNumber.Two), mPlayer2StoreText, mPlayer2RemainingText, mPlayer2ScoreText);
		
		mResultText.setText(message);		
		
		// Add a click listener
		mNewSinglePlayerGameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(android.view.View arg0) {
				
				DifficultySelectorDialog difficultyDialog = new DifficultySelectorDialog(mActivity, GameMode.OnePlayer);
				difficultyDialog.show();
				dismiss();	
			}
		});
		
		// Add a click listener
		mNewTwoPlayerGameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(android.view.View arg0) {
				
				mActivity.setNewGame(GameMode.TwoPlayer, Difficulty.Easy);
				dismiss();	
			}
		});
	}
	
	/**
	 * Binds a players score to its view
	 * 
	 * @param score
	 * 		  The players score
	 * 
	 * @param storeView
	 * 		  The store view
	 * 
	 * @param remainingView
	 * 		 The remaining view
	 * 
	 * @param scoreView
	 * 		 The score view
	 */
	private void bindScore(PlayerScore score, TextView storeView, TextView remainingView, TextView scoreView) {
		storeView.setText(Integer.toString(score.getNumberInStore()));
		remainingView.setText(Integer.toString(score.getNumberRemaining()));
		scoreView.setText(Integer.toString(score.getTotalScore()));
	}
}
