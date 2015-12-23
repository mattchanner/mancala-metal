package com.axolotl.mancala.model;

import junit.framework.Assert;

import org.junit.Test;

public class MarbleTests {

	@Test
	public void testMarbleColourGetter() {
		Marble m = new Marble(MarbleColour.Yellow);
		Assert.assertEquals(MarbleColour.Yellow, m.getColour());
	}
	
}
