package model;
import controller.Controller;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Board; 1 of 4 main classes
 * board; the board of the game, consisted of 80 total squares 8 of which are "off limits"
 * boardRedPieces and boardBluePieces; all of the pieces on the board for each team
 */

public class Board {
	private int[][] board;
	private ArrayList<Piece> boardRedPieces = new ArrayList<Piece>();
	private ArrayList<Piece> boardBluePieces = new ArrayList<Piece>();
	
	/**
	 * Create the board
	 */
	
	public Board()	{
		board = new int[8][10];
	}
	
	/**
	 * Initialize the pieces on the board
	 */
	
	public void initPieces() {
		int n = 0;
		for(int i = -1; i <= 10; i++) {
			if(i == -1 || i == 1 || i == 9 || i == 10) {
				n = 1;
			} else if (i == 0) {
				n = 6;
			} else if (i == 2) {
				n = 4;
			} else if (i == 3) {
				n = 5;
			} else if (i == 4 || i == 5 || i == 6 || i == 8) {
				n = 2;
			} else if (i == 7) {
				n = 3;
			}
			
			for(int j = 0; j < n; j++) {
				if(i != 0 && i != -1) {
					Piece redPiece = new MoveablePiece();
					Piece bluePiece = new MoveablePiece();
					
					redPiece.setRank(i);
					bluePiece.setRank(i);
					redPiece.setImage("RedPieces/r" + i + ".png");
					bluePiece.setImage("BluePieces/b" + i + ".png");
					
					boardRedPieces.add(redPiece);
					boardBluePieces.add(bluePiece);
				} else {
					Piece redPiece = new ImmovablePiece();
					Piece bluePiece = new ImmovablePiece();
					
					redPiece.setRank(i);
					bluePiece.setRank(i);
					redPiece.setImage("RedPieces/r" + i + ".png");
					bluePiece.setImage("BluePieces/b" + i + ".png");
					
					boardRedPieces.add(redPiece);
					boardBluePieces.add(bluePiece);
				}
			}
		}
		
		Collections.shuffle(boardRedPieces);
		Collections.shuffle(boardBluePieces);
	}
	
	public void initPiecesHalf() {
		int n = 0;
		for(int i = -1; i <= 10; i++) {
			if(i == -1 || i == 1 || i == 4 || i == 5 || i == 6 || i == 8 || i == 9 || i == 10 || i == 7) {
				n = 1;
			} else if (i == 0) {
				n = 3;
			} else if (i == 2 || i == 3) {
				n = 2;
			}
			
			for(int j = 0; j < n; j++) {
				if(i != 0 && i != -1) {
					Piece redPiece = new MoveablePiece();
					Piece bluePiece = new MoveablePiece();
					
					redPiece.setRank(i);
					bluePiece.setRank(i);
					redPiece.setImage("RedPieces/r" + i + ".png");
					bluePiece.setImage("BluePieces/b" + i + ".png");
					
					boardRedPieces.add(redPiece);
					boardBluePieces.add(bluePiece);
				} else {
					Piece redPiece = new ImmovablePiece();
					Piece bluePiece = new ImmovablePiece();
					
					redPiece.setRank(i);
					bluePiece.setRank(i);
					redPiece.setImage("RedPieces/r" + i + ".png");
					bluePiece.setImage("BluePieces/b" + i + ".png");
					
					boardRedPieces.add(redPiece);
					boardBluePieces.add(bluePiece);
				}
			}
		}
		
		Collections.shuffle(boardRedPieces);
		Collections.shuffle(boardBluePieces);
	}
	
	/**
	 * setters and getters of each player's pieces on the board
	 * @param redPieces bluePieces
	 * @return redPieces bluePieces
	 */
	
	public void setBoardRedPieces(ArrayList<Piece> redPieces) { this.boardRedPieces = redPieces; }
	public ArrayList<Piece> getBoardRedPieces() { return boardRedPieces; }
	
	public void setBoardBluePieces(ArrayList<Piece> bluePieces) { this.boardBluePieces = bluePieces; }
	public ArrayList<Piece> getBoardBluePieces() { return boardBluePieces; }
}