package view;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import controller.Controller;
import model.Piece;
import model.Player;

/**
 * View; the class that implements the graphics (GUI) of the game
 *
 */

public class View extends JFrame {
	
	private ClassLoader cldr;
	private ArrayList<JButton> playerRedPieces = new ArrayList<JButton>();
	private ArrayList<JButton> playerBluePieces = new ArrayList<JButton>();
	private JButton[][] board;
	private JPanel panel = new JPanel();
	private JButton redHidden = new JButton(); 
	private JButton blueHidden = new JButton();;
	private ArrayList<JButton> tempRedPieces = new ArrayList<JButton>();
	private ArrayList<JButton> tempBluePieces = new ArrayList<JButton>();
	private Player redPlayer;
	private Player bluePlayer;
	private int attackerRank = -2, defenderRank = -2;
	private JLabel text1, text2, text3, text4, text5, text6, perRed, perBlue, tRound, text7, text8, sumRed, sumBlue, recRed, recBlue;
	private JCheckBox checkBox1, checkBox2;
	private float successfulAttRed = 0, totalAttRed = 0, successfulAttBlue = 0, totalAttBlue = 0;
	private int round = 1, sumRedInt = 0, sumBlueInt = 0;
	private JButton[][] redCaptured = new JButton[3][4], blueCaptured = new JButton[3][4];
	private Integer[][] redCapturedInt = new Integer[3][4], blueCapturedInt = new Integer[3][4];
	private JLabel[][] tRedCaptured = new JLabel[3][4], tBlueCaptured = new JLabel[3][4];
	private int redRecLeft = 0, blueRecLeft = 0;
	private ArrayList<Integer> tempIndexRed = new ArrayList<Integer>();
	private ArrayList<Integer> tempIndexBlue = new ArrayList<Integer>();
	Border recBorder = BorderFactory.createMatteBorder(4, 4, 4, 4, Color.YELLOW);
	
	/**
	 * Update position of piece
	 * @param oldSquare newRow newCol oldRow oldCol
	 */
	
	public void updatePiece(int newRow, int newCol, int oldRow, int oldCol) {
		int indexRed = 0, indexBlue = 0;
		if(board[newRow][newCol].getIcon() == null) {
			for(int i = 0; i < 8; i++) {
				for(int j = 0; j < 10; j++) {
					if(board[i][j].getIcon() != null) {
						for(int k = 0; k < playerRedPieces.size(); k++) {
							if(board[i][j].getIcon().equals(playerRedPieces.get(k).getIcon()) && board[i][j].getBounds().equals(board[oldRow][oldCol].getBounds())) {
								attackerRank = redPlayer.getRedPieces().get(k).getRank();
								indexRed = k;
							}
						}
						for(int k = 0; k < playerBluePieces.size(); k++) {
							if(board[i][j].getIcon().equals(playerBluePieces.get(k).getIcon()) && board[i][j].getBounds().equals(board[oldRow][oldCol].getBounds())) {	
								attackerRank = bluePlayer.getBluePieces().get(k).getRank();
								indexBlue = k;
							}
						}
					}
				}
			}
			
			board[newRow][newCol].setIcon(board[oldRow][oldCol].getIcon());
			board[oldRow][oldCol].setIcon(null);
			changeTurn();
		} else {
			boolean attRed = false;
			
			for(int i = 0; i < 8; i++) {
				for(int j = 0; j < 10; j++) {
					if(board[i][j].getIcon() != null) {
						for(int k = 0; k < playerRedPieces.size(); k++) {
							if(board[i][j].getIcon().equals(playerRedPieces.get(k).getIcon()) && board[i][j].getBounds().equals(board[oldRow][oldCol].getBounds())) {
								attackerRank = redPlayer.getRedPieces().get(k).getRank();
								indexRed = k;
								attRed = true;
							}
						}
						for(int k = 0; k < playerBluePieces.size(); k++) {
							if(board[i][j].getIcon().equals(playerBluePieces.get(k).getIcon()) && board[i][j].getBounds().equals(board[oldRow][oldCol].getBounds())) {	
								attackerRank = bluePlayer.getBluePieces().get(k).getRank();
								indexBlue = k;
							}
						}
					}
				}
			}
			
			changeTurn();	
			
			for(int i = 0; i < 8; i++) {
				for(int j = 0; j < 10; j++) {	
					if(board[i][j].getIcon() != null) {
						for(int k = 0; k < playerRedPieces.size(); k++) {
							if(board[i][j].getIcon().equals(playerRedPieces.get(k).getIcon()) && board[i][j].getBounds().equals(board[newRow][newCol].getBounds())) {
								defenderRank = redPlayer.getRedPieces().get(k).getRank();
								indexRed = k;
							}
						}
						for(int k = 0; k < playerBluePieces.size(); k++) {
							if(board[i][j].getIcon().equals(playerBluePieces.get(k).getIcon()) && board[i][j].getBounds().equals(board[newRow][newCol].getBounds())) {
								defenderRank = bluePlayer.getBluePieces().get(k).getRank();
								indexBlue = k;
								attRed = true;
							}
						}
					}
				}			
			}
			changeTurn();
		
			Controller.attack(attackerRank, defenderRank);
			
			if(Controller.getAttackResult() == 0) {
				board[oldRow][oldCol].setIcon(null);
				board[newRow][newCol].setIcon(null);
				if(attRed == true) {
					totalAttRed++;
					attackerRank++;
					redCapturedInt[attackerRank/4][attackerRank%4]++;
					attackerRank--;
					defenderRank++;
					blueCapturedInt[defenderRank/4][defenderRank%4]++;
				} else {
					totalAttBlue++;
					attackerRank++;
					blueCapturedInt[attackerRank/4][attackerRank%4]++;
					attackerRank--;
					defenderRank++;
					redCapturedInt[defenderRank/4][defenderRank%4]++;
				}
			} else if (Controller.getAttackResult() == 1) {
				board[newRow][newCol].setIcon(board[oldRow][oldCol].getIcon());
				board[oldRow][oldCol].setIcon(null);
				if(attRed == true) {
					totalAttRed++;
					successfulAttRed++;
					defenderRank++;
					blueCapturedInt[defenderRank/4][defenderRank%4]++;
				} else {
					totalAttBlue++;
					successfulAttBlue++;
					defenderRank++;
					redCapturedInt[defenderRank/4][defenderRank%4]++;
				}
			} else {
				board[oldRow][oldCol].setIcon(null);
				if(attRed == true) {
					totalAttRed++;
					attackerRank++;
					redCapturedInt[attackerRank/4][attackerRank%4]++;
					attackerRank--;
				} else {
					totalAttBlue++;
					attackerRank++;
					blueCapturedInt[attackerRank/4][attackerRank%4]++;
					attackerRank--;
				}
				
			}
			changeTurn();
		}
		
		for(int j = 0; j < 8; j++) {
			for(int k = 0; k < 10; k++) {	
				if(board[j][k] != null) {
					board[j][k].setBorder(UIManager.getBorder("Button.border"));	
				}
			}	
		}
		
		if(bluePlayer.getTurn() == true) {
			if(newRow == 2 && attackerRank != 2 && redRecLeft < 2 && Controller.getTotalRecRed() > 0 && !tempIndexRed.contains(indexRed)) {
				tempIndexRed.add(indexRed);
				redRecLeft++;
			} else {
				for(int i = 0; i < 3; i++) {
					for(int j = 0; j < 4; j++) {
						redCaptured[i][j].setBorder(UIManager.getBorder("Button.border"));
					}
				}
			}
		} else {
			if(newRow == 5 && attackerRank != 2 && blueRecLeft < 2 && Controller.getTotalRecBlue() > 0 && !tempIndexBlue.contains(indexBlue)) {
				tempIndexBlue.add(indexBlue);
				blueRecLeft++;
			} else {
				for(int i = 0; i < 3; i++) {
					for(int j = 0; j < 4; j++) {
						blueCaptured[i][j].setBorder(UIManager.getBorder("Button.border"));
					}
				}
			}
		}
		
		if(redCapturedInt[0][0] == 1)
			redPlayer.hasLost();
		if(blueCapturedInt[0][0] == 1)
			bluePlayer.hasLost();
		
		if(Controller.isGameFinished()) {
			GameOver();
		}
		
		UpdateMenu();
		repaint();
	}
	
	/**
	 * Initialize pieces
	 * @param redPieces bluePieces
	 */
	
	public void initPieces(ArrayList<Piece> redPieces, ArrayList<Piece> bluePieces) {
		redPlayer = Controller.getRedPlayer();
		bluePlayer = Controller.getBluePlayer();
		for(int i = 0; i < redPieces.size(); i++) {
			playerRedPieces.add(i, new JButton());
			URL imageURL = cldr.getResource(redPieces.get(i).getImage()); 
			Image image = new ImageIcon(imageURL).getImage();
            image = image.getScaledInstance(95, 95, Image.SCALE_SMOOTH);
            playerRedPieces.get(i).setIcon(new ImageIcon(image));
            
            tempRedPieces.add(i, new JButton());
            tempRedPieces.get(i).setIcon(playerRedPieces.get(i).getIcon());
			
            playerBluePieces.add(i, new JButton());
            URL imageURL2 = cldr.getResource(bluePieces.get(i).getImage()); 
            Image image2 = new ImageIcon(imageURL2).getImage();
            image2 = image2.getScaledInstance(95, 95, Image.SCALE_SMOOTH);
            playerBluePieces.get(i).setIcon(new ImageIcon(image2));
            
            tempBluePieces.add(i, new JButton());
            tempBluePieces.get(i).setIcon(playerBluePieces.get(i).getIcon());
		}
	}
	
	/**
	 * Initialize the board
	 * @param redPieces bluePieces
	 */
	
	public void initBoard(ArrayList<Piece> redPieces, ArrayList<Piece> bluePieces) {
		board = new JButton[8][10];
		setLayout(null);
		
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 10; j++) {	
				if((i == 3 || i == 4) && (j == 2 || j == 3 || j == 6 || j == 7)) {
					JLabel yellowSquare = new JLabel();
					yellowSquare.setOpaque(true);
					yellowSquare.setBackground(Color.yellow);
					add(yellowSquare);
					yellowSquare.setBounds(j*100-j+2, i*100-(3*i+i), 99, 96);
					
					board[i][j] = new JButton();
					board[i][j].setName("yellowSquare");
					add(board[i][j]);
				} else {
					board[i][j] = new JButton();
					board[i][j].setName("board");
					board[i][j].setBackground(Color.white);
					add(board[i][j]);
					board[i][j].setBounds(j*100-j+2, i*100-(3*i+i), 99, 96);	
				}
			}
			
			URL imageURL = cldr.getResource("RedPieces/redHidden.png"); 
			Image image = new ImageIcon(imageURL).getImage();
			image = image.getScaledInstance(95, 95, Image.SCALE_SMOOTH);
			
			URL imageURL2 = cldr.getResource("BluePieces/blueHidden.png"); 
			Image image2 = new ImageIcon(imageURL2).getImage();
			image2 = image2.getScaledInstance(95, 95, Image.SCALE_SMOOTH);
			
			redHidden.setIcon(new ImageIcon(image));
			blueHidden.setIcon(new ImageIcon(image2));
		}
		pack();
		initPiecesOnBoard(redPieces, bluePieces);
	}
	
	/**
	 * Initialize the pieces on the board
	 */
	
	public void initPiecesOnBoard(ArrayList<Piece> redPieces, ArrayList<Piece> bluePieces) {
		initPieces(redPieces, bluePieces);
		
		if(Controller.getCheck1State() == false) {
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 10; j++) {	
					board[i][j].setIcon(null);
				}
			}
			for(int i = 5; i < 8; i++) {
				for(int j = 0; j < 10; j++) {
					board[i][j].setIcon(null);
				}
			}
			
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 10; j++) {	
					board[i][j].setIcon(blueHidden.getIcon());
				}
			}

			for(int i = 5; i < 8; i++) {
				for(int j = 0; j < 10; j++) {
					int n = 0;
					if(i == 6)
						n=j+10;
					else if(i == 7)
						n=j+20;
					else
						n=j;

					board[i][j].setIcon(playerRedPieces.get(n).getIcon());
				}
			}
		} else {
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 10; j++) {	
					board[i][j].setIcon(null);
				}
			}
			for(int i = 5; i < 8; i++) {
				for(int j = 0; j < 10; j++) {
					board[i][j].setIcon(null);
				}
			}
			
			for(int i = 0; i < 1; i++) {
				for(int j = 0; j < 10; j++) {	
					board[i][j].setIcon(blueHidden.getIcon());
				}
			}
			for(int i = 1; i < 2; i++) {
				for(int j = 0; j < 6; j++) {	
					board[i][j].setIcon(blueHidden.getIcon());
				}
			}

			for(int i = 7; i < 8; i++) {
				for(int j = 0; j < 10; j++) {
					board[i][j].setIcon(playerRedPieces.get(j).getIcon());
				}
			}
			for(int i = 6; i < 7; i++) {
				for(int j = 0; j < 6; j++) {
					board[i][j].setIcon(playerRedPieces.get(j+10).getIcon());
				}
			}
		}

		repaint();
	}
	
	/**
	 * Changes pieces to face down and face up
	 */
	
	public void changeTurn() {
		int indexRed = 0, indexBlue = 0;
		int setRed = 0, setBlue = 0;
		
		if(redPlayer.getTurn() == true) {
			redPlayer.changeTurn();
			
			for(int i = 0; i < 8; i++) {
				for(int j = 0; j < 10; j++) {	
					if(board[i][j] != null && board[i][j].getIcon() != null) {
						for(int k = 0; k < playerRedPieces.size(); k++) {
							if(board[i][j].getIcon().equals(playerRedPieces.get(k).getIcon())) {
								tempRedPieces.remove(indexRed);
								tempRedPieces.add(indexRed, playerRedPieces.get(k));
								indexRed++;
							}
						}
						
						for(int k = 0; k < playerRedPieces.size(); k++) {
							if(board[i][j].getIcon().equals(playerRedPieces.get(k).getIcon())) {
								board[i][j].setIcon(redHidden.getIcon());
							}
						}
						
						for(int k = 0; k < playerBluePieces.size(); k++) {
							if(board[i][j].getIcon().equals(blueHidden.getIcon())) {
								board[i][j].setIcon(tempBluePieces.get(setBlue).getIcon());
								setBlue++;
							}
						}
					}
				}
			}
		} else {
			bluePlayer.changeTurn();
			round++;
			tRound.setText(round + "");
			
			for(int i = 0; i < 8; i++) {
				for(int j = 0; j < 10; j++) {	
					if(board[i][j] != null && board[i][j].getIcon() != null) {
						for(int k = 0; k < playerBluePieces.size(); k++) {
							if(board[i][j].getIcon().equals(playerBluePieces.get(k).getIcon())) {
								tempBluePieces.remove(indexBlue);
								tempBluePieces.add(indexBlue, playerBluePieces.get(k));
								indexBlue++;
							}
						}
						
						for(int k = 0; k < playerBluePieces.size(); k++) {
							if(board[i][j].getIcon().equals(playerBluePieces.get(k).getIcon())) {
								board[i][j].setIcon(blueHidden.getIcon());
							}
						}
						
						for(int k = 0; k < playerRedPieces.size(); k++) {
							if(board[i][j].getIcon().equals(redHidden.getIcon())) {
								board[i][j].setIcon(tempRedPieces.get(setRed).getIcon());
								setRed++;
							}
						}
					}
				}
			}
		}
		UpdateMenu();
		repaint();
	}
	
	public void GameOver() {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 10; j++) {	
				remove(board[i][j]);
			}
		}
		setBackground(Color.DARK_GRAY);
		if(redPlayer.isLost()) {
			JLabel blueWins = new JLabel("Game over! Blue player wins!");
			blueWins.setVisible(true);
			blueWins.setFont(new Font("Serif", Font.BOLD, 40));
			blueWins.setBounds(200,0,1000,1000);
			add(blueWins);
		} else {
			JLabel redWins = new JLabel("Game over! Red player wins!");
			redWins.setVisible(true);
			redWins.setFont(new Font("Serif", Font.BOLD, 40));
			redWins.setBounds(200,0,1000,1000);
			add(redWins);
		}
	}
	
	public void CreateMenu() {
		text1 = new JLabel("Ενεργοί Κανόνες");
		text1.setVisible(true);
		text1.setFont(new Font("Serif", Font.BOLD, 30));
		text1.setBounds(1050, 0, 1000, 100);
		add(text1);
		
		checkBox1 = new JCheckBox("Μειωμένος στρατός");
		checkBox1.setVisible(true);
		checkBox1.setBounds(1050, 90, 1000, 20);
		add(checkBox1);
		
		checkBox2 = new JCheckBox("Καμία υποχώρηση");
		checkBox2.setVisible(true);
		checkBox2.setBounds(1050, 120, 1000, 20);
		add(checkBox2);
		
		text2 = new JLabel("Στατιστικά");
		text2.setVisible(true);
		text2.setFont(new Font("Serif", Font.BOLD, 30));
		text2.setBounds(1050, 150, 1000, 100);
		add(text2);
		
		text3 = new JLabel("Red player turn");
		text3.setVisible(true);
		text3.setFont(new Font("Serif", Font.BOLD, 19));
		text3.setBounds(1050, 200, 1000, 100);
		add(text3);
		
		text4 = new JLabel("Ποσοστό επιτ. επίθεσης:");
		text4.setVisible(true);
		text4.setFont(new Font("Serif", Font.BOLD, 16));
		text4.setBounds(1050, 230, 1000, 100);
		add(text4);
		
		text5 = new JLabel("Διασώσεις:");
		text5.setVisible(true);
		text5.setFont(new Font("Serif", Font.BOLD, 16));
		text5.setBounds(1050, 260, 1000, 100);
		add(text5);
		
		text6 = new JLabel("Γύρος:");
		text6.setVisible(true);
		text6.setFont(new Font("Serif", Font.BOLD, 16));
		text6.setBounds(1050, 290, 1000, 100);
		add(text6);
		
		perRed = new JLabel("0%");
		perRed.setVisible(true);
		perRed.setFont(new Font("Serif", Font.BOLD, 16));
		perRed.setBounds(1240, 230, 1000, 100);
		add(perRed);
		
		perBlue = new JLabel("0%");
		perBlue.setVisible(false);
		perBlue.setFont(new Font("Serif", Font.BOLD, 16));
		perBlue.setBounds(1240, 230, 1000, 100);
		add(perBlue);
		
		tRound = new JLabel("1");
		tRound.setVisible(true);
		tRound.setFont(new Font("Serif", Font.BOLD, 16));
		tRound.setBounds(1110, 290, 1000, 100);
		add(tRound);
		
		text7 = new JLabel("Αιχμαλωτίσεις");
		text7.setVisible(true);
		text7.setFont(new Font("Serif", Font.BOLD, 30));
		text7.setBounds(1050, 340, 1000, 100);
		add(text7);
		
		text8 = new JLabel("Συνολικές Αιχμαλωτίσεις:");
		text8.setVisible(true);
		text8.setFont(new Font("Serif", Font.BOLD, 16));
		text8.setBounds(1050, 380, 1000, 100);
		add(text8);
		
		sumRed = new JLabel("0");
		sumRed.setVisible(true);
		sumRed.setFont(new Font("Serif", Font.BOLD, 16));
		sumRed.setBounds(1250, 380, 1000, 100);
		add(sumRed);
		
		sumBlue = new JLabel("0");
		sumBlue.setVisible(false);
		sumBlue.setFont(new Font("Serif", Font.BOLD, 16));
		sumBlue.setBounds(1250, 380, 1000, 100);
		add(sumBlue);
		
		recRed = new JLabel("0");
		recRed.setVisible(true);
		recRed.setFont(new Font("Serif", Font.BOLD, 16));
		recRed.setBounds(1140, 260, 1000, 100);
		add(recRed);
		
		recBlue = new JLabel("0");
		recBlue.setVisible(false);
		recBlue.setFont(new Font("Serif", Font.BOLD, 16));
		recBlue.setBounds(1140, 260, 1000, 100);
		add(recBlue);
		
		int index = -1;
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 4; j++) {
				redCaptured[i][j] = new JButton();
				redCaptured[i][j].setVisible(true);
				redCaptured[i][j].setBounds(1050+(i*95), 450+(j*75), 70, 70);
				URL imageURL = cldr.getResource("RedPieces/r" + index + ".png"); 
				Image image = new ImageIcon(imageURL).getImage();
	            image = image.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
				redCaptured[i][j].setIcon(new ImageIcon(image));
				add(redCaptured[i][j]);
				
				blueCaptured[i][j] = new JButton();
				blueCaptured[i][j].setVisible(false);
				blueCaptured[i][j].setBounds(1050+(i*95), 450+(j*75), 70, 70);
				URL imageURL2 = cldr.getResource("BluePieces/b" + index + ".png"); 
				Image image2 = new ImageIcon(imageURL2).getImage();
	            image2 = image2.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
				blueCaptured[i][j].setIcon(new ImageIcon(image2));
				add(blueCaptured[i][j]);
				
				index++;
				
				tRedCaptured[i][j] = new JLabel("0");
				tRedCaptured[i][j].setVisible(true);
				
				tBlueCaptured[i][j] = new JLabel("0");
				tBlueCaptured[i][j].setVisible(false);
				if(i == 0) {
					tRedCaptured[i][j].setBounds(1125, 450+(j*75), 1000, 100);
					tBlueCaptured[i][j].setBounds(1125, 450+(j*75), 1000, 100);
				} else if(i == 1) {
					tRedCaptured[i][j].setBounds(1050+(i*170), 450+(j*75), 1000, 100);
					tBlueCaptured[i][j].setBounds(1050+(i*170), 450+(j*75), 1000, 100);
				} else {
					tRedCaptured[i][j].setBounds(1050+(i*133), 450+(j*75), 1000, 100);
					tBlueCaptured[i][j].setBounds(1050+(i*133), 450+(j*75), 1000, 100);
				}
				tRedCaptured[i][j].setFont(new Font("Serif", Font.BOLD, 16));
				tBlueCaptured[i][j].setFont(new Font("Serif", Font.BOLD, 16));
				
				add(tRedCaptured[i][j]);
				add(tBlueCaptured[i][j]);
				
				redCapturedInt[i][j] = 0;
				blueCapturedInt[i][j] = 0;
			}
		}
		
		repaint();
	}
	
	public void UpdateMenu() {
		if(totalAttRed != 0)
			perRed.setText((successfulAttRed/totalAttRed)*100 + "%");
		if(totalAttBlue != 0)
			perBlue.setText((successfulAttBlue/totalAttBlue)*100 + "%");
		sumRedInt = 0;
		sumBlueInt = 0;
		if(redPlayer.getTurn() == true) {
			text3.setText("Red player turn");
			perRed.setVisible(true);
			perBlue.setVisible(false);
			
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 4; j++) {
					redCaptured[i][j].setVisible(true);
					blueCaptured[i][j].setVisible(false);
					
					tRedCaptured[i][j].setText(redCapturedInt[i][j] + "");
					tBlueCaptured[i][j].setText(blueCapturedInt[i][j] + "");
					
					tRedCaptured[i][j].setVisible(true);
					tBlueCaptured[i][j].setVisible(false);
					
					sumRedInt += redCapturedInt[i][j];
					sumBlueInt += blueCapturedInt[i][j];
				}
			}
			sumRed.setText(sumRedInt + "");
			sumBlue.setText(sumBlueInt + "");			
			sumRed.setVisible(true);
			sumBlue.setVisible(false);
			
			recRed.setText(-Controller.getTotalRecRed()+2 + "");
			recBlue.setText(-Controller.getTotalRecBlue()+2 + "");
			recRed.setVisible(true);
			recBlue.setVisible(false);
		} else {
			text3.setText("Blue player turn");
			perRed.setVisible(false);
			perBlue.setVisible(true);
			
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 4; j++) {
					redCaptured[i][j].setVisible(false);
					blueCaptured[i][j].setVisible(true);			
					
					tRedCaptured[i][j].setText(redCapturedInt[i][j] + "");
					tBlueCaptured[i][j].setText(blueCapturedInt[i][j] + "");
					
					tRedCaptured[i][j].setVisible(false);
					tBlueCaptured[i][j].setVisible(true);
					
					sumRedInt += redCapturedInt[i][j];
					sumBlueInt += blueCapturedInt[i][j];
				}
			}
			sumRed.setText(sumRedInt + "");
			sumBlue.setText(sumBlueInt + "");
			sumRed.setVisible(false);
			sumBlue.setVisible(true);
			
			recRed.setText(-Controller.getTotalRecRed()+2 + "");
			recBlue.setText(-Controller.getTotalRecBlue()+2 + "");
			recRed.setVisible(false);
			recBlue.setVisible(true);
		}
		
		if(redRecLeft > 0 && Controller.getTotalRecRed() > 0) {
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 4; j++) {
					if(redCapturedInt[i][j] > 0) {
						if(i == 0) {
							if(j == 0 || j == 1)
								redCaptured[i][j].setBorder(UIManager.getBorder("Button.border"));								
							else {
								redCaptured[i][j].setBorder(recBorder);							}
						} else {
							redCaptured[i][j].setBorder(recBorder);
						}
					} else {
						redCaptured[i][j].setBorder(UIManager.getBorder("Button.border"));	
					}
				}
			}
		} else {
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 4; j++) {
					redCaptured[i][j].setBorder(UIManager.getBorder("Button.border"));
				}
			}
		}
		
		if(blueRecLeft > 0 && Controller.getTotalRecBlue() > 0) {
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 4; j++) {
					if(blueCapturedInt[i][j] > 0) {
						if(i == 0) {
							if(j == 0 || j == 1)
								blueCaptured[i][j].setBorder(UIManager.getBorder("Button.border"));								
							else {
								blueCaptured[i][j].setBorder(recBorder);
							}
						} else {
							blueCaptured[i][j].setBorder(recBorder);
						}
					} else {
						blueCaptured[i][j].setBorder(UIManager.getBorder("Button.border"));	
					}
				}
			}
		} else {
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 4; j++) {
					blueCaptured[i][j].setBorder(UIManager.getBorder("Button.border"));
				}
			}
		}
		
		repaint();
	}
	
	public void minusRedRec() { redRecLeft--; }
	public void minusBlueRec() { blueRecLeft--; }
	
	/**
	 * Useful getters
	 * @return board playerRedPieces playerBluePieces redHidden blueHidden getCheckBox getRedCaptured getBlueCaptured
	 * @return getRedCapturedInt getBlueCapturedInt
	 */
	
	public JButton[][] getBoard() { return board; }
	public ArrayList<JButton> getRedPieces() { return playerRedPieces; }
	public ArrayList<JButton> getBluePieces() { return playerBluePieces; }
	public JButton getRedHidden() { return redHidden; }
	public JButton getBlueHidden() { return blueHidden; }
	public JCheckBox getCheckBox1() { return checkBox1; }
	public JCheckBox getCheckBox2() { return checkBox2; }
	public JButton[][] getRedCaptured() { return redCaptured; }
	public JButton[][] getBlueCaptured() { return blueCaptured; }
	public Integer[][] getRedCapturedInt() { return redCapturedInt; }
	public Integer[][] getBlueCapturedInt() { return blueCapturedInt; }
	public Border getRecBorder() { return recBorder; }
	
	public View() {
		cldr = this.getClass().getClassLoader();
		setResizable(false);
	    setTitle("Stratego Ice vs Fire");
	    setPreferredSize(new Dimension(1380, 800));
	    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    CreateMenu();
	}
}