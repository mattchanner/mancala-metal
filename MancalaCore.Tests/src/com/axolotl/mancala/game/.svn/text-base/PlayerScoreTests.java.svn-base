package com.axolotl.mancala.game;

import junit.framework.Assert;
import org.junit.Test;

public class PlayerScoreTests {

	@Test
	public void testPlayerScoreTotal() {
		PlayerScore score = new PlayerScore(10, 5);
		Assert.assertEquals(15, score.getTotalScore());		
	}
	
	@Test
	public void testPlayerScoreStoreGetter() {
		PlayerScore score = new PlayerScore(10, 5);
		Assert.assertEquals(5, score.getNumberInStore());
	}
	
	@Test
	public void testPlayerScoreRemainingGetter() {
		PlayerScore score = new PlayerScore(10, 5);
		Assert.assertEquals(10, score.getNumberRemaining());
	}
}
