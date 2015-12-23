package com.axolotl.mancala.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.axolotl.mancala.JLinq;
import com.axolotl.mancala.MancalaException;
import com.axolotl.mancala.Predicate;
import com.axolotl.mancala.game.PlayerNumber;
import com.axolotl.mancala.game.PlayerScore;

public class BoardTests {

	@Test
	public void testInitialBoard() {
		Board board = initialiseBoard(6, 4);
		assertEquals(6, board.getNumberOfHollowsPerPlayer());		
	}
	
	@Test
	public void testInitialBoardContainsMarblesInEachHole() {
		
		Board board = initialiseBoard(6, 4);
		
		int player1Count = 0;
		int player2Count = 0;
		
		List<Pit> pits = board.getPits();
		assertEquals(14, pits.size());
		
		for (Pit pit : pits) {
			
			if (pit.getPlayerNumber() == PlayerNumber.One) {
				player1Count++;
			} else {
				player2Count++;
			}
			
			if (pit instanceof Store) {
				assertEquals(0, pit.getNumberOfMarbles());
			} else {			
				assertEquals(4, pit.getNumberOfMarbles());
			}
		}
		
		assertEquals(7, player1Count);
		assertEquals(7, player2Count);
	}
	
	@Test
	public void testNextHollowsFromValidStartingPoint() {
		
		Board board = initialiseBoard(6, 2);
		
		Pit firstValid = board.first(new Predicate<Pit>() {
			public boolean matches(Pit pit) {
				return !(pit instanceof Store);
			}
		});
		
		List<Pit> subsequent = board.getNextPits(firstValid, PlayerNumber.One, 4);
		assertEquals(4, subsequent.size());		
	}
	
	@Test
	public void testNextHollowsIncludesPlayersStore() {
		
		Board board = initialiseBoard(6, 2);
		
		Pit firstPlayable = board.first(new Predicate<Pit>() {
			public boolean matches(Pit item) {
				return item.getPlayerNumber() == PlayerNumber.Two;
			}
		});
		
		assertEquals(Pit.class, firstPlayable.getClass());
		
		List<Pit> subsequent = board.getNextPits(firstPlayable, PlayerNumber.One, board.getNumberOfHollowsPerPlayer() * 2);
		Pit storePit = JLinq.first(subsequent, new Predicate<Pit>() {
			public boolean matches(Pit item) {
				return item instanceof Store && item.getPlayerNumber() == PlayerNumber.One;
			}
		});
		
		Assert.assertNotNull(storePit);
	}
	
	@Test
	public void testNextHollowsIgnoresOtherPlayersStore() {
		
		Board board = initialiseBoard(6, 2);
		
		Pit firstPlayable = board.getPits().get(7);
		List<Pit> subsequent = board.getNextPits(firstPlayable, PlayerNumber.One, 8);
		assertEquals(8, subsequent.size());
		
		for (Pit h : subsequent) {
			assertEquals(Pit.class, h.getClass());
		}		
	}
	
	@Test
	public void testCanGetAdjacentCellForPlayer1() {
		
		Board board = initialiseBoard(6, 4);
		
		Pit player1Pit = board.getPits().get(0);
		Pit adjacentPit = board.getAdjacentPit(player1Pit);
		int adjacentIndex = board.getPits().indexOf(adjacentPit);
		
		Assert.assertEquals(12, adjacentIndex);
	}
	
	@Test
	public void testCanGetAdjacentCellForPlayer2() {
		
		Board board = initialiseBoard(6, 4);
		
		Pit player2Pit = board.getPits().get(10);
		Pit adjacentPit = board.getAdjacentPit(player2Pit);
		int adjacentIndex = board.getPits().indexOf(adjacentPit);
		
		Assert.assertEquals(2, adjacentIndex);
	}
	
	@Test
	public void testPlayerScore() {
		
		Board board = initialiseBoard(4, 4);
		
		List<Pit> pits = board.getPits();
		
		Marble[] slot1 = pits.get(1).removeAll();
		
		pits.get(4).addAll(slot1);
		pits.get(2).removeAll();
		
		// expected = 4 in store, 8 in play
		PlayerScore playerScore = board.getScoreForPlayer(PlayerNumber.One);
		Assert.assertEquals(4, playerScore.getNumberInStore());
		Assert.assertEquals(8, playerScore.getNumberRemaining());
		Assert.assertEquals(12, playerScore.getTotalScore());
	}
	
	private static Board initialiseBoard(int numHollows, int numMarbles) {
		Board board = new BoardImpl();
		
		try {
			board.initialiseBoard(numHollows, 4);
		} catch (MancalaException e) {
			Assert.fail("Unexpected exception: " + e.toString());
		}
		
		return board;
	}
}
