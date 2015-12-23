package com.axolotl.mancala.model;

import junit.framework.Assert;

import org.junit.Test;

import com.axolotl.mancala.game.PlayerNumber;
import com.axolotl.mancala.model.Pit;
import com.axolotl.mancala.model.Marble;
import com.axolotl.mancala.model.MarbleColour;

public class HollowTests {

	@Test
	public void testHollowConstructor() {
		Pit pit = new Pit(PlayerNumber.Two);
		Assert.assertEquals(PlayerNumber.Two, pit.getPlayerNumber());
	}	
	
	@Test
	public void testMarbleCount() {
		Pit pit = new Pit(PlayerNumber.One);
		for (int index = 0; index < 5; index++) {
			pit.add(new Marble(MarbleColour.Blue));
		}
		Assert.assertEquals(5, pit.getNumberOfMarbles());
	}
	
	@Test
	public void testMarbleRemoval() {
		Pit pit = new Pit(PlayerNumber.One);
		for (int index = 0; index < 5; index++) {
			pit.add(new Marble(MarbleColour.Blue));
		}
		Marble[] marbles = pit.removeAll();
		Assert.assertEquals(5, marbles.length);
		Assert.assertEquals(0, pit.getNumberOfMarbles());
	}
}
