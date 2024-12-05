package controller;
import model.Piece;
import model.ImmovablePiece;
import model.InvalidMoveException;
import model.MoveablePiece;
import java.net.URL;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

import model.BluePlayer;
import model.Board;
import model.Player;
import model.RedPlayer;
import view.View;

/**
 * Controller; 1 of 4 main classes
 */

public class Controller {
	Board board;
	View view;
	static Player redPlayer;
	static Player bluePlayer;	
	private ClassLoader cldr;
	private static boolean gameStarted = false, gameFinished = false;	
	Border moveBorder = BorderFactory.createMatteBorder(4, 4, 4, 4, Color.GREEN);
	private int oldRow = 0; 
	private int oldCol = 0;
	private JButton oldSquare = new JButton();
	private static int attackResult = -1;
	private int recRowRed = -1, recColRed = -1, recRowBlue = -1, recColBlue = -1;
	private static int totalRecRed = 2, totalRecBlue = 2;
	private static boolean check1State = false;
	private boolean check2State = false;
	private int stateChanged = 0;
	
	
	/**
	 * Initialize the game
	 */
	
	public void initialize() {
		board = new Board();
		view = new View();
		redPlayer = new RedPlayer();
		bluePlayer = new BluePlayer();
		cldr = this.getClass().getClassLoader();
		view.setVisible(true);
		gameState();
	}
	
	public void gameState() {
		stateChanged++;
		board.getBoardRedPieces().clear();
		board.getBoardBluePieces().clear();
		
		if(check1State == false) {
			board.initPieces();	
		} else {
			board.initPiecesHalf();		
		}		
		for(int i = 0; i < board.getBoardBluePieces().size(); i++) {
			redPlayer.getRedPieces().add(i, board.getBoardRedPieces().get(i));
			bluePlayer.getBluePieces().add(i, board.getBoardBluePieces().get(i));
		}
		if(stateChanged == 1) {
			view.initBoard(redPlayer.getRedPieces(), bluePlayer.getBluePieces());
			setListeners();
		} else
			view.initPiecesOnBoard(redPlayer.getRedPieces(), bluePlayer.getBluePieces());
	}
	
	/**
	 * Checks if game is finished
	 * @return true if game is finished; otherwise false
	 */
	
	public static boolean isGameFinished() {
		if(redPlayer.isLost() || bluePlayer.isLost()) {
			gameFinished = true;
			return true;
		}
		else
			return false;
	}
	
	public static void main(String[] args) {
		Controller c = new Controller();
		c.initialize();
	}
	
	public void setListeners() {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 10; j++) {	
				view.getBoard()[i][j].addMouseListener(new PieceListener());
			}
		}
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 4; j++) {	
				view.getRedCaptured()[i][j].addMouseListener(new CapturedPieceListener());
				view.getBlueCaptured()[i][j].addMouseListener(new CapturedPieceListener());
			}
		}
		view.getCheckBox1().addMouseListener(new CheckboxListener());
		view.getCheckBox2().addMouseListener(new CheckboxListener());
	}	
	
	/**
	 * @param piece row col
	 */
	
	public void movePiece(JButton piece, int row, int col) {
		for(int i = 0; i < view.getRedPieces().size(); i++) {
			if(piece.getIcon().equals(view.getRedPieces().get(i).getIcon())) {
				if(!(redPlayer.getRedPieces().get(i) instanceof ImmovablePiece)) {
					if(redPlayer.getRedPieces().get(i).getRank() != 2) {		
						removeBorders();
						if(check2State == false)
							createBorders(piece, row, col);
						else
							createBorders2(piece, row, col);
					} else {
						removeBorders();
						if(check2State == false)
							createBordersScout(piece, row, col);
						else
							createBordersScout2(piece, row, col);
					}
				}
			}
		}
		for(int i = 0; i < view.getBluePieces().size(); i++) {
			if(piece.getIcon().equals(view.getBluePieces().get(i).getIcon())) {
				if(!(bluePlayer.getBluePieces().get(i) instanceof ImmovablePiece)) {
					if(bluePlayer.getBluePieces().get(i).getRank() != 2) {		
						removeBorders();
						if(check2State == false)
							createBorders(piece, row, col);
						else
							createBorders2(piece, row, col);
					} else {
						removeBorders();
						if(check2State == false)
							createBordersScout(piece, row, col);
						else
							createBordersScout2(piece, row, col);
					}
				}
			}
		}
	}
	
	/**
	 * Attacker attacks defender
	 * @param attackerRank defenderRank
	 */
	
	public static void attack(int attackerRank, int defenderRank) {
		if(defenderRank == 0 && attackerRank == 3)
			attackResult = 1;
		else if(defenderRank == 0 && attackerRank != 3)
			attackResult = 2;
		else if(defenderRank == 10 && attackerRank == 1)
			attackResult = 1;
		else if(defenderRank == -1) {
			attackResult = 1;
			gameFinished = true;
		} else if(defenderRank == attackerRank)
			attackResult = 0;
		else {
			if(attackerRank > defenderRank)
				attackResult = 1;
			else
				attackResult = 2;
		}
	}
	
	/**
	 * @return attackResult (0 if same rank attack; 1 if attacker wins; 2 if attacker loses)
	 */
	
	public static int getAttackResult() {
		return attackResult;
	}
	
	/**
	 * Bring a piece back from captured pieces
	 * @param recRow recCol
	 */
	
	public void recoverPiece(int recRowRed, int recColRed, int recRowBlue, int recColBlue, int row, int col) {
		int rankRed = recColRed+(recRowRed*4)-1;
		int rankBlue = recColBlue+(recRowBlue*4)-1;
		if(recRowRed != -1 && recColRed != -1) {
			if(totalRecRed > 0) {
				totalRecRed--;
				view.getRedCapturedInt()[recRowRed][recColRed]--;
				view.minusRedRec();
				for(int i = 0; i < 29; i++) {
					if(redPlayer.getRedPieces().get(i).getRank() == rankRed) {
						view.getBoard()[row][col].setIcon(view.getRedPieces().get(i).getIcon());
					}
				}
			} else {
				removeRecBordersRed();
			}
		} else {
			if(totalRecBlue > 0) {
				totalRecBlue--;
				view.getBlueCapturedInt()[recRowBlue][recColBlue]--;
				view.minusBlueRec();
				for(int i = 0; i < 29; i++) {
					if(bluePlayer.getBluePieces().get(i).getRank() == rankBlue) {
						view.getBoard()[row][col].setIcon(view.getBluePieces().get(i).getIcon());
					}
				}
			} else {
				removeRecBordersBlue();
			}
		}
		view.UpdateMenu();
	}
	
	private class PieceListener implements MouseListener {
		@Override
		public void mouseReleased(MouseEvent e) {
			JButton be = (JButton) e.getSource();

			for(int i = 0; i < 8; i++) {
				for(int j = 0; j < 10; j++) {	
					if(be.equals(view.getBoard()[i][j])) {		
						if(view.getBoard()[i][j].getBorder().equals(view.getRecBorder())) {	
							recoverPiece(recRowRed, recColRed, recRowBlue, recColBlue, i, j);
						}
						
						if(view.getBoard()[i][j].getBorder().equals(moveBorder)) {									
							view.updatePiece(i, j, oldRow, oldCol);
							gameStarted = true;
							break;
						}
						
						if(view.getBoard()[i][j].getIcon() != null) {
							movePiece(view.getBoard()[i][j], i, j);
							oldSquare = view.getBoard()[i][j];
							oldRow = i;
							oldCol = j;
						}					
					}
				}
			}
			
			if(gameStarted == true) {
				view.getCheckBox1().setEnabled(false);
				view.getCheckBox2().setEnabled(false);
			}

		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}
	
	private class CapturedPieceListener implements MouseListener {
		@Override
		public void mouseReleased(MouseEvent e) {
			JButton be = (JButton) e.getSource();

			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 4; j++) {	
					if(be.equals(view.getRedCaptured()[i][j])) {	
						removeBorders();
						if(view.getRedCaptured()[i][j].getBorder().equals(view.getRecBorder())) {
							recRowRed = i;
							recColRed = j;
							recRowBlue = -1;
							recColBlue = -1;
							for(int k = 5; k < 8; k++) {
								for(int l = 0; l < 10; l++) {
									if(view.getBoard()[k][l].getIcon() == null) {
										view.getBoard()[k][l].setBorder(view.getRecBorder());
									}
								}
							}
						} else
							removeBorders();
					}
					if(be.equals(view.getBlueCaptured()[i][j])) {	
						removeBorders();
						if(view.getBlueCaptured()[i][j].getBorder().equals(view.getRecBorder())) {
							recRowBlue = i;
							recColBlue = j;
							recRowRed = -1;
							recColRed = -1;
							for(int k = 0; k < 3; k++) {
								for(int l = 0; l < 10; l++) {
									if(view.getBoard()[k][l].getIcon() == null) {
										view.getBoard()[k][l].setBorder(view.getRecBorder());
									}
								}
							}
						} else
							removeBorders();
					}
				}
			}
		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}
	
	private class CheckboxListener implements MouseListener {
		@Override
		public void mouseReleased(MouseEvent e) {
			JCheckBox cb = (JCheckBox) e.getSource();
			
			if(cb == view.getCheckBox1()) {
				if(view.getCheckBox1().isEnabled()) {
					removeBorders();
					check1State = view.getCheckBox1().isSelected();
					gameState();
				}
			}
			
			if(cb == view.getCheckBox2()) {
				if(view.getCheckBox2().isEnabled()) {
					removeBorders();
					check2State = view.getCheckBox2().isSelected();
				}
			}
		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}
	
	/**
	 * Useful getters
	 * @return redPlayer bluePlayer totalRecRed totalRecBlue
	 */
	
	public static Player getRedPlayer() { return redPlayer; }
	public static Player getBluePlayer() { return bluePlayer; }
	public static int getTotalRecRed() { return totalRecRed; }
	public static int getTotalRecBlue() { return totalRecBlue; }
	public static boolean getCheck1State() { return check1State; }
	
	/**
	 * Resets all borders
	 */
	
	public void removeBorders() {
		for(int j = 0; j < 8; j++) {
			for(int k = 0; k < 10; k++) {	
				if(view.getBoard()[j][k] != null) {
					view.getBoard()[j][k].setBorder(UIManager.getBorder("Button.border"));	
				}
			}	
		}
	}
	
	/**
	 * Resets the yellow recover borders
	 */
	
	public void removeRecBordersRed() {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 4; j++) {
				view.getRedCaptured()[i][j].setBorder(UIManager.getBorder("Button.border"));
			}
		}
	}
	public void removeRecBordersBlue() {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 4; j++) {
				view.getBlueCaptured()[i][j].setBorder(UIManager.getBorder("Button.border"));
			}
		}
	}
	
	/**
	 * Creates the valid move borders for the next move
	 * @param piece row col
	 * team = 0 = red team
	 */
	
	public void createBorders(JButton piece, int row, int col) {
		int team = 0;
		for(int i = 0; i < view.getBluePieces().size(); i++) {
			if(piece.getIcon().equals(view.getBluePieces().get(i).getIcon())) {
				team = 1;
			}
		}
		
		if(row == 0 && col == 0) {		
			if(team == 0) {
				if(view.getBoard()[0][1].getIcon() == null || view.getBoard()[0][1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[0][1].setBorder(moveBorder);
				if(view.getBoard()[1][0].getIcon() == null || view.getBoard()[1][9].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[1][0].setBorder(moveBorder);
			} else {
				if(view.getBoard()[0][1].getIcon() == null || view.getBoard()[0][1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[0][1].setBorder(moveBorder);
				if(view.getBoard()[1][0].getIcon() == null || view.getBoard()[1][0].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[1][0].setBorder(moveBorder);
			}
		} else if (row == 7 && col == 9) {
			if(team == 0) {
				if(view.getBoard()[6][9].getIcon() == null || view.getBoard()[6][9].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[6][9].setBorder(moveBorder);
				if(view.getBoard()[7][8].getIcon() == null || view.getBoard()[7][8].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[7][8].setBorder(moveBorder);
			} else {
				if(view.getBoard()[6][9].getIcon() == null || view.getBoard()[6][9].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[6][9].setBorder(moveBorder);
				if(view.getBoard()[7][8].getIcon() == null || view.getBoard()[7][8].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[7][8].setBorder(moveBorder);
			}
		} else if (row == 7 && col == 0) {
			if(team == 0) {
				if(view.getBoard()[7][1].getIcon() == null || view.getBoard()[7][1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[7][1].setBorder(moveBorder);
				if(view.getBoard()[6][0].getIcon() == null || view.getBoard()[6][1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[6][0].setBorder(moveBorder);
			} else {
				if(view.getBoard()[7][1].getIcon() == null || view.getBoard()[7][1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[7][1].setBorder(moveBorder);
				if(view.getBoard()[6][0].getIcon() == null || view.getBoard()[6][1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[6][0].setBorder(moveBorder);
			}
		} else if (row == 0 && col == 9) {
			if(team == 0) {
				if(view.getBoard()[0][8].getIcon() == null || view.getBoard()[0][8].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[0][8].setBorder(moveBorder);
				if(view.getBoard()[1][9].getIcon() == null || view.getBoard()[1][9].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[1][9].setBorder(moveBorder);
			} else {
				if(view.getBoard()[0][8].getIcon() == null || view.getBoard()[0][8].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[0][8].setBorder(moveBorder);
				if(view.getBoard()[1][9].getIcon() == null || view.getBoard()[1][9].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[1][9].setBorder(moveBorder);
			}
		} else if (row == 0) {
			if(team == 0) {
				if(view.getBoard()[0][col+1].getIcon() == null || view.getBoard()[0][col+1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[0][col+1].setBorder(moveBorder);
				if(view.getBoard()[0][col-1].getIcon() == null || view.getBoard()[0][col-1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[0][col-1].setBorder(moveBorder);
				if(view.getBoard()[1][col].getIcon() == null || view.getBoard()[1][col].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[1][col].setBorder(moveBorder);
			} else {
				if(view.getBoard()[0][col+1].getIcon() == null || view.getBoard()[0][col+1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[0][col+1].setBorder(moveBorder);
				if(view.getBoard()[0][col-1].getIcon() == null || view.getBoard()[0][col-1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[0][col-1].setBorder(moveBorder);
				if(view.getBoard()[1][col].getIcon() == null || view.getBoard()[1][col].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[1][col].setBorder(moveBorder);
			}
		} else if (row == 7) {
			if(team == 0) {
				if(view.getBoard()[7][col+1].getIcon() == null || view.getBoard()[7][col+1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[7][col+1].setBorder(moveBorder);
				if(view.getBoard()[7][col-1].getIcon() == null || view.getBoard()[7][col-1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[7][col-1].setBorder(moveBorder);
				if(view.getBoard()[6][col].getIcon() == null || view.getBoard()[6][col].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[6][col].setBorder(moveBorder);
			} else {
				if(view.getBoard()[7][col+1].getIcon() == null || view.getBoard()[7][col+1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[7][col+1].setBorder(moveBorder);
				if(view.getBoard()[7][col-1].getIcon() == null || view.getBoard()[7][col-1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[7][col-1].setBorder(moveBorder);
				if(view.getBoard()[6][col].getIcon() == null || view.getBoard()[6][col].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[6][col].setBorder(moveBorder);
			}
		} else if (col == 0) {
			if(team == 0) {
				if(view.getBoard()[row+1][0].getIcon() == null || view.getBoard()[row+1][0].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row+1][0].setBorder(moveBorder);
				if(view.getBoard()[row-1][0].getIcon() == null || view.getBoard()[row-1][0].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row-1][0].setBorder(moveBorder);
				if(view.getBoard()[row][1].getIcon() == null || view.getBoard()[row][1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row][1].setBorder(moveBorder);
			} else {
				if(view.getBoard()[row+1][0].getIcon() == null || view.getBoard()[row+1][0].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row+1][0].setBorder(moveBorder);
				if(view.getBoard()[row-1][0].getIcon() == null || view.getBoard()[row-1][0].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row-1][0].setBorder(moveBorder);
				if(view.getBoard()[row][1].getIcon() == null || view.getBoard()[row][1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row][1].setBorder(moveBorder);
			}
		
		} else if (col == 9) {
			if(team == 0) {
				if(view.getBoard()[row+1][9].getIcon() == null || view.getBoard()[row+1][9].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row+1][9].setBorder(moveBorder);
				if(view.getBoard()[row-1][9].getIcon() == null || view.getBoard()[row-1][9].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row-1][9].setBorder(moveBorder);
				if(view.getBoard()[row][8].getIcon() == null || view.getBoard()[row][8].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row][8].setBorder(moveBorder);
			} else {
				if(view.getBoard()[row+1][9].getIcon() == null || view.getBoard()[row+1][9].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row+1][9].setBorder(moveBorder);
				if(view.getBoard()[row-1][9].getIcon() == null || view.getBoard()[row-1][9].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row-1][9].setBorder(moveBorder);
				if(view.getBoard()[row][8].getIcon() == null || view.getBoard()[row][8].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row][8].setBorder(moveBorder);
			}
		} else {
			if(team == 0) {
				if(view.getBoard()[row+1][col].getIcon() == null || view.getBoard()[row+1][col].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row+1][col].setBorder(moveBorder);
				if(view.getBoard()[row-1][col].getIcon() == null || view.getBoard()[row-1][col].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row-1][col].setBorder(moveBorder);
				if(view.getBoard()[row][col-1].getIcon() == null || view.getBoard()[row][col-1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row][col-1].setBorder(moveBorder);
				if(view.getBoard()[row][col+1].getIcon() == null || view.getBoard()[row][col+1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row][col+1].setBorder(moveBorder);
			} else {
				if(view.getBoard()[row+1][col].getIcon() == null || view.getBoard()[row+1][col].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row+1][col].setBorder(moveBorder);
				if(view.getBoard()[row-1][col].getIcon() == null || view.getBoard()[row-1][col].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row-1][col].setBorder(moveBorder);
				if(view.getBoard()[row][col-1].getIcon() == null || view.getBoard()[row][col-1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row][col-1].setBorder(moveBorder);
				if(view.getBoard()[row][col+1].getIcon() == null || view.getBoard()[row][col+1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row][col+1].setBorder(moveBorder);
			}
		}
	}
	
	/**
	 * Create the valid move borders for the scout
	 * @param piece row col
	 * team = 0 = red team
	 */
	
	public void createBordersScout(JButton piece, int row, int col) {
		int team = 0;
		for(int i = 0; i < view.getBluePieces().size(); i++) {
			if(piece.getIcon().equals(view.getBluePieces().get(i).getIcon())) {
				team = 1;
			}
		}		
		int indexUp = 1, indexDown = 1, indexLeft = 1, indexRight = 1;		
		
		if(team == 0) {
			if((row == 6 && row+indexDown <=7) || (row+indexDown <= 6)) {
				while(view.getBoard()[row+indexDown][col].getIcon() == null || view.getBoard()[row+indexDown][col].getIcon().equals(view.getBlueHidden().getIcon())) {
					if(view.getBoard()[row+indexDown][col].getIcon() != null) {
						if(view.getBoard()[row+indexDown][col].getIcon().equals(view.getBlueHidden().getIcon())) {
							view.getBoard()[row+indexDown][col].setBorder(moveBorder);
							break;
						}
					}
					if(view.getBoard()[row+indexDown][col].getName().equals("yellowSquare"))
						break;
					view.getBoard()[row+indexDown][col].setBorder(moveBorder);	
					if(row+indexDown <= 6)
						indexDown++;
					else
						break;
				}
			}
			if((row == 1 && row-indexUp >= 0) || (row-indexUp >= 1)) {
				while(view.getBoard()[row-indexUp][col].getIcon() == null || view.getBoard()[row-indexUp][col].getIcon().equals(view.getBlueHidden().getIcon())) {
					if(view.getBoard()[row-indexUp][col].getIcon() != null) {
						if(view.getBoard()[row-indexUp][col].getIcon().equals(view.getBlueHidden().getIcon())) {
							view.getBoard()[row-indexUp][col].setBorder(moveBorder);
							break;
						}
					}
					if(view.getBoard()[row-indexUp][col].getName().equals("yellowSquare"))
						break;
					view.getBoard()[row-indexUp][col].setBorder(moveBorder);	
					if(row-indexUp >= 1)
						indexUp++;
					else
						break;
				}
			}
			if((col == 1 && col-indexLeft >= 0) || (col-indexLeft >= 1)) {
				while(view.getBoard()[row][col-indexLeft].getIcon() == null || view.getBoard()[row][col-indexLeft].getIcon().equals(view.getBlueHidden().getIcon())) {
					if(view.getBoard()[row][col-indexLeft].getIcon() != null) {
						if(view.getBoard()[row][col-indexLeft].getIcon().equals(view.getBlueHidden().getIcon())) {
							view.getBoard()[row][col-indexLeft].setBorder(moveBorder);
							break;
						}
					}
					if(view.getBoard()[row][col-indexLeft].getName().equals("yellowSquare"))
						break;
					view.getBoard()[row][col-indexLeft].setBorder(moveBorder);	
					if(col-indexLeft >= 1)
						indexLeft++;
					else
						break;
				}
			}
			if((col == 8 && col+indexRight <= 9) || (col+indexRight <= 8)) {
				while(view.getBoard()[row][col+indexRight].getIcon() == null || view.getBoard()[row][col+indexRight].getIcon().equals(view.getBlueHidden().getIcon())) {
					if(view.getBoard()[row][col+indexRight].getIcon() != null) {
						if(view.getBoard()[row][col+indexRight].getIcon().equals(view.getBlueHidden().getIcon())) {
							view.getBoard()[row][col+indexRight].setBorder(moveBorder);
							break;
						}
					}
					if(view.getBoard()[row][col+indexRight].getName().equals("yellowSquare"))
						break;
					view.getBoard()[row][col+indexRight].setBorder(moveBorder);	
					if(col+indexRight <= 8)
						indexRight++;
					else
						break;
				}
			}
		} else {
			if((row == 6 && row+indexDown <=7) || (row+indexDown <= 6)) {
				while(view.getBoard()[row+indexDown][col].getIcon() == null || view.getBoard()[row+indexDown][col].getIcon().equals(view.getRedHidden().getIcon())) {
					if(view.getBoard()[row+indexDown][col].getIcon() != null) {
						if(view.getBoard()[row+indexDown][col].getIcon().equals(view.getRedHidden().getIcon())) {
							view.getBoard()[row+indexDown][col].setBorder(moveBorder);
							break;
						}
					}
					if(view.getBoard()[row+indexDown][col].getName().equals("yellowSquare"))
						break;
					view.getBoard()[row+indexDown][col].setBorder(moveBorder);	
					if(row+indexDown <= 6)
						indexDown++;
					else
						break;
				}
			}
			if((row == 1 && row-indexUp >= 0) || (row-indexUp >= 1)) {
				while(view.getBoard()[row-indexUp][col].getIcon() == null || view.getBoard()[row-indexUp][col].getIcon().equals(view.getRedHidden().getIcon())) {
					if(view.getBoard()[row-indexUp][col].getIcon() != null) {
						if(view.getBoard()[row-indexUp][col].getIcon().equals(view.getRedHidden().getIcon())) {
							view.getBoard()[row-indexUp][col].setBorder(moveBorder);
							break;
						}
					}
					if(view.getBoard()[row-indexUp][col].getName().equals("yellowSquare"))
						break;
					view.getBoard()[row-indexUp][col].setBorder(moveBorder);	
					if(row-indexUp >= 1)
						indexUp++;
					else
						break;
				}
			}
			if((col == 1 && col-indexLeft >= 0) || (col-indexLeft >= 1)) {
				while(view.getBoard()[row][col-indexLeft].getIcon() == null || view.getBoard()[row][col-indexLeft].getIcon().equals(view.getRedHidden().getIcon())) {
					if(view.getBoard()[row][col-indexLeft].getIcon() != null) {
						if(view.getBoard()[row][col-indexLeft].getIcon().equals(view.getRedHidden().getIcon())) {
							view.getBoard()[row][col-indexLeft].setBorder(moveBorder);
							break;
						}
					}
					if(view.getBoard()[row][col-indexLeft].getName().equals("yellowSquare"))
						break;
					view.getBoard()[row][col-indexLeft].setBorder(moveBorder);	
					if(col-indexLeft >= 1)
						indexLeft++;
					else
						break;
				}
			}
			if((col == 8 && col+indexRight <= 9) || (col+indexRight <= 8)) {
				while(view.getBoard()[row][col+indexRight].getIcon() == null || view.getBoard()[row][col+indexRight].getIcon().equals(view.getRedHidden().getIcon())) {
					if(view.getBoard()[row][col+indexRight].getIcon() != null) {
						if(view.getBoard()[row][col+indexRight].getIcon().equals(view.getRedHidden().getIcon())) {
							view.getBoard()[row][col+indexRight].setBorder(moveBorder);
							break;
						}
					}
					if(view.getBoard()[row][col+indexRight].getName().equals("yellowSquare"))
						break;
					view.getBoard()[row][col+indexRight].setBorder(moveBorder);	
					if(col+indexRight <= 8)
						indexRight++;
					else
						break;
				}
			}
		}
	}
	
	/**
	 * Create the valid move borders for the "no retreat" mode
	 * @param piece row col
	 */
	
	public void createBorders2(JButton piece, int row, int col) {
		int team = 0;
		for(int i = 0; i < view.getBluePieces().size(); i++) {
			if(piece.getIcon().equals(view.getBluePieces().get(i).getIcon())) {
				team = 1;
			}
		}
		
		if(row == 0 && col == 0) {		
			if(team == 0) {
				if(view.getBoard()[0][1].getIcon() == null || view.getBoard()[0][1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[0][1].setBorder(moveBorder);
				if(view.getBoard()[1][0].getIcon() != null) {
					if(view.getBoard()[1][0].getIcon().equals(view.getBlueHidden().getIcon()))
						view.getBoard()[1][0].setBorder(moveBorder);
				}
			} else {
				if(view.getBoard()[0][1].getIcon() == null || view.getBoard()[0][1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[0][1].setBorder(moveBorder);
				if(view.getBoard()[1][0].getIcon() == null || view.getBoard()[1][0].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[1][0].setBorder(moveBorder);
			}
		} else if (row == 7 && col == 9) {
			if(team == 0) {
				if(view.getBoard()[6][9].getIcon() == null || view.getBoard()[6][9].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[6][9].setBorder(moveBorder);
				if(view.getBoard()[7][8].getIcon() == null || view.getBoard()[7][8].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[7][8].setBorder(moveBorder);
			} else {
				if(view.getBoard()[7][8].getIcon() == null || view.getBoard()[7][8].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[7][8].setBorder(moveBorder);
				if(view.getBoard()[6][9].getIcon() != null) {
					if(view.getBoard()[6][9].getIcon().equals(view.getRedHidden().getIcon()))
						view.getBoard()[6][9].setBorder(moveBorder);
				}
			}
		} else if (row == 7 && col == 0) {
			if(team == 0) {
				if(view.getBoard()[7][1].getIcon() == null || view.getBoard()[7][1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[7][1].setBorder(moveBorder);
				if(view.getBoard()[6][0].getIcon() == null || view.getBoard()[6][0].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[6][0].setBorder(moveBorder);
			} else {
				if(view.getBoard()[7][1].getIcon() == null || view.getBoard()[7][1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[7][1].setBorder(moveBorder);
				if(view.getBoard()[6][0].getIcon() != null) {
					if(view.getBoard()[6][0].getIcon().equals(view.getRedHidden().getIcon()))
						view.getBoard()[6][0].setBorder(moveBorder);
				}
			}
		} else if (row == 0 && col == 9) {
			if(team == 0) {
				if(view.getBoard()[0][8].getIcon() == null || view.getBoard()[0][8].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[0][8].setBorder(moveBorder);
				if(view.getBoard()[1][9].getIcon() != null) {
					if(view.getBoard()[1][9].getIcon().equals(view.getBlueHidden().getIcon()))
						view.getBoard()[1][9].setBorder(moveBorder);
				}
			} else {
				if(view.getBoard()[0][8].getIcon() == null || view.getBoard()[0][8].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[0][8].setBorder(moveBorder);
				if(view.getBoard()[1][9].getIcon() == null || view.getBoard()[1][9].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[1][9].setBorder(moveBorder);
			}
		} else if (row == 0) {
			if(team == 0) {
				if(view.getBoard()[0][col+1].getIcon() == null || view.getBoard()[0][col+1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[0][col+1].setBorder(moveBorder);
				if(view.getBoard()[0][col-1].getIcon() == null || view.getBoard()[0][col-1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[0][col-1].setBorder(moveBorder);
				if(view.getBoard()[1][col].getIcon() != null) {
					if(view.getBoard()[1][col].getIcon().equals(view.getBlueHidden().getIcon())) 
						view.getBoard()[1][col].setBorder(moveBorder);
				}
			} else {
				if(view.getBoard()[0][col+1].getIcon() == null || view.getBoard()[0][col+1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[0][col+1].setBorder(moveBorder);
				if(view.getBoard()[0][col-1].getIcon() == null || view.getBoard()[0][col-1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[0][col-1].setBorder(moveBorder);
				if(view.getBoard()[1][col].getIcon() == null || view.getBoard()[1][col].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[1][col].setBorder(moveBorder);
			}
		} else if (row == 7) {
			if(team == 0) {
				if(view.getBoard()[7][col+1].getIcon() == null || view.getBoard()[7][col+1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[7][col+1].setBorder(moveBorder);
				if(view.getBoard()[7][col-1].getIcon() == null || view.getBoard()[7][col-1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[7][col-1].setBorder(moveBorder);
				if(view.getBoard()[6][col].getIcon() == null || view.getBoard()[6][col].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[6][col].setBorder(moveBorder);
			} else {
				if(view.getBoard()[7][col+1].getIcon() == null || view.getBoard()[7][col+1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[7][col+1].setBorder(moveBorder);
				if(view.getBoard()[7][col-1].getIcon() == null || view.getBoard()[7][col-1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[7][col-1].setBorder(moveBorder);
				if(view.getBoard()[6][col].getIcon() != null) {
					if(view.getBoard()[6][col].getIcon().equals(view.getRedHidden().getIcon())) 
						view.getBoard()[6][col].setBorder(moveBorder);
				}				
			}
		} else if (col == 0) {
			if(team == 0) {
				if(view.getBoard()[row-1][0].getIcon() == null || view.getBoard()[row-1][0].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row-1][0].setBorder(moveBorder);
				if(view.getBoard()[row+1][0].getIcon() != null) {
					if(view.getBoard()[row+1][0].getIcon().equals(view.getBlueHidden().getIcon())) 
						view.getBoard()[row+1][0].setBorder(moveBorder);
				}
				if(view.getBoard()[row][1].getIcon() == null || view.getBoard()[row][1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row][1].setBorder(moveBorder);
			} else {
				if(view.getBoard()[row+1][0].getIcon() == null || view.getBoard()[row+1][0].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row+1][0].setBorder(moveBorder);
				if(view.getBoard()[row-1][0].getIcon() != null) {
					if(view.getBoard()[row-1][0].getIcon().equals(view.getRedHidden().getIcon())) 
						view.getBoard()[row-1][0].setBorder(moveBorder);
				}
				if(view.getBoard()[row][1].getIcon() == null || view.getBoard()[row][1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row][1].setBorder(moveBorder);
			}
		
		} else if (col == 9) {
			if(team == 0) {
				if(view.getBoard()[row-1][9].getIcon() == null || view.getBoard()[row-1][9].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row-1][9].setBorder(moveBorder);
				if(view.getBoard()[row+1][9].getIcon() != null) {
					if(view.getBoard()[row+1][9].getIcon().equals(view.getBlueHidden().getIcon())) 
						view.getBoard()[row+1][9].setBorder(moveBorder);
				}
				if(view.getBoard()[row][8].getIcon() == null || view.getBoard()[row][8].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row][8].setBorder(moveBorder);
			} else {
				if(view.getBoard()[row+1][9].getIcon() == null || view.getBoard()[row+1][9].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row+1][9].setBorder(moveBorder);
				if(view.getBoard()[row-1][9].getIcon() != null) {
					if(view.getBoard()[row-1][9].getIcon().equals(view.getRedHidden().getIcon())) 
						view.getBoard()[row-1][9].setBorder(moveBorder);
				}
				if(view.getBoard()[row][8].getIcon() == null || view.getBoard()[row][8].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row][8].setBorder(moveBorder);
			}
		} else {
			if(team == 0) {
				if(view.getBoard()[row-1][col].getIcon() == null || view.getBoard()[row-1][col].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row-1][col].setBorder(moveBorder);
				if(view.getBoard()[row+1][col].getIcon() != null) {
					if(view.getBoard()[row+1][col].getIcon().equals(view.getBlueHidden().getIcon())) 
						view.getBoard()[row+1][col].setBorder(moveBorder);
				}
				if(view.getBoard()[row][col-1].getIcon() == null || view.getBoard()[row][col-1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row][col-1].setBorder(moveBorder);
				if(view.getBoard()[row][col+1].getIcon() == null || view.getBoard()[row][col+1].getIcon().equals(view.getBlueHidden().getIcon())) 
					view.getBoard()[row][col+1].setBorder(moveBorder);
			} else {
				if(view.getBoard()[row+1][col].getIcon() == null || view.getBoard()[row+1][col].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row+1][col].setBorder(moveBorder);
				if(view.getBoard()[row-1][col].getIcon() != null) {
					if(view.getBoard()[row-1][col].getIcon().equals(view.getRedHidden().getIcon())) 
						view.getBoard()[row-1][col].setBorder(moveBorder);
				}
				if(view.getBoard()[row][col-1].getIcon() == null || view.getBoard()[row][col-1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row][col-1].setBorder(moveBorder);
				if(view.getBoard()[row][col+1].getIcon() == null || view.getBoard()[row][col+1].getIcon().equals(view.getRedHidden().getIcon())) 
					view.getBoard()[row][col+1].setBorder(moveBorder);
			}
		}
	}
	
	/**
	 * Create the valid move borders for the scout for the "no retreat" mode
	 * @param piece row col
	 */
	
	public void createBordersScout2(JButton piece, int row, int col) {
		int team = 0;
		for(int i = 0; i < view.getBluePieces().size(); i++) {
			if(piece.getIcon().equals(view.getBluePieces().get(i).getIcon())) {
				team = 1;
			}
		}		
		int indexUp = 1, indexDown = 1, indexLeft = 1, indexRight = 1;		
		
		if(team == 0) {
			if((row == 6 && row+indexDown <=7) || (row+indexDown <= 6)) {
				if(view.getBoard()[row+indexDown][col].getIcon() != null) {
					if(view.getBoard()[row+indexDown][col].getIcon().equals(view.getBlueHidden().getIcon()))
						view.getBoard()[row+indexDown][col].setBorder(moveBorder);
				}
			}
			if((row == 1 && row-indexUp >= 0) || (row-indexUp >= 1)) {
				while(view.getBoard()[row-indexUp][col].getIcon() == null || view.getBoard()[row-indexUp][col].getIcon().equals(view.getBlueHidden().getIcon())) {
					if(view.getBoard()[row-indexUp][col].getIcon() != null) {
						if(view.getBoard()[row-indexUp][col].getIcon().equals(view.getBlueHidden().getIcon())) {
							view.getBoard()[row-indexUp][col].setBorder(moveBorder);
							break;
						}
					}
					if(view.getBoard()[row-indexUp][col].getName().equals("yellowSquare"))
						break;
					view.getBoard()[row-indexUp][col].setBorder(moveBorder);	
					if(row-indexUp >= 1)
						indexUp++;
					else
						break;
				}
			}
			if((col == 1 && col-indexLeft >= 0) || (col-indexLeft >= 1)) {
				while(view.getBoard()[row][col-indexLeft].getIcon() == null || view.getBoard()[row][col-indexLeft].getIcon().equals(view.getBlueHidden().getIcon())) {
					if(view.getBoard()[row][col-indexLeft].getIcon() != null) {
						if(view.getBoard()[row][col-indexLeft].getIcon().equals(view.getBlueHidden().getIcon())) {
							view.getBoard()[row][col-indexLeft].setBorder(moveBorder);
							break;
						}
					}
					if(view.getBoard()[row][col-indexLeft].getName().equals("yellowSquare"))
						break;
					view.getBoard()[row][col-indexLeft].setBorder(moveBorder);	
					if(col-indexLeft >= 1)
						indexLeft++;
					else
						break;
				}
			}
			if((col == 8 && col+indexRight <= 9) || (col+indexRight <= 8)) {
				while(view.getBoard()[row][col+indexRight].getIcon() == null || view.getBoard()[row][col+indexRight].getIcon().equals(view.getBlueHidden().getIcon())) {
					if(view.getBoard()[row][col+indexRight].getIcon() != null) {
						if(view.getBoard()[row][col+indexRight].getIcon().equals(view.getBlueHidden().getIcon())) {
							view.getBoard()[row][col+indexRight].setBorder(moveBorder);
							break;
						}
					}
					if(view.getBoard()[row][col+indexRight].getName().equals("yellowSquare"))
						break;
					view.getBoard()[row][col+indexRight].setBorder(moveBorder);	
					if(col+indexRight <= 8)
						indexRight++;
					else
						break;
				}
			}
		} else {
			if((row == 6 && row+indexDown <=7) || (row+indexDown <= 6)) {
				while(view.getBoard()[row+indexDown][col].getIcon() == null || view.getBoard()[row+indexDown][col].getIcon().equals(view.getRedHidden().getIcon())) {
					if(view.getBoard()[row+indexDown][col].getIcon() != null) {
						if(view.getBoard()[row+indexDown][col].getIcon().equals(view.getRedHidden().getIcon())) {
							view.getBoard()[row+indexDown][col].setBorder(moveBorder);
							break;
						}
					}
					if(view.getBoard()[row+indexDown][col].getName().equals("yellowSquare"))
						break;
					view.getBoard()[row+indexDown][col].setBorder(moveBorder);	
					if(row+indexDown <= 6)
						indexDown++;
					else
						break;
				}
			}
			if((row == 1 && row-indexUp >= 0) || (row-indexUp >= 1)) {
				if(view.getBoard()[row-indexUp][col].getIcon() != null) {
					if(view.getBoard()[row-indexUp][col].getIcon().equals(view.getRedHidden().getIcon()))
						view.getBoard()[row-indexUp][col].setBorder(moveBorder);
				}
			}
			if((col == 1 && col-indexLeft >= 0) || (col-indexLeft >= 1)) {
				while(view.getBoard()[row][col-indexLeft].getIcon() == null || view.getBoard()[row][col-indexLeft].getIcon().equals(view.getRedHidden().getIcon())) {
					if(view.getBoard()[row][col-indexLeft].getIcon() != null) {
						if(view.getBoard()[row][col-indexLeft].getIcon().equals(view.getRedHidden().getIcon())) {
							view.getBoard()[row][col-indexLeft].setBorder(moveBorder);
							break;
						}
					}
					if(view.getBoard()[row][col-indexLeft].getName().equals("yellowSquare"))
						break;
					view.getBoard()[row][col-indexLeft].setBorder(moveBorder);	
					if(col-indexLeft >= 1)
						indexLeft++;
					else
						break;
				}
			}
			if((col == 8 && col+indexRight <= 9) || (col+indexRight <= 8)) {
				while(view.getBoard()[row][col+indexRight].getIcon() == null || view.getBoard()[row][col+indexRight].getIcon().equals(view.getRedHidden().getIcon())) {
					if(view.getBoard()[row][col+indexRight].getIcon() != null) {
						if(view.getBoard()[row][col+indexRight].getIcon().equals(view.getRedHidden().getIcon())) {
							view.getBoard()[row][col+indexRight].setBorder(moveBorder);
							break;
						}
					}
					if(view.getBoard()[row][col+indexRight].getName().equals("yellowSquare"))
						break;
					view.getBoard()[row][col+indexRight].setBorder(moveBorder);	
					if(col+indexRight <= 8)
						indexRight++;
					else
						break;
				}
			}
		}
	}
}