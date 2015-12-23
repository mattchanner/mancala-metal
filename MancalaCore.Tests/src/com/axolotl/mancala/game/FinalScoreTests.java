package com.axolotl.mancala.game;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.axolotl.mancala.MancalaException;
import com.axolotl.mancala.model.Board;
import com.axolotl.mancala.model.BoardImpl;
import com.axolotl.mancala.model.Pit;
import com.axolotl.mancala.model.Store;

public class FinalScoreTests {

    @Test
    public void testFinalScoreWinnerInference() {

        PlayerScore player1Score = new PlayerScore(5, 10);
        PlayerScore player2Score = new PlayerScore(0, 8);

        FinalScore score = new FinalScore(player1Score, player2Score);

        Assert.assertEquals(GameResult.Player1Wins, score.getResult());
    }

    @Test
    public void testFinalScoreDrawInference() {

        PlayerScore player1Score = new PlayerScore(5, 10);
        PlayerScore player2Score = new PlayerScore(10, 5);

        FinalScore score = new FinalScore(player1Score, player2Score);

        Assert.assertEquals(GameResult.Draw, score.getResult());
    }

    @Test
    public void testFinalScoreGeneratedFromBoard() {

        Board board = new BoardImpl();

        try {
            board.initialiseBoard(4, 4);
        } catch (MancalaException e) {
            Assert.fail("Unexpected exception: " + e.toString());
        }

        List<Pit> pits = board.getPits();

        Store player1Store = board.getPlayersStore(PlayerNumber.One);
        player1Store.addAll(pits.get(0).removeAll());
        player1Store.addAll(pits.get(1).removeAll());

        Store player2Store = board.getPlayersStore(PlayerNumber.Two);
        player2Store.addAll(pits.get(5).removeAll());
        player2Store.addAll(pits.get(6).removeAll());

        FinalScore score = new FinalScore(board.getScoreForPlayer(PlayerNumber.One), board.getScoreForPlayer(PlayerNumber.Two));
        Assert.assertEquals(GameResult.Draw, score.getResult());
        
        Assert.assertEquals(8, score.getScore(PlayerNumber.One).getNumberInStore());
        Assert.assertEquals(8, score.getScore(PlayerNumber.One).getNumberRemaining());
        Assert.assertEquals(16, score.getScore(PlayerNumber.One).getTotalScore());
    }
}
