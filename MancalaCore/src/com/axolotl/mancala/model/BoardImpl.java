package com.axolotl.mancala.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.axolotl.mancala.JLinq;
import com.axolotl.mancala.MancalaException;
import com.axolotl.mancala.Predicate;
import com.axolotl.mancala.game.PlayerNumber;
import com.axolotl.mancala.game.PlayerScore;

/**
 * An implementation of the mancala board
 */
public class BoardImpl implements Board {

    // The linked list of all hollows on the board, including the pits
    private final LinkedList<Pit> allPits;
    
    // The immutable list of hollows
    private List<Pit> immutableList;

    // The row for player 1
    private final LinkedList<Pit> player1Row;

    // The row for player 2
    private final LinkedList<Pit> player2Row;

    // The store associated with player 1
    private Store player1Store;

    // The store associated with player 2
    private Store player2Store;

    // The number of hollows associated to each player
    private int numberOfHollowsPerPlayer;

    /**
     * Creates a new instance of the Board class
     */
    public BoardImpl() {
        allPits = new LinkedList<Pit>();
        player1Row = new LinkedList<Pit>();
        player2Row = new LinkedList<Pit>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.axolotl.mancala.BoardX#getNumberOfHollowsPerPlayer()
     */
    @Override
    public int getNumberOfHollowsPerPlayer() {
        return numberOfHollowsPerPlayer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.axolotl.mancala.BoardX#first()
     */
    @Override
    public Pit first(Predicate<Pit> predicate) {

        return JLinq.first(allPits, predicate);

    }

    /**
     * Gets the store associated with the given player
     * 
     * @param player
     *            The player to query by
     * @return The associated store
     */
    @Override
    public Store getPlayersStore(PlayerNumber player) {

        return player == PlayerNumber.One ? player1Store : player2Store;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.axolotl.mancala.BoardX#getHollows()
     */
    @Override
    public List<Pit> getPits(Predicate<Pit> predicate) {

        return JLinq.where(allPits, predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.axolotl.mancala.BoardX#getAdjacentHollow()
     */
    @Override
    public Pit getAdjacentPit(Pit pit) {

        final PlayerNumber side = pit.getPlayerNumber();

        List<Pit> row = side == PlayerNumber.One ? player1Row : player2Row;
        List<Pit> adjacentRow = side == PlayerNumber.One ? player2Row : player1Row;

        // get the index of the hollow as it is in the row for the given player
        int hollowIndex = row.indexOf(pit);
        int adjacentIndex = (row.size() - 1) - hollowIndex;

        return adjacentRow.get(adjacentIndex);
    }

	/**
	 * Returns the playable pits for the given player
	 * 
	 * @param player The player
	 * @return The pits associated to this player
	 */
	public List<Pit> getPlayersPits(PlayerNumber player) {
		return player == PlayerNumber.One ? player1Row : player2Row;
	}
	
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.axolotl.mancala.BoardX#getNextHollows(com.axolotl.mancala.Hollow,
     * com.axolotl.mancala.Player, int)
     */
    @Override
    public List<Pit> getNextPits(Pit currentPit, PlayerNumber currentPlayer, int numberOfHollows)  {

        if (currentPit instanceof Store) {
            return new ArrayList<Pit>();
        }

        int hollowIndex = allPits.indexOf(currentPit);

        List<Pit> pits = new ArrayList<Pit>();

        // Set initial index to current hollow position
        int currentIndex = hollowIndex;

        while (pits.size() < numberOfHollows) {

            // move to next hollow
            currentIndex++;

            if (currentIndex >= allPits.size()) {

                // move back to initial hollow
                currentIndex = 0;
            }

            Pit nextPit = allPits.get(currentIndex);

            if (nextPit instanceof Store && nextPit.getPlayerNumber() != currentPlayer) {
                continue;
            }

            pits.add(nextPit);
        }

        return pits;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.axolotl.mancala.BoardX#getScoreForPlayer(com.axolotl.mancala.Player)
     */
    @Override
    public PlayerScore getScoreForPlayer(PlayerNumber player) {

        int openPlayCount = 0;
        int storeCount = 0;

        for (Pit pit : allPits) {

            if (pit.getPlayerNumber() != player)
                continue;

            if (pit instanceof Store) {
                storeCount = pit.getNumberOfMarbles();
            } else {
                openPlayCount += pit.getNumberOfMarbles();
            }

        }

        return new PlayerScore(openPlayCount, storeCount);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.axolotl.mancala.BoardX#initialiseBoard(int, int)
     */
    @Override
    public void initialiseBoard(int numberOfHollowsPerPlayer, int numberOfMarblesPerHollow) {

        if (numberOfHollowsPerPlayer < 1) {
            throw new MancalaException("numberOfHollowsPerPlayer cannot be less than 1");
        }

        if (numberOfMarblesPerHollow < 0) {
            throw new MancalaException("numberOfMarblesPerHollow cannot be negative");
        }

        this.numberOfHollowsPerPlayer = numberOfHollowsPerPlayer;

        player1Row.clear();
        player2Row.clear();
        
        initialiseRow(allPits, PlayerNumber.One, numberOfHollowsPerPlayer, numberOfMarblesPerHollow);

        player1Store = new Store(PlayerNumber.One);
        allPits.add(player1Store);

        initialiseRow(allPits, PlayerNumber.Two, numberOfHollowsPerPlayer, numberOfMarblesPerHollow);

        player2Store = new Store(PlayerNumber.Two);
        allPits.add(player2Store);
        
        immutableList = Collections.unmodifiableList(allPits);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.axolotl.mancala.BoardX#clear()
     */
    @Override
    public void clear() {
        allPits.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.axolotl.mancala.BoardX#getHollows()
     */
    @Override
    public List<Pit> getPits() {
        return immutableList;
    }

    /**
     * Adds a row of playable hollows to the collector, and initialises each
     * hollow with the given number of marbles
     * 
     * @param collector
     *            The collecting parameter to add the hollows to
     * 
     * @param player
     *            The player associated to the created hollows
     * 
     * @param numberOfHollowsPerPlayer
     *            The number of hollows to create
     * 
     * @param numberOfMarblesPerHollow
     *            The number of marbles to add to the hollows
     */
    private void initialiseRow(List<Pit> collector, PlayerNumber player, int numberOfHollowsPerPlayer, int numberOfMarblesPerHollow) {

        MarbleColour[] colours = MarbleColour.values();
        
        for (int index = 0; index < numberOfHollowsPerPlayer; index++) {

            // Add a hollow to the top and bottom row
            Pit pit = new Pit(player);
            collector.add(pit);

            // Add the hollow to the player list for quick lookups later on
            if (player == PlayerNumber.One) {
                player1Row.add(pit);
            } else {
                player2Row.add(pit);
            }

            // Add the initial marbles to each of the hollows
            for (int marbleIndex = 0; marbleIndex < numberOfMarblesPerHollow; marbleIndex++) {

                pit.add(new Marble(colours[marbleIndex % colours.length]));
            }
        }
    }
}
