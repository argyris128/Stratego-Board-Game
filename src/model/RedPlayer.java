package model;
import java.util.ArrayList;

/**
 * Subclass of Player
 * redPieces; the pieces of red player
 */

public class RedPlayer extends Player {
	private ArrayList<Piece> redPieces;
	static boolean redTurn;
	
	/**
	 * Create red player
	 */
	
	public RedPlayer() {
		redPieces = new ArrayList<Piece>();
		redTurn = true;
	}
	
	public ArrayList<Piece> getRedPieces() { return redPieces; }
	public ArrayList<Piece> getBluePieces() { return null; }
    
    /**
     * Change turn
     */
    
    public void changeTurn() {
    	redTurn = !redTurn;
    	BluePlayer.blueTurn = !BluePlayer.blueTurn;
    }
    
    /**
     * @return redTurn
     */
    
    public boolean getTurn() { return redTurn; }
}