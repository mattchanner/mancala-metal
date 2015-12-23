package com.axolotl.mancala.mocks;

import java.util.List;

import com.axolotl.mancala.game.FinalScore;
import com.axolotl.mancala.game.GamePublisher;
import com.axolotl.mancala.game.PlayerNumber;
import com.axolotl.mancala.model.Pit;

public class CountingGamePublisher implements GamePublisher{

	private int boardChangedCallCount;
	private int gameCompleteCallCount;
	private int playerChangedCallCount;
	private int playerHasAnotherTurnCallCount;
	private int captureCallCount;
	
	/**
	 * @return the boardChangedCallCount
	 */
	public int getBoardChangedCallCount() {
		return boardChangedCallCount;
	}

	/**
	 * @return the gameCompleteCallCount
	 */
	public int getGameCompleteCallCount() {
		return gameCompleteCallCount;
	}
	
	/**
	 * @return the playerChangedCallCount
	 */
	public int getPlayerChangedCallCount() {
		return playerChangedCallCount;
	}

	/**
	 * @return the playerHasAnotherTurnCallCount
	 */
	public int getPlayerHasAnotherTurnCallCount() {
		return playerHasAnotherTurnCallCount;
	}
	
	/**
	 * @return the capture call count
	 */
	public int getCaptureCallCount() {
		return captureCallCount;
	}
	
	@Override
	public void notifyBoardChanged(List<Pit> changedPits) {
		boardChangedCallCount++;
	}

	@Override
	public void notifyGameComplete(FinalScore score) {
		gameCompleteCallCount++;
	}

	@Override
	public void notifyPlayerChanged(PlayerNumber newPlayer) {
		playerChangedCallCount++;
	}

	@Override
	public void notifyPlayerHasAnotherTurn() {
		playerHasAnotherTurnCallCount++;
	}
	
	@Override
	public void notifyCapture(Pit capturedPit) {
		captureCallCount++;		
	}
	
	public void reset() {
		boardChangedCallCount = 0;
		gameCompleteCallCount = 0;
		playerChangedCallCount = 0;
		playerHasAnotherTurnCallCount = 0;
		captureCallCount = 0;		
	}

	@Override
	public void notifyNewGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyGameRestored() {
		// TODO Auto-generated method stub
		
	}
}
