package com.axolotl.mancala.game;

import junit.framework.Assert;

import org.junit.Test;

import com.axolotl.mancala.MancalaException;
import com.axolotl.mancala.mocks.CountingGamePublisher;
import com.axolotl.mancala.model.Board;
import com.axolotl.mancala.model.Pit;
import com.axolotl.mancala.strategies.EgyptianStrategy;

public class GameTests {

	@Test
	public void testGameStrategy() {
		
		Game game = new GameImpl();
		game.setStrategy(new EgyptianStrategy(game.getBoard()));
		Assert.assertEquals(EgyptianStrategy.class, game.getStrategy().getClass());		
	}
	
	@Test
	public void testBoardInitialisation() {
		
		Board board = createAndStartGame().getBoard();
		
		Assert.assertEquals(6, board.getNumberOfHollowsPerPlayer());
		Assert.assertEquals(14, board.getPits().size());		
	}
	
	@Test
	public void testAddingFinalMarbleToPlayersStoreResultsInAnotherTurn() {
		
		Game game = createAndStartGame();
		CountingGamePublisher publisher = new CountingGamePublisher();
		game.getStrategy().setGamePublisher(publisher);
		
		// By moving the marbles from the second hollow, the final marble will land
		// in the player's store, causing the player to win another turn
		Pit pit = game.getBoard().getPits().get(2);
		
		try {
			
			game.makeMove(pit);
			
			Assert.assertEquals(1, publisher.getBoardChangedCallCount());
			Assert.assertEquals(0, publisher.getPlayerChangedCallCount());
			Assert.assertEquals(1, publisher.getPlayerHasAnotherTurnCallCount());
			
		} catch (MancalaException e) {
			Assert.fail("Unexpected exception: " + e.toString());
		}
	}
	
	private static Game createAndStartGame() {
		
		Game game = new GameImpl();
		game.setStrategy(new EgyptianStrategy(game.getBoard()));
		
		try {
			game.newGame(GameMode.TwoPlayer, Difficulty.Hard);
		} catch (MancalaException e) {
			Assert.fail("Unexpected exception: " + e.toString());
		}
		return game;
	}
}
