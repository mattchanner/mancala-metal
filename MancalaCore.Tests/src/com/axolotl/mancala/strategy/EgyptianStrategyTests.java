package com.axolotl.mancala.strategy;

import junit.framework.Assert;

import org.junit.Test;

import com.axolotl.mancala.game.GamePublisher;
import com.axolotl.mancala.mocks.CountingGamePublisher;
import com.axolotl.mancala.model.Board;
import com.axolotl.mancala.model.BoardImpl;
import com.axolotl.mancala.strategies.EgyptianStrategy;

public class EgyptianStrategyTests {

	@Test
	public void testStrategyInitialisation() {
		
		Board board = new BoardImpl();
		EgyptianStrategy strategy = new EgyptianStrategy(board);		
		GamePublisher publisher = new CountingGamePublisher();
		
		strategy.setBoard(board);
		strategy.setGamePublisher(publisher);
		strategy.reset();
		
		Assert.assertEquals(6, board.getNumberOfHollowsPerPlayer());
	}	
	
	@Test
	public void testCapturingOfOpposingHollow() {
		
		Board board = new BoardImpl();
		EgyptianStrategy strategy = new EgyptianStrategy(board);
		CountingGamePublisher publisher = new CountingGamePublisher();
		
		strategy.setBoard(board);
		strategy.setGamePublisher(publisher);
		strategy.reset();
		
		// clear marbles from the 1st hollow for player 2
		board.getPits().get(7).removeAll();
		
		strategy.makeMove(board.getPits().get(3));
		
		Assert.assertEquals(1, publisher.getBoardChangedCallCount());
		Assert.assertEquals(0, publisher.getCaptureCallCount());
		Assert.assertEquals(0, publisher.getGameCompleteCallCount());
		Assert.assertEquals(0, publisher.getPlayerHasAnotherTurnCallCount());
		Assert.assertEquals(1, publisher.getPlayerChangedCallCount());
	}
	
	@Test
	public void testEndOfGame() {
	    
		Board board = new BoardImpl();
		EgyptianStrategy strategy = new EgyptianStrategy(board);
        CountingGamePublisher publisher = new CountingGamePublisher();
        
        strategy.setBoard(board);
        strategy.setGamePublisher(publisher);
        strategy.reset();
        
        // clear most of the marbles from player 1
        board.getPits().get(0).removeAll();
        board.getPits().get(1).removeAll();
        board.getPits().get(2).removeAll();
        board.getPits().get(3).removeAll();
        board.getPits().get(4).removeAll();
        
        // Now that only one hollow remains for player 1 (assuming 6 hollows / player)
        // make a move on the final hollow, which will result in player 1 having all hollows
        // cleared, which in turn should signal the end of the game
        strategy.makeMove(board.getPits().get(5));
        
        Assert.assertEquals(1, publisher.getGameCompleteCallCount());
	}
}
