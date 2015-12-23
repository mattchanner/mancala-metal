package com.axolotl.mancala.system;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.axolotl.mancala.Utilities;
import com.axolotl.mancala.game.Difficulty;
import com.axolotl.mancala.game.FinalScore;
import com.axolotl.mancala.game.Game;
import com.axolotl.mancala.game.GameImpl;
import com.axolotl.mancala.game.GameListener;
import com.axolotl.mancala.game.GameMode;
import com.axolotl.mancala.game.PlayerNumber;
import com.axolotl.mancala.model.Pit;
import com.axolotl.mancala.strategies.ComputerPlayer;
import com.axolotl.mancala.strategies.EgyptianStrategy;
import com.axolotl.mancala.strategies.MancalaStrategy;

public class SystemTests {

	@Test
	public void testAIversusAI() {
	    
		Game game = new GameImpl();

		GameListenerImpl listener = new GameListenerImpl();

		game.addGameListener(listener);
		
		MancalaStrategy strategy = new EgyptianStrategy(game.getBoard());
		game.setStrategy(strategy);
		
		// Call newGame to initialise the board based on the current game strategy
		game.newGame(GameMode.OnePlayer, Difficulty.Hard);
		
		ComputerPlayer p1 = strategy.createAIPlayer(PlayerNumber.One, Difficulty.Easy);
		ComputerPlayer p2 = game.getPlayer2();
		
		while (!listener.isCompleted()) {

			PlayerNumber nextPlayer = game.getStrategy().getCurrentPlayer();
			
			ComputerPlayer player = nextPlayer == PlayerNumber.One ? p1 : p2;
			
		    game.makeMove(player.play());	
		    
		    // System.out.println(Utilities.printBoard(game.getBoard()));
		}

		FinalScore score = listener.getFinalScore();		
		
		System.out.println(Utilities.printFinalScore(score));
		
		int total = score.getScore(PlayerNumber.One).getTotalScore() + score.getScore(PlayerNumber.Two).getTotalScore();
		
		// The total of both player 1 and player 2 should equal the expected number of 
		// marbles for this strategy
		Assert.assertEquals(48, total);			
	}

	class GameListenerImpl implements GameListener {

		private boolean completed = false;
		private FinalScore score;

		public boolean isCompleted() {
			return completed;
		}
		
		public FinalScore getFinalScore() {
			return score;
		}
		
		@Override
		public void onBoardChanged(List<Pit> changedPits) {

		}

		@Override
		public void onGameComplete(FinalScore score) {
			completed = true;
			this.score = score;
		}

		@Override
		public void onPitCaptured(Pit capturedPit) {

		}

		@Override
		public void onPlayerChanged(PlayerNumber newPlayer) {

		}

		@Override
		public void onPlayerHasAnotherTurn() {

		}

		@Override
		public void onNewGame() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGameRestored() {
			// TODO Auto-generated method stub
			
		}
	}
}
