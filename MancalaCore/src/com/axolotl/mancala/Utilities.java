package com.axolotl.mancala;

import java.util.List;

import com.axolotl.mancala.game.FinalScore;
import com.axolotl.mancala.game.PlayerNumber;
import com.axolotl.mancala.model.Board;
import com.axolotl.mancala.model.Pit;
import com.axolotl.mancala.model.Store;

/**
 * Utility class used for constructing string representations of the model
 */
public class Utilities {

    /**
     * Creates a string representation of given board
     * @param board The board to stringify
     * @return The string form of the board
     */
	public static String printBoard(Board board) {
		
		StringBuilder builder = new StringBuilder();
		
		List<Pit> player1 = board.getPits(new Predicate<Pit>() {
			public boolean matches(Pit item) {
				return !(item instanceof Store) && item.getPlayerNumber() == PlayerNumber.One;
			}
		});
		
		builder.append("-----------------\n");
		builder.append("-               -\n");
		builder.append("-      2(" + board.getPlayersStore(PlayerNumber.Two).getNumberOfMarbles() + ")     -\n");
		builder.append("-               -\n");
		
		for (Pit h : player1) {
			
			Pit adj = board.getAdjacentPit(h);
			
			builder.append("-----------------\n");
			builder.append("-       |       -\n");
			builder.append("-   " + h.getNumberOfMarbles() + "   |   " + adj.getNumberOfMarbles() + "   -\n");
			builder.append("-       |       -\n");						
		}
		
		builder.append("-----------------\n");
		builder.append("-               -\n");
		builder.append("-      1(" + board.getPlayersStore(PlayerNumber.One).getNumberOfMarbles() + ")     -\n");
		builder.append("-               -\n");
		builder.append("-----------------\n");
		
		return builder.toString();
	}

	public static String printFinalScore(FinalScore score) {

		StringBuilder builder = new StringBuilder();
		
		builder.append("\n******************************\n");
		builder.append("*       Final Score\n");
		builder.append("******************************\n");
		builder.append("* Winner = " + score.getResult().toString() + "\n");
		builder.append("******************************\n");
		builder.append("Player 1's Score:\n");
		builder.append("# Open Play:     " + score.getScore(PlayerNumber.One).getNumberRemaining() + "\n");
		builder.append("# In Store :     " + score.getScore(PlayerNumber.One).getNumberInStore() + "\n");
		builder.append("Total      :     " + score.getScore(PlayerNumber.One).getTotalScore() + "\n");
		builder.append("******************************\n");
		builder.append("Player 2's Score:\n");
		builder.append("# Open Play:     " + score.getScore(PlayerNumber.Two).getNumberRemaining() + "\n");
		builder.append("# In Store :     " + score.getScore(PlayerNumber.Two).getNumberInStore() + "\n");
		builder.append("Total      :     " + score.getScore(PlayerNumber.Two).getTotalScore() + "\n");		return builder.toString();
		
	}
	
}
