package com.axolotl.mancala.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.axolotl.mancala.MancalaActivity;
import com.axolotl.mancala.R;
import com.axolotl.mancala.game.Difficulty;
import com.axolotl.mancala.game.GameMode;

/**
 * A dialog to enable the user to select a difficulty level
 * @author matt
 */
public class DifficultySelectorDialog extends Dialog implements OnItemClickListener {

	// The parent activity - if this reference is not null, it is assumed
	// that the dialog is shown from an existing board, otherwise a new activity
	// will be started
	private final MancalaActivity mMancalaActivity;
	
	// The game mode to use
	private GameMode mGameMode;
	
	/**
	 * Constructs a new instance of the dialog class.  
	 * 
	 * In this case, it is assumed that the dialog has been launched from 
	 * the main start screen, and a new mancala activity will be created 
	 * when the user makes their selection.
	 * 
	 * @param context The parent context
	 * 
	 * @param mode The game mode
	 */
	public DifficultySelectorDialog(Context context, GameMode mode) {
		super(context);
		mGameMode = mode;
		mMancalaActivity = null;
		setTitle(R.string.difficulty_dialog_title);
	}
	
	/**
	 * Constructs a new instance of the dialog class.  
	 * 
	 * In this case, it is assumed the dialog has been launched from an existing 
	 * mancala activity, so rather than launch a new activity when the user makes
	 * their selection, the setDifficulty method will be called instead.
	 * 
	 * @param context The parent game context
	 */
	public DifficultySelectorDialog(MancalaActivity context, GameMode mode) {
		super(context);
		mGameMode = mode;
		mMancalaActivity = context;
		setTitle(R.string.difficulty_dialog_title);
	}

	/**
	 * Creates the dialog
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// Inflate from XML
		setContentView(R.layout.difficultyselection);
		
		// Add a click listener to the list view
		ListView view = (ListView) findViewById(R.id.difficulty_list);		
		view.setOnItemClickListener(this);
	}
	
	/**
	 * Handles the click event on the list view
	 * 
	 * @param parent The list view adapter
	 * 
	 * @param view The sender
	 * 
	 * @param position The selected list position
	 * 
	 * @param id The id of the selected item
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		// If this instance has a reference to an existing mancala activity, 
		// simply call the setDifficulty method on that reference, otherwise,
		// start a new activity and pass the selected level in using the intent
		if (mMancalaActivity != null) {
			mMancalaActivity.setNewGame(mGameMode, Difficulty.values()[position]);
			dismiss();
		} else {
			Intent i = new Intent(getContext(), MancalaActivity.class);
			i.putExtra("DIFFICULTY", position);
			i.putExtra("GAMEMODE", mGameMode == GameMode.OnePlayer ? 0 : 1);
			getContext().startActivity(i);
		}
    }	
}
