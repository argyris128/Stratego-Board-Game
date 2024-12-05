package model;

import java.util.ArrayList;

/**
 * Player; 1 of 4 main classes
 * 
 */

public abstract class Player {	
	private boolean hasLost = false;
	
	/**
	 * Check if player has lost the game
	 * @return true if yes; otherwise no
	 */
	
	public boolean isLost() {
		return hasLost;
	}
	
	/**
	 * Player has lost
	 */
	
	public void hasLost() {
		hasLost = true;;
	}
	
	/**
	 * Changes the turn
	 */
	
	public abstract void changeTurn();
	public abstract boolean getTurn();
	
	public abstract ArrayList<Piece> getRedPieces();
	public abstract ArrayList<Piece> getBluePieces();

}