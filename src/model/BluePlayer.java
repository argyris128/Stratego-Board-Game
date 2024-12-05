package model;
import java.util.ArrayList;

/**
 * Subclass of Player
 * bluePieces; the pieces of blue player
 */

public class BluePlayer extends Player {
	private ArrayList<Piece> bluePieces;
	static boolean blueTurn;
	
	public BluePlayer() {
		bluePieces = new ArrayList<Piece>();
		blueTurn = false;
	}
	
	public ArrayList<Piece> getBluePieces() { return bluePieces; }
	public ArrayList<Piece> getRedPieces() { return null; }
    
    /**
     * Change turn
     */
    
    public void changeTurn() {
    	blueTurn = !blueTurn;
    	RedPlayer.redTurn = !RedPlayer.redTurn;
    }
    
    /**
     * @return blueTurn
     */
    
    public boolean getTurn() { return blueTurn; }
}