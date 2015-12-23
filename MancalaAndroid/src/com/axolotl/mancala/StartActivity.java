package com.axolotl.mancala;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.axolotl.mancala.game.GameMode;
import com.axolotl.mancala.ui.DifficultySelectorDialog;

/**
 * The initial activity in the game
 */
public class StartActivity extends Activity implements View.OnClickListener {
	
	// The single player button
	private Button mSinglePlayerButton;
	
	// The 2 player button
	private Button mTwoPlayerButton;
	
	/** 
     * Called when the activity is first created.
     * 
     * @param savedInstanceState The instance state
     *  
     **/
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	// Inflate the main layout
    	setContentView(R.layout.start);
    	
    	findViews();
    	
    	attachHandlers();
	}
    
    /**
     * Locates the view components
     */
    private void findViews() {    	
    	mSinglePlayerButton = (Button) findViewById(R.id.start_button_1_player);
    	mTwoPlayerButton = (Button) findViewById(R.id.start_button_2_player);
    }
    
    /**
     * Attached click handlers to the buttons
     */
    private void attachHandlers() {    	
    	mSinglePlayerButton.setOnClickListener(this);	
    	mTwoPlayerButton.setOnClickListener(this);	
    }

    /**
     * Handles the click event for each of the buttons
     */
	public void onClick(View view) {
		
		GameMode mode;
		if (view.getId() == R.id.start_button_1_player) {			
			mode = GameMode.OnePlayer;
			DifficultySelectorDialog difficultyDialog = new DifficultySelectorDialog(this, mode);
			difficultyDialog.show();
		} else {
			mode = GameMode.TwoPlayer;
			Intent i = new Intent(this, MancalaActivity.class);
			i.putExtra("GAMEMODE", 1);
			startActivity(i);
		}	
	}
}
